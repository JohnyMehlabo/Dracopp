package Parser.Exprs;

import Interpreter.RuntimeValue;

public interface Expr {
    void log();
    void codegen();
    RuntimeValue value();
}
