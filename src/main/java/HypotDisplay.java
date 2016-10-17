import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

/**
 * Created by advman on 2016-10-12.
 */
public class HypotDisplay {
    @FXML
    private ListView<String> sphinxHypot;
    ObservableList<String> items = FXCollections.observableArrayList();

    public HypotDisplay(){

    }
    public void displayHypto(String hypots){
        items.add(hypots);
        sphinxHypot.setItems(items);
    }
}
