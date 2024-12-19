import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Frontend {
    
   
    /**
     * Runs everything from user input to atom file output
     * @return fileName of outputted atoms
     */
    public String execute()
    {
        
        System.out.println("HIT");

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

        path = " ";
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

        FileInputOutput fio = new FileInputOutput(); //init class, saves space in main.java

	    String atom_output_fileName = fio.atomOutput(atoms, fileName);	// atom output to file

        return atom_output_fileName;
    }
}
