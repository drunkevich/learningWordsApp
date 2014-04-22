package drankoDmitry.learningcards;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

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
	private TextView tv;
	private EditText et;
	private CheckBox cb;
			
		
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
	    final FileItem item = dicts.get(position);
	    Log.d("catching file", item+"");
		String filename = item.toString();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    LayoutInflater inflater = this.getLayoutInflater();
	    View view = inflater.inflate(R.layout.import_file_layout, null);
	    tv = (TextView) view.findViewById(R.id.tv_import);
	    tv.setText(getResources().getString(R.string.verify_import_file)+filename+"?");
	    et = (EditText) view.findViewById(R.id.et_import);
	    et.setText(filename);
	    cb = (CheckBox) view.findViewById(R.id.cb_import);
	    cb.setChecked(true);
	    cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					ImportFileActivity.this.et.setEnabled(isChecked);
			}
		});
	    builder.setView(view);
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   Log.d("dialog","ok");
	            	   if (cb.isChecked()) {
	            		   CardsDatabase.readFile(item.file.getAbsolutePath(), ImportFileActivity.this, et.getText().toString());
	            	   } else {
	            		   CardsDatabase.readFile(item.file.getAbsolutePath(), ImportFileActivity.this, null);
	            	   }
	            	   Toast.makeText(ImportFileActivity.this, R.string.import_file, Toast.LENGTH_SHORT).show();
	               }
	           });
	    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   Log.d("dialog","cancel");
	               }
	           });      
	    builder.create().show();
	  }
	
	private class FileItem {
		public File file;
		
		public FileItem(File _file) {
			file = _file;
		}
		
		@Override
		public String toString() {
			return file.getName();
		}
		
	}
}
	
