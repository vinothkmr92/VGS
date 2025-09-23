package com.example.vgposrpt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME = "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    public static final String BRANCHES = "BRANCHES";
    public static final String USERS = "USERS";
    public static final String COUNTERS = "COUNTERS";
    public static final String TABLES = "TABLES";
    public static final String PRINTOPTION = "PRINTOPTION";
    public static final String RECEIPTSIZE = "RECEIPTSIZE";
    public static final String PRINTER_IP = "PRINTER_IP";
    public static final String HEADER = "HEADER";
    public static final String FOOTER = "FOOTER";
    public static final String ADDRESS = "ADDRESS";
    public static final String INCLUDE_MRP = "INCLUDE_MRP";
    public static final String MULTI_LANG = "MULTI_LANG";
    public static final String PRINTER = "PRINTER";
    public static final String PRINTOPTION_KOT = "PRINTOPTIONKOT";
    public static final String PRINTER_IP_KOT = "PRINTER_IP_KOT";
    public static final String USBDEVICENAME = "USBDEVICE";
    public static final String USBDEVICENAME_KOT = "USBDEVICE_KOT";
    public static final String ENABLEKOT = "ENABLEKOT";
    public static final String PRINTER_KOT = "PRINTER";
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
           editTextUserName =findViewById(R.id.userid);
           editTextPassword =  findViewById(R.id.password);
           buttonLogin =  findViewById(R.id.loginbtn);
           buttonLogin.setOnClickListener(this);
           sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
           CheckSQLSettings();
            CommonUtil.PrintOption = sharedpreferences.getString(PRINTOPTION,"None");
            CommonUtil.PrintOptionKot = sharedpreferences.getString(PRINTOPTION_KOT,"None");
            CommonUtil.PrinterIP = sharedpreferences.getString(PRINTER_IP,"");
            CommonUtil.PrinterIPKot = sharedpreferences.getString(PRINTER_IP_KOT,"");
            CommonUtil.printer = sharedpreferences.getString(PRINTER,"");
            CommonUtil.printerKot = sharedpreferences.getString(PRINTER_KOT,"");
            String usbdevice = sharedpreferences.getString(USBDEVICENAME,"");
            String[] sb = usbdevice.split("~");
            if(sb.length>1){
                CommonUtil.usbDeviceName = sb[1];
            }

            String usbdeviceKot = sharedpreferences.getString(USBDEVICENAME_KOT,"");
            String[] sbKOT = usbdeviceKot.split("~");
            if(sbKOT.length>1){
                CommonUtil.usbDeviceNameKot = sbKOT[1];
            }

            CommonUtil.ReceiptSize = sharedpreferences.getString(RECEIPTSIZE,"3");
            CommonUtil.ReceiptHeader = sharedpreferences.getString(HEADER,"");
            CommonUtil.ReceiptFooter = sharedpreferences.getString(FOOTER,"");
            CommonUtil.ReceiptAddress = sharedpreferences.getString(ADDRESS,"");
            CommonUtil.includeMRP = sharedpreferences.getString(INCLUDE_MRP,"N").equalsIgnoreCase("Y");
            CommonUtil.MultiLang = sharedpreferences.getString(MULTI_LANG,"Y").equalsIgnoreCase("Y");
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
        if(CommonUtil.loggedinUserRoleID>1){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), KotActivity.class);
            startActivity(intent);
        }
        finish();
    }
    @SuppressLint("RestrictedApi")
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

    @SuppressLint("NonConstantResourceId")
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
        AlertDialog.Builder dialog =  new AlertDialog.Builder(LoginActivity.this);
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("Ok", (dialog1, whichButton) -> {
            //do something with edt.getText().toString();
        });
        dialog.setCancelable(false);
        dialog.show();
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
            else new DoLogin().execute("");

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
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this,R.style.CustomProgressStyle);

        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Login");
            dialog.setMessage("Connecting to Server..");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            if(dialog.isShowing()){
                dialog.hide();
            }
            if(isSuccess){
                new LoadBranches().execute("");
            }
            else {
                showCustomDialog("Error",r);
            }
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
                        int roleid=0;
                        if(rs.next())
                        {
                            roleid = rs.getInt("ROLE_ID");
                            CommonUtil.loggedinUser = userid;
                            CommonUtil.loggedinUserRoleID = roleid;
                            z = "Login Successful";
                            isSuccess=true;
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
        private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this,R.style.CustomProgressStyle);

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
                ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                Connection con = connectionClass.CONN();
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
                    ArrayList<Branch> branchesforDef = new ArrayList<>();
                    while (rs.next())
                    {
                        Branch br = new Branch();
                        br.setBranch_Code(rs.getInt("ID"));
                        br.setBranch_Name(rs.getString("COMPANY_NAME"));
                        branches.add(br);
                        branchesforDef.add(br);
                        isSuccess = true;
                    }
                    CommonUtil.branchList = branches;
                    sharedpreferences.setList(BRANCHES,branchesforDef);

                    rs.close();
                    String userQuery = "SELECT USER_NAME FROM USERS";
                    ResultSet u = stmt.executeQuery(userQuery);
                    ArrayList<String> userList = new ArrayList<>();
                    while (u.next()){
                        userList.add(u.getString("USER_NAME"));
                    }
                    sharedpreferences.setList(USERS,userList);
                    CommonUtil.users = userList;
                    u.close();
                    String counterQuery = "SELECT * FROM COUNTERS";
                    ResultSet c = stmt.executeQuery(counterQuery);
                    ArrayList<Counters> counters = new ArrayList<>();
                    while (c.next()){
                        Counters cn = new Counters();
                        cn.CounterID = c.getString("COUNTER_ID");
                        cn.CounterName = c.getString("Counter_Description");
                        cn.branchCode = c.getInt("BRANCH_CODE");
                        counters.add(cn);
                    }
                    sharedpreferences.setList(COUNTERS,counters);
                    sharedpreferences.commit();
                    CommonUtil.countersList = counters;
                    c.close();
                    String tableQuery = "SELECT * FROM DiningTables";
                    ResultSet tb = stmt.executeQuery(tableQuery);
                    ArrayList<Tables> tables = new ArrayList<>();
                    while (tb.next()){
                        Tables tbn = new Tables();
                        tbn.TableID = tb.getInt("TABLE_ID");
                        tbn.TableName = tb.getString("TABLE_NAME");
                        tbn.BranchCode = tb.getInt("BRANCH_CODE");
                        tables.add(tbn);
                    }
                    sharedpreferences.setList(TABLES,tables);
                    sharedpreferences.commit();
                    CommonUtil.tablesList = tables;
                    tb.close();
                    ArrayList<Product> products = new ArrayList<>();
                    int branchCode = CommonUtil.defBranch;
                    String productquery = "SELECT P.PPWT,P.MRP,P.BRANCH_CODE,P.PRODUCT_ID,P.PRODUCT_DESCRIPTION,P.SELLING_PRICE,C.Category_Name,s.Supplier_Name,st.available_quantity as av FROM PRODUCTS p,\n" +
                            "(SELECT S.AVAILABLE_QUANTITY,A.* FROM Stocks S,(SELECT PRODUCT_ID,BRANCH_CODE,MAX(UPDATED_DATE) AS DT FROM \n" +
                            "STOCKS GROUP BY PRODUCT_ID,BRANCH_CODE) A WHERE S.PRODUCT_ID=A.PRODUCT_ID AND A.BRANCH_CODE=S.BRANCH_CODE AND \n" +
                            "S.UPDATED_DATE=A.DT) st,Category c,Suppliers s where c.Category_ID=p.CATEGORY_ID and \n" +
                            "s.Supplier_ID=p.SUPPLIER_ID and st.product_id=p.product_id and \n" +
                            "st.branch_code=p.branch_code AND P.BRANCH_CODE="+branchCode;
                    ResultSet productResults = stmt.executeQuery(productquery);
                    while (productResults.next())
                    {
                        Product pr = new Product();
                        pr.setBranchCode(productResults.getInt("BRANCH_CODE"));
                        pr.setProductID(productResults.getString("PRODUCT_ID"));
                        pr.setProductName(productResults.getString("PRODUCT_DESCRIPTION"));
                        pr.setPrice(productResults.getDouble("SELLING_PRICE"));
                        pr.setMRP(productResults.getDouble("MRP"));
                        pr.setPurchasedPrice(productResults.getDouble("PPWT"));
                        pr.setCategory(productResults.getString("Category_Name"));
                        pr.setSupplierName(productResults.getString("Supplier_Name"));

                        if(pr.getCategory().isEmpty()){
                            pr.setCategory("GENERAL");
                        }
                        if(pr.getSupplierName().isEmpty()){
                            pr.setSupplierName("OWN");
                        }
                        pr.setQty(1);
                        products.add(pr);
                        isSuccess = true;
                    }
                    rs.close();
                    for (Product p:products) {
                        query = "SELECT BATCH_NO FROM PRODUCT_BATCH_DETAILS WHERE BRANCH_CODE="+p.getBranchCode()+" AND PRODUCT_ID='"+p.getProductID()+"' AND AVAILABLE_QUANTITY>0";
                        ResultSet r = stmt.executeQuery(query);
                        if(r.next()){
                            p.setBatchNo(r.getString("BATCH_NO"));
                        }
                        r.close();
                        if(p.getBatchNo().isEmpty()){
                            p.setBatchNo(String.valueOf(p.getMRP()));
                        }
                    }
                    productResults.close();
                    CommonUtil.productsFull = products;
                    CommonUtil.categories = new ArrayList<>(products.stream().map(j->j.getCategory()).distinct().collect(Collectors.toList()));
                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    Date date = new Date();
                    String today = format.format(date);
                    String kotnoquery = "SELECT MAX(KOT_ID) AS KOTID FROM KOT WHERE CAST(KOT_DATE AS DATE)='"+today+"'";
                    ResultSet kotrs = stmt.executeQuery(kotnoquery);
                    if(kotrs.next()){
                        Integer maxkot = kotrs.getInt("KOTID");
                        CommonUtil.kotNo = maxkot+1;
                    }
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
