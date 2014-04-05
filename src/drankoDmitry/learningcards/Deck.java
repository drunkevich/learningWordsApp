package drankoDmitry.learningcards;

import java.util.Collections;
import java.util.LinkedList;
import android.content.ContentValues;
import android.content.Context;

public class Deck{
	
	private Context context;
	private LinkedList<Card> base;
	private LinkedList<Card> randomized;
	public static enum OrderType {BY_ID,PURE_RANDOM,RANDOM_BY_QUALITY};
	private OrderType order = OrderType.PURE_RANDOM;
	private String deckTag;
	
	public Deck(String tag, OrderType _order, Context ctx) {
		context = ctx;
		deckTag = tag;
		order = _order;
		base = CardsDatabase.readCards(tag, ctx);
		shuffle(order);
	}
	
	
	public void shuffle(OrderType order) {
		switch (order) {
		case PURE_RANDOM: shufflePureRandom(); break;
		case BY_ID: sortById(); break;
		case RANDOM_BY_QUALITY: shuffleByQualiy(); break;
		default: randomized = new LinkedList<Deck.Card>(base);break;
		}
		
	}




	private void shuffleByQualiy() {
		randomized = new LinkedList<Deck.Card>();
		int maxQ=1;
		for (Card card : base) {
			if (card.quality>maxQ)
				maxQ=card.quality;
		}
		for (Card card : base) {
			randomized.addAll(Collections.nCopies(card.getFrequency(maxQ), card));
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
		Deck.Card dc = randomized.removeFirst();
		randomized.addLast(dc);
		return dc;
	}
	
	public boolean editCard(int _id, String _word, String _translation, String _tag, int _quality) {
		for (Deck.Card dc : base) {
			if (dc.id == _id) {
				//TODO deleting card with wrong tag
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
					if (_quality<1) {
						_quality=1;
					} else if (_quality>5) {
						_quality=5;
					}
					dc.quality = _quality;
					contentValues.put(CardsDatabase.QUALITY, _quality);
				}
				CardsDatabase.updateCard(_id, contentValues, context);
				contentValues.clear();
				return true;
			}
		}
		return false;
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

		public int getFrequency(int maxQ) {
			return (int) Math.pow(3, (maxQ-this.quality));
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
}
