package com.example.vinoth.vgsposbt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddCustomerDialog extends AppCompatDialogFragment {
    private EditText customerNameEditText;
    private EditText customerMobileNumberEditText;
    private EditText customerAddressEditText;
    private CustomerDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layut_add_customer,null);
        builder.setView(view)
                .setTitle("Add Customer")
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    String customerName = customerNameEditText.getText().toString();
                    String mobileNo = customerMobileNumberEditText.getText().toString();
                    String address = customerAddressEditText.getText().toString();
                    listener.getCustomerInfo(customerName,mobileNo,address);
                });
        customerAddressEditText = view.findViewById(R.id.customerAddress);
        customerNameEditText = view.findViewById(R.id.customerName);
        customerMobileNumberEditText = view.findViewById(R.id.customerMobileNo);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (CustomerDialogListener) context;
        }
        catch (ClassCastException ex){
            throw  new ClassCastException(context+" must implement CustomerDialogListener interface");
        }

    }

    public interface CustomerDialogListener{
        void getCustomerInfo(String customerName,String mobileNo,String address);
    }
}
