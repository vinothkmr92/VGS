package com.example.vinoth.baklfin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    DatabaseHelper dbHelper;
    EditText userID;
    EditText password;
    Button btnLogin;
    private Dialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try{
            dbHelper = new DatabaseHelper(this);
            userID = (EditText)findViewById(R.id.userid);
            password = (EditText)findViewById(R.id.password);
            btnLogin = (Button)findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);
            progressBar = new Dialog(LoginActivity.this);
            progressBar.setContentView(R.layout.custom_progress_dialog);
            progressBar.setTitle("Loading");
        }
        catch (Exception ex){
            showCustomDialog("ERROR",ex.getMessage());
        }

    }


    @Override
    public void onClick(View view) {
        String usrId = userID.getText().toString();
        String pwd = password.getText().toString();
        Users users = dbHelper.GetUser(usrId);
        if(null != users){
           if(users.getPassword().equals(pwd)){
              CommonUtil.LogedINUser = users;
               Intent dcpage = new Intent(this,Home.class);
               dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(dcpage);
           }
           else {
               showCustomDialog("Warning","Invalid Password. Please Check Password");
           }
        }
        else {
          showCustomDialog("Warning","User Not Found. Please Check User ID");
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



}
