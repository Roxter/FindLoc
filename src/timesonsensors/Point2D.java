package timesonsensors;

class Point2D {

    private int mX;
    private int mY;

    public Point2D() {
        this (0,0);
    }

    public Point2D(int x, int y) {
        mX = x;
        mY = y;
    }

    public int getX() {
        return mX;
    }

    public int getY() { return mY; }

}