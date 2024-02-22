package Parser.Exprs.Parsing;

import Parser.Exprs.Expr;

public interface ExprLayer {
    Expr parse(int depth);
}
