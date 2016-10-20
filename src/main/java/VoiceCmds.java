import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.PatternSyntaxException;

/**
 * Created by advman on 2016-10-11.
 */
public class VoiceCmds {
    public List<String> curCmds;
    public String gameStage;
    public boolean fstPass;
    // true = CAP(computer assisted player), false= OPP(opposing player)
    public boolean whosTurn = true;
    // set flag in insure CAP does a [PILE PASS] combination
    // set to true to allow processing during PASSING game stage
    public boolean pileDone = true;
    public String prevVoice;
    public String cardValue;
    public int capIdx;
    public int oppIdx;
    public int dissIdx;
    public String lastCmd;

    LinkedList<String> handCap;
    LinkedList<String> handOpp;
    LinkedList<String> dissOpp;
    LinkedList<String> capLstCmds;
    LinkedList<String> oppLstCmds;

    List<String> deckStr;
    LinkedList<String> cardStatus;

    private static final String[] CMDS = {"DEAL", "REMOVE", "ADD", "SORT", "SORTRANK", "PASS", "DECK", "PILE", "END", "SET", "FIX","FINISH"};
    private static final String[] SUIT = {"CLUB", "DIAMOND", "HEART", "SPADE"};
    private static final String[] RANK = {"ACE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING"};
    static Logger log = Logger.getLogger(VoiceCmds.class.getName());

    public VoiceCmds() {
        handCap = new LinkedList<>();
        handOpp = new LinkedList<>();
        dissOpp = new LinkedList<>();
        capLstCmds = new LinkedList<>();
        oppLstCmds = new LinkedList<>();

        setHands(0, "", handOpp);
        setHands(0, "", dissOpp);
        setGameStage("LOADING");
        setLastCmd("");
        setFirstPass(true);
        setPrevVoice("");
        setCardValue("");
        setCapIdx(0);
        setOppIdx(0);
        setDissIdx(0);
        createDeck();
    }

    // sort handCap array by suit and rank
    public void sortHandCap(List<String> lst) {
        Collections.sort(lst);
        resetHands(handCap);
        for (String s : lst) {
            String[] spl = s.split("!");
            handCap.add(spl[1]);
        }
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
    public int getOppIdx() {
        return oppIdx;
    }
    public int getCapIdx() {
        return capIdx;
    }
    public int getDissIdx() {
        return dissIdx;
    }
    public void setOppIdx(int capIdx) {
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
            this.whosTurn = !this.whosTurn;
        }
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

        dissOpp.set(0,"");
        if (lst.get(0).equals("PASS")) {
            String[] spl = lst.get(1).split("\\s+");
            String card = spl[0] + " " + spl[1];
            setCardStatus(getStrIdx(card, deckStr), spl[2]);
            if (spl[2].equals("OPP")) {
                handOpp.add(card);
            }
            int splSize = spl.length;
            if (splSize==5) { // update the dissOpp00
                card = spl[3] + " " + spl[4];
                dissOpp.set(0, card);
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
                    if (sts1.equals("OPPD")) { // update handcap
                        handCap.remove(10);
                        handCap.add(10, card2);
                        setCardStatus(getStrIdx(card1, deckStr), "OPPD");
                        setCardStatus(getStrIdx(card2, deckStr), "CAP");
                    } else { // sts is CAPD
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

                    }
                }
            }
            setTurn();
        }

    }
    public void processCmds(String cmd, String card) {
        List<String> sorting;
        setLastCmd("");

        switch (cmd) {
            case "DEAL":
                if (getGameStage().equals("LOADING") || getGameStage().equals("DEALING") || getGameStage().equals("PASSING")) {
                    setLastCmd(cmd);
                    setGameStage("DEALING");
                    setCapIdx(0);
                    resetHands(handCap);
                    resetCardStatus();
                    whosTurn = true;
                }
                break;
            case "REMOVE":
                if (getGameStage().equals("PASSING") || getGameStage().equals("FINISHING" )) {

                    if (card.equals("")) {
                        setLastCmd(cmd);
                    } else {
                        switch (getGameStage()) {
                            case "PASSING":
                                if (getStrIdx(card, deckStr) != -1 && getStrIdx(card, handCap) != -1) {
                                    setCardStatus(getStrIdx(card, deckStr), "DECK");
                                    int idx = getStrIdx(card, handCap);
                                    handCap.remove(idx);
                                    setCapIdx(getCapIdx() - 1);
                                }
                                break;
                            case "FINISHING":
                                if (getStrIdx(card, deckStr) != -1 && getStrIdx(card, dissOpp) != -1) {
                                    setCardStatus(getStrIdx(card, deckStr), "DECK");
                                    int idx = getStrIdx(card, dissOpp);
                                    dissOpp.remove(idx);
                                    setDissIdx(getDissIdx() - 1);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            case "ADD":
                if (getGameStage().equals("PASSING") || getGameStage().equals("FINISHING")) {
                    if (card.equals("")) {
                        setLastCmd(cmd);
                    } else {
                        switch (getGameStage()) {
                            case "PASSING":
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
                                if (getDissIdx() < 10) { // the diss hand is full can't add.
                                    // check if the card status is equal to END
                                    if (getCardStatus(getStrIdx(card,deckStr)).equals("END")) break;
                                    // set the card status in the deck to in the computer aided players(CAP) hand
                                    setCardStatus(getStrIdx(card, deckStr), "END");
                                    setDissIdx(getDissIdx() + 1);
                                    setHands(getDissIdx(), card, dissOpp);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }

                break;
            case "SORTRANK":
                if (!getGameStage().equals("PASSING")) break;
                sorting = new ArrayList<>();
                for (String s : handCap) {
                    if (!s.equals("")) {
                        String[] spl = s.split("\\s+");
                        int rankIdx = getStrIdxArry(spl[0], RANK);
                        String splFormt = String.format("%02d", rankIdx);
                        sorting.add(splFormt + "!" + s);
                    }
                }
                sortHandCap(sorting);
                setLastCmd(cmd);
                break;
            case "SORT":
                if (!getGameStage().equals("PASSING")) break;
                sorting = new ArrayList<>();
                for (String s : handCap) {
                    if (!s.equals("")) {
                        String[] spl = s.split("\\s+");
                        int suitIdx = getStrIdxArry(spl[1], SUIT);
                        int rankIdx = getStrIdxArry(spl[0], RANK);
                        String splFormt = String.format("%02d", rankIdx);
                        sorting.add(suitIdx + splFormt + "!" + s);
                    }
                }
                sortHandCap(sorting);
                setLastCmd(cmd);
                break;
            case "SET":
                if (!getGameStage().equals("PASSING")) break;
                // setTurn() used from the controller to toggle turns
                // set command used to change default from CAP to OPP.
                whosTurn = !whosTurn;
                log.trace(cmd);
                break;
            case "DECK":
                if (!getGameStage().equals("PLAYING")) break;
                if (!whosTurn) break; // not a valid opp command
                if (card.equals("")) {
                    setLastCmd(cmd);
                    saveLstCmd(cmd);
                } else {
                    if (getStrIdx(card, deckStr) == -1) break;
                    if (getGameStage().equals("PLAYING") && !getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) break;
                    saveLstCmd(card + " " + getCardStatus(getStrIdx(card, deckStr)));
                    setCardStatus(getStrIdx(card, deckStr), "CAP");
                    handCap.set(0,card);
                }

                break;
            case "PILE":
                if (getGameStage().equals("PLAYING") || getGameStage().equals("PASSING")) {

                    if (card.equals("") && getGameStage().equals("PASSING")) {
                        setLastCmd(cmd);

                    } else {
                        if (whosTurn) { // CAP if true
                            if (card.equals("") && !dissOpp.get(1).equals("")) card=dissOpp.get(1);
                            if (getStrIdx(card, deckStr) == -1) break;
                            if (getGameStage().equals("PASSING") && !getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) break;
                            if (getGameStage().equals("PLAYING") && !getCardStatus(getStrIdx(card, deckStr)).equals("OPPD")) break;
                            setGameStage("PLAYING");
                            saveLstCmd(cmd);
                            saveLstCmd(card + " " + getCardStatus(getStrIdx(card, deckStr)));
                            setCardStatus(getStrIdx(card, deckStr), "CAPP");
                            handCap.set(0,card);

                        } else { // OPP
                            if (card.equals("") && !dissOpp.get(0).equals("")) card=dissOpp.get(0);
                            if (getStrIdx(card, deckStr) == -1) break;
                            if (getGameStage().equals("PASSING") && !getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) break;
                            if (getGameStage().equals("PLAYING") && !getCardStatus(getStrIdx(card, deckStr)).equals("CAPD")) break;
                            setGameStage("PLAYING");
                            saveLstCmd(cmd);
                            saveLstCmd(card + " " + getCardStatus(getStrIdx(card, deckStr)));
                            setCardStatus(getStrIdx(card, deckStr), "OPPP");
                            handOpp.set(0,card);
                            dissOpp.set(0, "");
                        }
                    }
                }

                break;
            case "PASS":
                if (getGameStage().equals("PASSING") || getGameStage().equals("PLAYING")) {
                    if (card.equals("")) {
                        setLastCmd(cmd);
                        saveLstCmd(cmd);
                    } else {
                        if (!getGameStage().equals("PLAYING")) setGameStage("PLAYING");
                        if (whosTurn) { // CAP if true
                            if (handCap.get(0).equals("")) {
                                if (getStrIdx(card, deckStr) != -1 && getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) {
                                    saveLstCmd(card + " " + getCardStatus(getStrIdx(card, deckStr)));
                                    setCardStatus(getStrIdx(card, deckStr), "CAPD");
                                    dissOpp.set(0, card);
                                    setTurn();
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
                                dissOpp.set(0, "");

                                setTurn();
                            }
                        }
                    }
                }

                break;
            case "FIX":
                if (getGameStage().equals("PLAYING") || getGameStage().equals("ENDREQUEST")) {
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
                                break;
                            } else {
                                if (capLstCmds.get(0).equals("PILE")) {
                                    if (capLstCmds.size()==2) { // it's a PILE with no PASS
                                        String[] spl = capLstCmds.get(1).split("\\s+");
                                        String cardLst = spl[0] + " " + spl[1];
                                        setCardStatus(getStrIdx(cardLst, deckStr), spl[2]);
                                        handCap.set(0,"");
                                        break;
                                    }
                                }
                            }

                        }
                        if (!oppLstCmds.isEmpty()) {
                            dissOpp.remove(1);
                            setDissIdx(getDissIdx() - 1);
                            fixCardStr(oppLstCmds);
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
                                    break;
                                }
                            }

                        }

                        if (!capLstCmds.isEmpty()) fixCardStr(capLstCmds);
                    }
                }
                break;
            case "END":
                if (getGameStage().equals("PLAYING") || getGameStage().equals("ENDREQUEST")) {
                    setLastCmd(cmd);
                    if (getGameStage().equals("ENDREQUEST")) {
                        setGameStage("ENDING");
                        log.trace("ENDING");
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

                        while (!dissOpp.isEmpty()) {
                            dissOpp.removeFirst();
                        }
                        setDissIdx(0);
                        setHands(0, "", dissOpp);
                        break;
                    }
                    if (getGameStage().equals("PLAYING")) setGameStage("ENDREQUEST");
                }
                break;
            case "FINISH":
                if (getGameStage().equals("FINISHING")) setGameStage("FINISHED");
                break;
            default:
                break;
        }

    }
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
    public void saveLstCmd(String str) {
        if(getTurn()) ensureCmdFmt(capLstCmds, str);
        if(!getTurn()) ensureCmdFmt(oppLstCmds, str);
    }

    public List<String> splitResult(String vResult) {
        List<String> resultList = new ArrayList<>();
        try {
            String[] splitArray = vResult.split("\\s+");
            resultList = Arrays.asList(splitArray);
        } catch (PatternSyntaxException ex) {
            //
        }
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
                if (getGameStage().equals("DEALING") || getGameStage().equals("ENDING")) {
                    switch (getGameStage()) {
                        case "DEALING":
                            if (getCapIdx() < 10) { // the deal is !done.
                                // check if the card status is equal to DECK
                                if (getCardStatus(getStrIdx(s,deckStr)).equals("DECK")) { // card is available to be added to cap hand
                                    // set the card status in the deck to in the computer aided players(CAP) hand
                                    setCardStatus(getStrIdx(s,deckStr), "CAP");
                                    setCapIdx(getCapIdx() + 1);
                                    setHands(getCapIdx(), s, handCap);
                                    lnkResultWithIdx.add(s);
                                    log.trace(s);
                                    if (getCapIdx()==10) {
                                        setLastCmd("");
                                        setGameStage("PASSING");
                                    }
                                }
                            }
                            break;
                        case "ENDING":
                            if (getDissIdx() < 10) { // the end meld is !done.
                                if (getCardStatus(getStrIdx(s,deckStr)).equals("END")) break;
                                setCardStatus(getStrIdx(s,deckStr), "END");
                                setDissIdx(getDissIdx() + 1);
                                setHands(getDissIdx(), s, dissOpp);
                                lnkResultWithIdx.add(s);
                                log.trace(s);
                                if (getDissIdx()==10) {
                                    setLastCmd("");
                                    setGameStage("FINISHING");
                                }
                            }
                            break;
                        default:
                            break;
                    }

                } else { // not in the dealing stage
                    if (!getGameStage().equals("LOADING") && !getLastCmd().equals("")) {
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

    public void setCurCmds(List<String> curCmds) {
        System.out.println("Valid voice cmds ");
        Iterator<String> iterator = curCmds.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        this.curCmds = curCmds;
    }

    public List<String> getCurCmds() {
        return curCmds;
    }

    public String getHypoResult(List<String> hypoResult) {
        String theResult = "unknown";
        Iterator<String> hypoIt = hypoResult.iterator();
        while (hypoIt.hasNext() && theResult.equals("unknown")) {
            String hypoValue = hypoIt.next().replaceAll("[^A-Z]+", "");
            if (isCurCmdValid(hypoValue)) {
                theResult = hypoValue;
                break;
            }
        }
        return theResult;
    }

    public boolean isCurCmdValid(String hypoValue) {
        boolean isValidCmd = false;
        Iterator<String> curCmdsIt = getCurCmds().iterator();
        while (curCmdsIt.hasNext()) {
            if (curCmdsIt.next().equals(hypoValue)) {
                isValidCmd = true;
                break;
            }
        }
        return isValidCmd;
    }
}
