package com.example.vinoth.vgspos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class CartActivity extends AppCompatActivity {

    RecyclerView recycler_itemlist;
    CartListAdapter cartListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Set back button to activity
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("KOT Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recycler_itemlist =(RecyclerView) findViewById(R.id.recycler_cart);
        recycler_itemlist.setHasFixedSize(true);
        recycler_itemlist.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        recycler_itemlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recycler_itemlist.getRecycledViewPool().setMaxRecycledViews(0, QuantityListener.itemsCarts.size());
        cartListAdapter = new CartListAdapter(CartActivity.this,QuantityListener.itemsCarts);
        try {
            Thread.sleep(100);
        }
        catch(Exception e) {}
        recycler_itemlist.swapAdapter(cartListAdapter,true);
        recycler_itemlist.getRecycledViewPool().clear();
        cartListAdapter.notifyDataSetChanged();

    }


    private void getIntentData(){
        if(getIntent()!=null && getIntent().getExtras()!=null){
            // Get the Required Parameters for sending Order to server.
        }
    }



    private void placeOrderRequest(){
        //Send Request to Server with required Parameters
    /*
   jsonCartList - Consists of Objects of all product selected.
    */

    }
}