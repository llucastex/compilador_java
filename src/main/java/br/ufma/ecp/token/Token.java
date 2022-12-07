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
          case LPAREN:
          case RPAREN:
          case LBRACE:
          case RBRACE:
          case LBRACKET:
          case RBRACKET:
          case COMMA:
          case DOT:
          case ASTERISK:
          case SLASH:
          case AND:
          case OR:
          case NOT:
          case LT:
          case GT:
          case EOF: 
            this.tokenType = TokenType.SYMBOL;
            break;
          case IDENT:
          case BOOLEAN:
          case LET:
          case INT:
          case VAR:
          case CONST:
          case PRINT:
          case CLASS:
          case METHOD:
          case FUNCTION:
          case CONSTRUCTOR:
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
    }

    public TokenSubTypes getType(){
        return this.type;
    }

    public String getLexeme(){
        return this.lexeme;
    }


    public String toString() {
        if (this.tokenType == TokenType.INT_CONST){
        return "<integerConstant> " + lexeme + " </integerConstant>";
        } else if(this.tokenType == TokenType.SYMBOL){
        return "<symbol> " + lexeme + " </symbol>";
        }
        return "<"+ this.tokenType.toString().toLowerCase() +"> " + lexeme + " </"+ this.tokenType.toString().toLowerCase() + ">";
    } 

    public String value () {
      return this.lexeme;
  }


}
