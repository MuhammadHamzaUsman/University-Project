package com.example.TextEditor.Interpreter.tokens;

import java.util.Arrays;
import java.util.List;

import com.example.TextEditor.Interpreter.interpreter.RuntimeError;

public class TokenStream {
    private List<Token> tokens;
    private int index;

    public TokenStream(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token peek(){
        try {
            return tokens.get(index);
        } catch (Exception e) {
            throw new RuntimeError(Arrays.toString(e.getStackTrace()));
        }
    }

    public Token next(){
        try {
            return tokens.get(index++);
        } catch (Exception e) {
            throw new RuntimeError(Arrays.toString(e.getStackTrace()));
        }
    }

    public Token expectType(Tokens token, String errorMessage) throws RuntimeError {
        Token type = this.next();

        if(type == null || type.getTokenType() != token){
            throw new RuntimeError(errorMessage + " Expecting" + token + " got " + type.getTokenType());
        }
        return type;
    }

    public boolean isEnd(){
        return index >= tokens.size();
    }
}
