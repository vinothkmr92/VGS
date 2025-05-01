package com.example.collections;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.RecursiveTask;

public class HomeActivity extends AppCompatActivity implements GetPaymentsDetails.PaymentDialogListener, View.OnClickListener {

    TextView usernameView;
    EditText memberidEditView;
    CardView memberView;
    TextView memberName;
    TextView outstanding;
    TextView mobileNumber;
    TextView address;
    LinearLayout viewLoans;
    public static HomeActivity instance;
    AlertDialog.Builder confrimDialog;
    PrinterUtil printerUtil;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String PRINTER = "PRINTER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        usernameView = findViewById(R.id.username);
        memberidEditView = findViewById(R.id.memberid);
        memberView = findViewById(R.id.memberView);
        memberName = findViewById(R.id.memberName);
        outstanding = findViewById(R.id.outstanding);
        mobileNumber = findViewById(R.id.mobileNumber);
        address = findViewById(R.id.address);
        viewLoans = findViewById(R.id.viewLoans);
        usernameView.setText(CommonUtil.loggedinUser);
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        String sqlserver = this.getApplicationContext().getString(R.string.SQL_SERVER);
        CommonUtil.SQL_SERVER = sharedpreferences.getString(SQLSERVER,sqlserver);
        CommonUtil.Printer = sharedpreferences.getString(PRINTER,"");
        memberidEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewLoans.removeAllViews();
                    memberView.setVisibility(View.INVISIBLE);
                    hideKeyboard(HomeActivity.this);
                    new GetMemberDtl().execute("");
                    return true;
                }
                return false;
            }
        });
        instance = this;
        confrimDialog =  new AlertDialog.Builder(this);
        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        confrimDialog.setTitle("Confirm Print");
        confrimDialog.setMessage("\nDo you want to print the receipt ?");
        confrimDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RefreshViews();
            }
        });
        confrimDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(printerUtil!=null){
                    try {
                        printerUtil.Print();
                    } catch (Exception e) {
                        showCustomDialog("Error",e.getMessage());
                    }
                    finally {
                        printerUtil = null;
                    }
                }
            }
        });
        confrimDialog.setCancelable(false);
        mobileNumber.setOnClickListener(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public  void  GoToPaymentHistory(){
        Intent page = new Intent(this,PaymentHistoryActivity.class);
        //page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
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
            case R.id.action_collectionRpt:
                Intent collRpt = new Intent(this,CollectionReportActivity.class);
                collRpt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(collRpt);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void addCard(Loan sr){
        View view = getLayoutInflater().inflate(R.layout.cart_loans,null);
        TextView loanNo = view.findViewById(R.id.loanno);
        TextView loanType = view.findViewById(R.id.loanType);
        TextView loanAmount = view.findViewById(R.id.loanAmt);
        MaterialButton btnPay = view.findViewById(R.id.btnPay);
        MaterialButton btnHist = view.findViewById(R.id.btnHist);
        TextView paidAmt = view.findViewById(R.id.amtPaid);
        loanNo.setText(sr.getLoanNo());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        String moneyString = formatter.format(sr.getBalanceAmt()).replace(symbol,symbol+" ");
        String paidmony = formatter.format(sr.getPaidAmt()).replace(symbol,symbol+" ");
        paidAmt.setText(paidmony);
        loanAmount.setText(moneyString);
        String btnTag =  sr.getLoanNo()+"~"+sr.getLoanType();
        btnPay.setTag(btnTag);
        btnHist.setTag(btnTag);
        loanType.setText(sr.getLoanType());
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String)v.getTag();
                String[] stags = tag.split("~");
                String loanNo = stags[0];
                String loanType = stags[1];
                try {
                    GetPaymentsDetails getPayment = new GetPaymentsDetails();
                    getPayment.loanNo = loanNo;
                    getPayment.isInterestLoan = loanType.contains("Interest");
                    getPayment.show(getSupportFragmentManager(),"Payment Details");
                }catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage().toString());
                }
            }
        });
        btnHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String)v.getTag();
                String[] stags = tag.split("~");
                String loanNo = stags[0];
                try {
                   new GetPayments().execute(loanNo);
                }catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage().toString());
                }
            }
        });
        viewLoans.addView(view);
    }
    public static HomeActivity getInstance() {
        return instance;
    }

    @Override
    public void getPaymentInfo(String loanNo,String paymentmode,double amount,boolean isInterest) {
        try
        {
           new SavePayment().execute(loanNo,paymentmode,String.valueOf(amount),isInterest?"Y":"N");
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }
    private static Double convertToDouble(String textValue) {
        double doubleValue;
        try {
            doubleValue = Double.parseDouble(textValue);
        } catch (Exception e) {
            doubleValue = 0.0;
        }
        return doubleValue;
    }

    public  void PrintPaymentReceipt(String LoanNo, String paymentMode, String Amt, Integer paymentID){
        try
        {
            Double paidAmt = convertToDouble(Amt);
            Double balance = CommonUtil.Outstanding-paidAmt;
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            formatter.setMaximumFractionDigits(0);
            String symbol = formatter.getCurrency().getSymbol();
            String outstanding = formatter.format(CommonUtil.Outstanding).replace(symbol,"Rs. ");
            String paidAmStr = formatter.format(paidAmt).replace(symbol,"Rs. ");
            String balStr = formatter.format(balance).replace(symbol,"Rs. ");
            ReceiptDtl rptDtl = new ReceiptDtl();
            rptDtl.MemberName = CommonUtil.memberName;
            rptDtl.LoanNo = LoanNo;
            rptDtl.Outstanding = outstanding;
            rptDtl.Paid = paidAmStr;
            rptDtl.Balance = balStr;
            rptDtl.PaymentMode = paymentMode;
            rptDtl.PaymentID = String.valueOf(paymentID);
            rptDtl.PaidDate = new Date();
            rptDtl.EndDate = CommonUtil.loanDtl.EndDate;
            printerUtil = new PrinterUtil(this,rptDtl,
                    null,null,false,false,false);
            confrimDialog.show();
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }
    public void RefreshViews(){
        viewLoans.removeAllViews();
        memberView.setVisibility(View.INVISIBLE);
        hideKeyboard(HomeActivity.this);
        new GetMemberDtl().execute("");
    }
    public void LoadLoans(){
        try{
            viewLoans.removeAllViews();
            for(int i=0;i<CommonUtil.loans.size();i++){
                Loan loan = CommonUtil.loans.get(i);
                addCard(loan);
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mobileNumber:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mobileNumber.getText().toString()));
                startActivity(intent);
                break;

        }
    }

    public class GetPayments extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        String memberid = memberidEditView.getText().toString();
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Getting Payments History.");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            //Toast.makeText(HomeActivity.this,r,Toast.LENGTH_SHORT).show();
            if(dialog.isShowing()){
                dialog.hide();
            }
            if(isSuccess){
                GoToPaymentHistory();
            }
            else {
                showCustomDialog("ERROR",r);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if(memberid.trim().equals(""))
                z = "Please enter Valid Member ID.";
            else
            {
                try {
                    String loanNo = params[0];
                    ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Database Connection Failed";
                    } else {
                        String query = "SELECT IS_INTEREST,PAYMENT_ID,PAYMENT_DATE,PAYMENT_MODE,AMOUNT,RECEIVED_BY,MEMBER_ID FROM PAYMENTS WHERE LOAN_NO ='"+loanNo+"' ORDER BY PAYMENT_DATE DESC";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        ArrayList<Payment> payments = new ArrayList<>();
                        while (rs.next())
                        {
                            Payment payment = new Payment();
                            payment.setPaymentID(rs.getInt("PAYMENT_ID"));
                            payment.setPaymentDate(rs.getTimestamp("PAYMENT_DATE"));
                            payment.setPaymentMode(rs.getString("PAYMENT_MODE"));
                            payment.setPaidAmount(rs.getDouble("AMOUNT"));
                            payment.setCollectedPerson(rs.getString("RECEIVED_BY"));
                            payment.isInterest = rs.getBoolean("IS_INTEREST");
                            payment.MemberID = rs.getString("MEMBER_ID");
                            payment.EndDate = CommonUtil.loanDtl.EndDate;
                            payments.add(payment);
                        }
                        CommonUtil.payments = payments;
                        CommonUtil.loanNo = loanNo;
                        CommonUtil.isinterestonly = CommonUtil.loanDtl.getLoanType().contains("Interest");
                        isSuccess=CommonUtil.payments.size()>0;
                        z =isSuccess ? "Payments Found.":"No Payments Found.";
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
    public class SavePayment extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        Integer paymentID = 0;
        String loanNo = "";
        String paymentMode = "";
        String amtpaying = "";
        String memberid = memberidEditView.getText().toString();
        ConnectionClass connectionClass = null;
        Connection con = null;
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Updating Payments");
            dialog.setMessage("Please Wait...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            //Toast.makeText(HomeActivity.this,r,Toast.LENGTH_LONG).show();
            if(isSuccess){
                //LoadLoans();
                if(dialog.isShowing()){
                    dialog.hide();
                }
                PrintPaymentReceipt(memberid,paymentMode,amtpaying,paymentID);
            }
            else {
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Status",r);
            }
        }
        private Double GetCurrentOutstanding(){
            Double outstanding = 0d;
            try{
                String query = "SELECT OUTSTANDING_AMOUNT FROM MEMBERS WHERE MEMBER_ID="+memberid;
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    outstanding = rs.getDouble("OUTSTANDING_AMOUNT");
                }
            }
            catch(Exception ex){
                Log.e("ERROR",ex.getMessage());
            }
            return  outstanding;
        }
        private Integer GetNextPaymentID(){
            Integer paymentID = 0;
            try{
                String query = "SELECT MAX(PAYMENT_ID) AS ID FROM PAYMENTS WHERE IS_FIELD_COLLECTION=1";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    paymentID = rs.getInt("ID");
                }
                paymentID++;
            }
            catch(Exception ex){
                Log.e("ERROR",ex.getMessage());
            }
            return paymentID;
        }

        @Override
        protected String doInBackground(String... params) {
            if(memberid.trim().equals(""))
                z = "Please enter Valid Member ID.";
            else
            {
                try {
                    if (con == null) {
                        z = "Database Connection Failed";
                    } else {
                        loanNo = params[0];
                        paymentMode = params[1];
                        amtpaying = params[2];
                        boolean isintest = params[3].equals("Y");
                        paymentID = GetNextPaymentID();
                        if(paymentID>0){
                            String query = "DELETE FROM PAYMENTS WHERE IS_FIELD_COLLECTION=1 AND PAYMENT_ID="+paymentID;
                            Statement stmt = con.createStatement();
                            stmt.executeUpdate(query);
                            query = "INSERT INTO PAYMENTS VALUES ("+paymentID+",GETDATE(),"+memberid+",'"+paymentMode+"',"+amtpaying+",'"+CommonUtil.loggedinUser+"','','"+loanNo+"',1,"+(isintest?"1":"0")+")";
                            int paymentRowAff = stmt.executeUpdate(query);
                            query = "UPDATE MEMBERS SET DUE_AMOUNT=DUE_AMOUNT-"+amtpaying+" WHERE MEMBER_ID="+memberid;
                            int memberBal = stmt.executeUpdate(query);
                            query = "UPDATE MEMBERS SET DUE_AMOUNT=0 WHERE DUE_AMOUNT<0";
                            stmt.executeUpdate(query);
                            query = "UPDATE LOANS SET INTEREST_PAID=0";
                            stmt.executeUpdate(query);
                            query = "UPDATE LOANS SET LOANS.INTEREST_PAID = P.PAID FROM [LOANS] L INNER JOIN(SELECT LOAN_NO, SUM(AMOUNT) PAID FROM PAYMENTS WHERE IS_INTEREST=1 GROUP BY  LOAN_NO) P ON L.LOAN_NO = P.LOAN_NO WHERE L.LOAN_TYPE='InterestOnly';";
                            stmt.executeUpdate(query);
                            query = "UPDATE LOANS SET PAID_AMOUNT=0";
                            stmt.executeUpdate(query);
                            query = "UPDATE LOANS SET LOANS.PAID_AMOUNT = P.PAID FROM [LOANS] L INNER JOIN(SELECT LOAN_NO, SUM(AMOUNT) PAID FROM PAYMENTS WHERE IS_INTEREST=0 GROUP BY  LOAN_NO) P ON L.LOAN_NO = P.LOAN_NO;";
                            stmt.executeUpdate(query);
                            query = "UPDATE MEMBERS SET MEMBERS.OUTSTANDING_AMOUNT = P.BAL FROM [MEMBERS] L INNER JOIN(SELECT MEMBER_ID, SUM(LOAN_AMOUNT+INTEREST_AMOUNT+PENALTY_AMOUNT-PAID_AMOUNT) AS BAL FROM LOANS GROUP BY  MEMBER_ID) P ON L.MEMBER_ID = P.MEMBER_ID;";
                            stmt.executeUpdate(query);
                            query = "UPDATE MEMBERS SET PENALTY_AMOUNT=0 WHERE OUTSTANDING_AMOUNT=0";
                            stmt.executeUpdate(query);
                            if(CommonUtil.loanDtl.getLoanType().contains("Interest")){
                                Double outstanding = GetCurrentOutstanding();
                                Double dueAmt = outstanding*(CommonUtil.loanDtl.getInterest()*100);
                                query = "UPDATE LOANS SET DUE_AMOUNT="+String.format("%.0f",dueAmt)+" WHERE LOAN_NO='"+loanNo+"'";
                                stmt.executeUpdate(query);
                            }
                            isSuccess = paymentRowAff >0 && memberBal>0;

                            z = isSuccess?"Payment Saved":"Unable to Save Payment Please Contact Support Team.";
                        }
                        else {
                            z = "Failed to get Payment ID. Please Contact Support Team.";
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
    public class GetLoanDtl extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        String memberid = memberidEditView.getText().toString();
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Getting Loan Details.");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {

            //Toast.makeText(HomeActivity.this,r,Toast.LENGTH_LONG).show();
            if(isSuccess){
                LoadLoans();
                if(dialog.isShowing()){
                    dialog.hide();
                }
            }
            else {
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Status",r);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if(memberid.trim().equals(""))
                z = "Please enter Valid Member ID.";
            else
            {
                try {
                    ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Database Connection Failed";
                    } else {
                        String query = "SELECT END_DATE,PENALTY_AMOUNT,LOAN_NO,LOAN_AMOUNT,INTEREST,LOAN_TYPE,TERM,PAID_AMOUNT FROM LOANS WHERE (LOAN_AMOUNT+INTEREST_AMOUNT+PENALTY_AMOUNT-PAID_AMOUNT) > 0 AND MEMBER_ID="+memberid;
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        ArrayList<Loan> loans = new ArrayList<>();
                        while (rs.next())
                        {
                            Loan loan = new Loan();
                            loan.PenaltyAmt = rs.getDouble("PENALTY_AMOUNT");
                            loan.setLoanNo(rs.getString("LOAN_NO"));
                            loan.setLoanAmount(rs.getDouble("LOAN_AMOUNT"));
                            loan.setInterest(rs.getDouble("INTEREST"));
                            loan.setLoanType(rs.getString("LOAN_TYPE"));
                            loan.setTerm(rs.getInt("TERM"));
                            loan.setPaidAmt(rs.getDouble("PAID_AMOUNT"));
                            loan.EndDate = rs.getDate("END_DATE");
                            loans.add(loan);
                            CommonUtil.loanDtl = loan;
                        }
                        CommonUtil.loans = loans;
                        isSuccess= !CommonUtil.loans.isEmpty();
                        z =isSuccess ? "Loans Found.":"No Pending Loans.";
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
    public class GetMemberDtl extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        String memberid = memberidEditView.getText().toString();
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Getting Member Details.");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            if(dialog.isShowing()){
                dialog.hide();
            }
            //Toast.makeText(HomeActivity.this,r,Toast.LENGTH_LONG).show();
            if(isSuccess){
                memberView.setVisibility(View.VISIBLE);
                memberName.setText(CommonUtil.memberName);
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                formatter.setMaximumFractionDigits(0);
                String symbol = formatter.getCurrency().getSymbol();
                outstanding.setText(formatter.format(CommonUtil.Outstanding).replace(symbol,symbol+" "));
                mobileNumber.setVisibility(View.VISIBLE);
                mobileNumber.setText(CommonUtil.mobileNumber);
                address.setVisibility(View.VISIBLE);
                address.setText(CommonUtil.address);
                new GetLoanDtl().execute("");
            }
            else {
                showCustomDialog("Status",r);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if(memberid.trim().equals(""))
                z = "Please enter Valid Member ID.";
            else
            {
                try {
                    ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Database Connection Failed";
                    } else {
                        String query = "SELECT NAME_1,MOBILE_1,ADDRESS_1,OUTSTANDING_AMOUNT FROM MEMBERS WHERE MEMBER_ID="+memberid;
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            CommonUtil.memberName = rs.getString("NAME_1");
                            CommonUtil.Outstanding = rs.getDouble("OUTSTANDING_AMOUNT");
                            CommonUtil.mobileNumber = rs.getString("MOBILE_1");
                            CommonUtil.address = rs.getString("ADDRESS_1");
                            z = "Member Found.";
                            isSuccess=true;
                        }
                        else
                        {
                            z = "Invalid Member ID. Please try again.";
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