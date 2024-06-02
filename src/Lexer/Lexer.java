package Lexer;

import java.util.*;

public class Lexer {

    static final Map<String, TokenType> KEYWORDS;
    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("var", TokenType.Var);
        KEYWORDS.put("debug", TokenType.Debug);
        KEYWORDS.put("if", TokenType.If);
        KEYWORDS.put("while", TokenType.While);
        KEYWORDS.put("func", TokenType.Func);
        KEYWORDS.put("struct", TokenType.Struct);
    }
    public static List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>();

        List<String> src = new ArrayList<>(List.of(code.split("")));

        while (!src.isEmpty()) {
            if (src.get(0).equals("="))
                tokens.add(new Token(TokenType.Equals, src.remove(0)));
            if (src.get(0).equals(">"))
                tokens.add(new Token(TokenType.BinaryOperator, src.remove(0)));
            if (src.get(0).equals("<"))
                tokens.add(new Token(TokenType.BinaryOperator, src.remove(0)));
            else if (src.get(0).equals("&"))
                tokens.add(new Token(TokenType.AddressOf, src.remove(0)));
            else if (src.get(0).equals(";"))
                tokens.add(new Token(TokenType.Semicolon, src.remove(0)));
            else if (src.get(0).equals(","))
                tokens.add(new Token(TokenType.Comma, src.remove(0)));
            else if (src.get(0).equals("."))
                tokens.add(new Token(TokenType.MemberAccessor, src.remove(0)));
            else if (src.get(0).equals(":"))
                tokens.add(new Token(TokenType.PointerMemberAccessor, src.remove(0)));
            else if (src.get(0).equals("("))
                tokens.add(new Token(TokenType.OpenParen, src.remove(0)));
            else if (src.get(0).equals(")"))
                tokens.add(new Token(TokenType.CloseParen, src.remove(0)));
            else if (src.get(0).equals("{"))
                tokens.add(new Token(TokenType.OpenBrace, src.remove(0)));
            else if (src.get(0).equals("}"))
                tokens.add(new Token(TokenType.CloseBrace, src.remove(0)));
            else if (src.get(0).equals("["))
                tokens.add(new Token(TokenType.OpenBracket, src.remove(0)));
            else if (src.get(0).equals("]"))
                tokens.add(new Token(TokenType.CloseBracket, src.remove(0)));
            else if (src.get(0).equals("+") || src.get(0).equals("*") || src.get(0).equals("/") || src.get(0).equals("%"))
                tokens.add(new Token(TokenType.BinaryOperator, src.remove(0)));
            else if (src.get(0).equals("-")) {
                if (src.size() > 1){
                    src.remove(0);
                    if (src.get(0).equals(">")) {
                        src.remove(0);
                        tokens.add(new Token(TokenType.Arrow, "->"));
                        continue;
                    }
                }
                tokens.add(new Token(TokenType.BinaryOperator, "-"));
            }
            else if (isSkippable(src.get(0)))
                src.remove(0);
            else if (src.get(0).equals("\"")) {
                src.remove(0);
                StringBuilder stringBuilder = new StringBuilder();
                while (!src.isEmpty() && !src.get(0).equals("\"")) {
                    if (src.get(0).equals("\\")) {
                        src.remove(0);
                        switch (src.remove(0)) {
                            case "\"": stringBuilder.append("\""); break;
                            case "n": stringBuilder.append("\n"); break;
                            case "t": stringBuilder.append("\t"); break;
                            case "0": stringBuilder.append("\0"); break;
                            default: {
                                System.err.println("Unknown escaping char after \"\\\"");
                                System.exit(-1);
                            }
                        }
                    }
                    else {
                        stringBuilder.append(src.remove(0));
                    }
                }
                stringBuilder.append("\0");
                if (src.isEmpty()) {
                    System.err.println("Missing closing \" at end of string literal");
                    System.exit(-1);
                }
                src.remove(0);
                tokens.add(new Token(TokenType.StringLiteral, stringBuilder.toString()));
            }
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
                System.err.printf("Unknown token: '%s'%n", src.remove(0));
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
