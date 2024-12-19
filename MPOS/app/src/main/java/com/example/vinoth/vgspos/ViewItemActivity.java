package com.example.vinoth.vgspos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewItemActivity extends AppCompatActivity {

    private static ViewItemActivity instance;
    DynamicView dynamicView;
    GridLayout gridLayout;
    private ListView listView;

    public static ViewItemActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        gridLayout = (GridLayout)findViewById(R.id.gridData);
        LoadData();
        instance = this;
    }

    @Override
    public void onBackPressed() {
        Intent page = new Intent(this,HomeActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }

    public void ClearData(){
        gridLayout.removeViews(4,Common.itemsCarts.size()*4);
    }
    public void LoadData(){
        for(int i=0;i<Common.itemsCarts.size();i++){
            ItemsCart item = Common.itemsCarts.get(i);
            dynamicView = new DynamicView(this.getApplicationContext());
            gridLayout.addView(dynamicView.clrButton(getApplicationContext(),i));
            int k = i+1;
            gridLayout.addView(dynamicView.snoTextView(getApplicationContext(),String.valueOf(k)));
            gridLayout.addView(dynamicView.itemNameTextView(getApplicationContext(),item.getItem_Name()));
            gridLayout.addView(dynamicView.qtyTextView(getApplicationContext(),String.valueOf(item.getQty())));
        }
    }
    public  String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    public  String GetformattedItemName(String name){
        String res = padRight(name,25);
        return res.substring(0,25);
    }
}