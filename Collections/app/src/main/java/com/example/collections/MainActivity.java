package com.example.collections;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME = "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    String sqlServer ;
    String sqlUserName;
    String sqlPassword;
    String sqlDB;
    EditText editTextUserName;
    EditText editTextPassword;
    MaterialButton buttonLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        try{
            editTextUserName = (EditText) findViewById(R.id.userid);
            editTextPassword = (EditText) findViewById(R.id.password);
            buttonLogin = (MaterialButton) findViewById(R.id.loginbtn);
            buttonLogin.setOnClickListener(this);
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            CheckSQLSettings();
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void  CheckSQLSettings(){
        String sqlserver = this.getApplicationContext().getString(R.string.SQL_SERVER);
        String dbname = this.getApplicationContext().getString(R.string.SQL_DBNAME);
        String usr = this.getApplicationContext().getString(R.string.SQL_USERNAME);
        String pwd = this.getApplicationContext().getString(R.string.SQL_PASSWORD);
        sqlServer = sharedpreferences.getString(SQLSERVER, sqlserver);
        sqlUserName = sharedpreferences.getString(SQLUSERNAME, usr);
        sqlPassword = sharedpreferences.getString(SQLPASSWORD, pwd);
        sqlDB = sharedpreferences.getString(SQLDB, dbname);
        CommonUtil.SQL_SERVER = sqlServer;
        CommonUtil.DB = sqlDB;
        CommonUtil.USERNAME = sqlUserName;
        CommonUtil.PASSWORD = sqlPassword;
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        if (sqlServer.isEmpty() || sqlUserName.isEmpty() || sqlDB.isEmpty()|| sqlPassword.isEmpty()) {
            showCustomDialog("Warning","Could not connect to Server");
        }
    }
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homemenu, menu);//Menu Resource, Menu
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        MenuItem rpt = menu.findItem(R.id.action_collectionRpt);
        rpt.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exitmenu:
                finish();
                System.exit(0);
                return true;
            case R.id.action_settings:
                Intent dcpage = new Intent(this,SettingsActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
    private void LoadHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onClick(View v) {
        try{
            String userid = editTextUserName.getText().toString();
            String password = editTextPassword.getText().toString();
            if(userid.isEmpty()){
                showCustomDialog("Warning","Please enter valid username.");
            }
            else if(password.isEmpty()){
                showCustomDialog("Warning","Please enter password.");
            }
            else {
                 new DoLogin().execute("");
            }

        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }

    public class DoLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        String userid = editTextUserName.getText().toString();
        String password = editTextPassword.getText().toString();
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Loading");
            dialog.setMessage("Connecting to SQL Server..");
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
        protected void onPostExecute(String r) {
            if(dialog.isShowing()){
                dialog.hide();
            }
            //Toast.makeText(MainActivity.this,r,Toast.LENGTH_LONG).show();
            if(isSuccess){
                LoadHome();
            }
            else {
                showCustomDialog("Message",r);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String[] hostwithport = CommonUtil.SQL_SERVER.split(":");
            if(userid.trim().equals("") || password.trim().equals("")){
                z = "Please enter valid UserName/Password";
            }
            else if(hostwithport.length<1){
                z = "Please configure valid SQL Server Host.";
            }
            else if(!isHostAvailable(hostwithport[0],Integer.parseInt(hostwithport[1]))){
                z = "Error:"+CommonUtil.SQL_SERVER+" host is unreachable. Please try again Later.";
            }
            else
            {
                try {

                    ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Database Connection Failed";
                    } else {
                        String query = "SELECT * FROM USERS WHERE USER_NAME='"+userid+"' AND PASSWORD='"+password+"'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            CommonUtil.loggedinUser = userid;
                            z = "Login Successful";
                            isSuccess=true;
                        }
                        else
                        {
                            z = "Invalid Username or Password. Please try again.";
                            isSuccess = false;
                        }

                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = ex.getMessage();
                }
            }
            return z;
        }
    }
}