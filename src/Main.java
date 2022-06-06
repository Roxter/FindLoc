import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Scene scene;
        MainView mainView;

//        final Parameters params = getParameters();        // Не получается создать граф. приложение с заданием параметров запуска
//        final List<String> paramsList = params.getRaw();

        Image ico = new Image("/resources/images/icon.png");
        stage.getIcons().add(ico);

        mainView = new MainView(stage);      // Создаем наследника VBox во избежание необходимости писать всё в одном классе
        scene = new Scene(mainView);

        // Отрисовка сцены
        stage.setScene(scene);
        stage.show();

        mainView.resizeMain();
    }

    public static void main(String[] args) {
        launch(args);
    }

}