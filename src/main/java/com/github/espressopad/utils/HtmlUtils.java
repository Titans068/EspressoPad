package com.github.espressopad.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

    public static String convertJavaDoc(String input) {
        // Define regular expressions for JavaDoc tags
        // You can add more patterns for other JavaDoc tags as needed
        if (input == null || input.isBlank()) return "";

        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.getDefault());
        Pattern pattern = Pattern.compile("@author[\\s\\t\\n\\r\\f\\v]+(.*?)");
        Matcher matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.author.b.1.div"));

        pattern = Pattern.compile("@apiNote[\\s\\t\\n\\r\\f\\v]+(.*?)");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.api.note.b.1.div"));

        pattern = Pattern.compile("\\{\\@code[\\s\\t\\n\\r\\f\\v]+(.*?)\\}");
        // Replace {@code ...} with <code>...</code>
        matcher = pattern.matcher(input);
        input = matcher.replaceAll("<code>$1</code>");

        pattern = Pattern.compile("<pre>\\{@code\\s*([^}]*)\\s*}</pre>");
        // Replace {@code ...} with <code>...</code>
        matcher = pattern.matcher(input);
        input = matcher.replaceAll("<pre><code>$1</code></pre>");

        pattern = Pattern.compile("@deprecated[\\s\\t\\n\\r\\f\\v]+(.*?)");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.deprecated.b.1.div"));

        pattern = Pattern.compile("\\{*\\@(exception|throws)[\\s\\t\\n\\r\\f\\v]+(.*?)\\}*");
        matcher = pattern.matcher(input);
        while (matcher.find())
            input = matcher.replaceAll(resourceBundle.getString("div.b.exception.b.2.div"));

        pattern = Pattern.compile("<div><b>Exception:</b>\\s*</div>\\b(\\w+)\\b");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.exception.b.nbsp.span.class.red.1.span"));

        pattern = Pattern.compile("@implNote[\\s\\t\\n\\r\\f\\v]+(.*?)");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.implementation.note.b.1.div"));

        pattern = Pattern.compile("@implSpec[\\s\\t\\n\\r\\f\\v]+(.*?)");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.implementation.spec.b.1.div"));

        pattern = Pattern.compile("@jls[\\s\\t\\n\\r\\f\\v]+(.*?)");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll("\n\t\t<div><b>jls: </b>$1</div>");

        // Replace {@link ...} with <a href="...">...</a>
        pattern = Pattern.compile("\\{\\@(link|linkplain)[\\s\\t\\n\\r\\f\\v]+(.*?)\\}");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll("<a href=\"$2\">$2</a>");

        pattern = Pattern.compile("\\{*@param[\\s\\t\\n\\r\\f\\v]+(.*?)\\}*");
        matcher = pattern.matcher(input);
        while (matcher.find())
            input = matcher.replaceAll(resourceBundle.getString("div.b.parameter.b.nbsp.1.div"));

        //"\n\t\t<div><b>Params:</b>&nbsp;$1</div>"
        pattern = Pattern.compile("<b>Parameter:</b>&nbsp;</div>((\\b(\\w+)\\b)|(&lt;\\w+&gt;))");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("b.parameter.b.nbsp.div.span.class.red.1.span.nbsp"));

        pattern = Pattern.compile("\\{*\\@return[\\s\\t\\n\\r\\f\\v]+(.*?)\\}*");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.returns.b.1.div"));

        pattern = Pattern.compile("\\{*\\@see[\\s\\t\\n\\r\\f\\v]+(.*?)\\}*");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.see.also.b.1.div"));

        pattern = Pattern.compile("\\{*\\@serial[\\s\\t\\n\\r\\f\\v]+(.*?)\\}*");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.serial.b.1.div"));

        pattern = Pattern.compile("\\{*\\@serialData[\\s\\t\\n\\r\\f\\v]+(.*?)\\}*");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.serial.data.b.1.div"));

        pattern = Pattern.compile("\\{*\\@serialField[\\s\\t\\n\\r\\f\\v]+(.*?)\\}*");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.serial.field.b.1.div"));

        pattern = Pattern.compile("\\{*\\@since[\\s\\t\\n\\r\\f\\v]+(.*?)\\}*");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.since.b.1.div"));

        pattern = Pattern.compile("@spec[\\s\\t\\n\\r\\f\\v]+(.*?)");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.spec.b.1.div"));

        pattern = Pattern.compile("\\{\\@value[\\s\\t\\n\\r\\f\\v]+(.*?)\\}");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll("\n\t\t<div><pre><code>$1</code></pre></div>");

        pattern = Pattern.compile("\\{*\\@version[\\s\\t\\n\\r\\f\\v]+(.*?)\\}*");
        matcher = pattern.matcher(input);
        input = matcher.replaceAll(resourceBundle.getString("div.b.version.b.1.div"));

        return input;
    }
}
