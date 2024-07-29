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
    OpenBracket,
    CloseBracket,

    If,
    While,
    Func,
    Struct,
    Class,
    Extends,
    Method,

    Comma,
    Semicolon,
    EOF
}
