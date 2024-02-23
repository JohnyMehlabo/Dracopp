package Parser.Stmts;

import Interpreter.Interpreter;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.Parsing.ExprParser;
import Parser.Parser;

public class VarDeclarationStmt implements Stmt {
    public String name;
    // TODO: Implement real types
    public String type;
    public Expr initialValue;

    VarDeclarationStmt(String name, Expr initialValue) {
        this.name = name;
        this.initialValue = initialValue;
    }

    public static VarDeclarationStmt parse() {
        Parser.eat();
        Parser.eat();

        Token nameToken = Parser.expect(TokenType.Identifier, "Expected identifier");

        Expr expr = null;
        if (Parser.at().kind == TokenType.Equals) {
            Parser.eat();
            expr = ExprParser.parseExpr();
        }

        Parser.expect(TokenType.Semicolon, "Expected ';' after a variable declaration statement");

        return new VarDeclarationStmt(nameToken.value, expr);
    }

    @Override
    public void log() {
        System.out.printf("Var Declaration Statement:\n\tName: %s\n\tType: %s\nInitial value:\n", name, type);
        if (initialValue != null) initialValue.log();
    }

    @Override
    public void run() {
        Interpreter.declareVar(name, type, initialValue);
    }
}
