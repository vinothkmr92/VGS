package com.imin.printer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.imin.library.SystemPropManager;
import com.imin.printerlib.IminPrintUtils;

import java.util.List;

public class ChooseFontsView extends LinearLayout {
    public ChooseFontsView(Context context) {
        super(context);
        initView(context);
    }

    public ChooseFontsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChooseFontsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ChooseFontsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private List<String> selectList;
    private int currentSelect = 0;

    public Button confirmBtn;
    public Button cancelBtn;
    public Spinner spinner;
    private String printText;

    private IminPrintUtils mIminPrintUtils;


    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_choose_fonts, this, true);
        confirmBtn = findViewById(R.id.confirm_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        spinner = findViewById(R.id.select_sp);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               currentSelect = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentSelect == 0){
                    String hexStr1 = codeWithString(printText, "cp852");
                    mIminPrintUtils.sendRAWData("1C2E1B7412" + hexStr1);

                }else if (currentSelect == 1){
                    String hexStr = codeWithString("ŔÁÂĂÄĹĆÇČÉĘËĚÍÎĎĐŃŇÓÔŐÖ×ŘŮÚŰÜÝŢßŕáâăäĺćçčéęëěíîď ","cp1250");
                    mIminPrintUtils.sendRAWData("1C2E1B7448" + hexStr);
                }else {


                }
                mIminPrintUtils.printAndFeedPaper(100);
                mIminPrintUtils.sendRAWData("1B40");
                // mIminPrintUtils.sendRAWData("1B40");
                if (SystemPropManager.getModel().startsWith("S1")){
                    mIminPrintUtils.sendRAWData("1C26");
                }
            }
        });
    }

    public void setData(List<String> list, String text, IminPrintUtils mImin){
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        printText = text;
        mIminPrintUtils = mImin;
        selectList = list;
    }

    private String codeWithString(String str, String charsetName) {
        String ss = str;
        byte[] bt = new byte[0];

        try {
            bt = ss.getBytes(charsetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s1 = "";
        for (int i = 0; i < bt.length; i++) {
            String tempStr = Integer.toHexString(bt[i]);
            if (tempStr.length() > 2)
                tempStr = tempStr.substring(tempStr.length() - 2);
            s1 = s1 + tempStr + "";
        }
        return s1.toUpperCase();
    }
}
