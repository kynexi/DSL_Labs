package parser;

public class ASTPrinter {

    public static void print(ASTNode node, String indent) {

        if (node instanceof AssignmentNode) {
            AssignmentNode n = (AssignmentNode) node;
            System.out.println(indent + "Assignment");
            System.out.println(indent + "  Variable: " + n.identifier.name);
            System.out.println(indent + "  Value:");
            print(n.expression, indent + "    ");
        }

        else if (node instanceof BinOpNode) {
            BinOpNode n = (BinOpNode) node;
            System.out.println(indent + "BinOp: " + n.operator.value);
            System.out.println(indent + "  Left:");
            print(n.left, indent + "    ");
            System.out.println(indent + "  Right:");
            print(n.right, indent + "    ");
        }

        else if (node instanceof NumberNode) {
            NumberNode n = (NumberNode) node;
            System.out.println(indent + "Number: " + n.value);
        }

        else if (node instanceof IdentifierNode) {
            IdentifierNode n = (IdentifierNode) node;
            System.out.println(indent + "Identifier: " + n.name);
        }

        else if (node instanceof FunctionNode) {
            FunctionNode n = (FunctionNode) node;
            System.out.println(indent + "Function: " + n.function.value);
            System.out.println(indent + "  Arg:");
            print(n.argument, indent + "    ");
        }
    }
}