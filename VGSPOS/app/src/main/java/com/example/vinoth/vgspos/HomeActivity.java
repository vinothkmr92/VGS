package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.port.android.WiFiPortConnection;
import com.sewoo.request.android.RequestHandler;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseHelper dbHelper;
    private Dialog progressBar;

    private EditText itemNo;
    private EditText itemName;
    private EditText Quantity;
    private TextView tQty;
    private TextView tItem;
    private TextView estAmt;
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
    private Spinner wtSpinner;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothSocket mBluetoothSocket = null;
    OutputStream mOutputStream = null;
    Set<BluetoothDevice> pairedDevices = null;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
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
    String headerMsg ;
    String footerMsg ;
    String printerip ;
    String bluetothName;
    boolean isWifiPrint;
    private Thread hThread;
    private WiFiPort wifiPort;
    public ESCPOSPrinter posPtr;
    public static final byte[] PRINT_ALIGN_LEFT = new byte[] { 0x1b, 'a', 0x00 };
    public static final byte[] PRINT_ALIGN_RIGHT = new byte[] { 0x1b, 'a', 0x02 };
    public static final byte[] PRINT_ALIGN_CENTER = new byte[] { 0x1b, 'a', 0x01 };
    private int rtn;
    // 0x1B
    private final char ESC = ESCPOS.ESC;

    public  HomeActivity() {posPtr=new ESCPOSPrinter();}
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
            wtSpinner = (Spinner) findViewById(R.id.waiterSpinner);
            estAmt = (TextView)findViewById(R.id.estimateAmt);
            priceTxt = (EditText)findViewById(R.id.itemPrice);
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
            ArrayList<String> waiters = new ArrayList<String>();
            waiters.add("NONE");
            waiters.add("WAITER - 1");
            waiters.add("WAITER - 2");
            waiters.add("WAITER - 3");
            waiters.add("WAITER - 4");
            waiters.add("WAITER - 5");
            wifiPort = WiFiPort.getInstance();
            if (QuantityListener.itemsCarts != null) {
                tItem.setText(String.valueOf(QuantityListener.itemsCarts.size()));
                int ttq = 0;
                for (int i = 0; i < QuantityListener.itemsCarts.size(); i++) {
                    ItemsCart ic = QuantityListener.itemsCarts.get(i);
                    ttq = ttq + ic.getQty();
                }
                tQty.setText(String.valueOf(ttq));
            }
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, waiters);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            wtSpinner.setAdapter(adapter);
            wtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Your code here
                    itemNo.requestFocus();
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            headerMsg = sharedpreferences.getString(HEADERMSG,"");
            footerMsg = sharedpreferences.getString(FOOTERMSG,"");
            printerip = sharedpreferences.getString(PRINTERIP,"");
            bluetothName = sharedpreferences.getString(BLUETOOTNAME,"");
            String isWifi = sharedpreferences.getString(ISWIFI,"");
            isWifiPrint = isWifi.equalsIgnoreCase("YES");
            if(!isWifiPrint){
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(HomeActivity.this,PERMISSIONS_BLUETOOTH
                            ,1);
                    return;
                }
                pairedDevices = mBluetoothAdapter.getBondedDevices();
                String getName = mBluetoothAdapter.getName();
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    if (device.getName().contains(bluetothName)) {
                        getName = device.getAddress();
                        break;
                    }

                }
                if(mBluetoothSocket==null){
                    ConnectBluetooth(getName);
                }
            }
            instance = this;
        } catch (Exception ex) {
            showCustomDialog("Error", ex.getMessage());
        }
    }
    @Override
    protected void onDestroy() {
        try
        {
            if(wifiPort != null)
                wifiPort.disconnect();
            mOutputStream.close();
            mBluetoothSocket.close();
        }
        catch (IOException e)
        {
            Log.e("ERR", e.getMessage(), e);
        }
        catch (InterruptedException e)
        {
            Log.e("ERR", e.getMessage(), e);
        }
        if((hThread != null) && (hThread.isAlive()))
        {
            hThread.interrupt();
            hThread = null;
        }
        super.onDestroy();
    }
    public int PrintData() throws InterruptedException
    {
        try
        {
            posPtr.setAsync(true);
            rtn = posPtr.printerSts();

            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            QuantityListener.itemsCarts = Common.itemsCarts;
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            String dateStr = format.format(date);
            int billno = SaveDetails();

            posPtr.printNormal(ESC+"|cA"+ESC+"|4C"+headerMsg+"\r\n");
            posPtr.printNormal("\n");
            //posPtr.printNormal(ESC+"|rATEL (123)-456-7890\n\n\n");
            String waiter  = wtSpinner.getSelectedItem().toString();
            posPtr.printNormal(ESC+"|lABILL NO: "+billno+"\n");
            if(!waiter.equals("NONE")){
                posPtr.printNormal(ESC+"|lAUSER: "+waiter);
                posPtr.printNormal("\n");
            }
            posPtr.printNormal(ESC+"|lADate: "+dateStr+"\n\n");
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME             QTY      PRICE    AMOUNT\n");
            double totalQty = 0d;
            double totalAmt = 0d;
            for(int k=0;k<QuantityListener.itemsCarts.size();k++){
                String name = QuantityListener.itemsCarts.get(k).getItem_Name();
                String qty = String.valueOf(QuantityListener.itemsCarts.get(k).getQty());
                String price = String.format("%.0f",QuantityListener.itemsCarts.get(k).getPrice());
                Double amt = QuantityListener.itemsCarts.get(k).getPrice()*QuantityListener.itemsCarts.get(k).getQty();
                totalAmt+=amt;
                String amts=String.format("%.0f",amt);
                name = StringUtils.rightPad(name,20);
                qty = StringUtils.leftPad(qty,5);
                price = StringUtils.leftPad(price,11);
                amts = StringUtils.leftPad(amts,10);
                String line = name+qty+price+amts+"\n";
                posPtr.printNormal(line);
            }
            String totalamt = String.format("%.0f",totalAmt);
            String txttotal = "TOTAL: "+totalamt+"/-";
            posPtr.lineFeed(1);
            posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
            posPtr.lineFeed(2);
            posPtr.printNormal(ESC+"|cA"+footerMsg+"\n");
            posPtr.lineFeed(5);
            posPtr.cutPaper();
            wifiPort.disconnect();
            RefreshViews();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    private void PrintWifi(){
        try
        {
            if(QuantityListener.itemsCarts == null || QuantityListener.itemsCarts.size() == 0){
                showCustomDialog("Warning", "Please add Items");
                return;
            }
            if(!wifiPort.isConnected()){
                try{
                    new connectPrinter().execute(printerip);
                }
                catch (Exception ex){
                    showCustomDialog("Exception",ex.getMessage().toString());
                }
            }

        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage().toString());
        }
        if(progressBar.isShowing()){
            progressBar.cancel();
        }
    }
    class connectPrinter extends AsyncTask<String, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        private WiFiPortConnection connection;
        @Override
        protected void onPreExecute()
        {
            dialog.setTitle(" ");
            dialog.setMessage("Printing.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            Integer retVal = null;
            try
            {
                // ip
                wifiPort.connect(params[0]);
                //connection = wifiPort.open(params[0]);

                //lastConnAddr = params[0];
                retVal = new Integer(0);
            }
            catch (IOException e)
            {
                Log.e("Wificonnection:",e.getMessage(),e);
                retVal = new Integer(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(result.intValue() == 0)
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                posPtr = new ESCPOSPrinter();
                posPtr.setAsync(true);
                try{
                    PrintData();
                }
                catch (Exception ex){
                    showCustomDialog("Exception",ex.getMessage().toString());
                }
                finally {
                    if(dialog.isShowing())
                        dialog.dismiss();
                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
            }

            super.onPostExecute(result);
        }
    }
    public static HomeActivity getInstance() {
        return instance;
    }
    public void PrintBltnBytes(byte[] bytes){
        try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            mOutputStream.write(bytes);
            mOutputStream.flush();
        } catch (Exception ex) {
            showCustomDialog("Exception", ex.getMessage().toString());
        }
    }
    public void PrintData(String txt,final byte[] pFormat, final byte[] pAlignment) {
        try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            mOutputStream.write(pAlignment);
            // Notify printer it should be printed in the given format:
            mOutputStream.write(pFormat);
            mOutputStream.write((txt+"\n").getBytes("GBK"));
            mOutputStream.flush();
        } catch (Exception ex) {
            showCustomDialog("Exception", ex.getMessage().toString());
        }
    }
    public boolean PrintWithFormat(byte[] buffer, final byte[] pFormat, final byte[] pAlignment) {
        try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            // Notify printer it should be printed with given alignment:
            mOutputStream.write(pAlignment);
            // Notify printer it should be printed in the given format:
            mOutputStream.write(pFormat);
            // Write the actual data:
            mOutputStream.write(buffer, 0, buffer.length);
            mOutputStream.flush();
            return true;
        } catch (IOException e) {
            Log.e("ERR", "Exception during write", e);
            return false;
        }
    }
    public void PaperCut() {
        try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            mOutputStream.write(new byte[]{0x0a, 0x0a, 0x1d, 0x56, 0x01});
            mOutputStream.flush();
        } catch (Exception ex) {
            showCustomDialog("Exception", ex.getMessage().toString());
        }
    }

    public void ConnectBluetooth(String btName) {
        try {
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(btName);
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
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(HomeActivity.SPP_UUID);
            mBluetoothSocket.connect();
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage().toString());
            CloseBluetooth();
        }
    }
    private void CloseBluetooth(){
        try {
            mOutputStream.close();
            mBluetoothSocket.close();
        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage().toString());
        }
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
            case R.id.settings:
                Intent settingsPage = new Intent(this,Settings.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
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
    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
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

    @Override
    public void onClick(View v) {
       switch (v.getId()){
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
                      if(sr.length()>0){
                          sr = sr.substring(0,sr.length()-1);
                          Quantity.setText(sr);
                      }
                  }
                  break;
           case R.id.viewitems:
                if(QuantityListener.itemsCarts.size()>0){
                    Intent intent = new Intent(this, ViewItemActivity.class);
                    startActivity(intent);
                }
                else {
                    showCustomDialog("Warning","Please add items into KOT");
                }
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
                if(isWifiPrint){
                    PrintWifi();
                }
                else{
                    PrintViaBlueTooth();
                }
               break;
           case  R.id.enter:
               if(itemNo.isFocused()){
                   String itemnostr = itemNo.getText().toString();
                   if(!itemnostr.isEmpty()){
                       Integer ino = Integer.parseInt(itemnostr);
                       Item item = dbHelper.GetItem(ino);
                       if(item!=null){
                           itemName.setText(item.getItem_Name());
                           String price =String.format("%.0f", item.getPrice());
                           priceTxt.setText(price);
                           Quantity.requestFocus();
                       }
                       else{
                           showCustomDialog("Msg","Invalid Item Number.");
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
    public  String padLeft(String s, int n) {
        return StringUtils.leftPad(s,n);
    }
    public String GetFormatedString(String txt,int maxLength){
        String res = padLeft(txt,maxLength);
        return res.substring(0,maxLength);
    }

    private int SaveDetails(){
        int newbillno = dbHelper.GetNextBillNo();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        String waiter  = wtSpinner.getSelectedItem().toString();
        for(int i=0;i<QuantityListener.itemsCarts.size();i++){
            Bills_Item bi = new Bills_Item();
            bi.setBill_No(newbillno);
            bi.setBill_DateStr(format.format(date));
            String name = QuantityListener.itemsCarts.get(i).getItem_Name();
            bi.setItem_Name(name);
            bi.setQty(QuantityListener.itemsCarts.get(i).getQty());
            bi.setWaiter(waiter);
            dbHelper.Insert_Bill_Items(bi);
        }
        return newbillno;
    }
    private void RefreshViews(){
        this.itemName.setText("");
        this.Quantity.setText("");
        QuantityListener.itemsCarts.clear();
        tItem.setText("0");
        tQty.setText("0");
        estAmt.setText("₹ 000");
        progressBar.hide();
        itemName.requestFocus();
    }
    public  void PrintViaBlueTooth() {
        int sl = 0;
        progressBar.show();
        QuantityListener.itemsCarts = Common.itemsCarts;
        String waiter  = wtSpinner.getSelectedItem().toString();
        try {
            if(QuantityListener.itemsCarts == null || QuantityListener.itemsCarts.size() == 0){
                showCustomDialog("Warning", "Please add Items");
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            //PrintBltnBytes(PRINT_ALIGN_CENTER);
            //PrintData(headerMsg);
            //PrintBltnBytes(PRINT_ALIGN_LEFT);
            String msg = headerMsg+"\n";
            PrintWithFormat(msg.getBytes(StandardCharsets.UTF_8),new Formatter().bold().get(),Formatter.centerAlign());
            int billno = SaveDetails();
            if(!waiter.equals("NONE")){
                PrintData("USER     :"+waiter,new Formatter().get(),Formatter.leftAlign());
            }
            PrintData("BILL NO  :"+billno,new Formatter().get(),Formatter.leftAlign());
            PrintData("DATE     : " + format.format(date),new Formatter().get(),Formatter.leftAlign());
            PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
            String hed = "ITEM        QTY    RATE   AMOUNT";
            PrintData(hed,new Formatter().bold().get(),Formatter.leftAlign());
            PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
            double totalAmt=0;
            for(int k=0;k<QuantityListener.itemsCarts.size();k++){
                String name = QuantityListener.itemsCarts.get(k).getItem_Name();
                String qty = String.valueOf(QuantityListener.itemsCarts.get(k).getQty());
                String price = String.format("%.0f",QuantityListener.itemsCarts.get(k).getPrice());
                Double amt = QuantityListener.itemsCarts.get(k).getPrice()*QuantityListener.itemsCarts.get(k).getQty();
                totalAmt+=amt;
                String amts=String.format("%.0f",amt);
                PrintData(name,new Formatter().get(),Formatter.leftAlign());
                PrintData("           "+GetFormatedString(qty,4)+GetFormatedString(price,9)+GetFormatedString(amts,8),
                        new Formatter().get(),Formatter.leftAlign());
            }
            PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            String ttAmtTxt = "TOTAL AMT:"+String.format("%.0f",totalAmt)+"/-";
            PrintData(ttAmtTxt,new Formatter().bold().get(),Formatter.centerAlign());
            PrintData("  ",new Formatter().get(),Formatter.leftAlign());
            PrintData(footerMsg,new Formatter().get(),Formatter.centerAlign());
            PrintData(" ",new Formatter().get(),Formatter.leftAlign());
            PrintData("  ",new Formatter().get(),Formatter.leftAlign());
            Toast.makeText(this, "Print Queued Successfully.!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            showCustomDialog("Print Error",e.getMessage());
            //Toast.makeText(MainActivity.this, e.getMessage(), 1).show();
            e.printStackTrace();
        }
        finally {
            RefreshViews();
        }
    }

}
