package lexer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        String code = Files.readString(Paths.get("3_LexerScanner/src/input.txt"));

        Tokenizer tokenizer = new Tokenizer(code);
        List<String> lexemes = tokenizer.tokenize();

        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyze(lexemes);

        StringBuilder output = new StringBuilder();

        output.append("Lexemes:\n");
        for (String lex : lexemes) {
            output.append(lex).append("\n");
        }

        output.append("\nTokens:\n");
        for (Token token : tokens) {
            output.append(token).append("\n");
        }

        Files.writeString(Paths.get("3_LexerScanner/src/output.txt"), output.toString());
    }
}