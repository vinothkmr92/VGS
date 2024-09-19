package com.example.mobis_gatepass;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.imin.printerlib.IminPrintUtils;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String EXPIRE_DT = "EXPIRE_DT";
    public static final String CustomDate = "Today";
    public static final String SlipNo = "SlipNo";
    public static final String APIURL = "APIURL";
    public static final String DEVICEUSER = "DEVICEUSER";
    public static final String SKIPSAP = "SKIPSAP";
    public static String android_id="";
    public String webApiUrl="";
    public String deviceUser="";

    private ImageButton btnScan;
    private EditText truckNumber;
    private EditText emptyTrolly;
    private EditText emptyBin;
    private EditText shopName;
    private EditText gateNo;
    private EditText other;
    private ImageButton btnPrint;
    private CheckBox bypasssap;

    private EditText vendorName;

    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    public  static IminPrintUtils mIminPrintUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        checkStorageManagerPermission();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 0);
        }
        try{

            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            Date dt = new Date();
            Date yesterday = getYesterday();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String expiredtstr = sharedpreferences.getString(EXPIRE_DT,simpleDateFormat.format(yesterday));
            String strSkipsap = sharedpreferences.getString(SKIPSAP,"N");
            Common.skipSAP = strSkipsap.equals("Y");
            Date expireDt = simpleDateFormat.parse(expiredtstr);
            Date compare = new Date(dt.getYear(),dt.getMonth(),dt.getDate());
            Common.isActivated = expireDt.compareTo(compare)>=0;
            Common.expireDate = expireDt;
            if(!Common.isActivated){
                boolean internetav = Is_InternetWorking();
                if(internetav){
                    Common.isActivated = false;
                    android_id = android.provider.Settings.Secure.getString(HomeActivity.this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                    AppActivation appActivation = new AppActivation(HomeActivity.this,android_id,this);
                    appActivation.CheckActivationStatus();
                }
                else{
                    showCustomDialog("Msg","Please connect to internet and Try again.",true);
                }
            }
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String dtt = sharedpreferences.getString(CustomDate, format.format(date));
            webApiUrl = sharedpreferences.getString(APIURL,webApiUrl);
            deviceUser = sharedpreferences.getString(DEVICEUSER,deviceUser);
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
            if(!Common.skipSAP && (webApiUrl.isEmpty() || deviceUser.isEmpty())){
                GetAPIDtl();
            }
            mIminPrintUtils = IminPrintUtils.getInstance(HomeActivity.this);
            IminPrintUtils.getInstance(HomeActivity.this).initPrinter(IminPrintUtils.PrintConnectType.SPI);
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage(),true);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkStorageManagerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
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
                return true;
            case R.id.settings:
                GetAPIDtl();
                return true;
            case R.id.refreshPage:
                this.truckNumber.setText("");
                this.vendorName.setText("");
                this.emptyTrolly.setText("");
                this.emptyBin.setText("");
                this.gateNo.setText("4210");
                this.shopName.setText("");
                this.other.setText("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Date getYesterday(){
        return new Date(System.currentTimeMillis()-24*60*60*1000);
    }
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }
    public boolean Is_InternetWorking(){
        boolean connected = false;
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
            else
                connected = false;
        }
        catch (Exception ex) {
            connected = false;
        }
        return connected;
    }
    public void showCustomDialog(String title, String Message, final Boolean close) {
        MaterialAlertDialogBuilder dialog =  new MaterialAlertDialogBuilder(HomeActivity.this,
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    public void showCustomDialog(String title, String Message, final boolean... closeapp) {
        MaterialAlertDialogBuilder dialogBuilder =  new MaterialAlertDialogBuilder(HomeActivity.this,
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
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
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }
    public void ValidateActivationResponse(String response){
        if(!Common.isActivated){
            showCustomDialog("Msg","Your Android device "+android_id+" is not activated\n"+response,true,true);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode , resultCode ,data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                try {
                    String[] rs = result.getContents().split("@");
                    truckNumber.setText(rs[0]);
                    vendorName.setText(rs[1]);
                    shopName.setText(rs[2]);
                } catch (Exception e) {
                    showCustomDialog("Exception",e.getMessage(),false);
                    truckNumber.setText("");
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void GetAPIDtl(){
        AlertDialog.Builder dialog =  new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_input_dialog, null);
        dialog.setView(dialogView);
        final EditText weburl = (EditText)dialogView.findViewById(R.id.weburl);
        final EditText deviceuser = (EditText)dialogView.findViewById(R.id.deviceuser);
        final CheckBox skipsap = (CheckBox)dialogView.findViewById(R.id.skipsap);
        weburl.setText(webApiUrl);
        deviceuser.setText(deviceUser);
        skipsap.setChecked(Common.skipSAP);
        dialog.setTitle("Enter API Details");
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String url = weburl.getText().toString();
                String user = deviceuser.getText().toString();
                String strskipsap = skipsap.isChecked()?"Y":"N";
                sharedpreferences = MySharedPreferences.getInstance(HomeActivity.this,MyPREFERENCES);
                sharedpreferences.putString(APIURL,url);
                sharedpreferences.putString(DEVICEUSER,user);
                sharedpreferences.putString(SKIPSAP,strskipsap);
                sharedpreferences.commit();
                webApiUrl = url;
                deviceUser = user;
                Common.skipSAP = skipsap.isChecked();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
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
                        requestPermission();
                    }
                    else {
                        IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
                        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
                        intentIntegrator.setBeepEnabled(true);
                        intentIntegrator.setOrientationLocked(false);
                        intentIntegrator.setCameraId(0);
                        intentIntegrator.setPrompt("Scan QR Code");
                        intentIntegrator.initiateScan();
                    }
                }
                catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage(),false);
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
            String req = this.gateNo.getText().toString()+"~"+this.shopName.getText().toString()+"~"+
                    this.vendorName.getText().toString()+"~"+this.truckNumber.getText().toString()+"~"+
                    this.emptyTrolly.getText().toString()+"~"+this.emptyBin.getText().toString()+"~"+
                    other.getText().toString()+"~"+deviceUser;

            if(Common.skipSAP){
                PrintGatePass();
            }
            else {
                new SaveGatePass().execute(req);
            }
        }

    }
    public  void PrintGatePass() {
        int sl = 0;
        try {
            String trNumber = this.truckNumber.getText().toString();
            String vnName = this.vendorName.getText().toString();
            String empTrolly = this.emptyTrolly.getText().toString();
            String empBin = this.emptyBin.getText().toString();
            String shopName = this.shopName.getText().toString();
            String gateNo = this.gateNo.getText().toString();
            String slipNo = sharedpreferences.getString(SlipNo,"");
            String othr = other.getText().toString();

            if(slipNo.length()==0){
                slipNo = "0";
            }
            sl = Integer.parseInt(slipNo);
            sl += 1;
            slipNo = String.format("%06d", sl);
            if (trNumber.trim().length()==0) {
                showCustomDialog("Warning","No Truck Details to Print.!",false);
                //Toast.makeText(this, "No Truck Details to Print.!", Toast.LENGTH_LONG).show();
                return;
            }
            if(empTrolly.length()==0 || empBin.length()==0){
                if(empBin.length()==0) {
                    showCustomDialog("Warning","Please Enter Empty Bins Count.",false);
                    return;
                }
                if(empTrolly.length()==0){
                    showCustomDialog("Warning","Please Enter Empty Trolley Count.",false);
                    return;
                }
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm aaa", Locale.getDefault());
            Date date = new Date();
            Bitmap hyndaiLogo = BitmapFactory.decodeResource(this.getResources(),R.drawable.hyundai_logo);
            mIminPrintUtils.setTextTypeface(Typeface.MONOSPACE);
            mIminPrintUtils.setAlignment(1);
            mIminPrintUtils.printSingleBitmap(hyndaiLogo);
            mIminPrintUtils.setTextSize(25);
            mIminPrintUtils.setTextStyle(1);
            mIminPrintUtils.printText("MOBIS MOTOR INDIA LIMITED");
            mIminPrintUtils.setTextSize(30);
            mIminPrintUtils.setTextStyle(1);
            mIminPrintUtils.printText("GATE PASS");
            mIminPrintUtils.setTextStyle(0);
            mIminPrintUtils.setTextSize(28);
            mIminPrintUtils.printText("VENDOR EMPTIES RETURN");
            mIminPrintUtils.setAlignment(0);
            mIminPrintUtils.setTextSize(21);
            mIminPrintUtils.printText("SLIP NO  : "+slipNo);
            mIminPrintUtils.printText("DATE     :" + format.format(date));
            mIminPrintUtils.printText("MO/IM DIV: "+ gateNo);
            mIminPrintUtils.printText("VENDOR CD: "+shopName);
            mIminPrintUtils.printText("VENDOR NM: " + vnName);
            mIminPrintUtils.printText("TRUCK NO : " + trNumber);
            mIminPrintUtils.printText("-----------------------------");
            mIminPrintUtils.setTextStyle(1);
            mIminPrintUtils.setTextSize(23);
            mIminPrintUtils.printText("ITEM                   QTY");
            mIminPrintUtils.setTextStyle(0);
            mIminPrintUtils.setTextSize(21);
            mIminPrintUtils.printText("-----------------------------");
            empTrolly = StringUtils.leftPad(empTrolly,14);
            empTrolly = empTrolly.substring(0,14);
            empBin = StringUtils.leftPad(empBin,14);
            empBin = empBin.substring(0,14);
            othr = StringUtils.leftPad(othr,14);
            othr = othr.substring(0,14);
            mIminPrintUtils.printText("EMPTY TROLLEYS " + empTrolly);
            mIminPrintUtils.printText("EMPTY BINS     " + empBin);
            mIminPrintUtils.printText("REJECTION/EXINV" + othr);
            mIminPrintUtils.printText("-----------------------------");
            mIminPrintUtils.printText("SECURITY\\GA");
            mIminPrintUtils.printAndFeedPaper(200);
            Toast.makeText(this, "Print Queued Successfully.!", Toast.LENGTH_LONG).show();
            this.truckNumber.setText("");
            this.vendorName.setText("");
            this.emptyTrolly.setText("");
            this.emptyBin.setText("");
            this.gateNo.setText("4210");
            this.shopName.setText("");
            this.other.setText("");
            sharedpreferences.putString(SlipNo,String.valueOf(sl));
            sharedpreferences.commit();
        } catch (Exception e) {
            showCustomDialog("Print Error",e.getMessage(),false);
            e.printStackTrace();
        }
    }

    class SaveGatePass extends AsyncTask<String,Void,String> {
        public  static final String NAMESPACE = "http://tempuri.org/";
        public  static final   String METHOD_NAME = "SaveGatePassEntry";
        public  static final  String SOAP_ACTION = "http://tempuri.org/IGATEPASS_WCF/SaveGatePassEntry";
        public  String URL ;
        // public  static final  String URL = "http://192.168.0.3//GATEPASS_WCF.svc";
        public  int Timeout = 30000;
        String response;
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        @Override
        protected void onPostExecute(String res){
            //  super.onPostExecute(res);
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            if(!res.isEmpty()){
                try{
                    String result = res;
                    if(res.equals("1")){
                        showCustomDialog("Success","Data Successfully Saved.",false);
                        PrintGatePass();
                    }
                    else if(res.startsWith("EXCEPTION")) {
                        showCustomDialog("Exception",res,false);
                    }
                    else {
                        showCustomDialog("Warning",res,false);
                    }
                    //truckNumber.setText("");
                }
                catch (Exception ex){
                    showCustomDialog("SAP CALL Error",ex.getMessage(),false);
                    ex.printStackTrace();
                }
            }
            else {
                showCustomDialog("Web Call Error","Unable to get Response from Web API. Please Contact IT team.",false);
                // Toast.makeText(MainActivity.this,"Error in WCF CAll",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            URL = webApiUrl;
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Connecting to SAP.....");
            dialog.show();
        }

        protected String doInBackground(String... params){
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("request",params[0]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(Request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
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
                res = "EXCEPTION:" + e.getMessage().toString();
            }
            return  res;
        }
    }
}