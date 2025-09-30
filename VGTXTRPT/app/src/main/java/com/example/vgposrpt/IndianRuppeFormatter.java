package com.example.vgposrpt;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class IndianRuppeFormatter extends ValueFormatter  {

    private NumberFormat mFormat;
    public IndianRuppeFormatter()
    {
        mFormat= NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        mFormat.setMaximumFractionDigits(0);
        //mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        return  mFormat.format(value);
    }

}
