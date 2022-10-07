package com.example.activator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextDeviceID;
    EditText editTextDeviceName;
    EditText editTextExpireDt;
    ImageButton btnSync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextDeviceID = (EditText) findViewById(R.id.editTextDeviceID);
        editTextDeviceName = (EditText) findViewById(R.id.editTextDeviceName);
        editTextExpireDt = (EditText) findViewById(R.id.editTextExpireDt);
        btnSync = (ImageButton) findViewById(R.id.btnsync);
        editTextDeviceID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String imei = editTextDeviceID.getText().toString();
                    if(!imei.isEmpty()){
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                        new GetDeviceDetails().execute(imei);
                    }
                    return true;
                }
                return false;
            }
        });
        btnSync.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        try{
            String deviceid = editTextDeviceID.getText().toString();
            String devicename = editTextDeviceName.getText().toString();
            String expiredt = editTextExpireDt.getText().toString();
            if(deviceid.isEmpty() || devicename.isEmpty() || expiredt.isEmpty()){
                showCustomDialog("Warning","Please check input data.");
                return;
            }
            new SyncData().execute(deviceid,devicename,expiredt);
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.toString());
        }
    }

    class GetDeviceDetails extends AsyncTask<String,Void,String> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPostExecute(String res){
            if(!res.isEmpty()){
                try{
                    boolean isActivated = !res.startsWith("ERROR");
                    if(isActivated){
                        String[] result = res.split("~");
                        editTextDeviceName.setText(result[1]);
                        editTextExpireDt.setText(result[0]);
                    }
                    else{
                        showCustomDialog("Msg",res);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    showCustomDialog("Exception",ex.toString());
                }
                finally {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }
        }
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle(" ");
            dialog.setMessage("Fetching Data from API.....");
            dialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(String... params){
            String res="";
            try
            {
                String host= MainActivity.this.getApplicationContext().getString(R.string.ActivationAPIHost);
                String imei = params[0];
                java.net.URL url = new URL("http://"+host+":9092/api/ActivationAPI/GetDeviceDetails?imei="+imei);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                connection.setDoOutput(true);
                String responseData = "";
                InputStream is = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                if ((responseData = responseReader.readLine()) != null) {
                    System.out.append("Response: " + responseData);
                    res = responseData;
                }
                responseReader.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                res = "ERROR: "+e.getMessage().toString();
            }
            return  res;
        }
    }
    class SyncData extends AsyncTask<String,Void,String> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPostExecute(String res){
            if(!res.isEmpty()){
                try{
                    boolean isActivated = !res.startsWith("ERROR");
                    if(isActivated){
                        showCustomDialog("Msg","Data Sync Successfully Completed");
                    }
                    else{
                        showCustomDialog("Msg",res);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    showCustomDialog("Exception",ex.toString());
                }
                finally {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }
        }
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle(" ");
            dialog.setMessage("API Sync in progress");
            dialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(String... params){
            String res="";
            try
            {
                String host= MainActivity.this.getApplicationContext().getString(R.string.ActivationAPIHost);
                String imei = params[0];
                String devicename= params[1];
                String expiredt = params[2];
                java.net.URL url = new URL("http://"+host+":9092/api/ActivationAPI/ActivateDevice?imei="+imei+"&devicename="+devicename+"&expiredt="+expiredt);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                connection.setDoOutput(true);
                String responseData = "";
                InputStream is = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                if ((responseData = responseReader.readLine()) != null) {
                    System.out.append("Response: " + responseData);
                    res = responseData;
                }
                responseReader.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                res = "ERROR: "+e.getMessage().toString();
            }
            return  res;
        }
    }


}