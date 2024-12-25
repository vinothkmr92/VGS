package com.example.vgposrpt;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SaleActivity extends AppCompatActivity {

    EditText prSearch;
    TextView totalAmt;
    LinearLayout prView;
    ArrayList<Product> productCart;
    ArrayList<Product> productShown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sale);
        try{
            prSearch = findViewById(R.id.prSearch);
            totalAmt = findViewById(R.id.billAmt);
            prView = findViewById(R.id.prView);
            productCart = new ArrayList<>();
            productShown = new ArrayList<>();
            prSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0)
                    {
                        new SearchProducts().execute(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
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


    public void showCustomDialog(String title,String Message) {
        MaterialAlertDialogBuilder dialog =  new MaterialAlertDialogBuilder(SaleActivity.this,
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void AddItemToCart(Product pr){
        Product alreadyExits = null;
        for (Product p:productCart) {
            if(p.getProductID()==pr.getProductID()){
                alreadyExits = p;
            }
        }
        if(alreadyExits!=null){
            Integer qty = alreadyExits.getQty();
            qty+=pr.getQty();
            pr.setQty(qty);
            productCart.remove(alreadyExits);
        }
        productCart.add(pr);
        Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        totalAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
    }

    private void RemoveItemToCart(Product pr){
        Product alreadyExits = null;
        for (Product p:productCart) {
            if(p.getProductID()==pr.getProductID()){
                alreadyExits = p;
            }
        }
        if(alreadyExits!=null){
            Integer qty = alreadyExits.getQty();
            if(qty==1){
                productCart.remove(alreadyExits);
            }
            else {
                qty-=pr.getQty();
                pr.setQty(qty);
                productCart.remove(alreadyExits);
                productCart.add(pr);
            }
        }
        Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        totalAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
    }


    private void LoadSearchView(Product pr){
        View view = getLayoutInflater().inflate(R.layout.items,null);
        TextView prName = view.findViewById(R.id.saleItemName);
        TextView prPrice = view.findViewById(R.id.saleItemRate);
        prName.setText(pr.getProductName());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        prPrice.setText(formatter.format(pr.getPrice()).replace(symbol,symbol+" "));
        ImageButton btnRemove = view.findViewById(R.id.btnRemoveItem);
        ImageButton btnAdd = view.findViewById(R.id.btnAddItem);
        btnAdd.setTag(pr.getProductID());
        btnRemove.setTag(pr.getProductID());
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prid = (String) v.getTag();
                Product pn = null;
                for (Product p:productShown) {
                    if(p.getProductID()==prid){
                       pn = p;
                    }
                }
                if(pn!=null){
                    pn.setQty(1);
                    RemoveItemToCart(pn);
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prid = (String) v.getTag();
                Product pn = null;
                for (Product p:productShown) {
                    if(p.getProductID()==prid){
                        pn = p;
                    }
                }
                if(pn!=null){
                    pn.setQty(1);
                    AddItemToCart(pn);
                }
            }
        });
        prView.addView(view);
    }

    public class SearchProducts extends AsyncTask<String,String, ArrayList<Product>>
    {
        int branchCode = 1;
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(SaleActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.salesreport);
            dialog.setMessage("Fetching Products...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Product> r) {
            if(!error.isEmpty()){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Status",error);
            }
            else {
                prView.removeAllViews();
                productShown = r;
                if(productShown.size()>0){
                    for (Product p:productShown) {
                        LoadSearchView(p);
                    }
                }
                else {
                    if(dialog.isShowing()){
                        dialog.hide();
                    }
                    Toast.makeText(SaleActivity.this,"No Products Found.",Toast.LENGTH_SHORT);
                }
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }

        @Override
        protected ArrayList<Product> doInBackground(String... params) {
            ArrayList<Product> products = new ArrayList<>();
            String searchString = params[0].toUpperCase();
            try {
                ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                Connection con = connectionClass.CONN();
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    String query = "SELECT BRANCH_CODE,PRODUCT_ID,PRODUCT_DESCRIPTION,SELLING_PRICE FROM PRODUCTS WHERE (UPPER(PRODUCT_ID) LIKE '%"+searchString+"%' OR UPPER(PRODUCT_DESCRIPTION) LIKE '%"+searchString+"%')";
                    if(branchCode>0){
                        query = query+String.format(" AND BRANCH_CODE=%s",branchCode);
                    }
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        Product pr = new Product();
                        pr.setBranchCode(rs.getInt("BRANCH_CODE"));
                        pr.setProductID(rs.getString("PRODUCT_ID"));
                        pr.setProductName(rs.getString("PRODUCT_DESCRIPTION"));
                        pr.setPrice(rs.getDouble("SELLING_PRICE"));
                        products.add(pr);
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