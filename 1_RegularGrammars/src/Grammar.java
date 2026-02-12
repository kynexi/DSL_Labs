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
}