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
<<<<<<< HEAD
    EditText editTextPasscode_1;
    EditText editTextPasscode_2;
    EditText editTextPasscode_3;
    EditText editTextPasscode_4;
=======
    EditText editTextPasscode;
>>>>>>> origin/master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
<<<<<<< HEAD
        editTextPasscode_1 = (EditText)findViewById(R.id.passcode_1);
        editTextPasscode_2 = (EditText)findViewById(R.id.passcode_2);
        editTextPasscode_3 = (EditText)findViewById(R.id.passcode_3);
        editTextPasscode_4 = (EditText)findViewById(R.id.passcode_4);
        String localPasscode = isUserPasscode?Common.userPasscode:Common.SettingsPassCode;
        ValidatePasscode(editTextPasscode_4);
        HandleEditText(editTextPasscode_1,editTextPasscode_2,null);
        HandleEditText(editTextPasscode_2,editTextPasscode_3,editTextPasscode_1);
        HandleEditText(editTextPasscode_3,editTextPasscode_4,editTextPasscode_2);
        HandleEditText(editTextPasscode_4,null,editTextPasscode_3);
        editTextPasscode_1.requestFocus();
        showKeyboard();
    }
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
=======
        editTextPasscode = (EditText)findViewById(R.id.passcode);
        String localPasscode = isUserPasscode?Common.userPasscode:Common.SettingsPassCode;
        ValidatePasscode(editTextPasscode);
        editTextPasscode.requestFocus();
        showSoftKeyboard(editTextPasscode);
>>>>>>> origin/master
    }
    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            // here is one more tricky issue
            // imm.showSoftInputMethod doesn't work well
            // and imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0) doesn't work well for all cases too
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }
<<<<<<< HEAD
    private void HandleEditText(EditText editText,EditText next,EditText prev){
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean valid = (keyCode==KeyEvent.KEYCODE_0 || keyCode==KeyEvent.KEYCODE_1
                        || keyCode==KeyEvent.KEYCODE_2|| keyCode==KeyEvent.KEYCODE_3
                        || keyCode==KeyEvent.KEYCODE_4|| keyCode==KeyEvent.KEYCODE_5
                        || keyCode==KeyEvent.KEYCODE_6|| keyCode==KeyEvent.KEYCODE_7
                        || keyCode==KeyEvent.KEYCODE_8|| keyCode==KeyEvent.KEYCODE_9);
                if(valid && next!=null){
                    next.requestFocus();
                }
                else if(keyCode==67 && prev!=null) {
                    prev.requestFocus();
                }
                return false;
            }
        });
    }
=======
>>>>>>> origin/master
    private void   ValidatePasscode(EditText search){
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
<<<<<<< HEAD
                    String firstDigit = editTextPasscode_1.getText().toString();
                    String secondDigit = editTextPasscode_2.getText().toString();
                    String thirdDigit = editTextPasscode_3.getText().toString();
                    String fourthDigit = editTextPasscode_4.getText().toString();
                    String enteredval = firstDigit+secondDigit+thirdDigit+fourthDigit;
=======
                    String enteredval = editTextPasscode.getText().toString();
>>>>>>> origin/master
                    String valuetoCompare = isUserPasscode?Common.userPasscode:Common.SettingsPassCode;
                    Common.isAuthenticated = isUserPasscode?enteredval.equals(valuetoCompare):false;
                    Common.openSettings = !isUserPasscode?enteredval.equals(valuetoCompare):false;
                    if(Common.isAuthenticated || Common.openSettings){
                        if(isUserPasscode){
                            Intent resultIntent = new Intent();
                            //resultIntent.putExtra(...);  // put data that you want returned to activity A
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