package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Compiler;
import Compiler.Types.ReferenceType;
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

        Type type = Parser.parseType();
        Token nameToken = Parser.expect(TokenType.Identifier, "Expected name identifier");
        type = Parser.parseArrayType(type);

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
        int size = type.getSize();
        Compiler.stackPtr += size;
        Compiler.scope.declareVar(name, type, Compiler.stackPtr);
        if (type instanceof ReferenceType) {
            if (initialValue == null) {
                System.err.println("Reference type value must be initialized");
                System.exit(-1);
            }
            // TODO: Implement type safety in reference variables
            initialValue.address();
            Assembler.mov(new RegisterMemory(null, Register.x32.EBP, (byte) -Compiler.stackPtr), size, Register.x32.ECX.ordinal(), size);
        }
        else if (initialValue != null) {
            Type valueType = initialValue.codegen();
            Type.cast(valueType, type);
            Assembler.mov(new RegisterMemory(null, Register.x32.EBP, (byte) -Compiler.stackPtr), size, Register.x32.EAX.ordinal(), size);
        }
        Assembler.sub(new RegisterMemory32(Register.x32.ESP), type.getSize());
    }
}
