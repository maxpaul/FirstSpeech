import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by advman on 2016-10-20.
 */
public class GinImgControl implements Initializable  {
    @FXML
    private ImageView capImg00, capImg01, capImg02, capImg03, capImg04, capImg05, capImg06, capImg07, capImg08, capImg09;
    // Array of image file names
    private static final List<String> deckImgFnames = new ArrayList<>();
    private static final String imgFnamedir = "/imgs/deck01/";

    private Image img = new Image(GinImgControl.class.getResourceAsStream("/imgs/deck01/2_of_clubs.png"));

    boolean updImgs = false;
    boolean endless = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       /* Platform.runLater(new Runnable() {
            @Override
            public void run() {
               capImg00.setImage(img);
            //    do
                while ((getUpdImgs())) {
                    capImg00.setImage(img);
                    GinController ginControl = new GinController();
                    List<String> lst = ginControl.getHandCap();
                    for (String s: lst) {
                        System.out.println("xxxx " + s);
                    }
                    setUpdImgs(false);
                    break;
                }
              //  while (endless);

            }

        });
   */ }
  /*  GinImgControl(List<String> lst) {
        for (String s: lst) {
            System.out.println("xxxx " + s);
        }

    }*/
   // public void setEndless(boolean bol) {endless= bol;}
    public void setUpdImgs(boolean bol) {updImgs= bol;}

    public boolean getUpdImgs() {
        return updImgs;
    }
}
