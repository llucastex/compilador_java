package br.ufma.ecp.token;

public class Token {

    public TokenType tokenType;
    final TokenSubTypes type;
    final String lexeme;
    

    public Token (TokenSubTypes type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;

        switch (this.type) {
          case PLUS:
          case MINUS:
          case EQ:
          case SEMICOLON:
            this.tokenType = TokenType.SYMBOL;
            break;
          case IDENT:
          case LET:
          case INT:
          case VAR:
          case CONST:
          case PRINT:
          case CLASS:
          case METHOD:
          case FUNCTION:
          case CONSTRUCTOR:
          case BOOLEAN:
          case CHAR:
          case VOID:
          case STATIC:
          case FIELD:
          case DO:
          case IF:
          case ELSE:
          case WHILE:
          case RETURN:
          case TRUE:
          case FALSE:
          case NULL:
          case THIS:
            this.tokenType = TokenType.KEYWORD;
            break;
          case NUMBER:
            this.tokenType = TokenType.INT_CONST;
            break;
          case STRING_CONST:
            this.tokenType = TokenType.STRING_CONST;
            break;
          default:
            this.tokenType = TokenType.IDENTIFIER;
        }
        // if (this.type == TokenSubTypes.PLUS){
        //     this.tokenType = TokenType.SYMBOL;
        // }
    }

    public TokenSubTypes getType(){
        return this.type;
    }

    public String getLexeme(){
        return this.lexeme;
    }

    public String TokentoString() {
        return "<"+ type +">" + lexeme + "</"+ type + ">";
    }

    public String toString() {
        return "<"+ this.tokenType.toString().toLowerCase() +"> " + lexeme + " </"+ this.tokenType.toString().toLowerCase() + ">";
    }  

    // public String toString() {
    //     return "<"+ this.tokenType +">" + lexeme + "</"+ this.tokenType + ">";
    // }

}
