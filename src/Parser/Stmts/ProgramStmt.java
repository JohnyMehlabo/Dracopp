package Parser.Stmts;

import java.util.ArrayList;
import java.util.List;

import Lexer.TokenType;
import Parser.Parser;

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
}
