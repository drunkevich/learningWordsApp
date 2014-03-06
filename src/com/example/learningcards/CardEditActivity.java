package com.example.learningcards;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class CardEditActivity extends Activity {
	
	
	protected static final int GET_TEXT_FILE = 1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_edit);
		
		Button addCard = (Button) findViewById(R.id.add_card);
		
		addCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CardEditActivity.this,AddingCardManuallyActivity.class));
							
				}
			});
		Button readFile = (Button) findViewById(R.id.read_file);
		readFile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CardEditActivity.this,ReadFileActivity.class));	
			}
		});
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.card_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		 switch (item.getItemId()) {
	        case R.id.delete_database:
	            CardsDatabase cdb = new CardsDatabase(this);
	            SQLiteDatabase db = cdb.getWritableDatabase();
	            db.delete(CardsDatabase.TABLE_NAME, null, null);
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	 
	   
}
