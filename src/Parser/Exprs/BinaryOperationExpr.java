package Parser.Exprs;

import java.util.HashMap;
import java.util.Map;

public class BinaryOperationExpr implements Expr{

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
}
