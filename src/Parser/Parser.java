package Parser;

import Lexer.Token;
import Lexer.TokenType;
import Parser.Stmts.*;

import java.util.List;

public class Parser {

    public static List<Token> tokens;

    public static Token at() {
        return tokens.get(0);
    }

    public static Token eat() {
        return tokens.remove(0);
    }

    public static Token expect(TokenType tokenType, String err) {
        if (at().kind != tokenType) {
            System.err.println(err);
            System.exit(-1);
        }
        return eat();
    }

    public static Stmt parseStmt() {
        return switch (at().kind) {
            case Var -> VarDeclarationStmt.parse();
            case Debug -> InterpreterDebugStmt.parse();
            default -> ExprStmt.parse();
        };
    }

    public static Stmt parse(List<Token> tks){
        tokens = tks;
        return ProgramStmt.parse();
    }
}
