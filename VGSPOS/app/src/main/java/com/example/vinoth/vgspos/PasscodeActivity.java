package com.example.vinoth.vgspos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class PasscodeActivity extends AppCompatActivity {

    public static boolean isUserPasscode;
    public static SaleReportActivity srpInstance;
    EditText editTextPasscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        editTextPasscode = (EditText)findViewById(R.id.passcode);
        String localPasscode = isUserPasscode?Common.userPasscode:Common.SettingsPassCode;
        ValidatePasscode(editTextPasscode);
        editTextPasscode.requestFocus();
        showKeyboard();
    }
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void   ValidatePasscode(EditText search){
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String enteredvalue = editTextPasscode.getText().toString();
                    String valuetoCompare = isUserPasscode?Common.userPasscode:Common.SettingsPassCode;
                    Common.isAuthenticated = isUserPasscode?enteredvalue.equals(valuetoCompare):false;
                    Common.openSettings = !isUserPasscode?enteredvalue.equals(valuetoCompare):false;
                    if(Common.isAuthenticated || Common.openSettings){
                        if(isUserPasscode){
                            Intent resultIntent = new Intent();
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                        else {
                            Intent settingsIntent = new Intent(v.getContext(),Settings.class);
                            startActivity(settingsIntent);
                        }
                    }
                    else {
                        isUserPasscode = false;
                        showCustomDialog("Access Denied","Please enter valid pin");
                    }


                }
                return  false;
            }

        });
    }
    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                GoHome();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
    @Override
    public void onBackPressed(){
        Common.billDate = new Date();
        Common.billNo = 0;
        Common.itemsCarts = null;
        Common.waiter = "";
        isUserPasscode = false;
        GoHome();
    }
    public  void  GoHome(){
        Intent page = new Intent(this,HomeActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
}