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
    public static void main(String[] args) {
        // TODO: what if args is empty?
        try {
            if (args == null || args.length == 0) {
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
                    if (args.length != 2) {
                        System.out.println("error");
                        exit(0);
                    }
                    Repository.add(args[1]);
                    break;
                // TODO: FILL THE REST IN
                case "commit":
                    if (args.length == 1 || args[1].isEmpty()) {
                        System.out.println("Please enter a commit message.");
                        exit(0);
                    } else {
                        Repository.commit(args[1]);
                    }
                    break;
                case "rm":
                    if (args.length != 2) {
                        System.out.println("error");
                        exit(0);
                    }
                    Repository.rm(args[1]);
                    break;
                case "log":
                    Repository.log();
                    break;
                case "global-log":
                    Repository.global_log();
                    break;
                case "find":
                    if (args.length != 2) {
                        System.out.println("error");
                        exit(0);
                    }
                    Repository.find(args[1]);
                    break;
                case "status":
                    Repository.status();
                    break;
                case "checkout":
                    Repository.checkout(args);
                    break;
                case "branch":
                    if (args.length != 2) {
                        System.out.println("error");
                        exit(0);
                    }
                    Repository.branch(args[1]);
                    break;
                case "rm-branch":
                    if (args.length != 2) {
                        System.out.println("error");
                        exit(0);
                    }
                    Repository.rm_branch(args[1]);
                    break;
                case "reset":
                    if (args.length != 2) {
                        System.out.println("error");
                        exit(0);
                    }
                    Repository.reset(args[1]);
                    break;
                case "merge":
                    if (args.length != 2) {
                        System.out.println("error");
                        exit(0);
                    }
                    Repository.merge(args[1]);
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    exit(0);
            }
        }catch (IOException e){
            exit(0);
        }
    }
}
