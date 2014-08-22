package com.harry.ireader;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileBrowerActivity extends Activity {
	
	private TextView tv_current_path;
	private ListView mFileListView;
	private TextFilenameFilter filenameFilter;
	private ArrayAdapter<FileInfo> mAdapter;
	private FileIconHelper mFileIconHelper;
	
	private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();
	
	private final String file_dir = "mnt/sdcard/IReader";
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED) 
            		|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.file_brower);
		
		tv_current_path = (TextView)findViewById(R.id.current_path_view);
		tv_current_path.setText(file_dir);
		
		mFileListView = (ListView)findViewById(R.id.file_path_list);
		mFileIconHelper = new FileIconHelper(this);
		mAdapter = new FileListAdapter(this, R.layout.file_browser_item, mFileNameList,
                mFileIconHelper);
		mFileListView.setAdapter(mAdapter);
		mFileListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				viewFile(mFileNameList.get(position));
			}
		});
		
		filenameFilter = new TextFilenameFilter();
		
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addDataScheme("file");
        registerReceiver(mReceiver, intentFilter);
        
		updateUI();
	}
	
	private void viewFile(FileInfo lFileInfo) {
        try {
            IntentBuilder.viewFile(this, lFileInfo.filePath);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	private void updateUI() {
        boolean sdCardReady = Util.isSDCardReady();
        View noSdView = findViewById(R.id.sd_not_available_page);
        noSdView.setVisibility(sdCardReady ? View.GONE : View.VISIBLE);

        View navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);
        mFileListView.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);

        if(sdCardReady) {
        	onRefreshFileList(file_dir);
        }
    }
	
	 public boolean onRefreshFileList(String path) {
	        File file = new File(path);
	        if (!file.exists()) {
	        	file.mkdirs();
	        }
	        ArrayList<FileInfo> fileList = mFileNameList;
	        fileList.clear();

	        File[] listFiles = file.listFiles(filenameFilter);
	        if (listFiles == null)
	            return true;

	        for (File child : listFiles) {
	            String absolutePath = child.getAbsolutePath();
	            if (Util.isNormalFile(absolutePath)) {
	                FileInfo lFileInfo = Util.GetFileInfo(child,
	                		filenameFilter, false);
	                if (lFileInfo != null) {
	                    fileList.add(lFileInfo);
	                }
	            }
	        }

	        showEmptyView(fileList.size() == 0);
	        
	        return true;
	    }
	
	 
	
	private void showEmptyView(boolean show) {
        View emptyView = findViewById(R.id.empty_view);
        if (emptyView != null)
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
