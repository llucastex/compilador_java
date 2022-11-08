package br.ufma.ecp;

import static br.ufma.ecp.token.TokenSubTypes.*;
import br.ufma.ecp.token.Token;

import br.ufma.ecp.token.TokenSubTypes;


public class Parser {
    private Scanner scan;
    private Token currentToken;
    private Token peekToken;

    private StringBuilder xmlOutput = new StringBuilder();

	  public Parser (byte[] input) {
        scan = new Scanner(input);
        nextToken();
    }

    private void nextToken () {
    currentToken = peekToken;
    peekToken = scan.nextToken();
}

    void parse() {
        parseClass();
    }
    
    //'class' className '{' classVarDec* subroutineDec* '}'
    //'class' className '{' classVarDec '}'
    void parseClass() {
        printNonTerminal("class");
        expectPeek(CLASS);
        expectPeek(IDENTIFIER);
        expectPeek(LBRACE);
        while (peekToken.getType() == FIELD || peekToken.getType() == STATIC) {
            parseClassVarDec();
        }

        while (peekTokenIs(FUNCTION) || peekTokenIs(CONSTRUCTOR) || peekTokenIs(METHOD)) {
            parseSubRoutineDec();
        }
        
        expectPeek(RBRACE);
        printNonTerminal("/class");
    }
    //( 'static' | 'field' ) type varName ( ',' varName)* ';'
    // Compiles a static declaration or a field declaration.
    void parseClassVarDec() {
        printNonTerminal("classVarDec");
        expectPeek(FIELD, STATIC);
        expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
        expectPeek(IDENTIFIER);
        while (peekToken.getType() == COMMA) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }
    
    //Compiles a VAR declaration.
    void parseVardec () {
        printNonTerminal("varDec");
        expectPeek(VAR);
        expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
        expectPeek(IDENTIFIER);
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
        }

        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }

    // Compiles a complete method, function, or constructor.
    // ( 'constructor' | 'function' | 'method' ) ( 'void' | type) subroutineName '(' parameterList ')' subroutineBody
    void parseSubRoutineDec(){
        printNonTerminal("subroutineDec");
        expectPeek(CONSTRUCTOR, FUNCTION, METHOD);
        expectPeek(VOID, INT, CHAR, BOOLEAN, IDENTIFIER);
        expectPeek(IDENTIFIER);
        expectPeek(LPAREN);
        parseParameterList();
        expectPeek(RPAREN);
        parseSubroutineBody();

        printNonTerminal("/subroutineDec");
    }
    // '{' varDec* statements '}'
    void parseSubroutineBody () {
        printNonTerminal("subroutineBody");
        expectPeek(LBRACE);
        while (peekTokenIs(VAR)) {
            parseVardec();
        }
        parseStatements();
        expectPeek(RBRACE);
        printNonTerminal("/subroutineBody");
    }

    // Compiles a (possibly empty) parameter list, not including the enclosing "()" .
    void parseParameterList(){
        printNonTerminal("parameterList");
        if (!peekTokenIs(RPAREN)){
            expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
            expectPeek(IDENTIFIER);
        }
        while (peekTokenIs(COMMA)){
            expectPeek(COMMA);
            expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
            expectPeek(IDENTIFIER);
        }
        printNonTerminal("/parameterList");
    }

    
   
    // 'while' '(' expression ')' '{' statements '}'
    void parseWhile () {
        printNonTerminal("whileStatement");
        expectPeek(WHILE);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);
        printNonTerminal("/whileStatement");
    }
    // Compiles an if statement, possibly with a trailing else clause.
    void parseIf () {
        printNonTerminal("ifStatement");
        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);
        if (peekTokenIs(ELSE)){
            expectPeek(ELSE);
            expectPeek(LBRACE);
            parseStatements();
            expectPeek(RBRACE);
        }
        printNonTerminal("/ifStatement");
    }
    // Compiles a return statement.
    void parseReturn () {
        printNonTerminal("returnStatement");
        expectPeek(RETURN);
        if (peekToken.getType() != SEMICOLON) {
            parseExpression();
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/returnStatement");
    }
    // 
    void parseSubroutineCall () {
        if (peekTokenIs (LPAREN)) {
            expectPeek(LPAREN);
            parseExpressionList();
            expectPeek(RPAREN);
        } else {
            // pode ser um metodo de um outro objeto ou uma função
            expectPeek(DOT);
            expectPeek(IDENTIFIER);
            expectPeek(LPAREN);
            parseExpressionList();
            expectPeek(RPAREN);
        }
    }

    // Compiles a do statement.
    void parseDo () {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENTIFIER);
        parseSubroutineCall();
        expectPeek(SEMICOLON);

        printNonTerminal("/doStatement");
    }

    // Compiles an expression.
    void parseExpressionList() {
        printNonTerminal("expressionList");

        if (!peekTokenIs(RPAREN)){
            parseExpression();
        }
        while (peekTokenIs(COMMA)){
            expectPeek(COMMA);
            parseExpression();
        }
        printNonTerminal("/expressionList");
    }
    // Compiles a sequence of statements, not including the enclosing ‘‘{}’’.
    void parseStatements () {
        printNonTerminal("statements");
        while (peekToken.getType() == WHILE ||
        peekToken.getType() == DO ||
        peekToken.getType() == IF ||
        peekToken.getType() == LET ||
        peekToken.getType() == RETURN ) {
            parseStatement();
        }
        printNonTerminal("/statements");
    }

    void parseStatement() {
        switch (peekToken.getType()) {
            case LET:
                parseLet();
                break;
            case WHILE:
                parseWhile();
                break;
            case IF:
                parseIf();
                break;
            case RETURN:
                parseReturn();
                break;
            case DO:
                parseDo();
                break;
            default:
            throw new Error("Syntax error - expected a statement");
        }
    }

    // letStatement -> 'let' varName  '=' term ';'
    // term -> number;
    void parseLet() {
        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENTIFIER);
        if (peekTokenIs (LBRACKET)) {
            expectPeek(LBRACKET);
            parseExpression();
            expectPeek(RBRACKET);
        }
        expectPeek(EQ);
        parseExpression();
        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");

    }

    void parseExpression() {
        printNonTerminal("expression");
        parserTerm ();
        while (peekToken.getType() == PLUS ||
        peekToken.getType() == MINUS ||
        peekToken.getType() == ASTERISK ||
        peekToken.getType() == SLASH ||
        peekToken.getType() == OR ||
        peekToken.getType() == NOT ||
        peekToken.getType() == LT ||
        peekToken.getType() == GT ||
        peekToken.getType() == EQ ||
        peekToken.getType() == AND) {
            expectPeek(peekToken.getType());
            parserTerm();
        }
        printNonTerminal("/expression");
    }

    void parseIdentifier(){
        if (peekTokenIs(LPAREN) || peekTokenIs(DOT)) {
            parseSubroutineCall();
        } else {
            if (peekTokenIs(LBRACKET)){
                expectPeek(LBRACKET);
                parseExpression();
                expectPeek(RBRACKET);
            }
        }
    };

    void parserTerm () {
        printNonTerminal("term");
        switch (peekToken.getType()) {
            case NUMBER:
                expectPeek(NUMBER);
                break;
            case IDENTIFIER:
                expectPeek(IDENTIFIER);
                parseIdentifier();
                break;
            case STRING:
                expectPeek(STRING);
                break;
            case THIS:
                expectPeek(THIS);
                break;
            case FALSE:
            case NULL:
            case TRUE:
                expectPeek(FALSE,NULL,TRUE);
                break;
            case MINUS:
            case NOT:
                expectPeek(MINUS, NOT);
                parserTerm();
                break;
    
            case LPAREN:
                expectPeek(LPAREN);
                parseExpression();
                expectPeek(RPAREN);
                break;

            default:
                ;
        }
        printNonTerminal("/term");
    }

    boolean currentTokenIs (TokenSubTypes type) {
        return currentToken.getType() == type;
    }


    boolean peekTokenIs (TokenSubTypes type) {
        return peekToken.getType() == type;
    }


    private void expectPeek(TokenSubTypes type) {
        if (peekToken.getType() == type ) {
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
            throw new Error("Syntax error - expected "+type+" found " + peekToken.getLexeme());
        }
    }

    private void expectPeek(TokenSubTypes... types) {
        
        for (TokenSubTypes type : types) {
            if (peekToken.getType() == type) {
                expectPeek(type);
                return;
            }
        }
        throw new Error("Syntax error ");

    }

    private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }

    public String XMLOutput() {
        return xmlOutput.toString();
    }
  
}
