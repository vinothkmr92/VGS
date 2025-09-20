package com.example.vgposrpt;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class SaleActivity extends AppCompatActivity implements View.OnClickListener,GetPaymentModeDialog.PaymentDialogListener {

    TextView totalAmt;
    LinearLayout prView;
    LinearLayout categoryView;
    LinearLayout productsView;
    ArrayList<Product> productCart;
    ArrayList<Product> fullProducts;
    TextInputEditText searchprid;
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
            categoryView = findViewById(R.id.catView);
            productsView = findViewById(R.id.PdView);
            totalAmt = findViewById(R.id.billAmt);
            prView = findViewById(R.id.prView);
            btnViewCart = findViewById(R.id.btnViewCart);
            btnViewCart.setOnClickListener(this);
            productCart = new ArrayList<>();
            fullProducts =  CommonUtil.productsFull;
            fullProducts.forEach(i->i.setQty(1));
            if(CommonUtil.cartItems.size()>0){
                CommonUtil.cartItems.clear();
            }
            searchprid = findViewById(R.id.searchprid);
            searchprid.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Perform your search operation here
                    String searchText = v.getText().toString().toUpperCase();
                    Product searchlist = fullProducts.stream().filter(c->c.getProductID().toUpperCase().contains(searchText) || c.getProductName().toUpperCase().contains(searchText) ).findFirst().orElse(null);
                    if(searchlist!=null){
                        AddItemToCart(searchlist);
                        searchprid.setText("");
                        searchprid.requestFocus();
                    }
                    if(searchlist==null){
                        searchprid.selectAll();
                        searchprid.requestFocus();
                        Toast.makeText(SaleActivity.this,"No Products found for the Search.",Toast.LENGTH_LONG).show();
                    }
                    // Example: Log the search text
                    Log.d("Search", "Searching for: " + searchText);
                    // Return true to indicate that you have handled the action
                    return true;
                }
                // Return false to let the system handle other actions
                return false;
            });
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE,Barcode.FORMAT_CODE_128)
                    .build();
            scanner = GmsBarcodeScanning.getClient(SaleActivity.this,options);
            if(CommonUtil.categories.size()>0){
               LoadCategoryMenu();
               LoadProductsMenu("ALL");
            }
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
    private void LoadCategoryMenu(){
        BuildCatButtons("ALL");
        for (String ct:
             CommonUtil.categories) {
            BuildCatButtons(ct);
        }
    }
    private void LoadProductsMenu(String catName){
        productsView.removeAllViews();
        List<Product> products = fullProducts.stream().collect(Collectors.toList());
        if(!catName.equals("ALL")){
            products = products.stream().filter(c->c.getCategory().equals(catName)).collect(Collectors.toList());
        }
        int itemcount = products.size();
        while (itemcount>0){
            LinearLayout ln = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            ln.setLayoutParams(layoutParams);
            ln.setOrientation(LinearLayout.HORIZONTAL);
            ln.setPadding(10,10,10,10);
            List<Product> splic = products.stream().limit(6).collect(Collectors.toList());
            for (Product ic:splic) {
                Button btn = BuildProductsButtons(ic.getProductName(),ic.getProductID());
                ln.addView(btn);
                products.remove(ic);
            }
            itemcount = products.size();
            if(splic.size()>0){
                productsView.addView(ln);
            }
        }
       // productsView.setStretchAllColumns(true);
    }
    private Button BuildProductsButtons(String prName,String prID){
        // Create a new Button
        Button dynamicButton = new Button(this);
        dynamicButton.setText(prName); // Set button text
        dynamicButton.setTag(prID);
        dynamicButton.setTextColor(Color.WHITE);
        dynamicButton.setTypeface(null, Typeface.BOLD);
        // Generate a random color
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        dynamicButton.setBackgroundColor(color);

        // Optional: Set layout parameters for the button
        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120,120);
        layoutParams.setMargins(10, 0, 0, 0); // Add some margin if desired
        dynamicButton.setLayoutParams(layoutParams);

        // Add the button to the LinearLayout


        // Optional: Set an OnClickListener for the dynamic button
        dynamicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event here
                // For example, change its color again or show a Toast
                String prid = v.getTag().toString();
                Product pr  = fullProducts.stream().filter(c->c.getProductID().equals(prid)).findFirst().get();
                if(pr!=null){
                    AddItemToCart(pr);
                }
            }
        });
        return  dynamicButton;
    }
    private void BuildCatButtons(String cat) {
        // Create a new Button
        Button dynamicButton = new Button(this);
        dynamicButton.setText(cat); // Set button text
        dynamicButton.setTag(cat);
        dynamicButton.setTextColor(Color.WHITE);
        dynamicButton.setTypeface(null, Typeface.BOLD);
        // Generate a random color
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        dynamicButton.setBackgroundColor(color);

        // Optional: Set layout parameters for the button
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 10, 0, 0); // Add some margin if desired
        dynamicButton.setLayoutParams(layoutParams);

        // Add the button to the LinearLayout
        categoryView.addView(dynamicButton);

        // Optional: Set an OnClickListener for the dynamic button
        dynamicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event here
                // For example, change its color again or show a Toast
                String catName = v.getTag().toString();
                LoadProductsMenu(catName);
            }
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
        Product alreadyExits = productCart.stream().filter(c->c.getProductID().equals(pr.getProductID())).findFirst().orElse(null);
        if(alreadyExits!=null){
            Integer qty = alreadyExits.getQty();
            qty+=1;
            alreadyExits.setQty(qty);
        }
        else{
            productCart.add(pr);
        }
        Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        totalAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
        latestQty = pr.getQty();
        if(latestQty>0){
            prView.removeAllViews();
            for (Product p:
                    productCart) {
                LoadPRView(p);
            }
        }
        return  latestQty;
    }

    private int RemoveItemToCart(Product pr,View v){
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
        if(latestQty==0){
            prView.removeAllViews();
            for (Product p:
                 productCart) {
                LoadPRView(p);
            }
        }
        return latestQty;
    }

    private void LoadPRView(Product pr){
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
                Product pn = fullProducts.stream().filter(c->c.getProductID().equals(prid)).findFirst().get();
                if(pn!=null){
                    if(pn.getQty()==null){
                        pn.setQty(1);
                    }
                    int qty = RemoveItemToCart(pn,v);
                    prQty.setText(String.valueOf(qty));
                    btnRemove.setVisibility(qty>0 ? View.VISIBLE:View.INVISIBLE);
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prid = (String) v.getTag();
                Product pn = fullProducts.stream().filter(c->c.getProductID().equals(prid)).findFirst().get();
                if(pn!=null){
                    if(pn.getQty()==null || pn.getQty()==0){
                        pn.setQty(1);
                    }
                    int qty = AddItemToCart(pn);
                    prQty.setText(String.valueOf(qty));
                    btnRemove.setVisibility(qty>0 ? View.VISIBLE:View.INVISIBLE);
                }
            }
        });
        prView.addView(view);
    }
    @Override
    public void onBackPressed(){
        if(CommonUtil.cartItems!=null){
            CommonUtil.cartItems.clear();
        }
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

    public void GoToSalePage() {
        Intent salePage = new Intent(SaleActivity.this,SaleActivity.class);
        salePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(salePage);
    }
    public void GetPaymentMode(){
        try {
            GetPaymentModeDialog addCustomer = new GetPaymentModeDialog();
            addCustomer.show(getSupportFragmentManager(),"");
        }catch (Exception ex){
            showCustomDialog("Error",ex.getMessage().toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnViewCart:
                if(productCart.size()>0) {
                    GetPaymentMode();
                }
                else {
                    showCustomDialog("Warning","No products in Cart to Sale");
                }
                break;
        }
    }

    @Override
    public void getPaymentMode(String paymentMode) {
        try{
            BillDetails bd = new BillDetails();
            bd.branchCode = CommonUtil.defBranch;
            bd.billProducts = productCart;
            Double billAmt = productCart.stream().mapToDouble(c->c.getAmount()).sum();
            bd.BillAmount = (int) Math.round(billAmt);
            bd.billUser = CommonUtil.loggedinUser;
            switch (paymentMode){
                case  "CASH":
                    bd.CashAmt=bd.BillAmount;
                    break;
                case "CARD":
                    bd.CardAmt = bd.BillAmount;
                    break;
                case "UPI":
                    bd.UpiAmt = bd.BillAmount;
            }
            new SaveNewBill().execute(bd);
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }

    public class SaveNewBill extends AsyncTask<BillDetails,String, Integer>
    {
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(SaleActivity.this,R.style.CustomProgressStyle);
        ConnectionClass connectionClass = null;
        Connection con = null;
        BillDetails billDetails;
        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.salesreport);
            dialog.setMessage("New Sale Entry..");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer billNo) {
            if(isSuccess){
                String msg = "";
                if(billNo>0){
                    msg ="Bill No - "+billNo+" is saved Successfully.";
                    CommonUtil.cartItems = new ArrayList<>();
                    boolean print = !CommonUtil.PrintOption.equalsIgnoreCase("NONE");
                    if(print){
                        try{
                            PrinterUtil printerUtil = new PrinterUtil(SaleActivity.this,SaleActivity.this,false);
                            printerUtil.billDetail = billDetails;
                            printerUtil.Print();
                        }
                        catch (Exception ex){
                            showCustomDialog("Error",ex.getMessage());
                        }
                        finally {
                            finish();
                            Intent salePage = new Intent(SaleActivity.this,SaleActivity.class);
                            salePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(salePage);
                        }
                    }
                    else{
                        showCustomDialog("Status",msg);
                    }
                }
                else {
                    msg = "Failed to Save new Bill. Contact support Team.";
                    showCustomDialog("Status",msg);
                }

            }
            else {
                if(dialog.isShowing()){
                    dialog.hide();
                }
                if(!error.isEmpty()){
                    showCustomDialog("Status",error);
                }
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }
        private  Double GetAvailableQty(String productID,Integer branchCode) throws SQLException {
            Double qty = 0d;
            String query = "SELECT S.AVAILABLE_QUANTITY FROM Stocks S,(SELECT PRODUCT_ID,BRANCH_CODE,MAX(UPDATED_DATE) AS DT FROM STOCKS GROUP BY PRODUCT_ID,BRANCH_CODE) A WHERE S.PRODUCT_ID=A.PRODUCT_ID AND A.BRANCH_CODE=S.BRANCH_CODE AND S.UPDATED_DATE=A.DT and A.Product_ID='"+productID+"' AND A.BRANCH_CODE="+branchCode;
            Statement stmt = con.createStatement();
            ResultSet s = stmt.executeQuery(query);
            if(s.next()){
                qty = s.getDouble("AVAILABLE_QUANTITY");
            }
            return qty;
        }
        private boolean UpdateStocks(Product pr) throws SQLException {
            boolean isDone = false;
            Double avQty = GetAvailableQty(pr.getProductID(),pr.getBranchCode());
            Double avNow = avQty-pr.getQty();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date date = new Date();
            String upddt = format.format(date);
            String query = "INSERT INTO STOCKS VALUES ('"+pr.getProductID()+"',"+avNow+","+pr.getQty()+",0,'"+upddt+"',"+pr.getBranchCode()+")";
            Statement stmt = con.createStatement();
            Integer rowAff = stmt.executeUpdate(query);
            isDone = rowAff>0;
            if(!pr.getBatchNo().isEmpty()){
                UpdateBatchDetails(pr);
            }
            return  isDone;
        }
        private Double GetBatchAvailableQty(String productID,String BatchNo,Integer branchCode) throws SQLException {
            Double qty = 0d;
            String query = "SELECT Available_Quantity FROM Product_Batch_Details WHERE Product_ID = '"+productID+"' AND BRANCH_CODE ="+branchCode+" AND Batch_No='"+BatchNo+"'";
            Statement stmt = con.createStatement();
            ResultSet s = stmt.executeQuery(query);
            if(s.next()){
                qty = s.getDouble("Available_Quantity");
            }
            return qty;
        }
        private void UpdateBatchDetails(Product pr) throws SQLException {
            Double avQty = GetBatchAvailableQty(pr.getProductID(),pr.getBatchNo(),pr.getBranchCode());
            Double avNow = avQty-pr.getQty();
            String query = "UPDATE PRODUCT_BATCH_DETAILS SET AVAILABLE_QUANTITY="+avNow+" WHERE BATCH_NO='"+pr.getBatchNo()+"' AND PRODUCT_ID='"+pr.getProductID()+"' AND BRANCH_CODE="+pr.getBranchCode();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        }

        @Override
        protected Integer doInBackground(BillDetails... params) {
            Integer billNo = 0;
            billDetails = params[0];
            try {
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    String getBillNoQuery = "SELECT MAX(BILL_NO) AS BILL_NO FROM SALE WHERE BRANCH_CODE="+CommonUtil.defBranch+" AND COUNTER_ID='CD1' AND BILL_TYPE='Normal'";
                    Statement stmt = con.createStatement();
                    ResultSet billNoRs = stmt.executeQuery(getBillNoQuery);
                    billNo = 0;
                    if(billNoRs.next()){
                        billNo = billNoRs.getInt("BILL_NO");
                    }
                    billNo++;
                    billDetails.BillNo = billNo;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = new Date();
                    String expdt = format.format(date);
                    int serialNo = 1;
                    boolean billProductsSaved = false;
                    for (Product p:billDetails.billProducts) {
                        try{
                            String billprodQuery = "INSERT INTO BILL_PRODUCTS VALUES ("+billNo+",'"+p.getProductID()+"',GETDATE(),'CD1',"+p.getQty()+",'"+p.getBatchNo()+"',"+p.getPrice()+","+p.getMRP()+",'"+p.getProductName()+"','"+p.getSupplierName()+"','"+expdt+"',0,"+p.getBranchCode()+","+serialNo+",'"+p.getCategory()+"','Normal',0,0)";
                            Integer rowAff = stmt.executeUpdate(billprodQuery);
                            billProductsSaved = rowAff>0;
                            if(billProductsSaved){
                                UpdateStocks(p);
                            }
                            else {
                                break;
                            }
                            serialNo++;
                        }
                        catch(Exception ex){
                            billProductsSaved = false;
                            break;
                        }
                    }
                    if(billProductsSaved){
                        Double profit = billDetails.billProducts.stream().mapToDouble(c->(c.getPrice()-c.getPurchasedPrice())*c.getQty()).sum();
                        String saleQuery = "INSERT INTO SALE VALUES ("+billNo+",GETDATE(),'CD1','"+billDetails.billUser+"',"+billDetails.BillAmount+","+billDetails.CashAmt+","+billDetails.CardAmt+","+billDetails.UpiAmt+",0,0,'',0,"+profit+",1,0,0,0,'Normal',0,0,0,'CLOSE','',0,"+billDetails.branchCode+")";
                        Integer rowAff = stmt.executeUpdate(saleQuery);
                        if(rowAff>0){
                            isSuccess = true;
                        }
                    }
                    else {
                        isSuccess = false;
                        String delBillQuery = "DELETE BILL_PRODUCTS WHERE BILL_NO="+billNo+" AND COUNTER_ID='CD1' AND BRANCH_CODE="+billDetails.branchCode;
                        stmt.executeUpdate(delBillQuery);
                        error="[Failed to Update Bill Products] Unable to Save Bill Details. Please Contact Support Team.";
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return billNo;
        }
    }
}