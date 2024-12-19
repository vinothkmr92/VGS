package com.example.vinoth.vgspos;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class QuantityListener implements View.OnClickListener {
    public  static ArrayList<ItemsCart> itemsCarts;
    public QuantityListener(Activity context, TextView tv_quantity, ItemsCart call, boolean isAdd) {
        if(isAdd){
            ItemsCart temp = itemsCarts.get(itemsCarts.indexOf(call));
            itemsCarts.remove(call);
            Double existingQty = call.getQty();
            if(isAdd){
                existingQty++;
            }
            else {
                existingQty--;
            }
            tv_quantity.setText(String.valueOf(existingQty));
            temp.setQty(existingQty);
            itemsCarts.add(temp);
        }
    }

    @Override
    public void onClick(View v) {
        //
    }
}
