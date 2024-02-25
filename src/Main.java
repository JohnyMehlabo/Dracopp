import Compiler.Assembler.Assembler;
import Compiler.Compiler;
import Interpreter.Interpreter;
import Lexer.Lexer;
import Lexer.Token;
import Parser.Parser;
import Parser.Stmts.Stmt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Main {
    private static List<Token> tokens;
    private static Stmt AST;
    private static ByteArrayOutputStream data;
    public static void main(String[] args) throws IOException {
        String src = """
                2+3;
                """;

        tokens = Lexer.tokenize(src);
        AST = Parser.parse(tokens);

        Compiler.compile(AST);

        data = Assembler.getData();
        logData();

        Interpreter.run(AST);
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
        for (byte b : data.toByteArray()) {
            System.out.printf("%02x ", b);
        }
        System.out.print('\n');
    }
}