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
            // TODO: Find better way to do this
            return new UnaryOperationExpr(ExprParser.parseNextLayer(depth - 1), "&");
        }
        if (Parser.at().kind == TokenType.BinaryOperator && Parser.at().value.equals("*")) {
            Parser.eat();
            return new UnaryOperationExpr(ExprParser.parseNextLayer(depth - 1), "*");
        }

        return ExprParser.parseNextLayer(depth);
    }
}
