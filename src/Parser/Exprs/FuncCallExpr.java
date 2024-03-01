package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Compiler;
import Compiler.Function;
import Compiler.Types.Type;

public class FuncCallExpr implements Expr{
    Expr caller;

    @Override
    public void log() {
        System.out.println("Function Call Expression:");
        System.out.println("\tCaller:");
        caller.log();
    }

    @Override
    public Type codegen() {
        if (caller.getClass().equals(IdentifierExpr.class)) {
            Function function = Compiler.resolveFunction(((IdentifierExpr) caller).symbol);
            if (function != null) {
                Assembler.call(((IdentifierExpr) caller).symbol);
                return function.returnType;
            }
            else {
                System.err.printf("Attempting to call undefined function '%s'\n", ((IdentifierExpr) caller).symbol);
                System.exit(-1);
            }
        } else {
            System.err.println("Caller must be an identifier expression");
            System.exit(-1);
        }

        return null;
    }

    public FuncCallExpr(Expr caller) {
        this.caller = caller;
    }
}
