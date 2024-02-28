package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;

public class IntegerLiteralExpr implements Expr {
    final int value;

    public IntegerLiteralExpr(int value) {
        this.value = value;
    }

    @Override
    public void log() {
        System.out.printf("Integer Literal Expression:\n\tValue: %d\n", value);
    }

    @Override
    public void codegen() {
        Assembler.mov(Register.x32.EAX, value);
    }
}
