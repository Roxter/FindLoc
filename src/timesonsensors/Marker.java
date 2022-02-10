package timesonsensors;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Marker {

    private Line line1;
    private Line line2;
    private Line line3;
    private Point2D point;
    private Circle circle;

    Point2D getPoint() {
        return point;
    }

    void drawCross(int x, int y, int num_of_sensor) {
        Text text_num = new Text(x - 3 , y - 15, Integer.toString(num_of_sensor));
        Text text_point = new Text(x, y + 17, x + ";" + y);
        point = new Point2D(x, y);
        line1 = new Line(point.getX() - 10, point.getY(), point.getX() + 10, point.getY());
        line2 = new Line(point.getX(), point.getY() - 10, point.getX(), point.getY() + 10);

        line1.setStroke(Color.GREEN);
        line2.setStroke(Color.GREEN);
        text_num.setStroke(Color.GREEN);
        text_point.setTextAlignment(TextAlignment.LEFT);

        Main.draw(line1, line2, text_num, text_point);
    }

    void drawCircleGen(int x, int y) {
        point = new Point2D(x, y);
        line3 = new Line(point.getX(), point.getY(), point.getX(), point.getY());
        circle = new Circle(7);

        line3.setStroke(Color.BLACK);
        line3.setStrokeWidth(2);
        circle.setCenterX(point.getX());
        circle.setCenterY(point.getY());
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1);

        Main.draw(circle, line3);
    }

    void drawCircleDist(int x, int y, double d) {
        circle = new Circle(d);
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLUE);
        circle.setStrokeWidth(1);

        Main.draw(circle);
    }

}