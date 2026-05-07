package parser;

import lexer.Token;

public class FunctionNode extends ASTNode {
    public Token function;
    public ASTNode argument;

    public FunctionNode(Token function, ASTNode argument) {
        this.function = function;
        this.argument = argument;
    }
}