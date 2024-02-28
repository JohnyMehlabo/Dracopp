package Parser.Stmts;

import Compiler.Assembler.Assembler;
import Compiler.Assembler.Register;
import Compiler.Assembler.RegisterMemory32;
import Compiler.Elf.ElfHandler;
import Compiler.Compiler;
import Lexer.TokenType;
import Parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class FuncDeclarationStmt implements Stmt {
    String name;
    List<Stmt> body;

    @Override
    public void log() {
        System.out.println("Function Declaration Statement:");
        System.out.printf("\tName: %s\n", name);
        System.out.println("\tBody:");
        for (Stmt stmt : body) {
            stmt.log();
        }
    }

    @Override
    public void codegen() {
        Compiler.addFunction(name);

        Compiler.startScope();
        ElfHandler.Text.addLabel(name, 1);

        Assembler.push(Register.x32.EBP);
        Assembler.mov(new RegisterMemory32(Register.x32.EBP), Register.x32.ESP);

        for (Stmt stmt : body) {
            stmt.codegen();
        }

        Assembler.leave();
        Assembler.ret();

        Compiler.endScope();

        Compiler.stackPtr = 0;
    }

    private FuncDeclarationStmt(String name, List<Stmt> body) {
        this.name = name;
        this.body = body;
    }

    public static FuncDeclarationStmt parse() {
        Parser.eat();

        String name = Parser.expect(TokenType.Identifier, "Expected identifier after 'func'").value;
        List<Stmt> body = new ArrayList<>();

        Parser.expect(TokenType.OpenParen, "Expected opening '('");
        Parser.expect(TokenType.CloseParen, "Expected closing ')'");

        Parser.expect(TokenType.OpenBrace, "Expected opening '{'");
        while ((Parser.at().kind != TokenType.CloseBrace) && Parser.notEOF()) {
            body.add(Parser.parseStmt());
        }
        Parser.expect(TokenType.CloseBrace, "Expected closing '}'");

        return new FuncDeclarationStmt(name, body);
    }
}
