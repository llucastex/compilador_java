package br.ufma.ecp;

import br.ufma.ecp.token.Token;
// import br.ufma.ecp.token.TokenType;

import br.ufma.ecp.token.TokenSubTypes;
// import br.ufma.ecp.token.TokenSubTypes.*;

public class Parser {
    private Scanner scan;
    private Token currentToken;

	  public Parser (byte[] input) {
        scan = new Scanner(input);
        currentToken = scan.nextToken();
    }

    private void nextToken () {
    currentToken = scan.nextToken();
}

    public void parse () {
        statements();
    }
  
    void statement () {
        if (currentToken.getType() == TokenSubTypes.PRINT) {
            printStatement();
        } else if (currentToken.getType() == TokenSubTypes.LET) {
            letStatement();
        } else {
            throw new Error("syntax error");
        }
    }

    void statements () {
          
          while (currentToken.getType() != TokenSubTypes.EOF) {
              statement();
          }
     }
  
  // Verifica se bate e avança pro proximo Token
  private void match(TokenSubTypes t) {
        if (currentToken.getType() == t) {
            nextToken();
        }else {
            throw new Error("syntax error");
        }
   }

  void number () {
        System.out.println("push " + currentToken.getLexeme());
        match(TokenSubTypes.NUMBER);
    }

    void expr() {
     term();
     oper();
  }

  void oper () {
        if (currentToken.getType() == TokenSubTypes.PLUS) {
            match(TokenSubTypes.PLUS);
            term();
            System.out.println("add");
            oper();
        } else if (currentToken.getType() == TokenSubTypes.MINUS) {
            match(TokenSubTypes.MINUS);
            term();
            System.out.println("sub");
            oper();
        } 
    }

  void term () {
        if (currentToken.getType() == TokenSubTypes.NUMBER)
            number();
        else if (currentToken.getType() == TokenSubTypes.IDENTIFIER) {
            System.out.println("push "+currentToken.getLexeme());
            match(TokenSubTypes.IDENTIFIER);
        }
        else
            throw new Error("syntax error");
    }

  void letStatement () {
        match(TokenSubTypes.LET); // identifica o "let"
        var id = currentToken.getType(); // pega o lexema do token incrementado
        match(TokenSubTypes.IDENTIFIER); // identifica a variavel
        match(TokenSubTypes.EQ); // identifica o simbolo de igualdade
        expr(); // avalia a expressão
        System.out.println("pop "+id);
        match(TokenSubTypes.SEMICOLON); // verifica o ponto e virgula
 }
  
  void printStatement () {
        match(TokenSubTypes.PRINT);
        expr();
        System.out.println("print");
        match(TokenSubTypes.SEMICOLON);
    }

    public String VMOutput() {
        return "";
    }

  
}
