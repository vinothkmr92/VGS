package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {


    DatabaseHelper dbHelper;
    EditText password;
    Button btnLogin;
    private Dialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            dbHelper = new DatabaseHelper(this);
            password = (EditText)findViewById(R.id.password);
            btnLogin = (Button)findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);
            progressBar = new Dialog(MainActivity.this);
            progressBar.setContentView(R.layout.custom_progress_dialog);
            progressBar.setTitle("Loading");
            password.addTextChangedListener(this);
        }
        catch (Exception ex){
            showCustomDialog("ERROR",ex.getMessage());
        }
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
        b.show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
       String sr = s.toString();
       if(sr.length()==4){
           String ps = password.getText().toString();
           if(ps.equals("1234")){
               Intent dcpage = new Intent(this,HomeActivity.class);
               dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(dcpage);
           }
           else {
               showCustomDialog("Warning","Invalid Password");
           }
       }
    }
}
