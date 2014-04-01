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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {

	
	private Random random = new Random();
	private View cardView;
	private TextView tvWord;
	private TextView tvTranslation;
	private TextView tvQuality;
	private ImageButton ibManageDeck;
	private ImageButton ibEdit;
	private Spinner spinnerTag;
	private GestureDetector mGestureDetector;
	private ArrayList<String> dbTags = new ArrayList<String>();
	private String currentTag;
	private String dbgTag = "dbgTag";
	private int SELECT_DECK_REQUEST_CODE = 1;
	private int ADD_CARDS = 2;
	private static int RANDOM_CARD = -1; 
	private Deck deck;
	private Deck.Card currentCard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

	cardView = findViewById(R.id.cardView);
	tvWord = (TextView) findViewById(R.id.textWord);
	tvTranslation = (TextView) findViewById(R.id.textTranslation);
	
	
	ibEdit = (ImageButton) findViewById(R.id.imageButtonEdit);
	ibEdit.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		    LayoutInflater inflater = MainActivity.this.getLayoutInflater();
		    builder.setView(inflater.inflate(R.layout.activity_adding_card_manually, null))
		    // Add action buttons
		           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		                   // TODO
		            	   Log.d("dialog","ok");
		               }
		           })
		           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		                   Log.d("dialog","cancel");
		               }
		           });      
		    builder.create().show();
		}
	});
	
	
	tvQuality = (TextView) findViewById(R.id.textQuality);
	tvQuality.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.d("ui", "onClick at quality");
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		    builder.setTitle(R.string.changeQuality)
		           .setItems(R.array.Qualities, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int n) {
		               currentCard.quality = n+1;
		               tvQuality.setText(""+currentCard.quality);
		               ContentValues cv = new ContentValues();
		               cv.put(CardsDatabase.QUALITY, n+1);
		               CardsDatabase.updateCard(currentCard.id, cv, MainActivity.this);
		           }
		    });
		    builder.create().show();
		}
	});
	
	
	ibManageDeck = (ImageButton) findViewById(R.id.imageButtonManageDeck);
	//TODO button
	
	
	spinnerTag = (Spinner) findViewById(R.id.spinnerSetTag);
	spinnerTag.setOnItemSelectedListener(new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			final String newT = arg0.getSelectedItem().toString();
			if (!newT.equals(currentTag)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				//TODO hard code
				builder.setMessage("select deck with tag "+newT+"?").setTitle(R.string.selectDeck);			
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   currentTag = newT;
		        	   deck = new Deck(newT, MainActivity.this);
		        	   showCard();
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
	
	
	
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						
						showCard();
						
						return true;
					}
					
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}
					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						
						tvTranslation.setText(currentCard.translation);
						
						return true;
					}
					
				});
		
		
		
	}
	
	/////////////////////////////////////////
	/////////////////////////////////////////
	//onCreate end
	/////////////////////////////////////////
	/////////////////////////////////////////
	
	private void showCard() {
		currentCard=deck.getCard();
		tvWord.setText(currentCard.word);
		tvQuality.setText(""+currentCard.quality);
		tvTranslation.setText("");
	}
	
	

	@Override
	protected void onStart() {
		Log.d(dbgTag, "onStart");
		
		
		dbTags = CardsDatabase.readTags(this);	
		ArrayAdapter<CharSequence> adapterT = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);

		for (String t:dbTags) {
			Log.d(dbgTag, t);
			adapterT.add(t);
		}
		adapterT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTag.setAdapter(adapterT);
		
		
		if (deck==null) 
			deck = new Deck(currentTag, this);
		
		showCard();
		
		super.onStart();
	}
	
//TODO ?
	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(dbgTag, "on touch");
		if (!deck.isEmpty()) {
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
	//TODO saved instance
	protected void onSaveInstanceState(Bundle outState) {
	    outState.putString("currentTag", currentTag);
	    super.onSaveInstanceState(outState);
	  }
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    currentTag = savedInstanceState.getString("currentTag");
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
