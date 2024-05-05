package com.github.espressopad;

import com.github.espressopad.views.EspressoPadView;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;
import java.util.ResourceBundle;

public class EspressoPadApplication {
    public static void main(String[] args) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.getDefault());
        JFrame frame = new JFrame(resourceBundle.getString("espresso.pad"));
        EspressoPadView root = new EspressoPadView(frame);
        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(800, 700));
        frame.add(root, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
    }
}
