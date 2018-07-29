package com.example.vinoth.st_hub;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothReceiverService {
    private static UUID MY_UUID = null;
    private static final String NAME = "Weight Indicator";
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_NONE = 0;
    private static final String TAG = "BluetoothReceiverService";
    private AcceptThread mAcceptThread;
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<ConnectedThread> mConnThreads;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private ArrayList<String> mDeviceAddresses;
    private ArrayList<String> mDeviceNames;
    private final Handler mHandler;
    private ArrayList<BluetoothSocket> mSockets;
    private int mState = 0;
    private ArrayList<UUID> mUuids;

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                if (BluetoothReceiverService.this.mAdapter.isEnabled()) {
                    BluetoothReceiverService.setMY_UUID(UUID.fromString("00001101-0000-1000-8000-" + BluetoothReceiverService.this.mAdapter.getAddress().replace(":", "")));
                }
               // Debugger.logI(BluetoothReceiverService.TAG, "MY_UUID.toString()==" + BluetoothReceiverService.getMY_UUID());
                if (BluetoothReceiverService.getMY_UUID() != null) {
                    tmp = BluetoothReceiverService.this.mAdapter.listenUsingRfcommWithServiceRecord("Weight Indicator", BluetoothReceiverService.getMY_UUID());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.mmServerSocket = tmp;
        }

        public void run() {
            try {
                setName("AcceptThread");
                BluetoothSocket socket = null;
                while (BluetoothReceiverService.this.mState != 3) {
                    try {
                        if (this.mmServerSocket != null) {
                            socket = this.mmServerSocket.accept();
                        }
                        if (socket != null) {
                            synchronized (BluetoothReceiverService.this) {
                                switch (BluetoothReceiverService.this.mState) {
                                    case 0:
                                    case 3:
                                        try {
                                            socket.close();
                                            break;
                                        } catch (IOException e) {
                                           // Debugger.logE(BluetoothReceiverService.TAG, "Could not close unwanted socket" + e);
                                            break;
                                        }
                                    case 1:
                                    case 2:
                                        BluetoothReceiverService.this.connected(socket, socket.getRemoteDevice(), BluetoothReceiverService.this.getAvailablePositionIndexForNewConnection(socket.getRemoteDevice()));
                                        break;
                                }
                            }
                        }
                    } catch (IOException e2) {
                        //Debugger.logE(BluetoothReceiverService.TAG, "accept() failed" + e2);
                        return;
                    }
                }
                return;
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }

        public void cancel() {
            try {
                if (this.mmServerSocket != null) {
                    this.mmServerSocket.close();
                }
            } catch (IOException e) {
              //  Debugger.logE(BluetoothReceiverService.TAG, "close() of server failed" + e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;
        private int selectedPosition;
        private UUID tempUuid;

        public ConnectThread(BluetoothDevice device, UUID uuidToTry, int selectedPosition) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            this.tempUuid = uuidToTry;
            this.selectedPosition = selectedPosition;
            try {
                tmp = device.createRfcommSocketToServiceRecord(uuidToTry);
            } catch (IOException e) {
               // Debugger.logE(BluetoothReceiverService.TAG, "create() failed" + e);
            }
            this.mmSocket = tmp;
        }

        public void run() {
            try {
                setName("ConnectThread");
                BluetoothReceiverService.this.mAdapter.cancelDiscovery();
                try {
                    this.mmSocket.connect();
                    synchronized (BluetoothReceiverService.this) {
                        BluetoothReceiverService.this.mConnectThread = null;
                    }
                    BluetoothReceiverService.this.mDeviceAddresses.set(this.selectedPosition, this.mmDevice.getAddress());
                    BluetoothReceiverService.this.mDeviceNames.set(this.selectedPosition, this.mmDevice.getName());
                    BluetoothReceiverService.this.connected(this.mmSocket, this.mmDevice, this.selectedPosition);
                } catch (IOException e) {
                    BluetoothReceiverService.this.connectionFailed();
                    try {
                        this.mmSocket.close();
                    } catch (IOException e2) {
                      //  Debugger.logE(BluetoothReceiverService.TAG, "unable to close() socket during connection failure" + e2);
                    }
                    BluetoothReceiverService.this.start();
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }

        public void cancel() {
            try {
                if (this.mmSocket != null) {
                    this.mmSocket.close();
                }
            } catch (IOException e) {
               // Debugger.logE(BluetoothReceiverService.TAG, "close() of connect socket failed" + e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket socket) {
           // Debugger.logD(BluetoothReceiverService.TAG, "create ConnectedThread");
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
              //  Debugger.logE(BluetoothReceiverService.TAG, "temp sockets not created" + e);
            }
            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
        }

        public void run() {
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    try {
                        byte[] buffer1 = new byte[1024];
                        char[] characters = new char[20];
                        int i = 0;
                        while (true) {
                            int b = this.mmInStream.read();
                            if (b >= 0) {
                                if (b == 10 || i >= 15) {
                                    String weight = new String(characters);
                                    if (!(weight == null || weight.equals(""))) {
                                        buffer1 = weight.getBytes("UTF-8");
                                        BluetoothReceiverService.this.mHandler.obtainMessage(2, buffer1.length, BluetoothReceiverService.this.getPositionIndexOfDevice(this.mmSocket.getRemoteDevice()), buffer1).sendToTarget();
                                    }
                                    i = 0;
                                    characters = new char[20];
                                } else {
                                    characters[i] = (char) b;
                                    i++;
                                }
                            }
                        }
                    } catch (IOException e) {
                      //  Debugger.logE(BluetoothReceiverService.TAG, "disconnected" + e);
                        BluetoothReceiverService.this.connectionLost(this.mmSocket.getRemoteDevice());
                        return;
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        public void write(byte[] buffer) {
            try {
                this.mmOutStream.write(buffer);
                BluetoothReceiverService.this.mHandler.obtainMessage(3, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
              //  Debugger.logE(BluetoothReceiverService.TAG, "Exception during write" + e);
            }
        }

        public void cancel() {
            try {
                if (this.mmSocket != null) {
                    this.mmSocket.close();
                }
            } catch (IOException e) {
               // Debugger.logE(BluetoothReceiverService.TAG, "close() of connect socket failed" + e);
            }
        }
    }

    public BluetoothReceiverService(Context context, Handler handler) {
        this.mHandler = handler;
        initializeArrayLists();
    }

    private static UUID getMY_UUID() {
        return MY_UUID;
    }

    private static void setMY_UUID(UUID mY_UUID) {
        MY_UUID = mY_UUID;
    }

    public boolean isDeviceConnectedAtPosition(int position) {
        if (this.mConnThreads.get(position) == null) {
            return false;
        }
        return true;
    }

    private void initializeArrayLists() {
        this.mDeviceAddresses = new ArrayList(2);
        this.mDeviceNames = new ArrayList(2);
        this.mConnThreads = new ArrayList(2);
        this.mSockets = new ArrayList(2);
        this.mUuids = new ArrayList(2);
        for (int i = 0; i < 2; i++) {
            this.mDeviceAddresses.add(null);
            this.mDeviceNames.add(null);
            this.mConnThreads.add(null);
            this.mSockets.add(null);
            this.mUuids.add(null);
        }
    }

    public ArrayList<String> getmDeviceNames() {
        return this.mDeviceNames;
    }

    public void setmDeviceNames(ArrayList<String> mDeviceNames) {
        this.mDeviceNames = mDeviceNames;
    }

    public ArrayList<String> getmDeviceAddresses() {
        return this.mDeviceAddresses;
    }

    public void setmDeviceAddresses(ArrayList<String> mDeviceAddresses) {
        this.mDeviceAddresses = mDeviceAddresses;
    }

    private synchronized void setState(int state, BluetoothDevice device) {
        try {
            this.mState = state;
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(1, state, -1, device).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized int getState() {
        return this.mState;
    }

    public synchronized void start() {
        try {
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            if (this.mAcceptThread == null) {
                this.mAcceptThread = new AcceptThread();
                this.mAcceptThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setState(1, null);
    }

    public synchronized void connect(BluetoothDevice device, int selectedPosition) {
        try {
            if (getPositionIndexOfDevice(device) == -1) {
              //  Debugger.logD(TAG, "connect to: " + device);
                if (this.mState == 2 && this.mConnectThread != null) {
                    this.mConnectThread.cancel();
                    this.mConnectThread = null;
                }
                if (this.mConnThreads.get(selectedPosition) != null) {
                    ((ConnectedThread) this.mConnThreads.get(selectedPosition)).cancel();
                    this.mConnThreads.set(selectedPosition, null);
                }
                try {
                    new ConnectThread(device, UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"), selectedPosition).start();
                    setState(2, device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Message msg = this.mHandler.obtainMessage(5);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "This device " + device.getName() + " Already Connected");
                msg.setData(bundle);
                this.mHandler.sendMessage(msg);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, int selectedPosition) {
        try {
           // Debugger.logD(TAG, "connected");
            ConnectedThread mConnectedThread = new ConnectedThread(socket);
            mConnectedThread.start();
            this.mConnThreads.set(selectedPosition, mConnectedThread);
            Message msg = this.mHandler.obtainMessage(4);
            Bundle bundle = new Bundle();
            bundle.putString("device_name", device.getName());
            msg.setData(bundle);
            this.mHandler.sendMessage(msg);
            setState(3, device);
        } catch (Exception e) {
            e.printStackTrace();
            setState(3, null);
        }
    }

    public synchronized void stop() {
        try {
          //  Debugger.logD(TAG, "stop");
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null) {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            if (this.mAcceptThread != null) {
                this.mAcceptThread.cancel();
                this.mAcceptThread = null;
            }
            for (int i = 0; i < 2; i++) {
                this.mDeviceNames.set(i, null);
                this.mDeviceAddresses.set(i, null);
                this.mSockets.set(i, null);
                if (this.mConnThreads.get(i) != null) {
                    ((ConnectedThread) this.mConnThreads.get(i)).cancel();
                    this.mConnThreads.set(i, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setState(0, null);
    }

    public void write(byte[] out, int devicePosition) {
        try {
            synchronized (this) {
                if (isDeviceConnectedAtPosition(devicePosition)) {
                    ConnectedThread r = (ConnectedThread) this.mConnThreads.get(devicePosition);
                   // Debugger.logE("write", "devicePosition : " + devicePosition);
                    r.write(out);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectionFailed() {
        try {
            setState(1, null);
            if (this.mHandler != null) {
                Message msg = this.mHandler.obtainMessage(5);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Unable to connect device");
                msg.setData(bundle);
                this.mHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectionLost(BluetoothDevice device) {
        try {
            int positionIndex = getPositionIndexOfDevice(device);
            if (positionIndex != -1) {
              //  Debugger.logI(TAG, "getPositionIndexOfDevice(device) ===" + ((String) this.mDeviceAddresses.get(getPositionIndexOfDevice(device))));
                this.mDeviceAddresses.set(positionIndex, null);
                this.mDeviceNames.set(positionIndex, null);
                this.mConnThreads.set(positionIndex, null);
                if (this.mHandler != null) {
                    Message msg = this.mHandler.obtainMessage(5);
                    Bundle bundle = new Bundle();
                    bundle.putString("toast", "Device connection was lost from " + device.getName());
                    msg.setData(bundle);
                    this.mHandler.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPositionIndexOfDevice(BluetoothDevice device) {
        int i = 0;
        while (i < this.mDeviceAddresses.size()) {
            try {
                if (this.mDeviceAddresses.get(i) != null && ((String) this.mDeviceAddresses.get(i)).equalsIgnoreCase(device.getAddress())) {
                    return i;
                }
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public int getAvailablePositionIndexForNewConnection(BluetoothDevice device) {
        try {
            if (getPositionIndexOfDevice(device) == -1) {
                for (int i = 0; i < this.mDeviceAddresses.size(); i++) {
                    if (this.mDeviceAddresses.get(i) == null) {
                        return i;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
