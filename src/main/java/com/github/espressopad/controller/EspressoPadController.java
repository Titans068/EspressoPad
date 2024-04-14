package com.github.espressopad.controller;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EspressoPadController {
    public void setupClosableTabs(JTabbedPane tabPane, String title) {
        int index = tabPane.indexOfTab(title);
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        JButton btnClose = new JButton("x");
        CompoundBorder border = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(), BorderFactory.createEmptyBorder(0, 2, 0, 2));
        btnClose.setBorder(border);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusable(false);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setupRemoveTab(tabPane);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 10, 0, 1);

        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);

        tabPane.setTabComponentAt(index, pnlTab);
    }

    public void setupMiddleMouseListener(JTabbedPane tabPane) {
        tabPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tabPane.getBoundsAt(tabPane.getSelectedIndex()).contains(e.getPoint()) &&
                        e.getButton() == MouseEvent.BUTTON2)
                    setupRemoveTab(tabPane);
            }
        });
    }

    private void setupRemoveTab(JTabbedPane tabPane) {
        if (tabPane.getTabCount() <= 2) return;
        tabPane.remove(tabPane.getSelectedComponent());
        tabPane.setSelectedComponent(tabPane.getComponentAt(tabPane.getTabCount() - 2));
    }
}
