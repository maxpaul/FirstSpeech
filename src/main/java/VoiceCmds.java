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
    public String prevVoice;
    public String cardValue;
    public int capIdx;
    public int oppIdx;
    public int dissIdx;
    public String lastCmd;
    LinkedList<String> handCap;
    LinkedList<String> handOpp;
    LinkedList<String> dissOpp;
    List<String> deckStr;
    LinkedList<String> cardStatus;

    private static final String[] CMDS = {"DEAL", "TAKE", "DISCARD", "PASS", "DRAW", "TAKE", "ADD", "REMOVE", "SORT", "SORTRANK","PASS", "DECK", "PILE", "END", "SET"};
    private static final String[] SUIT = {"CLUB", "DIAMOND", "HEART", "SPADE"};
    private static final String[] RANK = {"ACE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING"};
    static Logger log = Logger.getLogger(VoiceCmds.class.getName());

    public VoiceCmds() {
        handCap = new LinkedList<>();
        handOpp = new LinkedList<>();
        dissOpp = new LinkedList<>();
       // setHands(0, "", handCap);
       // setHands(0, "", handOpp);
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


    public void setHands(int capIdx, String str, LinkedList<String> lst) {
        lst.add(capIdx, str);
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
        if (getGameStage().equals("PLAYING"))
        this.whosTurn = !this.whosTurn; }
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

    public void processCmds(String cmd, String card) {
        List<String> sorting;
        setLastCmd("");

        switch (cmd) {
            case "DEAL":
                if (getGameStage().equals("LOADING") || getGameStage().equals("DEALING") || getGameStage().equals("PASSING")) {
                    setLastCmd("DEAL");
                    setGameStage("DEALING");
                    setCapIdx(0);
                    resetHands(handCap);
                   // setHands(0, "", handCap);
                    resetCardStatus();
                    whosTurn = true;
                    log.trace(cmd);
                }
                break;
            case "REMOVE":
                if (!getGameStage().equals("PASSING")) break;
                if (card.equals("")) {
                    setLastCmd("REMOVE");
                } else {
                    if (getStrIdx(card, deckStr) != -1 && getStrIdx(card, handCap) != -1) {
                        setCardStatus(getStrIdx(card, deckStr), "DECK");
                        int idx = getStrIdx(card, handCap);
                        handCap.remove(idx);
                        setCapIdx(getCapIdx() - 1);
                    }
                }
                break;
            case "ADD":
                if (!getGameStage().equals("PASSING")) break;
                if (card.equals("")) {
                    setLastCmd("ADD");
                } else {
                    if (getCapIdx() < 10) { // the cap hand is full can't addd.
                        // check if the card status is equal to DECK
                        if (getCardStatus(getStrIdx(card,deckStr)).equals("DECK")) { // card is available to be added to cap hand
                            // set the card status in the deck to in the computer aided players(CAP) hand
                            setCardStatus(getStrIdx(card, deckStr), "CAP");
                            setCapIdx(getCapIdx() + 1);
                            setHands(getCapIdx(), card, handCap);
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
                setLastCmd("SORTRANK");
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
                setLastCmd("SORT");
                break;
            case "SET":
                if (!getGameStage().equals("PASSING")) break;
                setGameStage("PLAYING");
                // setTurn() used from the controller to toggle turns
                // set command used to change default from CAP to OPP.
                whosTurn = false;
                break;
            case "DECK":
                if (!getGameStage().equals("PLAYING")) break;
                break;
            case "PILE":
                if (getGameStage().equals("PLAYING") || getGameStage().equals("PASSING")) {
                    if (card.equals("")) {
                        setLastCmd("PILE");
                    } else {
                        if (whosTurn) { // CAP if true
                            if (getStrIdx(card, deckStr) == -1) break;
                            if (getGameStage().equals("PASSING") && !getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) break;
                            if (getGameStage().equals("PLAYING") && !getCardStatus(getStrIdx(card, deckStr)).equals("OPPD")) break;
                            setGameStage("PLAYING");
                            setCardStatus(getStrIdx(card, deckStr), "CAP");
                            handCap.set(0,card);

                        } else { // OPP
                            if (getStrIdx(card, deckStr) != -1 && getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) {
                                setCardStatus(getStrIdx(card, deckStr), "OPPD");
                                setDissIdx(getDissIdx() + 1);
                                setHands(getDissIdx(), dissOpp.get(getDissIdx()-1), dissOpp);
                                for (int i = getDissIdx()-1; i>1; i--) {
                                    dissOpp.set(i,dissOpp.get(i-1));
                                }
                                dissOpp.set(1,card);
                                setTurn();
                            }

                        }
                    }
                }

                break;
            case "PASS":
                if (getGameStage().equals("PASSING") || getGameStage().equals("PLAYING")) {
                    if (card.equals("")) {
                        setLastCmd("PASS");
                    } else {
                        if (!getGameStage().equals("PLAYING")) setGameStage("PLAYING");
                        if (whosTurn) { // CAP if true
                            if (handCap.get(0).equals("")) {
                                if (getStrIdx(card, deckStr) != -1 && getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) {
                                    setCardStatus(getStrIdx(card, deckStr), "CAPD");
                                    setTurn();
                                }
                            } else {
                                if (getStrIdx(card, deckStr) != -1 && getCardStatus(getStrIdx(card, deckStr)).equals("CAP")) {
                                    setCardStatus(getStrIdx(card, deckStr), "CAPD");
                                    int idx = getStrIdx(card, handCap);
                                    handCap.remove(idx);
                                    setHands(getCapIdx(), handCap.get(0), handCap);
                                    handCap.set(0,"");
                                    setTurn();
                                }

                            }


                        } else { // OPP
                            if (getStrIdx(card, deckStr) != -1 && getCardStatus(getStrIdx(card, deckStr)).equals("DECK")) {
                                setCardStatus(getStrIdx(card, deckStr), "OPPD");
                                setDissIdx(getDissIdx() + 1);
                                setHands(getDissIdx(), dissOpp.get(getDissIdx()-1), dissOpp);
                                for (int i = getDissIdx()-1; i>1; i--) {
                                    dissOpp.set(i,dissOpp.get(i-1));
                                }
                                dissOpp.set(1,card);
                                setTurn();
                            }

                        }
                    }
                }

                break;
            case "END":
                if (!getGameStage().equals("PLAYING")) break;
                break;
            default:
                break;
        }

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
                if (!getLastCmd().equals("")) lnkResultWithIdx.add(s);
            } else { // it's a card
                if (getGameStage().equals("DEALING")) {
                    if (getCapIdx() < 10) { // the deal is !done.
                        // check if the card status is equal to DECK
                        if (getCardStatus(getStrIdx(s,deckStr)).equals("DECK")) { // card is available to be added to cap hand
                            // set the card status in the deck to in the computer aided players(CAP) hand
                            setCardStatus(getStrIdx(s,deckStr), "CAP");
                            setCapIdx(getCapIdx() + 1);
                            setHands(getCapIdx(), s, handCap);
                            lnkResultWithIdx.add(s + " " + getCapIdx());
                            log.trace(s);
                            if (getCapIdx()==10) {
                                setLastCmd("");
                                setGameStage("PASSING");
                            }
                        }
                    }
                } else { // not in the dealing stage
                    if (!getGameStage().equals("LOADING") && !getLastCmd().equals("")) {
                        if (handCap.get(0).equals("")) {
                            processCmds(getLastCmd(), s);
                            lnkResultWithIdx.add(getLastCmd() + " " + s + " 0");

                            log.trace(getLastCmd() + " " + s + " 0");
                            setLastCmd("");
                        } else {
                            if (getLastCmd().equals("PASS")) {
                                processCmds(getLastCmd(), s);
                                lnkResultWithIdx.add(getLastCmd() + " " + s + " 0");

                                log.trace(getLastCmd() + " " + s + " 0");
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
