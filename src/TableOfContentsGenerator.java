import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
            content.append(line).append(LINE_SEPARATOR);
            if (line.length() == 0 || line.charAt(0) == '\t' || line.startsWith("    ")) {
                prevNonHeaderLine = null;
                continue;
            }
            prevNonHeaderLine = addHeader(tableOfContents, prevNonHeaderLine, line.trim());
        }
    }

    private static String addHeader(final StringBuilder tableOfContents, String prevNonHeaderLine, final String line) {
        if (line.matches("#{1,6}\\s+?[\\S&&[^#]]+?.*")) {
            String tmp = line.trim();
            int headerLvl = tmp.indexOf(' ');
            int end = tmp.length() - 1;
            while (tmp.charAt(end) == '#') {
                end--;
            }
            tableOfContents.append(makeHeader(headerLvl, tmp.substring(headerLvl, end + 1).trim()));
            return null;
        } else if (prevNonHeaderLine != null && line.matches("=+?\\s*")) {
            tableOfContents.append(makeHeader(1, prevNonHeaderLine));
            return null;
        } else if (prevNonHeaderLine != null && line.matches("-+?\\s*")) {
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
