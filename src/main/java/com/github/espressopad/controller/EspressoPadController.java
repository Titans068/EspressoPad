package com.github.espressopad.controller;

import com.github.espressopad.io.ConsoleErrorStream;
import com.github.espressopad.io.ConsoleInputStream;
import com.github.espressopad.io.ConsoleOutputStream;
import com.github.espressopad.models.ViewModel;
import com.github.espressopad.views.components.FileTree;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EspressoPadController implements AutoCloseable {
    private static final JShell shell = JShell.builder().out(null).in(null).err(null).build();
    private final Logger logger = LoggerFactory.getLogger(EspressoPadController.class);
    private final DefaultCompletionProvider provider = new DefaultCompletionProvider();

    public static JShell getShell() {
        return shell;
    }

    public void setupTextChangeListener(TextEditor textEditor) {
        AutoCompletion ac = new AutoCompletion(this.provider);
        this.provider.setAutoActivationRules(true, ".");
        ac.setAutoCompleteEnabled(true);
        ac.setAutoActivationEnabled(true);
        ac.setShowDescWindow(true);
        ac.install(textEditor);
        textEditor.addCaretListener(event -> this.setupCaretChangeEvent(textEditor));
        textEditor.getDocument().addDocumentListener(new TextEditorListener(textEditor));
    }

    private void setupCaretChangeEvent(TextEditor textEditor) {
        textEditor.getViewModel()
                .getStatusBar()
                .setCharacterPosition(
                        String.format("%d:%d", textEditor.getCaretLineNumber() + 1, textEditor.getCaretOffsetFromLineStart() + 1)
                );
    }

    private void setupTextChangeEvent(TextEditor textEditor) {
        Executors.newFixedThreadPool(3).submit(() -> this.onChangeEvent(textEditor));
    }

    private void onChangeEvent(TextEditor textEditor) {
        try {
            textEditor.setDirty(true);
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
        try {
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

    public File setupTreeMouseListener(FileTree fileTree, MouseEvent event) {
        int selRow = fileTree.getRowForLocation(event.getX(), event.getY());
        TreePath selPath = fileTree.getPathForLocation(event.getX(), event.getY());
        if (selRow != -1 && selPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            fileTree.setSelectionPath(selPath);
            JPopupMenu contextMenu = new JPopupMenu();
            JMenuItem refreshMenuItem = new JMenuItem("Refresh tree");
            refreshMenuItem.addActionListener(e -> fileTree.refreshTree());
            contextMenu.add(refreshMenuItem);
            if (node.isLeaf()) {
                File file = Path.of(
                        String.valueOf(((DefaultMutableTreeNode) node.getParent()).getUserObject()),
                        String.valueOf(node)
                ).toFile();
                if (SwingUtilities.isRightMouseButton(event)) {
                    JMenuItem renameMenuItem = new JMenuItem("Rename file");
                    renameMenuItem.addActionListener(e -> this.renameFile(fileTree, file));
                    contextMenu.add(renameMenuItem);
                    JMenuItem deleteMenuItem = new JMenuItem("Delete file");
                    deleteMenuItem.addActionListener(e -> this.deleteFile(fileTree, file));
                    contextMenu.add(deleteMenuItem);
                    JMenuItem openFileLocationMenuItem = new JMenuItem("Open File Location");
                    openFileLocationMenuItem.addActionListener(e -> openFileLocation(file));
                    contextMenu.add(openFileLocationMenuItem);
                    contextMenu.show(fileTree, event.getX(), event.getY());
                } else if (event.getClickCount() == 2) return file;
            } else if (SwingUtilities.isRightMouseButton(event)) {
                JMenuItem openFileLocationMenuItem = new JMenuItem("Open File Location");
                openFileLocationMenuItem.addActionListener(e -> openFileLocation(new File(String.valueOf(node))));
                contextMenu.add(openFileLocationMenuItem);
                contextMenu.show(fileTree, event.getX(), event.getY());
            }
        }
        return null;
    }

    private void renameFile(FileTree fileTree, File file) {
        Object newName = JOptionPane.showInputDialog(
                JOptionPane.getFrameForComponent(fileTree),
                String.format("Rename %s?", file.getName()),
                "Rename file?",
                JOptionPane.QUESTION_MESSAGE,
                UIManager.getIcon("OptionPane.questionIcon"),
                null,
                file.getName()
        );
        String s = String.valueOf(newName);
        if (newName != null && !s.isBlank()) {
            file.renameTo(file.toPath().getParent().resolve(s).toFile());
            fileTree.refreshTree();
        }
    }

    private void deleteFile(FileTree fileTree, File file) {
        if (JOptionPane.showConfirmDialog(
                JOptionPane.getFrameForComponent(fileTree),
                String.format("Delete file %s?", file.getName()),
                "Delete file?",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            file.delete();
            fileTree.refreshTree();
        }
    }

    private void openFileLocation(File file) {
        try {
            if (file.isFile())
                Desktop.getDesktop().open(new File(file.getParent()));
            else Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(ViewModel viewModel) {
        String code = viewModel.getTextEditor().getText();
        JEditorPane editorPane = viewModel.getResultView();
        JProgressBar progressBar = viewModel.getStatusBar().getProgressBar();
        progressBar.setIndeterminate(true);
        viewModel.getStatusBar().setStatusLabel("Running");
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try (ConsoleOutputStream consoleOutputStream = new ConsoleOutputStream(editorPane);
                     ConsoleErrorStream consoleErrorStream = new ConsoleErrorStream(editorPane);
                     ConsoleInputStream consoleInputStream = new ConsoleInputStream(viewModel.getStatusBar());
                     PrintStream out = new PrintStream(consoleOutputStream);
                     PrintStream errStream = new PrintStream(consoleErrorStream);
                     JShell shell = JShell.builder().out(out).err(errStream).in(consoleInputStream).build()) {
                    SourceCodeAnalysis.CompletionInfo completion = shell.sourceCodeAnalysis().analyzeCompletion(code);
                    /*TODO List<SnippetEvent> l = shell.eval(handler.parseImportXml()
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
                    viewModel.getStatusBar().setStatusLabel("Ready");
                }
            }
        });
    }

    @Override
    public void close() {
        shell.close();
    }


    class TextEditorListener implements DocumentListener {
        private final TextEditor textEditor;

        TextEditorListener(TextEditor textEditor) {
            this.textEditor = textEditor;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            setupTextChangeEvent(this.textEditor);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            setupTextChangeEvent(this.textEditor);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            setupTextChangeEvent(this.textEditor);
        }
    }
}
