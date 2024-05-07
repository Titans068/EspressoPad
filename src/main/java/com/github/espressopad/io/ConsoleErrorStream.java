package com.github.espressopad.io;

import javax.swing.JEditorPane;

public class ConsoleErrorStream extends ConsoleOutputStream {
    public ConsoleErrorStream(JEditorPane document) {
        super(document);
    }

    @Override
    public void write(int b) {
        super.writeContent(b);
        this.document.setText(String.format("<font color=\"B22222\">%s</font>", this.element.toString()));
    }
}
