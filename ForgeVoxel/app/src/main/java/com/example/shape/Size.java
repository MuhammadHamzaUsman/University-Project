package com.example.shape;

import com.example.TextEditor.Interpreter.interpreter.RuntimeError;

public enum Size {
    LARGE(1.0),
    MEDIUM(0.625),
    SMALL(0.25);

    private double factor;

    private Size(double factor) {
        this.factor = factor;
    }

    public double getFactor() {
        return factor;
    }

    public static Size getSize(int index) throws RuntimeError{
        if(index < 0 || index > 2){ 
            throw new RuntimeError("Interpreter Error: Unkown Size Index: " + index);
        }

        return Size.values()[index];
    }
}
