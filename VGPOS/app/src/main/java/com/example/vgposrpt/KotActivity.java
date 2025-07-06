package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

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

public class KotActivity extends AppCompatActivity implements View.OnClickListener {

    TextView kotNoTextView;
    TextInputLayout tableNo;
    EditText catEditText;
    EditText prNameEditText;
    LinearLayout kotCartlayout;
    AutoCompleteTextView tablesAutoComplete;
    ImageButton btnCatSearch;
    ImageButton btnPrSearch;
    Button btnAdd;
    private Button btnCloseTable;
    private Button btnCancel;
    private Button btnPrint;
    Dialog itemSearchdialog;
    ArrayList<KotDetails> kotDetails;
    ArrayList<KotDetails> kotDetailsOld;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kot);
        try{
            kotDetails = new ArrayList<>();
            kotCartlayout = findViewById(R.id.kotCartlayout);
            kotNoTextView = findViewById(R.id.kotNoTxt);
            kotNoTextView.setText(String.valueOf(CommonUtil.kotNo));
            tableNo = findViewById(R.id.kotTb);
            catEditText = findViewById(R.id.catEditText);
            prNameEditText = findViewById(R.id.prNameEditText);
            btnAdd = findViewById(R.id.btnPrAdd);
            btnCatSearch = findViewById(R.id.btnCatSearch);
            btnPrSearch = findViewById(R.id.btnPrSearch);
            tablesAutoComplete = findViewById(R.id.kotTables);
            List<String> tableNames = CommonUtil.tablesList.stream().map(c->c.TableName).collect(Collectors.toList());
            ArrayAdapter<String> tablesAdaptor = new ArrayAdapter<>(this,com.google.android.material.R.layout.support_simple_spinner_dropdown_item,tableNames);
            tablesAutoComplete.setAdapter(tablesAdaptor);
            tablesAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String tbName = tableNo.getEditText().getText().toString();
                    Tables tb = CommonUtil.tablesList.stream().filter(c->c.TableName.equals(tbName)).findFirst().orElse(null);
                    if(tb!=null){
                        new LoadKotDetails().execute(String.valueOf(tb.TableID));
                    }
                }
            });
            btnCloseTable = (Button) findViewById(R.id.closeTable);
            btnCancel = (Button) findViewById(R.id.cancel);
            btnPrint = (Button) findViewById(R.id.print);
            btnPrSearch.setOnClickListener(this);
            btnCatSearch.setOnClickListener(this);
            btnAdd.setOnClickListener(this);
            btnCloseTable.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnPrint.setOnClickListener(this);
            tableNo.requestFocus();
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
    private void OpenItemSearchDialog(boolean isCat){
        itemSearchdialog =new Dialog(KotActivity.this);

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
        TextView header = itemSearchdialog.findViewById(R.id.searchDialogHeader);

        String headertxt = isCat?"Search Category":"Search Products";
        header.setText(headertxt);
        // Initialize array adapter
        List<String> listItems = new ArrayList<>();
        if(isCat){
            listItems = CommonUtil.categories;
        }
        else {
            String cat = catEditText.getText().toString();
            listItems = CommonUtil.productsFull.stream().filter(c->c.getCategory().equals(cat)).map(c->c.getProductName()).collect(Collectors.toList());
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<>(KotActivity.this,  R.layout.products_list_view, R.id.list_content,listItems);

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
                String selectredName = adapter.getItem(position);

                // Dismiss dialog
                itemSearchdialog.dismiss();
                if(isCat){
                    catEditText.setText(selectredName);
                }
                else {
                    prNameEditText.setText(selectredName);
                }
            }
        });
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(KotActivity.this);
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
    public  void  UpdateCarts(){
        KotDetails kotd = new KotDetails();
        kotd.ProductName = prNameEditText.getText().toString();
        Product pr = CommonUtil.productsFull.stream().filter(c->c.getProductName().equals(kotd.ProductName)).findFirst().orElse(null);
        if(pr!=null){
            kotd.ProductId = pr.getProductID();
            kotd.Qty = 1;
            kotd.KotID = Integer.valueOf(kotNoTextView.getText().toString());
            kotd.KotDate = new Date();
            kotd.BranchCode = CommonUtil.defBranch;
            kotd.KotUser = CommonUtil.loggedinUser;
            String tableName = tableNo.getEditText().getText().toString();
            Tables tb = CommonUtil.tablesList.stream().filter(c->c.TableName.equals(tableName)).findFirst().orElse(null);
            kotd.TableID = tb!=null ? tb.TableID:0;
            KotDetails exits = kotDetails.stream().filter(c->c.ProductId.equals(kotd.ProductId)).findFirst().orElse(null);
            if(exits!=null){
                kotd.Qty +=exits.Qty;
                kotDetails.remove(exits);
            }
            kotDetails.add(kotd);
            LoadKotCartView();
        }
        else {
            Toast.makeText(this,"Not a valid Product.",Toast.LENGTH_LONG).show();
        }

    }
    private void LoadKotCartView(){
        kotCartlayout.removeAllViews();
        for (KotDetails pr:
             kotDetails) {
            View view = getLayoutInflater().inflate(R.layout.cart_kot,null);
            TextView prName = view.findViewById(R.id.kotCartItemName);
            TextView prQty = view.findViewById(R.id.kotCartQty);
            prName.setText(pr.ProductName);
            prQty.setText(String.valueOf(pr.Qty));
            ImageButton btnRemove = view.findViewById(R.id.btnRemoveKot);
            ImageButton btnAdd = view.findViewById(R.id.btnAddKot);
            btnAdd.setTag(pr.ProductId);
            btnRemove.setTag(pr.ProductId);
            if(pr.Qty>0){
                btnRemove.setVisibility(View.VISIBLE);
            }
            else {
                btnRemove.setVisibility(View.INVISIBLE);
            }
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String prid = (String) v.getTag();
                    KotDetails pn = null;
                    for (KotDetails p:kotDetails) {
                        if(p.ProductId==prid){
                            pn = p;
                            break;
                        }
                    }
                    if(pn!=null){
                        int qty = RemoveItemToKot(pn);
                        prQty.setText(String.valueOf(qty));
                        if(qty>0){
                            btnRemove.setVisibility(View.VISIBLE);
                        }
                        else {
                            btnRemove.setVisibility(View.INVISIBLE);
                            LoadKotCartView();
                        }
                    }
                }
            });
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String prid = (String) v.getTag();
                    KotDetails pn = null;
                    for (KotDetails p:kotDetails) {
                        if(p.ProductId==prid){
                            pn = p;
                            break;
                        }
                    }
                    if(pn!=null){
                        int qty = AddItemToKot(pn);
                        prQty.setText(String.valueOf(qty));
                        if(qty>0){
                            btnRemove.setVisibility(View.VISIBLE);
                        }
                        else {
                            btnRemove.setVisibility(View.INVISIBLE);
                            LoadKotCartView();
                        }
                    }
                }
            });
            kotCartlayout.addView(view);
        }
    }
    private int AddItemToKot(KotDetails pr){
        int latestQty = 0;
        KotDetails alreadyExits = null;
        for (KotDetails p:kotDetails) {
            if(p.ProductId==pr.ProductId){
                alreadyExits = p;
                break;
            }
        }
        if(alreadyExits!=null){
            Integer qty = alreadyExits.Qty;
            qty+=1;
            pr.Qty = qty;
            kotDetails.remove(alreadyExits);
        }
        kotDetails.add(pr);
        latestQty = pr.Qty;
        return  latestQty;
    }

    private int RemoveItemToKot(KotDetails pr){
        int latestQty = 0;
        KotDetails alreadyExits = null;
        for (KotDetails p:kotDetails) {
            if(p.ProductId==pr.ProductId){
                alreadyExits = p;
                break;
            }
        }
        if(alreadyExits!=null){
            Integer qty = alreadyExits.Qty;
            if(qty==1){
                kotDetails.remove(alreadyExits);
                pr.Qty=0;
            }
            else {
                qty-=1;
                pr.Qty=qty;
                kotDetails.remove(alreadyExits);
                kotDetails.add(pr);
            }
        }
        latestQty = pr.Qty;
        return latestQty;
    }
    public void Cancel() {
        kotDetails.clear();
        LoadKotCartView();
        this.catEditText.setText("");
        this.prNameEditText.setText("");
        tableNo.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeTable:
                try {
                    //closeBT();
                } catch (Exception e) {
                    Toast.makeText(this,"Error:"+e.getMessage(), Toast.LENGTH_SHORT);
                }
                break;
            case R.id.cancel:
                Cancel();
                break;
            case R.id.print:
                //
                if(kotDetails.size()>0){
                    new SaveKot().execute(kotDetails);
                }
                else {
                    showCustomDialog("Warning","No Products Added. Please Add Products.");
                }
                break;
            case  R.id.btnCatSearch:
                String tableName = tableNo.getEditText().getText().toString();
                if(tableName.isEmpty()){
                    showCustomDialog("Warning","Please Select Table First");
                    tableNo.requestFocus();
                }
                else {
                    if(itemSearchdialog !=null){
                        if(!itemSearchdialog.isShowing()){
                            OpenItemSearchDialog(true);
                        }
                    }
                    else {
                        OpenItemSearchDialog(true);
                    }
                }
                break;
            case  R.id.btnPrSearch:
                String tbName = tableNo.getEditText().getText().toString();
                if(tbName.isEmpty()){
                    showCustomDialog("Warning","Please Select Table First");
                    tableNo.requestFocus();
                    break;
                }
                String cat = catEditText.getText().toString();
                if(cat.isEmpty()){
                    showCustomDialog("Warning","Please Select Valid Category.");
                }
                else {
                    if(itemSearchdialog !=null){
                        if(!itemSearchdialog.isShowing()){
                            OpenItemSearchDialog(false);
                        }
                    }
                    else {
                        OpenItemSearchDialog(false);
                    }
                }
                break;
            case  R.id.btnPrAdd:
                UpdateCarts();
                prNameEditText.setText("");
                break;
        }
    }

    public void RefreshKotPage(String title,String Message,boolean print,ArrayList<KotDetails> kotDetails) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(KotActivity.this);
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(print){
                    try{
                        PrinterUtil printerUtil = new PrinterUtil(KotActivity.this);
                        printerUtil.kotDetails = kotDetails;
                        printerUtil.Print();
                    }
                    catch (Exception ex){
                        showCustomDialog("Error",ex.getMessage());
                    }
                }
                Cancel();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public class SaveKot extends AsyncTask<ArrayList<KotDetails>,String, Integer>
    {
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(KotActivity.this);
        ConnectionClass connectionClass = null;
        Connection con = null;
        ArrayList<KotDetails> kotlist;
        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Generating KOT..Please Wait..");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer kotNo) {
            if(isSuccess){
                String msg = "";
                if(kotNo>0){
                    msg ="Kot No - "+kotNo+" is saved Successfully.";
                }
                else {
                    msg = "Failed to Save KOT. Contact support Team.";
                }
                boolean print = !CommonUtil.PrintOption.equalsIgnoreCase("NONE");
                RefreshKotPage("Status",msg,print,kotlist);
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
        protected Integer doInBackground(ArrayList<KotDetails>... params) {
            Integer kotNo = 0;
            kotlist = params[0];
            try {
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    Date date = new Date();
                    String today = format.format(date);
                    String kotnoquery = "SELECT MAX(KOT_ID) AS KOTID FROM KOT WHERE CAST(KOT_DATE AS DATE)='"+today+"'";
                    Statement stmt = con.createStatement();
                    ResultSet kotnoRs = stmt.executeQuery(kotnoquery);
                    kotNo = 0;
                    if(kotnoRs.next()){
                        kotNo = kotnoRs.getInt("KOTID");
                    }
                    kotNo++;
                    boolean kotsaved = false;
                    for(KotDetails kot:kotlist){
                        try{
                            String query = "INSERT INTO KOT VALUES ("+kotNo+",GETDATE(),"+kot.TableID+","+kot.BranchCode+",'"+kot.ProductId+"',"+kot.Qty+",'"+kot.KotUser+"')";
                            Integer rowAff = stmt.executeUpdate(query);
                            kotsaved = rowAff>0;
                            if(!kotsaved){
                                break;
                            }
                        }
                        catch (Exception ex){
                            kotsaved = false;
                            break;
                        }
                    }
                    isSuccess = kotsaved;
                    if(!kotsaved) {
                        isSuccess = false;
                        String delBillQuery = "DELETE KOT WHERE KOT_ID="+kotNo+" AND CAST(KOT_DATE AS DATE)='"+today+"'";
                        stmt.executeUpdate(delBillQuery);
                        error="[Failed to Insert into Kot Table] Unable to Save Kot Details. Please Contact Support Team.";
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return kotNo;
        }
    }

    public class LoadKotDetails extends AsyncTask<String,String,ArrayList<KotDetails>>
    {
        String z = "";
        Boolean isSuccess = false;
        private final ProgressDialog dialog = new ProgressDialog(KotActivity.this);
        ConnectionClass connectionClass = null;
        Connection con = null;
        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Loading Kot Details...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<KotDetails> r) {
            if(dialog.isShowing()){
                dialog.hide();
            }
            if(isSuccess){
                kotDetails = r;
                if(r.size()>0){
                    KotDetails first = kotDetails.get(0);
                    kotNoTextView.setText(String.valueOf(first.KotID));
                }
                LoadKotCartView();
            }
            else {
                showCustomDialog("Error",z);
            }
        }

        @Override
        protected ArrayList<KotDetails> doInBackground(String... params) {
            ArrayList<KotDetails> kotlist = new ArrayList<>();
            try {
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String tbid = params[0];
                    String query = "SELECT * FROM KOT WHERE IS_OPEN=1 AND BRANCH_CODE="+CommonUtil.defBranch+" AND CAST(KOT_DATE AS DATE)=CAST(GETDATE() AS DATE) AND TABLE_ID="+tbid;
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next())
                    {
                        KotDetails kot = new KotDetails();
                        kot.KotID = rs.getInt("KOT_ID");
                        kot.KotDate = rs.getDate("KOT_DATE");
                        kot.TableID = rs.getInt("TABLE_ID");
                        kot.ProductId = rs.getString("PRODUCT_ID");
                        Product pr = CommonUtil.productsFull.stream().filter(c->c.getProductID().equals(kot.ProductId)).findFirst().orElse(null);
                        kot.ProductName = pr!=null ? pr.getProductName():"";
                        kot.Qty = rs.getInt("QUANTITY");
                        kot.KotUser = rs.getString("KOT_USER");
                        kotlist.add(kot);
                    }
                    rs.close();
                    isSuccess = true;
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions";
            }
            return kotlist;
        }
    }
}