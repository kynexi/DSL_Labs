# Laboratory Work 5, Chomsky Normal Form

### Course: Formal Languages & Finite Automata
### Author: Brînză Vasile
### Group: FAF-242

----

## Theory

---
In formal language theory, a context-free grammar, G, is said to be in Chomsky normal form (first described by Noam Chomsky) if all of its production rules are of the form:
A → BC, or
A → a, or
S → ε,
where A, B, and C are nonterminal symbols, the letter a is a terminal symbol (a symbol that represents a constant value), S is the start symbol, and ε denotes the empty string.
Also, neither B nor C may be the start symbol, and the third production rule can only appear if ε is in L(G), the language produced by the context-free grammar G.
Every grammar in Chomsky normal form is context-free, and conversely, every context-free grammar can be transformed into an equivalent one which is in Chomsky normal form and has a size no larger than the square of the original grammar's size.

## Objectives

1. Learn about Chomsky Normal Form (CNF) [1].
2. Get familiar with the approaches of normalizing a grammar.
3. Implement a method for normalizing an input grammar by the rules of CNF.
    1. The implementation needs to be encapsulated in a method with an appropriate signature (also ideally in an appropriate class/type).
    2. The implemented functionality needs executed and tested.
    3. Also, another **BONUS point** would be given if the student will make the aforementioned function to accept any grammar, not only the one from the student's variant.


## Implementation description

The implementation is structured across six Java classes, each with a single responsibility. Production represents a single grammar rule, Grammar holds the full grammar structure, GrammarBuilder constructs grammars fluently, GrammarParser reads grammars from text files, CNFConverter performs the five normalization steps, and Main drives the program.

> Production.java

```java
// Production.java
public boolean isEpsilon() {
    return rhs.isEmpty() || (rhs.size() == 1 && rhs.get(0).equals("ε"));
}

public boolean isUnit(Set<String> nonTerminals) {
    return rhs.size() == 1 && nonTerminals.contains(rhs.get(0));
}

public String getUnitTarget() {
    return rhs.get(0);
}
```

Represents a single production rule LHS → RHS. Provides helper methods used throughout CNFConverter to classify rules without scattering that logic across the codebase.

> Grammar.java

```java
// Grammar.java
public Grammar(Set<String> nonTerminals, Set<String> terminals,
               List<Production> productions, String startSymbol) {
    this.nonTerminals = Collections.unmodifiableSet(new LinkedHashSet<>(nonTerminals));
    this.terminals    = Collections.unmodifiableSet(new LinkedHashSet<>(terminals));
    this.productions  = Collections.unmodifiableList(new ArrayList<>(productions));
    this.startSymbol  = startSymbol;
}

public List<Production> getProductionsFor(String lhs) {
    List<Production> result = new ArrayList<>();
    for (Production p : productions)
        if (p.getLhs().equals(lhs)) result.add(p);
    return result;
}
```

> GrammarBuilder.java

```java
// GrammarBuilder.java
Grammar variant5 = new GrammarBuilder("S")
        .nonTerminals("S", "A", "B", "C", "D")
        .terminals("a", "b", "d")
        .production("S", "d", "B")
        .production("S", "A")
        .production("C", "eps")
        .build();
```

A fluent builder that makes constructing a Grammar readable and concise, especially useful for the bonus requirement of accepting any grammar as input.

> GrammarParser.java

```java
// GrammarParser.java
for (String raw : lines) {
    String line = raw.contains("#")
            ? raw.substring(0, raw.indexOf('#')) : raw;
    line = line.trim();
    if (line.isEmpty()) continue;

    String upper = line.toUpperCase();
    if (upper.startsWith("NONTERMINALS:")) {
        for (String sym : splitSymbols(line.substring("NONTERMINALS:".length())))
            nonTerminals.add(sym);
    } else if (upper.startsWith("PRODUCTIONS:")) {
        inProductions = true;
    } else if (inProductions) {
        String[] parts = line.split("->", 2);
        String lhs = parts[0].trim();
        // tokens: replace 'eps' with ε
        List<String> rhs = new ArrayList<>();
        for (String sym : parts[1].trim().split("\\s+"))
            rhs.add(sym.equalsIgnoreCase("eps") ? "ε" : sym);
        productions.add(new Production(lhs, rhs));
    }
}
```

Reads a grammar from a plain .txt file, parsing the NONTERMINALS, TERMINALS, START, and PRODUCTIONS sections. Lines beginning with # are treated as comments. The token eps on any RHS is converted to ε internally.

> CNFConverter.java

Contains the five normalization steps, each as its own public method that takes a Grammar and returns a new transformed Grammar, leaving the original untouched.

Step 1 — eliminateEpsilonProductions. Nullable non-terminals are found via fixed-point iteration, then every production is expanded into all combinations where nullable symbols are optionally omitted, and bare ε-rules are discarded.

```java
// CNFConverter.java – eliminateEpsilonProductions()
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
```
Step 2 — eliminateRenamings. For each non-terminal, a BFS computes its unit-closure, then all non-unit productions at the end of those chains are collected directly under the original non-terminal.

```java
// CNFConverter.java – eliminateRenamings()
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
```

Step 3 — eliminateInaccessibleSymbols. A BFS from the start symbol marks every reachable non-terminal; any non-terminal not visited along with all its productions is removed.

```java
// CNFConverter.java – eliminateInaccessibleSymbols()
Set<String> accessible = new LinkedHashSet<>();
accessible.add(g.getStartSymbol());
Queue<String> queue = new LinkedList<>(Collections.singletonList(g.getStartSymbol()));
while (!queue.isEmpty()) {
    String cur = queue.poll();
    for (Production p : g.getProductionsFor(cur)) {
        for (String sym : p.getRhs()) {
            if (g.getNonTerminals().contains(sym) && accessible.add(sym))
                queue.add(sym);
        }
    }
}
```

Step 4 — eliminateNonProductiveSymbols. Starting from the terminals, fixed-point iteration marks any non-terminal productive if at least one of its productions consists entirely of already-productive symbols; non-productive non-terminals and any rule referencing them are removed.

```java
// CNFConverter.java – eliminateNonProductiveSymbols()
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
```

Step 5 — convertToChomskyNormalForm. Each terminal appearing in a production of length ≥ 2 is replaced by a fresh non-terminal wrapper, then any production longer than two symbols is split into a binary chain using additional fresh non-terminals until every rule is either A → BC or A → a.

```java
// CNFConverter.java – convertToChomskyNormalForm()

// Terminal wrappers
for (String t : g.getTerminals()) {
    String nt = freshSymbol(newNT);
    newNT.add(nt);
    newProds.add(new Production(nt, Collections.singletonList(t)));
    terminalWrapper.put(t, nt);
}

// Binarisation
if (rhs.size() > 2) {
    String fresh = freshSymbol(newNT);
    newNT.add(fresh);
    pass.add(new Production(p.getLhs(), Arrays.asList(rhs.get(0), fresh)));
    pass.add(new Production(fresh, new ArrayList<>(rhs.subList(1, rhs.size()))));
}
```


### Example Output

```
=== Input Grammar ===
  V_N   : [S, A, B, C, D]
  V_T   : [a, b, d]
  Start : S
  Rules :
     1.  S → d B
     2.  S → A
     3.  A → d
     4.  A → d S
     5.  A → a B d B
     6.  B → a
     7.  B → a S
     8.  B → A C
     9.  D → A B
    10.  C → b C
    11.  C → ε

=== Step 1: Eliminate Epsilon Productions ===
  V_N   : [S, A, B, C, D]
  V_T   : [a, b, d]
  Start : S
  Rules :
     1.  S → d B
     2.  S → A
     3.  A → d
     4.  A → d S
     5.  A → a B d B
     6.  B → a
     7.  B → a S
     8.  B → A C
     9.  B → A
    10.  D → A B
    11.  C → b C
    12.  C → b

=== Step 2: Eliminate Renamings ===
  V_N   : [S, A, B, C, D]
  V_T   : [a, b, d]
  Start : S
  Rules :
     1.  S → d B
     2.  S → d
     3.  S → d S
     4.  S → a B d B
     5.  A → d
     6.  A → d S
     7.  A → a B d B
     8.  B → a
     9.  B → a S
    10.  B → A C
    11.  B → d
    12.  B → d S
    13.  B → a B d B
    14.  C → b C
    15.  C → b
    16.  D → A B

=== Step 3: Eliminate Inaccessible Symbols ===
  V_N   : [S, A, B, C]
  V_T   : [a, b, d]
  Start : S
  Rules :
     1.  S → d B
     2.  S → d
     3.  S → d S
     4.  S → a B d B
     5.  A → d
     6.  A → d S
     7.  A → a B d B
     8.  B → a
     9.  B → a S
    10.  B → A C
    11.  B → d
    12.  B → d S
    13.  B → a B d B
    14.  C → b C
    15.  C → b

=== Step 4: Eliminate Non-Productive Symbols ===
  V_N   : [S, A, B, C]
  V_T   : [a, b, d]
  Start : S
  Rules :
     1.  S → d B
     2.  S → d
     3.  S → d S
     4.  S → a B d B
     5.  A → d
     6.  A → d S
     7.  A → a B d B
     8.  B → a
     9.  B → a S
    10.  B → A C
    11.  B → d
    12.  B → d S
    13.  B → a B d B
    14.  C → b C
    15.  C → b

=== Step 5: Chomsky Normal Form ===
  V_N   : [S, A, B, C, X1, X2, X3, X4, X7]
  V_T   : [a, b, d]
  Start : S
  Rules :
     1.  S → X3 B
     2.  S → d
     3.  S → X3 S
     4.  S → X1 X4
     5.  X4 → B X7
     6.  X7 → X3 B
     7.  A → d
     8.  A → X3 S
     9.  A → X1 X4
    10.  B → a
    11.  B → X1 S
    12.  B → A C
    13.  B → d
    14.  B → X3 S
    15.  B → X1 X4
    16.  C → X2 C
    17.  C → b
    18.  X1 → a
    19.  X2 → b
    20.  X3 → d
```

## Conclusions

In this lab, I went through the process of converting a context-free grammar into Chomsky Normal Form step by step. Each transformation removes something unnecessary or simplifies the structure until all rules follow the CNF format.

The implementation worked as expected and showed that the process is more complex in practice than it looks in theory, especially when dealing with epsilon and unit productions. Splitting the logic into multiple classes made the code easier to understand and modify.

Overall, the goal was achieved: the grammar was successfully converted to CNF, and the solution can be reused for other grammars as well.


