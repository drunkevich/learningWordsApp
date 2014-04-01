package drankoDmitry.learningcards;

import java.util.ArrayList;
import java.util.LinkedList;

import drankoDmitry.learningcards.Deck.Card;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CardsDatabase extends SQLiteOpenHelper {
	
	public final static String DEFAULT_TAG = "default";
	public final static int DEFAULT_QUALITY = 3;
	public final static String TABLE_NAME = "word_cards";
	public final static String WORD = "word";
	public final static String TRANSLATION = "translation";
	public final static String TAG = "tag";
	public final static String _ID = "_id";
	public final static String QUALITY = "quality";
	public final static String[] columns = { _ID, WORD, TRANSLATION, TAG, QUALITY};

	final private static String CREATE_CMD =

	"CREATE TABLE "+TABLE_NAME+" (" + _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ WORD + " TEXT NOT NULL, "+TRANSLATION+" TEXT NOT NULL, "+TAG+" TEXT, "+QUALITY+" INTEGER)";

	final private static String NAME = "word_cards_db";
	final private static Integer VERSION = 1;
	final private Context mContext;

	
	public CardsDatabase(Context context) {
		super(context, NAME, null, VERSION);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CMD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// N/A
	}
	
	void deleteDatabase() {
		mContext.deleteDatabase(NAME);
	}
	

	
	public static LinkedList<Card> readCards(String tag, Context ctx) {
			String query;
		if (tag==null){
			query = "SELECT * from "+CardsDatabase.TABLE_NAME;
		} else {
			query = "SELECT * from "+CardsDatabase.TABLE_NAME+" WHERE " +CardsDatabase.TAG+" = '"+tag+"'";
		}
		Log.d("sql query", query);
		CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c= db.rawQuery(query, null);
		Log.d("sql query", "query complite");
		LinkedList<Deck.Card> base = new LinkedList<Deck.Card>();
		if (c.moveToFirst()==false) {
			//TODO empty cursor
			base.add(new Card(0," "," "," ",0));
		} else {
			base.add(new Card(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4)));
			while (c.moveToNext()!=false) {
				base.add(new Card(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4)));
			}
		}
		c.close();
		db.close();
		return base;
	}
	
	public static ArrayList<String> readTags(Context ctx) {
		ArrayList<String> result = new ArrayList<String>();
		String query = "SELECT DISTINCT "+CardsDatabase.TAG+" from "+CardsDatabase.TABLE_NAME;
		Log.d("sql query", query);
		CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		Log.d("getting tags", ""+c.getCount());
		if (c.getCount()>0) {
			result.add(c.getString(0));
			while (c.moveToNext()) {
				result.add(c.getString(0));	
			}
		}
		c.close();
		db.close();
		return result;
		
	}
	
	public static void insertCard(ContentValues values, Context ctx) {
		CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.insert(CardsDatabase.TABLE_NAME, null, values);
		db.close();
		values.clear();
	}
	
	public static void deletedb(Context ctx) {
		CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(CardsDatabase.TABLE_NAME, null, null);
		db.close();
	}

	public static void deleteCard(int id, Context ctx) {
		String query = "DELETE FROM "+CardsDatabase.TABLE_NAME+" WHERE "+CardsDatabase._ID+" = "+id;
		Log.d("sql query", query);
		CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.rawQuery(query, null);
		db.close();
	}
	
	public static void updateCard(int id, ContentValues updatedValues ,Context ctx) {
		CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.update(CardsDatabase.TABLE_NAME, updatedValues, CardsDatabase._ID+" = "+id, null);
		db.close();
		updatedValues.clear();
	}
	
}
