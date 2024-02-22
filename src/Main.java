import Lexer.Lexer;
import Lexer.Token;
import java.util.List;
import java.util.Objects;

public class Main {
    private static List<Token> tokens;
    public static void main(String[] args) {
        String src = """
                var int asd = 14;
                """;

        tokens = Lexer.tokenize(src);
        logTokens();
    }

    static void logTokens() {
        for (Token tok : tokens) {
            System.out.printf("Kind: %s", tok.kind.name());
            if (!Objects.equals(tok.value, ""))
                System.out.printf(", Value: '%s'%n", tok.value);
        }
    }
}