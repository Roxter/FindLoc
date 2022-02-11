import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ReadDraw {

    private MainView mainView;
    private File source_file;
    private List<Integer[]> mainParamsList;

    private Integer num_ex, hit_1, hit_2, hit_3, hit_4, x, y, v;

    ReadDraw(MainView mainView, ParamsBox paramsBox) {
        this.mainView = mainView;
        this.mainParamsList = paramsBox.mainParamsList();
    }

    public void start(File sourceFile) throws IOException {
        this.source_file = sourceFile;      // в конструкторе это не получится инициализировать, т.к. при каждом вызове данного метода, в данном классе по ссылке this.source_file останется привязан старый объект (проблема только с типом File)
        readStream();
    }

    private void readStream() throws IOException {
        BufferedReader reader_obj = new BufferedReader(new InputStreamReader(new FileInputStream(source_file)));
        String rd_line;

        try {
            while ((rd_line = reader_obj.readLine()) != null) {                        // построчный проход текстового файла
                int flags_pattern = Pattern.CASE_INSENSITIVE;
                Pattern pattern_rd_line = Pattern.compile("^.*?(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*).*?$", flags_pattern);
                Matcher matcher_rd_line = pattern_rd_line.matcher(rd_line);           // инииализация матчинга

                if (matcher_rd_line.find()) {                                         // если в строке найдены совпадения по паттерну
                    num_ex = Integer.parseInt(matcher_rd_line.group(1));
                    hit_1 = Integer.parseInt(matcher_rd_line.group(2));
                    hit_2 = Integer.parseInt(matcher_rd_line.group(3));
                    hit_3 = Integer.parseInt(matcher_rd_line.group(4));
                    hit_4 = Integer.parseInt(matcher_rd_line.group(5));
                    x = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(6))));
                    y = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(7))));
                    v = (int) (Math.round(Double.parseDouble(matcher_rd_line.group(8))));

                    mainView.drawPoint(x, y);       // отрисовка найденной точки

                    this.mainParamsList.add(new Integer[]{num_ex, hit_1, hit_2, hit_3, hit_4, x, y, v});

                    // Вывод инф. в поле TextArea out_field
                    mainView.toTextOut1(num_ex + " " + hit_1 + " " + hit_2 + " " + hit_3 + " " + hit_4 + " " + x + " " + y + " " + v + "\n");
                }
            }
            reader_obj.close();
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
