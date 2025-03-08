package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Parser;

public class ReturnStmt implements Stmt{

    public Expr returnValue;

    ReturnStmt(Expr returnValue) { this.returnValue = returnValue; }

    public static ReturnStmt parse() {
        Parser.eat();

        if (Parser.at().kind == TokenType.Semicolon) {
            Parser.eat();
            return new ReturnStmt(null);
        } else {
            Expr returnValue = Parser.parseExpr();
            Parser.expect(TokenType.Semicolon, "Expected ';' after return statement\n");

            return new ReturnStmt(returnValue);
        }
    }

    @Override
    public void log() {
        System.out.println("Return Statement:\nReturn value:");
        if (returnValue != null) returnValue.log();
    }

    @Override
    public void codegen() {
        if (returnValue != null)
            returnValue.codegen();

        Assembler.leave();
        Assembler.ret();
    }
}
