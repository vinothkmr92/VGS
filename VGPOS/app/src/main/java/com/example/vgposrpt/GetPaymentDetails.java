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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.radiobutton.MaterialRadioButton;

public class GetPaymentDetails extends DialogFragment {
    private EditText payingAmt;
    private TextView memView;
    private PaymentDialogListener listener;
    private MaterialRadioButton cashRadioBtn;
    private MaterialRadioButton upiRadioBtn;
    private MaterialRadioButton cardRadioBtn;
    public String memberName;
    public Integer memID;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_get_payment,null);
        TextView titleview = new TextView(getActivity());
        titleview.setText("PAYMENT DETAILS");
        titleview.setBackgroundColor(Color.BLUE);
        titleview.setPadding(10, 10, 10, 10);
        titleview.setGravity(Gravity.CENTER);
        titleview.setTextColor(Color.WHITE);
        titleview.setTypeface(titleview.getTypeface(), Typeface.BOLD);
        titleview.setTextSize(20);
        builder.setView(view)
                .setCustomTitle(titleview)
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setPositiveButton("Pay", (dialog, which) -> {
                    String amt = payingAmt.getText().toString();
                    Double amtToPay = convertToDouble(amt);
                    if(amtToPay>0){
                        String paymentMode = cashRadioBtn.isChecked() ? "CASH": cardRadioBtn.isChecked()?"CARD": "UPI";
                        listener.getPaymentInfo(memID,paymentMode,amtToPay);
                    }
                    else {
                        Toast.makeText(getContext(),"Please enter valid amount to pay.",Toast.LENGTH_LONG);
                    }
                });
        memView = view.findViewById(R.id.memidandno);
        payingAmt = view.findViewById(R.id.paymentAmt);
        cashRadioBtn = view.findViewById(R.id.cashRadioBtn);
        upiRadioBtn = view.findViewById(R.id.upiRadioBtn);
        cardRadioBtn = view.findViewById(R.id.cardRadioBtn);
        cashRadioBtn.setChecked(true);
        cashRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardRadioBtn.setChecked(!cashRadioBtn.isChecked());
                upiRadioBtn.setChecked(!cashRadioBtn.isChecked());
            }
        });
        upiRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardRadioBtn.setChecked(!upiRadioBtn.isChecked());
                cashRadioBtn.setChecked(!upiRadioBtn.isChecked());
            }
        });
        cardRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashRadioBtn.setChecked(!cardRadioBtn.isChecked());
                upiRadioBtn.setChecked(!cardRadioBtn.isChecked());
            }
        });
        memView.setText(memberName);
        payingAmt.requestFocus();
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (PaymentDialogListener) context;
            context = context;
        }
        catch (ClassCastException ex){
            throw  new ClassCastException(context+" must implement CustomerDialogListener interface");
        }

    }

    private static Double convertToDouble(String textValue) {
        double doubleValue;
        try {
            doubleValue = Double.parseDouble(textValue);
        } catch (Exception e) {
            doubleValue = 0.0;
        }
        return doubleValue;
    }
    public interface PaymentDialogListener{
        void getPaymentInfo(Integer memberID,String paymentMode,double amount);
    }
}
