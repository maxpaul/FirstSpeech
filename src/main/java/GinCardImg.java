import javafx.scene.image.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by advman on 2016-10-20.
 */
public class GinCardImg {
    // Array of image file names
    private static final List<String> deckImgFnames = new ArrayList<>();
    private static final List<Image> images = new ArrayList<>();
    private static final List<String> smallImgFnames = new ArrayList<>();
    private static final List<Image> smallImgs = new ArrayList<>();
    private static final String imgFnamedir = "/imgs/deck01/";
    private static final List<String> faceCards = new ArrayList<>();;

    public GinCardImg() {
        faceCards.add("");
        faceCards.add("JACK CLUB");
        faceCards.add("QUEEN CLUB");
        faceCards.add("KING CLUB");
        faceCards.add("JACK DIAMOND");
        faceCards.add("QUEEN DIAMOND");
        faceCards.add("KING DIAMOND");
        faceCards.add("JACK HEART");
        faceCards.add("QUEEN HEART");
        faceCards.add("KING HEART");
        faceCards.add("JACK SPADE");
        faceCards.add("QUEEN SPADE");
        faceCards.add("KING SPADE");

        smallImgFnames.add(imgFnamedir + "dsc02058.png");
        smallImgFnames.add(imgFnamedir + "jack_of_clubs2.png");
        smallImgFnames.add(imgFnamedir + "queen_of_clubs2.png");
        smallImgFnames.add(imgFnamedir + "king_of_clubs2.png");
        smallImgFnames.add(imgFnamedir + "jack_of_diamonds2.png");
        smallImgFnames.add(imgFnamedir + "queen_of_diamonds2.png");
        smallImgFnames.add(imgFnamedir + "king_of_diamonds2.png");
        smallImgFnames.add(imgFnamedir + "jack_of_hearts2.png");
        smallImgFnames.add(imgFnamedir + "queen_of_hearts2.png");
        smallImgFnames.add(imgFnamedir + "king_of_hearts2.png");
        smallImgFnames.add(imgFnamedir + "jack_of_spades2.png");
        smallImgFnames.add(imgFnamedir + "queen_of_spades2.png");
        smallImgFnames.add(imgFnamedir + "king_of_spades2.png");

        deckImgFnames.add(0,imgFnamedir + "dsc02058.png");
        deckImgFnames.add(1,imgFnamedir + "ace_of_clubs.png");
        deckImgFnames.add(2,imgFnamedir + "2_of_clubs.png");
        deckImgFnames.add(3,imgFnamedir + "3_of_clubs.png");
        deckImgFnames.add(4,imgFnamedir + "4_of_clubs.png");
        deckImgFnames.add(5,imgFnamedir + "5_of_clubs.png");
        deckImgFnames.add(6,imgFnamedir + "6_of_clubs.png");
        deckImgFnames.add(7,imgFnamedir + "7_of_clubs.png");
        deckImgFnames.add(8,imgFnamedir + "8_of_clubs.png");
        deckImgFnames.add(9,imgFnamedir + "9_of_clubs.png");
        deckImgFnames.add(10,imgFnamedir + "10_of_clubs.png");
        deckImgFnames.add(11,imgFnamedir + "jack_of_clubs.png");
        deckImgFnames.add(12,imgFnamedir + "queen_of_clubs.png");
        deckImgFnames.add(13,imgFnamedir + "king_of_clubs.png");

        deckImgFnames.add(14,imgFnamedir + "ace_of_diamonds.png");
        deckImgFnames.add(15,imgFnamedir + "2_of_diamonds.png");
        deckImgFnames.add(16,imgFnamedir + "3_of_diamonds.png");
        deckImgFnames.add(17,imgFnamedir + "4_of_diamonds.png");
        deckImgFnames.add(18,imgFnamedir + "5_of_diamonds.png");
        deckImgFnames.add(19,imgFnamedir + "6_of_diamonds.png");
        deckImgFnames.add(20,imgFnamedir + "7_of_diamonds.png");
        deckImgFnames.add(21,imgFnamedir + "8_of_diamonds.png");
        deckImgFnames.add(22,imgFnamedir + "9_of_diamonds.png");
        deckImgFnames.add(23,imgFnamedir + "10_of_diamonds.png");
        deckImgFnames.add(24,imgFnamedir + "jack_of_diamonds.png");
        deckImgFnames.add(25,imgFnamedir + "queen_of_diamonds.png");
        deckImgFnames.add(26,imgFnamedir + "king_of_diamonds.png");

        deckImgFnames.add(27,imgFnamedir + "ace_of_hearts.png");
        deckImgFnames.add(28,imgFnamedir + "2_of_hearts.png");
        deckImgFnames.add(29,imgFnamedir + "3_of_hearts.png");
        deckImgFnames.add(30,imgFnamedir + "4_of_hearts.png");
        deckImgFnames.add(31,imgFnamedir + "5_of_hearts.png");
        deckImgFnames.add(32,imgFnamedir + "6_of_hearts.png");
        deckImgFnames.add(33,imgFnamedir + "7_of_hearts.png");
        deckImgFnames.add(34,imgFnamedir + "8_of_hearts.png");
        deckImgFnames.add(35,imgFnamedir + "9_of_hearts.png");
        deckImgFnames.add(36,imgFnamedir + "10_of_hearts.png");
        deckImgFnames.add(37,imgFnamedir + "jack_of_hearts.png");
        deckImgFnames.add(38,imgFnamedir + "queen_of_hearts.png");
        deckImgFnames.add(39,imgFnamedir + "king_of_hearts.png");

        deckImgFnames.add(40,imgFnamedir + "ace_of_spades.png");
        deckImgFnames.add(41,imgFnamedir + "2_of_spades.png");
        deckImgFnames.add(42,imgFnamedir + "3_of_spades.png");
        deckImgFnames.add(43,imgFnamedir + "4_of_spades.png");
        deckImgFnames.add(44,imgFnamedir + "5_of_spades.png");
        deckImgFnames.add(45,imgFnamedir + "6_of_spades.png");
        deckImgFnames.add(46,imgFnamedir + "7_of_spades.png");
        deckImgFnames.add(47,imgFnamedir + "8_of_spades.png");
        deckImgFnames.add(48,imgFnamedir + "9_of_spades.png");
        deckImgFnames.add(49,imgFnamedir + "10_of_spades.png");
        deckImgFnames.add(50,imgFnamedir + "jack_of_spades.png");
        deckImgFnames.add(51,imgFnamedir + "queen_of_spades.png");
        deckImgFnames.add(52,imgFnamedir + "king_of_spades.png");

        for (String s : deckImgFnames) {
            images.add(new javafx.scene.image.Image(GinCardImg.class.getResourceAsStream(s)));
        }
        for (String s : smallImgFnames) {
            smallImgs.add(new javafx.scene.image.Image(GinCardImg.class.getResourceAsStream(s)));
        }
    }

  /*  public String getImgPath(int idx){
        String imgPath = deckImgFnames.get(idx);
        return imgPath;
    }*/
    public Image getImage(int idx){
        Image img = images.get(idx);
        return img;
    }
    public Image getImage(int idx, String card){
        Image img;
        if (getStrIdx(card, faceCards) !=-1) {
            img = smallImgs.get(getStrIdx(card, faceCards));
        } else {
            img = images.get(idx);
        }
        return img;
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
