package br.ufma.ecp;

// import static br.ufma.ecp.token.TokenType.*;

// import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import br.ufma.ecp.token.Token;
// import br.ufma.ecp.token.Token.*;
import br.ufma.ecp.token.TokenSubTypes;
// import br.ufma.ecp.token.TokenSubTypes.*;
// import br.ufma.ecp.token.TokenType;

public class Scanner {

    private static final Map<String, TokenSubTypes> keywords;
   
    static {
        keywords = new HashMap<>();
        keywords.put("ident", TokenSubTypes.IDENT);
        keywords.put("let", TokenSubTypes.LET);
        keywords.put("int", TokenSubTypes.INT);
        keywords.put("var", TokenSubTypes.VAR);
        keywords.put("const", TokenSubTypes.CONST);
        keywords.put("print", TokenSubTypes.PRINT);
        keywords.put("class", TokenSubTypes.CLASS);
        keywords.put("method", TokenSubTypes.METHOD);
        keywords.put("function", TokenSubTypes.FUNCTION);
        keywords.put("constructor", TokenSubTypes.CONSTRUCTOR);
        keywords.put("bolean", TokenSubTypes.BOOLEAN);
        keywords.put("char", TokenSubTypes.CHAR);
        keywords.put("void", TokenSubTypes.VOID);
        keywords.put("static", TokenSubTypes.STATIC);
        keywords.put("field", TokenSubTypes.FIELD);
        keywords.put("do", TokenSubTypes.DO);
        keywords.put("if", TokenSubTypes.IF);
        keywords.put("else", TokenSubTypes.ELSE);
        keywords.put("while", TokenSubTypes.WHILE);
        keywords.put("return", TokenSubTypes.RETURN);
        keywords.put("true", TokenSubTypes.TRUE);
        keywords.put("false", TokenSubTypes.FALSE);
        keywords.put("null", TokenSubTypes.NULL);
        keywords.put("this", TokenSubTypes.THIS);
        keywords.put("eof", TokenSubTypes.EOF);
     
    }
    
    private byte[] input;
    private int current; 
  
    public Scanner (byte[] input) {
          this.input = input;
      }
  
    private char peek () {
          if (current < input.length)
             return (char)input[current];
         return '\0';
      }
  
    private void advance()  {
          char ch = peek();
          if (ch != '\0') {
              current++;
          }
      }
    private boolean isSymbol (char c) {
          String symbols = "{}()[].,;+-*/&|<>=~";
          return symbols.indexOf(c) > -1;
      }
    
    private boolean isAlpha(char c) {
          return (c >= 'a' && c <= 'z') ||
                 (c >= 'A' && c <= 'Z') ||
                  c == '_';
    }
      
    // Checa se é alphanumerico
    private boolean isAlphaNumeric(char c) {
              return isAlpha(c) || Character.isDigit((c));
    }
    
  // --- //
    
    public Token nextToken () {
      skipWhitespace();
      readComments();
      
      char ch = peek();
      if (ch == '"'){
        return readString();
      }
      
      if (isAlpha(ch)) {
         return identifier();
      }
        
      if (ch == '0') {
          advance();
          return new Token (TokenSubTypes.NUMBER, Character.toString(ch));
      }  else if (Character.isDigit(ch))
          return number();
             
      if (isSymbol(ch)){
              advance();
              switch (ch){
                case '<':
                  return new Token (TokenSubTypes.PLUS, "&lt");
                case '>':
                  return new Token (TokenSubTypes.PLUS, "&gt");
                case '"':
                  return new Token (TokenSubTypes.PLUS, "&quot");
                case '&':
                  return new Token (TokenSubTypes.PLUS, "&amp");
              }
              return new Token (TokenSubTypes.PLUS, String.valueOf(ch));
      }
  
      switch (ch) {
          case '+':
              advance();
              return new Token (TokenSubTypes.PLUS,"+");
          case '-':
              advance();
              return new Token (TokenSubTypes.MINUS,"-");
          case '\0':
              return new Token (TokenSubTypes.EOF,"EOF");
          case '=':
            advance();
            return new Token (TokenSubTypes.EQ,"=");
          case ';':    
            advance();
            return new Token (TokenSubTypes.SEMICOLON,";");
          default:
            throw new Error("lexical error at " + ch);
      }
      }
  
  // --- //
      private Token number() {
          int start = current ;
          while (Character.isDigit(peek())) {
              advance();
          }
          
          String n = new String(input, start, current-start)  ;
          return new Token(TokenSubTypes.NUMBER, n);
      }
    
      // Identifica o LET, PRINT e a VARIAVEL se houver. Caso contrario segue       // pra expressao
      private Token identifier() {
          int start = current;
          while (isAlphaNumeric(peek())) advance();
      
          String id = new String(input, start, current-start)  ;
          TokenSubTypes type = keywords.get(id);
          // System.out.println(type+" "+id);
          if (type == null) type = TokenSubTypes.IDENTIFIER;
          return new Token(type, id);
    }
  
      private void readComments(){
        boolean slash = false;
        while (peek() == '/' && slash == false){
        slash = readCommentLine(slash);
        skipWhitespace();
      }}
    
      private boolean readCommentLine(boolean slash){
        char ch = peek();
        if (ch != '/'){
         return slash;
          }
        advance();
        ch = peek();
  
        if (ch == '*'){
          advance();
          readCommentBlock();
          return slash;
        }
        
        if (ch != '/'){
         // throw new Error("lexical error at " + ch);
          current--;
          slash = true;
          return slash;
        } else{
          while(ch != '\r' && ch != '\t' && ch != '\n'){
            advance();
            ch = peek();
          }
        }
        return slash;
        }
  
        private void readCommentBlock(){
            int count = 2;
            char ch = peek();
            if (ch != '*'){
             throw new Error("lexical error at " + ch);
            } else{
              while(count != 0){
                advance();
                ch = peek();
                if (ch == '*'){
                  advance();
                  ch = peek();
                    if (ch == '/'){
                      count = 0;
                      advance();
                    }
                }
              }
            }
            }
  
      private Token readString(){
        char ch = peek();
        while(ch != '\r' && ch != '\t' && ch != '\n'){
            advance();
            ch = peek();
            if (ch == '"'){
              advance();
              return new Token (TokenSubTypes.STRING_CONST,"string constant");
            }
        }
  
        throw new Error("lexical error at " + ch);
        }
  
      
      private void skipWhitespace() {
          char ch = peek();
          while (ch == ' ' || ch == '\r' || ch == '\t' || ch == '\n') {
              advance();
              ch = peek();
          }
      }
}
