import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.swing.*;

public class Main {
  public static void main(String[] args){

	// Testing out Jframe
	JFrame codeCompiler = new JFrame();

	// File Chooser
	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/src/"));
	fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files", "c"));
	int returnValue = fileChooser.showOpenDialog(codeCompiler);
	String path = "";

	// If a file is selected, get the path
	if (returnValue == JFileChooser.APPROVE_OPTION)
		path = fileChooser.getSelectedFile().getAbsolutePath();
	
	ArrayList<Token> tokens = new ArrayList<>();
	File newFile = new File(path);
	String fileName = newFile.getName().substring(0, newFile.getName().lastIndexOf('.'));

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
	if(tokens.isEmpty()){
		System.err.println("No tokens found! Try pointing to a different file.");
		System.exit(1);
	}
	for(Token tok : tokens)
		System.out.println(tok.toString());

	// Parse the tokens
	System.out.println("\nParsing tokens...");
	ArrayList<Atom> atoms = Parser.parse(tokens);

	try (FileOutputStream fos = new FileOutputStream(fileName + "-output.atoms")) {

			for (Atom atom : atoms) {
				String output = "";
				output += atom.toString() + "\n";
				fos.write(output.getBytes(StandardCharsets.UTF_8));
			}
			System.out.println("\nResults have been written to '" + fileName + "-output.txt'");
	} catch (IOException e) {
			System.out.println("\nError writing to file: " + e.getMessage());
	}
	

	// Read the atom file
	System.out.println("\nReading resulting atoms from file...");

	atoms = new ArrayList<>(); // Flush the atoms

	// Parse the atoms from the file
	try (FileInputStream fis = new FileInputStream(fileName + "-output.atoms")) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = reader.readLine()) != null) {
					Atom atom = Atom.parseString(line);
					atoms.add(atom);
			}
	} catch (IOException e) {
		System.out.println("Error reading file: " + e.getMessage());
	}

	// Read the file and atomize it
	CodeGen.generate(atoms);

	int loc = 0;

	System.out.println("\nGenerating Mini Architecture code...");

	try (FileOutputStream fos = new FileOutputStream(fileName + "-output.txt")) {
		fos.write("Loc\tContents\t\tOP\n".getBytes()); // Write header to file

		for (Code code : CodeGen.code) {
			String output;
			if (!code.checkOperation().equals("HLT"))
				output = loc++ + "\t" + code.toString() + "\t\t" + code.checkOperation() + "\n";
			else
				output = loc++ + "\t" + code.toString() + "\t\t" + code.checkOperation() + "\n";
			fos.write(output.getBytes(StandardCharsets.UTF_8));
		}
	} catch (IOException e) {
		System.out.println("Error writing to file: " + e.getMessage());
	}
	
	System.out.println("\nLegible results have been written to '" + fileName + "-output.txt'");

	try(FileOutputStream file2 = new FileOutputStream(fileName + "-output.bin")) {
					for (Code code : CodeGen.code) {
						String binaryString = code.toBinaryString();

						//remove spaces in binary string
						binaryString = binaryString.replaceAll("\\s+", ""); 

						//convert binary string into bytes
						int length = binaryString.length();
						for(int i = 0; i < length; i+= 8){
							//extract 8 bits ( 1 byte ) per loop
							String byteString = binaryString.substring(i, Math.min(i + 8, length));

							//convert binary string to byte and write to .bin file
							byte b = (byte) Integer.parseInt(byteString, 2);
							file2.write(b);
						
					}
				}
				System.out.println("\nResults have been written to '" + fileName + "-output.bin' Hex editor needed to view content");
		} catch (IOException e) {
				System.out.println("Error writing to file: " + e.getMessage());
		}
		
	// Attempt to execute the MiniVM
	System.out.println("\nExecuting MiniVM...");
	try {
		MiniVM vm = new MiniVM(fileName + "-output.bin");
		vm.execute(true, true);
	} catch (FileNotFoundException e) {
		System.out.println("File not found: " + e.getMessage());
	} catch (IOException e) {
		System.out.println("I/O error: " + e.getMessage());
	} catch (Exception e) {
		System.out.println("Error executing MiniVM: " + e.getMessage());
	}

    } // End of main
} // End of class
