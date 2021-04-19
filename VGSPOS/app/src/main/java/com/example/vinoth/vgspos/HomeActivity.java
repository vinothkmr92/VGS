package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ngx.mp100sdk.Enums.Alignments;
import com.ngx.mp100sdk.Intefaces.INGXCallback;
import com.ngx.mp100sdk.NGXPrinter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseHelper dbHelper;
    private Dialog progressBar;

    private EditText itemNo;
    private EditText itemName;
    private EditText Quantity;
    private TextView tQty;
    private TextView tItem;

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
    private Button btnSpace;
    private Button btnMenu;
    private Button btnCancel;
    private Button btnPrint;
    private Button btnEnter;
    private Spinner wtSpinner;
    public static NGXPrinter ngxPrinter = NGXPrinter.getNgxPrinterInstance();
    private INGXCallback ingxCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressBar = new Dialog(HomeActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        try{
            dbHelper = new DatabaseHelper(this);
            itemNo = (EditText)findViewById(R.id.itemNo);
            itemName= (EditText)findViewById(R.id.itemName);
            Quantity= (EditText)findViewById(R.id.qty);
            tQty = (TextView)findViewById(R.id.ttQty);
            tItem= (TextView)findViewById(R.id.ttItem);
            wtSpinner = (Spinner)findViewById(R.id.waiterSpinner);
            btn1 = (Button)findViewById(R.id._1);
            btn2= (Button)findViewById(R.id._2);
            btn3= (Button)findViewById(R.id._3);
            btn4= (Button)findViewById(R.id._4);
            btn5= (Button)findViewById(R.id._5);
            btn6= (Button)findViewById(R.id._6);
            btn7= (Button)findViewById(R.id._7);
            btn8= (Button)findViewById(R.id._8);
            btn9= (Button)findViewById(R.id._9);
            btn0= (Button)findViewById(R.id._0);
            btnClear= (Button)findViewById(R.id._clr);
            btnDot= (Button)findViewById(R.id._dot);
            btnSpace= (Button)findViewById(R.id.viewitems);
            btnMenu= (Button)findViewById(R.id. menu);
            btnCancel= (Button)findViewById(R.id.cancel);
            btnPrint= (Button)findViewById(R.id.print);
            btnEnter= (Button)findViewById(R.id.enter);

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
            btnSpace.setOnClickListener(this);
            btnMenu.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnPrint.setOnClickListener(this);
            btnEnter.setOnClickListener(this);
            ArrayList<String> waiters = new ArrayList<String>();
            waiters.add("WAITER - 1");
            waiters.add("WAITER - 2");
            waiters.add("WAITER - 3");
            waiters.add("WAITER - 4");
            waiters.add("WAITER - 5");
            if(QuantityListener.itemsCarts!=null){
                tItem.setText(String.valueOf(QuantityListener.itemsCarts.size()));
                int ttq = 0;
                for(int i=0;i<QuantityListener.itemsCarts.size();i++){
                    ItemsCart ic = QuantityListener.itemsCarts.get(i);
                    ttq = ttq+ic.getQty();
                }
                tQty.setText(String.valueOf(ttq));
            }
            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,waiters);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            wtSpinner.setAdapter(adapter);
            wtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Your code here
                    itemNo.requestFocus();
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });
            this.ingxCallback = new INGXCallback() {
                public void onRunResult(boolean isSuccess) {
                    Log.i("NGX", "onRunResult:" + isSuccess);
                }

                public void onReturnString(String result) {
                    Log.i("NGX", "onReturnString:" + result);
                }

                public void onRaiseException(int code, String msg) {
                    Log.i("NGX", "onRaiseException:" + code + ":" + msg);
                }
            };
            ngxPrinter.initService(this, this.ingxCallback);
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.uploadExcel:
                Intent dcpage = new Intent(this,UploadActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            case R.id.homemenu:
                Intent page = new Intent(this,HomeActivity.class);
                page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(page);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id._clr:
                  if(itemNo.isFocused()){
                      String sr = itemNo.getText().toString();
                      if(sr.length()>0){
                          sr = sr.substring(0,sr.length()-1);
                          itemNo.setText(sr);
                      }
                  }
                  else if(Quantity.isFocused()){
                      String sr = Quantity.getText().toString();
                      if(sr.length()>0){
                          sr = sr.substring(0,sr.length()-1);
                          Quantity.setText(sr);
                      }
                  }
                  break;
           case R.id.viewitems:
                if(QuantityListener.itemsCarts.size()>0){
                    Intent intent = new Intent(this, ViewItemActivity.class);
                    startActivity(intent);
                }
                else {
                    showCustomDialog("Warning","Please add items into KOT");
                }
               break;
           case R.id._dot:
               if(itemNo.isFocused()){
                   String sr = itemNo.getText().toString();
                   sr +=".";
                   itemNo.setText(sr);
               }
               else if(Quantity.isFocused()){
                   String sr = Quantity.getText().toString();
                   sr +=".";
                   Quantity.setText(sr);
               }
               break;
           case R.id.menu:
               Intent intent = new Intent(this, ItemReport.class);
               startActivity(intent);
               break;
           case  R.id.cancel:
               QuantityListener.itemsCarts.clear();
               this.itemName.setText("");
               this.Quantity.setText("");
               QuantityListener.itemsCarts.clear();
               tItem.setText("0");
               tQty.setText("0");
               showCustomDialog("Info","Items Cleared.");
               itemName.requestFocus();
               break;
           case  R.id.print:
                PrintGatePass();
               break;
           case  R.id.enter:
               if(itemNo.isFocused()){
                   String itemnostr = itemNo.getText().toString();
                   Integer ino = Integer.parseInt(itemnostr);
                   String itemname = dbHelper.GetItemName(ino);
                   itemName.setText(itemname);
                   Quantity.requestFocus();
               }
               else if(Quantity.isFocused()){
                  if(itemNo.getText().toString().isEmpty() || itemName.getText().toString().isEmpty()){
                      showCustomDialog("Warning","Enter Valid Product");
                  }
                  else {
                      progressBar.show();
                      UpdateCarts();
                      itemNo.setText("");
                      itemName.setText("");
                      Quantity.setText("");
                      progressBar.hide();
                      itemNo.requestFocus();
                  }
               }
               break;
           default:
                 String addstr = getResources().getResourceEntryName(v.getId());
                 addstr =  addstr.replace("_","");
                   if(itemNo.isFocused()){
                       String sr = itemNo.getText().toString();
                       sr+=addstr;
                       itemNo.setText(sr);
                   }
                   else if(Quantity.isFocused()){
                       String sr = Quantity.getText().toString();
                       sr+=addstr;
                       Quantity.setText(sr);
                   }
                   break;

       }
    }
    public  void  UpdateCarts(){
        ArrayList<ItemsCart> itemsCarts = QuantityListener.itemsCarts;
        ItemsCart itc = new ItemsCart();
        itc.setItem_No(Integer.valueOf(itemNo.getText().toString()));
        itc.setItem_Name(itemName.getText().toString());
        itc.setQty(Integer.valueOf(Quantity.getText().toString()));
        if(itemsCarts == null){
            itemsCarts = new ArrayList<ItemsCart>();
        }
        if(itemsCarts.size()>0){
            Integer index = itemsCarts.indexOf(itc);
            if(index != -1){
                ItemsCart temp = itemsCarts.get(index);
                Integer newQty = itc.getQty()+temp.getQty();
                itemsCarts.remove(temp);
                itc.setQty(newQty);
            }
        }
        itemsCarts.add(itc);
        tItem.setText(String.valueOf(itemsCarts.size()));
        int ttq = 0;
        for(int i=0;i<itemsCarts.size();i++){
            ItemsCart ic = itemsCarts.get(i);
            ttq = ttq+ic.getQty();
        }
        tQty.setText(String.valueOf(ttq));
        QuantityListener.itemsCarts = itemsCarts;
    }
    public  String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    public  String GetformattedItemName(String name){
        String res = padRight(name,25);
        return res.substring(0,25);
    }
    private  void SaveDetails(){
        int newbillno = dbHelper.GetNextBillNo();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        String waiter  = wtSpinner.getSelectedItem().toString();
        for(int i=0;i<QuantityListener.itemsCarts.size();i++){
            Bills_Item bi = new Bills_Item();
            bi.setBill_No(newbillno);
            bi.setBill_DateStr(format.format(date));
            String name = QuantityListener.itemsCarts.get(i).getItem_Name();
            bi.setItem_Name(name);
            bi.setQty(QuantityListener.itemsCarts.get(i).getQty());
            bi.setWaiter(waiter);
            dbHelper.Insert_Bill_Items(bi);
        }
    }
    public  void PrintGatePass() {
        int sl = 0;
        progressBar.show();
        try {
            if(QuantityListener.itemsCarts == null || (QuantityListener.itemsCarts!=null && QuantityListener.itemsCarts.size()==0) ){
                showCustomDialog("Warning", "Please add Items");
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            SaveDetails();
            ngxPrinter.setDefault();
            //Bitmap hyndaiLogo = BitmapFactory.decodeResource(this.getResources(),R.drawable.hyundai_logo);
            //ngxPrinter.printImage(hyndaiLogo);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("SS HYDERABAD BIRIYANI", Alignments.CENTER,28);
            ngxPrinter.setStyleDoubleWidth();
            String waiter  = wtSpinner.getSelectedItem().toString();
            ngxPrinter.printText("KOT-> "+waiter, Alignments.CENTER, 30);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("ITEMS DETAILS", Alignments.CENTER, 28);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("DATE     : " + format.format(date), Alignments.LEFT, 24);
            ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
            ngxPrinter.printText("ITEM                     QTY", Alignments.LEFT, 24);
            ngxPrinter.printText("------------------------------", Alignments.LEFT, 24);
            for(int k=0;k<QuantityListener.itemsCarts.size();k++){
                String name = QuantityListener.itemsCarts.get(k).getItem_Name();
                String qty = String.valueOf(QuantityListener.itemsCarts.get(k).getQty());
                ngxPrinter.printText( GetformattedItemName(name)+": "+ qty, Alignments.LEFT, 24);
            }
            ngxPrinter.printText("                              ");
            ngxPrinter.printText("                              ");
            ngxPrinter.setDefault();
            Toast.makeText(this, "Print Queued Scuessfully.!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            showCustomDialog("Print Error",e.getMessage());
            //Toast.makeText(MainActivity.this, e.getMessage(), 1).show();
            e.printStackTrace();
        }
        finally {
            this.itemName.setText("");
            this.Quantity.setText("");
            QuantityListener.itemsCarts.clear();
            tItem.setText("0");
            tQty.setText("0");
            progressBar.hide();
            itemName.requestFocus();
        }
    }
}
