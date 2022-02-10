package timesonsensors;

import java.util.LinkedList;
import java.util.Random;

public class MainApp {

    private Random rand;
    private LinkedList<Marker> sensors_list = new LinkedList<>();
    private LinkedList<Double> times_list = new LinkedList<>();
    private LinkedList<Double> dists_list = new LinkedList<>();
    private LinkedList<Integer> speeds_list = new LinkedList<>();
    private Marker gen;         // Генератор дефекта
    private Marker sensor;
    private String stringforlabel7 = "";
    private String stringfortextfield4 = "";

    public MainApp() {             rand = new Random();    }

    void createSensor() {
        for (int i = rand.nextInt(5); i >= 0; i--)
            createSensor(getRandMarkerPos(), getRandMarkerPos(), getRandV());
    }

    void createSensor(int v) {
        for (int i = rand.nextInt(5); i >= 0; i--)
            createSensor(getRandMarkerPos(), getRandMarkerPos(), v);
    }

    void createSensor(int x, int y, int v) {
        sensor = new Marker();

        if (v == 0)     speeds_list.add(getRandV());  else    speeds_list.add(v);
        if (gen == null)    createGen();

        if (x == 0 && y == 0)   sensor.drawCross(getRandMarkerPos(), getRandMarkerPos(), sensors_list.size() + 1);
        else  sensor.drawCross(x, y, sensors_list.size() + 1);
        sensors_list.add(sensor);

        solveTimeOnDist();

        sensor.drawCircleDist(sensGetX(), sensGetY(), dists_list.getLast());

        solvePosGen();

        stringforlabel7 = String.format("gen pos: (%d,%d)", genGetX(), genGetY());
        stringfortextfield4 = stringfortextfield4 + " " + String.format("%d(%d, %d, %d, %s) ",
                sensors_list.size(), sensor.getPoint().getX(), sensor.getPoint().getY(), speeds_list.getLast(), (double)(Math.round((times_list.getLast())*100.0)/100.0)+"");

        Main.label7.setText(stringforlabel7);
        Main.textfield4.setText(stringfortextfield4);
//        if (apptestfx.Main.textfield3.getText().equals("")) apptestfx.Main.textfield3.setText(String.valueOf((speeds.getLast().intValue())));
    }

    private void createGen() {
        gen = new Marker();
        gen.drawCircleGen(getRandMarkerPos(), getRandMarkerPos());
    }

    private void solveTimeOnDist() {
        double dist = 0.0;
        dist = Math.sqrt(Math.abs((genGetX() - sensGetX()) * (genGetX() - sensGetX())) + Math.abs((genGetY() - sensGetY()) * (genGetY() - sensGetY())));
        dists_list.add(dist);
        times_list.add((dist / 100) / (double) speeds_list.getLast());
    }

    private void solvePosGen() {
//        sensors.get();
    }

    void resetSensors() {
        sensors_list.clear();
        times_list.clear();
        dists_list.clear();
        speeds_list.clear();
        gen = null;
        stringfortextfield4 = "";
    }

    int sensGetX() {   return sensors_list.getLast().getPoint().getX();    }

    int sensGetY() {   return sensors_list.getLast().getPoint().getY();    }

    int genGetX() {   return gen.getPoint().getX();    }

    int genGetY() {   return gen.getPoint().getY();    }

    private int getRandMarkerPos() {    return (rand.nextInt(Main.canvasside - 20) + 10);   }

    private int getRandV() {    return rand.nextInt(20) + 1;    }

}