import java.util.*;

public class ProcessingLogger {

    public static List<String> explainNode(RegexNode node) {
        List<String> steps = new ArrayList<>();
        explainNodeHelper(node, steps, 0);
        return steps;
    }

    private static void explainNodeHelper(RegexNode node, List<String> steps, int level) {
        String indent = "  ".repeat(level);

        if (node instanceof LiteralNode) {
            steps.add(indent + "Literal: '" + ((LiteralNode) node).value + "'");
        } else if (node instanceof SequenceNode) {
            steps.add(indent + "Sequence start");
            for (RegexNode child : ((SequenceNode) node).nodes) {
                explainNodeHelper(child, steps, level + 1);
            }
            steps.add(indent + "Sequence end");
        } else if (node instanceof AlternationNode) {
            steps.add(indent + "Alternation start, choosing one option");
            List<RegexNode> options = ((AlternationNode) node).options;
            // Randomly pick an option to reflect what was generated
            RegexNode chosen = options.get(new Random().nextInt(options.size()));
            explainNodeHelper(chosen, steps, level + 1);
            steps.add(indent + "Alternation end");
        } else if (node instanceof RepetitionNode) {
            RepetitionNode rep = (RepetitionNode) node;
            int count = new Random().nextInt(rep.max - rep.min + 1) + rep.min;
            steps.add(indent + "Repetition: repeat " + count + " times (min=" + rep.min + ", max=" + rep.max + ")");
            for (int i = 0; i < count; i++) {
                explainNodeHelper(rep.node, steps, level + 1);
            }
            steps.add(indent + "End repetition");
        }
    }

    public static List<String> explain(String regex) {
        RegexParser parser = new RegexParser(regex);
        RegexNode root = parser.parse();
        List<String> steps = new ArrayList<>();
        steps.add("Parsing and generation steps for regex: " + regex);
        steps.addAll(explainNode(root));
        return steps;
    }
}