package timesonsensors;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    final static int canvasside = 600;
    final static int maxspeed = 50;
    final static int indent = 40;
    private Stage primaryStage;
    private Button stepbutton;
    private Button randbutton;
    private Button rstbutton;
    private Button clrbutton;
    private TextField textfield1, textfield2;
    static TextField textfield3;
    static TextField textfield4;
    static Label label7;
    private Label label0, label1, label2, label3, label4, label5, label6;
    //    private Affine affine;
    private Scene scene;
    private static Canvas frontcanvas;
    private static Canvas backcanvas;
    private static Group frontcanvasgroup;
    private Pane pane;
    private FlowPane flowpane;
    private static VBox root;
    private GraphicsContext graph, graph1;
    private MainApp mainApp;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            this.primaryStage = primaryStage;
            initScene();

            stepbutton.setOnAction(actionEvent -> {
//                boolean isNumeric = textfield.getText().chars().allMatch(x -> Character.isDigit(x));
                if (textfield1.getText().isEmpty() && textfield2.getText().isEmpty() && textfield3.getText().isEmpty())
                    mainApp.createSensor(0,0,0);
                else
                    if (textfield1.getText().isEmpty() && textfield2.getText().isEmpty() && textfield3.getText().matches("[1-9][0-9]*") &&
                            Integer.parseInt(textfield3.getText()) <= maxspeed)
                        mainApp.createSensor(0,0, Integer.parseInt(textfield3.getText()));
                else if ((textfield1.getText().matches("[1-9][0-9]*") && Integer.parseInt(textfield1.getText()) <= canvasside) &&
                            (textfield2.getText().matches("[1-9][0-9]*") && Integer.parseInt(textfield2.getText()) <= canvasside))
                {
                    if (textfield3.getText().matches("[1-9][0-9]*") && Integer.parseInt(textfield3.getText()) <= maxspeed)
                        mainApp.createSensor(Integer.parseInt(textfield1.getText()), Integer.parseInt(textfield2.getText()), Integer.parseInt(textfield3.getText()));
                    else
                        mainApp.createSensor(Integer.parseInt(textfield1.getText()), Integer.parseInt(textfield2.getText()), 0);
                } else
                    textfield4.setText("Enter num" + "<" + canvasside + "pts or speed correctly!");
            });

            randbutton.setOnAction(actionEvent -> {
                if (textfield3.getText().matches("[1-9][0-9]*") && Integer.parseInt(textfield3.getText()) <= maxspeed)
                    mainApp.createSensor(Integer.parseInt(textfield3.getText()));
                else if (textfield3.getText().isEmpty())
                    mainApp.createSensor();
                else
                    textfield4.setText("Enter num" + "<" + canvasside + "pts or speed correctly!");
            });

            rstbutton.setOnAction(actionEvent -> {
                drawClear();
                mainApp.resetSensors();
                textfield4.clear();
                label7.setText("gen pos: (-,-)");
            });

            clrbutton.setOnAction(actionEvent -> {
                textfield1.clear();
                textfield2.clear();
                textfield3.clear();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initScene() {
        stepbutton = new Button("Add 1");
        randbutton = new Button("Rand 1-5");
        rstbutton = new Button("Reset");
        clrbutton = new Button("Clear Fields");
        textfield1 = new TextField();
        textfield2 = new TextField();
        textfield3 = new TextField();
        textfield4 = new TextField();
        label0 = new Label("");
        label1 = new Label("x(cm) 1-" + canvasside + ":");
        label2 = new Label("y(cm) 1-" + canvasside + ":");
        label3 = new Label("v(m/s) 1-" + maxspeed + ":");
        label4 = new Label("");
        label5 = new Label("");
        label6 = new Label("time on sensor (in format: numsensor(x, y, speed, seconds)):");
        label7 = new Label("gen pos: (-,-)");
        frontcanvas = new Canvas(canvasside, canvasside);
        backcanvas = new Canvas(canvasside +indent*2, canvasside +indent*2);
        frontcanvasgroup = new Group();
        pane = new Pane(backcanvas, frontcanvasgroup);
        flowpane = new FlowPane(label1, textfield1, label2, textfield2, label3, textfield3, stepbutton, randbutton, rstbutton, clrbutton);
        root = new VBox(pane, label0, flowpane, label4, label7, label6, textfield4, label5);
        scene = new Scene(root, Color.LIGHTGRAY);
        mainApp = new MainApp();

        textfield1.setPrefWidth(50);
        textfield2.setPrefWidth(50);
        textfield3.setPrefWidth(50);
        textfield4.setPrefWidth(canvasside);
        textfield4.setEditable(false);
//        canvas.setStyle("-fx-background-color: BEIGE;");
//        canvas.setPrefSize(canvasSide, canvasSide);
        frontcanvasgroup.getChildren().add(frontcanvas);
        graph = frontcanvas.getGraphicsContext2D();
        graph1 = backcanvas.getGraphicsContext2D();
        graph.setFill(Color.BEIGE);
        graph1.setFill(Color.LIGHTGRAY);
        graph.fillRect(0,0, canvasside, canvasside);
        graph1.fillRect(0,0, canvasside +indent*2, canvasside +indent*2);
//        pane.setPrefSize(canvasSide, canvasSide);
        frontcanvasgroup.setTranslateX(indent);
        frontcanvasgroup.setTranslateY(indent);
        pane.setStyle("-fx-background-color: LIGHTGRAY;");
        drawAxis();

//        stackpane.setAlignment(Pos.CENTER);
        flowpane.setHgap(5);
        flowpane.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: LIGHTGRAY;");
        root.setAlignment(Pos.CENTER);
        primaryStage.setTitle("Times On Sensors");
//        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawAxis() {
        Line xAxs = new Line(0,0, canvasside,0);
        Line yAxs = new Line(0,0,0, canvasside);

        frontcanvasgroup.getChildren().addAll(xAxs, yAxs);
        for(int i = 1; i < canvasside/50; i++) {   //Draw Grid Axis X
            frontcanvasgroup.getChildren().addAll(new Line(0, 50 * i, 10, 50 * i), new Text(50 * i + 2, 11, Integer.toString(50 * i)));
        }
        for(int i = 1; i < canvasside/50; i++) {   //Draw Grid Axis Y
            frontcanvasgroup.getChildren().addAll(new Line(50 * i, 0, 50 * i, 10), new Text(2 , 50 * i + 12, Integer.toString(50 * i)));
        }
    }

    public static void draw(Shape... shape) {
        Pane pane = new Pane(backcanvas, frontcanvasgroup);
//        pane.setPrefSize(100, 100);
        Rectangle outputClip = new Rectangle();
        pane.setClip(outputClip);
        pane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
        root.getChildren().set(0, pane);
        for(Shape s: shape)     frontcanvasgroup.getChildren().add(s);
    }

    public void drawClear() {
        frontcanvasgroup.getChildren().clear();
        frontcanvasgroup.getChildren().add(frontcanvas);
        drawAxis();
    }

    public static void main(String[] args) {        launch(args);    }

}