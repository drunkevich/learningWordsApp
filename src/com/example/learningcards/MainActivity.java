package com.example.learningcards;

import java.util.ArrayList;
import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {

	private SQLiteDatabase db;
	private CardsDatabase mDbHelper;
	private Cursor cursor;
	private Random random = new Random();
	private ViewFlipper mFlipper;
	private TextView[] mTextView = new TextView[2];
	private GestureDetector mGestureDetector;
	private boolean face = true;
	private ArrayList<String> dbTags = new ArrayList<String>();
	private String currentTag;
	private int currentQ=0;
	private boolean isRefreshNeed;
	private Spinner spinnerQuality;
	private Spinner spinnerTag;
	private String dbgTag = "dbgTag";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDbHelper = new CardsDatabase(this);
		db = mDbHelper.getWritableDatabase();
		mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		mTextView[0] = (TextView) findViewById(R.id.textView1);
		mTextView[1] = (TextView) findViewById(R.id.textView2);
		mFlipper.setBackgroundColor(getResources().getColor(R.color.bcgrd_word));
		
		isRefreshNeed = true;
		
		
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						

						mFlipper.setOutAnimation(outToLeftAnimation());
						
						cursor.moveToPosition(random.nextInt(cursor.getCount()));
						MainActivity.this.debuglog(cursor);
						spinnerQuality.setSelection(Integer.parseInt(cursor.getString(4))-1);
						spinnerTag.setSelection(dbTags.indexOf(cursor.getString(3)));
						mTextView[1-mFlipper.getDisplayedChild()].setText(cursor.getString(1));
						mFlipper.getChildAt(1-mFlipper.getDisplayedChild()).setBackgroundColor(getResources().getColor(R.color.bcgrd_word));
						mFlipper.showPrevious();
						
						return true;
					}
					
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}
					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						debuglog(cursor);
						mFlipper.setInAnimation(inFromRightAnimation());
						mFlipper.setOutAnimation(outToLeftAnimation());
						if (face) {
							mTextView[1-mFlipper.getDisplayedChild()].setText(cursor.getString(2));
							mFlipper.getChildAt(1-mFlipper.getDisplayedChild()).setBackgroundColor(getResources().getColor(R.color.bcgrd_translation));
						} else {
							mTextView[1-mFlipper.getDisplayedChild()].setText(cursor.getString(1));
							mFlipper.getChildAt(1-mFlipper.getDisplayedChild()).setBackgroundColor(getResources().getColor(R.color.bcgrd_word));
						}
						mFlipper.showPrevious();
						
						return true;
					}
					@Override
					public void onLongPress(MotionEvent e) {
						
					}
				});
		
		
		spinnerQuality = (Spinner) findViewById(R.id.spinner_quality);
		ArrayAdapter<CharSequence> adapterQ = ArrayAdapter.createFromResource(this,
		        R.array.Qualities, android.R.layout.simple_spinner_item);
		adapterQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerQuality.setAdapter(adapterQ);
		
		spinnerTag = (Spinner) findViewById(R.id.spinner_tag);
		
		
		
		
	}
	
	@Override
	protected void onStart() {
		refreshVisibleData(false);
		showCard();
		super.onStart();
	}
	
	private void showCard() {
		Log.d(dbgTag, "showCard()");
		if (cursor.getCount()>0) {
		cursor.moveToPosition(random.nextInt(cursor.getCount()));
		debuglog(cursor);
		mTextView[0].setText(cursor.getString(1));
		spinnerQuality.setSelection(Integer.parseInt(cursor.getString(4))-1);
		spinnerTag.setSelection(dbTags.indexOf(cursor.getString(3)));
	} else {
		//TODO
		Log.d(dbgTag, "no cards");
	}
		
	}

	private void debuglog(Cursor c) {
		
		String msg = c.getString(0)+" "+c.getString(1)+" "+c.getString(2)+" "+c.getString(3)+" "+c.getString(4);
		Log.d("current card log", msg);
		
	}

	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		//Log.d(dbgTag, "on touch");
		return mGestureDetector.onTouchEvent(event);
	}

 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		
	    switch (item.getItemId()) {
	        case R.id.item1:
	            startActivity(new Intent(MainActivity.this,CardEditActivity.class));	            
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
		
	
	
	
	@Override
	protected void onStop() {

		db.close();
//TODO ?
		super.onStop();

	}
	
	private Cursor readCards(String currentTag2, int currentQ2) {
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
	
	private Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(200);
		inFromRight.setInterpolator(new LinearInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(200);
		outtoLeft.setInterpolator(new LinearInterpolator());
		return outtoLeft;
	}
	
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString("currentTag", currentTag);
	    outState.putInt("currentQ", currentQ);
	  }
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    currentTag = savedInstanceState.getString("currentTag");
	    currentQ = savedInstanceState.getInt("currentQ");
	  }
	
	private void refreshVisibleData(boolean forced) {
		if (forced || isRefreshNeed) {
			cursor = readCards(currentTag,currentQ);
			dbTags = readTags();			
			ArrayAdapter<CharSequence> adapterT = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
			
			for (String t:dbTags) {
				adapterT.add(t);
			}
			adapterT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerTag.setAdapter(adapterT);
			isRefreshNeed = false;
		}
	}
//git test
	private ArrayList<String> readTags() {
		ArrayList<String> result = new ArrayList<String>();
		Cursor c = db.rawQuery("SELECT DISTINCT "+CardsDatabase.TAG+" from "+CardsDatabase.TABLE_NAME, null);
		c.moveToFirst();
		result.add(c.getString(0));
		while (c.moveToNext()) {
			result.add(c.getString(0));	
		}
		return result;
	}
	
}
