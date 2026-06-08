package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ViewBillsActivity extends AppCompatActivity implements View.OnClickListener {

    AutoCompleteTextView autoCompleteTextView;
    TextInputLayout txtBranch;
    TextInputLayout txtUser;
    AutoCompleteTextView autoCompleteUsers;
    TextView frmDateTextView;
    TextView toDateTextView;
    LinearLayout salesRptContainer;
    ScrollView salesrptScrollview;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    static ViewBillsActivity instance;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_view_bills);
            frmDateTextView = findViewById(R.id.viewBillsFrmDate);
            toDateTextView = findViewById(R.id.viewBillsToDate);
            salesRptContainer = findViewById(R.id.viewBillsContainer);
            salesrptScrollview = findViewById(R.id.billsScrollView);
            txtBranch = findViewById(R.id.br);
            txtUser = findViewById(R.id.viewBillUser);
            txtUser.setEnabled(CommonUtil.loggedinUserRoleID>1);
            txtBranch.setEnabled(CommonUtil.loggedinUserRoleID>1);
            frmDateTextView.setOnClickListener(this);
            toDateTextView.setOnClickListener(this);
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
            String defaultUser = "ALL";
            if(CommonUtil.loggedinUserRoleID==1){
                defaultUser = CommonUtil.loggedinUser;
            }
            txtUser.getEditText().setText(defaultUser);
            GetDefaultDate();
            datePickerDialog = new DatePickerDialog(ViewBillsActivity.this,myDateListener,year,month,day);
            todatePickerDialog = new DatePickerDialog(ViewBillsActivity.this,mytoDateListener,year,month,day);
            Branch defaultBranch = CommonUtil.branchList.get(0);
            if(CommonUtil.selectedBarnch!=null){
                defaultBranch = CommonUtil.selectedBarnch;
            }
            else if(CommonUtil.branchList.size()<=2){
                defaultBranch = CommonUtil.branchList.get(1);
            }
            txtBranch.getEditText().setText(defaultBranch.getBranch_Name());
            autoCompleteTextView = findViewById(R.id.viewBillsbranches);
            ArrayAdapter<Branch> adapter = new ArrayAdapter<Branch>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,CommonUtil.branchList);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO
                    new GetSales().execute("");
                }
            });
            ArrayList<String> userlistforAdaptor = new ArrayList<>();
            userlistforAdaptor.add("ALL");
            userlistforAdaptor.addAll(CommonUtil.users);
            ArrayAdapter<String> useradapter = new ArrayAdapter<>(this,com.google.android.material.R.layout.support_simple_spinner_dropdown_item,userlistforAdaptor);
            autoCompleteUsers = findViewById(R.id.viewBillUsers);
            autoCompleteUsers.setAdapter(useradapter);
            autoCompleteUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new GetSales().execute("");
                }
            });
            new GetSales().execute("");
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            instance = this;
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage(),true);
        }
    }
    public static ViewBillsActivity getInstance(){
        return instance;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.viewBillsFrmDate:
                if(CommonUtil.loggedinUserRoleID>1){
                    datePickerDialog.show();
                }
                break;
            case R.id.viewBillsToDate:
                if(CommonUtil.loggedinUserRoleID>1){
                    todatePickerDialog.show();
                }
                break;
        }
    }
    public void showCustomDialog(String title,String Message,Boolean close) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
                    // Positive action
                    if(close){
                        finish();
                        System.exit(0);
                    }
                })
                .show();
    }
    private  void GetDefaultDate(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
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

    private void addCard(Sale sr){
        View view = getLayoutInflater().inflate(R.layout.cart_bills,null);
        TextView billno = view.findViewById(R.id.billsCard_billNo);
        TextView billDate = view.findViewById(R.id.billsCard_billDate);
        TextView billAmt = view.findViewById(R.id.billsCard_billAmt);
        boolean print = !CommonUtil.PrintOption.equalsIgnoreCase("NONE");
        Button reprintBtn = view.findViewById(R.id.reprintBill);
        reprintBtn.setTag(sr);
        reprintBtn.setVisibility(print?View.VISIBLE:View.INVISIBLE);
        reprintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sale s = (Sale) reprintBtn.getTag();
                try{
                    PrinterUtil printerUtil = new PrinterUtil(ViewBillsActivity.this,null,s.billDetails);
                    printerUtil.activity = instance;
                    printerUtil.isActivity = true;
                    printerUtil.Print();
                }
                catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage(),false);
                }
            }
        });
        billno.setText(String.valueOf(sr.Bill_No));
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa",Locale.getDefault());
        billDate.setText(format.format(sr.Bill_Date));
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        String moneyString = formatter.format(sr.Bill_Amount).replace(symbol,symbol+" ");
        billAmt.setText(moneyString);
        salesRptContainer.addView(view);
    }
    public void  LoadSalesViews(ArrayList<Sale> sales){
        try{
            salesRptContainer.removeAllViews();
            for(int i=0;i<sales.size();i++){
                Sale sr = sales.get(i);
                addCard(sr);
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage(),false);
        }
    }
    public class GetSales extends AsyncTask<String,String, ArrayList<Sale>>
    {
        String frmDate = frmDateTextView.getText().toString();
        String toDate = toDateTextView.getText().toString();
        int branchCode = GetSelectedBranchCode();
        Boolean isSuccess = false;
        String error = "";
        String selectedUser = "ALL";
        ConnectionClass connectionClass = null;
        Connection con = null;
        private final ProgressDialog dialog = new ProgressDialog(ViewBillsActivity.this,R.style.CustomProgressStyle);
        SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
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
                LoadSalesViews(r);
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }
        private ArrayList<Product> GetBillProducts(int BillNo,Date billDate,int BranchCode,String counter){
            ArrayList<Product> prlist = new ArrayList<>();
            String bddtae=dateFormater.format(billDate);
            if(bddtae.contains("Sept")){
                bddtae = bddtae.replace("Sept","Sep");
            }
            String query = String.format("SELECT BP.PRODUCT_NAME,BP.PRICE,BP.Quantity,BP.Product_ID,BP.BRANCH_CODE,BP.Batch_No,BP.MRP,BP.SNO FROM BILL_PRODUCTS BP,PRODUCTS P WHERE BP.PRODUCT_ID=P.Product_ID AND BP.BRANCH_CODE=P.BRANCH_CODE AND BP.BILL_NO = %d AND BP.BRANCH_CODE = %d AND BP.COUNTER_ID='%s' AND CAST(BP.BILL_DATE AS DATE)='%s' ORDER BY BP.SNO",BillNo,BranchCode,counter,bddtae);
            try{
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()){
                    Product pr = new Product();
                    pr.setProductName(rs.getString("PRODUCT_NAME"));
                    pr.setPrice(rs.getDouble("PRICE"));
                    pr.setQty(rs.getInt("QUANTITY"));
                    pr.setProductID(rs.getString("PRODUCT_ID"));
                    pr.setBranchCode(rs.getInt("BRANCH_CODE"));
                    pr.setBatchNo(rs.getString("BATCH_NO"));
                    pr.setMRP(rs.getDouble("MRP"));
                    pr.setSNo(rs.getInt("SNO"));
                    prlist.add(pr);
                }
                rs.close();
            }
            catch(Exception ex){
              //
            }

            return prlist;
        }

        @Override
        protected ArrayList<Sale> doInBackground(String... params) {
            ArrayList<Sale> sales = new ArrayList<>();
            try {
                if (con == null) {
                    error = "Database Connection Failed.";
                } else {
                    String query = String.format("SELECT Bill_No,cast(BILL_DATE as datetime) as Bill_Date, BILL_AMMOUNT-(BILL_AMMOUNT*(DISCOUNT/100)) AS AMT,Cash_Received,Card_Received,Coupon_Received,Member_ID,User_Name,Branch_Code,Counter_ID FROM SALE WHERE CAST(BILL_DATE AS DATE) BETWEEN '%s' AND '%s'",frmDate,toDate);
                    if(branchCode>0){
                        query = query+String.format(" AND BRANCH_CODE=%s",branchCode);
                    }
                    if(!selectedUser.equals("ALL")){
                        query = query+String.format(" AND USER_NAME='%s' ",selectedUser);
                    }
                    query = query+" ORDER BY Bill_Date DESC";
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
                        BillDetails bd = new BillDetails();
                        bd.MemberID = rs.getInt("Member_ID");
                        bd.billUser = rs.getString("User_Name");
                        bd.BillAmount = sale.Bill_Amount.intValue();
                        bd.BillNo = sale.Bill_No;
                        bd.CashAmt = sale.Cash_Amount.intValue();
                        bd.CardAmt = sale.Card_Amount.intValue();
                        bd.UpiAmt = sale.Upi_Amount.intValue();
                        bd.branchCode = rs.getInt("Branch_Code");
                        bd.counter = rs.getString("Counter_ID");
                        Customer cn = CommonUtil.customers.stream().filter(c->c.MemberID==bd.MemberID).findFirst().orElse(null);
                        bd.MemberName = cn!=null ? cn.MemberName:"";
                        bd.billProducts = GetBillProducts(sale.Bill_No,sale.Bill_Date,bd.branchCode,bd.counter);
                        sale.billDetails = bd;
                        sales.add(sale);
                        isSuccess = true;
                    }
                    rs.close();
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