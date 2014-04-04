package drankoDmitry.learningcards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Deck {
	
	private Context context;
	private Random random = new Random();
	private LinkedList<Card> base;
	private LinkedList<Card> randomized;
	public static enum OrderType {BY_ID,PURE_RANDOM,RANDOM_BY_QUALITY};
	private OrderType order = OrderType.PURE_RANDOM; //TODO temp
	private String deckTag;
	
	public Deck(String tag, Context ctx) {
		context = ctx;
		deckTag = tag;
		base = CardsDatabase.readCards(tag, ctx);
		shuffle();
	}
	
	
	public void shuffle() {
		randomized = new LinkedList<Deck.Card>(base);
		switch (order) {
		case PURE_RANDOM :
			shufflePureRandom();
			break;
		default :
			//TODO
			break;
		}
		
	}


	private void shufflePureRandom() {
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

	public static class Card {
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
	}

	public boolean isEmpty() {
		if (base.size()>0)
			return false;
		else 
			return true;
	}
}
