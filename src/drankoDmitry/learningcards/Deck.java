package drankoDmitry.learningcards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Deck extends BaseAdapter {
	
	private Context context;
	private LinkedList<Card> base;
    private OrderType order = OrderType.PURE_RANDOM;
    ;
    private String deckTag;
	private LayoutInflater inflater;
    private Iterator<Card> inOrderIterator;
    private Random rnd = new Random();

    public Deck(String tag, OrderType _order, Context ctx) {
        context = ctx;
        deckTag = tag;
        order = _order;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        base = CardsDatabase.readCards(tag, ctx);
        Collections.sort(base);
        if (order == OrderType.BY_ID) {
            inOrderIterator = base.iterator();
        }
    }

    public Card getCard() {
        Card result = Card.emptyCard;
        switch (order) {
            case BY_ID:
                if (inOrderIterator.hasNext()) {
                    result = inOrderIterator.next();
                } else {
                    inOrderIterator = base.iterator();
                    if (inOrderIterator.hasNext()) {
                        result = inOrderIterator.next();
                    }
                }
                break;
            case PURE_RANDOM:
                int size = base.size();
                if (size > 0) {
                    result = base.get(rnd.nextInt(size));
                }
                break;
            case RANDOM_BY_QUALITY:
                int s = 0;
                for (Card c : base) {
                    int pow = 1 << c.getQuality();
                    s += pow;
                }
                if (s > 0) {
                    int pos = rnd.nextInt(s);
                    for (Card c : base) {
                        pos -= 1 << c.getQuality();
                        if (pos <= 0) {
                            result = c;
                            break;
                        }
                    }
                }
                break;
        }
        return result;
    }

    /*public void editCard(int _id, String _word, String _translation, String _tag, int _quality) {

        for (Deck.Card dc : base) {
            if (dc.id == _id) {
                ContentValues contentValues = new ContentValues();
                if (dc.word != _word) {
                    dc.word = _word;
                    contentValues.put(CardsDatabase.WORD, _word);
                }
                if (dc.translation != _translation){
                    dc.translation = _translation;
                    contentValues.put(CardsDatabase.TRANSLATION,_translation);
                }
                if (dc.tag != _tag) {
                    dc.tag = _tag;

                    contentValues.put(CardsDatabase.TAG, _tag);
                }
                if (dc.quality != _quality) {
                    dc.quality = _quality;
                    contentValues.put(CardsDatabase.QUALITY, _quality);
                }
                CardsDatabase.updateCard(_id, contentValues, context);
                contentValues.clear();
            }
        }
    }
    */
    public boolean deleteCard(int _id) {
        for (Card dc : base) {
            if (dc.getId() == _id) {
                base.remove(dc);
                CardsDatabase.deleteCard(_id, context);
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return (base.size() > 0);
    }

    @Override
    public int getCount() {
        return base.size();
    }

	@Override
    public Object getItem(int arg0) {
        return base.get(arg0);
    }

	@Override
    public long getItemId(int position) {
        return position;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_card, parent, false);
        Card card = base.get(position);
        ((TextView) view.findViewById(R.id.itemCardWord)).setText(card.getWord());
        ((TextView) view.findViewById(R.id.itemCardTranslate)).setText(card.getTranslation() + " ");
        ((TextView) view.findViewById(R.id.itemCardQ)).setText(" " + card.getQuality() + " ");

        return view;
    }

    public void save() {
        CardsDatabase.save(base, context);
    }

    public static enum OrderType {BY_ID, PURE_RANDOM, RANDOM_BY_QUALITY}
}
