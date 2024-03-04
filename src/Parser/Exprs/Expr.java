package Parser.Exprs;

import Compiler.Types.Type;
public interface Expr {
    void log();
    Type codegen();
    default Type address() {
        System.err.println("Cannot get address of expression");
        System.exit(-1);
        return null;
    }
}
