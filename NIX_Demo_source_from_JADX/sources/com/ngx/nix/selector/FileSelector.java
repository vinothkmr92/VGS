package com.ngx.nix.selector;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.ngx.nix.C0425R;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FileSelector {
    private Button mCancelButton;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public File mCurrentLocation;
    /* access modifiers changed from: private */
    public final Dialog mDialog;
    private ListView mFileListView;
    /* access modifiers changed from: private */
    public Spinner mFilterSpinner;
    private Button mNewFolderButton;
    final OnHandleFileListener mOnHandleFileListener;
    private Button mSaveLoadButton;
    /* access modifiers changed from: private */

    /* renamed from: sp */
    public SharedPreferences f95sp = PreferenceManager.getDefaultSharedPreferences(this.mContext);

    public FileSelector(Context context, FileOperation operation, OnHandleFileListener onHandleFileListener, String[] fileFilters) {
        this.mContext = context;
        this.mOnHandleFileListener = onHandleFileListener;
        File sdCard = Environment.getExternalStorageDirectory();
        if (sdCard.canRead()) {
            String path = this.f95sp.getString("location", "");
            if (path.length() > 0) {
                File itemLocation = new File(path);
                Log.d("String", "location" + itemLocation);
                this.mCurrentLocation = itemLocation;
            } else {
                this.mCurrentLocation = sdCard;
            }
        } else {
            this.mCurrentLocation = Environment.getRootDirectory();
        }
        this.mDialog = new Dialog(context);
        this.mDialog.setContentView(C0425R.layout.dialog);
        this.mDialog.setTitle(this.mCurrentLocation.getAbsolutePath());
        prepareFilterSpinner(fileFilters);
        prepareFilesList();
        setSaveLoadButton(operation);
        setNewFolderButton(operation);
        setCancelButton();
    }

    private void prepareFilterSpinner(String[] fitlesFilter) {
        this.mFilterSpinner = (Spinner) this.mDialog.findViewById(C0425R.C0427id.fileFilter);
        if (fitlesFilter == null || fitlesFilter.length == 0) {
            fitlesFilter = new String[]{FileUtils.FILTER_ALLOW_ALL};
            this.mFilterSpinner.setEnabled(true);
        }
        this.mFilterSpinner.setAdapter(new ArrayAdapter<>(this.mContext, C0425R.layout.spinner_item, fitlesFilter));
        this.mFilterSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View aView, int arg2, long arg3) {
                FileSelector.this.makeList(FileSelector.this.mCurrentLocation, ((TextView) aView).getText().toString());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void prepareFilesList() {
        this.mFileListView = (ListView) this.mDialog.findViewById(C0425R.C0427id.fileList);
        this.mFileListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((EditText) FileSelector.this.mDialog.findViewById(C0425R.C0427id.fileName)).setText("");
                if (id == 0) {
                    String parentLocation = FileSelector.this.mCurrentLocation.getParent();
                    if (parentLocation != null) {
                        String fileFilter = ((TextView) FileSelector.this.mFilterSpinner.getSelectedView()).getText().toString();
                        FileSelector.this.mCurrentLocation = new File(parentLocation);
                        FileSelector.this.mDialog.setTitle(parentLocation);
                        Editor editor = FileSelector.this.f95sp.edit();
                        editor.putString("location", FileSelector.this.mCurrentLocation.getAbsolutePath());
                        editor.commit();
                        FileSelector.this.makeList(FileSelector.this.mCurrentLocation, fileFilter);
                        return;
                    }
                    FileSelector.this.onItemSelect(parent, position);
                    return;
                }
                FileSelector.this.onItemSelect(parent, position);
            }
        });
        makeList(this.mCurrentLocation, this.mFilterSpinner.getSelectedItem().toString());
    }

    /* access modifiers changed from: private */
    public void makeList(File location, String fitlesFilter) {
        ArrayList<FileData> fileList = new ArrayList<>();
        if (location.getParent() != null) {
            fileList.add(new FileData("../", 0));
        }
        File[] listFiles = location.listFiles();
        if (listFiles != null) {
            ArrayList<FileData> fileDataList = new ArrayList<>();
            for (int index = 0; index < listFiles.length; index++) {
                File tempFile = listFiles[index];
                if (FileUtils.accept(tempFile, fitlesFilter)) {
                    fileDataList.add(new FileData(listFiles[index].getName(), tempFile.isDirectory() ? 1 : 2));
                }
            }
            fileList.addAll(fileDataList);
            Collections.sort(fileList);
        }
        if (this.mFileListView != null) {
            this.mFileListView.setAdapter(new FileListAdapter(this.mContext, fileList));
        }
    }

    /* access modifiers changed from: private */
    public void onItemSelect(AdapterView<?> parent, int position) {
        String itemText = ((FileData) parent.getItemAtPosition(position)).getFileName();
        String itemPath = this.mCurrentLocation.getAbsolutePath() + File.separator + itemText;
        this.mDialog.setTitle(itemPath);
        File itemLocation = new File(itemPath);
        if (!itemLocation.canRead()) {
            Toast.makeText(this.mContext, "Access denied!!!", 0).show();
        } else if (itemLocation.isDirectory()) {
            this.mCurrentLocation = itemLocation;
            String fileFilter = ((TextView) this.mFilterSpinner.getSelectedView()).getText().toString();
            Log.d("String", "Directory" + itemLocation);
            Editor editor = this.f95sp.edit();
            editor.putString("location", itemLocation.getAbsolutePath());
            editor.commit();
            makeList(this.mCurrentLocation, fileFilter);
        } else if (itemLocation.isFile()) {
            ((EditText) this.mDialog.findViewById(C0425R.C0427id.fileName)).setText(itemText);
        }
    }

    private void setSaveLoadButton(FileOperation operation) {
        this.mSaveLoadButton = (Button) this.mDialog.findViewById(C0425R.C0427id.fileSaveLoad);
        switch (operation) {
            case SAVE:
                this.mDialog.setTitle(this.mCurrentLocation.getAbsolutePath());
                this.mSaveLoadButton.setText(C0425R.string.saveButtonText);
                break;
            case LOAD:
                this.mSaveLoadButton.setText(C0425R.string.loadButtonText);
                break;
        }
        this.mSaveLoadButton.setOnClickListener(new SaveLoadClickListener(operation, this, this.mContext));
    }

    private void setNewFolderButton(FileOperation operation) {
        this.mNewFolderButton = (Button) this.mDialog.findViewById(C0425R.C0427id.newFolder);
        OnClickListener newFolderListener = new OnClickListener() {
            public void onClick(View v) {
                FileSelector.this.openNewFolderDialog();
            }
        };
        switch (operation) {
            case SAVE:
                this.mNewFolderButton.setVisibility(0);
                this.mNewFolderButton.setOnClickListener(newFolderListener);
                return;
            case LOAD:
                this.mNewFolderButton.setVisibility(8);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void openNewFolderDialog() {
        Builder alert = new Builder(this.mContext);
        alert.setTitle(C0425R.string.newFolderButtonText);
        alert.setMessage(C0425R.string.newFolderDialogMessage);
        final EditText input = new EditText(this.mContext);
        alert.setView(input);
        alert.setPositiveButton(C0425R.string.createButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (new File(FileSelector.this.mCurrentLocation.getAbsolutePath() + File.separator + input.getText().toString()).mkdir()) {
                    Toast t = Toast.makeText(FileSelector.this.mContext, C0425R.string.folderCreationOk, 0);
                    t.setGravity(17, 0, 0);
                    t.show();
                } else {
                    Toast t2 = Toast.makeText(FileSelector.this.mContext, C0425R.string.folderCreationError, 0);
                    t2.setGravity(17, 0, 0);
                    t2.show();
                }
                FileSelector.this.makeList(FileSelector.this.mCurrentLocation, ((TextView) FileSelector.this.mFilterSpinner.getSelectedView()).getText().toString());
            }
        });
        alert.show();
    }

    private void setCancelButton() {
        this.mCancelButton = (Button) this.mDialog.findViewById(C0425R.C0427id.fileCancel);
        this.mCancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FileSelector.this.mDialog.cancel();
            }
        });
    }

    public String getSelectedFileName() {
        return ((EditText) this.mDialog.findViewById(C0425R.C0427id.fileName)).getText().toString();
    }

    public File getCurrentLocation() {
        return this.mCurrentLocation;
    }

    public void show() {
        this.mDialog.show();
    }

    public void dismiss() {
        this.mDialog.dismiss();
    }
}
