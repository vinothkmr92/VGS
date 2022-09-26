package com.example.vinoth.vgspos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    EditText editTextHeaderMsg;
    EditText editTextFooterMsg;
    RadioButton radioButtonBluetooth;
    RadioButton radioButtonWifi;
    TextView txtViewBluetooth;
    TextView txtViewPrinterIP;
    EditText editTextPrinterIP;
    Spinner spinnerbluetothDevice;
    Button saveBtn;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String HEADERMSG = "HEADERMSG";
    public static final String FOOTERMSG= "FOOTERMSG";
    public static final String PRINTERIP = "PRINTERIP";
    public static final String BLUETOOTNAME = "BLUETOOTHNAME";
    public static final String ISWIFI = "ISWIFI";
    Set<BluetoothDevice> pairedDevices = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT
    };
    ArrayList<String> bluethootnamelist;
    private Dialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editTextHeaderMsg = (EditText) findViewById(R.id.headerMsg);
        editTextFooterMsg = (EditText) findViewById(R.id.footerMsg);
        radioButtonBluetooth = (RadioButton) findViewById(R.id.radioBtnBluetooth);
        radioButtonWifi = (RadioButton) findViewById(R.id.radioBtnWifi);
        txtViewBluetooth = (TextView) findViewById(R.id.txtViewBlutooth);
        txtViewPrinterIP = (TextView) findViewById(R.id.txtViewIPAddress);
        editTextPrinterIP = (EditText) findViewById(R.id.printerIP);
        spinnerbluetothDevice = (Spinner) findViewById(R.id.bltDevice);
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        String headerMsg = sharedpreferences.getString(HEADERMSG,"");
        String footerMsg = sharedpreferences.getString(FOOTERMSG,"");
        String printerip = sharedpreferences.getString(PRINTERIP,"");
        String bluetothName = sharedpreferences.getString(BLUETOOTNAME,"");
        String isWifi = sharedpreferences.getString(ISWIFI,"YES");
        editTextHeaderMsg.setText(headerMsg);
        editTextFooterMsg.setText(footerMsg);
        editTextPrinterIP.setText(printerip);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        radioButtonBluetooth.setChecked(false);
        radioButtonWifi.setChecked(true);
        saveBtn = (Button) findViewById(R.id.btnSave);
        radioButtonWifi.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButtonBluetooth.setChecked(false);
                    txtViewBluetooth.setVisibility(View.INVISIBLE);
                    spinnerbluetothDevice.setVisibility(View.INVISIBLE);
                }
                else{
                    txtViewBluetooth.setVisibility(View.VISIBLE);
                    spinnerbluetothDevice.setVisibility(View.VISIBLE);
                }
            }
        });
        radioButtonBluetooth.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked){
                     radioButtonWifi.setChecked(false);
                     txtViewPrinterIP.setVisibility(View.INVISIBLE);
                     editTextPrinterIP.setVisibility(View.INVISIBLE);
                 }
                 else{
                     txtViewPrinterIP.setVisibility(View.VISIBLE);
                     editTextPrinterIP.setVisibility(View.VISIBLE);
                 }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(Settings.this,PERMISSIONS_BLUETOOTH
                    ,1);
            return;
        }
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        bluethootnamelist = new ArrayList<>();
        int selectedindex=0;
        int i=0;
        for (BluetoothDevice device : pairedDevices) {
            // Add the name and address to an array adapter to show in a ListView
            bluethootnamelist.add(device.getName());
            if(device.getName().equals(bluetothName)){
                selectedindex=i;
            }
            i++;
        }
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,bluethootnamelist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbluetothDevice.setAdapter(adapter);
        spinnerbluetothDevice.setSelection(selectedindex);
        if(isWifi.equalsIgnoreCase("YES")){
          radioButtonWifi.setChecked(true);
          radioButtonBluetooth.setChecked(false);
          txtViewBluetooth.setVisibility(View.INVISIBLE);
          spinnerbluetothDevice.setVisibility(View.INVISIBLE);
        }
        else{
            radioButtonWifi.setChecked(false);
            radioButtonBluetooth.setChecked(true);
            txtViewPrinterIP.setVisibility(View.INVISIBLE);
            editTextPrinterIP.setVisibility(View.INVISIBLE);
        }
        saveBtn.setOnClickListener(this);
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
        Intent page = new Intent(this,HomeActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    @Override
    public void onClick(View v) {
        String headerMsg = editTextHeaderMsg.getText().toString();
        String footerMsg = editTextFooterMsg.getText().toString();
        String printer = editTextPrinterIP.getText().toString();
        String bluetoothName = spinnerbluetothDevice.getSelectedItem().toString();
        String isWifi = radioButtonWifi.isChecked() ? "YES":"NO";
        sharedpreferences.putString(HEADERMSG,headerMsg);
        sharedpreferences.putString(FOOTERMSG,footerMsg);
        sharedpreferences.putString(PRINTERIP,printer);
        sharedpreferences.putString(BLUETOOTNAME,bluetoothName);
        sharedpreferences.putString(ISWIFI,isWifi);
        sharedpreferences.commit();
        showCustomDialog("Saved","Successfully Saved Data",true);
    }
}