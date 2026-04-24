package cnf;

import java.util.*;

public class Grammar {

    private final Set<String>      nonTerminals;
    private final Set<String>      terminals;
    private final List<Production> productions;
    private final String           startSymbol;

    public Grammar(Set<String>      nonTerminals,
                   Set<String>      terminals,
                   List<Production> productions,
                   String           startSymbol) {
        this.nonTerminals = Collections.unmodifiableSet(new LinkedHashSet<>(nonTerminals));
        this.terminals    = Collections.unmodifiableSet(new LinkedHashSet<>(terminals));
        this.productions  = Collections.unmodifiableList(new ArrayList<>(productions));
        this.startSymbol  = startSymbol;
    }

    public Set<String>      getNonTerminals() { return nonTerminals; }
    public Set<String>      getTerminals()    { return terminals;    }
    public List<Production> getProductions()  { return productions;  }
    public String           getStartSymbol()  { return startSymbol;  }

    // Returns all productions whose LHS equals {@code lhs}.
    public List<Production> getProductionsFor(String lhs) {
        List<Production> result = new ArrayList<>();
        for (Production p : productions) {
            if (p.getLhs().equals(lhs)) result.add(p);
        }
        return result;
    }

    public void print() {
        printTo(new java.io.PrintWriter(System.out, true));
    }

    public void printTo(java.io.PrintWriter out) {
        out.println("  V_N   : " + nonTerminals);
        out.println("  V_T   : " + terminals);
        out.println("  Start : " + startSymbol);
        out.println("  Rules :");
        int i = 1;
        for (Production p : productions) {
            out.printf("    %2d.  %s%n", i++, p);
        }
    }
}