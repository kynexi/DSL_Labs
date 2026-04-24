package cnf;

import java.util.ArrayList;
import java.util.List;

// Represents a single production rule: LHS → symbol1 symbol2 … symbolN
// A RHS that is an empty list (or contains only "ε") represents an ε-production.

public class Production {

    private final String lhs;
    private final List<String> rhs;

    public Production(String lhs, List<String> rhs) {
        this.lhs = lhs;
        this.rhs = new ArrayList<>(rhs);
    }

    public String getLhs() {
        return lhs;
    }

    public List<String> getRhs() {
        return new ArrayList<>(rhs);
    }

    // Returns true when the RHS is empty or a bare ε symbol.
    public boolean isEpsilon() {
        return rhs.isEmpty() || (rhs.size() == 1 && rhs.get(0).equals("ε"));
    }

    // Returns true when the RHS is a single non-terminal (unit / renaming rule).
    public boolean isUnit(java.util.Set<String> nonTerminals) {
        return rhs.size() == 1 && nonTerminals.contains(rhs.get(0));
    }

    // Returns the single non-terminal on the RHS, assuming isUnit() is true
    public String getUnitTarget() {
        return rhs.get(0);
    }

    @Override
    public String toString() {
        String rhsStr = isEpsilon() ? "ε" : String.join(" ", rhs);
        return lhs + " → " + rhsStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Production)) return false;
        Production other = (Production) o;
        return lhs.equals(other.lhs) && rhs.equals(other.rhs);
    }

    @Override
    public int hashCode() {
        return 31 * lhs.hashCode() + rhs.hashCode();
    }
}