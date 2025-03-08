package Parser;

import Compiler.Types.*;
import Compiler.Types.Class;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Exprs.Parsing.ExprParser;
import Parser.Stmts.*;

import java.util.List;

public class Parser {

    public static List<Token> tokens;

    public static Token at() {
        return tokens.getFirst();
    }
    public static Token eat() {
        return tokens.removeFirst();
    }
    public static Token expect(TokenType tokenType, String err) {
        if (at().kind != tokenType) {
            System.err.println(err);
            System.exit(-1);
        }
        return eat();
    }
    public static boolean notEOF() {
        return tokens.getFirst().kind != TokenType.EOF;
    }

    public static Stmt parseStmt() {
        return switch (at().kind) {
            case Var -> VarDeclarationStmt.parse();
            case OpenBrace -> BlockStmt.parse();
            case If -> IfStmt.parse();
            case While -> WhileStmt.parse();
            case Func -> FuncDeclarationStmt.parse();
            case Return -> ReturnStmt.parse();
            case Struct -> StructDeclarationStmt.parse();
            case Class -> ClassDeclarationStmt.parse();
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
        Type type;
        if (Parser.at().kind == TokenType.Struct) {
            Parser.eat();
            Token structIdentifier = Parser.expect(TokenType.Identifier, "Expected struct name after keyword \"struct\"");
            Struct struct = Struct.resolveStruct(structIdentifier.value);
            type = new StructType(struct);
        } else if (Parser.at().kind == TokenType.Class) {
            Parser.eat();
            Token structIdentifier = Parser.expect(TokenType.Identifier, "Expected class name after keyword \"struct\"");
            Class aClass = Class.resolveClass(structIdentifier.value);
            type = new ClassType(aClass);
        }else {
            Token typeToken = Parser.expect(TokenType.Identifier, "Expected type identifier");

            type = BasicType.get(typeToken.value);
            if (type == null) {
                System.err.printf("Unknown type in variable declaration: '%s'\n", typeToken.value);
                System.exit(-1);
            }
        }

        while (at().kind == TokenType.BinaryOperator && at().value.equals("*")) {
            eat();
            type = new PointerType(type);
        }
        while (at().kind == TokenType.AddressOf) {
            eat();
            type = new ReferenceType(type);
        }
        return type;
    }
    public static Type parseArrayType(Type originalType) {
        Type type = originalType;
        while (at().kind == TokenType.OpenBracket) {
            if (type instanceof ReferenceType) {
                System.err.println("Cannot have array of references");
                System.exit(-1);
            }
            Parser.eat();
            Token size = Parser.expect(TokenType.IntLiteral, "Expected array size after \"[\"");
            type = new ArrayType(type, Integer.parseInt(size.value));
            Parser.expect(TokenType.CloseBracket, "Expected closing \"]\" after array size");
        }
        return type;
    }
}
