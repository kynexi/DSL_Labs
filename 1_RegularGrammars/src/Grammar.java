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
    }