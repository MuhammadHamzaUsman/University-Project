package com.example.TextEditor.Interpreter.values;

public interface FunctionCall {
    public Value execute(Value[] input);
    public int requiredArgs();
}
