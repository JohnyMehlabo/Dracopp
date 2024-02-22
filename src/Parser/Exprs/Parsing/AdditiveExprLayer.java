package Parser.Exprs.Parsing;

import Lexer.TokenType;
import Parser.Exprs.BinaryOperationExpr;
import Parser.Exprs.Expr;
import Parser.Parser;

public class AdditiveExprLayer implements ExprLayer {
    @Override
    public Expr parse(int depth) {
        Expr left = ExprParser.parseNextLayer(depth);

        while (Parser.at().kind == TokenType.BinaryOperator && (Parser.at().value.equals("+") || Parser.at().value.equals("-"))) {
            String operator = Parser.eat().value;
            Expr right = ExprParser.parseNextLayer(depth);
            left = new BinaryOperationExpr(left, right, operator);
        }

        return left;
    }
}
