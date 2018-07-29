package com.example.vinoth.vgpos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Home extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TableLayout dataGrid;
    boolean isFrmDt;
    EditText frmDt;
    EditText toDt;
    Dialog progressBar;
    TextView totSale;
    ConnectionClass connectionClass;

    private static Home mInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dataGrid = (TableLayout)findViewById(R.id.datagrid);
        frmDt = (EditText)findViewById(R.id.frmDt);
        toDt = (EditText)findViewById(R.id.toDt);
        isFrmDt = false;
        progressBar = new Dialog(Home.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        connectionClass = new ConnectionClass();
        totSale = (TextView)findViewById(R.id.totSale);
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Date date = new Date();
        String dt = format.format(date);
        dt = dt.toUpperCase();
        frmDt.setText(dt);
        toDt.setText(dt);
        mInstance = this;
        new LoadProductsSale().execute("");
    }


    public static synchronized Home getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year,month,day);
        setDate(cal);
    }


    private void setDate(final Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String dt = format.format(calendar.getTime());
        if(isFrmDt){

            frmDt.setText(dt.toUpperCase());
            new LoadProductsSale().execute("");
        }
        else {
            toDt.setText(dt.toUpperCase());
            new LoadProductsSale().execute("");
        }

    }
    public void datePicker_TO(View view){
        isFrmDt = false;
        DatePickerFragment fragment = new DatePickerFragment();
        try{
            String ds = toDt.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date date = format.parse(ds);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
        }catch (Exception ex){

        }
        fragment.show(getSupportFragmentManager(),"Daet Picker");
    }
    public void datePikcer(View view){
        isFrmDt = true;
        DatePickerFragment fragment = new DatePickerFragment();
        try{
            String ds = frmDt.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date date = format.parse(ds);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
        }catch (Exception ex){

        }
        fragment.show(getSupportFragmentManager(),"Date Picker" );
    }

    /**
     * Create a DatePickerFragment class that extends DialogFragment.
     * Define the onCreateDialog() method to return an instance of DatePickerDialog
     */
    public static class DatePickerFragment extends DialogFragment {

        public Calendar calendar;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

             final Calendar c = calendar.getInstance();
             int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener)
                            getActivity(), year, month, day);
        }

    }
    public class LoadProductsSale extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;


        String fromDate = frmDt.getText().toString();
        String toDate = toDt.getText().toString();

        ArrayList<ProductsQty> productsQties = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            Toast.makeText(Home.this,r,Toast.LENGTH_SHORT).show();
            if(isSuccess){
                //LoadHome();
                if(productsQties.size() >0){
                  LoadDataGrid(productsQties);
                  totSale.setText(z);
                }
                else {
                    showCustomDialog("Warning","No Data Found....!");
                    dataGrid.removeAllViews();
                    totSale.setText("000");
                }
            }
            else {
                showCustomDialog("Error",r);
            }
            // if(isSuccess) {
            //   Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
            //}

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT P.PRODUCT_DESCRIPTION,A.QTY FROM (SELECT BP.PRODUCT_ID,SUM(QUANTITY) AS QTY FROM BILL_PRODUCTS BP WHERE BP.BILL_DATE >= '"+fromDate+"' AND BP.BILL_DATE <= '"+toDate+"' GROUP BY BP.PRODUCT_ID ) AS A,PRODUCTS P WHERE A.PRODUCT_ID=P.PRODUCT_ID ORDER BY A.QTY DESC";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        ProductsQty pd = new ProductsQty();
                        String prd = rs.getString("PRODUCT_DESCRIPTION");
                        int MAX_CHAR = 20;
                        int maxLength = (prd.length() < MAX_CHAR)?prd.length():MAX_CHAR;
                        prd = prd.substring(0, maxLength);
                        pd.setPRODUCT_NAME(prd);
                        pd.setQTY(rs.getDouble("QTY"));
                        productsQties.add(pd);

                    }
                   String query2 = "SELECT sum(Bill_Ammount) AS BD FROM SALE WHERE BILL_DATE>='"+fromDate+"' and BILL_DATE <= '"+toDate+"'";
                    Statement st = con.createStatement();
                    ResultSet re = st.executeQuery(query2);
                    if(re.next()){
                        double ds = re.getDouble("BD");
                        ds = Math.round(ds);
                        String vs = String.valueOf(ds);
                        z=vs;
                        //totSale.setText(vs);
                    }
                    isSuccess = true;
                }
            }
            catch (Exception e){
                z = e.getMessage();
            }
            return z;
        }
    }

    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        //dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int whichButton) {
        //   //pass
        //}
        //});
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public  void  LoadDataGrid(ArrayList<ProductsQty> productsQties) {
        dataGrid.removeAllViews();
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setPadding(10, 10, 10, 10);
        TextView textView = new TextView(this);
        textView.setText("PRODUCT NAME");
        textView.setTextColor(Color.BLUE);
        //textView.setTextSize(30);
        textView.setBackgroundResource(R.drawable.cellborder);
        textView.setBackgroundColor(Color.WHITE);
        row.addView(textView);
        TextView textView1 = new TextView(this);
        textView1.setText("                    SOLD QTY");
        //textView1.setTextSize(30);
        textView1.setPadding(10, 0, 10, 0);
        textView1.setTextColor(Color.BLUE);
        textView1.setBackgroundResource(R.drawable.cellborder);
        textView1.setBackgroundColor(Color.WHITE);
        row.addView(textView1);
        dataGrid.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        int i = 0;
        for (ProductsQty b : productsQties) {
            // Inflate your row "template" and fill out the fields.
            TableRow row1 = new TableRow(this);
            row1.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row1.setPadding(5, 5, 5, 5);
            TextView textView3 = new TextView(this);
            textView3.setText(b.getPRODUCT_NAME());
            textView3.setTextColor(Color.BLACK);
            textView3.setBackgroundResource(R.drawable.cellborder);
            textView3.setBackgroundColor(Color.WHITE);
            row1.addView(textView3);
            TextView textView4 = new TextView(this);
            textView4.setText(String.valueOf("                  " + b.getQTY()));
            textView4.setTextColor(Color.BLACK);
            textView4.setBackgroundResource(R.drawable.cellborder);
            textView4.setBackgroundColor(Color.WHITE);
            textView4.setPadding(10, 0, 10, 0);
            row1.addView(textView4);
            dataGrid.addView(row1, new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

        // ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.grid_view_item,despatches);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }
}



