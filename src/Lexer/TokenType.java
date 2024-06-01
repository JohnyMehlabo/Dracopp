package Lexer;

public enum TokenType {
    IntLiteral,
    StringLiteral,

    Var,
    Identifier,

    Arrow,
    AddressOf,
    MemberAccessor,
    PointerMemberAccessor,
    BinaryOperator,
    Equals,

    OpenParen,
    CloseParen,
    OpenBrace,
    CloseBrace,

    If,
    While,
    Func,
    Struct,

    Debug,

    Comma,
    Semicolon,
    EOF
}
