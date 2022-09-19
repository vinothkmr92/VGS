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
    private void LoadHome(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                    String query = "SELECT PRODUCT_ID,PRODUCT_DESCRIPTION,SELLING_PRICE FROM PRODUCTS ORDER BY PRODUCT_DESCRIPTION";
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
                        double sPrice = rs.getDouble("SELLING_PRICE");
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


        String userid = usrName.getText().toString();


        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            Toast.makeText(LoginActivity.this,r,Toast.LENGTH_LONG).show();
            if(isSuccess){
                LoadHome();
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
            if(userid.trim().equals(""))
                z = "Please enter Registered Mobile Number";
            else
            {
                try {
                    Connection con = connectionClass.CONN(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "SELECT * FROM MEMBERS WHERE MOBILE_NUMBER='"+userid+"'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if(rs.next())
                        {
                            Member member = new Member();
                            Integer memid = rs.getInt("MEMBER_ID");
                            member.setMemberID(memid);
                            String membername = rs.getString("FIRSTNAME");
                            member.setMemberName(membername);
                            member.setMemberName(membername);
                            CommonUtil.loggedUserName = membername;
                            CommonUtil.member = member;
                            z = "Login successfull";
                            isSuccess=true;
                        }
                        else
                        {
                            z = "Not a valid Member";
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
}
