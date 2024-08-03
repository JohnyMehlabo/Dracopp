package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Types.Class;
import Compiler.Types.ClassType;
import Compiler.Types.Struct;
import Compiler.Types.StructType;
import Compiler.Types.Type;

public class MemberAccessorExpr implements Expr {
    Expr data;
    String memberName;

    @Override
    public void log() {

    }

    public MemberAccessorExpr(Expr data, String memberName) {
        this.data = data;
        this.memberName = memberName;
    }

    @Override
    public Type address() {
        Type type = data.address();
        if (type instanceof StructType) {
            Struct struct = ((StructType) type).struct;
            Struct.Member member = struct.getMember(memberName);

            Assembler.lea(Register.x32.ECX, new RegisterMemory32(null, Register.x32.ECX, member.offset));
            return member.type;
        }
        else if (type instanceof ClassType) {
            Class aClass = ((ClassType) type).aClass;
            Class.Member member = aClass.getMember(memberName);

            Assembler.lea(Register.x32.ECX, new RegisterMemory32(null, Register.x32.ECX, member.offset));
            return member.type;
        }
        else {
            System.err.println("Can't access member of non-struct or non-class type");
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
