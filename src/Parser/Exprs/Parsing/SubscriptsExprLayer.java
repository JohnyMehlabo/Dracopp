package Parser.Exprs.Parsing;

import Lexer.Token;
import Lexer.TokenType;
import Parser.Exprs.*;
import Parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class SubscriptsExprLayer implements ExprLayer {
    @Override
    public Expr parse(int depth) {
        Expr left = ExprParser.parseNextLayer(depth);
        List<Expr> args = new ArrayList<>();

        while (Parser.at().kind == TokenType.OpenParen) {
            Parser.eat();
            while (Parser.at().kind != TokenType.CloseParen && Parser.notEOF()) {
                if (!args.isEmpty())
                    Parser.expect(TokenType.Comma, "Expected ',' after argument");
                args.add(Parser.parseExpr());
            }
            Parser.expect(TokenType.CloseParen, "Expected closing ')'");
            left = new FuncCallExpr(left, args);
        }
        while (Parser.at().kind == TokenType.OpenBracket) {
            Parser.eat();
            Token indexToken = Parser.expect(TokenType.IntLiteral, "Expected index after \"[\" in array subscript");
            Parser.expect(TokenType.CloseBracket, "Expected closing \"]\" after index in array subscript");
            left = new ArraySubscriptExpr(left, Integer.parseInt(indexToken.value));
        }
        while (Parser.at().kind == TokenType.MemberAccessor) {
            Parser.eat();
            Token memberIdentifier = Parser.expect(TokenType.Identifier, "Expected member name identifier in member access");
            left = new MemberAccessorExpr(left, memberIdentifier.value);
        }
        while (Parser.at().kind == TokenType.PointerMemberAccessor) {
            Parser.eat();
            Token memberIdentifier = Parser.expect(TokenType.Identifier, "Expected member name identifier in member access");
            left = new PointerMemberAccessorExpr(left, memberIdentifier.value);
        }

        return left;
    }
}
