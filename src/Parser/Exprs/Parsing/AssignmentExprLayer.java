package Parser.Exprs.Parsing;

import Lexer.TokenType;
import Parser.Exprs.AssignmentExpr;
import Parser.Exprs.BinaryOperationExpr;
import Parser.Exprs.Expr;
import Parser.Parser;

public class AssignmentExprLayer implements ExprLayer {
    @Override
    public Expr parse(int depth) {
        Expr dst = ExprParser.parseNextLayer(depth);

        if (Parser.at().kind == TokenType.Equals) {
            Parser.eat();
            Expr src = this.parse(depth);

            return new AssignmentExpr(dst, src);
        }

        return dst;
    }
}
