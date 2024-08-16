package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ConnectionClass connectionClass;
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
        setContentView(R.layout.activity_login);
        try{
           editTextUserName = (EditText) findViewById(R.id.userid);
           editTextPassword = (EditText) findViewById(R.id.password);
           buttonLogin = (MaterialButton) findViewById(R.id.loginbtn);
           buttonLogin.setOnClickListener(this);
           connectionClass = new ConnectionClass();
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
    private void LoadHome() {
        Intent intent = new Intent(getApplicationContext(), SalesReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_wise_report, menu);//Menu Resource, Menu
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
    public void showCustomDialog(String title,String Message) {
        MaterialAlertDialogBuilder dialog =  new MaterialAlertDialogBuilder(LoginActivity.this,
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        try{
           String val = new DoLogin().execute("").get();
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
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.salesreport);
            dialog.setMessage("Connecting to Server..");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            if(dialog.isShowing()){
                dialog.hide();
            }
            Toast.makeText(LoginActivity.this,r,Toast.LENGTH_LONG).show();
            if(isSuccess){
                new LoadBranches().execute("");
            }
            else {
                showCustomDialog("Error",r);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if(userid.trim().equals(""))
                z = "Please enter Registered Mobile Number";
            else
            {
                try {
                    Connection con = connectionClass.CONN(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "SELECT * FROM USERS WHERE USER_NAME='"+userid+"' AND PASSWORD='"+password+"'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        int roleid=0;
                        if(rs.next())
                        {
                            roleid = rs.getInt("ROLE_ID");
                            if(roleid>1){
                                CommonUtil.loggedinUser = userid;
                                z = "Login Successfull";
                                isSuccess=true;
                            }
                            else {
                                z = "Not authorized to use app";
                                isSuccess = false;
                            }
                        }
                        else
                        {
                            z = "Not a valid User";
                            isSuccess = false;
                        }

                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions";
                }
            }
            return z;
        }
    }

    public class LoadBranches extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Connecting to Server..");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            if(dialog.isShowing()){
                dialog.hide();
            }
            LoadHome();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT ID,COMPANY_NAME FROM COMPANY_DETAILS";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    ArrayList<Branch> branches = new ArrayList<>();
                    Branch brall = new Branch();
                    brall.setBranch_Code(0);
                    brall.setBranch_Name("ALL");
                    branches.add(brall);
                    while (rs.next())
                    {
                        Branch br = new Branch();
                        br.setBranch_Code(rs.getInt("ID"));
                        br.setBranch_Name(rs.getString("COMPANY_NAME"));
                        branches.add(br);
                        isSuccess = true;
                    }
                    CommonUtil.branchList = branches;
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
}
