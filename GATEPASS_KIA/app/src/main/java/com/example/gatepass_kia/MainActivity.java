package com.example.gatepass_kia;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String CustomDate = "Today";
    public static final String SlipNo = "SlipNo";
    public static final String EXPIRE_DT = "EXPIRE_DT";
    public static final String APIURL = "APIURL";
    public static final String PRINTER = "PRINTER";
    private Button btnScan;
    private EditText truckNumber;
    private EditText emptyTrolly;
    private EditText emptyBin;
    private EditText shopName;
    private EditText gateNo;
    private EditText other;
    private EditText securityid;
    private Button btnPrint;
    private CheckBox bypasssap;
    private String android_id;
    private String serviceVersion;
    private EditText vendorName;
    private String printerName;
    private String soapurl;
    public SharedPreferences sharedpreferences;
    GmsBarcodeScanner scanner;
    private boolean isWebCallDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        try{
            isWebCallDone = false;
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String dct = sharedpreferences.getString(CustomDate, format.format(date));
            printerName = sharedpreferences.getString(PRINTER,"");
            soapurl = sharedpreferences.getString(APIURL,"");
            Date da = format.parse(dct);
            if (!da.equals(date)) {
                Date myDate = new Date();
                String prefData = format.format(myDate);
                editor.putString(CustomDate, prefData);
                editor.putString(SlipNo, "0");
                editor.commit();
            }
            Date dt = new Date();
            Date yesterday = getYesterday();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String expiredtstr = sharedpreferences.getString(EXPIRE_DT, simpleDateFormat.format(yesterday));
            Date expireDt = simpleDateFormat.parse(expiredtstr);
            Date compare = new Date(dt.getYear(), dt.getMonth(), dt.getDate());
            Common.isActivated = expireDt.compareTo(compare) >= 0;
            Common.expireDate = expireDt;
            android_id = android.provider.Settings.Secure.getString(MainActivity.this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            if (!Common.isActivated) {
                Common.isActivated = false;
                AppActivation appActivation = new AppActivation(MainActivity.this, android_id, this);
                appActivation.CheckActivationStatus();
            }
            btnScan = findViewById(R.id.ScanBtn);
            truckNumber = findViewById(R.id.truckNo);
            btnPrint =  findViewById(R.id.printBtn);
            vendorName = findViewById(R.id.vendorName);
            emptyTrolly = findViewById(R.id.emptyTrollys);
            emptyBin = findViewById(R.id.emptyBins);
            securityid = findViewById(R.id.securityID);
            btnScan.setOnClickListener(this);
            btnPrint.setOnClickListener(this);
            shopName = findViewById(R.id.shopName);
            gateNo  = findViewById(R.id.gateNo);
            other = findViewById(R.id.others);
            bypasssap = findViewById(R.id.byPassCheckbox);
            truckNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if(i == EditorInfo.IME_ACTION_GO){
                        try {
                            String req = "";
                            String trNumber = truckNumber.getText().toString();
                            String othr = other.getText().toString();
                            String empTrolly = emptyTrolly.getText().toString();
                            String empBin = emptyBin.getText().toString();
                            String secuirtyid = securityid.getText().toString();
                            if(secuirtyid.isEmpty()){
                                showCustomDialog("Warning","Please Enter valid Security ID");
                                return  false;
                            }
                            else{
                                req = trNumber+"~"+empBin+"~"+empTrolly+"~"+othr+"~"+secuirtyid;
                                if(!bypasssap.isChecked()) {
                                    new CallWebService().execute(req);
                                }
                            }
                        } catch (Exception e) {
                            showCustomDialog("WebService Error",e.getMessage());
                            truckNumber.setText("");
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            });
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE,Barcode.FORMAT_CODE_128)
                    .build();
            scanner = GmsBarcodeScanning.getClient(MainActivity.this,options);
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        finally {
            instace = this;
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void ScanQRCode(){
        if(scanner!=null){
            scanner.startScan().addOnSuccessListener(
                            barcode -> {
                                String scanResult = barcode.getRawValue();
                                try {
                                    String sid = this.securityid.getText().toString();
                                    if(sid.isEmpty()){
                                        securityid.setText(scanResult);
                                        emptyTrolly.requestFocus();
                                    }
                                    else{
                                        truckNumber.setText(scanResult);
                                        String req = "";
                                        String trNumber = this.truckNumber.getText().toString();
                                        String securityid = this.securityid.getText().toString();
                                        String othr = other.getText().toString();
                                        String empTrolly = this.emptyTrolly.getText().toString();
                                        String empBin = this.emptyBin.getText().toString();
                                        if(!bypasssap.isChecked()){
                                            req = trNumber+"~"+empBin+"~"+empTrolly+"~"+othr+"~"+securityid;
                                            new CallWebService().execute(req);
                                        }
                                    }

                                } catch (Exception e) {
                                    showCustomDialog("WebService Error",e.getMessage());
                                    truckNumber.setText("");
                                    e.printStackTrace();
                                }
                            })
                    .addOnCanceledListener(
                            () -> {
                                Toast.makeText(MainActivity.this,"Scanning was cancelled",Toast.LENGTH_SHORT).show();
                            })
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(MainActivity.this,"Failed To Scan. Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            });
        }
    }
    public void ValidateActivationResponse(String response) {
        if (!Common.isActivated) {
            showCustomDialog("Msg", "Your Android device " + android_id + " is not activated\n" + response, true, true);
        }
    }
    public void showCustomDialog(String title, String Message, boolean... closeapp) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n" + Message);
        if (closeapp.length > 1 && closeapp[1]) {
            dialogBuilder.setNeutralButton("Share Device ID", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    String shareBody = android_id;
                    String shareSub = "Share Device ID";
                    whatsappIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    whatsappIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    try {
                        startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(MainActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_LONG);
                    } finally {
                        finish();
                        System.exit(0);
                    }
                }
            });
        }
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (closeapp.length > 0 && closeapp[0]) {
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
        getMenuInflater().inflate(R.menu.mainmenu, menu);//Menu Resource, Menu
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }
    public void RefreshViews(){
        this.truckNumber.setText("");
        this.vendorName.setText("");
        this.emptyTrolly.setText("");
        this.emptyBin.setText("");
        this.gateNo.setText("");
        this.shopName.setText("");
        this.other.setText("");
        this.securityid.setText("");
        isWebCallDone = false;
        String slipNo = sharedpreferences.getString(SlipNo,"");
        if(slipNo.isEmpty()){
            slipNo = "0";
        }
        int sl = Integer.parseInt(slipNo);
        sl += 1;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SlipNo,String.valueOf(sl));
        editor.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.refresh:
                this.truckNumber.setText("");
                this.vendorName.setText("");
                this.emptyTrolly.setText("");
                this.emptyBin.setText("");
                this.gateNo.setText("");
                this.shopName.setText("");
                this.other.setText("");
                this.securityid.setText("");
                isWebCallDone = false;
                return true;
            case R.id.settingsMenu:
                Intent settingspage = new Intent(this, Settings.class);
                settingspage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingspage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private Date getYesterday() {
        return new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    }
    public static MainActivity instace;
    public static MainActivity getInstance() {
        return instace;
    }

    private void  PrintGatePass(){
        int sl = 0;
        try {
            String Securityno = this.securityid.getText().toString();
            String trNumber = this.truckNumber.getText().toString();
            String vnName = this.vendorName.getText().toString();
            String empTrolly = this.emptyTrolly.getText().toString();
            String empBin = this.emptyBin.getText().toString();
            String shopName = this.shopName.getText().toString();
            String gateNo = this.gateNo.getText().toString();
            String slipNo = sharedpreferences.getString(SlipNo,"");
            String othr = other.getText().toString();
            if(slipNo.isEmpty()){
                slipNo = "0";
            }
            sl = Integer.parseInt(slipNo);
            sl += 1;
            slipNo = String.format("%06d", sl);
            if (trNumber.trim().isEmpty()) {
                showCustomDialog("Warning","No Truck Details to Print.!");
                //Toast.makeText(this, "No Truck Details to Print.!", Toast.LENGTH_LONG).show();
                return;
            }
            if(empTrolly.isEmpty() || empBin.isEmpty()){
                if(empBin.isEmpty()) {
                    showCustomDialog("Warning","Please Enter Empty Bins Count.");
                    return;
                }
                if(empTrolly.isEmpty()){
                    showCustomDialog("Warning","Please Enter Empty Trolly Count.");
                    return;
                }
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            ReceiptData rpt = new ReceiptData();
            rpt.date = format.format(date);
            rpt.slipno = slipNo;
            rpt.securityno = Securityno;
            rpt.VendorName = vnName;
            rpt.gateno = gateNo;
            rpt.shopname = shopName;
            rpt.empBin = empBin;
            rpt.empTrolly = empTrolly;
            rpt.others = othr;
            rpt.truckNo = trNumber;
            PrinterUtil printerUtil = new PrinterUtil(this,MainActivity.this);
            printerUtil.receiptData = rpt;
            printerUtil.Print(printerName);
        }
        catch (RemoteException ex){
            showCustomDialog("Print Error",ex.getMessage());
            //Toast.makeText(MainActivity.this, e.getMessage(), 1).show();
            ex.printStackTrace();
        }
        catch (Exception e) {
            showCustomDialog("Print Error",e.getMessage());
            //Toast.makeText(MainActivity.this, e.getMessage(), 1).show();
            e.printStackTrace();
        }
    }
    public void  Print(){
        String trNumber = this.truckNumber.getText().toString();
        String sid = this.securityid.getText().toString();
        if(sid.isEmpty()){
            showCustomDialog("Warning","Please Enter Valid Security ID!");
        }
        if(trNumber.isEmpty()){
            showCustomDialog("Warning","Please check the Input Fields.!");
        }
        else {
            if(bypasssap.isChecked()){
                PrintGatePass();
            }
            else {
                if(isWebCallDone){
                    PrintGatePass();
                }
                else {
                    showCustomDialog("Warning","SAP Call Failed. Please Scan the Truck Number Again.");
                }
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ScanBtn:
                ScanQRCode();
                break;

        }
    }
    class CallWebService extends AsyncTask<String,Void,String> {
        public  static final String NAMESPACE = "http://tempuri.org/";
        public  static final   String METHOD_NAME = "GetSAPResponse";
        public  static final  String SOAP_ACTION = "http://tempuri.org/IGATEPASS_WCF/GetSAPResponse";
        //public  static final  String URL = "http://10.54.203.155:1001/GATEPASS_WCF.svc";
        public  int Timeout = 30000;
        String response;
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPostExecute(String res){
            if(dialog.isShowing()){
                dialog.cancel();
            }
            if(!res.isEmpty()){
                try{
                    GatePassResponse gatePassResponse = new GatePassResponse();
                    String[] response = res.split("~");
                    gatePassResponse.vendorName = response[0].toString();
                    gatePassResponse.shopName = response[1].toString();
                    gatePassResponse.gateNo = response[2].toString();
                    gatePassResponse.msgFromSAP = response[3].toString();
                    gatePassResponse.plant = response[4].toString();
                    String s = response[7].toString().toUpperCase();
                    gatePassResponse.isResponseOK = s.equals("OK");
                    isWebCallDone = gatePassResponse.isResponseOK;
                    if(response.length>8){
                        gatePassResponse.exception = response[8].toString();
                        String error = gatePassResponse.exception.trim();
                        if(!error.isEmpty()){
                            showCustomDialog("SAP Call Error",gatePassResponse.exception);
                            truckNumber.setText("");
                            return;
                        }
                    }
                    if(gatePassResponse.vendorName.isEmpty() || gatePassResponse.shopName.isEmpty()){
                        showCustomDialog("INVALID TRUCK","Invalid Truck No. Truck Details were not found in SAP. Please contact concern department");
                        truckNumber.setText("");
                        return;
                    }
                    vendorName.setText(gatePassResponse.vendorName);
                    shopName.setText(gatePassResponse.shopName);
                    gateNo.setText(gatePassResponse.gateNo);
                }
                catch (Exception ex){
                    showCustomDialog("SAP Call Error",ex.getMessage());
                    ex.printStackTrace();
                }
            }
            else {
                showCustomDialog("SAP Call Error","Unable to get Response from SAP. Please Contact IT team.");// Toast.makeText(MainActivity.this,"Error in WCF CAll",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Calling SAP");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Please Wait ....");
            dialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(String... params){
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("truckNumber",params[0]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(Request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(soapurl);
            String res="";
            try
            {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapPrimitive results = (SoapPrimitive)envelope.getResponse();
                res = results.toString();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                res = " ~ ~ ~ ~ ~ ~ ~" + e.getMessage().toString();
            }
            return  res;
        }
    }
}