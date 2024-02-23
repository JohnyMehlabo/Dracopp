import Interpreter.Interpreter;
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
        /* String src = """
                var int asd = (123 + 35) * 2;
                """; */


        String src = """
                var int x = 10;
                var int y = 5;
                debug x - y;
                """;
        tokens = Lexer.tokenize(src);
        AST = Parser.parse(tokens);

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
}