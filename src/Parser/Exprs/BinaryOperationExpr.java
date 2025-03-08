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
        Sum, Subtraction, Multiplication, Division, Equal, NotEqual, Greater, GreaterEqual, Less, LessEqual, Modulus;

        static final Map<String, Operator> STRING_OPERATOR_MAP;
        static {
            STRING_OPERATOR_MAP = new HashMap<>();
            STRING_OPERATOR_MAP.put("+", Sum);
            STRING_OPERATOR_MAP.put("-", Subtraction);
            STRING_OPERATOR_MAP.put("*", Multiplication);
            STRING_OPERATOR_MAP.put("/", Division);
            STRING_OPERATOR_MAP.put("%", Modulus);
            STRING_OPERATOR_MAP.put("==", Equal);
            STRING_OPERATOR_MAP.put("!=", NotEqual);
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

    private Type floatArithmetic(Type rhsType, Type lhsType) {
        // TODO: Clean the code for the 4 basic operations
        switch (operator) {
            case Sum:
                Assembler.push(Register.x32.EAX);
                if (((BasicType) lhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EAX);

                Assembler.push(Register.x32.EBX);
                if (((BasicType) rhsType).isFloat()) {
                    Assembler.fadd(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fiadd(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.fstp(new RegisterMemory32(null, Register.x32.ESP));
                Assembler.pop(Register.x32.EAX);
                break;
            case Subtraction:
                Assembler.push(Register.x32.EAX);
                if (((BasicType) lhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EAX);

                Assembler.push(Register.x32.EBX);
                if (((BasicType) rhsType).isFloat()) {
                    Assembler.fsub(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fisub(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.fstp(new RegisterMemory32(null, Register.x32.ESP));
                Assembler.pop(Register.x32.EAX);
                break;
            case Multiplication:
                Assembler.push(Register.x32.EAX);
                if (((BasicType) lhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EAX);

                Assembler.push(Register.x32.EBX);
                if (((BasicType) rhsType).isFloat()) {
                    Assembler.fmul(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fimul(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.fstp(new RegisterMemory32(null, Register.x32.ESP));
                Assembler.pop(Register.x32.EAX);
                break;
            case Division:
                Assembler.push(Register.x32.EAX);
                if (((BasicType) lhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EAX);

                Assembler.push(Register.x32.EBX);
                if (((BasicType) rhsType).isFloat()) {
                    Assembler.fdiv(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fidiv(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.fstp(new RegisterMemory32(null, Register.x32.ESP));
                Assembler.pop(Register.x32.EAX);
                break;
            case Less:
                Assembler.push(Register.x32.EBX);
                if (((BasicType) rhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EBX);

                Assembler.push(Register.x32.EAX);
                if (((BasicType) lhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EAX);
                Assembler.fcomip(0, 1);
                Assembler.fstp(0);
                Assembler.setb(new RegisterMemory8(Register.x8.AL));
                return BasicType.Bool;
            case Greater:
                Assembler.push(Register.x32.EBX);
                if (((BasicType) rhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EBX);

                Assembler.push(Register.x32.EAX);
                if (((BasicType) lhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EAX);
                Assembler.fcomip(0, 1);
                Assembler.fstp(0);
                Assembler.seta(new RegisterMemory8(Register.x8.AL));
                return BasicType.Bool;
            case LessEqual:
                Assembler.push(Register.x32.EBX);
                if (((BasicType) rhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EBX);

                Assembler.push(Register.x32.EAX);
                if (((BasicType) lhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EAX);
                Assembler.fcomip(0, 1);
                Assembler.fstp(0);
                Assembler.setbe(new RegisterMemory8(Register.x8.AL));
                return BasicType.Bool;
            case GreaterEqual:
                Assembler.push(Register.x32.EBX);
                if (((BasicType) rhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EBX);

                Assembler.push(Register.x32.EAX);
                if (((BasicType) lhsType).isFloat()) {
                    Assembler.fld(new RegisterMemory32(null, Register.x32.ESP));
                }
                else {
                    Assembler.fild(new RegisterMemory32(null, Register.x32.ESP));
                }
                Assembler.pop(Register.x32.EAX);
                Assembler.fcomip(0, 1);
                Assembler.fstp(0);
                Assembler.setae(new RegisterMemory8(Register.x8.AL));
                return BasicType.Bool;
            default:
                System.err.println("Not implemented");
                System.exit(-1);
        }
        return BasicType.Float;
    }

    @Override
    public Type codegen() {
        boolean isPointerArithmetic = false;
        boolean isFloatArithmetic = false;
        PointerType pointerType = null;

        Type rhsType = rhs.codegen();
        if (rhsType instanceof PointerType) {
            isPointerArithmetic = true;
            pointerType = (PointerType) rhsType;
        }
        else if (rhsType instanceof BasicType || rhsType instanceof ReferenceType) {
            if (rhsType instanceof ReferenceType) {
                rhsType = ((ReferenceType) rhsType).to;
            }
            else if (!((BasicType) rhsType).isFloat())
                Type.cast(rhsType, BasicType.Int);
            else
                isFloatArithmetic = true;
        }
        else {
            System.err.println("Invalid type for arithmetic");
            System.exit(-1);
        }

        Assembler.push(Register.x32.EAX);

        Type lhsType = lhs.codegen();
        if (lhsType instanceof PointerType && !isPointerArithmetic){
            isPointerArithmetic = true;
            pointerType = (PointerType) lhsType;
            if (((BasicType) rhsType).isFloat()) {
                System.err.println("Can't perform arithmetic between a float and a pointer");
            }
        }
        else if (lhsType instanceof PointerType) {
            System.err.println("Can't perform arithmetic operation on two pointers");
            System.exit(-1);
        }
        else if (lhsType instanceof BasicType || lhsType instanceof ReferenceType) {
            if (lhsType instanceof ReferenceType) {
                lhsType = ((ReferenceType) lhsType).to;
            }
            else if (!((BasicType) lhsType).isFloat())
                Type.cast(lhsType, BasicType.Int);
            else
                // TODO: Remember to implement and use the casting to float
                isFloatArithmetic = true;
        }
        else {
            System.err.println("Invalid type for arithmetic");
            System.exit(-1);
        }

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
        else if (!isFloatArithmetic) {
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
                case Equal:
                    Assembler.cmp(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    Assembler.sete(new RegisterMemory8(Register.x8.AL));
                    return BasicType.Bool;
                case NotEqual:
                    Assembler.cmp(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                    Assembler.setne(new RegisterMemory8(Register.x8.AL));
                    return BasicType.Bool;
            }
            return BasicType.Int;
        }
        else {
            return floatArithmetic(rhsType, lhsType);
        }
    }
}
