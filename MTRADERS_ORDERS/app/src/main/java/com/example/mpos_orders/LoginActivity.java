package com.example.mpos_orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
    Button login;
    EditText mobileNoTx;
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
        mobileNoTx = (EditText)findViewById(R.id.mobilenoTxt);
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
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }

    public void showCustomPopup(String title, String Message, final int index) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    public class LoadDropDownData extends AsyncTask<String,String,String>
    {
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        ArrayList<Product> productlist = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle(" ");
            dialog.setMessage("Loading Products");
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String r) {
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            if(r.startsWith("ERROR")){
                showCustomDialog("Exception",r);
            }
            else {
                CommonUtil.productsList = productlist;
            }
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

        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        String mobileno = mobileNoTx.getText().toString();


        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle(" ");
            dialog.setMessage("Connecting...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            if(isSuccess){
                if(pendingAmt>0){
                    showCustomDialog("Error","Please Pay your balance amount to proceed. Pending Amount:"+pendingAmt);
                }
                else{
                    LoadHome();
                }
            }
            else {
                showCustomDialog("Error",r);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if(mobileno.trim().equals(""))
                z = "Please enter valid mobile number";
            else
            {
                try {
                    Connection con = connectionClass.CONN(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "SELECT * FROM MEMBERS WHERE MOBILE_NUMBER='"+mobileno+"'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if(rs.next())
                        {
                            Member member = new Member();
                            Integer mid = rs.getInt("MEMBER_ID");
                            member.setMemberID(mid);
                            String membername = rs.getString("FIRSTNAME");
                            member.setMemberName(membername);
                            String password = rs.getString("PASSWORD");
                            member.setPassword(password);
                            double balance = rs.getDouble("Current_Balance");
                            if(balance>0){
                                pendingAmt = balance;
                                CommonUtil.paymentMember = member;
                                CommonUtil.pendingAmt = balance;

                            }
                            else{
                                CommonUtil.loggedUserName = membername;
                                CommonUtil.member = member;
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
