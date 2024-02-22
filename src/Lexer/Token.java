package Lexer;

public class Token {

    Token(TokenType kind, String value) {
        this.kind = kind;
        this.value = value;
    }
    Token(TokenType kind) {
        this.kind = kind;
        this.value = "";
    }

    public TokenType kind;
    public String value;
}
