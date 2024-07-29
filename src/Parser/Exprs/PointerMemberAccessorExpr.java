package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Types.*;
import Compiler.Types.Class;

public class PointerMemberAccessorExpr implements Expr {
    Expr data;
    String memberName;

    @Override
    public void log() {

    }

    public PointerMemberAccessorExpr(Expr data, String memberName) {
        this.data = data;
        this.memberName = memberName;
    }

    @Override
    public Type address() {
        Assembler.push(Register.x32.EAX);
        Type type = data.codegen();
        if (!(type instanceof PointerType)) {
            System.err.println("Can't pointer-access member of non-pointer type");
            System.exit(-1);
        }
        Type pointedType = ((PointerType) type).to;
        if (pointedType instanceof StructType)  {
            Struct struct = ((StructType) ((PointerType) type).to).struct;
            Struct.Member member = struct.getMember(memberName);

            Assembler.mov(Register.x32.ECX, new RegisterMemory32(Register.x32.EAX));
            Assembler.pop(Register.x32.EAX);
            Assembler.add(new RegisterMemory32(Register.x32.ECX), member.offset);
            return member.type;

        }
        if (pointedType instanceof ClassType)  {
            Class aClass = ((ClassType) ((PointerType) type).to).aClass;
            Class.Member member = aClass.getMember(memberName);

            Assembler.mov(Register.x32.ECX, new RegisterMemory32(Register.x32.EAX));
            Assembler.pop(Register.x32.EAX);
            Assembler.add(new RegisterMemory32(Register.x32.ECX), member.offset);
            return member.type;

        }
        else  {
            System.err.println("Can't pointer-access member of non-struct pointer type");
            System.exit(-1);
        }
        return null;
    }

    @Override
    public Type codegen() {
        Type memberType = address();
        Assembler.mov(Register.x32.EAX.ordinal(), memberType.getSize(), new RegisterMemory(null, Register.x32.ECX), memberType.getSize());

        return memberType;
    }
}
