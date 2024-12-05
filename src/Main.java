import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Main {
  public static void main(String[] args){
    System.out.println("Enter the name of the file you'd like to tokenize: ");
		String path = System.console().readLine();

		if(path.equals(""))
			path = "HelloWorld.txt"; // For testing purposes
		
		ArrayList<Token> tokens = new ArrayList<>();
		File newFile = new File(path);

		if (!newFile.exists())
			newFile = new File("src/" + path); // Automatically appends src if not in the right location

		// Read the file and tokenize it
		try (RandomAccessFile file = new RandomAccessFile(newFile.getAbsolutePath(), "r")) {
			System.out.println("Tokenizing file: '" + newFile.getName() + '\'');

			// Append each line to the total input
			String input = "";

			String line;
			while((line = file.readLine()) != null)
				input += line + '\n';
			tokens.addAll(Scanner.scan(input));
			file.close();
		} catch(IOException e){
			String error = e.getLocalizedMessage();
			System.out.println("Error reading file '"+ newFile.getName() + "': " + error.substring(error.indexOf('(') + 1, error.length()-1) + ", check the file path and try again.");
			System.exit(-1);
		}

		// Once all tokens are found, print them
		if(tokens.isEmpty())
			System.err.println("No tokens found! Try pointing to a different file.");
		else {
			for(Token tok : tokens)
				System.out.println(tok.toString());
		
		// Parse the tokens
		System.out.println("\nParsing tokens...");
		ArrayList<Atom> atoms = Parser.parse(tokens);
		for(Atom atom : atoms)
			System.out.println(atom.toString());

		// Generate Mini code
		System.out.println("\nGenerating Mini Architecture code...");
		CodeGen.generate(atoms);
		int loc = 0;
		System.out.println("Loc\tContents");
		for(Code code : CodeGen.code)
			System.out.println(loc++ + "\t" + code.toString() + "\t" + code.checkOperation());
		}
    }
}
