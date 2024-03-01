package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Compiler;
import Compiler.Scope.Variable;
import Compiler.Types.Type;

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
    public Type codegen() {
        Variable var = Compiler.scope.resolveVar(symbol);

        int size = Type.getSizeOf(var.type);
        Register reg = Register.fromSize(Register.x32.EAX.ordinal(), size);
        Assembler.mov(reg, size, new RegisterMemory(null, Register.x32.EBP, (byte) -var.stackPos), size);

        return var.type;
    }
}
