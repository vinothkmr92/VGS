package com.example.vgtxtrpt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

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
import java.util.stream.Collectors;


public class ReportFragment extends Fragment implements View.OnClickListener {


    TextView frmDateTextView;
    TextView toDateTextView;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    AutoCompleteTextView autoCompleteTextView;
    TextInputLayout txtBranch;
    PieChart prodChart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    MaterialButton btnViewProducts;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context cnt = getContext();
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        try{
            frmDateTextView = view.findViewById(R.id.productsrptFrmDate);
            toDateTextView = view.findViewById(R.id.productsrptToDate);
            txtBranch = view.findViewById(R.id.productsbr);
            prodChart = view.findViewById(R.id.chart2);
            btnViewProducts = view.findViewById(R.id.btnViewProducts);
            btnViewProducts.setOnClickListener(this);
            frmDateTextView.setOnClickListener(this);
            toDateTextView.setOnClickListener(this);
            txtBranch.setEnabled(CommonUtil.loggedinUserRoleID>1);
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date date = new Date();
            frmDateTextView.setText(format.format(date));
            toDateTextView.setText(format.format(date));
            if(!CommonUtil.frmDate.isEmpty()){
                frmDateTextView.setText(CommonUtil.frmDate);
            }
            if(!CommonUtil.toDate.isEmpty()){
                toDateTextView.setText(CommonUtil.toDate);
            }
            GetDefaultDate();
            datePickerDialog = new DatePickerDialog(getContext(),myDateListener,year,month,day);
            todatePickerDialog = new DatePickerDialog(getContext(),mytoDateListener,year,month,day);
            Counters defaultBranch = CommonUtil.countersList.get(0);

            if(CommonUtil.selectedCounter!=null){
                defaultBranch = CommonUtil.selectedCounter;
            }
            else if(CommonUtil.countersList.size()<=2){
                defaultBranch = CommonUtil.countersList.get(1);
            }
            txtBranch.getEditText().setText(defaultBranch.CounterName);
            autoCompleteTextView = view.findViewById(R.id.prductsbranches);
            ArrayAdapter<Counters> adapter = new ArrayAdapter<Counters>(getContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,CommonUtil.countersList);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO
                    new GetProductsSummary().execute("");
                }
            });
            new GetProductsSummary().execute("");
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage(),true);
        }
        return  view;
    }
    public void showCustomDialog(String title,String Message,Boolean close) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(getContext());
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(close){
                    getActivity().finish();
                    System.exit(0);
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.productsrptFrmDate:
                if(CommonUtil.loggedinUserRoleID>1){
                    datePickerDialog.show();
                }
                break;
            case R.id.productsrptToDate:
                if(CommonUtil.loggedinUserRoleID>1){
                    todatePickerDialog.show();
                }
                break;
            case R.id.btnViewProducts:
                CommonUtil.selectedCounter = GetSelectedCounter();
                CommonUtil.frmDate = frmDateTextView.getText().toString();
                CommonUtil.toDate = toDateTextView.getText().toString();
                Intent intent2 = new Intent(getContext(), ViewProductsActivity.class);
                startActivity(intent2);
        }
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
                        toDateTextView.setText(format.format(date));
                        new GetProductsSummary().execute("");
                    } catch (Exception e) {
                        showCustomDialog("Error",e.getMessage(),false);
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
                        frmDateTextView.setText(format.format(date));
                        new GetProductsSummary().execute("");
                    } catch (Exception e) {
                        showCustomDialog("Error",e.getMessage(),false);
                    }

                }
            };

    public String GetSelectedCounterID(){
        String branchcode = "0";
        List<Counters> sel =CommonUtil.countersList.stream().filter(u->u.CounterName.equals(autoCompleteTextView.getText().toString())).collect(Collectors.toList());
        if(sel.size()>0){
            branchcode = sel.get(0).CounterID;
        }
        return  branchcode;
    }
    public Counters GetSelectedCounter(){
        Counters brselected = null;
        List<Counters> sel =CommonUtil.countersList.stream().filter(u->u.CounterName.equals(autoCompleteTextView.getText().toString())).collect(Collectors.toList());
        if(sel.size()>0){
            brselected = sel.get(0);
        }
        return  brselected;
    }
    public static Float convertToFloat(double doubleValue) {
        return (float) doubleValue;
    }
    private void setupPieChartProd(ArrayList<ProductsSummary> cslist){

        List<PieEntry> pieEntires = new ArrayList<>();
        for( int i = 0 ; i<cslist.size();i++){
            Float amt = convertToFloat(cslist.get(i).getSoldQty());
            pieEntires.add(new PieEntry(amt,cslist.get(i).getProductName()));
        }
        PieDataSet dataSet = new PieDataSet(pieEntires,"");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.orienta);
        dataSet.setValueTypeface(typeface);
        //dataSet.setDrawValues(false);
        dataSet.setValueTextSize(20);

        PieData data = new PieData(dataSet);
        //Get the chart

        prodChart.setCenterText("PRODUCTS");
        prodChart.setDrawEntryLabels(false);
        prodChart.setContentDescription("");
        prodChart.setCenterTextSize(15);
        //chart.setDrawMarkers(true);
        //pieChart.setMaxHighlightDistance(34);
        //chart.setEntryLabelTextSize(30);
        prodChart.setHoleRadius(50);

        //legend attributes
        Legend legend = prodChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setWordWrapEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(15);
        legend.setFormSize(20);
        legend.setFormToTextSpace(2);
        prodChart.getDescription().setEnabled(false);
        prodChart.setData(data);
        prodChart.invalidate();

    }

    public class GetProductsSummary extends AsyncTask<String,String, ArrayList<ProductsSummary>>
    {
        String frmDate = frmDateTextView.getText().toString();
        String toDate = toDateTextView.getText().toString();
        String counterID = GetSelectedCounterID();
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);

        @Override
        protected void onPreExecute() {
            if(frmDate.contains("Sept")){
                frmDate = frmDate.replace("Sept","Sep");
            }
            if(toDate.contains("Sept")){
                toDate = toDate.replace("Sept","Sep");
            }
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.salesreport);
            dialog.setMessage("Loading...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ProductsSummary> r) {

            if(!error.isEmpty()){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Error",error,false);
            }
            else {
                if(r.size()>0){
                    btnViewProducts.setVisibility(View.VISIBLE);
                    prodChart.setVisibility(View.VISIBLE);
                }
                else {
                    btnViewProducts.setVisibility(View.GONE);
                    prodChart.setVisibility(View.GONE);
                }
                setupPieChartProd(r);
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        @Override
        protected ArrayList<ProductsSummary> doInBackground(String... params) {
            ArrayList<ProductsSummary> prod = new ArrayList<>();
            try {
                ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                Connection con = connectionClass.CONN();
                if (con == null) {
                    error = "Database Connection Failed.";
                } else {
                    String query = String.format("SELECT TOP 4 BP.PRODUCT_NAME,SUM(BP.QUANTITY) AS QA FROM BILL_PRODUCTS BP,SALE S WHERE S.BILL_NO=BP.Bill_No AND CAST(S.Bill_Date AS DATE)=CAST(BP.Bill_Date AS DATE) AND S.Counter_ID = BP.Counter_Id  AND  CAST(S.BILL_DATE AS DATE) BETWEEN '%s' AND '%s'",frmDate,toDate);
                    if(!counterID.equalsIgnoreCase("0")){
                        query = query+String.format(" AND BP.COUNTER_ID='%s' ",counterID);
                    }
                    query = query+"GROUP BY BP.PRODUCT_NAME ORDER BY QA DESC";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        ProductsSummary cs = new ProductsSummary();
                        cs.setProductName(rs.getString("PRODUCT_NAME"));
                        cs.setSoldQty(rs.getDouble("QA"));
                        prod.add(cs);
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return prod;
        }
    }
}