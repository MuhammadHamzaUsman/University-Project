package com.example.TextEditor.Interpreter;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import com.example.TextEditor.Interpreter.tokens.Tokens;

public class Highlighter {

    private static final String KEYWORD = "\\b(" + String.join("|", Tokens.KEYWORDS.keySet()) + ")\\b";
    private static final String LITERAL = "\\b(true|false|null)\\b";
    private static final String PAREN = "\\(|\\)";
    private static final String BRACES = "\\{|\\}";
    private static final String COMMENT = "//[^\n]*|/\\*(.|\\R)*?\\*/";
    private static final String NUMBER = "\\b\\d+(\\.\\d+)?\\b";
    private static final String IDENTIFIER = "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b";
    private static final String FUNCTION   = "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?=\\()";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD + ")"
        + "|(?<LITERAL>" + LITERAL + ")"
        + "|(?<FUNCTION>" + FUNCTION + ")"
        + "|(?<IDENTIFIER>" + IDENTIFIER + ")"
        + "|(?<PAREN>" + PAREN + ")"
        + "|(?<BRACES>" + BRACES + ")"
        + "|(?<COMMENT>" + COMMENT + ")"
        + "|(?<NUMBER>" + NUMBER + ")"
    );

    public static StyleSpans<Collection<String>> highlight(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int end = 0;
        StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();
        
        while (matcher.find()) {
            String styleClass =
                matcher.group("KEYWORD") != null ? "keyword"
                : matcher.group("LITERAL") != null ? "literal"
                : matcher.group("FUNCTION") != null ? "function"
                : matcher.group("IDENTIFIER") != null ? "identifier"
                : matcher.group("PAREN") != null ? "paren"
                : matcher.group("BRACES") != null ? "bracket"
                : matcher.group("COMMENT") != null ? "comment"
                : matcher.group("NUMBER") != null ? "number"
                : null;

            styleSpansBuilder.add(Collections.emptyList(), matcher.start() - end);
            styleSpansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            end = matcher.end();
        }

        styleSpansBuilder.add(Collections.emptyList(), text.length() - end);
        return styleSpansBuilder.create();
    }
}
