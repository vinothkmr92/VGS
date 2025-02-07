package com.example.inventoryslip;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME = "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    public static final String EXPIRE_DT = "EXPIRE_DT";
    public static final String PRINTER = "PRINTER";
    String sqlServer ;
    String sqlUserName;
    String sqlPassword;
    String sqlDB;
    AutoCompleteTextView autocompleteUsers;
    TextInputLayout editTextUserName;
    EditText editTextPassword;
    MaterialButton buttonLogin;
    String android_id;
    public static LoginActivity instance;
    public static LoginActivity getInstance() {
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        try{
            editTextUserName = findViewById(R.id.username);
            editTextPassword = (EditText) findViewById(R.id.password);
            buttonLogin = (MaterialButton) findViewById(R.id.loginbtn);
            autocompleteUsers = findViewById(R.id.users);
            buttonLogin.setOnClickListener(this);
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            CheckSQLSettings();
            Common.Printer = sharedpreferences.getString(PRINTER, "");
            instance = this;
            Date dt = new Date();
            Date yesterday = getYesterday();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String expiredtstr = sharedpreferences.getString(EXPIRE_DT,simpleDateFormat.format(yesterday));
            Date expireDt = simpleDateFormat.parse(expiredtstr);
            Date compare = new Date(dt.getYear(),dt.getMonth(),dt.getDate());
            Common.isActivated = expireDt.compareTo(compare)>=0;
            Common.expireDate = expireDt;
            if(!Common.isActivated){
                Common.isActivated = false;
                android_id = android.provider.Settings.Secure.getString(LoginActivity.this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                AppActivation appActivation = new AppActivation(LoginActivity.this,android_id,this);
                appActivation.CheckActivationStatus();
            }
            else{
                if(Common.usersList.isEmpty()){
                    new LoadUsers().execute();
                }
                else {
                    LoadUsersAutoComplete();
                }
            }
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
    public void ValidateActivationResponse(String response){
        if(!Common.isActivated){
            showCustomDialog("Msg","Your Android device "+android_id+" is not activated\n"+response,true,true);
        }
        else {
            if(Common.usersList.isEmpty()){
                new LoadUsers().execute();
            }
        }
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
        Common.SQL_SERVER = sqlServer;
        Common.DB = sqlDB;
        Common.USERNAME = sqlUserName;
        Common.PASSWORD = sqlPassword;
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        if (sqlServer.isEmpty() || sqlUserName.isEmpty() || sqlDB.isEmpty()|| sqlPassword.isEmpty()) {
            showCustomDialog("Warning","Could not connect to Server");
        }
    }
    private Date getYesterday(){
        return new Date(System.currentTimeMillis()-24*60*60*1000);
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
    public void showCustomDialog(String title,String Message,boolean... closeapp) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title);
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
                        Toast.makeText(LoginActivity.this,"Whatsapp have not been installed.",Toast.LENGTH_LONG);
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
            String userid = editTextUserName.getEditText().getText().toString();
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

    public void LoadUsersAutoComplete(){
        ArrayAdapter<String> useradapter = new ArrayAdapter<>(this,com.google.android.material.R.layout.support_simple_spinner_dropdown_item,Common.usersList);
        autocompleteUsers.setAdapter(useradapter);
        autocompleteUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTextPassword.requestFocus();
            }
        });
    }

    public class DoLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        String userid = editTextUserName.getEditText().getText().toString();
        String password = editTextPassword.getText().toString();
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

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
            String[] hostwithport = Common.SQL_SERVER.split(":");
            if(userid.trim().equals("") || password.trim().equals("")){
                z = "Please enter valid UserName/Password";
            }
            else if(hostwithport.length<1){
                z = "Please configure valid SQL Server Host.";
            }
            else if(!isHostAvailable(hostwithport[0],Integer.parseInt(hostwithport[1]))){
                z = "Error:"+Common.SQL_SERVER+" host is unreachable. Please try again Later.";
            }
            else
            {
                try {
                    ConnectionClass connectionClass = new ConnectionClass(Common.SQL_SERVER,Common.DB,Common.USERNAME,Common.PASSWORD);
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Database Connection Failed";
                    } else {
                        String query = "SELECT * FROM USERS WHERE USERNAME='"+userid+"' AND PASSWORD='"+password+"'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            Common.loggedinUser = userid;
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

    public class LoadUsers extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        ConnectionClass connectionClass = null;
        Connection con = null;
        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(Common.SQL_SERVER,Common.DB,Common.USERNAME,Common.PASSWORD);
            con = connectionClass.CONN();
            Common.usersList = new ArrayList<>();
            Common.stLocations = new ArrayList<>();
            Common.invLocations = new ArrayList<>();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Loading Users");
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
                LoadUsersAutoComplete();
            }
            else {
                showCustomDialog("Message",r);
            }
        }

        private ArrayList<String> GetLocations(boolean isStore) throws SQLException {
            ArrayList<String> locations  = new ArrayList<>();
            String tbName = isStore ? "STORAGE ":"INVENTORY ";
            String query = "SELECT * FROM "+tbName;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                locations.add(rs.getString("LOCATION_NAME").toUpperCase());
            }
            return locations;
        }

        @Override
        protected String doInBackground(String... params) {
            String[] hostwithport = Common.SQL_SERVER.split(":");
            if(hostwithport.length<1){
                z = "Please configure valid SQL Server Host.";
            }
            else if(!isHostAvailable(hostwithport[0],Integer.parseInt(hostwithport[1]))){
                z = "Error:"+Common.SQL_SERVER+" host is unreachable. Please try again Later.";
            }
            else
            {
                try {
                    if (con == null) {
                        z = "Database Connection Failed";
                    } else {
                        String query = "SELECT * FROM USERS WHERE UPPER(USERNAME) NOT IN ('ADMIN','VINOTH')";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        ArrayList<String> userslist = new ArrayList<>();
                        while (rs.next()){
                            userslist.add(rs.getString("USERNAME").toUpperCase());
                            isSuccess = true;
                        }
                        if(userslist.isEmpty()){
                            z="No valid users found";
                        }
                        else {
                            Common.usersList = userslist;
                            Common.stLocations = GetLocations(true);
                            Common.invLocations = GetLocations(false);
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