package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    TextView txtviewkotselection;
    TextView txtviewkotprinter;
    EditText editTextbillcopies;
    EditText editTextkotprinterip;
    CheckBox enablekot;
    public static final String ADDRESSLINE = "ADDRESSLINE";
    public static final String IS3INCH = "IS3INCH";
    TextView rptsizetextview;
    Spinner spinnerbluetothDevice;
    Button saveBtn;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String HEADERMSG = "HEADERMSG";
    public static final String FOOTERMSG= "FOOTERMSG";
    public static final String PRINTERIP = "PRINTERIP";
    public static final String BLUETOOTNAME = "BLUETOOTHNAME";
    public static final String PRINTKOT = "PRINTKOT";
    public static final String KOTPRINTERIP = "KOTPRINTERIP";
    public static final String BILLCOPIES = "BILLCOPIES";
    EditText editTextaddressline;
    CheckBox rpt3inch;
    public static final String ISWIFI = "ISWIFI";
    Set<BluetoothDevice> pairedDevices = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH
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
        rptsizetextview = (TextView)findViewById(R.id.rptsizetxt);
        rpt3inch = (CheckBox)findViewById(R.id.RptSize3inch);
        spinnerbluetothDevice = (Spinner) findViewById(R.id.bltDevice);
        txtviewkotselection = (TextView)findViewById(R.id.kotoption);
        txtviewkotprinter = (TextView)findViewById(R.id.txtViewIPAddressKOT);
        editTextkotprinterip = (EditText) findViewById(R.id.KOTprinterIP);
        enablekot = (CheckBox) findViewById(R.id.enableKOT);
        editTextbillcopies = (EditText)findViewById(R.id.billcopies);
        editTextaddressline = (EditText)findViewById(R.id.addressMsg);
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        String headerMsg = sharedpreferences.getString(HEADERMSG,"");
        String footerMsg = sharedpreferences.getString(FOOTERMSG,"");
        String printerip = sharedpreferences.getString(PRINTERIP,"");
        String bluetothName = sharedpreferences.getString(BLUETOOTNAME,"");
        String isWifi = sharedpreferences.getString(ISWIFI,"YES");
        String is3inch = sharedpreferences.getString(IS3INCH,"YES");
        String printkot = sharedpreferences.getString(PRINTKOT,"NO");
        String kotprinterip = sharedpreferences.getString(KOTPRINTERIP,"");
        String billcopies = sharedpreferences.getString(BILLCOPIES,"1");
        String addressline = sharedpreferences.getString(ADDRESSLINE,"");
        enablekot.setChecked(printkot.equalsIgnoreCase("YES"));
        editTextkotprinterip.setText(kotprinterip);
        rpt3inch.setChecked(is3inch.equalsIgnoreCase("YES"));
        editTextbillcopies.setText(billcopies);
        editTextHeaderMsg.setText(headerMsg);
        editTextFooterMsg.setText(footerMsg);
        editTextPrinterIP.setText(printerip);
        editTextaddressline.setText(addressline);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        radioButtonBluetooth.setChecked(false);
        radioButtonWifi.setChecked(true);
        txtViewBluetooth.setVisibility(View.INVISIBLE);
        spinnerbluetothDevice.setVisibility(View.INVISIBLE);
        rptsizetextview.setVisibility(View.INVISIBLE);
        rpt3inch.setVisibility(View.INVISIBLE);
        saveBtn = (Button) findViewById(R.id.btnSave);
        radioButtonWifi.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButtonBluetooth.setChecked(false);
                    txtViewBluetooth.setVisibility(View.INVISIBLE);
                    spinnerbluetothDevice.setVisibility(View.INVISIBLE);
                    rptsizetextview.setVisibility(View.INVISIBLE);
                    rpt3inch.setVisibility(View.INVISIBLE);
                }
                else{
                    txtViewBluetooth.setVisibility(View.VISIBLE);
                    spinnerbluetothDevice.setVisibility(View.VISIBLE);
                    rptsizetextview.setVisibility(View.VISIBLE);
                    rpt3inch.setVisibility(View.VISIBLE);
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
                     txtviewkotprinter.setVisibility(View.INVISIBLE);
                     txtviewkotselection.setVisibility(View.INVISIBLE);
                     editTextkotprinterip.setVisibility(View.INVISIBLE);
                     enablekot.setVisibility(View.INVISIBLE);
                 }
                 else{
                     txtViewPrinterIP.setVisibility(View.VISIBLE);
                     editTextPrinterIP.setVisibility(View.VISIBLE);
                     txtviewkotprinter.setVisibility(View.VISIBLE);
                     txtviewkotselection.setVisibility(View.VISIBLE);
                     editTextkotprinterip.setVisibility(View.VISIBLE);
                     enablekot.setVisibility(View.VISIBLE);
                 }
            }
        });
        if(!checkPermission()){
            ActivityCompat.requestPermissions(Settings.this,PERMISSIONS_BLUETOOTH
                    ,1);
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
            rptsizetextview.setVisibility(View.INVISIBLE);
            rpt3inch.setVisibility(View.INVISIBLE);
        }
        else{
            radioButtonWifi.setChecked(false);
            radioButtonBluetooth.setChecked(true);
            txtViewPrinterIP.setVisibility(View.INVISIBLE);
            editTextPrinterIP.setVisibility(View.INVISIBLE);
        }
        saveBtn.setOnClickListener(this);
        if(!Common.openSettings){
            Intent page = new Intent(this,SecuredAccess.class);
            page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(page);
        }

    }

    private boolean checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            int bluetooth = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT);
            return  bluetooth==PackageManager.PERMISSION_GRANTED;
        }
        else{
            int bluetooth = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH);
            return  bluetooth==PackageManager.PERMISSION_GRANTED;
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
        b.setCanceledOnTouchOutside(false);
        b.setCancelable(false);
        b.show();
    }
    public  void  GoHome(){
        Intent page = new Intent(this,HomeActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    @Override
    public void onClick(View v) {
        try{
            String headerMsg = editTextHeaderMsg.getText().toString();
            String footerMsg = editTextFooterMsg.getText().toString();
            String printer = editTextPrinterIP.getText().toString();
            String bluetoothName = radioButtonBluetooth.isChecked()? spinnerbluetothDevice.getSelectedItem().toString(): " ";
            String isWifi = radioButtonWifi.isChecked() ? "YES":"NO";
            String is3inch = rpt3inch.isChecked()?"YES":"NO";
            String printKOT = enablekot.isChecked()?"YES":"NO";
            String kotprinterip = editTextkotprinterip.getText().toString();
            String nocopies = editTextbillcopies.getText().toString();
            String addressline = editTextaddressline.getText().toString();
            sharedpreferences.putString(HEADERMSG,headerMsg);
            sharedpreferences.putString(FOOTERMSG,footerMsg);
            sharedpreferences.putString(PRINTERIP,printer);
            sharedpreferences.putString(BLUETOOTNAME,bluetoothName);
            sharedpreferences.putString(ISWIFI,isWifi);
            sharedpreferences.putString(IS3INCH,is3inch);
            sharedpreferences.putString(PRINTKOT,printKOT);
            sharedpreferences.putString(KOTPRINTERIP,kotprinterip);
            sharedpreferences.putString(BILLCOPIES,nocopies);
            sharedpreferences.putString(ADDRESSLINE,addressline);
            sharedpreferences.commit();
            int billcopies = Integer.parseInt(nocopies);
            Common.printerIP = printer;
            Common.headerMeg = headerMsg;
            Common.addressline = addressline;
            Common.footerMsg = footerMsg;
            Common.billcopies = billcopies;
            Common.bluetoothDeviceName = bluetoothName;
            Common.isWifiPrint = radioButtonWifi.isChecked();
            Common.is3Inch = rpt3inch.isChecked();
            Common.printKOT = enablekot.isChecked();
            Common.kotprinterIP = kotprinterip;
            showCustomDialog("Saved","Successfully Saved Data",true);
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage().toString(),false);
        }

    }
}