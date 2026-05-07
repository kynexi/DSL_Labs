package parser;

import lexer.*;

import java.io.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        // 1. Read input
        String input = readFile("6_ParserASTBuild/src/parser/input.txt");

        // 2. Tokenize
        Tokenizer tokenizer = new Tokenizer(input);
        List<String> lexemes = tokenizer.tokenize();

        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyze(lexemes);

        // 3. Write tokens
        writeTokens(tokens, "output_tokens.txt");

        // 4. Parse AST
        Parser parser = new Parser(tokens);
        ASTNode ast = parser.parse();

        // 5. Write AST
        writeAST(ast, "output_ast.txt");
    }

    // ---------------- FILE I/O ----------------

    private static String readFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(" ");
        }

        br.close();
        return sb.toString().trim();
    }

    private static void writeTokens(List<Token> tokens, String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));

        for (Token t : tokens) {
            bw.write(t.toString());
            bw.newLine();
        }

        bw.close();
    }

    private static void writeAST(ASTNode ast, String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));

        printAST(ast, "", bw);

        bw.close();
    }

    private static void printAST(ASTNode node, String indent, BufferedWriter bw) throws IOException {

        if (node instanceof AssignmentNode) {
            AssignmentNode n = (AssignmentNode) node;
            bw.write(indent + "Assignment\n");
            bw.write(indent + "  Variable: " + n.identifier.name + "\n");
            bw.write(indent + "  Value:\n");
            printAST(n.expression, indent + "    ", bw);
        }

        else if (node instanceof BinOpNode) {
            BinOpNode n = (BinOpNode) node;
            bw.write(indent + "BinOp: " + n.operator.value + "\n");
            bw.write(indent + "  Left:\n");
            printAST(n.left, indent + "    ", bw);
            bw.write(indent + "  Right:\n");
            printAST(n.right, indent + "    ", bw);
        }

        else if (node instanceof NumberNode) {
            NumberNode n = (NumberNode) node;
            bw.write(indent + "Number: " + n.value + "\n");
        }

        else if (node instanceof IdentifierNode) {
            IdentifierNode n = (IdentifierNode) node;
            bw.write(indent + "Identifier: " + n.name + "\n");
        }

        else if (node instanceof FunctionNode) {
            FunctionNode n = (FunctionNode) node;
            bw.write(indent + "Function: " + n.function.value + "\n");
            bw.write(indent + "  Arg:\n");
            printAST(n.argument, indent + "    ", bw);
        }
    }
}