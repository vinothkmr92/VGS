package com.example.posactivator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextMachineCode;
    EditText editTextNoofDays;
    EditText editTextActivationKey;
    ImageButton btnSync;
    ImageButton btnShare;
    CheckBox checkBoxIsServer;
    CheckBox checkBoxSMS;
    CheckBox checkBoxEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextMachineCode = (EditText) findViewById(R.id.editTextBoxMachineCode);
        editTextNoofDays = (EditText) findViewById(R.id.editTextBoxNoOfDays);
        editTextActivationKey = (EditText) findViewById(R.id.editTextBoxActivationKey);
        checkBoxIsServer = (CheckBox)findViewById(R.id.checkBoxISServer);
        checkBoxSMS = (CheckBox)findViewById(R.id.checkBoxSMS);
        checkBoxEmail = (CheckBox)findViewById(R.id.checkBoxEmail);
        btnSync = (ImageButton) findViewById(R.id.btnsync);
        btnShare = (ImageButton)findViewById(R.id.btnShare);
        btnSync.setOnClickListener(this);
        btnShare.setOnClickListener(this);
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
    private void SyncDb(){
        try{
            String machineCode  = editTextMachineCode.getText().toString();
            String noofDays =editTextNoofDays.getText().toString();
            String isServer = checkBoxIsServer.isChecked()?"YES":"NO";
            String SMS = checkBoxSMS.isChecked()?"YES":"NO";
            String email = checkBoxEmail.isChecked()?"YES":"NO";
            if(machineCode.isEmpty() || noofDays.isEmpty() || isServer.isEmpty() || SMS.isEmpty()
                    || email.isEmpty()){
                showCustomDialog("Warning","Please check input data.");
                return;
            }
            new SyncData().execute(machineCode,noofDays,isServer,SMS,email);
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.toString());
        }
    }
    public  void Share(){
        String key = editTextActivationKey.getText().toString();
        if(!key.isEmpty()){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody =editTextActivationKey.getText().toString();
            String shareSub = "Share Activation Key";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }
        else{
            showCustomDialog("Warning","No Key to Share.");
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsync:
                SyncDb();
                break;
            case R.id.btnShare:
                Share();
                break;
        }

    }
    class SyncData extends AsyncTask<String,Void,String> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPostExecute(String res){
            if(!res.isEmpty()){
                try{
                    boolean status = !res.startsWith("ERROR:");
                    if(status){
                        showCustomDialog("Msg","Successfully Created Key");
                        editTextActivationKey.setText(res);
                    }
                    else{
                        showCustomDialog("Msg",res);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    showCustomDialog("Exception",ex.toString());
                }
                finally {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }
        }
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle(" ");
            dialog.setMessage("API Sync in progress");
            dialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(String... params){
            String result = "";
            try
            {
                String host= MainActivity.this.getApplicationContext().getString(R.string.ActivationAPIHost);
                String machineCode = params[0];
                String noofdays= params[1];
                String isServer = params[2];
                String isSMS = params[3];
                String isEmail = params[4];
                int nod = Integer.parseInt(noofdays);
                int macCode = Integer.parseInt(machineCode);
                JSONObject requestData = new JSONObject();
                requestData.put("machineCode",macCode);
                requestData.put("isServer",isServer.equalsIgnoreCase("YES"));
                requestData.put("enableSMS",isSMS.equalsIgnoreCase("YES"));
                requestData.put("enableEmail",isEmail.equalsIgnoreCase("YES"));
                requestData.put("noofDays",nod);
                java.net.URL url = new URL("http://"+host+"/api/SKGL/POSActivation");
                //URL url = new URL("http://www.android.com/");
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
                    requestWriter.writeBytes(requestData.toString());
                    requestWriter.close();
                    String responseData = "";
                    InputStream is = connection.getInputStream();
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                    if ((responseData = responseReader.readLine()) != null) {
                        System.out.append("Response: " + responseData);
                        JSONObject res = new JSONObject(responseData);
                        boolean status = res.getBoolean("isSuccess");
                        String ActivationKey = res.getString("activationKey");
                        String ErrorMsg = res.getString("errorMsg");
                        if(!status){
                            result = "ERROR:"+ErrorMsg;
                        }
                        else{
                            result = ActivationKey;
                        }

                    }
                    responseReader.close();
                } catch (Exception exception) {
                    Log.e("ERROR", exception.getMessage());
                    result = "ERROR:"+exception.getMessage();
                    showCustomDialog("Error",exception.getMessage());
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                result = "ERROR:"+e.getMessage().toString();

            }
            return  result;
        }
    }


}