package com.example.vgposrpt;

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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewProductsActivity extends AppCompatActivity {

    ConnectionClass connectionClass;
    LinearLayout productsRptContainer;
    ScrollView productrptScrollview;
    TextView totalAmt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_products);
        connectionClass = new ConnectionClass();
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
        MaterialAlertDialogBuilder dialog =  new MaterialAlertDialogBuilder(ViewProductsActivity.this,
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
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
        qtyStr = "QTY: "+qtyStr;
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
        int branchCode = CommonUtil.selectedBarnch.getBranch_Code();
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(ViewProductsActivity.this);

        @Override
        protected void onPreExecute() {
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
                Connection con = connectionClass.CONN(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                if (con == null) {
                    error = "Error in connection with SQL server";
                } else {
                    String query = String.format("SELECT ROUND(SUM(BP.QUANTITY*BP.PRICE),0) AS PRICE,BP.PRODUCT_NAME,SUM(BP.QUANTITY) AS QA FROM BILL_PRODUCTS BP,SALE S WHERE S.BILL_NO=BP.Bill_No AND s.BRANCH_CODE=bp.BRANCH_CODE AND S.Bill_Date=BP.Bill_Date AND S.Counter_ID = BP.Counter_Id  AND  S.Bill_Date BETWEEN '%s' AND '%s'",frmDate,toDate);
                    if(branchCode>0){
                        query = query+String.format(" AND BP.BRANCH_CODE=%s ",branchCode);
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