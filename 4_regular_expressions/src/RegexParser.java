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
        StringBuilder sb = new StringBuilder();
        int balance = 0;
        List<String> options = new ArrayList<>();

        while (index < regex.length()) {
            char c = regex.charAt(index);
            if (c == '(') balance++;
            if (c == ')') balance--;

            if (c == '|' && balance == 0) {
                options.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }

            if (balance < 0) break; // end of current group
            index++;
        }
        options.add(sb.toString());

        for (String opt : options) {
            RegexParser parser = new RegexParser(opt);
            alt.options.add(parser.parseSequence());
        }

        return alt.options.size() == 1 ? alt.options.get(0) : alt;
    }

    private RegexNode applyQuantifier(RegexNode node) {
        if (index >= regex.length()) return node;

        char c = regex.charAt(index);

        if (c == '*') { index++; return new RepetitionNode(node, 0, 5); }
        if (c == '+') { index++; return new RepetitionNode(node, 1, 5); }
        if (c == '?') { index++; return new RepetitionNode(node, 0, 1); }
        if (c == '{') {
            index++;
            int num = 0;
            while (Character.isDigit(regex.charAt(index))) num = num*10 + (regex.charAt(index++)-'0');
            index++; // skip '}'
            return new RepetitionNode(node, num, num);
        }

        return node;
    }
}