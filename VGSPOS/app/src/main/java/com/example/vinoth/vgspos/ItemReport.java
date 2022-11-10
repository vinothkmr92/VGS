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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private GridLayout gridView;
    private DynamicViewItemRpt dynamicView;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    TextView searchTxtView;
    Dialog dialog;
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
        btnPrint.setOnClickListener(this);
        btnrpt.setOnClickListener(this);
        btnFrmDatePicker.setOnClickListener(this);
        btnToDatePicker.setOnClickListener(this);
        searchTxtView = (TextView)findViewById(R.id.customerinfoitemwise);
        searchTxtView.setText("ALL");
        ArrayList<String> wts = new ArrayList<String>();
        wts.add("ALL");
        ArrayList<Customer> customers = dbHelper.GetCustomers();
        for(int i=0;i<customers.size();i++){
            wts.add(customers.get(i).getCustomerName());
        }
        searchTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog=new Dialog(ItemReport.this);

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
                ArrayAdapter<String> adapter=new ArrayAdapter<>(ItemReport.this, android.R.layout.simple_list_item_1,wts);

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
        b.setCanceledOnTouchOutside(false);
        b.setCancelable(false);
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
            if(Common.isWifiPrint){
                PrintWifi printWifi = new PrintWifi(ItemReport.this,false);
                printWifi.Print();
            }
            else{
                PrintBluetooth printBluetooth = new PrintBluetooth(ItemReport.this);
                printBluetooth.PrintItemWiseReport();
            }

        } catch (Exception e) {
            showCustomDialog("Print Error",e.getMessage());
            e.printStackTrace();
        }
    }
    public void LoadReportViews(){
        String frmdt = frmDateTextView.getText().toString();
        String todt = toDateTextView.getText().toString();
        String waiter  = searchTxtView.getText().toString();
        ArrayList<ItemsRpt> itemsRpts = dbHelper.GetReports(frmdt,todt,waiter);
        int rc = gridView.getRowCount();
        if(rc>1){
            int count = (rc-1)*2;
            gridView.removeViews(2,count);
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
                String waiter  = searchTxtView.getText().toString();
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