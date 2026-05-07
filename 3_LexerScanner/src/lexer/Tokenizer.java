package lexer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private String input;

    public Tokenizer(String input) {
        this.input = input;
    }

    public List<String> tokenize() {

        List<String> lexemes = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (char c : input.toCharArray()) {

            if (Character.isWhitespace(c)) {
                if (current.length() > 0) {
                    lexemes.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            if ("+-*/()=".indexOf(c) >= 0) {

                if (current.length() > 0) {
                    lexemes.add(current.toString());
                    current.setLength(0);
                }

                lexemes.add(String.valueOf(c));
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            lexemes.add(current.toString());
        }

        return lexemes;
    }
}