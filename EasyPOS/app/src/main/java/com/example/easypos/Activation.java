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

public class Activation extends AppCompatActivity implements View.OnClickListener {

    EditText activationcodeView;
    Button activateBtn;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String ISACTIVATED = "ISACTIVATED";
    private Dialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        progressBar = new Dialog(Activation.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Activation In Progress....");
        activateBtn = (Button) findViewById(R.id.btnActivate);
        activationcodeView = (EditText) findViewById(R.id.activationcode);
        activateBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
    }

    @Override
    public void onClick(View v) {
        String actcod = activationcodeView.getText().toString();
        if(actcod.equals("1@Knowbut")){
             sharedpreferences.putString(ISACTIVATED,"true");
             sharedpreferences.commit();
             showCustomDialog("Status","Successfully Activated.",false);
        }
        else{
           showCustomDialog("Status","Activation Failed. Invalid activation code.",true);
        }
    }
    public  void  GoHome(){
        Intent page = new Intent(this,MainActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    public void showCustomDialog(String title, String Message, final boolean closeapp) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(closeapp){
                    finishAffinity(); // ClosingAlert();//do something with edt.getText().toString();
                }
                else{
                    GoHome();
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
}