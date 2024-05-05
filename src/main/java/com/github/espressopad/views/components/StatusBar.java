package com.github.espressopad.views.components;

import org.fife.rsta.ui.SizeGripIcon;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class StatusBar extends JPanel {
    private final JLabel findOccurrencesLabel;
    private final JLabel statusLabel;
    private final JLabel characterPosition;
    private final JProgressBar progressBar;
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    public StatusBar() {
        this.statusLabel = new JLabel(this.resourceBundle.getString("ready"));
        this.findOccurrencesLabel = new JLabel();
        this.characterPosition = new JLabel();
        this.characterPosition.setToolTipText(this.resourceBundle.getString("row.column"));
        this.progressBar = new JProgressBar();
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(this.statusLabel);
        this.add(Box.createHorizontalGlue());
        this.add(this.findOccurrencesLabel);
        this.add(this.characterPosition);
        this.add(Box.createHorizontalGlue());
        this.add(this.progressBar);
        this.add(new JLabel(new SizeGripIcon()));
    }

    public void setFindOccurrencesLabel(String label) {
        this.findOccurrencesLabel.setText(label);
    }

    public void setStatusLabel(String label) {
        this.statusLabel.setText(label);
    }

    public void setCharacterPosition(String label) {
        this.characterPosition.setText(label);
    }

    public JProgressBar getProgressBar() {
        return this.progressBar;
    }
}
