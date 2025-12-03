package com.example.TextEditor.Interpreter.values;

import java.util.Map;

import com.example.TextEditor.Interpreter.interpreter.RuntimeError;
import com.example.TextEditor.Interpreter.interpreter.Variables;
import com.example.TextEditor.Interpreter.statements.FunctionDecalarationStatement;
import com.example.TextEditor.Interpreter.tokens.Token;
import com.example.TextEditor.Interpreter.tokens.Tokens;
import com.example.shape.Colors;
import com.example.shape.Shape;
import com.example.shape.Size;
import com.example.shape.Voxel.PropertyInfoHolder;

public class Value {
    private ValueType valueType;
    private Double doubleValue;
    private Boolean boolValue;
    private FunctionCall function;
    private UserDefinedFunction userFunction;
    private Map<String, Value> map;

    private Value(){}

    public static Value of(double value){
        Value value_ = new Value();
        value_.valueType = ValueType.DOUBLE;
        value_.doubleValue = value;
        return value_;
    }

    public static Value of(boolean value){
        Value value_ = new Value();
        value_.valueType = ValueType.BOOLEAN;
        value_.boolValue = value;
        return value_;
    }

    public static Value ofNull(){
        Value value_ = new Value();
        value_.valueType = ValueType.NULL;
        return value_;
    }

    public static Value ofFunction(FunctionCall functionCall){
        Value value_ = new Value();
        value_.valueType = ValueType.FUNCTION;
        value_.function = functionCall;
        return value_;
    }

    public static Value ofUserFunction(FunctionDecalarationStatement funDecstmt, Variables variables){
        Value value_ = new Value();
        UserDefinedFunction userFunction = new UserDefinedFunction(
            funDecstmt.getName(), funDecstmt.getParameters(), variables, funDecstmt.getBody());
        value_.valueType = ValueType.USER_FUNCTION;
        value_.userFunction = userFunction;
        return value_;
    }

    public static Value ofMap(Map<String, Value> values){
        Value value_ = new Value();
        value_.valueType = ValueType.MAP;
        value_.map = values;
        return value_;
    }

    public Map<String, Value> getMap() {
        return map;
    }

    public void setMap(Map<String, Value> map) {
        this.map = map;
    }

    public UserDefinedFunction getUserFunction() {
        return userFunction;
    }

    public FunctionCall getFunction(){
        return function;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    public Token toToken(){
        return new Token(
            valueType == ValueType.DOUBLE ? Tokens.NUMBER : Tokens.BOOLEAN,
            valueType == ValueType.DOUBLE ? doubleValue.toString() : boolValue.toString()
        );
    }

    public String getString(){
        switch (valueType) {
            case BOOLEAN: return boolValue.toString();
            case DOUBLE: return doubleValue.toString();
            case NULL: return "null";
            case MAP: return map.toString();
            case USER_FUNCTION: return userFunction.getName(); 
            default: return "Native Function";
        }
    }

    public PropertyInfoHolder getVoxelProperties(){
        if(valueType == ValueType.MAP){
            try {
                int shapeIndex = map.get("Shape").getDoubleValue().intValue();
                int sizeIndex = map.get("Size").getDoubleValue().intValue();
                int colorIndex = map.get("Color").getDoubleValue().intValue();

                return new PropertyInfoHolder(Colors.getColor(colorIndex), Size.getSize(sizeIndex), Shape.getShape(shapeIndex));
            } catch (Exception e) {
                throw new RuntimeError("Interpreter Error: Inavlid Index or DataType for Voxel Properties");
            }
        }else if(valueType == ValueType.NULL){
            return null;
        }

        throw new RuntimeError("Interpreter Error: Inavlid Index or DataType for Voxel Properties Shape:");
    }

    @Override
    public String toString() {
        return "Value [\n\tvalueType=" + valueType + ", doubleValue=" + doubleValue + ", boolValue=" + boolValue +
            "\n\tFunction: " + function + 
            "\n\tUserFunction: " + (userFunction == null ? "null" : userFunction.toString().replace("\n", "\n\t")) + 
            "\n\tMap: " + (map == null ? "null" : map.toString().replace("\n", "\n\t")) + "\n]";
    }
}
