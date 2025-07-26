package com.example.vinoth.vgsposbt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

public class DynamicViewItemRpt {
    private final int textSize = 15;
    private final int txtPadding = 10;
    Context ctx;
    DynamicViewItemRpt(Context context){
        this.ctx = context;
    }
    public TextView itemIDTextView(Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(txtPadding,txtPadding,txtPadding,txtPadding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
        textView.setText(" "+text+" ");
        //textView.setBackgroundResource(R.drawable.border);
        textView.setMaxEms(8);
        return textView;
    }
    public TextView itemNameTextView(Context context, String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(txtPadding,txtPadding,txtPadding,txtPadding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
        String st = StringUtils.rightPad(text,35);
        st = st.substring(0,35);
        textView.setText(st);
        //textView.setBackgroundResource(R.drawable.border);
        return textView;
    }
    public TextView qtyTextView(Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(txtPadding,txtPadding,txtPadding,txtPadding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
        textView.setText(" "+text+" ");
        //textView.setBackgroundResource(R.drawable.border);
        textView.setMaxEms(8);
        return textView;
    }
    public TextView amtTextView (Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(txtPadding,txtPadding,txtPadding,txtPadding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        textView.setText(" "+text+" ");
        //textView.setBackgroundResource(R.drawable.border);
        textView.setMaxEms(8);
        return textView;
    }
}
