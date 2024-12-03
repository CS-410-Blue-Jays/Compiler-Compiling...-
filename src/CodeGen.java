import java.util.ArrayList;

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
    static int currentRegister = 0; // Current register number
    static ArrayList<Code> code = new ArrayList<>(); // Return this
    static ArrayList<Atom> atoms = new ArrayList<>(); // Input
    static ArrayList<String> vars = new ArrayList<>(); // Register numbers with variable names

    public static ArrayList<Code> generate(ArrayList<Atom> insertedAtoms) {
        atoms = insertedAtoms;
        parseCode();
        return code;
    }

    public static void parseCode(){
        while(hasMoreAtoms())
            parseAtom();
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
            case "NEG" -> parseNEG(curr);
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

    public static void parseADD(Atom current){

        // Do things here

    }

    public static void parseSUB(Atom current){

        System.out.println("SUB detected");
        // Do things here

    }

    public static void parseMUL(Atom current){

        System.out.println("MUL detected");
        // Do things here

    }

    public static void parseDIV(Atom current){

        System.out.println("DIV detected");
        // Do things here

    }

    public static void parseJMP(Atom current){

        System.out.println("JMP detected");
        // Do things here

    }

    public static void parseNEG(Atom current){

        System.out.println("NEG detected");
        // Do things here

    }

    public static void parseLBL(Atom current){

        System.out.println("LBL detected");
        // Do things here

    }

    public static void parseTST(Atom current){

        System.out.println("TST detected");
        // Do things here

    }

    public static void parseMOV(Atom current){

        System.out.println("MOV detected");
        // Do things here

    }

    public static int parseReg(String reg){

        // First, check if the variable name already has an associated register
        if(vars.contains(reg) && !vars.isEmpty()){
            return vars.indexOf(reg);
        } else {
            // If not, check if there are any available registers
            for(int i = 1; i < 15; i++){
                if(vars.get(i).equals("")){
                    vars.set(i, reg);
                    return i;
                }
            }
            throw new RuntimeException("No available registers");
        }
    }
}
