package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CartViewActivity extends AppCompatActivity implements View.OnClickListener,GetPaymentModeDialog.PaymentDialogListener {

    ImageButton btnSave;
    LinearLayout cartTable;
    TextView finalBillAmt;
    ArrayList<Product> productCart;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String BRANCH = "BRANCH";
    Integer branchCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_view);
        try{
           btnSave = findViewById(R.id.btnSaveSale);
           cartTable = findViewById(R.id.cartTable);
           finalBillAmt = findViewById(R.id.FinalbillAmt);
           Intent i = getIntent();
           btnSave.setOnClickListener(this);
           productCart = (ArrayList<Product>) i.getSerializableExtra("CART");
           sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
           branchCode = sharedpreferences.getInt(BRANCH,1);
           LoadCheckoutItems();
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

    private void  LoadCheckoutItems(){
        cartTable.removeAllViews();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        for (Product p:productCart) {
            View view = getLayoutInflater().inflate(R.layout.cartview,null);
            TextView prName = view.findViewById(R.id.cartItemName);
            TextView prPrice = view.findViewById(R.id.cartItemRate);
            TextView prQty = view.findViewById(R.id.cartItemQty);
            TextView prAmt = view.findViewById(R.id.cartItemAmt);
            ImageButton btnDel = view.findViewById(R.id.btnDelItem);
            btnDel.setTag(p.getProductID());
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String prid = (String) v.getTag();
                    Product pr = null;
                    for (Product p:productCart)
                    {
                       if(p.getProductID()==prid){
                           pr = p;
                           break;
                       }
                    }
                    if(pr!=null){
                        productCart.remove(pr);
                        LoadCheckoutItems();
                    }
                }
            });
            prName.setText(p.getProductName());
            prPrice.setText(formatter.format(p.getPrice()).replace(symbol,symbol+" "));
            prQty.setText(String.valueOf(p.getQty()));
            prAmt.setText(formatter.format(p.getAmount()).replace(symbol,symbol+" "));
            cartTable.addView(view);
        }
        Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
        finalBillAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
        CommonUtil.cartItems = productCart;
    }
    @Override
    public void onBackPressed() {
        Intent salePage = new Intent(this,SaleActivity.class);
        startActivity(salePage);
        super.onBackPressed();
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(CartViewActivity.this);
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
    public void GoToSalePage(String title,String Message,boolean print,BillDetails billDetails) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(CartViewActivity.this);
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(print){
                    try{
                        PrinterUtil printerUtil = new PrinterUtil(CartViewActivity.this,CartViewActivity.this,false);
                        printerUtil.billDetail = billDetails;
                        printerUtil.Print();
                    }
                    catch (Exception ex){
                        showCustomDialog("Error",ex.getMessage());
                    }
                    finally {
                        Intent salePage = new Intent(CartViewActivity.this,SaleActivity.class);
                        salePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(salePage);
                    }
                }
                else {
                    Intent salePage = new Intent(CartViewActivity.this,SaleActivity.class);
                    salePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(salePage);
                }

            }
        });
        dialog.setCancelable(false);
        dialog.show();
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
        switch (v.getId()) {
            case  R.id.btnSaveSale:
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
        private final ProgressDialog dialog = new ProgressDialog(CartViewActivity.this,R.style.CustomProgressStyle);
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
                }
                else {
                    msg = "Failed to Save new Bill. Contact support Team.";
                }
                boolean print = !CommonUtil.PrintOption.equalsIgnoreCase("NONE");
                GoToSalePage("Status",msg,print,billDetails);
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