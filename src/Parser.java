import java.util.ArrayList;
import java.util.Set;

public class Parser extends Token{
  public static void main(String[] args) {
    tokens.add(new Token(TokenType.KEYWORD, "if"));
    tokens.add(new Token(TokenType.OPEN_PARENTHESIS, "("));
    tokens.add(new Token(TokenType.LITERAL, "1"));
    tokens.add(new Token(TokenType.OPERATOR, "<"));
    tokens.add(new Token(TokenType.LITERAL, "2"));
    tokens.add(new Token(TokenType.CLOSE_PARENTHESIS, ")"));
    tokens.add(new Token(TokenType.OPEN_BRACKET, "{"));
    tokens.add(new Token(TokenType.LITERAL, "3"));
    tokens.add(new Token(TokenType.OPERATOR, "+"));
    tokens.add(new Token(TokenType.LITERAL, "4"));
    tokens.add(new Token(TokenType.CLOSE_BRACKET, "}"));
    parse(tokens);
  }
  
  private static int LABEL_INDEX = 0;
  private static int TEMP_INDEX = 0;
  private static int currentIndex = 0;
  private static int openBrackets = 0;
  private static int openParen = 0;
  private static final ArrayList<Atom> atoms = new ArrayList<>();
  private static ArrayList<Token> tokens = new ArrayList<>();

  public static ArrayList<Atom> parse(ArrayList<Token> insertedTokens){
    tokens = insertedTokens;
    parseProgram();
    return atoms;
  }

  // Helper method to check if there are more tokens to parse ~ Creek
  private static boolean hasMoreTokens(){
    return currentIndex < tokens.size();
  } 

  // Helper method to get current token ~ Creek
  private static Token getCurrentToken(){
    return hasMoreTokens() ? tokens.get(currentIndex) : null;
  }

  // Helper method to accept a token with it's value and advance to next token
  private static boolean accept(Token.TokenType type, String value){
    if(getCurrentToken().type.equals(type) && getCurrentToken().value.equals(value)){
      advance();
      return true;
    } else
      return false;
  }

  // Helper method to accept a token without it's value and advance to next token
  private static boolean accept(Token.TokenType type){
    if(getCurrentToken().type.equals(type)){
      advance();
      return true;
    } else
      return false;
  }

  // Helper method to assert a token with a specific value
  private static void expect(Token.TokenType type, String value){
    if(!accept(type, value))
      throw new RuntimeException("Unexpected token: " + getCurrentToken().getTokenType() + " with value: " + getCurrentToken().value + " expected: " + value);
  }
 
  // Helper method to assert a token without it's value
  private static void expect(Token.TokenType type){
    if(!accept(type))
      throw new RuntimeException("Unexpected token: " + getCurrentToken().getTokenType() + " with value: " + getCurrentToken().value + " expected: " + type);
  }

  // Helper method to move to the next token 
  private static Token advance(){
    return tokens.get(currentIndex++);
  }

  // Helper method to peek at the next token without advancing
  private static Token peek(){
    try {
        if(hasMoreTokens())
        {
          tokens.get(currentIndex + 1);
          return hasMoreTokens() ? tokens.get(currentIndex + 1) : null;

        }
    } catch (IndexOutOfBoundsException e) {
      throw new Error("Missing brackets in conditional statement.");
      
    }
        return null;
    }

  // private static Token peek(){
  //   return hasMoreTokens() ? tokens.get(currentIndex + 1) : null;
  // }


  // Helper method to parse brackets, keeping track of open brackets vs closed brackets
 


  private static void parseBrackets(){
    expect(TokenType.OPEN_BRACKET, "{");
    openBrackets++;
    while(openBrackets != 0){
      if(accept(TokenType.OPEN_BRACKET))
        openBrackets++;
      else if(accept(TokenType.CLOSE_BRACKET))
        openBrackets--;
      else 
        parseProgram();
    }
  }

  
  // Helper method for parsing parenthesis for conditionals/expressions
  private static void parseParenthesis(String type){
    expect(TokenType.OPEN_PARENTHESIS, "(");
    openParen++;
    while(openParen != 0){
      if(accept(TokenType.OPEN_PARENTHESIS))
        openParen++;
      else if(accept(TokenType.CLOSE_PARENTHESIS))
        openParen--;
      else {
        switch(type){
          case "condition" -> parseCondition(0); 
          case "expression" -> parseExpression(); 
          case "for" -> {
              parseInitialization();
              parseCondition(0);
              expect(TokenType.SEMICOLON, ";");
              parseUpdate();
          }   
          case "update" -> parseUpdate();
        }
      }
    }
  }

// Sets of available Keywords for types and operators / comparators ~ Creek
private static final Set<String> ASSIGNMENT_OPERATORS = Set.of("=", "+=", "-=");
private static final Set<String> COMPARATORS = Set.of("==", "<", ">", "<=", ">=", "!=");
private static final Set<String> ARITHMETIC_OPERATORS = Set.of("++", "+", "--", "-", "*", "/", "%");
private static final Set<String> TYPES = Set.of("int", "float");

// Helper method to check if it is a type ~ Creek
private static boolean isType(Token token) {
  return token != null && token.getTokenType() == TokenType.KEYWORD && TYPES.contains(token.value);
}

// Helper method to check if it is an identifier ~ Creek
private static boolean isIdentifier(Token token) {
  return token != null && token.getTokenType() == TokenType.IDENTIFIER;
}

// Helper method to check if it is an operand ~ Creek
private static boolean isOperand(Token token) {
  return token != null && (token.getTokenType() == TokenType.LITERAL || token.getTokenType() == TokenType.IDENTIFIER);
}

// Helper method to check if it is a comparator ~ Creek
private static boolean isComparator(Token token) {
  return token != null && token.getTokenType() == TokenType.OPERATOR && COMPARATORS.contains(token.value);
}

// Helper method to check if it is an operator ~ Creek
private static boolean isOperator(Token token) {
  return token != null && token.getTokenType() == TokenType.OPERATOR && ARITHMETIC_OPERATORS.contains(token.value);
}


  // used to convert string "<=" to its corresponding integer representation
  public static int getComparatorInteger(String comparator)
  {
    return switch(comparator){
      case "==" ->  1; 
      case "<" ->   2;
      case ">" ->   3;
      case "<=" ->  4;
      case ">=" ->  5;
      case "!=" ->  6;
      default -> throw new IllegalArgumentException("Unexpected comparator: " + comparator);
    };

  }


  /*
   * The following methods are used to recursively parse the given tokens; starting with the 
   * highest abstract level: parseProgram() until the code is fully translated into simple atoms.
   */

  // Parse program is the highest level of abstraction, it will parse the entire program ~ Brandon
  private static void parseProgram(){
    for(Atom atom : atoms)
      System.out.println(atom.toString());
    parseStatement();
    if(hasMoreTokens())
      parseProgram();
  }

  // Parse statement is the second highest level of abstraction, it will parse a single statement ~ Brandon
  private static void parseStatement(){
    if(accept(TokenType.KEYWORD, "if"))
      parseIf();
    else if(accept(TokenType.KEYWORD, "while"))
      parseWhile();
    else if(accept(TokenType.KEYWORD, "for"))
      parseFor();
    else if( getCurrentToken().getTokenType() == TokenType.KEYWORD && (getCurrentToken().value.equals("int") || getCurrentToken().value.equals("float"))){
      parseInitialization();
    }else if(getCurrentToken().getTokenType().equals(TokenType.LITERAL) || getCurrentToken().getTokenType().equals(TokenType.IDENTIFIER)){
      parseExpression();
    }else if(accept(TokenType.CLOSE_BRACKET))
      openBrackets--;
    else
      throw new RuntimeException("Unexpected token: " + getCurrentToken().getTokenType() + " with value: " + getCurrentToken().value);
  }

  // Method to parse if statements ~ Brandon
  private static void parseIf(){
    parseParenthesis("condition");
    parseBrackets();
    atoms.add(new Atom(Atom.Operation.LBL,"LBL"+LABEL_INDEX++));
  }

  // Method to parse while statements ~ Brandon
  private static ArrayList<Atom> parseWhile(){
    parseParenthesis("condition");
    parseBrackets();
    atoms.add(new Atom(Atom.Operation.LBL,"LBL"+LABEL_INDEX++));
    return atoms;
  }

  // Method to parse for statements ~ Brandon
  private static void parseFor(){
    parseParenthesis("for");
    parseBrackets();
    atoms.add(new Atom(Atom.Operation.LBL,"LBL"+LABEL_INDEX++));
  }

  // Method to parse expressions ~ Creek
  private static void parseExpression(){
    if(getCurrentToken().value.equals("=") || getCurrentToken().value.equals("+=") || getCurrentToken().value.equals("-=")){
      parseAssignment();
    } else if(getCurrentToken().type.equals(TokenType.OPERATOR) && (getCurrentToken().value.equals("++") || getCurrentToken().value.equals("--"))){
      parseUpdate();
    } else {
      if(getCurrentToken().getTokenType().equals(TokenType.OPERATOR))
        parseOperator();
      if(getCurrentToken().getTokenType().equals(TokenType.OPEN_PARENTHESIS)){
        parseParenthesis("expression");
      } else
        parseOperand();
    }
  }

  // Method to parse assignments into atoms
  private static void parseAssignment(){

    Atom assignment;

    // Use expect Identifier because it should not be a literal
    String identifier = getCurrentToken().value;
    expect(TokenType.IDENTIFIER);

    String operator = parseOperator();
    String value;

    switch(operator){
      case "=" -> {
        value = parseOperand();
        assignment = new Atom(Atom.Operation.MOV, value, identifier);
        atoms.add(assignment);
      }
      case "+=", "-=" -> {
        value = parseOperand();
        assignment = new Atom(operator.equals("+=") ? Atom.Operation.ADD : Atom.Operation.SUB, identifier, value, identifier);
        atoms.add(assignment);
      }
    }

    if(!peek().value.equals(")"))
      expect(TokenType.SEMICOLON, ";");
  }

  // Method to parse initializations ~ Creek
  private static void parseInitialization(){
    // Parse the type
    Atom init;
    String type = parseType();

    // Parse the identifier (result of the initialization)
    String identifier = parseOperand();

    // Append a decimal if float and not already present
    if(type.equals("float") && !identifier.contains("."))
      identifier += ".0";

    // Parse the assignment operator
    expect(TokenType.OPERATOR, "=");

    // Parse the left hand side of the assignment
    String value = parseOperand();

    expect(TokenType.SEMICOLON, ";");

    init = new Atom(Atom.Operation.MOV, value, identifier);
    atoms.add(init);
  }

  // Method to parse conditional statements - Tucker
  private static String parseCondition(int recursion){
      String left = parseOperand();
      int cmp = getComparatorInteger(parseComparator());
      String right = parseOperand();
        
      /*
       * need to track conditionals with LBLs so we know locations of conditions being compared in recursive calls
       */
    
      // peek to check for next possible condition: && or ||
      if (getCurrentToken().type.equals(TokenType.OPERATOR) || recursion != 0){
        Atom temp = new Atom(Atom.Operation.TST, left, right, "t" + TEMP_INDEX++);
        temp.setCMP(cmp);
        atoms.add(temp);

        // The OR case
        if(accept(Token.TokenType.OPERATOR, "||")){
          atoms.add(new Atom(Atom.Operation.NEG, temp.checkResult(), temp.checkResult()));
          String rightCond = parseCondition(1);
          atoms.add(new Atom(Atom.Operation.NEG, rightCond, rightCond));
          atoms.add(new Atom(Atom.Operation.TST, temp.checkResult(), rightCond, "LBL"+LABEL_INDEX, 6)); // negation !(!x && !y) == x || y
          return "";
        // The AND case
        } else if(accept(Token.TokenType.OPERATOR, "&&")){
          atoms.add(new Atom(Atom.Operation.TST, temp.checkResult(), parseCondition(1), "LBL"+LABEL_INDEX, 1));
        // Finished parsing, add the test atom
        } else {
          
        }
      } else
        atoms.add(new Atom(Atom.Operation.TST, left, right, "LBL"+LABEL_INDEX, cmp));
  
      return "t" + TEMP_INDEX;
    }

  // Method to parse comparators
  private static String parseComparator(){
    Token token = getCurrentToken();
    if (token != null && token.getTokenType() == TokenType.OPERATOR && COMPARATORS.contains(token.value)) {
      advance();
      return token.value;
    }
    throw new RuntimeException();
  }

  // Method to parse operators
  private static String parseOperator(){
      String operator = getCurrentToken().value;
      expect(TokenType.OPERATOR);
      return operator;
  }

  // Method to parse operands ~ Creek
  private static String parseOperand(){
    String value = getCurrentToken().value;
    if(accept(TokenType.LITERAL)){}
    else{expect(TokenType.IDENTIFIER);}
    return value;   
  }

  // Method to parse updates (ie. i++, i--) ~ Creek
  private static void parseUpdate(){
    Atom update;

    if(peek().value.equals("++"))
      update = new Atom(Atom.Operation.ADD, getCurrentToken().value, "1", getCurrentToken().value);
    else
      update = new Atom(Atom.Operation.SUB, getCurrentToken().value, "1", getCurrentToken().value);
    
    advance();
    advance();
    if(!getCurrentToken().value.equals(")"))
      expect(Token.TokenType.SEMICOLON);

    atoms.add(update);
  }

  // Method to parse the types of variables in initializations
  private static String parseType(){
    String value = getCurrentToken().value;
    if(accept(TokenType.KEYWORD, "int")){}
    else{expect(TokenType.KEYWORD, "float");}
    return value;
  }
}