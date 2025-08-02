package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SalesReportActivity extends AppCompatActivity implements View.OnClickListener {

    TextView frmDateTextView;
    TextView toDateTextView;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    TextView txtViewTotalAmt;
    TextView txtViewCashAmt;
    TextView txtViewCardAmt;
    TextView txtViewUpiAmt;
    TextView txtViewTotalBills;
    MaterialButton btnViewBills;
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteUserView;
    TextInputLayout txtBranch;
    TextInputLayout txtUser;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private Boolean doubleBackToExitPressedOnce = false;
    PieChart chart;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME= "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    public static final String PRINTER = "PRINTER";
    public static final String BRANCH = "BRANCH";
    public static final String BRANCHES = "BRANCHES";
    public static final String USERS = "USERS";
    public static final String PRINTOPTION = "PRINTOPTION";
    public static final String RECEIPTSIZE = "RECEIPTSIZE";
    public static final String PRINTER_IP = "PRINTER_IP";
    public static final String HEADER = "HEADER";
    public static final String FOOTER = "FOOTER";
    public static final String ADDRESS = "ADDRESS";
    public static final String INCLUDE_MRP = "INCLUDE_MRP";
    public static final String MULTI_LANG = "MULTI_LANG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_sales_report);
            chart = findViewById(R.id.salesChart);
            frmDateTextView = (TextView) findViewById(R.id.salesrptFrmDate);
            toDateTextView = (TextView) findViewById(R.id.salesrptToDate);
            txtViewTotalAmt = (TextView)findViewById(R.id.totalRevenueAmt);
            txtViewTotalBills = findViewById(R.id.billscount);
            btnViewBills = findViewById(R.id.btnViewBills);
            txtViewCashAmt = findViewById(R.id.cashAmt);
            txtViewCardAmt = findViewById(R.id.cardAmt);
            txtViewUpiAmt = findViewById(R.id.upiAmt);
            txtBranch = findViewById(R.id.salesbr);
            txtUser = findViewById(R.id.salesUser);
            autoCompleteUserView = findViewById(R.id.salesUsers);
            frmDateTextView.setOnClickListener(this);
            toDateTextView.setOnClickListener(this);
            btnViewBills.setOnClickListener(this);
            txtBranch.setEnabled(CommonUtil.loggedinUserRoleID>1);
            txtUser.setEnabled(CommonUtil.loggedinUserRoleID>1);
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            String sqlserver = this.getApplicationContext().getString(R.string.SQL_SERVER);
            String dbnamestr = this.getApplicationContext().getString(R.string.SQL_DBNAME);
            String usr = this.getApplicationContext().getString(R.string.SQL_USERNAME);
            String pwd = this.getApplicationContext().getString(R.string.SQL_PASSWORD);
            String hostname = sharedpreferences.getString(SQLSERVER,sqlserver);
            String Databasename = sharedpreferences.getString(SQLDB,dbnamestr);
            String sqlUserName = sharedpreferences.getString(SQLUSERNAME, usr);
            String sqlPassword = sharedpreferences.getString(SQLPASSWORD, pwd);
            Integer defBranch = sharedpreferences.getInt(BRANCH,1);
            ArrayList<Branch> bransettings = getBranchList();
            ArrayList<String> usersList = getUsersList();
            CommonUtil.users = usersList;
            CommonUtil.branches = bransettings;
            CommonUtil.SQL_SERVER = hostname;
            CommonUtil.DB = Databasename;
            CommonUtil.USERNAME = sqlUserName;
            CommonUtil.PASSWORD = sqlPassword;
            CommonUtil.defBranch = defBranch;
            CommonUtil.PrintOption = sharedpreferences.getString(PRINTOPTION,"None");
            CommonUtil.ReceiptSize = sharedpreferences.getString(RECEIPTSIZE,"3");
            CommonUtil.PrinterIP = sharedpreferences.getString(PRINTER_IP,"");
            CommonUtil.ReceiptHeader = sharedpreferences.getString(HEADER,"");
            CommonUtil.ReceiptFooter = sharedpreferences.getString(FOOTER,"");
            CommonUtil.ReceiptAddress = sharedpreferences.getString(ADDRESS,"");
            CommonUtil.includeMRP = sharedpreferences.getString(INCLUDE_MRP,"N").equalsIgnoreCase("Y");
            CommonUtil.MultiLang = sharedpreferences.getString(MULTI_LANG,"Y").equalsIgnoreCase("Y");
            String defaultUser = "ALL";
            if(CommonUtil.loggedinUserRoleID==1){
                defaultUser = CommonUtil.loggedinUser;
            }
            txtUser.getEditText().setText(defaultUser);
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date date = new Date();
            frmDateTextView.setText(format.format(date));
            toDateTextView.setText(format.format(date));
            if(!CommonUtil.frmDate.isEmpty()){
                frmDateTextView.setText(CommonUtil.frmDate);
            }
            if(!CommonUtil.toDate.isEmpty()){
                toDateTextView.setText(CommonUtil.toDate);
            }
            GetDefaultDate();
            datePickerDialog = new DatePickerDialog(SalesReportActivity.this,myDateListener,year,month,day);
            todatePickerDialog = new DatePickerDialog(SalesReportActivity.this,mytoDateListener,year,month,day);
            String defaultBranch = GetBranchName(defBranch);
            txtBranch.getEditText().setText(defaultBranch);
            autoCompleteTextView = findViewById(R.id.salesbranches);
            ArrayAdapter<Branch> adapter = new ArrayAdapter<Branch>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,CommonUtil.branchList);
            autoCompleteTextView.setAdapter(adapter);
            ArrayList<String> userlistforAdaptor = new ArrayList<>();
            userlistforAdaptor.add("ALL");
            userlistforAdaptor.addAll(usersList);
            ArrayAdapter<String> useradapter = new ArrayAdapter<>(this,com.google.android.material.R.layout.support_simple_spinner_dropdown_item,userlistforAdaptor);
            autoCompleteUserView.setAdapter(useradapter);
            autoCompleteUserView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new GetSales().execute("");
                }
            });
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO
                    new GetSales().execute("");
                }
            });

            new GetSales().execute("");
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage(),false);
        }
    }
    private String GetBranchName (Integer code){
        String branchName = "";
        for (Branch b:CommonUtil.branches) {
            if(b.getBranch_Code()==code){
                branchName = b.getBranch_Name();
                break;
            }
        }
        return branchName;
    }
    private ArrayList<Branch> getBranchList(){
        ArrayList<Branch> arrayItems = new ArrayList<>();
        String serializedObject = sharedpreferences.getString(BRANCHES, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Branch>>(){}.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return  arrayItems;
    }
    private ArrayList<String> getUsersList(){
        ArrayList<String> arrayItems = new ArrayList<>();
        String serializedObject = sharedpreferences.getString(USERS, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return  arrayItems;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homemenu, menu);//Menu Resource, Menu
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        MenuItem newSale = menu.findItem(R.id.salesnew);
        MenuItem kot = menu.findItem(R.id.kot);
        newSale.setVisible(CommonUtil.loggedinUserRoleID!=2);
        kot.setVisible(CommonUtil.loggedinUserRoleID!=2);
        kot.getIcon().setAlpha(CommonUtil.loggedinUserRoleID!=2 ? 255:130);
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
            case R.id.salesnew:
                Intent salePage = new Intent(this,SaleActivity.class);
                salePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(salePage);
                return true;
            case R.id.kot:
                Intent kotPage = new Intent(this,KotActivity.class);
                kotPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(kotPage);
                return true;
            case R.id.action_settings:
                Intent dcpage = new Intent(this,SettingsActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            case R.id.action_productrpt:
                CommonUtil.selectedBarnch = GetSelectedBranch();
                CommonUtil.frmDate = frmDateTextView.getText().toString();
                CommonUtil.toDate = toDateTextView.getText().toString();
                Intent dccpage = new Intent(getApplicationContext(),ProductsReportActivity.class);
                dccpage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dccpage);
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            System.exit(0);
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Please click BACK again to exit", Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void showCustomDialog(String title,String Message,Boolean close) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(SalesReportActivity.this);
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(close){
                    finish();
                    System.exit(0);
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private  void GetDefaultDate(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }
    public static Float convertToFloat(double doubleValue) {
        return (float) doubleValue;
    }
    private void setupPieChart(double cashAmt,double cardAmt,double upiAmt){

        List<PieEntry> pieEntires = new ArrayList<>();
        Float cashAmtf = convertToFloat(cashAmt);
        Float cardAmtf = convertToFloat(cardAmt);
        Float upiAmtf = convertToFloat(upiAmt);
        if(cashAmt>0){
            pieEntires.add(new PieEntry(cashAmtf,"Cash"));
        }
        if(cardAmt>0){
            pieEntires.add(new PieEntry(cardAmtf,"Card"));
        }
        if(upiAmt>0){
            pieEntires.add(new PieEntry(upiAmtf,"UPI"));
        }
        PieDataSet dataSet = new PieDataSet(pieEntires,"");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.orienta);
        dataSet.setValueTypeface(typeface);
        //dataSet.setDrawValues(false);
        dataSet.setValueTextSize(20);
        dataSet.setHighlightEnabled(true);
        dataSet.setValueFormatter(new IndianRuppeFormatter());
        PieData data = new PieData(dataSet);
        //Get the chart

        chart.setCenterText("Sales");
        chart.setDrawEntryLabels(false);
        chart.setContentDescription("");
        chart.setCenterTextSize(15);

        //chart.setDrawMarkers(true);
        //pieChart.setMaxHighlightDistance(34);
        //chart.setEntryLabelTextSize(30);
        chart.setHoleRadius(50);

        //legend attributes
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setWordWrapEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(15);
        legend.setFormSize(20);
        legend.setFormToTextSpace(2);
        chart.getDescription().setEnabled(false);
        chart.setData(data);
        chart.invalidate();
    }
    private DatePickerDialog.OnDateSetListener mytoDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                    String monthstr = String.valueOf(arg2+1);
                    monthstr = StringUtils.leftPad(monthstr,2,'0');
                    String dt = StringUtils.leftPad(String.valueOf(arg3),2,'0');
                    StringBuilder sb = new StringBuilder().append(dt).append("/")
                            .append(monthstr).append("/").append(arg1);
                    Date date = null;
                    try {
                        date = df.parse(sb.toString());
                        String s = format.format(date);
                        toDateTextView.setText(format.format(date));
                        new GetSales().execute("");
                    } catch (Exception e) {
                        showCustomDialog("Error",e.getMessage(),false);
                    }
                }
            };
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    //setDate(arg1, arg2+1, arg3);
                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                    String monthstr = String.valueOf(arg2+1);
                    monthstr = StringUtils.leftPad(monthstr,2,'0');
                    String dt = StringUtils.leftPad(String.valueOf(arg3),2,'0');
                    StringBuilder sb = new StringBuilder().append(dt).append("/")
                            .append(monthstr).append("/").append(arg1);
                    Date date = null;
                    try {
                        date = df.parse(sb.toString());
                        frmDateTextView.setText(format.format(date));
                        new GetSales().execute("");
                    } catch (Exception e) {
                        showCustomDialog("Error",e.getMessage(),false);
                    }

                }
            };

    public int GetSelectedBranchCode(){
        int branchcode = 0;
        List<Branch> sel =CommonUtil.branchList.stream().filter(u->u.getBranch_Name().equals(autoCompleteTextView.getText().toString())).collect(Collectors.toList());
        if(sel.size()>0){
            branchcode = sel.get(0).getBranch_Code();
        }
        return  branchcode;
    }
    public Branch GetSelectedBranch(){
        Branch brselected = null;
        List<Branch> sel =CommonUtil.branchList.stream().filter(u->u.getBranch_Name().equals(autoCompleteTextView.getText().toString())).collect(Collectors.toList());
        if(sel.size()>0){
            brselected = sel.get(0);
        }
        return  brselected;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.salesrptFrmDate:
                if(CommonUtil.loggedinUserRoleID>1){
                    datePickerDialog.show();
                }
                break;
            case R.id.salesrptToDate:
                if(CommonUtil.loggedinUserRoleID>1){
                    todatePickerDialog.show();
                }
                break;
            case R.id.btnViewBills:
                CommonUtil.selectedBarnch = GetSelectedBranch();
                CommonUtil.frmDate = frmDateTextView.getText().toString();
                CommonUtil.toDate = toDateTextView.getText().toString();
                Intent intent = new Intent(getApplicationContext(), ViewBillsActivity.class);
                startActivity(intent);
                break;
        }
    }
    public class GetSales extends AsyncTask<String,String, ArrayList<Sale>>
    {
        String frmDate = frmDateTextView.getText().toString();
        String toDate = toDateTextView.getText().toString();
        String selectedUser = "ALL";
        int branchCode = GetSelectedBranchCode();
        Double revenue = 0d;
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(SalesReportActivity.this,R.style.CustomProgressStyle);

        @Override
        protected void onPreExecute() {
            selectedUser = txtUser.getEditText().getText().toString();
            if(frmDate.contains("Sept")){
                frmDate = frmDate.replace("Sept","Sep");
            }
            if(toDate.contains("Sept")){
                toDate = toDate.replace("Sept","Sep");
            }
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.salesreport);
            dialog.setMessage("Loading...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Sale> r) {

            if(!error.isEmpty()){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Error",error,false);
            }
            else {
                Double billamts = 0d;
                Double cashamts = 0d;
                Double cardamts = 0d;
                Double upiamts = 0d;
                if(r.size()>0){
                    btnViewBills.setVisibility(View.VISIBLE);
                    txtViewTotalBills.setVisibility(View.VISIBLE);
                    txtViewTotalBills.setText("Total Bills: "+String.valueOf(r.size()));
                    billamts = r.stream().mapToDouble(c->c.Bill_Amount).sum();
                    cashamts = r.stream().mapToDouble(c->c.Cash_Amount).sum();
                    cardamts = r.stream().mapToDouble(c->c.Card_Amount).sum();
                    upiamts = r.stream().mapToDouble(c->c.Upi_Amount).sum();
                }
                else {
                    btnViewBills.setVisibility(View.GONE);
                    txtViewTotalBills.setVisibility(View.GONE);
                }
                if(cashamts==0 && cardamts ==0 && upiamts==0){
                    chart.setVisibility(View.GONE);
                }
                else {
                    chart.setVisibility(View.VISIBLE);
                    setupPieChart(cashamts,cardamts,upiamts);
                }
                CommonUtil.totalRevenueAmt = billamts;
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                formatter.setMaximumFractionDigits(0);
                String symbol = formatter.getCurrency().getSymbol();
                txtViewTotalAmt.setText(formatter.format(billamts).replace(symbol,symbol+" "));
                txtViewCashAmt.setText(formatter.format(cashamts).replace(symbol,symbol+" "));
                txtViewCardAmt.setText(formatter.format(cardamts).replace(symbol,symbol+" "));
                txtViewUpiAmt.setText(formatter.format(upiamts).replace(symbol,symbol+" "));
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        @Override
        protected ArrayList<Sale> doInBackground(String... params) {
            ArrayList<Sale> sales = new ArrayList<>();
            try {
                ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                Connection con = connectionClass.CONN();
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    String query = String.format("SELECT Bill_No,cast(BILL_DATE as datetime) as Bill_Date, BILL_AMMOUNT-(BILL_AMMOUNT*(DISCOUNT/100)) AS AMT,Cash_Received,Card_Received,Coupon_Received FROM SALE WHERE CAST(BILL_DATE AS DATE) BETWEEN '%s' AND '%s'",frmDate,toDate);
                    if(branchCode>0){
                        query = query+String.format(" AND BRANCH_CODE=%s",branchCode);
                    }
                    if(!selectedUser.equals("ALL")){
                        query = query+String.format(" AND USER_NAME='%s'",selectedUser);
                    }
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        Sale sale = new Sale();
                        sale.Bill_No = rs.getInt("Bill_No");
                        Timestamp t = rs.getTimestamp("Bill_Date");
                        sale.Bill_Date = new Date(t.getTime());
                        sale.Bill_Amount = rs.getDouble("AMT");
                        sale.Cash_Amount = rs.getDouble("Cash_Received");
                        sale.Card_Amount = rs.getDouble("Card_Received");
                        sale.Upi_Amount = rs.getDouble("Coupon_Received");
                        sales.add(sale);
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return sales;
        }
    }
}