package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ItemReport extends AppCompatActivity implements  View.OnClickListener{

    DatabaseHelper dbHelper;
    private Dialog progressBar;
    TextView frmDateTextView;
    TextView toDateTextView;
    ImageButton btnFrmDatePicker;
    ImageButton btnToDatePicker;
    private Button btnrpt;
    private Button btnPrint;
    private Spinner waiters;
    private GridLayout gridView;
    private DynamicViewItemRpt dynamicView;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_report);
        progressBar = new Dialog(ItemReport.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        dbHelper = new DatabaseHelper(this);
        frmDateTextView = (TextView) findViewById(R.id.itemrptFrmDate);
        toDateTextView = (TextView) findViewById(R.id.itemrptToDate);
        btnFrmDatePicker = (ImageButton) findViewById(R.id.btndpFrmDateItem);
        btnToDatePicker = (ImageButton) findViewById(R.id.btndpToDateItem);
        gridView = (GridLayout) findViewById(R.id.gridDataRpt);
        btnrpt = (Button) findViewById(R.id.btngetreport);
        btnPrint = (Button)findViewById(R.id.btnrptprint);
        waiters = (Spinner)findViewById(R.id.rptwaiter);
        btnPrint.setOnClickListener(this);
        btnrpt.setOnClickListener(this);
        btnFrmDatePicker.setOnClickListener(this);
        btnToDatePicker.setOnClickListener(this);
        ArrayList<String> wts = new ArrayList<String>();
        wts.add("ALL");
        wts.add("WAITER - 1");
        wts.add("WAITER - 2");
        wts.add("WAITER - 3");
        wts.add("WAITER - 4");
        wts.add("WAITER - 5");
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,wts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waiters.setAdapter(adapter);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        frmDateTextView.setText(format.format(date));
        toDateTextView.setText(format.format(date));
        GetDefaultDate();
        datePickerDialog = new DatePickerDialog(ItemReport.this,myDateListener,year,month,day);
        todatePickerDialog = new DatePickerDialog(ItemReport.this,mytoDateListener,year,month,day);
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
    private  void GetDefaultDate(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
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

    private void PrintReport(ArrayList<ItemsRpt> items){
        try {
            String frmdt = frmDateTextView.getText().toString();
            String todt = toDateTextView.getText().toString();
            Common.itemsRpts = items;
            Common.saleReportFrmDate = frmdt;
            Common.saleReportToDate = todt;
            Common.isItemWiseRptBill = true;
           PrintWifi printWifi = new PrintWifi(ItemReport.this,false);
           printWifi.Print();

        } catch (Exception e) {
            showCustomDialog("Print Error",e.getMessage());
            e.printStackTrace();
        }
    }
    public void LoadReportViews(){
        String frmdt = frmDateTextView.getText().toString();
        String todt = toDateTextView.getText().toString();
        String waiter  = waiters.getSelectedItem().toString();
        ArrayList<ItemsRpt> itemsRpts = dbHelper.GetReports(frmdt,todt,waiter);
        if(gridView.getRowCount()>1){
            gridView.removeViews(2,itemsRpts.size()*2);
        }
        for(int i=0;i<itemsRpts.size();i++){
            ItemsRpt item = itemsRpts.get(i);
            dynamicView = new DynamicViewItemRpt(this);
            gridView.addView(dynamicView.itemNameTextView(this,item.getItemName()));
            String qtyStr = String.format("%.0f",item.getQuantity());
            gridView.addView(dynamicView.qtyTextView(this,qtyStr));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btndpFrmDateItem:
                datePickerDialog.show();
                break;
            case R.id.btndpToDateItem:
                todatePickerDialog.show();
                break;
            case R.id.btngetreport:
                LoadReportViews();
                break;
            case R.id.btnrptprint:
                String frmdt = frmDateTextView.getText().toString();
                String todt = toDateTextView.getText().toString();
                String waiter  = waiters.getSelectedItem().toString();
                ArrayList<ItemsRpt> items = dbHelper.GetReports(frmdt,todt,waiter);
                if(items.size()>0){
                    PrintReport(items);
                }
                else {
                    showCustomDialog("Warning","No Details to Print.");
                }
        }

    }
}