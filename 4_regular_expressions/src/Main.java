public class Main {
    public static void main(String[] args) {

        String[] regexes = {
                "(a|b)(c|d)E+G?",
                "P(Q|R|S)T(UV|W|X)*Z+",
                "1(0|1)*2(3|4){5}36"
        };

        RegexGenerator generator = new RegexGenerator();

        for (String regex : regexes) {
            System.out.println("Regex: " + regex);

            for (int i = 0; i < 5; i++) {
                String result = generator.generate(regex);
                System.out.println("  -> " + result);
            }

        }
    }
}