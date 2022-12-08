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
        
        DOT,
        COMMA,
        SEMICOLON,    

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
        WHILE, CLASS,CONSTRUCTOR,FUNCTION,
        METHOD,FIELD,STATIC,VAR,INT,
        CHAR,BOOLEAN,VOID,TRUE,FALSE,
        NULL,THIS,LET,DO,IF,
        ELSE, RETURN,
        IDENT,
        CONST,
        PRINT,
        // else
        NUMBER,
        STRING,
        STRING_CONST,
        IDENTIFIER,
        INTEGER
    
}
