package com.example.vgtxtrpt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
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
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements View.OnClickListener {


    TextView frmDateTextView;
    TextView toDateTextView;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    TextView txtViewTotalAmt;
    TextView txtViewCashAmt;
    TextView txtViewCardAmt;
    TextView txtViewUpiAmt;
    TextView txtViewTotalBills;
    MaterialButton btnViewBills;
    AutoCompleteTextView autoCompleteTextView;
    TextInputLayout txtBranch;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private Boolean doubleBackToExitPressedOnce = false;
    PieChart chart;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME= "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    public static final String COUNTERS = "COUNTERS";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        try{
            chart = view.findViewById(R.id.salesChart);
            frmDateTextView = view.findViewById(R.id.salesrptFrmDate);
            toDateTextView = view.findViewById(R.id.salesrptToDate);
            txtViewTotalAmt = view.findViewById(R.id.totalRevenueAmt);
            txtViewTotalBills = view.findViewById(R.id.billscount);
            btnViewBills = view.findViewById(R.id.btnViewBills);
            txtViewCashAmt = view.findViewById(R.id.cashAmt);
            txtViewCardAmt = view.findViewById(R.id.cardAmt);
            txtViewUpiAmt = view.findViewById(R.id.upiAmt);
            txtBranch = view.findViewById(R.id.salesbr);
            frmDateTextView.setOnClickListener(this);
            toDateTextView.setOnClickListener(this);
            btnViewBills.setOnClickListener(this);
            txtBranch.setEnabled(CommonUtil.loggedinUserRoleID>1);
            sharedpreferences = MySharedPreferences.getInstance(requireContext(),MyPREFERENCES);
            String sqlserver = requireContext().getApplicationContext().getString(R.string.SQL_SERVER);
            String dbnamestr = requireContext().getApplicationContext().getString(R.string.SQL_DBNAME);
            String usr = requireContext().getApplicationContext().getString(R.string.SQL_USERNAME);
            String pwd = requireContext().getApplicationContext().getString(R.string.SQL_PASSWORD);
            String hostname = sharedpreferences.getString(SQLSERVER,sqlserver);
            String Databasename = sharedpreferences.getString(SQLDB,dbnamestr);
            String sqlUserName = sharedpreferences.getString(SQLUSERNAME, usr);
            String sqlPassword = sharedpreferences.getString(SQLPASSWORD, pwd);
            CommonUtil.SQL_SERVER = hostname;
            CommonUtil.DB = Databasename;
            CommonUtil.USERNAME = sqlUserName;
            CommonUtil.PASSWORD = sqlPassword;
            CommonUtil.countersList = getCountersList();
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
            datePickerDialog = new DatePickerDialog(requireContext(),myDateListener,year,month,day);
            todatePickerDialog = new DatePickerDialog(requireContext(),mytoDateListener,year,month,day);
            Counters defaultBranch = CommonUtil.countersList.get(0);

            if(CommonUtil.selectedCounter!=null){
                defaultBranch = CommonUtil.selectedCounter;
            }
            else if(CommonUtil.countersList.size()<=2){
                defaultBranch = CommonUtil.countersList.get(1);
            }
            txtBranch.getEditText().setText(defaultBranch.CounterName);
            autoCompleteTextView = view.findViewById(R.id.salesbranches);
            ArrayAdapter<Counters> adapter = new ArrayAdapter<Counters>(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,CommonUtil.countersList);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO
                    new GetSales().execute("");
                }
            });

            new GetSales().execute("");
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage(),false);
        }
        return view;
    }

    private ArrayList<Counters> getCountersList(){
        ArrayList<Counters> counters = new ArrayList<>();
        String serializedObject = sharedpreferences.getString(COUNTERS, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Counters>>(){}.getType();
            counters = gson.fromJson(serializedObject, type);
        }
        return  counters;
    }




    public void showCustomDialog(String title,String Message,Boolean close) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(requireContext());
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(close){
                    requireActivity().finish();
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
    public static Float convertToFloat(double doubleValue) {
        return (float) doubleValue;
    }
    private void setupPieChart(double cashAmt,double cardAmt,double upiAmt){

        List<PieEntry> pieEntires = new ArrayList<>();
        Float cashAmtf = convertToFloat(cashAmt);
        Float cardAmtf = convertToFloat(cardAmt);
        Float upiAmtf = convertToFloat(upiAmt);
        if(cashAmt>0){
            pieEntires.add(new PieEntry(cashAmtf,"Cash"));
        }
        if(cardAmt>0){
            pieEntires.add(new PieEntry(cardAmtf,"Card"));
        }
        if(upiAmt>0){
            pieEntires.add(new PieEntry(upiAmtf,"UPI"));
        }
        PieDataSet dataSet = new PieDataSet(pieEntires,"");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.orienta);
        dataSet.setValueTypeface(typeface);
        //dataSet.setDrawValues(false);
        dataSet.setValueTextSize(20);
        dataSet.setHighlightEnabled(true);
        dataSet.setValueFormatter(new IndianRuppeFormatter());
        PieData data = new PieData(dataSet);
        //Get the chart

        chart.setCenterText("Sales");
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
                        toDateTextView.setText(format.format(date));
                        new GetSales().execute("");
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
                        new GetSales().execute("");
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.salesrptFrmDate:
                if(CommonUtil.loggedinUserRoleID>1){
                    datePickerDialog.show();
                }
                break;
            case R.id.salesrptToDate:
                if(CommonUtil.loggedinUserRoleID>1){
                    todatePickerDialog.show();
                }
                break;
            case R.id.btnViewBills:
                CommonUtil.selectedCounter = GetSelectedCounter();
                CommonUtil.frmDate = frmDateTextView.getText().toString();
                CommonUtil.toDate = toDateTextView.getText().toString();
                Intent intent = new Intent(requireContext().getApplicationContext(), ViewBillsActivity.class);
                startActivity(intent);
                break;
        }
    }
    public class GetSales extends AsyncTask<String,String, ArrayList<Sale>>
    {
        String frmDate = frmDateTextView.getText().toString();
        String toDate = toDateTextView.getText().toString();
        String counterID = GetSelectedCounterID();
        Double revenue = 0d;
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(requireContext(),R.style.CustomProgressStyle);

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
        protected void onPostExecute(ArrayList<Sale> r) {

            if(!error.isEmpty()){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Error",error,false);
            }
            else {
                Double billamts = 0d;
                Double cashamts = 0d;
                Double cardamts = 0d;
                Double upiamts = 0d;
                if(r.size()>0){
                    btnViewBills.setVisibility(View.VISIBLE);
                    txtViewTotalBills.setVisibility(View.VISIBLE);
                    txtViewTotalBills.setText("Total Bills: "+String.valueOf(r.size()));
                    billamts = r.stream().mapToDouble(c->c.Bill_Amount).sum();
                    cashamts = r.stream().mapToDouble(c->c.Cash_Amount).sum();
                    cardamts = r.stream().mapToDouble(c->c.Card_Amount).sum();
                    upiamts = r.stream().mapToDouble(c->c.Upi_Amount).sum();
                }
                else {
                    btnViewBills.setVisibility(View.GONE);
                    txtViewTotalBills.setVisibility(View.GONE);
                }
                if(cashamts==0 && cardamts ==0 && upiamts==0){
                    chart.setVisibility(View.GONE);
                }
                else {
                    chart.setVisibility(View.VISIBLE);
                    setupPieChart(cashamts,cardamts,upiamts);
                }
                CommonUtil.totalRevenueAmt = billamts;
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                formatter.setMaximumFractionDigits(0);
                String symbol = formatter.getCurrency().getSymbol();
                txtViewTotalAmt.setText(formatter.format(billamts).replace(symbol,symbol+" "));
                txtViewCashAmt.setText(formatter.format(cashamts).replace(symbol,symbol+" "));
                txtViewCardAmt.setText(formatter.format(cardamts).replace(symbol,symbol+" "));
                txtViewUpiAmt.setText(formatter.format(upiamts).replace(symbol,symbol+" "));
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        @Override
        protected ArrayList<Sale> doInBackground(String... params) {
            ArrayList<Sale> sales = new ArrayList<>();
            try {
                ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                Connection con = connectionClass.CONN();
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    String query = String.format("SELECT Bill_No,cast(BILL_DATE as datetime) as Bill_Date, (Cash_Received+Card_Received+Coupon_Received) AS AMT,Cash_Received,Card_Received,Coupon_Received FROM SALE WHERE CAST(BILL_DATE AS DATE) BETWEEN '%s' AND '%s'",frmDate,toDate);
                    if(!counterID.equalsIgnoreCase("0")){
                        query = query+String.format(" AND COUNTER_ID='%s'",counterID);
                    }
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        Sale sale = new Sale();
                        sale.Bill_No = rs.getInt("Bill_No");
                        Timestamp t = rs.getTimestamp("Bill_Date");
                        sale.Bill_Date = new Date(t.getTime());
                        sale.Bill_Amount = rs.getDouble("AMT");
                        sale.Cash_Amount = rs.getDouble("Cash_Received");
                        sale.Card_Amount = rs.getDouble("Card_Received");
                        sale.Upi_Amount = rs.getDouble("Coupon_Received");
                        sales.add(sale);
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return sales;
        }
    }
}