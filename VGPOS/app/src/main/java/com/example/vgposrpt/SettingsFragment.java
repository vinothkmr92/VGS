package com.example.vgposrpt;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    EditText host;
    EditText dbname;
    Button testConnection;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME= "SQLUSERNAME";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLDB = "SQLDB";
    public static final String PRINTER = "PRINTER";
    public static final String PRINTER_KOT = "PRINTER";
    public static final String BRANCH = "BRANCH";
    public static final String BRANCHES = "BRANCHES";
    public static final String PRINTOPTION = "PRINTOPTION";
    public static final String PRINTOPTION_KOT = "PRINTOPTIONKOT";
    public static final String RECEIPTSIZE = "RECEIPTSIZE";
    public static final String PRINTER_IP = "PRINTER_IP";
    public static final String PRINTER_IP_KOT = "PRINTER_IP_KOT";
    public static final String HEADER = "HEADER";
    public static final String FOOTER = "FOOTER";
    public static final String ADDRESS = "ADDRESS";
    public static final String INCLUDE_MRP = "INCLUDE_MRP";
    public static final String MULTI_LANG = "MULTI_LANG";
    public static final String USBDEVICENAME = "USBDEVICE";
    public static final String USBDEVICENAME_KOT = "USBDEVICE_KOT";
    public static final String ENABLEKOT = "ENABLEKOT";
    public static final String ISMOBILE = "ISMOBILE";
    ArrayList<String> bluethootnamelist;
    ArrayList<String> bluethootnamelistKot;
    ArrayList<String> usbDeviceList;
    ArrayList<String> usbDeviceListKot;
    ArrayList<Branch> branchArrayList;
    Set<BluetoothDevice> pairedDevices = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static String[] PERMISSIONS_BLUETOOTH = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    TextInputLayout usbprinter;
    AutoCompleteTextView usbprinterselection;
    TextInputLayout usbprinterkot;
    AutoCompleteTextView usbprinterselectionkot;
    TextInputLayout printer;
    AutoCompleteTextView printerselection;
    TextInputLayout printerkot;
    AutoCompleteTextView printerselectionkot;
    TextInputLayout branchCd;
    AutoCompleteTextView branches;
    MaterialRadioButton printerOptionNone;
    MaterialRadioButton printerOptionWifi;
    MaterialRadioButton printerOptionBluetooth;
    MaterialRadioButton printerOptionUsb;
    MaterialRadioButton printerOptionUsbkot;
    EditText printerIP;
    MaterialRadioButton printerOptionNoneKot;
    MaterialRadioButton printerOptionWifiKot;
    MaterialRadioButton printerOptionBluetoothKot;
    EditText printerIPKot;
    MaterialRadioButton rpt2Inch;
    MaterialRadioButton rpt3Inch;
    TextInputLayout printeriplayout;
    TextInputLayout printeriplayoutkot;
    MaterialCardView rptSizeLayout;
    TextView rptSizeTextView;
    LinearLayout headerfooterLayout;
    EditText header;
    EditText address;
    EditText footer;
    LinearLayout includemrpLayout;
    SwitchMaterial includeMRP;
    MaterialCardView printerSettingsView;
    SwitchMaterial multiLang;
    SwitchMaterial enableKot;
    MaterialCardView kotprinteroptions;
    SwitchMaterial isMobile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        try{
            host = view.findViewById(R.id.host);
            dbname= view.findViewById(R.id.dbname);
            branchCd = view.findViewById(R.id.defaultBranch);
            branches = view.findViewById(R.id.branches);
            printerIP = view.findViewById(R.id.printerip);
            printerIPKot = view.findViewById(R.id.printeripkot);
            testConnection = view.findViewById(R.id.testButton);
            printer = view.findViewById(R.id.printer);
            printerkot = view.findViewById(R.id.printerkot);
            printerselection = view.findViewById(R.id.printers);
            printerselectionkot = view.findViewById(R.id.printerskot);
            usbprinter = view.findViewById(R.id.usbprinter);
            usbprinterkot = view.findViewById(R.id.usbprinterkot);
            usbprinterselection = view.findViewById(R.id.usbprinters);
            usbprinterselectionkot = view.findViewById(R.id.usbprinterskot);
            printerOptionNone = view.findViewById(R.id.radioBtnNoPrinter);
            printerOptionNoneKot = view.findViewById(R.id.radioBtnNoPrinterKot);
            printerOptionWifi = view.findViewById(R.id.radioBtnWifiPrinter);
            printerOptionWifiKot = view.findViewById(R.id.radioBtnWifiPrinterKot);
            printerOptionBluetooth = view.findViewById(R.id.radioBtnBlueToothPrinter);
            printerOptionBluetoothKot = view.findViewById(R.id.radioBtnBlueToothPrinterKot);
            printerOptionUsb = view.findViewById(R.id.radioBtnUSBPrinter);
            printerOptionUsbkot = view.findViewById(R.id.radioBtnUSBPrinterKot);
            kotprinteroptions = view.findViewById(R.id.kotprintoptions);
            rpt2Inch = view.findViewById(R.id.radioBtnrptSize2Inch);
            rpt3Inch = view.findViewById(R.id.radioBtnrptSize3Inch);
            printeriplayout = view.findViewById(R.id.wifiprinterlayout);
            printeriplayoutkot = view.findViewById(R.id.wifiprinterlayoutKot);
            rptSizeLayout = view.findViewById(R.id.rptsizeLayout);
            rptSizeTextView = view.findViewById(R.id.rptsizeTxtView);
            headerfooterLayout = view.findViewById(R.id.headerfooterLayout);
            header = view.findViewById(R.id.header);
            address = view.findViewById(R.id.address);
            footer = view.findViewById(R.id.footer);
            includemrpLayout = view.findViewById(R.id.mrpLayout);
            includeMRP = view.findViewById(R.id.includeMRP);
            multiLang = view.findViewById(R.id.multilangswitch);
            enableKot = view.findViewById(R.id.enableKot);
            printerSettingsView = view.findViewById(R.id.printerSettingsCard);
            isMobile = view.findViewById(R.id.isMobile);
            sharedpreferences = MySharedPreferences.getInstance(getContext(),MyPREFERENCES);
            String sqlserver = getContext().getApplicationContext().getString(R.string.SQL_SERVER);
            String dbnamestr = getContext().getApplicationContext().getString(R.string.SQL_DBNAME);
            String hostname = sharedpreferences.getString(SQLSERVER,sqlserver);
            String Databasename = sharedpreferences.getString(SQLDB,dbnamestr);
            enableKot.setChecked(sharedpreferences.getString(ENABLEKOT,"N").equalsIgnoreCase("Y"));
            kotprinteroptions.setVisibility(enableKot.isChecked()?View.VISIBLE:View.GONE);
            Integer defBranch = sharedpreferences.getInt(BRANCH,1);
            ArrayList<Branch> bransettings = getBranchList();
            branchArrayList = bransettings;
            branchCd.getEditText().setText(GetBranchName(defBranch));
            host.setText(hostname);
            dbname.setText(Databasename);
            testConnection.setOnClickListener(this);
            String bluetothName = sharedpreferences.getString(PRINTER,"");
            printer.getEditText().setText(bluetothName);
            String blutoothkot = sharedpreferences.getString(PRINTER_KOT,"");
            printerkot.getEditText().setText(blutoothkot);
            String usbName = sharedpreferences.getString(USBDEVICENAME,"");
            usbprinter.getEditText().setText(usbName);
            String usbNameKot = sharedpreferences.getString(USBDEVICENAME_KOT,"");
            usbprinterkot.getEditText().setText(usbNameKot);
            ArrayAdapter branchadapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,branchArrayList);
            branchadapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
            branches.setAdapter(branchadapter);
            printerIP.setText(sharedpreferences.getString(PRINTER_IP,""));
            printerIPKot.setText(sharedpreferences.getString(PRINTER_IP_KOT,""));
            header.setText(sharedpreferences.getString(HEADER,""));
            footer.setText(sharedpreferences.getString(FOOTER,"Thank you visit again."));
            address.setText(sharedpreferences.getString(ADDRESS,""));
            isMobile.setChecked(sharedpreferences.getString(ISMOBILE,"Y").equalsIgnoreCase("Y"));
            includeMRP.setChecked(sharedpreferences.getString(INCLUDE_MRP,"N").equalsIgnoreCase("Y"));
            multiLang.setChecked(sharedpreferences.getString(MULTI_LANG,"Y").equalsIgnoreCase("Y"));
            enableKot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    kotprinteroptions.setVisibility(isChecked?View.VISIBLE:View.GONE);
                }
            });
            printerOptionNone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        printerOptionWifi.setChecked(false);
                        printerOptionBluetooth.setChecked(false);
                        printerOptionUsb.setChecked(false);
                        usbprinter.setVisibility(View.GONE);
                        printerIP.setVisibility(View.GONE);
                        printer.setVisibility(View.GONE);
                        printeriplayout.setVisibility(View.GONE);
                        rptSizeLayout.setVisibility(View.GONE);
                        rptSizeTextView.setVisibility(View.GONE);
                        headerfooterLayout.setVisibility(View.GONE);
                        printerSettingsView.setVisibility(View.GONE);
                    }
                }
            });
            printerOptionNoneKot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        printerOptionWifiKot.setChecked(false);
                        printerOptionBluetoothKot.setChecked(false);
                        printerOptionUsbkot.setChecked(false);
                        usbprinterkot.setVisibility(View.GONE);
                        printerIPKot.setVisibility(View.GONE);
                        printerkot.setVisibility(View.GONE);
                        printeriplayoutkot.setVisibility(View.GONE);
                        rptSizeLayout.setVisibility(View.GONE);
                        rptSizeTextView.setVisibility(View.GONE);
                        headerfooterLayout.setVisibility(View.GONE);
                        printerSettingsView.setVisibility(View.GONE);
                    }
                }
            });

            printerOptionWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        printerOptionNone.setChecked(false);
                        printerOptionBluetooth.setChecked(false);
                        printerOptionUsb.setChecked(false);
                        usbprinter.setVisibility(View.GONE);
                        rptSizeLayout.setVisibility(View.VISIBLE);
                        rptSizeTextView.setVisibility(View.VISIBLE);
                        printer.setVisibility(View.GONE);
                        headerfooterLayout.setVisibility(View.VISIBLE);
                        printerSettingsView.setVisibility(View.VISIBLE);
                    }
                    printerIP.setVisibility(isChecked ? View.VISIBLE:View.GONE);
                    printeriplayout.setVisibility(isChecked?View.VISIBLE:View.GONE);

                }
            });
            printerOptionWifiKot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        printerOptionNoneKot.setChecked(false);
                        printerOptionBluetoothKot.setChecked(false);
                        printerOptionUsbkot.setChecked(false);
                        usbprinterkot.setVisibility(View.GONE);
                        rptSizeLayout.setVisibility(View.VISIBLE);
                        rptSizeTextView.setVisibility(View.VISIBLE);
                        printerkot.setVisibility(View.GONE);
                        headerfooterLayout.setVisibility(View.VISIBLE);
                        printerSettingsView.setVisibility(View.VISIBLE);
                    }
                    printerIPKot.setVisibility(isChecked ? View.VISIBLE:View.GONE);
                    printeriplayoutkot.setVisibility(isChecked?View.VISIBLE:View.GONE);

                }
            });
            printerOptionBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        printerOptionWifi.setChecked(false);
                        printerOptionNone.setChecked(false);
                        printerOptionUsb.setChecked(false);
                        usbprinter.setVisibility(View.GONE);
                        rptSizeLayout.setVisibility(View.VISIBLE);
                        rptSizeTextView.setVisibility(View.VISIBLE);
                        printerIP.setVisibility(View.GONE);
                        printeriplayout.setVisibility(View.GONE);
                        headerfooterLayout.setVisibility(View.VISIBLE);
                        printerSettingsView.setVisibility(View.VISIBLE);
                    }
                    printer.setVisibility(isChecked ? View.VISIBLE:View.GONE);
                }
            });
            printerOptionBluetoothKot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        printerOptionWifiKot.setChecked(false);
                        printerOptionNoneKot.setChecked(false);
                        printerOptionUsbkot.setChecked(false);
                        usbprinterkot.setVisibility(View.GONE);
                        rptSizeLayout.setVisibility(View.VISIBLE);
                        rptSizeTextView.setVisibility(View.VISIBLE);
                        printerIPKot.setVisibility(View.GONE);
                        printeriplayoutkot.setVisibility(View.GONE);
                        headerfooterLayout.setVisibility(View.VISIBLE);
                        printerSettingsView.setVisibility(View.VISIBLE);
                    }
                    printerkot.setVisibility(isChecked ? View.VISIBLE:View.GONE);
                }
            });
            printerOptionUsb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        printerOptionWifi.setChecked(false);
                        printerOptionNone.setChecked(false);
                        printerOptionBluetooth.setChecked(false);
                        printer.setVisibility(View.GONE);
                        rptSizeLayout.setVisibility(View.VISIBLE);
                        rptSizeTextView.setVisibility(View.VISIBLE);
                        printerIP.setVisibility(View.GONE);
                        printeriplayout.setVisibility(View.GONE);
                        headerfooterLayout.setVisibility(View.VISIBLE);
                        printerSettingsView.setVisibility(View.VISIBLE);
                    }
                    usbprinter.setVisibility(isChecked ? View.VISIBLE:View.GONE);
                }
            });
            printerOptionUsbkot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        printerOptionWifiKot.setChecked(false);
                        printerOptionNoneKot.setChecked(false);
                        printerOptionBluetoothKot.setChecked(false);
                        printerkot.setVisibility(View.GONE);
                        rptSizeLayout.setVisibility(View.VISIBLE);
                        rptSizeTextView.setVisibility(View.VISIBLE);
                        printerIPKot.setVisibility(View.GONE);
                        printeriplayoutkot.setVisibility(View.GONE);
                        headerfooterLayout.setVisibility(View.VISIBLE);
                        printerSettingsView.setVisibility(View.VISIBLE);
                    }
                    usbprinterkot.setVisibility(isChecked ? View.VISIBLE:View.GONE);
                }
            });
            rpt2Inch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rpt3Inch.setChecked(!isChecked);
                    includemrpLayout.setVisibility(!isChecked?View.VISIBLE:View.GONE);
                }
            });
            rpt3Inch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rpt2Inch.setChecked(!isChecked);
                    includemrpLayout.setVisibility(isChecked?View.VISIBLE:View.GONE);
                }
            });
            includemrpLayout.setVisibility(rpt3Inch.isChecked()?View.VISIBLE:View.GONE);
            String printerOption  = sharedpreferences.getString(PRINTOPTION,"None");
            switch (printerOption){
                case "None":
                    printerOptionNone.setChecked(true);
                    break;
                case  "WiFi":
                    printerOptionWifi.setChecked(true);
                    break;
                case "Bluetooth":
                    printerOptionBluetooth.setChecked(true);
                    break;
                case "USB":
                    printerOptionUsb.setChecked(true);
                    break;
            }
            String printerOptionKot  = sharedpreferences.getString(PRINTOPTION_KOT,"None");
            switch (printerOptionKot){
                case "None":
                    printerOptionNoneKot.setChecked(true);
                    break;
                case  "WiFi":
                    printerOptionWifiKot.setChecked(true);
                    break;
                case "Bluetooth":
                    printerOptionBluetoothKot.setChecked(true);
                    break;
                case "USB":
                    printerOptionUsbkot.setChecked(true);
                    break;
            }
            headerfooterLayout.setVisibility(printerOptionNone.isChecked()?View.GONE:View.VISIBLE);
            printerSettingsView.setVisibility((printerOptionNone.isChecked() || printerOptionNoneKot.isChecked())?View.GONE:View.VISIBLE);
            rptSizeLayout.setVisibility((printerOptionNone.isChecked() || printerOptionNoneKot.isChecked())?View.GONE:View.VISIBLE);
            printerIP.setVisibility(printerOptionWifi.isChecked() ? View.VISIBLE:View.GONE);
            printerIPKot.setVisibility(printerOptionWifiKot.isChecked()?View.VISIBLE:View.GONE);
            printer.setVisibility(printerOptionBluetooth.isChecked() ? View.VISIBLE:View.GONE);
            printerkot.setVisibility(printerOptionBluetoothKot.isChecked()?View.VISIBLE:View.GONE);
            printeriplayout.setVisibility(printerIP.getVisibility());
            printeriplayoutkot.setVisibility(printerIPKot.getVisibility());
            usbprinter.setVisibility(printerOptionUsb.isChecked()?View.VISIBLE:View.GONE);
            usbprinterkot.setVisibility(printerOptionUsbkot.isChecked()?View.VISIBLE:View.GONE);
            String reciptSize = sharedpreferences.getString(RECEIPTSIZE,"2");
            switch (reciptSize){
                case "2":
                    rpt2Inch.setChecked(true);
                    break;
                case "3":
                    rpt3Inch.setChecked(true);
                    break;
            }

            try{
                /*if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
                    ActivityResultLauncher<String> permisionlauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),isGranted->{
                        if(isGranted){
                            //
                        }
                    });
                    permisionlauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                }*/
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return TODO;
                    //return true;
                }
                pairedDevices = mBluetoothAdapter.getBondedDevices();
                bluethootnamelist = new ArrayList<>();
                bluethootnamelistKot = new ArrayList<>();
                for (BluetoothDevice device : pairedDevices) {
                    bluethootnamelist.add(device.getName());
                    bluethootnamelistKot.add(device.getName());
                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,bluethootnamelist);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                printerselection.setAdapter(adapter);
                ArrayAdapter adapterkot = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,bluethootnamelistKot);
                adapterkot.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                printerselectionkot.setAdapter(adapterkot);
                String usbDevice = sharedpreferences.getString(USBDEVICENAME,"");
                String usbDevicekot = sharedpreferences.getString(USBDEVICENAME_KOT,"");
                usbDeviceList = GetUSBDevice();
                usbDeviceListKot = GetUSBDevice();
                ArrayAdapter usbAdaptor = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,usbDeviceList);
                usbAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                usbprinterselection.setAdapter(usbAdaptor);
                ArrayAdapter usbAdaptorKot = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,usbDeviceListKot);
                usbAdaptorKot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                usbprinterselectionkot.setAdapter(usbAdaptorKot);
            }
            catch (Exception ex){
                showCustomDialog("Error in Bluetooth Permission",ex.getMessage(),false);
            }
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage(),true);
        }
        return view;
    }
    public ArrayList<String> GetUSBDevice(){
        ArrayList<String> deviceList = new ArrayList<>();
        UsbManager usbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> mDeviceIterator = mDeviceList.values().iterator();
        UsbDevice mDevice = null;
        while (mDeviceIterator.hasNext()) {
            mDevice = mDeviceIterator.next();
            if(mDevice!=null){
                String devicename = mDevice.getManufacturerName()+"~"+mDevice.getProductName();
                deviceList.add(devicename);
            }
        }
        return deviceList;
    }
    private ArrayList<Branch> getBranchList(){
        ArrayList<Branch> arrayItems = new ArrayList<>();
        String serializedObject = sharedpreferences.getString(BRANCHES, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Branch>>(){}.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return  arrayItems;
    }
    private String GetBranchName (Integer code){
        String branchName = "";
        for (Branch b:branchArrayList) {
            if(b.getBranch_Code()==code){
                branchName = b.getBranch_Name();
                break;
            }
        }
        return branchName;
    }
    private Integer GetBranchCode (String branchName){
        Integer code = 1;
        for (Branch b:branchArrayList) {
            if(b.getBranch_Name() == branchName){
                code = b.getBranch_Code();
                break;
            }
        }
        return code;
    }
    private void checkPermission(){

    }
    public  void  GoLogin(){
        Intent page = new Intent(getContext(),LoginActivity.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    public void showCustomDialog(String title,String Message,Boolean close) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(getContext());
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(close){
                    GoLogin();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    public  void  SaveSettings(){
        String hostname = host.getText().toString();
        String printer = printerselection.getText().toString();
        String printerkot = printerselectionkot.getText().toString();
        String usbprinter = usbprinterselection.getText().toString();
        String usbprinterkot = usbprinterselectionkot.getText().toString();
        String brName = branches.getText().toString();
        Integer brCode = GetBranchCode(brName);
        String SQLUser = getContext().getApplicationContext().getString(R.string.SQL_USERNAME);
        String SQLPassword = getContext().getApplicationContext().getString(R.string.SQL_PASSWORD);
        String printerOption = "";
        if(printerOptionNone.isChecked()){
            printerOption = "None";
        }
        else if(printerOptionWifi.isChecked()){
            printerOption = "WiFi";
        }
        else  if(printerOptionBluetooth.isChecked()){
            printerOption = "Bluetooth";
        }
        else if(printerOptionUsb.isChecked()){
            printerOption = "USB";
        }
        String printerOptionKot = "";
        if(printerOptionNoneKot.isChecked()){
            printerOptionKot = "None";
        }
        else if(printerOptionWifiKot.isChecked()){
            printerOptionKot = "WiFi";
        }
        else  if(printerOptionBluetoothKot.isChecked()){
            printerOptionKot = "Bluetooth";
        }
        else if(printerOptionUsbkot.isChecked()){
            printerOptionKot = "USB";
        }
        String receiptSize = "";
        if(rpt3Inch.isChecked()){
            receiptSize = "3";
        }
        else if(rpt2Inch.isChecked()){
            receiptSize = "2";
        }
        String printerip = printerIP.getText().toString();
        String printeripkot = printerIPKot.getText().toString();
        String headerMsg = header.getText().toString();
        String addressMsg = address.getText().toString();
        String footerMsg = footer.getText().toString();

        if(hostname.isEmpty()){
            hostname = getContext().getApplicationContext().getString(R.string.SQL_SERVER);
        }
        String Databasename = dbname.getText().toString();
        if(hostname.isEmpty()  || Databasename.isEmpty() ){
            showCustomDialog("Warning","Host  / Dbname should not be Empty.",false);
        }
        else{
            sharedpreferences.putString(SQLSERVER,hostname);
            sharedpreferences.putString(SQLUSERNAME,SQLUser);
            sharedpreferences.putString(SQLPASSWORD,SQLPassword);
            sharedpreferences.putString(SQLDB,Databasename);
            sharedpreferences.putString(PRINTER,printer);
            sharedpreferences.putString(PRINTER_KOT,printerkot);
            sharedpreferences.putString(USBDEVICENAME,usbprinter);
            sharedpreferences.putString(USBDEVICENAME_KOT,usbprinterkot);
            sharedpreferences.putInt(BRANCH,brCode);
            sharedpreferences.putString(PRINTER_IP,printerip);
            sharedpreferences.putString(PRINTER_IP_KOT,printeripkot);
            sharedpreferences.putString(PRINTOPTION,printerOption);
            sharedpreferences.putString(PRINTOPTION_KOT,printerOptionKot);
            sharedpreferences.putString(ENABLEKOT,enableKot.isChecked()?"Y":"N");
            sharedpreferences.putString(RECEIPTSIZE,receiptSize);
            sharedpreferences.putString(HEADER,headerMsg);
            sharedpreferences.putString(ADDRESS,addressMsg);
            sharedpreferences.putString(FOOTER,footerMsg);
            sharedpreferences.putString(INCLUDE_MRP,includeMRP.isChecked()?"Y":"N");
            sharedpreferences.putString(MULTI_LANG,multiLang.isChecked()?"Y":"N");
            sharedpreferences.putString(ISMOBILE,isMobile.isChecked()?"Y":"N");
            CommonUtil.printer = printer;
            sharedpreferences.commit();
            showCustomDialog("Saved","Settings Saved Successfully",true);
        }
    }

    @Override
    public void onClick(View v) {
        String hostname = host.getText().toString();
        if(hostname.isEmpty()){
            hostname = getContext().getApplicationContext().getString(R.string.SQL_SERVER);
        }
        String SQLUser = getContext().getApplicationContext().getString(R.string.SQL_USERNAME);
        String SQLPassword = getContext().getApplicationContext().getString(R.string.SQL_PASSWORD);
        String Databasename = dbname.getText().toString();
        new CheckDBConnection().execute(hostname,Databasename,SQLUser,SQLPassword);
    }

    class CheckDBConnection extends AsyncTask<String,Void,String> {

        //  ArrayList hubList = new ArrayList();
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);
        @Override
        public void  onPreExecute(){
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Testing SQL Host...");
            dialog.show();
            super.onPreExecute();
        }
        public  boolean isHostAvailable(final String host, final int port) {
            try (final Socket socket = new Socket()) {
                final InetAddress inetAddress = InetAddress.getByName(host);
                final InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);
                socket.connect(inetSocketAddress, 5000);
                return true;
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        public void onPostExecute(String result) {
            if(dialog.isShowing()){
                dialog.hide();
            }
            if(!result.startsWith("ERROR")){
                Toast.makeText(getContext(),result,Toast.LENGTH_LONG).show();
                SaveSettings();
            }
            else {
                result = result.replace("ERROR","");
                showCustomDialog("Error",result,false);
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            String status = "";
            Connection conn = null;
            Statement st = null;
            try {
                String[] hostwithport = strings[0].split(":");
                if(hostwithport.length<1){
                    status = "ERROR:Please configure valid SQL Server Host.";
                }
                else if(!isHostAvailable(hostwithport[0],Integer.parseInt(hostwithport[1]))){
                    status = "ERROR:"+strings[0]+" host is unreachable. Please try again Later.";
                }
                else {
                    ConnectionClass connectionClass = new ConnectionClass(strings[0],strings[1],strings[2],strings[3]);
                    Connection con = connectionClass.CONN();
                    if(null != con) {
                        status = "DB Connection Successful.";
                    }
                    else {
                        status = "Database Connection Failed.";
                    }
                }

            }  catch (Exception e) {
                //Handle errors for Class.forName
                //showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
                status = "ERROR: "+e.getMessage();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    //showCustomDialog("Exception",se2.getMessage());
                    status = "ERROR: "+se2.getMessage();
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    //showCustomDialog("Exception",se.getMessage());
                    status = "ERROR: "+se.getMessage();
                }//end finally try
                return status;
            }
        }
    }
}