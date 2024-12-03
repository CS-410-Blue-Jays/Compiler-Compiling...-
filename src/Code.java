public class Code {
    // Fields for the Code class
    private final int operation; // Operation code (0-9)
    private final int compare; // Compare code (1-6)
    private final int reg; // Register code (hexadecimal)
    private final int data; // Data code (decimal memory address)

    // Possible operations for the Mini architecture (In-order: 0-9)
    public enum Operation {
        CLR, // Clear Floating-Point Register
        ADD, // Add Floating-Point Registers
        SUB, // Subtract Floating-Point Registers
        MUL, // Multiply Floating-Point Registers
        DIV, // Divide Floating-Point Registers
        JMP, // Conditional Branch
        CMP, // Compare, Set Flag
        LOD, // Load Floating-Point Register
        STO, // Store Floating-Point Register
        HLT  // Halt Processor
    }

    // Need to return a string representation of the Code object
    @Override
    public String toString() {
        return operation + "" + compare + "" + Integer.toHexString(reg)+ "" + addPadding(data);
    }

    // Constructor for the CLR, ADD, SUB, MUL, DIV, LOD, STO instructions
    public Code(int Operation, int Comparator, int Register, int Data){
        this.operation = Operation;
        this.compare = Comparator;
        this.reg = Register;
        this.data = Data;
    }

    // Constructor for the JMP instruction
    public Code(Operation Instruction, int Data){
        this.operation = getOperation(Instruction);
        this.compare = this.reg = 0; // No compare or register to store
        this.data = Data; 
    }

    // Constructor for the HLT instruction
    public Code(Operation Instruction){
        this.operation = getOperation(Instruction);
        this.compare = this.reg = 0; // No compare or register to store
        this.data = 00000; // No data to store
    }

    // Returns the operation from a passed Operation
    public final int getOperation(Operation op){
        return switch (op) {
            case CLR -> 0;
            case ADD -> 1;
            case SUB -> 2;
            case MUL -> 3;
            case DIV -> 4;
            case JMP -> 5;
            case CMP -> 6;
            case LOD -> 7;
            case STO -> 8;
            case HLT -> 9;
            default -> throw new IllegalArgumentException("Invalid instruction: " + op);
        };
    }

    // Add left-padding to the data until it has 5 places
    public final String addPadding(int num){
        return String.format("%05d", num);
    }
}
