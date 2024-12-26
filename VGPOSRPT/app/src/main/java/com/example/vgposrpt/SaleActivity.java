package com.example.vgposrpt;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SaleActivity extends AppCompatActivity implements View.OnClickListener {

    EditText prSearch;
    TextView totalAmt;
    LinearLayout prView;
    TextInputLayout searchBar;
    ArrayList<Product> productCart;
    ArrayList<Product> productShown;
    ImageButton btnScanQR;
    ImageButton btnViewCart;
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sale);
        try{
            prSearch = findViewById(R.id.prSearch);
            totalAmt = findViewById(R.id.billAmt);
            prView = findViewById(R.id.prView);
            searchBar = findViewById(R.id.search_bar);
            btnScanQR = findViewById(R.id.scanQR);
            btnViewCart = findViewById(R.id.btnViewCart);
            btnScanQR.setOnClickListener(this);
            btnViewCart.setOnClickListener(this);
            productCart = new ArrayList<>();
            productShown = new ArrayList<>();
            searchBar.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String serch = prSearch.getText().toString();
                    if(!serch.isEmpty()){
                        hideKeyboard(SaleActivity.this);
                        new SearchProducts().execute(serch);
                    }
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
        AlertDialog.Builder dialog =  new AlertDialog.Builder(SaleActivity.this);
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private int AddItemToCart(Product pr){
        int latestQty = 0;
        Product alreadyExits = null;
        for (Product p:productCart) {
            if(p.getProductID()==pr.getProductID()){
                alreadyExits = p;
                break;
            }
        }
        if(alreadyExits!=null){
            Integer qty = alreadyExits.getQty();
            qty+=1;
            pr.setQty(qty);
            productCart.remove(alreadyExits);
        }
        productCart.add(pr);
        Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        totalAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
        latestQty = pr.getQty();
        return  latestQty;
    }

    private int RemoveItemToCart(Product pr){
        int latestQty = 0;
        Product alreadyExits = null;
        for (Product p:productCart) {
            if(p.getProductID()==pr.getProductID()){
                alreadyExits = p;
                break;
            }
        }
        if(alreadyExits!=null){
            Integer qty = alreadyExits.getQty();
            if(qty==1){
                productCart.remove(alreadyExits);
                pr.setQty(0);
            }
            else {
                qty-=1;
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
        latestQty = pr.getQty();
        return latestQty;
    }


    private void LoadSearchView(Product pr){
        View view = getLayoutInflater().inflate(R.layout.items,null);
        TextView prName = view.findViewById(R.id.saleItemName);
        TextView prPrice = view.findViewById(R.id.saleItemRate);
        TextView prQty = view.findViewById(R.id.saleItemQty);
        prName.setText(pr.getProductName());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        prPrice.setText(formatter.format(pr.getPrice()).replace(symbol,symbol+" "));
        prQty.setText("0");
        ImageButton btnRemove = view.findViewById(R.id.btnRemoveItem);
        ImageButton btnAdd = view.findViewById(R.id.btnAddItem);
        btnAdd.setTag(pr.getProductID());
        btnRemove.setTag(pr.getProductID());
        btnRemove.setVisibility(View.INVISIBLE);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prid = (String) v.getTag();
                Product pn = null;
                for (Product p:productShown) {
                    if(p.getProductID()==prid){
                       pn = p;
                       break;
                    }
                }
                if(pn!=null){
                    if(pn.getQty()==null){
                        pn.setQty(1);
                    }
                    int qty = RemoveItemToCart(pn);
                    prQty.setText(String.valueOf(qty));
                    if(qty>0){
                        btnRemove.setVisibility(View.VISIBLE);
                    }
                    else {
                        btnRemove.setVisibility(View.INVISIBLE);
                    }
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
                        break;
                    }
                }
                if(pn!=null){
                    if(pn.getQty()==null){
                        pn.setQty(1);
                    }
                    int qty = AddItemToCart(pn);
                    prQty.setText(String.valueOf(qty));
                    if(qty>0){
                        btnRemove.setVisibility(View.VISIBLE);
                    }
                    else {
                        btnRemove.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        prView.addView(view);
    }
    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult Result = IntentIntegrator.parseActivityResult(requestCode , resultCode ,data);
        if(Result != null){
            if(Result.getContents() == null){
                Log.d("MainActivity" , "cancelled scan");
                Toast.makeText(SaleActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d("MainActivity" , "Scanned");
                prSearch.setText(Result.getContents());
                String serch = prSearch.getText().toString();
                if(!serch.isEmpty()){
                    new SearchProducts().execute(serch);
                }
            }
        }
        else {
            super.onActivityResult(requestCode , resultCode , data);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnViewCart:
                if(productCart.size()>0){
                    Intent cartPage = new Intent(this,CartViewActivity.class);
                    cartPage.putExtra("CART", (Serializable) productCart);
                    startActivity(cartPage);
                }
                else {
                    showCustomDialog("Warning","Please add products into cart.");
                }
                break;
            case R.id.scanQR:
                try{
                    if(!hasCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        IntentIntegrator intentIntegrator = new IntentIntegrator(SaleActivity.this);
                        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
                        intentIntegrator.setBeepEnabled(true);
                        intentIntegrator.setOrientationLocked(false);
                        intentIntegrator.setCameraId(0);
                        intentIntegrator.setPrompt("Scan Barcode/QR Code");
                        intentIntegrator.initiateScan();
                    }

                }
                catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage());
                }
                break;
        }
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