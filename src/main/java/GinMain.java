import edu.cmu.sphinx.frontend.util.StreamCepstrumSource;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.HTK.Lab;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Created by advman on 2016-10-11.
 */
public class GinMain extends Application {

    public GinController controller;
    public GinImgControl imgControl;
    private BorderPane mainLayout;
    private Stage primaryStage;
    final static Logger log = Logger.getLogger(GinMain.class.getName());


    @Override
    public void start(Stage stage) throws IOException {

        startGinMng(stage);

    }
    // not woring ....
    public void reload() throws IOException {
        URL location = getClass().getResource("Board.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        mainLayout = loader.load();
        primaryStage.getScene().setRoot(mainLayout);
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
            Platform.exit();
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
