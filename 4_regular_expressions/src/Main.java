import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("4_regular_expressions/src/input.txt"));
        List<String> regexes = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) regexes.add(line);
        }
        br.close();

        PrintWriter pw = new PrintWriter(new FileWriter("4_regular_expressions/src/output.txt"));
        RegexGenerator generator = new RegexGenerator();

        for (String regex : regexes) {
            pw.println("Regex: " + regex);
            for (int i = 0; i < 5; i++) {
                String result = generator.generate(regex);
                pw.println("  -> " + result);
            }

            pw.println("\n----------------------\n");
        }

        pw.close();
        System.out.println("generation complete");
    }
}