package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Compiler;
import Compiler.Scope.Variable;
import Compiler.Types.ArrayType;
import Compiler.Types.PointerType;
import Compiler.Types.ReferenceType;
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

        if (!(var.type instanceof ArrayType)) {
            int size = var.type.getSize();
            Assembler.mov(Register.x32.EAX.ordinal(), size, new RegisterMemory(null, Register.x32.EBP, (byte) -var.stackPos), size);

            return var.type;
        }
        else {
            Assembler.mov(Register.x32.EAX, new RegisterMemory32(Register.x32.EBP));
            Assembler.sub(new RegisterMemory32(Register.x32.EAX), var.stackPos);

            return new PointerType(((ArrayType) var.type).type);
        }
    }

    @Override
    public Type address() {
        Variable var = Compiler.scope.resolveVar(symbol);
        Assembler.mov(Register.x32.ECX, new RegisterMemory32(Register.x32.EBP));
        Assembler.sub(new RegisterMemory32(Register.x32.ECX), var.stackPos);
        if (var.type instanceof ReferenceType) {
            Assembler.mov(Register.x32.ECX, new RegisterMemory32(null, Register.x32.ECX));
            return ((ReferenceType) var.type).to;
        }
        return var.type;
    }
}
