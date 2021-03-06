package drankoDmitry.learningcards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardsDatabase extends SQLiteOpenHelper {
	
	public final static String DEFAULT_TAG = "default";
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

    public static LinkedList<Card> readCards(String tag, Context ctx) {
        String query;
        if (tag == null) {
            query = "SELECT * from " + CardsDatabase.TABLE_NAME;
        } else {
            query = "SELECT * from " + CardsDatabase.TABLE_NAME + " WHERE " + CardsDatabase.TAG + " = '" + tag + "'";
        }
        Log.d("sql query", query);
        CardsDatabase helper = new CardsDatabase(ctx);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        Log.d("sql query", "query complite");
        LinkedList<Card> base = new LinkedList<Card>();
        if (!c.moveToFirst()) {
            //TODO empty cursor
            base.add(Card.emptyCard);
        } else {
            base.add(new Card(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4)));
            while (c.moveToNext()) {
                base.add(new Card(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4)));
            }
        }
        c.close();
        db.close();
        return base;
    }

    public static ArrayList<String> readTags(Context ctx) {
        ArrayList<String> result = new ArrayList<String>();
        String query = "SELECT DISTINCT " + CardsDatabase.TAG + " from " + CardsDatabase.TABLE_NAME;
        Log.d("sql query", query);
        CardsDatabase helper = new CardsDatabase(ctx);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Log.d("getting tags", "" + c.getCount());
        if (c.getCount() > 0) {
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

    public static void insertCards(List<ContentValues> list, Context ctx) {
        CardsDatabase helper = new CardsDatabase(ctx);
        SQLiteDatabase db = helper.getWritableDatabase();

        for (ContentValues values : list) {
            db.insert(CardsDatabase.TABLE_NAME, null, values);
            values.clear();
        }

        db.close();
    }

    public static void deletedb(Context ctx) {
        CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(CardsDatabase.TABLE_NAME, null, null);
        db.close();
	}

    public static void deleteCard(int id, Context ctx) {
        String query = "DELETE FROM " + CardsDatabase.TABLE_NAME + " WHERE " + CardsDatabase._ID + " = " + id;
        Log.d("sql query", query);
        CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getWritableDatabase();
        db.rawQuery(query, null);
        db.close();
	}

    public static void updateCard(int id, ContentValues updatedValues, Context ctx) {
        CardsDatabase helper = new CardsDatabase(ctx);
        SQLiteDatabase db = helper.getWritableDatabase();

        db.update(CardsDatabase.TABLE_NAME, updatedValues, CardsDatabase._ID + " = " + id, null);
        db.close();
        updatedValues.clear();
    }

    public static void readFile(String path, Context ctx, String tag, String lang1, String lang2) {  //TODO contracts?
        LinkedList<ContentValues> valuesList = new LinkedList<ContentValues>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String str;
            while ((str = br.readLine()) != null) {


                String[] s = str.split(";");
                int l = s.length;
                for (int i = 0; i < l; i++) {
                    s[i] = s[i].trim();
                }
                if ((l > 0) && (s[0].length() > 0)) {
                    ContentValues values = new ContentValues();
                    values.put(CardsDatabase.WORD, s[0]);
                    if ((l > 1) && (s[1].length() > 0)) values.put(CardsDatabase.TRANSLATION, s[1]);

                    if (tag != null) {
                        values.put(CardsDatabase.TAG, tag);
                    } else {
                        if ((l > 2) && (s[2].length() > 0))
                            values.put(CardsDatabase.TAG, s[2]);
                        else
                            values.put(CardsDatabase.TAG, CardsDatabase.DEFAULT_TAG);
                    }

                    if ((l > 3) && (s[3].length() > 0))
                        try {
                            int q = Integer.parseInt(s[3]);
                            values.put(CardsDatabase.QUALITY, q);
                        } catch (Exception e) {
                            values.put(CardsDatabase.QUALITY, Card.defaultQ);
                        }
                    else
                        values.put(CardsDatabase.QUALITY, Card.defaultQ);
                    valuesList.add(values);
                }

            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lang1 == null) {
            CardsDatabase helper = new CardsDatabase(ctx);
            SQLiteDatabase db = helper.getWritableDatabase();
            for (ContentValues values : valuesList) {
                db.insert(CardsDatabase.TABLE_NAME, null, values);
                values.clear();
            }
            db.close();
        } else {
            new YandexTranslateHelper().translateAndAdd(valuesList, lang1, lang2, ctx);
        }
    }

    public static void save(LinkedList<Card> base, Context ctx) {
        CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getWritableDatabase();
        for (Card card : base) {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put(CardsDatabase.WORD, card.getWord());
            updatedValues.put(CardsDatabase.TRANSLATION, card.getTranslation());
            updatedValues.put(CardsDatabase.TAG, card.getTag());
            updatedValues.put(CardsDatabase.QUALITY, card.getQuality());
            db.update(CardsDatabase.TABLE_NAME, updatedValues, CardsDatabase._ID + " = " + card.getId(), null);
            updatedValues.clear();
        }
        db.close();

    }

    public static void clearQualities(Context ctx) {
        CardsDatabase helper = new CardsDatabase(ctx);
		SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(CardsDatabase.QUALITY, Card.defaultQ);
        db.update(CardsDatabase.TABLE_NAME, updatedValues, null, null);
        updatedValues.clear();
        db.close();
    }

    public static void savedb(Context context) {
        File out = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "learning_cards_backup.txt");
        try {
            FileOutputStream stream = new FileOutputStream(out);

            CardsDatabase helper = new CardsDatabase(context);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * from " + CardsDatabase.TABLE_NAME, null);
            c.moveToFirst();
            stream.write((c.getInt(0) + ";" + c.getString(1) + ";" + c.getString(2) + ";" + c.getString(3) + ";" + c.getInt(4) + "\n").getBytes());
            while (c.moveToNext()) {
                stream.write((c.getInt(0) + ";" + c.getString(1) + ";" + c.getString(2) + ";" + c.getString(3) + ";" + c.getInt(4) + "\n").getBytes());
            }
            c.close();
            db.close();
            stream.close();
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
	}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }


}
