package com.example.vinoth.vgspos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SaleReportActivity extends AppCompatActivity implements View.OnClickListener {

    TextView frmDateTextView;
    TextView toDateTextView;
    ImageButton btnFrmDatePicker;
    ImageButton btnToDatePicker;
    Spinner waiterSpinner;
    Button btnGetReport;
    ImageButton btnPrintReport;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    String selectedDate;
    DatabaseHelper dbHelper;
    GridLayout gridLayout;
    DynamicViewSaleReport dynamicView;
    TextView txtViewTotalSaleAmt;
    private Dialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_report);
        dbHelper = new DatabaseHelper(this);
        progressBar = new Dialog(SaleReportActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading...");
        frmDateTextView = (TextView) findViewById(R.id.salerptFrmDate);
        toDateTextView = (TextView) findViewById(R.id.salerptToDate);
        btnFrmDatePicker = (ImageButton) findViewById(R.id.btndpFrmDate);
        btnToDatePicker = (ImageButton) findViewById(R.id.btndpToDate);
        waiterSpinner = (Spinner) findViewById(R.id.salerptwaiter);
        btnGetReport = (Button) findViewById(R.id.btngetsalereport);
        btnPrintReport = (ImageButton) findViewById(R.id.btnsalerptprint);
        gridLayout = (GridLayout)findViewById(R.id.gridDataSaleRpt);
        txtViewTotalSaleAmt = (TextView)findViewById(R.id.totalSaleAmt);
        btnGetReport.setOnClickListener(this);
        btnPrintReport.setOnClickListener(this);
        btnFrmDatePicker.setOnClickListener(this);
        btnToDatePicker.setOnClickListener(this);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        frmDateTextView.setText(format.format(date));
        toDateTextView.setText(format.format(date));
        ArrayList<String> wts = new ArrayList<String>();
        wts.add("ALL");
        wts.add("WAITER - 1");
        wts.add("WAITER - 2");
        wts.add("WAITER - 3");
        wts.add("WAITER - 4");
        wts.add("WAITER - 5");
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,wts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waiterSpinner.setAdapter(adapter);
        GetDefaultDate();
        datePickerDialog = new DatePickerDialog(SaleReportActivity.this,myDateListener,year,month,day);
        todatePickerDialog = new DatePickerDialog(SaleReportActivity.this,mytoDateListener,year,month,day);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.uploadExcel:
                Intent dcpage = new Intent(this,UploadActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            case R.id.settings:
                Intent settingsPage = new Intent(this,Settings.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return  true;
            case R.id.homemenu:
                Intent page = new Intent(this,HomeActivity.class);
                page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(page);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    String monthstr = String.valueOf(arg2+1);
                    monthstr = StringUtils.leftPad(monthstr,2,'0');
                    StringBuilder sb = new StringBuilder().append(arg3).append("/")
                            .append(monthstr).append("/").append(arg1);
                    toDateTextView.setText(sb.toString());
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
                    String monthstr = String.valueOf(arg2+1);
                    monthstr = StringUtils.leftPad(monthstr,2,'0');
                    StringBuilder sb = new StringBuilder().append(arg3).append("/")
                            .append(monthstr).append("/").append(arg1);
                    frmDateTextView.setText(sb.toString());
                }
            };
    private void setDate(int year, int month, int day){
        StringBuilder sb = new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year);
        selectedDate = sb.toString();
    }
    private  void GetDefaultDate(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }
    private void LoadSaleReport(){
        String frmdt = frmDateTextView.getText().toString();
        String todt = toDateTextView.getText().toString();
        String waiter  = waiterSpinner.getSelectedItem().toString();
        //ArrayList<SaleReport> items = dbHelper.GetAllSales(waiter);
        ArrayList<SaleReport> items = dbHelper.GetSaleReport(frmdt,todt,waiter);
        int rc = gridLayout.getRowCount();
        if(gridLayout.getRowCount()>1){
            gridLayout.removeViews(3,items.size()*3);
        }
        double totalSaleamt=0;
        for(int i=0;i<items.size();i++){
            SaleReport sr = items.get(i);
            dynamicView = new DynamicViewSaleReport(this);
            gridLayout.addView(dynamicView.billNoTextView(this,sr.getBillNo()));
            gridLayout.addView(dynamicView.billDateTextView(this,sr.getBillDate()));
            String saleAmtStr = String.format("%.0f",sr.getBillAmount());
            gridLayout.addView(dynamicView.billAmountTextView(this,saleAmtStr));
            totalSaleamt+=sr.getBillAmount();
        }
        String ttSaleAmtstring = String.format("%.0f",totalSaleamt);
        txtViewTotalSaleAmt.setText("₹ "+ttSaleAmtstring);
    }
    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btndpFrmDate:
                datePickerDialog.show();
                break;
            case R.id.btndpToDate:
                todatePickerDialog.show();
                break;
            case R.id.btngetsalereport:
                progressBar.show();
                LoadSaleReport();
                if(progressBar.isShowing())
                    progressBar.cancel();
                break;
            case R.id.btnsalerptprint:
                progressBar.show();
                try{
                    String frmdt = frmDateTextView.getText().toString();
                    String todt = toDateTextView.getText().toString();
                    String waiter  = waiterSpinner.getSelectedItem().toString();
                    ArrayList<SaleReport> items = dbHelper.GetSaleReport(frmdt,todt,waiter);
                    if(items.size()>0){
                        Common.saleReports = items;
                        Common.saleReportFrmDate = frmdt;
                        Common.saleReportToDate = todt;
                        PrintWifi printWifi = new PrintWifi(SaleReportActivity.this,false);
                        printWifi.Print();
                    }
                    else {
                        showCustomDialog("Info","No record found.");
                    }
                }
                catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage().toString());
                }
                finally {
                    if(progressBar.isShowing()){
                        progressBar.cancel();
                    }
                }
        }
    }
}