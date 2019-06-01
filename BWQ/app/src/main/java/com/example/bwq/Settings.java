package com.example.bwq;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    EditText btname;
    Button testConnection;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String BTNAME = "BTNAME";
    private Dialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            testConnection = (Button)findViewById(R.id.testButton);
            btname = (EditText)findViewById(R.id.btName);
            progressBar = new Dialog(Settings.this);
            progressBar.setContentView(R.layout.custom_progress_dialog);
            progressBar.setTitle("Loading");
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            // sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String BTName = sharedpreferences.getString(BTNAME,"");
            btname.setText(BTName);
            testConnection.setOnClickListener(this);
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage(),true);
        }
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
    public  void  SaveSettings(){
        progressBar.show();
        String bltName = btname.getText().toString();
        if(bltName.isEmpty() ){
            progressBar.cancel();
            showCustomDialog("Warning","Host / Username / Dbname should not be Empty.",false);
        }
        else{
            sharedpreferences.putString(BTNAME,bltName);
            sharedpreferences.commit();
            progressBar.cancel();
            showCustomDialog("Saved","Settings Saved Sucessfully",true);
        }
    }

    @Override
    public void onClick(View v) {
       SaveSettings();
    }
}
