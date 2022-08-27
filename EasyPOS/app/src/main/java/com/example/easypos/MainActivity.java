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
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos80;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button printButton;
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
            connetNet();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = new Dialog(MainActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading...");
        printButton = (Button) findViewById(R.id.printbtn);
        printButton.setOnClickListener(this);
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
        priceEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)){
                    qtyEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        qtyEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    String qtystr = qtyEditText.getText().toString();
                    String pricestr = priceEditText.getText().toString();
                    if(qtystr.isEmpty()){
                        priceEditText.setFocusable(true);
                        priceEditText.requestFocus();
                    }
                    else{
                        if(pricestr.isEmpty()){
                            showSnackbar("Please Enter Price Value");
                        }
                        else{
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
                        }

                    }
                    priceEditText.setFocusableInTouchMode(true);
                    priceEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

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
        printHeaderText(companyname,1);
        printAddress();
        String head = "SNO        PRICE          QTY          AMOUNT";
        printTextHeaderSno(head);
        for(int i=0;i<itemsList.size();i++){
            Items item = itemsList.get(i);
            String sno = org.apache.commons.lang3.StringUtils.rightPad(item.getSno(),12," ");
            String price = org.apache.commons.lang3.StringUtils.rightPad(item.getPrice(),15," ");
            String qty = org.apache.commons.lang3.StringUtils.rightPad(item.getQty(),15," ");
            String amt = org.apache.commons.lang3.StringUtils.rightPad(item.getAmt(),5," ");
            String line = sno+price+qty+amt;
            printText(line,false);
        }
        String totalamt = totalAmtTextView.getText().toString();
        String txttotal = "TOTAL: "+totalamt+"/-";
        printText(" ",false);
        printHeaderText(txttotal,2);
        printText(" ",false);
        gridLayout.removeViews(4,itemsList.size()*4);
        itemsList.clear();
        totalAmtTextView.setText("000");
        printText(" ",true);
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
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
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
                    //in this ,you could call acceptdatafromprinter(),when disconnect ,will execute onfailed();
                    binder.acceptdatafromprinter(new UiExecute() {
                        @Override
                        public void onsucess() {

                        }

                        @Override
                        public void onfailed() {
                            ISCONNECT=false;
                            showSnackbar("Printer Connection Failed");
                            Intent intent=new Intent();
                            intent.setAction(DISCONNECT);
                            sendBroadcast(intent);
                        }
                    });
                }
                @Override
                public void onfailed() {
                    //Execution of the connection in the UI thread after the failure of the connection
                    ISCONNECT=false;
                    showSnackbar("Printer connection failed");
                }
            });

        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.printbtn:
                PrintWifi();
                break;
        }
    }
    private void LoadGrid(Items item){
        dynamicView = new DynamicView(this.getApplicationContext());
        gridLayout.addView(dynamicView.snoTextView(getApplicationContext(),item.getSno()));
        gridLayout.addView(dynamicView.priceTextView(getApplicationContext(),item.getPrice()));
        gridLayout.addView(dynamicView.qtyTextView(getApplicationContext(),item.getQty()));
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