package drankoDmitry.learningcards;

import java.util.ArrayList;

import drankoDmitry.learningcards.Deck.Card;
import drankoDmitry.learningcards.Deck.OrderType;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class CardsListActivity extends Activity {

	private Spinner spinnerTag;
	private ArrayList<String> dbTags = new ArrayList<String>();
	private String dbgTag = "dbgTag";
	private String currentTag;
	private ListView list;
	private Deck currentDeck;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cards_list);
		currentDeck = new Deck(currentTag, OrderType.BY_ID, CardsListActivity.this);
		list = (ListView) findViewById(R.id.listView);
		list.setAdapter(currentDeck);
		list.setOnItemClickListener(new OnItemClickListener() {

			private EditText d_word;
			private EditText d_translation;
			private EditText d_tag;
			private EditText d_quality;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final Card currentCard = (Card) currentDeck.getItem(arg2);
				AlertDialog.Builder builder = new AlertDialog.Builder(CardsListActivity.this);
			    LayoutInflater inflater = CardsListActivity.this.getLayoutInflater();
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
			            	   currentDeck.editCard(currentCard.id, d_word.getText().toString(), d_translation.getText().toString(), d_tag.getText().toString(), Integer.parseInt(d_quality.getText().toString()));
			            	   if (!d_tag.equals(currentCard.tag)) {
				            		  refreshTags();
				            		  currentDeck = new Deck(currentTag, OrderType.BY_ID, CardsListActivity.this);
				            	  }
			            	   list.setAdapter(currentDeck);
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
		
		spinnerTag = (Spinner) findViewById(R.id.spinnerSetListTag);
		refreshTags();
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
					currentDeck = new Deck(currentTag, OrderType.BY_ID, CardsListActivity.this);
					list.setAdapter(currentDeck);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		
	
		
	}

	private void refreshTags() {
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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cards_list, menu);
		return true;
	}

}
