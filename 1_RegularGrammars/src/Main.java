import java.util.*;

public class Main {

    public static void main(String[] args) {

        // Variant 5
        Set<String> VN = new HashSet<>(Arrays.asList("S", "F", "L"));
        Set<String> VT = new HashSet<>(Arrays.asList("a", "b", "c", "d"));

        // P
        Map<String, List<String>> productions = new HashMap<>();
        productions.put("S", Arrays.asList("bS", "aF", "d"));
        productions.put("F", Arrays.asList("cF", "dF", "aL", "b"));
        productions.put("L", Arrays.asList("aL", "c"));
        Grammar grammar = new Grammar(VN, VT, productions, "S");

        System.out.println("Generated strings:");
        for (String s : grammar.generateFiveStrings()) {
            System.out.println(s);
        }

        FiniteAutomation fa = grammar.toFA();

        System.out.println("\nAcceptance tests:");
        System.out.println("d -> " + fa.accepts("d"));
        System.out.println("bd -> " + fa.accepts("bd"));
        System.out.println("ab -> " + fa.accepts("ab"));
        System.out.println("aac -> " + fa.accepts("aac"));
        System.out.println("bbbbd -> " + fa.accepts("bbbbd"));
        System.out.println("xyz -> " + fa.accepts("xyz"));
        System.out.println("hamham -> " + fa.accepts("xyz"));
    }
}
