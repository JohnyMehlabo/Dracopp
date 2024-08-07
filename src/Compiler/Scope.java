package Compiler;

import Compiler.Types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scope {
    public static class Variable {
        public String name;
        public int stackPos;
        public Type type;
        public boolean isGlobal;

        Variable(String name, Type type, int stackPos, boolean isGlobal) {
            this.name = name;
            this.type = type;
            this.stackPos = stackPos;
            this.isGlobal = isGlobal;
        }
    }

    public Scope (Scope parentScope) {
        this.parentScope = parentScope;
    }

    Scope parentScope;
    List<Variable> variables = new ArrayList<>();

    public void declareVar(String name, Type type, int stackPos) {
        if (variables.stream().anyMatch(v -> v.name.equals(name))) {
            System.err.printf("Redefinition of variable '%s'\n", name);
            System.exit(-1);
        }
        variables.add(new Variable(name, type, stackPos, false));
    }

    public void declareVar(String name, Type type) {
        if (variables.stream().anyMatch(v -> v.name.equals(name))) {
            System.err.printf("Redefinition of variable '%s'\n", name);
            System.exit(-1);
        }
        variables.add(new Variable(name, type, 0, true));
    }

    public Variable resolveVar(String name) {
        Optional<Variable> variable = variables.stream().filter(v -> v.name.equals(name)).findFirst();

        if (variable.isPresent()) return variable.get();
        if (parentScope == null) {
            System.err.printf("Couldn't resolve variable '%s'\n", name);
            System.exit(-1);
        }
        return parentScope.resolveVar(name);
    }

    public boolean isGlobalScope() {
        return this.parentScope == null;
    }
}
