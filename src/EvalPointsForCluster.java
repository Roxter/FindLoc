public class EvalPointsForCluster {
    private Integer x_min;
    private Integer y_min;
    private Integer x_max;
    private Integer y_max;

    EvalPointsForCluster() {
        Reset();
    }

    void Reset() {
        x_min = Integer.MAX_VALUE;
        y_min = Integer.MAX_VALUE;
        x_max = Integer.MIN_VALUE;
        y_max = Integer.MIN_VALUE;
    }

    void Eval(Integer x, Integer y) {
        x_min = Integer.min(x_min, x);
        y_min = Integer.min(y_min, y);
        x_max = Integer.max(x_max, x);
        y_max = Integer.max(y_max, y);
    }

    boolean Changed() {
        return
                (x_max > Integer.MIN_VALUE && y_max > Integer.MIN_VALUE && x_min < Integer.MAX_VALUE && y_min < Integer.MAX_VALUE && (x_max > x_min || y_max > y_min));
    }

    Integer GetXMin() {
        return x_min;
    }

    Integer GetXMax() {
        return x_max;
    }

    Integer GetYMin() {
        return y_min;
    }

    Integer GetYMax(){
        return y_max;
    }
}
