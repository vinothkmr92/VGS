package com.example.vinoth.vgsposbt;

import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomePage extends AppCompatActivity {

    TableLayout tblLayout;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        tblLayout = findViewById(R.id.tblLayout);
        dbHelper = new DatabaseHelper(this);
        ArrayList<ItemsCart> items = dbHelper.GetItemsAsItemCart();
        tblLayout.removeAllViews();
        tblLayout.setStretchAllColumns(true);
        tblLayout.bringToFront();
        int itemcount = items.size();
        while (itemcount>0){
            TableRow tr =  new TableRow(this);
            List<ItemsCart> splic = items.stream().limit(2).collect(Collectors.toList());
            for (ItemsCart ic:splic) {
                View view = GetItemView(ic);
                tr.addView(view);
                items.remove(ic);
            }
            itemcount = items.size();
            tblLayout.addView(tr);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private View GetItemView(ItemsCart item){
        View view = getLayoutInflater().inflate(R.layout.items,null);
        TextView prName = view.findViewById(R.id.saleItemName);
        TextView prPrice = view.findViewById(R.id.saleItemRate);
        TextView prQty = view.findViewById(R.id.saleItemQty);
        prName.setText(item.getItem_Name());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        prPrice.setText(formatter.format(item.getPrice()).replace(symbol,symbol+" "));

        prQty.setText(String.valueOf(item.getQty()));
        ImageButton btnRemove = view.findViewById(R.id.btnRemoveItem);
        ImageButton btnAdd = view.findViewById(R.id.btnAddItem);
        btnAdd.setTag(item.getItem_No());
        btnRemove.setTag(item.getItem_No());
        if(item.getQty()>0){
            btnRemove.setVisibility(View.VISIBLE);
        }
        else {
            btnRemove.setVisibility(View.INVISIBLE);
        }
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prid = (String) v.getTag();
                /*Product pn = null;
                for (Product p:productShown) {
                    if(p.getProductID()==prid){
                        pn = p;
                        break;
                    }
                }
                if(pn!=null){
                    if(pn.getQty()==null){
                        pn.setQty(1);
                    }
                    int qty = RemoveItemToCart(pn);
                    prQty.setText(String.valueOf(qty));
                    if(qty>0){
                        btnRemove.setVisibility(View.VISIBLE);
                    }
                    else {
                        btnRemove.setVisibility(View.INVISIBLE);
                    }
                }*/
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prid = (String) v.getTag();
                /*Product pn = null;
                for (Product p:productShown) {
                    if(p.getProductID()==prid){
                        pn = p;
                        break;
                    }
                }
                if(pn!=null){
                    if(pn.getQty()==null || pn.getQty()==0){
                        pn.setQty(1);
                    }
                    int qty = AddItemToCart(pn);
                    prQty.setText(String.valueOf(qty));
                    if(qty>0){
                        btnRemove.setVisibility(View.VISIBLE);
                    }
                    else {
                        btnRemove.setVisibility(View.INVISIBLE);
                    }
                }*/
            }
        });
        return view;
    }
}