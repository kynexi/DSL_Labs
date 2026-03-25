# Laboratory Work 4, Regular Expressions

### Course: Formal Languages & Finite Automata

### Author: Brînză Vasile

### Group: FAF-242

----

## Theory

Regular expressions, often abbreviated as regex or regexp, are powerful tools used in computer science and programming for pattern matching within strings of text.
They provide a concise and flexible means of searching, extracting, and manipulating textual data based on specific patterns.
Utilized across various programming languages and text processing utilities, regular expressions enable developers to perform tasks such as validation of input, searching for specific patterns within large datasets, and text manipulation with precision and efficiency.
The syntax of regular expressions consists of a combination of literal characters and metacharacters, forming patterns that define the desired matches.
With their versatility and widespread adoption, regular expressions serve as an indispensable tool for tasks ranging from simple string manipulation to complex data extraction and transformation.
However, mastering regular expressions requires understanding their syntax, metacharacters, and application-specific nuances to leverage their full potential effectively.

## Objectives:

1. Write and cover what regular expressions are, what they are used for.
2. Take your variant code
3. Write a code that will generate valid combinations of symbols conform given regular expressions (examples will be shown).
4. In case you have an example, where symbol may be written undefined number of times, take a limit of 5 times (to evade generation of extremely long combinations)
5. Bonus point: write a function that will show sequence of processing regular expression (like, what you do first, second and so on)

## Implementation description

> RegexParser.java

```java
RegexParser parser = new RegexParser("(a|b)(c|d)E+G?");
RegexNode root = parser.parse();
```
The parser reads a regex and splits it into nodes according to the structure:
- SequenceNode represents (a|b)(c|d)E+G? as a sequence of four nodes.
- AlternationNode represents a|b and c|d.
- RepetitionNode represents E+ and G?.

This structure allows the generator to traverse and build the string dynamically.

> Generator.java

```java
public String generate(RegexNode node) {
    if (node instanceof LiteralNode) return String.valueOf(((LiteralNode) node).value);
    if (node instanceof SequenceNode) {
        StringBuilder sb = new StringBuilder();
        for (RegexNode child : ((SequenceNode) node).nodes) sb.append(generate(child));
        return sb.toString();
    }
    if (node instanceof AlternationNode) {
        List<RegexNode> options = ((AlternationNode) node).options;
        return generate(options.get(new Random().nextInt(options.size())));
    }
    if (node instanceof RepetitionNode) {
        RepetitionNode rep = (RepetitionNode) node;
        int count = new Random().nextInt(rep.max - rep.min + 1) + rep.min;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(generate(rep.node));
        return sb.toString();
    }
    return "";
}
```

The generator uses recursion to produce valid outputs from the regex tree:

The program respects the quantifiers:

- * → 0–5 repetitions,
- + → 1–5 repetitions,
-   ? → 0 or 1 repetition,
-  {n} → exactly n repetitions.

> ProcessingLogger.java

```java
List<String> steps = ProcessingLogger.explain("(a|b)(c|d)E+G?");
for (String step : steps) System.out.println(step);
```

To help understand how strings are generated, the ProcessingLogger traverses the same regex tree and prints the steps:

It shows the sequence of nodes, which alternation branch was chosen, and how many repetitions occurred:

```
Sequence start
  Alternation start, choosing one option
    Literal: 'b'
  Alternation end
  Alternation start, choosing one option
    Literal: 'c'
  Alternation end
  Repetition: repeat 4 times (min=1, max=5)
    Literal: 'E'
  End repetition
  Repetition: repeat 1 times (min=0, max=1)
    Literal: 'G'
  End repetition
Sequence end
```

#### Input

```
(a|b)(c|d)E+G?
P(Q|R|S)T(UV|W|X)*Z+
1(0|1)*2(3|4){5}36
```
Note: The regex notation used in Java is slightly different from the variant provided.

#### Output example

```
Regex: (a|b)(c|d)E+G?
  -> adEE
  -> bcEEEEE
  -> bdEG
  -> bcEEEE
  -> acEE
---

Processing steps:
  Parsing and generation steps for regex: (a|b)(c|d)E+G?
  Sequence start
    Alternation start, choosing one option
      Literal: 'a'
    Alternation end
    Alternation start, choosing one option
      Literal: 'd'
    Alternation end
    Repetition: repeat 5 times (min=1, max=5)
      Literal: 'E'
      Literal: 'E'
      Literal: 'E'
      Literal: 'E'
      Literal: 'E'
    End repetition
    Repetition: repeat 0 times (min=0, max=1)
    End repetition
  Sequence end

----------------------
```

# Conclusion

For this project, I implemented a Java program that can generate valid strings from complex regular expressions. The program is designed to work dynamically, meaning it can take any set of regexes (in standard syntax) as input and produce valid outputs without hardcoding the results. I also included a processing logger that explains step by step how the program generates each string.


