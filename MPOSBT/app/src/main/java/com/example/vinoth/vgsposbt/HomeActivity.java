package com.example.vinoth.vgsposbt;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.NumberFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, AddCustomerDialog.CustomerDialogListener {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String HEADERMSG = "HEADERMSG";
    public static final String FOOTERMSG = "FOOTERMSG";
    public static final String PRINTERIP = "PRINTERIP";
    public static final String BLUETOOTNAME = "BLUETOOTHNAME";
    public static final String PRINTTYPE = "WIFI";
    public static final String IS3INCH = "IS3INCH";

    public static final String PRINTKOT = "PRINTKOT";
    public static final String KOTPRINTERIP = "KOTPRINTERIP";
    public static final String BILLCOPIES = "BILLCOPIES";
    public static final String ADDRESSLINE = "ADDRESSLINE";
    public static final String INCLUDEMRP = "INCLUDEMRP";
    public static final String MULTILANG = "MULTILANG";
    public static final String USBDEVICENAME = "USBDEVICE";
    public static final String BTSCALENAME = "BTSCALENAME";
    public static final String DEVICEUSERNAME = "DEVICEUSERNAME";
    public static final String DEFBINWT = "DEFBINWT";
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    public static HomeActivity instance;
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN
    };
    public EditText priceTxt;
    DatabaseHelper dbHelper;
    String headerMsg;
    String footerMsg;
    String printerip;
    String bluetothName;
    boolean isWifiPrint;
    TextView searchTxtView;
    Dialog dialog;
    Dialog itemSearchdialog;
    ArrayList<String> waiters;

    AlertDialog.Builder builder;
    private Dialog progressBar;
    private EditText itemNo;
    private EditText itemName;
    private EditText Quantity;
    private TextView tQty;
    private TextView tItem;
    private TextView estAmt;
    private TextView billnoTxtView;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;
    private Button btn9;
    private Button btn0;
    private Button btnClear;
    private Button btnDot;
    private Button btnSpace;
    private Button btnMenu;
    private Button btnCancel;
    private Button btnPrint;
    private Button btnEnter;
    private ImageButton btnAddCustomer;
    private EditText deductionperEditText;
    private EditText deductionWtEditText;
    private TextView netwtText;
    private MySharedPreferences sharedpreferences;
    Double grossQty;

    public static HomeActivity getInstance() {
        return instance;
    }

    GmsBarcodeScanner scanner;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    String blutName;
    ProgressDialog progressDialog;
    String weightFrmScale = "";
    Switch binRedSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressBar = new Dialog(HomeActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        grossQty = 0d;
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        try {
            dbHelper = new DatabaseHelper(this);
            itemNo = (EditText) findViewById(R.id.itemNo);
            itemName = (EditText) findViewById(R.id.itemName);
            Quantity = (EditText) findViewById(R.id.qty);
            tQty = (TextView) findViewById(R.id.ttQty);
            deductionperEditText = findViewById(R.id.deductionPer);
            deductionWtEditText = findViewById(R.id.deductionWt);
            netwtText = findViewById(R.id.netWt);
            tItem = (TextView) findViewById(R.id.ttItem);
            estAmt = (TextView) findViewById(R.id.estimateAmt);
            priceTxt = (EditText) findViewById(R.id.itemPrice);
            searchTxtView = (TextView) findViewById(R.id.customerInfoTxtView);
            billnoTxtView = (TextView) findViewById(R.id.billnoTxt);
            binRedSwitch = findViewById(R.id.reduceBin);
            int newbillno = dbHelper.GetNextBillNo();
            billnoTxtView.setText(String.valueOf(newbillno));
            btn1 = (Button) findViewById(R.id._1);
            btn2 = (Button) findViewById(R.id._2);
            btn3 = (Button) findViewById(R.id._3);
            btn4 = (Button) findViewById(R.id._4);
            btn5 = (Button) findViewById(R.id._5);
            btn6 = (Button) findViewById(R.id._6);
            btn7 = (Button) findViewById(R.id._7);
            btn8 = (Button) findViewById(R.id._8);
            btn9 = (Button) findViewById(R.id._9);
            btn0 = (Button) findViewById(R.id._0);
            btnClear = (Button) findViewById(R.id._clr);
            btnDot = (Button) findViewById(R.id._dot);
            btnSpace = (Button) findViewById(R.id.viewitems);
            btnMenu = (Button) findViewById(R.id.menu);
            btnCancel = (Button) findViewById(R.id.cancel);
            btnPrint = (Button) findViewById(R.id.print);
            btnEnter = (Button) findViewById(R.id.enter);
            btnAddCustomer = (ImageButton) findViewById(R.id.btnAddMember);
            btnAddCustomer.setOnClickListener(this);
            btn1.setOnClickListener(this);
            btn2.setOnClickListener(this);
            btn3.setOnClickListener(this);
            btn4.setOnClickListener(this);
            btn5.setOnClickListener(this);
            btn6.setOnClickListener(this);
            btn7.setOnClickListener(this);
            btn8.setOnClickListener(this);
            btn9.setOnClickListener(this);
            btn0.setOnClickListener(this);
            btnClear.setOnClickListener(this);
            btnDot.setOnClickListener(this);
            btnSpace.setOnClickListener(this);
            btnMenu.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnPrint.setOnClickListener(this);
            btnEnter.setOnClickListener(this);
            sharedpreferences = MySharedPreferences.getInstance(this, MyPREFERENCES);
            headerMsg = sharedpreferences.getString(HEADERMSG, "");
            waiters = new ArrayList<String>();
            waiters.add("NONE");
            ArrayList<Customer> customers = dbHelper.GetCustomers();
            for (int i = 0; i < customers.size(); i++) {
                waiters.add(customers.get(i).getMobileNumber());
            }
            itemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        priceTxt.requestFocus();
                        return true;
                    }
                    return false;
                }
            });
            priceTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String txt = priceTxt.getText().toString();
                    if (hasFocus) {
                        closeKeyboard();
                        if (hasFocus && !txt.isEmpty()) {
                            ((EditText) v).selectAll();
                        }
                    }
                }
            });
            itemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        showKeyboard();
                    } else {
                        closeKeyboard();
                    }
                }
            });
            deductionWtEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    String txt = deductionWtEditText.getText().toString();
                    if (hasFocus && !txt.isEmpty()) {
                        ((EditText) v).selectAll();
                    }
                }
            });
            deductionperEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    String txt = deductionperEditText.getText().toString();
                    if (hasFocus && !txt.isEmpty()) {
                        ((EditText) v).selectAll();
                    }
                }
            });
            if(waiters.size()>0){
                searchTxtView.setText(waiters.get(0));
            }
            searchTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Initialize dialog
                    dialog = new Dialog(HomeActivity.this);

                    // set custom dialog
                    dialog.setContentView(R.layout.dialog_searchable_spinner);

                    // set custom height and width
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

                    // set transparent background
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    // show dialog
                    dialog.show();

                    // Initialize and assign variable
                    EditText editText = dialog.findViewById(R.id.edit_text);
                    ListView listView = dialog.findViewById(R.id.list_view);

                    // Initialize array adapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1, waiters);

                    // set adapter
                    listView.setAdapter(adapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // when item selected from list
                            // set selected item on textView
                            searchTxtView.setText(adapter.getItem(position));

                            // Dismiss dialog
                            dialog.dismiss();
                            itemNo.requestFocus();

                        }
                    });
                }
            });
            if (QuantityListener.itemsCarts != null) {
                tItem.setText(String.valueOf(QuantityListener.itemsCarts.size()));
                double ttq = 0;
                for (int i = 0; i < QuantityListener.itemsCarts.size(); i++) {
                    ItemsCart ic = QuantityListener.itemsCarts.get(i);
                    ttq = ttq + ic.getQty();
                }
                tQty.setText(String.valueOf(ttq));
                LoadTotalAmt();
            }
            byte[] ic = dbHelper.GetReceiptIcon();
            if (ic != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(ic, 0, ic.length);
                Common.shopLogo = bitmap;
            }
            footerMsg = sharedpreferences.getString(FOOTERMSG, "");
            printerip = sharedpreferences.getString(PRINTERIP, "");
            String printkot = sharedpreferences.getString(PRINTKOT, "NO");
            String kotprinterip = sharedpreferences.getString(KOTPRINTERIP, "");
            String billcopies = sharedpreferences.getString(BILLCOPIES, "1");
            String addressline = sharedpreferences.getString(ADDRESSLINE, "");
            String includeMRP = sharedpreferences.getString(INCLUDEMRP, "NO");
            String isMultilang = sharedpreferences.getString(MULTILANG, "NO");
            Common.MultiLang = isMultilang.equalsIgnoreCase("YES");
            Common.printKOT = printkot.equalsIgnoreCase("YES");
            Common.includeMRPinReceipt = includeMRP.equalsIgnoreCase("YES");
            Common.kotprinterIP = kotprinterip;
            Common.billcopies = Integer.parseInt(billcopies);
            Common.addressline = addressline;
            Common.printerIP = printerip;
            Common.headerMeg = headerMsg;
            Common.footerMsg = footerMsg;
            String defbin = sharedpreferences.getString(DEFBINWT, "0");
            String scalename = sharedpreferences.getString(BTSCALENAME, "");
            String devName = sharedpreferences.getString(DEVICEUSERNAME, "");
            blutName = scalename;
            Common.scalebtName = scalename;
            Common.defBinWt = Double.valueOf(defbin);
            Common.devUser = devName;
            bluetothName = sharedpreferences.getString(BLUETOOTNAME, "");
            Common.bluetoothDeviceName = bluetothName;
            Common.usbDeviceName = sharedpreferences.getString(USBDEVICENAME, "");
            String RptSize = sharedpreferences.getString(IS3INCH, "3");
            Common.RptSize = RptSize;
            String printType = sharedpreferences.getString(PRINTTYPE, "WIFI");
            Common.printType = printType;
            if (!checkBLuetoothPermission()) {
                ActivityCompat.requestPermissions(HomeActivity.this, PERMISSIONS_BLUETOOTH
                        , 1);
            }
            instance = this;
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Print Bill");
            builder.setMessage("Do you want to print bill ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    SaveAndPrintBill(true);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    SaveAndPrintBill(false);
                }
            });
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_CODE_128)
                    .build();
            scanner = GmsBarcodeScanning.getClient(HomeActivity.this, options);
            builder.setCancelable(true);
            itemNo.requestFocus();
            if(!blutName.isEmpty()){
                if (findBT()) {
                    openBT();
                }
            }
        } catch (Exception ex) {
            showCustomDialog("Error", ex.getMessage());
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }



    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    public void RefreshBillNo(){
        int newbillno = dbHelper.GetNextBillNo();
        billnoTxtView.setText(String.valueOf(newbillno));
    }
    private void ScanQRCode() {
        if (scanner != null) {
            scanner.startScan().addOnSuccessListener(
                            barcode -> {
                                String rawValue = barcode.getRawValue();
                                itemNo.requestFocus();
                                itemNo.setText(rawValue);
                                LoadProductNameandPrice();
                            })
                    .addOnCanceledListener(
                            () -> {
                                Toast.makeText(HomeActivity.this, "Scanning was cancelled", Toast.LENGTH_SHORT).show();
                            })
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(HomeActivity.this, "Failed To Scan. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
        }
    }

    private void SaveAndPrintBill(boolean print) {
        int billno = SaveDetails();
        double discountAmt = 0;
        ReceiptData receiptData = new ReceiptData();
        receiptData.billno = billno;
        receiptData.discount = discountAmt;
        receiptData.billDate = new Date();
        receiptData.waiter = searchTxtView.getText().toString();
        Customer cus = dbHelper.GetCustomer(receiptData.waiter);
        receiptData.selectedCustomer = cus;
        receiptData.itemsCarts = Common.itemsCarts;
        if (print) {
            Print(receiptData);
        } else {
            RefreshViews();
        }
    }

    private double GetBillAmt() {
        double totalAmt = 0;
        for (int i = 0; i < Common.itemsCarts.size(); i++) {
            ItemsCart itemsCart = Common.itemsCarts.get(i);
            double amt = itemsCart.getQty() * itemsCart.getPrice();
            totalAmt += amt;
        }
        return totalAmt;
    }


    private void CalculateDiscountPer() {
        if (grossQty > 0) {
            double discountAmt = Double.valueOf(deductionWtEditText.getText().toString());
            double discountper = (discountAmt / grossQty) * 100;
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            double finalBillValue = grossQty - discountAmt;
            deductionperEditText.setText(decimalFormat.format(discountper));
            netwtText.setText(decimalFormat.format(finalBillValue));
        }
    }

    private void CalculateDiscountAmt() {
        if (grossQty > 0) {
            double discountper = Double.valueOf(deductionperEditText.getText().toString());
            double discountAmt = grossQty * (discountper / 100);
            double finalBillValue = grossQty - discountAmt;
            DecimalFormat formater = new DecimalFormat("#.###");
            deductionWtEditText.setText(formater.format(discountAmt));
            netwtText.setText(formater.format(finalBillValue));
        }
    }

    private void OpenItemSearchDialog() {
        itemSearchdialog = new Dialog(HomeActivity.this);

        // set custom dialog
        itemSearchdialog.setContentView(R.layout.dialog_items_search);

        // set custom height and width
        itemSearchdialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

        // set transparent background
        itemSearchdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // show dialog
        itemSearchdialog.show();

        // Initialize and assign variable
        EditText editText = itemSearchdialog.findViewById(R.id.edit_textItem);
        ListView listView = itemSearchdialog.findViewById(R.id.list_viewItem);

        // Initialize array adapter
        ArrayList<String> itemnames = dbHelper.GetItemNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeActivity.this, R.layout.products_list_view, R.id.list_content, itemnames);

        // set adapter
        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // when item selected from list
                // set selected item on textView
                String selectredName = adapter.getItem(position);
                // Dismiss dialog
                itemSearchdialog.dismiss();
                Item item = dbHelper.GetItem(selectredName);
                if (item != null) {
                    itemNo.setText(String.valueOf(item.getItem_No()));
                    itemName.setText(item.getItem_Name());
                    DecimalFormat formater = new DecimalFormat("0.000");
                    String price = formater.format(item.getPrice());
                    Double tr = 0d;
                    try {
                        tr = Double.valueOf(Quantity.getText().toString());

                    } catch (Exception ex) {
                        //showCustomDialog("EXCEPTION",ex.getMessage());
                        Toast.makeText(HomeActivity.this, "ERROR: " + ex.getMessage(), Toast.LENGTH_LONG);
                    }
                    if(tr<=0){
                        Toast.makeText(HomeActivity.this, "Unable to read Weight. Please contact support.", Toast.LENGTH_LONG);
                    }
                    priceTxt.setText(price);
                    deductionWtEditText.selectAll();
                    deductionWtEditText.requestFocus();
                }
            }
        });
    }

    private boolean checkBLuetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int bluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
            return bluetooth == PackageManager.PERMISSION_GRANTED;
        } else {
            int bluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
            return bluetooth == PackageManager.PERMISSION_GRANTED;
        }
    }



    public boolean Is_InternetWorking() {
        boolean connected = false;
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_VPN).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            } else
                connected = false;
        } catch (Exception ex) {
            connected = false;
        }
        return connected;
    }

    private void Print(ReceiptData receiptData) {
        try {
            if (QuantityListener.itemsCarts == null || QuantityListener.itemsCarts.size() == 0) {
                showCustomDialog("Warning", "Please add Items");
                return;
            }
            try {
                if (!isWifiPrint) {
                    if (!checkBLuetoothPermission()) {
                        ActivityCompat.requestPermissions(HomeActivity.getInstance(), PERMISSIONS_BLUETOOTH
                                , 1);
                    }
                }
                PrinterUtil printerUtil = new PrinterUtil(HomeActivity.this, this, true);
                printerUtil.receiptData = receiptData;
                printerUtil.Print();
            } catch (Exception ex) {
                showCustomDialog("Exception", ex.getMessage().toString());
                CancelBt();
            }
        } catch (Exception ex) {
            showCustomDialog("Exception", ex.getMessage().toString());
            CancelBt();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        try {
            closeBT();
        } catch (IOException e) {
            Toast.makeText(this,"Error:"+e.getMessage(), Toast.LENGTH_SHORT);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        try {
            //closeBT();
        } catch (Exception ex)
        {
            Toast.makeText(this,"Error:"+ex.getMessage(), Toast.LENGTH_SHORT);
        }
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.uploadExcel:
                Intent dcpage = new Intent(this, UploadActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return true;
            case R.id.itemMaster:
                Intent itemmaster = new Intent(this, ItemsMasterActivity.class);
                itemmaster.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(itemmaster);
                return true;

            case R.id.settings:
                Common.openSettings = false;
                Intent settingsPage = new Intent(this, Settings.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return true;
            case R.id.saleReort:
                Intent saleReportPage = new Intent(this, SaleReportActivity.class);
                saleReportPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(saleReportPage);
                return true;
            case R.id.homemenu:
                Intent page = new Intent(this, HomeActivity.class);
                page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(page);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        TextView titleview = new TextView(this);
        titleview.setText(title);
        titleview.setBackgroundColor(Color.WHITE);
        titleview.setPadding(10, 10, 10, 10);
        titleview.setGravity(Gravity.CENTER);
        titleview.setTextColor(Color.BLACK);
        titleview.setTypeface(titleview.getTypeface(), Typeface.BOLD_ITALIC);
        titleview.setTextSize(20);
        dialogBuilder.setCustomTitle(titleview);
        dialogBuilder.setMessage("\n" + Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }

    private int GetItematIndex(ArrayList<ItemsCart> items, Integer itemno) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            ItemsCart itemsCart = items.get(i);
            if (itemsCart.getItem_No() == itemno) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void SortItemsCarts() {
        ArrayList<ItemsCart> items = new ArrayList<>();
        for (int i = 0; i < Common.itemsCarts.size(); i++) {
            ItemsCart item = Common.itemsCarts.get(i);
            int index = GetItematIndex(items, item.getItem_No());
            if (index > -1) {
                ItemsCart existingitem = items.get(index);
                if (existingitem.getPrice() == item.getPrice()) {
                    double qty = item.getQty();
                    qty += existingitem.getQty();
                    item.setQty(qty);
                    items.remove(index);
                }
            }
            items.add(item);
        }
        Common.itemsCarts = items;
    }

    private boolean checkBlueToothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int bluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
            int scan = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        } else {
            int bluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
            int scan = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void LoadProductNameandPrice() {
        try{
            progressDialog.show();
            if (itemNo.isFocused()) {
                String itemnostr = itemNo.getText().toString();
                if (!itemnostr.isEmpty()) {
                    Item item = dbHelper.GetItem(itemnostr);
                    if (item != null) {
                        itemName.setText(item.getItem_Name());
                        DecimalFormat formater = new DecimalFormat("0.000");
                        String price = formater.format(item.getPrice());
                        //String price =String.format("%.0f", isAcPrice.isChecked()?item.getAcPrice():item.getPrice());
                        Double tr = 0d;
                        try {
                            tr = Double.valueOf(Quantity.getText().toString());
                        } catch (Exception ex) {
                            //showCustomDialog("EXCEPTION",ex.getMessage());
                            Toast.makeText(this, "ERROR: " + ex.getMessage(), Toast.LENGTH_SHORT);
                        }
                        if(tr<=0){
                            Toast.makeText(this,"Unable to read Weight. Please contact support.",Toast.LENGTH_LONG);
                        }
                        priceTxt.setText(price);
                        deductionWtEditText.selectAll();
                        deductionWtEditText.requestFocus();
                    } else {
                        itemName.requestFocus();
                        Toast.makeText(getApplicationContext(), "Invalid Item Number.", Toast.LENGTH_LONG);
                    }
                } else {
                    if (itemSearchdialog != null) {
                        if (!itemSearchdialog.isShowing()) {
                            OpenItemSearchDialog();
                        }
                    } else {
                        OpenItemSearchDialog();
                    }
                }

            } else if (Quantity.isFocused()) {
                if (itemNo.getText().toString().isEmpty() || itemName.getText().toString().isEmpty()) {
                    showCustomDialog("Warning", "Enter Valid Product");
                }
                else {
                   deductionWtEditText.requestFocus();
                }
            }
        }
        finally {
            if(progressDialog.isShowing()){
                progressDialog.cancel();
            }
        }

    }

    public void CancelBt() {
        QuantityListener.itemsCarts.clear();
        Common.itemsCarts.clear();
        this.itemName.setText("");
        this.Quantity.setText("");
        tItem.setText("0");
        tQty.setText("0");
        estAmt.setText(GetCurrency(0d));
        deductionperEditText.setText("0");
        deductionWtEditText.setText("0");
        //showCustomDialog("Info","Items Cleared.");
        itemNo.requestFocus();
    }

    public void GetPaymentMode() {
        try {
            Common.paymentMode = "";
            GetPaymentModeDialog addCustomer = new GetPaymentModeDialog();
            addCustomer.show(getSupportFragmentManager(), "");
        } catch (Exception ex) {
            showCustomDialog("Error", ex.getMessage().toString());
        }
    }

    public void OpenAddCustomerDialog() {
        try {
            AddCustomerDialog addCustomer = new AddCustomerDialog();
            addCustomer.show(getSupportFragmentManager(), "Add Member Dialog");
        } catch (Exception ex) {
            showCustomDialog("Error", ex.getMessage().toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddMember:
                OpenAddCustomerDialog();
                break;
            case R.id._clr:
                if (itemNo.isFocused()) {
                    String sr = itemNo.getText().toString();
                    if (sr.length() > 0) {
                        sr = sr.substring(0, sr.length() - 1);
                        itemNo.setText(sr);
                    }
                } else if (priceTxt.isFocused()) {
                    String sr = priceTxt.getText().toString();
                    if (sr.length() > 0) {
                        sr = sr.substring(0, sr.length() - 1);
                        priceTxt.setText(sr);
                    }
                } else if (deductionWtEditText.isFocused()) {
                    String sr = deductionWtEditText.getText().toString();
                    if (sr.length() > 0) {
                        sr = sr.substring(0, sr.length() - 1);
                        deductionWtEditText.setText(sr);
                    }
                } else if (deductionperEditText.isFocused()) {
                    String sr = deductionperEditText.getText().toString();
                    if (sr.length() > 0) {
                        sr = sr.substring(0, sr.length() - 1);
                        deductionperEditText.setText(sr);
                    }
                }
                break;
            case R.id.viewitems:
                try {
                    SortItemsCarts();
                    QuantityListener.itemsCarts = Common.itemsCarts;
                    if (QuantityListener.itemsCarts.size() > 0) {
                        try {
                            closeBT();
                        }
                        catch (Exception e){
                            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
                        }
                        Intent intent = new Intent(this, ViewItemActivity.class);
                        startActivity(intent);
                    } else {
                        showCustomDialog("Warning", "Please add items into KOT");
                    }
                    QuantityListener.itemsCarts = Common.itemsCarts;
                    double totalAmt = GetBillAmt();
                    estAmt.setText(GetCurrency(totalAmt));
                } catch (Exception ex) {
                    //Toast.makeText(this.)
                }
                break;
            case R.id._dot:
                if (itemNo.isFocused()) {
                    String sr = itemNo.getText().toString();
                    sr += ".";
                    itemNo.setText(sr);
                } else if (deductionperEditText.isFocused()) {
                    String sr = deductionperEditText.getText().toString();
                    sr += ".";
                    deductionperEditText.setText(sr);
                } else if (deductionWtEditText.isFocused()) {
                    String sr = deductionWtEditText.getText().toString();
                    sr += ".";
                    deductionWtEditText.setText(sr);
                }
                break;
            case R.id.menu:
                try {
                    //closeBT();
                } catch (Exception e) {
                    Toast.makeText(this,"Error:"+e.getMessage(), Toast.LENGTH_SHORT);
                }
                Intent intent = new Intent(this, ItemReport.class);
                startActivity(intent);
                break;
            case R.id.cancel:
                CancelBt();
                break;
            case R.id.print:
                if (QuantityListener.itemsCarts == null || QuantityListener.itemsCarts.size() == 0) {
                    showCustomDialog("Warning", "Please add Items");
                    return;
                }
                SortItemsCarts();
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.enter:
                if (priceTxt.isFocused()) {
                    deductionWtEditText.requestFocus();
                } else if (itemName.isFocused()) {
                    priceTxt.requestFocus();
                } else if (deductionperEditText.isFocused()) {
                    CalculateDiscountAmt();
                } else if (deductionWtEditText.isFocused()) {
                    CalculateDiscountPer();
                    progressBar.show();
                    UpdateCarts();
                    itemNo.setText("");
                    itemName.setText("");
                    Quantity.setText("");
                    priceTxt.setText("");
                    deductionWtEditText.setText("0");
                    deductionperEditText.setText("0");
                    netwtText.setText("0");
                    progressBar.hide();
                    itemNo.requestFocus();
                } else {
                    LoadProductNameandPrice();
                }
                break;
            case R.id.scanQR:
                try {
                    if (!hasCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        ScanQRCode();
                    }
                } catch (Exception ex) {
                    showCustomDialog("Error", ex.getMessage());
                }
                break;
            default:
                String addstr = getResources().getResourceEntryName(v.getId());
                addstr = addstr.replace("_", "");
                if (itemNo.isFocused()) {
                    String sr = itemNo.getText().toString();
                    sr += addstr;
                    itemNo.setText(sr);
                } else if (deductionperEditText.isFocused()) {
                    int startSelection = deductionperEditText.getSelectionStart();
                    int endSelection = deductionperEditText.getSelectionEnd();
                    String selectedText = deductionperEditText.getText().toString().substring(startSelection, endSelection);
                    if (!selectedText.isEmpty()) {
                        deductionperEditText.setText(addstr);
                    } else {
                        String sr = deductionperEditText.getText().toString();
                        sr += addstr;
                        deductionperEditText.setText(sr);
                    }
                } else if (deductionWtEditText.isFocused()) {
                    int startSelection = deductionWtEditText.getSelectionStart();
                    int endSelection = deductionWtEditText.getSelectionEnd();
                    String selectedText = deductionWtEditText.getText().toString().substring(startSelection, endSelection);
                    if (!selectedText.isEmpty()) {
                        deductionWtEditText.setText(addstr);
                    } else {
                        String sr = deductionWtEditText.getText().toString();
                        sr += addstr;
                        deductionWtEditText.setText(sr);
                    }
                } else if (priceTxt.isFocused()) {
                    String sr = priceTxt.getText().toString();
                    sr += addstr;
                    priceTxt.setText(sr);
                }
                break;
        }
    }

    private String GetCurrency(double amt) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        String moneyString = formatter.format(amt).replace(symbol, symbol + " ");
        return moneyString;
    }

    public void LoadTotalAmt() {
        if (Common.itemsCarts != null && Common.itemsCarts.size() > 0) {
            tItem.setText(String.valueOf(Common.itemsCarts.size()));
            double ttq = 0;
            double amt = 0;
            for (int i = 0; i < Common.itemsCarts.size(); i++) {
                ItemsCart ic = Common.itemsCarts.get(i);
                ttq = ttq + ic.NetWeight();
                double pr = ic.getPrice() * ic.NetWeight();
                amt += pr;
            }
            DecimalFormat formater = new DecimalFormat("#.###");
            tQty.setText(formater.format(ttq));
            estAmt.setText(GetCurrency(amt));
        }
    }

    public void UpdateCarts() {
        QuantityListener.itemsCarts = Common.itemsCarts;
        ArrayList<ItemsCart> itemsCarts = QuantityListener.itemsCarts;
        ItemsCart itc = new ItemsCart();
        itc.setItem_No(Integer.valueOf(itemNo.getText().toString()));
        itc.setItem_Name(itemName.getText().toString());
        Double qt = Double.valueOf(Quantity.getText().toString());
        qt = binRedSwitch.isChecked()? qt+Common.defBinWt:qt;
        itc.setQty(qt);
        itc.setPrice(Double.valueOf(priceTxt.getText().toString()));
        itc.setDeduction(Double.valueOf(deductionWtEditText.getText().toString()));
        itc.BinWeight = binRedSwitch.isChecked()?Common.defBinWt:0;
        Item item = dbHelper.GetItem(itemNo.getText().toString());
        if (itemsCarts == null) {
            itemsCarts = new ArrayList<ItemsCart>();
        }
        if (itemsCarts.size() > 0) {
            for (int i = 0; i < itemsCarts.size(); i++) {
                ItemsCart ic = itemsCarts.get(i);
                if (ic.getItem_No() == itc.getItem_No() && ic.getPrice() == itc.getPrice()) {
                    Double newQty = itc.NetWeight() + ic.NetWeight();
                    itemsCarts.remove(ic);
                    itc.setQty(newQty);
                }
            }
        }
        itemsCarts.add(itc);
        Common.itemsCarts = itemsCarts;
        QuantityListener.itemsCarts = itemsCarts;
        LoadTotalAmt();
    }


    private int SaveDetails() {
        int newbillno = dbHelper.GetNextBillNo();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String waiter = searchTxtView.getText().toString();
        double discountAmt = 0;
        double saleAmt = 0;
        for (int i = 0; i < QuantityListener.itemsCarts.size(); i++) {
            Bills_Item bi = new Bills_Item();
            bi.setBill_No(newbillno);
            bi.setItem_No(QuantityListener.itemsCarts.get(i).getItem_No());
            bi.setBill_DateStr(format.format(date));
            bi.setBill_Date(date);
            String name = QuantityListener.itemsCarts.get(i).getItem_Name();
            bi.setItem_Name(name);
            bi.setQty(QuantityListener.itemsCarts.get(i).getQty());
            bi.setWaiter(waiter);
            double price = QuantityListener.itemsCarts.get(i).getPrice();
            bi.setPrice(price);
            bi.setDeduction(QuantityListener.itemsCarts.get(i).getDeduction());
            bi.BinWeight = QuantityListener.itemsCarts.get(i).BinWeight;
            double amt = QuantityListener.itemsCarts.get(i).NetWeight() * QuantityListener.itemsCarts.get(i).getPrice();
            saleAmt += amt;
            dbHelper.Insert_Bill_Items(bi);
        }
        Bills bills = new Bills();
        bills.setBill_No(newbillno);
        bills.setBill_Date(format.format(date));
        bills.setSale_Amt(saleAmt);
        bills.setUser(waiter);
        dbHelper.Insert_Bills(bills);
        int nextBillNo = dbHelper.GetNextBillNo();
        billnoTxtView.setText(String.valueOf(nextBillNo));
        return newbillno;
    }

    public void RefreshViews() {
        this.itemName.setText("");
        this.Quantity.setText("");
        grossQty = 0d;
        this.deductionperEditText.setText("0");
        this.deductionWtEditText.setText("0");
        this.netwtText.setText("0");
        if (Common.itemsCarts != null) {
            Common.itemsCarts.clear();
        }
        if (QuantityListener.itemsCarts != null) {
            QuantityListener.itemsCarts.clear();
        }
        tItem.setText("0");
        tQty.setText("0");
        estAmt.setText(GetCurrency(0d));
        int newbillno = dbHelper.GetNextBillNo();
        billnoTxtView.setText(String.valueOf(newbillno));
        searchTxtView.setText("NONE");
        binRedSwitch.setChecked(false);
        progressBar.hide();
        itemNo.requestFocus();
    }

    public void SetWeight() {


    }

    boolean findBT() {
        boolean proceedFurther = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            String error = "Device Doesn't Supports Bluetooth";
            if(progressDialog.isShowing()){
                progressDialog.cancel();
            }
            showCustomDialog("Message", error);
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                String error = "Go to Settings and Enable Bluetooth";
                if(progressDialog.isShowing()){
                    progressDialog.cancel();
                }
                showCustomDialog("Message", error);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
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
                String finalBtName = "HC";
                if (!blutName.isEmpty()) {
                    finalBtName = blutName;
                }
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().startsWith(finalBtName)) {
                        mmDevice = device;
                        proceedFurther = true;
                        break;
                    }
                }
            }

        }
        return proceedFurther;

    }

    void openBT() throws IOException {

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        if (mmSocket == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if(!mmSocket.isConnected()){
                mmSocket.connect();
            }
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
                                            String wt = data;
                                            wt = wt.replace("Kg","");
                                            wt = wt.replace("\r","");
                                            wt = wt.replace("\n","");
                                            Double grosswet = Double.valueOf(wt);
                                            Double binWt = binRedSwitch.isChecked()?Common.defBinWt:0;
                                            grosswet = grosswet - binWt;
                                            grossQty = grosswet;
                                            DecimalFormat formater = new DecimalFormat("#.###");
                                            Quantity.setText(formater.format(grosswet));
                                            netwtText.setText(formater.format(grosswet));
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

    @Override
    protected void finalize() throws Throwable {
        closeBT();
    }
    void closeBT() throws IOException
    {
        stopWorker = true;
        if(mmOutputStream!=null){
            mmOutputStream.close();
        }
        if(mmInputStream!=null){
            mmInputStream.close();
        }
        if(mmSocket!=null){
            mmSocket.close();
        }
    }

    @Override
    public void getCustomerInfo(String customerName, String mobileNo, String address) {
        Customer customer = dbHelper.GetCustomer(mobileNo);
        if(customer==null){
            customer = new Customer();
        }
        customer.setCustomerName(customerName);
        customer.setMobileNumber(mobileNo);
        customer.setAddress(address);
        dbHelper.InsertCustomer(customer);
        showCustomDialog("Msg","Successfully added Customer details");
        waiters = new ArrayList<String>();
        ArrayList<Customer> customers = dbHelper.GetCustomers();
        for(int i=0;i<customers.size();i++){
            waiters.add(customers.get(i).getCustomerName());
        }
    }
}
