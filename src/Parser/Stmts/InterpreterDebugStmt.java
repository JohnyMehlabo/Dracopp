package Parser.Stmts;

import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Parser;

public class InterpreterDebugStmt implements Stmt {

    final Expr expr;

    @Override
    public void log() {

    }

    InterpreterDebugStmt(Expr expr) {
        this.expr = expr;
    }

    public static InterpreterDebugStmt parse() {
        Parser.eat();
        Expr expr = Parser.parseExpr();

        Parser.expect(TokenType.Semicolon, "Expected semicolon after interpreter debug statement");

        return new InterpreterDebugStmt(expr);
    }

    @Override
    public void codegen() {

    }
}
