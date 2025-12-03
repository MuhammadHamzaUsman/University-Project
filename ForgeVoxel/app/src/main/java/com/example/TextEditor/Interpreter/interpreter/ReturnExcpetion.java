package com.example.TextEditor.Interpreter.interpreter;

import com.example.TextEditor.Interpreter.values.Value;

public class ReturnExcpetion extends RuntimeException{
    Value returnValue;

    public ReturnExcpetion(Value returnValue) {
        this.returnValue = returnValue;
    }

    public Value getReturnValue() {
        return returnValue;
    }
}
