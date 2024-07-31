package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Types.*;

public class ArraySubscriptExpr implements Expr {
    Expr data;
    Expr index;

    @Override
    public void log() {

    }

    public ArraySubscriptExpr(Expr data, Expr index) {
        this.data = data;
        this.index = index;
    }

    @Override
    public Type address() {
        Type type = data.address();

        if (!(type instanceof ArrayType)) {
            System.err.println("Can't subscript non-array type");
            System.exit(-1);
        }

        Assembler.push(Register.x32.EAX);
        Type indexType = index.codegen();
        if (!(indexType instanceof BasicType)) {
            System.err.println("Index in array-subscript must be a basic type");
            System.exit(-1);
        } else if (((BasicType) indexType).isFloat()) {
            System.err.println("Index in array-subscript can't be a floating point number ");
            System.exit(-1);
        }
        if (((ArrayType) type).type instanceof StructType || ((ArrayType) type).type instanceof ClassType || ((ArrayType) type).type instanceof ArrayType) {
            Assembler.mov(Register.x32.EBX, ((ArrayType) type).type.getSize());
            Assembler.mul(Register.x32.EBX);
        }
        else {
            Assembler.shl(new RegisterMemory32(Register.x32.EAX), (byte)(Math.log(((ArrayType) type).type.getSize()) / Math.log(2)));
        }


        Assembler.add(new RegisterMemory32(Register.x32.ECX), Register.x32.EAX);
        Assembler.pop(Register.x32.EAX);

        return ((ArrayType) type).type;
    }

    @Override
    public Type codegen() {
        Type type = address();
        Assembler.mov(Register.x32.EAX.ordinal(), type.getSize(), new RegisterMemory(null, Register.x32.ECX), type.getSize());

        return type;
    }
}
