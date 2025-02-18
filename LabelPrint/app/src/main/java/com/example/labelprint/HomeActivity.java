package com.example.labelprint;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tscdll.TSCUSBActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    public UsbManager usbManager;
    public UsbDevice usbDevice;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    Button btnPrint;
    public TSCUSBActivity TscUSB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        btnPrint = findViewById(R.id.btn);
        btnPrint.setOnClickListener(this);
        try{
            TscUSB = new TSCUSBActivity();
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    if(usbManager.hasPermission(usbDevice)){
                        SendPrintLabelCommands(usbManager,usbDevice);
                    }
                }
            }
        }
    };
    public void ConnectUSB() {
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> mDeviceIterator = mDeviceList.values().iterator();
        UsbDevice mDevice = null;
        while (mDeviceIterator.hasNext()) {
            mDevice = mDeviceIterator.next();
            int interfaceCount = mDevice.getInterfaceCount();
            Toast.makeText(HomeActivity.this, "INTERFACE COUNT: " + String.valueOf(interfaceCount), Toast.LENGTH_SHORT).show();

            if (mDevice == null) {
                Toast.makeText(HomeActivity.this, "mDevice is null", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(HomeActivity.this, "USB Device found", Toast.LENGTH_SHORT).show();
            }
        }
        usbDevice = mDevice;
        if (usbManager != null) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    HomeActivity.this,
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0
            );
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(usbReceiver, filter, Context.RECEIVER_EXPORTED);
            usbManager.requestPermission(mDevice, permissionIntent);
        }
    }
    private void SendPrintLabelCommands(UsbManager uManager,UsbDevice uDevice){
        try{
            TscUSB.openport(uManager,uDevice);
            Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sample);
            TscUSB.sendfile(this,uri);
            TscUSB.closeport();
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }
    private void PrintLabel() {
        try {
            ConnectUSB();
        }
        catch (Exception ex) {
            showCustomDialog("Error",ex.getMessage());
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                PrintLabel();
                break;
        }
    }
}