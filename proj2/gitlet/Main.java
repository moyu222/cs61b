package gitlet;

import java.util.ResourceBundle;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(args, 2);
                checkInit();
                String fileName = args[1];
                Repository.add(fileName);
                break;
            case "commit":
                validateNumArgs(args, 2);
                checkInit();
                String message = args[1];
                Repository.commit(message);
                break;
            case "rm":
                validateNumArgs(args, 2);
                checkInit();
                String file = args[1];
                Repository.rm(file);
                break;
            case "log":
                validateNumArgs(args, 1);
                checkInit();
                Repository.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                checkInit();
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                checkInit();
                String commitMessage = args[1];
                Repository.find(commitMessage);
                break;
            case "status":
                validateNumArgs(args, 1);
                checkInit();
                Repository.status();
                break;
            case "checkout":
                checkInit();

                if (args.length == 3 && args[1].equals("--")) {
                    Repository.checkoutLatestFile(args[2]);

                } else if (args.length == 4 && args[2].equals("--")) {
                    Repository.checkoutIdFile(args[1], args[3]);

                } else if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);

                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "branch":
                validateNumArgs(args, 2);
                checkInit();
                String branchName = args[1];
                Repository.branch(branchName);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                checkInit();
                String rmBranchName = args[1];
                Repository.rmBranch(rmBranchName);
                break;
            case "reset":
                validateNumArgs(args, 2);
                checkInit();
                String commitId = args[1];
                Repository.reset(commitId);
                break;
            case "merge":
                validateNumArgs(args, 2);
                checkInit();
                String mergeBranchName = args[1];
                Repository.merge(mergeBranchName);
                break;
        }

    }

    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    public static void checkInit() {
        if (!Repository.checkStructure()) {
            System.out.println("Not in an initialized Gitlet direcory.");
            System.exit(0);
        }
    }
}
