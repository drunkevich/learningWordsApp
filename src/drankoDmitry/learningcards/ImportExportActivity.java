package drankoDmitry.learningcards;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

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
		
		Button exportFile = (Button) findViewById(R.id.export_file);
		
		exportFile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
						Log.d("onClick", "export");
						
						//TODO
				}
			});
		
		Button clearQualities = (Button) findViewById(R.id.clear_qualities);
		
		clearQualities.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
						Log.d("onClick", "clear");
						
						AlertDialog.Builder builder = new AlertDialog.Builder(ImportExportActivity.this);
				        builder.setMessage(R.string.clear_qualities)
				               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				                   public void onClick(DialogInterface dialog, int id) {
				                	   CardsDatabase.clearQualities(ImportExportActivity.this);
				                	   
				                	   Toast.makeText(ImportExportActivity.this, R.string.clearing_qualities, Toast.LENGTH_SHORT).show();
				                   }
				               })
				               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				                   public void onClick(DialogInterface dialog, int id) {
				                       
				                   }
				               });
				        builder.create().show();
				}
			});
		
		Button deleteDB = (Button) findViewById(R.id.delete_db);
		
		deleteDB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
						Log.d("onClick", "delete");
						
						 AlertDialog.Builder builder = new AlertDialog.Builder(ImportExportActivity.this);
					        builder.setMessage(R.string.delete_database)
					               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					                   public void onClick(DialogInterface dialog, int id) {
					                	   CardsDatabase.deletedb(ImportExportActivity.this);
					                	   //TODO toast example copy from here
					                	   Toast.makeText(ImportExportActivity.this, R.string.deleting_db, Toast.LENGTH_SHORT).show();
					                   }
					               })
					               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					                   public void onClick(DialogInterface dialog, int id) {
					                       
					                   }
					               });
					        builder.create().show();
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
