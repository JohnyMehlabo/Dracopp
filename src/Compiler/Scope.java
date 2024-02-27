package Compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scope {
    public static class Variable {
        public String name;
        public int stackPos;

        Variable(String name, int stackPos) {
            this.name = name;
            this.stackPos = stackPos;
        }
    }

    public Scope (Scope parentScope) {
        this.parentScope = parentScope;
    }

    Scope parentScope;
    List<Variable> variables = new ArrayList<>();

    public void declareVar(String name, int stackPos) {
        if (variables.stream().anyMatch(v -> v.name.equals(name))) {
            System.err.printf("Redefinition of variable '%s'\n", name);
            System.exit(-1);
        }
        variables.add(new Variable(name, stackPos));
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
}
