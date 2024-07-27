package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Assembler.RegisterMemory8;
import Compiler.Types.*;

import java.util.HashMap;
import java.util.Map;

public class BinaryOperationExpr implements Expr {

    public enum Operator {
        Sum, Subtraction, Multiplication, Division, Greater, GreaterEqual, Less, LessEqual, Modulus;

        static final Map<String, Operator> STRING_OPERATOR_MAP;
        static {
            STRING_OPERATOR_MAP = new HashMap<>();
            STRING_OPERATOR_MAP.put("+", Sum);
            STRING_OPERATOR_MAP.put("-", Subtraction);
            STRING_OPERATOR_MAP.put("*", Multiplication);
            STRING_OPERATOR_MAP.put("/", Division);
            STRING_OPERATOR_MAP.put("%", Modulus);
            STRING_OPERATOR_MAP.put(">", Greater);
            STRING_OPERATOR_MAP.put(">=", GreaterEqual);
            STRING_OPERATOR_MAP.put("<", Less);
            STRING_OPERATOR_MAP.put("<=", LessEqual);
        }

        public static Operator fromString(String s) {
            return STRING_OPERATOR_MAP.get(s);
        }
    }

    final Expr lhs;
    final Expr rhs;
    final Operator operator;

    public BinaryOperationExpr(Expr lhs, Expr rhs, String operator) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = Operator.fromString(operator);
    }

    @Override
    public void log() {
        System.out.println("Binary Operation Expression:\nLHS:");
        lhs.log();
        System.out.println("RHS:");
        rhs.log();
        System.out.printf("Operator: %s\n", operator.toString());
    }

    @Override
    public Type codegen() {
        boolean isPointerArithmetic = false;
        PointerType pointerType = null;

        Type rhsType = rhs.codegen();
        if (rhsType instanceof PointerType) {
            isPointerArithmetic = true;
            pointerType = (PointerType) rhsType;
        }
        else
            Type.cast(rhsType, BasicType.Int);
        Assembler.push(Register.x32.EAX);

        Type lhsType = lhs.codegen();
        if (lhsType instanceof PointerType && !isPointerArithmetic){
            isPointerArithmetic = true;
            pointerType = (PointerType) lhsType;

        }
        else if (lhsType instanceof PointerType) {
            System.err.println("Can't add perform arithmetic operation on two pointers");
            System.exit(-1);
        }
        else
            Type.cast(lhsType, BasicType.Int);

        Assembler.pop(Register.x32.EBX);

        if (isPointerArithmetic) {
            if (pointerType.to instanceof StructType || pointerType.to instanceof ArrayType) {
                System.err.println("Can't perform pointer arithmetic on struct or array pointer");
                System.exit(-1);
            }
            switch (operator) {
                case Sum:
                    if (rhsType instanceof PointerType)
                        Assembler.shl(new RegisterMemory32(Register.x32.EAX), (byte)(Math.log(((PointerType) rhsType).to.getSize()) / Math.log(2)));
                    if (lhsType instanceof PointerType)
                        Assembler.shl(new RegisterMemory32(Register.x32.EBX), (byte)(Math.log(((PointerType) lhsType).to.getSize()) / Math.log(2)));
                    Assembler.add(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    break;
                case Subtraction:
                    if (rhsType instanceof PointerType)
                        Assembler.shl(new RegisterMemory32(Register.x32.EAX), (byte)(Math.log(((PointerType) rhsType).to.getSize()) / Math.log(2)));
                    if (lhsType instanceof PointerType)
                        Assembler.shl(new RegisterMemory32(Register.x32.EBX), (byte)(Math.log(((PointerType) lhsType).to.getSize()) / Math.log(2)));
                    Assembler.sub(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    break;
                default:
                    System.err.println("Operator not compatible with pointer arithmetic");
            }
            return pointerType;
        }
        else {
            switch (operator) {
                case Sum:
                    Assembler.add(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    break;
                case Subtraction:
                    Assembler.sub(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    break;
                case Multiplication:
                    Assembler.mul(Register.x32.EBX);
                    break;
                case Division:
                    Assembler.push(Register.x32.EDX);
                    Assembler.mov(Register.x32.EDX, 0);
                    Assembler.div(Register.x32.EBX);
                    Assembler.pop(Register.x32.EDX);
                    break;
                case Modulus:
                    Assembler.push(Register.x32.EDX);
                    Assembler.mov(Register.x32.EDX, 0);
                    Assembler.div(Register.x32.EBX);
                    Assembler.mov(Register.x32.EAX, new RegisterMemory32(Register.x32.EDX));
                    Assembler.pop(Register.x32.EDX);
                    break;
                case Greater:
                    Assembler.cmp(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    Assembler.setg(new RegisterMemory8(Register.x8.AL));
                    return BasicType.Bool;
                case GreaterEqual:
                    Assembler.cmp(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    Assembler.setge(new RegisterMemory8(Register.x8.AL));
                    return BasicType.Bool;
                case Less:
                    Assembler.cmp(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    Assembler.setl(new RegisterMemory8(Register.x8.AL));
                    return BasicType.Bool;
                case LessEqual:
                    Assembler.cmp(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    Assembler.setle(new RegisterMemory8(Register.x8.AL));
                    return BasicType.Bool;
            }
            return BasicType.Int;
        }
    }
}
