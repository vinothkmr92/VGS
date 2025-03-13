package com.example.gatepass_new;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.USBPort;
import com.sewoo.port.android.USBPortConnection;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.request.android.RequestHandler;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class PrinterUtil {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    // 0x1B
    private final char ESC = ESCPOS.ESC;
    private final char ESC2 = ESCPOS.SP;
    public ESCPOSPrinter posPtr;
    public boolean onlyBill;
    private Context context;
    private Thread hThread;
    private WiFiPort wifiPort;
    private int rtn;
    private BluetoothPort bluetoothPort;
    private boolean isWifi;
    private USBPort usbPort;
    private boolean isUsbport;
    private UsbDevice usbDevice;
    private UsbManager usbManager;
    private Activity activity;
    private boolean receivedBrodCast;
    public GateEntry geEntry;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
                    usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null && !receivedBrodCast) {
                            // YOUR PRINT CODE HERE
                            receivedBrodCast = true;
                            usbPort = new USBPort(usbManager);
                            new ConnectUSBPrinter().execute();
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void finalize() throws Throwable {
        try
        {
            if(bluetoothPort!=null){
                bluetoothPort.disconnect();
            }
            if(wifiPort != null){
                wifiPort.disconnect();
            }
        }
        catch (IOException e)
        {
            Log.e("ERR", e.getMessage(), e);
        }
        catch (InterruptedException e)
        {
            Log.e("ERR", e.getMessage(), e);
        }
        if((hThread != null) && (hThread.isAlive()))
        {
            hThread.interrupt();
            hThread = null;
        }
        super.finalize();
    }
    public PrinterUtil(Context cntx, Activity act){
        activity = act;
        posPtr=new ESCPOSPrinter();
        switch (Common.printType){
            case "WIFI":
                isWifi = true;
                wifiPort = WiFiPort.getInstance();
                break;
            case "USB":
                isUsbport = true;
                break;
            default:
                bluetoothPort = BluetoothPort.getInstance();
                break;
        }
        context = cntx;
    }




    private  int GetPrinterStatus() {
        int sts = 0;
        try
        {
            posPtr.setAsync(true);
            rtn = posPtr.printerSts();
            sts = rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            sts = -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
            sts = -1;
        }
        return sts;
    }


    public  void PrintGatePass(GateEntry ge) {
        int sl = 0;
        HomeActivity act = (HomeActivity)activity;
        try {
            String trNumber = ge.trNumber;
            String vnName = ge.vnName;
            String empTrolly = ge.empTrolly;
            String empBin = ge.empBin;
            String shopName = ge.shopName;
            String gateNo = ge.gateNo;
            String slipNo = ge.slipNo;
            String othr = ge.othr;

            if(slipNo.length()==0){
                slipNo = "0";
            }
            sl = Integer.parseInt(slipNo);
            sl += 1;
            slipNo = String.format("%06d", sl);
            if (trNumber.trim().length()==0) {

                act.showCustomDialog("Warning","No Truck Details to Print.!",false);
                //Toast.makeText(this, "No Truck Details to Print.!", Toast.LENGTH_LONG).show();
                return;
            }
            if(empTrolly.length()==0 || empBin.length()==0){
                if(empBin.length()==0) {
                    act.showCustomDialog("Warning","Please Enter Empty Bins Count.",false);
                    return;
                }
                if(empTrolly.length()==0){
                    act.showCustomDialog("Warning","Please Enter Empty Trolley Count.",false);
                    return;
                }
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm aaa", Locale.getDefault());
            Date date = new Date();
            Bitmap hyndaiLogo = BitmapFactory.decodeResource(act.getResources(),R.drawable.hyundai_logo);
            posPtr.printBitmap(hyndaiLogo,1,400);
            posPtr.printNormal(ESC+"|bC"+ESC+"|cA"+ESC+"|2CMOBIS MOTOR INDIA LIMITED\r\n");
            posPtr.printNormal(ESC+"|cA"+ESC+"|2CGATE PASS\r\n");
            posPtr.printNormal(ESC+"|cA"+ESC+"|2CVENDOR EMPTIES RETURN\r\n");
            posPtr.printNormal("SLIP NO  : "+slipNo+"\r\n");
            posPtr.printNormal("DATE     :" + format.format(date)+"\r\n");
            posPtr.printNormal("MO/IM DIV: "+ gateNo+"\r\n");
            posPtr.printNormal("VENDOR CD: "+shopName+"\r\n");
            posPtr.printNormal("VENDOR NM: " + vnName+"\r\n");
            posPtr.printNormal("TRUCK NO : " + trNumber+"\r\n");
            posPtr.printNormal("--------------------------------");
            //posPtr.setText(23,1);
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM                         QTY\n");
            //posPtr.printNormal("");
            //posPtr.setText(21,0);
            posPtr.printNormal("--------------------------------");
            empTrolly = StringUtils.leftPad(empTrolly,17);
            empTrolly = empTrolly.substring(0,17);
            empBin = StringUtils.leftPad(empBin,17);
            empBin = empBin.substring(0,17);
            othr = StringUtils.leftPad(othr,17);
            othr = othr.substring(0,17);
            posPtr.printNormal("EMPTY TROLLEYS " + empTrolly);
            posPtr.printNormal("EMPTY BINS     " + empBin);
            posPtr.printNormal("REJECTION/EXINV" + othr);
            posPtr.printNormal("--------------------------------");
            posPtr.printNormal("SECURITY\\GA\r\n");
            posPtr.lineFeed(5);
            Toast.makeText(activity, "Print Queued Successfully.!", Toast.LENGTH_LONG).show();
            act.RefreshViews(sl);
        } catch (Exception e) {
            act.showCustomDialog("Print Error",e.getMessage(),false);
            e.printStackTrace();
        }
    }

    private boolean checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int bluetooth = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT);
            int scan = ContextCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        } else {
            int bluetooth = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
            int scan = ContextCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        }
    }
    public BluetoothDevice GetBluetoothDevice() throws Exception {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!checkBluetoothPermission()) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_BLUETOOTH
                    , 1);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice mydevice =null;
        for (BluetoothDevice device : pairedDevices) {
            // Add the name and address to an array adapter to show in a ListView
            if (device.getName().toLowerCase().contains("print")) {
                mydevice = device;
                break;
            }
        }

        return  mydevice;
    }

    public void Print() throws Exception{
        try{
            if(isUsbport){
                ConnectUSB();

            }
            else if(isWifi){
                new ConnectPrinter().execute(Common.printerIP);
            }
            else {
                BluetoothDevice btDevice = GetBluetoothDevice();
                new ConnectToBluetoothPrinter().execute(btDevice);
            }
        }
        catch (Exception ex){
            throw  ex;
        }
    }

    public void ConnectUSB() {
        usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> mDeviceIterator = mDeviceList.values().iterator();
        UsbDevice mDevice = null;
        while (mDeviceIterator.hasNext()) {
            mDevice = mDeviceIterator.next();
            int interfaceCount = mDevice.getInterfaceCount();
            Toast.makeText(activity, "INTERFACE COUNT: " + String.valueOf(interfaceCount), Toast.LENGTH_SHORT).show();

            if (mDevice == null) {
                Toast.makeText(activity, "mDevice is null", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, "USB Device found", Toast.LENGTH_SHORT).show();
            }
        }
        usbDevice = mDevice;
        if (usbManager != null) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    activity,
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
            );
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            activity.registerReceiver(this.usbReceiver, filter);
            usbManager.requestPermission(mDevice, permissionIntent);
        }
    }

    private void PassMsgToActivity(String title,String Msg){
        String localClassName = activity.getLocalClassName();
        switch (localClassName){
            case "HomeActivity":
                HomeActivity.getInstance().showCustomDialog(title,Msg);
                break;
        }
    }

    class ConnectPrinter extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        private GateEntry _geEntry;
        @Override
        protected void onPreExecute()
        {
            _geEntry = geEntry;
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Printing.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String retVal = null;
            try
            {
                // ip
                wifiPort.connect(params[0]);
                retVal="CONNECTED";
            }
            catch (Exception e)
            {
                Log.e("Wifi-connection:",e.getMessage(),e);
                retVal = "ERROR:"+e.getMessage();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("CONNECTED"))
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                posPtr = new ESCPOSPrinter();
                posPtr.setAsync(true);
                try{
                        PrintGatePass(_geEntry);
                }
                catch (Exception ex)
                {
                    PassMsgToActivity("Error",ex.getMessage());
                }
                finally {
                    if(dialog.isShowing())
                        dialog.dismiss();
                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                PassMsgToActivity("Connection Failed","Failed to Connect Printer."+result+".\nPlease contact support team.");
            }
            super.onPostExecute(result);
        }
    }

    class ConnectToBluetoothPrinter extends AsyncTask<BluetoothDevice, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        BluetoothDevice btdevice = null;
        private GateEntry _geentry;
        @Override
        protected void onPreExecute()
        {
            _geentry = geEntry;
            dialog.setTitle("Printing");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Connecting to Printer.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(BluetoothDevice... params)
        {
            String retVal = null;
            try
            {
                btdevice = params[0];
                if(bluetoothPort.isConnected()){
                    bluetoothPort.disconnect();
                }
                bluetoothPort.connect(btdevice);
                retVal="CONNECTED";
            }
            catch (Exception e)
            {
                Log.e("Bluetooth-connection:",e.getMessage(),e);
                retVal = "ERROR:"+e.getMessage();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("CONNECTED"))
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                posPtr = new ESCPOSPrinter();
                posPtr.setAsync(true);
                try{
                    PrintGatePass(_geentry);
                }
                catch (Exception ex)
                {
                    PassMsgToActivity("Error",ex.getMessage());
                }
                finally {
                    if(dialog.isShowing())
                        dialog.dismiss();

                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                PassMsgToActivity("Connection Failed","Failed to Connect Printer."+result+"\nPlease contact support team.");
            }
            super.onPostExecute(result);
        }
    }

    class ConnectUSBPrinter extends AsyncTask<String, Void, USBPortConnection>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        private GateEntry _geentry;
        @Override
        protected void onPreExecute()
        {
            _geentry = geEntry;
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Printing.....");
            dialog.show();
            super.onPreExecute();
        }


        @Override
        protected USBPortConnection doInBackground(String... params)
        {
            USBPortConnection retVal = null;
            try
            {
                // ip
                retVal = usbPort.connect_device(usbDevice);
            }
            catch (Exception e)
            {
                retVal = null;
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(USBPortConnection usbconnection)
        {
            if(usbconnection!=null) {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                try {
                    posPtr = new ESCPOSPrinter(usbconnection);
                    posPtr.setAsync(true);
                    PrintGatePass(_geentry);
                } catch (Exception ex) {
                    PassMsgToActivity("Error",ex.getMessage());
                } finally {

                    if (dialog.isShowing())
                        dialog.dismiss();

                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                PassMsgToActivity("Connection Failed","Failed to Connect Printer.\nPlease contact support team.");
            }
            super.onPostExecute(usbconnection);
        }
    }
}
