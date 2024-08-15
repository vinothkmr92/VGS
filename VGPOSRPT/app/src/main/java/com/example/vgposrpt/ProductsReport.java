package com.example.vgposrpt;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProductsReport extends AppCompatActivity implements View.OnClickListener {

    TextView frmDateTextView;
    TextView toDateTextView;
    LinearLayout itemRptContainer;
    ScrollView itemsrptScrollview;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    TextView txtViewTotalAmt;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_products_report);
            frmDateTextView = (TextView) findViewById(R.id.itemrptFrmDate);
            toDateTextView = (TextView) findViewById(R.id.itemrptToDate);
            txtViewTotalAmt = (TextView)findViewById(R.id.ttAmtPrdReport);
            itemRptContainer = (LinearLayout)findViewById(R.id.itemsrptContainer);
            itemsrptScrollview = (ScrollView)findViewById(R.id.itemsrptScrollView);
            frmDateTextView.setOnClickListener(this);
            toDateTextView.setOnClickListener(this);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            frmDateTextView.setText(format.format(date));
            toDateTextView.setText(format.format(date));
            GetDefaultDate();
            datePickerDialog = new DatePickerDialog(ProductsReport.this,myDateListener,year,month,day);
            todatePickerDialog = new DatePickerDialog(ProductsReport.this,mytoDateListener,year,month,day);
            ArrayAdapter<Branch> adapter = new ArrayAdapter<Branch>(this,R.layout.drop_down_item,CommonUtil.branchList);
            AutoCompleteTextView autoCompleteTextView = findViewById(R.id.branches);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO
                }
            });
            autoCompleteTextView.setText("ALL");
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage(),false);
        }


    }
    public void showCustomDialog(String title,String Message,Boolean close) {
        MaterialAlertDialogBuilder dialog =  new MaterialAlertDialogBuilder(ProductsReport.this,
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(close){
                    finish();
                    System.exit(0);
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private  void GetDefaultDate(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
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
                    StringBuilder sb = new StringBuilder().append(arg1).append("-")
                            .append(monthstr).append("-").append(dt);
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
                    StringBuilder sb = new StringBuilder().append(arg1).append("-")
                            .append(monthstr).append("-").append(dt);
                    frmDateTextView.setText(sb.toString());
                }
            };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.itemrptFrmDate:
                datePickerDialog.show();
                break;
            case R.id.itemrptToDate:
                todatePickerDialog.show();
                break;
        }
    }
}