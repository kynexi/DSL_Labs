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
}
