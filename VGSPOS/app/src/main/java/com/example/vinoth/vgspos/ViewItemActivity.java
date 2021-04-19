package com.example.vinoth.vgspos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewItemActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        listView = (ListView) findViewById(R.id.viewlist);
        ArrayList<ItemsRpt> items = new ArrayList<>();
        for(int i=0;i<QuantityListener.itemsCarts.size();i++){
            ItemsRpt ir = new ItemsRpt();
            String name = QuantityListener.itemsCarts.get(i).getItem_Name();
            name = GetformattedItemName(name);
            ir.setItemName(name);
            ir.setQuantity(QuantityListener.itemsCarts.get(i).getQty());
            items.add(ir);
        }
        ViewItemAdaptor adaptor = new ViewItemAdaptor(this,R.layout.custom_view_layout,items);
        listView.setAdapter(adaptor);
    }
    public  String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    public  String GetformattedItemName(String name){
        String res = padRight(name,25);
        return res.substring(0,25);
    }
}