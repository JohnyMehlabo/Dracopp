package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Compiler;
import Compiler.Function;
import Compiler.Types.Type;

import java.util.ArrayList;
import java.util.List;

public class FuncCallExpr implements Expr{
    Expr caller;
    List<Expr> args;

    static final List<Register.x32> ARG_REGISTER_LIST = new ArrayList<>(List.of(Register.x32.EAX, Register.x32.EBX, Register.x32.ECX, Register.x32.EDX, Register.x32.ESI, Register.x32.EDI));
    static final List<Register.x32> PROTECTED_REGISTER_LIST = new ArrayList<>(List.of(Register.x32.EAX, Register.x32.EBX, Register.x32.ECX));

    @Override
    public void log() {
        System.out.println("Function Call Expression:");
        System.out.println("\tCaller:");
        caller.log();
    }

    @Override
    public Type codegen() {
        if (caller.getClass().equals(IdentifierExpr.class)) {
            Function function = Compiler.resolveFunction(((IdentifierExpr) caller).symbol);
            if (function != null) {
                if (args.size() != function.args.size()) {
                    System.err.println("Argument number mismatch in function call");
                    System.exit(-1);
                }

                int protectedRegistersCount = 0;

                for (int i = 0; i < function.args.size(); i++) {
                    Register.x32 register = ARG_REGISTER_LIST.get(i);
                    Type argType = args.get(i).codegen();
                    Type.cast(argType, function.args.get(i).type);
                    Type.castToSize(function.args.get(i).type, 4);
                    Assembler.mov(register, new RegisterMemory32(Register.x32.EAX));
                    if (PROTECTED_REGISTER_LIST.contains(register)) {
                        Assembler.push(register);
                        protectedRegistersCount++;
                    }
                }

                for (int i = protectedRegistersCount - 1; i >= 0 ; i--) {
                    Assembler.pop(PROTECTED_REGISTER_LIST.get(i));
                }

                Assembler.call(((IdentifierExpr) caller).symbol);
                return function.returnType;
            }
            else {
                System.err.printf("Attempting to call undefined function '%s'\n", ((IdentifierExpr) caller).symbol);
                System.exit(-1);
            }
        } else {
            System.err.println("Caller must be an identifier expression");
            System.exit(-1);
        }

        return null;
    }

    public FuncCallExpr(Expr caller, List<Expr> args) {
        this.caller = caller;
        this.args = args;
    }
}
