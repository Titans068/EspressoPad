package com.github.espressopad.views.components;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.ResourceBundle;

public class HyperlinkLabel extends JLabel {
    private String url;
    private final String html = "<html><a href=''>%s</a></html>";
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    public HyperlinkLabel(String text) {
        this(text, null, null);
    }

    public HyperlinkLabel(String text, String url) {
        this(text, url, null);
    }

    public void setURL(String url) {
        this.url = url;
    }

    public HyperlinkLabel(String text, String url, String tooltip) {
        super(text);
        this.url = url;
        this.setForeground(Color.BLUE.darker());
        this.setToolTipText(tooltip);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                HyperlinkLabel.this.setText(String.format(HyperlinkLabel.this.html, text));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                HyperlinkLabel.this.setText(text);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(URI.create(HyperlinkLabel.this.url));
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(HyperlinkLabel.this,
                            String.format(HyperlinkLabel.this.resourceBundle.getString("could.not.open.the.hyperlink.error.s"), e1.getMessage()),
                            HyperlinkLabel.this.resourceBundle.getString("error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
