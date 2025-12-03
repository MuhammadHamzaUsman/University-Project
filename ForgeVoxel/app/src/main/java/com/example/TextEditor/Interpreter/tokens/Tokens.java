package com.example.TextEditor.Interpreter.tokens;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.shape.Colors;
import com.example.shape.Shape;
import com.example.shape.Size;

public enum Tokens {
    NUMBER,
    BOOLEAN,
    IDENTIFIER,
    LEFT_PARANTHESES,
    RIGHT_PARANTHESES,
    COMMA,
    OPERATOR,
    IF,
    ELSE,
    LEFT_BRACES,
    RIGHT_BRACES,
    RETURN,
    VAR,
    WHILE,
    DEF,
    NULL,
    COLON;

    public String prefix(){
        return this.name() + ": ";
    }

    public final static Map<String, Tokens> KEYWORDS = Map.ofEntries(
        Map.entry("if", IF),
        Map.entry("else", ELSE),
        Map.entry("return", RETURN),
        Map.entry("var",  VAR),
        Map.entry("while", WHILE),
        Map.entry("def", DEF),
        Map.entry("null", NULL)
    );

    private final static Set<String> RESERVE_WORDS;

    static{
        RESERVE_WORDS = new HashSet<>();
        RESERVE_WORDS.addAll(KEYWORDS.keySet());
        RESERVE_WORDS.addAll(Functions.nativeFunctions.keySet());
        RESERVE_WORDS.add("true");
        RESERVE_WORDS.add("false");

        // Colors
        for (Colors color : Colors.values()) {
            RESERVE_WORDS.add(color.name());
        }

        // Size
        for (Size size : Size.values()) {
            RESERVE_WORDS.add(size.name());
        }

        // Shape
        for (Shape shape : Shape.values()) {
            RESERVE_WORDS.add(shape.name());
        }
    }

    public static boolean isKeyWord(String token){
        return KEYWORDS.get(token) != null;
    }

    public static boolean isReservedWord(String token){
        return RESERVE_WORDS.contains(token);
    }

    public static Tokens getKeyWord(String token){
        return KEYWORDS.get(token);
    }
}