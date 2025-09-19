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

public class CartViewActivity extends AppCompatActivity implements View.OnClickListener {

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


}