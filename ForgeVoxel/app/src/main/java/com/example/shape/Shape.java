package com.example.shape;

import com.example.TextEditor.Interpreter.interpreter.RuntimeError;

public enum Shape {
    CUBE(1),
    SPHERE(0.5),
    CYLINDER(0.5);

    double dimensions;

    private Shape(double dimensions){
        this.dimensions = dimensions;
    }

    public double getDimension(){
        return dimensions;
    }

    public static Shape getShape(int index) throws RuntimeError{
        if(index < 0 || index > 2){ 
            throw new RuntimeError("Interpreter Error: Unkown Shape Index: " + index);
        }

        return Shape.values()[index];
    }
}
