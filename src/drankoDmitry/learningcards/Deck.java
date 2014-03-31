package drankoDmitry.learningcards;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;

public class Deck {
	
	private Random random = new Random();
	private ArrayList<Card> base;
	private ArrayList<Card> randomized;
	public static enum OrderType {BY_ID,PURE_RANDOM,RANDOM_BY_QUALITY};
	private OrderType order = OrderType.PURE_RANDOM; //TODO temp
	private String deckTag;
	
	public Deck(String tag, Context ctx) {
		deckTag = tag;
		Cursor c = CardsDatabase.readCards(tag, ctx);
		if (c.moveToFirst()!=false) {
			//TODO empty cursor
		}
		base = new ArrayList<Deck.Card>();
		base.add(new Card(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4)));
		while (c.moveToNext()!=false) {
			base.add(new Card(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4)));
		}
		c.close();
		shaffle();
	}
	
	
	public void shaffle() {
		randomized = new ArrayList<Deck.Card>();
		switch (order) {
		case PURE_RANDOM :
			shafflePureRandom();
			break;
		default :
			//TODO
			break;
		}
		
	}


	private void shafflePureRandom() {
		for (Card card : base) {
				randomized.add(random.nextInt(randomized.size()+1), card);
		}
		
	}


	public class Card {
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
}
