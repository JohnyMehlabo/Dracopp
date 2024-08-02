package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Compiler;
import Compiler.Elf.ElfHandler;
import Compiler.Types.ReferenceType;
import Compiler.Types.Type;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Exprs.Expr;
import Parser.Parser;

import java.io.IOException;

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
        if (!Compiler.scope.isGlobalScope()) {
            Compiler.stackPtr += size;
            Compiler.scope.declareVar(name, type, Compiler.stackPtr);
            if (type instanceof ReferenceType) {
                if (initialValue == null) {
                    System.err.println("Reference type value must be initialized");
                    System.exit(-1);
                }
                // TODO: Implement type safety in reference variables
                initialValue.address();
                Assembler.mov(new RegisterMemory(null, Register.x32.EBP, -Compiler.stackPtr), size, Register.x32.ECX.ordinal(), size);
            }
            else if (initialValue != null) {
                Type valueType = initialValue.codegen();
                Type.cast(valueType, type);
                Assembler.mov(new RegisterMemory(null, Register.x32.EBP, -Compiler.stackPtr), size, Register.x32.EAX.ordinal(), size);
            }
        }
        else {
            Compiler.scope.declareVar(name, type);
            ElfHandler.BSS.addLabel(name, 1);
            ElfHandler.BSS.reserveBytes(size);

            if (type instanceof ReferenceType) {
                System.err.println("Global variable can't be of reference type");
                System.exit(-1);
            }
            else if (initialValue != null) {
                System.err.println("Global variable can't have initial value");
                System.exit(-1);
            }
        }

    }
}
