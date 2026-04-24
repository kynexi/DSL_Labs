import java.util.*;

abstract class RegexNode {}

class LiteralNode extends RegexNode {
    char value;

    LiteralNode(char value) {
        this.value = value;
    }
}

class SequenceNode extends RegexNode {
    List<RegexNode> nodes = new ArrayList<>();
}

class AlternationNode extends RegexNode {
    List<RegexNode> options = new ArrayList<>();
}

class RepetitionNode extends RegexNode {
    RegexNode node;
    int min;
    int max;

    RepetitionNode(RegexNode node, int min, int max) {
        this.node = node;
        this.min = min;
        this.max = max;
    }
}