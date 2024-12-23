package com.example.vinoth.vgspos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewItemActivity extends AppCompatActivity {

    private static ViewItemActivity instance;
    LinearLayout viewItems;

    public static ViewItemActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        viewItems = findViewById(R.id.viewItems);
        LoadData();
        instance = this;
    }

    @Override
    public void onBackPressed() {
        Intent page = new Intent(this,HomeActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }

    public void LoadData(){
        viewItems.removeAllViews();
        for(int i=0;i<Common.itemsCarts.size();i++){
            ItemsCart item = Common.itemsCarts.get(i);
            AddCarts(item);
        }
    }
    private void  AddCarts(ItemsCart cart){
        View view = getLayoutInflater().inflate(R.layout.viewitem,null);
        TextView itemName = view.findViewById(R.id.viewcart_name);
        TextView qty = view.findViewById(R.id.viewcart_qty);
        ImageButton btnClr = view.findViewById(R.id.viewcart_btnclr);
        itemName.setText(cart.getItem_Name());
        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        String str = nf.format(cart.getQty());
        qty.setText(str);
        String btnTag =  cart.getItem_No();
        btnClr.setTag(btnTag);
        btnClr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemNo = (String)v.getTag();
                Common.itemsCarts.removeIf(c->c.getItem_No()==itemNo);
                LoadData();
            }
        });
        viewItems.addView(view);
    }
    public  String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    public  String GetformattedItemName(String name){
        String res = padRight(name,25);
        return res.substring(0,25);
    }
}