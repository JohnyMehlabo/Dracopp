package Parser.Exprs;

import Interpreter.Interpreter;
import Interpreter.RuntimeValue;

public class IdentifierExpr implements Expr {
    String symbol;

    public IdentifierExpr(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void log() {
        System.out.printf("Identifier Expr: %s\n", symbol);
    }

    @Override
    public void codegen() {
        // TODO: Implement this
    }

    @Override
    public RuntimeValue value() {
        return Interpreter.resolveVar(symbol);
    }
}
