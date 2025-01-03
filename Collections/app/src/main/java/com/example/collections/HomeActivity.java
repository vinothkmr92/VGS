package com.example.collections;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class HomeActivity extends AppCompatActivity implements GetPaymentsDetails.PaymentDialogListener {

    TextView usernameView;
    EditText memberidEditView;
    CardView memberView;
    TextView memberName;
    TextView outstanding;
    LinearLayout viewLoans;
    public static HomeActivity instance;
    MaterialAlertDialogBuilder confrimDialog;
    PrinterUtil printerUtil;
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
        viewLoans = findViewById(R.id.viewLoans);
        usernameView.setText(CommonUtil.loggedinUser);
        memberidEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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
        confrimDialog =  new MaterialAlertDialogBuilder(HomeActivity.this,
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
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
        MaterialAlertDialogBuilder dialog =  new MaterialAlertDialogBuilder(HomeActivity.this,
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
        String btnTag =  sr.getLoanNo();
        btnPay.setTag(btnTag);
        btnHist.setTag(btnTag);
        loanType.setText(sr.getLoanType());
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loanNo = (String)v.getTag();
                try {
                    GetPaymentsDetails getPayment = new GetPaymentsDetails();
                    getPayment.loanNo = loanNo;
                    getPayment.show(getSupportFragmentManager(),"Payment Details");
                }catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage().toString());
                }
            }
        });
        btnHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loanNo = (String)v.getTag();
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
    public void getPaymentInfo(String loanNo,String paymentmode,double amount) {
        try
        {
           new SavePayment().execute(loanNo,paymentmode,String.valueOf(amount));
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
            printerUtil = new PrinterUtil(this,CommonUtil.memberName,
                    LoanNo,outstanding,paidAmStr,balStr,
                    paymentMode,String.valueOf(paymentID),new Date(),false);
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
                        String query = "SELECT PAYMENT_ID,PAYMENT_DATE,PAYMENT_MODE,AMOUNT,RECEIVED_BY FROM PAYMENTS WHERE LOAN_NO ='"+loanNo+"' ORDER BY PAYMENT_DATE DESC";
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
                            payments.add(payment);
                        }
                        CommonUtil.payments = payments;
                        CommonUtil.loanNo = loanNo;
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
            dialog.setMessage("Getting Loan Details.");
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
                PrintPaymentReceipt(loanNo,paymentMode,amtpaying,paymentID);
            }
            else {
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Status",r);
            }
        }

        private Integer GetNextPaymentID(){
            Integer paymentID = 0;
            try{
                String query = "SELECT MAX(PAYMENT_ID) AS ID FROM PAYMENTS";
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
                        paymentID = GetNextPaymentID();
                        if(paymentID>0){
                            String query = "DELETE FROM PAYMENTS WHERE PAYMENT_ID="+paymentID;
                            Statement stmt = con.createStatement();
                            stmt.executeUpdate(query);
                            query = "INSERT INTO PAYMENTS VALUES ("+paymentID+",GETDATE(),"+memberid+",'"+paymentMode+"',"+amtpaying+",'"+CommonUtil.loggedinUser+"','',"+loanNo+")";
                            int paymentRowAff = stmt.executeUpdate(query);
                            query = "UPDATE MEMBERS SET PAID_AMOUNT=PAID_AMOUNT+"+amtpaying+" WHERE MEMBER_ID="+memberid;
                            int memberBal = stmt.executeUpdate(query);
                            query = "UPDATE MEMBERS SET OUTSTANDING_AMOUNT=LOAN_AMOUNT+INTEREST_AMOUNT-PAID_AMOUNT";
                            stmt.executeUpdate(query);
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
                        String query = "SELECT LOAN_NO,LOAN_AMOUNT,INTEREST,LOAN_TYPE,TERM FROM LOANS WHERE MEMBER_ID="+memberid;
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        ArrayList<Loan> loans = new ArrayList<>();
                        ArrayList<Loan> pendingLoans = new ArrayList<>();
                        while (rs.next())
                        {
                            Loan loan = new Loan();
                            double d = rs.getDouble("LOAN_NO");
                            loan.setLoanNo(String.valueOf(d));
                            loan.setLoanAmount(rs.getDouble("LOAN_AMOUNT"));
                            loan.setInterest(rs.getDouble("INTEREST"));
                            loan.setLoanType(rs.getString("LOAN_TYPE"));
                            loan.setTerm(rs.getInt("TERM"));
                            loans.add(loan);
                        }
                        for (Loan l:loans)
                        {
                            query = "SELECT SUM(AMOUNT) AS PAID_AMT FROM PAYMENTS WHERE LOAN_NO='"+l.getLoanNo()+"'";
                            ResultSet rs1 = stmt.executeQuery(query);
                            if(rs1.next()){
                                l.setPaidAmt(rs1.getDouble("PAID_AMT"));
                            }
                            if(l.getBalanceAmt()>0){
                                pendingLoans.add(l);
                            }
                        }
                        CommonUtil.loans = pendingLoans;
                        isSuccess=CommonUtil.loans.size()>0;
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
                        String query = "SELECT NAME_1,OUTSTANDING_AMOUNT FROM MEMBERS WHERE MEMBER_ID="+memberid;
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            CommonUtil.memberName = rs.getString("NAME_1");
                            CommonUtil.Outstanding = rs.getDouble("OUTSTANDING_AMOUNT");
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