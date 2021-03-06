import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ReadDraw {

    private final MainView mainView;
    private File source_file;
    private final ParamsBox paramsBox;

    ReadDraw(MainView mainView, ParamsBox paramsBox) {
        this.mainView = mainView;
        this.paramsBox = paramsBox;
    }

    public void start(File sourceFile) throws IOException {
        this.source_file = sourceFile;      // в конструкторе это не получится инициализировать, т.к. при каждом вызове данного метода, в данном классе по ссылке this.source_file останется привязан старый объект (проблема только с типом File)
        readStream();
    }

    // Потоковое чтение строк в файле
    private void readStream() throws IOException {
        BufferedReader reader_obj = new BufferedReader(new InputStreamReader(new FileInputStream(source_file)));
        String rd_line;

        try {
            while ((rd_line = reader_obj.readLine()) != null) {                        // построчный проход текстового файла
                int flags_pattern = Pattern.CASE_INSENSITIVE;
                Pattern pattern_rd_line = Pattern.compile(
                        "^.*?(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*).*?$", flags_pattern);
//                Pattern pattern_rd_line = Pattern.compile("^.*?(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*)\\s+([-]?\\d+[.,]?\\d*).*?$", flags_pattern);
                Matcher matcher_rd_line = pattern_rd_line.matcher(rd_line);           // инииализация матчинга
                if (matcher_rd_line.find()) {                                         // если в строке найдены совпадения по паттерну
                    List <Integer> curr_params_list = new ArrayList<>();              // хранилище снятого по паттерну набора параметров
                    for (int i = 1; i < matcher_rd_line.groupCount() + 1; i++) {
//                        if (matcher_rd_line.group(i).contains(","))
                            curr_params_list.add(NumberFormat.getInstance(java.util.Locale.FRANCE).parse(matcher_rd_line.group(i)).intValue());
//                        else
//                            System.out.format("%d", Integer.valueOf(matcher_rd_line.group(i)));
                    }

                    int x = curr_params_list.get(paramsBox.x_pos_in_pattern);
                    int y = curr_params_list.get(paramsBox.y_pos_in_pattern);

                    // Фиксация мин макс координат для правильной отрисовки диаграммы
                    if (x > this.paramsBox.found_xmax)
                        this.paramsBox.found_xmax = x;
                    else if (x < this.paramsBox.found_xmin)
                        this.paramsBox.found_xmin = x;
                    if (y > this.paramsBox.found_ymax)
                        this.paramsBox.found_ymax = y;
                    else if (y < this.paramsBox.found_ymin)
                        this.paramsBox.found_ymin = y;

                    // Фиксация определенных по паттерну параметров в список
//                    this.mainParamsList.add(new Integer[]{num_ex, hit_1, hit_2, hit_3, hit_4, x, y, v, amp});
                    this.paramsBox.mainParamsList().add(curr_params_list);

                    // Собираем строку для вывода
                    StringBuilder str_toTextOut = new StringBuilder();
                    for (Integer s: curr_params_list) {
                        str_toTextOut.append(s).append(" ");
                    }

                    // Вывод инф. в поле out_field1
                    mainView.toTextOut1(str_toTextOut + "\n");
                }
            }
            reader_obj.close();

//            System.out.println("XYMin:" + this.paramsBox.found_xmin + ";" + this.paramsBox.found_ymin + " XYMax:" + this.paramsBox.found_xmax + ";" + this.paramsBox.found_ymax);
        } catch (PatternSyntaxException pse) {                                          // исключение в случае несоответствия паттерну
            System.err.println("Неправильное регулярное выражение: " + pse.getMessage());
            System.err.println("Описание: " + pse.getDescription());
            System.err.println("Позиция: " + pse.getIndex());
            System.err.println("Неправильный шаблон: " + pse.getPattern());
        } catch (IllegalStateException e) {
            System.out.println("No matches for spec pattern!");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}