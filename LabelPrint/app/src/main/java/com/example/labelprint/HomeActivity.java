package com.example.labelprint;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tscdll.TSCUSBActivity;

import org.apache.commons.lang3.StringUtils;
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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<ArrayList<String>> excelData = new ArrayList<>();
    ArrayList<String> commandToPrint = new ArrayList<>();
    public UsbManager usbManager;
    public UsbDevice usbDevice;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    Button btnPrint;
    Button btnUpload;
    public TSCUSBActivity TscUSB;
    public static final String EXPIRE_DT = "EXPIRE_DT";
    public static final String PRNPATH = "PRN";
    public static final String NOC = "NOC";
    public static final String MyPREFERENCES = "MyPrefs";
    private MySharedPreferences sharedpreferences;
    public String prn;
    public static final int requestcode = 1;
    private static String EXCEL_SHEET_NAME = "Sheet1";
    private static Workbook workbook =null;
    private static Cell cell=null;
    private static CellStyle headerCellStyle=null;
    private static Sheet sheet = null;
    TableLayout tableView;
    Integer NofColumns;
    String android_id;
    private boolean receivedBrodCast;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        btnPrint = findViewById(R.id.btn);
        btnUpload = findViewById(R.id.btnUploadExcel);
        tableView = findViewById(R.id.tblLayout);
        btnUpload.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        try{
            TscUSB = new TSCUSBActivity();
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            prn = sharedpreferences.getString(PRNPATH,"");
            NofColumns = sharedpreferences.getInt(NOC,0);
            Date dt = new Date();
            Date yesterday = getYesterday();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String expiredtstr = sharedpreferences.getString(EXPIRE_DT,simpleDateFormat.format(yesterday));
            Date expireDt = simpleDateFormat.parse(expiredtstr);
            Date compare = new Date(dt.getYear(),dt.getMonth(),dt.getDate());
            Common.isActivated = expireDt.compareTo(compare)>=0;
            Common.expireDate = expireDt;
            android_id = android.provider.Settings.Secure.getString(HomeActivity.this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            if(!Common.isActivated){
                Common.isActivated = false;
                AppActivation appActivation = new AppActivation(HomeActivity.this,android_id,this);
                appActivation.CheckActivationStatus();
            }
            if(prn.isEmpty()){
                showCustomDialog("Please upload valid PRN file.");
            }
            if(NofColumns<=0){
                showCustomDialog("Please Enter valid No of Columns.");
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private Date getYesterday(){
        return new Date(System.currentTimeMillis()-24*60*60*1000);
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
    public void showCustomDialog(String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Warning");
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                Intent settingsPage = new Intent(HomeActivity.this,SettingsActivity.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
    public void showCustomDialog(String Message,boolean close) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Warning");
        dialogBuilder.setMessage("\n"+Message);
        if(close){
            dialogBuilder.setNeutralButton("Share Device ID", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    String shareBody =android_id;
                    String shareSub = "Share Device ID";
                    whatsappIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    whatsappIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    try {
                        startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(HomeActivity.this,"Whatsapp have not been installed.",Toast.LENGTH_LONG);
                    }
                    finally {
                        finish();
                        System.exit(0);
                    }
                }
            });
        }
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(close){
                    finish();
                    System.exit(0);
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
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
            case R.id.settings:
                Intent settingsPage = new Intent(this,SettingsActivity.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void PrintLabel() {
        try {
            if(excelData.size()>0){
                new ConnectLabelPrinter().execute("");
            }
            else {
                showCustomDialog("Warning","Please upload valid Excel to print.");
            }

        }
        catch (Exception ex) {
            showCustomDialog("Error",ex.getMessage());
        }
    }
    public boolean checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            return  Environment.isExternalStorageManager();
        }
        else{
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void requestStoragePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            try {
                Uri uri = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                storeageActivitytResultLanucher.launch(intent);
            } catch (Exception ex){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storeageActivitytResultLanucher.launch(intent);
            }
        }
        else{
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,1);
        }
    }
    private void UploadExcel(){
        try {
            excelData.clear();
            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
            fileintent.setType("*/*");
            startActivityForResult(fileintent, requestcode);
        } catch (Exception ex) {
            showCustomDialog("Error",ex.getMessage());
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                PrintLabel();
                break;
            case R.id.btnUploadExcel:
                UploadExcel();
                break;
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
                                showCustomDialog("Warning","Please select valid Excel file.");
                                return;
                            }
                            inStream.close();
                            Sheet sheet = wb.getSheetAt(0);
                            int counter=0;
                            int rowCount = sheet.getLastRowNum();
                            DecimalFormat formater = new DecimalFormat("#.###");
                            for(int i=0;i<=rowCount;i++){
                                Row row = sheet.getRow(i);
                                ArrayList<String> cellData = new ArrayList<>();
                                for(int j=0;j<row.getLastCellNum();j++) {
                                    Cell cell = row.getCell(j);
                                    String value = "";
                                    switch (cell.getCellType()){
                                        case NUMERIC:
                                            Double d = cell.getNumericCellValue();
                                            value = formater.format(d);
                                            break;
                                        case STRING:
                                            value=row.getCell(j).getStringCellValue();
                                            break;
                                    }
                                    cellData.add(value);
                                }
                                counter++;
                                excelData.add(cellData);
                            }
                            tableView.removeAllViews();
                            tableView.setStretchAllColumns(true);
                            tableView.bringToFront();
                            for(int i=0;i<excelData.size();i++){
                                ArrayList<String> row = excelData.get(i);
                                TableRow tr =  new TableRow(this);
                                for(int k=0;k<row.size();k++){
                                    TextView c1 = new TextView(this);
                                    c1.setText(row.get(k));
                                    c1.setTextColor(Color.BLUE);
                                    tr.addView(c1);
                                }
                                tableView.addView(tr);
                            }

                            //table.setHeaderAdapter(myHeaderAdapter);
                            showCustomDialog("Status","  "+counter+" - Rows Uploaded");
                        } catch (IOException e) {
                            showCustomDialog("Error",e.getMessage());
                            e.printStackTrace();
                        }

                    }
                } catch (Exception ex) {
                    showCustomDialog("Error",ex.getMessage());
                    ex.printStackTrace();
                    //lbl.setText(ex.getMessage().toString());
                }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
    public void ConnectUSB() {
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> mDeviceIterator = mDeviceList.values().iterator();
        UsbDevice mDevice = null;
        while (mDeviceIterator.hasNext()) {
            mDevice = mDeviceIterator.next();
            int interfaceCount = mDevice.getInterfaceCount();
            Toast.makeText(HomeActivity.this, "INTERFACE COUNT: " + String.valueOf(interfaceCount), Toast.LENGTH_SHORT).show();

            if (mDevice == null) {
                Toast.makeText(HomeActivity.this, "mDevice is null", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(HomeActivity.this, "USB Device found", Toast.LENGTH_SHORT).show();
            }
        }
        usbDevice = mDevice;
        if (usbManager != null) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    HomeActivity.this,
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0
            );
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(usbReceiver, filter, Context.RECEIVER_EXPORTED);
            usbManager.requestPermission(mDevice, permissionIntent);
        }
    }
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    if(usbManager.hasPermission(usbDevice) && !receivedBrodCast){
                        receivedBrodCast = true;
                        SendPrintLabelCommands(usbManager,usbDevice);
                    }
                }
            }
        }
    };

    private void SendPrintLabelCommands(UsbManager uManager, UsbDevice uDevice){
        try{
            TscUSB.openport(uManager,uDevice);
            //Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sample);
            //TscUSB.sendfile(this,uri);
            for(int i=0;i<commandToPrint.size();i++){
                TscUSB.sendcommand(commandToPrint.get(i));
            }
            TscUSB.closeport();
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        finally {
            receivedBrodCast = false;
            commandToPrint.clear();
            excelData.clear();
            tableView.removeAllViews();
        }
    }
    private ArrayList<String> CreateCommandToPrint(){
        ArrayList<String> command = new ArrayList<>();
        ArrayList<ArrayList<String>> datatoPrint = new ArrayList<>();
        for(int i=0;i<excelData.size();i++){
            ArrayList<String> row = excelData.get(i);
            String n = row.get(row.size()-1);
            Double noc = Double.parseDouble(n);
            row.remove(n);
            while (noc>0){
                datatoPrint.add(row);
                noc--;
            }
        }
        if(datatoPrint.size()>NofColumns){
            int div = datatoPrint.size() / NofColumns;
            while (div>0){
                String sr = prn;
                ArrayList<ArrayList<String>> splicelist =(ArrayList<ArrayList<String>>) datatoPrint.stream().limit(NofColumns).collect(Collectors.toList());
                for(int k=0;k<splicelist.size();k++){
                    ArrayList<String> row = splicelist.get(k);
                    for(int j=0;j<row.size();j++){
                        String clName = getColumnNameFromIndex(j + 1, k+1);
                        String value = row.get(j);
                        sr = StringUtils.replace(sr,"@"+clName,value);
                        //sr = sr.Replace("@" + clName, value);
                    }
                    datatoPrint.remove(row);
                }
                command.add(sr);
                div--;
            }
            if(datatoPrint.size()>0){
                String sr = prn;
                int pendingColumns = NofColumns;
                int rowCount = 1;
                for(int k=0;k<datatoPrint.size();k++){
                    ArrayList<String> row = datatoPrint.get(k);
                    for(int j=0;j<row.size();j++){
                        String clName = getColumnNameFromIndex(j + 1, k+1);
                        String value = row.get(j);
                        sr = StringUtils.replace(sr,"@"+clName,value);
                        //sr = sr.Replace("@" + clName, value);
                    }
                    pendingColumns--;
                }
                int j=pendingColumns;
                while (j<NofColumns)
                {
                    StringBuilder snew = new StringBuilder();
                    String[] sarray = sr.split("\n");
                    ArrayList<String> checkCol = new ArrayList<>();
                    for (int m = 0; m < NofColumns; m++)
                    {
                        String clName = getColumnNameFromIndex(m + 1, j);
                        clName = "@" + clName;
                        checkCol.add(clName);
                    }
                    for (int k = 0; k < sarray.length; k++)
                    {
                        String check = sarray[k];
                        if (!checkCol.stream().anyMatch(l -> check.contains(l)))
                        {
                            if (k == (sarray.length - 1))
                            {
                                snew.append(sarray[k]);
                            }
                            else
                            {
                                snew.append(sarray[k]).append("\n");
                            }
                        }
                    }
                    sr = snew.toString();
                    j++;
                }
                command.add(sr);
            }
        }
        else {
            String sr = prn;
            int pendingColumns = NofColumns;
            int rowCount = 1;
            for(int k=0;k<datatoPrint.size();k++){
                ArrayList<String> row = datatoPrint.get(k);
                for(int j=0;j<row.size();j++){
                    String clName = getColumnNameFromIndex(j + 1, k+1);
                    String value = row.get(j);
                    sr = StringUtils.replace(sr,"@"+clName,value);
                    //sr = sr.Replace("@" + clName, value);
                }
                pendingColumns--;
            }
            int j=pendingColumns;
            while (j<NofColumns)
            {
                StringBuilder snew = new StringBuilder();
                String[] sarray = sr.split("\n");
                ArrayList<String> checkCol = new ArrayList<>();
                for (int m = 0; m < NofColumns; m++)
                {
                    String clName = getColumnNameFromIndex(m + 1, j);
                    clName = "@" + clName;
                    checkCol.add(clName);
                }
                for (int k = 0; k < sarray.length; k++)
                {
                    String check = sarray[k];
                    if (!checkCol.stream().anyMatch(l -> check.contains(l)))
                    {
                        if (k == (sarray.length - 1))
                        {
                            snew.append(sarray[k]);
                        }
                        else
                        {
                            snew.append(sarray[k]).append("\n");
                        }
                    }
                }
                sr = snew.toString();
                j++;
            }
            command.add(sr);
        }
        return command;
    }
    private String getColumnNameFromIndex(int column, int row)
    {
        column--;
        String col = String.valueOf((char)('A' + (column % 26)));
        while (column >= 26)
        {
            column = (column / 26) - 1;
            col = (char) ('A' + (column % 26)) + col;
        }
        return col + "_" + row;
    }
    private void Print(ArrayList<String> cmd){
        try{
            commandToPrint = cmd;
            ConnectUSB();
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }

    public void ValidateActivationResponse(String response){
        if(!Common.isActivated){
            showCustomDialog("Your Android device "+android_id+" is not activated\n"+response,true);
        }
    }
    class ConnectLabelPrinter extends AsyncTask<String, Void, ArrayList<String>>
    {
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        @Override
        protected void onPreExecute()
        {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Printing Label");
            dialog.show();
            super.onPreExecute();
        }




        @Override
        protected ArrayList<String> doInBackground(String... params)
        {
            ArrayList<String> cmd = new ArrayList<>();
            try
            {
                cmd = CreateCommandToPrint();

            }
            catch (Exception e)
            {
                if(dialog.isShowing()){
                    dialog.cancel();
                }
                showCustomDialog("Error",e.getMessage());
            }
            return cmd;
        }

        @Override
        protected void onPostExecute(ArrayList<String> res)
        {
            Print(res);
            if(dialog.isShowing()){
                dialog.cancel();
            }
            super.onPostExecute(res);
        }
    }
}