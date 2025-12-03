package com.example.TextEditor.Interpreter.interpreter;

public class RuntimeError extends RuntimeException{
    public RuntimeError(String message){
        super(message);
    }
}
