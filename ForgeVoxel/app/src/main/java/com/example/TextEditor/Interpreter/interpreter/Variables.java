package com.example.TextEditor.Interpreter.interpreter;

import java.util.HashMap;
import java.util.Map;

import com.example.TextEditor.Interpreter.values.Value;
import com.example.TextEditor.Interpreter.values.ValueType;

public class Variables {
    private Map<String, Value> variables;
    private Variables parent;

    public Variables(Variables parent){
        this.parent = parent;
        variables = new HashMap<>();
    }

    public boolean declareVariable(String variable, Value value){
        if(variables.containsKey(variable)){
            throw new RuntimeError("Interpreter Error: Cannot Redeclare variable: " + variable);
        }

        variables.put(variable, value);
        return true;
    }

    public boolean isVaraibleIntialized(String variable){
        if(!variables.containsKey(variable)){
            return false;
        }

        return variables.get(variable).getValueType() != ValueType.NULL;
    }

    public boolean isVaraibleDeclared(String variable){
        return variables.containsKey(variable);
    } 

    public boolean assignValue(String variable, Value value){
        Variables scope = resolve(variable);
        return scope.variables.replace(variable, value) == null ? false : true;
    }

    public Value getValue(String variable){
        Variables scope = resolve(variable);
        return scope.variables.get(variable);
    }

    public Map<String, Value> getVaraibles(){
        return variables;
    }

    public Variables resolve(String variable) throws RuntimeError {
        if(isVaraibleDeclared(variable)){
            return this;
        }
        if(parent == null){
            throw new RuntimeError("Interpreter Error: Undeclared variable: " + variable);
        }

        return parent.resolve(variable);
    }

    public Variables getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Variables [variables=" + variables + ", parent=" + "parent" + "]";
    }
}