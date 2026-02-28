import java.util.*;

class FiniteAutomation {

    Set<String> states;
    Set<String> alphabet;
    Map<String, Map<Character, Set<String>>> transitions;
    String startState;
    Set<String> finalStates;

    public FiniteAutomation(Set<String> states,
                            Set<String> alphabet,
                            Map<String, Map<Character, Set<String>>> transitions,
                            String startState,
                            Set<String> finalStates) {

        this.states = states;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.startState = startState;
        this.finalStates = finalStates;
    }

    public boolean accepts(String input) {
        Set<String> currentStates = new HashSet<>();
        currentStates.add(startState);

        for (char symbol : input.toCharArray()) {
            Set<String> nextStates = new HashSet<>();

            for (String state : currentStates) {
                if (transitions.get(state).containsKey(symbol)) {
                    nextStates.addAll(
                            transitions.get(state).get(symbol)
                    );
                }
            }

            currentStates = nextStates;
            if (currentStates.isEmpty()) {
                return false;
            }
        }

        for (String state : currentStates) {
            if (finalStates.contains(state)) {
                return true;
            }
        }

        return false;
    }

    public Grammar toRegularGrammar() {
        Set<String> VN = new HashSet<>(states);

        Set<String> VT = new HashSet<>();
        for (String symbol : alphabet) {
            VT.add(symbol);
        }

        Map<String, List<String>> productions = new HashMap<>();

        for (String state : states) {
            List<String> rules = new ArrayList<>();
            Map<Character, Set<String>> trans = transitions.get(state);

            for (Character symbol : trans.keySet()) {
                Set<String> targets = trans.get(symbol);
                for (String target : targets) {
                    // when the target is final, add terminal-only production
                    if (finalStates.contains(target)) {
                        rules.add(symbol.toString());
                    }
                    rules.add(symbol + target);
                }
            }

            if (!rules.isEmpty()) {
                productions.put(state, rules);
            }
        }

        return new Grammar(VN, VT, productions, startState);
    }

    public boolean isDeterministic() {
        for (String state : transitions.keySet()) {
            Map<Character, Set<String>> trans = transitions.get(state);
            for (char symbol : trans.keySet()) {
                Set<String> targets = trans.get(symbol);
                // more than one target means non-deterministic
                if (targets.size() > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public FiniteAutomation convertToDFA() {
        Map<Set<String>, String> mapping = new HashMap<>();
        Set<String> dfaStates = new HashSet<>();
        Set<String> dfaFinalStates = new HashSet<>();
        Map<String, Map<Character, Set<String>>> dfaTransitions = new HashMap<>();
        Queue<Set<String>> queue = new LinkedList<>();

        // initial state
        Set<String> startSet = new HashSet<>();
        startSet.add(startState);
        mapping.put(startSet, "D0");
        queue.add(startSet);
        dfaStates.add("D0");

        int counter = 1;

        while (!queue.isEmpty()) {
            Set<String> currentSet = queue.poll();
            String currentName = mapping.get(currentSet);
            dfaTransitions.put(currentName, new HashMap<>());

            // looping over alphabet
            for (String symbolStr : alphabet) {
                if (symbolStr.length() != 1) continue; // invalid symbols
                char symbol = symbolStr.charAt(0);
                Set<String> nextSet = new HashSet<>();
                for (String state : currentSet) {
                    Map<Character, Set<String>> stateTrans = transitions.get(state);
                    if (stateTrans.containsKey(symbol)) {
                        nextSet.addAll(stateTrans.get(symbol));
                    }
                }

                if (!nextSet.isEmpty()) {
                    String nextName;
                    if (mapping.containsKey(nextSet)) {
                        nextName = mapping.get(nextSet);
                    } else {
                        nextName = "D" + counter++;
                        mapping.put(nextSet, nextName);
                        queue.add(nextSet);
                        dfaStates.add(nextName);
                    }

                    dfaTransitions.get(currentName).put(symbol, Set.of(nextName));
                }
            }
        }

        // DFA final states
        for (Set<String> ndfaSet : mapping.keySet()) {
            for (String s : ndfaSet) {
                if (finalStates.contains(s)) {
                    dfaFinalStates.add(mapping.get(ndfaSet));
                    break;
                }
            }
        }

        return new FiniteAutomation(dfaStates, alphabet, dfaTransitions, "D0", dfaFinalStates);
    }
}
