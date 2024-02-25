package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Interpreter.Integer32Value;
import Interpreter.RuntimeValue;

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

    Expr lhs;
    Expr rhs;
    Operator operator;

    public BinaryOperationExpr(Expr lhs, Expr rhs, String operator) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = Operator.fromString(operator);
    }

    @Override
    public void log() {
        System.out.println("Binary Operation Expr:\nLHS:");
        lhs.log();
        System.out.println("RHS:");
        rhs.log();
        System.out.printf("Operator: %s\n", operator.toString());
    }

    @Override
    public void codegen() {
        lhs.codegen();
        Assembler.push(Register.x32.EAX);
        rhs.codegen();
        Assembler.pop(Register.x32.EBX);

        switch (operator) {
            case Sum: {
                Assembler.add(Register.x32.EAX, Register.x32.EBX);
                break;
            }
            default: {
                // TODO: Implement this
                System.err.println("Not implemented");
            }

        }
    }

    @Override
    public RuntimeValue value() {
        RuntimeValue lhsValue, rhsValue;

        lhsValue = lhs.value();
        rhsValue = rhs.value();

        if (lhsValue instanceof Integer32Value && rhsValue instanceof Integer32Value) {
            int finalValue = intOperation((Integer32Value) lhsValue, (Integer32Value) rhsValue);
            return new Integer32Value(finalValue);
        }

        else {
            System.err.println("Invalid binary operator types");
            System.exit(-1);
        }

        return null;
    }

    private int intOperation(Integer32Value lhsValue, Integer32Value rhsValue) {
        int finalValue = 0;

        switch (operator) {
            case Sum -> finalValue = lhsValue.value + rhsValue.value;
            case Subtraction -> finalValue = lhsValue.value - rhsValue.value;
            case Multiplication -> finalValue = lhsValue.value * rhsValue.value;
            case Division -> finalValue = lhsValue.value / rhsValue.value;
        }

        return finalValue;
    }
}
