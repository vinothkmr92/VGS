package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.NumberFormat;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class KotActivity extends AppCompatActivity implements View.OnClickListener {

    TextView kotNoTextView;
    TextInputLayout tableNo;
    EditText itemNoEditText;
    EditText itemNameEditText;
    EditText qtyEditText;
    LinearLayout kotCartlayout;
    AutoCompleteTextView tablesAutoComplete;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;
    private Button btn9;
    private Button btn0;
    private Button btnClear;
    private Button btnDot;
    private Button btnCloseTable;
    private Button btnCancel;
    private Button btnPrint;
    private Button btnEnter;
    Dialog itemSearchdialog;
    ArrayList<KotDetails> kotDetails;
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
            itemNoEditText = findViewById(R.id.itemNo);
            itemNameEditText = findViewById(R.id.itemName);
            qtyEditText = findViewById(R.id.qty);
            tablesAutoComplete = findViewById(R.id.kotTables);
            List<String> tableNames = CommonUtil.tablesList.stream().map(c->c.TableName).collect(Collectors.toList());
            ArrayAdapter<String> tablesAdaptor = new ArrayAdapter<>(this,com.google.android.material.R.layout.support_simple_spinner_dropdown_item,tableNames);
            tablesAutoComplete.setAdapter(tablesAdaptor);
            btn1 = (Button) findViewById(R.id._1);
            btn2 = (Button) findViewById(R.id._2);
            btn3 = (Button) findViewById(R.id._3);
            btn4 = (Button) findViewById(R.id._4);
            btn5 = (Button) findViewById(R.id._5);
            btn6 = (Button) findViewById(R.id._6);
            btn7 = (Button) findViewById(R.id._7);
            btn8 = (Button) findViewById(R.id._8);
            btn9 = (Button) findViewById(R.id._9);
            btn0 = (Button) findViewById(R.id._0);
            btnClear = (Button) findViewById(R.id._clr);
            btnDot = (Button) findViewById(R.id._dot);
            btnCloseTable = (Button) findViewById(R.id.closeTable);
            btnCancel = (Button) findViewById(R.id.cancel);
            btnPrint = (Button) findViewById(R.id.print);
            btnEnter = (Button) findViewById(R.id.enter);
            btn1.setOnClickListener(this);
            btn2.setOnClickListener(this);
            btn3.setOnClickListener(this);
            btn4.setOnClickListener(this);
            btn5.setOnClickListener(this);
            btn6.setOnClickListener(this);
            btn7.setOnClickListener(this);
            btn8.setOnClickListener(this);
            btn9.setOnClickListener(this);
            btn0.setOnClickListener(this);
            btnClear.setOnClickListener(this);
            btnDot.setOnClickListener(this);
            btnCloseTable.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnPrint.setOnClickListener(this);
            btnEnter.setOnClickListener(this);
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
    private void OpenItemSearchDialog(){
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

        // Initialize array adapter
        List<String> itemnames = CommonUtil.productsFull.stream().map(c->c.getProductName()).collect(Collectors.toList());
        ArrayAdapter<String> adapter=new ArrayAdapter<>(KotActivity.this,  R.layout.products_list_view, R.id.list_content,itemnames);

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
                Product pr = CommonUtil.productsFull.stream().filter(c->c.getProductName().equals(selectredName)).findFirst().orElse(null);
                if(pr!=null){
                    itemNoEditText.setText(pr.getProductID());
                    itemNameEditText.setText(pr.getProductName());
                    qtyEditText.setText("1");
                    qtyEditText.selectAll();
                    qtyEditText.requestFocus();
                }
                else{
                    itemNoEditText.requestFocus();
                    Toast.makeText(getApplicationContext(),"Invalid Product ID.",Toast.LENGTH_LONG);
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
        kotd.ProductId = itemNoEditText.getText().toString();
        kotd.ProductName = itemNameEditText.getText().toString();
        kotd.Qty = Integer.valueOf(qtyEditText.getText().toString());
        kotd.KotID = Integer.valueOf(kotNoTextView.getText().toString());
        kotd.KotDate = new Date();
        kotd.BranchCode = CommonUtil.defBranch;
        kotd.KotUser = CommonUtil.USERNAME;
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
    private void LoadKotCartView(){
        kotCartlayout.removeAllViews();
        for (KotDetails pr:
             kotDetails) {
            View view = getLayoutInflater().inflate(R.layout.cart_kot,null);
            TextView prName = view.findViewById(R.id.kotCartItemName);
            TextView prQty = view.findViewById(R.id.kotCartQty);
            prName.setText(pr.ProductName);
            prQty.setText(String.valueOf(pr.Qty));
            kotCartlayout.addView(view);
        }
    }
    public void Cancel() {
        //QuantityListener.itemsCarts.clear();
        //Common.itemsCarts.clear();
        kotDetails.clear();
        LoadKotCartView();
        this.itemNameEditText.setText("");
        this.itemNoEditText.setText("");
        this.qtyEditText.setText("");
        //showCustomDialog("Info","Items Cleared.");
        itemNoEditText.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id._clr:
                if (itemNoEditText.isFocused()) {
                    String sr = itemNoEditText.getText().toString();
                    if (sr.length() > 0) {
                        sr = sr.substring(0, sr.length() - 1);
                        itemNoEditText.setText(sr);
                    }
                } else if (qtyEditText.isFocused()) {
                    String sr = qtyEditText.getText().toString();
                    if (sr.length() > 0) {
                        sr = sr.substring(0, sr.length() - 1);
                        qtyEditText.setText(sr);
                    }
                }
                break;
            case R.id._dot:
                if (itemNoEditText.isFocused()) {
                    String sr = itemNoEditText.getText().toString();
                    sr += ".";
                    itemNoEditText.setText(sr);
                }
                break;
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
                break;
            case R.id.enter:
                LoadProductName();
                break;
            default:
                String addstr = getResources().getResourceEntryName(v.getId());
                addstr = addstr.replace("_", "");
                if (itemNoEditText.isFocused()) {
                    String sr = itemNoEditText.getText().toString();
                    sr += addstr;
                    itemNoEditText.setText(sr);
                } else if (qtyEditText.isFocused()) {
                    int startSelection = qtyEditText.getSelectionStart();
                    int endSelection = qtyEditText.getSelectionEnd();
                    String selectedText = qtyEditText.getText().toString().substring(startSelection, endSelection);
                    if (!selectedText.isEmpty()) {
                        qtyEditText.setText(addstr);
                    } else {
                        String sr = qtyEditText.getText().toString();
                        sr += addstr;
                        qtyEditText.setText(sr);
                    }
                }
                break;
        }
    }

    public void LoadProductName(){
        if(itemNoEditText.isFocused()){
            String itemnostr = itemNoEditText.getText().toString();
            if(!itemnostr.isEmpty()){
                Product pr = CommonUtil.productsFull.stream().filter(c->c.getProductID().equals(itemnostr)).findFirst().orElse(null);
                if(pr!=null){
                    itemNameEditText.setText(pr.getProductName());
                    qtyEditText.setText("1");
                    qtyEditText.selectAll();
                    qtyEditText.requestFocus();
                }
                else{
                    itemNoEditText.requestFocus();
                    Toast.makeText(getApplicationContext(),"Invalid Product ID.",Toast.LENGTH_LONG);
                }

            }
            else{
                if(itemSearchdialog !=null){
                    if(!itemSearchdialog.isShowing()){
                        OpenItemSearchDialog();
                    }
                }
                else {
                    OpenItemSearchDialog();
                }
            }

        }
        else if(qtyEditText.isFocused()){
            if(itemNoEditText.getText().toString().isEmpty()){
                showCustomDialog("Warning","Enter Valid Product");
            }
            else {
                UpdateCarts();
                itemNoEditText.setText("");
                itemNameEditText.setText("");
                qtyEditText.setText("");
                itemNoEditText.requestFocus();
            }
        }
    }
}