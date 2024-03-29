package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


public class AddItemDialog extends AppCompatDialogFragment {
    private EditText editTextItemNo;
    private EditText editTextItemName;
    private EditText editTextItemPrice;
    private EditText editTextItemStock;
    private EditText editTextItemAcPrice;
    private AddItemDialog.CustomerDialogListener listener;
    public String ItemNo="";
    public String ItemName="";
    public String ItemPrice="";
    public String ItemStock = "";
    public String ItemAcPrice = "";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_item,null);
        builder.setView(view)
                .setTitle("Add Items")
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    String ItemNo = editTextItemNo.getText().toString();
                    String ItemName = editTextItemName.getText().toString();
                    String priceString = editTextItemPrice.getText().toString();
                    double price = Double.parseDouble(priceString);
                    String stockString = editTextItemStock.getText().toString();
                    if(stockString.isEmpty()){
                        stockString="0";
                    }
                    double stock = Double.parseDouble(stockString);
                    String acpriceString = editTextItemAcPrice.getText().toString();
                    if(acpriceString.isEmpty()){
                        acpriceString = "0";
                    }
                    double acPrice = Double.parseDouble(acpriceString);
                    listener.getCustomerInfo(ItemNo,ItemName,price,stock,acPrice);
                });
        editTextItemNo = view.findViewById(R.id.ItemNoadd);
        editTextItemName = view.findViewById(R.id.ItemNameadd);
        editTextItemPrice = view.findViewById(R.id.ItemPriceadd);
        editTextItemStock = view.findViewById(R.id.ItemStockadd);
        editTextItemAcPrice = view.findViewById(R.id.ItemAcpriceadd);
        if(!ItemNo.isEmpty()){
            editTextItemNo.setText(ItemNo);
        }
        if(!ItemName.isEmpty()){
            editTextItemName.setText(ItemName);
        }
        if(!ItemPrice.isEmpty()){
            editTextItemPrice.setText(ItemPrice);
        }
        if(!ItemStock.isEmpty()){
            editTextItemStock.setText(ItemStock);
        }
        if(!ItemAcPrice.isEmpty()){
            editTextItemAcPrice.setText(ItemAcPrice);
        }
        String itemname = editTextItemName.getText().toString();
        if(!itemname.isEmpty()){
            editTextItemName.selectAll();
        }
        editTextItemName.requestFocus();
        showKeyboard();
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (AddItemDialog.CustomerDialogListener) context;
        }
        catch (ClassCastException ex){
            throw  new ClassCastException(context+" must implement CustomerDialogListener interface");
        }

    }

    public interface CustomerDialogListener{
        void getCustomerInfo(String ItemNo,String ItemName,Double Price,Double Stock,Double AcPrice);
    }
}
