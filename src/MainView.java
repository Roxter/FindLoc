import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
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

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainView extends VBox {

    private final int axisMax = 700;
    private final int indent = 40;
    private final int initRangePoints = 40;
    private final int initSpeed = 220;
    private final int initMinAmp = 1;
    private final int initMaxAmp = 999;
    private final int minPts = 2;
    private final int maxPts = 5;

    private final int front_canvas_side = axisMax * 2;
    private final int back_canvas_side = front_canvas_side +indent*2;
    private final int point_size = 1;

    private final AtomicBoolean is_started_ReadDraw;
    private final AtomicBoolean is_draw_points_on_circles;
    private long start_time;
    private long end_time;
    private String elapsed_time_in_str;

    private Label welcome_message;
    private final Label elapsed_time_label;
    private final Label range_label;
    private final Label speed_label;
    private final Label amp_label;
    private final Label min_pts_label;
    private final Label max_pts_label;
    private final TextField sourcefile_field;
    private final TextField range_field;
    private final TextField speed_field;
    private final TextField minAmp_field;
    private final TextField maxAmp_field;
    private final TextField min_pts_field;
    private final TextField max_pts_field;
    private final TextArea out_field1;
    private final TextArea out_field2;
    String work_dir;
    private File selected_file;
    private final FileChooser filechooser;
    private final Button sourcefile_button;
    private final Button start_button;
    private final Button diff_draw_button;
    private final Canvas frontcanvas;
    private final Canvas backcanvas;
    private final Group frontcanvas_group;
    private final HBox sourcefile_box;
    private final HBox start_box;
    private final HBox out_field_box;
    private final Pane canvas_pane;
    private final GraphicsContext graph, graph1;

    private final ParamsBox params;

    public MainView(Stage primaryStage) {

        // Инициализация переменных
        is_started_ReadDraw = new AtomicBoolean(false);
        is_draw_points_on_circles = new AtomicBoolean(true);
        params = new ParamsBox();

        // Инициализация текстовых меток
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        //welcome_message = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        range_label = new Label(" range:");
        speed_label = new Label(" speed:");
        amp_label = new Label(" amp:");
        min_pts_label = new Label(" range_pts: ");
        max_pts_label = new Label(" -");

        // Инициализация подсчёта времени выполнения
        elapsed_time_label = new Label("elapsed time:----------");

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
//        selected_file = new File("./in.txt");
        work_dir = "C:\\Users\\1\\IdeaProjects\\";
        selected_file = new File(work_dir + "in.txt");
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

        // Инициализация ограничительных полей
        range_field = new TextField(String.valueOf(initRangePoints));
        speed_field = new TextField(String.valueOf(initSpeed));
        minAmp_field = new TextField(String.valueOf(initMinAmp));
        maxAmp_field = new TextField(String.valueOf(initMaxAmp));
        min_pts_field = new TextField(String.valueOf(minPts));
        max_pts_field = new TextField(String.valueOf(maxPts));
        
        // Инициализация кнопки запуска процесса
        start_button = new Button("Read and Draw");
        ReadDraw read_and_draw = new ReadDraw(this, params);
        SolveDraw solve_and_draw = new SolveDraw(this, params);
        start_button.setOnAction(e -> {
            try {
                double elapsed_time;

                if (selected_file.exists() && !selected_file.isDirectory()) {
//                    System.out.println("in main() mainParamsList:" + params.mainParamsList().size());
                    params.clearRefOutParamsList();
//                    System.out.println("in main() mainParamsList:" + params.mainParamsList().size());

                    if (!is_started_ReadDraw.get()) {
                        drawClear();
                        clearTextOut();
                        is_started_ReadDraw.set(true);
                        start_button.setText("Solve and Draw");
                        params.clearMainParamsList();

                        read_and_draw.start(selected_file);
                    } else {
                        if (fieldsIsFill()) {
                            is_started_ReadDraw.set(false);
                            start_button.setText("Read and Draw");

                            start_time = System.currentTimeMillis();

                            solve_and_draw.run(selected_file, Integer.parseInt(range_field.getText()), Integer.parseInt(speed_field.getText()), Integer.parseInt(minAmp_field.getText()), Integer.parseInt(maxAmp_field.getText()), Integer.parseInt(min_pts_field.getText()), Integer.parseInt(max_pts_field.getText()));
    //                            diff_draw_button.fire();

                            end_time = System.currentTimeMillis() - start_time;
                            if (end_time < 60000) {
                                elapsed_time = (double)end_time / 1000.0;
                                elapsed_time_in_str = Double.toString(elapsed_time);
                                elapsed_time_label.setText(" elapsed time: " + elapsed_time_in_str + " secs");
                            }
                            else {
                                elapsed_time = end_time / 1000.0 / 60.0;
                                NumberFormat nf_out = NumberFormat.getNumberInstance(Locale.US);
                                nf_out.setMaximumFractionDigits(3);
                                elapsed_time_in_str = nf_out.format(elapsed_time);
                                elapsed_time_label.setText("elapsed_time elapsed_time: " + elapsed_time_in_str + " minutes");
                            }

                            drawAxis();
                            drawAllPoints();
                            drawOutPoints();

                        } else
                            range_field.setText("Enter a valid value!");
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        // Инициализация кнопки
        diff_draw_button = new Button("Save Canvas");
        diff_draw_button.setOnAction(e -> {
            /*if (!params.mainParamsList().isEmpty() && !params.refOutParamsList().isEmpty()) {
                drawClear();
                if (is_draw_points_on_circles.get()) {      // Снизу вверх: точки все - круги - оси - точки полученные
                    for (int i = 0; i < params.mainParamsList().size(); i++) {
                        drawPoint(params.mainParamsList().get(i)[5], params.mainParamsList().get(i)[6]);
                    }
                    for (int i = 0; i < params.refOutParamsList().size(); i++) {
                        drawCircle(params.refOutParamsList().get(i)[5], params.refOutParamsList().get(i)[6], false);
                    }
                    drawAxis();
                    for (int i = 0; i < params.refOutParamsList().size(); i++) {
                        drawPoint(params.refOutParamsList().get(i)[5], params.refOutParamsList().get(i)[6]);
                    }
                } else {                                   // Снизу вверх: круги - оси - точки все - оси
                    for (int i = 0; i < params.refOutParamsList().size(); i++) {
                        drawCircle(params.refOutParamsList().get(i)[5], params.refOutParamsList().get(i)[6], false);
                    }
                    drawAxis();
                    for (int i = 0; i < params.mainParamsList().size(); i++) {
                        drawPoint(params.mainParamsList().get(i)[5], params.mainParamsList().get(i)[6]);
                    }
                }

            } else
                out_field1.setText("Params is empty!");*/

            captureAndSaveDisplay();
        });

        // Инициализация панели вывода информации
        out_field1 = new TextArea();
        out_field2 = new TextArea();

        // Компоновка панелей
        canvas_pane = new Pane(backcanvas, frontcanvas_group);
        sourcefile_box = new HBox(sourcefile_field, sourcefile_button);
        start_box = new HBox(range_label, range_field, speed_label, speed_field, amp_label, minAmp_field, maxAmp_field, min_pts_label, min_pts_field, max_pts_label, max_pts_field, start_button, diff_draw_button, elapsed_time_label);
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
        speed_field.setStyle("-fx-pref-width: 40");
        minAmp_field.setStyle("-fx-pref-width: 40");
        maxAmp_field.setStyle("-fx-pref-width: 40");
        min_pts_field.setStyle("-fx-pref-width: 40");
        max_pts_field.setStyle("-fx-pref-width: 40");
        out_field1.setStyle("-fx-pref-height: 100");
        out_field1.setEditable(false);
        out_field2.setStyle("-fx-pref-height: 100");
        out_field2.setEditable(false);
        this.setStyle("-fx-background-color: LIGHTGRAY;");
        this.setAlignment(Pos.CENTER);

        // Первичная отрисовка в canvas
        drawAxis();
    }

    private boolean fieldsIsFill() {
        return range_field.getText().matches("[1-9][0-9]*") && speed_field.getText().matches("[1-9][0-9]*") && minAmp_field.getText().matches("[1-9][0-9]*")&& maxAmp_field.getText().matches("[1-9][0-9]*") && min_pts_field.getText().matches("[1-9][0-9]*") && max_pts_field.getText().matches("[1-9][0-9]*");
    }

    public void captureAndSaveDisplay(){
        String out_file_string;
        if (!params.mainParamsList().isEmpty() && !params.refOutParamsList().isEmpty() && elapsed_time_in_str != null) {
            out_file_string = selected_file.getPath().replaceAll(selected_file.getName(), "in" + params.mainParamsList().size() + "_r" + Integer.parseInt(range_field.getText()) + "_s" + Integer.parseInt(speed_field.getText()) + "_minamp" + Integer.parseInt(minAmp_field.getText()) + "_maxamp" + Integer.parseInt(maxAmp_field.getText()) + "_p" + Integer.parseInt(min_pts_field.getText()) + "to" + Integer.parseInt(max_pts_field.getText()) + "_out" + params.outParamsList().size() + "_time" + elapsed_time_in_str + ".png");
        } else {
            out_file_string = selected_file.getPath().replaceAll(selected_file.getName(), selected_file.getName() + ".png");
        }
        File out_img_file = new File(out_file_string);
        try {
            WritableImage writableImage = frontcanvas_group.snapshot(new SnapshotParameters(), null);
//            snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", out_img_file);
        } catch (IOException ex) {
            ex.printStackTrace();
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

    private void drawAllPoints() {
        for (int i = 0; i < params.mainParamsList().size(); i++) {
            drawPoint(params.mainParamsList().get(i).get(5), params.mainParamsList().get(i).get(6));
        }
    }

    private void drawOutPoints() {
        for (int i = 0; i < params.outParamsList().size(); i++) {
            drawPointColor(params.outParamsList().get(i).get(6), params.outParamsList().get(i).get(7), Color.GRAY);
        }
    }

    public Integer transX(Integer x) {        return x + front_canvas_side / 2;    }

    public Integer transY(Integer y) {        return front_canvas_side - y - front_canvas_side / 2;    }

    public void drawAxis() {
        Color color = Color.LIGHTBLUE;
        Line axis_lineX = new Line(transX(-axisMax), transY(0), transX(axisMax), transY(0));      // Координаты Y в axisLineX отображаются в canvasside - n
        Line axis_lineY = new Line(transX(0), transY(-axisMax), transX(0), transY(axisMax));      // Координаты X в axisLineY отображаются в canvasside - n
        Line grid_line_axisX;
        Line grid_line_axisY;
        Text text_grid_axisX;
        Text text_grid_axisY;

        axis_lineX.setStroke(color);
        axis_lineX.setStrokeWidth(1);
        axis_lineY.setStroke(color);
        axis_lineY.setStrokeWidth(1);
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

    public void drawPoint(Integer x, Integer y) {
        Rectangle point = new Rectangle();
        point.setX(transX(x));
        point.setY(transY(y));
        point.setWidth(point_size);
        point.setHeight(point_size);
        draw(point);
    }

    public void drawPointColor(Integer x, Integer y, Color c) {
        Rectangle point = new Rectangle();
        point.setX(transX(x));
        point.setY(transY(y));
        point.setWidth(point_size);
        point.setHeight(point_size);
        point.setFill(c);
        draw(point);
    }

    public void drawCircle(Integer x, Integer y, boolean is_amp) {
        if (fieldsIsFill()) {
            Circle circle = new Circle(Integer.parseInt(range_field.getText()));

            circle.setCenterX(transX(x));
            circle.setCenterY(transY(y));
            if (!is_amp)
                circle.setFill(Color.rgb(0, (int)(Math.random() * 255), (int)(Math.random() * 255)));
            else
                circle.setFill(Color.rgb(255, 0,0));
//            circle.setFill(Color.rgb((num_ex*20+20) % 255, (num_ex*20+50) % 255, (num_ex*20+133) % 255));
//            circle.setFill(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
//        circle.setStrokeWidth(1);
//        circle.setStroke(Color.BLACK);
            draw(circle);
        } else
            range_field.setText("Enter a valid value!");
    }

    public void drawRectangle(Integer start_x, Integer start_y, Integer end_x, Integer end_y) {
        if (fieldsIsFill()) {
            Color color_lines = Color.YELLOW;

            Rectangle rectangle;
            rectangle = new Rectangle(start_x, start_y, Math.abs(end_x-start_x), Math.abs(end_y-start_y));
            rectangle.setFill(Color.GREEN);
//            System.out.format("                %d %d %d %d%n", start_x, start_y, end_x, end_y);

            Line line1 = new Line(start_x, start_y, end_x, start_y);
            Line line2 = new Line(end_x, end_y, end_x, start_y);
            Line line3 = new Line(end_x, end_y, start_x, end_y);
            Line line4 = new Line(start_x, start_y, start_x, end_y);

            line1.setStroke(color_lines);
            line1.setStrokeWidth(1);
            line2.setStroke(color_lines);
            line2.setStrokeWidth(1);
            line3.setStroke(color_lines);
            line3.setStrokeWidth(1);
            line4.setStroke(color_lines);
            line4.setStrokeWidth(1);

            draw(rectangle);
            draw(line1);
            draw(line2);
            draw(line3);
            draw(line4);
        } else
            range_field.setText("Enter a valid value!");
    }

    public void drawClear() {
        frontcanvas_group.getChildren().clear();
        frontcanvas_group.getChildren().add(frontcanvas);
        drawAxis();
        elapsed_time_label.setText("elapsed time:----------");
    }

    public void toTextOut1(String str) { out_field1.appendText(str); }

    public void toTextOut2(String str) { out_field2.appendText(str); }

    public void clearTextOut() {
        out_field1.clear();
        out_field2.clear();
    }

}