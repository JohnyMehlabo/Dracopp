package Compiler;

import Compiler.Types.Type;

import java.util.List;

public class Function {
    public String name;
    public Type returnType;
    public List<Arg> args;

    public Function(String name, Type returnType, List<Arg> args) {
        this.name = name;
        this.returnType = returnType;
        this.args = args;
    }

    public static class Arg {
        public String name;
        public Type type;

        public Arg(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }
}
