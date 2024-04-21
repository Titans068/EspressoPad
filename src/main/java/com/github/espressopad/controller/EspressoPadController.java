package com.github.espressopad.controller;

import com.github.espressopad.io.ConsoleErrorStream;
import com.github.espressopad.io.ConsoleOutputStream;
import com.github.espressopad.models.ViewModel;
import com.github.espressopad.views.components.TextEditor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.*;
import jdk.jshell.JShell;
import jdk.jshell.JShellException;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EspressoPadController implements AutoCloseable {
    private static final JShell shell = JShell.builder().out(null).in(null).err(null).build();
    private final Logger logger = LoggerFactory.getLogger(EspressoPadController.class);
    private final DefaultCompletionProvider provider = new DefaultCompletionProvider();

    public void setupClosableTabs(JTabbedPane tabPane, String title) {
        int index = tabPane.indexOfTab(title);
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        JButton btnClose = new JButton("x");
        CompoundBorder border = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(), BorderFactory.createEmptyBorder(0, 2, 0, 2));
        btnClose.setBorder(border);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusable(false);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setupRemoveTab(tabPane);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 10, 0, 1);

        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);

        tabPane.setTabComponentAt(index, pnlTab);
    }

    public void setupMiddleMouseListener(JTabbedPane tabPane) {
        tabPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tabPane.getBoundsAt(tabPane.getSelectedIndex()).contains(e.getPoint()) &&
                        e.getButton() == MouseEvent.BUTTON2)
                    setupRemoveTab(tabPane);
            }
        });
    }

    public void setupRemoveTab(JTabbedPane tabPane) {
        if (tabPane.getTabCount() <= 2) return;
        tabPane.remove(tabPane.getSelectedComponent());
        tabPane.setSelectedComponent(tabPane.getComponentAt(tabPane.getTabCount() - 2));
    }

    public void setupTextChangeListener(TextEditor textEditor) {
        AutoCompletion ac = new AutoCompletion(this.provider);
        this.provider.setAutoActivationRules(true, ".");
        ac.setAutoCompleteEnabled(true);
        ac.setAutoActivationEnabled(true);
        ac.setShowDescWindow(true);
        ac.install(textEditor);
        textEditor.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                textEditor.getViewModel().getStatusBar().setCharacterPosition(
                        String.format("%d:%d", textEditor.getCaretLineNumber() + 1, textEditor.getCaretOffsetFromLineStart() + 1)
                );
            }
        });
        textEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setupTextChangeEvent(textEditor);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setupTextChangeEvent(textEditor);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setupTextChangeEvent(textEditor);
            }
        });
    }

    private void setupTextChangeEvent(TextEditor textEditor) {
        Executors.newFixedThreadPool(3).submit(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        SourceCodeAnalysis.CompletionInfo completionInfo = shell.sourceCodeAnalysis()
                                .analyzeCompletion(textEditor.getText());
                        while (completionInfo.source() != null && !completionInfo.source().isBlank()) {
                            if (completionInfo.completeness() != SourceCodeAnalysis.Completeness.COMPLETE) break;
                            List<SnippetEvent> snippetEvents = shell.eval(completionInfo.source());
                            for (SnippetEvent snippetEvent : snippetEvents) {
                                switch (snippetEvent.snippet().kind()) {
                                    case METHOD:
                                        BodyDeclaration<?> body = StaticJavaParser.parseBodyDeclaration(snippetEvent.snippet().source());
                                        body.asMethodDeclaration().getBody().ifPresent(x -> {
                                            for (Statement st : x.getStatements())
                                                shell.eval(st.toString());
                                        });
                                        break;
                                    case STATEMENT:
                                        Statement stmts = StaticJavaParser.parseStatement(snippetEvent.snippet().source());
                                        if (stmts.isDoStmt()) {
                                            DoStmt doStmt = stmts.asDoStmt();
                                            for (Statement st : doStmt.getBody().asBlockStmt().getStatements())
                                                shell.eval(st.toString());
                                        } else if (stmts.isForEachStmt()) {
                                            ForEachStmt forEachStmt = stmts.asForEachStmt();
                                            for (Statement st : forEachStmt.getBody().asBlockStmt().getStatements())
                                                shell.eval(st.toString());
                                        } else if (stmts.isForStmt()) {
                                            ForStmt forStmt = stmts.asForStmt();
                                            for (Statement st : forStmt.getBody().asBlockStmt().getStatements())
                                                shell.eval(st.toString());
                                        } else if (stmts.isIfStmt()) {
                                            IfStmt ifStmt = stmts.asIfStmt();
                                            for (Statement st : ifStmt.getThenStmt().asBlockStmt().getStatements())
                                                shell.eval(st.toString());
                                        } else if (stmts.isSwitchStmt()) {
                                            SwitchStmt switchStmt = stmts.asSwitchStmt();
                                            for (SwitchEntry switchEntry : switchStmt.getEntries())
                                                for (Statement st : switchEntry.getStatements())
                                                    shell.eval(st.toString());
                                        } else if (stmts.isSynchronizedStmt()) {
                                            SynchronizedStmt syncStmt = stmts.asSynchronizedStmt();
                                            for (Statement st : syncStmt.getBody().asBlockStmt().getStatements())
                                                shell.eval(st.toString());
                                        } else if (stmts.isTryStmt()) {
                                            TryStmt tryStmt = stmts.asTryStmt();
                                            for (Statement st : tryStmt.getTryBlock().asBlockStmt().getStatements())
                                                shell.eval(st.toString());
                                            for (CatchClause catchClause : tryStmt.getCatchClauses()) {
                                                for (Statement st : catchClause.getBody().getStatements())
                                                    shell.eval(st.toString());
                                            }
                                            tryStmt.getFinallyBlock().ifPresent(x -> {
                                                for (Statement st : x.getStatements())
                                                    shell.eval(st.toString());
                                            });
                                        } else if (stmts.isWhileStmt()) {
                                            WhileStmt whileStmt = stmts.asWhileStmt();
                                            for (Statement st : whileStmt.getBody().asBlockStmt().getStatements())
                                                shell.eval(st.toString());
                                        }
                                        break;
                                }
                            }
                            if (!completionInfo.remaining().isBlank())
                                completionInfo = shell.sourceCodeAnalysis().analyzeCompletion(completionInfo.remaining());
                            else break;
                        }
                    } catch (IllegalStateException e) {
                    }
                    int lineStartOffsetOfCurrentLine = textEditor.getLineStartOffsetOfCurrentLine();
                    String currentLine = textEditor.getText(lineStartOffsetOfCurrentLine,
                            textEditor.getCaretPosition() - lineStartOffsetOfCurrentLine);
                    List<SourceCodeAnalysis.Suggestion> completionSuggestions = shell.sourceCodeAnalysis()
                            .completionSuggestions(currentLine, currentLine.length(), new int[1]);
                    /*Trie trie = new Trie();
                    trie.addAll(completionSuggestions.stream().map(SourceCodeAnalysis.Suggestion::continuation)
                            .collect(Collectors.toList()));
                    List<String> completed = trie.findCompletions(currentLine);*/
                    if (provider.getCompletions(textEditor) != null) {
                        for (Completion completion : provider.getCompletions(textEditor))
                            provider.removeCompletion(completion);
                    }
                    for (SourceCodeAnalysis.Suggestion suggestion : completionSuggestions)
                        provider.addCompletion(new BasicCompletion(provider, suggestion.continuation()));
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void run(ViewModel viewModel) {
        String code = viewModel.getTextEditor().getText();
        JEditorPane editorPane = viewModel.getResultView();
        JProgressBar progressBar = viewModel.getStatusBar().getProgressBar();
        progressBar.setIndeterminate(true);
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try (ConsoleOutputStream consoleOutputStream = new ConsoleOutputStream(editorPane);
                     ConsoleErrorStream consoleErrorStream = new ConsoleErrorStream(editorPane);
                     PrintStream out = new PrintStream(consoleOutputStream);
                     PrintStream errStream = new PrintStream(consoleErrorStream);
                     JShell shell = JShell.builder().out(out).err(errStream).build()) {
                    SourceCodeAnalysis.CompletionInfo completion = shell.sourceCodeAnalysis().analyzeCompletion(code);
                    /*List<SnippetEvent> l = shell.eval(handler.parseImportXml()
                            .stream()
                            .map(imports -> String.format("import %s;", imports))
                            .collect(Collectors.joining()));*/
                    while (!completion.source().isBlank()) {
                        List<SnippetEvent> snippets = shell.eval(completion.source());

                        for (var snippet : snippets) {
                            // Check the status of the evaluation
                            String src = snippet.snippet().source().trim();
                            switch (snippet.status()) {
                                case VALID:
                                    logger.debug(src);
                                    break;
                                case REJECTED: //Compile time errors
                                    List<String> errors = shell.diagnostics(snippet.snippet())
                                            .map(x -> String.format("\n\"%s\" -> %s\n", src,
                                                    x.getMessage(Locale.ENGLISH)))
                                            .collect(Collectors.toList());
                                    logger.error("Code evaluation failed. Diagnostic info:\n{}", errors);
                                    errStream.println(errors);
                                    break;
                            }
                            //Runtime errors
                            if (snippet.exception() != null) {
                                logger.error("Code evaluation failed at \"{}\"", src);
                                errStream.printf("Code evaluation failed at \"%s\"\nDiagnostic info:\n", src);
                                snippet.exception().printStackTrace(errStream);
                                logger.error("EVALUATION ERROR", snippet.exception());
                                try {
                                    throw snippet.exception();
                                } catch (JShellException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        if (!completion.remaining().isBlank())
                            completion = shell.sourceCodeAnalysis().analyzeCompletion(completion.remaining());
                        else break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    progressBar.setValue(progressBar.getMinimum());
                    progressBar.setIndeterminate(false);
                }
            }
        });
    }

    @Override
    public void close() {
        shell.close();
    }
}
