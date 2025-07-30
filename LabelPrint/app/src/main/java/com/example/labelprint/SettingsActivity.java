package com.example.labelprint;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PRNPATH = "PRN";
    public static final String NOC = "NOC";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String PRINTER = "PRINTER";
    public UsbDevice selectedUSBDevice;
    EditText prnpath;
    EditText noc;
    Button btnUpload;
    Button btnSave;
    Spinner usbDevices;
    private MySharedPreferences sharedpreferences;
    private static int RESULT_LOAD_FILE = 1;
    private final ActivityResultLauncher<Intent> storeageActivitytResultLanucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){

                }
                else{

                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        try{
            prnpath = findViewById(R.id.prnfile);
            btnSave = findViewById(R.id.btnSaveSettings);
            btnUpload = findViewById(R.id.btnUploadprn);
            noc = findViewById(R.id.noc);
            usbDevices = findViewById(R.id.usbDevice);
            btnUpload.setOnClickListener(this);
            btnSave.setOnClickListener(this);
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            String prnfile = sharedpreferences.getString(PRNPATH,"");
            Integer nofcolumns = sharedpreferences.getInt(NOC,0);
            String usbDevice = sharedpreferences.getString(PRINTER,"");
            noc.setText(String.valueOf(nofcolumns));
            if(!prnfile.isEmpty()){
                prnpath.setText(prnfile);
            }
            int selectedindedusbdevice = 0;
            int k=0;
            ArrayList<String> usbdevicelist = GetUSBDevice();
            for(String d:usbdevicelist){
                String[] vas = d.split("~");
                if(vas[1].equals(usbDevice)){
                    selectedindedusbdevice = k;
                }
                k++;
            }
            ArrayAdapter usbAdaptor = new ArrayAdapter(this, android.R.layout.simple_spinner_item,usbdevicelist);
            usbAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            usbDevices.setAdapter(usbAdaptor);
            usbDevices.setSelection(selectedindedusbdevice);
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
    public UsbDevice GetUSBDevice(String searchName){
        UsbManager usbManager = (UsbManager) SettingsActivity.this.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> mDeviceIterator = mDeviceList.values().iterator();
        UsbDevice mDevice = null;
        while (mDeviceIterator.hasNext()) {
            mDevice = mDeviceIterator.next();
            if(mDevice!=null){
                String prName = mDevice.getProductName();
                if(searchName.equals("PRINTER") && prName==null){
                    break;
                }
                else if(prName!=null && prName.equals(searchName)){
                    break;
                }
            }
        }
        return mDevice;
    }
    public ArrayList<String> GetUSBDevice(){
        ArrayList<String> deviceList = new ArrayList<>();
        UsbManager usbManager = (UsbManager) SettingsActivity.this.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> mDeviceIterator = mDeviceList.values().iterator();
        UsbDevice mDevice = null;
        while (mDeviceIterator.hasNext()) {
            mDevice = mDeviceIterator.next();
            if(mDevice!=null){
                String manfuac = mDevice.getManufacturerName()!=null ? mDevice.getManufacturerName():"DEFAULT";
                String prName = mDevice.getProductName()!=null?mDevice.getProductName():"PRINTER";
                String devicename = manfuac+"~"+prName;
                deviceList.add(devicename);
            }
        }
        return deviceList;
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

    private void  SaveSettings(){
        try{
            String prnfile = prnpath.getText().toString();
            String nofcolumns = noc.getText().toString();
            if(prnfile.isEmpty()){
                showCustomDialog("Warning","PRN file path is empty. Please select upload valid PRN file.");
                return;
            }
            else if(nofcolumns.isEmpty()){
                showCustomDialog("Warning","Please enter valid no of columns.");
                return;
            }
            else {
                String usbselected = usbDevices.getSelectedItem().toString();
                String[] sb = usbselected.split("~");
                Common.usbDevice =  GetUSBDevice(sb[1]);
                sharedpreferences.putString(PRNPATH,prnfile);
                Integer nofc = Integer.parseInt(nofcolumns);
                sharedpreferences.putInt(NOC,nofc);
                sharedpreferences.commit();
                Toast.makeText(getApplicationContext(),"Settings Saved Successfully !",Toast.LENGTH_SHORT).show();
                Intent homepage = new Intent(this,HomeActivity.class);
                homepage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homepage);
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }
    private String readFile(Uri uri)
    {
        String myData = "";
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            br.close();
            inputStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return myData;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnUploadprn:
                if(!checkFilePermission()){
                    requestStoragePermission();
                }
                LoadFileUpload();
                break;
            case R.id.btnSaveSettings:
                SaveSettings();
                break;
        }
    }
    private void LoadFileUpload(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        startActivityForResult(i, RESULT_LOAD_FILE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_FILE && resultCode == RESULT_OK && null != data) {
            try{
                String path = data.getDataString();
                Uri uri = data.getData();
                String prn = readFile(uri);
                prnpath.setText(prn);
                /*Uri selectedFile = data.getData();
                String[] filePathColumn = { MediaStore.Files.FileColumns.RELATIVE_PATH };
                Cursor cursor = getContentResolver().query(selectedFile,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String prnfilePath = cursor.getString(columnIndex);
                cursor.close();*/
                //prnpath.setText(path);
            }
            catch (Exception ex){
                showCustomDialog("Error",ex.getMessage());
            }

        }
    }
    public void requestStoragePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            try {
                Uri uri = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                storeageActivitytResultLanucher.launch(intent);
            } catch (Exception ex){
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storeageActivitytResultLanucher.launch(intent);
            }
        }
        else{
            ActivityCompat.requestPermissions(SettingsActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,1);
        }
    }

    public boolean checkFilePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            return  Environment.isExternalStorageManager();
        }
        else{
            int write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }
}