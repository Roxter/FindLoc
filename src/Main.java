import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;
    private Scene scene;
    private MainView mainView;
    private String[] args;

    @Override
    public void start(Stage stage) {

//        final Parameters params = getParameters();        // Не получается создать граф. приложение с заданием параметров запуска
//        final List<String> paramsList = params.getRaw();

        this.primaryStage = stage;

        Image ico = new Image("/resources/images/icon.png");
        primaryStage.getIcons().add(ico);

        mainView = new MainView(primaryStage);      // Создаем наследника VBox во избежание необходимости писать всё в одном классе
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
