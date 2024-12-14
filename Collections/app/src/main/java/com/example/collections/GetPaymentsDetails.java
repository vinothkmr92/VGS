package com.example.collections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
   private PaymentDialogListener listener;
   private MaterialRadioButton cashRadioBtn;
   private MaterialRadioButton upiRadioBtn;
   public String loanNo;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_get_payment,null);
        builder.setView(view)
                .setTitle("Payment Detail")
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setPositiveButton("Pay", (dialog, which) -> {
                    String LoanNo = loanNotxtView.getText().toString();
                    String amt = payingAmt.getText().toString();
                    Double amtToPay = convertToDouble(amt);
                    if(amtToPay>0){
                        String paymentMode = cashRadioBtn.isChecked() ? "CASH":"UPI";
                        listener.getPaymentInfo(LoanNo,paymentMode,amtToPay);
                    }
                    else {
                        Toast.makeText(getContext(),"Please enter valid amount to pay.",Toast.LENGTH_LONG);
                    }
                });
        loanNotxtView = view.findViewById(R.id.loannopayment);
        payingAmt = view.findViewById(R.id.paymentAmt);
        cashRadioBtn = view.findViewById(R.id.cashRadioBtn);
        upiRadioBtn = view.findViewById(R.id.upiRadioBtn);
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
        if(!loanNo.isEmpty()){
            loanNotxtView.setText(loanNo);
        }
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
        void getPaymentInfo(String loanNo,String paymentMode,double amount);
    }
}
