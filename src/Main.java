import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Main {
  public static void main(String[] args){
    System.out.println("Enter the name of the file you'd like to tokenize: ");
			// String path = System.console().readLine();
			String path = "src/HelloWorld.txt"; // For testing purposes
			ArrayList<Token> tokens = new ArrayList<>(); // Its definitely used, quiet down compiler

			File newFile = new File(path);

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
				System.out.println("No tokens found! Try pointing to a different file.");
			else
				for(Token tok : tokens)
					System.out.println(tok.toString());

			System.out.println("\nParsing tokens...");
			// Parse the tokens
			ArrayList<Atom> atoms = Parser.parse(tokens);
			for(Atom atom : atoms)
				System.out.println(atom.toString());

    }
}
