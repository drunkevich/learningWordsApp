package drankoDmitry.learningcards;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

	private static CardsDatabase mDbHelper;
	private static SQLiteDatabase db;
	
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
	
	
	public static void initialize(Context c) {
		if (db==null) {
			mDbHelper = new CardsDatabase(c);
			db = mDbHelper.getWritableDatabase();
		}
	}

	public static void closedb() {
		
		db.close();
		
	}
	
	public static Cursor readCards(String currentTag2, int currentQ2) {
		if ((currentTag2==null) && (currentQ2==0)){
		return db.query(CardsDatabase.TABLE_NAME,
				CardsDatabase.columns, null, new String[] {}, null, null, null);
		} else if ((currentTag2!=null) && (currentQ2==0)) {
			return db.query(CardsDatabase.TABLE_NAME,
					CardsDatabase.columns, CardsDatabase.TAG+" = "+currentTag2, new String[] {}, null, null, null);

		} else if ((currentTag2==null) && (currentQ2!=0)) {
			return db.query(CardsDatabase.TABLE_NAME,
					CardsDatabase.columns, CardsDatabase.QUALITY+" = "+currentQ2, new String[] {}, null, null, null);

		} else {
			return db.query(CardsDatabase.TABLE_NAME,
					CardsDatabase.columns, CardsDatabase.TAG+" = "+currentTag2+" and "+CardsDatabase.QUALITY+" = "+currentQ2, new String[] {}, null, null, null);

		}
	}
	
	public static ArrayList<String> readTags() {
		ArrayList<String> result = new ArrayList<String>();
		Cursor c = db.rawQuery("SELECT DISTINCT "+CardsDatabase.TAG+" from "+CardsDatabase.TABLE_NAME, null);
		
		if (c.getCount()>0) {
			c.moveToFirst();
			result.add(c.getString(0));
			while (c.moveToNext()) {
				result.add(c.getString(0));	
			}
		return result;
		}
		else return null;
	}
	
	public static void insert(ContentValues values) {
		 db.insert(CardsDatabase.TABLE_NAME, null, values);
	}
	
	public static void deletedb() {
		db.delete(CardsDatabase.TABLE_NAME, null, null);
	}

	public static void deleteCard(String string) {
		// TODO Auto-generated method stub
		//db.delete
	}
}
