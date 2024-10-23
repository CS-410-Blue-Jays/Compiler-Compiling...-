public class Atom {
  private String operation;
  private String left;
  private String right;
  private String result;
  private String destination;
  private String comparison;
  
  // Constructor for add/mul/div/sub operations
  public Atom(String operation, String left, String right, String Result){
    this.operation = operation;
    this.left = left;
    this.right = right;
    this.result = result;
    this.destination = null;
    this.comparison = null;
  }
  // Constructor for jump
  public Atom(String operation, String destination){
    this.operation = operation;
    this.left = null;
    this.right = null;
    this.result = null;
    this.destination = destination;
    this.comparison = null;
  }
  // Constructor for condition test operationns ( TST )
  public Atom(String operation, String left, String right, String destination, String comparison){
    this.operation = operation;
    this.left = left;
    this.right = right;
    this.result = null;
    this.destination = destination;
    this.comparison = comparison;
  }


}
