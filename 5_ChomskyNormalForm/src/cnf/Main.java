package cnf;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a grammar variant:");
        System.out.println("  1 - input.txt");
        System.out.println("  2 - input1.txt");
        System.out.println("  3 - input2.txt");
        System.out.print("Enter choice (1/2/3): ");

        String choice = scanner.nextLine().trim();
        String inputFile;
        switch (choice) {
            case "1": inputFile = "src/cnf/input.txt";  break;
            case "2": inputFile = "src/cnf/input1.txt"; break;
            case "3": inputFile = "src/cnf/input2.txt"; break;
            default:
                System.out.println("Invalid choice. Defaulting to input.txt");
                inputFile = "input.txt";
        }

        Grammar grammar = GrammarParser.parse(inputFile);
        CNFConverter converter = new CNFConverter();

        try (PrintWriter out = new PrintWriter(new FileWriter("output.txt"))) {

            out.println("=== Input Grammar ===");
            grammar.printTo(out);

            Grammar g = grammar;

            g = converter.eliminateEpsilonProductions(g);
            out.println("\n=== Step 1: Eliminate Epsilon Productions ===");
            g.printTo(out);

            g = converter.eliminateRenamings(g);
            out.println("\n=== Step 2: Eliminate Renamings ===");
            g.printTo(out);

            g = converter.eliminateInaccessibleSymbols(g);
            out.println("\n=== Step 3: Eliminate Inaccessible Symbols ===");
            g.printTo(out);

            g = converter.eliminateNonProductiveSymbols(g);
            out.println("\n=== Step 4: Eliminate Non-Productive Symbols ===");
            g.printTo(out);

            g = converter.convertToChomskyNormalForm(g);
            out.println("\n=== Step 5: Chomsky Normal Form ===");
            g.printTo(out);
        }

        System.out.println("Done. Output written to output.txt");
    }
}