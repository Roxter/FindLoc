import java.util.ArrayList;
import java.util.List;

public class ParamsBox {

    private final List<List <Integer>> mainParamsList;
    private final List<List <Integer>> postProcList;
    private final List<List <Integer>> refOutParamsList;       // список опорных параметров
    private final List<List <Integer>> outParamsList;

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