package Parser.Stmts;

import Lexer.TokenType;
import Parser.Parser;
import Compiler.Compiler;

import java.util.ArrayList;
import java.util.List;

public class BlockStmt implements Stmt {

    final List<Stmt> body;

    @Override
    public void log() {
        System.out.println("Block Stmt: ");
        for (Stmt stmt : body) { stmt.log(); }
    }

    @Override
    public void codegen() {
        Compiler.startScope();
        for (Stmt stmt : body) { stmt.codegen(); }
        Compiler.endScope();
    }

    private BlockStmt(List<Stmt> body) {
        this.body = body;
    }

    public static BlockStmt parse() {
        List<Stmt> body = new ArrayList<>();
        Parser.eat();
        while (Parser.at().kind != TokenType.CloseBrace && Parser.notEOF()) {
            body.add(Parser.parseStmt());
        }
        Parser.expect(TokenType.CloseBrace, "Expected closing '}'");

        return new BlockStmt(body);
    }
}
