package com.example.collections;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class GetPaymentsDetails extends AppCompatDialogFragment {
   private EditText payingAmt;
   private TextView loanNotxtView;
   private LinearLayout intprinlayout;
   private PaymentDialogListener listener;
   private MaterialRadioButton cashRadioBtn;
   private MaterialRadioButton upiRadioBtn;
   private MaterialRadioButton principalRadioBtn;
   private MaterialRadioButton interestRadioBtn;
   public String loanNo;
   public boolean isInterestLoan;
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
                    String LoanNo = loanNotxtView.getText().toString();
                    String amt = payingAmt.getText().toString();
                    Double amtToPay = convertToDouble(amt);
                    boolean isintereset = intprinlayout.getVisibility()==View.VISIBLE ? interestRadioBtn.isChecked():false;
                    if(amtToPay>0){
                        String paymentMode = cashRadioBtn.isChecked() ? "CASH":"UPI";
                        listener.getPaymentInfo(LoanNo,paymentMode,amtToPay,isintereset);
                    }
                    else {
                        Toast.makeText(getContext(),"Please enter valid amount to pay.",Toast.LENGTH_LONG);
                    }
                });
        intprinlayout = view.findViewById(R.id.intprinlayout);
        loanNotxtView = view.findViewById(R.id.loannopayment);
        payingAmt = view.findViewById(R.id.paymentAmt);
        cashRadioBtn = view.findViewById(R.id.cashRadioBtn);
        upiRadioBtn = view.findViewById(R.id.upiRadioBtn);
        interestRadioBtn = view.findViewById(R.id.intestRadioBtn);
        principalRadioBtn = view.findViewById(R.id.principalRadioBtn);
        cashRadioBtn.setChecked(true);
        cashRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upiRadioBtn.setChecked(!cashRadioBtn.isChecked());
            }
        });
        upiRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashRadioBtn.setChecked(!upiRadioBtn.isChecked());
            }
        });
        interestRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                principalRadioBtn.setChecked(!interestRadioBtn.isChecked());
            }
        });
        principalRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interestRadioBtn.setChecked(!principalRadioBtn.isChecked());
            }
        });
        if(!loanNo.isEmpty()){
            loanNotxtView.setText(loanNo);
        }
        intprinlayout.setVisibility(isInterestLoan?View.VISIBLE:View.GONE);
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
        void getPaymentInfo(String loanNo,String paymentMode,double amount,boolean isInterest);
    }
}
