package br.ufma.ecp;

import static br.ufma.ecp.token.TokenSubTypes.*;
import br.ufma.ecp.token.Token;
import br.ufma.ecp.SymbolTable.Kind;
import br.ufma.ecp.SymbolTable.Symbol;
import br.ufma.ecp.VMWriter.Command;
import br.ufma.ecp.VMWriter.Segment;

import br.ufma.ecp.token.TokenSubTypes;
// import br.ufma.ecp.token.TokenType;


public class Parser{
    private Scanner scan;
    private Token currentToken;
    private Token peekToken;
    private SymbolTable symbolTable;
    private VMWriter vmWriter;

    private StringBuilder xmlOutput = new StringBuilder();

    private String className;
    private int ifLabelNum;
    private int whileLabelNum;

	  public Parser (byte[] input) {
        scan = new Scanner(input);
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter();

        nextToken();

        ifLabelNum = 0;
        whileLabelNum = 0;

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
        className = currentToken.value();
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

        SymbolTable.Kind kind = Kind.STATIC;
        if (checkToken(FIELD))
            kind = Kind.FIELD;

        expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
        String type = currentToken.value();
        expectPeek(IDENTIFIER);
        String name = currentToken.value();

        symbolTable.define(name, type, kind);
        while (checkToken(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);

            name = currentToken.value();
            symbolTable.define(name, type, kind);
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }
    
    // Compiles a VAR declaration.
    // 'var' type varName ( ',' varName)* ';'
    void parseVardec () {
        printNonTerminal("varDec");
        expectPeek(VAR);

        SymbolTable.Kind kind = Kind.VAR;

        expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
        String type = currentToken.value();

        expectPeek(IDENTIFIER);
        String name = currentToken.value();
        symbolTable.define(name, type, kind);

        while (checkToken(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
            name = currentToken.value();
            symbolTable.define(name, type, kind);
        }

        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }

    // Compiles a complete method, function, or constructor.
    // ( 'constructor' | 'function' | 'method' ) ( 'void' | type) subroutineName '(' parameterList ')' subroutineBody
    void parseSubRoutineDec(){
        printNonTerminal("subroutineDec");

        ifLabelNum = 0;
        whileLabelNum = 0;

        symbolTable.startSubroutine();

        var subroutineType = currentToken.getType();

        if (subroutineType == METHOD) {
            symbolTable.define("this", className, Kind.ARG);
        }


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

        var functionName = className + "." + currentToken.value();
        expectPeek(LPAREN);
        parseParameterList();
        expectPeek(RPAREN);
        parseSubroutineBody(functionName, subroutineType);

        printNonTerminal("/subroutineDec");
    }

    // '{' varDec* statements '}'
    void parseSubroutineBody (String functionName, TokenSubTypes subroutineType) {
        printNonTerminal("subroutineBody");
        expectPeek(LBRACE);
        while (checkToken(VAR)) {
            parseVardec();
        }
        var nlocals = symbolTable.varCount(Kind.VAR);

        vmWriter.writeFunction(functionName, nlocals);

        if (subroutineType == CONSTRUCTOR) {
            vmWriter.writePush(Segment.CONST, symbolTable.varCount(Kind.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(Segment.POINTER, 0);
        }

        if (subroutineType == METHOD) {
            System.out.println("Entrei aqui");
            vmWriter.writePush(Segment.ARG, 0);
            vmWriter.writePop(Segment.POINTER, 0);
        }


        parseStatements();
        expectPeek(RBRACE);
        printNonTerminal("/subroutineBody");
    }

    // Compiles a (possibly empty) parameter list, not including the enclosing "()" .
    // ((type varName) ( ',' type varName)*)?
    void parseParameterList(){
        printNonTerminal("parameterList");

        SymbolTable.Kind kind = Kind.ARG;

        if (!checkToken(RPAREN)){
            expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
            String type = currentToken.value();
            expectPeek(IDENTIFIER);
            String name = currentToken.value();
            symbolTable.define(name, type, kind);

        }
        while (checkToken(COMMA)){
            expectPeek(COMMA);
            expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
            String type = currentToken.value();
            expectPeek(IDENTIFIER);
            String name = currentToken.value();
            symbolTable.define(name, type, kind);
        }
        printNonTerminal("/parameterList");
    }

    
    // 'while' '(' expression ')' '{' statements '}'
    void parseWhile () {
        printNonTerminal("whileStatement");

        var labelTrue = "WHILE_EXP" + whileLabelNum;
        var labelFalse = "WHILE_END" + whileLabelNum;
        whileLabelNum++;

        vmWriter.writeLabel(labelTrue);

        expectPeek(WHILE);
        expectPeek(LPAREN);
        parseExpression();

        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf(labelFalse);

        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();

        vmWriter.writeGoto(labelTrue); // Go back to labelTrue and check condition
        vmWriter.writeLabel(labelFalse); // Breaks out of while loop because ~(condition) is true

        expectPeek(RBRACE);
        printNonTerminal("/whileStatement");
    }
    // Compiles an if statement
    // 'if' '(' expression ')' '{' statements '}' ( 'else' '{' statements '}' )?
    void parseIf () {
        printNonTerminal("ifStatement");

        var labelTrue = "IF_TRUE" + ifLabelNum;
        var labelFalse = "IF_FALSE" + ifLabelNum;
        var labelEnd = "IF_END" + ifLabelNum;

        ifLabelNum++;

        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);

        vmWriter.writeIf(labelTrue);
        vmWriter.writeGoto(labelFalse);
        vmWriter.writeLabel(labelTrue);

        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);

        if (checkToken(ELSE))
        {
            vmWriter.writeGoto(labelEnd);
        }

        vmWriter.writeLabel(labelFalse);

        if (checkToken(ELSE)){
            expectPeek(ELSE);
            expectPeek(LBRACE);
            parseStatements();
            expectPeek(RBRACE);
            vmWriter.writeLabel(labelEnd);
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
        }else{
            System.out.println("Entrei aqui");
            vmWriter.writePush(Segment.CONST, 0);
        }
        expectPeek(SEMICOLON);
        vmWriter.writeReturn();
        printNonTerminal("/returnStatement");
    }
    // subroutineName '(' expressionList ')' | (className|varName) '.' subroutineName '(' expressionList ')'
    void parseSubroutineCall () {
        var nArgs = 0;

        var ident = currentToken.value();
        
        var symbol = symbolTable.resolve(ident); // classe ou objeto
        var functionName = ident + ".";

        if (checkToken (LPAREN)) {
            expectPeek(LPAREN);
            vmWriter.writePush(Segment.POINTER, 0);
            nArgs = parseExpressionList() + 1;
            expectPeek(RPAREN);
            functionName = className + "." + ident;
        } else {
            // pode ser um metodo de um outro objeto ou uma função
            expectPeek(DOT);
            expectPeek(IDENTIFIER);

            if (symbol != null) { // é um metodo
                functionName = symbol.type() + "." + currentToken.value();
                
                vmWriter.writePush(kind2Segment(symbol.kind()), symbol.index());
                nArgs = 1; // do proprio objeto
            } else {
                functionName += currentToken.value(); // é uma função
            }


            expectPeek(LPAREN);
            nArgs += parseExpressionList();
            expectPeek(RPAREN);
        }

        vmWriter.writeCall(functionName, nArgs);
    }

    // Compiles a do statement.
    // 'do' subroutineCall ';'
    void parseDo () {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENTIFIER);
        parseSubroutineCall();
        expectPeek(SEMICOLON);
        vmWriter.writePop(Segment.TEMP, 0);
        printNonTerminal("/doStatement");
    }

    // Compiles an expression list.
    // (expression ( ',' expression)* )?
    int parseExpressionList() {
        printNonTerminal("expressionList");

        var nArgs = 0;

        if (!checkToken(RPAREN)){
            parseExpression();
            nArgs = 1;
        }
        while (checkToken(COMMA)){
            expectPeek(COMMA);
            parseExpression();
            nArgs++;
        }
        printNonTerminal("/expressionList");
        return nArgs;
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
        var isArray = false;
        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENTIFIER);
        // System.out.println("O tipo e lexema eh: "+ currentToken.getType()+" e " + currentToken.getLexeme() + " e value: "+ currentToken.value());
        var symbol = symbolTable.resolve(currentToken.value());
        if (checkToken (LBRACKET)) {
            expectPeek(LBRACKET);
            parseExpression();
            vmWriter.writePush(kind2Segment(symbol.kind()), symbol.index());
            vmWriter.writeArithmetic(Command.ADD);

            expectPeek(RBRACKET);
            isArray = true;
        }
        expectPeek(EQ);
        parseExpression();

        if (isArray) {

            vmWriter.writePop(Segment.TEMP, 0);    // push result back onto stack
            vmWriter.writePop(Segment.POINTER, 1); // pop address pointer into pointer 1
            vmWriter.writePush(Segment.TEMP, 0);   // push result back onto stack
            vmWriter.writePop(Segment.THAT, 0);    // Store right hand side evaluation in THAT 0.
    

        } else {
            vmWriter.writePop(kind2Segment(symbol.kind()), symbol.index());
        }


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
            var op = peekToken.getType();
            expectPeek(peekToken.getType());
            parserTerm();
            compileOperators(op);
        }
        printNonTerminal("/expression");
    }

    void compileOperators(TokenSubTypes type) {

        if (type == ASTERISK) {
            vmWriter.writeCall("Math.multiply", 2);
        } else if (type == SLASH) {
            vmWriter.writeCall("Math.divide", 2);
        } else {
            vmWriter.writeArithmetic(typeOperator(type));
        }
    }


    // identifier
    void parseIdentifier(){
        // System.out.println("O tipo e lexema eh: "+ currentToken.getType()+" e " + currentToken.getLexeme() + " e value: "+ currentToken.value());
        Symbol sym = symbolTable.resolve(currentToken.value());
        // System.out.println(sym);
        if (checkToken(LPAREN) || checkToken(DOT)) {
            parseSubroutineCall();
        } else {
            if (checkToken(LBRACKET)){
                expectPeek(LBRACKET);
                parseExpression();
                System.out.println("Entrei aqui parseIdentifier");
                vmWriter.writePush(kind2Segment(sym.kind()), sym.index());
                vmWriter.writeArithmetic(Command.ADD);

                expectPeek(RBRACKET);
                vmWriter.writePop(Segment.POINTER, 1); // pop address pointer into pointer 1
                vmWriter.writePush(Segment.THAT, 0);
            } else{
            // System.out.println("Entrei aqui parseIdentifier else");
            System.out.println(sym +" - " +currentToken.value());
                vmWriter.writePush(kind2Segment(sym.kind()), sym.index());
            }
        }
    };
    // integerConstant | stringConstant | keywordConstant | varName | varName '[' expression ']' | subroutineCall | '(' expression ')' | unaryOp term
    void parserTerm () {
        printNonTerminal("term");
        switch (peekToken.getType()) {
            case NUMBER:
                expectPeek(NUMBER);
                // System.out.println("O tipo e lexema eh: "+ currentToken.getType()+" e " + currentToken.getLexeme() + " e value: "+ currentToken.value());
                vmWriter.writePush(Segment.CONST, Integer.parseInt(currentToken.value()));
                break;
            case IDENTIFIER:
                expectPeek(IDENTIFIER);
                parseIdentifier();
                break;
            case STRING:
                expectPeek(STRING);
                var strValue = currentToken.value();
                vmWriter.writePush(Segment.CONST, strValue.length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < strValue.length(); i++) {
                    vmWriter.writePush(Segment.CONST, strValue.charAt(i));
                    vmWriter.writeCall("String.appendChar", 2);
                }
                break;
            case THIS:
                expectPeek(THIS);
                vmWriter.writePush(Segment.POINTER, 0);
                break;
            case FALSE:
            case NULL:
            case TRUE:
                expectPeek(FALSE,NULL,TRUE);
                vmWriter.writePush(Segment.CONST, 0);
                if (currentToken.getType() == TRUE)
                    vmWriter.writeArithmetic(Command.NOT);
                break;
            case MINUS:
            case NOT:
                expectPeek(MINUS, NOT);
                var op = currentToken.getType();
                parserTerm();
                if (op == MINUS)
                    vmWriter.writeArithmetic(Command.NEG);
                else
                    vmWriter.writeArithmetic(Command.NOT);

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

    public String VMOutput() {
        return vmWriter.vmOutput();
    }

    private Segment kind2Segment(Kind kind) {
        if (kind == Kind.STATIC)
            return Segment.STATIC;
        if (kind == Kind.FIELD)
            return Segment.THIS;
        if (kind == Kind.VAR)
            return Segment.LOCAL;
        if (kind == Kind.ARG)
            return Segment.ARG;
        return null;
    }

    private Command typeOperator(TokenSubTypes type) {
        if (type == PLUS)
            return Command.ADD;
        if (type == MINUS)
            return Command.SUB;
        if (type == LT)
            return Command.LT;
        if (type == GT)
            return Command.GT;
        if (type == EQ)
            return Command.EQ;
        if (type == AND)
            return Command.AND;
        if (type == OR)
            return Command.OR;
        return null;
    }



  
}
