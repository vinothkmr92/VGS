package com.example.gatepass.gatepass;

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
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    // 0x1B
    private final char ESC = ESCPOS.ESC;
    private final char ESC2 = ESCPOS.SP;
    public ESCPOSPrinter posPtr;
    private Context context;
    private Thread hThread;
    private int rtn;
    private BluetoothPort bluetoothPort;
    public ReceiptData receiptData;
    private Activity activity;
    @Override
    protected void finalize() throws Throwable {
        try
        {
            if(bluetoothPort!=null){
                bluetoothPort.disconnect();
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
        bluetoothPort = BluetoothPort.getInstance();
        context = cntx;
    }

    public int PrintBillData(ReceiptData rcptData) throws InterruptedException
    {
        try
        {
            PrintBill(rcptData);
            if(bluetoothPort!=null){
                bluetoothPort.disconnect();
            }
            return 1;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            MainActivity.getInstance().showCustomDialog("Error",e.getMessage());
        }
        return 0;
    }

    Bitmap getMultiLangTextAsImage(String text, float textSize, Typeface typeface)  {
        try{
            TextPaint mPaint = new TextPaint();
            mPaint.setColor(Color.BLACK);
            if (typeface != null) mPaint.setTypeface(typeface);
            mPaint.setTextSize(textSize);
            Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
            int widthm = 400;
            StaticLayout mStaticLayout = new StaticLayout(text, mPaint, widthm, alignment, 0, 0, true);
            int width = mStaticLayout.getWidth();
            int height = mStaticLayout.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            mStaticLayout.draw(canvas);
            return bitmap;
        }
        catch (Exception ex){
            return  null;
        }
    }
    Bitmap getTextAsImage(String text, float textSize,Layout.Alignment alignment,Typeface tp)  {
        try{
            TextPaint mPaint = new TextPaint();
            mPaint.setColor(Color.BLACK);
            if(tp==null){
                Typeface typeface = Typeface.MONOSPACE;
                Typeface boldTypeface = Typeface.create(typeface,Typeface.BOLD);
                mPaint.setTypeface(boldTypeface);
            }
            else {
                mPaint.setTypeface(tp);
            }
            mPaint.setTextSize(textSize);
            int widthm = 380;
            StaticLayout mStaticLayout = new StaticLayout(text, mPaint, widthm, alignment, 0, 0, true);
            int width = mStaticLayout.getWidth();
            int height = mStaticLayout.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            mStaticLayout.draw(canvas);
            return bitmap;
        }
        catch (Exception ex){
            return  null;
        }
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

    private void PrintBill(ReceiptData rcptData) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
        Date date = new Date();
        Bitmap hyndaiLogo = BitmapFactory.decodeResource(activity.getResources(),R.drawable.hyundai_logo);
        //Bitmap kialog = BitmapFactory.decodeResource(activity.getResources(),R.drawable.kialogo);
        posPtr.printBitmap(kialog,1);
        posPtr.setAlignment(1);
       // posPtr.setText(2,1);
        posPtr.printNormal(ESC + "|cA" + ESC + "|1CHYUNDAI MOTOR INDIA LIMITED\n");
        //posPtr.printNormal(ESC+"|cA"+ESC+"|1CKIA INDIA\n");
        posPtr.printNormal(ESC + "|cA" + ESC + "|2CGATE PASS\n");
        posPtr.printNormal(ESC + "|cA" + ESC + "|1CVENDOR EMPTIES RETURN\n");
        posPtr.setAlignment(0);
       // posPtr.setText(1,0);
        posPtr.printNormal("SLIP NO  : "+rcptData.slipno+"\n");
        posPtr.printNormal("DATE     : " +rcptData.date+"\n");
        posPtr.printNormal("SHOP     : "+ rcptData.shopname+"\n");
        posPtr.printNormal("GATE NO  : "+rcptData.gateno+"\n");
        posPtr.printNormal("VENDOR   : " + rcptData.VendorName+"\n");
        posPtr.printNormal("TRUCK NO : " + rcptData.truckNo+"\n");
        posPtr.printNormal("--------------------------------"+"\n");
        //posPtr.setText(1,1);
        posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM                      QTY   "+"\n");
        posPtr.printNormal("--------------------------------"+"\n");
        posPtr.printNormal("EMPTY TROLLEYS              " + rcptData.empTrolly+"\n");
        posPtr.printNormal("EMPTY BINS                  " + rcptData.empBin+"\n");
        posPtr.printNormal("OTHERS                      " + rcptData.others+"\n");
        posPtr.printNormal("--------------------------------\n");
        posPtr.printNormal("SECURITY\\GA             "+rcptData.securityno);
        posPtr.lineFeed(1);
        posPtr.lineFeed(7);
        posPtr.cutPaper();
    }

    private boolean checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int bluetooth = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT);
            int scan = ActivityCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        } else {
            int bluetooth = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
            int scan = ActivityCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_SCAN);
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
            if (device.getName().toUpperCase().contains("PRINT")) {
                mydevice = device;
                break;
            }
        }

        return  mydevice;
    }

    public void Print() throws Exception{
        try{
            BluetoothDevice btDevice = GetBluetoothDevice();
            new ConnectToBluetoothPrinter().execute(btDevice);
        }
        catch (Exception ex){
            throw  ex;
        }
    }

    private void PassMsgToActivity(String title,String Msg){
        MainActivity.getInstance().showCustomDialog(title,Msg);
    }

    class ConnectToBluetoothPrinter extends AsyncTask<BluetoothDevice, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        BluetoothDevice btdevice = null;
        private ReceiptData _receiptData;
        @Override
        protected void onPreExecute()
        {
            _receiptData = receiptData;
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
                    PrintBillData(_receiptData);
                }
                catch (Exception ex)
                {
                    PassMsgToActivity("Error",ex.getMessage());
                }
                finally {
                    if(dialog.isShowing())
                        dialog.dismiss();
                    MainActivity.getInstance().RefreshViews();
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
}
