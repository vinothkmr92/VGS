package com.example.easypos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DynamicView implements View.OnClickListener {
    Context ctx;
    private final int textSize = 13;
    private final int padding = 25;
    DynamicView(Context context){
        this.ctx = context;
    }

    public Button clrButton(Context context,int index){
        final ViewGroup.LayoutParams lparams =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button btnView = new Button(context);
        btnView.setBackgroundResource(R.drawable.close);
        btnView.setOnClickListener(this);
        btnView.setTag(index);
        btnView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        return btnView;
    }
    public TextView snoTextView(Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(padding,padding,padding,padding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setText(" "+text+" ");
        textView.setMaxEms(8);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        return textView;
    }
    public TextView priceTextView(Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(padding,padding,padding,padding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(" "+text+" ");
        textView.setMaxEms(8);
        return textView;
    }
    public TextView qtyTextView(Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(padding,padding,padding,padding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(" "+text+" ");
        textView.setMaxEms(8);
        return textView;
    }
    public TextView amtTextView(Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(padding,padding,padding,padding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(" "+text+" ");
        textView.setMaxEms(8);
        return textView;
    }

    @Override
    public void onClick(View v) {
        int index = (Integer)v.getTag();
        MainActivity.getInstance().RemoveLine(index);
    }
}
