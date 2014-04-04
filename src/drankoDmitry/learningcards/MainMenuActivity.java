package drankoDmitry.learningcards;


import drankoDmitry.learningcards.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.Intent;

public class MainMenuActivity extends Activity {
	
	private EditText et;
	private TextView tv;
	private CheckBox cb;
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
	            Uri uri = intent.getData();
	            Log.d("catching file", uri.toString());
	            importFile(uri);
	        } else {
	            Log.d("catching file", "intent was something else: "+action);
	        }
		
	}

	
	
	

	private void importFile(Uri uri) {
		final Uri u = uri;
		String filename_txt = uri.getLastPathSegment();
		Log.d("catching file", filename_txt);
		String filename = filename_txt.substring(0, filename_txt.indexOf("."));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    LayoutInflater inflater = this.getLayoutInflater();
	    View view = inflater.inflate(R.layout.import_file_layout, null);
	    tv = (TextView) view.findViewById(R.id.tv_import);
	    tv.setText("import file "+filename_txt+" ?");
	    et = (EditText) view.findViewById(R.id.et_import);
	    et.setText(filename);
	    cb = (CheckBox) view.findViewById(R.id.cb_import);
	    cb.setChecked(true);
	    cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					MainMenuActivity.this.et.setEnabled(isChecked);
			}
		});
	    builder.setView(view);
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   Log.d("dialog","ok");
	            	   if (cb.isChecked()) {
	            		   CardsDatabase.readFile(u, MainMenuActivity.this, et.getText().toString());
	            	   } else {
	            		   CardsDatabase.readFile(u, MainMenuActivity.this, null);
	  	            	}
	               }
	           });
	    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   Log.d("dialog","cancel");
	               }
	           });      
	    builder.create().show();
		
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
