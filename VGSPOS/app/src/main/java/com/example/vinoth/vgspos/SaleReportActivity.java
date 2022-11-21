package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SaleReportActivity extends AppCompatActivity implements View.OnClickListener {

    TextView frmDateTextView;
    TextView toDateTextView;
    ImageButton btnFrmDatePicker;
    ImageButton btnToDatePicker;
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
    TextView searchTxtView;
    Dialog dialog;
    private static SaleReportActivity instance;

    public static SaleReportActivity getInstance() {
        return instance;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

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
        btnGetReport = (Button) findViewById(R.id.btngetsalereport);
        btnPrintReport = (ImageButton) findViewById(R.id.btnsalerptprint);
        gridLayout = (GridLayout)findViewById(R.id.gridDataSaleRpt);
        txtViewTotalSaleAmt = (TextView)findViewById(R.id.totalSaleAmt);
        btnGetReport.setOnClickListener(this);
        btnPrintReport.setOnClickListener(this);
        btnFrmDatePicker.setOnClickListener(this);
        btnToDatePicker.setOnClickListener(this);
        searchTxtView = (TextView)findViewById(R.id.customerinfosaleRpt);
        searchTxtView.setText("ALL");
        ArrayList<String> wts = new ArrayList<String>();
        wts.add("ALL");
        ArrayList<Customer> customers = dbHelper.GetCustomers();
        for(int i=0;i<customers.size();i++){
            wts.add(customers.get(i).getCustomerName());
        }
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        frmDateTextView.setText(format.format(date));
        toDateTextView.setText(format.format(date));
        searchTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog=new Dialog(SaleReportActivity.this);

                // set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                // set custom height and width
                dialog.getWindow().setLayout(500,500);

                // set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // show dialog
                dialog.show();

                // Initialize and assign variable
                EditText editText=dialog.findViewById(R.id.edit_text);
                ListView listView=dialog.findViewById(R.id.list_view);

                // Initialize array adapter
                ArrayAdapter<String> adapter=new ArrayAdapter<>(SaleReportActivity.this, android.R.layout.simple_list_item_1,wts);

                // set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        searchTxtView.setText(adapter.getItem(position));

                        // Dismiss dialog
                        dialog.dismiss();

                    }
                });
            }
        });
        GetDefaultDate();
        datePickerDialog = new DatePickerDialog(SaleReportActivity.this,myDateListener,year,month,day);
        todatePickerDialog = new DatePickerDialog(SaleReportActivity.this,mytoDateListener,year,month,day);
        instance = this;
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
                    String dt = StringUtils.leftPad(String.valueOf(arg3),2,'0');
                    StringBuilder sb = new StringBuilder().append(dt).append("/")
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
                    String dt = StringUtils.leftPad(String.valueOf(arg3),2,'0');
                    StringBuilder sb = new StringBuilder().append(dt).append("/")
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
    private ArrayList<SaleReport> GetSaleReport(){
        String frmdt = frmDateTextView.getText().toString();
        String todt = toDateTextView.getText().toString();
        String waiter  = searchTxtView.getText().toString();
        ArrayList<SaleReport> sales = dbHelper.GetAllSales(waiter);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date fromDate = format.parse(frmdt,new ParsePosition(0));
        Date toDate = format.parse(todt,new ParsePosition(0));
        ArrayList<SaleReport> items = new ArrayList<SaleReport>();
        for(int i=0;i<sales.size();i++){
            SaleReport sr = sales.get(i);
            if(sr.getBillDt().compareTo(fromDate) >= 0 && sr.getBillDt().compareTo(toDate) <=0 ){
                items.add(sr);
            }
        }
        return items;
    }

    private void LoadSaleReport(){
        ArrayList<SaleReport> items = GetSaleReport();
        int rc = gridLayout.getRowCount();
        if(rc>1){
            int count = (rc-1)*4;
            gridLayout.removeViews(4,count);
        }
        double totalSaleamt=0;
        for(int i=0;i<items.size();i++){
            SaleReport sr = items.get(i);
            dynamicView = new DynamicViewSaleReport(this);
            gridLayout.addView(dynamicView.billNoTextView(this,sr.getBillNo()));
            gridLayout.addView(dynamicView.billDateTextView(this,sr.getBillDate()));
            String saleAmtStr = String.format("%.0f",sr.getBillAmount());
            gridLayout.addView(dynamicView.billAmountTextView(this,saleAmtStr));
            String billdetails = sr.getBillNo()+"~"+sr.getBillDate();
            gridLayout.addView(dynamicView.printButton(this,billdetails));
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
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
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
                    ArrayList<SaleReport> items = GetSaleReport();
                    if(items.size()>0){
                        Common.saleReports = items;
                        Common.saleReportFrmDate = frmDateTextView.getText().toString();
                        Common.saleReportToDate = toDateTextView.getText().toString();
                        if(Common.isWifiPrint){
                            PrintWifi printWifi = new PrintWifi(SaleReportActivity.this,false);
                            printWifi.Print();
                        }
                        else{
                            PrintBluetooth printBluetooth = new PrintBluetooth(SaleReportActivity.this);
                            printBluetooth.PrintSaleReport();
                        }
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
    public void PrintBill(String billdetail){
        try{
            String[] bd = billdetail.split("~");
            if(bd.length>1){
                int billno = 0;
                String user = "";
                Date billdt = new Date();
                ArrayList<Bills_Item> bills = dbHelper.GetBills_Item(bd[1],bd[0]);
                ArrayList<ItemsCart> itemsCarts = new ArrayList<>();
                for (Bills_Item bi:
                     bills) {
                    ItemsCart ic = new ItemsCart();
                    ic.setItem_No(bi.getItem_No());
                    ic.setItem_Name(bi.getItem_Name());
                    ic.setPrice(bi.getPrice());
                    ic.setQty((int) bi.getQty());
                    user = bi.getWaiter();
                    billno = bi.getBill_No();
                    billdt = bi.getBill_Date();
                    itemsCarts.add(ic);
                }
                Common.billDate = billdt;
                Common.billNo = billno;
                Common.itemsCarts = itemsCarts;
                Common.waiter = user;
                if(Common.isWifiPrint){
                    PrintWifi printWifi = new PrintWifi(SaleReportActivity.this,true);
                    printWifi.onlyBill = true;
                    printWifi.Print();
                }
                else{
                    PrintBluetooth printBluetooth = new PrintBluetooth(SaleReportActivity.this);
                    printBluetooth.Print();
                }
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }
}