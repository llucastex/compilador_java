package br.ufma.ecp;

import static br.ufma.ecp.token.TokenSubTypes.*;
import br.ufma.ecp.token.Token;

import br.ufma.ecp.token.TokenSubTypes;


public class Parser{
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
        while (checkToken(FIELD) || checkToken(STATIC)) {
            parseClassVarDec();
        }

        while (checkToken(FUNCTION) || checkToken(CONSTRUCTOR) || checkToken(METHOD)) {
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
        while (checkToken(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }
    
    // Compiles a VAR declaration.
    // 'var' type varName ( ',' varName)* ';'
    void parseVardec () {
        printNonTerminal("varDec");
        expectPeek(VAR);
        expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
        expectPeek(IDENTIFIER);
        while (checkToken(COMMA)) {
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
        
        if(checkToken(CONSTRUCTOR)){
            expectPeek(CONSTRUCTOR);
            expectPeek(IDENTIFIER);
            expectPeek(IDENTIFIER);
        }

        if(checkToken(FUNCTION) || checkToken(METHOD)){
            expectPeek(FUNCTION, METHOD);
            expectPeek(VOID, INT, CHAR, BOOLEAN);
            expectPeek(IDENTIFIER);
        }

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
        while (checkToken(VAR)) {
            parseVardec();
        }
        parseStatements();
        expectPeek(RBRACE);
        printNonTerminal("/subroutineBody");
    }

    // Compiles a (possibly empty) parameter list, not including the enclosing "()" .
    // ((type varName) ( ',' type varName)*)?
    void parseParameterList(){
        printNonTerminal("parameterList");
        if (!checkToken(RPAREN)){
            expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
            expectPeek(IDENTIFIER);
        }
        while (checkToken(COMMA)){
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
    // Compiles an if statement
    // 'if' '(' expression ')' '{' statements '}' ( 'else' '{' statements '}' )?
    void parseIf () {
        printNonTerminal("ifStatement");
        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);
        if (checkToken(ELSE)){
            expectPeek(ELSE);
            expectPeek(LBRACE);
            parseStatements();
            expectPeek(RBRACE);
        }
        printNonTerminal("/ifStatement");
    }

    // Compiles a return statement
    // 'return' expression? ';'
    void parseReturn () {
        printNonTerminal("returnStatement");
        expectPeek(RETURN);
        if (!checkToken(SEMICOLON)) {
            parseExpression();
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/returnStatement");
    }
    // subroutineName '(' expressionList ')' | (className|varName) '.' subroutineName '(' expressionList ')'
    void parseSubroutineCall () {
        if (checkToken (LPAREN)) {
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
    // 'do' subroutineCall ';'
    void parseDo () {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENTIFIER);
        parseSubroutineCall();
        expectPeek(SEMICOLON);

        printNonTerminal("/doStatement");
    }

    // Compiles an expression list.
    // (expression ( ',' expression)* )?
    void parseExpressionList() {
        printNonTerminal("expressionList");

        if (!checkToken(RPAREN)){
            parseExpression();
        }
        while (checkToken(COMMA)){
            expectPeek(COMMA);
            parseExpression();
        }
        printNonTerminal("/expressionList");
    }
    // Compiles a sequence of statements.
    // statement*
    void parseStatements () {
        printNonTerminal("statements");
        while (checkToken(WHILE) ||
        checkToken(DO)  ||
        checkToken(IF) ||
        checkToken(LET) ||
        checkToken(RETURN) ) {
            parseStatement();
        }
        printNonTerminal("/statements");
    }
    // letStatement | ifStatement | whileStatement | doStatement | returnStatement
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
        if (checkToken (LBRACKET)) {
            expectPeek(LBRACKET);
            parseExpression();
            expectPeek(RBRACKET);
        }
        expectPeek(EQ);
        parseExpression();
        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");

    }
    // term (op term)*
    void parseExpression() {
        printNonTerminal("expression");
        parserTerm ();
        while (checkToken(PLUS) ||
        checkToken(MINUS) ||
        checkToken(ASTERISK) ||
        checkToken(SLASH) ||
        checkToken(OR) ||
        checkToken(NOT) ||
        checkToken(LT) ||
        checkToken(GT) ||
        checkToken(EQ) ||
        checkToken(AND)) {
            expectPeek(peekToken.getType());
            parserTerm();
        }
        printNonTerminal("/expression");
    }
    // identifier
    void parseIdentifier(){
        if (checkToken(LPAREN) || checkToken(DOT)) {
            parseSubroutineCall();
        } else {
            if (checkToken(LBRACKET)){
                expectPeek(LBRACKET);
                parseExpression();
                expectPeek(RBRACKET);
            }
        }
    };
    // integerConstant | stringConstant | keywordConstant | varName | varName '[' expression ']' | subroutineCall | '(' expression ')' | unaryOp term
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

    boolean checkToken (TokenSubTypes type) {
        return peekToken.getType() == type;
    }


    public void expectPeek(TokenSubTypes type) {
        if (peekToken.getType() == type ) {
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
            throw new Error("Syntax error - expected "+type+" found " + peekToken.getLexeme());
        }
    }

    public void expectPeek(TokenSubTypes... types) {
        
        for (TokenSubTypes type : types) {
            if (peekToken.getType() == type) {
                expectPeek(type);
                return;
            }
        }
        throw new Error("Syntax error ");

    }

    public void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }

    public String XMLOutput() {
        return xmlOutput.toString();
    }
  
}
