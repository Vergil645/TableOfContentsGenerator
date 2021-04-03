import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

public class TableOfContentsGenerator {
    /*
        Precondition:
            args.length == 1 && args[0] -- correct name of markdown file
        Postcondition:
            File with name args[0] isn't changed
            Print to standard output (System.out) the table of contents for the specified file
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: TableOfContentsGenerator input_file");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
            System.out.println(generateTableOfContents(reader));
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
        }
    }

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final int supHeaderLvl = 7;
    private static final int[] number = new int[supHeaderLvl];

    private static String generateTableOfContents(final BufferedReader reader) throws IOException {
        StringBuilder tableOfContents = new StringBuilder();
        StringBuilder content = new StringBuilder();

        Arrays.fill(number, 1);
        String prevNonHeaderLine = null;
        String line;

        while (true) {
            line = reader.readLine();
            if (line == null) {
                return tableOfContents.append(LINE_SEPARATOR).append(content).toString();
            }
            prevNonHeaderLine = addHeader(tableOfContents, prevNonHeaderLine, line.strip());
            content.append(line).append(LINE_SEPARATOR);
        }
    }

    private static String addHeader(final StringBuilder tableOfContents, String prevNonHeaderLine, final String line) {
        if (Pattern.matches("#{1,6}\\s+?[\\S&&[^#]]+?.*", line)) {
            int headerLvl = line.indexOf(' ');
            int end = line.length() - 1;
            while (line.charAt(end) == '#') {
                end--;
            }
            tableOfContents.append(makeHeader(headerLvl, line.substring(headerLvl, end + 1).strip()));
            return null;
        } else if (prevNonHeaderLine != null && Pattern.matches("=+?.*", line)) {
            tableOfContents.append(makeHeader(1, prevNonHeaderLine));
            return null;
        } else if (prevNonHeaderLine != null && Pattern.matches("-+?.*", line)) {
            tableOfContents.append(makeHeader(2, prevNonHeaderLine));
            return null;
        } else {
            return line;
        }
    }

    private static String makeHeader(final int lvl, final String line) {
        StringBuilder res = new StringBuilder();
        res.append("\t".repeat(lvl - 1)).append(number[lvl]++).append(". [");
        Arrays.fill(number, lvl + 1, supHeaderLvl, 1);
        res.append(line).append("](#");
        res.append(line.toLowerCase().replace(' ', '-'));
        res.append(')').append(LINE_SEPARATOR);
        return res.toString();
    }
}
