package com.github.espressopad.models;

import com.github.espressopad.views.components.TextEditor;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

public class ViewModel {
    private JPanel tab;
    private TextEditor textEditor;
    private JEditorPane resultView;

    public ViewModel() {
        this(null, null, null);
    }

    private ViewModel(JPanel tab, TextEditor textEditor, JEditorPane resultView) {
        this.setTextEditor(textEditor);
        this.setResultView(resultView);
        this.setTab(tab);
    }

    public JPanel getTab() {
        return this.tab;
    }

    public void setTab(JPanel tab) {
        if (tab == null)
            tab = new JPanel();
        this.tab = tab;
        this.tab.setLayout(new BorderLayout());
        double divider = .6d;
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new RTextScrollPane(this.textEditor), this.resultView);
        splitPane.setDividerLocation(divider);
        splitPane.setResizeWeight(divider);
        this.tab.add(splitPane, BorderLayout.CENTER);
        //TODO this.tab.getTabPane().getStylesheets().add(this.getClass().getResource("editor.css").toExternalForm());
    }

    public TextEditor getTextEditor() {
        return this.textEditor;
    }

    public void setTextEditor(TextEditor textEditor) {
        if (textEditor == null)
            textEditor = new TextEditor();
        this.textEditor = textEditor;
    }

    public JEditorPane getResultView() {
        return this.resultView;
    }

    public void setResultView(JEditorPane resultView) {
        if (resultView == null)
            resultView = new JEditorPane();
        this.resultView = resultView;
        this.resultView.setEditable(false);
        this.resultView.setContentType("text/html");
    }
}
