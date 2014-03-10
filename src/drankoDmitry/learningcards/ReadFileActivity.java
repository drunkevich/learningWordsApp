package drankoDmitry.learningcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import drankoDmitry.learningcards.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ReadFileActivity extends Activity {

	private static final String LOG_TAG = "myDBG";
	EditText path;
	EditText tag;
	EditText quality;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_file);
		
		
		path = (EditText) findViewById(R.id.path);
		tag = (EditText) findViewById(R.id.file_tag);
		quality = (EditText) findViewById(R.id.file_quality);
	
		Button importFile = (Button) findViewById(R.id.import_file);
		
		importFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				readFile();
			}

			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.read_file, menu);
		return true;
	}
	private void readFile() {
		
			String filePath = path.getText().toString();
			Editable mTag = tag.getText();
			String dTag;
			if (mTag.length()>0)
				dTag = mTag.toString();
			else
				dTag = CardsDatabase.DEFAULT_TAG;
			
				
			Editable mQuantity = quality.getText();
			int dQuality;
			if (mQuantity.length()>0)
				try {
					dQuality = Integer.parseInt(mQuantity.toString());
					if (dQuality<1 || dQuality>5) dQuality=5;
				} catch (Exception e) {
					dQuality = CardsDatabase.DEFAULT_QUALITY;
				}
			else
				dQuality = CardsDatabase.DEFAULT_QUALITY;
			
			
			
			if (!Environment.getExternalStorageState().equals(
			        Environment.MEDIA_MOUNTED)) {
			      Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
			      return;
			    }
			    File sdPath = Environment.getExternalStorageDirectory();

			    sdPath = new File(sdPath.getAbsolutePath() + "/home/");
			    File sdFile = new File(sdPath, filePath);
			    try {

			        BufferedReader br = new BufferedReader(new FileReader(sdFile));
			        String str = "";
			        while ((str = br.readLine()) != null) {
			          Log.d(LOG_TAG, str);
			          
			          String[] s =str.split(";");
			          int l = s.length;
			          if ((l>1)&&(s[0].length()>0)&&(s[1].length()>0)){
			        	  ContentValues values = new ContentValues();
			        	  values.put(CardsDatabase.WORD, s[0]);
			        	  values.put(CardsDatabase.TRANSLATION, s[1]);
			        	  
			        	  if ((l>2)&&(s[2].length()>0)) 
				        	  values.put(CardsDatabase.TAG, s[2]);
			        	  else
				        	  values.put(CardsDatabase.TAG, dTag);
			        	  
			        	  if ((l>3)&&(s[3].length()>0))
			        		  try {
			        			  int q = Integer.parseInt(s[3]);
			        			  values.put(CardsDatabase.QUALITY, q);
			        		  } catch (Exception e) {
			        			  values.put(CardsDatabase.QUALITY, dQuality);
			        		  }
			        	  else 
			        		  values.put(CardsDatabase.QUALITY, dQuality);
			        	  CardsDatabase.insert(values);
			          }
			          
			        }
			        br.close();
			      } catch (Exception e) {
			        e.printStackTrace();
			      }
		
	}
}

