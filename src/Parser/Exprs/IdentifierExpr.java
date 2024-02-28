package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Compiler;
import Compiler.Scope.Variable;

public class IdentifierExpr implements Expr {
    final String symbol;

    public IdentifierExpr(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void log() {
        System.out.printf("Identifier Expression: %s\n", symbol);
    }

    @Override
    public void codegen() {
        // TODO: Implement this
        Variable var = Compiler.scope.resolveVar(symbol);

        Assembler.mov(Register.x32.EAX, new RegisterMemory32(null, Register.x32.EBP, (byte) -var.stackPos));
    }
}
