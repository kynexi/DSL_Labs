import java.util.*;

public class Main {
    public static void main(String[] args) {
        Set<String> states = new HashSet<>(Arrays.asList("q0", "q1", "q2", "q3"));
        Set<String> alphabet = new HashSet<>(Arrays.asList("a", "b"));
        Set<String> finalStates = new HashSet<>(Collections.singletonList("q3"));
        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();

        for (String state : states) {
            transitions.put(state, new HashMap<>());
        }

        // δ(q0,a) = q1
        transitions.get("q0").computeIfAbsent('a', k -> new HashSet<>()).add("q1");

        // δ(q0,b) = q0
        transitions.get("q0").computeIfAbsent('b', k -> new HashSet<>()).add("q0");

        // δ(q1,a) = q2 and q3
        transitions.get("q1").computeIfAbsent('a', k -> new HashSet<>()).add("q2");
        transitions.get("q1").computeIfAbsent('a', k -> new HashSet<>()).add("q3");

        // δ(q2,a) = q3
        transitions.get("q2").computeIfAbsent('a', k -> new HashSet<>()).add("q3");

        // δ(q2,b) = q0
        transitions.get("q2").computeIfAbsent('b', k -> new HashSet<>()).add("q0");

        FiniteAutomation fa = new FiniteAutomation(states, alphabet, transitions, "q0", finalStates);
        System.out.println("FA accepts 'aa': " + fa.accepts("aa"));
        System.out.println("FA accepts 'aab': " + fa.accepts("aab"));
        System.out.println("FA accepts 'aba': " + fa.accepts("aba"));

        Grammar grammar = fa.toRegularGrammar();
        System.out.println("\nGenerated Regular Grammar:");
        for (String left : grammar.productions.keySet()) {
            System.out.println(left + " -> " + grammar.productions.get(left));
        }

        System.out.println("\nIs FA deterministic? " + fa.isDeterministic());

        FiniteAutomation dfa = fa.convertToDFA();
        System.out.println("\nDFA transitions:");
        for (String state : dfa.transitions.keySet()) {
            System.out.println(state + " : " + dfa.transitions.get(state));
        }
        System.out.println("DFA final states: " + dfa.finalStates);
    }
}