package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class QuickSaleFragment extends Fragment implements View.OnClickListener {

    Dialog itemSearchdialog;
    Dialog dialog;
    TextView searchCustomer;
    TextView totalAmt;
    EditText prIDEditText;
    EditText prNameEditText;
    EditText prPriceEditText;
    EditText prTrackingIDEditText;
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
            searchCustomer = view.findViewById(R.id.quickSaleCustomer);
            saleCartlayout = view.findViewById(R.id.quickSaleCartLayout);
            prIDEditText = view.findViewById(R.id.quickSaleprID);
            prNameEditText = view.findViewById(R.id.quickSalePrName);
            btnAdd = view.findViewById(R.id.quickSaleAddBtn);
            btnScanQR = view.findViewById(R.id.quickSaleScanbtn);
            btnCancel = view.findViewById(R.id.quickSaleCancelBtn);
            btnPrint = view.findViewById(R.id.quickSalePrintBtn);
            prPriceEditText = view.findViewById(R.id.quickSalePrice);
            totalAmt = view.findViewById(R.id.quickSaleBillAmt);
            prTrackingIDEditText = view.findViewById(R.id.quickSaleTrackingID);
            prQtyEditText = view.findViewById(R.id.quickSaleQty);
            productCart = new ArrayList<>();
            prIDEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search = prIDEditText.getText().toString();
                    if(search.isEmpty()){
                        OpenItemSearchDialog();
                        return  true;
                    }
                    else {
                        Product prcheck = fullProducts.stream().filter(c->c.getProductID().equals(search)).findFirst().orElse(null);
                        if(prcheck!=null){
                            prTrackingIDEditText.setText("");
                            prIDEditText.setText(prcheck.getProductID());
                            prNameEditText.setText(prcheck.getProductName());
                            prPriceEditText.setText(prcheck.getPrice().toString());
                            prQtyEditText.setText("1");
                            prQtyEditText.selectAll();
                            prQtyEditText.requestFocus();
                            return true;
                        }
                        else {
                            showCustomDialog("Warning","Invalid Product ID.");
                            prTrackingIDEditText.setText("");
                            prIDEditText.setText("");
                            prNameEditText.setText("");
                            prPriceEditText.setText("");
                            prQtyEditText.setText("");
                            return false;
                        }
                    }

                }
                return false;
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
            searchCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Initialize dialog
                    dialog=new Dialog(QuickSaleFragment.getInstance().getContext());

                    // set custom dialog
                    dialog.setContentView(R.layout.dialog_searchable_spinner);

                    // set custom height and width
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);

                    // set transparent background
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    // show dialog
                    dialog.show();

                    // Initialize and assign variable
                    EditText editText=dialog.findViewById(R.id.edit_text);
                    ListView listView=dialog.findViewById(R.id.list_view);

                    // Initialize array adapter
                    ArrayAdapter<Customer> adapter=new ArrayAdapter<>(QuickSaleFragment.getInstance().getContext(), android.R.layout.simple_list_item_1,CommonUtil.customers);

                    // set adapter
                    listView.setAdapter(adapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // when item selected from list
                            // set selected item on textView
                            searchCustomer.setText(adapter.getItem(position).toString());

                            // Dismiss dialog
                            dialog.dismiss();
                        }
                    });
                }
            });

        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        return  view;
    }
    private void OpenItemSearchDialog(){
        itemSearchdialog =new Dialog(QuickSaleFragment.getInstance().getContext());

        // set custom dialog
        itemSearchdialog.setContentView(R.layout.dialog_items_search);

        // set custom height and width
        itemSearchdialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);

        // set transparent background
        itemSearchdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // show dialog
        itemSearchdialog.show();

        // Initialize and assign variable
        EditText editText=itemSearchdialog.findViewById(R.id.edit_textItem);
        ListView listView=itemSearchdialog.findViewById(R.id.list_viewItem);

        // Initialize array adapter
        ArrayAdapter<Product> adapter=new ArrayAdapter<>(QuickSaleFragment.getInstance().getContext(),  R.layout.products_list_view, R.id.list_content,fullProducts);

        // set adapter
        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // when item selected from list
                // set selected item on textView
                Product prcheck = adapter.getItem(position);
                if(prcheck!=null){
                    prTrackingIDEditText.setText("");
                    prIDEditText.setText(prcheck.getProductID());
                    prNameEditText.setText(prcheck.getProductName());
                    prPriceEditText.setText(prcheck.getPrice().toString());
                    prQtyEditText.setText("1");
                    prQtyEditText.selectAll();
                    prQtyEditText.requestFocus();
                }
                itemSearchdialog.dismiss();
            }
        });
    }
    private void ScanQRCode(){
        if(scanner!=null){
            scanner.startScan().addOnSuccessListener(
                            barcode -> {
                                String rawValue = barcode.getRawValue();
                                String[] res = rawValue.split("~");
                                Product prcheck = null;
                                String trackingid = "";
                                if(res.length>1) {
                                    String prid = res[0];
                                    trackingid = rawValue;
                                    prcheck = fullProducts.stream().filter(c->c.getProductID().equals(prid)).findFirst().orElse(null);
                                }
                                else{
                                    prcheck = fullProducts.stream().filter(c->c.getProductID().equals(rawValue)).findFirst().orElse(null);
                                }

                                if(prcheck!=null){
                                    prQtyEditText.setEnabled(trackingid.isEmpty());
                                    //pr.setTrackingID(trackingid);
                                    if(trackingid.isEmpty()){
                                        prTrackingIDEditText.setText(trackingid);
                                        prIDEditText.setText(prcheck.getProductID());
                                        prNameEditText.setText(prcheck.getProductName());
                                        prPriceEditText.setText(prcheck.getPrice().toString());
                                        prQtyEditText.setText("1");
                                        prQtyEditText.selectAll();
                                        prQtyEditText.requestFocus();
                                    }
                                    else {
                                        String trid = trackingid;
                                        Product p = (productCart!=null && productCart.size()>0) ? productCart.stream().filter(c->c.getTrackingID().equals(trid)).findFirst().orElse(null):null;
                                        if(p!=null){
                                            showCustomDialog("Warning","Tracking ID already in Cart. Please Verify Tracking ID");
                                        }
                                        else{
                                            prcheck.setTrackingID(trackingid);
                                            new CheckTrackingID().execute(prcheck);
                                        }
                                    }
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
        btnAdd.setVisibility(pr.getTrackingID().isEmpty()?View.VISIBLE:View.GONE);
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
                productCart.add(new Product(pr));
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
        Product alreadyExits = productCart.stream().filter(c->c.getTrackingID().equals(pr.getTrackingID()) && c.getProductID().equals(pr.getProductID())).findFirst().orElse(null);
        if(alreadyExits!=null){
            Integer qty = alreadyExits.getQty();
            qty+=1;
            alreadyExits.setQty(qty);
        }
        else{
            productCart.add(new Product(pr));
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
        prNameEditText.setText("");
        prPriceEditText.setText("");
        prTrackingIDEditText.setText("");
        prIDEditText.setText("");
        prQtyEditText.setText("");
        totalAmt.setText("0");
        searchCustomer.setText("");
    }
    public void GetPaymentMode(){
        try {
            GetPaymentModeDialog addCustomer = new GetPaymentModeDialog();
            addCustomer.show(getChildFragmentManager(),"");
        }catch (Exception ex){
            showCustomDialog("Error",ex.getMessage().toString());
        }
    }
    private Integer GetMemberID(String memberName,String mobileNumber){
        Customer cn = CommonUtil.customers.stream().filter(c->c.MobileNumber.equals(mobileNumber) && c.MemberName.equals(memberName)).findFirst().orElse(null);
        Integer memid = cn!=null ? cn.MemberID:0;
        return memid;
    }
    public void getPaymentMode(String paymentMode) {
        try{
            BillDetails bd = new BillDetails();
            bd.branchCode = CommonUtil.defBranch;
            bd.billProducts = productCart;
            Double billAmt = productCart.stream().mapToDouble(c->c.getAmount()).sum();
            bd.BillAmount = (int) Math.round(billAmt);
            bd.billUser = CommonUtil.loggedinUser;
            String member = searchCustomer.getText().toString();
            if(!member.isEmpty()){
                String[] mc = member.split("-");
                bd.MemberID = GetMemberID(mc[0],mc[1]);
                bd.MemberName = mc[0];
            }
            else {
                bd.MemberName = "";
                bd.MemberID=0;
            }
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
                    pr.setTrackingID(prTrackingIDEditText.getText().toString());
                    AddItemToCart(pr);
                    prNameEditText.setText("");
                    prPriceEditText.setText("");
                    prIDEditText.setText("");
                    prTrackingIDEditText.setText("");
                    prQtyEditText.setText("");
                }
                else {
                    showCustomDialog("Warning","No valid product to add to Cart.");
                }
        }
    }
    public class CheckTrackingID extends  AsyncTask<Product,String,String>{
        Boolean isSuccess = false;
        String error = "";
        Product pr;
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);
        ConnectionClass connectionClass = null;
        Connection con = null;
        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.salesreport);
            dialog.setMessage("Checking Tracking ID..");
            dialog.show();
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String res) {
            if(isSuccess){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                if(res.equals("1")){
                    showCustomDialog("Status","Product Already Sold. Please validate the Tracking ID.");
                    prNameEditText.setText("");
                    prPriceEditText.setText("");
                    prIDEditText.setText("");
                    prQtyEditText.setText("");
                    prTrackingIDEditText.setText("");
                }
                else {
                    prTrackingIDEditText.setText(pr.getTrackingID());
                    prIDEditText.setText(pr.getProductID());
                    prNameEditText.setText(pr.getProductName());
                    prPriceEditText.setText(pr.getPrice().toString());
                    prQtyEditText.setText("1");
                    prPriceEditText.selectAll();
                    prPriceEditText.requestFocus();
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
        @Override
        protected String doInBackground(Product... strings) {
            String res="0";
            pr = strings[0];
            String trackingid = pr.getTrackingID();
            try {
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    String query = String.format("SELECT SOLD FROM Products_Tracking WHERE TRACKING_ID='%s' " +
                            "AND BRANCH_CODE=1",trackingid);
                    Statement stmt = con.createStatement();
                    ResultSet billNoRs = stmt.executeQuery(query);
                    if(billNoRs.next()){
                        res = billNoRs.getString("SOLD");
                    }
                    isSuccess = true;
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return res;
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
            s.close();
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
            s.close();
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
                    billNoRs.close();
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
                        String saleQuery = "INSERT INTO SALE VALUES ("+billNo+",GETDATE(),'CD1','"+billDetails.billUser+"',"+billDetails.BillAmount+","+billDetails.CashAmt+","+billDetails.CardAmt+","+billDetails.UpiAmt+",0,0,'',"+billDetails.MemberID+","+profit+",1,0,0,0,'Normal',0,0,0,'CLOSE','',0,"+billDetails.branchCode+")";
                        Integer rowAff = stmt.executeUpdate(saleQuery);
                        if(rowAff>0){
                            isSuccess = true;
                        }
                        for (Product p:billDetails.billProducts) {
                            try{
                                String trackingQuery = String.format("UPDATE Products_Tracking SET SOLD = 1 WHERE PRODUCT_ID='%s' AND BRANCH_CODE=%d AND TRACKING_ID='%s'",p.getProductID(),p.getBranchCode(),p.getTrackingID());
                                stmt.executeUpdate(trackingQuery);
                            }
                            catch(Exception ex)
                            {
                                Log.e("Error",ex.getMessage());
                            }
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