package drankoDmitry.learningcards;

import drankoDmitry.learningcards.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddingCardManuallyActivity extends Activity {
	
	
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adding_card_manually);	
		
			
		
		final EditText word = (EditText) findViewById(R.id.word);
		final EditText translation = (EditText) findViewById(R.id.translation);
		final EditText tag = (EditText) findViewById(R.id.tag);
		final EditText quality = (EditText) findViewById(R.id.quality);
		
		Button addCard = (Button) findViewById(R.id.add_card);
		addCard.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				Editable mWord = word.getText();
				Editable mTranslation = translation.getText();
				
				if ((mWord.length()>0)&&(mTranslation.length()>0)) {
					
					ContentValues values = new ContentValues();

					values.put(CardsDatabase.WORD, mWord.toString());
					values.put(CardsDatabase.TRANSLATION, mTranslation.toString());
					
					Editable mTag = tag.getText();
					if (mTag.length()>0)
						values.put(CardsDatabase.TAG, mTag.toString());
					else
						values.put(CardsDatabase.TAG, CardsDatabase.DEFAULT_TAG);
					
					Editable mQuality = quality.getText();
					if (mQuality.length()>0)
						try {
							int iQuality = Integer.parseInt(mQuality.toString());
							if (iQuality<1 || iQuality>5) iQuality=5;
							values.put(CardsDatabase.QUALITY, iQuality);
						} catch (Exception e) {
							values.put(CardsDatabase.QUALITY, CardsDatabase.DEFAULT_QUALITY);
						}
					else
						values.put(CardsDatabase.QUALITY, CardsDatabase.DEFAULT_QUALITY);
					
					CardsDatabase.insert(values);
				}
			}
		});
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.adding_card_manually, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		return super.onOptionsItemSelected(item);
	}
	
	
}
