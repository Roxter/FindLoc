package com;

import java.util.ArrayList;
import java.util.List;

public class ParamsBox {

    // Const Params
    final int x_pos_in_pattern = 5;
    final int y_pos_in_pattern = 6;
    final int v_pos_in_pattern = 7;
    final int amp_pos_in_pattern = 8;
    final int dset_in_pattern = 9;

    // Var params
    private final List<List <Integer>> mainParamsList;
    private final List<List <Integer>> postProcList;
    private final List<List <Integer>> refOutParamsList;       // список опорных параметров
    private final List<List <Integer>> outParamsList;

    // Граничные точки со всей плоскости
    Integer found_xmax = Integer.MIN_VALUE;
    Integer found_ymax = Integer.MIN_VALUE;
    Integer found_xmin = Integer.MAX_VALUE;
    Integer found_ymin = Integer.MAX_VALUE;

    ParamsBox() {
        mainParamsList = new ArrayList<>();
        postProcList = new ArrayList<>();
        outParamsList = new ArrayList<>();
        refOutParamsList = new ArrayList<>();
    }

    List<List <Integer>> mainParamsList() {
        return mainParamsList;
    }

    List<List <Integer>> postProcList() {
        return postProcList;
    }

    List<List <Integer>> outParamsList() {
        return outParamsList;
    }

    List<List <Integer>> refOutParamsList() { return refOutParamsList; }

    void clearAllParams() {
        mainParamsList.clear();
        postProcList.clear();
        outParamsList.clear();
        refOutParamsList.clear();
    }

    void clearMainParamsList() {
//        System.out.println("mainParamsList:" + mainParamsList.size());
        mainParamsList.clear();
//        System.out.println("mainParamsList:" + mainParamsList.size());
    }

    void clearPostProcList() {
        postProcList.clear();
    }

    void clearOutParamsList() {
        outParamsList.clear();
    }

    void clearRefOutParamsList() {
        refOutParamsList.clear();
    }

}