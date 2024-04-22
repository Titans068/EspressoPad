package com.github.espressopad.views;

import com.github.espressopad.views.components.PlaceHolderTextField;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

public class SettingsView {
    private final JTabbedPane view = new JTabbedPane();
    private JRadioButton searchDependencyRadio;
    private JButton searchDependencyBtn;
    private JRadioButton pickJarRadio;
    private JButton pickJarBtn;
    private PlaceHolderTextField artifactIDText;
    private PlaceHolderTextField extensionText;
    private PlaceHolderTextField classifierText;
    private PlaceHolderTextField versionText;
    private PlaceHolderTextField groupIDText;
    private JList<String> searchResultsList;
    private JButton addArtifactButton;
    private JList<String> installedArtifactsList;
    private JButton removeInstalledArtifactBtn;
    private JButton saveInstalledArtifactBtn;
    private PlaceHolderTextField importText;
    private JButton addImportBtn;
    private JList<String> importList;
    private JButton removeImportBtn;
    private JButton saveImportBtn;

    public SettingsView() {
        this.setupDependenciesView();
        this.setupImportsManagement();
    }

    public void show(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Settings", true);
        dialog.setSize(new Dimension(700, 500));
        dialog.setContentPane(this.view);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void setupDependenciesView() {
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setPreferredSize(new Dimension(200, 50));
        panel1.add(panel2, BorderLayout.NORTH);
        searchDependencyRadio = new JRadioButton();
        searchDependencyRadio.setSelected(true);
        searchDependencyRadio.setText("Search for dependencies");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(searchDependencyRadio, gbc);
        pickJarRadio = new JRadioButton();
        pickJarRadio.setText("Pick Jar file");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(pickJarRadio, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel3, gbc);
        groupIDText = new PlaceHolderTextField();
        groupIDText.setPlaceHolder("Group ID");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(groupIDText, gbc);
        artifactIDText = new PlaceHolderTextField();
        artifactIDText.setPlaceHolder("Artifact ID");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(artifactIDText, gbc);
        extensionText = new PlaceHolderTextField();
        extensionText.setPlaceHolder("Extension");
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(extensionText, gbc);
        classifierText = new PlaceHolderTextField();
        classifierText.setPlaceHolder("Classifier");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(classifierText, gbc);
        versionText = new PlaceHolderTextField();
        versionText.setPlaceHolder("Version");
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(versionText, gbc);
        searchDependencyBtn = new JButton();
        searchDependencyBtn.setIcon(FontIcon.of(FontAwesomeSolid.SEARCH, 15));
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(searchDependencyBtn, gbc);
        pickJarBtn = new JButton();
        pickJarBtn.setText("Pick JAR");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel2.add(pickJarBtn, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel4.setPreferredSize(new Dimension(78, 300));
        panel1.add(panel4, BorderLayout.CENTER);
        searchResultsList = new JList<>();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        searchResultsList.setBorder(BorderFactory.createTitledBorder("Search Results"));
        panel4.add(searchResultsList, gbc);
        addArtifactButton = new JButton();
        addArtifactButton.setIcon(FontIcon.of(FontAwesomeSolid.ARROW_RIGHT, 15));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = .1;
        panel4.add(addArtifactButton, gbc);
        installedArtifactsList = new JList<>();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        installedArtifactsList.setBorder(BorderFactory.createTitledBorder("Installed Artifacts"));
        panel4.add(installedArtifactsList, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weighty = 0;
        panel4.add(panel5, gbc);
        removeInstalledArtifactBtn = new JButton();
        removeInstalledArtifactBtn.setIcon(FontIcon.of(FontAwesomeSolid.MINUS, 15));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(removeInstalledArtifactBtn, gbc);
        this.saveInstalledArtifactBtn = new JButton();
        this.saveInstalledArtifactBtn.setIcon(FontIcon.of(FontAwesomeSolid.SAVE, 15));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(this.saveInstalledArtifactBtn, gbc);

        this.view.addTab("Manage Dependencies", panel1);
    }

    private void setupImportsManagement() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        importText = new PlaceHolderTextField();
        importText.setPlaceHolder("Wildcard import string e.g. java.net.*");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(importText, gbc);
        addImportBtn = new JButton();
        addImportBtn.setIcon(FontIcon.of(FontAwesomeSolid.PLUS, 15));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = .1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addImportBtn, gbc);
        importList = new JList<>();
        importList.setBorder(BorderFactory.createTitledBorder("Imports"));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(importList, gbc);
        removeImportBtn = new JButton();
        removeImportBtn.setIcon(FontIcon.of(FontAwesomeSolid.MINUS, 15));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(removeImportBtn, gbc);
        saveImportBtn = new JButton();
        saveImportBtn.setIcon(FontIcon.of(FontAwesomeSolid.SAVE, 15));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panel.add(saveImportBtn, gbc);

        this.view.addTab("Manage Imports", panel);
    }

    private String getDependencyString() {
        String dep = String.format("%s:%s:%s", this.groupIDText.getText(), this.artifactIDText.getText(),
                this.versionText.getText());
        if (!this.extensionText.getText().isBlank())
            dep = String.format("%s:%s:%s:%s", this.groupIDText.getText(),
                    this.artifactIDText.getText(), this.extensionText.getText(), this.versionText.getText());
        if (!this.classifierText.getText().isBlank())
            dep = String.format("%s:%s:%s:%s", this.groupIDText.getText(),
                    this.artifactIDText.getText(), this.classifierText.getText(), this.versionText.getText());
        if (!this.extensionText.getText().isBlank() && !this.classifierText.getText().isBlank())
            dep = String.format("%s:%s:%s:%s:%s", this.groupIDText.getText(),
                    this.artifactIDText.getText(), this.extensionText.getText(), this.classifierText.getText(),
                    this.versionText.getText());
        return dep;
    }
}
