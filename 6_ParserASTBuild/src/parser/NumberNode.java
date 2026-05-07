package parser;

public class NumberNode extends ASTNode {
    public double value;

    public NumberNode(String value) {
        this.value = Double.parseDouble(value);
    }
}