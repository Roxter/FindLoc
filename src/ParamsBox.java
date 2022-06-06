import java.util.ArrayList;
import java.util.List;

public class ParamsBox {

    // Const Params
    public final int x_pos_in_pattern = 5;
    public final int y_pos_in_pattern = 6;
    public final int v_pos_in_pattern = 7;
    public final int amp_pos_in_pattern = 8;
    public final int dset_in_pattern = 9;

    // Var params
    private final List<List <Integer>> mainParamsList;
    private final List<List <Integer>> postProcList;
    private final List<List <Integer>> refOutParamsList;       // список опорных параметров
    private final List<List <Integer>> outParamsList;

    // Граничные точки со всей плоскости
    public Integer found_xmax = Integer.MIN_VALUE;
    public Integer found_ymax = Integer.MIN_VALUE;
    public Integer found_xmin = Integer.MAX_VALUE;
    public Integer found_ymin = Integer.MAX_VALUE;

    ParamsBox() {
        mainParamsList = new ArrayList<>();
        postProcList = new ArrayList<>();
        outParamsList = new ArrayList<>();
        refOutParamsList = new ArrayList<>();
    }

    public List<List <Integer>> mainParamsList() {
        return mainParamsList;
    }

    public List<List <Integer>> postProcList() {
        return postProcList;
    }

    public List<List <Integer>> outParamsList() {
        return outParamsList;
    }

    public List<List <Integer>> refOutParamsList() { return refOutParamsList; }

    public void clearAllParams() {
        mainParamsList.clear();
        postProcList.clear();
        outParamsList.clear();
        refOutParamsList.clear();
    }

    public void clearMainParamsList() {
//        System.out.println("mainParamsList:" + mainParamsList.size());
        mainParamsList.clear();
//        System.out.println("mainParamsList:" + mainParamsList.size());
    }

    public void clearPostProcList() {
        postProcList.clear();
    }

    public void clearOutParamsList() {
        outParamsList.clear();
    }

    public void clearRefOutParamsList() {
        refOutParamsList.clear();
    }

}