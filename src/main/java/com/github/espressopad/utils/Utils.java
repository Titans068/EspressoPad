package com.github.espressopad.utils;

import javax.swing.filechooser.FileSystemView;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Label;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<Font> getMonospaceFonts() {
        Label th = new Label("1 l");
        Label tk = new Label("MWX");

        String[] fontFamilyList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        List<Font> mFamilyList = new ArrayList<>();

        for (String fontFamilyName : fontFamilyList) {
            Font font = new Font(fontFamilyName, Font.PLAIN, 14);
            th.setFont(font);
            tk.setFont(font);
            if (th.getBounds().getWidth() == tk.getBounds().getWidth())
                mFamilyList.add(font);
        }
        return mFamilyList;
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
}
