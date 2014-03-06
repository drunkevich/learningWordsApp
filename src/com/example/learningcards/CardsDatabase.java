package com.example.learningcards;

import android.content.Context;
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
	
}
