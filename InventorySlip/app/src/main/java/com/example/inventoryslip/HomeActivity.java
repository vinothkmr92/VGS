package com.example.inventoryslip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView fromDateView;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    TextView slipnoTxtview;
    AutoCompleteTextView autoCompleteStLocation;
    TextInputLayout editTextStLocation;
    AutoCompleteTextView autoCompleteInvLocation;
    TextInputLayout editTextInvLocation;
    EditText editTextPartID;
    EditText editTextPartNo;
    EditText editTextDescription;
    Button btnSavePrint;
    EditText editTextQty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        try{
            slipnoTxtview = findViewById(R.id.slipno);
            fromDateView = findViewById(R.id.frmDt);
            autoCompleteStLocation = findViewById(R.id.stlocations);
            editTextStLocation = findViewById(R.id.stlocation);
            autoCompleteInvLocation = findViewById(R.id.invlocations);
            editTextInvLocation = findViewById(R.id.invlocation);
            editTextPartID = findViewById(R.id.partid);
            editTextPartNo = findViewById(R.id.partno);
            editTextDescription = findViewById(R.id.partdesc);
            btnSavePrint = findViewById(R.id.btnPrint);
            editTextQty = findViewById(R.id.qty);
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(HomeActivity.this,myDateListener,year,month,day);
            fromDateView.setOnClickListener(this);
            btnSavePrint.setOnClickListener(this);
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date date = new Date();
            fromDateView.setText(format.format(date));
            ArrayAdapter<String> stlocAdaptor = new ArrayAdapter<>(this,com.google.android.material.R.layout.support_simple_spinner_dropdown_item,Common.stLocations);
            autoCompleteStLocation.setAdapter(stlocAdaptor);
            ArrayAdapter<String> invlocAdaptor = new ArrayAdapter<>(this,com.google.android.material.R.layout.support_simple_spinner_dropdown_item,Common.invLocations);
            autoCompleteInvLocation.setAdapter(invlocAdaptor);
            editTextPartID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId== EditorInfo.IME_ACTION_SEARCH){
                        String serch = editTextPartID.getText().toString().toUpperCase();
                        if(!serch.isEmpty()) {
                            hideKeyboard(HomeActivity.this);
                            new SearchPart().execute(serch);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Please enter valid product id",Toast.LENGTH_SHORT).show();
                        }

                    }
                    return false;
                }
            });
            new GetLatestSlipNo().execute("");
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
    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private void SaveSlipEntry(){
        String qtystr = editTextQty.getText().toString();
        Integer qty = 0;
        if(!qtystr.isEmpty()){
            qty = Integer.parseInt(qtystr);
        }
        if(editTextStLocation.getEditText().getText().toString().isEmpty()){
            showCustomDialog("Warning","Please select valid Storage Location");
            return;
        }
        else if(editTextInvLocation.getEditText().getText().toString().isEmpty()){
            showCustomDialog("Warning","Please select valid Inventory Location");
            return;
        }
        else if(editTextPartID.getText().toString().isEmpty()){
            showCustomDialog("Warning","Please enter valid PartID");
            return;
        }
        else if(editTextPartNo.getText().toString().isEmpty()){
            showCustomDialog("Warning","Invalid Parts Details");
            return;
        }
        else if(editTextDescription.getText().toString().isEmpty()){
            showCustomDialog("Warning","Invalid Parts Details");
            return;
        }
        else if(qty<=0){
            showCustomDialog("Warning","Please enter valid Quantity");
            return;
        }
        else {
            new SaveSlip().execute("");
        }
    }
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
                        fromDateView.setText(format.format(date));
                    } catch (Exception e) {
                        showCustomDialog("Error",e.getMessage());
                    }

                }
            };
    @Override
    public void onClick(View v) {
        switch ((v.getId())){
            case R.id.frmDt:
                datePickerDialog.show();
                break;
            case R.id.btnPrint:
                SaveSlipEntry();
                break;
        }
    }

    public class SearchPart extends AsyncTask<String,String, Part>
    {
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

        @Override
        protected void onPreExecute() {
            editTextPartNo.setText("");
            editTextDescription.setText("");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Searching Part");
            dialog.setMessage("Please Wait...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Part r) {
            if(!error.isEmpty()){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Status",error);
            }
            else {
                //Collections.sort(productShown, Collections.reverseOrder());
                if(r!=null){
                    editTextPartNo.setText(r.PartNo);
                    editTextDescription.setText(r.Description);
                }
                else {
                    if(dialog.isShowing()){
                        dialog.hide();
                    }
                    Toast.makeText(HomeActivity.this,"No Part Details Found.",Toast.LENGTH_SHORT).show();
                }
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        @Override
        protected Part doInBackground(String... params) {
            Part p = null;
            String searchString = params[0].toUpperCase();
            Integer qty = 0;
            if(params.length>1){
                qty = Integer.parseInt(params[1]);
            }
            try {
                ConnectionClass connectionClass = new ConnectionClass(Common.SQL_SERVER,Common.DB,Common.USERNAME,Common.PASSWORD);
                Connection con = connectionClass.CONN();
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    String query = "SELECT * FROM PARTS WHERE UPPER(PART_ID) = '"+searchString+"'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        p = new Part();
                        p.PartID = rs.getString("PART_ID");
                        p.PartNo = rs.getString("PART_NO");
                        p.Description = rs.getString("DESCRIPTION");
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
            return p;
        }
    }
    public class GetLatestSlipNo extends AsyncTask<String,String, String>
    {
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        ConnectionClass connectionClass = null;
        Connection con = null;
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Loading");
            dialog.setMessage("Please Wait...");
            dialog.show();
            connectionClass = new ConnectionClass(Common.SQL_SERVER,Common.DB,Common.USERNAME,Common.PASSWORD);
            con = connectionClass.CONN();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            if(!error.isEmpty()){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Error",error);
            }
            else {
                slipnoTxtview.setText(r);
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String r = "1";
            try {
                if (con == null) {
                    error = "Database Connection Failed";
                }
                else
                {
                    String query = "SELECT MAX(SLIP_NO) AS SLIP_NO FROM SLIPS";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    Integer slipno = 0;
                    if(rs.next())
                    {
                        slipno = rs.getInt("SLIP_NO");
                        isSuccess = true;
                    }
                    rs.close();
                    slipno++;
                    r = String.valueOf(slipno);
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions: "+ex.getMessage();
            }
            return r;
        }
    }
    public class SaveSlip extends AsyncTask<String,String, String>
    {
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        ConnectionClass connectionClass = null;
        Connection con = null;
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Loading");
            dialog.setMessage("Please Wait...");
            dialog.show();
            connectionClass = new ConnectionClass(Common.SQL_SERVER,Common.DB,Common.USERNAME,Common.PASSWORD);
            con = connectionClass.CONN();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String r) {
            if(!error.isEmpty()){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Error",error);
            }
            else {
                if(isSuccess){
                    showCustomDialog("Info","Entry Successfully Saved.");
                    editTextStLocation.getEditText().setText("");
                    editTextInvLocation.getEditText().setText("");
                    editTextPartID.setText("");
                    editTextPartNo.setText("");
                    editTextDescription.setText("");
                    editTextQty.setText("");
                    slipnoTxtview.setText(r);
                }
                else {
                    showCustomDialog("Warning","Unable to Save Slip Details.\n Please contact support team.");
                }
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        private Integer GetLatestSlipNo() throws SQLException {
            String query = "SELECT MAX(SLIP_NO) AS SLIP_NO FROM SLIPS";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            Integer slipno = 0;
            if(rs.next())
            {
                slipno = rs.getInt("SLIP_NO");
            }
            rs.close();
            slipno++;
            return slipno;
        }
        @Override
        protected String doInBackground(String... params) {
            String r = "";
            try {
                if (con == null) {
                    error = "Database Connection Failed";
                }
                else
                {
                    Integer slipNo = GetLatestSlipNo();
                    r = String.valueOf(slipNo);
                    String stLocation = editTextStLocation.getEditText().getText().toString();
                    String invLocation = editTextInvLocation.getEditText().getText().toString();
                    String PartID = editTextPartID.getText().toString();
                    String qty = editTextQty.getText().toString();
                    String username = Common.loggedinUser;
                    String query = String.format("INSERT INTO SLIPS VALUES (%s,GETDATE(),'%s',%s,'%s','%s','%s')",slipNo,PartID,qty,stLocation,invLocation,username);
                    Statement stmt = con.createStatement();
                    int rowAff = stmt.executeUpdate(query);
                    isSuccess = rowAff>0;
                    if(isSuccess){
                        slipNo = GetLatestSlipNo();
                        r = String.valueOf(slipNo);
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions: "+ex.getMessage();
            }
            return r;
        }
    }
}