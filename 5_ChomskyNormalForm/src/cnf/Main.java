package cnf;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Grammar grammar = GrammarParser.parse("C:\\Users\\vasya\\Desktop\\DSL\\DSL_Labs\\5_ChomskyNormalForm\\src\\cnf\\input.txt");
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