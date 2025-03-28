package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ItemsMasterActivity extends AppCompatActivity implements View.OnClickListener,AddItemDialog.CustomerDialogListener {

    EditText editTextItemNo;
    TextView txtViewItemNo;
    TextView txtViewItemName;
    TextView txtViewItemPrice;
    TextView txtviewItemStock;
    TextView txtviewItemAcPrice;
    ImageButton btnUpdateItem;
    DatabaseHelper dbHelper;
    GridLayout gridLayout;
    private  ProgressDialog dialog;
    Dialog itemSearchdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_master);
        dbHelper = new DatabaseHelper(ItemsMasterActivity.this);
        editTextItemNo = (EditText) findViewById(R.id.editTextItemID);
        txtViewItemNo = (TextView) findViewById(R.id.txtViewItemID);
        txtViewItemName = (TextView) findViewById(R.id.txtViewItemName);
        txtViewItemPrice = (TextView) findViewById(R.id.txtViewItemPrice);
        txtviewItemStock = (TextView)findViewById(R.id.txtViewItemStock);
        btnUpdateItem = (ImageButton) findViewById(R.id.btnupdateitems);
        txtviewItemAcPrice = (TextView)findViewById(R.id.txtViewItemAcPrice);
        gridLayout = (GridLayout)findViewById(R.id.itemViewGrid);
        gridLayout.setVisibility(View.INVISIBLE);
        btnUpdateItem.setEnabled(false);
        btnUpdateItem.setOnClickListener(this);
        editTextItemNo.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                //  Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                dialog.show();
                SearchItems();
                return true;
            }
            return false;
        });
        dialog = new ProgressDialog(ItemsMasterActivity.this);
        dialog.setTitle(" ");
        dialog.setMessage("Loading.....");
    }
    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
    public void OpenAddItemDialog(){
        try {
            AddItemDialog addItemDialog = new AddItemDialog();
            addItemDialog.ItemNo = editTextItemNo.getText().toString();
            addItemDialog.show(getSupportFragmentManager(),"Add Item Dialog");
        }catch (Exception ex){
            showCustomDialog("Error",ex.getMessage().toString());
        }
    }
    private boolean isNumeric(String val){
        try{
            Integer integer = Integer.parseInt(val);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
    private void ResetTxtValues(){
        String itemno = editTextItemNo.getText().toString();
        if(isNumeric(itemno)) {
            int iNo = Integer.parseInt(itemno);
            Item item = dbHelper.GetItem(iNo);
            if (item != null) {
                txtViewItemNo.setText(String.valueOf(item.getItem_No()));
                txtViewItemName.setText(item.getItem_Name());
                txtViewItemPrice.setText(String.format("%.0f", item.getPrice()));
                txtViewItemPrice.setText(String.format("%.0f",item.getPrice()));
                txtviewItemStock.setText(String.format("%.0f",item.getStocks()));
                txtviewItemAcPrice.setText(String.format("%.0f",item.getAcPrice()));
                gridLayout.setVisibility(View.VISIBLE);
                btnUpdateItem.setEnabled(true);
            } else {
                txtViewItemPrice.setText("");
                txtViewItemName.setText("");
                txtViewItemNo.setText("");
                txtviewItemStock.setText("");
                txtviewItemAcPrice.setText("");
                gridLayout.setVisibility(View.INVISIBLE);
                btnUpdateItem.setEnabled(false);
            }
        }
    }
    private void SearchItems(){
        try{
            String itemno = editTextItemNo.getText().toString();
            if(itemno.isEmpty()){
                OpenItemSearchDialog();
            }
            if(isNumeric(itemno)){
                int iNo = Integer.parseInt(itemno);
                Item item = dbHelper.GetItem(iNo);
                if(item!=null){
                    txtViewItemNo.setText(String.valueOf(item.getItem_No()));
                    txtViewItemName.setText(item.getItem_Name());
                    txtViewItemPrice.setText(String.format("%.0f",item.getPrice()));
                    txtviewItemStock.setText(String.format("%.0f",item.getStocks()));
                    txtviewItemAcPrice.setText(String.format("%.0f",item.getAcPrice()));
                    gridLayout.setVisibility(View.VISIBLE);
                    btnUpdateItem.setEnabled(true);
                }
                else{
                    OpenAddItemDialog();
                    btnUpdateItem.setEnabled(false);
                    gridLayout.setVisibility(View.INVISIBLE);
                }
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        finally {
            if(dialog.isShowing())
                dialog.dismiss();
        }
    }
    private void OpenItemSearchDialog(){
        itemSearchdialog =new Dialog(ItemsMasterActivity.this);

        // set custom dialog
        itemSearchdialog.setContentView(R.layout.dialog_items_search);

        // set custom height and width
        itemSearchdialog.getWindow().setLayout(800,800);

        // set transparent background
        itemSearchdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // show dialog
        itemSearchdialog.show();

        // Initialize and assign variable
        EditText editText=itemSearchdialog.findViewById(R.id.edit_textItem);
        ListView listView=itemSearchdialog.findViewById(R.id.list_viewItem);

        // Initialize array adapter
        ArrayList<String> itemnames = dbHelper.GetItemNames();
        ArrayAdapter<String> adapter=new ArrayAdapter<>(ItemsMasterActivity.this, android.R.layout.simple_list_item_1,itemnames);

        // set adapter
        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // when item selected from list
                // set selected item on textView
                String selectredName = adapter.getItem(position);

                // Dismiss dialog
                itemSearchdialog.dismiss();
                Item item = dbHelper.GetItem(selectredName);
                if(item!=null){
                    txtViewItemNo.setText(String.valueOf(item.getItem_No()));
                    txtViewItemName.setText(item.getItem_Name());
                    txtViewItemPrice.setText(String.format("%.0f",item.getPrice()));
                    txtviewItemStock.setText(String.format("%.0f",item.getStocks()));
                    txtviewItemAcPrice.setText(String.format("%.0f",item.getAcPrice()));
                    gridLayout.setVisibility(View.VISIBLE);
                    btnUpdateItem.setEnabled(true);
                }
            }
        });
    }
    private void UpdateItems(){
        try{
            AddItemDialog addItemDialog = new AddItemDialog();
            addItemDialog.ItemNo = txtViewItemNo.getText().toString();
            addItemDialog.ItemName = txtViewItemName.getText().toString();
            addItemDialog.ItemPrice = txtViewItemPrice.getText().toString();
            addItemDialog.ItemStock = txtviewItemStock.getText().toString();
            addItemDialog.ItemAcPrice = txtviewItemAcPrice.getText().toString();
            addItemDialog.show(getSupportFragmentManager(),"Update Item Dialog");
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        finally {
            if(dialog.isShowing())
                dialog.dismiss();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnupdateitems:
                dialog.show();
                UpdateItems();
                break;
        }
    }

    @Override
    public void getCustomerInfo(String ItemNo, String ItemName, Double Price,Double Stock,Double AcPrice) {
        try{
            Item item = new Item();
            if(isNumeric(ItemNo)){
                item.setItem_No(Integer.parseInt(ItemNo));
                item.setItem_Name(ItemName);
                item.setPrice(Price);
                item.setStocks(Stock);
                item.setAcPrice(AcPrice);
                dbHelper.Insert_Item(item);
                showCustomDialog("Msg","Successfully saved item details");
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        finally {
            ResetTxtValues();
        }
    }
}