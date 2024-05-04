package com.github.espressopad.io;

import javax.swing.JEditorPane;
import java.io.OutputStream;

public class ConsoleOutputStream extends OutputStream {
    protected final JEditorPane document;
    protected StringBuilder element = new StringBuilder();
    protected Character prev = null;

    public ConsoleOutputStream(JEditorPane document) {
        this.document = document;
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
                if (this.prev != null && this.prev != '\r')
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
        this.document.setText(this.element.toString());
    }
}
