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
	private ViewFlipper mFlipper;
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
		
		
		mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		mTextView[0] = (TextView) findViewById(R.id.textView1);
		mTextView[1] = (TextView) findViewById(R.id.textView2);
		mFlipper.setBackgroundColor(getResources().getColor(R.color.bcgrd_word));
		
		
		
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						

						mFlipper.setOutAnimation(outToLeftAnimation());
						
						cursor.moveToPosition(random.nextInt(cursor.getCount()));
						//MainActivity.this.debuglog(cursor);
						spinnerQuality.setSelection(cursor.getInt(4)-1);
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
					
				});
		
		
		
		
		
		
		
		
		
		
		spinnerQuality = (Spinner) findViewById(R.id.spinner_quality);
		ArrayAdapter<CharSequence> adapterQ = ArrayAdapter.createFromResource(this,
		        R.array.Qualities, android.R.layout.simple_spinner_item);
		adapterQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerQuality.setAdapter(adapterQ);
		spinnerQuality.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				final String newQ = arg0.getSelectedItem().toString();
				String oldQ = cursor.getString(4);
				if (!newQ.equals(oldQ)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					//TODO hard code
					builder.setMessage("change card quality from "+oldQ+" to "+newQ+"?").setTitle(R.string.card_edition);			
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   editCardLocally(CardsDatabase.QUALITY,newQ);
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
		
		
		spinnerTag = (Spinner) findViewById(R.id.spinner_tag);
		spinnerTag.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final String newT = arg0.getSelectedItem().toString();
				String oldT = cursor.getString(3);
				if (!newT.equals(oldT)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					//TODO hard code
					builder.setMessage("change card tag from "+oldT+" to "+newT+"?").setTitle(R.string.card_edition);			
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   editCardLocally(CardsDatabase.TAG,newT);
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
	
	
	
	
	private void editCardLocally(String column, String newValue) {
		int id = cursor.getInt(0);
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(column, newValue);
		CardsDatabase.updateCard(id, updatedValues);
		refreshVisibleData();
		showCard(id);
	}
	
	private void showCard(int id) {
		Log.d(dbgTag, "showCard "+id);
		if (cursor.getCount()>0) {
			cursor.moveToFirst();
			if (id!=RANDOM_CARD) {
				while ((cursor.getInt(0)!=id)&&(!cursor.isAfterLast())) {
					cursor.moveToNext();
				}
				if (cursor.isAfterLast())
					cursor.moveToPosition(random.nextInt(cursor.getCount()));
			} else {
				cursor.moveToPosition(random.nextInt(cursor.getCount()));
			}
			debuglog(cursor);
			mTextView[0].setText(cursor.getString(1));
			spinnerQuality.setSelection(cursor.getInt(4)-1);
			spinnerTag.setSelection(dbTags.indexOf(cursor.getString(3)));
			
		} else {
		Log.d(dbgTag, "no cards");
		}
	}

	@Override
	protected void onStart() {
		Log.d(dbgTag, "onStart");
		CardsDatabase.initialize(this);
		refreshVisibleData();
		showCard(RANDOM_CARD);
		super.onStart();
	}
	
		
	private void debuglog(Cursor c) {
		
		String msg = c.getString(0)+" "+c.getString(1)+" "+c.getString(2)+" "+c.getString(3)+" "+c.getString(4);
		Log.d("current card log", msg);
		
	}

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
	        case R.id.select:
	    		Intent intent2 = new Intent(this,DeckChooser.class);
	    		startActivityForResult(intent2, SELECT_DECK_REQUEST_CODE);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
		
	
	
	
	@Override
	protected void onStop() {

		CardsDatabase.closedb();
		super.onStop();

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
	
	private void refreshVisibleData() {
		
			cursor = CardsDatabase.readCards(currentTag,currentQ);
			if (cursor.getCount()>0) {
				dbTags = CardsDatabase.readTags();			
				ArrayAdapter<CharSequence> adapterT = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
			
				for (String t:dbTags) {
					adapterT.add(t);
				}
				adapterT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerTag.setAdapter(adapterT);
			} else {
				Log.d(dbgTag, "no cards selected");
				if (CardsDatabase.readCards(null,0).getCount()>0) {
					            
					AlertDialog.Builder builder = new AlertDialog.Builder(this);				
					builder.setMessage(R.string.dialog_empty_deck).setTitle(R.string.dialog_empty_deck_title);			
					builder.setNeutralButton(R.string.add_cards, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   Intent intent = new Intent(MainActivity.this,CardEditActivity.class);
								intent.putExtra("force alert", false);
								startActivity(intent);
				           }
				       });
				builder.setNeutralButton(R.string.select_deck, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   Intent intent2 = new Intent(MainActivity.this,DeckChooser.class);
				        	   startActivityForResult(intent2, SELECT_DECK_REQUEST_CODE);
				           }
				       });
					AlertDialog dialog = builder.create();
					dialog.show();
					
				} else {
					Intent intent = new Intent(MainActivity.this,CardEditActivity.class);
					intent.putExtra("force alert", true);
					startActivityForResult(intent,ADD_CARDS);	
				}
			}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		
		if (requestCode == SELECT_DECK_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.d("dbg","into activity result");
				currentTag=data.getStringExtra("tag");
				currentQ=data.getIntExtra("quality", 0);
				CardsDatabase.initialize(this);
				cursor = CardsDatabase.readCards(currentTag, currentQ);
				Log.d(dbgTag, ""+cursor.getCount());
					refreshVisibleData();
					showCard(RANDOM_CARD);
			}
		} else if (requestCode == ADD_CARDS) {
			if (resultCode == RESULT_CANCELED) {
				CardsDatabase.initialize(this);
				if (CardsDatabase.readCards(null,0).getCount()==0) {
					finish();
				}
			}
		}

	}
	
}
