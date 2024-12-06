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
  
  private static int LABEL_INDEX, TEMP_INDEX, currentIndex, OR_INDEX = 0;
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
    return hasMoreTokens() ? tokens.get(currentIndex + 1) : null;
  }

  // Helper method to parse brackets, keeping track of open brackets vs closed brackets
  private static void parseBrackets(){
    expect(TokenType.OPEN_BRACKET);
    parseProgram();
    expect(TokenType.CLOSE_BRACKET);
  }

  // Helper method for parsing parenthesis for conditionals/expressions
  private static void parseParenthesis(String type){
    expect(TokenType.OPEN_PARENTHESIS, "(");
    switch(type){
      case "condition" -> parseCondition();
      case "expression" -> parseExpression();
      case "for" -> {
          parseInitialization();
          atoms.add(new Atom(Atom.Operation.LBL, "LBL" + LABEL_INDEX++)); // Add the pre-for label
          parseCondition();
          expect(TokenType.SEMICOLON, ";"); // Semicolon after condition in for loop
          parseUpdate();
      }   
      case "update" -> parseUpdate();
    }
    expect(TokenType.CLOSE_PARENTHESIS, ")");
  }

// Sets of available Keywords for types and operators / comparators ~ Creek
@SuppressWarnings("unused")
private static final Set<String> ASSIGNMENT_OPERATORS = Set.of("=", "+=", "-=");
private static final Set<String> COMPARATORS = Set.of("==", "<", ">", "<=", ">=", "!=");
private static final Set<String> ARITHMETIC_OPERATORS = Set.of("++", "+", "--", "-", "*", "/", "%");
private static final Set<String> TYPES = Set.of("int", "float");

// Helper method to check if it is a type ~ Creek
@SuppressWarnings("unused")
private static boolean isType(Token token) {
  return token != null && token.getTokenType() == TokenType.KEYWORD && TYPES.contains(token.value);
}

// Helper method to check if it is an identifier ~ Creek
@SuppressWarnings("unused")
private static boolean isIdentifier(Token token) {
  return token != null && token.getTokenType() == TokenType.IDENTIFIER;
}

// Helper method to check if it is an operand ~ Creek
@SuppressWarnings("unused")
private static boolean isOperand(Token token) {
  return token != null && (token.getTokenType() == TokenType.LITERAL || token.getTokenType() == TokenType.IDENTIFIER);
}

// Helper method to check if it is a comparator ~ Creek
@SuppressWarnings("unused")
private static boolean isComparator(Token token) {
  return token != null && token.getTokenType() == TokenType.OPERATOR && COMPARATORS.contains(token.value);
}

// Helper method to check if it is an operator ~ Creek
@SuppressWarnings("unused")
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
    parseStatement();
    if(hasMoreTokens() && getCurrentToken().getTokenType() != TokenType.CLOSE_BRACKET)
      parseProgram();
  }

  // Parse statement is the second highest level of abstraction, it will parse a single statement ~ Brandon
  private static void parseStatement() {
    if(accept(TokenType.KEYWORD, "if"))
      parseIf();
    else if(accept(TokenType.KEYWORD, "while"))
      parseWhile();
    else if(accept(TokenType.KEYWORD, "for"))
      parseFor();
    else if(getCurrentToken().getTokenType() == TokenType.KEYWORD && 
    (getCurrentToken().value.equals("int") || getCurrentToken().value.equals("float"))){
      parseInitialization();
    }else if(getCurrentToken().getTokenType().equals(TokenType.LITERAL) || getCurrentToken().getTokenType().equals(TokenType.IDENTIFIER)){
      parseExpression();
    }else if(!getCurrentToken().getTokenType().equals(TokenType.CLOSE_BRACKET))
      throw new RuntimeException("Unexpected token: " + getCurrentToken().getTokenType() + " with value: " + getCurrentToken().value);
  }

  // Method to parse if statements ~ Brandon
  private static void parseIf(){
    Atom ifAtom = new Atom(Atom.Operation.LBL,"LBL"+LABEL_INDEX);
    parseParenthesis("condition");
    LABEL_INDEX++;
    parseBrackets();
    atoms.add(ifAtom);
  }

  // Method to parse while statements ~ Brandon
  private static void parseWhile(){
    Atom startLoop = new Atom(Atom.Operation.LBL,"LBL"+LABEL_INDEX++);
    Atom endLoop = new Atom(Atom.Operation.LBL,"LBL"+LABEL_INDEX);
    atoms.add(startLoop); // Add the pre-while label
    parseParenthesis("condition");
    LABEL_INDEX++;
    parseBrackets();
    atoms.add(new Atom(Atom.Operation.JMP, startLoop.checkDestination())); // Jump back to the pre-while label
    atoms.add(endLoop); // Add the post-while label
  }

  // Method to parse for statements ~ Brandon
  private static void parseFor(){
    String startLoop = "LBL"+LABEL_INDEX;
    parseParenthesis("for");
    Atom endLoop = new Atom(Atom.Operation.LBL,"LBL"+ LABEL_INDEX);
    LABEL_INDEX++;
    parseBrackets();
    atoms.add(new Atom(Atom.Operation.JMP, startLoop)); // Jump back to the pre-for label
    atoms.add(endLoop); // Add the post-for label
  }

  // Method to parse expressions ~ Creek
  private static void parseExpression(){
    if(peek().value.equals("=") || peek().value.equals("+=") || peek().value.equals("-=")){
      parseAssignment();
    } else if(peek().type.equals(TokenType.OPERATOR) && (peek().value.equals("++") || peek().value.equals("--"))){
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
    if(getCurrentToken().getTokenType().equals(TokenType.OPEN_PARENTHESIS)){
      parseParenthesis("expression");
      value = "t" + TEMP_INDEX++;
    } else
      value = parseOperand();

    // If there is another operator
    if(getCurrentToken().getTokenType().equals(TokenType.OPERATOR)){
      String nextOperator = parseOperator();
      String nextValue = parseOperand();
      switch(nextOperator){
        case "*" -> {
          assignment = new Atom(Atom.Operation.MUL, value, nextValue, identifier);
          atoms.add(assignment);
        }
        case "/" -> {
          assignment = new Atom(Atom.Operation.DIV, value, nextValue, identifier);
          atoms.add(assignment);
        }
        case "+" -> {
          assignment = new Atom(Atom.Operation.ADD, value, nextValue, identifier);
          atoms.add(assignment);
        }
        case "-" -> {
          assignment = new Atom(Atom.Operation.SUB, value, nextValue, identifier);
          atoms.add(assignment);
        }
      }
    } else {
      switch(operator){
        case "=" -> {
          assignment = new Atom(Atom.Operation.MOV, value, identifier);
          atoms.add(assignment);
        }
        case "+=", "-=" -> {
          assignment = new Atom(operator.equals("+=") ? Atom.Operation.ADD : Atom.Operation.SUB, identifier, value, identifier);
          atoms.add(assignment);
        }
      }
    }
    expect(TokenType.SEMICOLON);
  }

  // Method to parse initializations ~ Creek
  private static void parseInitialization(){
    // Parse the type
    String type = parseType();

    // Parse the identifier (result of the initialization)
    String identifier = parseOperand();

    // Parse the assignment operator
    expect(TokenType.OPERATOR, "=");

    // Parse the right hand side of the assignment
    String value = parseOperand();

    // Append a decimal if float and not already present
    if(type.equals("float") && !value.contains("."))
      value += ".0";

    expect(TokenType.SEMICOLON, ";");

    atoms.add(new Atom(Atom.Operation.MOV, value, identifier));
  }

  // Method to parse conditional statements - Tucker
  private static void parseCondition(){
    String left = parseOperand();
    int cmp = getComparatorInteger(parseComparator());
    cmp = 7 - cmp; // Invert the comparator
    String right = parseOperand();

    // If there are more tokens after the right operand (not closing parenthesis or semicolon), we are not finished
    if(!getCurrentToken().type.equals(TokenType.CLOSE_PARENTHESIS) && !getCurrentToken().type.equals(TokenType.SEMICOLON)){
      if(accept(TokenType.OPERATOR, "&&")){
        atoms.add(new Atom(Atom.Operation.TST, left, right, "LBL" + LABEL_INDEX, cmp)); // Add the current atom
        parseCondition(); // Recursive call
      } else {
        expect(TokenType.OPERATOR, "||");
        atoms.add(new Atom(Atom.Operation.TST, left, right, "OR_" + OR_INDEX, cmp)); // Add the current atom with new label
        atoms.add(new Atom(Atom.Operation.LBL, "OR_" + OR_INDEX++));
        parseCondition(); // Recursive call 
      }
    } else
      atoms.add(new Atom(Atom.Operation.TST, left, right, "LBL" + LABEL_INDEX, cmp)); // Add the current atom
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