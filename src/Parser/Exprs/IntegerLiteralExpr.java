package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Interpreter.Integer32Value;
import Interpreter.RuntimeValue;

public class IntegerLiteralExpr implements Expr {
    public int value;

    public IntegerLiteralExpr(int value) {
        this.value = value;
    }

    @Override
    public void log() {
        System.out.printf("Integer Literal Expr:\n\tValue: %d\n", value);
    }

    @Override
    public void codegen() {
        Assembler.mov(Register.x32.EAX, value);
    }

    @Override
    public RuntimeValue value() {
        return new Integer32Value(value);
    }
}
