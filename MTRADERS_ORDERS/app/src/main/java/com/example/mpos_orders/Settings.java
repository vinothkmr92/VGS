package com.example.mpos_orders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;

import static androidx.core.content.ContextCompat.startActivity;

public class Settings extends AppCompatActivity implements View.OnClickListener{

    EditText host;
    EditText username;
    EditText password;
    EditText dbname;
    Button testConnection;
    ConnectionClass connectionClass;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME= "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            host = (EditText) findViewById(R.id.host);
            username = (EditText)findViewById(R.id.username);
            password = (EditText)findViewById(R.id.password);
            dbname= (EditText)findViewById(R.id.dbname);
            testConnection = (Button)findViewById(R.id.testButton);
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            String hostname = sharedpreferences.getString(SQLSERVER,"");
            String SQLUser = sharedpreferences.getString(SQLUSERNAME,"");
            String SQLPassword = sharedpreferences.getString(SQLPASSWORD,"");
            String Databasename = sharedpreferences.getString(SQLDB,"");
            host.setText(hostname);
            username.setText(SQLUser);
            password.setText(SQLPassword);
            dbname.setText(Databasename);
            connectionClass = new ConnectionClass();
            testConnection.setOnClickListener(this);
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage(),true);
        }

    }
    public  void  SaveSettings(){
        String hostname = host.getText().toString();
        String SQLUser = username.getText().toString();
        String SQLPassword = password.getText().toString();
        String Databasename = dbname.getText().toString();
        if(hostname.isEmpty() || SQLUser.isEmpty() || Databasename.isEmpty() ){
            showCustomDialog("Warning","Host / Username / Dbname should not be Empty.",false);
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
        String SQLUser = username.getText().toString();
        String SQLPassword = password.getText().toString();
        String Databasename = dbname.getText().toString();
        new CheckDBConnection().execute(hostname,Databasename,SQLUser,SQLPassword);
    }

    class CheckDBConnection extends AsyncTask<String,Void,String> {

        //  ArrayList hubList = new ArrayList();
        private final ProgressDialog dialog = new ProgressDialog(Settings.this);
        @Override
        public void  onPreExecute(){
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle(" ");
            dialog.setMessage("Connecting...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        public void onPostExecute(String result) {
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            if(!result.startsWith("ERROR")){
                showCustomDialog("Message",result,false);
                SaveSettings();
            }
            else {
                showCustomDialog("Error",result,false);
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            String status = "";
            Connection conn = null;
            Statement st = null;
            try {

                Connection con = connectionClass.CONN(strings[0],strings[1],strings[2],strings[3]);
                if(null != con) {
                    status = "DB Connection Successful.";
                }
                else {
                    status = "ERROR: INVALID CONNECTION STRING.";
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
    public  void  GoHome(){
        Intent page = new Intent(this,MainActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
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
        //dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int whichButton) {
        //   //pass
        //}
        //});
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
}