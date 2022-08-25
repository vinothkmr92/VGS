package com.example.easypos;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.os.IBinder;

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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("disbinder","disconnected");
        }
    };
    CoordinatorLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printButton = (Button) findViewById(R.id.printbtn);
        printButton.setOnClickListener(this);
        Intent intent=new Intent(this, PosprinterService.class);
        boolean isconnected = bindService(intent, conn, BIND_AUTO_CREATE);


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
        printText("ok da punda");
    }
    private void printText(String texttoPrint){

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
                            byte[] data1= StringUtils.strTobytes(str);
                            list.add(data1);
                            //should add the command of print and feed line,because print only when one line is complete, not one line, no print
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            //cut pager
                            list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));
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

        String ipAddress="192.168.1.100";
        if (ipAddress.equals(null)||ipAddress.equals("")){


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
}