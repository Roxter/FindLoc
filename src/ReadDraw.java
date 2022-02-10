import javafx.scene.shape.Circle;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ReadDraw {

    private File source_file;
    private File out_file;
    private List<Integer[]> mainParamsList;

    ReadDraw(File sourceFile, List<Integer[]> mainParamsList) throws IOException, ParseException {
        this.source_file = sourceFile;
        this.mainParamsList = mainParamsList;
        this.mainParamsList = new ArrayList<>();
        start();
    }

    private void start() throws IOException, ParseException {
        out_file = new File("out_" + source_file.getName());

        BufferedReader reader_obj = new BufferedReader(new InputStreamReader(new FileInputStream(source_file)));
        BufferedWriter writer_obj = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out_file)));

        runStream(reader_obj, writer_obj);

        writer_obj.close();
    }

    private void runStream (BufferedReader reader_obj, BufferedWriter writer_obj) throws IOException, ParseException {
        String rd_line;

        try {
            while ((rd_line = reader_obj.readLine()) != null) {                        // построчный проход текстового файла
                int flags_pattern = Pattern.CASE_INSENSITIVE;
                Pattern pattern_rd_line = Pattern.compile("^.*?(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*).*?$", flags_pattern);
                Matcher matcher_rd_line = pattern_rd_line.matcher(rd_line);           // инииализация матчинга

                if (matcher_rd_line.find()) {                                         // если в строке найдены совпадения по паттерну
                    Integer num_ex = Integer.parseInt(matcher_rd_line.group(1));
                    Integer hit_1 = Integer.parseInt(matcher_rd_line.group(2));
                    Integer hit_2 = Integer.parseInt(matcher_rd_line.group(3));
                    Integer hit_3 = Integer.parseInt(matcher_rd_line.group(4));
                    Integer hit_4 = Integer.parseInt(matcher_rd_line.group(5));
                    Integer x = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(6))));
                    Integer y = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(7))));
                    Integer v = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(8))));

                    mainParamsList.add(new Integer[]{num_ex, hit_1, hit_2, hit_3, hit_4, x, y, v});

                    Circle circle = new Circle(1);
                    circle.setCenterX(Main.transX(x));
                    circle.setCenterY(Main.transY(y));
                    Main.draw(circle);

                    // Вывод инф. в поле TextArea out_field
                    Main.out_field.appendText(num_ex + "\n" + hit_1 + "\n" + hit_2 + "\n" + hit_3 + "\n" + hit_4 + "\n" + x + "\n" + y + "\n" + v + "\n");

                    // Запись инф. в файл out_**.txt
//                    for (int i = 0; i < num_ex.length(); i++)
//                        writer_obj.write(num_ex.charAt(i));                      // посимвольная запись в файл
//                    }
//                    writer_obj.write("\n");
                }
            }
        } catch (PatternSyntaxException pse) {                                          // исключение в случае несоответствия паттерну
            System.err.println("Неправильное регулярное выражение: " + pse.getMessage());
            System.err.println("Описание: " + pse.getDescription());
            System.err.println("Позиция: " + pse.getIndex());
            System.err.println("Неправильный шаблон: " + pse.getPattern());
        } catch (IllegalStateException e) {
            System.out.println("No matches for spec pattern!");
        }
    }
}
