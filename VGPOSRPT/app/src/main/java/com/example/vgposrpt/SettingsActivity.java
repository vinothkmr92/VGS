package com.example.vgposrpt;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText host;
    EditText dbname;
    Button testConnection;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME= "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    public static final String PRINTER = "PRINTER";
    ArrayList<String> bluethootnamelist;
    Set<BluetoothDevice> pairedDevices = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static String[] PERMISSIONS_BLUETOOTH = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    TextInputLayout printer;
    AutoCompleteTextView printerselection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try{
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_settings);

            setContentView(R.layout.activity_settings);
            host = (EditText) findViewById(R.id.host);
            dbname= (EditText)findViewById(R.id.dbname);
            testConnection = (Button)findViewById(R.id.testButton);
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            String sqlserver = this.getApplicationContext().getString(R.string.SQL_SERVER);
            String dbnamestr = this.getApplicationContext().getString(R.string.SQL_DBNAME);
            String hostname = sharedpreferences.getString(SQLSERVER,sqlserver);
            String Databasename = sharedpreferences.getString(SQLDB,dbnamestr);
            host.setText(hostname);
            dbname.setText(Databasename);
            testConnection.setOnClickListener(this);
            String bluetothName = sharedpreferences.getString(PRINTER,"");
            printer.getEditText().setText(bluetothName);
            if(!checkPermission()){
                ActivityCompat.requestPermissions(SettingsActivity.this,PERMISSIONS_BLUETOOTH
                        ,1);
            }
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            bluethootnamelist = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) {
                bluethootnamelist.add(device.getName());
            }
            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,bluethootnamelist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            printerselection.setAdapter(adapter);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage(),true);
        }
    }
    private boolean checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            int bluetooth = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT);
            int scan = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        }
        else{
            int bluetooth = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH);
            int scan = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        }
    }
    public  void  GoLogin(){
        Intent page = new Intent(this,LoginActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    public void showCustomDialog(String title,String Message,Boolean close) {
        MaterialAlertDialogBuilder dialog =  new MaterialAlertDialogBuilder(SettingsActivity.this,
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

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
        String printer = printerselection.getText().toString();
        String SQLUser = this.getApplicationContext().getString(R.string.SQL_USERNAME);
        String SQLPassword = this.getApplicationContext().getString(R.string.SQL_PASSWORD);
        if(hostname.isEmpty()){
            hostname = this.getApplicationContext().getString(R.string.SQL_SERVER);
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
            sharedpreferences.putString(PRINTER,printer);
            CommonUtil.printer = printer;
            sharedpreferences.commit();
            showCustomDialog("Saved","Settings Saved Successfully",true);
        }
    }

    @Override
    public void onClick(View v) {
        String hostname = host.getText().toString();
        if(hostname.isEmpty()){
            hostname = this.getApplicationContext().getString(R.string.SQL_SERVER);
        }
        String SQLUser = this.getApplicationContext().getString(R.string.SQL_USERNAME);
        String SQLPassword = this.getApplicationContext().getString(R.string.SQL_PASSWORD);
        String Databasename = dbname.getText().toString();
        new CheckDBConnection().execute(hostname,Databasename,SQLUser,SQLPassword);
    }

    class CheckDBConnection extends AsyncTask<String,Void,String> {

        //  ArrayList hubList = new ArrayList();
        private final ProgressDialog dialog = new ProgressDialog(SettingsActivity.this);
        @Override
        public void  onPreExecute(){
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Connecting to Server..");
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
                showCustomDialog("Status",result,false);
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