package cnf;

import java.io.*;
import java.nio.file.*;
import java.util.*;

// Parses a plain-text grammar file into a Grammar
public class GrammarParser {

    public static Grammar parse(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        Set<String>      nonTerminals = new LinkedHashSet<>();
        Set<String>      terminals    = new LinkedHashSet<>();
        String           start        = null;
        List<Production> productions  = new ArrayList<>();

        boolean inProductions = false;

        for (String raw : lines) {
            // Strip inline comments
            String line = raw.contains("#") ? raw.substring(0, raw.indexOf('#')) : raw;
            line = line.trim();
            if (line.isEmpty()) continue;

            String upper = line.toUpperCase();

            if (upper.startsWith("NONTERMINALS:")) {
                inProductions = false;
                String rest = line.substring("NONTERMINALS:".length());
                for (String sym : splitSymbols(rest)) nonTerminals.add(sym);

            } else if (upper.startsWith("TERMINALS:")) {
                inProductions = false;
                String rest = line.substring("TERMINALS:".length());
                for (String sym : splitSymbols(rest)) terminals.add(sym);

            } else if (upper.startsWith("START:")) {
                inProductions = false;
                start = line.substring("START:".length()).trim();

            } else if (upper.startsWith("PRODUCTIONS:")) {
                inProductions = true;

            } else if (inProductions) {
                // Expect:  LHS -> sym1 sym2 ...
                if (!line.contains("->")) {
                    throw new IllegalArgumentException(
                            "Invalid production line (missing '->'): " + raw);
                }
                String[] parts = line.split("->", 2);
                String lhs = parts[0].trim();
                String rhsRaw = parts[1].trim();

                List<String> rhs;
                if (rhsRaw.isEmpty() || rhsRaw.equalsIgnoreCase("eps")) {
                    rhs = Collections.singletonList("ε");
                } else {
                    rhs = new ArrayList<>();
                    for (String sym : rhsRaw.split("\\s+")) {
                        rhs.add(sym.equalsIgnoreCase("eps") ? "ε" : sym);
                    }
                }
                productions.add(new Production(lhs, rhs));
            }
        }

        if (start == null)        throw new IllegalArgumentException("START symbol not defined in " + filePath);
        if (nonTerminals.isEmpty()) throw new IllegalArgumentException("NONTERMINALS not defined in " + filePath);
        if (terminals.isEmpty())    throw new IllegalArgumentException("TERMINALS not defined in " + filePath);

        return new Grammar(nonTerminals, terminals, productions, start);
    }

    // Splits a comma- or whitespace-separated symbol list, ignoring empty tokens.
    private static List<String> splitSymbols(String s) {
        List<String> result = new ArrayList<>();
        for (String token : s.split("[,\\s]+")) {
            token = token.trim();
            if (!token.isEmpty()) result.add(token);
        }
        return result;
    }
}