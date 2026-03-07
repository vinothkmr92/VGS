package com.example.vinoth.vgspos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DynamicViewSaleReport implements View.OnClickListener {
    private final int textSize = 15;
    private final int txtPadding = 10;
    Context ctx;
    DynamicViewSaleReport(Context context){
        this.ctx = context;
    }
    public TextView billNoTextView(Context context, String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(5,5,5,5);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
        textView.setText(" "+text+" ");
        textView.setMaxEms(8);
        return textView;
    }
    public TextView billDateTextView(Context context,String text){
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(context);
        textView.setLayoutParams(lparams);
        textView.setTextSize(textSize);
        textView.setPadding(txtPadding,txtPadding,0,txtPadding);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
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
    public Button printButton(Context context, String billno){
        final ViewGroup.LayoutParams lparams =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button btnView = new Button(context);
        btnView.setBackgroundResource(R.drawable.ic_baseline_print_24);
        btnView.setOnClickListener(this);
        String tag = "Print"+"~"+billno;
        btnView.setTag(tag);
        btnView.setLayoutParams(new LinearLayout.LayoutParams(70, 70));
        return btnView;
    }
    public Button deleteButton(Context context,String billno){
        final ViewGroup.LayoutParams lparam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button btnDelete = new Button(context);
        btnDelete.setBackgroundResource(R.drawable.close);
        btnDelete.setOnClickListener(this);
        String tag = "Delete"+"~"+billno;
        btnDelete.setTag(tag);
        btnDelete.setLayoutParams(new LinearLayout.LayoutParams(70,70));
        return  btnDelete;
    }

    @Override
    public void onClick(View v) {
        String[] bd = ((String)v.getTag()).split("~");
        if(bd.length>2){
            String action = bd[0];
            String billNo = bd[1]+"~"+bd[2];
            switch (action){
                case "Print":
                    SaleReportActivity.getInstance().PrintBill(billNo);
                    break;
                case "Delete":
                    SaleReportActivity.getInstance().DeleteBill(billNo);
                    break;
            }
        }


    }
}
