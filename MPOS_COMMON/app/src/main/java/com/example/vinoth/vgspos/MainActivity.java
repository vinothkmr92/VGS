package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {


    DatabaseHelper dbHelper;
    EditText password;
    Button btnLogin;
    private Dialog progressBar;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String ISIMEIACTIVATED = "ISACTIVE";
    public static final String EXPIRE_DT = "EXPIRE_DT";
    public String IMEI_Number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            dbHelper = new DatabaseHelper(this);
            password = (EditText) findViewById(R.id.password);
            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);
            progressBar = new Dialog(MainActivity.this);
            progressBar.setContentView(R.layout.custom_progress_dialog);
            progressBar.setTitle("Loading");
            password.addTextChangedListener(this);
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                showCustomDialog("Warning","Enable Full Permission");
                //finish();
            }
            IMEI_Number = telephonyManager.getDeviceId();
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            boolean isActivated = sharedpreferences.getBoolean(ISIMEIACTIVATED,false);
            String expireDt = sharedpreferences.getString(EXPIRE_DT,"");
            /*if(Is_InternetWorking()){
                new CallWebService().execute(IMEI_Number);
                //Thread.sleep(1000);
            }
            else{
                if(isActivated){
                    if(expireDt.isEmpty()){
                        showCustomDialog("FAILD", "Application Not Activated.!", true);
                    }
                    else {
                        Date exDate =new SimpleDateFormat("dd-MMM-yyyy").parse(expireDt);
                        Date dt = new Date();
                        if(exDate.before(dt)){
                            showCustomDialog("FAILD","Application Expired.",true);

                        }
                    }
                }
                else {
                    showCustomDialog("FAILD", "Application Not Activated.!", true);
                }
            }*/


        }
        catch (Exception ex){
            showCustomDialog("ERROR",ex.getMessage());
        }
    }



    class CallWebService extends AsyncTask<String,Void,String> {
        public  static final String NAMESPACE = "http://tempuri.org/";
        public  static final   String METHOD_NAME = "GetLicense";
        public  static final  String SOAP_ACTION = "http://tempuri.org/IVGS_APKService/GetLicense";
        public  static final  String URL = "http://vinothapk.somee.com/VGS_APKService.svc";
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
                    if(!res.contains("NO DATA FOUND") && !res.startsWith(("ERROR:"))){
                        String[] srlist = result.split("~");
                        String expire = srlist[2];
                        Boolean isActive = srlist[3].equals("1");
                        sharedpreferences.putBoolean(ISIMEIACTIVATED,isActive);
                        sharedpreferences.putString(EXPIRE_DT,expire);
                        sharedpreferences.commit();
                        boolean isActivated = sharedpreferences.getBoolean(ISIMEIACTIVATED,false);
                        String expireDt = sharedpreferences.getString(EXPIRE_DT,"");
                        if(isActivated){
                            if(expireDt.isEmpty()){
                                showCustomDialog("FAILD", "Application Not Activated.!", true);
                            }
                            else {
                                Date exDate =new SimpleDateFormat("dd-MMM-yyyy").parse(expireDt);
                                Date dt = new Date();
                                if(exDate.before(dt)){
                                    showCustomDialog("FAILD","Application Expired.",true);

                                }
                            }
                        }
                        else {
                            showCustomDialog("FAILD", "Application Not Activated.!", true);
                        }
                    }
                    if(res.contains("NO DATA FOUND")){
                        showCustomDialog("FAILD", "Application Not Activated.!", true);
                    }
                    //truckNumber.setText("");
                }
                catch (Exception ex){
                    showCustomDialog("SAP CALL Error",ex.getMessage());
                    ex.printStackTrace();
                }
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

            Request.addProperty("IMEI",params[0]);

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
                res = e.getMessage().toString();

            }
            return  res;
        }
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
        b.show();
    }
    private void RecreateActivity(){
        this.recreate();
    }
    public void showCustomDialog(String title, String Message,boolean close) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              finish();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
       String sr = s.toString();
       if(sr.length()==4){
           String ps = password.getText().toString();
           if(ps.equals("1234")){
               Intent dcpage = new Intent(this,HomeActivity.class);
               dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(dcpage);
           }
           else {
               showCustomDialog("Warning","Invalid Password");
           }
       }
    }
}
