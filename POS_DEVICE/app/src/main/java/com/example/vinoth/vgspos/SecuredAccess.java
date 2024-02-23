package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SecuredAccess extends AppCompatActivity implements View.OnClickListener {

    EditText settingspwd;
    Button activateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secured_access);
        activateBtn = (Button) findViewById(R.id.btnActivate);
        settingspwd = (EditText) findViewById(R.id.activationcode);
        activateBtn.setOnClickListener(this);
    }
    @Override
    public void onBackPressed(){
        GoHome();
    }
    @Override
    public void onClick(View v) {
        String actcod = settingspwd.getText().toString();
        if(actcod.equals("1@Knowbut")){
            Common.openSettings = true;
            Intent page = new Intent(this, Settings.class);
            page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(page);
        }
        else{
            showCustomDialog("Status","Access Failed. Invalid Pass code.");
        }
    }
    public  void  GoHome(){
        Intent page = new Intent(this,HomeActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                GoHome();
            }
        });
        //dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int whichButton) {
        //   //pass
        //}
        //});
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
}