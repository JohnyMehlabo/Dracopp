package Parser.Stmts;

import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.Parsing.ExprParser;
import Parser.Parser;

public class ExprStmt implements Stmt {

    Expr expr;

    @Override
    public void log() {
        System.out.print("Expression Stmt: ");
    }

    @Override
    public void codegen() {
        expr.codegen();
    }

    @Override
    public void run() {
        expr.value();
    }

    private ExprStmt(Expr expr) {
        this.expr = expr;
    }

    public static ExprStmt parse() {
        ExprStmt exprStmt = new ExprStmt(ExprParser.parseExpr());
        Parser.expect(TokenType.Semicolon, "Expected ';' after an expression statement");
        return exprStmt;
    }
}
