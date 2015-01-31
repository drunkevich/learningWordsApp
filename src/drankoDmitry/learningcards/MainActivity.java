package drankoDmitry.learningcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import drankoDmitry.learningcards.Deck.OrderType;

public class MainActivity extends Activity {

	
	private TextView tvWord;
	private TextView tvTranslation;
	private ImageButton ibManageDeck;
	private Button buttonCorrect;
	private Button buttonIncorrect;
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
	private SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		settings = getSharedPreferences("preferences", 0);
		currentTag=settings.getString("currentTag", null);
		invert = settings.getBoolean("invert", false);
		order = OrderType.valueOf(settings.getString("order", OrderType.PURE_RANDOM.toString()));
		Log.d("order",order.name());

	tvWord = (TextView) findViewById(R.id.textWord);
	tvTranslation = (TextView) findViewById(R.id.textTranslation);
	
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
	            		   deck.save();
	            		   deck = new Deck(currentTag, order, MainActivity.this);
	            		   showNextCard();
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
	refreshTags();
	spinnerTag.setOnItemSelectedListener(new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			String newT = null;
			if (arg2!=0) {
                newT = arg0.getSelectedItem().toString();
                if (!newT.equals(currentTag)) {
                    currentTag = newT;
                    deck.save();
                    deck = new Deck(newT, order, MainActivity.this);
                    showNextCard();
                }
            }
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	});
	
	buttonCorrect = (Button) findViewById(R.id.button_correct);
	buttonCorrect.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
            currentCard.setBetterQuality(MainActivity.this);
            showNextCard();
		}
	});
	buttonIncorrect = (Button) findViewById(R.id.button_incorrect);
	buttonIncorrect.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
            currentCard.setWorseQuality(MainActivity.this);
            showNextCard();
		}
	});
	
	mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}
					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						
						if (!invert) {
                            tvTranslation.setText(currentCard.getTranslation());
                        } else {
                            tvTranslation.setText(currentCard.getWord());
                        }
						return true;
					}
					@Override
					public void onLongPress(MotionEvent e) {
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					    LayoutInflater inflater = MainActivity.this.getLayoutInflater();
					    View view = inflater.inflate(R.layout.edit_card_dialog, null);
					    d_word = (EditText) view.findViewById(R.id.word);
                        d_word.setText(currentCard.getWord());
                        d_translation = (EditText) view.findViewById(R.id.translation);
                        d_translation.setText(currentCard.getTranslation());
                        d_tag = (EditText) view.findViewById(R.id.tag);
                        d_tag.setText(currentCard.getTag());
                        d_quality = (EditText) view.findViewById(R.id.quality);
                        d_quality.setText("" + currentCard.getQuality());
                        builder.setView(view)
					           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					               @Override
					               public void onClick(DialogInterface dialog, int id) {
					            	   Log.d("dialog","ok");
                                       currentCard.editCard(d_word.getText().toString(), d_translation.getText().toString(), d_tag.getText().toString(), Integer.parseInt(d_quality.getText().toString()), MainActivity.this);
                                       if (d_tag.getText().toString().equals(currentCard.getTag())) {
                                           refreshCurrentCard();
					            	  } else {
					            		  refreshTags();
					            		  deck.save();
					            		  deck = new Deck(currentTag, order, MainActivity.this);
					            		  showNextCard();
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
		
		if (deck==null) {
			deck = new Deck(currentTag, order, this);
		}
			
		showNextCard();
		
	}
	
	/////////////////////////////////////////
	/////////////////////////////////////////
	//onCreate end
	/////////////////////////////////////////
	/////////////////////////////////////////
	
	private void refreshTags() {
		dbTags = CardsDatabase.readTags(this);	
		ArrayAdapter<CharSequence> adapterT = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		adapterT.add(getResources().getText(R.string.all_tags));
		for (String t:dbTags) {
			Log.d(dbgTag, t);
			adapterT.add(t);
		}
		adapterT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTag.setAdapter(adapterT);
		spinnerTag.setSelection(adapterT.getPosition(currentTag));
	}

	@Override
	public void onStop() {
		deck.save();
		SharedPreferences.Editor editor = settings.edit();
	      editor.putBoolean("invert", invert);
	      editor.putString("currentTag", currentTag);
	      editor.putString("order", order.toString());
	      editor.commit();
		super.onStop();
	}
	
	
	
	private void showNextCard() {
		currentCard=deck.getCard();
		refreshCurrentCard();
	}
	
	
	private void refreshCurrentCard() {
		if (invert) {
            tvWord.setText(currentCard.getTranslation());
        } else {
            tvWord.setText(currentCard.getWord());
        }
		tvTranslation.setText("");
	}

	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(dbgTag, "on touch");
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
		return true;
	    // Handle item selection
		
	}
	
}
