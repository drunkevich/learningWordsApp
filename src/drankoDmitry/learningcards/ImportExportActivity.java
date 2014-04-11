package drankoDmitry.learningcards;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ImportExportActivity extends Activity {

	protected static final int REQUEST_FILE_GET = 36;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_export);
		
		Button importFile = (Button) findViewById(R.id.import_file);
		
		importFile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
						Log.d("onClick", "import");
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					    intent.setType("*/*");
					    if (intent.resolveActivity(getPackageManager()) != null) {
					        startActivityForResult(intent, REQUEST_FILE_GET);
					    }

				}
			});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.import_export, menu);
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_FILE_GET && resultCode == RESULT_OK) {
	    	Log.d("getFile", data.toString());
	    }
	}
}
