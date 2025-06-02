package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.sewoo.request.android.RequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BtWeighingScaleService {

    public boolean deviceFound;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    Activity activity;
    Context context;
    HomeActivity homeActivity;
    private Thread hThread;

    public BtWeighingScaleService(Context cntx, HomeActivity actvty) {
        homeActivity = actvty;
        context = cntx;
    }

    @Override
    protected void finalize() throws Throwable {
        closeBT();
    }

    boolean findBT() {
        boolean proceedFurther = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            String error = "Device Doesn't Supports Bluetooth";
            homeActivity.showCustomDialog("Error", error);
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                String error = "Go to Settings and Enable Bluetooth";
            }
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return false;
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(Common.scalebtName)) {
                        mmDevice = device;
                        proceedFurther = true;
                        break;
                    }
                }
            }
        }
        return proceedFurther;
    }




    public void ReadWeightFromScale(){
        new ConnectWeightScale().execute();
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }

    class ConnectWeightScale extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        @Override
        protected void onPreExecute()
        {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Connecting to Weighing Scale.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            Boolean deviceFound = false;
            String retVal = "";
            try
            {
                deviceFound = findBT();
                retVal = deviceFound ? "CONNECTED" : "UNABLE TO CONNECT";
            }
            catch (Exception e)
            {
                Log.e("Wifi-connection:",e.getMessage(),e);
                retVal = "ERROR:"+e.getMessage();
            }
            return retVal;
        }

        String GetDataFromScale(){
            String data = "";
            final byte delimiter = 10;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            try
            {
                int bytesAvailable = mmInputStream.available();
                if(bytesAvailable > 0)
                {
                    byte[] packetBytes = new byte[bytesAvailable];
                    mmInputStream.read(packetBytes);
                    for(int i=0;i<bytesAvailable;i++)
                    {
                        byte b = packetBytes[i];
                        if(b == delimiter)
                        {
                            byte[] encodedBytes = new byte[readBufferPosition];
                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                            data = new String(encodedBytes, "US-ASCII");
                            readBufferPosition = 0;
                            if(!data.isEmpty()){
                                break;
                            }
                        }
                        else
                        {
                            readBuffer[readBufferPosition++] = b;
                        }
                    }
                }
            }
            catch (IOException ex)
            {
                stopWorker = true;
            }
            data = data.replace("Kg","");
            data = data.replace("\r","");
            data = data.replace("\n","");
            return data;
        }

        String openBTGetValue() throws IOException {
            String data = "";
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            if (mmSocket == null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return "";
                }
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                data = GetDataFromScale();
            }
            else {
                if(mmSocket.isConnected()){
                    homeActivity.showCustomDialog("Message","Connected to Bluetooth Device");
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("CONNECTED"))
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                try{
                    String d = openBTGetValue();
                    if(!d.isEmpty()){
                        //HomeActivity.getInstance().SetWeight(d);
                    }
                }
                catch (Exception ex)
                {
                    homeActivity.showCustomDialog("Error",ex.getMessage());
                }
                finally {
                    if(dialog.isShowing())
                        dialog.dismiss();
                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                homeActivity.showCustomDialog("Connection Failed","Failed to Connect Weighing Scale."+result+".\nPlease contact support team.");
            }
            super.onPostExecute(result);
        }
    }
}
