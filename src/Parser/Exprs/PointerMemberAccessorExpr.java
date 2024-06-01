package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Types.PointerType;
import Compiler.Types.Struct;
import Compiler.Types.Struct.Member;
import Compiler.Types.StructType;
import Compiler.Types.Type;

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
        Type type = data.codegen();
        if (!(type instanceof PointerType)) {
            System.err.println("Can't pointer-access member of non-pointer type");
            System.exit(-1);
        }
        if (!(((PointerType) type).to instanceof StructType))  {
            System.err.println("Can't pointer-access member of non-struct pointer type");
            System.exit(-1);
        }

        Struct struct = ((StructType) ((PointerType) type).to).struct;
        Member member = struct.getMember(memberName);

        Assembler.mov(Register.x32.ECX, new RegisterMemory32(Register.x32.EAX));
        Assembler.add(new RegisterMemory32(Register.x32.ECX), member.offset);
        return member.type;
    }

    @Override
    public Type codegen() {
        Type memberType = address();
        Assembler.mov(Register.x32.EAX.ordinal(), memberType.getSize(), new RegisterMemory(null, Register.x32.ECX), memberType.getSize());

        return memberType;
    }
}
