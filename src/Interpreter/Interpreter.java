package Interpreter;

import Parser.Exprs.Expr;
import Parser.Stmts.Stmt;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {

    private static final Map<String, RuntimeValue> variables = new HashMap<>();

    public static void declareVar(String name, String type, Expr initialValue) {
        if (variables.get(name) == null) {
            variables.put(name, initialValue.value());
        } else {
            System.err.printf("Redefinition of variable %s\n", name);
        }
    }

    public static RuntimeValue resolveVar(String name) {
        return variables.get(name);
    }

    public static void run(Stmt program) {
        program.run();
    }
}
