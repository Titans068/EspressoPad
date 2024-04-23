package com.github.espressopad.io;

import com.github.espressopad.views.components.StatusBar;

import javax.swing.JOptionPane;
import java.io.InputStream;

public class ConsoleInputStream extends InputStream {
    private final StringBuilder buffer = new StringBuilder();
    private final StatusBar statusBar;

    public ConsoleInputStream(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override
    public int read() {
        if (this.buffer.length() == 0) {
            this.statusBar.setStatusLabel("Awaiting input");
            this.buffer.append(
                    JOptionPane.showInputDialog(
                            JOptionPane.getFrameForComponent(this.statusBar),
                            "Enter input:",
                            "Awaiting input",
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
