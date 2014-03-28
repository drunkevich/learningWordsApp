package drankoDmitry.learningcards;

import java.util.ArrayList;
import java.util.Random;

import drankoDmitry.learningcards.R;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {

	
	private Cursor cursor;
	private Random random = new Random();
	private ViewFlipper mSwitcher;
	private TextView[] mTextView = new TextView[2];
	private GestureDetector mGestureDetector;
	private boolean face = true;
	private ArrayList<String> dbTags = new ArrayList<String>();
	private String currentTag;
	private int currentQ=0;
	private Spinner spinnerQuality;
	private Spinner spinnerTag;
	private String dbgTag = "dbgTag";
	private int SELECT_DECK_REQUEST_CODE = 1;
	private int ADD_CARDS = 2;
	private static int RANDOM_CARD = -1; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		mSwitcher = (ViewFlipper) findViewById(R.id.viewSwitcher1);
		mTextView[0] = (TextView) findViewById(R.id.textView1);
		mTextView[1] = (TextView) findViewById(R.id.textView2);
		
		
		
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						

						mSwitcher.setOutAnimation(outToLeftAnimation());
						
						cursor.moveToPosition(random.nextInt(cursor.getCount()));
						//MainActivity.this.debuglog(cursor);
						spinnerQuality.setSelection(cursor.getInt(4)-1);
						spinnerTag.setSelection(dbTags.indexOf(cursor.getString(3)));
						mTextView[1-mSwitcher.getDisplayedChild()].setText(cursor.getString(1));
						mSwitcher.getChildAt(1-mSwitcher.getDisplayedChild()).setBackgroundColor(getResources().getColor(R.color.bcgrd_word));
						mSwitcher.showPrevious();
						
						return true;
					}
					
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}
					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						debuglog(cursor);
						mSwitcher.setInAnimation(inFromRightAnimation());
						mSwitcher.setOutAnimation(outToLeftAnimation());
						if (face) {
							mTextView[1-mSwitcher.getDisplayedChild()].setText(cursor.getString(2));
							mSwitcher.getChildAt(1-mSwitcher.getDisplayedChild()).setBackgroundColor(getResources().getColor(R.color.bcgrd_translation));
						} else {
							mTextView[1-mSwitcher.getDisplayedChild()].setText(cursor.getString(1));
							mSwitcher.getChildAt(1-mSwitcher.getDisplayedChild()).setBackgroundColor(getResources().getColor(R.color.bcgrd_word));
						}
						mSwitcher.showPrevious();
						
						return true;
					}
					
				});
		
		
		
		
		
		
		
		
		
		
		
		
		spinnerTag = (Spinner) findViewById(R.id.spinner1);
		spinnerTag.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final String newT = arg0.getSelectedItem().toString();
				String oldT = cursor.getString(3);
				if (!newT.equals(oldT)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					//TODO hard code
					builder.setMessage("change deck with tag "+oldT+" to "+newT+"?").setTitle(R.string.selectDeck);			
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   //TODO change deck
			           }

					
					});
					builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   
			           }
					});
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
	}
	
	/////////////////////////////////////////
	/////////////////////////////////////////
	//onCreate end
	/////////////////////////////////////////
	/////////////////////////////////////////
	
	
	
	

	@Override
	protected void onStart() {
		Log.d(dbgTag, "onStart");
		//TODO
		super.onStart();
	}
	
		
	private void debuglog(Cursor c) {
		
		String msg = c.getString(0)+" "+c.getString(1)+" "+c.getString(2)+" "+c.getString(3)+" "+c.getString(4);
		Log.d("current card log", msg);
		
	}
//TODO ?
	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(dbgTag, "on touch");
		if (cursor.getCount()>0) {
			return mGestureDetector.onTouchEvent(event);
		} else {
			Intent intent = new Intent(MainActivity.this,CardEditActivity.class);
			intent.putExtra("force alert", false); //TODO ???
			startActivity(intent);	  
			return false;
		}
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
	        case R.id.editCards:
	        	Intent intent1 = new Intent(MainActivity.this,CardEditActivity.class);
				intent1.putExtra("force alert", false);
				startActivity(intent1);	            
	            return true;
	       
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	private Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(300);
		inFromRight.setInterpolator(new LinearInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(300);
		outtoLeft.setInterpolator(new LinearInterpolator());
		return outtoLeft;
	}
	
	protected void onSaveInstanceState(Bundle outState) {
	    outState.putString("currentTag", currentTag);
	    outState.putInt("currentQ", currentQ);
	    super.onSaveInstanceState(outState);
	  }
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    currentTag = savedInstanceState.getString("currentTag");
	    currentQ = savedInstanceState.getInt("currentQ");
	    super.onRestoreInstanceState(savedInstanceState);
	  }
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_CARDS) {
			if (resultCode == RESULT_CANCELED){
				//if () {
				//	finish();
				//}
			}
		}

	}
	
}
