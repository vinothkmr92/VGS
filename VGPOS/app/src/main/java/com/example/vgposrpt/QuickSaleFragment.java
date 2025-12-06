package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class QuickSaleFragment extends Fragment implements View.OnClickListener {

    TextView totalAmt;
    EditText prIDEditText;
    EditText prNameEditText;
    EditText prPriceEditText;
    EditText prQtyEditText;
    LinearLayout saleCartlayout;
    Button btnScanQR;
    Button btnAdd;
    private Button btnCancel;
    private Button btnPrint;
    public static QuickSaleFragment quickSaleInstance;
    ArrayList<Product> productCart;
    ArrayList<Product> fullProducts;
    GmsBarcodeScanner scanner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context cn = getContext();
        View view = inflater.inflate(R.layout.fragment_quick_sale, container, false);
        try{
            saleCartlayout = view.findViewById(R.id.quickSaleCartLayout);
            prIDEditText = view.findViewById(R.id.quickSaleprID);
            prNameEditText = view.findViewById(R.id.quickSalePrName);
            btnAdd = view.findViewById(R.id.quickSaleAddBtn);
            btnScanQR = view.findViewById(R.id.quickSaleScanbtn);
            btnCancel = view.findViewById(R.id.quickSaleCancelBtn);
            btnPrint = view.findViewById(R.id.quickSalePrintBtn);
            prPriceEditText = view.findViewById(R.id.quickSalePrice);
            prQtyEditText = view.findViewById(R.id.quickSaleQty);
            totalAmt = view.findViewById(R.id.quickSaleBillAmt);
            productCart = new ArrayList<>();
            prQtyEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String txt = prQtyEditText.getText().toString();
                    if(hasFocus && !txt.isEmpty()){
                            ((EditText)v).selectAll();
                    }
                }
            });
            prPriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String txt = prPriceEditText.getText().toString();
                    if(hasFocus && !txt.isEmpty()){
                        ((EditText)v).selectAll();
                    }
                }
            });
            fullProducts =  CommonUtil.productsFull;
            fullProducts.forEach(i->i.setQty(1));
            if(CommonUtil.cartItems.size()>0){
                CommonUtil.cartItems.clear();
            }
            btnAdd.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnPrint.setOnClickListener(this);
            btnScanQR.setOnClickListener(this);
            quickSaleInstance = this;
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE,Barcode.FORMAT_CODE_128)
                    .build();
            scanner = GmsBarcodeScanning.getClient(cn,options);

        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        return  view;
    }
    private void ScanQRCode(){
        if(scanner!=null){
            scanner.startScan().addOnSuccessListener(
                            barcode -> {
                                String rawValue = barcode.getRawValue();
                                Product pr = fullProducts.stream().filter(c->c.getProductID().equals(rawValue)).findFirst().orElse(null);
                                if(pr!=null){
                                    prIDEditText.setText(pr.getProductID());
                                    prNameEditText.setText(pr.getProductName());
                                    prPriceEditText.setText(pr.getPrice().toString());
                                    prQtyEditText.setText("1");
                                    prQtyEditText.requestFocus();
                                }
                                else {
                                    showCustomDialog("Warning","Invalid Product ID. Please Scan Valid Product");
                                }
                            })
                    .addOnCanceledListener(
                            () -> {
                                Toast.makeText(getContext(),"Scanning was cancelled",Toast.LENGTH_SHORT).show();
                            })
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(getContext(),"Failed To Scan. Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            });
        }
    }
    public static QuickSaleFragment getInstance(){
        return quickSaleInstance;
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(getContext());
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
        saleCartlayout.addView(view);
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
            saleCartlayout.removeAllViews();
            for (Product p:
                    productCart) {
                LoadPRView(p);
            }
        }
        return latestQty;
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
            saleCartlayout.removeAllViews();
            for (Product p:
                    productCart) {
                LoadPRView(p);
            }
        }
        return  latestQty;
    }
    public void Cancel() {
        productCart.clear();
        saleCartlayout.removeAllViews();
        prQtyEditText.setText("");
        prNameEditText.setText("");
        prPriceEditText.setText("");
        prIDEditText.setText("");
        totalAmt.setText("0");
    }
    public void GetPaymentMode(){
        try {
            GetPaymentModeDialog addCustomer = new GetPaymentModeDialog();
            addCustomer.show(getChildFragmentManager(),"");
        }catch (Exception ex){
            showCustomDialog("Error",ex.getMessage().toString());
        }
    }
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quickSaleScanbtn:
                ScanQRCode();
                break;
            case  R.id.quickSaleCancelBtn:
                Cancel();
                break;
            case R.id.quickSalePrintBtn:
                if(productCart.size()>0) {
                    GetPaymentMode();
                }
                else {
                    showCustomDialog("Warning","No products in Cart to Sale");
                }
                break;
            case R.id.quickSaleAddBtn:
                String prid = prIDEditText.getText().toString();
                Product pr = fullProducts.stream().filter(c->c.getProductID().equals(prid)).findFirst().orElse(null);
                if(pr!=null){
                    String price = prPriceEditText.getText().toString();
                    pr.setPrice(Double.valueOf(price));
                    String qty = prQtyEditText.getText().toString();
                    pr.setQty(Integer.valueOf(qty));
                    AddItemToCart(pr);
                    prQtyEditText.setText("");
                    prNameEditText.setText("");
                    prPriceEditText.setText("");
                    prIDEditText.setText("");
                }
                else {
                    showCustomDialog("Warning","No valid product to add to Cart.");
                }
        }
    }
    public class SaveNewBill extends AsyncTask<BillDetails,String, Integer>
    {
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);
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
                            PrinterUtil printerUtil = new PrinterUtil(getContext(),getInstance(),billDetails);
                            printerUtil.Print();
                        }
                        catch (Exception ex){
                            showCustomDialog("Error",ex.getMessage());
                        }
                    }
                    else{
                        showCustomDialog("Status",msg);
                        Cancel();
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