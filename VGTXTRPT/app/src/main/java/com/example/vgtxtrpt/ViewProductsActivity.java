package com.example.vgtxtrpt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewProductsActivity extends AppCompatActivity {

    LinearLayout productsRptContainer;
    ScrollView productrptScrollview;
    TextView totalAmt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_products);
        productsRptContainer = findViewById(R.id.viewProductsContainer);
        productrptScrollview = findViewById(R.id.productsScrollView);
        totalAmt = findViewById(R.id.prductsTotal);
        new GetSoldProducts().execute("");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void showCustomDialog(String title,String Message,Boolean close) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(ViewProductsActivity.this);
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(close){
                    finish();
                    System.exit(0);
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private void addCard(ProductsSummary sr){
        View view = getLayoutInflater().inflate(R.layout.cart_item,null);
        TextView qty = view.findViewById(R.id.itemcard_qty);
        TextView prname = view.findViewById(R.id.itemcard_name);
        TextView billAmt = view.findViewById(R.id.itemcard_amt);
        DecimalFormat formater = new DecimalFormat("#.###");
        String qtyStr = formater.format(sr.getSoldQty());
        qty.setText(qtyStr);
        prname.setText(sr.getProductName());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        String moneyString = formatter.format(sr.getSoldAmount()).replace(symbol,symbol+" ");
        billAmt.setText(moneyString);
        productsRptContainer.addView(view);
    }
    public void  LoadProductsViews(ArrayList<ProductsSummary> sales){
        try{
            productsRptContainer.removeAllViews();
            for(int i=0;i<sales.size();i++){
                ProductsSummary sr = sales.get(i);
                addCard(sr);
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage(),false);
        }
    }
    public class GetSoldProducts extends AsyncTask<String,String, ArrayList<ProductsSummary>>
    {
        String frmDate = CommonUtil.frmDate;
        String toDate = CommonUtil.toDate ;
        String counterid = CommonUtil.selectedCounter.CounterID;
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(ViewProductsActivity.this,R.style.CustomProgressStyle);

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
                Double total=0d;
                if(r.size()>0){
                   total =  r.stream().mapToDouble(c->c.getSoldAmount()).sum();
                }
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                formatter.setMaximumFractionDigits(0);
                String symbol = formatter.getCurrency().getSymbol();
                totalAmt.setText(formatter.format(total).replace(symbol,symbol+" "));
                LoadProductsViews(r);
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        @Override
        protected ArrayList<ProductsSummary> doInBackground(String... params) {
            ArrayList<ProductsSummary> products = new ArrayList<>();
            try {
                ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                Connection con = connectionClass.CONN();
                if (con == null) {
                    error = "Database Connection Failed.";
                } else {
                    String query = String.format("SELECT ROUND(SUM(BP.QUANTITY*(BP.PRICE-(BP.PRICE*(BP.DISCOUNT/100)))),0) AS PRICE,BP.PRODUCT_NAME,SUM(BP.QUANTITY) AS QA FROM BILL_PRODUCTS BP,SALE S WHERE S.BILL_NO=BP.Bill_No AND CAST(S.Bill_Date AS DATE)=CAST(BP.Bill_Date AS DATE) AND S.Counter_ID = BP.Counter_Id  AND  CAST(S.BILL_DATE AS DATE) BETWEEN '%s' AND '%s'",frmDate,toDate);
                    if(!counterid.equalsIgnoreCase("0")){
                        query = query+String.format(" AND BP.COUNTER_ID='%s' ",counterid);
                    }
                    query = query+"GROUP BY BP.PRODUCT_NAME ORDER BY PRICE DESC";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        ProductsSummary ps = new ProductsSummary();
                        ps.setSoldQty(rs.getDouble("QA"));
                        ps.setSoldAmount(rs.getDouble("PRICE"));
                        ps.setProductName(rs.getString("PRODUCT_NAME"));
                        products.add(ps);
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return products;
        }
    }
}