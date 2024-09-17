package com.imin.printer.js;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.imin.printerPlugin.IminPrintService;

public class PrinterPluginUtils {

    private IminPrintService iminPrintService;
    private String TAG = "PrinterPluginUtils==lsy===";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iminPrintService = IminPrintService.Stub.asInterface(service);
            Log.i(TAG,"==onServiceConnected=");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"==onServiceDisconnected=");
            iminPrintService =null;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.i(TAG,"==onBindingDied=");
        }
    };


    public PrinterPluginUtils(Activity mContent){
        Intent intent = new Intent();
        //intent.setPackage("com.imin.printerPlugin");
        //intent.setAction("com.imin.printerPlugin.AidlPrintService");
        ComponentName component = new ComponentName("com.imin.printerPlugin","com.imin.printerPlugin.AidlPrintService");
        intent.setComponent(component);
        //mContent.startService(intent);
        mContent.bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }


    public void print(byte[] data,double contrast) throws RemoteException {
        Log.i(TAG,"iminPrintService"+iminPrintService);
        if (iminPrintService!= null){
            iminPrintService.printImage(data,contrast);
        }
    }
}
