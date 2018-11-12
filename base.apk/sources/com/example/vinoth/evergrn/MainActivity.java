package com.example.vinoth.evergrn;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    public static final String BTNAME = "BTNAME";
    public static final String IMEIVERIFIED = "IMEIVERIFIED";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLDB = "SQLDB";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME = "SQLUSERNAME";
    String IMEI;
    ArrayList<Integer> PickerList;
    ArrayList<Integer> RoomNoList;
    ArrayList<String> SuperVisorList;
    String blutName;
    ImageButton btnGetWeigh;
    Button btnReset;
    Button btnSave;
    ConnectionClass connectionClass;
    ConnectionClass_Cloud connectionClass_cloud;
    int counter;
    TextView dt;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    InputStream mmInputStream;
    OutputStream mmOutputStream;
    BluetoothSocket mmSocket;
    EditText netweight;
    TextView netwtst;
    Spinner pickerNoSpiner;
    Dialog progressBar;
    byte[] readBuffer;
    int readBufferPosition;
    Spinner roomNoSpiner;
    private MySharedPreferences sharedpreferences;
    String sqlDB;
    String sqlPassword;
    String sqlServer;
    String sqlUserName;
    volatile boolean stopWorker;
    Spinner supervisorSpiner;
    TelephonyManager telephonyManager;
    String weightFromBluetooth;
    EditText wet;
    Thread workerThread;

    public class CheckIMEI extends AsyncTask<String, String, String> {
        String IMEI_Number = MainActivity.this.IMEI;
        Boolean isSuccess = Boolean.valueOf(null);
        String z = "";

        protected void onPreExecute() {
            MainActivity.this.progressBar.show();
        }

        protected void onPostExecute(String str) {
            MainActivity.this.progressBar.cancel();
            if (this.isSuccess.booleanValue()) {
                MainActivity.this.sharedpreferences.putString(MainActivity.IMEIVERIFIED, "Y");
                MainActivity.this.sharedpreferences.commit();
                return;
            }
            MainActivity.this.showCustomPopup("Error", str, 0);
        }

        protected java.lang.String doInBackground(java.lang.String... r5) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r4 = this;
            r0 = 0;
            r5 = r5[r0];	 Catch:{ Exception -> 0x0060 }
            r1 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ Exception -> 0x0060 }
            r1 = r1.connectionClass_cloud;	 Catch:{ Exception -> 0x0060 }
            r1 = r1.CONN();	 Catch:{ Exception -> 0x0060 }
            if (r1 != 0) goto L_0x0012;	 Catch:{ Exception -> 0x0060 }
        L_0x000d:
            r5 = "Error in connection with SQL server";	 Catch:{ Exception -> 0x0060 }
            r4.z = r5;	 Catch:{ Exception -> 0x0060 }
            goto L_0x006a;	 Catch:{ Exception -> 0x0060 }
        L_0x0012:
            r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0060 }
            r2.<init>();	 Catch:{ Exception -> 0x0060 }
            r3 = "SELECT * FROM IMEI_DETAILS WHERE IMEI_NO='";	 Catch:{ Exception -> 0x0060 }
            r2.append(r3);	 Catch:{ Exception -> 0x0060 }
            r2.append(r5);	 Catch:{ Exception -> 0x0060 }
            r5 = "'";	 Catch:{ Exception -> 0x0060 }
            r2.append(r5);	 Catch:{ Exception -> 0x0060 }
            r5 = r2.toString();	 Catch:{ Exception -> 0x0060 }
            r1 = r1.createStatement();	 Catch:{ Exception -> 0x0060 }
            r5 = r1.executeQuery(r5);	 Catch:{ Exception -> 0x0060 }
            r1 = r5.next();	 Catch:{ Exception -> 0x0060 }
            if (r1 == 0) goto L_0x0055;	 Catch:{ Exception -> 0x0060 }
        L_0x0036:
            r1 = "ISACTIVE";	 Catch:{ Exception -> 0x0060 }
            r5 = r5.getString(r1);	 Catch:{ Exception -> 0x0060 }
            r5 = r5.toUpperCase();	 Catch:{ Exception -> 0x0060 }
            r1 = "Y";	 Catch:{ Exception -> 0x0060 }
            r5 = r5.equals(r1);	 Catch:{ Exception -> 0x0060 }
            if (r5 == 0) goto L_0x0050;	 Catch:{ Exception -> 0x0060 }
        L_0x0048:
            r5 = 1;	 Catch:{ Exception -> 0x0060 }
            r5 = java.lang.Boolean.valueOf(r5);	 Catch:{ Exception -> 0x0060 }
            r4.isSuccess = r5;	 Catch:{ Exception -> 0x0060 }
            goto L_0x006a;	 Catch:{ Exception -> 0x0060 }
        L_0x0050:
            r5 = "Your Device Has been Deactivated. Please Contact Administrators";	 Catch:{ Exception -> 0x0060 }
            r4.z = r5;	 Catch:{ Exception -> 0x0060 }
            goto L_0x006a;	 Catch:{ Exception -> 0x0060 }
        L_0x0055:
            r5 = "Your Device is Not Activated. Please Contact Administrators";	 Catch:{ Exception -> 0x0060 }
            r4.z = r5;	 Catch:{ Exception -> 0x0060 }
            r5 = java.lang.Boolean.valueOf(r0);	 Catch:{ Exception -> 0x0060 }
            r4.isSuccess = r5;	 Catch:{ Exception -> 0x0060 }
            goto L_0x006a;
        L_0x0060:
            r5 = java.lang.Boolean.valueOf(r0);
            r4.isSuccess = r5;
            r5 = "Exceptions";
            r4.z = r5;
        L_0x006a:
            r5 = r4.z;
            return r5;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.example.vinoth.evergrn.MainActivity.CheckIMEI.doInBackground(java.lang.String[]):java.lang.String");
        }
    }

    public class LoadDropDownData extends AsyncTask<String, String, String> {
        ArrayList<Integer> pkList = new ArrayList();
        ArrayList<Integer> rmList = new ArrayList();
        ArrayList<String> spList = new ArrayList();

        protected void onPreExecute() {
            MainActivity.this.progressBar.show();
        }

        protected void onPostExecute(String str) {
            MainActivity.this.progressBar.cancel();
            if (str.startsWith("ERROR")) {
                MainActivity.this.showCustomDialog("Exception", str);
                return;
            }
            MainActivity.this.SuperVisorList = this.spList;
            MainActivity.this.RoomNoList = this.rmList;
            MainActivity.this.PickerList = this.pkList;
            MainActivity.this.LoadSpiners();
        }

        protected String doInBackground(String... strArr) {
            try {
                strArr = strArr[0];
                strArr = MainActivity.this.connectionClass.CONN(MainActivity.this.sqlServer, MainActivity.this.sqlDB, MainActivity.this.sqlUserName, MainActivity.this.sqlPassword);
                if (strArr == null) {
                    return "Error in connection with SQL server";
                }
                Statement createStatement = strArr.createStatement();
                ResultSet executeQuery = createStatement.executeQuery("SELECT SUPERVISOR FROM SUPERVISORS");
                while (executeQuery.next()) {
                    this.spList.add(executeQuery.getString("SUPERVISOR"));
                }
                executeQuery.close();
                createStatement.close();
                createStatement = strArr.createStatement();
                executeQuery = createStatement.executeQuery("SELECT ROOM_NO FROM ROOMS");
                while (executeQuery.next()) {
                    this.rmList.add(Integer.valueOf(executeQuery.getInt("ROOM_NO")));
                }
                executeQuery.close();
                createStatement.close();
                strArr = strArr.createStatement().executeQuery("SELECT PICKER_NO FROM PICKERS");
                while (strArr.next()) {
                    this.pkList.add(Integer.valueOf(strArr.getInt("PICKER_NO")));
                }
                return "SUCCESS";
            } catch (String[] strArr2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ERROR:");
                stringBuilder.append(strArr2.getMessage());
                return stringBuilder.toString();
            }
        }
    }

    public class SaveEntry extends AsyncTask<Integer, String, String> {
        Integer pickerno;
        Integer roomno;
        String supervisor;
        String wt;

        protected void onPreExecute() {
            this.roomno = (Integer) MainActivity.this.roomNoSpiner.getSelectedItem();
            this.pickerno = (Integer) MainActivity.this.pickerNoSpiner.getSelectedItem();
            this.supervisor = MainActivity.this.supervisorSpiner.getSelectedItem().toString();
            this.wt = MainActivity.this.netweight.getText().toString();
            MainActivity.this.progressBar.show();
        }

        protected void onPostExecute(String str) {
            MainActivity.this.progressBar.cancel();
            if (str.startsWith("ERROR")) {
                MainActivity.this.showCustomDialog("Error", str);
                return;
            }
            MainActivity.this.showCustomDialog("EVERNGRN", "Picker Entry Added Successfully.");
            MainActivity.this.pickerNoSpiner.setSelection(0);
        }

        protected java.lang.String doInBackground(java.lang.Integer... r6) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r5 = this;
            r6 = 0;
            r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0097 }
            r0 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ Exception -> 0x0097 }
            r0 = r0.connectionClass;	 Catch:{ Exception -> 0x0097 }
            r1 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ Exception -> 0x0097 }
            r1 = r1.sqlServer;	 Catch:{ Exception -> 0x0097 }
            r2 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ Exception -> 0x0097 }
            r2 = r2.sqlDB;	 Catch:{ Exception -> 0x0097 }
            r3 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ Exception -> 0x0097 }
            r3 = r3.sqlUserName;	 Catch:{ Exception -> 0x0097 }
            r4 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ Exception -> 0x0097 }
            r4 = r4.sqlPassword;	 Catch:{ Exception -> 0x0097 }
            r0 = r0.CONN(r1, r2, r3, r4);	 Catch:{ Exception -> 0x0097 }
            if (r0 != 0) goto L_0x0023;	 Catch:{ Exception -> 0x0097 }
        L_0x001f:
            r6 = "ERROR: Error in connection with SQL server";	 Catch:{ Exception -> 0x0097 }
            goto L_0x00ad;
        L_0x0023:
            r1 = 1;
            r2 = "SELECT MAX(ENTRY_ID) AS ID FROM PICKER_ENTRIES";	 Catch:{ Exception -> 0x004e }
            r3 = r0.createStatement();	 Catch:{ Exception -> 0x004e }
            r2 = r3.executeQuery(r2);	 Catch:{ Exception -> 0x004e }
            r4 = r2.next();	 Catch:{ Exception -> 0x004e }
            if (r4 == 0) goto L_0x0048;	 Catch:{ Exception -> 0x004e }
        L_0x0034:
            r4 = "ID";	 Catch:{ Exception -> 0x004e }
            r4 = r2.getInt(r4);	 Catch:{ Exception -> 0x004e }
            r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x004e }
            r4 = r4.intValue();	 Catch:{ Exception -> 0x004e }
            r4 = r4 + r1;	 Catch:{ Exception -> 0x004e }
            r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x004e }
            r6 = r4;	 Catch:{ Exception -> 0x004e }
        L_0x0048:
            r2.close();	 Catch:{ Exception -> 0x004e }
            r3.close();	 Catch:{ Exception -> 0x004e }
        L_0x004e:
            r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0097 }
            r2.<init>();	 Catch:{ Exception -> 0x0097 }
            r3 = "INSERT INTO PICKER_ENTRIES VALUES (?,?,?,";	 Catch:{ Exception -> 0x0097 }
            r2.append(r3);	 Catch:{ Exception -> 0x0097 }
            r3 = r5.wt;	 Catch:{ Exception -> 0x0097 }
            r2.append(r3);	 Catch:{ Exception -> 0x0097 }
            r3 = ",GETDATE(),?)";	 Catch:{ Exception -> 0x0097 }
            r2.append(r3);	 Catch:{ Exception -> 0x0097 }
            r2 = r2.toString();	 Catch:{ Exception -> 0x0097 }
            r2 = r0.prepareStatement(r2);	 Catch:{ Exception -> 0x0097 }
            r6 = r6.intValue();	 Catch:{ Exception -> 0x0097 }
            r2.setInt(r1, r6);	 Catch:{ Exception -> 0x0097 }
            r6 = 2;	 Catch:{ Exception -> 0x0097 }
            r1 = r5.pickerno;	 Catch:{ Exception -> 0x0097 }
            r1 = r1.intValue();	 Catch:{ Exception -> 0x0097 }
            r2.setInt(r6, r1);	 Catch:{ Exception -> 0x0097 }
            r6 = 3;	 Catch:{ Exception -> 0x0097 }
            r1 = r5.roomno;	 Catch:{ Exception -> 0x0097 }
            r1 = r1.intValue();	 Catch:{ Exception -> 0x0097 }
            r2.setInt(r6, r1);	 Catch:{ Exception -> 0x0097 }
            r6 = 4;	 Catch:{ Exception -> 0x0097 }
            r1 = r5.supervisor;	 Catch:{ Exception -> 0x0097 }
            r2.setString(r6, r1);	 Catch:{ Exception -> 0x0097 }
            r2.execute();	 Catch:{ Exception -> 0x0097 }
            r2.close();	 Catch:{ Exception -> 0x0097 }
            r0.close();	 Catch:{ Exception -> 0x0097 }
            r6 = "SUCCESS";	 Catch:{ Exception -> 0x0097 }
            goto L_0x00ad;
        L_0x0097:
            r6 = move-exception;
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r1 = "ERROR:";
            r0.append(r1);
            r6 = r6.getMessage();
            r0.append(r6);
            r6 = r0.toString();
        L_0x00ad:
            return r6;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.example.vinoth.evergrn.MainActivity.SaveEntry.doInBackground(java.lang.Integer[]):java.lang.String");
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);
        this.progressBar = new Dialog(this);
        this.progressBar.setContentView(R.layout.custom_progress_dialog);
        this.progressBar.setTitle("Loading");
        this.connectionClass = new ConnectionClass();
        this.connectionClass_cloud = new ConnectionClass_Cloud();
        this.dt = (TextView) findViewById(R.id.dt);
        this.dt.setText(new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date()));
        this.netwtst = (TextView) findViewById(R.id.netwtstring);
        this.netweight = (EditText) findViewById(R.id.netweight);
        this.btnGetWeigh = (ImageButton) findViewById(R.id.btnGetWeight);
        this.btnSave = (Button) findViewById(R.id.btnSave);
        this.btnReset = (Button) findViewById(R.id.btnReset);
        this.btnGetWeigh.setOnClickListener(this);
        this.btnReset.setOnClickListener(this);
        this.btnSave.setOnClickListener(this);
        this.supervisorSpiner = (Spinner) findViewById(R.id.supervisorDropDwn);
        this.roomNoSpiner = (Spinner) findViewById(R.id.roomnoDropDwn);
        this.pickerNoSpiner = (Spinner) findViewById(R.id.pickerDropDwn);
        this.wet = (EditText) findViewById(R.id.weight);
        this.SuperVisorList = new ArrayList();
        this.RoomNoList = new ArrayList();
        this.PickerList = new ArrayList();
        this.sharedpreferences = MySharedPreferences.getInstance(this, "MyPrefs");
        if (this.sharedpreferences.getString(IMEIVERIFIED, "").equals("Y") == null) {
            this.IMEI = "";
            try {
                this.telephonyManager = (TelephonyManager) getSystemService("phone");
                if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != null) {
                    showCustomDialog("Warning", "Enable Permission to Check IMEI Number");
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
                } else {
                    this.IMEI = this.telephonyManager.getDeviceId();
                }
                if (this.IMEI.isEmpty() != null) {
                    showCustomPopup("Error", "Permission issue to Read IMEI Details.", 0);
                    return;
                }
                new CheckIMEI().execute(new String[]{this.IMEI});
            } catch (Bundle bundle2) {
                showCustomDialog("Exception", bundle2.getMessage());
            }
        } else {
            CheckSQLSettings();
        }
    }

    public void StartSettingsActivity() {
        startActivity(new Intent(this, Settings.class));
    }

    public void CheckSQLSettings() {
        this.sqlServer = this.sharedpreferences.getString("SQLSERVER", "");
        this.sqlUserName = this.sharedpreferences.getString("SQLUSERNAME", "");
        this.sqlPassword = this.sharedpreferences.getString("SQLPASSWORD", "");
        this.sqlDB = this.sharedpreferences.getString("SQLDB", "");
        this.blutName = this.sharedpreferences.getString("BTNAME", "");
        try {
            if (findBT()) {
                openBT();
            }
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ERROR: ");
            stringBuilder.append(e.getMessage());
            Toast.makeText(this, stringBuilder.toString(), 1);
        }
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        if (!(this.sqlServer.isEmpty() || this.sqlUserName.isEmpty() || this.sqlDB.isEmpty())) {
            if (!this.sqlPassword.isEmpty()) {
                new LoadDropDownData().execute(new String[]{""});
                return;
            }
        }
        StartSettingsActivity();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(android.view.MenuItem r5) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r4 = this;
        r0 = r5.getItemId();
        r1 = 2131165190; // 0x7f070006 float:1.794459E38 double:1.052935506E-314;
        r2 = 1;
        if (r0 == r1) goto L_0x0043;
    L_0x000a:
        r1 = 2131165243; // 0x7f07003b float:1.7944698E38 double:1.052935532E-314;
        r3 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        if (r0 == r1) goto L_0x0029;
    L_0x0011:
        r1 = 2131165250; // 0x7f070042 float:1.7944712E38 double:1.0529355356E-314;
        if (r0 == r1) goto L_0x001b;
    L_0x0016:
        r5 = super.onOptionsItemSelected(r5);
        return r5;
    L_0x001b:
        r5 = new android.content.Intent;
        r0 = com.example.vinoth.evergrn.MainActivity.class;
        r5.<init>(r4, r0);
        r5.setFlags(r3);
        r4.startActivity(r5);
        return r2;
    L_0x0029:
        r5 = new android.content.Intent;
        r0 = r4.getApplicationContext();
        r1 = com.example.vinoth.evergrn.MainActivity.class;
        r5.<init>(r0, r1);
        r5.setFlags(r3);
        r0 = "EXIT";
        r5.putExtra(r0, r2);
        r4.closeBT();	 Catch:{ Exception -> 0x003f }
    L_0x003f:
        r4.startActivity(r5);
        return r2;
    L_0x0043:
        r5 = new android.content.Intent;
        r0 = com.example.vinoth.evergrn.Settings.class;
        r5.<init>(r4, r0);
        r4.startActivity(r5);
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.vinoth.evergrn.MainActivity.onOptionsItemSelected(android.view.MenuItem):boolean");
    }

    public void showCustomDialog(String str, String str2) {
        Builder builder = new Builder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.custom_dialog, null));
        builder.setTitle(str);
        str = new StringBuilder();
        str.append("\n");
        str.append(str2);
        builder.setMessage(str.toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    public void showCustomPopup(String str, String str2, int i) {
        i = new Builder(this);
        i.setView(getLayoutInflater().inflate(R.layout.custom_dialog, null));
        i.setTitle(str);
        str = new StringBuilder();
        str.append("\n");
        str.append(str2);
        i.setMessage(str.toString());
        i.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(android.content.DialogInterface r1, int r2) {
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                /*
                r0 = this;
                r1 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ Exception -> 0x0005 }
                r1.closeBT();	 Catch:{ Exception -> 0x0005 }
            L_0x0005:
                r1 = com.example.vinoth.evergrn.MainActivity.this;
                r1.finish();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.example.vinoth.evergrn.MainActivity.2.onClick(android.content.DialogInterface, int):void");
            }
        });
        i.create().show();
    }

    public void LoadSpiners() {
        this.progressBar.show();
        SpinnerAdapter arrayAdapter = new ArrayAdapter(this, 17367048, this.SuperVisorList);
        arrayAdapter.setDropDownViewResource(17367049);
        this.supervisorSpiner.setAdapter(arrayAdapter);
        arrayAdapter = new ArrayAdapter(this, 17367048, this.RoomNoList);
        arrayAdapter.setDropDownViewResource(17367049);
        this.roomNoSpiner.setAdapter(arrayAdapter);
        arrayAdapter = new ArrayAdapter(this, 17367048, this.PickerList);
        arrayAdapter.setDropDownViewResource(17367049);
        this.pickerNoSpiner.setAdapter(arrayAdapter);
        this.progressBar.cancel();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGetWeight:
                this.wet.setText(this.weightFromBluetooth);
                view = Float.valueOf(Float.parseFloat(this.weightFromBluetooth));
                Float valueOf = Float.valueOf(view.floatValue() - 1.8f);
                TextView textView = this.netwtst;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(view.toString());
                stringBuilder.append("-1.800 = ");
                textView.setText(stringBuilder.toString());
                view = new DecimalFormat("0.000");
                view.setMaximumFractionDigits(3);
                this.netweight.setText(view.format(valueOf));
                return;
            case R.id.btnReset:
                this.supervisorSpiner.setSelection(0);
                this.roomNoSpiner.setSelection(0);
                this.pickerNoSpiner.setSelection(0);
                this.wet.setText("0.000");
                return;
            case R.id.btnSave:
                new SaveEntry().execute(new Integer[]{Integer.valueOf(0)});
                return;
            default:
                return;
        }
    }

    boolean findBT() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            showCustomDialog("Message", "Device Doesn't Supports Bluetooth");
            this.progressBar.cancel();
        } else {
            if (!this.mBluetoothAdapter.isEnabled()) {
                showCustomDialog("Message", "Go to Settings and Enable Bluetooth");
                this.progressBar.cancel();
            }
            Set<BluetoothDevice> bondedDevices = this.mBluetoothAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                String str = "HC";
                if (!this.blutName.isEmpty()) {
                    str = this.blutName;
                }
                for (BluetoothDevice bluetoothDevice : bondedDevices) {
                    if (bluetoothDevice.getName().startsWith(str)) {
                        this.mmDevice = bluetoothDevice;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void openBT() throws IOException {
        this.progressBar.show();
        UUID fromString = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        if (this.mmSocket == null) {
            this.mmSocket = this.mmDevice.createRfcommSocketToServiceRecord(fromString);
            this.mmSocket.connect();
            this.mmOutputStream = this.mmSocket.getOutputStream();
            this.mmInputStream = this.mmSocket.getInputStream();
            this.progressBar.cancel();
            beginListenForData();
        } else if (this.mmSocket.isConnected()) {
            showCustomDialog("Message", "Connected to Bluetooth Device");
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        this.stopWorker = false;
        this.readBufferPosition = 0;
        this.readBuffer = new byte[1024];
        this.workerThread = new Thread(new Runnable() {
            public void run() {
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                /*
                r9 = this;
            L_0x0000:
                r0 = java.lang.Thread.currentThread();
                r0 = r0.isInterrupted();
                if (r0 != 0) goto L_0x0068;
            L_0x000a:
                r0 = com.example.vinoth.evergrn.MainActivity.this;
                r0 = r0.stopWorker;
                if (r0 != 0) goto L_0x0068;
            L_0x0010:
                r0 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ IOException -> 0x0062 }
                r0 = r0.mmInputStream;	 Catch:{ IOException -> 0x0062 }
                r0 = r0.available();	 Catch:{ IOException -> 0x0062 }
                if (r0 <= 0) goto L_0x0000;	 Catch:{ IOException -> 0x0062 }
            L_0x001a:
                r1 = new byte[r0];	 Catch:{ IOException -> 0x0062 }
                r2 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ IOException -> 0x0062 }
                r2 = r2.mmInputStream;	 Catch:{ IOException -> 0x0062 }
                r2.read(r1);	 Catch:{ IOException -> 0x0062 }
                r2 = 0;	 Catch:{ IOException -> 0x0062 }
                r3 = 0;	 Catch:{ IOException -> 0x0062 }
            L_0x0025:
                if (r3 >= r0) goto L_0x0000;	 Catch:{ IOException -> 0x0062 }
            L_0x0027:
                r4 = r1[r3];	 Catch:{ IOException -> 0x0062 }
                r5 = 10;	 Catch:{ IOException -> 0x0062 }
                if (r4 != r5) goto L_0x0051;	 Catch:{ IOException -> 0x0062 }
            L_0x002d:
                r4 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ IOException -> 0x0062 }
                r4 = r4.readBufferPosition;	 Catch:{ IOException -> 0x0062 }
                r4 = new byte[r4];	 Catch:{ IOException -> 0x0062 }
                r5 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ IOException -> 0x0062 }
                r5 = r5.readBuffer;	 Catch:{ IOException -> 0x0062 }
                r6 = r4.length;	 Catch:{ IOException -> 0x0062 }
                java.lang.System.arraycopy(r5, r2, r4, r2, r6);	 Catch:{ IOException -> 0x0062 }
                r5 = new java.lang.String;	 Catch:{ IOException -> 0x0062 }
                r6 = "US-ASCII";	 Catch:{ IOException -> 0x0062 }
                r5.<init>(r4, r6);	 Catch:{ IOException -> 0x0062 }
                r4 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ IOException -> 0x0062 }
                r4.readBufferPosition = r2;	 Catch:{ IOException -> 0x0062 }
                r4 = r0;	 Catch:{ IOException -> 0x0062 }
                r6 = new com.example.vinoth.evergrn.MainActivity$3$1;	 Catch:{ IOException -> 0x0062 }
                r6.<init>(r5);	 Catch:{ IOException -> 0x0062 }
                r4.post(r6);	 Catch:{ IOException -> 0x0062 }
                goto L_0x005f;	 Catch:{ IOException -> 0x0062 }
            L_0x0051:
                r5 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ IOException -> 0x0062 }
                r5 = r5.readBuffer;	 Catch:{ IOException -> 0x0062 }
                r6 = com.example.vinoth.evergrn.MainActivity.this;	 Catch:{ IOException -> 0x0062 }
                r7 = r6.readBufferPosition;	 Catch:{ IOException -> 0x0062 }
                r8 = r7 + 1;	 Catch:{ IOException -> 0x0062 }
                r6.readBufferPosition = r8;	 Catch:{ IOException -> 0x0062 }
                r5[r7] = r4;	 Catch:{ IOException -> 0x0062 }
            L_0x005f:
                r3 = r3 + 1;
                goto L_0x0025;
            L_0x0062:
                r0 = com.example.vinoth.evergrn.MainActivity.this;
                r1 = 1;
                r0.stopWorker = r1;
                goto L_0x0000;
            L_0x0068:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.example.vinoth.evergrn.MainActivity.3.run():void");
            }
        });
        this.workerThread.start();
    }

    void closeBT() throws IOException {
        this.stopWorker = true;
        this.mmOutputStream.close();
        this.mmInputStream.close();
        this.mmSocket.close();
    }
}
