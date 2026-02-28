import java.util.*;

class Grammar {
    Set<String> VN;
    Set<String> VT;
    Map<String, List<String>> productions;
    String startSymbol;

    public Grammar(Set<String> VN, Set<String> VT,
                   Map<String, List<String>> productions,
                   String startSymbol) {
        this.VN = VN;
        this.VT = VT;
        this.productions = productions;
        this.startSymbol = startSymbol;
    }

    public String generateString() {
        StringBuilder result = new StringBuilder();
        String current = startSymbol;
        Random random = new Random();

        while (true) {
            List<String> rules = productions.get(current);
            String production = rules.get(random.nextInt(rules.size()));

            if (production.length() == 1) {
                result.append(production);
                break;
            } else {
                result.append(production.charAt(0));
                current = String.valueOf(production.charAt(1));
            }
        }

        return result.toString();
    }

    public List<String> generateFiveStrings() {
        Set<String> results = new HashSet<>();
        while (results.size() < 5) {
            results.add(generateString());
        }
        return new ArrayList<>(results);
    }

    public FiniteAutomation toFA() {

        Set<String> states = new HashSet<>(VN);
        Set<String> finalStates = new HashSet<>();
        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();

        String finalState = "Qf";
        states.add(finalState);
        finalStates.add(finalState);

        for (String state : states) {
            transitions.put(state, new HashMap<>());
        }

        for (String left : productions.keySet()) {
            for (String production : productions.get(left)) {

                char terminal = production.charAt(0);

                if (production.length() == 1) {
                    transitions.get(left)
                            .computeIfAbsent(terminal, k -> new HashSet<>())
                            .add(finalState);
                } else {
                    String nextState = String.valueOf(production.charAt(1));
                    transitions.get(left)
                            .computeIfAbsent(terminal, k -> new HashSet<>())
                            .add(nextState);
                }
            }
        }

        return new FiniteAutomation(states, VT, transitions,
                startSymbol, finalStates);
    }

    public String classifyGrammar() {
        boolean isRegular = true;
        boolean isContextFree = true;
        // assume grammar is regular / context-free

        for (String left : productions.keySet()) {
            // type 2 and 3 must have exactly one non-terminal on LHS
            if (left.length() != 1 || !VN.contains(left)) {
                isRegular = false;
                isContextFree = false;
                break;
            }

            List<String> rights = productions.get(left);
            for (String right : rights) {
                // when right-hand side has more than 2 symbols, cannot be regular
                if (right.length() > 2) {
                    isRegular = false;
                }

                // right-hand side shorter than LHS, cannot be context-sensitive
                if (right.length() < left.length()) {
                    return "Type 0 (Unrestricted)";
                }
            }
        }

        if (isRegular) return "Type 3 (Regular)";
        if (isContextFree) return "Type 2 (Context-Free)";
        return "Type 1 (Context-Sensitive)";
    }
}