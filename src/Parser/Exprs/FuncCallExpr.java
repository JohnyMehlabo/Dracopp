package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Compiler;

public class FuncCallExpr implements Expr{
    Expr caller;

    @Override
    public void log() {
        System.out.println("Function Call Expression:");
        System.out.println("\tCaller:");
        caller.log();
    }

    @Override
    public void codegen() {
        if (caller.getClass().equals(IdentifierExpr.class)) {
            if (Compiler.resolveFunction(((IdentifierExpr) caller).symbol) != null)
                Assembler.call(((IdentifierExpr) caller).symbol);
            else {
                System.err.printf("Attempting to call undefined function '%s'\n", ((IdentifierExpr) caller).symbol);
                System.exit(-1);
            }
        } else {
            System.err.println("Caller must be an identifier expression");
            System.exit(-1);
        }
    }

    public FuncCallExpr(Expr caller) {
        this.caller = caller;
    }
}
