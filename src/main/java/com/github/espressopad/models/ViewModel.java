package com.github.espressopad.models;

import com.github.espressopad.views.components.StatusBar;
import com.github.espressopad.views.components.TextEditor;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import java.io.File;

public class ViewModel {
    private JPanel tab;
    private TextEditor textEditor;
    private JEditorPane resultView;
    private StatusBar statusBar;
    private File backingFile = null;

    public ViewModel() {
        this(null, null, null, null);
    }

    private ViewModel(JPanel tab, TextEditor textEditor, JEditorPane resultView, StatusBar statusBar) {
        this.setTextEditor(textEditor);
        this.setResultView(resultView);
        this.setStatusBar(statusBar);
        this.setTab(tab);
    }

    public JPanel getTab() {
        return this.tab;
    }

    public void setTab(JPanel tab) {
        if (tab == null)
            tab = new JPanel();
        this.tab = tab;
    }

    public TextEditor getTextEditor() {
        return this.textEditor;
    }

    public void setTextEditor(TextEditor textEditor) {
        if (textEditor == null)
            textEditor = new TextEditor(this);
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

    public StatusBar getStatusBar() {
        return this.statusBar;
    }

    public void setStatusBar(StatusBar statusBar) {
        if (statusBar == null)
            statusBar = new StatusBar();
        this.statusBar = statusBar;
    }

    public File getBackingFile() {
        return this.backingFile;
    }

    public void setBackingFile(File backingFile) {
        this.backingFile = backingFile;
    }
}
