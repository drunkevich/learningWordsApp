package drankoDmitry.learningcards;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainMenuActivity extends Activity {
	
	private EditText et;
	private TextView tv;
	private CheckBox cb;
	private SharedPreferences settings;

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
		
		Button editCards = (Button) findViewById(R.id.edit_cards);
		
		editCards.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainMenuActivity.this,CardsListActivity.class));
							
				}
			});
		
		Button importExport = (Button) findViewById(R.id.import_export);
		
		importExport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainMenuActivity.this,ImportExportActivity.class));
							
				}
			});
		Button help = (Button) findViewById(R.id.help);
		help.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainMenuActivity.this, "Help, I need somebody\nHelp, not just anybody\nHelp, you know I need someone, help", Toast.LENGTH_SHORT).show();
			}
		});
		   
		
		 final Intent intent = getIntent();  
	        final String action = intent.getAction();

	        if(Intent.ACTION_VIEW.equals(action)){
	            Uri uri = intent.getData();
	            Log.d("catching file", uri.toString());
	            importFile(uri);
	        } else {
	            Log.d("catching file", "intent was something else: "+action);
	        }
		
	        settings = getSharedPreferences("preferences", 0);
	        boolean firstStart = settings.getBoolean("firstStart", true);
	        if (firstStart) {
	        	loadPreset();
	        	SharedPreferences.Editor editor = settings.edit();
	  	      	editor.putBoolean("firstStart", false);
	  	      	editor.commit();
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
	    tv.setText(getResources().getString(R.string.verify_import_file)+filename_txt+"?");
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
                           CardsDatabase.readFile(u.getEncodedPath(), MainMenuActivity.this, et.getText().toString(), null, null);
                       } else {
                           CardsDatabase.readFile(u.getEncodedPath(), MainMenuActivity.this, null, null, null);
                       }
	            	   Toast.makeText(MainMenuActivity.this, R.string.import_file, Toast.LENGTH_SHORT).show();
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
		getMenuInflater().inflate(R.menu.main_menu, menu);
		Log.d("menu", "ok");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return true;
	    
	}
	 
	public void loadPreset() {
		try {
    		InputStream inputStream = this.getResources().openRawResource(R.raw.test);
    		InputStreamReader inputreader = new InputStreamReader(inputStream);
    		BufferedReader br = new BufferedReader(inputreader);
            String str;
            while ((str = br.readLine()) != null) {
    			Log.d("import", str);
    			String[] s =str.split(";");
    			int l = s.length;
                for (int i = 0; i < l; i++) {
                    s[0] = s[0].trim();
                }
                if ((l>1)&&(s[0].length()>0)&&(s[1].length()>0)){
    				ContentValues values = new ContentValues();
    				values.put(CardsDatabase.WORD, s[0]);
    				values.put(CardsDatabase.TRANSLATION, s[1]);
        	  
    				if ((l>2)&&(s[2].length()>0)) 
    					values.put(CardsDatabase.TAG, s[2]);
    				else
    					values.put(CardsDatabase.TAG, CardsDatabase.DEFAULT_TAG);
        	  
    				if ((l>3)&&(s[3].length()>0))
    					try {
    						int q = Integer.parseInt(s[3]);
    						values.put(CardsDatabase.QUALITY, q);
    					} catch (Exception e) {
                            values.put(CardsDatabase.QUALITY, Card.defaultQ);
                        }
    				else
                        values.put(CardsDatabase.QUALITY, Card.defaultQ);

                    CardsDatabase.insertCard(values,this);
    			}
    		}
    		br.close();
    	} catch (Exception e) {
    		e.printStackTrace();
		}
 	   Toast.makeText(MainMenuActivity.this, "importing file", Toast.LENGTH_SHORT).show();
	}
	   
	
	
}
