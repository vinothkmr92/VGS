package com.ngx.nix.selector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.ngx.nix.C0425R;
import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<FileData> mFileDataArray;

    public FileListAdapter(Context context, List<FileData> aFileDataArray) {
        this.mFileDataArray = (ArrayList) aFileDataArray;
        this.mContext = context;
    }

    public int getCount() {
        return this.mFileDataArray.size();
    }

    public Object getItem(int position) {
        return this.mFileDataArray.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        FileData tempFileData = (FileData) this.mFileDataArray.get(position);
        TextViewWithImage tempView = new TextViewWithImage(this.mContext);
        tempView.setText(tempFileData.getFileName());
        int imgRes = -1;
        switch (tempFileData.getFileType()) {
            case 0:
                imgRes = C0425R.C0426drawable.up_folder;
                break;
            case 1:
                imgRes = C0425R.C0426drawable.folder;
                break;
            case 2:
                imgRes = C0425R.mipmap.image;
                break;
        }
        tempView.setImageResource(imgRes);
        return tempView;
    }
}
