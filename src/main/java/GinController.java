import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by advman on 2016-10-11.
 */
public class GinController implements Initializable {

    @FXML
    private ListView<String> sphinxHypotLstV;
    @FXML
    private ListView<String> sphinxResultLstV;
    @FXML
    private GridPane capHandGrid;
    @FXML
    private Label cap00, cap01, cap02, cap03, cap04, cap05, cap06, cap07, cap08, cap09, cap10;
    @FXML
    private Label opp00, opp01, opp02, opp03, opp04, opp05, opp06, opp07, opp08, opp09, opp10;
    @FXML
    private Label oppDiss00, oppDiss01, oppDiss02, oppDiss03, oppDiss04, oppDiss05, oppDiss06, oppDiss07, oppDiss08, oppDiss09, oppDiss10, oppDiss11, oppDiss12, oppDiss13, oppDiss14, oppDiss15, oppDiss16;

    @FXML
    private Label club00, club01, club02, club03, club04, club05, club06, club07, club08, club09, club10, club11, club12, club13;
    @FXML
    private Label dmon00, dmon01, dmon02, dmon03, dmon04, dmon05, dmon06, dmon07, dmon08, dmon09, dmon10, dmon11, dmon12, dmon13;
    @FXML
    private Label heart00, heart01, heart02, heart03, heart04, heart05, heart06, heart07, heart08, heart09, heart10, heart11, heart12, heart13;
    @FXML
    private Label spade00, spade01, spade02, spade03, spade04, spade05, spade06, spade07, spade08, spade09, spade10, spade11, spade12, spade13;

    @FXML
    private Label capTurn, oppTurn, disCardCnt, disGameStage;

    private List<Label> capLable =new ArrayList<>();
    private List<Label> oppLable =new ArrayList<>();
    private List<Label> dissLable = new ArrayList<>();

    private List<Label> clubLable =new ArrayList<>();
    private List<Label> dmonLable =new ArrayList<>();
    private List<Label> heartLable =new ArrayList<>();
    private List<Label> spadeLable =new ArrayList<>();

    private Service<Void> synixThread;
    List<String> handCapLst = new ArrayList<>();
    List<String> handDissOppLst = new ArrayList<>();
    List<String> handOppLst = new ArrayList<>();
    String gameStage;
    ObservableList<String> sphinxHypotItems = FXCollections.observableArrayList();
    ObservableList<String> sphinxResultItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                //LinkedList<String> handCAP = new LinkedList<>();
                boolean endLess = true;
                // set the game stage
                VoiceCmds cmd = new VoiceCmds();
                String gameStage = cmd.getGameStage();
                updTurnVar(cmd.getTurn(),gameStage);

                // sphinx: start configuration
                Configuration configuration = new Configuration();
                // sphinx: Load model from the jar
                configuration
                        .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
                // sphinx: load simple language model
                configuration
                        .setDictionaryPath("2350.dic");
                configuration
                        .setLanguageModelPath("2350.lm");
                LiveSpeechRecognizer recognizer2 = null;
                try {
                    recognizer2 = new LiveSpeechRecognizer(configuration);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Start recognition process pruning previously cached data.
                recognizer2.startRecognition(true);
                SpeechResult result2;
                displayHyptoLstV("Ready ...");
                do {

                    while ((result2 = recognizer2.getResult()) != null) {

                        List<String> resultList = new ArrayList<>();
                        //resultList = cmd.splitResult(result2.getHypothesis());
                        resultList.add(result2.getHypothesis());

                        displayHyptoLstV(result2.getHypothesis().toString());

                        //  Display Recognized words/times);
                        for (WordResult r : result2.getWords()) {
                            displayHyptoLstV(r.toString());
                        }

                        resultList = cmd.splitResult(result2.getHypothesis());

                        List<String> handCap = cmd.getHandCap();
                        List<String> cardStatus = cmd.getcardStatusLst();
                        List<String> dissOpp = cmd.getDissOpp();
                        gameStage = cmd.getGameStage();

                        setHandCap(handCap);
                        setHandDissOpp(dissOpp);
                        for (String s : resultList) {
                            displayResultLstV(s);
                            if (gameStage.equals("DEALING") || gameStage.equals("PASSING")) {
                                displayHands(s, capLable, handCapLst);
                            }
                        }
                        if (gameStage.equals("PLAYING")) {
                            updHandDsp(capLable, handCapLst);
                            updHandDsp(dissLable, handDissOppLst);
                        }

                        updTurnVar(cmd.getTurn(), cmd.getGameStage());
                        updDeckDsp(cardStatus);

                        break;
                    }

                } while (endLess);
                recognizer2.stopRecognition();
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        loadDissOpp();
        loadCap();
        loadClubs();
        loadDmon();
        loadHeart();
        loadSpade();

    }
    private void updTurnVar(boolean bol, String gameStage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                oppTurn.setStyle("-fx-background-color: #f4f4f4;");
                capTurn.setStyle("-fx-background-color: #f4f4f4;");
                if (bol)  {
                    capTurn.setStyle("-fx-background-color: green;");
                } else {
                     oppTurn.setStyle("-fx-background-color: green;");
                }
                disGameStage.setText(gameStage);
            }
        });
    }
    private void setHandOpp(List<String> handOppLst){
        this.handOppLst= handOppLst;
    }
    private void setHandCap(List<String> handCapLst){
        this.handCapLst= handCapLst;
    }
    private void setHandDissOpp(List<String> handDissOppLst){
        this.handDissOppLst= handDissOppLst;
    }
    public void loadOpp() {
        oppLable.add(opp00);
        oppLable.add(opp01);
        oppLable.add(opp02);
        oppLable.add(opp03);
        oppLable.add(opp04);
        oppLable.add(opp05);
        oppLable.add(opp06);
        oppLable.add(opp07);
        oppLable.add(opp08);
        oppLable.add(opp09);
        oppLable.add(opp10);
    }
    public void loadDissOpp() {
        dissLable.add(oppDiss00);
        dissLable.add(oppDiss01);
        dissLable.add(oppDiss02);
        dissLable.add(oppDiss03);
        dissLable.add(oppDiss04);
        dissLable.add(oppDiss05);
        dissLable.add(oppDiss06);
        dissLable.add(oppDiss07);
        dissLable.add(oppDiss08);
        dissLable.add(oppDiss09);
        dissLable.add(oppDiss10);
        dissLable.add(oppDiss11);
        dissLable.add(oppDiss12);
        dissLable.add(oppDiss13);
        dissLable.add(oppDiss14);
        dissLable.add(oppDiss15);
        dissLable.add(oppDiss16);
    }
    public void loadCap() {
        capLable.add(cap00);
        capLable.add(cap01);
        capLable.add(cap02);
        capLable.add(cap03);
        capLable.add(cap04);
        capLable.add(cap05);
        capLable.add(cap06);
        capLable.add(cap07);
        capLable.add(cap08);
        capLable.add(cap09);
        capLable.add(cap10);
    }
    public void loadClubs() {
        clubLable.add(club00);
        clubLable.add(club01);
        clubLable.add(club02);
        clubLable.add(club03);
        clubLable.add(club04);
        clubLable.add(club05);
        clubLable.add(club06);
        clubLable.add(club07);
        clubLable.add(club08);
        clubLable.add(club09);
        clubLable.add(club10);
        clubLable.add(club11);
        clubLable.add(club12);
        clubLable.add(club13);
    }
    public void loadDmon() {
        dmonLable.add(dmon00);
        dmonLable.add(dmon01);
        dmonLable.add(dmon02);
        dmonLable.add(dmon03);
        dmonLable.add(dmon04);
        dmonLable.add(dmon05);
        dmonLable.add(dmon06);
        dmonLable.add(dmon07);
        dmonLable.add(dmon08);
        dmonLable.add(dmon09);
        dmonLable.add(dmon10);
        dmonLable.add(dmon11);
        dmonLable.add(dmon12);
        dmonLable.add(dmon13);
    }
    public void loadHeart() {
        heartLable.add(heart00);
        heartLable.add(heart01);
        heartLable.add(heart02);
        heartLable.add(heart03);
        heartLable.add(heart04);
        heartLable.add(heart05);
        heartLable.add(heart06);
        heartLable.add(heart07);
        heartLable.add(heart08);
        heartLable.add(heart09);
        heartLable.add(heart10);
        heartLable.add(heart11);
        heartLable.add(heart12);
        heartLable.add(heart13);
    }
    public void loadSpade() {
        spadeLable.add(spade00);
        spadeLable.add(spade01);
        spadeLable.add(spade02);
        spadeLable.add(spade03);
        spadeLable.add(spade04);
        spadeLable.add(spade05);
        spadeLable.add(spade06);
        spadeLable.add(spade07);
        spadeLable.add(spade08);
        spadeLable.add(spade09);
        spadeLable.add(spade10);
        spadeLable.add(spade11);
        spadeLable.add(spade12);
        spadeLable.add(spade13);
    }
    public void setMain(GinMain ginCheck) {

    }

    public void setDeckLabel(int idx, int offset, List<Label> lst, String sts) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lst.get(idx-offset).setText("?");
                lst.get(idx-offset).setStyle("-fx-background-color: f4f4f4;");

                if (!sts.equals("DECK")) { // sts DECK, CAP(D), OPP(D),

                    lst.get(idx-offset).setText("");
                    if (sts.equals("CAP")) lst.get(idx-offset).setStyle("-fx-border-color: green;" +
                                                                        "-fx-background-color: black;" +
                                                                        "-fx-border-width: 2;");
                    if (sts.equals("CAPD")) lst.get(idx-offset).setStyle("-fx-border-color: green;" +
                                                                         "-fx-background-color: f4f4f4;" +
                                                                         "-fx-border-width: 2;");
                    if (sts.equals("OPPD")) lst.get(idx-offset).setStyle("-fx-border-color: red;" +
                                                                         "-fx-background-color: f4f4f4;" +
                                                                         "-fx-border-width: 3;");
                }
            }
        });
    }

    public void updDeckDsp(List<String> cardStatus) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int cnt =0;
                for (int i=1; i< cardStatus.size(); i++){
                    // update clubs
                    if (i<14) {
                        setDeckLabel(i, 0, clubLable, cardStatus.get(i));
                    } else {
                        if (i<27) {
                            setDeckLabel(i, 13, dmonLable, cardStatus.get(i));
                        } else {
                            if (i<40) {
                                setDeckLabel(i, 26, heartLable, cardStatus.get(i));
                            } else {
                                if (i<53) {
                                    setDeckLabel(i, 39, spadeLable, cardStatus.get(i));
                                }
                            }
                        }
                    }
                    if (cardStatus.get(i).equals("DECK")) cnt++;
                }
                disCardCnt.setText(Integer.toString(cnt-10));
            }
        });
    }
    public void displayHyptoLstV(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sphinxHypotItems.add(str);
                sphinxHypotLstV.setItems(sphinxHypotItems);
                sphinxHypotLstV.scrollTo(sphinxHypotLstV.getItems().size());
            }
        });
    }

    public void updHandDsp(List<Label> lstLabel, List<String> lst) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                for (int i=0; i<lstLabel.size(); i++){
                    lstLabel.get(i).setText("");
                }
                for (int i=0; i<lst.size(); i++){
                    lstLabel.get(i).setText(lst.get(i));
                }
            }
        });
    }
    public void displayHands(String str, List<Label> lstLabel, List<String> lst) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // display the cap cards
                String[] spl = str.split("\\s+");
                if (spl.length==3) { // it's a card
                    lstLabel.get(Integer.parseInt(spl[2])).setText(spl[0] + " " + spl[1]);
                } else { // it's a command
                    if (spl.length==1) { // is a deal command
                        if (spl[0].equals("DEAL") || spl[0].equals("SORTRANK") ||spl[0].equals("SORT")) {
                            for (int i=0; i<11; i++){
                                lstLabel.get(i).setText("");
                            }
                            if (spl[0].equals("SORT") || spl[0].equals("SORTRANK")) {
                                for (int i=0; i<lst.size(); i++){
                                    lstLabel.get(i).setText(lst.get(i));

                                }
                            }
                        }

                    } else { // it's another command
                        //  if (spl[0].equals("REMOVE")) {
                        for (int i=0; i<11; i++){
                            lstLabel.get(i).setText("");
                        }
                        for (int i=0; i<lst.size(); i++){
                            lstLabel.get(i).setText(lst.get(i));
                        }

                        //  }
                    }

                }
            }
        });
    }
    public void displayResultLstV(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sphinxResultItems.add(str);
                sphinxResultLstV.setItems(sphinxResultItems);
                sphinxResultLstV.scrollTo(sphinxResultLstV.getItems().size());
            }
        });
    }

    public void clickedbutton(ActionEvent event) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (String s : handDissOppLst) {
                    displayHyptoLstV(s);
                }
            }
        });
    }
}