package gitlet;

import java.io.IOException;

import static java.lang.System.exit;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        try {
            if (args.length == 0 || args == null) {
                System.out.println("Please enter a command.");
                exit(0);
            }
            String firstArg = args[0];
            switch (firstArg) {
                case "init":
                    // TODO: handle the `init` command
                    Repository.init();
                    break;
                case "add":
                    // TODO: handle the `add [filename]` command
                    Repository.add(args[1]);
                    break;
                // TODO: FILL THE REST IN
                case "commit":
                    if (args.length == 1) {
                        System.out.println("Please enter a commit message");
                        exit(0);
                    } else {
                        Repository.commit(args[1]);
                    }
                    break;
                case "rm":
                    Repository.rm(args[1]);
                default:
                    System.out.println("No command with that name exists.");
                    exit(0);
            }
        } catch (Exception e){
            System.exit(0);
        }
    }
}
