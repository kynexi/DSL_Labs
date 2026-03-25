public class RegexGenerator {

    public String generate(String regex) {
        RegexParser parser = new RegexParser(regex);
        RegexNode root = parser.parse();
        Generator generator = new Generator();
        return generator.generate(root);
    }
}