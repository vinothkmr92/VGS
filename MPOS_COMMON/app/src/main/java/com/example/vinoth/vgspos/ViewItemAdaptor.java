package com.example.vinoth.vgspos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ViewItemAdaptor extends ArrayAdapter<ItemsRpt> {
    private static  final String TAG ="ViewItemAdaptor";
    private Context mContext;
    private int mResource;
    public  ViewItemAdaptor(Context context, int resource, ArrayList<ItemsRpt> object){
        super(context,resource,object);
        mContext = context;
        mResource = resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String itemName = getItem(position).getItemName();
        double qty = getItem(position).getQuantity();
        ItemsRpt rpt = new ItemsRpt();
        rpt.setItemName(itemName);
        rpt.setQuantity(qty);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);
        TextView name = (TextView)convertView.findViewById(R.id.viewName);
        TextView q = (TextView)convertView.findViewById(R.id.viewQty);
        name.setText(itemName);
        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        String str = nf.format(qty);
        q.setText(str);
        return  convertView;
    }
}
