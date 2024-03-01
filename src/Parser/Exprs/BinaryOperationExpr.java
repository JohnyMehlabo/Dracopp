package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Types.BasicType;
import Compiler.Types.Type;

import java.util.HashMap;
import java.util.Map;

public class BinaryOperationExpr implements Expr {

    public enum Operator {
        Sum, Subtraction, Multiplication, Division;

        static final Map<String, Operator> STRING_OPERATOR_MAP;
        static {
            STRING_OPERATOR_MAP = new HashMap<>();
            STRING_OPERATOR_MAP.put("+", Sum);
            STRING_OPERATOR_MAP.put("-", Subtraction);
            STRING_OPERATOR_MAP.put("*", Multiplication);
            STRING_OPERATOR_MAP.put("/", Division);
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
        Type rhsType = rhs.codegen();
        Type.cast(rhsType, BasicType.Int);
        Assembler.push(Register.x32.EAX);

        Type lhsType = lhs.codegen();
        Type.cast(lhsType, BasicType.Int);
        Assembler.pop(Register.x32.EBX);

        switch (operator) {
            case Sum:
                Assembler.add(Register.x32.EAX, Register.x32.EBX);
                break;
            case Subtraction:
                Assembler.sub(new RegisterMemory32(Register.x32.EAX), Register.x32.EBX);
                break;
            case Multiplication:
                Assembler.mul(Register.x32.EBX);
                break;
            case Division:
                Assembler.mov(Register.x32.EDX, 0);
                Assembler.div(Register.x32.EBX);
                break;
        }
        return BasicType.Int;
    }
}
