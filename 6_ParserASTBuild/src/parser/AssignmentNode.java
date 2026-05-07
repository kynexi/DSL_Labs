package parser;

public class AssignmentNode extends ASTNode {
    public IdentifierNode identifier;
    public ASTNode expression;

    public AssignmentNode(IdentifierNode identifier, ASTNode expression) {
        this.identifier = identifier;
        this.expression = expression;
    }
}