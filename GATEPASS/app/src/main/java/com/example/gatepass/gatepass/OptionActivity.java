package com.example.gatepass.gatepass;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class OptionActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnGateEntry;
    Button btnTruckEntry;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String isActivated = "IsActivated";
    public static final String IMEIVERIFIED = "IMEIVERIFIED";
    public SharedPreferences sharedpreferences;
    ConnectionClass connectionClass;
    String IMEI;
    TelephonyManager telephonyManager;
    private Dialog progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        btnGateEntry = (Button)findViewById(R.id.gateEntryButton);
        btnTruckEntry = (Button)findViewById(R.id.truckEntryButton);
        btnTruckEntry.setOnClickListener(this);
        btnGateEntry.setOnClickListener(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String code = sharedpreferences.getString(isActivated,"");
        String isIMEIVERFIED = sharedpreferences.getString(IMEIVERIFIED,"");
        connectionClass = new ConnectionClass();
        progressBar = new Dialog(OptionActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle(R.string.dialog_title);
        if(!isIMEIVERFIED.equals(("Y"))) {
            IMEI = "";
            try{
                telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    showCustomDialog("Warning","Enable Permission to Check IMEI Number");
                    ActivityCompat.requestPermissions(OptionActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            1);

                }
                else {
                    IMEI = telephonyManager.getDeviceId();
                }
                if(IMEI.isEmpty()){
                    showCustomPopup("Error","Permission issue to Read IMEI Details.",0);
                }
                new CheckIMEI().execute(IMEI);
            }
            catch (Exception ex){
                showCustomDialog("Exception",ex.getMessage());
            }
        }

    }
    public class CheckIMEI extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;


        String IMEI_Number = IMEI;


        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            //Toast.makeText(LoginActivity.this,r,Toast.LENGTH_LONG).show();
            if(!isSuccess){
                showCustomPopup("Error",r,0);
            }
            else {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(IMEIVERIFIED,"Y");
                editor.commit();
            }
            // if(isSuccess) {
            //   Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
            //}

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String imi = params[0];
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT * FROM IMEI_DETAILS WHERE IMEI_NO='"+imi+"'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        String act = rs.getString("ISACTIVE");
                        act = act.toUpperCase();
                        if(act.equals("Y")){
                            isSuccess = true;
                        }
                        else {
                            z = "Your Device Has been Deactivated. Please Contact Administrators";
                        }
                    }
                    else
                    {
                        z = "Your Device is Not Activated. Please Contact Administrators";
                        isSuccess = false;
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions";
            }
            return z;
        }
    }
    public void showCustomPopup(String title, String Message, final int index) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
                //do something with edt.getText().toString();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
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
    public void ActivateApp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_input_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);
        final EditText edt = (EditText)dialogView.findViewById(R.id.code);
        dialogBuilder.setTitle("Enter Code");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String s = edt.getText().toString();
                if(!s.isEmpty()){
                    if(s.equals("1@Nopassword")){
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(isActivated,"True");
                        editor.commit();
                    }
                    else{
                        finish();
                        System.exit(0);
                    }
                }
                else {
                    finish();
                    System.exit(0);
                }
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
    public void StartMainActivity() {
        Intent mass = new Intent(this, MainActivity.class);
        mass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mass);
    }
    public void StartGateEntryActivity() {
        Intent mass = new Intent(this, GateEntry.class);
        mass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mass);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gateEntryButton:
                StartGateEntryActivity();
                break;
            case R.id.truckEntryButton:
                StartMainActivity();
                break;
        }
    }
}
