package com.example.bwq;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ngx.mp100sdk.Enums.Alignments;
import com.ngx.mp100sdk.Intefaces.INGXCallback;
import com.ngx.mp100sdk.NGXPrinter;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;



class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }
}

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String BTNAME = "BTNAME";
    public static final String ACTIVATED = "ACTIVATED";
    DatabaseHelper dbHelper;
    private Dialog progressBar;

    public static NGXPrinter ngxPrinter = NGXPrinter.getNgxPrinterInstance();
    private INGXCallback ingxCallback;

    ImageButton printBT;
    AutoCompleteTextView prName;
    TextView wtView;

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
    String blutName;

    TextView dtDispl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        progressBar = new Dialog(MainActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        prName = (AutoCompleteTextView)findViewById(R.id.prName);
        prName.setOnEditorActionListener(new DoneOnEditorActionListener());
        wtView = (TextView) findViewById(R.id.wtEditor);
        dtDispl = (TextView)findViewById(R.id.dtDisplay);
        printBT = (ImageButton)findViewById(R.id.printBtn);
        printBT.setOnClickListener(this);
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        try{
            this.ingxCallback = new INGXCallback() {
                public void onRunResult(boolean isSuccess) {
                    Log.i("NGX", "onRunResult:" + isSuccess);
                }
                public void onReturnString(String result) {
                    Log.i("NGX", "onReturnString:" + result);
                }

                public void onRaiseException(int code, String msg) {
                    Log.i("NGX", "onRaiseException:" + code + ":" + msg);
                }
            };
            ngxPrinter.initService(this, this.ingxCallback);
            CheckSettings();
            if(progressBar.isShowing()){
                progressBar.cancel();
            }
            Thread myThread = null;

            Runnable runnable = new CountDownRunner();
            myThread= new Thread(runnable);
            myThread.start();
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage());
        }
    }
    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    Date dt = new Date();
                    int hours = dt.getHours();
                    int minutes = dt.getMinutes();
                    int seconds = dt.getSeconds();
                    String curTime = hours + ":" + minutes + ":" + seconds;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa");
                    Date date = new Date();
                    dtDispl.setText("DATE:    "+dateFormat.format(date));
                }catch (Exception e) {}
            }
        });
    }


    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }
    public void  CheckSettings(){
        progressBar.show();
        blutName = sharedpreferences.getString(BTNAME,"");
        try{
            if(findBT()){
                openBT();
            }
        }
        catch (Exception ex){
            //showCustomDialog("EXCEPTION",ex.getMessage());
            Toast.makeText(this,"ERROR: "+ex.getMessage(),Toast.LENGTH_LONG);
        }
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        if (blutName.isEmpty()) {
            progressBar.cancel();
            StartSettingsActivity();
        }
        else {
            LoadAutoComplete();
            progressBar.cancel();
        }

    }
    public void StartSettingsActivity() {
        //showCustomDialog("Warning", "Host / Username / Dbname should not be Empty");
        Intent st = new Intent(this, Settings.class);
        //st.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(st);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                try{
                    closeBT();
                }
                catch (Exception e) {
                }
                startActivity(intent);
                return true;
            case R.id.reports:
                Intent dcpage = new Intent(this,Reports.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            case R.id.Settings:
                Intent settingsPage = new Intent(this,Settings.class);
                //settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return true;
            case R.id.homemenu:
                Intent page = new Intent(this,MainActivity.class);
                page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(page);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showCustomPopup(String title, String Message, final int index) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try{
                    closeBT();
                }
                catch (Exception e) {
                }
                finish();
                //do something with edt.getText().toString();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    private void LoadAutoComplete(){
        ArrayList<String> prNamesList = dbHelper.GetProductNames();
        if(prNamesList.size()>0){
            ArrayAdapter<String> adaptershopList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,prNamesList);
            prName.setAdapter(adaptershopList);
        }

    }
    boolean findBT()
    {
        boolean proceedFurther = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            String error = "Device Doesn't Supports Bluetooth";
            showCustomDialog("Message",error);
        }
        else
        {
            if(!mBluetoothAdapter.isEnabled())
            {
                String  error = "Go to Settings and Enable Bluetooth";
                showCustomDialog("Message",error);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0)
            {
                String finalBtName = "HC";
                if(!blutName.isEmpty()){
                    finalBtName = blutName;
                }
                for(BluetoothDevice device : pairedDevices)
                {
                    if(device.getName().startsWith(finalBtName))
                    {
                        mmDevice = device;
                        proceedFurther = true;
                        break;
                    }
                }
            }
        }
        return  proceedFurther;

    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        if(mmSocket == null){
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
        }
        else {
            if(mmSocket.isConnected()){
                showCustomDialog("Message","Connected to Bluetooth Device");
            }
        }

    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
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
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            String res = data;
                                            Double ft = Double.parseDouble(res);
                                            DecimalFormat formatD = new DecimalFormat();
                                            formatD.setMaximumFractionDigits(3);
                                            String s = String.format("%.3f", ft);
                                            wtView.setText(s);
                                        }
                                    });
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
                }
            }
        });

        workerThread.start();
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }

    @Override
    public void onClick(View v) {
        try{
            if(prName.getText().toString().isEmpty()){
                showCustomDialog("Warning","Please Enter Product Name.");
                return;
            }
            Product pr = new Product();
            pr.setProductID(dbHelper.GetNextProductID());
            pr.setProductName(prName.getText().toString());
            Double wt=Double.parseDouble(wtView.getText().toString());
            pr.setWeight(wt);
            pr.setSupplier("");
            pr.setQty(0d);
            Date date = new Date();
            SimpleDateFormat st = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
            pr.setDtString(st.format(date));
            dbHelper.InsesrtProduct(pr);
            PrintDtl(pr.getProductName(),String.valueOf(pr.getWeight()));
            prName.setText("");
            LoadAutoComplete();
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage());
        }
    }
    public static String rightPadding(String str, int num) {
        return String.format("%1$-" + num + "s", str);
    }
    private  void PrintDtl(String prName,String Wt){
        try{
            if(null == ngxPrinter){
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm aa", Locale.getDefault());
            Date date = new Date();
            ngxPrinter.setDefault();
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("YES YES TECHNOLOGIES", Alignments.CENTER,30);
            ngxPrinter.setStyleDoubleWidth();
            ngxPrinter.setStyleBold();
            String prHeader = rightPadding("PRODUCT NAME",12);
            ngxPrinter.printText("                   ");
            ngxPrinter.printText("                   ");
            ngxPrinter.printText("-----------------------------------", Alignments.LEFT, 20);
            ngxPrinter.printText(prHeader+": WEIGHT",Alignments.LEFT,30);
            ngxPrinter.printText("-----------------------------------", Alignments.LEFT, 20);
            String sr = rightPadding(prName,12);
            ngxPrinter.printText(sr+": "+Wt,Alignments.LEFT,30);
            ngxPrinter.printText("                   ");
            ngxPrinter.printText("                   ");
            ngxPrinter.printText("                   ");
            ngxPrinter.printText("                   ");
            ngxPrinter.setDefault();
        }
        catch (Exception ex){
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG);
            ngxPrinter.printText("Excpetion: "+ex.getMessage());
        }
    }
}
