package parser;

import lexer.Token;

public class BinOpNode extends ASTNode {
    public ASTNode left;
    public Token operator;
    public ASTNode right;

    public BinOpNode(ASTNode left, Token operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}