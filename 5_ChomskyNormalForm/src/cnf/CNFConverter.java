package cnf;

import java.util.*;

// Converts an arbitrary Context-Free Grammar to Chomsky Normal Form (CNF).
// The steps are applied in order:
// 1. Eliminate ε-productions
// 2. Eliminate renamings (unit productions)
// 3. Eliminate inaccessible symbols
// 4. Eliminate non-productive symbols
// 5. Convert remaining productions to binary form (CNF)
public class CNFConverter {

    // Counter used to generate unique helper non-terminal names
    private int freshCounter = 0;
    public Grammar convert(Grammar grammar) {
        Grammar g = grammar;

        System.out.println("--CNF Normalization--");

        System.out.println("\nOriginal Grammar");
        g.print();

        g = eliminateEpsilonProductions(g);
        System.out.println("\nStep 1: Eliminate ε-productions");
        g.print();

        g = eliminateRenamings(g);
        System.out.println("\nStep 2: Eliminate renamings");
        g.print();

        g = eliminateInaccessibleSymbols(g);
        System.out.println("\nStep 3: Eliminate inaccessible symbols");
        g.print();

        g = eliminateNonProductiveSymbols(g);
        System.out.println("\nStep 4: Eliminate non-productive syms");
        g.print();

        g = convertToChomskyNormalForm(g);
        System.out.println("\nStep 5: Chomsky Normal Form");
        g.print();

        System.out.println("\n-----");

        return g;
    }

    public Grammar eliminateEpsilonProductions(Grammar g) {
        Set<String> nonTerminals = g.getNonTerminals();

        // 1a. Compute nullable set (fixed-point iteration)
        Set<String> nullable = new LinkedHashSet<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Production p : g.getProductions()) {
                if (nullable.contains(p.getLhs())) continue;
                if (p.isEpsilon() || nullable.containsAll(p.getRhs())) {
                    nullable.add(p.getLhs());
                    changed = true;
                }
            }
        }

        // 1b. Expand productions: drop nullable symbols in every possible subset
        Set<Production> newProds = new LinkedHashSet<>();
        for (Production p : g.getProductions()) {
            for (List<String> combo : generateCombinations(p.getRhs(), nullable)) {
                if (!combo.isEmpty()) {                       // drop pure ε
                    newProds.add(new Production(p.getLhs(), combo));
                }
            }
        }

        return new Grammar(nonTerminals, g.getTerminals(), new ArrayList<>(newProds), g.getStartSymbol());
    }

    private Set<List<String>> generateCombinations(List<String> rhs, Set<String> nullable) {
        Set<List<String>> result = new LinkedHashSet<>();
        result.add(new ArrayList<>());
        for (String sym : rhs) {
            Set<List<String>> next = new LinkedHashSet<>();
            for (List<String> current : result) {
                List<String> withSym = new ArrayList<>(current);
                withSym.add(sym);
                next.add(withSym);
                if (nullable.contains(sym)) {
                    next.add(new ArrayList<>(current));  // version without sym
                }
            }
            result = next;
        }
        return result;
    }

    public Grammar eliminateRenamings(Grammar g) {
        Set<String> nonTerminals = g.getNonTerminals();
        List<Production> newProds = new ArrayList<>();

        for (String nt : nonTerminals) {
            // BFS to find all non-terminals reachable by unit rules
            Set<String> unitReachable = new LinkedHashSet<>();
            unitReachable.add(nt);
            Queue<String> queue = new LinkedList<>(Collections.singletonList(nt));
            while (!queue.isEmpty()) {
                String cur = queue.poll();
                for (Production p : g.getProductionsFor(cur)) {
                    if (p.isUnit(nonTerminals)) {
                        String target = p.getUnitTarget();
                        if (unitReachable.add(target)) queue.add(target);
                    }
                }
            }

            // Collect all non-unit productions reachable through the unit chain
            Set<Production> combined = new LinkedHashSet<>();
            for (String reachable : unitReachable) {
                for (Production p : g.getProductionsFor(reachable)) {
                    if (!p.isUnit(nonTerminals)) {
                        combined.add(new Production(nt, p.getRhs()));
                    }
                }
            }
            newProds.addAll(combined);
        }

        return new Grammar(nonTerminals, g.getTerminals(), newProds, g.getStartSymbol());
    }

    public Grammar eliminateInaccessibleSymbols(Grammar g) {
        Set<String> accessible = new LinkedHashSet<>();
        accessible.add(g.getStartSymbol());
        Queue<String> queue = new LinkedList<>(Collections.singletonList(g.getStartSymbol()));

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            for (Production p : g.getProductionsFor(cur)) {
                for (String sym : p.getRhs()) {
                    if (g.getNonTerminals().contains(sym) && accessible.add(sym)) {
                        queue.add(sym);
                    }
                }
            }
        }

        Set<String> newNT = new LinkedHashSet<>();
        List<Production> newProds = new ArrayList<>();
        for (String nt : g.getNonTerminals()) {
            if (accessible.contains(nt)) {
                newNT.add(nt);
                newProds.addAll(g.getProductionsFor(nt));
            }
        }

        return new Grammar(newNT, g.getTerminals(), newProds, g.getStartSymbol());
    }

    public Grammar eliminateNonProductiveSymbols(Grammar g) {
        Set<String> productive = new LinkedHashSet<>(g.getTerminals());

        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nt : g.getNonTerminals()) {
                if (productive.contains(nt)) continue;
                for (Production p : g.getProductionsFor(nt)) {
                    if (productive.containsAll(p.getRhs())) {
                        productive.add(nt);
                        changed = true;
                        break;
                    }
                }
            }
        }

        Set<String> newNT = new LinkedHashSet<>();
        List<Production> newProds = new ArrayList<>();
        for (String nt : g.getNonTerminals()) {
            if (!productive.contains(nt)) continue;
            newNT.add(nt);
            for (Production p : g.getProductionsFor(nt)) {
                if (productive.containsAll(p.getRhs())) newProds.add(p);
            }
        }

        return new Grammar(newNT, g.getTerminals(), newProds, g.getStartSymbol());
    }

    public Grammar convertToChomskyNormalForm(Grammar g) {
        Set<String>      newNT    = new LinkedHashSet<>(g.getNonTerminals());
        List<Production> newProds = new ArrayList<>(g.getProductions());

        // 5a. Create wrapper non-terminals for terminals inside long productions
        Map<String, String> terminalWrapper = new LinkedHashMap<>();
        for (String t : g.getTerminals()) {
            boolean needed = false;
            for (Production p : newProds) {
                if (p.getRhs().size() >= 2 && p.getRhs().contains(t)) {
                    needed = true;
                    break;
                }
            }
            if (needed) {
                String nt = freshSymbol(newNT);
                newNT.add(nt);
                newProds.add(new Production(nt, Collections.singletonList(t)));
                terminalWrapper.put(t, nt);
            }
        }

        // 5b. Replace terminals in productions of length ≥ 2
        List<Production> replaced = new ArrayList<>();
        for (Production p : newProds) {
            if (p.getRhs().size() >= 2) {
                List<String> newRhs = new ArrayList<>();
                for (String sym : p.getRhs()) {
                    newRhs.add(terminalWrapper.getOrDefault(sym, sym));
                }
                replaced.add(new Production(p.getLhs(), newRhs));
            } else {
                replaced.add(p);
            }
        }
        newProds = replaced;

        // 5c. Break productions of length > 2 into binary productions
        boolean anyLong = true;
        while (anyLong) {
            anyLong = false;
            List<Production> pass = new ArrayList<>();
            for (Production p : newProds) {
                List<String> rhs = p.getRhs();
                if (rhs.size() > 2) {
                    anyLong = true;
                    // A → X1 X2 … Xn  ⟹  A → X1 Fresh,  Fresh → X2 … Xn
                    String fresh = freshSymbol(newNT);
                    newNT.add(fresh);
                    pass.add(new Production(p.getLhs(), Arrays.asList(rhs.get(0), fresh)));
                    pass.add(new Production(fresh, new ArrayList<>(rhs.subList(1, rhs.size()))));
                } else {
                    pass.add(p);
                }
            }
            newProds = pass;
        }

        return new Grammar(newNT, g.getTerminals(), newProds, g.getStartSymbol());
    }

    private String freshSymbol(Set<String> existing) {
        String candidate;
        do { candidate = "X" + (++freshCounter); } while (existing.contains(candidate));
        return candidate;
    }
}