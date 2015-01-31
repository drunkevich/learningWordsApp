package drankoDmitry.learningcards;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class ImportFileActivity extends ListActivity {
	
	private File path;
    private String str_path;
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
    private CheckBox tb;

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
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
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


        tb = (CheckBox) view.findViewById(R.id.tb);
        tb.setChecked(false);


        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
                       Log.d("dialog", "ok");
                       if (!tb.isChecked()) {
                           if (cb.isChecked()) {
                               CardsDatabase.readFile(item.file.getAbsolutePath(), ImportFileActivity.this, et.getText().toString(), null, null);
                           } else {
                               CardsDatabase.readFile(item.file.getAbsolutePath(), ImportFileActivity.this, null, null, null);
                           }
                           Toast.makeText(ImportFileActivity.this, R.string.import_file, Toast.LENGTH_SHORT).show();
                       } else {
                           str_path = item.file.getAbsolutePath();
                           new YandexTranslateHelper().getLanguages(ImportFileActivity.this);
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

    public void makeToast(List<String> langs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.translating_layout, null);
        TextView chooseLang = (TextView) view.findViewById(R.id.choose_lang);
        chooseLang.setText(R.string.choose_lang);

        final Spinner langSpiner1 = (Spinner) view.findViewById(R.id.lang_spinner1);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for (String s : langs) {
            adapter.add(s);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpiner1.setAdapter(adapter);

        final Spinner langSpiner2 = (Spinner) view.findViewById(R.id.lang_spinner2);
        langSpiner2.setAdapter(adapter);

        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.d("dialog", "ok");
                String lang1 = (String) langSpiner1.getSelectedItem();
                String lang2 = (String) langSpiner2.getSelectedItem();
                if (cb.isChecked()) { //TODO
                    CardsDatabase.readFile(str_path, ImportFileActivity.this, et.getText().toString(), lang1, lang2);
                } else {
                    CardsDatabase.readFile(str_path, ImportFileActivity.this, null, lang1, lang2);
                }
                Toast.makeText(ImportFileActivity.this, R.string.import_file, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("dialog", "cancel");
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
	
