package Lexer;

public enum TokenType {
    IntLiteral,

    Var,
    Identifier,

    BinaryOperator,
    Equals,

    OpenParen,
    CloseParen,
    OpenBrace,
    CloseBrace,

    If,

    Debug,

    Semicolon,
    EOF
}
