package Parser.Exprs.Parsing;

import Parser.Exprs.Expr;

public class ExprParser {

    private static final ExprLayer[] layers = new ExprLayer[] {
            new AdditiveExprLayer(),
            new MultiplicativeExprLayer(),
            new SubscriptsExprLayer(),
            new PrimaryExprLayer()

    };

    protected static Expr parseNextLayer(int depth) {
        return layers[depth + 1].parse(depth + 1);
    }

    public static Expr parseExpr() {
        return layers[0].parse(0);
    }
}
