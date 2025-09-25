package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    AutoCompleteTextView autoCompleteUsersView;
    TextInputLayout txtBranch;
    TextInputLayout txtUser;
    PieChart chart;
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
            chart = view.findViewById(R.id.chart1);
            prodChart = view.findViewById(R.id.chart2);
            btnViewProducts = view.findViewById(R.id.btnViewProducts);
            btnViewProducts.setOnClickListener(this);
            frmDateTextView.setOnClickListener(this);
            toDateTextView.setOnClickListener(this);
            txtUser = view.findViewById(R.id.productUser);
            autoCompleteUsersView = view.findViewById(R.id.productUsers);
            txtBranch.setEnabled(CommonUtil.loggedinUserRoleID>1);
            txtUser.setEnabled(CommonUtil.loggedinUserRoleID>1);
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
            String defaultUser = "ALL";
            if(CommonUtil.loggedinUserRoleID==1){
                defaultUser = CommonUtil.loggedinUser;
            }
            txtUser.getEditText().setText(defaultUser);
            GetDefaultDate();
            datePickerDialog = new DatePickerDialog(getContext(),myDateListener,year,month,day);
            todatePickerDialog = new DatePickerDialog(getContext(),mytoDateListener,year,month,day);
            Branch defaultBranch = CommonUtil.branchList.get(0);

            if(CommonUtil.selectedBarnch!=null){
                defaultBranch = CommonUtil.selectedBarnch;
            }
            else if(CommonUtil.branchList.size()<=2){
                defaultBranch = CommonUtil.branchList.get(1);
            }
            txtBranch.getEditText().setText(defaultBranch.getBranch_Name());
            autoCompleteTextView = view.findViewById(R.id.prductsbranches);
            ArrayAdapter<Branch> adapter = new ArrayAdapter<Branch>(getContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,CommonUtil.branchList);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO
                    new GetCategorySummary().execute("");
                }
            });
            ArrayList<String> userlistforAdaptor = new ArrayList<>();
            userlistforAdaptor.add("ALL");
            userlistforAdaptor.addAll(CommonUtil.users);
            ArrayAdapter<String> useradapter = new ArrayAdapter<>(getContext(),com.google.android.material.R.layout.support_simple_spinner_dropdown_item,userlistforAdaptor);
            autoCompleteUsersView.setAdapter(useradapter);
            autoCompleteUsersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new GetCategorySummary().execute("");
                }
            });
            new GetCategorySummary().execute("");
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
                CommonUtil.selectedBarnch = GetSelectedBranch();
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
                        new GetCategorySummary().execute("");
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
                        new GetCategorySummary().execute("");
                    } catch (Exception e) {
                        showCustomDialog("Error",e.getMessage(),false);
                    }

                }
            };

    public int GetSelectedBranchCode(){
        int branchcode = 0;
        List<Branch> sel =CommonUtil.branchList.stream().filter(u->u.getBranch_Name().equals(autoCompleteTextView.getText().toString())).collect(Collectors.toList());
        if(sel.size()>0){
            branchcode = sel.get(0).getBranch_Code();
        }
        return  branchcode;
    }
    public Branch GetSelectedBranch(){
        Branch brselected = null;
        List<Branch> sel =CommonUtil.branchList.stream().filter(u->u.getBranch_Name().equals(autoCompleteTextView.getText().toString())).collect(Collectors.toList());
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
    private void setupPieChart(ArrayList<CategorySummary> cslist){

        List<PieEntry> pieEntires = new ArrayList<>();
        for( int i = 0 ; i<cslist.size();i++){
            Float amt = convertToFloat(cslist.get(i).getSoldAmount());
            pieEntires.add(new PieEntry(amt,cslist.get(i).getCategory()));
        }
        PieDataSet dataSet = new PieDataSet(pieEntires,"");
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.orienta);
        dataSet.setValueTypeface(typeface);
        dataSet.setValueFormatter(new IndianRuppeFormatter());
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setDrawValues(false);
        dataSet.setValueTextSize(20);

        PieData data = new PieData(dataSet);
        //Get the chart

        chart.setCenterText("CATEGORY");
        chart.setDrawEntryLabels(false);
        chart.setContentDescription("");
        chart.setCenterTextSize(15);
        //chart.setDrawMarkers(true);
        //pieChart.setMaxHighlightDistance(34);
        //chart.setEntryLabelTextSize(30);
        chart.setHoleRadius(50);

        //legend attributes
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setWordWrapEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(15);
        legend.setFormSize(20);
        legend.setFormToTextSpace(2);
        chart.getDescription().setEnabled(false);
        chart.setData(data);
        chart.invalidate();

        new GetProductsSummary().execute("");

    }

    public class GetCategorySummary extends AsyncTask<String,String, ArrayList<CategorySummary>>
    {
        String frmDate = frmDateTextView.getText().toString();
        String toDate = toDateTextView.getText().toString();
        int branchCode = GetSelectedBranchCode();
        Boolean isSuccess = false;
        String error = "";
        String selectedUser = "ALL";
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);

        @Override
        protected void onPreExecute() {
            selectedUser = txtUser.getEditText().getText().toString();
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
        protected void onPostExecute(ArrayList<CategorySummary> r) {

            if(!error.isEmpty()){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Error",error,false);
            }
            else {
                if(r.size()>0){
                    chart.setVisibility(View.VISIBLE);
                }
                else {
                    chart.setVisibility(View.GONE);
                }
                setupPieChart(r);
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        @Override
        protected ArrayList<CategorySummary> doInBackground(String... params) {
            ArrayList<CategorySummary> cates = new ArrayList<>();
            try {
                ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                Connection con = connectionClass.CONN();
                if (con == null) {
                    error = "Database Connection Failed.";
                } else {
                    String query = String.format("SELECT TOP 4 ROUND(SUM(BP.QUANTITY*BP.PRICE),0) AS PRICE,BP.CATEGORY FROM BILL_PRODUCTS BP,SALE S WHERE S.BILL_NO=BP.Bill_No AND s.BRANCH_CODE=bp.BRANCH_CODE AND CAST(S.Bill_Date AS DATE)=CAST(BP.Bill_Date AS DATE) AND S.Counter_ID = BP.Counter_Id  AND  CAST(S.BILL_DATE AS DATE) BETWEEN '%s' AND '%s'",frmDate,toDate);
                    if(branchCode>0){
                        query = query+String.format(" AND BP.BRANCH_CODE=%s ",branchCode);
                    }
                    if(!selectedUser.equals("ALL")){
                        query = query+String.format(" AND S.USER_NAME='%s' ",selectedUser);
                    }
                    query = query+"GROUP BY BP.CATEGORY ORDER BY PRICE DESC";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        CategorySummary cs = new CategorySummary();
                        cs.setCategory(rs.getString("CATEGORY"));
                        cs.setSoldAmount(rs.getDouble("PRICE"));
                        cates.add(cs);
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return cates;
        }
    }

    public class GetProductsSummary extends AsyncTask<String,String, ArrayList<ProductsSummary>>
    {
        String frmDate = frmDateTextView.getText().toString();
        String toDate = toDateTextView.getText().toString();
        int branchCode = GetSelectedBranchCode();
        Boolean isSuccess = false;
        String error = "";
        String selectedUser = "ALL";
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);

        @Override
        protected void onPreExecute() {
            selectedUser = txtUser.getEditText().getText().toString();
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
                    String query = String.format("SELECT TOP 4 BP.PRODUCT_NAME,SUM(BP.QUANTITY) AS QA FROM BILL_PRODUCTS BP,SALE S WHERE S.BILL_NO=BP.Bill_No AND s.BRANCH_CODE=bp.BRANCH_CODE AND CAST(S.Bill_Date AS DATE)=CAST(BP.Bill_Date AS DATE) AND S.Counter_ID = BP.Counter_Id  AND  CAST(S.BILL_DATE AS DATE) BETWEEN '%s' AND '%s'",frmDate,toDate);
                    if(branchCode>0){
                        query = query+String.format(" AND BP.BRANCH_CODE=%s ",branchCode);
                    }
                    if(!selectedUser.equals("ALL")){
                        query = query+String.format(" AND S.USER_NAME='%s' ",selectedUser);
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