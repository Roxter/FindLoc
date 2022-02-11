import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainView extends VBox {

    final static int axisMax = 400;
    final static int maxspeed = 50;
    final static int indent = 40;
    final static int maxRangePoints = 20;

    private int front_canvas_side = axisMax * 2;
    private int back_canvas_side = front_canvas_side +indent*2;

    private AtomicBoolean is_started_ReadDraw;
    private AtomicBoolean is_draw_points_on_circles;

    private Label welcome_message;
    private TextField sourcefile_field;
    private TextField range_field;
    private TextArea out_field1;
    private TextArea out_field2;
    private File selected_file;
    private FileChooser filechooser;
    private Button sourcefile_button;
    private Button start_button;
    private Button diff_draw_button;
    private Canvas frontcanvas;
    private Canvas backcanvas;
    private Group frontcanvas_group;
    private HBox sourcefile_box;
    private HBox start_box;
    private HBox out_field_box;
    private Pane canvas_pane;
    private GraphicsContext graph, graph1;

    private ParamsBox params;

    public MainView(Stage primaryStage) {

        // Инициализация переменных
        is_started_ReadDraw = new AtomicBoolean(false);
        is_draw_points_on_circles = new AtomicBoolean(true);
        params = new ParamsBox();

        // Форма приветствия
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        //welcome_message = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        // Инициализация canvas области
        frontcanvas = new Canvas(front_canvas_side, front_canvas_side);
        backcanvas = new Canvas(back_canvas_side, back_canvas_side);
        frontcanvas_group = new Group();
        frontcanvas_group.getChildren().add(frontcanvas);
        graph = frontcanvas.getGraphicsContext2D();
        graph1 = backcanvas.getGraphicsContext2D();
        graph.setFill(Color.BEIGE);
        graph1.setFill(Color.LIGHTGRAY);
        graph.fillRect(0,0, front_canvas_side, front_canvas_side);
        graph1.fillRect(0,0, back_canvas_side, back_canvas_side);
        frontcanvas_group.setTranslateX(indent);
        frontcanvas_group.setTranslateY(indent);

        // Инициализация поля, хранящего путь к входному файлу
        sourcefile_field = new TextField();         // Поле хранения пути к входному файлу, необходимо для взаимодействия с обработчиком кнопки (поскольку им нельзя взаимодействовать с переменной типа File )
        selected_file = new File("./in.txt");
        if (selected_file.exists() && !selected_file.isDirectory())
            sourcefile_field.setText(selected_file.getAbsolutePath());
        else
            sourcefile_field.setText("Выберите файл с расширением .txt");

        // Инициализация выбора входного файла
        filechooser = new FileChooser();
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        filechooser.setInitialDirectory(new File("."));
        sourcefile_button = new Button("Input File");
        sourcefile_button.setOnAction(e -> {
            File f = filechooser.showOpenDialog(primaryStage);
            if (f != null) {
                selected_file = f;
                sourcefile_field.setText(selected_file.getAbsolutePath());
            }
        });

        // Инициализация поля ввода радиуса кластера
        range_field = new TextField(String.valueOf(maxRangePoints));
        
        // Инициализация кнопки запуска процесса
        start_button = new Button("Read and Draw");
        ReadDraw read_and_draw = new ReadDraw(this, params);
        SolveDraw solve_and_draw = new SolveDraw(this, params);
        start_button.setOnAction(e -> {
            if (selected_file.exists() && !selected_file.isDirectory()) {
                params.refOutParamsList().clear();
                try {
                    if (!is_started_ReadDraw.get()) {
                        drawClear();
                        clearTextOut();
                        is_started_ReadDraw.set(true);
                        start_button.setText("Solve and Draw");
                        params.mainParamsList().clear();
                        read_and_draw.start(selected_file);
                    } else {
                        if (range_field.getText().matches("[1-9][0-9]*")) {
                            is_started_ReadDraw.set(false);
                            start_button.setText("Read and Draw");
                            solve_and_draw.start(selected_file, Integer.parseInt(range_field.getText()));
                        } else
                            range_field.setText("Enter a valid value!");
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        // Инициализация кнопки различения меток
        diff_draw_button = new Button("Diff Draw");
        diff_draw_button.setOnAction(e -> {
            if (!params.mainParamsList().isEmpty() && !params.refOutParamsList().isEmpty()) {
                drawClear();
                if (is_draw_points_on_circles.get()) {      // точки все - круги - точки полученные
                    for (int i = 0; i < params.mainParamsList().size(); i++) {
                        drawPoint(params.mainParamsList().get(i)[5], params.mainParamsList().get(i)[6]);
                    }
                    for (int i = 0; i < params.refOutParamsList().size(); i++) {
                        drawCircle(params.refOutParamsList().get(i)[0], params.refOutParamsList().get(i)[1]);
                    }
                    for (int i = 0; i < params.refOutParamsList().size(); i++) {
                        drawPoint(params.refOutParamsList().get(i)[0], params.refOutParamsList().get(i)[1]);
                    }
                } else {                                   // круги - точки все
                    for (int i = 0; i < params.refOutParamsList().size(); i++) {
                        drawCircle(params.refOutParamsList().get(i)[0], params.refOutParamsList().get(i)[1]);
                    }
                    for (int i = 0; i < params.mainParamsList().size(); i++) {
                        drawPoint(params.mainParamsList().get(i)[5], params.mainParamsList().get(i)[6]);
                    }
                }
            } else
                out_field1.setText("Params is empty!");
        });

        // Инициализация панели вывода информации
        out_field1 = new TextArea();
        out_field2 = new TextArea();

        // Компоновка панелей
        canvas_pane = new Pane(backcanvas, frontcanvas_group);
        sourcefile_box = new HBox(sourcefile_field, sourcefile_button);
        start_box = new HBox(range_field, start_button, diff_draw_button);
        out_field_box = new HBox(out_field1, out_field2);
        this.getChildren().addAll(canvas_pane, sourcefile_box, start_box, out_field_box);

        // Установка параметров объектов
        canvas_pane.setStyle("-fx-background-color: LIGHTGRAY;");
        sourcefile_box.setAlignment(Pos.CENTER);
        sourcefile_field.setPrefWidth(front_canvas_side - sourcefile_button.getWidth());
        sourcefile_field.setEditable(false);
        start_button.setAlignment(Pos.BASELINE_RIGHT);
        start_box.setAlignment(Pos.CENTER);
        range_field.setStyle("-fx-pref-width: 40");
        out_field1.setStyle("-fx-pref-height: 100");
        out_field1.setEditable(false);
        out_field2.setStyle("-fx-pref-height: 100");
        out_field2.setEditable(false);
        this.setStyle("-fx-background-color: LIGHTGRAY;");
        this.setAlignment(Pos.CENTER);

        // Первичная отрисовка в canvas
        drawAxis();
    }

    private int transX(int x) {        return x + front_canvas_side / 2;    }

    private int transY(int y) {        return front_canvas_side - y - front_canvas_side / 2;    }

    private void drawAxis() {
        Color color = Color.LIGHTGREEN;
        Line axis_lineX = new Line(transX(-axisMax), transY(0), transX(axisMax), transY(0));      // Координаты Y в axisLineX отображаются в canvasside - n
        Line axis_lineY = new Line(transX(0), transY(-axisMax), transX(0), transY(axisMax));      // Координаты X в axisLineY отображаются в canvasside - n
        Line grid_line_axisX;
        Line grid_line_axisY;
        Text text_grid_axisX;
        Text text_grid_axisY;

        axis_lineX.setStroke(color);
        axis_lineY.setStroke(color);
        frontcanvas_group.getChildren().addAll(axis_lineX, axis_lineY);
        for(int i = (-axisMax / 50 + 1); i < (axisMax / 50); i++) {   //Draw Grid Axis X
            if (i != 0) {
                grid_line_axisX = new Line(transX(50 * i), transY(0), transX(50 * i), transY(10));
                grid_line_axisX.setStroke(color);
                text_grid_axisX = new Text(transX(50 * i + 2), transY(3), Integer.toString(50 * i));
                text_grid_axisX.setFill(color);
                frontcanvas_group.getChildren().addAll(grid_line_axisX, text_grid_axisX);
            }
            else {
                text_grid_axisX = new Text(transX(50 * i + 2), transY(3), Integer.toString(50 * i));
                text_grid_axisX.setFill(color);
                frontcanvas_group.getChildren().addAll(text_grid_axisX);
            }
        }
        for(int i = (-axisMax / 50 + 1); i < (axisMax / 50); i++) {   //Draw Grid Axis Y
            if (i != 0) {
                grid_line_axisY = new Line(transX(0), transY(50 * i), transX(10), transY(50 * i));
                grid_line_axisY.setStroke(color);
                text_grid_axisY = new Text(transX(2), transY(50 * i + 3), Integer.toString(50 * i));
                text_grid_axisY.setFill(color);
                frontcanvas_group.getChildren().addAll(grid_line_axisY, text_grid_axisY);
            }
        }
    }

    private void draw(Shape... shape) {
        Pane pane = new Pane(backcanvas, frontcanvas_group);
        pane.setPrefSize(front_canvas_side +indent*2, front_canvas_side +indent*2);
        Rectangle outputClip = new Rectangle();
        pane.setClip(outputClip);
        pane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
        this.getChildren().set(0, pane);
        for(Shape s: shape)     frontcanvas_group.getChildren().add(s);
    }

    public void drawPoint(Integer x, Integer y) {
        Rectangle point = new Rectangle();
        point.setX(transX(x));
        point.setY(transY(y));
        point.setWidth(2);
        point.setHeight(2);
        draw(point);
    }

    public void drawCircle(Integer x, Integer y) {
        Circle circle = new Circle(maxRangePoints);

        circle.setCenterX(transX(x));
        circle.setCenterY(transY(y));
        circle.setFill(Color.YELLOW);
//        circle.setStrokeWidth(1);
//        circle.setStroke(Color.BLACK);

        draw(circle);
    }

    public void drawClear() {
        frontcanvas_group.getChildren().clear();
        frontcanvas_group.getChildren().add(frontcanvas);
        drawAxis();
    }

    public void toTextOut1(String str) { out_field1.appendText(str); }

    public void toTextOut2(String str) { out_field2.appendText(str); }

    public void clearTextOut() {
        out_field1.clear();
        out_field2.clear();
    }

}