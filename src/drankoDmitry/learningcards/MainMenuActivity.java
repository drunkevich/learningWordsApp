package drankoDmitry.learningcards;


import drankoDmitry.learningcards.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;

public class MainMenuActivity extends Activity {
	
	
	protected static final int GET_TEXT_FILE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		Button learnCard = (Button) findViewById(R.id.learn_cards);
		
		learnCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainMenuActivity.this,MainActivity.class));
							
				}
			});
		
		Button addCard = (Button) findViewById(R.id.add_cards);
		
		addCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainMenuActivity.this,AddingCardManuallyActivity.class));
							
				}
			});
		
		 final Intent intent = getIntent();  
	        final String action = intent.getAction();

	        if(Intent.ACTION_VIEW.equals(action)){
	            //uri = intent.getStringExtra("URI");
	            Uri uri2 = intent.getData();
	            String uri = uri2.getEncodedPath() + "  complete: " + uri2.toString();
	            Log.d("catching file", uri);
	        } else {
	            Log.d("catching file", "intent was something else: "+action);
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
	            CardsDatabase.deletedb(this);
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	 
	   
}
