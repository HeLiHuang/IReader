package com.harry.ireader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import com.harry.ireader.R;

public class FileListAdapter extends ArrayAdapter<FileInfo> {
    private LayoutInflater mInflater;
    private FileIconHelper mFileIcon;
    private List<FileInfo> fileInfos;
    private Context mContext;

    public FileListAdapter(Context context, int resource,
            List<FileInfo> objects,
            FileIconHelper fileIcon) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mFileIcon = fileIcon;
        mContext = context;
        
        fileInfos = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(R.layout.file_browser_item, parent, false);
        }

        FileInfo lFileInfo = fileInfos.get(position);
        FileListItem.setupFileListItemInfo(mContext, view, lFileInfo,
                mFileIcon);
        return view;
    }
}
