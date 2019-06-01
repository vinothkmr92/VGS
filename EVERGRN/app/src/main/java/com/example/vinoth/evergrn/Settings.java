package com.example.vinoth.evergrn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Settings extends AppCompatActivity implements View.OnClickListener{

    EditText host;
    EditText username;
    EditText password;
    EditText dbname;
    EditText btname;
    Button testConnection;
    ConnectionClass connectionClass;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME= "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    public static final String BTNAME = "BTNAME";
    private Dialog progressBar;
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
            btname = (EditText)findViewById(R.id.btname);
            progressBar = new Dialog(Settings.this);
            progressBar.setContentView(R.layout.custom_progress_dialog);
            progressBar.setTitle("Loading");
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            // sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String hostname = sharedpreferences.getString(SQLSERVER,"");
            String SQLUser = sharedpreferences.getString(SQLUSERNAME,"");
            String SQLPassword = sharedpreferences.getString(SQLPASSWORD,"");
            String Databasename = sharedpreferences.getString(SQLDB,"");
            String BTName = sharedpreferences.getString(BTNAME,"");
            host.setText(hostname);
            username.setText(SQLUser);
            password.setText(SQLPassword);
            dbname.setText(Databasename);
            btname.setText(BTName);
            connectionClass = new ConnectionClass();
            testConnection.setOnClickListener(this);
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage(),true);
        }

    }
    public  void  SaveSettings(){
        progressBar.show();
        String hostname = host.getText().toString();
        String SQLUser = username.getText().toString();
        String SQLPassword = password.getText().toString();
       // String defWeight = defaultWeight.getText().toString();
        String Databasename = dbname.getText().toString();
        String bltName = btname.getText().toString();
       // String dbfen = dbname_fen.getText().toString();
        if(hostname.isEmpty() || SQLUser.isEmpty() || Databasename.isEmpty() ){
            progressBar.cancel();
            showCustomDialog("Warning","Host / Username / Dbname should not be Empty.",false);

        }
        else{

           // SharedPreferences.Editor editor = sharedpreferences.edit();
            sharedpreferences.putString(SQLSERVER,hostname);
            sharedpreferences.putString(SQLUSERNAME,SQLUser);
            sharedpreferences.putString(SQLPASSWORD,SQLPassword);
            sharedpreferences.putString(SQLDB,Databasename);
            sharedpreferences.putString(BTNAME,bltName);
            sharedpreferences.commit();
            progressBar.cancel();
            showCustomDialog("Saved","Settings Saved Sucessfully",true);


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

        @Override
        public void  onPreExecute(){
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        public void onPostExecute(String result) {
            progressBar.cancel();
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
                  status = "DB Connection Succesfull.";
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
        b.show();
    }
}
