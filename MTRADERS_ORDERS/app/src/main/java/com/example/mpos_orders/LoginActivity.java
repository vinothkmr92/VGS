package com.example.mpos_orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ConnectionClass connectionClass;
    Dialog progressBar;
    Button login;
    EditText usrName;
    EditText pass;
    private static LoginActivity mInstance;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button)findViewById(R.id.btnLogin);
        login.setOnClickListener(this);
        usrName = (EditText)findViewById(R.id.userText);
        pass = (EditText)findViewById(R.id.passText);
        progressBar = new Dialog(LoginActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        connectionClass = new ConnectionClass();
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        CheckSQLSettings();
    }
    public void StartSettingsActivity() {
        //showCustomDialog("Warning", "Host / Username / Dbname should not be Empty");
        Intent st = new Intent(this, Settings.class);
        //st.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(st);
    }
    public void  CheckSQLSettings(){
        sqlServer = sharedpreferences.getString(SQLSERVER, "");
        sqlUserName = sharedpreferences.getString(SQLUSERNAME, "");
        sqlPassword = sharedpreferences.getString(SQLPASSWORD, "");
        sqlDB = sharedpreferences.getString(SQLDB, "");
        CommonUtil.SQL_SERVER = sqlServer;
        CommonUtil.DB = sqlDB;
        CommonUtil.USERNAME = sqlUserName;
        CommonUtil.PASSWORD = sqlPassword;
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        if (sqlServer.isEmpty() || sqlUserName.isEmpty() || sqlDB.isEmpty()|| sqlPassword.isEmpty()) {
            StartSettingsActivity();
        }
        else{
            new LoadDropDownData().execute("");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loginmenu, menu);//Menu Resource, Menu
        return true;
    }
    private void LoadLogin(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finishAffinity();
                return true;
            case R.id.Settings:
                Intent settingsPage = new Intent(this,Settings.class);
                startActivity(settingsPage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void LoadHome(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void LoadPaymentPage(){
        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static synchronized LoginActivity getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onClick(View view) {
        new DoLogin().execute("");
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

    public void showCustomPopup(String title, String Message, final int index) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
                //do something with edt.getText().toString();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    public class LoadDropDownData extends AsyncTask<String,String,String>
    {

        ArrayList<Product> productlist = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            if(r.startsWith("ERROR")){
                showCustomDialog("Exception",r);
            }
            else {
                CommonUtil.productsList = productlist;
            }

            // if(isSuccess) {
            //   Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
            //}

        }

        @Override
        protected String doInBackground(String... params) {
            String z="";
            try {
                String imi = params[0];
                Connection con = connectionClass.CONN(sqlServer,sqlDB,sqlUserName,sqlPassword);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT PRODUCT_ID,PRODUCT_DESCRIPTION,SELLING_PRICE_TAX FROM PRODUCTS WHERE HIDE=0 ORDER BY PRODUCT_DESCRIPTION";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    Product pr = new Product();
                    pr.setProductID("0");
                    pr.setProductName("<SELECT PRODUCT>");
                    productlist.add(pr);
                    while (rs.next())
                    {
                        String prid = rs.getString("PRODUCT_ID");
                        String prname = rs.getString("PRODUCT_DESCRIPTION");
                        double sPrice = rs.getDouble("SELLING_PRICE_TAX");
                        Product ps = new Product();
                        ps.setProductID(prid);
                        ps.setProductName(prname);
                        ps.setSellingPrice(sPrice);
                        productlist.add(ps);
                    }
                    rs.close();
                    stmt.close();
                    z = "SUCCESS";
                }
            }
            catch (Exception ex)
            {

                z = "ERROR:"+ex.getMessage();
            }
            return z;
        }
    }
    public class DoLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        double pendingAmt = 0d;


        String userid = usrName.getText().toString();
        String password = pass.getText().toString();


        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            Toast.makeText(LoginActivity.this,r,Toast.LENGTH_LONG).show();
            if(isSuccess){
                if(pendingAmt>0){

                    showCustomDialog("Error","Please Pay your balance amount to proceed. Pending Amount:"+pendingAmt);
                    LoadPaymentPage();
                }
                else{
                    LoadHome();
                }
            }
            else {
                showCustomDialog("Error",r);
            }
            // if(isSuccess) {
            //   Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
            //}

        }

        @Override
        protected String doInBackground(String... params) {
            if(userid.trim().equals("")|| password.trim().equals(""))
                z = "Please enter User Id and Password";
            else
            {
                try {
                    Connection con = connectionClass.CONN(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "SELECT * FROM MEMBERS WHERE MEMBER_ID="+userid+" AND PASSWORD='"+password+"'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if(rs.next())
                        {
                            Member member = new Member();
                            Integer mid = Integer.parseInt(userid);
                            member.setMemberID(mid);
                            String membername = rs.getString("FIRSTNAME");
                            member.setMemberName(membername);
                            member.setPassword(password);
                            double balance = rs.getDouble("Current_Balance");
                            if(balance ==0){
                                CommonUtil.loggedUserName = membername;
                                CommonUtil.member = member;
                            }
                            else{
                                pendingAmt = balance;
                                CommonUtil.paymentMember = member;
                                CommonUtil.pendingAmt = balance;
                            }
                            z = "Login Successful";
                            isSuccess=true;
                        }
                        else
                        {
                            z = "Invalid Credentials";
                            isSuccess = false;
                        }

                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions: "+ex.getMessage();
                }
            }
            return z;
        }
    }
}
