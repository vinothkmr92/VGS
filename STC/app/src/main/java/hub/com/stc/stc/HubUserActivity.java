package hub.com.stc.stc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HubUserActivity extends AppCompatActivity implements View.OnClickListener{

    Spinner hubSpinner;
    Spinner stateSpinner;
    CheckBox isDocument;
    EditText awbNumber;
    EditText Quantity;
    EditText weight;
    Button sendBtn;
    Spinner manifestTransit;
    Spinner destType;
    Button addBtn;
    ArrayList<Despatch> despatches;
    ArrayList<Hubs> hubsArrayList;
    RadioButton hubRadioButton;
    RadioButton srcRadioButton;
    TextView selectSRCText;
    Spinner srcSpinner;
    ArrayList<Src> srcList;
    ArrayList<States> stateArrayList;
    String weightFromBluetooth="";

    public static final Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String PostgreSQLHost = "PostgreSQLHost";
    public static final String PostgreSQLUserName= "PostgreSQLUserName";
    public static final String PostgreSQLPassword = "PostgreSQLPassword";
    public static final String PostgreSQLDB = "PostgreSQLDB";
    public static final String PostgreSQLDB_FEN = "PostgreSQLDB_FEN";
    public static final String DefaultWeight = "DefaultWeight";
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static  String DB_URL = "jdbc:postgresql://192.168.43.107:5432/HUB";
    static  String HOST ="";
    static  String STDBNAME = "";
    static  String FENDBNAME ="";
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
    //  Database credentials
    static  String USER = "postgres";
    static  String PASS = "1@Vinothkmr";
    ArrayList hubList ;
    ArrayList stateList;
    private Dialog progressBar;
    private TableLayout dataGrid;
    private Boolean isHubLoaded;
    private CheckBox isMuliple;
    private ImageButton addimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_user);
        hubSpinner = (Spinner)findViewById(R.id.hubSpinner);
        stateSpinner = (Spinner)findViewById(R.id.stateSpinner);
        isDocument = (CheckBox) findViewById(R.id.isDocument);
        awbNumber = (EditText)findViewById(R.id.awbNumber);
        Quantity = (EditText)findViewById(R.id.qty);
        weight = (EditText)findViewById(R.id.Weight);
        sendBtn = (Button)findViewById(R.id.sendButton);
        addBtn = (Button)findViewById(R.id.addButton);
        isMuliple = (CheckBox) findViewById(R.id.ismultiple);
        addimage = (ImageButton) findViewById(R.id.addimage);
        hubRadioButton = (RadioButton)findViewById(R.id.hubradioButton);
        srcRadioButton = (RadioButton)findViewById(R.id.srcradioButton);
        selectSRCText = (TextView)findViewById(R.id.selectsrcText);
        srcSpinner = (Spinner)findViewById(R.id.srcSpinner);
        manifestTransit = (Spinner)findViewById(R.id.manifestTransitspinner);
        destType = (Spinner)findViewById(R.id.despatchtypespinner);
        progressBar = new Dialog(HubUserActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        despatches = new ArrayList<Despatch>();
        dataGrid = (TableLayout) findViewById(R.id.datagrid);
        hubsArrayList = new ArrayList<Hubs>();
        stateArrayList = new ArrayList<States>();
        addBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        srcList = new ArrayList<Src>();
        isHubLoaded = false;
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        //getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String hostname = sharedpreferences.getString(PostgreSQLHost,"");
        String SQLUser = sharedpreferences.getString(PostgreSQLUserName,"");
        String SQLPassword = sharedpreferences.getString(PostgreSQLPassword,"");
        String Databasename = sharedpreferences.getString(PostgreSQLDB,"");
        String defWeight = sharedpreferences.getString(DefaultWeight,"0.250");
        STDBNAME = sharedpreferences.getString(PostgreSQLDB,"");
        FENDBNAME = sharedpreferences.getString(PostgreSQLDB_FEN,"");
        hubRadioButton.setOnClickListener(this);
        srcRadioButton.setOnClickListener(this);
        addimage.setOnClickListener(this);
        selectSRCText.setVisibility(View.VISIBLE);
        srcSpinner.setVisibility(View.VISIBLE);
        isDocument.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isDocument.isChecked()){
                    isMuliple.setVisibility(View.VISIBLE);

                }
                else {
                    isMuliple.setVisibility(View.INVISIBLE);
                }
            }
        });
        isMuliple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isMuliple.isChecked()){
                    addimage.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.VISIBLE);
                }
                else {
                    addBtn.setVisibility(View.INVISIBLE);
                    addimage.setVisibility(View.INVISIBLE);
                }
            }
        });
        isHubLoaded = true;
        stateSpinner.setEnabled(false);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Your code here
                States st = (States) stateSpinner.getSelectedItem();
                new LoadHubs().execute(st.getState_code());
                new LoadSRC().execute(CommonUtil.selectedHub.getHub_code());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        hubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(srcRadioButton.isChecked()){
                    Hubs hub = (Hubs)hubSpinner.getSelectedItem();
                    new LoadSRC().execute(hub.getHub_code());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //awbNumber.setOnEditorActionListener(new DoneOnEditorActionListener());
        if(hostname.isEmpty() || SQLUser.isEmpty() || Databasename.isEmpty()){
            StartSettingsActivity();
        }
        else {
            DB_URL = "jdbc:postgresql://"+hostname+":5432/"+Databasename;
            HOST = hostname;
            USER = SQLUser;
            PASS = SQLPassword;
            weight.setText(defWeight);
            hubList = new ArrayList();
            stateList = new ArrayList();
            new LoadStates().execute("");
            LoadManifestAndDespatchSpinner();
        }
        try{
            progressBar.show();
           if(findBT()){
               openBT();
           }


        }catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
            progressBar.cancel();
        }
        finally {
            if(progressBar.isShowing()){

            }
        }
        awbNumber.setOnKeyListener(new View.OnKeyListener() {


            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                  //  Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                    if(isDocument.isChecked()){
                        boolean isExists = false;
                        int index = 0;
                        for(int i=0;i<despatches.size();i++){
                            Despatch d = despatches.get(i);
                            if(d.getAWB_NUMBER().equals(awbNumber.getText().toString())){
                                index = i;
                                isExists = true;
                                break;
                            }
                        }
                        if(isExists){
                            awbNumber.setNextFocusDownId(R.id.awbNumber);
                            showCustomPopup("Duplicate Entries","Do you want to Delete Existing or Cancel ?",index);
                            awbNumber.setText("");
                        }
                        else{
                            if(awbNumber.getText().toString().isEmpty()){
                                showCustomDialog("Warning","Consignment Number Can not be Empty. Please Enter Valid AWB Number.");
                            }
                            else {
                                int length = awbNumber.getText().toString().length();
                                if(length != 11){
                                    showCustomDialog("Warning","AWB Length should be 11. Please check the Number");
                                    awbNumber.selectAll();
                                }
                                else {
                                    if(ValidateAWB(awbNumber.getText().toString())){
                                        awbNumber.setNextFocusDownId(R.id.awbNumber);
                                        Despatch ds = new Despatch();
                                        ds.setAWB_NUMBER(awbNumber.getText().toString());
                                        ds.setQUANTITY(Quantity.getText().toString());
                                        ds.setWEIGHT((weight.getText().toString()));
                                        despatches.add(ds);
                                        LoadDataGrid();
                                        awbNumber.setText("");
                                        Quantity.setText("1");
                                        String defWeight = sharedpreferences.getString(DefaultWeight,"0.250");
                                        weight.setText(defWeight);
                                    }
                                    else {
                                        showCustomDialog("Error","Invalid AWB Number. Please Check the Number.");
                                        awbNumber.selectAll();
                                    }

                                }

                            }

                        }

                    }
                    else {
                        boolean isExists = false;
                        int index = 0;
                        for(int i=0;i<despatches.size();i++){
                            Despatch d = despatches.get(i);
                            if(d.getAWB_NUMBER().equals(awbNumber.getText().toString())){
                                index = i;
                                isExists = true;
                                break;
                            }
                        }
                        if(isExists){
                           // awbNumber.setNextFocusDownId(R.id.qty);
                            showCustomPopup("Duplicate Entries","Do you want to Delete Existing or Cancel ?",index);
                            awbNumber.setText("");
                        }
                        else {

                            if(awbNumber.getText().toString().isEmpty()){
                                showCustomDialog("Warning","Consignment Number Can not be Empty. Please Enter Valid AWB Number.");
                                awbNumber.selectAll();
                            }
                            else {

                                if(isMuliple.isChecked()){
                                    weight.setText(weightFromBluetooth);
                                    awbNumber.setNextFocusDownId(R.id.qty);
                                }
                                else {
                                    int length = awbNumber.getText().toString().length();
                                    if(length != 11){
                                        showCustomDialog("Warning","AWB Length should be 11. Please check the Number");
                                    }
                                    else {
                                        if(ValidateAWB(awbNumber.getText().toString())){
                                            weight.setText(weightFromBluetooth);
                                            Despatch ds = new Despatch();
                                            ds.setAWB_NUMBER(awbNumber.getText().toString());
                                            ds.setQUANTITY(Quantity.getText().toString());
                                            ds.setWEIGHT((weight.getText().toString()));
                                            despatches.add(ds);
                                            LoadDataGrid();
                                            awbNumber.setText("");
                                            Quantity.setText("1");
                                            String defWeight = sharedpreferences.getString(DefaultWeight,"0.250");
                                            weight.setText(defWeight);
                                        }
                                        else {
                                            showCustomDialog("Error","Invalid AWB Number. Please Check the Number.");
                                            awbNumber.selectAll();
                                        }
                                    }

                                }
                              //  awbNumber.setNextFocusDownId(R.id.qty);

                            }

                        }

                    }
                    awbNumber.setFocusableInTouchMode(true);
                    awbNumber.setFocusable(true);
                    awbNumber.requestFocus();
                    return true;
                }

                return false;
            }
        });

    }
    public boolean ValidateAWB(String awb){
        boolean isValid = false;
        String sr = awb.substring(0,10);
        long value = Long.parseLong(sr);
        long lastdigit = value%7;
        String validawb = String.valueOf(value)+String.valueOf(lastdigit);
        if(awb.equals(validawb)){
            isValid = true;
        }
        return isValid;
    }
    public void FocustoAWBNumber(){
        awbNumber.setFocusableInTouchMode(true);
        awbNumber.requestFocus();
    }




    @Override
    public void onStop(){
        super.onStop();
        try{
            closeBT();

        }catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());

        }
    }

    class DoneOnEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                awbNumber.requestFocus();
                return true;
            }
            return false;
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
                for(BluetoothDevice device : pairedDevices)
                {
                    if(device.getName().startsWith("HC"))
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
    public  void  LoadDataGrid(){
        dataGrid.removeAllViews();
        if(despatches.size()>0){
            stateSpinner.setEnabled(false);
            hubSpinner.setEnabled(false);
            srcSpinner.setEnabled(false);
            srcRadioButton.setEnabled(false);
            hubRadioButton.setEnabled(false);
            manifestTransit.setEnabled(false);
            destType.setEnabled(false);
        }
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setPadding(10,10,10,10);
        TextView textView = new TextView(this);
        textView.setText("AWB NUMBER");
        textView.setTextColor(Color.BLUE);
        //textView.setTextSize(30);
        textView.setBackgroundResource(R.drawable.cellborder);
        textView.setBackgroundColor(Color.WHITE);
        row.addView(textView);
        TextView textView1 = new TextView(this);
        textView1.setText("        QUANTITY");
        //textView1.setTextSize(30);
        textView1.setPadding(10,0,10,0);
        textView1.setTextColor(Color.BLUE);
        textView1.setBackgroundResource(R.drawable.cellborder);
        textView1.setBackgroundColor(Color.WHITE);
        row.addView(textView1);
        TextView textView2 = new TextView(this);
        textView2.setText("        WEIGHT");
        //textView2.setTextSize(30);
        textView2.setPadding(10,0,10,0);
        textView2.setTextColor(Color.BLUE);
        textView2.setBackgroundResource(R.drawable.cellborder);
        textView2.setBackgroundColor(Color.WHITE);
        row.addView(textView2);
        dataGrid.addView(row,new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        int i=0;
        for(Despatch b : despatches)
        {
            // Inflate your row "template" and fill out the fields.
            TableRow row1 = new TableRow(this);
            row1.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row1.setPadding(5,5,5,5);
            TextView textView3 = new TextView(this);
            textView3.setText(b.getAWB_NUMBER());
            textView3.setTextColor(Color.BLACK);
            textView3.setBackgroundResource(R.drawable.cellborder);
            textView3.setBackgroundColor(Color.WHITE);
            row1.addView(textView3);
            TextView textView4 = new TextView(this);
            textView4.setText(String.valueOf("                  "+b.getQUANTITY()));
            textView4.setTextColor(Color.BLACK);
            textView4.setBackgroundResource(R.drawable.cellborder);
            textView4.setBackgroundColor(Color.WHITE);
            textView4.setPadding(10,0,10,0);
            row1.addView(textView4);
            TextView textView5 = new TextView(this);
            textView5.setText(String.valueOf("                  "+b.getWEIGHT()));
            textView5.setBackgroundResource(R.drawable.cellborder);
            textView5.setTextColor(Color.BLACK);
            textView5.setPadding(10,0,10,0);
            textView5.setBackgroundColor(Color.WHITE);
            row1.addView(textView5);
            dataGrid.addView(row1,new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
        awbNumber.requestFocus();
       // ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.grid_view_item,despatches);
       // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }
    public  void  StartSettingsActivity(){
        showCustomDialog("Warning","Host / Username / Dbname should not be Empty");
        Intent st = new Intent(this,Settings.class);
        st.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        try{
            closeBT();
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        switch (item.getItemId()) {
            case R.id.exit:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                return true;
            case R.id.logout:
                CommonUtil.LoggedInHUB = "";
                CommonUtil.isLoggedIn = false;
                Intent loginPage = new Intent(this,LoginActivity.class);
                loginPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginPage);
                return true;
            case R.id.Settings:
                Intent settingsPage = new Intent(this,Settings.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return true;
            case R.id.homemenu:
                if(CommonUtil.isLoggedIn){
                    Intent page = new Intent(this,MainActivity.class);
                    page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(page);
                }
                else {
                    Intent loginage = new Intent(this,LoginActivity.class);
                    loginage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginage);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showCustomPopup(String title, String Message, final int index) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                despatches.remove(index);
                LoadDataGrid();
                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int whichButton) {
           //pass
        }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
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
    public void LoadManifestAndDespatchSpinner(){
        ArrayList<Manifest_Tranist> manifestList = new ArrayList<Manifest_Tranist>();
        Manifest_Tranist mt = new Manifest_Tranist();
        mt.setMt_Code("S");
        mt.setMt_Name("SURFACE");
        manifestList.add(mt);
        mt = new Manifest_Tranist();
        mt.setMt_Code("A");
        mt.setMt_Name("AIR");
        manifestList.add(mt);

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,manifestList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        manifestTransit.setAdapter(adapter);

        ArrayList<Despatch_Type> destpatchList = new ArrayList<Despatch_Type>();
        Despatch_Type dt = new Despatch_Type();
        dt.setType_Code("N");
        dt.setType_Name("Normal");
        destpatchList.add(dt);

        dt = new Despatch_Type();
        dt.setType_Code("M");
        dt.setType_Name("Miss Route");
        destpatchList.add(dt);

        dt=new Despatch_Type();
        dt.setType_Code("R");
        dt.setType_Name("Return");
        destpatchList.add(dt);

        dt=new Despatch_Type();
        dt.setType_Code("A");
        dt.setType_Name("Accounts");
        destpatchList.add(dt);

        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,destpatchList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destType.setAdapter(adapter1);
    }
   public void LoadStateSpinner(ArrayList states){
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,states);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);
        if(srcRadioButton.isChecked()){
            States st = null;
            for(int i=0;i<stateArrayList.size();i++){
                if(CommonUtil.selectedHub.getState_code().equals(stateArrayList.get(i).getState_code())){
                    st = stateArrayList.get(i);
                    break;
                }
            }
            int index = getIndex(stateSpinner,st.getState_name());
            stateSpinner.setSelection(index);
        }
   }
   public  void  LoadHubSpinner(ArrayList hubs){
       ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,hubs);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       hubSpinner.setAdapter(adapter);
       if(srcRadioButton.isChecked()){
           int index = getIndex(hubSpinner,CommonUtil.selectedHub.getHub_name().trim());
           hubSpinner.setSelection(index);
         //  hubSpinner.setEnabled(false);
       }

   }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
    public void ShoworHideDropdowns_SRC(){
        hubRadioButton.setChecked(false);
        selectSRCText.setVisibility(View.VISIBLE);
        srcSpinner.setVisibility(View.VISIBLE);
        stateSpinner.setEnabled(false);
        Hubs hub = CommonUtil.selectedHub;
        if(hub != null){
            isHubLoaded = true;

            new LoadHubs().execute(CommonUtil.selectedHub.getState_code().trim());
            States st = null;
            for(int i=0;i<stateArrayList.size();i++){
                States s = stateArrayList.get(i);
                if(hub.getState_code().trim().equals(s.getState_code().trim())){
                    st = s;
                    break;
                }
            }
            if(st != null){
                stateSpinner.setSelection(getIndex(stateSpinner,st.getState_name()));



            }
        }
        new LoadSRC().execute(CommonUtil.selectedHub.getHub_code());

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addimage:
                double currentWeight = Double.parseDouble(weight.getText().toString());
                double newWeight = Double.parseDouble(weightFromBluetooth);
                double totale = currentWeight+newWeight;
                DecimalFormat f = new DecimalFormat("##.00");
                weight.setText(f.format(totale));
                break;
            case R.id.srcradioButton:
                if(srcRadioButton.isChecked()){

                   ShoworHideDropdowns_SRC();
                  // ShoworHideDropdowns_SRC();
                }
                break;
            case R.id.hubradioButton:
                if(hubRadioButton.isChecked()){
                    srcRadioButton.setChecked(false);
                    hubSpinner.setEnabled(true);
                    stateSpinner.setEnabled(true);
                    selectSRCText.setVisibility(View.INVISIBLE);
                    srcSpinner.setVisibility(View.INVISIBLE);
                }
                break;
            case  R.id.sendButton:
                if(despatches.size()>0){
                    ArrayList<Despatchs> despList = new ArrayList<Despatchs>();
                    for(int i=0;i<despatches.size();i++){
                        Despatchs ds = new Despatchs();
                        Despatch d = despatches.get(i);
                        ds.setAwb_no(d.getAWB_NUMBER());
                        int qty = Integer.parseInt(d.getQUANTITY());
                        ds.setQty(qty);
                        double weight = Double.parseDouble(d.getWEIGHT());
                        ds.setWeight(weight);
                        Despatch_Type despatch_type = (Despatch_Type)destType.getSelectedItem();
                        if(despatch_type != null){
                            ds.setDesp_type(despatch_type.getType_Code());
                        }
                        else{
                            ds.setDesp_type("N");
                        }
                        if(isDocument.isChecked()){
                            ds.setDoc_type("D");
                            ds.setNon_doc(false);
                        }
                        else {
                            ds.setDoc_type("N");
                            ds.setNon_doc(true);
                        }
                        ds.setSrc_hubsc("H");
                        if(srcRadioButton.isChecked()){
                            ds.setDesp_hubsc("S");
                        }
                        else {
                            ds.setDesp_hubsc("H");
                        }
                        despList.add(ds);
                    }
                   // showCustomDialog("DespList","DespList: "+despList.size());
                    new AddDespatchesAndManifest().execute(despList);
                }
                else {
                    showCustomDialog("Warning","No AWB to Send..!");
                }
                break;
            case R.id.addButton:
                Despatch ds = new Despatch();
                ds.setAWB_NUMBER(awbNumber.getText().toString());
                ds.setQUANTITY(Quantity.getText().toString());
                ds.setWEIGHT((weight.getText().toString()));
                despatches.add(ds);
                LoadDataGrid();
                awbNumber.setText("");
                Quantity.setText("1");
                String defWeight = sharedpreferences.getString(DefaultWeight,"0.250");
                weight.setText(defWeight);
                awbNumber.setNextFocusDownId(R.id.awbNumber);
                awbNumber.setFocusable(true);
                awbNumber.setFocusableInTouchMode(true);
                awbNumber.requestFocus();
                break;
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            //showCustomDialog("Message","Device Name: "+capitalize(model));
            return capitalize(model);
        } else {
            //showCustomDialog("Message","Device Name: "+capitalize(manufacturer)+" "+model);
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
public String GetDestStateAndHub(){
        String dest_code = "";
    States st = (States)stateSpinner.getSelectedItem();
    dest_code = st.getState_code();
        if(!srcRadioButton.isChecked()){
            Hubs hub = (Hubs)hubSpinner.getSelectedItem();
            dest_code = dest_code+hub.getHub_code();
        }
        else {
            Src src = (Src)srcSpinner.getSelectedItem();

                dest_code = dest_code+src.getSc_code();


        }


        return dest_code;
}
    class AddDespatchesAndManifest extends AsyncTask<ArrayList,Void,String> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void  onPreExecute(){
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        public void onPostExecute(String result) {
            progressBar.cancel();
            if(result.startsWith("Error")){
                showCustomDialog("Error",result);
            }
            else {
                showCustomDialog("Message","Manifest details '"+result+"' Sucessfully added.");
                despatches.clear();
                dataGrid.removeAllViews();
                stateSpinner.setEnabled(true);
                hubSpinner.setEnabled(true);
                srcSpinner.setEnabled(true);
                srcRadioButton.setEnabled(true);
                hubRadioButton.setEnabled(true);
                manifestTransit.setEnabled(true);
                destType.setEnabled(true);
                awbNumber.setText("");
            }

        }
        public boolean isAcccount(String awbNumber,String DBName){
            boolean isVaidAccount = false;
            Connection conn = null;
            PreparedStatement st = null;
            try{
                //STEP 2: Register JDBC driver
                Class.forName(JDBC_DRIVER);

                //STEP 3: Open a connection
                DB_URL = "jdbc:postgresql://"+HOST+":5432/"+DBName;
                System.out.println("Connecting to database...");
                if(PASS.isEmpty()){
                    conn = DriverManager.getConnection(DB_URL,USER,null);
                }
                else {
                    conn = DriverManager.getConnection(DB_URL,USER,PASS);
                }
                String query = "SELECT public.awb_data(?)";
                st = conn.prepareStatement(query);
                st.setString(1,awbNumber);
                ResultSet rs = st.executeQuery();
                while(rs.next()){
                    String output = rs.getString(0);
                    if(!output.startsWith("(xx")){
                        isVaidAccount = true;
                    }
                }
            }
            catch (Exception e) {
                //Handle errors for Class.forName
//                showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
               // error = "Error: "+e.getMessage();

            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                  //  error = "Error: "+se2.getMessage();
                    //showCustomDialog("Exception", se2.getMessage());
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    //error = "Error: "+se.getMessage();
                    // showCustomDialog("Exception", se.getMessage());
                }
            }
            return isVaidAccount;
        }
        public String AddManifest_Despatches(ArrayList<Despatchs> despatchs,String SourceHubSRC,String DestHubSRC,String DBName){
            String error = "";
            Connection conn = null;
            Statement st = null;
            try {
                //STEP 2: Register JDBC driver
                Class.forName(JDBC_DRIVER);

                //STEP 3: Open a connection
                DB_URL = "jdbc:postgresql://"+HOST+":5432/"+DBName;
                System.out.println("Connecting to database...");
                if(PASS.isEmpty()){
                    conn = DriverManager.getConnection(DB_URL,USER,null);
                }
                else {
                    conn = DriverManager.getConnection(DB_URL,USER,PASS);
                }
                st = conn.createStatement();
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyMMdd", Locale.getDefault());
                String prefData = format.format(date);
                String manifestidLike = CommonUtil.selectedHub.getState_code()+CommonUtil.selectedHub.getHub_code();
                String sql = "SELECT config_value FROM public.config WHERE CONFIG_NAME='LastManifest'";
                ResultSet sr = st.executeQuery(sql);
                int serialNum = 0;
                while (sr.next()){

                    String s = sr.getString("config_value");
                    s = s.trim();
                    if(s != null && !s.isEmpty()){
                        String datevalue = s.substring(0,6);
                        if(datevalue.equals(prefData)){
                            serialNum = Integer.parseInt(s);
                        }
                        else{
                            String sn = prefData+"0000";
                            int in = Integer.parseInt(sn);
                            serialNum = in;
                        }

                    }

                }
                int nextSerialNum = serialNum+1;
                //  String se = String.valueOf(serialNum);
                //String ser = String.format("%04d", serialNum);
                manifestidLike = manifestidLike+nextSerialNum;
                sr.close();
                st.close();

                if(despatchs.size()>0){
                    int qtySum = 0;
                    double weightSum = 0;
                    String hubscCode = despatchs.get(0).getDesp_hubsc();
                    for(int i=0;i<despatchs.size();i++){
                        qtySum+=despatchs.get(i).getQty();
                        weightSum+=despatchs.get(i).getWeight();
                    }
                    // weightSum = Math.round(weightSum);
                    DecimalFormat ft = new DecimalFormat("0.000");
                    String w = ft.format(weightSum);
                    String Query = "INSERT INTO manifest VALUES(?,?,?,current_timestamp,?,?,?,?,?,"+w+",?,?,?,?,?,?,?,?,?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(Query);
                    preparedStatement.setString(1,manifestidLike);
                    preparedStatement.setString(2,SourceHubSRC);
                    preparedStatement.setString(3,DestHubSRC);
                    Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    //Instant instant = ...;

                    Timestamp ts = new Timestamp(TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()));
                    //preparedStatement+"+05:30");
                    // preparedStatement.setTimestamp(4,ts,tzUTC);
                    preparedStatement.setInt(4,despatchs.size());
                    String manifest_transit = "S";
                    Manifest_Tranist mt = (Manifest_Tranist)manifestTransit.getSelectedItem();
                    if(mt != null){
                        manifest_transit = mt.getMt_Code();
                    }
                    String desp_type = "N";
                    Despatch_Type dt = (Despatch_Type)destType.getSelectedItem();
                    if(dt != null){
                        desp_type = dt.getType_Code();
                    }
                    preparedStatement.setString(5,manifest_transit);
                    String deviceName = getDeviceName();
                    if(deviceName.length()>25){
                        deviceName = deviceName.substring(0,25);
                    }
                    preparedStatement.setString(6,CommonUtil.UserName);
                    preparedStatement.setString(7,"Y");
                    preparedStatement.setInt(8,qtySum);


                    //preparedStatement.setDouble(9,weightSum);
                    preparedStatement.setString(9,null);
                    preparedStatement.setString(10,deviceName);
                    preparedStatement.setString(11,hubscCode);
                    Date today = new Date();
                    String todayString = String.format(prefData, today);
                    preparedStatement.setDate(12,new java.sql.Date(System.currentTimeMillis()));
                    preparedStatement.setString(13,despatchs.get(0).getDoc_type());
                    preparedStatement.setString(14,"Y");
                    preparedStatement.setString(15,"Y");
                    preparedStatement.setString(16,"N");
                    preparedStatement.setString(17,desp_type);
                    boolean added = preparedStatement.execute();
                    preparedStatement.close();
                    Statement statement = conn.createStatement();
                    String nextManifest = String.valueOf(nextSerialNum);
                    String newQuery = "UPDATE public.config SET config_value='"+nextSerialNum+"' WHERE CONFIG_NAME='LastManifest'";
                    statement.execute(newQuery);
                    statement.close();
                    //  showCustomDialog("Manifest inserted","Manifest ID: "+manifestidLike);
                }

                for(int i=0;i<despatchs.size();i++){
                    Despatchs d = despatchs.get(i);
                    DecimalFormat ft = new DecimalFormat("0.000");
                    String wt = ft.format(d.getWeight());
                    String query = "INSERT INTO despatch (despatch_id,desp_systime,desp_source,desp_dest,desp_user," +
                            "desp_manifest,awb_no,non_doc,server_sync,desp_date,qty,weight,doc_type,hub_sync,sc_sync,sync_hub,sync_sc," +
                            "sync_lock,next_step,desp_type,desp_system,desp_hubsc,src_hubsc,desp_local,org_hub,org_sc,acc_sec,del_no,country,share_amt,act_weight,new_leaf,ashare_amt,awb_cost,mba_cost) VALUES (default,current_timestamp,?,?,?,?,?,?,?,?,?,"+wt+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1,SourceHubSRC);

                    ps.setString(2,DestHubSRC);
                    String deviceName = getDeviceName();
                    if(deviceName.length()>25){
                        deviceName = deviceName.substring(0,25);
                    }
                    String usName = CommonUtil.UserName;
                    if(usName.length()>25){
                        usName = usName.substring(0,25);
                    }
                    ps.setString(3,usName);
                    ps.setString(4,manifestidLike);
                    ps.setString(5,d.getAwb_no());
                    ps.setBoolean(6,d.isNon_doc());
                    ps.setString(7,"Y");
                    Date today = new Date();
                    String todayString = String.format(prefData, today);
                    ps.setDate(8,new java.sql.Date(System.currentTimeMillis()));
                    ps.setInt(9,d.getQty());
                    //ps.setDouble(10,d.getWeight());
                    ps.setString(10,d.getDoc_type());
                    ps.setString(11,null);
                    ps.setString(12,"");
                    ps.setString(13,null);
                    ps.setString(14,null);
                    ps.setString(15,"N");
                    ps.setString(16,"Y");
                    String despType = d.getDesp_type();
                    if(despType.equals("A")){
                        boolean isvalid = isAcccount(d.getAwb_no(),DBName);
                        if(!isvalid){
                            despType = "N";
                        }
                    }
                    ps.setString(17,despType);
                    ps.setString(18,deviceName);
                    ps.setString(19,d.getDesp_hubsc());
                    ps.setString(20,d.getSrc_hubsc());
                    ps.setBoolean(21,false);
                    ps.setString(22,"");
                    ps.setString(23,"");
                    ps.setString(24,null);
                    ps.setString(25,null);
                    ps.setInt(26,0);
                    ps.setInt(27,0);
                    ps.setInt(28,0);
                    ps.setBoolean(29,false);
                    ps.setInt(30,0);
                    ps.setInt(31,0);
                    ps.setInt(32,0);
                    ps.execute();
                    ps.close();

                    //showCustomDialog(" Msg","Despatched..");
                }
                conn.close();
                error = manifestidLike;
            }  catch (Exception e) {
                //Handle errors for Class.forName
//                showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
                error = "Error: "+e.getMessage();

            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    error = "Error: "+se2.getMessage();
                    //showCustomDialog("Exception", se2.getMessage());
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    error = "Error: "+se.getMessage();
                   // showCustomDialog("Exception", se.getMessage());
                }
            }
            return error;
        }

        @Override
        protected String doInBackground(ArrayList... strings) {
            Boolean isAdded = false;
            String error ="";
            //end finally tr
            ArrayList<Despatchs> despatches = strings[0];
            if(srcRadioButton.isChecked()){
                Hubs selectedHub = (Hubs)hubSpinner.getSelectedItem();
                if(selectedHub != null){
                    if(selectedHub.getHub_code().equals(CommonUtil.selectedHub.getHub_code())){
                        String source = CommonUtil.selectedHub.getState_code()+CommonUtil.selectedHub.getHub_code();
                        String dest = GetDestStateAndHub();
                        ArrayList<Despatchs> stDespatches = new ArrayList<Despatchs>();
                        ArrayList<Despatchs> fenDespatches = new ArrayList<Despatchs>();
                        for(int i=0;i<despatches.size();i++){
                            Despatchs d = despatches.get(i);
                           String awb = despatches.get(i).getAwb_no();
                           String firstTwoDigit = awb.substring(0,2);
                           int checkawb = Integer.parseInt(firstTwoDigit);
                           if(checkawb>= 50 && checkawb<=95){
                               stDespatches.add(d);
                           }
                           else {
                               fenDespatches.add(d);
                           }
                        }
                        if(stDespatches.size()>0){
                            error= error+ " , "+AddManifest_Despatches(stDespatches,source,dest,STDBNAME);
                        }
                        if(fenDespatches.size()>0){
                            error = error+ " , "+AddManifest_Despatches(fenDespatches,source,dest,FENDBNAME);
                        }

                    }
                    else {
                        String source = CommonUtil.selectedHub.getState_code()+CommonUtil.selectedHub.getHub_code();
                        String dest = CommonUtil.selectedHub.getState_code()+selectedHub.getHub_code();
                        //ArrayList<Despatch> dsList = new ArrayList<Despatch>();
                        for(int i=0;i<despatches.size();i++){
                            despatches.get(i).setDesp_hubsc("H");
                        }
                        ArrayList<Despatchs> stDespatches = new ArrayList<Despatchs>();
                        ArrayList<Despatchs> fenDespatches = new ArrayList<Despatchs>();
                        for(int i=0;i<despatches.size();i++){
                            Despatchs d = despatches.get(i);
                            String awb = despatches.get(i).getAwb_no();
                            String firstTwoDigit = awb.substring(0,2);
                            int checkawb = Integer.parseInt(firstTwoDigit);
                            if(checkawb>= 50 && checkawb<=95){
                                stDespatches.add(d);
                            }
                            else {
                                fenDespatches.add(d);
                            }
                        }
                        if(stDespatches.size()>0){
                            error = error+ " , "+AddManifest_Despatches(stDespatches,source,dest,STDBNAME);
                        }
                        if(fenDespatches.size()>0) {
                            error = error+ " , "+AddManifest_Despatches(fenDespatches,source,dest,FENDBNAME);
                        }
                        source = CommonUtil.selectedHub.getState_code()+selectedHub.getHub_code();
                        dest = GetDestStateAndHub();
                        for(int i=0;i<despatches.size();i++){
                            despatches.get(i).setDesp_hubsc("S");
                        }
                        error = error+ " , "+AddDirrentStateHub(error,despatches,source,dest);
                    }
                }
            }
            else {
                String source = CommonUtil.selectedHub.getState_code()+CommonUtil.selectedHub.getHub_code();
                String dest = GetDestStateAndHub();
                ArrayList<Despatchs> stDespatches = new ArrayList<Despatchs>();
                ArrayList<Despatchs> fenDespatches = new ArrayList<Despatchs>();
                for(int i=0;i<despatches.size();i++){
                    Despatchs d = despatches.get(i);
                    String awb = despatches.get(i).getAwb_no();
                    String firstTwoDigit = awb.substring(0,2);
                    int checkawb = Integer.parseInt(firstTwoDigit);
                    if(checkawb>= 50 && checkawb<=95){
                        stDespatches.add(d);
                    }
                    else {
                        fenDespatches.add(d);
                    }
                }
                if(stDespatches.size()>0){
                    error = error+ " , "+AddManifest_Despatches(stDespatches,source,dest,STDBNAME);
                }
                if(fenDespatches.size()>0) {
                    error = error+ " , "+AddManifest_Despatches(fenDespatches,source,dest,FENDBNAME);
                }
            }
                return error;

        }
        public String AddDirrentStateHub(String err,ArrayList<Despatchs> despatches,String source,String dest){

            ArrayList<Despatchs> stDespatches = new ArrayList<Despatchs>();
            ArrayList<Despatchs> fenDespatches = new ArrayList<Despatchs>();
            for(int i=0;i<despatches.size();i++){
                Despatchs d = despatches.get(i);
                String awb = despatches.get(i).getAwb_no();
                String firstTwoDigit = awb.substring(0,2);
                int checkawb = Integer.parseInt(firstTwoDigit);
                if(checkawb>= 50 && checkawb<=95){
                    stDespatches.add(d);
                }
                else {
                    fenDespatches.add(d);
                }
            }
            String error = "";
            if(stDespatches.size()>0){
                error = AddManifest_Despatches(stDespatches,source,dest,STDBNAME);
            }
            if(fenDespatches.size()>0) {
                error = AddManifest_Despatches(fenDespatches,source,dest,FENDBNAME);
            }
            err = error +"\n"+err;
            return err;
        }
    }
    class LoadHubs extends AsyncTask<String,Void,ArrayList> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void  onPreExecute(){
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        public void onPostExecute(ArrayList result) {
            progressBar.cancel();
            if(result.size()>0){
                hubsArrayList = result;
                LoadHubSpinner(result);


            }
            else {
                showCustomDialog("Warnings","No States Found In Database.");
            }


        }


        @Override
        protected ArrayList doInBackground(String... strings) {
            ArrayList hubs = new ArrayList();
            Connection conn = null;
            Statement st = null;
            try {
                //STEP 2: Register JDBC driver
                Class.forName(JDBC_DRIVER);

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                if(PASS.isEmpty()){
                    conn = DriverManager.getConnection(DB_URL,USER,null);
                }
                else {
                    conn = DriverManager.getConnection(DB_URL,USER,PASS);
                }


                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                String statecode = strings[0];
                st = conn.createStatement();
                String sql;
                sql = "SELECT hub_code,hub_name,hub_state FROM OPN_HUBS WHERE HUB_STATE='"+statecode+"' order by hub_name";
                ResultSet rs = st.executeQuery(sql);

                //STEP 5: Extract data from result set
                while (rs.next()) {
                    //Retrieve by column name
                    Hubs hub = new Hubs();
                    String code = rs.getString("hub_code").trim();
                    String name = rs.getString("hub_name").trim();
                    String state = rs.getString("hub_state").trim();
                    hub.setHub_code(code);
                    hub.setHub_name(name);
                    hub.setState_code(state);
                    hubs.add(hub);
                }
                //STEP 6: Clean-up environment
                rs.close();
                st.close();
                conn.close();
            }  catch (Exception e) {
                //Handle errors for Class.forName
                showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    showCustomDialog("Exception",se2.getMessage());
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    showCustomDialog("Exception",se.getMessage());
                }//end finally try
                return hubs;
            }
        }
    }
    public void LoadSrcSpinner(ArrayList states){
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,states);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        srcSpinner.setAdapter(adapter);
    }
    class LoadSRC extends AsyncTask<String,Void,ArrayList> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void  onPreExecute(){
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        public void onPostExecute(ArrayList result) {
            progressBar.cancel();
            if(result.size()>0){
                srcList = result;
                LoadSrcSpinner(result);
            }
            else {
                showCustomDialog("Warnings","No States Found In Database.");
            }


        }


        @Override
        protected ArrayList doInBackground(String... strings) {
            ArrayList<Src> srcArrayList = new ArrayList<Src>();
            Connection conn = null;
            Statement st = null;
            try {
                //STEP 2: Register JDBC driver
                Class.forName(JDBC_DRIVER);

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                if(PASS.isEmpty()){
                    conn = DriverManager.getConnection(DB_URL,USER,null);
                }
                else {
                    conn = DriverManager.getConnection(DB_URL,USER,PASS);
                }


                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                st = conn.createStatement();
                String sql;
                String hubCode =strings[0];
                if(hubCode.isEmpty()){
                 hubCode = CommonUtil.LoggedInHUB;
                }
                sql = "SELECT  sc_code,sc_name FROM OPN_SCS where sc_hub='"+hubCode+"' ORDER BY sc_name";
                ResultSet rs = st.executeQuery(sql);

                //STEP 5: Extract data from result set
                while (rs.next()) {
                    //Retrieve by column name
                    Src states = new Src();
                    String code = rs.getString("sc_code").trim();
                    String name = rs.getString("sc_name").trim();
                    states.setSc_code(code);
                    states.setSc_name(name);
                    srcArrayList.add(states);
                }
                //STEP 6: Clean-up environment
                rs.close();
                st.close();
                conn.close();
            }  catch (Exception e) {
                //Handle errors for Class.forName
                showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    showCustomDialog("Exception",se2.getMessage());
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    showCustomDialog("Exception",se.getMessage());
                }//end finally try
                return srcArrayList;
            }
        }
    }

    class LoadStates extends AsyncTask<String,Void,ArrayList> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void  onPreExecute(){
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        public void onPostExecute(ArrayList result) {
            progressBar.cancel();
            if(result.size()>0){
                stateArrayList = result;
                LoadStateSpinner(result);
            }
            else {
                showCustomDialog("Warnings","No States Found In Database.");
            }


        }


        @Override
        protected ArrayList doInBackground(String... strings) {
           ArrayList<States> stList = new ArrayList<States>();
            Connection conn = null;
            Statement st = null;
            try {
                //STEP 2: Register JDBC driver
                Class.forName(JDBC_DRIVER);

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                if(PASS.isEmpty()){
                    conn = DriverManager.getConnection(DB_URL,USER,null);
                }
                else {
                    conn = DriverManager.getConnection(DB_URL,USER,PASS);
                }


                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                st = conn.createStatement();
                String sql;
                sql = "SELECT  state_code,state_name FROM OPN_STATES ORDER BY state_name";
                ResultSet rs = st.executeQuery(sql);

                //STEP 5: Extract data from result set
                while (rs.next()) {
                    //Retrieve by column name
                    States states = new States();
                    String code = rs.getString("state_code").trim();
                    String name = rs.getString("state_name").trim();
                    states.setState_code(code);
                    states.setState_name(name);
                    stList.add(states);
                }
                //STEP 6: Clean-up environment
                rs.close();
                st.close();
                conn.close();
            }  catch (Exception e) {
                //Handle errors for Class.forName
                showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    showCustomDialog("Exception",se2.getMessage());
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    showCustomDialog("Exception",se.getMessage());
                }//end finally try
                return stList;
            }
        }
    }

}
