package drankoDmitry.learningcards;

import java.util.ArrayList;

import drankoDmitry.learningcards.Deck.Card;
import drankoDmitry.learningcards.Deck.OrderType;
import drankoDmitry.learningcards.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	
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
	private boolean invert;
	private OrderType order;
	private String dbgTag = "dbgTag";
	private Deck deck;
	private Card currentCard;
	private EditText d_word;
	private EditText d_translation;
	private EditText d_tag;
	private EditText d_quality;
	private RadioGroup rg;
	private CheckBox cb;
	SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		settings = getSharedPreferences("preferences", 0);
		currentTag=settings.getString("currentTag", null);
		invert = settings.getBoolean("invert", false);
		order = OrderType.valueOf(settings.getString("order", OrderType.PURE_RANDOM.toString()));
		Log.d("order",order.name());

	cardView = findViewById(R.id.cardView);
	tvWord = (TextView) findViewById(R.id.textWord);
	tvTranslation = (TextView) findViewById(R.id.textTranslation);
	
	
	ibEdit = (ImageButton) findViewById(R.id.imageButtonEdit);
	ibEdit.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		    LayoutInflater inflater = MainActivity.this.getLayoutInflater();
		    View view = inflater.inflate(R.layout.edit_card_dialog, null);
		    d_word = (EditText) view.findViewById(R.id.word);
		    d_word.setText(currentCard.word);
			d_translation = (EditText) view.findViewById(R.id.translation);
			d_translation.setText(currentCard.translation);
			d_tag = (EditText) view.findViewById(R.id.tag);
			d_tag.setText(currentCard.tag);
			d_quality = (EditText) view.findViewById(R.id.quality);
			d_quality.setText(""+currentCard.quality);
		    builder.setView(view)
		           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		            	   Log.d("dialog","ok");
		            	   deck.editCard(currentCard.id, d_word.getText().toString(), d_translation.getText().toString(), d_tag.getText().toString(), Integer.parseInt(d_quality.getText().toString()));
		            	   refreshCurrentCard();
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
	ibManageDeck.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		    LayoutInflater inflater = MainActivity.this.getLayoutInflater();
		    View view = inflater.inflate(R.layout.manage_dialog, null);
		    rg = (RadioGroup) view.findViewById(R.id.radioGroup1);
		    switch (order) {
		    	case RANDOM_BY_QUALITY:	rg.check(R.id.radio0); break;
		    	case PURE_RANDOM:	rg.check(R.id.radio1); break;
		    	case BY_ID:	rg.check(R.id.radio2); break;
		    	default: break;
		    }
		    
		    cb = (CheckBox) view.findViewById(R.id.checkBox_invert);
		    cb.setChecked(invert);
		    builder.setView(view)
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   Log.d("dialog","ok");
	            	   boolean newInvert = cb.isChecked();
	            	   if (newInvert!=invert) {
	            		   invert=newInvert;
	            		   refreshCurrentCard();
	            	   }
	            	   OrderType newOrder;
	            	   switch (rg.getCheckedRadioButtonId()) {
	   		    			case R.id.radio0:	newOrder = OrderType.RANDOM_BY_QUALITY; break;
	   		    			case R.id.radio1:	newOrder = OrderType.PURE_RANDOM; break;
	   		    			case R.id.radio2:	newOrder = OrderType.BY_ID; break;
	   		    			default: newOrder = null;
	            	   }
	            	   Log.d("check",""+rg.getCheckedRadioButtonId());
	            	   if (newOrder!=order) {
	            		   order = newOrder;
	            		   Log.d("order", order.name());
	            		   deck = new Deck(currentTag, order, MainActivity.this);
	            		   showCard();
	            	   }
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
	
	spinnerTag = (Spinner) findViewById(R.id.spinnerSetTag);
	dbTags = CardsDatabase.readTags(this);	
	ArrayAdapter<CharSequence> adapterT = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
	adapterT.add("all cards");
	for (String t:dbTags) {
		Log.d(dbgTag, t);
		adapterT.add(t);
	}
	adapterT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinnerTag.setAdapter(adapterT);
	spinnerTag.setSelection(adapterT.getPosition(currentTag));
	spinnerTag.setOnItemSelectedListener(new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			String newT = null;
			if (arg2!=0) {
				 newT = arg0.getSelectedItem().toString();
			}
			if (newT!=currentTag) {
				currentTag = newT;	
				deck = new Deck(newT, order, MainActivity.this);
	        	showCard();
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
						
						if (!invert) {
							tvTranslation.setText(currentCard.translation);
						} else {
							tvTranslation.setText(currentCard.word);
						}
						return true;
					}
					
				});
		
		
		//TODO???
		if (deck==null) 
			deck = new Deck(currentTag, order, this);
		
		showCard();
		
		
		
		
		
	}
	
	/////////////////////////////////////////
	/////////////////////////////////////////
	//onCreate end
	/////////////////////////////////////////
	/////////////////////////////////////////
	
	@Override
	public void onStop() {
		SharedPreferences.Editor editor = settings.edit();
	      editor.putBoolean("invert", invert);
	      editor.putString("currentTag", currentTag);
	      editor.putString("order", order.toString());
	      editor.commit();
		super.onStop();
	}
	
	
	
	private void showCard() {
		currentCard=deck.getCard();
		refreshCurrentCard();
	}
	
	private void refreshCurrentCard() {
		if (invert) {
			tvWord.setText(currentCard.translation);
		} else {
			tvWord.setText(currentCard.word);
		}
		tvQuality.setText(""+currentCard.quality);
		tvTranslation.setText("");
	}

//TODO ?
	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(dbgTag, "on touch");
		if (!deck.isEmpty()) {
			return mGestureDetector.onTouchEvent(event);
		} else {
			Intent intent = new Intent(MainActivity.this,MainMenuActivity.class);
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
	        	Intent intent1 = new Intent(MainActivity.this,MainMenuActivity.class);
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
	
	

	
	
}
