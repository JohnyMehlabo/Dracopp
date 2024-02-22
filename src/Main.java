import Lexer.Lexer;
import Lexer.Token;
import Parser.Parser;
import Parser.Stmts.Stmt;

import java.util.List;
import java.util.Objects;

public class Main {
    private static List<Token> tokens;
    private static Stmt AST;
    public static void main(String[] args) {
        String src = """
                var int asd = (123 + 35) * 2;
                """;

        tokens = Lexer.tokenize(src);
        AST = Parser.parse(tokens);
        logTokens();
        logAST();
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
}