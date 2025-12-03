package com.example.TextEditor.Interpreter.tokens;

import java.util.Map;

import com.example.TextEditor.Interpreter.interpreter.Variables;
import com.example.TextEditor.Interpreter.values.FunctionCall;
import com.example.TextEditor.Interpreter.values.Value;

public class Functions {

    public static Map<String, Value> nativeFunctions = Map.ofEntries(
        Map.entry("abs", Value.ofFunction( new FunctionCall(){
            @Override public String toString() {return "abs";}
            @Override public Value execute(Value[] args) { return Value.of( Math.abs(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;}
        }) ),
        Map.entry("acos", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "acos";}
            @Override public Value execute(Value[] args) { return Value.of( Math.acos(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("asin", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "asin";}
            @Override public Value execute(Value[] args) { return Value.of( Math.asin(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("atan", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "atan";}
            @Override public Value execute(Value[] args) { return Value.of( Math.atan(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("atan2", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "atan2";}
            @Override public Value execute(Value[] args) { return Value.of( Math.atan2(args[0].getDoubleValue(), args[1].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("cbrt", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "cbrt";}
            @Override public Value execute(Value[] args) { return Value.of( Math.cbrt(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("ceil", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "ceil";}
            @Override public Value execute(Value[] args) { return Value.of( Math.ceil(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("copySign", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "copySign";}
            @Override public Value execute(Value[] args) { return Value.of( Math.copySign(args[0].getDoubleValue(), args[1].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("cos", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "cos";}
            @Override public Value execute(Value[] args) { return Value.of( Math.cos(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("cosh", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "cosh";}
            @Override public Value execute(Value[] args) { return Value.of( Math.cosh(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("exp", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "exp";}
            @Override public Value execute(Value[] args) { return Value.of( Math.exp(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("expml", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "expml";}
            @Override public Value execute(Value[] args) { return Value.of( Math.expm1(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("floor", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "floor";}
            @Override public Value execute(Value[] args) { return Value.of( Math.floor(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("floorMod", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "floorMod";}
            @Override public Value execute(Value[] args) { return Value.of( Math.floorMod(args[0].getDoubleValue().longValue(), args[1].getDoubleValue().longValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("floorDiv", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "floorDiv";}
            @Override public Value execute(Value[] args) { return Value.of( Math.floorDiv(args[0].getDoubleValue().longValue(), args[1].getDoubleValue().longValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("getExponent", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "getExponent";}
            @Override public Value execute(Value[] args) { return Value.of( Math.getExponent(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("hypot", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "hypot";}
            @Override public Value execute(Value[] args) { return Value.of( Math.hypot(args[0].getDoubleValue(), args[1].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("IEEEremainder", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "IEEEremainder";}
            @Override public Value execute(Value[] args) { return Value.of( Math.IEEEremainder(args[0].getDoubleValue(), args[1].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("incrementExact", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "incrementExact";}
            @Override public Value execute(Value[] args) { return Value.of( Math.incrementExact(args[0].getDoubleValue().longValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("log", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "log";}
            @Override public Value execute(Value[] args) { return Value.of( Math.log(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("log10", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "log10";}
            @Override public Value execute(Value[] args) { return Value.of( Math.log10(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("log1p", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "log1p";}
            @Override public Value execute(Value[] args) { return Value.of( Math.log1p(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("max", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "max";}
            @Override public Value execute(Value[] args) { return Value.of( Math.max(args[0].getDoubleValue(), args[1].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("min", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "min";}
            @Override public Value execute(Value[] args) { return Value.of( Math.min(args[0].getDoubleValue(), args[1].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("multiplyExact", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "multiplyExact";}
            @Override public Value execute(Value[] args) { return Value.of( Math.multiplyExact(args[0].getDoubleValue().longValue(), args[1].getDoubleValue().longValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("negateExact", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "negateExact";}
            @Override public Value execute(Value[] args) { return Value.of( Math.negateExact(args[0].getDoubleValue().longValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("nextAfter", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "nextAfter";}
            @Override public Value execute(Value[] args) { return Value.of( Math.nextAfter(args[0].getDoubleValue(), args[1].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("nextDown", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "nextDown";}
            @Override public Value execute(Value[] args) { return Value.of( Math.nextDown(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;}
        }) ),
        Map.entry("nextUp", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "nextUp";}
            @Override public Value execute(Value[] args) { return Value.of( Math.nextUp(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("pow", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "pow";}
            @Override public Value execute(Value[] args) { return Value.of( Math.pow(args[0].getDoubleValue(), args[1].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("random", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "random";}
            @Override public Value execute(Value[] args) { return Value.of( Math.random() ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("rint", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "rint";}
            @Override public Value execute(Value[] args) { return Value.of( Math.rint(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("round", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "round";}
            @Override public Value execute(Value[] args) { return Value.of( Math.round(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("signum", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "signum";}
            @Override public Value execute(Value[] args) { return Value.of( Math.signum(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("scalb", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "scalb";}
            @Override public Value execute(Value[] args) { return Value.of( Math.scalb(args[0].getDoubleValue(), args[1].getDoubleValue().intValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("sin", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "sin";}
            @Override public Value execute(Value[] args) { return Value.of( Math.sin(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("sinh", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "sinh";}
            @Override public Value execute(Value[] args) { return Value.of( Math.sinh(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("sqrt", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "sqrt";}
            @Override public Value execute(Value[] args) { return Value.of( Math.sqrt(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("subtractExact", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "subtractExact";}
            @Override public Value execute(Value[] args) { return Value.of( Math.subtractExact(args[0].getDoubleValue().longValue(), args[1].getDoubleValue().longValue()) ); }
            @Override public int requiredArgs() {return 2;} 
        }) ),
        Map.entry("tan", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "tan";}
            @Override public Value execute(Value[] args) { return Value.of( Math.tan(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("tanh", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "tanh";}
            @Override public Value execute(Value[] args) { return Value.of( Math.tanh(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("toDegrees", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "toDegrees";}
            @Override public Value execute(Value[] args) { return Value.of( Math.toDegrees(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("toIntExact", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "toIntExact";}
            @Override public Value execute(Value[] args) { return Value.of( Math.toIntExact(args[0].getDoubleValue().longValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("toRadians", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "toRadians";}
            @Override public Value execute(Value[] args) { return Value.of( Math.toRadians(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("ulp", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "ulp";}
            @Override public Value execute(Value[] args) { return Value.of( Math.ulp(args[0].getDoubleValue()) ); }
            @Override public int requiredArgs() {return 1;} 
        }) ),
        Map.entry("print", Value.ofFunction( new FunctionCall(){ 
            @Override public String toString() {return "print";}
            @Override public Value execute(Value[] args) { return Value.ofNull(); }
            @Override public int requiredArgs() {return 0;} 
        }) )
    );

    public static void loadFunctions(Variables variables){
        nativeFunctions.forEach(
            (name, function) -> {variables.declareVariable(name, function);}
        );
    }
}