import java.io.FileNotFoundException;
import java.io.IOException;

public class CommandUI {

    static java.util.Scanner prompt = new java.util.Scanner(System.in);

    public void execute()
    {
        String default_file_for_backend;
        String default_file_for_VM;
        ASCII_ART();
        startPrompt();
        runFrontend();
        // default_file_for_VM = runBackend(default_file_for_backend);
        runVM("default_file_for_VM");

    } 
    
    public String startPrompt()
    {
        System.out.println("Would you like to execute the frontend?(Y/N)");
        String response = prompt.next().toUpperCase();
        // prompt.close();
        if(response.equals("Y"))
        {
            System.out.println("Received: " + response);
            runFrontend();
            System.out.println("Frontend has terminated");
            // return output;
        }
        
        System.out.println("The application will close now" );
        System.exit(0);
        return null;

    }

    public String backendPrompt(String defaultFile)
    {
        String response;

        System.out.println("Would you like to execute the backend?(Y/N)");
        response = prompt.nextLine();
        System.err.println("received");
        runBackend(defaultFile);

        return "";
    }
    
    public String runFrontend()
    {
        Frontend frontend = new Frontend();
	    String atom_output_fileName = frontend.execute();
        System.out.println("Frontend done executing, results have been outputted to " + atom_output_fileName);
        return atom_output_fileName;
    }
    
    public String runBackend(String atom_output_fileName)
    {
        Backend backend = new Backend(atom_output_fileName);
        backend.setFileName(atom_output_fileName);	//possibly prompt user for this...
        String codeGen_output_file = backend.execute();
        return codeGen_output_file;
    }

    public void runVM(String defaultFile)
    {
          // Attempt to execute the MiniVM
        System.out.println("\nExecuting MiniVM...");
        try {
            MiniVM vm = new MiniVM(defaultFile);
            vm.execute(true, false);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error executing MiniVM: " + e.getMessage());
        }

        System.exit(1);
    }
    
    
    
    
    
    /**
     * Runs the command line interface ACII ART for our application
     */
    public void ASCII_ART()
    {
        System.out.println("\n\n\n\n");
        System.out.println( " ▄▄▄▄    ██▓     █    ██ ▓█████     ▄▄▄██▀▀▀▄▄▄     ▓██   ██▓  ██████ \n"  +
                            " ▓█████▄ ▓██▒    ██  ▓██▒▓█   ▀       ▒██  ▒████▄    ▒██  ██▒▒██    ▒\n"+
                            " ▒██▒ ▄██▒██░    ▓██  ▒██░▒███         ░██  ▒██  ▀█▄   ▒██ ██░░ ▓██▄   \n"+
                            " ▒██░█▀  ▒██░    ▓▓█  ░██░▒▓█  ▄    ▓██▄██▓ ░██▄▄▄▄██  ░ ▐██▓░  ▒   ██▒\n"+
                            " ░▓█  ▀█▓░██████▒▒▒█████▓ ░▒████▒    ▓███▒   ▓█   ▓██▒ ░ ██▒▓░▒██████▒▒\n"+
                            " ░▒▓███▀▒░ ▒░▓  ░░▒▓▒ ▒ ▒ ░░ ▒░ ░    ▒▓▒▒░   ▒▒   ▓▒█░  ██▒▒▒ ▒ ▒▓▒ ▒ ░\n"+
                            " ▒░▒   ░ ░ ░ ▒  ░░░▒░ ░ ░  ░ ░  ░    ▒ ░▒░    ▒   ▒▒ ░▓██ ░▒░ ░ ░▒  ░ ░\n"+
                            "  ░    ░   ░ ░    ░░░ ░ ░    ░       ░ ░ ░    ░   ▒   ▒ ▒ ░░  ░  ░  ░  \n"+
                            " ░          ░  ░   ░        ░  ░    ░   ░        ░  ░░ ░           ░  \n"+
                            "      ░                                              ░ ░              \n"+
                            " ▄████▄   ▒█████   ███▄ ▄███▓ ███▄ ▄███▓ ▄▄▄       ███▄    █ ▓█████▄  \n"+
                            "▒██▀ ▀█  ▒██▒  ██▒▓██▒▀█▀ ██▒▓██▒▀█▀ ██▒▒████▄     ██ ▀█   █ ▒██▀ ██▌ \n"+
                            "▒▓█    ▄ ▒██░  ██▒▓██    ▓██░▓██    ▓██░▒██  ▀█▄  ▓██  ▀█ ██▒░██   █▌ \n"+
                            "▒▓▓▄ ▄██▒▒██   ██░▒██    ▒██ ▒██    ▒██ ░██▄▄▄▄██ ▓██▒  ▐▌██▒░▓█▄   ▌ \n"+
                            "▒ ▓███▀ ░░ ████▓▒░▒██▒   ░██▒▒██▒   ░██▒ ▓█   ▓██▒▒██░   ▓██░░▒████▓  \n"+
                            "░ ░▒ ▒  ░░ ▒░▒░▒░ ░ ▒░   ░  ░░ ▒░   ░  ░ ▒▒   ▓▒█░░ ▒░   ▒ ▒  ▒▒▓  ▒  \n"+
                            "  ░  ▒     ░ ▒ ▒░ ░  ░      ░░  ░      ░  ▒   ▒▒ ░░ ░░   ░ ▒░ ░ ▒  ▒  \n"+
                            "░        ░ ░ ░ ▒  ░      ░   ░      ░     ░   ▒      ░   ░ ░  ░ ░  ░  \n"+
                            "░ ░          ░ ░         ░          ░         ░  ░         ░    ░     \n"+
                            "░                                                             ░       \n"+
                            "                    ██▓     ██▓ ███▄    █ ▓█████                      \n"+
                            "                   ▓██▒    ▓██▒ ██ ▀█   █ ▓█   ▀                      \n"+
                            "                  ▒██░    ▒██▒▓██  ▀█ ██▒▒███                         \n"+
                            "                  ▒██░    ░██░▓██▒  ▐▌██▒▒▓█  ▄                       \n"+
                            "                   ░██████▒░██░▒██░   ▓██░░▒████▒                     \n"+
                            "                   ░ ▒░▓  ░░▓  ░ ▒░   ▒ ▒ ░░ ▒░ ░                     \n"+
                            "                   ░ ░ ▒  ░ ▒ ░░ ░░   ░ ▒░ ░ ░  ░                     \n"+
                            "                                                                         "
        );
        System.out.println("""



                           Welcome to the CS-410 Blue Jays Compiler.
                           
                           You will be asked to run a frontend, backend, and mini Virtual Machine.
                           
                           Before each run, you will be asked to input the name of a file you would like to test.
                           
                           Please refer to the default file, your file directory, or previous file named in the command line for this.
                           
                           For all other inquiries, please answer in \"Y\" for YES and \"N\" for NO
                           
                           Thank You



                           """ );

    }
}
