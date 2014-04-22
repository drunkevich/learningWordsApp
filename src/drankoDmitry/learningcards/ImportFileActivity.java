package drankoDmitry.learningcards;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ImportFileActivity extends ListActivity {
	
	private File path;
	private ArrayList<FileItem> dicts = new ArrayList<FileItem>();
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
			return (sel.isFile()&&filename.endsWith(".txt"));
		}
	};
			
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (!isExternalStorageReadable()) {
			 Toast.makeText(ImportFileActivity.this, R.string.cant_read_external_storage, Toast.LENGTH_SHORT).show();
			 finish();
		}
		path = new File(Environment.getExternalStorageDirectory()+"");
		fileSearch(path);
		Log.d("dicts found", dicts.size()+"");
		//TODO
		ListAdapter adapter = new ArrayAdapter<FileItem>(this, android.R.layout.simple_list_item_1, dicts);
		setListAdapter(adapter);

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
		Log.d("fileSearch", dicts.size()+"");
		File[] fList = root.listFiles(dictFilter);
		if (fList!=null) {
			for (File f: fList) {
				dicts.add(new FileItem(f));
			}
		}
	
		File[] dList = root.listFiles(dirFilter);
		if (dList!=null) {
			for (File d: dList) {
				fileSearch(d);
			}
		}
	}
	
	@Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    FileItem item = dicts.get(position);
	    Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	  }
	
	private class FileItem {
		private File file;
		
		public FileItem(File _file) {
			file = _file;
		}
		
		@Override
		public String toString() {
			return file.getName();
		}
		
	}
}
	
