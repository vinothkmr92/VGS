package com.example.vinoth.vgsposbt;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    public static final String INCLUDEMRP = "INCLUDEMRP";
    public static final String ADDRESSLINE = "ADDRESSLINE";
    public static final String IS3INCH = "IS3INCH";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String HEADERMSG = "HEADERMSG";
    public static final String FOOTERMSG= "FOOTERMSG";
    public static final String PRINTERIP = "PRINTERIP";
    public static final String BLUETOOTNAME = "BLUETOOTHNAME";
    public static final String PRINTKOT = "PRINTKOT";
    public static final String KOTPRINTERIP = "KOTPRINTERIP";
    public static final String BILLCOPIES = "BILLCOPIES";
    public static final String MULTILANG = "MULTILANG";
    public static final String PRINTTYPE = "WIFI";
    public static final String USBDEVICENAME = "USBDEVICE";
    public static final String BTSCALENAME = "BTSCALENAME";
    public static final String DEVICEUSERNAME = "DEVICEUSERNAME";
    public static final String DEFBINWT = "DEFBINWT";
    private static int RESULT_LOAD_IMAGE = 1;
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    private final ActivityResultLauncher<Intent> storeageActivitytResultLanucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){

                }
                else{

                }
            });
    EditText defBinWeight;
    EditText deviceUserName;
    Spinner btScaleName;
    EditText editTextHeaderMsg;
    EditText editTextFooterMsg;
    EditText txtViewuserpasscode;
    RadioButton radioButton2Inch;
    RadioButton radioButton3Inch;
    RadioButton radioButton4Inch;
    RadioButton radioButtonBluetooth;
    RadioButton radioButtonWifi;
    RadioButton radioButtonUSB;
    TextView txtViewUSBDevice;
    TextView txtViewBluetooth;
    TextView txtViewPrinterIP;
    EditText editTextPrinterIP;
    TextView txtviewkotselection;
    EditText editTextbillcopies;
    DatabaseHelper dbHelper;
    TextView rptsizetextview;
    Spinner spinnerbluetothDevice;
    Spinner spinnerUsbDevice;
    Button saveBtn;
    Button loadPictureBtn;
    ImageView imageView;
    EditText editTextaddressline;
    Set<BluetoothDevice> pairedDevices = null;
    ArrayList<String> bluethootnamelist;
    ImageButton btnclerlogo;
    EditText deviceidView;
    Switch multilang;
    private MySharedPreferences sharedpreferences;
    private BluetoothAdapter mBluetoothAdapter = null;
    private Dialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dbHelper = new DatabaseHelper(this);
        editTextHeaderMsg = (EditText) findViewById(R.id.headerMsg);
        editTextFooterMsg = (EditText) findViewById(R.id.footerMsg);
        btnclerlogo = (ImageButton)findViewById(R.id.btnclearlogo);
        radioButtonBluetooth = (RadioButton) findViewById(R.id.radioBtnBluetooth);
        radioButtonWifi = (RadioButton) findViewById(R.id.radioBtnWifi);
        radioButtonUSB = findViewById(R.id.radioBtnUSB);
        txtViewBluetooth = (TextView) findViewById(R.id.txtViewBlutooth);
        txtViewUSBDevice = findViewById(R.id.txtViewUSBDevice);
        txtViewPrinterIP = (TextView) findViewById(R.id.txtViewIPAddress);
        editTextPrinterIP = (EditText) findViewById(R.id.printerIP);
        rptsizetextview = (TextView)findViewById(R.id.rptsizetxt);
        radioButton2Inch = (RadioButton)findViewById(R.id.radiobtn2Inch);
        radioButton3Inch = (RadioButton)findViewById(R.id.radiobtn3Inch);
        radioButton4Inch = (RadioButton)findViewById(R.id.radiobtn4Inch);
        spinnerbluetothDevice = (Spinner) findViewById(R.id.bltDevice);
        spinnerUsbDevice = findViewById(R.id.usbDevice);
        editTextbillcopies = (EditText)findViewById(R.id.billcopies);
        editTextaddressline = (EditText)findViewById(R.id.addressMsg);
        loadPictureBtn = (Button)findViewById(R.id.buttonLoadPicture);
        imageView = (ImageView)findViewById(R.id.imgView);
        deviceidView = (EditText)findViewById(R.id.deviceid);
        multilang = findViewById(R.id.checkBoxMultiLanguage);
        btScaleName = findViewById(R.id.bltWtDevice);
        deviceUserName = findViewById(R.id.deviceUser);
        defBinWeight = findViewById(R.id.binwt);
        String android_id = android.provider.Settings.Secure.getString(Settings.this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        deviceidView.setText(android_id);
        txtViewuserpasscode = (EditText) findViewById(R.id.userpasscode);
        txtViewuserpasscode.setText(Common.userPasscode);
        byte[] ic = dbHelper.GetReceiptIcon();
        if(ic!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(ic,0,ic.length);
            imageView.setImageBitmap(bitmap);
        }
        btnclerlogo.setVisibility(ic!=null ? View.VISIBLE:View.GONE);
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        String headerMsg = sharedpreferences.getString(HEADERMSG,"");
        String footerMsg = sharedpreferences.getString(FOOTERMSG,"");
        String printerip = sharedpreferences.getString(PRINTERIP,"");
        String bluetothName = sharedpreferences.getString(BLUETOOTNAME,"");
        String printType = sharedpreferences.getString(PRINTTYPE,"WIFI");
        String rptsize = sharedpreferences.getString(IS3INCH,"3");
        String billcopies = sharedpreferences.getString(BILLCOPIES,"1");
        String addressline = sharedpreferences.getString(ADDRESSLINE,"");
        String isMultiLang = sharedpreferences.getString(MULTILANG,"NO");
        String usbDevice = sharedpreferences.getString(USBDEVICENAME,"");
        multilang.setChecked(isMultiLang.equalsIgnoreCase("YES"));
        String defbin = sharedpreferences.getString(DEFBINWT,"");
        String scalename = sharedpreferences.getString(BTSCALENAME,"");
        String devName = sharedpreferences.getString(DEVICEUSERNAME,"");
        defBinWeight.setText(defbin);
        deviceUserName.setText(devName);
        switch (rptsize){
            case  "2":
                radioButton2Inch.setChecked(true);
                break;
            case "3":
                radioButton3Inch.setChecked(true);
                break;
            case "4":
                radioButton4Inch.setChecked(true);
                break;
        }
        editTextbillcopies.setText(billcopies);
        editTextHeaderMsg.setText(headerMsg);
        editTextFooterMsg.setText(footerMsg);
        editTextPrinterIP.setText(printerip);
        editTextaddressline.setText(addressline);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        radioButtonBluetooth.setChecked(false);
        radioButtonWifi.setChecked(true);
        txtViewBluetooth.setVisibility(View.GONE);
        spinnerbluetothDevice.setVisibility(View.GONE);
        rptsizetextview.setVisibility(View.GONE);
        radioButton2Inch.setVisibility(View.GONE);
        radioButton3Inch.setVisibility(View.GONE);
        radioButton4Inch.setVisibility(View.GONE);
        saveBtn = (Button) findViewById(R.id.btnSave);
        radioButton2Inch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButton2Inch.isChecked()){
                    radioButton3Inch.setChecked(false);
                    radioButton4Inch.setChecked(false);
                }
            }
        });
        radioButton3Inch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButton3Inch.isChecked()){
                    radioButton4Inch.setChecked(false);
                    radioButton2Inch.setChecked(false);
                }
            }
        });
        radioButton4Inch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButton4Inch.isChecked()){
                    radioButton2Inch.setChecked(false);
                    radioButton3Inch.setChecked(false);
                }
            }
        });

        radioButtonWifi.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButtonUSB.setChecked(false);
                    radioButtonBluetooth.setChecked(false);
                    txtViewUSBDevice.setVisibility(View.GONE);
                    spinnerUsbDevice.setVisibility(View.GONE);
                    txtViewBluetooth.setVisibility(View.GONE);
                    spinnerbluetothDevice.setVisibility(View.GONE);
                    rptsizetextview.setVisibility(View.GONE);
                    radioButton2Inch.setVisibility(View.GONE);
                    radioButton3Inch.setVisibility(View.GONE);
                    radioButton4Inch.setVisibility(View.GONE);
                }
                else{
                    txtViewBluetooth.setVisibility(View.VISIBLE);
                    spinnerbluetothDevice.setVisibility(View.VISIBLE);
                    rptsizetextview.setVisibility(View.VISIBLE);
                    radioButton2Inch.setVisibility(View.VISIBLE);
                    radioButton3Inch.setVisibility(View.VISIBLE);
                    radioButton4Inch.setVisibility(View.VISIBLE);
                }
            }
        });
        radioButtonBluetooth.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked){
                     radioButtonUSB.setChecked(false);
                     radioButtonWifi.setChecked(false);
                     txtViewUSBDevice.setVisibility(View.GONE);
                     spinnerUsbDevice.setVisibility(View.GONE);
                     txtViewPrinterIP.setVisibility(View.GONE);
                     editTextPrinterIP.setVisibility(View.GONE);
                 }
                 else{
                     txtViewPrinterIP.setVisibility(View.VISIBLE);
                     editTextPrinterIP.setVisibility(View.VISIBLE);
                 }
            }
        });
        radioButtonUSB.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radioButtonWifi.setChecked(!isChecked);
                radioButtonBluetooth.setChecked(!isChecked);
                txtViewPrinterIP.setVisibility(isChecked ? View.GONE:View.VISIBLE);
                editTextPrinterIP.setVisibility(isChecked ? View.GONE:View.VISIBLE);
                txtViewBluetooth.setVisibility(isChecked ? View.GONE:View.VISIBLE);
                spinnerbluetothDevice.setVisibility(isChecked? View.GONE:View.VISIBLE);
                txtViewUSBDevice.setVisibility(isChecked ? View.VISIBLE: View.GONE);
                spinnerUsbDevice.setVisibility(isChecked ? View.VISIBLE: View.GONE);
            }
        });
        if(!checkPermission()){
            ActivityCompat.requestPermissions(Settings.this,PERMISSIONS_BLUETOOTH
                    ,1);
        }
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        bluethootnamelist = new ArrayList<>();
        int selectedindex=0;
        int i=0;
        int scaleselindex = 0;
        for (BluetoothDevice device : pairedDevices) {
            bluethootnamelist.add(device.getName());
            if(device.getName().equals(bluetothName)){
                selectedindex=i;
            }
            if(device.getName().equals(scalename)){
                scaleselindex =i;
            }
            i++;
        }
        ArrayAdapter scadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,bluethootnamelist);
        scadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        btScaleName.setAdapter(scadapter);
        btScaleName.setSelection(scaleselindex);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,bluethootnamelist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbluetothDevice.setAdapter(adapter);
        spinnerbluetothDevice.setSelection(selectedindex);
        int selectedindedusbdevice=0;
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
        spinnerUsbDevice.setAdapter(usbAdaptor);
        spinnerUsbDevice.setSelection(selectedindedusbdevice);
        switch (printType){
            case "WIFI":
                radioButtonWifi.setChecked(true);
                radioButtonBluetooth.setChecked(false);
                txtViewBluetooth.setVisibility(View.GONE);
                spinnerbluetothDevice.setVisibility(View.GONE);
                rptsizetextview.setVisibility(View.GONE);
                radioButton2Inch.setVisibility(View.GONE);
                radioButton3Inch.setVisibility(View.GONE);
                radioButton4Inch.setVisibility(View.GONE);
                txtViewUSBDevice.setVisibility(View.GONE);
                spinnerUsbDevice.setVisibility(View.GONE);
                break;
            case "USB":
                radioButtonUSB.setChecked(true);
                radioButtonWifi.setChecked(false);
                radioButtonBluetooth.setChecked(false);
                txtViewPrinterIP.setVisibility(View.GONE);
                editTextPrinterIP.setVisibility(View.GONE);
                txtViewUSBDevice.setVisibility(View.VISIBLE);
                spinnerUsbDevice.setVisibility(View.VISIBLE);
                break;
            default:
                radioButtonWifi.setChecked(false);
                radioButtonBluetooth.setChecked(true);
                txtViewPrinterIP.setVisibility(View.GONE);
                editTextPrinterIP.setVisibility(View.GONE);
                txtViewUSBDevice.setVisibility(View.GONE);
                spinnerUsbDevice.setVisibility(View.GONE);
                break;
        }
        saveBtn.setOnClickListener(this);
        btnclerlogo.setOnClickListener(this);
        loadPictureBtn.setOnClickListener(this);
        if(!Common.openSettings){
            PasscodeActivity.isUserPasscode = false;
            Intent page = new Intent(this,PasscodeActivity.class);
            startActivity(page);
        }
        if(!checkFilePermission()){
            requestStoragePermission();
        }
        txtViewuserpasscode.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                String txt = editTextaddressline.getText().toString();
                if(hasFocus && !txt.isEmpty()){
                    ((EditText)v).selectAll();
                }
            }
        });
        editTextbillcopies.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                String txt = editTextbillcopies.getText().toString();
                if(hasFocus && !txt.isEmpty()){
                    ((EditText)v).selectAll();
                }
            }
        });
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
            ActivityCompat.requestPermissions(Settings.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,1);
        }
    }

    public ArrayList<String> GetUSBDevice(){
        ArrayList<String> deviceList = new ArrayList<>();
        UsbManager usbManager = (UsbManager) Settings.this.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> mDeviceIterator = mDeviceList.values().iterator();
        UsbDevice mDevice = null;
        while (mDeviceIterator.hasNext()) {
            mDevice = mDeviceIterator.next();
            if(mDevice!=null){
                String devicename = mDevice.getManufacturerName()+"~"+mDevice.getProductName();
                deviceList.add(devicename);
            }
        }
        return deviceList;
    }
    public boolean checkFilePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            return  Environment.isExternalStorageManager();
        }
        else{
            int write = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    private boolean checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            int bluetooth = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT);
            int scan = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        }
        else{
            int bluetooth = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH);
            int scan = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        }
    }
    public void showCustomDialog(String title, String Message, final boolean wait) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(wait){
                    GoHome(); // ClosingAlert();//do something with edt.getText().toString();
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.setCancelable(false);
        b.show();
    }
    @Override
    public void onBackPressed() {
        GoHome();
    }
    public  void  GoHome(){
        Intent page = new Intent(this,HomeActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }

    private void SaveSettings(){
        try{
            String headerMsg = editTextHeaderMsg.getText().toString();
            String footerMsg = editTextFooterMsg.getText().toString();
            String printer = editTextPrinterIP.getText().toString();
            String scalebt = btScaleName.getSelectedItem().toString();
            String defbin = defBinWeight.getText().toString();
            String devuser =  deviceUserName.getText().toString();
            String bluetoothName = radioButtonBluetooth.isChecked()? spinnerbluetothDevice.getSelectedItem().toString(): " ";
            String printType = radioButtonUSB.isChecked() ? "USB" : radioButtonWifi.isChecked()?"WIFI": "BLUETOOTH";
            String rptSize = radioButton2Inch.isChecked()?"2":radioButton3Inch.isChecked()?"3":radioButton4Inch.isChecked()?"4":"3";
            String nocopies = editTextbillcopies.getText().toString();
            String addressline = editTextaddressline.getText().toString();
            String isMultilang = multilang.isChecked()?"YES":"NO";
            String usbselected = radioButtonUSB.isChecked()?spinnerUsbDevice.getSelectedItem().toString():" ~ ";
            String[] sb = usbselected.split("~");
            sharedpreferences.putString(USBDEVICENAME,sb[1]);
            sharedpreferences.putString(MULTILANG,isMultilang);
            sharedpreferences.putString(HEADERMSG,headerMsg);
            sharedpreferences.putString(FOOTERMSG,footerMsg);
            sharedpreferences.putString(PRINTERIP,printer);
            sharedpreferences.putString(BLUETOOTNAME,bluetoothName);
            sharedpreferences.putString(PRINTTYPE,printType);
            sharedpreferences.putString(IS3INCH,rptSize);
            sharedpreferences.putString(BILLCOPIES,nocopies);
            sharedpreferences.putString(ADDRESSLINE,addressline);
            sharedpreferences.putString(BTSCALENAME,scalebt);
            sharedpreferences.putString(DEVICEUSERNAME,devuser);
            sharedpreferences.putString(DEFBINWT,defbin);
            sharedpreferences.commit();
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            if(drawable!=null){
                Bitmap bitmap = drawable.getBitmap();
                if(bitmap!=null){
                    ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,bytearray);
                    byte[] image = bytearray.toByteArray();
                    dbHelper.InsertIcon(image);
                    Common.shopLogo = bitmap;
                }
                else{
                    dbHelper.DeleteIcon();
                    Common.shopLogo = null;
                }
            }
            else{
                dbHelper.DeleteIcon();
                Common.shopLogo = null;
            }
            int billcopies = Integer.parseInt(nocopies);
            Common.printerIP = printer;
            Common.headerMeg = headerMsg;
            Common.addressline = addressline;
            Common.footerMsg = footerMsg;
            Common.billcopies = billcopies;
            Common.bluetoothDeviceName = bluetoothName;
            Common.printType = printType;
            Common.RptSize = rptSize;
            Common.userPasscode = txtViewuserpasscode.getText().toString();
            Common.usbDeviceName = sb[1];
            Common.scalebtName = scalebt;
            Common.devUser = devuser;
            Common.defBinWt = Double.valueOf(defbin);
            Toast.makeText(getApplicationContext(),"Settings Details Updated.!",Toast.LENGTH_SHORT).show();
            GoHome();
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage().toString(),false);
        }
    }
    private void LoadPictureToUpload(){
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.putExtra("crop", "true");
        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            try{
                Uri selectedImage = data.getData();
                Bitmap photo = null;
                if(selectedImage!=null){
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    photo = BitmapFactory.decodeFile(picturePath);
                }
                else {
                    photo = (Bitmap) data.getExtras().get("data");
                }
                if(photo!=null){
                    imageView.setImageBitmap(photo);
                    btnclerlogo.setVisibility(View.VISIBLE);
                }
                else {
                    showCustomDialog("Warning","Could not load image. Please contact support team.",false);
                }
            }
            catch (Exception ex){
                showCustomDialog("Error",ex.getMessage(),false);
            }

        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                SaveSettings();
                break;
            case R.id.buttonLoadPicture:
                LoadPictureToUpload();
                break;
            case R.id.btnclearlogo:
                imageView.setImageBitmap(null);
                imageView.setImageDrawable(null);
                break;
        }

    }
}