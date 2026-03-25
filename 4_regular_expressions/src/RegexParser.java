import java.util.*;

public class RegexParser {

    private String regex;
    private int index = 0;

    public RegexParser(String regex) {
        this.regex = regex;
    }

    public RegexNode parse() {
        return parseSequence();
    }

    private RegexNode parseSequence() {
        SequenceNode sequence = new SequenceNode();

        while (index < regex.length() && regex.charAt(index) != ')') {
            sequence.nodes.add(parseElement());
        }

        return sequence.nodes.size() == 1 ? sequence.nodes.get(0) : sequence;
    }

    private RegexNode parseElement() {
        RegexNode node;

        if (regex.charAt(index) == '(') {
            index++; // skip '('
            node = parseAlternation();
            index++; // skip ')'
        } else {
            node = new LiteralNode(regex.charAt(index++));
        }

        return applyQuantifier(node);
    }

    private RegexNode parseAlternation() {
        AlternationNode alt = new AlternationNode();

        while (true) {
            alt.options.add(parseSequence());

            if (index >= regex.length() || regex.charAt(index) != '|') break;
            index++; // skip '|'
        }

        return alt.options.size() == 1 ? alt.options.get(0) : alt;
    }

    private RegexNode applyQuantifier(RegexNode node) {
        if (index >= regex.length()) return node;

        char c = regex.charAt(index);

        if (c == '*') {
            index++;
            return new RepetitionNode(node, 0, 5);
        } else if (c == '+') {
            index++;
            return new RepetitionNode(node, 1, 5);
        } else if (c == '?') {
            index++;
            return new RepetitionNode(node, 0, 1);
        } else if (c == '{') {
            index++;
            int num = 0;
            while (Character.isDigit(regex.charAt(index))) {
                num = num * 10 + (regex.charAt(index++) - '0');
            }
            index++; // skip '}'
            return new RepetitionNode(node, num, num);
        }

        return node;
    }
}