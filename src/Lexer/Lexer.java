package Lexer;

import java.util.*;

public class Lexer {

    static Map<String, TokenType> KEYWORDS;
    static {
        KEYWORDS = new HashMap<String, TokenType>();
    }
    public static List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<Token>();

        List<String> src = new ArrayList<>(List.of(code.split("")));

        while (!src.isEmpty()) {
            if (src.get(0).equals("="))
                tokens.add(new Token(TokenType.Equals, src.remove(0)));
            else if (src.get(0).equals(";"))
                tokens.add(new Token(TokenType.Semicolon, src.remove(0)));
            else if (isSkippable(src.get(0)))
                src.remove(0);
            else if (isInt(src.get(0))) {
                StringBuilder num = new StringBuilder();
                while (!src.isEmpty() && isInt(src.get(0))) {
                    num.append(src.remove(0));
                }
                tokens.add(new Token(TokenType.IntLiteral, num.toString()));
            } else if (isAlpha(src.get(0))) {
                StringBuilder identifier = new StringBuilder();
                while (!src.isEmpty() && (isAlpha(src.get(0)) || isInt(src.get(0)) || src.get(0).equals("_"))) {
                    identifier.append(src.remove(0));
                }

                // CHECK FOR RESERVED KEYWORDS
                TokenType reserved = KEYWORDS.get(identifier.toString());
                // If value is not undefined then the identifier is
                // recognized keyword
                // Unrecognized name must mean user defined symbol.
                tokens.add(new Token(Objects.requireNonNullElse(reserved, TokenType.Identifier), identifier.toString()));
            } else {
                System.err.printf("Unknown token: '%s'%n", src.remove(0));;
                System.exit(-1);
            }
        }

        tokens.add(new Token(TokenType.EOF));
        return tokens;
    }

    static boolean isSkippable(String s) {
        return s.equals(" ") || s.equals("\n") || s.equals("\t") || s.equals("\r");
    }

    static boolean isInt(String s) {
        char c = s.charAt(0);
        char lowerBound = '0';
        char upperBound = '9';
        return c >= lowerBound && c <= upperBound;
    }

    static boolean isAlpha(String s) {
        return !s.toUpperCase().equals(s.toLowerCase());
    }
}
