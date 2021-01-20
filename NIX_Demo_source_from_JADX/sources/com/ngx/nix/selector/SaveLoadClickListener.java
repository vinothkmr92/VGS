package com.ngx.nix.selector;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.ngx.nix.C0425R;
import java.io.File;

public class SaveLoadClickListener implements OnClickListener {
    private final Context mContext;
    private final FileSelector mFileSelector;
    private final FileOperation mOperation;

    public SaveLoadClickListener(FileOperation operation, FileSelector fileSelector, Context context) {
        this.mOperation = operation;
        this.mFileSelector = fileSelector;
        this.mContext = context;
    }

    public void onClick(View view) {
        String text = this.mFileSelector.getSelectedFileName();
        if (checkFileName(text)) {
            String filePath = this.mFileSelector.getCurrentLocation().getAbsolutePath() + File.separator + text;
            File file = new File(filePath);
            int messageText = 0;
            switch (this.mOperation) {
                case SAVE:
                    if (file.exists() && !file.canWrite()) {
                        messageText = C0425R.string.cannotSaveFileMessage;
                        break;
                    }
                case LOAD:
                    if (file.exists()) {
                        if (!file.canRead()) {
                            messageText = C0425R.string.accessDenied;
                            break;
                        }
                    } else {
                        messageText = C0425R.string.missingFile;
                        break;
                    }
                    break;
            }
            if (messageText != 0) {
                Toast t = Toast.makeText(this.mContext, messageText, 0);
                t.setGravity(17, 0, 0);
                t.show();
                return;
            }
            this.mFileSelector.mOnHandleFileListener.handleFile(filePath);
            this.mFileSelector.dismiss();
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean checkFileName(String text) {
        if (text.length() != 0) {
            return true;
        }
        Builder builder = new Builder(this.mContext);
        builder.setTitle(C0425R.string.information);
        builder.setMessage(C0425R.string.fileNameFirstMessage);
        builder.setNeutralButton(C0425R.string.okButtonText, null);
        builder.show();
        return false;
    }
}
