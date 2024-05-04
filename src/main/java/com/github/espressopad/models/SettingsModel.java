package com.github.espressopad.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;

@JacksonXmlRootElement(localName = "settings")
public class SettingsModel implements Serializable {
    @JacksonXmlProperty(localName = "font")
    private String font;
    @JacksonXmlProperty(localName = "fontSize")
    private int fontSize;
    @JacksonXmlProperty(localName = "theme")
    private String theme;
    @JacksonXmlProperty(localName = "wordWrap")
    private boolean wordWrap;
    @JacksonXmlProperty(localName = "lookAndFeel")
    private String lookAndFeel;

    public String getFont() {
        return this.font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getTheme() {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isWordWrap() {
        return this.wordWrap;
    }

    public void setWordWrap(boolean wordWrap) {
        this.wordWrap = wordWrap;
    }

    public String getLookAndFeel() {
        return this.lookAndFeel;
    }

    public void setLookAndFeel(String lookAndFeel) {
        this.lookAndFeel = lookAndFeel;
    }
}
