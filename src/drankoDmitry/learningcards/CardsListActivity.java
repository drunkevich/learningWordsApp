package drankoDmitry.learningcards;

import java.util.ArrayList;

import drankoDmitry.learningcards.Deck.OrderType;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
	private ListAdapter currentDeck;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cards_list);
		
		spinnerTag = (Spinner) findViewById(R.id.spinnerSetListTag);
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
					currentDeck = new Deck(currentTag, OrderType.BY_ID, CardsListActivity.this);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		
	list = (ListView) findViewById(R.id.listView);
		list.setAdapter(currentDeck);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cards_list, menu);
		return true;
	}

}
