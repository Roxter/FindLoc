import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.List;

public class ParamsBox {

    private List<Integer[]> mainParamsList;
    private List<Integer[]> refOutParamsList;       // список опорных параметров
    private List<Integer[]> outParamsList;

    ParamsBox() {
        mainParamsList = new ArrayList<>();
        outParamsList = new ArrayList<>();
        refOutParamsList = new ArrayList<>();
    }

    public List<Integer[]> mainParamsList() {
        return mainParamsList;
    }

    public List<Integer[]> outParamsList() {
        return outParamsList;
    }

    public List<Integer[]> refOutParamsList() {
        return refOutParamsList;
    }

    public void clearParams() {
        mainParamsList.clear();
        outParamsList.clear();
        refOutParamsList.clear();
    }
}
