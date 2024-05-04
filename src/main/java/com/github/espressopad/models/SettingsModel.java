package com.github.espressopad.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.github.espressopad.utils.FontDeserializer;
import com.github.espressopad.utils.FontSerializer;

import java.awt.Font;
import java.io.Serializable;

@JacksonXmlRootElement(localName = "settings")
public class SettingsModel implements Serializable {
    @JacksonXmlProperty(localName = "font")
    @JsonSerialize(using = FontSerializer.class)
    @JsonDeserialize(using = FontDeserializer.class)
    private Font font;
    @JacksonXmlProperty(localName = "theme")
    private String theme;
    @JacksonXmlProperty(localName = "wordWrap")
    private boolean wordWrap;
    @JacksonXmlProperty(localName = "lookAndFeel")
    private String lookAndFeel;

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        this.font = font;
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
