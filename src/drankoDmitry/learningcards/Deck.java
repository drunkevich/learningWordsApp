package drankoDmitry.learningcards;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Deck extends BaseAdapter {
	
	private Context context;
	private LinkedList<Card> base;
	private LinkedList<Card> randomized;
	public static enum OrderType {BY_ID,PURE_RANDOM,RANDOM_BY_QUALITY};
	private OrderType order = OrderType.PURE_RANDOM;
	private String deckTag;
	private LayoutInflater inflater;
	private Random random = new Random();
	
	public Deck(String tag, OrderType _order, Context ctx) {
		context = ctx;
		deckTag = tag;
		order = _order;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		base = CardsDatabase.readCards(tag, ctx);
		shuffle(order);
	}
	
	
	public void shuffle(OrderType order) {
		switch (order) {
		case PURE_RANDOM: shufflePureRandom(); break;
		case BY_ID: sortById(); break;
		case RANDOM_BY_QUALITY: shuffleByQuality(); break;
		default: randomized = new LinkedList<Deck.Card>(base);break;
		}
		
	}




	private void shuffleByQuality() {
		randomized = new LinkedList<Deck.Card>();
		Card c = base.getFirst();
		int maxQ = c.quality;
		int minQ = c.quality;
		for (Card card : base) {
			if (card.quality>maxQ)
				maxQ=card.quality;
			if (card.quality<minQ)
				minQ=card.quality;
		}
		if (maxQ==minQ) {
			randomized = new LinkedList<Deck.Card>(base);
		} else {
			int gape = maxQ-minQ;
			while (randomized.size()==0) {
				for (Card card : base) {
					if ((card.quality-random.nextInt(gape+1))<=minQ) {
						randomized.addLast(card);
					}
				}
			}
		}
		Collections.shuffle(randomized);
	}


	private void sortById() {
		randomized = new LinkedList<Deck.Card>(base);
		Collections.sort(randomized);
	}


	private void shufflePureRandom() {
		randomized = new LinkedList<Deck.Card>(base);
		Collections.shuffle(randomized);
	}
	
	public Deck.Card getCard() {
		if (randomized.size()==0) {
			save();
			shuffle(order);
		}
		Deck.Card dc = randomized.removeFirst();
		return dc;
	}
	
	public void editCard(int _id, String _word, String _translation, String _tag, int _quality) {
		
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
	
	public boolean deleteCard(int _id) {
		for (Deck.Card dc : base) {
			if (dc.id == _id) {
				base.remove(dc);
				CardsDatabase.deleteCard(_id, context);
				return true;
			}
		}
		return false;
	}

	public static class Card implements Comparable<Card>{
		int id; 
		String word;
		String translation;
		String tag;
		int quality;
		
		public Card (int _id, String _word, String _translation, String _tag, int _quality) {
			id=_id;
			word=_word;
			translation=_translation;
			tag=_tag;
			quality=_quality;
		}

		

		@Override
		public int compareTo(Card another) {
			return (this.id-another.id);
		}
	}

	public boolean isEmpty() {
		if (base.size()>0)
			return false;
		else 
			return true;
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
		((TextView) view.findViewById(R.id.itemCardWord)).setText(card.word);
		((TextView) view.findViewById(R.id.itemCardTranslate)).setText(card.translation+" ");
		((TextView) view.findViewById(R.id.itemCardQ)).setText(" "+card.quality+" ");
		
		return view;
	}
	
	public void save() {
		CardsDatabase.save(base, context);
	}
}
