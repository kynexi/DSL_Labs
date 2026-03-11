import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public List<Token> analyze(List<String> lexemes) {

        List<Token> tokens = new ArrayList<>();

        for (String lexeme : lexemes) {

            if (lexeme.matches("\\d+")) {
                tokens.add(new Token(TokenType.INT, lexeme));
            }

            else if (lexeme.matches("\\d+\\.\\d+")) {
                tokens.add(new Token(TokenType.FLOAT, lexeme));
            }

            else if (lexeme.equals("+")) {
                tokens.add(new Token(TokenType.PLUS, lexeme));
            }

            else if (lexeme.equals("-")) {
                tokens.add(new Token(TokenType.MINUS, lexeme));
            }

            else if (lexeme.equals("*")) {
                tokens.add(new Token(TokenType.MULTIPLY, lexeme));
            }

            else if (lexeme.equals("/")) {
                tokens.add(new Token(TokenType.DIVIDE, lexeme));
            }

            else if (lexeme.equals("(")) {
                tokens.add(new Token(TokenType.LPAREN, lexeme));
            }

            else if (lexeme.equals(")")) {
                tokens.add(new Token(TokenType.RPAREN, lexeme));
            }

            else if (lexeme.equals("=")) {
                tokens.add(new Token(TokenType.ASSIGN, lexeme));
            }

            else if (lexeme.equals("sin")) {
                tokens.add(new Token(TokenType.SIN, lexeme));
            }

            else if (lexeme.equals("cos")) {
                tokens.add(new Token(TokenType.COS, lexeme));
            }

            else {
                tokens.add(new Token(TokenType.IDENTIFIER, lexeme));
            }
        }

        return tokens;
    }
}