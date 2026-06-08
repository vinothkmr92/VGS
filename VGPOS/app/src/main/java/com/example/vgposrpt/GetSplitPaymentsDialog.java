package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class GetSplitPaymentsDialog extends DialogFragment {
    EditText cashAmt;
    EditText cardAmt;
    EditText upiAmt;
    TextView billAmt;
    Button btnPay;
    public Integer BillAmount;
    //private PaymentDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.getsplitpayments,null);
        TextView titleview = new TextView(getActivity());
        titleview.setText("SPLIT PAYMENTS");
        titleview.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.SeaGreen, null));
        titleview.setPadding(10, 10, 10, 10);
        titleview.setGravity(Gravity.CENTER);
        titleview.setTextColor(Color.WHITE);
        titleview.setTypeface(titleview.getTypeface(), Typeface.BOLD);
        titleview.setTextSize(35);
        builder.setView(view)
                .setCustomTitle(titleview);
        cashAmt = view.findViewById(R.id.splitpaymentCashAmt);
        cardAmt = view.findViewById(R.id.splitpaymentCardAmt);
        upiAmt = view.findViewById(R.id.splitpaymentUPIAmt);
        billAmt = view.findViewById(R.id.splitpaymentbillamt);
        btnPay = view.findViewById(R.id.splitpaymentPayBtn);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        billAmt.setText(formatter.format(BillAmount).replace(symbol,symbol+" "));
        btnPay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String casAmtStr = cashAmt.getText().toString();
                String cardAmtStr = cardAmt.getText().toString();
                String upiAmtStr = upiAmt.getText().toString();
                Integer cashAmtI =casAmtStr.isEmpty() ? 0: Integer.valueOf(casAmtStr);
                Integer cardAmtI =cardAmtStr.isEmpty()?0: Integer.valueOf(cardAmtStr);
                Integer upiAmtI =upiAmtStr.isEmpty()?0: Integer.valueOf(upiAmtStr);
                Integer totalReceived = cashAmtI+cardAmtI+upiAmtI;
                if(totalReceived.equals(BillAmount)){
                    dismiss();
                    if(CommonUtil.isMobileDevice){
                        QuickSaleFragment dn = (QuickSaleFragment)getParentFragment();
                        dn.getSplitPayments(cashAmtI,cardAmtI,upiAmtI);
                    }
                    else {
                        SaleFragment dn = (SaleFragment)getParentFragment();
                        dn.getSplitPayments(cashAmtI,cardAmtI,upiAmtI);
                    }
                }
                else {
                    showCustomDialog("Warning","Amounts Received Should be Equal to Bill Amount.");
                }
                //listener.getPaymentMode("CASH");
            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
    public void showCustomDialog(String title,String Message) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(title)
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
                    // Positive action
                })
                .show();
    }
}

