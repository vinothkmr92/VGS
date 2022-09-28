package com.example.vinoth.vgspos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PrintBluetooth {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private OutputStream mOutputStream;
    Set<BluetoothDevice> pairedDevices;
    private Context context;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public PrintBluetooth(Context ctx) throws Exception {
        context = ctx;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(HomeActivity.getInstance(),new String[]{Manifest.permission.BLUETOOTH_CONNECT}
                    ,1);
            return ;
        }
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        String getName = mBluetoothAdapter.getName();
        for (BluetoothDevice device : pairedDevices) {
            // Add the name and address to an array adapter to show in a ListView
            if (device.getName().contains(Common.bluetoothDeviceName)) {
                getName = device.getAddress();
                break;
            }

        }
        ConnectBluetooth(getName);
    }

    @Override
    protected void finalize() throws Throwable {
        try
        {
            if(mOutputStream!=null)
                mOutputStream.close();
            if(mBluetoothSocket != null)
                mBluetoothSocket.close();
        }
        catch (Exception e)
        {
            Log.e("ERR", e.getMessage(), e);
        }
        super.finalize();
    }

    public void ConnectBluetooth(String btName) throws Exception {
        try {
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(btName);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
            mBluetoothSocket.connect();
        }
        catch (Exception ex){
            throw ex;
        }
    }
    public void PrintData(String txt,final byte[] pFormat, final byte[] pAlignment) {
        try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            mOutputStream.write(pAlignment);
            mOutputStream.write(pFormat);
            mOutputStream.write((txt+"\n").getBytes("GBK"));
            mOutputStream.flush();
        } catch (Exception ex) {
            Log.e("ERR", "Exception during write", ex);
        }
    }
    public boolean PrintWithFormat(byte[] buffer, final byte[] pFormat, final byte[] pAlignment) {
        try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            // Notify printer it should be printed with given alignment:
            mOutputStream.write(pAlignment);
            // Notify printer it should be printed in the given format:
            mOutputStream.write(pFormat);
            // Write the actual data:
            mOutputStream.write(buffer, 0, buffer.length);
            mOutputStream.flush();
            return true;
        } catch (IOException e) {
            Log.e("ERR", "Exception during write", e);
            return false;
        }
    }
    public  String padLeft(String s, int n) {
        return StringUtils.leftPad(s,n);
    }
    public String GetFormatedString(String txt,int maxLength){
        String res = padLeft(txt,maxLength);
        return res.substring(0,maxLength);
    }
    public  void Print() {
        int sl = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            String msg = Common.headerMeg+"\n";
            PrintWithFormat(msg.getBytes(StandardCharsets.UTF_8),new Formatter().bold().get(),Formatter.centerAlign());
            if(!Common.waiter.equals("NONE")){
                PrintData("USER     :"+Common.waiter,new Formatter().get(),Formatter.leftAlign());
            }
            PrintData("BILL NO  :"+Common.billNo,new Formatter().get(),Formatter.leftAlign());
            PrintData("DATE     : " + format.format(date),new Formatter().get(),Formatter.leftAlign());
            PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
            String hed = "ITEM        QTY    RATE   AMOUNT";
            PrintData(hed,new Formatter().bold().get(),Formatter.leftAlign());
            PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
            double totalAmt=0;
            for(int k=0;k<Common.itemsCarts.size();k++){
                String name = Common.itemsCarts.get(k).getItem_Name();
                String qty = String.valueOf(Common.itemsCarts.get(k).getQty());
                String price = String.format("%.0f",Common.itemsCarts.get(k).getPrice());
                Double amt = Common.itemsCarts.get(k).getPrice()*Common.itemsCarts.get(k).getQty();
                totalAmt+=amt;
                String amts=String.format("%.0f",amt);
                PrintData(name,new Formatter().get(),Formatter.leftAlign());
                PrintData("           "+GetFormatedString(qty,4)+GetFormatedString(price,9)+GetFormatedString(amts,8),
                        new Formatter().get(),Formatter.leftAlign());
            }
            PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            String ttAmtTxt = "TOTAL AMT:"+String.format("%.0f",totalAmt)+"/-";
            PrintData(ttAmtTxt,new Formatter().bold().get(),Formatter.centerAlign());
            PrintData("  ",new Formatter().get(),Formatter.leftAlign());
            PrintData(Common.footerMsg,new Formatter().get(),Formatter.centerAlign());
            PrintData(" ",new Formatter().get(),Formatter.leftAlign());
            PrintData("  ",new Formatter().get(),Formatter.leftAlign());
            Toast.makeText(context, "Print Queued Successfully.!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            //Toast.makeText(MainActivity.this, e.getMessage(), 1).show();
            e.printStackTrace();
        }
    }
}
