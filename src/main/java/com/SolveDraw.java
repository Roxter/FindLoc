package com;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class SolveDraw {

    private final MainView mainView;
    private File source_file;
    private final ParamsBox paramsBox;
    private Integer range_value;
    private Integer speed_value;
    private Integer minAmp_value;
    private Integer maxAmp_value;
    private Integer min_pts;
    private Integer max_pts;

    SolveDraw(MainView mainView, ParamsBox paramsBox) {
        this.mainView = mainView;
        this.paramsBox = paramsBox;
    }

    void start(File sourceFile, Integer range_value, Integer speed_value, Integer minAmp_value, Integer maxAmp_value, Integer min_pts, Integer max_pts) throws IOException {
        this.source_file = sourceFile;                   // в конструкторе не получится инициализировать, т.к. при каждом вызове данного метода, в данном классе по ссылке this.source_file останется привязан старый объект (проблема только с типом File)
        this.range_value = range_value;
        this.speed_value = speed_value;
        this.minAmp_value = minAmp_value;
        this.maxAmp_value = maxAmp_value;
        this.min_pts = min_pts;
        this.max_pts = max_pts;

        mainView.clearTextOut();

        filterStream();
        enumerationStream();
        filterRefOutParamsList();

//        int i = 0;
        // Вывод инф. в поле out_field2
        for (List<Integer> curr_param_list : paramsBox.outParamsList()) {
            StringBuilder str_toTextOut = new StringBuilder();
            for (Integer s: curr_param_list) {
                str_toTextOut.append(s).append(" ");
//                if (i < 3)
//                    System.out.println(curr_param_list.get(1) + " " + i + " " + str_toTextOut);
            }
            mainView.toTextOut2(str_toTextOut + "\n");
//            i++;
        }

//        writeStream();
    }

    private void filterStream() {
        for (int i = 0; i < paramsBox.mainParamsList().size(); i++) {
            if ((paramsBox.mainParamsList().get(i).get(paramsBox.amp_pos_in_pattern) > minAmp_value) && (paramsBox.mainParamsList().get(i).get(paramsBox.amp_pos_in_pattern) < maxAmp_value)) {
                paramsBox.postProcList().add(new ArrayList<>(paramsBox.mainParamsList().get(i)));
            }
        }
    }

    private void enumerationStream() {
        boolean param_is_first;
        int acc = 0;
        List<Integer> temp_params_list;

        System.out.println("Need to enumerate " + paramsBox.postProcList().size() + " points");

        for (int i = 0; i < paramsBox.postProcList().size(); i++) {
            int x1 = paramsBox.postProcList().get(i).get(paramsBox.x_pos_in_pattern);
            int y1 = paramsBox.postProcList().get(i).get(paramsBox.y_pos_in_pattern);
            int v1 = paramsBox.postProcList().get(i).get(paramsBox.v_pos_in_pattern);
            int dset1 = paramsBox.postProcList().get(i).get(paramsBox.dset_in_pattern);
            param_is_first = true;

            for (int j = 0; j < paramsBox.postProcList().size(); j++) {
                if (i != j) {
                    int x2 = paramsBox.postProcList().get(j).get(paramsBox.x_pos_in_pattern);
                    int y2 = paramsBox.postProcList().get(j).get(paramsBox.y_pos_in_pattern);
                    int v2 = paramsBox.postProcList().get(j).get(paramsBox.v_pos_in_pattern);
                    int dset2 = paramsBox.postProcList().get(j).get(paramsBox.dset_in_pattern);
                    double solved_range_value = Math.sqrt(Math.abs((x2 - x1) * (x2 - x1)) + Math.abs((y2 - y1) * (y2 - y1)));
                    int diff_v = Math.abs(v2 - v1);
                    mainView.toTextOut1(solved_range_value + " " + diff_v + " " + dset1 + " " + dset2 + "\n");

                    acc = acc + 1;
                    if (paramsBox.refOutParamsList() != null && (solved_range_value < range_value) && (diff_v < speed_value) ) {     // сначала ограничиваем по расстоянию/скорости, пишем отфильтрованное в промежуточный refOutParamsList лист
//                    if (refOutParamsList != null && (solved_range_value < range_value) && (diff_v < speed_value)) {
                        if (param_is_first) {                                                      // если попадает под критерий второй раз, то также отрисовываем предыдущую точку, в ином случае алгоритм сюда не войдёт
                            temp_params_list = new ArrayList<>(paramsBox.postProcList().get(i));               // временный промежуточный объект списка параметров во избежание добавления ссылок на данные в mainParamsList
//                            System.out.println(temp_params_list == mainParamsList.get(i));                              // отладка
                            paramsBox.refOutParamsList().add(new ArrayList<>(temp_params_list));                 // запись промежуточного объекта temp_params_list в хранилище refOutParamsList
//                            refOutParamsList.add(mainParamsList.get(i));                         // неверно, т.к. в дальнейшем при изменении объектов refOutParamsList, запись осуществляется также и в mainParamsList (видимо метод .add() записывает ссылки, а не копии объектов)
//                            System.out.println("mainParamsList:" + mainParamsList);                                   // отладка
                            paramsBox.refOutParamsList().get(paramsBox.refOutParamsList().size() -  1).add(0, 1);   // .size() - 1 пишется поскольку не существует метода getLast()
//                            System.out.println(refOutParamsList + " " + "param_is_first" + " i:" + i + " j:" + j);    // отладка
                            param_is_first = false;
                        }
//                        refOutParamsList.add(new Integer[] {0, mainParamsList.get(j)[0], mainParamsList.get(j)[1], mainParamsList.get(j)[2], mainParamsList.get(j)[3], mainParamsList.get(j)[4], mainParamsList.get(j)[5], mainParamsList.get(j)[6], mainParamsList.get(j)[7], mainParamsList.get(j)[8]});
                        temp_params_list = new ArrayList<>(paramsBox.postProcList().get(j));
                        paramsBox.refOutParamsList().add(new ArrayList<>(temp_params_list));
                        paramsBox.refOutParamsList().get(paramsBox.refOutParamsList().size() -  1).add(0, 0);
//                        System.out.println(paramsBox.refOutParamsList() + " " + " i:" + i + " j:" + j);                           // отладка
                    }
                }
            }
        }
//        System.out.println(refOutParamsList);
    }

    private void filterRefOutParamsList() {
        List<List <Integer>> temp_params_list = new ArrayList<>();
        List<Integer> curr_refOut;
        int cntr_pts = 0;
        EvalPointsForCluster minmax_eval = new EvalPointsForCluster();

        for (int i = 0; i < paramsBox.refOutParamsList().size() - 1; i++) {     // идём по листу сохранённых опорных данных
            cntr_pts = cntr_pts + 1;
            curr_refOut = new ArrayList<>(paramsBox.refOutParamsList().get(i));
            if (paramsBox.refOutParamsList().get(i).get(0) == 1 && cntr_pts != 1) {    // если набор комбинаций изменился (проверка cntr_pts != 1 во избежание лишнего ненужного перехода внутрь)
                if (temp_params_list.size() == 0 && minmax_eval.Changed()) {
                    mainView.drawRectangle(minmax_eval.GetXMin(), minmax_eval.GetYMin(), minmax_eval.GetXMax(), minmax_eval.GetYMax());
//                    System.out.println("draw rect " + "[" + minmax_eval.GetXMin() + ";" + minmax_eval.GetYMin() + " " + minmax_eval.GetXMax() + ";" + minmax_eval.GetYMax() + "]");
                }
                minmax_eval.Reset();
                cntr_pts = 1;
                mainView.points_in_clusters = mainView.points_in_clusters + 1;
                temp_params_list.clear();
            }
            if (cntr_pts < min_pts) {                                       // указана нижняя граница интервала точек, который нужно захватить фильтром
                temp_params_list.add(new ArrayList<>(curr_refOut));
            } else if (cntr_pts <= max_pts) {                               // указана верхняя граница интервала точек, который нужно захватить фильтром
//                System.out.println(cntr_pts + " " + i + " ");
                if (temp_params_list.size() != 0) {
//                    System.out.println(temp_params_list);
                    for (List<Integer> p : temp_params_list) {
                        paramsBox.outParamsList().add(new ArrayList<>(p));
//                        mainView.drawCircle(p[6], p[7], (p[9] > amp_value));        || для кругов
//                        System.out.println("i:" + i + " " + mainView.transX(p.get(6)) + " " + mainView.transY(p.get(7)));
                        minmax_eval.Eval(p.get(paramsBox.x_pos_in_pattern + 1), p.get(paramsBox.y_pos_in_pattern + 1));       // для прямоугольников
//                        System.out.println(minmax_eval.GetXMin() + " " + minmax_eval.GetYMin() + " " + minmax_eval.GetXMax() + " " + minmax_eval.GetYMax());
//                        mainView.drawPoint(p[6], p[7]);
                    }
                }
                paramsBox.outParamsList().add(new ArrayList<>(curr_refOut));
//                mainView.drawCircle(refOutParamsList.get(i)[6], refOutParamsList.get(i)[7], (refOutParamsList.get(i)[9] > amp_value));        || для кругов
                minmax_eval.Eval(curr_refOut.get(paramsBox.x_pos_in_pattern + 1), curr_refOut.get(paramsBox.y_pos_in_pattern + 1));       // для прямоугольников
//                mainView.drawPoint(refOutParamsList.get(i)[6], refOutParamsList.get(i)[7]);
                temp_params_list.clear();
            }
//            System.out.println("temp_params_list:" + temp_params_list);
//            System.out.println("outParamsList" + outParamsList);
//            System.out.println("XMin:" + minmax_eval.GetXMin() + " YMin:" + minmax_eval.GetYMin() + " XMax:" + minmax_eval.GetXMax() + " YMax:" + minmax_eval.GetYMax() );
        }
        if (minmax_eval.Changed()) {
//            System.out.println("Changed!");
            mainView.drawRectangle(minmax_eval.GetXMin(), minmax_eval.GetYMin(), minmax_eval.GetXMax(), minmax_eval.GetYMax());
        }
    }

    private void writeStream() throws IOException {
//        String out_file_string = source_file.getPath() + "out_" + source_file.getName();
        String out_file_string = source_file.getPath().replaceAll(source_file.getName(), "out_" + source_file.getName());
        File out_file = new File(out_file_string);
        BufferedWriter writer_obj = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out_file.getAbsoluteFile())));

        // Запись инф. в файл out_**.txt
        /*for (List<Integer> i: paramsBox.mainParamsList()) {
            for (int j = 0; j < paramsBox.mainParamsList().get(0).length; j++) {
                String str_to_write = Integer.toString(i[j]);
                writer_obj.write(str_to_write + " ");                      // посимвольная запись в файл
            }
            writer_obj.write("\n");
        }*/

        writer_obj.close();
    }

}
