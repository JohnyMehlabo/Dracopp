package Parser.Exprs.Parsing;

import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.UnaryOperationExpr;
import Parser.Parser;

public class MemoryOperatorExprLayer implements ExprLayer{
    @Override
    public Expr parse(int depth) {
        if (Parser.at().kind == TokenType.AddressOf) {
            Parser.eat();
            return new UnaryOperationExpr(ExprParser.parseNextLayer(depth), "&");
        }
        if (Parser.at().kind == TokenType.BinaryOperator && Parser.at().value.equals("*")) {
            Parser.eat();
            return new UnaryOperationExpr(ExprParser.parseNextLayer(depth), "*");
        }

        return ExprParser.parseNextLayer(depth);
    }
}
