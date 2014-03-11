package drankoDmitry.learningcards;


import drankoDmitry.learningcards.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;

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
		
		
		boolean startAlert = getIntent().getExtras().getBoolean("force alert");
		if (startAlert) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			
			builder.setMessage(R.string.dialog_no_cards);

			
			AlertDialog dialog = builder.create();
			dialog.show();
		}
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
	            CardsDatabase.deletedb();
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	 
	   
}
