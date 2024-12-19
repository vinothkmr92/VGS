package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int requestcode = 1;
    private static String EXCEL_SHEET_NAME = "Sheet1";
    private static Workbook workbook =null;
    private static Cell cell=null;
    private static CellStyle headerCellStyle=null;
    private static Sheet sheet = null;

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

    private final ActivityResultLauncher<Intent> storeageActivitytResultLanucher = registerForActivityResult(
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
    ImageButton btnUpload;
    ImageButton btnExport;
    CheckBox deleteCheckbox;
    DatabaseHelper dbHelper;
    TextView lbl;
    private Dialog progressBar;

    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    /**
     * Checks if Storage is Available
     *
     * @return boolean
     */
    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    /**
     * Setup header cell style
     */
    private static void setHeaderCellStyle() {
        headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.AQUA.getIndex());
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
    }

    /**
     * Setup Header Row
     */
    private static void setHeaderRow() {
        Row row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("ITEM_NAME");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("ITEM_CODE");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("RATE1");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("RATE2");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("STOCKS");
        cell.setCellStyle(headerCellStyle);
    }

    /**
     * Fills Data into Excel Sheet
     * <p>
     * NOTE: Set row index as i+1 since 0th index belongs to header row
     *
     * @param dataList - List containing data to be filled into excel
     */
    private static void fillDataIntoExcel(ArrayList<Item> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getItem_Name());

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getItem_No());

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getPrice());

            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getAcPrice());

            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getStocks());
        }
    }

    /**
     * Store Excel Workbook in external storage
     *
     * @param context  - application context
     * @param fileName - name of workbook which will be stored in device
     * @return boolean - returns state whether workbook is written into storage or not
     */
    private static boolean storeExcelInStorage(Context context, String fileName) {
        boolean isSuccess;
        File file = new File(context.getExternalFilesDir(DOWNLOAD_SERVICE), fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e("LOG", "Writing file" + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e("ERROR", "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e("ERROR", "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return isSuccess;
    }

    public static boolean exportDataIntoWorkbook(Context context, String fileName,
                                                 ArrayList<Item> dataList) {
        boolean isWorkbookWrittenIntoStorage;

        // Check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("error", "Storage not available or read only");
            return false;
        }

        // Creating a New HSSF Workbook (.xls format)
        workbook = new HSSFWorkbook();

        setHeaderCellStyle();

        // Creating a New Sheet and Setting width for each column
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        sheet.setColumnWidth(0, (15 * 300));
        sheet.setColumnWidth(1, (15 * 200));
        sheet.setColumnWidth(2, (15 * 200));
        sheet.setColumnWidth(3, (15 * 200));
        sheet.setColumnWidth(4, (15 * 200));

        setHeaderRow();
        fillDataIntoExcel(dataList);
        isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

        return isWorkbookWrittenIntoStorage;
    }

    public static String getPath(Context context, Uri uri) {
        try {

            /*File filen = new File(uri.getPath());//create path from uri
            final String[] splitn = filen.getPath().split(":");//split the path.
            return  splitn[1];//assign it to a string(your choice).*/
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
                else if (isMediaDocument(uri)) {
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
                    else if("document".equals(type)){
                        String fileName = getFilePath(context, uri);
                        if (fileName != null) {
                            String rootpath = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
                            return rootpath + "/Documents/" + fileName;
                        }

                        String id = DocumentsContract.getDocumentId(uri);
                        if (id.startsWith("raw:")) {
                            id = id.replaceFirst("raw:", "");
                            File file = new File(id);
                            if (file.exists())
                                return id;
                        }

                        final Uri contentUris = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUris, null, null);
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                } else if ("content".equalsIgnoreCase(uri.getScheme())) {
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
        catch (Exception ex){
            Toast.makeText(context.getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            return  null;
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        lbl = (TextView) findViewById(R.id.lblView);
        btnUpload = (ImageButton) findViewById(R.id.btnUpload);
        btnExport = (ImageButton)findViewById(R.id.btnExportproduct);
        deleteCheckbox = (CheckBox)findViewById(R.id.clearProducts);
        dbHelper = new DatabaseHelper(this);
        btnUpload.setOnClickListener(this);
        btnExport.setOnClickListener(this);
        progressBar = new Dialog(UploadActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading...");
        if(!checkPermission()){
            requestStoragePermission();
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

    public void requestStoragePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            try {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                storeageActivitytResultLanucher.launch(intent);
            } catch (Exception ex){
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storeageActivitytResultLanucher.launch(intent);
            }
        }
        else{
            ActivityCompat.requestPermissions(UploadActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUpload:
                UploadExcel();
                break;
            case R.id.btnExportproduct:
                ExportExcel();
                break;
        }

    }

    private void UploadExcel(){
        try {
            progressBar.show();
            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
            fileintent.setType("*/*");
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

    private void ExportExcel(){
        ArrayList<Item> sal = dbHelper.GetItems();
        String fileName = "Products.xls";
        if(exportDataIntoWorkbook(getApplicationContext(),fileName,sal)){
            try{
                File file = new File(getApplicationContext().getExternalFilesDir(DOWNLOAD_SERVICE), fileName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(UploadActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);
                intent.setDataAndType(uri,"application/vnd.ms-excel");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
            catch (Exception ex){
                showCustomDialog("Exception",ex.getMessage());
            }

        }
        else{
            showCustomDialog("Warning","Could not export to Excel.");
        }

    }

    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
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
                            if(deleteCheckbox.isChecked()){
                                dbHelper.DeleteAllItem();
                            }
                            for(Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();){
                                Row row = rit.next();
                                row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                String itemname = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                String itemno = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                String pricestr = row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                double price = isDouble(pricestr) ? Double.parseDouble(pricestr):0;
                                String acpricestr = row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                double acprice = isDouble(acpricestr) ? Double.parseDouble(acpricestr):0;
                                String stockstr = row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                double stock = isDouble(stockstr) ? Double.parseDouble(stockstr):0;
                                Item item = new Item();
                                itemno = itemno.replace(".0","");
                                item.setItem_No(itemno);
                                item.setItem_Name(itemname);
                                item.setPrice(price);
                                item.setAcPrice(acprice);
                                item.setStocks(stock);
                                try{
                                    Item check = dbHelper.GetItem(item.getItem_No());
                                    if(check!=null){
                                        double st = check.getStocks()+item.getStocks();
                                        item.setStocks(st);
                                    }
                                    dbHelper.Insert_Item(item);
                                    counter++;
                                }
                                catch (Exception ex)
                                {
                                    lbl.setText(ex.getMessage());
                                }
                            }
                            lbl.setText("  "+counter+" - Rows Uploaded");
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
}