import org.apache.log4j.Logger;
import java.util.*;


/**
 * Created by advman on 2016-10-11.
 */
public class VoiceCmds {
    private List<String> curCmds;
    private String gameStage;
    private boolean fstPass;
    // true = CAP(computer assisted player), false= OPP(opposing player)
    private boolean whosTurn = true;
    // set flag in insure CAP does a [PILE PASS] combination
    // set to true to allow processing during PASSING game stage
    private boolean pileDone = true;
    public String prevVoice;
    public String cardValue;
    public int capIdx;
    public int oppIdx;
    public int dissIdx;
    public int deckCnt;
    public String lastCmd;
    public String sortSts;

    LinkedList<String> handCap;
    LinkedList<String> handCapSort;
    LinkedList<String> handOpp;
    LinkedList<String> dissOpp;
    LinkedList<String> capLstCmds;
    LinkedList<String> oppLstCmds;

    LinkedList<String> msgQueue =new LinkedList<>();
    List<String> deckStr;
    LinkedList<String> cardStatus;

    private static final String[] CMDS = {"DEAL", "REMOVE", "ADD", "SORT", "SORTRANK", "PASS", "DECK", "PILE", "END", "SET", "FIX","FINISH"};
    private static final String[] SUIT = {"CLUB", "DIAMOND", "HEART", "SPADE"};
    private static final String[] RANK = {"ACE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING"};
    // Not used yet so keep
    private static final String[] STAGES = {"LOADING", "DEALING", "ADDREMOVE", "PASSING", "PLAYING", "ENDREQUEST","ENDING", "FINISHING","FINISHED"};
    static Logger log = Logger.getLogger(VoiceCmds.class.getName());

    public VoiceCmds() {
        handCap = new LinkedList<>();
        // declare and initalize the handcap sort flag
        handCapSort = new LinkedList<>();
        for (int i=0;i<11;i++) handCapSort.add(i,"");
        handOpp = new LinkedList<>();
        dissOpp = new LinkedList<>();
        capLstCmds = new LinkedList<>();
        oppLstCmds = new LinkedList<>();

        setHands(0, "", handOpp);
        setHands(0, "", dissOpp);
        setHands(0, "", handCap);
        setGameStage("LOADING");
        setSortSts("SORTRANK");
        setLastCmd("");
        setFirstPass(true);
        setPrevVoice("");
        setCardValue("");
        setCapIdx(0);
        setOppIdx(0);
        setDissIdx(0);
        createDeck();
        // used to store messages to retrieved by controller.
        resetMsgQueue();
    }
    // sort handCap array by rank or suit and rank
    public void sortHandCap() {

        List<String> sortedList =new ArrayList<>();

        if(getSortSts().equals("SORTRANK")) { // sort by rank
            for (String s : handCap) {
                if (!s.equals("")) {
                    String[] spl = s.split("\\s+");
                    int rankIdx = getStrIdxArry(spl[0], RANK);
                    String splFormt = String.format("%02d", rankIdx);
                    sortedList.add(splFormt + "!" + s);
                }
            }
            Collections.sort(sortedList);
            sortedList = sort3and4(sortedList);
        } else { // sort by suit and Rank
            for (String s : handCap) {
                if (!s.equals("")) {
                    String[] spl = s.split("\\s+");
                    int suitIdx = getStrIdxArry(spl[1], SUIT);
                    int rankIdx = getStrIdxArry(spl[0], RANK);
                    String splFormt = String.format("%02d", rankIdx);
                    sortedList.add(suitIdx + splFormt + "!" + s);
                }
            }
            Collections.sort(sortedList);
            sortedList = sortSeq(sortedList);
        }
        Collections.sort(sortedList);
        resetHands(handCap);
        resetHands(handCapSort);
        for (String s : sortedList) {
            // eg. "00!ACE CLUB *"
            String[] spl = s.split("!");

            // eg. [00] [ACE CLUB *]
            String[] spl2 = spl[1].split("\\s+");

            //eg. [ACE] [CLUB] [*]
            handCap.add(spl2[0]+" "+spl2[1]); // "ACE CLUB"

            // add the sort indicator
            if (spl2.length==3) {
                handCapSort.add(spl2[2]); // "*"
            } else {
                handCapSort.add("");
            }
        }
    }
    // Flag sort rank 3&4 of kind
    public List<String> sort3and4(List<String> lst) {

        Integer[] rankCnt = new Integer[14];
        for (int i=0; i<rankCnt.length; i++) rankCnt[i] = 0;

        for (String s : lst) {
            String[] spl = s.split("!");
            int rankInt = Integer.parseInt(spl[0]);
            rankCnt[rankInt]++;
        }
        List<String> sorted3and4 = new ArrayList<>();
        List<String> rankCnt3or4 = new ArrayList<>();
        for (int i=0; i<rankCnt.length; i++) {
            if (rankCnt[i] > 2) rankCnt3or4.add(String.format("%02d", i));
        }
        for (String s : lst) {
            String[] spl = s.split("!");

            if (getStrIdx(spl[0], rankCnt3or4) != -1) {
                sorted3and4.add("10" + s + " *");
            } else {
                sorted3and4.add("11" + s);
            }
        }
        return sorted3and4;
    }
    // Flag sequential incremental suits of 3 or more.
    public List<String> sortSeq(List<String> lst) {
        // Create a temp array that checks if the sequence sorted array
        // has sequenctial incremental values eg. 4-5-6.
        // An array of x,x,x,4,5,6,x,x,x,x
        // will have   1,1,1,1,2,3,1,1,1,1
        Integer[] seqCntArry = new Integer[10];
        for (int i=0; i<seqCntArry.length; i++) seqCntArry[i] = 0;
        int prevSuit =-1;
        int prevRank =-1;
        int seqCnt=0;

        for (int i=0;i<lst.size();i++) {
            String[] spl = lst.get(i).split("!");
            int suitInt = Integer.parseInt(spl[0].substring(0, 1));
            int rankInt = Integer.parseInt(spl[0].substring(1,3));
            if (suitInt==prevSuit && rankInt-1 == prevRank) {
                seqCnt++;
            } else {
                seqCnt =1;
            }
            prevSuit=suitInt;
            prevRank=rankInt;
            seqCntArry[i] = seqCnt;

            msgQueue.add(lst.get(i)+" suit int "+ suitInt+" rank int "+rankInt);
        }
        for (int i=0;i<seqCntArry.length;i++) msgQueue.add(seqCntArry[i].toString());

        // Setup temp array to store the idx values of the cards to be flagged
        // as sequenctial incremental sets of 3 or more.
        Integer[] seqUpdArry = new Integer[10];
        for (int i=0; i<seqUpdArry.length; i++) seqUpdArry[i] = 0;
        // starting at relative postion 3 because that is the first possible
        // occurence of a count value of 3
        for (int i=2;i<seqCntArry.length;i++) {
            if (seqCntArry[i]>2 && i+2>seqCntArry.length) {
                seqUpdArry[i]=seqCntArry[i];
                i++;
            } else {
                if (seqCntArry[i]>2 && seqCntArry[i+1]<seqCntArry[i]) seqUpdArry[i]=seqCntArry[i];
            }
        }

        for (int i=0;i<seqUpdArry.length;i++) msgQueue.add(seqUpdArry[i].toString());
        // Iterate back from the index value for the count value 3 or more
        for (int i=0;i<seqUpdArry.length;i++) {
            if (seqUpdArry[i] !=0) {
                for (int j=i;j>i-seqUpdArry[i];j--) {
                    lst.set(j,lst.get(j)+" *");
                }
            }
        }
        // if the elem has a * add 10 to the existing sort value or 11
        // example 000!ACE Club becomes 10000!ACE CLUB
        for (int i=0;i<lst.size();i++) {
            String[] spl = lst.get(i).split("!");
            String[] splAgn =spl[1].split("\\s+");
            if (splAgn.length==3) {
                lst.set(i,"10"+ lst.get(i));
            } else {
                lst.set(i,"11"+ lst.get(i));
            }
        }
        for (String s : lst) msgQueue.add(s);
        return lst;
    }
    public void resetMsgQueue() {
        while (!msgQueue.isEmpty()) {
            msgQueue.removeFirst();
        }
    }
    public LinkedList<String> getMsgQueue() {
        return msgQueue;
    }
    // set the last command
    public void setLastCmd(String str) {
        this.lastCmd = str;
    }
    // set the last command
    public String getLastCmd() {
        return lastCmd;
    }
    // reset the card status on deal
    public void resetCardStatus() {
        for (int i = 1; i < cardStatus.size(); i++) {
            setCardStatus(i, "DECK");
        }
    }
    // status of sort equal SORT or SORTRANK or ""
    public void setSortSts(String sortSts) {this.sortSts = sortSts;;}
    public String getSortSts() {return sortSts;}
    // set card status
    public void setCardStatus(int idx, String sts) {
        cardStatus.set(idx, sts);
    }
    // return the status value of the card in relation to the deck
    public String getCardStatus(int idx) {
        return cardStatus.get(idx);
    }
    public void createDeck() {
        deckStr = new ArrayList<>();
        cardStatus = new LinkedList<>();
        deckStr.add("blank");
        cardStatus.add("blank");
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 13; i++) {
                deckStr.add(RANK[i] + " " + SUIT[j]);
                cardStatus.add("DECK");
            }
        }
    }
    // return the index of the list based on card string.
    public int getStrIdxArry(String str, String[] lst) {
        for (int i = 0; i < lst.length; i++) {
            if (lst[i].equals(str)) return i;
        }
        return -1;
    }
    // return the index of the list based on card string.
    public int getStrIdx(String str, List<String> lst) {
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).indexOf(str) != -1) {
                return i;
            }
        }
        return -1;
    }
    public void resetHands(LinkedList<String> lst ) {
        while (!lst.isEmpty()) {
            lst.removeFirst();
        }
        setHands(0, "", lst);
    }
    public void setHands(int idx, String str, LinkedList<String> lst) {
        lst.add(idx, str);
    }
    public int getOppIdx() {return oppIdx;}
    public int getCapIdx() {return capIdx;}
    public int getDissIdx() {
        return dissIdx;
    }
    public void setOppIdx(int oppIdx) {
        this.oppIdx = oppIdx;
    }
    public void setCapIdx(int capIdx) {
        this.capIdx = capIdx;
    }
    public void setDissIdx(int dissIdx) {
        this.dissIdx = dissIdx;
    }
    public void setTurn() {
        if (getGameStage().equals("PLAYING")) {
            if(getTurn()) { // changing to OPP
                while (!oppLstCmds.isEmpty()) {
                    oppLstCmds.removeFirst();
                }
            } else { // changing to CAP
                while (!capLstCmds.isEmpty()) {
                    capLstCmds.removeFirst();
                }
            }
        }
        this.whosTurn = !this.whosTurn;
    }
    // the number of cards of Deck cards
    public int getDeckCnt() {return deckCnt;}
    public void setDeckCnt(int deckCnt) {
        this.deckCnt = deckCnt;;
    }
    public boolean getTurn() { return whosTurn; }
    public void setFirstPass(boolean fstPass) {
        this.fstPass = fstPass;
    }
    public boolean getFirstPass() {
        return fstPass;
    }
    public void setPrevVoice(String prevVoice) {
        this.prevVoice = prevVoice;
    }
    public String getPrevVoice() {
        return prevVoice;
    }
    public void setCardValue(String cardValue) {
        this.cardValue = cardValue;
    }
    public String getCardValue() {
        return cardValue;
    }
    public static boolean useList(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }
    public void setGameStage(String gameStage) {
        this.gameStage = gameStage;
    }
    public String getGameStage() {
        return gameStage;
    }
    public List<String> getHandCap() {
        return handCap;
    }
    public List<String> getHandCapSort() {
        return handCapSort;
    }
    public List<String> getDissOpp() {
        return dissOpp;
    }
    public List<String> getcardStatusLst() {
        return cardStatus;
    }
    public List<String> getHandOpp() {
        return handOpp;
    }
    public List<String> getCapLstCmds() {
        return capLstCmds;
    }
    public List<String> getOppLstCmds() {
        return oppLstCmds;
    }
    public void fixCardStr(LinkedList<String> lst) {
        log.trace("FIX");
        dissOpp.set(0,"");
        String card;
        if (lst.get(0).equals("PASS")) {

            // Add condition to check for first passing stage
            // when no pass card is required
            if (lst.size()>1) {
                String[] spl = lst.get(1).split("\\s+");
                card = spl[0] + " " + spl[1];
                setCardStatus(getStrIdx(card, deckStr), spl[2]);
                if (spl[2].equals("OPP")) {
                    handOpp.add(card);
                }
                int splSize = spl.length;
                if (splSize==5) { // update the dissOpp00
                    card = spl[3] + " " + spl[4];
                    dissOpp.set(0, card);
                }
            } else {
                card = dissOpp.get(1);
                setCardStatus(getStrIdx(card, deckStr), "DECK");
                dissOpp.remove(1);
                setDissIdx(getDissIdx() - 1);
            }
            // Allow for fix during the first Passing stage
            if (getDeckCnt()==31) {
                setGameStage("ADDREMOVE");
                dissOpp.set(0,card);
            }
            setTurn();
        } else { // it's a DECK/PILE with a Pass card
            if (lst.get(0).equals("DECK")) { // no deck for OPP
                String[] spl = lst.get(1).split("\\s+");
                String card1 = spl[0] + " " + spl[1];
                spl = lst.get(3).split("\\s+");
                String card2 = spl[0] + " " + spl[1];
                if (card1.equals(card2)) { // what was passed equals DECK
                    setCardStatus(getStrIdx(card1, deckStr), "DECK");
                } else {
                    handCap.remove(10);
                    handCap.add(10, card2);
                    setCardStatus(getStrIdx(card1, deckStr), "DECK");
                    setCardStatus(getStrIdx(card2, deckStr), "CAP");
                }
            } else { // it's a pile for CAP
                if (lst.get(0).equals("PILE")) {
                    String[] spl = lst.get(1).split("\\s+");
                    String card1 = spl[0] + " " + spl[1];
                    String sts1= spl[2];
                    spl = lst.get(3).split("\\s+");
                    String card2 = spl[0] + " " + spl[1];
                    String sts2= spl[2];
                    switch (sts1) {
                        case "OPPD": // update handcap
                            handCap.remove(10);
                            handCap.add(10, card2);
                            setCardStatus(getStrIdx(card1, deckStr), "OPPD");
                            setCardStatus(getStrIdx(card2, deckStr), "CAP");
                            break;
                        case "CAPD": // sts is CAPD
                            if (sts2.equals("OPP")) { // card came from handOpp
                                handOpp.removeFirst();
                                handOpp.add(card2);
                                setCardStatus(getStrIdx(card1, deckStr), "CAPD");
                                setCardStatus(getStrIdx(card2, deckStr), "OPP");
                                dissOpp.set(0, card1);
                                handOpp.set(0,"");
                            } else { // pass card was a DECK card
                                handOpp.removeFirst();
                                setCardStatus(getStrIdx(card1, deckStr), "CAPD");
                                setCardStatus(getStrIdx(card2, deckStr), "DECK");
                                dissOpp.set(0, card1);
                                handOpp.set(0,"");
                            }
                            break;
                        // if sts1 is deck it's the first pile card
                        // so it can be updated to DECK which was the previous status.
                        // The exitsting status will reflect what player took the card
                        // so CAP or OPP and can be removed from the hand.  The pass card
                        // gets added back.  Add the pile card to dissOpp.set(0)
                        case "DECK": // sts is CAPD
                            setCardStatus(getStrIdx(card1, deckStr), "DECK");
                            dissOpp.set(0, card1);

                            if (sts2.equals("CAP")) { // CAP took the pile card
                                handCap.remove(10);
                                handCap.add(10, card2);
                                setCardStatus(getStrIdx(card2, deckStr), "CAP");
                            } else { // OPP took the pile card
                                if (sts2.equals("OPP")) { // card came from handOpp
                                    handOpp.removeFirst();
                                    handOpp.add(card2);
                                    setCardStatus(getStrIdx(card2, deckStr), "OPP");

                                } else { // pass card was a DECK card
                                    int idx = getStrIdx(card1, handOpp);
                                    if (idx != -1) {
                                        handOpp.remove(idx);
                                        setOppIdx(getOppIdx() - 1);
                                    }
                                    setCardStatus(getStrIdx(card2, deckStr), "DECK");
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            setTurn();
        }
    }
    public void processCmds(String cmd, String card) {
        //List<String> sorting;
        setLastCmd("");

        switch (cmd) {
            case "DEAL":
                if (getGameStage().equals("LOADING") ||
                        getGameStage().equals("DEALING") ||
                     //   getGameStage().equals("FINISHED") ||
                        getGameStage().equals("PASSING") ||
                        getGameStage().equals("ADDREMOVE")) {
                    setLastCmd(cmd);
                    setGameStage("DEALING");
                    setCapIdx(0);
                    resetHands(handCap);
                    resetHands(dissOpp);
                    resetCardStatus();
                    whosTurn = true;
                }
                break;
            case "REMOVE":
                if (getGameStage().equals("ADDREMOVE") ||
                        getGameStage().equals("FINISHING" )) {

                    if (card.equals("")) {
                        setLastCmd(cmd);
                    } else {
                        switch (getGameStage()) {
                            case "ADDREMOVE":
                                if (getStrIdx(card, deckStr) != -1 && getStrIdx(card, handCap) != -1) {
                                    setCardStatus(getStrIdx(card, deckStr), "DECK");
                                    int idx = getStrIdx(card, handCap);
                                    handCap.remove(idx);
                                    setCapIdx(getCapIdx() - 1);
                                }
                                break;
                            case "FINISHING":
                                if (getStrIdx(card, deckStr) != -1 && getStrIdx(card, handOpp) != -1) {
                                    setCardStatus(getStrIdx(card, deckStr), "DECK");
                                    int idx = getStrIdx(card, handOpp);
                                    handOpp.remove(idx);
                                    setOppIdx(getOppIdx() - 1);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            case "ADD":
                if (getGameStage().equals("ADDREMOVE") ||
                        getGameStage().equals("FINISHING")) {
                    if (card.equals("")) {
                        setLastCmd(cmd);
                    } else {
                        switch (getGameStage()) {
                            case "ADDREMOVE":
                                if (getCapIdx() < 10) { // the cap hand is full can't add.
                                    // check if the card status is equal to DECK
                                    if (getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) { // card is available to be added to cap hand
                                        // set the card status in the deck to in the computer aided players(CAP) hand
                                        setCardStatus(getStrIdx(card, deckStr), "CAP");
                                        setCapIdx(getCapIdx() + 1);
                                        setHands(getCapIdx(), card, handCap);
                                    }
                                }
                                break;
                            case "FINISHING":
                                if (getOppIdx() < 10) { // the Opp hand is full can't add.
                                    // check if the card status is equal to END
                                    if (getCardStatus(getStrIdx(card,deckStr)).equals("END")) break;
                                    setCardStatus(getStrIdx(card, deckStr), "END");
                                    setOppIdx(getOppIdx() + 1);
                                    setHands(getOppIdx(), card, handOpp);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            case "SORTRANK":
                if (!handCap.get(0).equals("") || !handOpp.get(0).equals("")) break;
                if (getGameStage().equals("PASSING") ||
                        getGameStage().equals("ADDREMOVE") ||
                        getGameStage().equals("PLAYING")) {
                    setSortSts(cmd);
                    sortHandCap();
                }
                break;
            case "SORT":
                if (!handCap.get(0).equals("") || !handOpp.get(0).equals("")) break;
                if (getGameStage().equals("PASSING") ||
                        getGameStage().equals("ADDREMOVE") ||
                        getGameStage().equals("PLAYING")) {
                    setSortSts(cmd);
                    sortHandCap();
                }
                break;
            case "SET":
                if (getGameStage().equals("PASSING") ||
                        getGameStage().equals("ADDREMOVE") ||
                        getGameStage().equals("DEALING")) {
                    // setTurn() used from the controller to toggle turns
                    // set command used to change default from CAP to OPP.
                    whosTurn = !whosTurn;
                    log.trace(cmd);
                }
                break;
            case "DECK":
                if (!getGameStage().equals("PLAYING")) break;
                if (!whosTurn) break; // only a valid opp command.

                if (card.equals("")) {
                    setLastCmd(cmd);
                    saveLstCmd(cmd);
                } else {
                    if (getStrIdx(card, deckStr) == -1) break;
                    if (getGameStage().equals("PLAYING") && !getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) break;
                    saveLstCmd(card + " " + getCardStatus(getStrIdx(card, deckStr))); // used for the FIX command.
                    setCardStatus(getStrIdx(card, deckStr), "CAP");
                    handCap.set(0,card);
                }
                break;
            case "PILE":
                if (getGameStage().equals("ADDREMOVE") &&
                        getCapIdx() == 10) setGameStage("PASSING");

                if (getGameStage().equals("PLAYING") ||
                        getGameStage().equals("PASSING")) {
                    if (card.equals("")) setLastCmd(cmd);
                    // This line of code will handle the first pass sequence after the deal.
                    if (getCardStatus(getStrIdx(dissOpp.get(0), deckStr)).equals("DECK")) card = dissOpp.get(0);
                    if (whosTurn) { // CAP if true
                        if (card.equals("") && !dissOpp.get(1).equals("")) card=dissOpp.get(1);
                        if (getStrIdx(card, deckStr) == -1) break;
                        if (getGameStage().equals("PLAYING") && !getCardStatus(getStrIdx(card, deckStr)).equals("OPPD")) break;
                        setGameStage("PLAYING");
                        saveLstCmd(cmd);
                        saveLstCmd(card + " " + getCardStatus(getStrIdx(card, deckStr)));
                        setCardStatus(getStrIdx(card, deckStr), "CAPP");
                        handCap.set(0,card);

                    } else { // OPP
                        if (card.equals("") && !dissOpp.get(0).equals("")) card=dissOpp.get(0);
                        if (getStrIdx(card, deckStr) == -1) break;
                        if (getGameStage().equals("PLAYING") && !getCardStatus(getStrIdx(card, deckStr)).equals("CAPD")) break;
                        setGameStage("PLAYING");
                        saveLstCmd(cmd);
                        saveLstCmd(card + " " + getCardStatus(getStrIdx(card, deckStr)));
                        setCardStatus(getStrIdx(card, deckStr), "OPPP");
                        handOpp.set(0,card);
                        dissOpp.set(0, "");
                    }
                }
                break;
            case "PASS":
                if (getGameStage().equals("ADDREMOVE") &&
                        getCapIdx() == 10) setGameStage("PASSING");
                // During PASSING stage we have the card so set it.
                if (getGameStage().equals("PASSING")) {
                    saveLstCmd(cmd);
                    log.trace(cmd);
                    card = dissOpp.get(0);
                }
                if (getGameStage().equals("PASSING") ||
                        getGameStage().equals("PLAYING")) {

                    if (card.equals("")) {
                        setLastCmd(cmd);
                        saveLstCmd(cmd);
                    } else {
                        msgQueue.add("card " +card);
                        msgQueue.add("dissOpp "+ dissOpp.get(0) +" deck sts "+getCardStatus(getStrIdx(dissOpp.get(0), deckStr)));
                        // This line of code will handle the first pass sequence after the deal.
                      //  if (getCardStatus(getStrIdx(dissOpp.get(0), deckStr)).equals("DECK")) card = dissOpp.get(0);
                     //   if (!getGameStage().equals("PLAYING")) setGameStage("PLAYING");
                        if (whosTurn) { // CAP if true
                            if (handCap.get(0).equals("")) {
                                if (getStrIdx(card, deckStr) != -1 && getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) {
                                    saveLstCmd(card + " " + getCardStatus(getStrIdx(card, deckStr)));
                                    setCardStatus(getStrIdx(card, deckStr), "CAPD");
                                    dissOpp.set(0, card);
                                    setTurn();
                                } else { // handle the pass stage of game
                                    if (getGameStage().equals("PASSING")) {
                                        String prevSts = getCardStatus(getStrIdx(card, deckStr));
                                        // set the status to CAPD so it can not be passed again
                                        setCardStatus(getStrIdx(card, deckStr), "OPPD");
                                        setGameStage("PLAYING");
                                        setTurn();
                                    }
                                }
                            } else {
                                if (getStrIdx(card, deckStr) != -1 && getCardStatus(getStrIdx(card, deckStr)).equals("CAP")) {
                                    saveLstCmd(card + " " + getCardStatus(getStrIdx(card, deckStr)));
                                    setCardStatus(getStrIdx(card, deckStr), "CAPD");
                                    int idx = getStrIdx(card, handCap);
                                    handCap.remove(idx);
                                    setHands(getCapIdx(), handCap.get(0), handCap);
                                    setCardStatus(getStrIdx(handCap.get(0), deckStr), "CAP");
                                    handCap.set(0,"");
                                    dissOpp.set(0, card);
                                    setTurn();
                                }
                            }
                        } else { // OPP

                            if (getStrIdx(card, deckStr) != -1 &&
                                (getCardStatus(getStrIdx(card, deckStr)).equals("DECK")
                                        || getCardStatus(getStrIdx(card, deckStr)).equals("OPP"))) {
                                // save the previous status
                                String prevSts = getCardStatus(getStrIdx(card, deckStr));
                                setDissIdx(getDissIdx() + 1);
                                setHands(getDissIdx(), dissOpp.get(getDissIdx()-1), dissOpp);
                                for (int i = getDissIdx()-1; i>1; i--) {
                                    dissOpp.set(i,dissOpp.get(i-1));
                                }
                                dissOpp.set(1,card);

                                if (!handOpp.get(0).equals("")) {
                                    if (getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) {
                                        setCardStatus(getStrIdx(handOpp.get(0), deckStr), "OPP");
                                        setOppIdx(getOppIdx() + 1);
                                        setHands(getOppIdx(), handOpp.get(0), handOpp);
                                        handOpp.set(0,"");
                                    } else { // it's a known card in the hand status equal OPP.
                                        int idx = getStrIdx(card, handOpp);
                                        handOpp.remove(idx);
                                        setHands(getOppIdx(), handOpp.get(0), handOpp);
                                        setCardStatus(getStrIdx(handOpp.get(0), deckStr), "OPP");
                                        handOpp.set(0,"");
                                    }
                                } else {
                                    int idx = getStrIdx(card, handOpp);
                                    if (idx != -1) {
                                        handOpp.remove(idx);
                                        setOppIdx(getOppIdx() - 1);
                                    }
                                }
                                setCardStatus(getStrIdx(card, deckStr), "OPPD");
                                saveLstCmd(card + " " + prevSts + " " + dissOpp.get(0));
                                if (!getGameStage().equals("PASSING")) dissOpp.set(0, "");

                                setTurn();
                            } else { // handle the pass stage of game
                                if (getGameStage().equals("PASSING")) {
                                    String prevSts = getCardStatus(getStrIdx(card, deckStr));
                                    // set the status to CAPD so it can not be passed again
                                    setCardStatus(getStrIdx(card, deckStr), "CAPD");
                                    setDissIdx(getDissIdx() + 1);
                                    setHands(getDissIdx(), dissOpp.get(getDissIdx()-1), dissOpp);
                                    for (int i = getDissIdx()-1; i>1; i--) {
                                        dissOpp.set(i,dissOpp.get(i-1));
                                    }
                                    dissOpp.set(1,card);
                                    setGameStage("PLAYING");
                                    setTurn();
                                }
                            }
                        }
                    }
                }
                break;
            case "FIX":
                if (getGameStage().equals("PLAYING") ||
                        getGameStage().equals("PASSING") ||
                        getGameStage().equals("ENDREQUEST")) {
                    if (getGameStage().equals("ENDREQUEST"))  {
                        setGameStage("PLAYING");
                        break;
                    }
                    if (whosTurn) { // it's CAP's turn so reverse OPP
                        if (!capLstCmds.isEmpty()) {
                            if (capLstCmds.get(0).equals("DECK")) { // OPP never had deck
                                String[] spl = capLstCmds.get(1).split("\\s+");
                                String cardLst = spl[0] + " " + spl[1];
                                setCardStatus(getStrIdx(cardLst, deckStr), spl[2]);
                                handCap.set(0,"");
                                log.trace("FIX");
                                break;
                            } else {
                                if (capLstCmds.get(0).equals("PILE")) {
                                    if (capLstCmds.size()==2) { // it's a PILE with no PASS
                                        String[] spl = capLstCmds.get(1).split("\\s+");
                                        String cardLst = spl[0] + " " + spl[1];
                                        setCardStatus(getStrIdx(cardLst, deckStr), spl[2]);
                                        handCap.set(0,"");
                                        log.trace("FIX");
                                        msgQueue.add("deckcnt "+String.valueOf(getDeckCnt()));
                                        // Allow for fix during the first Passing stage
                                        if (getDeckCnt()==31) setGameStage("ADDREMOVE");
                                        break;
                                    }
                                }
                            }
                        }
                        if (!oppLstCmds.isEmpty()) {
                            dissOpp.remove(1);
                            setDissIdx(getDissIdx() - 1);
                            // Add condition to check for first passing stage
                            // when no pass card is required
                            if (oppLstCmds.size()>1) {
                                fixCardStr(oppLstCmds);
                            } else {
                                setCardStatus(getStrIdx(dissOpp.get(0), deckStr), "DECK");
                            }

                            // Allow for fix during the first Passing stage
                            if (getDeckCnt()==31) setGameStage("ADDREMOVE");
                        }
                    } else { // it's OPP's turn so reverse CAP
                        if (!oppLstCmds.isEmpty()) {
                            if (oppLstCmds.get(0).equals("PILE")) {
                                if (oppLstCmds.size()==2) { // it's a PILE with no PASS
                                    String[] spl = oppLstCmds.get(1).split("\\s+");
                                    String cardLst = spl[0] + " " + spl[1];
                                    setCardStatus(getStrIdx(cardLst, deckStr), spl[2]);
                                    handOpp.set(0,"");
                                    dissOpp.set(0,cardLst);
                                    log.trace("FIX");
                                    msgQueue.add("deckcnt "+String.valueOf(getDeckCnt()));
                                    // Allow for fix during the first Passing stage
                                    if (getDeckCnt()==31) setGameStage("ADDREMOVE");
                                    break;
                                }
                            }
                        }
                        if (!capLstCmds.isEmpty()) fixCardStr(capLstCmds);
                    }
                }
                break;
            case "END":
                if (getGameStage().equals("PLAYING") ||
                        getGameStage().equals("ENDREQUEST")) {
                    setLastCmd(cmd);
                    if (getGameStage().equals("ENDREQUEST")) {
                        setGameStage("ENDING");
                        resetHands(handOpp);
                        setOppIdx(0);
                        break;
                    }
                    if (getGameStage().equals("PLAYING")) setGameStage("ENDREQUEST");
                }
                break;
            case "FINISH":
                if (getGameStage().equals("FINISHED")) break;
                if (getGameStage().equals("FINISHING")) setGameStage("FINISHED");
                log.trace("DUMP");
                log.trace("dissOpp");
                for (String s : dissOpp) {
                    log.trace(s);
                }
                log.trace("handOpp");
                for (String s : handOpp) {
                    log.trace(s);
                }
                log.trace("handCap");
                for (String s : handCap) {
                    log.trace(s);
                }
                log.trace("deckSts");
                for (String s : deckStr) {
                    log.trace(s + " " + getCardStatus(getStrIdx(s, deckStr)));
                }
                setLastCmd(cmd);
                break;
            default:
                break;
        }
    }
    /*
    The method is used to ensure the format the command to be processes by the Fix
    requset.
     */
    public void ensureCmdFmt(LinkedList<String> lst, String str) {
        if (lst.isEmpty()) {
            if (str.equals("PASS") || str.equals("DECK") || str.equals("PILE")) lst.add(str);
        } else {
            String tstString = new String();
            if (lst.get(0).equals("PASS")) tstString = "PASS";
            if (lst.get(0).equals("DECK") || lst.get(0).equals("PILE")) tstString = "deckpile";
            switch (tstString) {
                case "PASS":
                    if (useList(CMDS, str)) break;
                    if (lst.size()==1) lst.add(str);
                    break;
                case "deckpile":
                    if (useList(CMDS, str) && lst.size() ==2 && str.equals("PASS")) lst.add(str);
                    if (!useList(CMDS, str) && (lst.size()==1 || (lst.size()==3 ))) lst.add(str);
                    break;
                default:
                    break;
            }
        }
    }
    /*
    The method is used to process the FIX command.  This command will back out
    the last commands processed.
     */
    public void saveLstCmd(String str) {
        if(getTurn()) ensureCmdFmt(capLstCmds, str);
        if(!getTurn()) ensureCmdFmt(oppLstCmds, str);
    }
    /*
    This method is structured to accept single word commands such as DEAL or PASS.
    Some commands must have a card association while others do not for example SORT.
    This method will process the single word command in the first pass and wait for
    the card in the next processing cycle.  Commands can have single cards, many cards or none.
    The commands that require cards will be saved during processing and retrieved during
    during the subsequent pass.  For example, if the result is a command then process the
    command and save it.  If the result is a card process the card based on the last saved
    command.  If a card has no proceed command it is ignored.
     */
    public List<String> splitResult(String vResult) {
        List<String> resultList = new ArrayList<>();
        String[] splitArray = vResult.split("\\s+");
        resultList = Arrays.asList(splitArray);

        List<String> lnkResult = validVoiceResults(resultList);
        List<String> lnkResultWithIdx = new ArrayList<>();

        for (String s : lnkResult) {
            if (useList(CMDS, s)) { // if command
                processCmds(s, "");
                if (!getLastCmd().equals("")) {
                    lnkResultWithIdx.add(s);
                    log.trace(s);
                }
            } else { // it's a card
                // DEALING and ending must accept multiple cards.
                if (getGameStage().equals("DEALING") || getGameStage().equals("ENDING")) {
                    switch (getGameStage()) {
                        case "DEALING":
                            if (getCapIdx() < 10) { // the deal is !done.
                                // check if the card status is equal to DECK
                                if (getCardStatus(getStrIdx(s,deckStr)).equals("DECK")) { // card is available to be added to cap hand

                                    if (getCapIdx()==0 && dissOpp.get(0).equals("")) {
                                        dissOpp.set(0, s);
                                    } else {
                                        // needed because dissOpp card is decksts "DECK" unit PASSING stage complete
                                        if (!s.equals(dissOpp.get(0))) {
                                            setCapIdx(getCapIdx() + 1);
                                            setCardStatus(getStrIdx(s,deckStr), "CAP");
                                            setHands(getCapIdx(), s, handCap);
                                        }
                                    }
                                    lnkResultWithIdx.add(s);
                                    log.trace(s);
                                    if (getCapIdx()==10) {
                                        setLastCmd("");
                                        setGameStage("ADDREMOVE");
                                        processCmds(getSortSts(), "");
                                    }
                                }
                            }
                            break;
                        case "ENDING":
                            if (getOppIdx() < 10) { // the end meld is !done.
                                if (getCardStatus(getStrIdx(s,deckStr)).equals("END")) break;
                                setCardStatus(getStrIdx(s,deckStr), "END");
                                setOppIdx(getOppIdx() + 1);
                                setHands(getOppIdx(), s, handOpp);
                                lnkResultWithIdx.add(s);
                                log.trace(s);
                                if (getOppIdx()==10) {
                                    setLastCmd("");
                                    setGameStage("FINISHING");
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } else { // not in the dealing stage
                    if (!getGameStage().equals("LOADING") &&
                            !getLastCmd().equals("")) {
                        if (handCap.get(0).equals("") && handOpp.get(0).equals("")) {
                            processCmds(getLastCmd(), s);
                            lnkResultWithIdx.add(s);
                            log.trace(s);
                            setLastCmd("");
                        } else {
                            if (getLastCmd().equals("PASS")) {
                                processCmds(getLastCmd(), s);
                                lnkResultWithIdx.add(s);
                                log.trace(s);
                                setLastCmd("");
                            }
                        }
                    }
                }
            }
        }

        msgQueue.add("result.."+ lnkResult.get(lnkResult.size()-1).toString());
        return lnkResultWithIdx;
    }
    public LinkedList<String> validVoiceResults(List<String> resultList) {
        LinkedList<String> lnklist = new LinkedList<>();
        Iterator<String> vResultIt = resultList.iterator();
        while (vResultIt.hasNext()) {
            String vValue = vResultIt.next();
            if (useList(CMDS, vValue) || useList(SUIT, vValue) || useList(RANK, vValue)) {
                lnklist.add(vValue);
            }
        }
        LinkedList<String> lnklistCard = new LinkedList<>();
        for (String s : lnklist) {
            if (getFirstPass()) {
                setFirstPass(false);
                setCardValue("");
                if (!useList(SUIT, s)) {
                    if (useList(CMDS, s)) {
                        lnklistCard.add(s);
                        setPrevVoice(s);
                        setCardValue("");
                    } else { // it's a RANK
                        setPrevVoice(s);
                        setCardValue(s + " ");
                    }
                }
            } else { // not first pass
                if (useList(CMDS, s)) {
                    lnklistCard.add(s);
                    setPrevVoice(s);
                    setCardValue("");
                } else { // it's a RANK or SUIT
                    if (useList(RANK, s)) {

                        setCardValue(s + " ");
                        setPrevVoice(s);

                    } else { // it's a Suit
                        if (!useList(RANK, getPrevVoice())) {
                            setPrevVoice(s);
                            setCardValue("");
                        } else { // it was previously a RANK
                            setPrevVoice(s);
                            lnklistCard.add(getCardValue() + s);
                            setCardValue("");
                        }
                    }
                }

            }
        }
        return lnklistCard;
    }
}