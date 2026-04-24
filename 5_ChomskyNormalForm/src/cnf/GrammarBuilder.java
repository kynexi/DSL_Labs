package cnf;

import java.util.*;

// Fluent builder for constructing a Grammar
public class GrammarBuilder {

    private final String           startSymbol;
    private final Set<String>      nonTerminals = new LinkedHashSet<>();
    private final Set<String>      terminals    = new LinkedHashSet<>();
    private final List<Production> productions  = new ArrayList<>();

    public GrammarBuilder(String startSymbol) {
        this.startSymbol = startSymbol;
    }

    // Registers one or more non-terminal symbols.
    public GrammarBuilder nonTerminals(String... symbols) {
        nonTerminals.addAll(Arrays.asList(symbols));
        return this;
    }

    // Registers one or more terminal symbols.
    public GrammarBuilder terminals(String... symbols) {
        terminals.addAll(Arrays.asList(symbols));
        return this;
    }

    public GrammarBuilder production(String lhs, String... rhs) {
        productions.add(new Production(lhs, Arrays.asList(rhs)));
        return this;
    }

    public Grammar build() {
        return new Grammar(nonTerminals, terminals, productions, startSymbol);
    }
}