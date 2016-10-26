import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by advman on 2016-10-11.
 */
public class GinController implements Initializable {

    @FXML
    private ListView<String> sphinxHypotLstV;
    @FXML
    private ListView<String> sphinxResultLstV;
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
    @FXML
    private Button reloadButton, testButton, cmdButton, sysOut;
    @FXML
    private CheckBox togVoice;
    @FXML
    private TextField testFname;
    @FXML
    private ChoiceBox cmdType1, cmdType2, cmdRank1, cmdRank2, cmdSuit1, cmdSuit2;
    @FXML
    private ImageView capImg00, capImg01, capImg02, capImg03, capImg04, capImg05, capImg06, capImg07, capImg08, capImg09, capImg10;
    @FXML
    private StackPane capImg00SP, capImg01SP, capImg02SP, capImg03SP, capImg04SP, capImg05SP, capImg06SP, capImg07SP, capImg08SP, capImg09SP, capImg10SP;
    @FXML
    private ImageView oppImg00, oppImg01, oppImg02, oppImg03, oppImg04, oppImg05, oppImg06, oppImg07, oppImg08, oppImg09, oppImg10;
    @FXML
    private StackPane oppImg00SP, oppImg01SP, oppImg02SP, oppImg03SP, oppImg04SP, oppImg05SP, oppImg06SP, oppImg07SP, oppImg08SP, oppImg09SP, oppImg10SP;
    @FXML
    private ImageView oppDImg00, oppDImg01, oppDImg02, oppDImg03, oppDImg04, oppDImg05, oppDImg06, oppDImg07, oppDImg08, oppDImg09, oppDImg10, oppDImg11, oppDImg12, oppDImg13, oppDImg14, oppDImg15, oppDImg16;
    @FXML
    private StackPane oppDImg00SP, oppDImg01SP, oppDImg02SP, oppDImg03SP, oppDImg04SP, oppDImg05SP, oppDImg06SP, oppDImg07SP, oppDImg08SP, oppDImg09SP, oppDImg10SP, oppDImg11SP, oppDImg12SP, oppDImg13SP, oppDImg14SP, oppDImg15SP, oppDImg16SP;

    @FXML
    private ImageView pileImg, deckImg;
    @FXML
    private StackPane pileImgSP, deckImgSP;


    private List<ImageView> capImageV =new ArrayList<>();
    private List<StackPane> capImageVSP =new ArrayList<>();
    private List<ImageView> oppImageV =new ArrayList<>();
    private List<StackPane> oppImageVSP =new ArrayList<>();
    private List<ImageView> oppImageVD =new ArrayList<>();
    private List<StackPane> oppImageVDSP =new ArrayList<>();

    private List<Label> clubLable =new ArrayList<>();
    private List<Label> dmonLable =new ArrayList<>();
    private List<Label> heartLable =new ArrayList<>();
    private List<Label> spadeLable =new ArrayList<>();

    // URL for Sphinx files
    // www.speech.cs.cmu.edu/tools/lmtool-new.html
    private static final String synixDic = "synix.dic";
    private static final String synixLm = "synix.lm";

    List<String> handCapLst = new ArrayList<>();
    List<String> handDissOppLst = new ArrayList<>();
    List<String> handOppLst = new ArrayList<>();
    List<String> handCapSort = new ArrayList<>();

    ObservableList<String> cmdTypeLst= FXCollections.observableArrayList("DEAL", "REMOVE", "ADD", "SORT", "SORTRANK", "PASS", "DECK", "PILE", "END", "SET", "FIX","FINISH");
    ObservableList<String> cmdRankLst= FXCollections.observableArrayList("ACE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING");
    ObservableList<String> cmdSuitLst= FXCollections.observableArrayList("CLUB", "DIAMOND", "HEART", "SPADE");

    ObservableList<String> sphinxHypotItems = FXCollections.observableArrayList();
    ObservableList<String> sphinxResultItems = FXCollections.observableArrayList();

    List<String> ginStepTest;
    boolean fstStep = true;
    int stepTestIdx = 0;
    boolean endless;
    VoiceCmds cmd = new VoiceCmds();
    private static GinCardImg cardImg = new GinCardImg();
    static Logger log = Logger.getLogger(GinController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
               // capImg00.setImage(img);
                setEndless(true);
                updTurnVar(cmd.getTurn(),cmd.getGameStage());

                // sphinx: start configuration
                Configuration configuration = new Configuration();
                // sphinx: Load model from the jar
                configuration
                        .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
                // sphinx: load simple language model
                configuration
                        .setDictionaryPath(synixDic);
                configuration
                        .setLanguageModelPath(synixLm);
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

                      //  List<String> resultList = new ArrayList<>();
                        if (togVoice.isSelected()) {

                           // resultList.add(result2.getHypothesis());
                            displayHyptoLstV("sphinx Hypothesis ...");
                            displayHyptoLstV(result2.getHypothesis().toString());
                            //  Display Recognized words/times);
                            for (WordResult r : result2.getWords()) {
                                displayHyptoLstV(r.toString());
                            }
                            processResult(result2.getHypothesis());
                            //resultList = cmd.splitResult(result2.getHypothesis());

                            //List<String> resultList = cmd.splitResult(s);
                            // check if the finished
                         /*   boolean fin = false;
                            for (String s : resultList) {
                                displayResultLstV(s);
                                if (s.equals("FINISH")) fin=true;
                            }
                            if (fin) reload();
                            if (!fin) updBorderPane();*/
                        }
                        break;
                    }
                } while (getEndless());

                recognizer2.stopRecognition();
                displayHyptoLstV(" Stopping ...");

                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        // load the image display lists
        loadCapImgV();
        loadCapImgVSP();
        loadOppImgV();
        loadOppImgVSP();
        loadOppImgVD();
        loadOppImgVDSP();
        // load deck display titles
        loadClubs();
        loadDmon();
        loadHeart();
        loadSpade();

        cmdType1.setItems(cmdTypeLst);
        cmdType2.setItems(cmdTypeLst);
        cmdRank1.setItems(cmdRankLst);
        cmdRank2.setItems(cmdRankLst);
        cmdSuit1.setItems(cmdSuitLst);
        cmdSuit2.setItems(cmdSuitLst);

    }
    private void processResult(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<String> resultList = new ArrayList<>();
                resultList = cmd.splitResult(str);
                // check if the finished
                boolean fin = false;
                for (String s : resultList) {
                    displayResultLstV(s);
                    if (s.equals("FINISH")) fin=true;
                }
                if (fin) reload();
                if (!fin) updBorderPane();
            }
        });
    }
    private void reload() {
        cmd = new VoiceCmds();
        fstStep = true;
        pileImg.setImage(null);
        updBorderPane();
    }
    private void updBorderPane() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<String> handCap = cmd.getHandCap();
                handCapSort = cmd.getHandCapSort();
                List<String> cardStatus = cmd.getcardStatusLst();
                List<String> dissOpp = cmd.getDissOpp();
                List<String> handOpp = cmd.getHandOpp();
                String gameStage = cmd.getGameStage();

                setHandCap(handCap);
                setHandOpp(handOpp);
                setHandDissOpp(dissOpp);

                updHandImg(oppImageV, handOppLst, oppImageVSP, false);
                updHandImg(oppImageVD, handDissOppLst, oppImageVDSP, false);

                // display any info message added to queue
                dspMsgQueue(cmd.getMsgQueue());

                updHandImg(capImageV, handCapLst, capImageVSP, true );
                updDeckPileImg();
                updTurnVar(cmd.getTurn(), cmd.getGameStage());

                updDeckDsp(cardStatus);
            }
        });
    }
    private void dspMsgQueue(LinkedList<String> lst) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (String s : lst) displayHyptoLstV(s);
                cmd.resetMsgQueue();
            }
        });
    }
    private void updTurnVar(boolean curTurn, String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                oppTurn.setStyle("-fx-background-color: #f4f4f4;");
                capTurn.setStyle("-fx-background-color: #f4f4f4;");
                capTurn.setText("");
                oppTurn.setText("");
                capTurn.setTextFill(Color.web("#f4f4f4"));
                if (curTurn)  {
                    capTurn.setStyle("-fx-background-color: green;" +
                                     "-fx-font-size: 20;");
                    capTurn.setText(str);
                    capTurn.setTextFill(Color.web("#ffffff"));
                  //  capTurn.setStyle("-fx-font-weight: bold;");
                } else {
                    oppTurn.setStyle("-fx-background-color: green;" +
                            "-fx-font-size: 20;");
                    oppTurn.setText(str);
                    oppTurn.setTextFill(Color.web("#ffffff"));
                }
              //  disGameStage.setText(str);
            }
        });
    }
    private void setHandOpp(List<String> handOppLst){
        this.handOppLst= handOppLst;
    }
    public List<String> getHandCap() {
        return handCapLst;
    }
    private void setHandCap(List<String> handCapLst){
        this.handCapLst= handCapLst;
    }
    private void setHandDissOpp(List<String> handDissOppLst){
        this.handDissOppLst= handDissOppLst;
    }
    public boolean getEndless() {return endless;}
    public void setEndless(boolean bol) {endless= bol;}
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
                    if (sts.equals("OPP")) lst.get(idx-offset).setStyle("-fx-border-color: red;" +
                                                                        "-fx-background-color: blue;" +
                                                                        "-fx-border-width: 3;");
                    if (sts.equals("END")) lst.get(idx-offset).setText("X");
                }
            }
        });
    }
    public void updDeckDsp(List<String> cardStatus) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int cntDeck =0;
                int cntOpp=0;
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
                    if (cardStatus.get(i).equals("DECK")) cntDeck++;
                    if (cardStatus.get(i).equals("OPP")) cntOpp++;
                }
                disCardCnt.setText(Integer.toString(cntDeck-10+cntOpp));
                cmd.setDeckCnt(cntDeck-10+cntOpp);
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
    public void updDeckPileImg() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Image img;
                pileImgSP.setStyle("-fx-border-color: transparent;");

                if (!handCapLst.get(0).equals("") &&
                        cmd.getCardStatus(cmd.getStrIdx(handCapLst.get(0), cmd.deckStr)).equals("CAPP"))
                        pileImgSP.setStyle("-fx-border-color: darkblue;");
                if (!handOppLst.get(0).equals("") &&
                        cmd.getCardStatus(cmd.getStrIdx(handOppLst.get(0), cmd.deckStr)).equals("OPPP"))
                        pileImgSP.setStyle("-fx-border-color: darkblue;");

                if (!handDissOppLst.get(0).equals("")) {
                    int cardIdx = cmd.getStrIdx(handDissOppLst.get(0), cmd.deckStr);
                    img = cardImg.getImage(cardIdx);
                    pileImg.setImage(img);
                } else {
                    if (cmd.getGameStage().equals("DEALING")) pileImg.setImage(null);
                }
                if (handDissOppLst.get(0).equals("") &&
                        handCapLst.get(0).equals("") &&
                        handOppLst.get(0).equals("") &&
                        handDissOppLst.size()>1) {
                    int cardIdx = cmd.getStrIdx(handDissOppLst.get(1), cmd.deckStr);
                    img = cardImg.getImage(cardIdx);
                    pileImg.setImage(img);
                }

            }
        });
    }
    public void updHandImg(List<ImageView> lstImgv, List<String> lst, List<StackPane> spLst, boolean sort) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Image img;

                for (int i=1; i<spLst.size(); i++){
                    img = cardImg.getImage(0); // the back of card
                    spLst.get(i).setStyle("-fx-border-color: transparent;");
                    if (i<=lst.size()-1) {
                        if (!lst.get(i).equals("")) {

                            int cardIdx = cmd.getStrIdx(lst.get(i), cmd.deckStr);
                            img = cardImg.getImage(cardIdx);
                            // check for 3or4 so seq and update stackpane border
                            if (sort) if (handCapSort.get(i).equals("*")) spLst.get(i).setStyle("-fx-border-color: darkblue;");
                        }
                    }

                    lstImgv.get(i).setImage(img);
                }
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
    private void displayResultLstV(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sphinxResultItems.add(str);
                sphinxResultLstV.setItems(sphinxResultItems);
                sphinxResultLstV.scrollTo(sphinxResultLstV.getItems().size());
            }
        });
    }
    public void addLstcmds(List<String> lst) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                }
        });
    }
    public void submitCmd(ActionEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                if(cmdType1.getValue()!=null) sb.append(cmdType1.getValue()).append(" ");
                if(cmdRank1.getValue()!=null) sb.append(cmdRank1.getValue()).append(" ");
                if(cmdSuit1.getValue()!=null) sb.append(cmdSuit1.getValue()).append(" ");
                if(cmdType2.getValue()!=null) sb.append(cmdType2.getValue()).append(" ");
                if(cmdRank2.getValue()!=null) sb.append(cmdRank2.getValue()).append(" ");
                if(cmdSuit2.getValue()!=null) sb.append(cmdSuit2.getValue()).append(" ");

                displayHyptoLstV("Submit command ...\n"+sb);
                List<String> resultList = cmd.splitResult(sb.toString());
                for (String s : resultList) displayResultLstV(s);

                updBorderPane();

                cmdType1.setValue(null);
                cmdRank1.setValue(null);
                cmdSuit1.setValue(null);
                cmdType2.setValue(null);
                cmdRank2.setValue(null);
                cmdSuit2.setValue(null);
            }
        });
    }
    public void togVoiceClick(ActionEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayHyptoLstV("Voice checking " + togVoice.isSelected());
            }
        });
    }
    public void issueTest(ActionEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stream<String> stream = null;
                try {
                    String fname = testFname.getText();
                    displayHyptoLstV("Test file .. "+fname);
                    if (!fname.equals("")) stream = Files.lines(Paths.get(fname));
                   // if (fname.equals("")) stream = Files.lines(Paths.get("ginTest.txt"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<String> ginTest = stream != null ? stream.collect(Collectors.toList()) : null;

                for (String s : ginTest) {
                    processResult(s);
                  //  List<String> resultList = cmd.splitResult(s);
                  //  for (String ss : resultList) displayResultLstV(ss);
                   // updBorderPane();
                }

            }
        });
    }
    public void sysOutClick(ActionEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayHyptoLstV("HandCapLst ...");
                for (String s : handCapLst) {
                    displayHyptoLstV(s);
                }
            }
        });
    }
    public void stepTest(ActionEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(fstStep) {
                    fstStep = false;

                    stepTestIdx = 0;
                    Stream<String> stream = null;
                    try {
                        String fname = testFname.getText();
                        displayHyptoLstV("Step Test file .. "+fname);
                        if (!fname.equals("")) stream = Files.lines(Paths.get(fname));
                        // if (fname.equals("")) stream = Files.lines(Paths.get("ginTest.txt"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ginStepTest = stream != null ? stream.collect(Collectors.toList()) : null;
                }
                if (stepTestIdx<ginStepTest.size()) {
                    displayResultLstV("Step cmd.. "+ginStepTest.get(stepTestIdx));
                   // List<String> resultList = cmd.splitResult(ginStepTest.get(stepTestIdx));
                   // for (String s : resultList) displayResultLstV("Result.. "+s);
                   // updBorderPane();
                    processResult(ginStepTest.get(stepTestIdx));
                    stepTestIdx++;
                } else {
                    displayResultLstV("No more cmds");
                }

            }
        });
    }
    public void reloadApp(ActionEvent event) {
        //log.trace("RELOAD");
        displayResultLstV("Reload pressed ... ");
        reload();
    }
    private void loadCapImgV() {
        capImageV.add(capImg00);
        capImageV.add(capImg01);
        capImageV.add(capImg02);
        capImageV.add(capImg03);
        capImageV.add(capImg04);
        capImageV.add(capImg05);
        capImageV.add(capImg06);
        capImageV.add(capImg07);
        capImageV.add(capImg08);
        capImageV.add(capImg09);
        capImageV.add(capImg10);

    }
    private void loadCapImgVSP() {
        capImageVSP.add(capImg00SP);
        capImageVSP.add(capImg01SP);
        capImageVSP.add(capImg02SP);
        capImageVSP.add(capImg03SP);
        capImageVSP.add(capImg04SP);
        capImageVSP.add(capImg05SP);
        capImageVSP.add(capImg06SP);
        capImageVSP.add(capImg07SP);
        capImageVSP.add(capImg08SP);
        capImageVSP.add(capImg09SP);
        capImageVSP.add(capImg10SP);

    }
    private void loadOppImgV() {
        oppImageV.add(oppImg00);
        oppImageV.add(oppImg01);
        oppImageV.add(oppImg02);
        oppImageV.add(oppImg03);
        oppImageV.add(oppImg04);
        oppImageV.add(oppImg05);
        oppImageV.add(oppImg06);
        oppImageV.add(oppImg07);
        oppImageV.add(oppImg08);
        oppImageV.add(oppImg09);
        oppImageV.add(oppImg10);

    }
    private void loadOppImgVSP() {
        oppImageVSP.add(oppImg00SP);
        oppImageVSP.add(oppImg01SP);
        oppImageVSP.add(oppImg02SP);
        oppImageVSP.add(oppImg03SP);
        oppImageVSP.add(oppImg04SP);
        oppImageVSP.add(oppImg05SP);
        oppImageVSP.add(oppImg06SP);
        oppImageVSP.add(oppImg07SP);
        oppImageVSP.add(oppImg08SP);
        oppImageVSP.add(oppImg09SP);
        oppImageVSP.add(oppImg10SP);

    }
    private void loadOppImgVD() {
        oppImageVD.add(oppDImg00);
        oppImageVD.add(oppDImg01);
        oppImageVD.add(oppDImg02);
        oppImageVD.add(oppDImg03);
        oppImageVD.add(oppDImg04);
        oppImageVD.add(oppDImg05);
        oppImageVD.add(oppDImg06);
        oppImageVD.add(oppDImg07);
        oppImageVD.add(oppDImg08);
        oppImageVD.add(oppDImg09);
        oppImageVD.add(oppDImg10);
        oppImageVD.add(oppDImg11);
        oppImageVD.add(oppDImg12);
        oppImageVD.add(oppDImg13);
        oppImageVD.add(oppDImg14);
        oppImageVD.add(oppDImg15);
        oppImageVD.add(oppDImg16);

    }
    private void loadOppImgVDSP() {
        oppImageVDSP.add(oppDImg00SP);
        oppImageVDSP.add(oppDImg01SP);
        oppImageVDSP.add(oppDImg02SP);
        oppImageVDSP.add(oppDImg03SP);
        oppImageVDSP.add(oppDImg04SP);
        oppImageVDSP.add(oppDImg05SP);
        oppImageVDSP.add(oppDImg06SP);
        oppImageVDSP.add(oppDImg07SP);
        oppImageVDSP.add(oppDImg08SP);
        oppImageVDSP.add(oppDImg09SP);
        oppImageVDSP.add(oppDImg10SP);
        oppImageVDSP.add(oppDImg11SP);
        oppImageVDSP.add(oppDImg12SP);
        oppImageVDSP.add(oppDImg13SP);
        oppImageVDSP.add(oppDImg14SP);
        oppImageVDSP.add(oppDImg15SP);
        oppImageVDSP.add(oppDImg16SP);

    }
    private void loadClubs() {
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
    private void loadDmon() {
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
    private void loadHeart() {
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
    private void loadSpade() {
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
}
