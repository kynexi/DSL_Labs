# Laboratory Work 1, Grammars and Finite Automata

### Course: Formal Languages & Finite Automata
### Author: Brînză Vasile
### Group: FAF-242

----

## Theory

In formal language theory, there is a close relationship between grammars and automata. 
A grammar defines a language by generating strings, while an automaton recognizes or accepts strings from a language. 
The conversion from a grammar to a finite automaton enables us to verify whether a given string belongs to the language defined by the grammar.

## Objectives:

* Discover what a language is and what it needs to have to be considered a formal one;
* Provide the initial setup for the evolving project;
* According to the variant number, get the grammar definition and do the following:

  a. Implement a type/class for your grammar;

  b. Add one function that would generate 5 valid strings from the language expressed by your given grammar; 

  c. Implement some functionality that would convert an object of type Grammar to one of type Finite Automaton; 

  d. For the Finite Automaton, please add a method that checks if an input string can be obtained via the state transition from it.

## Implementation description

#### Grammar Class:
This class is responsible for storing the data about the symbols that compose the grammar and use them to generate strings or transform to FiniteAutomaton.

```java
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
```

#### Finite Automaton Class

This class represents the Finite Automaton and it allows checking if a string belongs to a language

```java
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

```

## Conclusions

<div align="center">
  <img src="resources/img.png" alt="Result of a run" width="30%">
  <p>Figure 1 - Result of a run</p>
</div>

This lab showed how a grammar can generate strings and a finite automaton can check if a string belongs to the same language. Converting the grammar to an automaton confirmed they represent the same language. It helped understand how formal language theory works in practice.