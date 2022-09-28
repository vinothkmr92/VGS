package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layut_add_customer,null);
        builder.setView(view)
                .setTitle("Add Customer")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String customerName = customerNameEditText.getText().toString();
                        String mobileNo = customerMobileNumberEditText.getText().toString();
                        String address = customerAddressEditText.getText().toString();
                        listener.getCustomerInfo(customerName,mobileNo,address);
                    }
                });
        customerAddressEditText = view.findViewById(R.id.customerAddress);
        customerNameEditText = view.findViewById(R.id.customerName);
        customerMobileNumberEditText = view.findViewById(R.id.customerMobileNo);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (CustomerDialogListener) context;
        }
        catch (ClassCastException ex){
            throw  new ClassCastException(context.toString()+" must implement CustomerDialogListener interface");
        }

    }

    public interface CustomerDialogListener{
        void getCustomerInfo(String customerName,String mobileNo,String address);
    }
}
