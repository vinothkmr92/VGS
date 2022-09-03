package com.example.easypos;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos80;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton printButton;
    public static boolean ISCONNECT;
    public static String DISCONNECT="com.posconsend.net.disconnetct";
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String COMPANYNAME = "COMPANYNAME";
    public static final String ADDRESS= "ADDRESS";
    public static final String PRINTERIP = "PRINTERIP";
    public static final String ISACTIVATED = "ISACTIVATED";
    //IMyBinder interface，All methods that can be invoked to connect and send data are encapsulated within this interface
    public static IMyBinder binder;
    //bindService connection
    ServiceConnection conn= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Bind successfully
            binder= (IMyBinder) iBinder;
            while (!ISCONNECT){
                connetNet();
            }
            Log.e("binder","connected");
            progressBar.cancel();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("disbinder","disconnected");
            progressBar.cancel();
        }
    };
    CoordinatorLayout container;
    GridLayout gridLayout;
    DynamicView dynamicView;
    public  int snonumber = 0;
    EditText priceEditText;
    EditText qtyEditText;
    TextView totalAmtTextView;
    ArrayList<Items> itemsList = new ArrayList<>();
    Dialog progressBar;
    String companyname;
    String address;
    String printeripaddress;
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
    private Button btnDot;
    private Button btnClear;
    private AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = new Dialog(MainActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading...");
        printButton = (ImageButton)findViewById(R.id.printbtn);
        printButton.setOnClickListener(this);
        printButton.setBackgroundResource(R.drawable.close);
        printButton.setEnabled(false);
        Intent intent=new Intent(this, PosprinterService.class);
        progressBar.show();
        gridLayout = (GridLayout) findViewById(R.id.gridData);
        priceEditText = (EditText) findViewById(R.id.price);
        totalAmtTextView = (TextView) findViewById(R.id.totalAmt);
        qtyEditText = (EditText)findViewById(R.id.qty);
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        companyname = sharedpreferences.getString(COMPANYNAME,"");
        address = sharedpreferences.getString(ADDRESS,"");
        printeripaddress = sharedpreferences.getString(PRINTERIP,"");
        String isactivated = sharedpreferences.getString(ISACTIVATED,"");
        if(!isactivated.equals("true")){
            GoActivation();
        }
        boolean isconnected = bindService(intent, conn, BIND_AUTO_CREATE);
        btn1 = (Button)findViewById(R.id._1);
        btn2= (Button)findViewById(R.id._2);
        btn3= (Button)findViewById(R.id._3);
        btn4= (Button)findViewById(R.id._4);
        btn5= (Button)findViewById(R.id._5);
        btn6= (Button)findViewById(R.id._6);
        btn7= (Button)findViewById(R.id._7);
        btn8= (Button)findViewById(R.id._8);
        btn9= (Button)findViewById(R.id._9);
        btn0= (Button)findViewById(R.id._0);
        btnDot= (Button)findViewById(R.id._dot);
        btnClear = (Button)findViewById(R.id._clr);
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
        btnDot.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        qtyEditText.requestFocus();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Click Yes to Print Second Copy ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                PrintWifi();
                ClearDetails();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                ClearDetails();
                dialog.dismiss();
            }
        });
        alert = builder.create();
    }

    public  void  GoActivation(){
        Intent page = new Intent(this,Activation.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
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
                finishAffinity();
                return true;
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
    public void PrintWifi(){
        progressBar.show();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
        Date date = new Date();
        printHeaderText(companyname,1);
        printAddress();
        printText(" ",false);
        String dateStr = format.format(date);
        printText("Date: "+dateStr,false);
        printText(" ",false);
        String head = "SNO          QTY      PRICE               AMOUNT";
        printTextHeaderSno(head);
        double totalQty = 0d;
        for(int i=0;i<itemsList.size();i++){
            Items item = itemsList.get(i);
            String sno = org.apache.commons.lang3.StringUtils.rightPad(item.getSno(),5," ");
            String price = org.apache.commons.lang3.StringUtils.leftPad(item.getPrice(),10," ");
            String qty = org.apache.commons.lang3.StringUtils.leftPad(item.getQty(),12," ");
            String amt = org.apache.commons.lang3.StringUtils.leftPad(item.getAmt(),21," ");
            String line = sno+qty+price+amt;
            printText(line,false);
            printText(" ",false);
            double q = Double.parseDouble(item.getQty());
            totalQty+=q;
        }
        String totalqty =String.valueOf(totalQty);
        String txttotalqty = "TOTAL QTY: "+totalqty;
        String totalamt = totalAmtTextView.getText().toString();
        String txttotal = "TOTAL: "+totalamt+"/-";
        printText(" ",false);
        printHeaderText(txttotal,2);
        printHeaderText(txttotalqty,0);
        printText(" ",true);
    }
    private  void ClearDetails(){
        gridLayout.removeViews(4,itemsList.size()*4);
        itemsList.clear();
        totalAmtTextView.setText("000");
        progressBar.cancel();
        snonumber=0;
    }

    private void printText(String texttoPrint,boolean cutPaper){

        MainActivity.binder.writeDataByYouself(
                new UiExecute() {
                    @Override
                    public void onsucess() {

                    }

                    @Override
                    public void onfailed() {

                    }
                }, new ProcessData() {
                    @Override
                    public List<byte[]> processDataBeforeSend() {

                        List<byte[]> list=new ArrayList<byte[]>();
                        //creat a text ,and make it to byte[],
                        String str=texttoPrint;
                        if (str.equals(null)||str.equals("")){
                           // showSnackbar("NO Text to Print");
                        }else {
                            //initialize the printer
//                            list.add( DataForSendToPrinterPos58.initializePrinter());
                            list.add(DataForSendToPrinterPos80.initializePrinter());
                            byte[] data1= StringUtils.strTobytes(str);
                            list.add(data1);
                            //should add the command of print and feed line,because print only when one line is complete, not one line, no print
                            //list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            list.add(DataForSendToPrinterPos80.printAndFeed(1));
                            //cut pager
                            if(cutPaper){
                                list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));
                            }
                            return list;
                        }
                        return null;
                    }
                });

    }
    private void printTextHeaderSno(String texttoPrint){

        MainActivity.binder.writeDataByYouself(
                new UiExecute() {
                    @Override
                    public void onsucess() {

                    }

                    @Override
                    public void onfailed() {

                    }
                }, new ProcessData() {
                    @Override
                    public List<byte[]> processDataBeforeSend() {

                        List<byte[]> list=new ArrayList<byte[]>();
                        //creat a text ,and make it to byte[],
                        String str=texttoPrint;
                        if (str.equals(null)||str.equals("")){
                            showSnackbar("NO Text to Print");
                        }else {
                            //initialize the printer
//                            list.add( DataForSendToPrinterPos58.initializePrinter());
                            list.add(DataForSendToPrinterPos80.initializePrinter());
                            //list.add(DataForSendToPrinterPos80.selectCharacterSize());
                            //list.add(DataForSendToPrinterPos80.selectFont(1));
                            byte[] data1= StringUtils.strTobytes(str);
                            list.add(data1);
                            //should add the command of print and feed line,because print only when one line is complete, not one line, no print
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            return list;
                        }
                        return null;
                    }
                });

    }
    private void printHeaderText(String txttoprint,int allignment){

        MainActivity.binder.writeDataByYouself(
                new UiExecute() {
                    @Override
                    public void onsucess() {

                    }

                    @Override
                    public void onfailed() {

                    }
                }, new ProcessData() {
                    @Override
                    public List<byte[]> processDataBeforeSend() {

                        List<byte[]> list=new ArrayList<byte[]>();
                        //creat a text ,and make it to byte[],
                        String str=txttoprint;
                        if (str.equals(null)||str.equals("")){
                            showSnackbar("NO Text to Print");
                        }else {
                            //initialize the printer
//                            list.add( DataForSendToPrinterPos58.initializePrinter());
                            list.add(DataForSendToPrinterPos80.initializePrinter());
                            list.add(DataForSendToPrinterPos80.selectCharacterSize(9));
                            list.add(DataForSendToPrinterPos80.selectAlignment(allignment));
                            //list.add(DataForSendToPrinterPos80.selectFont(1));
                            byte[] data1= StringUtils.strTobytes(str);
                            list.add(data1);
                            //should add the command of print and feed line,because print only when one line is complete, not one line, no print
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());

                            return list;
                        }
                        return null;
                    }
                });

    }
    private void printAddress(){

        MainActivity.binder.writeDataByYouself(
                new UiExecute() {
                    @Override
                    public void onsucess() {

                    }

                    @Override
                    public void onfailed() {

                    }
                }, new ProcessData() {
                    @Override
                    public List<byte[]> processDataBeforeSend() {

                        List<byte[]> list=new ArrayList<byte[]>();
                        //creat a text ,and make it to byte[],
                        String str=address;
                        if (str.equals(null)||str.equals("")){
                            showSnackbar("NO Text to Print");
                        }else {
                            //initialize the printer
//                            list.add( DataForSendToPrinterPos58.initializePrinter());
                            list.add(DataForSendToPrinterPos80.initializePrinter());
                            list.add(DataForSendToPrinterPos80.selectCharacterSize(9));
                            list.add(DataForSendToPrinterPos80.selectAlignment(1));
                            //list.add(DataForSendToPrinterPos80.selectFont(1));
                            byte[] data1= StringUtils.strTobytes(str);
                            list.add(data1);
                            //should add the command of print and feed line,because print only when one line is complete, not one line, no print
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());

                            return list;
                        }
                        return null;
                    }
                });

    }
    private void showSnackbar(String showstring){
        showCustomDialog("Msg",showstring);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binder.disconnectCurrentPort(new UiExecute() {
            @Override
            public void onsucess() {

            }

            @Override
            public void onfailed() {

            }
        });
        unbindService(conn);
    }
    private void connetNet(){
        String ipAddress=printeripaddress;
        if (ipAddress.equals(null)||ipAddress.equals("")){
               showSnackbar("Invalid Printer IP.");

        }else {
            //ipAddress :ip address; portal:9100
            binder.connectNetPort(ipAddress,9100, new UiExecute() {
                @Override
                public void onsucess() {
                    ISCONNECT=true;
                    showSnackbar("Printer connection successful");
                    printButton.setEnabled(true);
                    printButton.setBackgroundResource(R.drawable.print);
                    //in this ,you could call acceptdatafromprinter(),when disconnect ,will execute onfailed();
                    binder.acceptdatafromprinter(new UiExecute() {
                        @Override
                        public void onsucess() {
                            printButton.setEnabled(true);
                            printButton.setBackgroundResource(R.drawable.print);
                        }

                        @Override
                        public void onfailed() {
                            ISCONNECT=false;
                            showSnackbar("Printer Connection Failed");
                            Intent intent=new Intent();
                            intent.setAction(DISCONNECT);
                            sendBroadcast(intent);
                            printButton.setEnabled(false);
                            printButton.setBackgroundResource(R.drawable.close);
                        }
                    });
                }
                @Override
                public void onfailed() {
                    //Execution of the connection in the UI thread after the failure of the connection
                    ISCONNECT=false;
                    showSnackbar("Printer connection failed");
                    printButton.setEnabled(false);
                    printButton.setBackgroundResource(R.drawable.close);
                }
            });
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.printbtn:
                PrintWifi();
                alert.show();
                break;
            case R.id._clr:
                if(priceEditText.isFocused()){
                    String sr = priceEditText.getText().toString();
                    if(sr.length()>0){
                        sr = sr.substring(0,sr.length()-1);
                        priceEditText.setText(sr);
                    }
                }
                else if(qtyEditText.isFocused()){
                    String sr = qtyEditText.getText().toString();
                    if(sr.length()>0){
                        sr = sr.substring(0,sr.length()-1);
                        qtyEditText.setText(sr);
                    }
                }
                break;
            case  R.id._dot:
                if(qtyEditText.isFocused()){
                    priceEditText.requestFocus();
                    break;
                }
                if(priceEditText.isFocused()){
                    if(qtyEditText.getText().toString().isEmpty()  ){
                        showCustomDialog("Warning","Enter Valid Quantity");
                    }
                    else {
                        progressBar.show();
                        snonumber++;
                        Items item =new Items();
                        item.setPrice(priceEditText.getText().toString());
                        item.setQty(qtyEditText.getText().toString());
                        item.setSno(String.valueOf(snonumber));
                        double priced = Double.parseDouble(item.getPrice());
                        double qtyd = Double.parseDouble(item.getQty());
                        double amt = priced*qtyd;
                        String amtstr = String.format("%.0f", amt);
                        item.setAmt(amtstr);
                        LoadGrid(item);
                        progressBar.hide();
                        qtyEditText.requestFocus();
                    }
                }
                break;
            default:
                String addstr = getResources().getResourceEntryName(view.getId());
                addstr =  addstr.replace("_","");
                if(priceEditText.isFocused()){
                    String sr = priceEditText.getText().toString();
                    sr+=addstr;
                    priceEditText.setText(sr);
                }
                else if(qtyEditText.isFocused()){
                    String sr = qtyEditText.getText().toString();
                    sr+=addstr;
                    qtyEditText.setText(sr);
                }
                break;
        }
    }
    private void LoadGrid(Items item){
        dynamicView = new DynamicView(this.getApplicationContext());
        gridLayout.addView(dynamicView.snoTextView(getApplicationContext(),item.getSno()));
        gridLayout.addView(dynamicView.qtyTextView(getApplicationContext(),item.getQty()));
        gridLayout.addView(dynamicView.priceTextView(getApplicationContext(),item.getPrice()));
        gridLayout.addView(dynamicView.amtTextView(getApplicationContext(),item.getAmt()));
        itemsList.add(item);
        double totalAmt = 0;
        for(int i=0;i<itemsList.size();i++){
            double amt = Double.parseDouble(itemsList.get(i).getAmt());
            totalAmt+=amt;
        }
        String totalstr = String.format("%.0f", totalAmt);
        totalAmtTextView.setText(totalstr);
        priceEditText.setText("");
        qtyEditText.setText("");
    }
}