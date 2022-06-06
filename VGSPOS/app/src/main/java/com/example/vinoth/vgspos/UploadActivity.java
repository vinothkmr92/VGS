package com.example.vinoth.vgspos;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnUpload;
    DatabaseHelper dbHelper;
    public static final int requestcode = 1;
    TextView lbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        lbl = (TextView) findViewById(R.id.lblView);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        dbHelper = new DatabaseHelper(this);
        btnUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
            //fileintent.setType("gagt/sdf");
            String[] mimeTypes ={"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                fileintent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                if (mimeTypes.length > 0) {
                    fileintent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
            } else {
                String mimeTypesStr = "";
                for (String mimeType : mimeTypes) {
                    mimeTypesStr += mimeType + "|";
                }
                fileintent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
            }
            startActivityForResult(fileintent, requestcode);
        } catch (Exception ex) {
            lbl.setText(ex.getMessage());
        }
    }

    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String fileName = getFilePath(context, uri);
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                }

                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:", "");
                    File file = new File(id);
                    if (file.exists())
                        return id;
                }

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();
                return uri.getPath();
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }

        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getFilePath(Context context, Uri uri) {

        Cursor cursor = null;
        final String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case requestcode:
                String FilePath = getPath(this,data.getData());
                try {
                    if (resultCode == RESULT_OK) {
                        AssetManager am = this.getAssets();
                        InputStream inStream;
                        Workbook wb = null;
                        try {
                            inStream = new FileInputStream(FilePath);
                            wb = new HSSFWorkbook(inStream);
                            inStream.close();
                            Sheet sheet = wb.getSheetAt(0);
                            if (sheet == null) {
                                return;
                            }
                            Integer counter=0;
                            Integer i=0;
                            for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext(); ){
                                Row row = rit.next();
                                if(i==0){
                                    i++;
                                    continue;
                                }
                                ContentValues contentValues = new ContentValues();
                                row.getCell(0, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
                                Item item = new Item();
                                item.setItem_Name(row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                                String id = row.getCell(1,Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                                Integer itemid = Integer.parseInt(id);
                                item.setItem_No(itemid);
                                try{
                                    ArrayList<Item> s = dbHelper.GetItems();
                                    Integer index = s.indexOf(item);
                                    if(index != -1){
                                        dbHelper.Delete_Item(item.getItem_No());
                                    }
                                    dbHelper.Insert_Item(item);
                                    counter++;
                                    lbl.setText("  "+counter+" - Rows Uploaded");
                                }
                                catch (Exception ex){
                                    lbl.setText(ex.getMessage());
                                }
                                i++;
                            }

                        } catch (IOException e) {
                            lbl.setText(e.getMessage().toString());
                            e.printStackTrace();
                        }

                    }
                } catch (Exception ex) {
                    lbl.setText(ex.getMessage().toString());
                    ex.printStackTrace();
                    //lbl.setText(ex.getMessage().toString());
                }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.uploadExcel:
                Intent dcpage = new Intent(this,UploadActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            case R.id.homemenu:
                Intent page = new Intent(this,HomeActivity.class);
                page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(page);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}