package com.example.gatepass_new;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    GmsBarcodeScanner scanner;
    public static HomeActivity getInstance() {
        return instance;
    }
    public static HomeActivity instance;
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String EXPIRE_DT = "EXPIRE_DT";
    public static final String CustomDate = "Today";
    public static final String SlipNo = "SlipNo";
    private MySharedPreferences sharedpreferences;
    String android_id;
    private ImageButton btnScan;
    private EditText truckNumber;
    private EditText emptyTrolly;
    private EditText emptyBin;
    private EditText shopName;
    private EditText gateNo;
    private EditText other;
    private ImageButton btnPrint;
    private EditText vendorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        instance = this;
        try{
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
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
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String dtt = sharedpreferences.getString(CustomDate, format.format(date));
            Date da = format.parse(dtt);
            if (!da.equals(date)) {
                Date myDate = new Date();
                String prefData = format.format(myDate);
                sharedpreferences.putString(CustomDate, prefData);
                sharedpreferences.putString(SlipNo, "0");
                sharedpreferences.commit();
            }
            btnScan = (ImageButton) findViewById(R.id.ScanBtn);
            truckNumber = (EditText)findViewById(R.id.truckNo);
            btnPrint = (ImageButton) findViewById(R.id.printBtn);
            vendorName = (EditText)findViewById(R.id.vendorName);
            emptyTrolly = (EditText)findViewById(R.id.emptyTrollys);
            emptyBin = (EditText)findViewById(R.id.emptyBins);
            shopName = (EditText)findViewById(R.id.shopName);
            gateNo  = (EditText)findViewById(R.id.gateNo);
            other = (EditText)findViewById(R.id.others);
            gateNo.setText("4210");
            btnScan.setOnClickListener(this);
            btnPrint.setOnClickListener(this);
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE,Barcode.FORMAT_CODE_128)
                    .build();
            scanner = GmsBarcodeScanning.getClient(HomeActivity.this,options);
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
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private Date getYesterday(){
        return new Date(System.currentTimeMillis()-24*60*60*1000);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }
    public void showCustomDialog(String title, String Message,boolean... closeapp) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        TextView titleview = new TextView(this);
        titleview.setText(title);
        titleview.setBackgroundColor(Color.WHITE);
        titleview.setPadding(10, 10, 10, 10);
        titleview.setGravity(Gravity.CENTER);
        titleview.setTextColor(Color.BLACK);
        titleview.setTypeface(titleview.getTypeface(), Typeface.BOLD_ITALIC);
        titleview.setTextSize(20);
        dialogBuilder.setCustomTitle(titleview);
        dialogBuilder.setMessage("\n"+Message);
        if(closeapp.length>1 && closeapp[1]){
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
                if(closeapp.length>0 && closeapp[0]){
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
    public void ValidateActivationResponse(String response){
        if(!Common.isActivated){
            showCustomDialog("Msg","Your Android device "+android_id+" is not activated\n"+response,true,true);
        }
    }
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }
    private void ScanQRCode(){
        if(scanner!=null){
            scanner.startScan().addOnSuccessListener(
                            barcode -> {
                                try {
                                    String[] rs = barcode.getRawValue().split("@");
                                    truckNumber.setText(rs[0]);
                                    vendorName.setText(rs[1]);
                                    shopName.setText(rs[2]);
                                } catch (Exception e) {
                                    showCustomDialog("Exception",e.getMessage(),false);
                                    truckNumber.setText("");
                                    e.printStackTrace();
                                }
                            })
                    .addOnCanceledListener(
                            () -> {
                                Toast.makeText(HomeActivity.this,"Scanning was cancelled",Toast.LENGTH_SHORT).show();
                            })
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(HomeActivity.this,"Failed To Scan. Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            });
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.printBtn:
                this.PrintPass();
                break;
            case R.id.ScanBtn:
                try{
                    if(!hasCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        ScanQRCode();
                    }
                }
                catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage());
                }
                break;
        }
    }
    public void  PrintPass(){
        String trNumber = this.truckNumber.getText().toString();
        String othr = other.getText().toString();
        String empTrolly = this.emptyTrolly.getText().toString();
        String empBin = this.emptyBin.getText().toString();
        if(trNumber.isEmpty()){
            showCustomDialog("Warning","Please check the Input Fields.!",false);
        }
        else {
            try{
                GateEntry ge = new GateEntry();
                ge.trNumber = trNumber;
                ge.empBin = empBin;
                ge.empTrolly = empTrolly;
                ge.othr = othr;
                ge.vnName = this.vendorName.getText().toString();
                ge.shopName = this.shopName.getText().toString();
                ge.gateNo = this.gateNo.getText().toString();
                ge.slipNo = sharedpreferences.getString(SlipNo,"");
                PrinterUtil printerUtil = new PrinterUtil(HomeActivity.this,this);
                printerUtil.geEntry = ge;
                printerUtil.Print();
            }
            catch (Exception ex){
                showCustomDialog("Error",ex.getMessage());
            }
        }

    }
    public void RefreshViews(int sl){
        this.truckNumber.setText("");
        this.vendorName.setText("");
        this.emptyTrolly.setText("");
        this.emptyBin.setText("");
        this.gateNo.setText("4210");
        this.shopName.setText("");
        this.other.setText("");
        sharedpreferences.putString(SlipNo,String.valueOf(sl));
        sharedpreferences.commit();
    }
}