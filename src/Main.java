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
    private static List<Token> tokens;
    private static Stmt AST;
    private static byte[] data;
    public static void main(String[] args) throws IOException {
        String src = Files.readString(Path.of("code.d++"));

        tokens = Lexer.tokenize(src);
        AST = Parser.parse(tokens);

        logAST();

        Compiler.compile(AST);
        data = Assembler.getData().toByteArray();
        logData();
    }

    static void logTokens() {
        for (Token tok : tokens) {
            System.out.printf("Kind: %s", tok.kind.name());
            if (!Objects.equals(tok.value, ""))
                System.out.printf(", Value: '%s'%n", tok.value);
            System.out.print('\n');
        }
    }

    static void logAST() {
        AST.log();
    }

    static void logData() {
        for (byte b : data) {
            System.out.printf("%02x ", b);
        }
        System.out.print('\n');
    }
}