package com.example.gatepass.gatepass;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ngx.mp100sdk.Enums.Alignments;
import com.ngx.mp100sdk.Intefaces.INGXCallback;
import com.ngx.mp100sdk.NGXPrinter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


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
    private Dialog progressBar;
    private boolean isWebCallDone;

    private IntentIntegrator qrScan;
    public static NGXPrinter ngxPrinter = NGXPrinter.getNgxPrinterInstance();
    private INGXCallback ingxCallback;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String CustomDate = "Today";
    public static final String SlipNo = "SlipNo";
    public static final String isActivated = "IsActivated";
    public SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String dt = sharedpreferences.getString(CustomDate, format.format(date));
            Date da = format.parse(dt);
            isWebCallDone = false;
            if (!da.equals(date)) {
                Date myDate = new Date();
                String prefData = format.format(myDate);
                editor.putString(CustomDate, prefData);
                editor.putString(SlipNo, "0");
                editor.commit();
            }
            btnScan = (ImageButton) findViewById(R.id.ScanBtn);
            truckNumber = (EditText)findViewById(R.id.truckNo);
            btnPrint = (ImageButton) findViewById(R.id.printBtn);
            vendorName = (EditText)findViewById(R.id.vendorName);
            emptyTrolly = (EditText)findViewById(R.id.emptyTrollys);
            emptyBin = (EditText)findViewById(R.id.emptyBins);
            qrScan = new IntentIntegrator(this);
            btnScan.setOnClickListener(this);
            btnPrint.setOnClickListener(this);
            shopName = (EditText)findViewById(R.id.shopName);
            gateNo  = (EditText)findViewById(R.id.gateNo);
            other = (EditText)findViewById(R.id.others);
            progressBar = new Dialog(MainActivity.this);
            progressBar.setContentView(R.layout.custom_progress_dialog);
            progressBar.setTitle(R.string.dialog_title);
            bypasssap = (CheckBox)findViewById(R.id.byPassCheckbox);
            gateNo.setText("4210");

            truckNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if(i == EditorInfo.IME_ACTION_GO){
                        try {
                            //  truckNumber.setText(result.getContents().toString());
                            String req = "";
                            String trNumber = truckNumber.getText().toString();
                            String othr = other.getText().toString();
                            String empTrolly = emptyTrolly.getText().toString();
                            String empBin = emptyBin.getText().toString();
                        } catch (Exception e) {
                            showCustomDialog("WebService Error",e.getMessage());
                            truckNumber.setText("");
                            e.printStackTrace();
                            //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                        }
                        finally {
                        } //do whatever you want
                    }
                    return false;
                }
            });
//for reading


                this.ingxCallback = new INGXCallback() {
                    public void onRunResult(boolean isSuccess) {
                        Log.i("NGX", "onRunResult:" + isSuccess);
                    }

                    public void onReturnString(String result) {
                        Log.i("NGX", "onReturnString:" + result);
                    }

                    public void onRaiseException(int code, String msg) {
                        Log.i("NGX", "onRaiseException:" + code + ":" + msg);
                    }
                };
                ngxPrinter.initService(this, this.ingxCallback);



        } catch (Exception e) {
            showCustomDialog("Initialize Error",e.getMessage());
        }
        finally {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                return true;
            case R.id.refreshPage:
                this.isWebCallDone = false;
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
    @Override
    public void onBackPressed(){
        //Toast.makeText(getApplicationContext(),"You Are Not Allowed to Exit the App", Toast.LENGTH_SHORT).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

       // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        //dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           // public void onClick(DialogInterface dialog, int whichButton) {
             //   //pass
            //}
        //});
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void  PrintPass(){

        String req = "";
        String trNumber = this.truckNumber.getText().toString();
        String othr = other.getText().toString();
        String empTrolly = this.emptyTrolly.getText().toString();
        String empBin = this.emptyBin.getText().toString();
        if(trNumber.isEmpty()){
            showCustomDialog("Warining","Please check the Input Fields.!");
        }
        else {
                PrintGatePass();
        }

    }
    public  void PrintGatePass() {
        int sl = 0;

        try {
           //new CallWebService().execute("  ");
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
            ngxPrinter.setDefault();
            Bitmap hyndaiLogo = BitmapFactory.decodeResource(this.getResources(),R.drawable.hyundai_logo);
            ngxPrinter.printImage(hyndaiLogo);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("HYUNDAI MOTOR INDIA LIMITED",Alignments.CENTER,28);
            ngxPrinter.setStyleDoubleWidth();
            ngxPrinter.printText("GATE PASS", Alignments.CENTER, 30);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("VENDOR EMPTIES RETURN", Alignments.CENTER, 28);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("SLIP NO  : "+slipNo,Alignments.LEFT,24);
            ngxPrinter.printText("DATE     : " + format.format(date), Alignments.LEFT, 24);
            ngxPrinter.printText("MO/IM DIV: "+ gateNo,Alignments.LEFT,24);
            ngxPrinter.printText("VENDOR CD: "+shopName,Alignments.LEFT,24);
            ngxPrinter.printText("VENDOR NM: " + vnName, Alignments.LEFT, 24);
            ngxPrinter.printText("TRUCK NO : " + trNumber, Alignments.LEFT, 24);
            ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
            ngxPrinter.printText("ITEM                     QTY", Alignments.LEFT, 24);
            ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
            ngxPrinter.printText("EMPTY TROLLEYS           " + empTrolly, Alignments.LEFT, 24);
            ngxPrinter.printText("EMPTY BINS               " + empBin, Alignments.LEFT, 24);
            ngxPrinter.printText("REJECTION/EXINV          " + othr, Alignments.LEFT, 24);
            ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
            ngxPrinter.printText("SECURITY\\GA");
            ngxPrinter.printText("                              ");
            ngxPrinter.printText("                              ");
            ngxPrinter.setDefault();
            Toast.makeText(this, "Print Queued Scuessfully.!", Toast.LENGTH_LONG).show();
            this.truckNumber.setText("");
            this.vendorName.setText("");
            this.emptyTrolly.setText("");
            this.emptyBin.setText("");
            this.gateNo.setText("");
            this.shopName.setText("");
            this.other.setText("");
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(SlipNo,String.valueOf(sl));
            editor.commit();
        } catch (Exception e) {
            showCustomDialog("Print Error",e.getMessage());
            //Toast.makeText(MainActivity.this, e.getMessage(), 1).show();
            e.printStackTrace();
        }
        finally {

        }
    }
    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    String[] rs = result.getContents().split("@");
                    truckNumber.setText(rs[0]);
                    vendorName.setText(rs[1]);
                    shopName.setText(rs[2]);
                } catch (Exception e) {
                    showCustomDialog("Exception",e.getMessage());
                    truckNumber.setText("");
                    e.printStackTrace();
                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
                finally {
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void ScanQRCode(View view){

        //String req = "";
        String trNumber = this.truckNumber.getText().toString();
        String othr = other.getText().toString();
        String empTrolly = this.emptyTrolly.getText().toString();
        String empBin = this.emptyBin.getText().toString();
        if( othr.isEmpty() || empTrolly.isEmpty() || empBin.isEmpty()){
            showCustomDialog("Warining","Please check the Input Fields.!");
        }
        else {

            if(trNumber.isEmpty()) {

                qrScan.initiateScan();
            }
            else {
                try {
                    //  truckNumber.setText(result.getContents().toString());
                    String req = "";
                    //String trNumber = truckNumber.getText().toString();
                    //String othr = other.getText().toString();
                    //String empTrolly = emptyTrolly.getText().toString();
                    //String empBin = emptyBin.getText().toString();
                    if(!bypasssap.isChecked()){
                        req = trNumber+"~"+empBin+"~"+empTrolly+"~"+othr;
                        //new CallWebService().execute(req);
                    }

                } catch (Exception e) {
                    showCustomDialog("WebService Error",e.getMessage());
                    truckNumber.setText("");
                    e.printStackTrace();

                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

                }
                finally {
                }
            }
        }



    }



    class SaveGatePass extends AsyncTask<String,Void,String> {
        public  static final String NAMESPACE = "http://tempuri.org/";
        public  static final   String METHOD_NAME = "SaveGatePassEntry";
        public  static final  String SOAP_ACTION = "http://tempuri.org/IGATEPASS_WCF/SaveGatePassEntry";
        public  static final  String URL = "http://10.54.203.152/hmindia/GATEPASS_WCF.svc";
        // public  static final  String URL = "http://192.168.0.3//GATEPASS_WCF.svc";
        public  int Timeout = 30000;
        String response;
        @Override
        protected void onPostExecute(String res){
            //  super.onPostExecute(res);
            progressBar.cancel();
            if(!res.isEmpty()){
                try{
                    String result = res;
                    if(res.equals("1")){
                        showCustomDialog("Sucess","Data Sucessfully Saved in SAP.");
                    }
                    else if(res.startsWith("EXCEPTION")) {
                        showCustomDialog("Exception from SAP Call",res);
                    }
                    else {
                        showCustomDialog("Warning","Unable to Save Data into SAP. Contact IT Team. Return Code:"+res);
                    }
                    //truckNumber.setText("");
                }
                catch (Exception ex){
                    showCustomDialog("SAP CALL Error",ex.getMessage());
                    ex.printStackTrace();
                }
            }
            else {
                showCustomDialog("SAP CALL Error","Unable to get Response from SAP. Please Contact IT team.");
                // Toast.makeText(MainActivity.this,"Error in WCF CAll",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        protected String doInBackground(String... params){


            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);



            /*
             * Set the category to be the argument of the web service method
             *
             * */

            Request.addProperty("request",params[0]);

            /*
             * Set the web service envelope
             *
             * */
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(Request);

            //envelope.addMapping(NAMESPACE, "GatePassResponse",new GatePassResponse().getClass());
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            // AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(URL);
            /*
             * Call the web service and retrieve result ... how luvly <3
             *
             * */
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

    class CallWebService extends AsyncTask<String,Void,String>{
      public  static final String NAMESPACE = "http://tempuri.org/";
        public  static final   String METHOD_NAME = "GetSAPResponse";
        public  static final  String SOAP_ACTION = "http://tempuri.org/IGATEPASS_WCF/GetSAPResponse";
        public  static final  String URL = "http://10.54.203.152/hmindia/GATEPASS_WCF.svc";
        //public  static final  String URL = "http://192.168.1.6/GATEPASS_WCF.svc";
        public  int Timeout = 30000;
          String response;
        @Override
        protected void onPostExecute(String res){
               //  super.onPostExecute(res);
            progressBar.cancel();
            //showCustomDialog("RESPONSE",res);
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
                    if(s.equals("OK")) {
                        gatePassResponse.isResponseOK = true;
                        isWebCallDone = true;
                    }
                    else {
                        gatePassResponse.isResponseOK = false;
                        isWebCallDone = false;
                    }
                    if(response.length>8){
                        gatePassResponse.exception = response[8].toString();
                        String error = gatePassResponse.exception.trim();
                        if(!error.isEmpty()){
                            showCustomDialog("SAP CALL Error",gatePassResponse.exception);
                            truckNumber.setText("");
                            return;
                            //Toast.makeText(MainActivity.this,gatePassResponse.exception,Toast.LENGTH_LONG).show();
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
                    showCustomDialog("SAP CALL Error",ex.getMessage());
                    ex.printStackTrace();
                }


            }
            else {
                showCustomDialog("SAP CALL Error","Unable to get Response from SAP. Please Contact IT team.");
               // Toast.makeText(MainActivity.this,"Error in WCF CAll",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        protected String doInBackground(String... params){


            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);



        /*
         * Set the category to be the argument of the web service method
         *
         * */

            Request.addProperty("truckNumber",params[0]);

        /*
         * Set the web service envelope
         *
         * */
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(Request);

            //envelope.addMapping(NAMESPACE, "GatePassResponse",new GatePassResponse().getClass());
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
           // AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(URL);
        /*
         * Call the web service and retrieve result ... how luvly <3
         *
         * */
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



    @Override
    public void onClick(View view) {
          switch (view.getId()){
              case R.id.printBtn:
                  this.PrintPass();
                  break;
              case R.id.ScanBtn:
                  this.ScanQRCode(view);
                  break;

          }
    }
    class Login extends AsyncTask<String, Void, String> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void onPreExecute() {
            super.onPreExecute();
          //  progressBar.show();
        }

        @Override
        public void onPostExecute(String result) {
          //  progressBar.cancel();
            showCustomDialog(result,"Message");

        }


        @Override
        protected String doInBackground(String... strings) {
            String msg = "";
            Connection conn = null;
            Statement st = null;
            try {
                //STEP 2: Register JDBC driver
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");

                    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://vgsclouddbserver.database.windows.net:1433;database=VGS_CLOUD;user=vinoth;password=1@Vinothkmr");

                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                st = conn.createStatement();
                String sql;
                sql = "SELECT * FROM USERS";
                ResultSet rs = st.executeQuery(sql);

                //STEP 5: Extract data from result set
                boolean userFound = false;
                while (rs.next()) {
                    //Retrieve by column name
                    String userName = rs.getString("USR_ID");
                    String password_temp = rs.getString("USR_NAME");
                }

                //STEP 6: Clean-up environment
                rs.close();
                st.close();
                conn.close();
            } catch (Exception e) {
                //Handle errors for Class.forName
                //  showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
                System.out.println(e.getMessage());
                msg = "ERROR:" + e.getMessage();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    // showCustomDialog("Exception",se2.getMessage());
                    msg = "ERROR:" + se2.getMessage();
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    msg = "ERROR:" + se.getMessage();
                    //showCustomDialog("Exception",se.getMessage());
                }//end finally try
                return msg;
            }
        }
    }
}
