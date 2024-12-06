public class Atom {
  private final Operation operation;
  private final String left;
  private final String right;
  private final String result;
  private int comparison;
  private final String destination;

  public enum Operation {
    ADD, SUB, MUL, DIV, JMP, NEG, LBL, TST, MOV
  }

  @Override
  public String toString() {
    return switch (operation) {
      case JMP -> "(" + operation + ", , , , " + destination + ")";
      case NEG -> "(" + operation + ", " + left + ", , " + result + ")";
      case LBL -> "(" + operation + ", , , , " + destination + ")";
      case TST -> "(" + operation + ", " + left + ", " + right + ", " + result + ", " + comparison + ", " + destination + ")";
      case ADD, SUB, MUL, DIV, MOV -> "(" + operation + ", " + left + ", " + right + ", " + result + ")";
    };
  }
  
  // Constructor for add/sub/mul/div operations
  // Example (SUB, "A", "B", "C") -> C = A - B
  public Atom(Operation operation, String left, String right, String result){
    this.operation = operation;
    this.left = left;
    this.right = right;
    this.result = result;
    this.destination = "";
    this.comparison = -1;
  }

  // Constructor for unconditional jump operations ( JMP / LBL )
  // Example (JMP, "dest") -> Jump to label or (LBL, "label") -> Label
  public Atom(Operation operation, String destination){
    this.operation = operation;
    this.left = "";
    this.right = "";
    this.result = "";
    this.destination = destination;
    this.comparison = -1;
  }

  // Constructor for condition test operationns ( TST )
  // Example (TST, "A", "B", "label", 1) -> If A == B, jump to label
  public Atom(Operation operation, String left, String right, String destination, int comparison){
    this.operation = operation;
    this.left = left;
    this.right = right;
    this.result = "";
    this.destination = destination;
    this.comparison = comparison;
  }

  // Constructor for mov operations ( MOV / NEG)
  // Example (MOV, "A", "B") -> A = B or (NEG, "A", "A") -> A = -A
  public Atom(Operation operation, String left, String result){
    this.operation = operation;
    this.left = left;
    this.right = "";
    this.result = result;
    this.destination = "";
    this.comparison = -1;
  }

  public void setCMP(int comparison){
    this.comparison = comparison;
  }

  // Checks the operation of the atom
  public String checkOperator(){
    return this.operation.toString();
  }

  // Checks the left operand of the atom
  public String checkLeft(){
    return this.left;
  }

  // Checks the right operand of the atom
  public String checkRight(){
    return this.right;
  }

  // Checks the result of the atom
  public String checkResult(){
    return this.result;
  }

  // Checks the comparison of the atom
  public String checkComparator(){
    return switch (this.comparison) {
      case 0 -> "Always";
      case 1 -> "Equal";
      case 2 -> "Lesser";
      case 3 -> "Greater";
      case 4 -> "LesserOrEqual";
      case 5 -> "GreaterOrEqual";
      case 6 -> "NotEqual";
      default -> "";
    };
  }

  public int checkComparatorNum(){
    return this.comparison;
  }

  // Checks the destination of the atom
  public String checkDestination(){
    return this.destination;
  }
}
