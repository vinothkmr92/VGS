package com.example.bwq;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Reports extends AppCompatActivity implements View.OnClickListener {


    EditText srchPr;
    EditText frmDt;
    EditText toDt;
    TableLayout gridDt;
    ImageButton btnSrch;
    DatabaseHelper dbHelper;
    private Dialog progressBar;
    DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        dbHelper = new DatabaseHelper(this);
        progressBar = new Dialog(Reports.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        srchPr= (EditText)findViewById(R.id.srchPrd);
        srchPr.setOnEditorActionListener(new DoneOnEditorActionListener());
        frmDt = (EditText) findViewById(R.id.frmDt);
        toDt = (EditText) findViewById(R.id.toDt);
        frmDt.setInputType(InputType.TYPE_NULL);
        frmDt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Reports.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                frmDt.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        toDt.setInputType(InputType.TYPE_NULL);
        toDt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Reports.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                toDt.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        gridDt = (TableLayout)findViewById(R.id.datagrid);
        btnSrch = (ImageButton) findViewById(R.id.btnSearch);
        btnSrch.setOnClickListener(this);
    }

    private void LoadGridView(ArrayList<Product> prList){
        gridDt.removeAllViews();
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setPadding(10,10,10,10);
        TextView textView = new TextView(this);
        textView.setText("PRODUCT");
        textView.setTextColor(Color.BLUE);
        //textView.setTextSize(30);
        textView.setBackgroundResource(R.drawable.cellborder);
        textView.setBackgroundColor(Color.WHITE);
        row.addView(textView);
        TextView textView1 = new TextView(this);
        textView1.setText("                   WT");
        //textView1.setTextSize(30);
        textView1.setPadding(10,0,10,0);
        textView1.setTextColor(Color.BLUE);
        textView1.setBackgroundResource(R.drawable.cellborder);
        textView1.setBackgroundColor(Color.WHITE);
        row.addView(textView1);
        TextView textView2 = new TextView(this);
        textView2.setText("                                 DATE");
        //textView2.setTextSize(30);
        textView2.setPadding(10,0,10,0);
        textView2.setTextColor(Color.BLUE);
        textView2.setBackgroundResource(R.drawable.cellborder);
        textView2.setBackgroundColor(Color.WHITE);
        row.addView(textView2);
        gridDt.addView(row,new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        int i=0;
        for(Product b : prList)
        {
            // Inflate your row "template" and fill out the fields.
            TableRow row1 = new TableRow(this);
            row1.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row1.setPadding(5,5,5,5);
            TextView textView3 = new TextView(this);
            textView3.setText(b.getProductName());
            textView3.setTextColor(Color.BLACK);
            textView3.setBackgroundResource(R.drawable.cellborder);
            textView3.setBackgroundColor(Color.WHITE);
            row1.addView(textView3);
            TextView textView4 = new TextView(this);
            textView4.setText(String.valueOf("                  "+b.getWeight()));
            textView4.setTextColor(Color.BLACK);
            textView4.setBackgroundResource(R.drawable.cellborder);
            textView4.setBackgroundColor(Color.WHITE);
            textView4.setPadding(10,0,10,0);
            row1.addView(textView4);
            TextView textView5 = new TextView(this);
            textView5.setText(String.valueOf("                  "+b.getDtString()));
            textView5.setBackgroundResource(R.drawable.cellborder);
            textView5.setTextColor(Color.BLACK);
            textView5.setPadding(10,0,10,0);
            textView5.setBackgroundColor(Color.WHITE);
            row1.addView(textView5);
            gridDt.addView(row1,new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

    }

    @Override
    public void onClick(View v) {
        progressBar.show();
        try{
            String prName = srchPr.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fd = frmDt.getText().toString();
            String td = toDt.getText().toString();
            if(fd.isEmpty() || td.isEmpty()){
                showCustomDialog("Warning","Please Select Date..!");
                progressBar.cancel();
                return;
            }
            Date fromDate = sdf.parse(fd);
            Date toDate = sdf.parse(td);
            ArrayList<Product> pdList = new ArrayList<Product>();
            for(Product p: dbHelper.GetProducts()){
                if(p.getPdDateAlone().compareTo(fromDate)>=0 && p.getPdDateAlone().compareTo(toDate)<=0){
                    if(!prName.isEmpty()){
                        if(p.getProductName().equals(prName)){
                            pdList.add(p);
                        }
                    }
                    else {
                        pdList.add(p);
                    }
                }
            }
            LoadGridView(pdList);
            progressBar.cancel();
        }
        catch (Exception ex){
            progressBar.cancel();
            showCustomDialog("Exception",ex.getMessage());
        }
    }

    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}
