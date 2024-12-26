package com.example.vgposrpt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class CartViewActivity extends AppCompatActivity {

    ImageButton btnSave;
    LinearLayout cartTable;
    TextView finalBillAmt;
    ArrayList<Product> productCart;
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
           productCart = (ArrayList<Product>) i.getSerializableExtra("CART");
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
}