package br.ufma.ecp.token;
import java.util.Arrays;

public enum TokenSubTypes {

        // Symbols
        // delimitator
        LPAREN("("),
        RPAREN(")"),
        LBRACE("{"),
        RBRACE("}"),
        LBRACKET("["),
        RBRACKET("]"),    
        
        DOT("."),
        COMMA(","),
        SEMICOLON(";"),    

        PLUS("+"),
        MINUS("-"),
        ASTERISK("*"),
        SLASH("/"),
        AND("&"),
        OR("|"),
        NOT("~"),    

        LT("<"),
        GT(">"),    
        EQ("="),



        EOF(), 

        ILLEGAL(),


        
        // Keywords
        WHILE("while"), CLASS("class"),CONSTRUCTOR("constructor"),FUNCTION("function"),
        METHOD("method"),FIELD("field"),STATIC("static"),VAR("var"),INT("int"),
        CHAR("char"),BOOLEAN("boolean"),VOID("void"),TRUE("true"),FALSE("false"),
        NULL("null"),THIS("this"),LET("let"),DO("do"),IF("if"),
        ELSE("else"), RETURN("return"),
        IDENT(),
        CONST(),
        PRINT(),
        // else
        NUMBER(),
        STRING(),
        STRING_CONST(),
        IDENTIFIER(),
        INTEGER();


        private TokenSubTypes() {
        }
    
        private TokenSubTypes(String value) {
            this.value = value;
        }

        public String value;

        public static TokenSubTypes fromValue(String value) {
                return Arrays.stream(TokenSubTypes.values())
                        .filter(symbolType -> symbolType.value != null && symbolType.value.equals(value))
                        .findFirst()
                        .orElse(null);
            }
        
    
}
