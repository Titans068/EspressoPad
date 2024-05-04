package com.github.espressopad.views.components;

import com.github.espressopad.models.ViewModel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

public class TextEditor extends RSyntaxTextArea {
    private ViewModel viewModel;
    private RTextScrollPane scrollPane;
    private boolean dirty = false;

    public TextEditor(ViewModel viewModel) {
        this();
        this.viewModel = viewModel;
    }

    private TextEditor() {
        super();
        this.setBracketMatchingEnabled(true);
        this.setCloseCurlyBraces(true);
        this.setAnimateBracketMatching(true);
        this.setPaintMatchedBracketPair(true);
        this.setHighlightCurrentLine(true);
        this.setSyntaxEditingStyle(SYNTAX_STYLE_JAVA);
        this.setCodeFoldingEnabled(true);
        //this.setLineWrap(true);
        this.setMarkOccurrences(true);
        this.setAutoIndentEnabled(true);
    }

    public RTextScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public void setScrollPane(RTextScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public ViewModel getViewModel() {
        return this.viewModel;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
