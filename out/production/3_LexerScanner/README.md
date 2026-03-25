# Laboratory Work 3, Lexer & Scanner

### Course: Formal Languages & Finite Automata
### Author: Brînză Vasile
### Group: FAF-242

----

## Theory

---
The term lexer comes from lexical analysis which, in turn, represents the process of extracting lexical tokens from a string of characters. There are several alternative names for the mechanism called lexer, for example tokenizer or scanner. 

The lexical analysis is one of the first stages used in a compiler/interpreter when dealing with programming, markup or other types of languages. The tokens are identified based on some rules of the language and the products that the lexer gives are called lexemes. So basically the lexer is a stream of lexemes. Now in case it is not clear what's the difference between lexemes and tokens, there is a big one. 

The lexeme is just the byproduct of splitting based on delimiters, for example spaces, but the tokens give names or categories to each lexeme. So the tokens don't retain necessarily the actual value of the lexeme, but rather the type of it and maybe some metadata.

## Objectives

1. Understand what lexical analysis is.
2. Get familiar with the inner workings of a lexer/scanner/tokenizer.
3. Implement a sample lexer and show how it works.


## Implementation description

---

### TokenType Enum

```java
public enum TokenType {
    INT,
    FLOAT,
    IDENTIFIER,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,

    LPAREN,
    RPAREN,

    ASSIGN,

    SIN,
    COS
}
```

Defines all possible token categories the lexer can produce, including numbers, identifiers, operators, parentheses, assignment, and keywords. 

Using an enum ensures tokens are structured, consistent, and easy to reference in the lexer and parser.



### Token Class

```java
public class Token {

    public TokenType type;
    public String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return type + "(" + value + ")";
    }
}
```
A Token stores what the piece of code is (its type) and its actual text (its value). For example, 3.14 is a FLOAT, and x is an IDENTIFIER. The toString() lets us print it nicely.


### Tokenizer Class

```java
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
```

The Tokenizer just cuts the code into pieces called lexemes. For example, `x = sin(3.14)` becomes `["x", "=", "sin", "(", "3.14", ")"]`. At this stage, we don’t yet know what type each piece is, we just split them.


### Lexer Class

```java
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public List<Token> analyze(List<String> lexemes) {

        List<Token> tokens = new ArrayList<>();

        for (String lexeme : lexemes) {

            if (lexeme.matches("\\d+")) {
                tokens.add(new Token(TokenType.INT, lexeme));
            }

            else if (lexeme.matches("\\d+\\.\\d+")) {
                tokens.add(new Token(TokenType.FLOAT, lexeme));
            }

            else if (lexeme.equals("+")) {
                tokens.add(new Token(TokenType.PLUS, lexeme));
            }

            else if (lexeme.equals("-")) {
                tokens.add(new Token(TokenType.MINUS, lexeme));
            }

            else if (lexeme.equals("*")) {
                tokens.add(new Token(TokenType.MULTIPLY, lexeme));
            }

            else if (lexeme.equals("/")) {
                tokens.add(new Token(TokenType.DIVIDE, lexeme));
            }

            else if (lexeme.equals("(")) {
                tokens.add(new Token(TokenType.LPAREN, lexeme));
            }

            else if (lexeme.equals(")")) {
                tokens.add(new Token(TokenType.RPAREN, lexeme));
            }

            else if (lexeme.equals("=")) {
                tokens.add(new Token(TokenType.ASSIGN, lexeme));
            }

            else if (lexeme.equals("sin")) {
                tokens.add(new Token(TokenType.SIN, lexeme));
            }

            else if (lexeme.equals("cos")) {
                tokens.add(new Token(TokenType.COS, lexeme));
            }

            else {
                tokens.add(new Token(TokenType.IDENTIFIER, lexeme));
            }
        }

        return tokens;
    }
}
```

The Lexer looks at each lexeme and decides what type it is. For example, "3.14" becomes a FLOAT, "sin" becomes a SIN function, and "x" becomes an IDENTIFIER. Now we have structured information, not just plain strings.

```java
if (lexeme.matches("\\d+")) { ... }      // INT
else if (lexeme.matches("\\d+\\.\\d+")) { ... } // FLOAT
else if (lexeme.equals("sin")) { ... }   // SIN
else { ... }                             // IDENTIFIER
```

This part of the Lexer checks each lexeme to see what it is. Numbers with a dot are FLOAT, numbers without a dot are INT, "sin" is a function, and anything else is a variable name. This is how the lexer knows how to classify the code.

### Example Output

`input`

```
x = sin(3.14) + cos(2) * 4.5
```

`output`

```
Lexemes:
x
=
sin
(
3.14
)
+
cos
(
2
)
*
4.5

Tokens:
IDENTIFIER(x)
ASSIGN(=)
SIN(sin)
LPAREN(()
FLOAT(3.14)
RPAREN())
PLUS(+)
COS(cos)
LPAREN(()
INT(2)
RPAREN())
MULTIPLY(*)
FLOAT(4.5)

```


## Conclusions

In this project, I learned how a program can take raw text and turn it into a structured set of tokens that a computer can understand. First, the Tokenizer splits the input into small pieces called lexemes, like numbers, operators, or function names. Then, the Lexer looks at each piece and decides what type it is, giving it meaning, like INT, FLOAT, IDENTIFIER, or SIN.

By reading from a file and writing the output to another file, I also learned how to handle real inputs and save results for checking. This separation of responsibilities makes the code easier to understand and maintain.

Overall, this project helped me understand the first step of how compilers or interpreters process code, and it showed me the importance of breaking a task into smaller steps, like first splitting the text, then classifying it. It also made me realize that even a small lexer needs careful rules to handle numbers, operators, variables, and functions correctly.