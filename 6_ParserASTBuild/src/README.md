# Laboratory Work 6, Parser & Building an Abstract Syntax Tree

### Course: Formal Languages & Finite Automata
### Author: Brînză Vasile
### Group: FAF-242

----

## Theory

---
An abstract syntax tree (AST) is a data structure used in computer science to represent the structure of a program or code snippet. It is a tree representation of the abstract syntactic structure of text (often source code) written in a formal language. Each node of the tree denotes a construct occurring in the text.
It is sometimes called just a syntax tree. The syntax is "abstract" in the sense that it does not represent every detail appearing in the real syntax, but rather just the structural or content-related details.
For instance, grouping parentheses are implicit in the tree structure, so these do not have to be represented as separate nodes. Likewise, a syntactic construct like an if-condition-then statement may be denoted by means of a single node with three branches.
This distinguishes abstract syntax trees from concrete syntax trees, traditionally designated parse trees. Parse trees are typically built by a parser during the source code translation and compiling process.
Once built, additional information is added to the AST by means of subsequent processing, e.g., contextual analysis. Abstract syntax trees are also used in program analysis and program transformation systems.
Abstract syntax trees are data structures widely used in compilers to represent the structure of program code. An AST is usually the result of the syntax analysis phase of a compiler.
It often serves as an intermediate representation of the program through several stages that the compiler requires, and has a strong impact on the final output of the compiler.

## Objectives

1. Get familiar with parsing, what it is and how it can be programmed.
2. Get familiar with the concept of AST.
3. In addition to what has been done in the 3rd lab work do the following:
    1. In case you didn't have a type that denotes the possible types of tokens you need to:
        1. Have a type ```TokenType``` (like an enum) that can be used in the lexical analysis to categorize the tokens.
        2. Please use regular expressions to identify the type of the token.
    2. Implement the necessary data structures for an AST that could be used for the text you have processed in the 3rd lab work.
    3. Implement a simple parser program that could extract the syntactic information from the input text.

## Implementation description

---

> ASTNode

```java
public abstract class ASTNode {}
```

Base class for all AST elements. Every node in the syntax tree extends this class.


> NumberNode

```java
public class NumberNode extends ASTNode {
    public double value;

    public NumberNode(String value) {
        this.value = Double.parseDouble(value);
    }
}
```

Represents numeric values (INT and FLOAT).

> IdentifierNode

```java
public class IdentifierNode extends ASTNode {
    public String name;

    public IdentifierNode(String name) {
        this.name = name;
    }
}
```

Represents variables like x, y, etc.

> BinOpNode

```java
import lexer.Token;

public class BinOpNode extends ASTNode {
    public ASTNode left;
    public Token operator;
    public ASTNode right;

    public BinOpNode(ASTNode left, Token operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}
```

Represents binary operations (+, -, *, /).

> FunctionNode

```java
import lexer.Token;

public class FunctionNode extends ASTNode {
    public Token function;
    public ASTNode argument;

    public FunctionNode(Token function, ASTNode argument) {
        this.function = function;
        this.argument = argument;
    }
}
```

Represents sin() and cos() calls.

> AssignmentNode

```java
public class AssignmentNode extends ASTNode {
    public IdentifierNode identifier;
    public ASTNode expression;

    public AssignmentNode(IdentifierNode identifier, ASTNode expression) {
        this.identifier = identifier;
        this.expression = expression;
    }
}

```

Represents assignments like x = expression.

> Parser

```java
ASTNode factor() {
    Token t = current;

    if (t.type == TokenType.INT || t.type == TokenType.FLOAT) {
        eat(t.type);
        return new NumberNode(t.value);
    }

    if (t.type == TokenType.IDENTIFIER) {
        eat(TokenType.IDENTIFIER);
        return new IdentifierNode(t.value);
    }

    if (t.type == TokenType.LPAREN) {
        eat(TokenType.LPAREN);
        ASTNode n = expression();
        eat(TokenType.RPAREN);
        return n;
    }

    if (t.type == TokenType.SIN || t.type == TokenType.COS) {
        Token f = t;
        eat(t.type);
        eat(TokenType.LPAREN);
        ASTNode arg = expression();
        eat(TokenType.RPAREN);
        return new FunctionNode(f, arg);
    }

    throw new RuntimeException();
}
```

The parser uses recursive descent to transform tokens into an AST, following operator precedence and supporting functions.

It follows this grammar:

```
statement → IDENTIFIER = expression  
expression → term ((+ | -) term)*  
term → factor ((* | /) factor)*  
factor → INT | FLOAT | IDENTIFIER | (expression) | sin(expression) | cos(expression)
```

### Example Output

`input`

```
x = sin(3.14) + cos(2) * 4.5
```

`output`

```
Assignment
  Variable: x
  Value:
    BinOp: +
      Left:
        Function: sin
      Right:
        BinOp: *
          Left:
            Function: cos
          Right:
            Number: 4.5
```


## Conclusions

The parser transforms a linear token stream into a structured AST. It respects operator precedence, supports nested expressions, and handles function calls like sin and cos. This forms the syntactic analysis stage of a basic compiler pipeline.

