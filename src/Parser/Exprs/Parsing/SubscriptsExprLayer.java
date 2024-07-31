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

        while (Parser.at().kind == TokenType.OpenParen || Parser.at().kind == TokenType.OpenBracket || Parser.at().kind == TokenType.MemberAccessor || Parser.at().kind == TokenType.PointerMemberAccessor) {
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
            if (Parser.at().kind == TokenType.OpenBracket) {
                Parser.eat();
                Expr index = Parser.parseExpr();
                Parser.expect(TokenType.CloseBracket, "Expected closing \"]\" after index in array subscript");
                left = new ArraySubscriptExpr(left, index);
            }
            if (Parser.at().kind == TokenType.MemberAccessor) {
                Parser.eat();
                Token memberIdentifier = Parser.expect(TokenType.Identifier, "Expected member name identifier in member access");
                left = new MemberAccessorExpr(left, memberIdentifier.value);
            }
            if (Parser.at().kind == TokenType.PointerMemberAccessor) {
                Parser.eat();
                Token memberIdentifier = Parser.expect(TokenType.Identifier, "Expected member name identifier in member access");
                left = new PointerMemberAccessorExpr(left, memberIdentifier.value);
            }
        }

        return left;
    }
}
