import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Created by advman on 2016-10-11.
 */
public class GinMain extends Application {

    public GinController controller;
    private BorderPane mainLayout;
    private Stage primaryStage;
    final static Logger log = Logger.getLogger(GinMain.class.getName());

    private static boolean endLess = true;

    @Override
    public void start(Stage stage) throws IOException {
        startGinMng(stage);
    }

    public void startGinMng(Stage primaryStage) throws IOException {
        // Load the GUI. The MainController class will be automatically created and wired up.
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MaxPaul Gin Tool with Voice Recognition");

        URL location = getClass().getResource("Board.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        mainLayout = loader.load();

        controller = loader.getController();
        controller.setMain(this);

        Scene scene = new Scene(mainLayout);

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            endLess = false;
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
