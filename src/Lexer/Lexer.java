package Lexer;

import java.util.*;

public class Lexer {

    static final Map<String, TokenType> KEYWORDS;
    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("var", TokenType.Var);
        KEYWORDS.put("if", TokenType.If);
        KEYWORDS.put("else", TokenType.Else);
        KEYWORDS.put("while", TokenType.While);
        KEYWORDS.put("func", TokenType.Func);
        KEYWORDS.put("return", TokenType.Return);
        KEYWORDS.put("struct", TokenType.Struct);
        KEYWORDS.put("class", TokenType.Class);
        KEYWORDS.put("extends", TokenType.Extends);
        KEYWORDS.put("method", TokenType.Method);
    }
    public static List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>();

        List<String> src = new ArrayList<>(List.of(code.split("")));

        while (!src.isEmpty()) {
            if (src.getFirst().equals("=")) {
                if (src.size() > 1) {
                    src.removeFirst();
                    if (src.getFirst().equals("=")) {
                        src.removeFirst();
                        tokens.add(new Token(TokenType.BinaryOperator, "=="));
                        continue;
                    }
                }
                tokens.add(new Token(TokenType.Equals, "="));
            }
            else if (src.getFirst().equals("!")) {
                if (src.size() > 1) {
                    src.removeFirst();
                    if (src.getFirst().equals("=")) {
                        src.removeFirst();
                        tokens.add(new Token(TokenType.BinaryOperator, "!="));
                        continue;
                    }
                }
                System.err.println("Invalid token '!'");
                System.exit(-1);
            }
            else if (src.getFirst().equals(">")) {
                if (src.size() > 1) {
                    src.removeFirst();
                    if (src.getFirst().equals("=")) {
                        src.removeFirst();
                        tokens.add(new Token(TokenType.BinaryOperator, ">="));
                        continue;
                    }
                }
                tokens.add(new Token(TokenType.BinaryOperator, ">"));
            }
            else if (src.getFirst().equals("<")) {
                if (src.size() > 1) {
                    src.removeFirst();
                    if (src.getFirst().equals("=")) {
                        src.removeFirst();
                        tokens.add(new Token(TokenType.BinaryOperator, "<="));
                        continue;
                    }
                }
                tokens.add(new Token(TokenType.BinaryOperator, "<"));
            }
            else if (src.getFirst().equals("&"))
                tokens.add(new Token(TokenType.AddressOf, src.removeFirst()));
            else if (src.getFirst().equals(";"))
                tokens.add(new Token(TokenType.Semicolon, src.removeFirst()));
            else if (src.getFirst().equals(","))
                tokens.add(new Token(TokenType.Comma, src.removeFirst()));
            else if (src.getFirst().equals("."))
                tokens.add(new Token(TokenType.MemberAccessor, src.removeFirst()));
            else if (src.getFirst().equals(":"))
                tokens.add(new Token(TokenType.PointerMemberAccessor, src.removeFirst()));
            else if (src.getFirst().equals("("))
                tokens.add(new Token(TokenType.OpenParen, src.removeFirst()));
            else if (src.getFirst().equals(")"))
                tokens.add(new Token(TokenType.CloseParen, src.removeFirst()));
            else if (src.getFirst().equals("{"))
                tokens.add(new Token(TokenType.OpenBrace, src.removeFirst()));
            else if (src.getFirst().equals("}"))
                tokens.add(new Token(TokenType.CloseBrace, src.removeFirst()));
            else if (src.getFirst().equals("["))
                tokens.add(new Token(TokenType.OpenBracket, src.removeFirst()));
            else if (src.getFirst().equals("]"))
                tokens.add(new Token(TokenType.CloseBracket, src.removeFirst()));
            else if (src.getFirst().equals("+") || src.getFirst().equals("*") || src.getFirst().equals("/") || src.getFirst().equals("%"))
                tokens.add(new Token(TokenType.BinaryOperator, src.removeFirst()));
            else if (src.getFirst().equals("-")) {
                if (src.size() > 1){
                    src.removeFirst();
                    if (src.getFirst().equals(">")) {
                        src.removeFirst();
                        tokens.add(new Token(TokenType.Arrow, "->"));
                        continue;
                    }
                }
                tokens.add(new Token(TokenType.BinaryOperator, "-"));
            }
            else if (isSkippable(src.getFirst()))
                src.removeFirst();
            else if (src.getFirst().equals("\"")) {
                src.removeFirst();
                StringBuilder stringBuilder = new StringBuilder();
                while (!src.isEmpty() && !src.getFirst().equals("\"")) {
                    if (src.getFirst().equals("\\")) {
                        src.removeFirst();
                        switch (src.removeFirst()) {
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
                        stringBuilder.append(src.removeFirst());
                    }
                }
                stringBuilder.append("\0");
                if (src.isEmpty()) {
                    System.err.println("Missing closing \" at end of string literal");
                    System.exit(-1);
                }
                src.removeFirst();
                tokens.add(new Token(TokenType.StringLiteral, stringBuilder.toString()));
            }
            else if (isInt(src.getFirst())) {
                boolean isFloat = false;
                StringBuilder num = new StringBuilder();
                while (!src.isEmpty() && (isInt(src.getFirst()) || src.getFirst().equals("."))) {
                    if (src.getFirst().equals(".") && !isFloat)
                        isFloat = true;
                    else if (src.getFirst().equals(".") && isFloat) {
                        System.err.println("Invalid number construction. Unexpected \".\"");
                        System.exit(-1);
                    }
                    num.append(src.removeFirst());
                }
                if (!isFloat)
                    tokens.add(new Token(TokenType.IntLiteral, num.toString()));
                else
                    tokens.add(new Token(TokenType.FloatLiteral, num.toString()));
            } else if (isAlpha(src.getFirst())) {
                StringBuilder identifier = new StringBuilder();
                while (!src.isEmpty() && (isAlpha(src.getFirst()) || isInt(src.getFirst()) || src.getFirst().equals("_"))) {
                    identifier.append(src.removeFirst());
                }

                // CHECK FOR RESERVED KEYWORDS
                TokenType reserved = KEYWORDS.get(identifier.toString());
                // If value is not undefined then the identifier is
                // recognized keyword
                // Unrecognized name must mean user defined symbol.
                tokens.add(new Token(Objects.requireNonNullElse(reserved, TokenType.Identifier), identifier.toString()));
            } else {
                System.err.printf("Unknown token: '%s'%n", src.removeFirst());
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
