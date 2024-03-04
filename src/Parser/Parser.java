package Parser;

import Compiler.Types.BasicType;
import Compiler.Types.PointerType;
import Compiler.Types.Type;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.Parsing.ExprParser;
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
    public static boolean notEOF() {
        return tokens.get(0).kind != TokenType.EOF;
    }

    public static Stmt parseStmt() {
        return switch (at().kind) {
            case Var -> VarDeclarationStmt.parse();
            case Debug -> InterpreterDebugStmt.parse();
            case OpenBrace -> BlockStmt.parse();
            case If -> IfStmt.parse();
            case While -> WhileStmt.parse();
            case Func -> FuncDeclarationStmt.parse();
            default -> ExprStmt.parse();
        };
    }

    public static Stmt parse(List<Token> tks){
        tokens = tks;
        return ProgramStmt.parse();
    }

    public static Expr parseExpr(){
        return ExprParser.parseExpr();
    }

    public static Type parseType() {
        Token typeToken = Parser.expect(TokenType.Identifier, "Expected type identifier");

        Type type = BasicType.get(typeToken.value);
        if (type == null) {
            System.err.printf("Unknown type in variable declaration: '%s'\n", typeToken.value);
            System.exit(-1);
        }
        while (at().kind == TokenType.BinaryOperator && at().value.equals("*")) {
            eat();
            type = new PointerType(type);
        }
        return type;
    }
}
