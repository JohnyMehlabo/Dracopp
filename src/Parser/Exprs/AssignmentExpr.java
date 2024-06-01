package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Types.Type;

public class AssignmentExpr implements Expr{

    final Expr dst;
    final Expr src;

    @Override
    public void log() {
        System.out.println("Assignment Expression:\nDestination:");
        dst.log();
        System.out.println("Source:");
        src.log();
    }

    public AssignmentExpr(Expr dst, Expr src) {
        this.dst = dst;
        this.src = src;
    }

    @Override
    public Type codegen() {
        Type srcType = src.codegen();
        Type dstType = dst.address();
        Type.cast(dstType, srcType);

        Assembler.mov(new RegisterMemory(null, Register.x32.ECX), dstType.getSize(), Register.x32.EAX.ordinal(), dstType.getSize());
        return dstType;
    }
}
