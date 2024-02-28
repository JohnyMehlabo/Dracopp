package Parser.Exprs.Parsing;

import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.FuncCallExpr;
import Parser.Parser;

public class SubscriptsExprLayer implements ExprLayer {
    @Override
    public Expr parse(int depth) {
        Expr left = ExprParser.parseNextLayer(depth);

        if (Parser.at().kind == TokenType.OpenParen) {
            Parser.eat();
            Parser.expect(TokenType.CloseParen, "Expected closing ')'");
            left = new FuncCallExpr(left);
        }

        return left;
    }
}
