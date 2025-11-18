package TextEditor.Interpreter.interpreter;

import java.util.HashMap;
import java.util.Map;

import TextEditor.Interpreter.values.Value;
import TextEditor.Interpreter.values.ValueType;

public class Variables {
    private Map<String, Value> variables;
    private Variables parent;

    public Variables(Variables parent){
        this.parent = parent;
        variables = new HashMap<>();
    }

    public boolean declareVariable(String variable, Value value){
        if(variables.containsKey(variable)){
            System.err.print("Cannot Redeclare variable: " + variable);
            System.exit(1);
            return false;
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

    public Variables resolve(String variable){
        if(isVaraibleDeclared(variable)){
            return this;
        }
        if(parent == null){
            System.err.println("Interpreter Error: Undeclared variable: " + variable);
            System.exit(1);
        }

        return parent.resolve(variable);
    }

    public Variables getParent() {
        return parent;
    }
}