package com.example.easypos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    EditText companyNameEditTextView;
    EditText addressEditTextView;
    EditText printerIPEDitTextView;
    Button saveButton;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String COMPANYNAME = "COMPANYNAME";
    public static final String ADDRESS= "ADDRESS";
    public static final String PRINTERIP = "PRINTERIP";
    private Dialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        companyNameEditTextView = (EditText) findViewById(R.id.companyname);
        addressEditTextView = (EditText) findViewById(R.id.address);
        printerIPEDitTextView = (EditText) findViewById(R.id.printerIP);
        progressBar = new Dialog(Settings.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading...");
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        String companyname = sharedpreferences.getString(COMPANYNAME,"");
        String address = sharedpreferences.getString(ADDRESS,"");
        String printerip = sharedpreferences.getString(PRINTERIP,"");
        companyNameEditTextView.setText(companyname);
        addressEditTextView.setText(address);
        printerIPEDitTextView.setText(printerip);
        saveButton = (Button) findViewById(R.id.btnSave);
        saveButton.setOnClickListener(this);
    }


    public void showCustomDialog(String title, String Message, final boolean wait) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(wait){
                    GoHome(); // ClosingAlert();//do something with edt.getText().toString();
                }

            }
        });
        //dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int whichButton) {
        //   //pass
        //}
        //});
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    public  void  GoHome(){
        Intent page = new Intent(this,MainActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    @Override
    public void onClick(View v) {
        String companyname = companyNameEditTextView.getText().toString();
        String address = addressEditTextView.getText().toString();
        String printerip = printerIPEDitTextView.getText().toString();
        sharedpreferences.putString(COMPANYNAME,companyname);
        sharedpreferences.putString(ADDRESS,address);
        sharedpreferences.putString(PRINTERIP,printerip);
        sharedpreferences.commit();
        showCustomDialog("Saved","Successfully Saved Data",true);
    }
}