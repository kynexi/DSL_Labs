package parser;

import lexer.Token;
import lexer.TokenType;
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private int pos = 0;
    private Token current;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = tokens.get(pos);
    }

    private void eat(TokenType type) {
        if (current.type == type) {
            pos++;
            if (pos < tokens.size()) {
                current = tokens.get(pos);
            }
        } else {
            throw new RuntimeException("Expected " + type + " but got " + current.type);
        }
    }

    public ASTNode parse() {
        return statement();
    }

    private ASTNode statement() {
        Token id = current;
        eat(TokenType.IDENTIFIER);

        eat(TokenType.ASSIGN);

        ASTNode expr = expression();

        return new AssignmentNode(new IdentifierNode(id.value), expr);
    }

    private ASTNode expression() {
        ASTNode node = term();

        while (current.type == TokenType.PLUS || current.type == TokenType.MINUS) {
            Token op = current;
            eat(op.type);
            node = new BinOpNode(node, op, term());
        }

        return node;
    }

    private ASTNode term() {
        ASTNode node = factor();

        while (current.type == TokenType.MULTIPLY || current.type == TokenType.DIVIDE) {
            Token op = current;
            eat(op.type);
            node = new BinOpNode(node, op, factor());
        }

        return node;
    }

    private ASTNode factor() {
        Token token = current;

        if (token.type == TokenType.INT || token.type == TokenType.FLOAT) {
            eat(token.type);
            return new NumberNode(token.value);
        }

        if (token.type == TokenType.IDENTIFIER) {
            eat(TokenType.IDENTIFIER);
            return new IdentifierNode(token.value);
        }

        if (token.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN);
            ASTNode node = expression();
            eat(TokenType.RPAREN);
            return node;
        }

        if (token.type == TokenType.SIN || token.type == TokenType.COS) {
            Token func = token;
            eat(token.type);

            eat(TokenType.LPAREN);
            ASTNode arg = expression();
            eat(TokenType.RPAREN);

            return new FunctionNode(func, arg);
        }

        throw new RuntimeException("Invalid syntax: " + token.value);
    }
}