package com.example.vgposrpt;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    EditText host;
    EditText dbname;
    Button testConnection;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME= "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        try{
            host = view.findViewById(R.id.host);
            dbname= view.findViewById(R.id.dbname);
            testConnection = view.findViewById(R.id.testButton);
            sharedpreferences = MySharedPreferences.getInstance(getContext(),MyPREFERENCES);
            String sqlserver = getContext().getApplicationContext().getString(R.string.SQL_SERVER);
            String dbnamestr = getContext().getApplicationContext().getString(R.string.SQL_DBNAME);
            String hostname = sharedpreferences.getString(SQLSERVER,sqlserver);
            String Databasename = sharedpreferences.getString(SQLDB,dbnamestr);
            host.setText(hostname);
            dbname.setText(Databasename);
            testConnection.setOnClickListener(this);
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage(),true);
        }
        return view;
    }




    private void checkPermission(){

    }
    public  void  GoLogin(){
        Intent page = new Intent(getContext(),LoginActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    public void showCustomDialog(String title,String Message,Boolean close) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(getContext());
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(close){
                    GoLogin();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    public  void  SaveSettings(){
        String hostname = host.getText().toString();
        String SQLUser = getContext().getApplicationContext().getString(R.string.SQL_USERNAME);
        String SQLPassword = getContext().getApplicationContext().getString(R.string.SQL_PASSWORD);


        if(hostname.isEmpty()){
            hostname = getContext().getApplicationContext().getString(R.string.SQL_SERVER);
        }
        String Databasename = dbname.getText().toString();
        if(hostname.isEmpty()  || Databasename.isEmpty() ){
            showCustomDialog("Warning","Host  / Dbname should not be Empty.",false);
        }
        else{
            sharedpreferences.putString(SQLSERVER,hostname);
            sharedpreferences.putString(SQLUSERNAME,SQLUser);
            sharedpreferences.putString(SQLPASSWORD,SQLPassword);
            sharedpreferences.putString(SQLDB,Databasename);
            sharedpreferences.commit();
            showCustomDialog("Saved","Settings Saved Successfully",true);
        }
    }

    @Override
    public void onClick(View v) {
        String hostname = host.getText().toString();
        if(hostname.isEmpty()){
            hostname = getContext().getApplicationContext().getString(R.string.SQL_SERVER);
        }
        String SQLUser = getContext().getApplicationContext().getString(R.string.SQL_USERNAME);
        String SQLPassword = getContext().getApplicationContext().getString(R.string.SQL_PASSWORD);
        String Databasename = dbname.getText().toString();
        new CheckDBConnection().execute(hostname,Databasename,SQLUser,SQLPassword);
    }

    class CheckDBConnection extends AsyncTask<String,Void,String> {

        //  ArrayList hubList = new ArrayList();
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);
        @Override
        public void  onPreExecute(){
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Testing SQL Host...");
            dialog.show();
            super.onPreExecute();
        }
        public  boolean isHostAvailable(final String host, final int port) {
            try (final Socket socket = new Socket()) {
                final InetAddress inetAddress = InetAddress.getByName(host);
                final InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);
                socket.connect(inetSocketAddress, 5000);
                return true;
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        public void onPostExecute(String result) {
            if(dialog.isShowing()){
                dialog.hide();
            }
            if(!result.startsWith("ERROR")){
                Toast.makeText(getContext(),result,Toast.LENGTH_LONG).show();
                SaveSettings();
            }
            else {
                result = result.replace("ERROR","");
                showCustomDialog("Error",result,false);
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            String status = "";
            Connection conn = null;
            Statement st = null;
            try {
                String[] hostwithport = strings[0].split(":");
                if(hostwithport.length<1){
                    status = "ERROR:Please configure valid SQL Server Host.";
                }
                else if(!isHostAvailable(hostwithport[0],Integer.parseInt(hostwithport[1]))){
                    status = "ERROR:"+strings[0]+" host is unreachable. Please try again Later.";
                }
                else {
                    ConnectionClass connectionClass = new ConnectionClass(strings[0],strings[1],strings[2],strings[3]);
                    Connection con = connectionClass.CONN();
                    if(null != con) {
                        status = "DB Connection Successful.";
                    }
                    else {
                        status = "Database Connection Failed.";
                    }
                }

            }  catch (Exception e) {
                //Handle errors for Class.forName
                //showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
                status = "ERROR: "+e.getMessage();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    //showCustomDialog("Exception",se2.getMessage());
                    status = "ERROR: "+se2.getMessage();
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    //showCustomDialog("Exception",se.getMessage());
                    status = "ERROR: "+se.getMessage();
                }//end finally try
                return status;
            }
        }
    }
}