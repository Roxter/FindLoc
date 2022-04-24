import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Main extends Application {

    private Stage primaryStage;
    private Scene scene;
    private MainView mainView;
    private String[] args;

    @Override
    public void start(Stage stage) {

//        Main m = new Main();

//        final Parameters params = getParameters();
//        final List<String> paramsList = params.getRaw();

        this.primaryStage = stage;

        mainView = new MainView(primaryStage);
        scene = new Scene(mainView);

        // Установка параметров объектов
//        mainView.setStyle("-fx-background-color: LIGHTGRAY;");
//        mainView.setAlignment(Pos.CENTER);
        primaryStage.setTitle("Find Locations");

        // Отрисовка сцены
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
