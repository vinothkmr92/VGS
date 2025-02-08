package com.example.collections;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textview.MaterialTextView;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CollectionReportActivity extends AppCompatActivity implements View.OnClickListener {


    TextView fromDateView;
    TextView toDateView;
    MaterialTextView totalCollectionView;
    MaterialTextView cashAmtView;
    MaterialTextView upiAmtView;
    PieChart collectionChart;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    Button btnPrint;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    Switch consolidatedRpt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection_report);
        try{
            InitializeComponents();
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date date = new Date();
            fromDateView.setText(format.format(date));
            toDateView.setText(format.format(date));
            new GetCollections().execute("");

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

    private void  InitializeComponents(){
        fromDateView = findViewById(R.id.frmDt);
        toDateView = findViewById(R.id.toDt);
        totalCollectionView = findViewById(R.id.totalCollectedAmt);
        cashAmtView = findViewById(R.id.cashAmt);
        upiAmtView = findViewById(R.id.upiAmt);
        collectionChart = findViewById(R.id.collectionChart);
        btnPrint = findViewById(R.id.btnPrintReport);
        consolidatedRpt = findViewById(R.id.consolidated);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(CollectionReportActivity.this,myDateListener,year,month,day);
        todatePickerDialog = new DatePickerDialog(CollectionReportActivity.this,mytoDateListener,year,month,day);
        btnPrint.setOnClickListener(this);
        fromDateView.setOnClickListener(this);
        toDateView.setOnClickListener(this);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(),R.font.orienta);
        Typeface boldTypeface = Typeface.create(typeface,Typeface.BOLD);
        collectionChart.setNoDataTextTypeface(boldTypeface);
        collectionChart.setNoDataTextColor(ResourcesCompat.getColor(getResources(),R.color.MediumVioletRed,null));
        collectionChart.setNoDataText("NO DATA FOUND.");
    }


    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(CollectionReportActivity.this);
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

    public static Float convertToFloat(double doubleValue) {
        return (float) doubleValue;
    }
    private void setupPieChart(double cashAmt,double upiAmt){

        List<PieEntry> pieEntires = new ArrayList<>();
        Float cashAmtf = convertToFloat(cashAmt);
        Float upiAmtf = convertToFloat(upiAmt);
        if(cashAmt>0){
            pieEntires.add(new PieEntry(cashAmtf,"Cash"));
        }
        if(upiAmt>0){
            pieEntires.add(new PieEntry(upiAmtf,"UPI"));
        }
        PieDataSet dataSet = new PieDataSet(pieEntires,"");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.orienta);
        dataSet.setValueTypeface(typeface);
        //dataSet.setDrawValues(false);
        dataSet.setValueTextSize(20);
        dataSet.setHighlightEnabled(true);
        dataSet.setValueFormatter(new IndianRuppeFormatter());
        PieData data = new PieData(dataSet);
        //Get the chart

        collectionChart.setCenterText("Collections");
        collectionChart.setDrawEntryLabels(false);
        collectionChart.setContentDescription("");
        collectionChart.setCenterTextSize(15);

        //chart.setDrawMarkers(true);
        //pieChart.setMaxHighlightDistance(34);
        //chart.setEntryLabelTextSize(30);
        collectionChart.setHoleRadius(50);

        //legend attributes
        Legend legend = collectionChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setWordWrapEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(15);
        legend.setFormSize(20);
        legend.setFormToTextSpace(2);
        collectionChart.getDescription().setEnabled(false);
        collectionChart.setData(data);
        collectionChart.invalidate();
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
                        String s = format.format(date);
                        toDateView.setText(format.format(date));
                        new GetCollections().execute("");
                    } catch (Exception e) {
                        showCustomDialog("Error",e.getMessage());
                    }
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
                        new GetCollections().execute("");
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
            case R.id.toDt:
                todatePickerDialog.show();
                break;
            case R.id.btnPrintReport:
                PrintCollectionReport();
                break;
        }
    }
    private void PrintCollectionReport(){
      try{
          if(CommonUtil.collectionsRptConsolidated !=null && !CommonUtil.collections.isEmpty()){
              PrinterUtil printerUtil = new PrinterUtil(this,null,
                      CommonUtil.collectionsRptConsolidated,CommonUtil.collections,false,true,consolidatedRpt.isChecked());
              printerUtil.Print();
          }
          else {
              showCustomDialog("Warning","No Data to Print.");
          }
      }
      catch (Exception ex){
          showCustomDialog("Error",ex.getMessage());
      }
    }

    public class GetCollections extends AsyncTask<String,String, CollectionsRptConsolidated>
    {
        String frmDate = fromDateView.getText().toString();
        String toDate = toDateView.getText().toString();
        String selectedUser = CommonUtil.loggedinUser.toUpperCase();
        Double revenue = 0d;
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(CollectionReportActivity.this);
        ConnectionClass connectionClass = null;
        Connection con = null;
        @Override
        protected void onPreExecute() {
            CommonUtil.collections = new ArrayList<>();
            btnPrint.setVisibility(View.GONE);
            CommonUtil.collectionsRptConsolidated = null;
            if(frmDate.contains("Sept")){
                frmDate = frmDate.replace("Sept","Sep");
            }
            if(toDate.contains("Sept")){
                toDate = toDate.replace("Sept","Sep");
            }
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Getting Collections Data");
            dialog.setMessage("Please Wait...");
            dialog.show();
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(CollectionsRptConsolidated r) {
            if(!error.isEmpty()){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Error",error);
            }
            else {
                btnPrint.setVisibility(View.VISIBLE);
                CommonUtil.collectionsRptConsolidated = r;
                Double billamts = r.TotalAmt;
                Double cashamts = r.CashAmt;
                Double upiamts = r.UpiAmt;
                if(billamts>0){
                    setupPieChart(cashamts,upiamts);
                }
                else {
                    collectionChart.clear();
                }
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                formatter.setMaximumFractionDigits(0);
                String symbol = formatter.getCurrency().getSymbol();
                totalCollectionView.setText(formatter.format(billamts).replace(symbol,symbol+" "));
                cashAmtView.setText(formatter.format(cashamts).replace(symbol,symbol+" "));
                upiAmtView.setText(formatter.format(upiamts).replace(symbol,symbol+" "));
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        @Override
        protected CollectionsRptConsolidated doInBackground(String... params) {
            CollectionsRptConsolidated coll = new CollectionsRptConsolidated();
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            try {
                if (con == null) {
                    error = "Database Connection Failed";
                }
                else
                {
                    coll.frmDate = format.parse(frmDate);
                    coll.toDate = format.parse(toDate);
                    String query = String.format("SELECT SUM(AMOUNT) AS AMT,COUNT(*) AS CN FROM PAYMENTS WHERE IS_FIELD_COLLECTION=1 AND CAST(PAYMENT_DATE AS DATE) BETWEEN '%s' AND '%s'",frmDate,toDate);
                    query = query+String.format(" AND UPPER(RECEIVED_BY)='%s'",selectedUser);
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        coll.TotalAmt = rs.getDouble("AMT");
                        coll.NoofBills = rs.getInt("CN");
                        isSuccess = true;
                    }
                    rs.close();
                    String cashQuery = query+" AND PAYMENT_MODE='CASH' ";
                    String upiQuery = query+" AND PAYMENT_MODE='UPI'";
                    ResultSet cashR = stmt.executeQuery(cashQuery);
                    if(cashR.next()){
                        coll.CashAmt = cashR.getDouble("AMT");
                        coll.CashBills = cashR.getInt("CN");
                    }
                    cashR.close();
                    ResultSet upiR = stmt.executeQuery(upiQuery);
                    if(upiR.next()){
                        coll.UpiAmt = upiR.getDouble("AMT");
                        coll.UpiBills = upiR.getInt("CN");
                    }
                    upiR.close();
                    String colQuery = String.format("SELECT PAYMENT_ID,MEMBER_ID,LOAN_NO,AMOUNT FROM PAYMENTS WHERE IS_FIELD_COLLECTION=1 AND CAST(PAYMENT_DATE AS DATE) BETWEEN '%s' AND '%s' AND UPPER(RECEIVED_BY)='%s'",frmDate,toDate,selectedUser);
                    ResultSet colResult = stmt.executeQuery(colQuery);
                    ArrayList<Collections> cols = new ArrayList<>();
                    while (colResult.next()){
                        Collections col = new Collections();
                        col.paymentID = colResult.getString("PAYMENT_ID");
                        col.MemberID = colResult.getString("MEMBER_ID");
                        col.AccNo = colResult.getString("LOAN_NO");
                        col.Amount = colResult.getDouble("AMOUNT");
                        cols.add(col);
                    }
                    CommonUtil.collections = cols;
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions: "+ex.getMessage();
            }
            return coll;
        }
    }
}