package Parser.Exprs.Parsing;

import Compiler.Types.Type;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.FuncCallExpr;
import Parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubscriptsExprLayer implements ExprLayer {
    @Override
    public Expr parse(int depth) {
        Expr left = ExprParser.parseNextLayer(depth);
        List<Expr> args = new ArrayList<>();

        if (Parser.at().kind == TokenType.OpenParen) {
            Parser.eat();
            while (Parser.at().kind != TokenType.CloseParen && Parser.notEOF()) {
                if (!args.isEmpty())
                    Parser.expect(TokenType.Comma, "Expected ',' after argument");
                args.add(Parser.parseExpr());
            }
            Parser.expect(TokenType.CloseParen, "Expected closing ')'");
            left = new FuncCallExpr(left, args);
        }

        return left;
    }
}
