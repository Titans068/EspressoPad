package com.github.espressopad.views;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class SavePromptDialog {
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.getDefault());
    private final List<String> unsavedFiles;
    private final JDialog dialog;
    private final JPanel panel = new JPanel(new BorderLayout());
    private final DefaultListModel<String> unsavedListModel = new DefaultListModel<>();
    private final JList<String> unsavedList = new JList<>(this.unsavedListModel);
    private final JButton yesButton = new JButton(this.resourceBundle.getString("yes"));
    private final JButton noButton = new JButton(this.resourceBundle.getString("no"));
    private final JButton cancelButton = new JButton(this.resourceBundle.getString("cancel"));
    private int result = JOptionPane.CLOSED_OPTION;

    public SavePromptDialog(JFrame frame, List<String> unsavedFiles) {
        this.dialog = new JDialog(frame, this.resourceBundle.getString("save.changes"), true);
        this.unsavedFiles = unsavedFiles;
        this.setupInterface();
    }

    private void setupInterface() {
        this.panel.setBorder(BorderFactory.createTitledBorder(this.resourceBundle.getString("save.changes.to.the.following")));
        this.unsavedListModel.addAll(this.unsavedFiles);
        this.panel.add(new JScrollPane(this.unsavedList), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();

        this.yesButton.addActionListener(event -> this.setResult(JOptionPane.YES_OPTION));
        this.noButton.addActionListener(event -> this.setResult(JOptionPane.NO_OPTION));
        this.cancelButton.addActionListener(event -> this.setResult(JOptionPane.CANCEL_OPTION));
        buttonPanel.add(this.yesButton);
        buttonPanel.add(this.noButton);
        buttonPanel.add(this.cancelButton);

        this.panel.add(buttonPanel, BorderLayout.SOUTH);
        this.dialog.setContentPane(this.panel);
        this.dialog.getRootPane().setDefaultButton(this.yesButton);
    }

    public int getResult() {
        this.dialog.setSize(new Dimension(350, 200));
        this.dialog.setLocationRelativeTo(null);
        this.dialog.setVisible(true);
        return this.result;
    }

    private void setResult(int result) {
        this.result = result;
        this.dialog.dispose();
    }
}
