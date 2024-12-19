package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.request.android.RequestHandler;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PrinterUtil {
    private Context context;
    private Thread hThread;
    private WiFiPort wifiPort;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public ESCPOSPrinter posPtr;
    private int rtn;
    private boolean printSale;
    public boolean onlyBill;
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    private BluetoothPort bluetoothPort;
    private boolean isWifi;
    // 0x1B
    private final char ESC = ESCPOS.ESC;
    private final char ESC2 = ESCPOS.SP;
    public  PrinterUtil(Context cntx,boolean prtSale,boolean iswifi) {
        posPtr=new ESCPOSPrinter();
        isWifi = iswifi;
        if(isWifi){
            wifiPort = WiFiPort.getInstance();
        }
        else {
            bluetoothPort = BluetoothPort.getInstance();
        }
        context = cntx;
        printSale = prtSale;
    }
    @Override
    protected void finalize() throws Throwable {
        try
        {
            if(bluetoothPort!=null){
                bluetoothPort.disconnect();
            }
            if(wifiPort != null){
                wifiPort.disconnect();
            }
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
        super.finalize();
    }
    public int PrintBillData() throws InterruptedException
    {
        try
        {
            if(onlyBill){
                if(Common.includeMRPinReceipt){
                    PrintBillWithMRP();
                }
                else{
                    PrintBill();
                }

            }
            else {
                int copiesprinted = Common.billcopies;
                while (copiesprinted>0){
                    if(Common.includeMRPinReceipt){
                        PrintBillWithMRP();
                    }
                    else{
                        PrintBill();
                    }
                    copiesprinted--;
                }
            }
            if(bluetoothPort!=null){
                bluetoothPort.disconnect();
            }
            if(wifiPort != null){
                wifiPort.disconnect();
            }
            return 1;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            HomeActivity.getInstance().showCustomDialog("Error",e.getMessage());
        }
        return 0;
    }
    private void PrintBillWithMRP() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
        String dateStr = format.format(Common.billDate);
        Bitmap bitmapIcon = Common.shopLogo;
        if(bitmapIcon!=null){
            try {
                posPtr.printBitmap(bitmapIcon,1,400);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(onlyBill){
            posPtr.printNormal(ESC+"|cA"+ESC+"|2CCOPY BILL\r\n");
        }
        posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+Common.headerMeg+"\r\n");
        posPtr.printNormal(ESC+"|cA"+Common.addressline+"\r\n");
        posPtr.printNormal("\n");
        posPtr.printNormal(ESC+"|lABILL NO  : "+Common.billNo+"\n");
        if(!Common.waiter.isEmpty() && !Common.waiter.equals("NONE")){
            posPtr.printNormal(ESC+"|lANAME     : "+Common.waiter);
            posPtr.printNormal("\n");
        }
        posPtr.printNormal(ESC+"|lADATE     : "+dateStr+"\n\n");
        posPtr.printNormal("----------------------------------------------\n");
        posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME       QTY    MRP     PRICE    AMOUNT\n");
        posPtr.printNormal("----------------------------------------------\n");
        double totalAmt = 0d;
        double mrpTotalAmt = 0d;
        double discountAmt = 0d;
        DecimalFormat formater = new DecimalFormat("#.###");
        for(int k=0;k<Common.itemsCarts.size();k++){
            String name = Common.itemsCarts.get(k).getItem_Name();
            String qty = formater.format(Common.itemsCarts.get(k).getQty());
            String price = formater.format(Common.itemsCarts.get(k).getPrice());
            String mrp = formater.format(Common.itemsCarts.get(k).getMRP());
            double mrpd = Common.itemsCarts.get(k).getMRP();
            Double amt = Common.itemsCarts.get(k).getPrice()*Common.itemsCarts.get(k).getQty();
            totalAmt+=amt;
            double mrpamt = mrpd*Common.itemsCarts.get(k).getQty();
            mrpTotalAmt+=mrpamt;
            String amts=String.format("%.0f",amt);
            qty = StringUtils.leftPad(qty,19);
            mrp = StringUtils.leftPad(mrp,7);
            price = StringUtils.leftPad(price,10);
            amts = StringUtils.leftPad(amts,10);
            String line = qty+mrp+price+amts+"\n";
            Bitmap xb = getMultiLangTextAsImage(name, 24, Typeface.DEFAULT);
            if(xb!=null){
                posPtr.printBitmap(xb,0);
            }
            posPtr.printNormal(line);
            posPtr.lineFeed(1);
        }
        posPtr.printNormal("----------------------------------------------\n");
        String totalamt = String.format("%.0f",totalAmt);
        String mrptotalStr = String.format("%.0f",mrpTotalAmt);
        discountAmt = mrpTotalAmt-totalAmt;
        String discountAmtStr = formater.format(discountAmt);
        String txttotal = "TOTAL: "+totalamt+"/-";
        String mrptxt = "MRP TOTAL: "+mrptotalStr+"/-";
        String discountTxt = "DISCOUNT AMT: "+discountAmtStr+"/-";
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|rA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|1C"+mrptxt+"\n");
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|1C"+discountTxt+"\n");
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|cA"+Common.footerMsg+"\n");
        posPtr.lineFeed(5);
        posPtr.cutPaper();
    }

    Bitmap getMultiLangTextAsImage(String text, float textSize, Typeface typeface)  {
        try{
            TextPaint mPaint = new TextPaint();
            mPaint.setColor(Color.BLACK);
            if (typeface != null) mPaint.setTypeface(typeface);
            mPaint.setTextSize(textSize);
            Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
            int widthm = Common.RptSize.equals("2") ? 400:500;
            StaticLayout mStaticLayout = new StaticLayout(text, mPaint, widthm, alignment, 0, 0, true);
            int width = mStaticLayout.getWidth();
            int height = mStaticLayout.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            mStaticLayout.draw(canvas);
            return bitmap;
        }
        catch (Exception ex){
            return  null;
        }

    }
    private  int GetPrinterStatus() {
        int sts = 0;
        try
        {
            posPtr.setAsync(true);
            rtn = posPtr.printerSts();
            sts = rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            sts = -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
            sts = -1;
        }
        return sts;
    }
    private void PrintBill() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
        String dateStr = format.format(Common.billDate);
        Bitmap bitmapIcon = Common.shopLogo;
        if(bitmapIcon!=null){
            try {
                if(Common.RptSize.equals("2")){
                    posPtr.printBitmap(bitmapIcon,1,400);
                }
                else {
                    posPtr.printBitmap(bitmapIcon,1,500);
                }
                posPtr.lineFeed(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(onlyBill){
            posPtr.printNormal(ESC+"|cA"+ESC+"|2CCOPY BILL\r\n");
        }
        DecimalFormat formater = new DecimalFormat("#.###");
        posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+Common.headerMeg+"\r\n");
        posPtr.printNormal(ESC+"|cA"+Common.addressline+"\r\n");
        posPtr.printNormal("\n");
        posPtr.printNormal(ESC+"|lABILL NO  : "+Common.billNo+"\n");
        if(!Common.waiter.isEmpty() && !Common.waiter.equals("NONE")){
            posPtr.printNormal(ESC+"|lANAME     : "+Common.waiter);
            posPtr.printNormal("\n");
        }
        posPtr.printNormal(ESC+"|lADATE     : "+dateStr+"\n\n");
        if(Common.RptSize.equals("2")){
            posPtr.printNormal("--------------------------------");
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM        QTY    RATE   AMOUNT\n");
            posPtr.printNormal("--------------------------------");
        }
        else {
            posPtr.printNormal("----------------------------------------------\n");
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME             QTY      PRICE    AMOUNT\n");
            posPtr.printNormal("----------------------------------------------\n");
        }

        double totalAmt = 0d;
        double billAmt = 0d;
        posPtr.setAlignment(0);
        for(int k=0;k<Common.itemsCarts.size();k++){
            String name = Common.itemsCarts.get(k).getItem_Name();
            String qty = formater.format(Common.itemsCarts.get(k).getQty());
            String price = formater.format(Common.itemsCarts.get(k).getPrice());
            Double amt = Common.itemsCarts.get(k).getPrice()*Common.itemsCarts.get(k).getQty();
            billAmt+=amt;
            String amts=String.format("%.0f",amt);
            String line = "";
            if(Common.RptSize.equals("2")){
                if(name.length()>33){
                    name = name.substring(0,32);
                }
                qty = StringUtils.leftPad(qty,15);
                price = StringUtils.leftPad(price,8);
                amts = StringUtils.leftPad(amts,9);
                line = qty+price+amts+"\n";
            }
            else {
                if(name.length()>47){
                    name = name.substring(0,46);
                }
                qty = StringUtils.leftPad(qty,25);
                price = StringUtils.leftPad(price,11);
                amts = StringUtils.leftPad(amts,10);
                line = qty+price+amts+"\n";
            }
            if(Common.MultiLang){
                Bitmap xb = getMultiLangTextAsImage(name, 24, Typeface.DEFAULT);
                if(xb!=null){
                    posPtr.printBitmap(xb,0);
                }
                else {
                    posPtr.printNormal(name+"\n");
                }
            }
            else {
                posPtr.printNormal(name+"\n");
            }
            posPtr.printNormal(line);
        }
        if(Common.RptSize.equals("2")){
            posPtr.printNormal("--------------------------------");
        }
        else {
            posPtr.printNormal("----------------------------------------------\n");
        }
        if(Common.discount>0){
            String tt = String.format("%.0f",billAmt);
            tt = StringUtils.leftPad(tt,6);
            String discount = "(-)"+formater.format(Common.discount);
            discount = StringUtils.leftPad(discount,6);
            String ttstring = "TOTAL:";
            String discstring = "DISCOUNT:";
            if(Common.RptSize.equals("2")){

                ttstring = StringUtils.leftPad(ttstring,26);
                discstring = StringUtils.leftPad(discstring,26);
            }
            else {
                ttstring = StringUtils.leftPad(ttstring,40);
                discstring = StringUtils.leftPad(discstring,40);
            }
            posPtr.printNormal(ttstring+tt+"\n");
            posPtr.printNormal(discstring+discount+"\n");
            posPtr.lineFeed(1);
        }
        totalAmt = billAmt-Common.discount;
        String totalamt = String.format("%.0f",totalAmt);
        String txttotal = "NET TOTAL: "+totalamt+"/-";
        posPtr.lineFeed(1);
        if(Common.RptSize.equals("2")){
            posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|1C"+txttotal+"\n");
        }
        else {
            posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
        }
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|cA"+Common.footerMsg+"\n");
        posPtr.lineFeed(4);
        posPtr.cutPaper();
    }
    public int PrintKOT() throws InterruptedException
    {
        try
        {
            DecimalFormat formater = new DecimalFormat("#.###");
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
            Date date = new Date();
            String dateStr = format.format(date);
            posPtr.printNormal(ESC+"|cA"+ESC+"|2CKOT\r\n");
            posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+Common.headerMeg+"\r\n");
            posPtr.printNormal("\n");
            posPtr.printNormal(ESC+"|lABILL NO  : "+Common.billNo+"\n");
            if(!Common.waiter.isEmpty() && !Common.waiter.equals("NONE")){
                posPtr.printNormal(ESC+"|lANAME     : "+Common.waiter);
                posPtr.printNormal("\n");
            }

            posPtr.printNormal(ESC+"|lADATE     : "+dateStr+"\n\n");
            if(Common.RptSize.equals("2")){
                posPtr.printNormal("--------------------------------");
                posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME                    QTY\n");
                posPtr.printNormal("--------------------------------");
            }
            else {
                posPtr.printNormal("----------------------------------------------\n");
                posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME                                   QTY\n");
                posPtr.printNormal("----------------------------------------------\n");
            }
            for(int k=0;k<Common.itemsCarts.size();k++){
                String name = Common.itemsCarts.get(k).getItem_Name();
                String qty = formater.format(Common.itemsCarts.get(k).getQty());
                if(Common.RptSize.equals("2")){
                    qty = StringUtils.leftPad(qty,32);
                }
                else{
                    qty = StringUtils.leftPad(qty,46);
                }
                String line = qty+"\n";
                if(Common.MultiLang){
                    Bitmap xb = getMultiLangTextAsImage(name, 24, Typeface.DEFAULT);
                    if(xb!=null){
                        posPtr.printBitmap(xb,0);
                    }
                    else {
                        posPtr.printNormal(name+"\n");
                    }
                }
                else {
                    posPtr.printNormal(name+"\n");
                }
                posPtr.printNormal(line);
            }
            posPtr.lineFeed(4);
            posPtr.cutPaper();
            if(bluetoothPort!=null){
                bluetoothPort.disconnect();
            }
            if(wifiPort != null){
                wifiPort.disconnect();
            }
            return 1;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            HomeActivity.getInstance().showCustomDialog("Error",e.getMessage());
        }
        return 0;
    }
    public int PrintItemWiseSaleReport() throws InterruptedException
    {

        try
        {
            DecimalFormat formater = new DecimalFormat("#.###");
            posPtr.printNormal(ESC+"|cA"+ESC+"|2CITEM WISE REPORT\r\n");
            posPtr.printNormal("\n");
            posPtr.printNormal(ESC+"|lAFROM DATE: "+Common.saleReportFrmDate+"\n");
            posPtr.printNormal(ESC+"|lATO   DATE: "+Common.saleReportToDate+"\n\n");
            if(Common.RptSize.equals("2")){
                posPtr.printNormal("--------------------------------");
                posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME                    QTY\n");
                posPtr.printNormal("--------------------------------");
            }
            else {
                posPtr.printNormal("----------------------------------------------\n");
                posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME                                  QTY\n");
                posPtr.printNormal("----------------------------------------------\n");
            }

            for(int k=0;k<Common.itemsRpts.size();k++){
                ItemsRpt itemsRpt = Common.itemsRpts.get(k);
                String itemname = itemsRpt.getItemName();
                Double qty = itemsRpt.getQuantity();
                String qtystr=formater.format(qty);
                if(Common.RptSize.equals("2")){
                    qtystr = StringUtils.leftPad(qtystr,32);
                }
                else {
                    qtystr = StringUtils.leftPad(qtystr,46);
                }
                String line = qtystr+"\n";
                if(Common.MultiLang){
                    Bitmap xb = getMultiLangTextAsImage(itemname, 24, Typeface.DEFAULT);
                    if(xb!=null){
                        posPtr.printBitmap(xb,0);
                    }
                    else {
                        posPtr.printNormal(itemname+"\n");
                    }
                }
                else {
                    posPtr.printNormal(itemname+"\n");
                }
                posPtr.printNormal(line);
            }
            posPtr.lineFeed(5);
            posPtr.cutPaper();
            if(bluetoothPort!=null){
                bluetoothPort.disconnect();
            }
            if(wifiPort != null){
                wifiPort.disconnect();
            }
            return 1;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            HomeActivity.getInstance().showCustomDialog("Error",e.getMessage());
        }
        return 0;
    }
    public int PrintSaleReport() throws InterruptedException
    {

        try
        {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mmaaa", Locale.getDefault());
            posPtr.printNormal(ESC+"|cA"+ESC+"|2CSALE REPORT\r\n");
            posPtr.printNormal("\n");
            posPtr.printNormal(ESC+"|lAFROM DATE: "+Common.saleReportFrmDate+"\n");
            posPtr.printNormal(ESC+"|lATO   DATE: "+Common.saleReportToDate+"\n\n");
            if(Common.RptSize.equals("2")){
                posPtr.printNormal("--------------------------------");
                posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"BILL NO     BILL_DATE     AMOUNT\n");
                posPtr.printNormal("--------------------------------");
            }
            else {
                posPtr.printNormal("----------------------------------------------\n");
                posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"BILL NO         BILL_DATE               AMOUNT\n");
                posPtr.printNormal("----------------------------------------------\n");
            }
            double totalAmt = 0d;
            for(int k=0;k<Common.saleReports.size();k++){
                SaleReport saleReport = Common.saleReports.get(k);
                String billno = saleReport.getBillNo();
                Date bDate = saleReport.getBillDt();
                String billDate = format.format(bDate);
                Double amt = saleReport.getBillAmount();
                totalAmt+=amt;
                String amts=String.format("%.0f",amt);
                if(Common.RptSize.equals("2")){
                    billno = StringUtils.rightPad(billno,7);
                    billDate = StringUtils.leftPad(billDate,14);
                    amts = StringUtils.leftPad(amts,7);
                }
                else {
                    billno = StringUtils.rightPad(billno,7);
                    billDate = StringUtils.leftPad(billDate,18);
                    amts = StringUtils.leftPad(amts,21);
                }
                String line = billno+billDate+amts+"\n";
                posPtr.printNormal(line);
            }
            String totalamt = String.format("%.0f",totalAmt);
            String txttotal = "TOTAL AMOUNT: "+totalamt+"/-";
            posPtr.lineFeed(1);
            if(Common.RptSize.equals("2")){
                posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|1C"+txttotal+"\n");
            }
            else {
                posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
            }
            posPtr.lineFeed(4);
            posPtr.cutPaper();
            if(bluetoothPort!=null){
                bluetoothPort.disconnect();
            }
            if(wifiPort != null){
                wifiPort.disconnect();
            }
            return 1;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            HomeActivity.getInstance().showCustomDialog("Error",e.getMessage());
        }
        return 0;
    }



    public void Print() throws Exception{
        try{
            if(isWifi){
                new ConnectPrinter().execute(Common.printerIP);
            }
            else {
                BluetoothDevice btDevice = GetBluetoothDevice();
                new ConnectToBluetoothPrinter().execute(btDevice);
            }
        }
        catch (Exception ex){
            throw  ex;
        }
    }
    private boolean checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int bluetooth = ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT);
            int scan = ContextCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        } else {
            int bluetooth = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
            int scan = ContextCompat.checkSelfPermission(context,Manifest.permission.BLUETOOTH_SCAN);
            return bluetooth == PackageManager.PERMISSION_GRANTED && scan == PackageManager.PERMISSION_GRANTED;
        }
    }
    public BluetoothDevice GetBluetoothDevice() throws Exception {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!checkBluetoothPermission()) {
            ActivityCompat.requestPermissions(HomeActivity.getInstance(), PERMISSIONS_BLUETOOTH
                    , 1);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice mydevice =null;
        for (BluetoothDevice device : pairedDevices) {
            // Add the name and address to an array adapter to show in a ListView
            if (device.getName().contains(Common.bluetoothDeviceName)) {
                mydevice = device;
                break;
            }
        }

        return  mydevice;
    }
    class ConnectPrinterKOT extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        @Override
        protected void onPreExecute()
        {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Printing.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String retVal = null;
            try
            {
                // ip
                wifiPort.connect(params[0]);
                retVal="CONNECTED";
            }
            catch (Exception e)
            {
                Log.e("Wifi-connection:",e.getMessage(),e);
                retVal = "ERROR:"+e.getMessage();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("CONNECTED"))
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                posPtr = new ESCPOSPrinter();
                posPtr.setAsync(true);
                try{
                    PrintKOT();
                }
                catch (Exception ex)
                {
                    HomeActivity.getInstance().showCustomDialog("Error",ex.getMessage());
                }
                finally {
                    HomeActivity.getInstance().RefreshViews();
                    if(dialog.isShowing())
                        dialog.dismiss();
                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                HomeActivity.getInstance().showCustomDialog("Failed to Connect Printer",result);
            }
            super.onPostExecute(result);
        }
    }
    class ConnectToBluetoothPrinterKOT extends AsyncTask<BluetoothDevice, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        @Override
        protected void onPreExecute()
        {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Printing.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(BluetoothDevice... params)
        {
            String retVal = null;
            try
            {
                // ip
                bluetoothPort.connect(params[0]);
                retVal="CONNECTED";
            }
            catch (Exception e)
            {
                Log.e("Bluetooth-connection:",e.getMessage(),e);
                retVal = "ERROR:"+e.getMessage();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("CONNECTED"))
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                posPtr = new ESCPOSPrinter();
                posPtr.setAsync(true);
                try{
                    PrintKOT();
                }
                catch (Exception ex)
                {
                    HomeActivity.getInstance().showCustomDialog("Error",ex.getMessage());
                }
                finally {
                    HomeActivity.getInstance().RefreshViews();
                    if(dialog.isShowing())
                        dialog.dismiss();
                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                HomeActivity.getInstance().showCustomDialog("Failed to Connect Printer",result);
            }
            super.onPostExecute(result);
        }
    }
    class ConnectPrinter extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        @Override
        protected void onPreExecute()
        {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Printing.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String retVal = null;
            try
            {
                // ip
                wifiPort.connect(params[0]);
                retVal="CONNECTED";
            }
            catch (Exception e)
            {
                Log.e("Wifi-connection:",e.getMessage(),e);
                retVal = "ERROR:"+e.getMessage();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("CONNECTED"))
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                posPtr = new ESCPOSPrinter();
                posPtr.setAsync(true);
                try{
                        if(Common.isItemWiseRptBill){
                            PrintItemWiseSaleReport();
                        }
                        else if(printSale){
                            PrintBillData();
                        }
                        else{
                            PrintSaleReport();
                        }
                }
                catch (Exception ex)
                {
                      HomeActivity.getInstance().showCustomDialog("Error",ex.getMessage());
                }
                finally {
                    if(Common.isItemWiseRptBill){
                        Common.isItemWiseRptBill = false;
                    }
                    if(dialog.isShowing())
                        dialog.dismiss();
                    if(!onlyBill && printSale){
                        if(Common.printKOT){
                            new ConnectPrinterKOT().execute(Common.kotprinterIP);
                        }
                        else{
                            HomeActivity.getInstance().RefreshViews();
                        }
                    }

                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                HomeActivity.getInstance().showCustomDialog("Failed to Connect Printer",result);
            }
            super.onPostExecute(result);
        }
    }
    class ConnectToBluetoothPrinter extends AsyncTask<BluetoothDevice, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        BluetoothDevice btdevice = null;
        @Override
        protected void onPreExecute()
        {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Printing.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(BluetoothDevice... params)
        {
            String retVal = null;
            try
            {
                btdevice = params[0];
                if(bluetoothPort.isConnected()){
                    bluetoothPort.disconnect();
                }
                bluetoothPort.connect(btdevice);
                retVal="CONNECTED";
            }
            catch (Exception e)
            {
                Log.e("Bluetooth-connection:",e.getMessage(),e);
                retVal = "ERROR:"+e.getMessage();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("CONNECTED"))
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                posPtr = new ESCPOSPrinter();
                posPtr.setAsync(true);
                try{
                    if(Common.isItemWiseRptBill){
                        PrintItemWiseSaleReport();
                    }
                    else if(printSale){
                        PrintBillData();
                    }
                    else{
                        PrintSaleReport();
                    }
                }
                catch (Exception ex)
                {
                    HomeActivity.getInstance().showCustomDialog("Error",ex.getMessage());
                }
                finally {
                    if(Common.isItemWiseRptBill){
                        Common.isItemWiseRptBill = false;
                    }
                    if(dialog.isShowing())
                        dialog.dismiss();
                    if(!onlyBill && printSale){
                        if(Common.printKOT){
                            new ConnectToBluetoothPrinterKOT().execute(btdevice);
                        }
                        else{
                            HomeActivity.getInstance().RefreshViews();
                        }
                    }

                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                HomeActivity.getInstance().showCustomDialog("Failed to Connect Printer",result);
            }
            super.onPostExecute(result);
        }
    }
}
