package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    private Dialog progressBar;

    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        lbl = (TextView) findViewById(R.id.lblView);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        dbHelper = new DatabaseHelper(this);
        btnUpload.setOnClickListener(this);
        progressBar = new Dialog(UploadActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading...");
        if(!checkPermission()){
            requestStoragePermission();
        }
    }
    public void requestStoragePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            storeageActivitytResultLanucher.launch(intent);
        }
        else{
            ActivityCompat.requestPermissions(UploadActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,1);
        }
    }
    public boolean checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
         return  Environment.isExternalStorageManager();
        }
        else{
            int write = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }
    private ActivityResultLauncher<Intent> storeageActivitytResultLanucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){

                    }
                    else{

                    }
                }
            });
    @Override
    public void onClick(View v) {
        try {
            progressBar.show();
            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
            fileintent.setType("application/*");
            startActivityForResult(fileintent, requestcode);
        } catch (Exception ex) {
            lbl.setText(ex.getMessage());
        }
        finally {
            if(progressBar.isShowing()){
                progressBar.cancel();
            }
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
                    return Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Download/" + fileName;
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

    private boolean isNumeric(String val){
        try{
            Integer integer = Integer.parseInt(val);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
    private boolean isDouble(String val){
        try{
            Double d = Double.parseDouble(val);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case requestcode:
                String FilePath = getPath(this,data.getData());
                Log.e("File Path",FilePath);
                if(FilePath.contains("/root_path")){
                    FilePath = FilePath.replace("/root_path","");
                }
                Log.e("File Path",FilePath);
                try {
                    if (resultCode == RESULT_OK) {
                        AssetManager am = this.getAssets();
                        InputStream inStream;
                        Workbook wb = null;
                        try {
                            inStream = new FileInputStream(FilePath);
                            Log.e("Extension",FilePath.substring(FilePath.lastIndexOf(".")));
                            if(FilePath.substring(FilePath.lastIndexOf(".")).equals(".xls")){
                                Log.e("File Type","Selected file is XLS");
                                wb = new HSSFWorkbook(inStream);
                            }
                            else if(FilePath.substring(FilePath.lastIndexOf(".")).equals(".xlsx")){
                                Log.e("File Type","Selected file is XLSX");
                                wb = new XSSFWorkbook(inStream);
                            }
                            else{
                                wb = null;
                                lbl.setText("Please select valid Excel file.");
                                return;
                            }
                            inStream.close();
                            Sheet sheet = wb.getSheetAt(0);
                            int counter=0;
                            for(Iterator<Row> rit = sheet.rowIterator();rit.hasNext();){
                                Row row = rit.next();
                                row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                String itemname = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                if(itemname.equals("ITEM NAME") || itemname.isEmpty()){
                                    continue;
                                }
                                else{
                                    String itemnostr = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                    Integer itemno = isNumeric(itemnostr) ? Integer.parseInt(itemnostr):0;
                                    String pricestr = row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                    double price = isDouble(pricestr) ? Double.parseDouble(pricestr):0;
                                    String acpricestr = row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                    double acprice = isDouble(acpricestr) ? Double.parseDouble(acpricestr):0;
                                    String stockstr = row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                    double stock = isDouble(stockstr) ? Double.parseDouble(stockstr):0;
                                    Item item = new Item();
                                    item.setItem_No(itemno);
                                    item.setItem_Name(itemname);
                                    item.setPrice(price);
                                    item.setAcPrice(acprice);
                                    item.setStocks(stock);
                                    try{
                                        ArrayList<Item> s = dbHelper.GetItems();
                                        for(int k=0;k<s.size();k++){
                                            int ino = s.get(k).getItem_No();
                                            if(itemno == ino){
                                                dbHelper.Delete_Item(item.getItem_No());
                                                break;
                                            }
                                        }
                                        dbHelper.Insert_Item(item);
                                        counter++;
                                    }
                                    catch (Exception ex)
                                    {
                                        lbl.setText(ex.getMessage());
                                    }
                                }
                            }
                            lbl.setText("  "+counter+" - Rows Uploaded");
                           /* CSVReader reader = new CSVReader(new FileReader(FilePath));
                            String[] nextLine;
                            Integer counter=0;
                            while ((nextLine = reader.readNext()) != null) {
                                // nextLine[] is an array of values from the line
                                String itemname = nextLine[0];
                                if(itemname.equals("ITEM NAME")){
                                    continue;
                                }
                                if(itemname.isEmpty()){
                                    break;
                                }
                                String itemnos = nextLine[1];
                                String prices = nextLine[2];
                                if(itemnos.isEmpty()){
                                    break;
                                }
                                if(prices.isEmpty()){
                                    prices="0";
                                }
                                Integer itemno = Integer.parseInt(itemnos);
                                Double price = Double.parseDouble(prices);
                                Item item = new Item();
                                item.setItem_Name(itemname);
                                item.setItem_No(itemno);
                                item.setPrice(price);
                                try{
                                    ArrayList<Item> s = dbHelper.GetItems();
                                    Integer index = s.indexOf(item);
                                    if(index != -1){
                                        dbHelper.Delete_Item(item.getItem_No());
                                    }
                                    dbHelper.Insert_Item(item);
                                    counter++;
                                }
                                catch (Exception ex)
                                {
                                    lbl.setText(ex.getMessage());
                                }
                                lbl.setText("  "+counter+" - Rows Uploaded");
                            }*/
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
        super.onActivityResult(requestCode,resultCode,data);
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
            case R.id.settings:
                Intent settingsPage = new Intent(this,Settings.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
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