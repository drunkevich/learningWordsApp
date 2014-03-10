package drankoDmitry.learningcards;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class DeckChooser extends Activity {

	private Spinner spinnerQuality;
	private Spinner spinnerTag;
	private Button button_ok;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_chooser);
		
		spinnerQuality = (Spinner) findViewById(R.id.dc_q);
		ArrayAdapter<CharSequence> adapterQ = ArrayAdapter.createFromResource(this,
		        R.array.Qualities, android.R.layout.simple_spinner_item);
		adapterQ.add("-");
		adapterQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerQuality.setAdapter(adapterQ);
		
		spinnerTag = (Spinner) findViewById(R.id.dc_tag);
		ArrayList<String> dbTags = CardsDatabase.readTags();			
		ArrayAdapter<CharSequence> adapterT = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
	
		for (String t:dbTags) {
			adapterT.add(t);
		}
		adapterT.add("-");
		adapterT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTag.setAdapter(adapterT);
		
		
		button_ok = (Button) findViewById(R.id.dc_ok);
		button_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				enterClicked();
			}
		});
	}

	protected void enterClicked() {
		
		String tag = spinnerTag.getSelectedItem().toString();
		if (tag.equals("-")) tag=null;
		String q = spinnerQuality.getSelectedItem().toString();
		int i;
		if (q.equals("-")) i=0;
		else i = Integer.parseInt(q);
		Intent intent = new Intent();
		intent.putExtra("tag", tag);
		intent.putExtra("quality", i);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deck_chooser, menu);
		return true;
	}

}
