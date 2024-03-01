package Parser.Exprs;

import Compiler.Types.Type;
public interface Expr {
    void log();
    Type codegen();
}
