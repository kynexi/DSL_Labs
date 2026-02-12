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

}