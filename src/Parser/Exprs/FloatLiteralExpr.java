package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Types.BasicType;
import Compiler.Types.Type;

public class FloatLiteralExpr implements Expr {
    final float value;

    public FloatLiteralExpr(float value) {
        this.value = value;
    }

    @Override
    public void log() {
        System.out.printf("Float Literal Expression:\n\tValue: %f\n", value);
    }

    @Override
    public Type codegen() {
        Assembler.mov(Register.x32.EAX, Float.floatToIntBits(value));
        return BasicType.Float;
    }
}
