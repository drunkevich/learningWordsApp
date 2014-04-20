package drankoDmitry.learningcards;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class ImportFileActivity extends Activity {
	
	private File path;
	private ArrayList<File> dicts = new ArrayList<File>();
	private FilenameFilter dirFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String filename) {
			File sel = new File(dir, filename);
			return sel.isDirectory();
		}
	};
	private FilenameFilter dictFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String filename) {
			File sel = new File(dir, filename);
			return (sel.isFile()&&filename.endsWith(".lcd"));
		}
	};
			
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_file);
		if (!isExternalStorageReadable()) {
			 Toast.makeText(ImportFileActivity.this, R.string.cant_read_external_storage, Toast.LENGTH_SHORT).show();
			 finish();
		}
		path = new File(Environment.getExternalStorageDirectory()+"");
		fileSearch(path);
		Log.d("dicts found", dicts.size()+"");
		//TODO
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.import_file, menu);
		return true;
	}

	private boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	private void fileSearch(File root) {
		File[] fList = root.listFiles(dictFilter);
		for (File f: fList) {
			dicts.add(f);
		}
		File[] dList = root.listFiles(dirFilter);
		for (File d: dList) {
			fileSearch(d);
		}
	}
}
	
