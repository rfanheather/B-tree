import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

public class treesearch {
    /**
     * TODO convert the input file into command lines
     *
     * @param fileName
     * @return command lines
     */
    private static List<String> getCommands(String fileName) throws IOException {
        if(fileName == null) return new ArrayList<>(0);

        File file = new File(fileName);

        if(! (file.exists() && file.canRead())) {
            System.err.println("Cannot access file! Non-existent or read access restricted");
            return new ArrayList<>(0);
        }

        //Use a list to store commands
        List<String> commandLines = new ArrayList<>(32);
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()) {
            commandLines.add(scanner.nextLine());
        }
        scanner.close();
        return commandLines;
    }

    /**
     * TODO Implement commands in the text file
     *
     * @param commandArgs, tree, writer
     *
     */
    private static void processCommandLine(String[] commandArgs, BPlusTree tree, PrintWriter writer) {
        if (commandArgs == null || commandArgs.length == 0 || commandArgs[0] == null) {
            writer.println("Null command!");
        }

        String command = commandArgs[0];

        //Insert(Double key, String value)
        if (command.trim().equalsIgnoreCase("Insert")){
            Double key = Double.parseDouble(commandArgs[1]);
            String value = commandArgs[2];

            tree.Insert(key, value);
        }
        //Search
        else if (command.trim().equalsIgnoreCase("Search")) {
            if (commandArgs.length > 2) {
                //It is a range search: Search(Double key1, Double key2)
                Double key1 = Double.parseDouble(commandArgs[1]);
                Double key2 = Double.parseDouble(commandArgs[2]);

                ArrayList<MyPair> result = tree.Search(key1, key2);

                //Print the result
                if (result.size() == 0) {
                    writer.println("Null");
                } else {
                    int i;
                    for (i = 0; i < result.size() - 1; i++) {
                        writer.print("(" + result.get(i).key() + "," +result.get(i).value() + ")" + ",");
                    }
                    writer.println("(" + result.get(i).key() + "," +result.get(i).value() + ")");
                }
            } else {
                //It is not a range search: Search (Double key)
                Double key = Double.parseDouble(commandArgs[1]);

                ArrayList<String> result = tree.Search(key);

                //Print the result
                if (result.size() == 0) {
                    writer.println("Null");
                } else {
                    int i;
                    for (i = 0; i < result.size() - 1; i++) {
                        writer.print(result.get(i) + ",");
                    }
                    writer.println(result.get(i));
                }
            }
        }
        //Initialize
        else {
            int m = Integer.parseInt(commandArgs[0]);
            tree.Initialize(m);
        }
    }

    public static void main(String[] args) throws IOException {
        String fileName = args[0]; // Get the name of the text file
        List<String> commandLines = getCommands(fileName);
        BPlusTree tree = new BPlusTree();
        //Create an output file
        PrintWriter writer = new PrintWriter("output_file.txt", "UTF-8");
        for (String commandLine : commandLines) {
            String[] commandArgs = commandLine.split("\\(|,|\\)");
            processCommandLine(commandArgs, tree, writer);
        }
        writer.flush();
        writer.close();
    }
}

