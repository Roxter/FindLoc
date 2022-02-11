import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;

public class Main extends Application {

    private Stage primaryStage;
    private Scene scene;
    private MainView mainView;

    @Override
    public void start(Stage stage) {

//        Main m = new Main();

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
        launch();
    }

}
