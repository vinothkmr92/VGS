package com.imin.printer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imin.printerlib.IminPrintUtils;
import com.imin.printerlib.util.LogUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class NvBitmapActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mAddBmpFilePath, mBmpLoad, mBmpPrint, mClearPath;
    private EditText mBmpPath_et;
    private static final int FILE_SELECT_CODE = 0;
    private IminPrintUtils mIminPrintUtils;
    private String loadPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nv_bitmap);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 0);
        }
        mAddBmpFilePath = findViewById(R.id.SelectBmpFile);
        mBmpLoad = findViewById(R.id.Load_nvbmp_btn);
        mBmpPrint = findViewById(R.id.Print_nvbmp_btn);
        mClearPath = findViewById(R.id.Clear_Path_Btn);
        mBmpPath_et = findViewById(R.id.Nvbmp_path_et);
        mAddBmpFilePath.setOnClickListener(this);
        mBmpLoad.setOnClickListener(this);
        mBmpPrint.setOnClickListener(this);
        mClearPath.setOnClickListener(this);
        mIminPrintUtils = IminPrintUtils.getInstance(NvBitmapActivity.this);
        int connectType = getIntent().getIntExtra("connectType",0);
        if(connectType == 1){
            mIminPrintUtils.initPrinter(IminPrintUtils.PrintConnectType.USB);
        }else if(connectType == 2){
            mIminPrintUtils.initPrinter(IminPrintUtils.PrintConnectType.BLUETOOTH);
        }else  if(connectType == 3){
            mIminPrintUtils.initPrinter(IminPrintUtils.PrintConnectType.SPI);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SelectBmpFile:
                selectBmpFile();
                Log.i("xgh", " selectBmpFile");
                break;
            case R.id.Load_nvbmp_btn:
                setDownloadNvBmp();
                break;
            case R.id.Print_nvbmp_btn:
                PrintNvBmp();

                break;
            case R.id.Clear_Path_Btn:
                mBmpPath_et.setText("");
                break;
        }
    }

    // 设置下载位图
    private void setDownloadNvBmp() {
        loadPath = mBmpPath_et.getText().toString().trim();
        FileInputStream file = null;
        try {
            file = new FileInputStream(loadPath.substring(0,loadPath.length()-1));
            Log.i("xgh****", "bitmap,sharePath=1=> "+(file==null));
        } catch (FileNotFoundException e) {
            Log.i("xgh****", "bitmap,sharePath=00=> "+e.getMessage());
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(file);
        byte[] b =Utils.intToByte(Utils.getPixelsByBitmap(bitmap));

//        Log.i("xgh****", "bitmap,sharePath=2=> "+(bitmap==null));
//        if (bitmap != null){
//            int width = bitmap.getWidth();
//            int  heigh = bitmap.getHeight();
//            int iDataLen = width * heigh;
//            int[] pixels = new int[iDataLen];
//            byte[] bytes = BmpUtils.printDiskImagefile(pixels,bitmap.getWidth(),bitmap.getHeight());
            LogUtils.showLogCompletion("xgh****",""+ Arrays.toString(b));
//        }
        /*if (mIminPrintUtils.setDownloadNvBmp(loadPath)) {
            Toast.makeText(NvBitmapActivity.this, getString(R.string.Download_bmp_prompt),
                    Toast.LENGTH_SHORT).show();
        }*/
    }


    // 打印位图【指定下载位图的索引】
    private void PrintNvBmp() {
        String[] printNumbers = null;
        printNumbers = new String[]{"printer bitmap 1",
                "printer bitmap 2",
                "printer bitmap 3"};
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(getString(R.string.Print_Bmp_btn));
        b.setSingleChoiceItems(printNumbers, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
//                                mIminPrintUtils.printNvBitmap(1);
                                mIminPrintUtils.printAndFeedPaper(100);
                                dialog.dismiss();
                                break;
                            case 1:
//                                mIminPrintUtils.printNvBitmap(2);
                                mIminPrintUtils.printAndFeedPaper(100);
                                dialog.dismiss();
                                break;
                            case 2:
//                                mIminPrintUtils.printNvBitmap(3);
                                mIminPrintUtils.printAndFeedPaper(100);
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                });

        b.show();
    }

    // 选择bmp图片文件
    private void selectBmpFile() {
        showFileChooser();
        String bmpPath = mBmpPath_et.getText().toString().trim();
        Utils.putValue(NvBitmapActivity.this, "path", bmpPath);
    }

    // 显示文件选择路径
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a BIN file"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path="";
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    path = Utils.uriToFileApiQ(NvBitmapActivity.this,uri);
                    }else {
                        path = Utils.getFilePathFromContentUri( uri,getContentResolver());
                    }

                    String sharePath = Utils.getValue(NvBitmapActivity.this, "path", "").toString().trim();
                    Log.i("xgh****", "uri==> "+uri+" ,path==> "+path+" ,sharePath==> "+sharePath);
                    if (!TextUtils.isEmpty(path)) {
                        if (TextUtils.isEmpty(path)) {
                            mBmpPath_et.setText(path + ";");
                        } else {
                            mBmpPath_et.setText(sharePath + path + ";");
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
