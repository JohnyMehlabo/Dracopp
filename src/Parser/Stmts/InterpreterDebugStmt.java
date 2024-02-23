package Parser.Stmts;

import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.Parsing.ExprParser;
import Parser.Parser;

public class InterpreterDebugStmt implements Stmt {

    public Expr expr;

    @Override
    public void log() {

    }

    @Override
    public void run() {
        expr.value().log();
    }

    InterpreterDebugStmt(Expr expr) {
        this.expr = expr;
    }

    public static InterpreterDebugStmt parse() {
        Parser.eat();
        Expr expr = ExprParser.parseExpr();

        Parser.expect(TokenType.Semicolon, "Expected semicolon after interpreter debug statement");

        return new InterpreterDebugStmt(expr);
    }
}
