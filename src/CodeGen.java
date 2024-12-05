import java.util.ArrayList;
import java.util.HashMap;

public class CodeGen {

    // Local test for code generation
    public static void main(String[] args) {
        ArrayList<Atom> testAtoms = new ArrayList<>();
        
        testAtoms.add(new Atom(Atom.Operation.MOV, "4", "g"));
        testAtoms.add(new Atom(Atom.Operation.LBL, "LBL0"));
        testAtoms.add(new Atom(Atom.Operation.TST, "g", "2", "LBL1", 2));
        testAtoms.add(new Atom(Atom.Operation.ADD, "4", "2", "h"));
        testAtoms.add(new Atom(Atom.Operation.MUL, "4", "4", "j"));
        testAtoms.add(new Atom(Atom.Operation.DIV, "30", "5", "k"));
        testAtoms.add(new Atom(Atom.Operation.SUB, "4", "2", "l"));
        
        CodeGen.generate(testAtoms);
    }

    static int currentAtom = 0; // Current place in atoms
    static ArrayList<Code> code = new ArrayList<>(); // Return this
    static ArrayList<Atom> atoms = new ArrayList<>(); // Input
    static ArrayList<String> vars = new ArrayList<>(); // Register numbers with variable names

    //program counter with values 100, 104, etc, incremented for each instruction minus lbl jmp, ?
    static int programCounter = 0;
    
    static int currLBL = 0;                                       //to track next avail instance of LBL
    static HashMap<String,String> labelTable = new HashMap<>();  // Stores labels and values

    public static ArrayList<Code> generate(ArrayList<Atom> insertedAtoms) {
        atoms = insertedAtoms;
        parseCode();
        return code;
    }

    public static void parseCode(){
        while(hasMoreAtoms())
            parseAtom();
        code.add(new Code(Code.Operation.HLT.ordinal())); // Add the HALT instruction at the end
    }

    public static boolean hasMoreAtoms(){
        return currentAtom < atoms.size();
    }

    public static void parseAtom(){
        Atom curr = getCurrentAtom();
        switch(curr.checkOperator()){
            case "ADD" -> parseADD(curr);
            case "SUB" -> parseSUB(curr);
            case "MUL" -> parseMUL(curr);
            case "DIV" -> parseDIV(curr);
            case "JMP" -> parseJMP(curr);
            case "LBL" -> parseLBL(curr);
            case "TST" -> parseTST(curr);
            case "MOV" -> parseMOV(curr);
            default -> throw new RuntimeException("Invalid operation: " + curr.checkOperator());
        }
        advance(); // Move to the next atom
    }

    public static Atom getCurrentAtom(){
        return atoms.get(currentAtom);
    }

    public static void advance(){
        currentAtom++;
    }

    public static void parseADD(Atom current){ // ~ Brandon
        programCounter ++;
        int data = parseReg(current.checkRight());
        int reg = parseReg(current.checkResult());
        Code newInstruction = new Code(Code.Operation.ADD.ordinal(), reg, data);
        code.add(newInstruction);
    }

    public static void parseSUB(Atom current) { // ~ Steven
        programCounter ++;

        int data = parseReg(current.checkRight());
        int reg = parseReg(current.checkResult());
        Code newInstruction = new Code(Code.Operation.SUB.ordinal(), reg, data);
        code.add(newInstruction);
    }

    public static void parseMUL(Atom current){ // ~ Steven
        programCounter ++;

        int data = parseReg(current.checkRight());
        int reg = parseReg(current.checkResult());
        Code newInstruction = new Code(Code.Operation.MUL.ordinal(), reg, data);
        code.add(newInstruction);        
    }

    public static void parseDIV(Atom current){ // ~ Tucker
        programCounter ++;

        int data = parseReg(current.checkRight());
        int reg = parseReg(current.checkResult());
        Code newInstruction = new Code(Code.Operation.DIV.ordinal(), reg, data);
        code.add(newInstruction);
    }

    public static void parseJMP(Atom current){
        programCounter ++; 
        /* in case 
        //JMP 110
        //LBL 111
        //EXAMPLE 111
        //, we want pc to send us to following instruction AFTER the JMP, 
        so we must increment pc here
        */

        int data = parseReg(current.checkRight()); // Destination
        Code newInstruction = new Code(Code.Operation.JMP.ordinal(), data); // Make the instruction
        code.add(newInstruction);
    }

    public static void parseLBL(Atom current){ // ~ Luke

        //get next available location
        String label = "LBL" + currLBL++;
        String location = Integer.toBinaryString(programCounter ); // does not increment, holds place for next instruction

        labelTable.put(label, location);

        System.out.println("Updated Label Table: " + label + " at location: " + labelTable.get(label)); //confirmation
        
    }

    public static void parseTST(Atom current){
        programCounter ++;

        int data = parseReg(current.checkRight());
        int cmp = parseReg(current.checkComparator());
        int reg = parseReg(current.checkResult());
        Code newInstruction = new Code(Code.Operation.CMP.ordinal(), cmp, reg, data);
        code.add(new Code(Code.Operation.LOD.ordinal(), reg, parseReg(current.checkLeft())));
        code.add(newInstruction);

    }

    public static void parseMOV(Atom current){ // ~ Brandon
        programCounter ++;

        int data = parseReg(current.checkRight());
        int reg = parseReg(current.checkResult());
        Code newInstruction = new Code(Code.Operation.LOD.ordinal(), reg, data);
        code.add(newInstruction);
    }

    public static int parseReg(String reg){

        // First, check if it is a variable or a literal
        try {
            return Integer.parseInt(reg);
        } catch (NumberFormatException e) {}

        // Second, check if the variable name already has an associated register
        if(vars.contains(reg)){
            return vars.indexOf(reg) - 1;
        } else if (vars.size() != 16){
            vars.add(reg);
            return vars.indexOf(reg);
        } else {
            // If not, check if there are any available registers
            throw new RuntimeException("No available registers");
        }
    }
}
