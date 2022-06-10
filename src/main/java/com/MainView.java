package com;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

class MainView extends BorderPane {

    private int axisMax = 1;
    private final int initRangePoints = 18;
    private final int initSpeed = 220;
    private final int initMinAmp = 47;
    private final int initMaxAmp = 999;
    private final int minPts = 2;
    private final int maxPts = 5;

    private final int point_size = 1;
    private final int line_size = 1;
    private final String font_params_label = "arial";
    private final int border_Pane = 20;
    private final int min_width_inp_fields = 40;
    int window_init_width = 600;
    int window_init_height = 600;
    int window_min_width;
    int window_min_height = 400;
//    int window_max_width = 800;
//        int window_max_height = 600;

    Color color_point_cluster = Color.GRAY;
    Color color_border_rect_cluster = Color.YELLOW;
    Color color_rect_cluster = Color.GREEN;
    Color color_axis = Color.LIGHTBLUE;

    private final AtomicBoolean is_started_ReadDraw;
//    private final AtomicBoolean is_draw_points_on_circles;
    private long start_time;
    private long end_time;
    private double elapsed_time = -1.0;
    private String elapsed_time_in_str;
    int square_clusters = 0;
    int points_in_clusters = 0;

//    private Text welcome_message;
    private final Text range_label;
    private final Text speed_label;
    private final Text amp_label;
    private final Text min_pts_label;
    private final Text max_pts_label;
    private final Text draw_params_label;
    private final Text elapsed_time_label;
    private final Text points_in_clusters_label;
    private final Text points_after_filter_amp_label;
    private final Text square_clusters_label;
    private Text params_label;
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
    private final CheckBox draw_params;
    private final Button sourcefile_button;
    private final Button read_and_draw_button;
    private final Button save_canvas_button;
    private final Pane canvas_pane;
    private final ScrollPane scroll_pane;
    private final HBox sourcefile_pane;
    private final HBox params_pane;
    private final HBox control_pane;
    private final HBox out_field_pane;
    private final VBox bottom_pane;

    private final ParamsBox paramsBox;

    public MainView(Stage primaryStage) {

        // Инициализация переменных
        //axisMax = Integer.parseInt(axisMaxFromMain);
        //String javaVersion = System.getProperty("java.version");
        //String javafxVersion = System.getProperty("javafx.version");
        is_started_ReadDraw = new AtomicBoolean(false);
//        is_draw_points_on_circles = new AtomicBoolean(true);
        paramsBox = new ParamsBox();
        ReadDraw read_and_draw = new ReadDraw(this, paramsBox);
        SolveDraw solve_and_draw = new SolveDraw(this, paramsBox);

        // Инициализация элементов
        // Инициализация выбора входного файла
        sourcefile_field = new TextField();         // Поле хранения пути к входному файлу, необходимо для взаимодействия с обработчиком кнопки (поскольку им нельзя взаимодействовать с переменной типа File )
        //selected_file = new File("./in.txt");
        work_dir = "C:\\Users\\1\\IdeaProjects\\";
        selected_file = new File(work_dir + "in.txt");
        if (selected_file.exists() && !selected_file.isDirectory())
            sourcefile_field.setText(selected_file.getAbsolutePath());
        else
            sourcefile_field.setText("Выберите файл с расширением .txt");
        filechooser = new FileChooser();
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        filechooser.setInitialDirectory(new File("."));
        // Инициализация кнопок
        draw_params = new CheckBox();
        sourcefile_button = new Button("Input File");
        read_and_draw_button = new Button("Read and Draw");
//        System.out.println((int) read_and_draw_button.getPr);
        save_canvas_button = new Button("Save Canvas");
        // Инициализация полей ввода ограничений
        range_field = new TextField(String.valueOf(initRangePoints));
        speed_field = new TextField(String.valueOf(initSpeed));
        minAmp_field = new TextField(String.valueOf(initMinAmp));
        maxAmp_field = new TextField(String.valueOf(initMaxAmp));
        min_pts_field = new TextField(String.valueOf(minPts));
        max_pts_field = new TextField(String.valueOf(maxPts));
        out_field1 = new TextArea();
        out_field2 = new TextArea();
        // Инициализация тестовых меток
        //welcome_message = new Text("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        range_label = new Text("range:");
        speed_label = new Text(" speed:");
        amp_label = new Text(" amp:");
        min_pts_label = new Text(" range_pts: ");
        max_pts_label = new Text(" -");
        draw_params_label = new Text(" params on graph: ");
        elapsed_time_label = new Text("elapsed time:  ----------");
        points_after_filter_amp_label = new Text("points after filter amp:  ----------");
        points_in_clusters_label = new Text("points in clusters:  ----------");
        square_clusters_label = new Text("square of clusters:  ----------");

        // Компоновка элементов на панелях
        sourcefile_pane = new HBox(sourcefile_field, sourcefile_button);
        params_pane = new HBox(
            range_label, range_field, speed_label, speed_field, amp_label, minAmp_field, maxAmp_field,
            min_pts_label, min_pts_field, max_pts_label, max_pts_field, draw_params_label, draw_params, read_and_draw_button, save_canvas_button
        );
        out_field_pane = new HBox(out_field1, out_field2);
        control_pane = new HBox(elapsed_time_label, points_after_filter_amp_label, points_in_clusters_label, square_clusters_label);
        bottom_pane = new VBox(sourcefile_pane, params_pane, control_pane, out_field_pane);
        canvas_pane = new Pane();
        scroll_pane = new ScrollPane(canvas_pane);
        setCenter(scroll_pane);
        setBottom(bottom_pane);

        // Привязка элементов
        HBox.setHgrow(sourcefile_field, Priority.ALWAYS);
        HBox.setHgrow(range_field, Priority.ALWAYS);
        HBox.setHgrow(speed_field, Priority.ALWAYS);
        HBox.setHgrow(minAmp_field, Priority.ALWAYS);
        HBox.setHgrow(maxAmp_field, Priority.ALWAYS);
        HBox.setHgrow(min_pts_field, Priority.ALWAYS);
        HBox.setHgrow(max_pts_field, Priority.ALWAYS);
        HBox.setHgrow(out_field1, Priority.ALWAYS);
        HBox.setHgrow(out_field2, Priority.ALWAYS);
//        HBox.setHgrow(sourcefile_button, Priority.ALWAYS);
//        HBox.setHgrow(read_and_draw_button, Priority.ALWAYS);
//        HBox.setHgrow(save_canvas_button, Priority.ALWAYS);
//        sourcefile_field.setMinWidth(Region.USE_PREF_SIZE);
//        sourcefile_button.setMinWidth(Region.USE_PREF_SIZE);
//        read_and_draw_button.setMinWidth(Region.USE_PREF_SIZE);
//        save_canvas_button.setMinWidth(Region.USE_PREF_SIZE);
//        out_field1.setMinWidth(Region.USE_PREF_SIZE);
//        out_field2.setMinWidth(Region.USE_PREF_SIZE);
//        range_field.setMinWidth(Region.USE_PREF_SIZE);
//        speed_field.setMinWidth(Region.USE_PREF_SIZE);
//        minAmp_field.setMinWidth(Region.USE_PREF_SIZE);
//        maxAmp_field.setMinWidth(Region.USE_PREF_SIZE);
//        min_pts_field.setMinWidth(Region.USE_PREF_SIZE);
//        max_pts_field.setMinWidth(Region.USE_PREF_SIZE);

        // Настройка элементов
        sourcefile_field.setEditable(false);
        draw_params.setAlignment(Pos.CENTER_RIGHT);
        read_and_draw_button.setAlignment(Pos.BASELINE_RIGHT);
//        range_field.setStyle("-fx-max-width: " + min_width_inp_fields + ";");     // для запрета расширения полей при расширении окна
//        speed_field.setStyle("-fx-max-width: " + min_width_inp_fields + ";");
//        minAmp_field.setStyle("-fx-max-width: " + min_width_inp_fields + ";");
//        maxAmp_field.setStyle("-fx-max-width: " + min_width_inp_fields + ";");
//        min_pts_field.setStyle("-fx-max-width: " + min_width_inp_fields + ";");
//        max_pts_field.setStyle("-fx-max-width: " + min_width_inp_fields + ";");
//        out_field2.setMinHeight(75);
        draw_params.setMinWidth(20);
        read_and_draw_button.setMinWidth(93);    // чтобы была возможность определить точную ширину окна по макс. расположению элементов
//        read_and_draw_button.setAlignment(Pos.CENTER);
        save_canvas_button.setMinWidth(80);
        // Расположение элементов
        range_label.setTextAlignment(TextAlignment.CENTER);
        window_min_width = (int) Math.round(range_label.prefWidth(-1) + speed_label.prefWidth(-1) +
                amp_label.prefWidth(-1) + min_pts_label.prefWidth(-1) +
                draw_params_label.prefWidth(-1) + max_pts_label.prefWidth(-1) +
                draw_params.getMinWidth() + read_and_draw_button.getMinWidth() + read_and_draw_button.getMinWidth()) +
                min_width_inp_fields * 6 + border_Pane * 2;
//        System.out.println("draw_params.getPrefWidth:" + draw_params.getPrefWidth());
//        System.out.println(range_label.prefWidth(-1) + " " + speed_label.prefWidth(-1) + " " +
//                amp_label.prefWidth(-1) + " " + min_pts_label.prefWidth(-1) + " "  + max_pts_label.prefWidth(-1) + " " +
//                read_and_draw_button.getMinWidth() + " " + read_and_draw_button.getMinWidth() + " " +
//                min_width_inp_fields * 6 + " " + border_Pane * 2);
//        out_field1.setMinWidth(window_min_width / 2.0);
        out_field1.setEditable(false);
        out_field1.maxHeight(50);
//        out_field2.setMinWidth(window_min_width / 2.0);
        out_field1.setStyle("-fx-pref-height: 100");
        out_field2.setEditable(false);
        out_field2.setStyle("-fx-pref-height: 100");
//        out_field1.maxHeight(50);

        // Настройка панелей
        scroll_pane.setPannable(true);
        scroll_pane.setCenterShape(true);
        double control_pane_height = bottom_pane.getBoundsInLocal().getHeight();
        scroll_pane.setPrefHeight(getHeight() - control_pane_height);
        sourcefile_pane.setStyle("-fx-padding: "  + border_Pane + " 0 " + border_Pane / 2 + " 0;");
        params_pane.setStyle("-fx-padding: 0 0 " + border_Pane / 2 + " 0;");
        control_pane.setStyle("-fx-padding: 0 0 " + border_Pane / 2 + " 0;");
        control_pane.setSpacing(10);
        out_field_pane.setSpacing(10);
        //out_field_box.setStyle("-fx-padding: 0 0 " + border_scrollPane / 2 + " 0;");
        canvas_pane.setStyle("-fx-background-color: BEIGE;");
        //scroll_pane.setStyle("-fx-border-width: " + border_scrollPane + ";" + "-fx-border-color: LIGHTGRAY;");
        // Расположение панелей
        sourcefile_pane.setAlignment(Pos.CENTER);
        params_pane.setAlignment(Pos.CENTER);
        control_pane.setAlignment(Pos.CENTER);
        out_field_pane.setAlignment(Pos.CENTER);
        bottom_pane.setAlignment(Pos.CENTER);
        //scroll_pane.setAlignment(Pos.TOP_CENTER);
//        setAlignment(Pos.CENTER);
        // Установка параметров главного окна
        setStyle("-fx-background-color: LIGHTGRAY; -fx-padding: " + border_Pane + ";");
        primaryStage.setTitle("Find Locations");
        primaryStage.setWidth(window_init_width);
        primaryStage.setHeight(window_init_height);
        primaryStage.setMinWidth(window_min_width);
        primaryStage.setMinHeight(window_min_height);
//        primaryStage.minWidthProperty().bind(read_and_draw_button.widthProperty());

        // Первичная отрисовка осей
//        axisMax = window_start_width / 2 - border_scrollPane * 2;
//        System.out.println(axisMax);
//        drawAxis();   // делает некорректно перемещаемые setVvalue и setHvalue в обработчике start_button с первого его запуска (со второго отрабатывает корректно)
//        scroll_pane.setVvalue(0.5);
//        scroll_pane.setHvalue(0.5);

        // Обработчики событий
        centerPaneOnScroll(scroll_pane.getViewportBounds(), canvas_pane);
        scroll_pane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> centerPaneOnScroll(newValue, canvas_pane));
        sourcefile_button.setOnAction(e -> {
            File f = filechooser.showOpenDialog(primaryStage);
            if (f != null) {
                selected_file = f;
                sourcefile_field.setText(selected_file.getAbsolutePath());
            }
        });
        save_canvas_button.setOnAction(e -> captureAndSaveDisplay());
        read_and_draw_button.setOnAction(e -> {
            try {
                if (selected_file.exists() && !selected_file.isDirectory()) {
//                    System.out.println("in main() mainParamsList:" + params.mainParamsList().size());
                    paramsBox.clearRefOutParamsList();
//                    System.out.println("in main() mainParamsList:" + params.mainParamsList().size());

                    if (!is_started_ReadDraw.get()) {
                        is_started_ReadDraw.set(true);
                        read_and_draw_button.setText("Solve and Draw");
//                        paramsBox.clearMainParamsList();
                        paramsBox.clearAllParams();

                        read_and_draw.start(selected_file);
//                        calc_square_clusters();
                        clearTextOut();
                        drawClear();
                        drawAxis();
                        drawAllPoints();
                        scroll_pane.setVvalue(0.5);
                        scroll_pane.setHvalue(0.5);

                    } else {
                        if (fieldsIsFill()) {
                            is_started_ReadDraw.set(false);
                            read_and_draw_button.setText("Read and Draw");

                            start_time = System.currentTimeMillis();

                            solve_and_draw.start(selected_file, Integer.parseInt(range_field.getText()), Integer.parseInt(speed_field.getText()), Integer.parseInt(minAmp_field.getText()),
                                    Integer.parseInt(maxAmp_field.getText()), Integer.parseInt(min_pts_field.getText()), Integer.parseInt(max_pts_field.getText()));

                            end_time = System.currentTimeMillis() - start_time;
                            if (end_time < 60000) {
                                elapsed_time = (double)end_time / 1000.0;
                                elapsed_time_in_str = elapsed_time + " secs";
                                elapsed_time_label.setText(" elapsed time: " + elapsed_time_in_str);
                            }
                            else {
                                elapsed_time = end_time / 1000.0 / 60.0;
                                NumberFormat nf_out = NumberFormat.getNumberInstance(Locale.US);
                                nf_out.setMaximumFractionDigits(3);
                                elapsed_time_in_str = nf_out.format(elapsed_time) + " minutes";
                                elapsed_time_label.setText("elapsed_time: " + elapsed_time_in_str);
                            }
                            points_after_filter_amp_label.setText("points after filter amp: " + paramsBox.postProcList().size());
                            points_in_clusters_label.setText("points in clusters: " + points_in_clusters);
                            calc_square_clusters();
                            square_clusters_label.setText("square of clusters: " + square_clusters);

                            drawAxis();
                            drawAllPoints();
                            drawOutPoints();
                            draw_params();
                            scroll_pane.setVvalue(0.5);
                            scroll_pane.setHvalue(0.5);
                        } else
                            range_field.setText("Enter a valid value!");
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        draw_params.setOnAction(e -> draw_params());
    }

    private void draw_params() {
        if (draw_params.isSelected()) {
            if (fieldsIsFill() && elapsed_time != -1.0) {
                String params = "Input Strings = " + paramsBox.mainParamsList().size() +
                        "\nMax Radius = " + Integer.parseInt(range_field.getText()) +
                        "\nMax Diff Speed = " + Integer.parseInt(speed_field.getText()) +
                        "\nMin Amp = " + Integer.parseInt(minAmp_field.getText()) +
                        "\nMax Amp = " + Integer.parseInt(maxAmp_field.getText()) +
                        "\nRange points in Cluster = " + Integer.parseInt(min_pts_field.getText()) +
                        " to " + Integer.parseInt(max_pts_field.getText()) +
                        "\nTime: " + elapsed_time_in_str +
                        "\nPoints after filter amp: " + paramsBox.postProcList().size() +
                        "\nPoints in clusters: " + points_in_clusters +
                        "\nSquare clusters: " + square_clusters;
                params_label = new Text(axisMax * 2 - 300, axisMax * 2 - 215, params);
                params_label.setStyle("-fx-font: 20 " + font_params_label + ";");
                drawShape(params_label);
            }
        } else {
            drawClearParams();
        }
    }

    private void calc_square_clusters() {
        String path_img_file = "C:\\Users\\Public\\in.png";
        Path path = Paths.get(path_img_file);

        try {
            File out_img_file = new File(path_img_file);
            WritableImage writableImage = canvas_pane.snapshot(new SnapshotParameters(), null);
//            snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);

            ImageIO.write(renderedImage, "png", out_img_file);
            InputStream is = Files.newInputStream(path);
            BufferedImage bi = ImageIO.read(is); // Use ImageIO to create a BufferedImage
//            BufferedImage bi = ImageIO.read(out_img_file); // For Input file
//                int color1 = bi.getRGB(96, 488);
//                System.out.printf("%d %d %d %d", (color1 & 0xff0000) >> 16, (color1 & 0xff00) >> 8, color1 & 0xff, (color1 & 0xff000000) >>> 24);

            int start_w = 0;
            int start_h = 0;
//            int end_w = 184;
//            int end_h = 474;
            int end_w = bi.getWidth();
            int end_h = bi.getHeight();
            for (int i = start_h; i < end_h; i++) {
                for (int j = start_w; j < end_w; j++) {
                    java.awt.Color color_awt = new java.awt.Color(bi.getRGB(j, i));
                    int red = color_awt.getRed();
                    int green = color_awt.getGreen();
                    int blue = color_awt.getBlue();

//                    Color color = new Color(color_awt.getRed() / 255.0, color_awt.getGreen() / 255.0, color_awt.getBlue() / 255.0, color_awt.getAlpha() / 255.0); // bi.getRGB returns an integer like -14350844, representing the specific color. use Color class to get the individual colors with: myColor.getBlue()...

                    if (red == (int)(color_border_rect_cluster.getRed() * 255) && green == (int)(color_border_rect_cluster.getGreen() * 255) && blue == (int)(color_border_rect_cluster.getBlue() * 255) ||
                            red == (int)(color_rect_cluster.getRed() * 255) && green == (int)(color_rect_cluster.getGreen() * 255) && blue == (int)(color_rect_cluster.getBlue() * 255) ||
                            red == (int)(color_point_cluster.getRed() * 255) && green == (int)(color_point_cluster.getGreen() * 255) && blue == (int)(color_point_cluster.getBlue() * 255)
                    ) {
                        square_clusters = square_clusters + 1;
                        //                    System.out.printf("%d %d %d %d %d %d\n", red, green, blue, (int)(color_border_rect_cluster.getRed() * 255), (int)(color_border_rect_cluster.getGreen() * 255), (int)(color_border_rect_cluster.getBlue() * 255));
                    }
                }
            }

            String message;
            if (out_img_file.exists())
                message =  "is in use by another app";
            else
                message = "does not exist";
            if (!out_img_file.delete())
                throw new IOException("Cannot delete file, because file " + message + ".");
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void resizeMain() {
        System.out.println(read_and_draw_button.getWidth());
    }

    private void centerPaneOnScroll(Bounds viewPortBounds, Node centeredNode) {
        double width = viewPortBounds.getWidth();
        double height = viewPortBounds.getHeight();
        if (width > centeredNode.getBoundsInLocal().getWidth()) {
            centeredNode.setTranslateX((width - centeredNode.getBoundsInLocal().getWidth()) / 2);
        } else {
            centeredNode.setTranslateX(0);
        }
        if (height > centeredNode.getBoundsInLocal().getHeight()) {
            centeredNode.setTranslateY((height - centeredNode.getBoundsInLocal().getHeight()) / 2);
        } else {
            centeredNode.setTranslateY(0);
        }
    }

    private double snap_for_line(int n) {
        return (((double) n) + .5);
    }

    private boolean fieldsIsFill() {
        return range_field.getText().matches("[1-9][0-9]*") && speed_field.getText().matches("[1-9][0-9]*") &&
                minAmp_field.getText().matches("[1-9][0-9]*")&& maxAmp_field.getText().matches("[1-9][0-9]*") &&
                min_pts_field.getText().matches("[1-9][0-9]*") && max_pts_field.getText().matches("[1-9][0-9]*");
    }

    void captureAndSaveDisplay(){
        String out_file_string;
        if (!paramsBox.mainParamsList().isEmpty() && !paramsBox.refOutParamsList().isEmpty() && elapsed_time_in_str != null) {
            out_file_string = selected_file.getPath().replaceAll(selected_file.getName(),
    "in" + paramsBox.mainParamsList().size() +
                "_r" + Integer.parseInt(range_field.getText()) +
                "_s" + Integer.parseInt(speed_field.getText()) +
                "_minamp" + Integer.parseInt(minAmp_field.getText()) +
                "_maxamp" + Integer.parseInt(maxAmp_field.getText()) +
                "_p" + Integer.parseInt(min_pts_field.getText()) +
                "to" + Integer.parseInt(max_pts_field.getText()) +
                "_out" + paramsBox.outParamsList().size() +
                "_time" + elapsed_time_in_str +
                "_pts" + points_in_clusters +
                "_sq" + square_clusters +
                ".png");
        } else {
            out_file_string = selected_file.getPath().replaceAll(selected_file.getName(), selected_file.getName() + ".png");
        }
        File out_img_file = new File(out_file_string);
        try {
            WritableImage writableImage = canvas_pane.snapshot(new SnapshotParameters(), null);
//            snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", out_img_file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void drawShape(Shape... shape) {
//        BorderPane pane = new BorderPane(frontcanvas_group);
//        pane.setPrefSize(100, 100);
//        Rectangle outputClip = new Rectangle();
//        pane.setClip(outputClip);
//        pane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
//            outputClip.setWidth(newValue.getWidth());
//            outputClip.setHeight(newValue.getHeight());
//        });
//        getChildren().set(0, pane);
        for (Shape s: shape)     canvas_pane.getChildren().add(s);
    }

    private void drawAllPoints() {
//        draw(new Text(window_min_height, window_min_width, Integer.toString(window_min_height) + " " + Integer.toString(window_min_width)));
//        draw(new Text(window_min_height, window_max_width, Integer.toString(window_min_height) + " " + Integer.toString(window_max_width)));
//        draw(new Text(window_max_height, window_min_width, Integer.toString(window_max_height) + " " + Integer.toString(window_min_width)));
//        draw(new Text(window_max_height, window_max_width, Integer.toString(window_max_height) + " " + Integer.toString(window_max_width)));

//        draw(new Text(paramsBox.found_xmin, paramsBox.found_ymin, "XminYmin:" + Integer.toString(paramsBox.found_xmin) + ";" + Integer.toString(paramsBox.found_ymin)));
//        draw(new Text(paramsBox.found_xmin, paramsBox.found_ymax, "XminYmax:" + Integer.toString(paramsBox.found_xmin) + ";" + Integer.toString(paramsBox.found_ymax)));
//        draw(new Text(paramsBox.found_xmax, paramsBox.found_ymin, "XmaxYmin:" + Integer.toString(paramsBox.found_xmax) + ";" + Integer.toString(paramsBox.found_ymin)));
//        draw(new Text(paramsBox.found_xmax+80, paramsBox.found_ymax, "XmaxYmax:" + Integer.toString(paramsBox.found_xmax) + ";" + Integer.toString(paramsBox.found_ymax)));
        for (List<Integer> l: paramsBox.mainParamsList()) {
            drawPoint(l.get(paramsBox.x_pos_in_pattern), l.get(paramsBox.y_pos_in_pattern));
            // Отрисовка координат
//            drawShape(new Text(transX(l.get(x_pos_in_pattern)), transY(l.get(y_pos_in_pattern)), Integer.toString(l.get(x_pos_in_pattern)) + " " + Integer.toString(l.get(y_pos_in_pattern))));
        }
    }

    private void drawOutPoints() {
        for (List<Integer> l: paramsBox.outParamsList())
            drawPointColor(l.get(paramsBox.x_pos_in_pattern + 1), l.get(paramsBox.y_pos_in_pattern + 1), color_point_cluster);
    }

    Integer transX(int x) {        return x + axisMax;    }

    Integer transY(int y) {        return axisMax * 2 - y - axisMax;    }

    void drawAxis() {
        Line axis_lineX;
        Line axis_lineY;
        Line grid_line_axisX;
        Line grid_line_axisY;
        Text text_grid_axisX;
        Text text_grid_axisY;

//        int current_width_canvas = (int) canvas_pane.getBoundsInLocal().getWidth();
//        int current_height_canvas = (int) canvas_pane.getBoundsInLocal().getHeight();

//        int space_for_shift_x;

//        if (params.found_xmin && params.found_xmax > 0)

        // Обновление размера осей координат
        if (axisMax < Math.abs(paramsBox.found_xmin) && paramsBox.found_xmin != Integer.MAX_VALUE)
            axisMax = Math.abs(paramsBox.found_xmin);
        if (axisMax < Math.abs(paramsBox.found_xmax) && paramsBox.found_xmax != Integer.MIN_VALUE)
            axisMax = Math.abs(paramsBox.found_xmax);
        if (axisMax < Math.abs(paramsBox.found_ymin) && paramsBox.found_ymin != Integer.MAX_VALUE)
            axisMax = Math.abs(paramsBox.found_ymin);
        if (axisMax < Math.abs(paramsBox.found_ymax) && paramsBox.found_ymax != Integer.MIN_VALUE)
            axisMax = Math.abs(paramsBox.found_ymax);

//        System.out.println("Founded XYMin:" + paramsBox.found_xmin + ";" + paramsBox.found_ymin + " Founded XYMax:" + paramsBox.found_xmax + ";" + paramsBox.found_ymax + " AxisMax:" + axisMax);

        axis_lineX = new Line(
                snap_for_line(transX(-axisMax)), snap_for_line(transY(0)),
                snap_for_line(transX(axisMax)), snap_for_line(transY(0))
        );      // Координаты Y в axisLineX отображаются в canvasside - n

        axis_lineY = new Line(
                snap_for_line(transX(0)), snap_for_line(transY(-axisMax)),
                snap_for_line(transX(0)), snap_for_line(transY(axisMax))
        );      // Координаты X в axisLineY отображаются в canvasside - n
        axis_lineX.setStroke(color_axis);
        axis_lineX.setStrokeWidth(line_size);
        axis_lineY.setStroke(color_axis);
        axis_lineY.setStrokeWidth(line_size);

        drawShape(axis_lineX, axis_lineY);

        // Отрисовка крайних координат осей
//        drawShape(new Text(transX(-axisMax), transY(0), Integer.toString(transX(-axisMax)) + ";" + Integer.toString(transY(0))));
//        drawShape(new Text(transX(axisMax), transY(0), Integer.toString(transX(axisMax)) + ";" + Integer.toString(transY(0))));
//        drawShape(new Text(transY(0), transX(-axisMax), Integer.toString(transX(0)) + ";" + Integer.toString(transY(-axisMax))));
//        drawShape(new Text(transY(0), transX(axisMax), Integer.toString(transX(0)) + ";" + Integer.toString(transY(axisMax))));

        // Рисуем разметку оси X с шагом 50 точек
        for (int i = (-axisMax / 50); i <= (axisMax / 50); i++) {
            if (i != 0) {
                grid_line_axisX = new Line(
                        snap_for_line(transX(50 * i)), snap_for_line(transY(0)),
                        snap_for_line(transX(50 * i)), snap_for_line(transY(10))
                );
                grid_line_axisX.setStroke(color_axis);
                grid_line_axisX.setStrokeWidth(line_size);
                text_grid_axisX = new Text(transX(50 * i + 2), transY(3), Integer.toString(50 * i));
                text_grid_axisX.setFill(color_axis);
                drawShape(grid_line_axisX, text_grid_axisX);
            }
            else {
                text_grid_axisX = new Text(transX(2), transY(3), Integer.toString(0));
                text_grid_axisX.setFill(color_axis);
                drawShape(text_grid_axisX);
            }
        }

        // Рисуем разметку оси Y с шагом 50 точек
        for (int i = (-axisMax / 50); i <= (axisMax / 50); i++) {
            if (i != 0) {
                grid_line_axisY = new Line(
                        snap_for_line(transX(0)), snap_for_line(transY(50 * i)),
                        snap_for_line(transX(10)), snap_for_line(transY(50 * i)));
                grid_line_axisY.setStroke(color_axis);
                grid_line_axisY.setStrokeWidth(line_size);
                text_grid_axisY = new Text(transX(2), transY(50 * i + 3), Integer.toString(50 * i));
                text_grid_axisY.setFill(color_axis);
                drawShape(grid_line_axisY, text_grid_axisY);
            }
        }
    }

    void drawPoint(int x, int y) {
        Rectangle point = new Rectangle(transX(x), transY(y), point_size, point_size);
        canvas_pane.getChildren().add(point);
    }

    void drawPointColor(int x, int y, Color c) {
        Rectangle point = new Rectangle(transX(x), transY(y), point_size, point_size);
        point.setFill(c);
        canvas_pane.getChildren().add(point);
    }

    void drawCircle(int x, int y, boolean is_amp) {      // Резерв для случая отрисовки кластерных кругов вместо прямоугольников
        if (fieldsIsFill()) {
            Circle circle = new Circle(Integer.parseInt(range_field.getText()));

            circle.setCenterX(x);
            circle.setCenterY(y);
            if (!is_amp)
                circle.setFill(Color.rgb(0, (int)(Math.random() * 255), (int)(Math.random() * 255)));
            else
                circle.setFill(Color.rgb(255, 0,0));
//            circle.setFill(Color.rgb((num_ex*20+20) % 255, (num_ex*20+50) % 255, (num_ex*20+133) % 255));
//            circle.setFill(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
//        circle.setStrokeWidth(1);
//        circle.setStroke(Color.BLACK);
            drawShape(circle);
        } else
            range_field.setText("Enter a valid value!");
    }

    void drawRectangle(int start_x, int start_y, int end_x, int end_y) {
        if (fieldsIsFill()) {

//            System.out.format("                %d %d %d %d%n", transX(start_x), transY(start_y), transX(end_x), transY(end_y));

            Rectangle rectangle;
            rectangle = new Rectangle(transX(start_x), transY(end_y), Math.abs(end_x - start_x), Math.abs(end_y - start_y));
//            rectangle.set
//            System.out.println(start_x + ";" + start_y + ";" + end_x + ";" + end_y);
//            System.out.println(start_x + ";" + start_y + ";" + Math.abs(end_x-start_x) + ";" + Math.abs(end_y-start_y));
            rectangle.setFill(color_rect_cluster);

            // Линии окантовки кластерных прямоугольников
            Line line1 = new Line(snap_for_line(transX(start_x)), snap_for_line(transY(start_y)), snap_for_line(transX(end_x)), snap_for_line(transY(start_y)));
            Line line2 = new Line(snap_for_line(transX(end_x)), snap_for_line(transY(end_y)), snap_for_line(transX(end_x)), snap_for_line(transY(start_y)));
            Line line3 = new Line(snap_for_line(transX(end_x)), snap_for_line(transY(end_y)), snap_for_line(transX(start_x)), snap_for_line(transY(end_y)));
            Line line4 = new Line(snap_for_line(transX(start_x)), snap_for_line(transY(start_y)), snap_for_line(transX(start_x)), snap_for_line(transY(end_y)));
            line1.setStroke(color_border_rect_cluster);
            line1.setStrokeWidth(1);
            line2.setStroke(color_border_rect_cluster);
            line2.setStrokeWidth(1);
            line3.setStroke(color_border_rect_cluster);
            line3.setStrokeWidth(1);
            line4.setStroke(color_border_rect_cluster);
            line4.setStrokeWidth(1);

            drawShape(rectangle);
            drawShape(line1);
            drawShape(line2);
            drawShape(line3);
            drawShape(line4);
        } else
            range_field.setText("Enter a valid value!");
    }

    void drawClear() {
        canvas_pane.getChildren().clear();
//        canvas_pane.getChildren().add(frontcanvas);
        elapsed_time = -1.0;
        elapsed_time_label.setText("elapsed time:  ----------");
        points_after_filter_amp_label.setText("points after filter amp:  ----------");
        points_in_clusters_label.setText("points in clusters:  ----------");
        points_in_clusters = 0;
        square_clusters_label.setText("square of clusters:  ----------");
        square_clusters = 0;
    }

    private void drawClearParams() {
        canvas_pane.getChildren().remove(params_label);
        params_label = null;
    }

    void toTextOut1(String str) { out_field1.appendText(str); }

    void toTextOut2(String str) { out_field2.appendText(str); }

    void clearTextOut() {
        out_field1.clear();
        out_field2.clear();
    }

}

