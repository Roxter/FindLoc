import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.List;

public class ParamsBox {

    private final List<List <Integer>> mainParamsList;
    private final List<List <Integer>> refOutParamsList;       // список опорных параметров
    private final List<List <Integer>> outParamsList;

    ParamsBox() {
        mainParamsList = new ArrayList<>();
        outParamsList = new ArrayList<>();
        refOutParamsList = new ArrayList<>();
    }

    public List<List <Integer>> mainParamsList() {
        return mainParamsList;
    }

    public List<List <Integer>> outParamsList() {
        return outParamsList;
    }

    public List<List <Integer>> refOutParamsList() { return refOutParamsList; }

    public void clearParams() {
        mainParamsList.clear();
        outParamsList.clear();
        refOutParamsList.clear();
    }

    public void clearMainParamsList() {
//        System.out.println("mainParamsList:" + mainParamsList.size());
        mainParamsList.clear();
//        System.out.println("mainParamsList:" + mainParamsList.size());
    }

    public void clearOutParamsList() {
        outParamsList.clear();
    }

    public void clearRefOutParamsList() {
        refOutParamsList.clear();
    }

}
