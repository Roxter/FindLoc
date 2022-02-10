import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Main extends Application {

    final static int axisMax = 400;
    final static int maxspeed = 50;
    final static int indent = 40;

    private static int canvasside;

    private Stage primaryStage;

    private Label welcome_message;
    private TextField sourcefile_field;
    public static TextArea out_field;
    private File sourcefile_var;
    private File selected_file;
    private FileChooser filechooser;
    private Button sourcefile_button;
    private Button start_button;
    private static Canvas frontcanvas;
    private static Canvas backcanvas;
    private static Group frontcanvas_group;
    private static HBox sourcefile_box;
    private static VBox root;
    private static Pane canvas_pane;
    private static GraphicsContext graph, graph1;
    private static Scene scene;

    private static List<Integer[]> mainParamsList;

    @Override
    public void start(Stage stage) {

        MainView mainView = new MainView();

        this.primaryStage = stage;

        canvasside = axisMax * 2;

        // Форма приветствия
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        //welcome_message = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        // Инициализация canvas области
        frontcanvas = new Canvas(canvasside, canvasside);
        backcanvas = new Canvas(canvasside +indent*2, canvasside +indent*2);
        frontcanvas_group = new Group();
        frontcanvas_group.getChildren().add(frontcanvas);
        graph = frontcanvas.getGraphicsContext2D();
        graph1 = backcanvas.getGraphicsContext2D();
        graph.setFill(Color.BEIGE);
        graph1.setFill(Color.LIGHTGRAY);
        graph.fillRect(0,0, canvasside, canvasside);
        graph1.fillRect(0,0, canvasside +indent*2, canvasside +indent*2);
        frontcanvas_group.setTranslateX(indent);
        frontcanvas_group.setTranslateY(indent);

        // Инициализация поля, хранящего путь к входному файлу
        sourcefile_field = new TextField();         // Поле хранения пути к входному файлу, необходимо для взаимодействия с обработчиком кнопки (поскольку им нельзя взаимодействовать с переменной типа File )
        selected_file = new File("./1.txt");
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
            setSource_file(filechooser.showOpenDialog(primaryStage));
            if (getSource_file() != null) {
                sourcefile_field.setText(getSource_file().getAbsolutePath());
            }
        });

        // Инициализация панели вывода информации
        out_field = new TextArea();

        // Инициализация кнопки запуска процесса
        start_button = new Button("Read and Draw");
        start_button.setOnAction(e -> {
            try {
                new ReadDraw(getSource_file(), mainParamsList);
            } catch (IOException | ParseException ioException) {
                ioException.printStackTrace();
            }
        });

        // Первичная отрисовка в canvas
        drawAxis();

        // Компоновка панелей
        canvas_pane = new Pane(backcanvas, frontcanvas_group);
        sourcefile_box = new HBox(sourcefile_field, sourcefile_button);
        root = new VBox(canvas_pane, sourcefile_box, start_button, out_field);
        scene = new Scene(root);

        // Установка параметров объектов
        canvas_pane.setStyle("-fx-background-color: LIGHTGRAY;");
        sourcefile_box.setAlignment(Pos.CENTER);
        sourcefile_field.setPrefWidth(canvasside - sourcefile_button.getWidth());
        sourcefile_field.setEditable(false);
        out_field.setStyle("-fx-pref-height: 100");
        out_field.setEditable(false);
        root.setStyle("-fx-background-color: LIGHTGRAY;");
        root.setAlignment(Pos.CENTER);
        primaryStage.setTitle("Find Locations");

        // Отрисовка сцены
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void drawAxis() {
        Line axis_lineX = new Line(transX(-axisMax), transY(0), transX(axisMax), transY(0));      // Координаты Y в axisLineX отображаются в canvasside - n
        Line axis_lineY = new Line(transX(0), transY(-axisMax), transX(0), transY(axisMax));      // Координаты X в axisLineY отображаются в canvasside - n
        Line grid_line_axisX;
        Line grid_line_axisY;
        Text text_grid_axisX;
        Text text_grid_axisY;

        frontcanvas_group.getChildren().addAll(axis_lineX, axis_lineY);
        for(int i = (-axisMax / 50 + 1); i < (axisMax / 50); i++) {   //Draw Grid Axis X
            if (i != 0) {
                grid_line_axisX = new Line(transX(50 * i), transY(0), transX(50 * i), transY(10));
                text_grid_axisX = new Text(transX(50 * i + 2), transY(3), Integer.toString(50 * i));
                frontcanvas_group.getChildren().addAll(grid_line_axisX, text_grid_axisX);
            }
            else {
                text_grid_axisX = new Text(transX(50 * i + 2), transY(3), Integer.toString(50 * i));
                frontcanvas_group.getChildren().addAll(text_grid_axisX);
            }
        }
        for(int i = (-axisMax / 50 + 1); i < (axisMax / 50); i++) {   //Draw Grid Axis Y
            if (i != 0) {
                grid_line_axisY = new Line(transX(0), transY(50 * i), transX(10), transY(50 * i));
                text_grid_axisY = new Text(transX(2), transY(50 * i + 3), Integer.toString(50 * i));
                frontcanvas_group.getChildren().addAll(grid_line_axisY, text_grid_axisY);
            }
        }

    }

    public static void draw(Shape... shape) {
        Pane pane = new Pane(backcanvas, frontcanvas_group);
//        pane.setPrefSize(100, 100);
        Rectangle outputClip = new Rectangle();
        pane.setClip(outputClip);
        pane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
        root.getChildren().set(0, pane);
        for(Shape s: shape)     frontcanvas_group.getChildren().add(s);
    }

    public void drawClear() {
        frontcanvas_group.getChildren().clear();
        frontcanvas_group.getChildren().add(frontcanvas);
        drawAxis();
    }

    public static int transX(int x) {        return x + canvasside / 2;    }

    public static int transY(int y) {        return canvasside - y - canvasside / 2;    }

    public static void main(String[] args) {
        launch();
    }

    private File getSource_file() {
        return selected_file;
    }

    private void setSource_file(File sourceFile) {
        selected_file = sourceFile;
    }

}
