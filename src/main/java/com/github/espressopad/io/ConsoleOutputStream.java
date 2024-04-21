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
                element.append("<br>");
                break;
            case '\n':
                if (prev != null && prev != '\r')
                    element.append("<br>");
                break;
            case '\t':
                // Replace tabs with four non-breaking spaces
                element.append("&nbsp;".repeat(4));
                break;
            case '\b':
                element = new StringBuilder(element.substring(0, element.length() - 1));
                break;
            case '\f':
                element.append("<br>");
                element.append("&nbsp;".repeat(4));
                break;
            default:
                // Append other characters as text
                element.append(c);
                break;
        }
        prev = c;
        document.setText(element.toString());
    }
}
