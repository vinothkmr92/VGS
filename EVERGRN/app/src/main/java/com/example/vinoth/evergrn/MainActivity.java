package com.example.vinoth.evergrn;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    TelephonyManager telephonyManager;
    String IMEI;
    ConnectionClass connectionClass;
    ConnectionClass_Cloud connectionClass_cloud;
    Dialog progressBar;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME = "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    public static final String BTNAME = "BTNAME";
    public static final String IMEIVERIFIED = "IMEIVERIFIED";
    Spinner supervisorSpiner;
    Spinner roomNoSpiner;
    Spinner pickerNoSpiner;
    public static String weightFromBluetooth;
    EditText wet;
    ImageButton btnGetWeigh;
    Button btnSave;
    Button btnReset;
    TextView dt;
    TextView netwtst;
    EditText netweight;
    ArrayList<String> SuperVisorList;
    ArrayList<String> RoomNoList;
    ArrayList<String> PickerList;

    String sqlServer ;
    String sqlUserName;
    String sqlPassword;
    String sqlDB;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = new Dialog(MainActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        connectionClass = new ConnectionClass();
        connectionClass_cloud = new ConnectionClass_Cloud();
        dt = (TextView)findViewById(R.id.dt);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String prefData = format.format(date);
        dt.setText(prefData);
        netwtst = (TextView)findViewById(R.id.netwtstring);
        netweight = (EditText)findViewById(R.id.netweight);
        btnGetWeigh = (ImageButton) findViewById(R.id.btnGetWeight);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnReset = (Button)findViewById(R.id.btnReset);
        btnGetWeigh.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        supervisorSpiner = (Spinner)findViewById(R.id.supervisorDropDwn);
        roomNoSpiner = (Spinner)findViewById(R.id.roomnoDropDwn);
        pickerNoSpiner = (Spinner)findViewById(R.id.pickerDropDwn);
        wet = (EditText)findViewById(R.id.weight);
        SuperVisorList = new ArrayList<>();
        RoomNoList = new ArrayList<>();
        PickerList = new ArrayList<>();
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        String isimeiVerified = sharedpreferences.getString(IMEIVERIFIED,"Y");

        if(!isimeiVerified.equals("Y")) {
            IMEI = "";
            try{
                telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    showCustomDialog("Warning","Enable Permission to Check IMEI Number");
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            1);

                }
                else {
                    IMEI = telephonyManager.getDeviceId();
                }
                if(IMEI.isEmpty()){
                    showCustomPopup("Error","Permission issue to Read IMEI Details.",0);
                    return;
                }
                new CheckIMEI().execute(IMEI);

            }
            catch (Exception ex){
                showCustomDialog("Exception",ex.getMessage());
            }
        }
        else {
            CheckSQLSettings();
        }



    }
    public void StartSettingsActivity() {
        //showCustomDialog("Warning", "Host / Username / Dbname should not be Empty");
        Intent st = new Intent(this, Settings.class);
        //st.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(st);
    }
    public void  CheckSQLSettings(){

        //sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        sqlServer = sharedpreferences.getString(SQLSERVER, "");
        sqlUserName = sharedpreferences.getString(SQLUSERNAME, "");
        sqlPassword = sharedpreferences.getString(SQLPASSWORD, "");
        sqlDB = sharedpreferences.getString(SQLDB, "");
        blutName = sharedpreferences.getString(BTNAME,"");
        CommonUtil.SQL_SERVER = sqlServer;
        CommonUtil.DB = sqlDB;
        CommonUtil.USERNAME = sqlUserName;
        CommonUtil.PASSWORD = sqlPassword;
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
        if (sqlServer.isEmpty() || sqlUserName.isEmpty() || sqlDB.isEmpty()|| sqlPassword.isEmpty()) {

            StartSettingsActivity();
        }
        else {
            new LoadDropDownData().execute("");
        }
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
            case R.id.dcentry:
                Intent dcpage = new Intent(this,DCActivity.class);
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

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
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

    public void showCustomPopup(String title, String Message, final int index) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

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
    public void LoadSpiners() {
         progressBar.show();
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,SuperVisorList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        supervisorSpiner.setAdapter(adapter);

        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,RoomNoList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomNoSpiner.setAdapter(adapter1);

        ArrayAdapter adapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,PickerList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickerNoSpiner.setAdapter(adapter2);


         progressBar.cancel();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.btnGetWeight:
              //  weightFromBluetooth = "4.589";
                   wet.setText(weightFromBluetooth);
                Float wt=Float.parseFloat(weightFromBluetooth);
                Float result = wt-1.800f;
                netwtst.setText(wt.toString()+"-1.800 = ");
                DecimalFormat df = new DecimalFormat("0.000");
                df.setMaximumFractionDigits(3);
                netweight.setText(df.format(result));
                   break;
            case  R.id.btnReset:
                  supervisorSpiner.setSelection(0);
                  roomNoSpiner.setSelection(0);
                  pickerNoSpiner.setSelection(0);
                  wet.setText("0.000");
                  break;
            case R.id.btnSave:
                ValidateDropDowns();
        }

    }

    public  void ValidateDropDowns() {
        String supervisor = supervisorSpiner.getSelectedItem().toString();
        if(supervisor.startsWith("<")){
            showCustomDialog("Validation","Please Select Supervisor.");
        }
        else {
            String roomNo = roomNoSpiner.getSelectedItem().toString();
            if(roomNo.startsWith("<")){
                showCustomDialog("Validation","Please Select Room No.");
            }
            else {
                String pickerNo = pickerNoSpiner.getSelectedItem().toString();
                if(pickerNo.startsWith("<")){
                    showCustomDialog("Validation","Please Select Picker No.");
                }
                else {
                    new SaveEntry().execute(0);
                }
            }
        }
    }
    public class SaveEntry extends AsyncTask<Integer,String,String>
    {

        Integer roomno;
        Integer pickerno;
        String supervisor;
        String wt;
        @Override
        protected void onPreExecute() {
            String rmNo = roomNoSpiner.getSelectedItem().toString();
            roomno = Integer.parseInt(rmNo);
            String pcNo = pickerNoSpiner.getSelectedItem().toString();
            pickerno = Integer.parseInt(pcNo);
            supervisor = supervisorSpiner.getSelectedItem().toString();
            wt = netweight.getText().toString();
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            if(r.startsWith("ERROR")){
                showCustomDialog("Error",r);
            }
            else {
                showCustomDialog("EVERNGRN","Picker Entry Added Successfully.");
                //supervisorSpiner.setSelection(0);
                //roomNoSpiner.setSelection(0);
                pickerNoSpiner.setSelection(0);
                wet.setText("0.000");
            }

            // if(isSuccess) {
            //   Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
            //}

        }

        @Override
        protected String doInBackground(Integer... params) {
            String z="";
            try {
                Integer id = 0;
                Connection con = connectionClass.CONN(sqlServer,sqlDB,sqlUserName,sqlPassword);
                if (con == null) {
                    z = "ERROR: Error in connection with SQL server";
                } else {
                    try{
                        String qr = "SELECT MAX(ENTRY_ID) AS ID FROM PICKER_ENTRIES";
                        Statement statement = con.createStatement();
                        ResultSet sr = statement.executeQuery(qr);
                        if(sr.next()){
                            Integer s = sr.getInt("ID");
                            s+=1;
                            id = s;
                        }
                        sr.close();
                        statement.close();
                    }
                    catch (Exception ex){

                    }
                    String query = "INSERT INTO PICKER_ENTRIES VALUES (?,?,?,"+wt+",GETDATE(),?)";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.setInt(1,id);
                    preparedStatement.setInt(2,pickerno);
                    preparedStatement.setInt(3,roomno);
                    preparedStatement.setString(4,supervisor);
                    preparedStatement.execute();
                    preparedStatement.close();
                    con.close();
                    z = "SUCCESS";
                }
            }
            catch (Exception ex)
            {

                z = "ERROR:"+ex.getMessage();
            }
            return z;
        }
    }
    public class LoadDropDownData extends AsyncTask<String,String,String>
    {

        ArrayList<String> spList = new ArrayList<>();
        ArrayList<String> rmList = new ArrayList<>();
        ArrayList<String> pkList = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            if(r.startsWith("ERROR")){
                 showCustomDialog("Exception",r);
            }
            else {
                 SuperVisorList = spList;
                 RoomNoList = rmList;
                 PickerList = pkList;
                 LoadSpiners();
            }

            // if(isSuccess) {
            //   Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
            //}

        }

        @Override
        protected String doInBackground(String... params) {
             String z="";
            try {
                String imi = params[0];
                Connection con = connectionClass.CONN(sqlServer,sqlDB,sqlUserName,sqlPassword);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT SUPERVISOR FROM SUPERVISORS";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    spList.add("<SELECT SUPERVISOR>");
                    while (rs.next())
                    {
                        String act = rs.getString("SUPERVISOR");
                           spList.add(act);
                    }
                    rs.close();
                    stmt.close();
                    query = "SELECT ROOM_NO FROM ROOMS";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    rmList.add("<SELECT ROOMNO>");
                    while (rs.next()){
                        Integer s = rs.getInt("ROOM_NO");
                        rmList.add(s.toString());
                    }
                    rs.close();
                    stmt.close();
                    query="SELECT PICKER_NO FROM PICKERS";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    pkList.add("<SELECT PICKER>");
                    while (rs.next()){
                        Integer s = rs.getInt("PICKER_NO");
                        pkList.add(s.toString());
                    }
                    z = "SUCCESS";
                }
            }
            catch (Exception ex)
            {

                z = "ERROR:"+ex.getMessage();
            }
            return z;
        }
    }
    public class CheckIMEI extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;


        String IMEI_Number = IMEI;


        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            //Toast.makeText(LoginActivity.this,r,Toast.LENGTH_LONG).show();
            if(!isSuccess){
                showCustomPopup("Error",r,0);
            }
            else {

                sharedpreferences.putString(IMEIVERIFIED,"Y");
                sharedpreferences.commit();
            }
            // if(isSuccess) {
            //   Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
            //}

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String imi = params[0];
                Connection con = connectionClass_cloud.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT * FROM IMEI_DETAILS WHERE IMEI_NO='"+imi+"'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        String act = rs.getString("ISACTIVE");
                        act = act.toUpperCase();
                        if(act.equals("Y")){
                            isSuccess = true;
                        }
                        else {
                            z = "Your Device Has been Deactivated. Please Contact Administrators";
                        }
                    }
                    else
                    {
                        z = "Your Device is Not Activated. Please Contact Administrators";
                        isSuccess = false;
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions";
            }
            return z;
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
            progressBar.cancel();
        }
        else
        {
            if(!mBluetoothAdapter.isEnabled())
            {

                String  error = "Go to Settings and Enable Bluetooth";
                showCustomDialog("Message",error);
                progressBar.cancel();
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
        progressBar.show();
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        if(mmSocket == null){
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            progressBar.cancel();
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
                                            weightFromBluetooth = data;
                                            wet.setText(weightFromBluetooth);
                                            CommonUtil.WeightFromBlueTooth = weightFromBluetooth;
                                            Float wt=Float.parseFloat(weightFromBluetooth);
                                            Float result = wt-1.800f;
                                            netwtst.setText(wt.toString()+"-1.800 = ");
                                            DecimalFormat df = new DecimalFormat("0.000");
                                            df.setMaximumFractionDigits(3);
                                            netweight.setText(df.format(result));

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
}
