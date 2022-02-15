import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SolveDraw {

    private MainView mainView;
    private File source_file;
    private List<Integer[]> mainParamsList;
    private List<Integer[]> refOutParamsList;
    private List<Integer[]> outParamsList;
    private Integer range_value;
    private Integer speed_value;

    SolveDraw(MainView mainView, ParamsBox paramsBox) {
        this.mainView = mainView;
        this.mainParamsList = paramsBox.mainParamsList();
        this.refOutParamsList = paramsBox.refOutParamsList();
        this.outParamsList = paramsBox.outParamsList();
    }

    public void run(File sourceFile, Integer range_value, Integer speed_value) throws IOException {
        this.source_file = sourceFile;                   // в конструкторе не получится инициализировать, т.к. при каждом вызове данного метода, в данном классе по ссылке this.source_file останется привязан старый объект (проблема только с типом File)
        this.range_value = range_value;
        this.speed_value = speed_value;

        mainView.clearTextOut();

        solveStream();
        filterRefOutParamsList();

//        outParamsList.add(new Integer[] {range_value, range_value});

        for (Integer[] integers : outParamsList) {
            mainView.toTextOut2(integers[0] + " " + integers[1] + " " + integers[2] + " " + integers[3] + " " + integers[4] + " " + integers[5] + " " + integers[6] + " " + integers[7] + " " + integers[8] + "\n");
        }

//        writeStream();
    }

    private void solveStream() {
        boolean param_is_first;
        List<Integer[]> temp_param_var = new ArrayList<>();
        int acc = 0;

        for (int i = 0; i < mainParamsList.size(); i++) {
            int x1 = mainParamsList.get(i)[5];
            int y1 = mainParamsList.get(i)[6];
            int v1 = mainParamsList.get(i)[7];
            param_is_first = true;

            temp_param_var.add(new Integer[] {1, mainParamsList.get(i)[0], mainParamsList.get(i)[1], mainParamsList.get(i)[2], mainParamsList.get(i)[3], mainParamsList.get(i)[4], mainParamsList.get(i)[5], mainParamsList.get(i)[6], mainParamsList.get(i)[7]});
            for (int j = 0; j < mainParamsList.size(); j++) {
                if (i != j) {
                    int x2 = mainParamsList.get(j)[5];
                    int y2 = mainParamsList.get(j)[6];
                    int v2 = mainParamsList.get(j)[7];
                    double solved_range_value = Math.sqrt(Math.abs((x2 - x1) * (x2 - x1)) + Math.abs((y2 - y1) * (y2 - y1)));
                    int diff_v = Math.abs(v2 - v1);
                    mainView.toTextOut1(solved_range_value + " " + diff_v + "\n");

                    acc = acc + 1;
                    if ((solved_range_value < range_value) && (diff_v < speed_value)) {
                        if (param_is_first) {               // если попадает под критерий второй раз, то также отрисовываем предыдущую точку, в ином случае алгоритм сюда не войдёт
                            refOutParamsList.add(temp_param_var.get(temp_param_var.size() - 1));
                            param_is_first = false;
                        }
                        refOutParamsList.add(new Integer[] {0, mainParamsList.get(j)[0], mainParamsList.get(j)[1], mainParamsList.get(j)[2], mainParamsList.get(j)[3], mainParamsList.get(j)[4], mainParamsList.get(j)[5], mainParamsList.get(j)[6], mainParamsList.get(j)[7]});
                    }
                }
            }
        }

    }

    private void filterRefOutParamsList() {
        List<Integer[]> temp_param_list = new ArrayList<>();
//        Integer[] temp_param_var = new Integer[9];
        int acc = 0;
        boolean found_on_filter = false;

        for (int i = 0; i < refOutParamsList.size()-1; i++) {     // идём по листу сохранённых опорных данных
            acc = acc + 1;
            if (refOutParamsList.get(i)[0] == 1 && acc != 1) {
                acc = 1;
                temp_param_list.clear();
            }
        if (acc < 3) {                                            // указана нижняя граница интервала точек, который нужно захватить фильтром
                temp_param_list.add(new Integer[] {refOutParamsList.get(i)[0], refOutParamsList.get(i)[1], refOutParamsList.get(i)[2], refOutParamsList.get(i)[3], refOutParamsList.get(i)[4], refOutParamsList.get(i)[5], refOutParamsList.get(i)[6], refOutParamsList.get(i)[7], refOutParamsList.get(i)[8]});
            } else if (acc <= 9) {                               // указана верхняя граница интервала точек, который нужно захватить фильтром
//                System.out.println(acc + " " + i);
                if (!temp_param_list.isEmpty())
                    for (Integer[] integer: temp_param_list) {
                        outParamsList.add(integer);
                        mainView.drawCircle(integer[6], integer[7], integer[1]);
                        mainView.drawPoint(integer[6], integer[7]);
                    }
                outParamsList.add(new Integer[] {refOutParamsList.get(i)[0], refOutParamsList.get(i)[1], refOutParamsList.get(i)[2], refOutParamsList.get(i)[3], refOutParamsList.get(i)[4], refOutParamsList.get(i)[5], refOutParamsList.get(i)[6], refOutParamsList.get(i)[7], refOutParamsList.get(i)[8]});
//                mainView.drawCircle(refOutParamsList.get(i)[6], refOutParamsList.get(i)[7], mainParamsList.get(i)[1]);
                mainView.drawPoint(refOutParamsList.get(i)[6], refOutParamsList.get(i)[7]);
                temp_param_list.clear();
            }
        }
    }

    private void writeStream() throws IOException {
//        String out_file_string = source_file.getPath() + "out_" + source_file.getName();
        String out_file_string = source_file.getPath().replaceAll(source_file.getName(), "out_" + source_file.getName());
        File out_file = new File(out_file_string);
        BufferedWriter writer_obj = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out_file.getAbsoluteFile())));

        // Запись инф. в файл out_**.txt
        for (Integer[] integers : mainParamsList) {
            for (int j = 0; j < mainParamsList.get(0).length; j++) {
                String str_to_write = Integer.toString(integers[j]);
                writer_obj.write(str_to_write + " ");                      // посимвольная запись в файл
            }
            writer_obj.write("\n");
        }

        writer_obj.close();
    }
}
