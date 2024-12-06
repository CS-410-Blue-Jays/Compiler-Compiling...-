public class Code {

    private final long code; // The full code (the mini will implement this in 32 bits)

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
        return Long.toString(code);
    }

    public String toBinaryString(){
        return Long.toBinaryString(code);
    }

    // Constructor for the CMP instruction
    public Code(int Operation, int Comparator, int Register, int Data){
        this.code = Operation * 10000000 + Comparator * 1000000 + Register * 100000 + Data;
    }

    // Constructor for the CLR, ADD, SUB, MUL, DIV, LOD, STO instructions
    public Code(int Operation, int Register, int Data){
        this.code = Operation * 10000000 + Register * 100000 + Data;
    }

    // Constructor for the JMP instruction
    public Code(int Operation, int Data){
        this.code = Operation * 10000000 + Data;
    }

    // Constructor for the HLT instruction
    public Code(int Operation){
        this.code = Operation * 10000000;
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

    public final String checkOperation(){
        return switch (Integer.parseInt(Long.toString(this.code)) / 10000000) {
            case 0 -> "CLR";
            case 1 -> "ADD";
            case 2 -> "SUB";
            case 3 -> "MUL";
            case 4 -> "DIV";
            case 5 -> "JMP";
            case 6 -> "CMP";
            case 7 -> "LOD";
            case 8 -> "STO";
            case 9 -> "HLT";
            default -> throw new IllegalArgumentException("Invalid instruction: " + this.code);
        };
    }

    // Add left-padding to the data until it has 5 places
    public final String addPadding(int num){
        return String.format("%05d", num);
    }
}
