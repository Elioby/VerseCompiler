package dev.jok.verse.lexer;

public enum TokenType {

    // Punctuation
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, LEFT_BRACKET, RIGHT_BRACKET,
    COMMA, DOT, SEMICOLON, COLON,

    // Math
    EQUALS, PLUS, MINUS, STAR, SLASH,
    GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER_INT, NUMBER_FLOAT,

    // Keywords
    AND, OR, NOT, TRUE, FALSE,
    VAR, RETURN, SELF,
    IF, WHILE, FOR,

    // @Todo(Jok): remove
    // Temporary
    PRINT,

    EOF;

}
