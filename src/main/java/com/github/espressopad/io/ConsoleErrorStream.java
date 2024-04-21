package com.github.espressopad.io;

import javax.swing.JEditorPane;

public class ConsoleErrorStream extends ConsoleOutputStream {
    public ConsoleErrorStream(JEditorPane document) {
        super(document);
    }

    @Override
    public void write(int b) {
        char c = (char) b;
        switch (c) {
            case '\r':
                // Append a <br> element for newline characters
                this.element.append("<br>");
                break;
            case '\n':
                if (prev != null && prev != '\r')
                    this.element.append("<br>");
                break;
            case '\t':
                // Replace tabs with four non-breaking spaces
                this.element.append("&nbsp;".repeat(4));
                break;
            case '\b':
                this.element = new StringBuilder(this.element.substring(0, this.element.length() - 1));
                break;
            case '\f':
                this.element.append("<br>");
                this.element.append("&nbsp;".repeat(4));
                break;
            default:
                // Append other characters as text
                this.element.append(c);
                break;
        }
        this.prev = c;
        this.document.setText(String.format("<font color=\"B22222\">%s</font>", element.toString()));
    }
}
