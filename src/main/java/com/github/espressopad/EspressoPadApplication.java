package com.github.espressopad;

import com.github.espressopad.views.EspressoPadView;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class EspressoPadApplication {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Espresso Pad");
        EspressoPadView root = new EspressoPadView(frame);
        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(800, 700));
        frame.add(root, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
    }
}
