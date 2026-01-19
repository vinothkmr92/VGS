package com.example.gatepass_kia;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Set;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String APIURL = "APIURL";
    public static final String PRINTER = "PRINTER";
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    private MySharedPreferences sharedpreferences;
    private BluetoothAdapter mBluetoothAdapter = null;
    Set<BluetoothDevice> pairedDevices = null;
    ArrayList<String> bluethootnamelist;
    EditText apiUrlEditText;
    Button saveBtn;
    Spinner bluttoothSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        try{
            apiUrlEditText = findViewById(R.id.apiURL);
            saveBtn = findViewById(R.id.btnSave);
            bluttoothSpinner = findViewById(R.id.bltDevice);
            saveBtn.setOnClickListener(this);
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            String apiUrl = sharedpreferences.getString(APIURL,"");
            apiUrlEditText.setText(apiUrl);
            String bluetothName = sharedpreferences.getString(PRINTER,"");
            if(!checkPermission()){
                ActivityCompat.requestPermissions(Settings.this,PERMISSIONS_BLUETOOTH
                        ,1);
            }
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            bluethootnamelist = new ArrayList<>();
            int selectedindex=0;
            int i=0;
            for (BluetoothDevice device : pairedDevices) {
                bluethootnamelist.add(device.getName());
                if(device.getName().equals(bluetothName)){
                    selectedindex=i;
                }
                i++;
            }
            ArrayAdapter scadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,bluethootnamelist);
            scadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bluttoothSpinner.setAdapter(scadapter);
            bluttoothSpinner.setSelection(selectedindex);

        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage(),false);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private boolean checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            int bluetooth = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT);
            int scan = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        }
        else{
            int bluetooth = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH);
            int scan = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        }
    }
    public  void  GoHome(){
        Intent page = new Intent(this,MainActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    public void showCustomDialog(String title, String Message, final boolean wait) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(wait){
                    GoHome(); // ClosingAlert();//do something with edt.getText().toString();
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.setCancelable(false);
        b.show();
    }

    private void SaveSettings(){
        try{
            String bluethName = bluttoothSpinner.getSelectedItem().toString();
            String apiUrl = apiUrlEditText.getText().toString();
            sharedpreferences.putString(PRINTER,bluethName);
            sharedpreferences.putString(APIURL,apiUrl);
            sharedpreferences.commit();
            showCustomDialog("Success","Settings Saved",true);
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage(),false);
        }
    }
    @Override
    public void onClick(View view) {
          switch (view.getId()){
              case R.id.btnSave:
                  SaveSettings();
                  break;
          }
    }
}