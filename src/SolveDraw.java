import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SolveDraw {

    private MainView mainView;
    private File source_file;
    private List<Integer[]> mainParamsList;
    private List<Integer[]> refOutParamsList;

    SolveDraw(MainView mainView, ParamsBox paramsBox) {
        this.mainView = mainView;
        this.mainParamsList = paramsBox.mainParamsList();
        this.refOutParamsList = paramsBox.refOutParamsList();
    }

    public void start(File sourceFile, Integer range_value) throws IOException {
        this.source_file = sourceFile;      // в конструкторе не получится инициализировать, т.к. при каждом вызове данного метода, в данном классе по ссылке this.source_file останется привязан старый объект (проблема только с типом File)

        mainView.clearTextOut();

        for (int i = 0; i < mainParamsList.size(); i++) {
            int x1 = mainParamsList.get(i)[5];
            int y1 = mainParamsList.get(i)[6];
            for (int j = 0; j < mainParamsList.size(); j++) {
                if (j != i) {
                    int x2 = mainParamsList.get(j)[5];
                    int y2 = mainParamsList.get(j)[6];
                    double solved_range_value = Math.sqrt(Math.abs((x2 - x1) * (x2 - x1)) + Math.abs((y2 - y1) * (y2 - y1)));
                    mainView.toTextOut1(solved_range_value + "\n");
                    if (solved_range_value < range_value) {
                        refOutParamsList.add(new Integer[]{x2, y2});
                        mainView.drawCircle(x2, y2);
                        mainView.drawPoint(x2, y2);
                    }
                }
            }
        }

//        outParamsList.add(new Integer[] {range_value, range_value});

        for (Integer[] integers : refOutParamsList) {
            mainView.toTextOut2(integers[0] + " " + integers[1] + "\n");
        }

//        writeStream();
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
