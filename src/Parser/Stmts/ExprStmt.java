package Parser.Stmts;

import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Parser;

public class ExprStmt implements Stmt {

    final Expr expr;

    @Override
    public void log() {
        System.out.print("Expression Stmt: ");
    }

    @Override
    public void codegen() {
        expr.codegen();
    }

    private ExprStmt(Expr expr) {
        this.expr = expr;
    }

    public static ExprStmt parse() {
        ExprStmt exprStmt = new ExprStmt(Parser.parseExpr());
        Parser.expect(TokenType.Semicolon, "Expected ';' after an expression statement");
        return exprStmt;
    }
}
