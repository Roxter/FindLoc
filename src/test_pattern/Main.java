package test_pattern;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private final File source_file;

    public static void main(String[] args) throws IOException {
        File f = new File("./in.txt");
        Main m = new Main(f);
        m.readStream();
    }

    Main(File source_file) {
        this.source_file = source_file;
    }

    private void readStream() throws IOException {
        BufferedReader reader_obj = new BufferedReader(new InputStreamReader(new FileInputStream(source_file)));
        String rd_line;

        while ((rd_line = reader_obj.readLine()) != null) {
//            Pattern pattern_rd_line = Pattern.compile("^.*?(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*).*?$", Pattern.CASE_INSENSITIVE);
            Pattern pattern_rd_line = Pattern.compile("^.*?(\\d+)\\s+(\\d+).*$", Pattern.CASE_INSENSITIVE);
            Matcher matcher_rd_line = pattern_rd_line.matcher(rd_line);           // инииализация матчинга
            System.out.println(rd_line);

//            num_ex = Integer.parseInt(matcher_rd_line.group(1));
//            hit_1 = Integer.parseInt(matcher_rd_line.group(2));
//            hit_2 = Integer.parseInt(matcher_rd_line.group(3));
//            hit_3 = Integer.parseInt(matcher_rd_line.group(4));
//            hit_4 = Integer.parseInt(matcher_rd_line.group(5));
//            x = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(6))));
//            y = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(7))));
//            v = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(8))));
//            amp = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(9))));

            System.out.print("detected on pattern: ");
            if (matcher_rd_line.find()) {
                for (int i = 1; ; i++) { try {
                    System.out.print("group" + i + ": " + matcher_rd_line.group(i) + " ");
                } catch (IndexOutOfBoundsException e) {
                    break;
                } }
//                System.out.print(matcher_rd_line.group(1) + " " + matcher_rd_line.group(2) + " " + matcher_rd_line.group(3) + " " + matcher_rd_line.group(4) + " " + matcher_rd_line.group(5) + " " + matcher_rd_line.group(6) + " " + matcher_rd_line.group(7) + " " + matcher_rd_line.group(8) + " " + matcher_rd_line.group(9));
            }

            System.out.println("\n");
        }
    }
}
