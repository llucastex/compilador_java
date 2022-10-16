package br.ufma.ecp;

// import br.ufma.ecp.token.TokenType.*;
// import br.ufma.ecp.token.TokenType;
import br.ufma.ecp.token.TokenSubTypes;
// import br.ufma.ecp.token.TokenSubTypes.*;



import br.ufma.ecp.token.Token; 
// import br.ufma.ecp.token.Token.*; 

public class App 
{

    
    public static void main( String[] args )
    {

    
        String input = """
          
            
class SquareGame {
    field Square square; 
    field int direction; 
 
    constructor SquareGame new() {
       let square = square;
       let direction = direction;
       return square;
    }
 
    method void dispose() {
       do square.dispose();
       do Memory.deAlloc(square);
       return;
    }
 
    method void moveSquare() {
       if (direction) { do square.moveUp(); }
       if (direction) { do square.moveDown(); }
       if (direction) { do square.moveLeft(); }
       if (direction) { do square.moveRight(); }
       do Sys.wait(direction);
       return;
    }
 
    method void run() {
       var char key;
       var boolean exit;
       
       let exit = key;
       while (exit) {
          while (key) {
             let key = key;
             do moveSquare();
          }
 
          if (key) { let exit = exit; }
          if (key) { do square.decSize(); }
          if (key) { do square.incSize(); }
          if (key) { let direction = exit; }
          if (key) { let direction = key; }
          if (key) { let direction = square; }
          if (key) { let direction = direction; }
 
          while (key) {
             let key = key;
             do moveSquare();
          }
       }
       return;
     }
 }
 
 
          """;
        Scanner scan = new Scanner (input.getBytes());
        System.out.println("<tokens>");
        for (Token tk = scan.nextToken(); tk.getType() != TokenSubTypes.EOF; tk = scan.nextToken()) {
            System.out.println(tk);
        }
        System.out.println("</tokens>");

        /*
        Parser p = new Parser (input.getBytes());
        p.parse();
        */


        //Parser p = new Parser (fromFile().getBytes());
        //p.parse();

        /*
        String input = "489-85+69";
        Scanner scan = new Scanner (input.getBytes());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        Token tk = new Token(NUMBER, "42");
        System.out.println(tk);
        */
    }
}
