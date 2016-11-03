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
import org.apache.log4j.varia.StringMatchFilter;


import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private ListView<String> possLstV;
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
    private Label rankCnt01, suitCnt01, combs01, chg01;
    @FXML
    private Label rankCnt02, suitCnt02, combs02, chg02;
    @FXML
    private Label rankCnt03, suitCnt03, combs03, chg03;
    @FXML
    private Label rankCnt04, suitCnt04, combs04, chg04;
    @FXML
    private Label rankCnt05, suitCnt05, combs05, chg05;
    @FXML
    private Label rankCnt06, suitCnt06, combs06, chg06;
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
    private ImageView playImg01, playImg02, playImg03, playImg04, playImg05, playImg06;
    @FXML
    private ImageView choiceImg01, choiceImg02, choiceImg03, choiceImg04, choiceImg05, choiceImg06;
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

    private List<ImageView> choiceImgV =new ArrayList<>();

    private List<Label> clubLable =new ArrayList<>();
    private List<Label> dmonLable =new ArrayList<>();
    private List<Label> heartLable =new ArrayList<>();
    private List<Label> spadeLable =new ArrayList<>();

    private List<Label> rankCnt =new ArrayList<>();
    private List<Label> suitCnt =new ArrayList<>();
    private List<Label> combsCnt =new ArrayList<>();
    private List<Label> chgCnt =new ArrayList<>();

    // URL for Sphinx files
    // www.speech.cs.cmu.edu/tools/lmtool-new.html
    private static final String synixDic = "synix.dic";
    private static final String synixLm = "synix.lm";

    List<String> handCapLst = new ArrayList<>();
    List<String> handDissOppLst = new ArrayList<>();
    List<String> handOppLst = new ArrayList<>();
    List<String> handCapSort = new ArrayList<>();
    List<String> handCapSortFlag = new ArrayList<>();

    ObservableList<String> cmdTypeLst= FXCollections.observableArrayList("DEAL", "REMOVE", "ADD", "SORT", "SORTRANK", "PASS", "DECK", "PILE", "END", "SET", "FIX","FINISH");
    ObservableList<String> cmdRankLst= FXCollections.observableArrayList("ACE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING");
    ObservableList<String> cmdSuitLst= FXCollections.observableArrayList("CLUB", "DIAMOND", "HEART", "SPADE");

    ObservableList<String> sphinxHypotItems = FXCollections.observableArrayList();
    ObservableList<String> sphinxResultItems = FXCollections.observableArrayList();
    ObservableList<String> possItems = FXCollections.observableArrayList();

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

                        if (togVoice.isSelected()) {
                            displayHyptoLstV("sphinx Hypothesis ...");
                            displayHyptoLstV(result2.getHypothesis().toString());
                            //  Display Recognized words/times);
                            for (WordResult r : result2.getWords()) {
                                displayHyptoLstV(r.toString());
                            }
                            processResult(result2.getHypothesis());
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
        loadChoiceImgV();
        loadEvalCnt();
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
                cmd.sortHandCap();

                if (cmd.getSortSts().equals("SORT")) {
                    handCapSort = cmd.getRankSort();
                    handCapSortFlag = cmd.getRankSortFlag();
                } else {
                    handCapSort = cmd.getSuitSort();
                    handCapSortFlag = cmd.getSuitSortFlag();
                }

                List<String> cardStatus = cmd.getcardStatusLst();
                List<String> dissOpp = cmd.getDissOpp();
                List<String> handOpp = cmd.getHandOpp();
                String gameStage = cmd.getGameStage();

                setHandCap(handCap);
                setHandOpp(handOpp);
                setHandDissOpp(dissOpp);

                // display any info message added to queue
                dspMsgQueue(cmd.getMsgQueue());

                updHandImg(capImageV, handCapSort, capImageVSP, true );
                updHandImg(oppImageV, handOppLst, oppImageVSP, false);
                updHandImg(oppImageVD, handDissOppLst, oppImageVDSP, false);

                updDeckPileImg();
                updTurnVar(cmd.getTurn(), cmd.getGameStage());
                //display the entire deck with assocaited status color coded
                //for example; CAP is Black with green border.
                updDeckDsp(cardStatus);
                updHandCnts();
                // evaluate options routine
                displayHyptoLstV("Turn " +cmd.getTurn());
                displayHyptoLstV("handSort0 "+handCapSort.get(0));
                displayHyptoLstV("hand0 "+handCap.get(0));
                displayHyptoLstV("handDiss0 "+handDissOppLst.get(0));
                if (handDissOppLst.size()>1)
                displayHyptoLstV("handDiss1 "+handDissOppLst.get(1));
                displayHyptoLstV("handOpp0 "+handOppLst.get(0));
                if(cmd.getTurn()) {
                    switch (cmd.getGameStage()) {
                        case "ADDREMOVE":
                            updEvalPane(handDissOppLst.get(0));
                            break;
                        case "PASSING":
                            updEvalPane(handDissOppLst.get(0));
                            break;
                        case "LOADING":
                            updEvalPane("");
                            break;
                        case "PLAYING":
                            if (!handCap.get(0).equals("")) { // Deck Command issued
                                updEvalPane(handCap.get(0));
                            } else
                                updEvalPane(handDissOppLst.get(1));
                            break;
                        default:
                            break;
                    }
                } else updEvalPane(""); // clear the eval pane

               // if(!cmd.getGameStage().equals("DEALING")) updEvalPane();
            }
        });
    }
    private void updEvalPane(String optCard) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int cardIdx = cmd.getStrIdx(optCard, cmd.deckStr);
                Image img = cardImg.getImage(cardIdx, optCard);
                playImg01.setImage(img);

                List<String> optLstKep = new ArrayList<>();
                List<String> justStar = new ArrayList<>();
                // dspPossLstV("optLst crt ");
                // create a list of card without the "*"
                if (!optCard.equals("")) {
                    for (int i =0;i<handCapSortFlag.size();i++) {
                        if (!handCapSortFlag.get(i).equals("*")) {
                            // Remove the "*" cards
                            if (!handCapSort.get(i).equals(""))
                                optLstKep.add(handCapSort.get(i));
                        } else { // Save the "*" cards
                            justStar.add(handCapSort.get(i));
                        }
                    }
                }
                // cycle through the non "*" cards replacing each with the
                // pile card or deck card call the options routine and return
                // the number of combinations and the hand deadwood counts
                List<String> optLstResult = new ArrayList<>();
                for (int i =0;i<optLstKep.size();i++) {
                    LinkedList<String> optLstIn = new LinkedList<>();
                    for (int j =0;j<optLstKep.size();j++) {
                        if(i==j) {
                            optLstIn.add(optCard);
                        } else {
                            optLstIn.add(optLstKep.get(j));
                        }
                    }
                    // add the saved "*' cards
                    for (String s: justStar) optLstIn.add(s);
                /*    displayHyptoLstV("optLstIn crt "+i);
                    for (String s: optLstIn) dspPossLstV(s);
                    displayHyptoLstV(handDissOppLst.get(0)
                            +" for "+optLstKep.get(i)+" "+cmd.evalOptions(optLstIn));*/
                    // add the results to an array
                    optLstResult.add(optLstKep.get(i)+" "+cmd.evalOptions(optLstIn));
                }
                // Result format <ACE[0] CLUB[1]> <combs[2]> <rank[3]> <suit[4]>
                //  for (String s: optLstResult) displayHyptoLstV(s);
                // Calculate the percentage change with the optional card
                List<String> optLstSorted = new ArrayList<>();
                int totChg = 0;
                for (int i=0;i<optLstResult.size();i++) {
                    String[] spl = optLstResult.get(i).split("\\s+");
                    // change in combinations
                    int finV = Integer.parseInt(spl[2]);
                    int combChg = finV-cmd.getPossibilities();
                    // change in deadwood must be based on a single value
                    // so use the lower of the two rank / suit
                    if (Integer.parseInt(spl[3])<Integer.parseInt(spl[4])) {
                        finV = Integer.parseInt(spl[3]);
                    } else finV = Integer.parseInt(spl[4]);
                    int handChg = 0;
                    if (cmd.getRankCnt()<cmd.getSuitCnt()) {
                        handChg = (finV-cmd.getRankCnt())*-1;
                    } else handChg = (finV-cmd.getSuitCnt())*-1;

                    totChg = combChg+handChg;
                    //displayHyptoLstV("totChg*****");
                    //displayHyptoLstV(totChg+ " "+optLstResult.get(i));
                    int sortTotChg =0;
                    if (totChg<=0) sortTotChg =0;
                    if (totChg>0) sortTotChg =totChg;
                    //  displayHyptoLstV(optLstResult.get(i)+" "+totChg);
                    String splChg = String.format("%02d", sortTotChg);
                    String splCombs = String.format("%02d", Integer.parseInt(spl[2]));
                    optLstSorted.add("1" + splCombs + splChg + "!" + optLstResult.get(i)+" "+totChg);

                }
                Collections.sort(optLstSorted);
                Collections.reverse(optLstSorted);
                  displayHyptoLstV("**********");
                  for (String s: optLstSorted) displayHyptoLstV(s);
                for (int i=0;i<5;i++) {
                    choiceImgV.get(i).setImage(null);
                    combsCnt.get(i).setText("");
                    rankCnt.get(i).setText("");
                    suitCnt.get(i).setText("");
                    chgCnt.get(i).setText("");
                    if (i<optLstSorted.size()) {
                        if (!optLstSorted.get(i).equals("")) {
                            // eg. "00!ACE CLUB *"
                            String[] spl = optLstSorted.get(i).split("!");
                            // eg. [00] [ACE CLUB *]
                            String[] spl2 = spl[1].split("\\s+");
                            String card = spl2[0]+" "+spl2[1];
                            cardIdx = cmd.getStrIdx(card, cmd.deckStr);
                            img = cardImg.getImage(cardIdx, card);
                            choiceImgV.get(i).setImage(img);
                            combsCnt.get(i).setText(spl2[2]);
                            rankCnt.get(i).setText(spl2[3]);
                            suitCnt.get(i).setText(spl2[4]);
                            chgCnt.get(i).setText(spl2[5]);
                        }
                    }
                }
            }
        });
    }
    private void updHandCnts() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                rankCnt01.setText(String.valueOf(cmd.getRankCnt()));
                suitCnt01.setText(String.valueOf(cmd.getSuitCnt()));
                combs01.setText(String.valueOf(cmd.getPossibilities()));
            }
        });
    }
    private void dspMsgQueue(LinkedList<String> lst) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (String s : lst) displayHyptoLstV(s);
                // testing debugging
             /*   dspPossLstV("dissOpp0 "+handDissOppLst.get(0));
                dspPossLstV("handCap0 "+handCapLst.get(0));
                dspPossLstV("handOpp0 "+handOppLst.get(0));
                if (handDissOppLst.size()>1)
                    dspPossLstV("dissOpp1 "+handDissOppLst.get(1));
                dspPossLstV("-------------");*/
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
    public void dspPossLstV(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                possItems.add(str);
                possLstV.setItems(possItems);
                possLstV.scrollTo(possLstV.getItems().size());
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
                            if (sort) if (handCapSortFlag.get(i).equals("*")) spLst.get(i).setStyle("-fx-border-color: darkblue;");
                        }
                    }

                    lstImgv.get(i).setImage(img);
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
                processResult(sb.toString());
               /* List<String> resultList = cmd.splitResult(sb.toString());
                for (String s : resultList) displayResultLstV(s);

                updBorderPane();*/

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

                StringBuilder sb = new StringBuilder();
                for (String s : ginTest) {
                    sb.append(s+" ");

                  /*  if (s.equals("PASS")) try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                      List<String> resultList = cmd.splitResult(s);
                    for (String ss : resultList) displayResultLstV(ss);
                    updBorderPane();*/
                }
                processResult(sb.toString());
               // processResult(s);
            }
        });
    }
    public void sysOutClick(ActionEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayHyptoLstV("Run updBorderPane adhoc ...");
                updBorderPane();
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
    private void loadChoiceImgV() {
        choiceImgV.add(choiceImg02);
        choiceImgV.add(choiceImg03);
        choiceImgV.add(choiceImg04);
        choiceImgV.add(choiceImg05);
        choiceImgV.add(choiceImg06);
    }
    private void loadEvalCnt() {

        rankCnt.add(rankCnt02);
        rankCnt.add(rankCnt03);
        rankCnt.add(rankCnt04);
        rankCnt.add(rankCnt05);
        rankCnt.add(rankCnt06);

        suitCnt.add(suitCnt02);
        suitCnt.add(suitCnt03);
        suitCnt.add(suitCnt04);
        suitCnt.add(suitCnt05);
        suitCnt.add(suitCnt06);

        combsCnt.add(combs02);
        combsCnt.add(combs03);
        combsCnt.add(combs04);
        combsCnt.add(combs05);
        combsCnt.add(combs06);

        chgCnt.add(chg02);
        chgCnt.add(chg03);
        chgCnt.add(chg04);
        chgCnt.add(chg05);
        chgCnt.add(chg06);
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
