import java.util.ArrayList;
import java.util.List;

/**
 * Created by advman on 2016-10-22.
 */
public class CardSeqSort {
    private static final List<String> seqsArray = new ArrayList<>();
    private List<String> sortedList;

    public CardSeqSort() {
        loadSeqsArray();
    }

    private static void loadSeqsArray() {
        // ten has 4
        seqsArray.add("00010203040506070809");
        seqsArray.add("01020304050607080910");
        seqsArray.add("02030405060708091011");
        seqsArray.add("03040506070809101112");
        // Nine has 5
        seqsArray.add("000102030405060708");
        seqsArray.add("010203040506070809");
        seqsArray.add("020304050607080910");
        seqsArray.add("030405060708091011");
        seqsArray.add("040506070809101112");
        // Eight has 6
        seqsArray.add("0001020304050607");
        seqsArray.add("0102030405060708");
        seqsArray.add("0203040506070809");
        seqsArray.add("0304050607080910");
        seqsArray.add("0405060708091011");
        seqsArray.add("0506070809101112");
        //Seven has 7
        seqsArray.add("00010203040506");
        seqsArray.add("01020304050607");
        seqsArray.add("02030405060708");
        seqsArray.add("03040506070809");
        seqsArray.add("04050607080910");
        seqsArray.add("05060708091011");
        seqsArray.add("06070809101112");
        // Six has 8
        seqsArray.add("000102030405");
        seqsArray.add("010203040506");
        seqsArray.add("020304050607");
        seqsArray.add("030405060708");
        seqsArray.add("040506070809");
        seqsArray.add("050607080910");
        seqsArray.add("060708091011");
        seqsArray.add("070809101112");
        // Five has 9
        seqsArray.add("0001020304");
        seqsArray.add("0102030405");
        seqsArray.add("0203040506");
        seqsArray.add("0304050607");
        seqsArray.add("0405060708");
        seqsArray.add("0506070809");
        seqsArray.add("0607080910");
        seqsArray.add("0708091011");
        seqsArray.add("0809101112");
        // Four has 10
        seqsArray.add("00010203");
        seqsArray.add("01020304");
        seqsArray.add("02030405");
        seqsArray.add("03040506");
        seqsArray.add("04050607");
        seqsArray.add("05060708");
        seqsArray.add("06070809");
        seqsArray.add("07080910");
        seqsArray.add("08091011");
        seqsArray.add("09101112");
        // Three has 11
        seqsArray.add("000102");
        seqsArray.add("010203");
        seqsArray.add("020304");
        seqsArray.add("030405");
        seqsArray.add("040506");
        seqsArray.add("050607");
        seqsArray.add("060708");
        seqsArray.add("070809");
        seqsArray.add("080910");
        seqsArray.add("091011");
        seqsArray.add("101112");
    }
    // set the last command
    public void setSortLst(List<String> lst) {
        this.sortedList = lst;
    }
    // set the last command
    public List<String> getSortLst() {
        return sortedList;
    }
    public List<String> getSeqSort(List<String> inputLst) {
        //
        sortedList = inputLst;
        Integer[] suitCnt = new Integer[4];
        for (int i = 0; i < suitCnt.length; i++) suitCnt[i] = 0;
        // cnt the number of suit cards
        for (int i = 0; i < inputLst.size(); i++) {
            System.out.println(inputLst.get(i) + " " + inputLst.get(i).substring(0, 1));
            int suitInt = Integer.parseInt(inputLst.get(i).substring(0, 1));
            suitCnt[suitInt]++;
        }

        for (int i = 0; i < suitCnt.length; i++) {
            // System.out.println(i +" " + suitCnt[i]);
            if (suitCnt[i] > 2) chkSeqMain(inputLst, Integer.toString(i), suitCnt[i]);
        }
        System.out.println();

        return sortedList;
    }

    // main sequence
    public void chkSeqMain(List<String> inputLst, String suitIdx, int suitCnt) {
        // build the rankStr
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputLst.size(); i++) {
            if (inputLst.get(i).substring(0, 1).equals(suitIdx)) sb.append(inputLst.get(i).substring(1, 3));
            // System.out.println("substr 1,2 "+inputLst.get(i).substring(1,3));
        }
        System.out.println(sb + " " + suitCnt);
        chkRankStr(sb.toString(), suitCnt, suitIdx);
    }

    public void chkRankStr(String rankStr, int suitCnt, String suitIdx) {
        switch (suitCnt) {

            case 3:
             //   if (getStrIdx(rankStr, seqsArray) == -1) break;
                List<String> sortSeq3 = new ArrayList<>();
                List<String> rankStrLst = new ArrayList<>();
                StringBuilder sb;
               for (int i=0;i<suitCnt;i++) {
                    sb = new StringBuilder();
                    sb.append(suitIdx);
                    sb.append(rankStr.substring(i*2,i*2+2));
                    rankStrLst.add(i,sb.toString());
                    System.out.println(sb);
                }
          /*
                sb = new StringBuilder();
                sb.append(suitIdx);
                sb.append(rankStr.substring(2,4));
                rankStrLst.add(1,sb.toString());
                System.out.println(sb);
                sb = new StringBuilder();
                sb.append(suitIdx);
                sb.append(rankStr.substring(4,6));
                rankStrLst.add(2,sb.toString());
                System.out.println(sb);
*/
               for (String s : sortedList) {
                    String[] spl = s.split("!");

                    if (getStrIdx(spl[0], rankStrLst) != -1) {
                        sortSeq3.add("10" + s + " *");
                    }

                }
                for (String s :sortSeq3) {
                    System.out.println(s);
                }
                break;
            default:
                break;
        }

    }
    public int getStrIdx(String str, List<String> lst) {
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).indexOf(str) != -1) {
                return i;
            }
        }
        return -1;
    }
}
