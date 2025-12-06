package com.example.vgposrpt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class GetPaymentModeDialog extends DialogFragment {
    ImageButton btnCash;
    ImageButton btnCard;
    ImageButton btnUpi;
    //private PaymentDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.getpaymentmode,null);
        TextView titleview = new TextView(getActivity());
        titleview.setText("PAYMENT MODE");
        titleview.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.SeaGreen, null));
        titleview.setPadding(10, 10, 10, 10);
        titleview.setGravity(Gravity.CENTER);
        titleview.setTextColor(Color.WHITE);
        titleview.setTypeface(titleview.getTypeface(), Typeface.BOLD);
        titleview.setTextSize(35);
        builder.setView(view)
                .setCustomTitle(titleview);
        btnCash = view.findViewById(R.id.btnCash);
        btnCard = view.findViewById(R.id.btnCard);
        btnUpi = view.findViewById(R.id.btnUpi);
        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(CommonUtil.isMobileDevice){
                    QuickSaleFragment dn = (QuickSaleFragment)getParentFragment();
                    dn.getPaymentMode("CASH");
                }
                else {
                    SaleFragment dn = (SaleFragment)getParentFragment();
                    dn.getPaymentMode("CASH");
                }
                //listener.getPaymentMode("CASH");
            }
        });
        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(CommonUtil.isMobileDevice){
                    QuickSaleFragment dn = (QuickSaleFragment)getParentFragment();
                    dn.getPaymentMode("CARD");
                }
                else {
                    SaleFragment dn = (SaleFragment)getParentFragment();
                    dn.getPaymentMode("CARD");
                }
                //listener.getPaymentMode("CARD");
            }
        });
        btnUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(CommonUtil.isMobileDevice){
                    QuickSaleFragment dn = (QuickSaleFragment)getParentFragment();
                    dn.getPaymentMode("UPI");
                }
                else {
                    SaleFragment dn = (SaleFragment)getParentFragment();
                    dn.getPaymentMode("UPI");
                }
                //listener.getPaymentMode("UPI");
            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
