package com.github.espressopad;

import com.github.espressopad.views.EspressoPadView;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class EspressoPadApplication {
    public static void main(String[] args) {
        /*try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }*/

        EspressoPadView root = new EspressoPadView();
        JFrame frame = new JFrame("Espresso Pad");
        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(500, 500));
        frame.add(root, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
