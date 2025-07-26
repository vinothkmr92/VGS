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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import com.google.android.material.textfield.TextInputLayout;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

public class SaleActivity extends AppCompatActivity implements View.OnClickListener {

    EditText prSearch;
    TextView totalAmt;
    LinearLayout prView;
    TextInputLayout searchBar;
    ArrayList<Product> productCart;
    ArrayList<Product> productShown;
    ImageButton btnViewCart;
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    GmsBarcodeScanner scanner;
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
            btnViewCart = findViewById(R.id.btnViewCart);
            btnViewCart.setOnClickListener(this);
            productCart = new ArrayList<>();
            productShown = new ArrayList<>();
            prSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId== EditorInfo.IME_ACTION_SEARCH){
                        String serch = prSearch.getText().toString();
                        if(!serch.isEmpty()) {
                            hideKeyboard(SaleActivity.this);
                            new SearchProducts().execute(serch);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Please enter valid product id",Toast.LENGTH_SHORT).show();
                        }

                    }
                    return false;
                }
            });
            searchBar.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String serch = prSearch.getText().toString();
                    if(serch.isEmpty()){
                        hideKeyboard(SaleActivity.this);
                        try{
                            if(!hasCameraPermission()){
                                requestCameraPermission();
                            }
                            else {
                                ScanQRCode();
                            }
                        }
                        catch (Exception ex){
                            showCustomDialog("Error",ex.getMessage());
                        }
                    }
                }
            });
            if(CommonUtil.cartItems.size()>0){
                productCart = CommonUtil.cartItems;
                for (Product p:productCart) {
                    productShown.add(p);
                    LoadSearchView(p);
                }
                Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                formatter.setMaximumFractionDigits(0);
                String symbol = formatter.getCurrency().getSymbol();
                totalAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
            }
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE,Barcode.FORMAT_CODE_128)
                    .build();
            scanner = GmsBarcodeScanning.getClient(SaleActivity.this,options);
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

    private void ScanQRCode(){
        if(scanner!=null){
           scanner.startScan().addOnSuccessListener(
                           barcode -> {
                               String rawValue = barcode.getRawValue();
                               prSearch.setText(rawValue);
                               String prid = prSearch.getText().toString();
                               if(!prid.isEmpty()){
                                   new SearchProducts().execute(prid,"1");
                               }
                           })
                   .addOnCanceledListener(
                           () -> {
                               Toast.makeText(SaleActivity.this,"Scanning was cancelled",Toast.LENGTH_SHORT).show();
                           })
                   .addOnFailureListener(
                           e -> {
                               Toast.makeText(SaleActivity.this,"Failed To Scan. Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                           });
        }
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
        Integer qty = pr.getQty()!=null ? pr.getQty():0;
        prQty.setText(String.valueOf(qty));
        ImageButton btnRemove = view.findViewById(R.id.btnRemoveItem);
        ImageButton btnAdd = view.findViewById(R.id.btnAddItem);
        btnAdd.setTag(pr.getProductID());
        btnRemove.setTag(pr.getProductID());
        if(qty>0){
            btnRemove.setVisibility(View.VISIBLE);
        }
        else {
            btnRemove.setVisibility(View.INVISIBLE);
        }
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
                    if(pn.getQty()==null || pn.getQty()==0){
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
    @Override
    public void onBackPressed(){
        Intent rptPage = new Intent(this,SalesReportActivity.class);
        rptPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(rptPage);
        super.onBackPressed();
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnViewCart:
                if(productCart.size()>0){
                    Intent cartPage = new Intent(this,CartViewActivity.class);
                    cartPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    cartPage.putExtra("CART", (Serializable) productCart);
                    startActivity(cartPage);
                }
                else {
                    showCustomDialog("Warning","Please add products into cart.");
                }
                break;
        }
    }

    public class SearchProducts extends AsyncTask<String,String, ArrayList<Product>>
    {
        int branchCode = 1;
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(SaleActivity.this,R.style.CustomProgressStyle);

        @Override
        protected void onPreExecute() {
            branchCode = CommonUtil.defBranch;
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.vgpos);
            dialog.setTitle("Searching");
            dialog.setMessage("Please Wait...");
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
                if(productCart.size()>0){
                    for(int k=0;k<productCart.size();k++){
                        productShown.add(k,productCart.get(k));
                    }
                }
                //Collections.sort(productShown, Collections.reverseOrder());
                if(productShown.size()>0){
                    try{
                        for (Product p:productShown) {
                            LoadSearchView(p);
                        }
                    }
                    catch (Exception ex){
                        if(dialog.isShowing()){
                            dialog.hide();
                        }
                        showCustomDialog("Error",ex.getMessage());
                    }
                    finally {
                        Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
                        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                        formatter.setMaximumFractionDigits(0);
                        String symbol = formatter.getCurrency().getSymbol();
                        totalAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
                    }

                }
                else {
                    if(dialog.isShowing()){
                        dialog.hide();
                    }
                    Toast.makeText(SaleActivity.this,"No Products Found.",Toast.LENGTH_SHORT).show();
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
            Integer qty = 0;
            if(params.length>1){
                qty = Integer.parseInt(params[1]);
            }
            try {
                ConnectionClass connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                Connection con = connectionClass.CONN();
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    String query = "SELECT P.PPWT,P.MRP,P.BRANCH_CODE,P.PRODUCT_ID,P.PRODUCT_DESCRIPTION,P.SELLING_PRICE,C.Category_Name,s.Supplier_Name,st.available_quantity as av FROM PRODUCTS p,\n" +
                            "(SELECT S.AVAILABLE_QUANTITY,A.* FROM Stocks S,(SELECT PRODUCT_ID,BRANCH_CODE,MAX(UPDATED_DATE) AS DT FROM \n" +
                            "STOCKS GROUP BY PRODUCT_ID,BRANCH_CODE) A WHERE S.PRODUCT_ID=A.PRODUCT_ID AND A.BRANCH_CODE=S.BRANCH_CODE AND \n" +
                            "S.UPDATED_DATE=A.DT) st,Category c,Suppliers s where c.Category_ID=p.CATEGORY_ID and \n" +
                            "s.Supplier_ID=p.SUPPLIER_ID and st.product_id=p.product_id and \n" +
                            "st.branch_code=p.branch_code AND P.BRANCH_CODE="+branchCode+" AND (UPPER(P.PRODUCT_ID) LIKE '%"+searchString+"%' OR UPPER(P.PRODUCT_DESCRIPTION) LIKE '%"+searchString+"%')";
                    //String query = "SELECT PPWT,MRP,BRANCH_CODE,PRODUCT_ID,PRODUCT_DESCRIPTION,SELLING_PRICE FROM PRODUCTS WHERE (UPPER(PRODUCT_ID) LIKE '%"+searchString+"%' OR UPPER(PRODUCT_DESCRIPTION) LIKE '%"+searchString+"%')";

                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        Product pr = new Product();
                        pr.setBranchCode(rs.getInt("BRANCH_CODE"));
                        pr.setProductID(rs.getString("PRODUCT_ID"));
                        pr.setProductName(rs.getString("PRODUCT_DESCRIPTION"));
                        pr.setPrice(rs.getDouble("SELLING_PRICE"));
                        pr.setMRP(rs.getDouble("MRP"));
                        pr.setPurchasedPrice(rs.getDouble("PPWT"));
                        pr.setCategory(rs.getString("Category_Name"));
                        pr.setSupplierName(rs.getString("Supplier_Name"));

                        if(pr.getCategory().isEmpty()){
                            pr.setCategory("GENERAL");
                        }
                        if(pr.getSupplierName().isEmpty()){
                            pr.setSupplierName("OWN");
                        }
                        pr.setQty(qty);
                        products.add(pr);
                        isSuccess = true;
                    }
                    rs.close();
                    for (Product p:products) {
                        query = "SELECT BATCH_NO FROM PRODUCT_BATCH_DETAILS WHERE BRANCH_CODE="+p.getBranchCode()+" AND PRODUCT_ID='"+p.getProductID()+"' AND AVAILABLE_QUANTITY>0";
                        ResultSet r = stmt.executeQuery(query);
                        if(r.next()){
                            p.setBatchNo(r.getString("BATCH_NO"));
                        }
                        r.close();
                        if(p.getBatchNo().isEmpty()){
                            p.setBatchNo(String.valueOf(p.getMRP()));
                        }
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