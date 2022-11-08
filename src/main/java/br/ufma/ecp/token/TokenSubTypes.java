package br.ufma.ecp.token;

public enum TokenSubTypes {

        // Symbols
        // delimitator
        LPAREN,
        RPAREN,
        LBRACE,
        RBRACE,
        LBRACKET,
        RBRACKET,
        
        COMMA,
        SEMICOLON,
        DOT,

        PLUS,
        MINUS,
        ASTERISK,
        SLASH,

        AND,
        OR,
        NOT,

        LT,
        GT,
        EQ,



        EOF, 

        ILLEGAL,


        // Keywords
        IDENT,
        LET,
        INT,
        VAR,
        CONST,
        PRINT,
        CLASS,
        METHOD,
        FUNCTION,
        CONSTRUCTOR,
        BOOLEAN,
        CHAR,
        VOID,
        STATIC,
        FIELD,
        DO,
        IF,
        ELSE,
        WHILE,
        RETURN,
        TRUE,
        FALSE,
        NULL,
        THIS,
        // else
        NUMBER,
        STRING,
        STRING_CONST,
        IDENTIFIER,
}
