package Compiler;

import Compiler.Types.Type;

public class Function {
    public String name;
    public Type returnType;

    Function(String name, Type returnType) {
        this.name = name;
        this.returnType = returnType;
    }
}
