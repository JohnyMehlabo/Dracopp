package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
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

        int size = var.type.getSize();
        Assembler.mov(Register.x32.EAX.ordinal(), size, new RegisterMemory(null, Register.x32.EBP, (byte) -var.stackPos), size);

        return var.type;
    }

    @Override
    public Type address() {
        Variable var = Compiler.scope.resolveVar(symbol);
        Assembler.mov(Register.x32.ECX, new RegisterMemory32(Register.x32.EBP));
        Assembler.sub(new RegisterMemory32(Register.x32.ECX), (byte) var.stackPos);
        return var.type;
    }
}
