package com.github.espressopad.utils;

import bibliothek.gui.dock.DefaultDockable;

import javax.swing.JComponent;
import javax.swing.filechooser.FileSystemView;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Utils {
    public static Font[] getMonospaceFonts() {
        FontRenderContext frc = new FontRenderContext(
                null,
                RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
                RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT
        );
        return Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts())
                .filter(font ->
                        font.getStringBounds("i", frc).getWidth() == font.getStringBounds("m", frc).getWidth())
                .toArray(Font[]::new);
    }

    public static Path validateDefaultDirectory() {
        Path path = Path.of(FileSystemView.getFileSystemView().getDefaultDirectory().getPath(), "EspressoPad");
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return path;
    }

    public static DefaultDockable createDockable(JComponent panel, String title) {
        DefaultDockable dockable = new DefaultDockable();
        dockable.setTitleText(title);
        panel.setOpaque(true);
        dockable.add(panel);
        return dockable;
    }
}
