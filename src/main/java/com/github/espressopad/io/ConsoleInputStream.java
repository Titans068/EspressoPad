package com.github.espressopad.io;

import com.github.espressopad.views.components.StatusBar;

import javax.swing.JOptionPane;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

public class ConsoleInputStream extends InputStream {
    private final StringBuilder buffer = new StringBuilder();
    private final StatusBar statusBar;
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    public ConsoleInputStream(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override
    public int read() {
        if (this.buffer.length() == 0) {
            this.statusBar.setStatusLabel(this.resourceBundle.getString("awaiting.input"));
            this.buffer.append(
                    JOptionPane.showInputDialog(
                            JOptionPane.getFrameForComponent(this.statusBar),
                            this.resourceBundle.getString("enter.input"),
                            this.resourceBundle.getString("awaiting.input"),
                            JOptionPane.QUESTION_MESSAGE)
            );
            this.buffer.append("\n");
        }

        char charToRead = this.buffer.charAt(0);
        this.buffer.deleteCharAt(0);
        System.out.print(charToRead);
        return charToRead;
    }
}
