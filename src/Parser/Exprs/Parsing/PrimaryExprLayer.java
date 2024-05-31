package Parser.Exprs.Parsing;

import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.IdentifierExpr;
import Parser.Exprs.IntegerLiteralExpr;
import Parser.Exprs.StringLiteralExpr;
import Parser.Parser;

public class PrimaryExprLayer implements ExprLayer {
    @Override
    public Expr parse(int depth) {
        return switch (Parser.at().kind) {
            case IntLiteral -> new IntegerLiteralExpr(Integer.parseInt(Parser.eat().value));
            case StringLiteral -> new StringLiteralExpr(Parser.eat().value);
            case OpenParen -> {
                Parser.eat();
                Expr expr = Parser.parseExpr();
                Parser.expect(TokenType.CloseParen, "Expected closing parenthesis");
                yield expr;
            }
            case Identifier -> new IdentifierExpr(Parser.eat().value);
            default -> null;
        };
    }
}
