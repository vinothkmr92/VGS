package com.example.vinoth.vgspos;

import android.app.Activity;
import android.view.View;

import java.util.ArrayList;

public class DeleteItemListener implements View.OnClickListener {
    public  static ArrayList<ItemsCart> itemsCarts = QuantityListener.itemsCarts;
    public DeleteItemListener(Activity context, ItemsCart call, CartListAdapter cartListAdapter) {
        itemsCarts.remove(call);
        QuantityListener.itemsCarts = itemsCarts;
    }

    @Override
    public void onClick(View v) {

    }
}
