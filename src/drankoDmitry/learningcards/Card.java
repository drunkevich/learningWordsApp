package drankoDmitry.learningcards;

import android.content.ContentValues;
import android.content.Context;

/**
 * Created by dima on 12.01.15.
 */
public class Card implements Comparable<Card> {

    static int maxQ = 4;
    static int minQ = 0;
    static int defaultQ = 4;
    static Card emptyCard = new Card(-1, "", "", "", 1);

    private int id;
    private String word;
    private String translation;
    private String tag;
    private int quality;

    public Card(int _id, String _word, String _translation, String _tag, int _quality) {
        id = _id;
        word = _word;
        translation = _translation;
        tag = _tag;
        quality = qBound(_quality);

    }

    public static int qBound(int testQ) {
        if (testQ > maxQ) return maxQ;
        if (testQ < minQ) return minQ;
        return testQ;
    }

    public void editCard(String _word, String _translation, String _tag, int _quality, Context context) {

        ContentValues contentValues = new ContentValues();
        if (word.equals(_word)) {
            word = _word;
            contentValues.put(CardsDatabase.WORD, _word);
        }
        if (translation.equals(_translation)) {
            translation = _translation;
            contentValues.put(CardsDatabase.TRANSLATION, _translation);
        }
        if (tag.equals(_tag)) {
            tag = _tag;

            contentValues.put(CardsDatabase.TAG, _tag);
        }
        if (quality != _quality) {
            quality = qBound(_quality);
            contentValues.put(CardsDatabase.QUALITY, quality);
        }
        CardsDatabase.updateCard(id, contentValues, context);
        contentValues.clear();
    }

    public void setBetterQuality(Context context) {
        quality = qBound(quality + 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardsDatabase.QUALITY, quality);
        CardsDatabase.updateCard(id, contentValues, context);
        contentValues.clear();
    }

    public void setWorseQuality(Context context) {
        quality = qBound(quality - 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardsDatabase.QUALITY, quality);
        CardsDatabase.updateCard(id, contentValues, context);
        contentValues.clear();
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }

    public String getTag() {
        return tag;
    }

    public int getQuality() {
        return quality;
    }

    @Override
    public int compareTo(Card another) {
        return (this.id - another.id);
    }
}
