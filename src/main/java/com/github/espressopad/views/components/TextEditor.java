package com.github.espressopad.views.components;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class TextEditor extends RSyntaxTextArea {

    public TextEditor() {
        super();
        this.setBracketMatchingEnabled(true);
        this.setCloseCurlyBraces(true);
        this.setAnimateBracketMatching(true);
        this.setPaintMatchedBracketPair(true);
        this.setHighlightCurrentLine(true);
        this.setSyntaxEditingStyle(SYNTAX_STYLE_JAVA);
        this.setCodeFoldingEnabled(true);
        this.setLineWrap(true);
    }
}
