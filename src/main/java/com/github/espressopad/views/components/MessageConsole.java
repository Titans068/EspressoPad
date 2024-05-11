package com.github.espressopad.views.components;

import com.github.espressopad.io.ConsoleOutputStream;
import com.github.espressopad.utils.LimitLinesDocumentListener;

import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.io.PrintStream;

/*
 *  Create a simple console to display text messages.
 *
 *  Messages can be directed here from different sources. Each source can
 *  have its messages displayed in a different color.
 *
 *  Messages can either be appended to the console or inserted as the first
 *  line of the console
 *
 *  You can limit the number of lines to hold in the Document.
 */
public class MessageConsole {
    private final JTextComponent textComponent;
    private final Document document;
    private final boolean isAppend;
    private DocumentListener limitLinesListener;

    public JTextComponent getTextComponent() {
        return this.textComponent;
    }

    public boolean isAppend() {
        return this.isAppend;
    }

    public Document getDocument() {
        return this.document;
    }

    public MessageConsole(JTextComponent textComponent) {
        this(textComponent, true);
    }

    /*
     *	Use the text component specified as a simply console to display
     *  text messages.
     *
     *  The messages can either be appended to the end of the console or
     *  inserted as the first line of the console.
     */
    public MessageConsole(JTextComponent textComponent, boolean isAppend) {
        this.textComponent = textComponent;
        this.document = textComponent.getDocument();
        this.isAppend = isAppend;
    }

    /*
     *  Redirect the output from the standard output to the console
     *  using the default text color and null PrintStream
     */
    public ConsoleOutputStream redirectOut() {
        return this.redirectOut(null, null);
    }

    /*
     *  Redirect the output from the standard output to the console
     *  using the specified color and PrintStream. When a PrintStream
     *  is specified the message will be added to the Document before
     *  it is also written to the PrintStream.
     */
    public ConsoleOutputStream redirectOut(Color textColor, PrintStream printStream) {
        return new ConsoleOutputStream(this, textColor, printStream);
    }

    /*
     *  Redirect the output from the standard error to the console
     *  using the default text color and null PrintStream
     */
    public ConsoleOutputStream redirectErr() {
        return this.redirectErr(null, null);
    }

    /*
     *  Redirect the output from the standard error to the console
     *  using the specified color and PrintStream. When a PrintStream
     *  is specified the message will be added to the Document before
     *  it is also written to the PrintStream.
     */
    public ConsoleOutputStream redirectErr(Color textColor, PrintStream printStream) {
        return new ConsoleOutputStream(this, textColor, printStream);
    }

    /*
     *  To prevent memory from being used up you can control the number of
     *  lines to display in the console
     *
     *  This number can be dynamically changed, but the console will only
     *  be updated the next time the Document is updated.
     */
    public void setMessageLines(int lines) {
        if (this.limitLinesListener != null)
            this.document.removeDocumentListener(this.limitLinesListener);

        this.limitLinesListener = new LimitLinesDocumentListener(lines, this.isAppend);
        this.document.addDocumentListener(this.limitLinesListener);
    }
}
