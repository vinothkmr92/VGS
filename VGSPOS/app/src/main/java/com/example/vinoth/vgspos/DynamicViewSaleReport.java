package com.example.vinoth.vgspos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DynamicViewSaleReport {
    Context ctx;
    private final int textSize = 15;
    private final int txtPadding = 15;
    DynamicViewSaleReport(Context context){
        this.ctx = context;
    }
    public TextView billNoTextView(Context context, String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(txtPadding,txtPadding,txtPadding,txtPadding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(" "+text+" ");
        textView.setMaxEms(8);
        return textView;
    }
    public TextView billDateTextView(Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(txtPadding,txtPadding,txtPadding,txtPadding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(" "+text+" ");
        textView.setMaxEms(8);
        return textView;
    }
    public TextView billAmountTextView(Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(txtPadding,txtPadding,txtPadding,txtPadding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(" "+text+" ");
        textView.setMaxEms(8);
        return textView;
    }
}
