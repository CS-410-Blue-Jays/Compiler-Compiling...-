
public class Main {
  public static void main(String[] args){

	// System.out.println("");

	// java.util.Scanner s = new java.util.Scanner(System.in);	//for prompting
        
	// System.out.print("Welcome: Execute Frontend? (Y or N):");	//prompt

	// String result = s.nextLine();  // Reads the entire line (string input)
	
	


	Frontend frontend = new Frontend();
	String atom_output_fileName = frontend.executeFrontend();
	
	Backend backend = new Backend(atom_output_fileName);
	backend.main(args);

	// s.close();
	} // End of main
} // End of class
