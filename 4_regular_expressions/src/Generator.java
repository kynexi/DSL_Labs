import java.util.*;

public class Generator {

    private Random random = new Random();

    public String generate(RegexNode node) {
        if (node instanceof LiteralNode) {
            return String.valueOf(((LiteralNode) node).value);
        }

        if (node instanceof SequenceNode) {
            StringBuilder sb = new StringBuilder();
            for (RegexNode n : ((SequenceNode) node).nodes) {
                sb.append(generate(n));
            }
            return sb.toString();
        }

        if (node instanceof AlternationNode) {
            List<RegexNode> options = ((AlternationNode) node).options;
            return generate(options.get(random.nextInt(options.size())));
        }

        if (node instanceof RepetitionNode) {
            RepetitionNode rep = (RepetitionNode) node;
            int count = random.nextInt(rep.max - rep.min + 1) + rep.min;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                sb.append(generate(rep.node));
            }
            return sb.toString();
        }

        return "";
    }
}