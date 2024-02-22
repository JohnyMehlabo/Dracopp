package Parser.Exprs.Parsing;

import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.IntegerLiteralExpr;
import Parser.Parser;

public class PrimaryExprLayer implements ExprLayer {
    @Override
    public Expr parse(int depth) {
        return switch (Parser.at().kind) {
            case IntLiteral -> new IntegerLiteralExpr(Integer.parseInt(Parser.eat().value));
            case OpenParen -> {
                Parser.eat();
                Expr expr = ExprParser.parseExpr();
                Parser.expect(TokenType.CloseParen, "Expected closing parenthesis");
                yield expr;
            }
            default -> null;
        };
    }
}
