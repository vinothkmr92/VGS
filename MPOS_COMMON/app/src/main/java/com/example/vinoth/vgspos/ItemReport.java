package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ngx.mp100sdk.Enums.Alignments;
import com.ngx.mp100sdk.Intefaces.INGXCallback;
import com.ngx.mp100sdk.NGXPrinter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemReport extends AppCompatActivity implements  View.OnClickListener{

    DatabaseHelper dbHelper;
    private Dialog progressBar;
    private EditText reportDt;
    private ListView listView;
    private Button btnrpt;
    private Button btnPrint;
    private Spinner waiters;
    public static NGXPrinter ngxPrinter = NGXPrinter.getNgxPrinterInstance();
    private INGXCallback ingxCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_report);
        progressBar = new Dialog(ItemReport.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        dbHelper = new DatabaseHelper(this);
        reportDt = (EditText) findViewById(R.id.rptDate);
        listView = (ListView) findViewById(R.id.rptView);
        btnrpt = (Button) findViewById(R.id.btngetreport);
        btnPrint = (Button)findViewById(R.id.btnrptprint);
        waiters = (Spinner)findViewById(R.id.rptwaiter);
        btnPrint.setOnClickListener(this);
        btnrpt.setOnClickListener(this);
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
        reportDt.setText(format.format(date));
        try{
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
            showCustomDialog("Error",ex.getMessage());
        }
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
            ngxPrinter.setDefault();
            //Bitmap hyndaiLogo = BitmapFactory.decodeResource(this.getResources(),R.drawable.hyundai_logo);
            //ngxPrinter.printImage(hyndaiLogo);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("SS HYDERABAD BIRIYANI", Alignments.CENTER,28);
            ngxPrinter.setStyleDoubleWidth();
            String waiter  = waiters.getSelectedItem().toString();
            ngxPrinter.printText("KOT REPORTS-> "+waiter, Alignments.CENTER, 30);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("ITEMS DETAILS", Alignments.CENTER, 28);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("REPORT DATE     : " + reportDt.getText().toString(), Alignments.LEFT, 24);
            ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
            ngxPrinter.printText("ITEM                     QTY", Alignments.LEFT, 24);
            ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
            NumberFormat nf = DecimalFormat.getInstance();
            nf.setMaximumFractionDigits(0);
            for(int k=0;k<items.size();k++){
                String name = items.get(k).getItemName();
                String qty = nf.format(items.get(k).getQuantity());
                ngxPrinter.printText( name+": "+ qty, Alignments.LEFT, 24);
            }
            ngxPrinter.printText("                              ");
            ngxPrinter.printText("                              ");
            ngxPrinter.setDefault();
            Toast.makeText(this, "Print Queued Scuessfully.!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            showCustomDialog("Print Error",e.getMessage());
            //Toast.makeText(MainActivity.this, e.getMessage(), 1).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        String dt = reportDt.getText().toString();
        String waiter  = waiters.getSelectedItem().toString();
        ArrayList<ItemsRpt> items = dbHelper.GetReports(dt,waiter);
        switch (v.getId()){
            case R.id.btngetreport:
                ItemListAdaptor adaptor = new ItemListAdaptor(this,R.layout.activity_custom_report_layout,items);
                listView.setAdapter(adaptor);
                break;
            case R.id.btnrptprint:
                if(items.size()>0){
                    PrintReport(items);
                }
                else {
                    showCustomDialog("Warning","No Details to Print.");
                }
        }

    }
}