package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseHelper dbHelper;
    private Dialog progressBar;

    private EditText itemNo;
    private EditText itemName;
    private EditText Quantity;
    private EditText rate;
    private TextView ttQty;
    private TextView ttItem;
    private TextView ttAmt;

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
            rate= (EditText)findViewById(R.id.price);
            TextView ttQty = (TextView)findViewById(R.id.ttQty);
            TextView ttItem= (TextView)findViewById(R.id.ttItem);
            TextView ttAmt= (TextView)findViewById(R.id.ttAmt);

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
            btnSpace= (Button)findViewById(R.id.space);
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

        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
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
                      sr = sr.substring(0,sr.length()-1);
                      itemNo.setText(sr);
                  }
                  else if(Quantity.isFocused()){
                      String sr = Quantity.getText().toString();
                      sr = sr.substring(0,sr.length()-1);
                      Quantity.setText(sr);
                  }
                  break;
           case R.id.space:
               if(itemNo.isFocused()){
                   String sr = itemNo.getText().toString();
                   sr+=" ";
                   itemNo.setText(sr);
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
                     //
               ////
               break;
           case  R.id.cancel:
               ///
               break;
           case  R.id.print:
               ///
               break;
           case  R.id.enter:
               ///
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
}
