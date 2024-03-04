package Parser.Exprs;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Types.BasicType;
import Compiler.Types.PointerType;
import Compiler.Types.Type;

import java.util.HashMap;
import java.util.Map;

public class UnaryOperationExpr implements Expr {

    public enum Operator {
        AddressOf, Dereference;

        static final Map<String, Operator> STRING_OPERATOR_MAP;
        static {
            STRING_OPERATOR_MAP = new HashMap<>();
            STRING_OPERATOR_MAP.put("&", AddressOf);
            STRING_OPERATOR_MAP.put("*", Dereference);
        }

        public static Operator fromString(String s) {
            return STRING_OPERATOR_MAP.get(s);
        }
    }

    final Expr expr;
    final Operator operator;

    public UnaryOperationExpr(Expr expr, String operator) {
        this.expr = expr;
        this.operator = Operator.fromString(operator);
    }

    @Override
    public void log() {
        System.out.println("Unary Operation Expression:\nExpr:");
        expr.log();
        System.out.printf("Operator: %s\n", operator.toString());
    }

    @Override
    public Type codegen() {
        switch (operator) {
            case AddressOf: {
                Type type = expr.address();
                Assembler.mov(Register.x32.EAX, new RegisterMemory32(Register.x32.ECX));
                return new PointerType(type);
            }
            case Dereference: {
                Type type = expr.codegen();

                if (type instanceof PointerType){
                    Assembler.mov(Register.x32.EAX, new RegisterMemory32(null, Register.x32.EAX));
                    return ((PointerType) type).to;
                }
                System.err.println("Cannot dereference non-pointer type");
                System.exit(-1);
            }
        }
        return null;
    }
}

