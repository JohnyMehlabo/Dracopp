package Lexer;

public enum TokenType {
    IntLiteral,

    Var,
    Identifier,

    Arrow,
    BinaryOperator,
    Equals,

    OpenParen,
    CloseParen,
    OpenBrace,
    CloseBrace,

    If,
    While,
    Func,

    Debug,

    Semicolon,
    EOF
}
