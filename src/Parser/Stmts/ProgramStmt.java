package Parser.Stmts;

import Lexer.TokenType;
import Parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class ProgramStmt implements Stmt {
    public List<Stmt> statements = new ArrayList<>();

    public ProgramStmt() {}

    static public ProgramStmt parse() {
        ProgramStmt programStmt = new ProgramStmt();

        while (Parser.at().kind != TokenType.EOF) {
            programStmt.statements.add(Parser.parseStmt());
        }

        return programStmt;
    }

    @Override
    public void log() {
        for (Stmt stmt : statements) {
            System.out.println("Program Statement");
            stmt.log();
        }
    }

    @Override
    public void run() {
        for (Stmt stmt : statements) {
            stmt.run();
        }
    }

    @Override
    public void codegen() {
        for (Stmt stmt : statements) {
            stmt.codegen();
        }
    }
}
