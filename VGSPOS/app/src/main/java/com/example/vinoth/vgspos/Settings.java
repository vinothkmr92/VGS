package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Set;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    public static final String INCLUDEMRP = "INCLUDEMRP";
    private static int RESULT_LOAD_IMAGE = 1;
    EditText editTextHeaderMsg;
    EditText editTextFooterMsg;
    EditText txtViewuserpasscode;

    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    RadioButton radioButton2Inch;
    RadioButton radioButton3Inch;
    RadioButton radioButton4Inch;
    RadioButton radioButtonBluetooth;
    RadioButton radioButtonWifi;
    TextView txtViewBluetooth;
    TextView txtViewPrinterIP;
    EditText editTextPrinterIP;
    TextView txtviewkotselection;
    TextView txtviewkotprinter;
    EditText editTextbillcopies;
    EditText editTextkotprinterip;
    DatabaseHelper dbHelper;
    CheckBox enablekot;
    public static final String ADDRESSLINE = "ADDRESSLINE";
    public static final String IS3INCH = "IS3INCH";
    TextView rptsizetextview;
    Spinner spinnerbluetothDevice;
    Button saveBtn;
    CheckBox checkBoxIncludeMRP;
    Button loadPictureBtn;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String HEADERMSG = "HEADERMSG";
    public static final String FOOTERMSG= "FOOTERMSG";
    public static final String PRINTERIP = "PRINTERIP";
    public static final String BLUETOOTNAME = "BLUETOOTHNAME";
    public static final String PRINTKOT = "PRINTKOT";
    public static final String KOTPRINTERIP = "KOTPRINTERIP";
    public static final String BILLCOPIES = "BILLCOPIES";
    public static final String MULTILANG = "MULTILANG";
    ImageView imageView;
    EditText editTextaddressline;
    public static final String ISWIFI = "ISWIFI";
    Set<BluetoothDevice> pairedDevices = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private final ActivityResultLauncher<Intent> storeageActivitytResultLanucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){

                }
                else{

                }
            });
    ArrayList<String> bluethootnamelist;
    private Dialog progressBar;
    ImageButton btnclerlogo;
    EditText deviceidView;
    CheckBox multilang;
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
        txtViewBluetooth = (TextView) findViewById(R.id.txtViewBlutooth);
        txtViewPrinterIP = (TextView) findViewById(R.id.txtViewIPAddress);
        editTextPrinterIP = (EditText) findViewById(R.id.printerIP);
        rptsizetextview = (TextView)findViewById(R.id.rptsizetxt);
        radioButton2Inch = (RadioButton)findViewById(R.id.radiobtn2Inch);
        radioButton3Inch = (RadioButton)findViewById(R.id.radiobtn3Inch);
        radioButton4Inch = (RadioButton)findViewById(R.id.radiobtn4Inch);
        spinnerbluetothDevice = (Spinner) findViewById(R.id.bltDevice);
        txtviewkotprinter = (TextView)findViewById(R.id.txtViewIPAddressKOT);
        editTextkotprinterip = (EditText) findViewById(R.id.KOTprinterIP);
        enablekot = (CheckBox) findViewById(R.id.enableKOT);
        editTextbillcopies = (EditText)findViewById(R.id.billcopies);
        editTextaddressline = (EditText)findViewById(R.id.addressMsg);
        checkBoxIncludeMRP = (CheckBox)findViewById(R.id.includeMRP);
        loadPictureBtn = (Button)findViewById(R.id.buttonLoadPicture);
        imageView = (ImageView)findViewById(R.id.imgView);
        deviceidView = (EditText)findViewById(R.id.deviceid);
        multilang = findViewById(R.id.checkBoxMultiLanguage);
        String android_id = android.provider.Settings.Secure.getString(Settings.this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        deviceidView.setText(android_id);
        txtViewuserpasscode = (EditText) findViewById(R.id.userpasscode);
        txtViewuserpasscode.setText(Common.userPasscode);
        byte[] ic = dbHelper.GetReceiptIcon();
        if(ic!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(ic,0,ic.length);
            imageView.setImageBitmap(bitmap);
        }
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        String headerMsg = sharedpreferences.getString(HEADERMSG,"");
        String footerMsg = sharedpreferences.getString(FOOTERMSG,"");
        String printerip = sharedpreferences.getString(PRINTERIP,"");
        String bluetothName = sharedpreferences.getString(BLUETOOTNAME,"");
        String isWifi = sharedpreferences.getString(ISWIFI,"YES");
        String rptsize = sharedpreferences.getString(IS3INCH,"3");
        String printkot = sharedpreferences.getString(PRINTKOT,"NO");
        String kotprinterip = sharedpreferences.getString(KOTPRINTERIP,"");
        String billcopies = sharedpreferences.getString(BILLCOPIES,"1");
        String addressline = sharedpreferences.getString(ADDRESSLINE,"");
        String includeMRP = sharedpreferences.getString(INCLUDEMRP,"NO");
        String isMultiLang = sharedpreferences.getString(MULTILANG,"NO");
        enablekot.setChecked(printkot.equalsIgnoreCase("YES"));
        multilang.setChecked(isMultiLang.equalsIgnoreCase("YES"));
        editTextkotprinterip.setText(kotprinterip);
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
        checkBoxIncludeMRP.setChecked(includeMRP.equalsIgnoreCase("YES"));
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        radioButtonBluetooth.setChecked(false);
        radioButtonWifi.setChecked(true);
        txtViewBluetooth.setVisibility(View.INVISIBLE);
        spinnerbluetothDevice.setVisibility(View.INVISIBLE);
        rptsizetextview.setVisibility(View.INVISIBLE);
        radioButton2Inch.setVisibility(View.INVISIBLE);
        radioButton3Inch.setVisibility(View.INVISIBLE);
        radioButton4Inch.setVisibility(View.INVISIBLE);
        saveBtn = (Button) findViewById(R.id.btnSave);
        radioButton2Inch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButton2Inch.isChecked()){
                    radioButton3Inch.setChecked(false);
                    radioButton4Inch.setChecked(false);
                    checkBoxIncludeMRP.setVisibility(View.INVISIBLE);
                }
            }
        });
        radioButton3Inch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButton3Inch.isChecked()){
                    checkBoxIncludeMRP.setVisibility(View.VISIBLE);
                    radioButton4Inch.setChecked(false);
                    radioButton2Inch.setChecked(false);
                }
                else{
                    checkBoxIncludeMRP.setVisibility(View.INVISIBLE);
                }
            }
        });
        radioButton4Inch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButton4Inch.isChecked()){
                    checkBoxIncludeMRP.setVisibility(View.VISIBLE);
                    radioButton2Inch.setChecked(false);
                    radioButton3Inch.setChecked(false);
                }
                else{
                    checkBoxIncludeMRP.setVisibility(View.INVISIBLE);
                }
            }
        });

        radioButtonWifi.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButtonBluetooth.setChecked(false);
                    txtViewBluetooth.setVisibility(View.INVISIBLE);
                    spinnerbluetothDevice.setVisibility(View.INVISIBLE);
                    rptsizetextview.setVisibility(View.INVISIBLE);
                    radioButton2Inch.setVisibility(View.INVISIBLE);
                    radioButton3Inch.setVisibility(View.INVISIBLE);
                    radioButton4Inch.setVisibility(View.INVISIBLE);
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
                     radioButtonWifi.setChecked(false);
                     txtViewPrinterIP.setVisibility(View.INVISIBLE);
                     editTextPrinterIP.setVisibility(View.INVISIBLE);
                     txtviewkotprinter.setVisibility(View.INVISIBLE);
                     editTextkotprinterip.setVisibility(View.INVISIBLE);
                 }
                 else{
                     txtViewPrinterIP.setVisibility(View.VISIBLE);
                     editTextPrinterIP.setVisibility(View.VISIBLE);
                     txtviewkotprinter.setVisibility(View.VISIBLE);
                     editTextkotprinterip.setVisibility(View.VISIBLE);
                 }
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
        for (BluetoothDevice device : pairedDevices) {
            bluethootnamelist.add(device.getName());
            if(device.getName().equals(bluetothName)){
                selectedindex=i;
            }
            i++;
        }
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,bluethootnamelist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbluetothDevice.setAdapter(adapter);
        spinnerbluetothDevice.setSelection(selectedindex);
        if(isWifi.equalsIgnoreCase("YES")){
            radioButtonWifi.setChecked(true);
            radioButtonBluetooth.setChecked(false);
            txtViewBluetooth.setVisibility(View.INVISIBLE);
            spinnerbluetothDevice.setVisibility(View.INVISIBLE);
            rptsizetextview.setVisibility(View.INVISIBLE);
            radioButton2Inch.setVisibility(View.INVISIBLE);
            radioButton3Inch.setVisibility(View.INVISIBLE);
            radioButton4Inch.setVisibility(View.INVISIBLE);
        }
        else{
            radioButtonWifi.setChecked(false);
            radioButtonBluetooth.setChecked(true);
            txtViewPrinterIP.setVisibility(View.INVISIBLE);
            editTextPrinterIP.setVisibility(View.INVISIBLE);
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
            String bluetoothName = radioButtonBluetooth.isChecked()? spinnerbluetothDevice.getSelectedItem().toString(): " ";
            String isWifi = radioButtonWifi.isChecked() ? "YES":"NO";
            String rptSize = radioButton2Inch.isChecked()?"2":radioButton3Inch.isChecked()?"3":radioButton4Inch.isChecked()?"4":"3";
            String printKOT = enablekot.isChecked()?"YES":"NO";
            String includeMRP = checkBoxIncludeMRP.isChecked()?"YES":"NO";
            String kotprinterip = editTextkotprinterip.getText().toString();
            String nocopies = editTextbillcopies.getText().toString();
            String addressline = editTextaddressline.getText().toString();
            String isMultilang = multilang.isChecked()?"YES":"NO";
            sharedpreferences.putString(MULTILANG,isMultilang);
            sharedpreferences.putString(HEADERMSG,headerMsg);
            sharedpreferences.putString(FOOTERMSG,footerMsg);
            sharedpreferences.putString(PRINTERIP,printer);
            sharedpreferences.putString(BLUETOOTNAME,bluetoothName);
            sharedpreferences.putString(ISWIFI,isWifi);
            sharedpreferences.putString(IS3INCH,rptSize);
            sharedpreferences.putString(PRINTKOT,printKOT);
            sharedpreferences.putString(KOTPRINTERIP,kotprinterip);
            sharedpreferences.putString(BILLCOPIES,nocopies);
            sharedpreferences.putString(ADDRESSLINE,addressline);
            sharedpreferences.putString(INCLUDEMRP,includeMRP);
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
            Common.includeMRPinReceipt = includeMRP.equalsIgnoreCase("YES");
            Common.isWifiPrint = radioButtonWifi.isChecked();
            Common.RptSize = rptSize;
            Common.printKOT = enablekot.isChecked();
            Common.kotprinterIP = kotprinterip;
            Common.userPasscode = txtViewuserpasscode.getText().toString();
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
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
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