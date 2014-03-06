package com.example.learningcards;

import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDbHelper = new CardsDatabase(this);
		db = mDbHelper.getReadableDatabase();
		mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		mTextView[0] = (TextView) findViewById(R.id.textView1);
		mTextView[1] = (TextView) findViewById(R.id.textView2);
		mFlipper.setBackgroundColor(getResources().getColor(R.color.bcgrd_word));
		cursor = readCards();
		
		try {
			cursor.moveToPosition(random.nextInt(cursor.getCount()));
			mTextView[0].setText(cursor.getString(1));
		} catch (Exception e) {
			mTextView[0].setText("create cards first");
		}
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						
						mFlipper.setInAnimation(inFromRightAnimation());
						mFlipper.setOutAnimation(outToLeftAnimation());
						
						cursor.moveToPosition(random.nextInt(cursor.getCount()));
						
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
		
		//TODO
		Spinner spinner = (Spinner) findViewById(R.id.spinner_quality);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.Qualities, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
	}
	
	
	@Override 
	public boolean onTouchEvent(MotionEvent event) {
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

		super.onStop();

	}
	
	private Cursor readCards() {
		return db.query(CardsDatabase.TABLE_NAME,
				CardsDatabase.columns, null, new String[] {}, null, null, null);
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
}
