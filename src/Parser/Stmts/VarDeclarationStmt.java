package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Compiler;
import Compiler.Types.BasicType;
import Compiler.Types.Type;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Parser;

public class VarDeclarationStmt implements Stmt {
    public String name;
    public Type type;
    public Expr initialValue;

    VarDeclarationStmt(String name, Type type, Expr initialValue) {
        this.name = name;
        this.type = type;
        this.initialValue = initialValue;
    }

    public static VarDeclarationStmt parse() {
        Parser.eat();

        Token typeToken = Parser.expect(TokenType.Identifier, "Expected type identifier");

        Type type = BasicType.get(typeToken.value);
        if (type == null) {
            System.err.printf("Unknown type in variable declaration: '%s'\n", typeToken.value);
            System.exit(-1);
        }

        Token nameToken = Parser.expect(TokenType.Identifier, "Expected name identifier");

        Expr expr = null;
        if (Parser.at().kind == TokenType.Equals) {
            Parser.eat();
            expr = Parser.parseExpr();
        }

        Parser.expect(TokenType.Semicolon, "Expected ';' after a variable declaration statement");

        return new VarDeclarationStmt(nameToken.value, type, expr);
    }

    @Override
    public void log() {
        System.out.printf("Var Declaration Statement:\n\tName: %s\n\tType: %s\nInitial value:\n", name, type);
        if (initialValue != null) initialValue.log();
    }

    @Override
    public void codegen() {
        int size = Type.getSizeOf(type);
        Compiler.stackPtr += size;
        Compiler.scope.declareVar(name, type, Compiler.stackPtr);
        if (initialValue != null) {
            initialValue.codegen();
            Register reg = Register.fromSize(Register.x32.EAX.ordinal(), size);
            Assembler.mov(new RegisterMemory(null, Register.x32.EBP, (byte) -Compiler.stackPtr), size, reg, size);
        }
        Assembler.sub(new RegisterMemory32(Register.x32.ESP), (byte)4);
    }
}
