package Parser.Stmts;

import Lexer.TokenType;
import Parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class ProgramStmt implements Stmt {
    public final List<Stmt> statements = new ArrayList<>();

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
        System.out.println("Program Statement");
        for (Stmt stmt : statements) {
            stmt.log();
        }
    }

    @Override
    public void codegen() {
        for (Stmt stmt : statements) {
            stmt.codegen();
        }
    }
}
