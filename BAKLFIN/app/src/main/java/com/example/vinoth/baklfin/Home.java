package com.example.vinoth.baklfin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ngx.mp100sdk.Enums.Alignments;
import com.ngx.mp100sdk.Intefaces.INGXCallback;
import com.ngx.mp100sdk.NGXPrinter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

public class Home extends AppCompatActivity implements View.OnClickListener{

    DatabaseHelper dbHelper;
    private Dialog progressBar;
    ConnectionClass connectionClass;
    ArrayList<Customers> customerList;
    ArrayList<Groups> groupsList;
    ArrayList<Grp_Payment_Details> paymentDetails;

    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME = "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    public static final String SENDERID = "SENDERID";

    EditText monthlyPendingTextBox;
    EditText outstandingTextBox;
    EditText paymentAmtTextBox;
    Spinner GrpSpinner;
    Spinner CustomerSpinner;
    Button btnPay;
    public static NGXPrinter ngxPrinter = NGXPrinter.getNgxPrinterInstance();
    private INGXCallback ingxCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            dbHelper = new DatabaseHelper(this);
            connectionClass = new ConnectionClass();
            progressBar = new Dialog(Home.this);
            progressBar.setContentView(R.layout.custom_progress_dialog);
            progressBar.setTitle("Loading");
            customerList = new ArrayList<Customers>();
            groupsList = new ArrayList<Groups>();
            paymentDetails = new ArrayList<Grp_Payment_Details>();
            LoadGropCustomerPayment();
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            CommonUtil.SQL_SERVER = sharedpreferences.getString(SQLSERVER, "");
            CommonUtil.USERNAME = sharedpreferences.getString(SQLUSERNAME, "");
            CommonUtil.PASSWORD = sharedpreferences.getString(SQLPASSWORD, "");
            CommonUtil.DB = sharedpreferences.getString(SQLDB, "");
            CommonUtil.SENDERID = sharedpreferences.getString(SENDERID,"");
            monthlyPendingTextBox = (EditText)findViewById(R.id.monthlyPendingView);
            outstandingTextBox = (EditText)findViewById(R.id.outstandingView);
            paymentAmtTextBox = (EditText)findViewById(R.id.amtPaying);
            GrpSpinner = (Spinner)findViewById(R.id.groupDropDown);
            CustomerSpinner = (Spinner)findViewById(R.id.customerDropDown);
            btnPay = (Button)findViewById(R.id.btnPay);
            btnPay.setOnClickListener(this);
            GrpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    progressBar.show();
                    String selectedGroupName = GrpSpinner.getSelectedItem().toString();
                    Groups grpSelected = null;
                    for(int i=0;i<groupsList.size();i++){
                        Groups g = groupsList.get(i);
                        if(g.getGroupName().equals(selectedGroupName)){
                            grpSelected = g;
                            break;
                        }
                    }
                    if(null!=grpSelected){
                        ArrayList<Customers> custOnGroup = grpSelected.getCustomerOnGrp();
                        LoadCustomerSpinners(custOnGroup);
                        progressBar.cancel();
                    }
                    else {
                        progressBar.cancel();
                        showCustomDialog("ERROR","Invalid Group Selection.!");
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            CustomerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    progressBar.show();
                    String selectedGroupName = GrpSpinner.getSelectedItem().toString();
                    Groups grpSelected = null;
                    for(int i=0;i<groupsList.size();i++){
                        Groups g = groupsList.get(i);
                        if(g.getGroupName().equals(selectedGroupName)){
                            grpSelected = g;
                            break;
                        }
                    }
                    String selValue = CustomerSpinner.getSelectedItem().toString();
                    String[] sv = selValue.split("-");
                    int custIdSelected = Integer.parseInt(sv[0]);
                    Customers cSelected = null;
                    for(int i=0;i<customerList.size();i++){
                        Customers cut = customerList.get(i);
                        if(cut.getCustomerID() == custIdSelected){
                            cSelected = cut;
                            break;
                        }
                    }
                    ArrayList<Grp_Payment_Details> CutPaymentList = new ArrayList<Grp_Payment_Details>();
                    java.util.Date date= new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int month = cal.get(Calendar.MONTH);
                    double totalPaid =0;
                    double ttMonthlyPaid = 0;
                    for(int i=0;i<paymentDetails.size();i++){
                        Grp_Payment_Details g = paymentDetails.get(i);
                        if(g.getCustomer_ID() == custIdSelected){
                            totalPaid+=g.getPaid_Amt();
                            Calendar cn = Calendar.getInstance();
                            cn.setTime(g.getPayment_Date());
                            int mn = cn.get(Calendar.MONTH);
                            if(mn == month){
                                ttMonthlyPaid+=g.getPaid_Amt();
                            }
                            CutPaymentList.add(g);
                        }
                    }
                    double outstandingBalance = grpSelected.getAmount()-totalPaid;
                    outstandingTextBox.setText(String.valueOf(outstandingBalance));
                    double monthlyPending = grpSelected.getMonthlyAmt()-ttMonthlyPaid;
                    monthlyPendingTextBox.setText(String.valueOf(monthlyPending));
                    paymentAmtTextBox.setText(String.valueOf(monthlyPending));
                    progressBar.cancel();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            LoadGroupSpinners();
            this.ingxCallback = new INGXCallback() {
                public void onRunResult(boolean isSuccess) {
                    Log.i("NGX", "onRunResult:" + isSuccess);
                }

                public void onReturnString(String result) {
                    Log.i("NGX", "onReturnString:" + result);
                }

                public void onRaiseException(int code, String msg) {
                    Log.i("NGX", "onRaiseException:" + code + ":" + msg);
                }
            };
            ngxPrinter.initService(this, this.ingxCallback);
        }
        catch (Exception ex){
            showCustomDialog("EXCEPTION",ex.getMessage());
        }


    }
    public void LoadGropCustomerPayment(){
        paymentDetails = dbHelper.GetPaymentDetails();
        groupsList = dbHelper.GetGroups();
        customerList = dbHelper.GetCustomers();
    }
    public void LoadCustomerSpinners(ArrayList<Customers> custList){
        if( customerList.size()==0){
            showCustomDialog("Error","No Customer Found On Selected Group");
            return;
        }
        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,custList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CustomerSpinner.setAdapter(adapter1);
    }
    public void LoadGroupSpinners(){
        if(groupsList.size()==0 || customerList.size()==0){
            showCustomDialog("Error","No Group Found. Please Sync DB To Get Details from Server.");
            return;
        }
        progressBar.show();
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,groupsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        GrpSpinner.setAdapter(adapter);
        progressBar.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }

    private void PrintReceipt(String customerName,String MonthlyPending,String TotalOutstanding,String PaidAmt,String GrpName ){
        try {


            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            ngxPrinter.setDefault();
            ngxPrinter.setStyleDoubleWidth();
            ngxPrinter.printText("PAYMENT RECEIPT", Alignments.CENTER, 24);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("BACKYALAKSHMI MICRO FINANCE", Alignments.CENTER, 28);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("PRIVATE LIMITED", Alignments.CENTER, 28);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("backyalakshmimicrofinance@gmail.com", Alignments.CENTER, 20);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("LAND LINE: 044-25549900.", Alignments.CENTER, 24);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("CUSTOMER CARE: 74188 99988.", Alignments.CENTER, 24);
            ngxPrinter.setStyleBold();
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("DATE      :" + format.format(date), Alignments.LEFT, 24);
            ngxPrinter.printText("NAME      :" + customerName, Alignments.LEFT, 24);
            ngxPrinter.printText("GROUP NAME:" + GrpName, Alignments.LEFT, 24);
            ngxPrinter.printText("--------------------------------", Alignments.LEFT, 24);
            ngxPrinter.printText("AMOUNT PAID          :" + PaidAmt, Alignments.LEFT, 24);
            ngxPrinter.printText("MONTHLY PENDING AMT  :" + MonthlyPending, Alignments.LEFT, 24);
            ngxPrinter.printText("TOTAL OUTSTANDING    :" + TotalOutstanding, Alignments.LEFT, 24);
            ngxPrinter.printText("--------------------------------", Alignments.LEFT, 24);
            ngxPrinter.printText("                              ");
            ngxPrinter.printText("PAYMENT COLLECTED BY: " + CommonUtil.LogedINUser.getUser_Name(), Alignments.LEFT, 24);
            ngxPrinter.printText("                              ");
            ngxPrinter.printText("*** THANK YOU ***", Alignments.CENTER, 24);
            ngxPrinter.printText("                              ");
            ngxPrinter.printText("                              ");
            ngxPrinter.setDefault();
        }
        catch (Exception ex){
            showCustomDialog("Error in Printing",ex.getMessage());
        }
    }


    class SendSMS extends  AsyncTask<String,Void,String>{

        String msg;
        @Override
        public void onPreExecute(){
            super.onPreExecute();

        }

        @Override
        public void onPostExecute(String result){
            showPopupAndRecreate("Payment Suceeded",msg);
        }

        @Override
        protected String doInBackground(String... strings) {
            msg = strings[0];
            String sendTo = strings[1];
            String senderid = strings[2];

            String api = "http://sms.websource.asia/api/sendhttp.php?authkey=24504AOEZlGvcSY5abb3fb3&mobiles=rid&message=MSG&sender=sid&route=6&country=91";
            api = api.replace("MSG",msg);
            api = api.replace("rid",sendTo);
            api = api.replace("sid",senderid);
            String responseString = null;
            try {
                URL url = new URL(api);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    // Do normal input or output stream reading
                    responseString = "SUCEESS";
                }
                else {
                    responseString = "FAILED"; // See documentation for more info on response handling
                }
            } catch (Exception e) {
                //TODO Handle problems..
                responseString = e.getMessage();
            }
            return responseString;
        }
    }

    @Override
    public void onClick(View v) {
        progressBar.setTitle("Saving...");
        progressBar.show();
        try{
            String amtPaying = paymentAmtTextBox.getText().toString();
            if(amtPaying.isEmpty() || amtPaying.equals("0.0")){
                showCustomDialog("Warning","Please Enter Amount Paying !");
                return;
            }
            String grpName = GrpSpinner.getSelectedItem().toString();
            Groups grp = null;
            for(Groups g : groupsList){
                if(g.getGroupName().equals(grpName)){
                    grp = g;
                    break;
                }
            }
            if(null != grp){
                String cidname = CustomerSpinner.getSelectedItem().toString();
                String[] sl = cidname.split("-");
                int cid = Integer.parseInt(sl[0]);
                double paidAmt = Double.parseDouble(amtPaying);
                Grp_Payment_Details pd_Details = new Grp_Payment_Details();
                pd_Details.setPayment_ID(dbHelper.GetNextPaymentID());
                pd_Details.setSyncSts(0);
                pd_Details.setPayment_Type("NGX");
                pd_Details.setReceivedBy(CommonUtil.LogedINUser.getUser_Name());
                pd_Details.setGroupID(grp.getGroupID());
                pd_Details.setCustomer_ID(cid);
                Date dt = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                pd_Details.setPayment_Date_str(dateFormat.format(dt));
                pd_Details.setPaid_Amt(paidAmt);
                double outstanding = Double.parseDouble(outstandingTextBox.getText().toString());
                double balance = outstanding-paidAmt;
                pd_Details.setBalance_Amt(balance);
                dbHelper.Insert_Payment_Details(pd_Details);
                StringBuilder builder = new StringBuilder();
                double monthlypendingbefore = Double.parseDouble(monthlyPendingTextBox.getText().toString());
                double monthlypend = monthlypendingbefore-paidAmt;
                builder.append("CUSTOMER ID      : "+cid);
                builder.append("\n");
                builder.append("PAID AMOUNT      : "+paidAmt);
                builder.append("\n");
                builder.append("MONTHLY PENDING  : "+monthlypend);
                builder.append("\n");
                builder.append("TOTAL OUTSTANDING: "+balance);
                String custName = "";
                String mobileNum = "";
                for(Customers c : customerList){
                    if(cid == c.getCustomerID()){
                        custName = c.getCustomerName();
                        mobileNum = c.getMobileNumber();
                    }
                }
                PrintReceipt(custName,String.valueOf(monthlypend),String .valueOf(balance),String.valueOf(paidAmt),grpName);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Group Name :"+grpName);
                stringBuilder.append("\n");
                stringBuilder.append("PAID AMOUNT      : "+paidAmt);
                stringBuilder.append("\n");
                stringBuilder.append("MONTHLY PENDING  : "+monthlypend);
                stringBuilder.append("\n");
                stringBuilder.append("TOTAL OUTSTANDING: "+balance);
                stringBuilder.append("\n");
                stringBuilder.append("Received By - "+CommonUtil.LogedINUser.getUser_Name());
                stringBuilder.append("\n");
                stringBuilder.append("Thanks for Your Payment.");
                new SendSMS().execute(stringBuilder.toString(),mobileNum,CommonUtil.SENDERID);
            }
            else {
                showPopupAndRecreate("ERROR","Invalid Group Selected. Please Check");
            }
        }
        catch (Exception ex){
            showPopupAndRecreate("EXCEPTION",ex.getMessage());
        }
        finally {
            progressBar.setTitle("Loading");
            progressBar.cancel();
        }


    }
    private void RecreateActivity(){
        this.recreate();
    }
    class SyncDEVICE extends AsyncTask<String,Void,String> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void  onPreExecute(){
            super.onPreExecute();
            progressBar.setTitle("Sync Inprogress...");
            progressBar.show();
        }

        @Override
        public void onPostExecute(String result) {
            progressBar.setTitle("Loading");
            progressBar.cancel();
            if(!result.startsWith("ERROR")){
                showPopupAndRecreate("Message",result);
            }
            else {
                showPopupAndRecreate("Error",result);
            }

        }

        private Integer GetNextPaymentID(Connection cn){
            Integer id = 0;
            try{
                String query = "SELECT MAX(PAYMENT_ID) AS ID FROM GRP_PAYMENT_DETAILS";
                Statement stm = cn.createStatement();
                ResultSet rs = stm.executeQuery(query);
                while (rs.next()){
                    id = rs.getInt("ID");
                }
            }
            catch (Exception ex){

            }
            id++;
            return id;
        }
        private void UploadPaymentsAndClear(Connection con) throws SQLException {
            try{
                 ArrayList<Grp_Payment_Details> paymentFromDeviceList = dbHelper.GetAllPaymentDetails();
                 for(int i=0;i<paymentFromDeviceList.size();i++){
                     Grp_Payment_Details grp = paymentFromDeviceList.get(i);
                     String query = "INSERT INTO GRP_PAYMENT_DETAILS VALUES (?,'"+grp.getPayment_Date_str()+"',?,?,?,'"+grp.getReceivedBy()+"',?,'NGX',1)";
                     PreparedStatement preparedStatement = con.prepareStatement(query);
                     Integer PaymentId = GetNextPaymentID(con);
                     preparedStatement.setInt(1,PaymentId);
                     preparedStatement.setInt(2,grp.getCustomer_ID());
                     preparedStatement.setInt(3,grp.getGroupID());
                     preparedStatement.setDouble(4,grp.getPaid_Amt());
                     preparedStatement.setDouble(5,grp.getBalance_Amt());
                     preparedStatement.execute();
                 }
                dbHelper.ClearAllRecrods();
                 paymentDetails.clear();
                 String query = "SELECT * FROM GRP_PAYMENT_DETAILS";
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(query);
                 while (rs.next()){
                     Grp_Payment_Details grp = new Grp_Payment_Details();
                     grp.setPayment_ID(rs.getInt("PAYMENT_ID"));
                     Date date = rs.getDate("PAYMENT_DATE");
                     DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                     String strDate = dateFormat.format(date);
                     grp.setPayment_Date_str(strDate);
                     grp.setCustomer_ID(rs.getInt("CUSTOMER_ID"));
                     grp.setGroupID(rs.getInt("GR_ID"));
                     grp.setPaid_Amt(rs.getDouble("PAID_AMOUNT"));
                     grp.setReceivedBy(rs.getString("RECEIVED_BY"));
                     grp.setBalance_Amt(rs.getDouble("BALACNE_AMOUNT"));
                     grp.setPayment_Type(rs.getString("PAYMENT_TYPE"));
                     grp.setSyncSts(rs.getInt("SYNC_STS"));
                     paymentDetails.add(grp);
                     dbHelper.Insert_Payment_Details(grp);
                 }
             st.close();
                 rs.close();
            }
            catch (Exception ex){
                throw ex;
            }
        }

        private void LoadCustomers(Connection con) throws SQLException {
            try{
                customerList.clear();
                String custQuery = "SELECT * FROM CUSTOMERS";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(custQuery);
                while (rs.next()){
                    Customers cu = new Customers();
                    cu.setCustomerID(rs.getInt("CUSTOMER_ID"));
                    cu.setCustomerName(rs.getString("CUSTOMER_NAME"));
                    cu.setMobileNumber(rs.getString("MOBILE_NUMBER"));
                    cu.setAddress(rs.getString("ADDRESS"));
                    cu.setID_Proof(rs.getString("ID_PROOF"));
                    customerList.add(cu);
                    dbHelper.Insert_Customers(cu);
                }
                stmt.close();
                rs.close();
            }
            catch (Exception ex){
                throw  ex;
            }
        }
        private ArrayList<Customers> GetListOfCustomersFromGroup(Connection con,int grpId) throws  SQLException {
            ArrayList<Customers> custList = new ArrayList<Customers>();
            try{
                String Query = "SELECT CUSTOMER_ID FROM CUST_GR WHERE GR_ID="+grpId;
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(Query);
                while (rs.next()){
                    int cid = rs.getInt("CUSTOMER_ID");
                    for(int i=0;i<customerList.size();i++){
                        Customers c = customerList.get(i);
                        if(c.getCustomerID()==cid){
                            custList.add(c);
                            break;
                        }
                    }
                }
                st.close();
                rs.close();
            }
            catch (Exception ex){
                throw ex;
            }
            return custList;
        }
        private void LoadUsers (Connection con) throws SQLException {
            try{
                String Query = "SELECT * FROM USERS WHERE USER_ID NOT IN (10001)";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(Query);
                while (rs.next()){
                    Users users = new Users();
                    users.setUser_Id(rs.getInt("USER_ID"));
                    users.setUser_Name(rs.getString("USER_NAME"));
                    users.setMobile_Number(rs.getString("MOBILE_NUMBER"));
                    users.setPassword(rs.getString("PASSWORD"));
                    dbHelper.InsertUser(users);
                }
                st.close();
                rs.close();
            }
            catch (Exception ex){
                throw  ex;
            }
        }
        private void LoadGroups(Connection con) throws SQLException {
            try{
                groupsList.clear();
                String query = "SELECT * FROM GROUPS";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()){
                    Groups gr = new Groups();
                    gr.setGroupID(rs.getInt("GR_ID"));
                    gr.setGroupName(rs.getString("GR_NAME"));
                    gr.setAmount(rs.getDouble("AMOUNT"));
                    gr.setMaxCount(rs.getInt("MAX_CUST"));
                    gr.setInterst(rs.getDouble("INTEREST"));
                    gr.setTenure(rs.getInt("TENURE"));
                    gr.setCustomerOnGrp(GetListOfCustomersFromGroup(con,gr.getGroupID()));
                    groupsList.add(gr);
                    dbHelper.Insert_Groups(gr);
                }
                rs.close();
                stmt.close();
            }
            catch (Exception ex){
                throw ex;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String status = "";
            Connection conn = null;
            Statement st = null;
            try {
                Connection con = connectionClass.CONN(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                if(null != con) {
                    UploadPaymentsAndClear(con);
                    LoadCustomers(con);
                    LoadGroups(con);
                    LoadUsers(con);
                    status = "Sucessfully Sycned Database With Master DB";
                }
                else {
                    status = "ERROR: INVALID CONNECTION STRING.";
                }

            }  catch (Exception e) {
                //Handle errors for Class.forName
                //showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
                status = "ERROR: "+e.getMessage();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    //showCustomDialog("Exception",se2.getMessage());
                    status = "ERROR: "+se2.getMessage();
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    //showCustomDialog("Exception",se.getMessage());
                    status = "ERROR: "+se.getMessage();
                }//end finally try
                return status;
            }
        }
    }
  private void SyncDB(){
      if(CommonUtil.SQL_SERVER.isEmpty() || CommonUtil.DB.isEmpty() || CommonUtil.USERNAME.isEmpty() ){
          showCustomDialog("Warning","Please Configure SQL Database");
          Intent settingsPage = new Intent(this,Settings.class);
          startActivity(settingsPage);
      }
      else {
         new SyncDEVICE().execute("");
      }

  }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Sync:
                SyncDB();
                return true;
            case R.id.logout:
                Intent dcpage = new Intent(this,LoginActivity.class);
                CommonUtil.LogedINUser = null;
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            case R.id.Settings:
                Intent settingsPage = new Intent(this,Settings.class);
                //settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


   public void showPopupAndRecreate(String title,String Message){
       AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
       LayoutInflater inflater = this.getLayoutInflater();
       final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
       dialogBuilder.setView(dialogView);

       // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

       dialogBuilder.setTitle(title);
       dialogBuilder.setMessage("\n"+Message);
       dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
              RecreateActivity();
           }
       });
       AlertDialog b = dialogBuilder.create();
       b.show();
   }

    public void showCustomDialog(String title, String Message) {
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
        b.show();
    }
}
