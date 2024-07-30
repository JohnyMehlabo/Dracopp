import Compiler.Assembler.Assembler;
import Compiler.Compiler;
import Lexer.Lexer;
import Lexer.Token;
import Parser.Parser;
import Parser.Stmts.Stmt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Main {
    private static byte[] data;
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Missing require argument \"filename\"");
            System.exit(-1);
        }

        Stmt AST = parseFile(args[0]);
        logAST(AST);

        Compiler.compile(AST);
        data = Assembler.getData().toByteArray();
        logData();
    }

    static Stmt parseFile(String path) {
        String src = null;
        try {
            src = Files.readString(Path.of("code.d++"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Token> tokens = Lexer.tokenize(src);
        logTokens(tokens);
        return Parser.parse(tokens);
    }

    static void logTokens(List<Token> tokens) {
        for (Token tok : tokens) {
            System.out.printf("Kind: %s", tok.kind.name());
            if (!Objects.equals(tok.value, ""))
                System.out.printf(", Value: '%s'%n", tok.value);
            // System.out.print('\n');
        }
    }

    static void logAST(Stmt AST) {
        AST.log();
    }

    static void logData() {
        for (byte b : data) {
            System.out.printf("%02x ", b);
        }
        System.out.print('\n');
    }
}