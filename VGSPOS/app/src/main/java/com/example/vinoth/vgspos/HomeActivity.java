package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, AddCustomerDialog.CustomerDialogListener {

    DatabaseHelper dbHelper;
    private Dialog progressBar;

    private EditText itemNo;
    private EditText itemName;
    private EditText Quantity;
    private TextView tQty;
    private TextView tItem;
    private TextView estAmt;
    private TextView billnoTxtView;
    private CheckBox isAcPrice;
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


    public static HomeActivity instance;
    public  EditText priceTxt;
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH
    };
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String HEADERMSG = "HEADERMSG";
    public static final String FOOTERMSG= "FOOTERMSG";
    public static final String PRINTERIP = "PRINTERIP";
    public static final String BLUETOOTNAME = "BLUETOOTHNAME";
    public static final String ISWIFI = "ISWIFI";
    public static final String IS3INCH = "IS3INCH";
    public static final String EXPIRE_DT = "EXPIRE_DT";
    public static final String PRINTKOT = "PRINTKOT";
    public static final String KOTPRINTERIP = "KOTPRINTERIP";
    public static final String BILLCOPIES = "BILLCOPIES";
    public static final String ADDRESSLINE = "ADDRESSLINE";
    String headerMsg ;
    String footerMsg ;
    String printerip ;
    String bluetothName;
    boolean isWifiPrint;
    Button btnScanQr;
    TextView searchTxtView;
    Dialog dialog;
    ArrayList<String> waiters;
    String android_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressBar = new Dialog(HomeActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        try {
            dbHelper = new DatabaseHelper(this);
            itemNo = (EditText) findViewById(R.id.itemNo);
            itemName = (EditText) findViewById(R.id.itemName);
            Quantity = (EditText) findViewById(R.id.qty);
            tQty = (TextView) findViewById(R.id.ttQty);
            tItem = (TextView) findViewById(R.id.ttItem);
            estAmt = (TextView)findViewById(R.id.estimateAmt);
            priceTxt = (EditText)findViewById(R.id.itemPrice);
            searchTxtView = (TextView)findViewById(R.id.customerInfoTxtView);
            billnoTxtView = (TextView)findViewById(R.id.billnoTxt);
            isAcPrice = (CheckBox) findViewById(R.id.isAC);
            int newbillno = dbHelper.GetNextBillNo();
            billnoTxtView.setText(String.valueOf(newbillno));
            //searchTxtView.setText("NONE");
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
            btnScanQr = (Button)findViewById(R.id.scanQR);
            btnClear = (Button) findViewById(R.id._clr);
            btnDot = (Button) findViewById(R.id._dot);
            btnSpace = (Button) findViewById(R.id.viewitems);
            btnMenu = (Button) findViewById(R.id.menu);
            btnCancel = (Button) findViewById(R.id.cancel);
            btnPrint = (Button) findViewById(R.id.print);
            btnEnter = (Button) findViewById(R.id.enter);
            btnAddCustomer = (ImageButton)findViewById(R.id.btnAddMember);
            btnAddCustomer.setOnClickListener(this);
            btnScanQr.setOnClickListener(this);
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
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            headerMsg = sharedpreferences.getString(HEADERMSG,"");
            waiters = new ArrayList<String>();
            waiters.add("NONE");
            ArrayList<Customer> customers = dbHelper.GetCustomers();
            for(int i=0;i<customers.size();i++){
                waiters.add(customers.get(i).getCustomerName());
            }
            Date dt = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 5);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
            String expiredtstr = sharedpreferences.getString(EXPIRE_DT,simpleDateFormat.format(cal.getTime()));
            Date expireDt = simpleDateFormat.parse(expiredtstr);
            Date compare = new Date(dt.getYear(),dt.getMonth(),dt.getDate());
            if(expireDt.compareTo(compare)<0){
                boolean internetav = Is_InternetWorking();
                if(internetav){
                    Common.isActivated = false;
                    android_id = android.provider.Settings.Secure.getString(HomeActivity.this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                    AppActivation appActivation = new AppActivation(HomeActivity.this,android_id,this);
                    appActivation.CheckActivationStatus();
                }
                else{
                    showCustomDialog("Msg","Your application expired.\nPlease connect to internet and Try again.",true);
                }
            }
            searchTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Initialize dialog
                    dialog=new Dialog(HomeActivity.this);

                    // set custom dialog
                    dialog.setContentView(R.layout.dialog_searchable_spinner);

                    // set custom height and width
                    dialog.getWindow().setLayout(800,800);

                    // set transparent background
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    // show dialog
                    dialog.show();

                    // Initialize and assign variable
                    EditText editText=dialog.findViewById(R.id.edit_text);
                    ListView listView=dialog.findViewById(R.id.list_view);

                    // Initialize array adapter
                    ArrayAdapter<String> adapter=new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1,waiters);

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
                int ttq = 0;
                for (int i = 0; i < QuantityListener.itemsCarts.size(); i++) {
                    ItemsCart ic = QuantityListener.itemsCarts.get(i);
                    ttq = ttq + ic.getQty();
                }
                tQty.setText(String.valueOf(ttq));
                LoadTotalAmt();
            }
            footerMsg = sharedpreferences.getString(FOOTERMSG,"");
            printerip = sharedpreferences.getString(PRINTERIP,"");
            String printkot = sharedpreferences.getString(PRINTKOT,"NO");
            String kotprinterip = sharedpreferences.getString(KOTPRINTERIP,"");
            String billcopies = sharedpreferences.getString(BILLCOPIES,"1");
            String addressline = sharedpreferences.getString(ADDRESSLINE,"");
            Common.printKOT = printkot.equalsIgnoreCase("YES");
            Common.kotprinterIP = kotprinterip;
            Common.billcopies = Integer.parseInt(billcopies);
            Common.addressline = addressline;
            Common.printerIP = printerip;
            Common.headerMeg = headerMsg;
            Common.footerMsg = footerMsg;
            bluetothName = sharedpreferences.getString(BLUETOOTNAME,"");
            Common.bluetoothDeviceName = bluetothName;
            String is3Inch = sharedpreferences.getString(IS3INCH,"YES");
            Common.is3Inch = is3Inch.equalsIgnoreCase("YES");
            String isWifi = sharedpreferences.getString(ISWIFI,"YES");
            isWifiPrint = isWifi.equalsIgnoreCase("YES");
            Common.isWifiPrint = isWifiPrint;
            if(!isWifiPrint){
                if(!checkBLuetoothPermission()){
                    ActivityCompat.requestPermissions(HomeActivity.this,PERMISSIONS_BLUETOOTH
                            ,1);
                }
            }
            instance = this;
        } catch (Exception ex) {
            showCustomDialog("Error", ex.getMessage());
        }
    }
    private boolean checkBLuetoothPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            int bluetooth = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT);
            return  bluetooth==PackageManager.PERMISSION_GRANTED;
        }
        else{
            int bluetooth = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH);
            return  bluetooth==PackageManager.PERMISSION_GRANTED;
        }
    }
    public void ValidateActivationResponse(String response){
        if(!Common.isActivated){
            showCustomDialog("Msg","Your Android device "+android_id+" is not activated\n"+response,true,true);
        }
    }
    public boolean Is_InternetWorking(){
        boolean connected = false;
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
            else
                connected = false;
        }
        catch (Exception ex) {
            connected = false;
        }
        return connected;
    }
    private void PrintViaBlueTooth(){
        try
        {
            progressBar.show();
            if(QuantityListener.itemsCarts == null || QuantityListener.itemsCarts.size() == 0){
                showCustomDialog("Warning", "Please add Items");
                return;
            }
            try{
                PrintBluetooth printBluetooth = new PrintBluetooth(HomeActivity.this);
                int copiesprinted = Common.billcopies;
                while (copiesprinted>0){
                    printBluetooth.Print();
                    copiesprinted--;
                }
                printBluetooth.CloseBT();
            }
            catch (Exception ex){
                showCustomDialog("Exception",ex.getMessage().toString());
            }
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage().toString());
        }
        finally {
            if(progressBar.isShowing()){
                progressBar.cancel();
            }
        }
    }
    private void PrintWifi(){
        try
        {
            if(QuantityListener.itemsCarts == null || QuantityListener.itemsCarts.size() == 0){
                showCustomDialog("Warning", "Please add Items");
                return;
            }
                try{
                    PrintWifi printWifi = new PrintWifi(HomeActivity.this,true);
                    printWifi.Print();
                }
                catch (Exception ex){
                    showCustomDialog("Exception",ex.getMessage().toString());
                }
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage().toString());
        }
    }
    public static HomeActivity getInstance() {
        return instance;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.uploadExcel:
                Intent dcpage = new Intent(this,UploadActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            case R.id.itemMaster:
                Intent itemmaster = new Intent(this,ItemsMasterActivity.class);
                itemmaster.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(itemmaster);
                return true;
            case R.id.settings:
                Common.openSettings = false;
                Intent settingsPage = new Intent(this,Settings.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return  true;
            case R.id.saleReort:
                Intent saleReportPage = new Intent(this,SaleReportActivity.class);
                saleReportPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(saleReportPage);
                return  true;
            case R.id.homemenu:
                Intent page = new Intent(this,HomeActivity.class);
                page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(page);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showCustomDialog(String title, String Message,boolean... closeapp) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        if(closeapp.length>1 && closeapp[1]){
            dialogBuilder.setNeutralButton("Share Device ID", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, android_id);
                    try {
                        startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(HomeActivity.this,"Whatsapp have not been installed.",Toast.LENGTH_LONG);
                    }
                    finally {
                        finish();
                        System.exit(0);
                    }
                }
            });
        }
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(closeapp.length>0 && closeapp[0]){
                    finish();
                    System.exit(0);
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }
    private int GetItematIndex(ArrayList<ItemsCart> items,int itemno){
        int index=-1;
        for(int i=0;i<items.size();i++){
            ItemsCart itemsCart = items.get(i);
            if(itemsCart.getItem_No() == itemno){
                index = i;
                break;
            }
        }
        return index;
    }
    private void SortItemsCarts(){
        ArrayList<ItemsCart>  items = new ArrayList<>();
        for(int i=0;i<Common.itemsCarts.size();i++){
            ItemsCart item = Common.itemsCarts.get(i);
                int index = GetItematIndex(items,item.getItem_No());
                if(index>-1){
                    ItemsCart existingitem = items.get(index);
                    int qty = item.getQty();
                    qty+=existingitem.getQty();
                    item.setQty(qty);
                    items.remove(index);
                }
            items.add(item);
        }
        Common.itemsCarts = items;
    }
    public boolean isNumeric(String val){
        try {
            Integer it = Integer.parseInt(val);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Integer.");
            return false;
        }
    }
    public void LoadProductNameandPrice(){
        if(itemNo.isFocused()){
            String itemnostr = itemNo.getText().toString();
            if(!itemnostr.isEmpty()){
                if(isNumeric(itemnostr)){
                    Integer ino = Integer.parseInt(itemnostr);
                    Item item = dbHelper.GetItem(ino);
                    if(item!=null){
                        itemName.setText(item.getItem_Name());
                        String price =String.format("%.0f", isAcPrice.isChecked()?item.getAcPrice():item.getPrice());
                        priceTxt.setText(price);
                        Quantity.requestFocus();
                    }
                    else{
                        showCustomDialog("Msg","Invalid Item Number.");
                    }
                }
                else{
                    showCustomDialog("Msg","Item No should not be a String.");
                }

            }

        }
        else if(Quantity.isFocused()){
            if(itemNo.getText().toString().isEmpty() || itemName.getText().toString().isEmpty()){
                showCustomDialog("Warning","Enter Valid Product");
            }
            else {
                progressBar.show();
                UpdateCarts();
                itemNo.setText("");
                itemName.setText("");
                Quantity.setText("");
                priceTxt.setText("");
                progressBar.hide();
                itemNo.requestFocus();
            }
        }
    }
    public void OpenAddCustomerDialog(){
        try {
            AddCustomerDialog addCustomer = new AddCustomerDialog();
            addCustomer.show(getSupportFragmentManager(),"Add Member Dialog");
        }catch (Exception ex){
            showCustomDialog("Error",ex.getMessage().toString());
        }
    }
    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.btnAddMember:
               OpenAddCustomerDialog();
               break;
           case R.id._clr:
                  if(itemNo.isFocused()){
                      String sr = itemNo.getText().toString();
                      if(sr.length()>0){
                          sr = sr.substring(0,sr.length()-1);
                          itemNo.setText(sr);
                      }
                  }
                  else if(Quantity.isFocused()){
                      String sr = Quantity.getText().toString();
                      if(sr.isEmpty()){
                          priceTxt.setText(" ");
                          priceTxt.requestFocus();
                      }
                      else{
                          if(sr.length()>0){
                              sr = sr.substring(0,sr.length()-1);
                              Quantity.setText(sr);
                          }
                      }
                  }
                  else if(priceTxt.isFocused()){
                      String sr = priceTxt.getText().toString();
                      if(sr.length()>0){
                          sr = sr.substring(0,sr.length()-1);
                          priceTxt.setText(sr);
                      }
                  }
                  break;
           case R.id.viewitems:
               SortItemsCarts();
               QuantityListener.itemsCarts = Common.itemsCarts;
                if(QuantityListener.itemsCarts.size()>0){
                    Intent intent = new Intent(this, ViewItemActivity.class);
                    startActivity(intent);
                }
                else {
                    showCustomDialog("Warning","Please add items into KOT");
                }
                QuantityListener.itemsCarts = Common.itemsCarts;
                double totalAmt = 0;
                for(int i=0;i<Common.itemsCarts.size();i++){
                    ItemsCart itemsCart = Common.itemsCarts.get(i);
                    double amt = itemsCart.getQty()*itemsCart.getPrice();
                    totalAmt+=amt;
                }
                String ttamtStr = "₹ "+String.format("%.0f",totalAmt);
                estAmt.setText(ttamtStr);
               break;
           case R.id._dot:
               if(itemNo.isFocused()){
                   String sr = itemNo.getText().toString();
                   sr +=".";
                   itemNo.setText(sr);
               }
               else if(Quantity.isFocused()){
                   String sr = Quantity.getText().toString();
                   sr +=".";
                   Quantity.setText(sr);
               }
               break;
           case R.id.menu:
               Intent intent = new Intent(this, ItemReport.class);
               startActivity(intent);
               break;
           case  R.id.cancel:
               QuantityListener.itemsCarts.clear();
               Common.itemsCarts.clear();
               this.itemName.setText("");
               this.Quantity.setText("");
               tItem.setText("0");
               tQty.setText("0");
               estAmt.setText("₹ 000");
               showCustomDialog("Info","Items Cleared.");
               itemName.requestFocus();
               break;
           case  R.id.print:
               SortItemsCarts();
               int billno = SaveDetails();
               Common.billNo = billno;
               Common.billDate = new Date();
               Common.waiter  = searchTxtView.getText().toString();
                if(isWifiPrint){
                    PrintWifi();
                }
                else{
                    PrintViaBlueTooth();
                    RefreshViews();
                }
               break;
           case  R.id.enter:
               if(priceTxt.isFocused()){
                   Quantity.requestFocus();
               }
               else{
                   LoadProductNameandPrice();
               }
               break;
           case R.id.scanQR:
               IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
               intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
               intentIntegrator.setBeepEnabled(true);
               intentIntegrator.setOrientationLocked(false);
               intentIntegrator.setCameraId(0);
               intentIntegrator.setPrompt("SCAN ITEM NO");
               intentIntegrator.setBarcodeImageEnabled(false);
               intentIntegrator.initiateScan();
               break;
           default:
                 String addstr = getResources().getResourceEntryName(v.getId());
                 addstr =  addstr.replace("_","");
                   if(itemNo.isFocused()){
                       String sr = itemNo.getText().toString();
                       sr+=addstr;
                       itemNo.setText(sr);
                   }
                   else if(Quantity.isFocused()){
                       String sr = Quantity.getText().toString();
                       sr+=addstr;
                       Quantity.setText(sr);
                   }
                   else if(priceTxt.isFocused()){
                       String sr = priceTxt.getText().toString();
                       sr+=addstr;
                       priceTxt.setText(sr);
                   }
                   break;

       }
    }
    public void LoadTotalAmt(){
        if(Common.itemsCarts.size()>0){
            tItem.setText(String.valueOf(Common.itemsCarts.size()));
            int ttq = 0;
            double amt = 0;
            for(int i=0;i<Common.itemsCarts.size();i++){
                ItemsCart ic = Common.itemsCarts.get(i);
                ttq = ttq+ic.getQty();
                double pr = ic.getPrice()*ic.getQty();
                amt+=pr;
            }
            tQty.setText(String.valueOf(ttq));
            String amtstr = String.format("%.0f", amt);
            estAmt.setText("₹ "+amtstr);
        }
    }
    public  void  UpdateCarts(){
        QuantityListener.itemsCarts = Common.itemsCarts;
        ArrayList<ItemsCart> itemsCarts = QuantityListener.itemsCarts;
        ItemsCart itc = new ItemsCart();
        itc.setItem_No(Integer.valueOf(itemNo.getText().toString()));
        itc.setItem_Name(itemName.getText().toString());
        itc.setQty(Integer.valueOf(Quantity.getText().toString()));
        itc.setPrice(Double.valueOf(priceTxt.getText().toString()));
        if(itemsCarts == null){
            itemsCarts = new ArrayList<ItemsCart>();
        }
        if(itemsCarts.size()>0){
            Integer index = itemsCarts.indexOf(itc);
            if(index != -1){
                ItemsCart temp = itemsCarts.get(index);
                Integer newQty = itc.getQty()+temp.getQty();
                itemsCarts.remove(temp);
                itc.setQty(newQty);
            }
        }
        itemsCarts.add(itc);
        Common.itemsCarts = itemsCarts;
        QuantityListener.itemsCarts = itemsCarts;
        LoadTotalAmt();
    }


    private int SaveDetails(){
        int newbillno = dbHelper.GetNextBillNo();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String waiter  = searchTxtView.getText().toString();
        double saleAmt = 0;
        for(int i=0;i<QuantityListener.itemsCarts.size();i++){
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
            double amt = QuantityListener.itemsCarts.get(i).getQty()*QuantityListener.itemsCarts.get(i).getPrice();
            saleAmt+=amt;
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
    public void RefreshViews(){
        this.itemName.setText("");
        this.Quantity.setText("");
        if(Common.itemsCarts!=null){
            Common.itemsCarts.clear();
        }
        if(QuantityListener.itemsCarts!=null){
            QuantityListener.itemsCarts.clear();
        }
        tItem.setText("0");
        tQty.setText("0");
        estAmt.setText("₹ 000");
        int newbillno = dbHelper.GetNextBillNo();
        billnoTxtView.setText(String.valueOf(newbillno));
        searchTxtView.setText("NONE");
        isAcPrice.setChecked(false);
        progressBar.hide();
        itemName.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult Result = IntentIntegrator.parseActivityResult(requestCode , resultCode ,data);
        if(Result != null){
            if(Result.getContents() == null){
                Log.d("MainActivity" , "cancelled scan");
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d("MainActivity" , "Scanned");
                itemNo.requestFocus();
                itemNo.setText(Result.getContents());
                LoadProductNameandPrice();
                Toast.makeText(this,"Scanned -> " + Result.getContents(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode , resultCode , data);
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
