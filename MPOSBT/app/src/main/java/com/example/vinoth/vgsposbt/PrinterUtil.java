package com.example.vinoth.vgsposbt;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.USBPort;
import com.sewoo.port.android.USBPortConnection;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.request.android.RequestHandler;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class PrinterUtil {
    private static final String ACTION_USB_PERMISSION = "com.example.vinoth.vgspos.USB_PERMISSION";
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    // 0x1B
    private final char ESC = ESCPOS.ESC;
    private final char ESC2 = ESCPOS.SP;
    public ESCPOSPrinter posPtr;
    public boolean onlyBill;
    private Context context;
    private Thread hThread;
    private WiFiPort wifiPort;
    private int rtn;
    private boolean printSale;
    private BluetoothPort bluetoothPort;
    public ReceiptData receiptData;
    private boolean isWifi;
    public ArrayList<ItemsRpt> itemsRpts;
    public ArrayList<SaleReport> saleReports;
    private USBPort usbPort;
    private boolean isUsbport;
    private UsbDevice usbDevice;
    private UsbManager usbManager;
    private Activity activity;
    public boolean isItemWiseRptBill;
    private boolean receivedBrodCast;
    public Integer headCount;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
                        usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);


                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null && !receivedBrodCast) {
                            // YOUR PRINT CODE HERE
                            receivedBrodCast = true;
                            usbPort = new USBPort(usbManager);
                            new ConnectUSBPrinter().execute();
                        }
                    }
                    /*if (usbManager != null && usbDevice != null && !receivedBrodCast) {
                        // YOUR PRINT CODE HERE
                        receivedBrodCast = true;
                        usbPort = new USBPort(usbManager);
                        new ConnectUSBPrinter().execute();
                    }*/
                }
            }
        }
    };

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
    public  PrinterUtil(Context cntx,Activity act,boolean prtSale){
        activity = act;
        posPtr=new ESCPOSPrinter();
        headCount = 0;
        switch (Common.printType){
            case "WIFI":
                isWifi = true;
                wifiPort = WiFiPort.getInstance();
                break;
            case "USB":
                isUsbport = true;
                break;
            default:
                bluetoothPort = BluetoothPort.getInstance();
                break;
        }
        context = cntx;
        printSale = prtSale;
    }

    public int PrintBillData(ReceiptData rcptData) throws InterruptedException
    {
        try
        {
            if(onlyBill){
                if(Common.includeMRPinReceipt){
                    PrintBillWithMRP(rcptData);
                }
                else{
                    PrintBill(rcptData);
                }

            }
            else {
                int copiesprinted = Common.billcopies;
                while (copiesprinted>0){
                    if(Common.includeMRPinReceipt){
                        PrintBillWithMRP(rcptData);
                    }
                    else{
                        PrintBill(rcptData);
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

    Bitmap getMultiLangTextAsImage(String text, float textSize, Typeface typeface)  {
        try{
            TextPaint mPaint = new TextPaint();
            mPaint.setColor(Color.BLACK);
            if (typeface != null) mPaint.setTypeface(typeface);
            mPaint.setTextSize(textSize);
            Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
            int widthm = Common.RptSize.equals("2") ? 400:600;
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
    Bitmap getTextAsImage(String text, float textSize,Layout.Alignment alignment,Typeface tp)  {
        try{
            TextPaint mPaint = new TextPaint();
            mPaint.setColor(Color.BLACK);
            if(tp==null){
                Typeface typeface = ResourcesCompat.getFont(context,R.font.orienta);
                Typeface boldTypeface = Typeface.create(typeface,Typeface.BOLD);
                mPaint.setTypeface(boldTypeface);
            }
            else {
                mPaint.setTypeface(tp);
            }
            mPaint.setTextSize(textSize);
            int widthm = Common.RptSize.equals("2") ? 380:600;
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

    private void PrintBillWithMRP(ReceiptData rcptData) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
        String dateStr = format.format(rcptData.billDate);
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

        Bitmap header = getTextAsImage(Common.headerMeg,30, Layout.Alignment.ALIGN_CENTER,null);
        if(header!=null){
            posPtr.printBitmap(header,0);
        }
        else {
            posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+Common.headerMeg+"\r\n");
        }
        posPtr.printNormal(ESC+"|cA"+Common.addressline+"\r\n");
        posPtr.printNormal("\n");
        posPtr.printNormal(ESC+"|lABILL NO  : "+rcptData.billno+"\n");
        if(!rcptData.waiter.isEmpty() && !rcptData.waiter.equals("NONE")){
            posPtr.printNormal(ESC+"|lANAME     : "+rcptData.waiter);
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
        for(int k=0;k<rcptData.itemsCarts.size();k++){
            String name = rcptData.itemsCarts.get(k).getItem_Name();
            String qty = formater.format(rcptData.itemsCarts.get(k).getQty());
            String price = formater.format(rcptData.itemsCarts.get(k).getPrice());
            Double amt = rcptData.itemsCarts.get(k).getPrice()* rcptData.itemsCarts.get(k).getQty();
            totalAmt+=amt;
            double mrpamt = rcptData.itemsCarts.get(k).getQty();
            mrpTotalAmt+=mrpamt;
            String amts=String.format("%.0f",amt);
            qty = StringUtils.leftPad(qty,19);
            String mrp = StringUtils.leftPad("",7);
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
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        String totalamt = formatter.format(totalAmt).replace(symbol,symbol+" ");
        String mrptotalStr = formatter.format(mrpTotalAmt).replace(symbol,symbol+" ");
        discountAmt = mrpTotalAmt-totalAmt;
        String discountAmtStr = formatter.format(discountAmt).replace(symbol,symbol+" ");
        String txttotal = "Grand Total  "+totalamt+"/-";
        String mrptxt = "MRP Total  "+mrptotalStr;
        String discountTxt = "Discount Amt  "+discountAmtStr;
        posPtr.lineFeed(1);
        Typeface typeface = ResourcesCompat.getFont(context,R.font.orienta);
        Bitmap mbp = getTextAsImage(mrptxt,25, Layout.Alignment.ALIGN_NORMAL,typeface);
        if(mbp!=null){
            posPtr.printBitmap(mbp,0);
        }
        else {
            posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|1C"+mrptxt+"\n");
        }
        if(discountAmt>0){
            Bitmap dbp = getTextAsImage(discountTxt,25, Layout.Alignment.ALIGN_NORMAL,typeface);
            if(dbp!=null){
                posPtr.printBitmap(dbp,0);
            }
            else {
                posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|1C"+discountTxt+"\n");
            }
        }
        posPtr.lineFeed(1);
        Bitmap bp = getTextAsImage(txttotal,30, Layout.Alignment.ALIGN_CENTER,null);
        if(bp!=null){
            posPtr.printBitmap(bp,0);
        }
        else {
            posPtr.printNormal(ESC+"|rA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
        }
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|cA"+Common.footerMsg+"\n");
        posPtr.lineFeed(5);
        posPtr.cutPaper();
    }

    private void PrintBill(ReceiptData rcptData) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
        String dateStr = format.format(rcptData.billDate);
        if(onlyBill){
            posPtr.printText("COPY BILL\n",1,1,2);
            //posPtr.printNormal(ESC + "|cA" + ESC + "|1CCOPY BILL\r\n");
        }
        DecimalFormat formater = new DecimalFormat("#.###");
        posPtr.printText(Common.headerMeg+"\n",1,1,2);
        posPtr.printText(Common.addressline+"\n",1,1,2);
        //posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+Common.headerMeg+"\n");
        //posPtr.printNormal(ESC+"|cA"+Common.addressline+"\n\n");
        String user = Common.devUser;
        if(!user.isEmpty()){
            posPtr.printText("Clerk    : "+user+"\n",0,1,1);
            //posPtr.printNormal(ESC+"|bC"+ESC+"|lAClerk    : "+ user+"\n");
        }
        posPtr.printText("Bill No  : "+rcptData.billno+"\n",0,1,1);
        //posPtr.printNormal(ESC+"|bC"+ESC+"|lABill No  : "+ rcptData.billno+"\n");
        if(rcptData.selectedCustomer!=null){
            posPtr.printText("Customer : "+ rcptData.selectedCustomer.getCustomerName()+"\n",0,1,1);
            posPtr.printText("ID & Ward: "+rcptData.selectedCustomer.getMobileNumber()+" - "+rcptData.selectedCustomer.getAddress()+"\n",0,1,1);
            //posPtr.printNormal(ESC+"|bC"+ESC+"|lACustomer : "+ rcptData.selectedCustomer.getCustomerName()+"\n");
            //posPtr.printNormal(ESC+"|bC"+ESC+"|lAID & Ward: "+rcptData.selectedCustomer.getMobileNumber()+" - "+rcptData.selectedCustomer.getAddress()+"\n");
        }
        posPtr.printText("Date     : "+dateStr+"\n",0,1,1);
        //posPtr.printNormal(ESC+"|bC"+ESC+"|lADate     : "+dateStr+"\n");
        if(Common.RptSize.equals("2")){
            posPtr.printNormal("--------------------------------");
        }
        else {
            posPtr.printNormal("----------------------------------------------\n");
        }


        //posPtr.printNormal("QTY        *          PRICE             AMOUNT\n");
        posPtr.printText("Item Name\n",0,1,1);
        posPtr.printText("Act.Wt   Bin.Wt  Impurities\n",0,1,1);
        //posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|1CItem Name\n");
        //posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|1CAct.Wt   Bin.Wt  Impurities\n");
        if(Common.RptSize.equals("2")){
            posPtr.printText("Qty    *     Price                  Amount\n",0,1,1);
            //posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|1C"+"Qty    *     Price        Amount");
            posPtr.printNormal("--------------------------------");
        }
        else {
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"QTY        *          PRICE             AMOUNT\n");
            posPtr.printNormal("----------------------------------------------\n");
        }
        double totalAmt = 0d;
        double billAmt = 0d;
        posPtr.setAlignment(0);
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        for(int k=0;k<rcptData.itemsCarts.size();k++){
            double deduction = rcptData.itemsCarts.get(k).getDeduction();
            double binWt = rcptData.itemsCarts.get(k).BinWeight;
            String name = rcptData.itemsCarts.get(k).getItem_Name();
            String qty = decimalFormat.format(rcptData.itemsCarts.get(k).getQty());
            String ded = decimalFormat.format(deduction);
            String net = decimalFormat.format(rcptData.itemsCarts.get(k).NetWeight());
            String price = formater.format(rcptData.itemsCarts.get(k).getPrice());
            Double amt = rcptData.itemsCarts.get(k).getPrice()*rcptData.itemsCarts.get(k).NetWeight();
            billAmt+=amt;
            String amts=String.format("%.0f",amt);
            String line = "";
            int column1 = Common.RptSize.equals("2") ? 7:11;
            int column2 = Common.RptSize.equals("2") ? 5:13;
            int column3 = Common.RptSize.equals("2") ? 24:21;
            int namefill = Common.RptSize.equals("2") ? 40:46;
            name = StringUtils.rightPad(name,namefill);
            if(name.length()>namefill){
                name = name.substring(0,namefill);
            }
            String binded = binWt>0 ? "- "+StringUtils.rightPad(decimalFormat.format(binWt),column1):"";
            String dedStr = deduction>0 ? "- "+StringUtils.rightPad(decimalFormat.format(deduction),column1):"";
            String secondline = StringUtils.rightPad(qty,column1)+binded+ dedStr;

            net = StringUtils.rightPad(net,column1)+"*     ";
            //price = StringUtils.leftPad(price,column2);
            amts = StringUtils.leftPad(amts,column3);
            line = net+price+amts+(Common.RptSize.equals("2")?"":"\n");
            posPtr.printText(name+"\n",0,1,1);
            Typeface typeface = ResourcesCompat.getFont(context,R.font.orienta);
            //posPtr.printAndroidFont(typeface,name,380,1,0);

            //posPtr.printNormal(ESC+"|bC"+name+(Common.RptSize.equals("2")?"":"\n"));
            if(deduction>0 || binWt>0){
                posPtr.printText(secondline+"\n",0,1,1);
                //posPtr.printNormal(ESC+"|bC"+secondline+"\n");
            }
            posPtr.printText(line+"\n",0,1,1);
            //posPtr.printNormal(ESC+"|bC"+line);
        }
        if(Common.RptSize.equals("2")){
            posPtr.printNormal("--------------------------------");
        }
        else {
            posPtr.printNormal("----------------------------------------------\n");
        }
        Integer totalItems = rcptData.itemsCarts.size();
        Double totalQty = rcptData.itemsCarts.stream().mapToDouble(c->c.NetWeight()).sum();
        //posPtr.printNormal(ESC+"|bC"+"Total Items: "+totalItems+"\n");
        //posPtr.printNormal(ESC+"|bC"+"Total Qty  : "+decimalFormat.format(totalQty)+" Kg\n");
        posPtr.printText("Total Items : "+totalItems+"\n",0,1,1);
        posPtr.printText("Total Qty   : "+decimalFormat.format(totalQty)+" Kg\n",0,1,1);
        if(rcptData.discount>0 || rcptData.advance>0){
            String tt = String.format("%.0f",billAmt);
            tt = StringUtils.leftPad(tt,6);
            String discount = "(-)"+formater.format(rcptData.discount);
            discount = StringUtils.leftPad(discount,6);
            String advance = "(-)"+formater.format(rcptData.advance);
            advance = StringUtils.leftPad(advance,6);
            String ttstring = "Sub Total:";
            String discstring = "Discount:";
            String advancestring = "Advance:";
            int padleft = Common.RptSize.equals("2") ? 26:40;
            ttstring = StringUtils.leftPad(ttstring,padleft);
            discstring = StringUtils.leftPad(discstring,padleft);
            advancestring= StringUtils.leftPad(advancestring,padleft);
            posPtr.printNormal(ttstring+tt+"\n");
            if(rcptData.discount>0){
                posPtr.printNormal(discstring+discount+"\n");
            }
            if(rcptData.advance>0){
                posPtr.printNormal(advancestring+advance+"\n");
            }
        }
        totalAmt = billAmt-rcptData.discount-rcptData.advance;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        String totalamt = formatter.format(totalAmt).replace(symbol,"Rs. ");
        String txttotal = "Grand Total  "+totalamt+"/-";
        posPtr.lineFeed(1);
        if(Common.RptSize.equals("2")){
            posPtr.printText(txttotal+"\n",1,1,2);
            //posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|1C"+txttotal+"\n");
        }
        else {
            posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
        }
        posPtr.lineFeed(1);
        posPtr.printText(Common.footerMsg+"\n",1,1,2);
        posPtr.lineFeed(2);
        posPtr.cutPaper();
    }

    public int PrintKOT(ReceiptData rcptData) throws InterruptedException
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
            posPtr.printNormal(ESC+"|lABILL NO  : "+rcptData.billno+"\n");
            if(!rcptData.waiter.isEmpty() && !rcptData.waiter.equals("NONE")){
                posPtr.printNormal(ESC+"|lANAME     : "+rcptData.waiter);
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
            for(int k=0;k<rcptData.itemsCarts.size();k++){
                String name = rcptData.itemsCarts.get(k).getItem_Name();
                String qty = formater.format(rcptData.itemsCarts.get(k).getQty());
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

    public int PrintItemWiseSaleReport(ArrayList<ItemsRpt> items) throws InterruptedException
    {
        try
        {
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            DecimalFormat amtFormat = new DecimalFormat("#.##");
            posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|2CITEM WISE REPORT\r\n");
            posPtr.printNormal("\n");
            posPtr.printNormal(ESC+"|bC"+ESC+"|2C"+ESC+"|lAFROM:"+Common.saleReportFrmDate+"\n");
            posPtr.printNormal(ESC+"|bC"+ESC+"|2C"+ESC+"|lATO  :"+Common.saleReportToDate+"\n\n");
            if(Common.RptSize.equals("2")){
                posPtr.printNormal("--------------------------------");
            }
            else {
                posPtr.printNormal("----------------------------------------------\n");
            }
            Double totalAmt = 0d;
            Double totalQty = 0d;
            for(int k=0;k<items.size();k++){
                ItemsRpt itemsRpt = items.get(k);
                String itemid = itemsRpt.getItemID();
                String itemname = itemsRpt.getItemName();
                Double qty = itemsRpt.getQuantity();
                totalQty+=qty;
                Double amt = itemsRpt.getAmount();
                totalAmt+=amt;
                String qtystr=decimalFormat.format(qty);
                String amtStr = amtFormat.format(amt);
                String line =qtystr;
                int len = line.length();
                if(Common.RptSize.equals("2")){
                    line = line+StringUtils.leftPad(amtStr,16-len);
                    itemname = itemname.length()>13 ? itemname.substring(0,13):itemname;
                }
                else {
                    line = line+StringUtils.leftPad(amtStr,46-len);
                    itemname = itemname.length()>40 ? itemname.substring(0,40):itemname;
                }

                String line1 = StringUtils.rightPad(itemid,2)+" "+itemname;
                if(Common.MultiLang){
                    Bitmap xb = getMultiLangTextAsImage(line1, 24, Typeface.DEFAULT);
                    if(xb!=null){
                        posPtr.printBitmap(xb,0);
                    }
                    else {
                        posPtr.printNormal(ESC+"|bC"+ESC+"|2C"+line1+"\n");
                    }
                }
                else {
                    posPtr.printNormal(ESC+"|bC"+ESC+"|2C"+line1+"\n");
                }
                posPtr.printNormal(ESC+"|bC"+ESC+"|2C"+line+"\n\n");

            }
            if(Common.RptSize.equals("2")){
                posPtr.printNormal("--------------------------------");
            }
            else {
                posPtr.printNormal("----------------------------------------------\n");
            }
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            formatter.setMaximumFractionDigits(0);
            String symbol = formatter.getCurrency().getSymbol();
            String totalAmtStr = formatter.format(totalAmt).replace(symbol,symbol+" ");
            String ttAmt = "TOTAL AMOUNT: "+totalAmtStr;
            int splitpaymentSize = 30;
            Typeface typeface = ResourcesCompat.getFont(context,R.font.orienta);
            posPtr.lineFeed(1);
            Bitmap bp = getTextAsImage(ttAmt,splitpaymentSize, Layout.Alignment.ALIGN_NORMAL,typeface);
            if(bp!=null){
                posPtr.printBitmap(bp,0);
            }
            else {
                posPtr.printNormal(ESC+"|rA"+ESC+"|bC"+ESC+"|2C"+ttAmt+"\n");
            }
            String ttWtstr = "TOTAL WT: "+decimalFormat.format(totalQty)+" Kg";
            Bitmap bps = getTextAsImage(ttWtstr,splitpaymentSize, Layout.Alignment.ALIGN_NORMAL,typeface);
            if(bps!=null){
                posPtr.printBitmap(bps,0);
            }
            else {
                posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|2CTOTAL WEIGHTS: "+decimalFormat.format(totalQty)+" Kg\n");
            }
            posPtr.lineFeed(3);
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

    public int PrintSaleReport(ArrayList<SaleReport> sales) throws InterruptedException
    {
        try
        {
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mmaaa", Locale.getDefault());
            Bitmap header = getTextAsImage("SALE REPORT",30, Layout.Alignment.ALIGN_CENTER,null);
            if(header!=null){
                posPtr.printBitmap(header,0);
            }
            else {
                posPtr.printNormal(ESC+"|cA"+ESC+"|1CSALE REPORT\r\n");
            }
            posPtr.printNormal("\n");
            posPtr.printNormal(ESC+"|lAFROM DATE: "+Common.saleReportFrmDate+"\n");
            posPtr.printNormal(ESC+"|lATO   DATE: "+Common.saleReportToDate+"\n\n");
            if(Common.RptSize.equals("2")){
                posPtr.printNormal("--------------------------------");
            }
            else {
                posPtr.printNormal("----------------------------------------------\n");
            }
            double totalAmt = 0d;
            double totalQty = 0d;
            for(int k=0;k<sales.size();k++){
                SaleReport saleReport = sales.get(k);
                String custID = saleReport.CustomerID;
                String name = saleReport.CustomerName;
                String wt = decimalFormat.format(saleReport.totalWt);
                Double amt = saleReport.getBillAmount();
                totalAmt+=amt;
                totalQty+=saleReport.totalWt;
                if(Common.RptSize.equals("2")){
                    custID = StringUtils.leftPad(custID,6);
                    name = name.length()>17 ? name.substring(0,17):StringUtils.rightPad(name,17);
                    wt = StringUtils.leftPad(wt,8);
                }
                else {
                    custID = StringUtils.leftPad(custID,6);
                    name = name.length()>31 ? name.substring(0,31):StringUtils.rightPad(name,31);
                    wt = StringUtils.leftPad(wt,8);
                }
                String line = custID+" "+name+wt+"\n";
                posPtr.printNormal(line);
            }
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            formatter.setMaximumFractionDigits(0);
            String symbol = formatter.getCurrency().getSymbol();
            String totalAmtStr = formatter.format(totalAmt).replace(symbol,symbol+" ");
            String ttAmt = "TOTAL AMOUNT: "+totalAmtStr;
            int splitpaymentSize = 30;
            Typeface typeface = ResourcesCompat.getFont(context,R.font.orienta);
            posPtr.lineFeed(1);
            Bitmap bp = getTextAsImage(ttAmt,splitpaymentSize, Layout.Alignment.ALIGN_NORMAL,typeface);
            if(bp!=null){
                posPtr.printBitmap(bp,0);
            }
            else {
                posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|2C"+ttAmt+"\n");
            }
            String ttWtstr = "TOTAL WT: "+decimalFormat.format(totalQty)+" Kg";
            Bitmap bps = getTextAsImage(ttWtstr,splitpaymentSize, Layout.Alignment.ALIGN_NORMAL,typeface);
            if(bps!=null){
                posPtr.printBitmap(bps,0);
            }
            else {
                posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|2CTOTAL WEIGHTS: "+decimalFormat.format(totalQty)+" Kg\n");
            }
            String headCnt = "HEAD COUNT: "+headCount;
            Bitmap bph = getTextAsImage(headCnt,splitpaymentSize,Layout.Alignment.ALIGN_NORMAL,typeface);
            if(bph!=null){
                posPtr.printBitmap(bph,0);
            }
            else {
                posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|2CHEAD COUNT: "+headCount+"\n");
            }
            posPtr.lineFeed(3);
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
            ActivityCompat.requestPermissions(activity, PERMISSIONS_BLUETOOTH
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

    public void Print() throws Exception{
        try{
            if(isUsbport){
                ConnectUSB();

            }
            else if(isWifi){
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

    public void ConnectUSB() {
        usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> mDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> mDeviceIterator = mDeviceList.values().iterator();
        UsbDevice mDevice = null;
        while (mDeviceIterator.hasNext()) {
            mDevice = mDeviceIterator.next();
            if (mDevice == null) {
                Toast.makeText(activity, "mDevice is null", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, "USB Device found", Toast.LENGTH_SHORT).show();
                if(mDevice.getProductName().equals(Common.usbDeviceName)){
                    break;
                }
            }
        }
        usbDevice = mDevice;
        if (usbManager != null) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    activity,
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    PendingIntent.FLAG_MUTABLE
            );
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            activity.registerReceiver(this.usbReceiver, filter);
            usbManager.requestPermission(mDevice, permissionIntent);
        }
    }

    private void PassMsgToActivity(String title,String Msg){
        String localClassName = activity.getLocalClassName();
        switch (localClassName){
            case "HomeActivity":
                HomeActivity.getInstance().showCustomDialog(title,Msg);
                break;
            case "ItemReport":
                ItemReport.getInstance().showCustomDialog(title,Msg);
                break;
            case "SaleReportActivity":
                SaleReportActivity.getInstance().showCustomDialog(title,Msg);
                break;
        }
    }

    class ConnectPrinterKOT extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        private ReceiptData _receiptData;
        @Override
        protected void onPreExecute()
        {
            _receiptData = receiptData;
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
                    PrintKOT(_receiptData);
                }
                catch (Exception ex)
                {
                    PassMsgToActivity("Error",ex.getMessage());
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
                PassMsgToActivity("Connection Failed","Failed to Connect Printer."+result+".\nPlease contact support team.");
            }
            super.onPostExecute(result);
        }
    }

    class ConnectToBluetoothPrinterKOT extends AsyncTask<BluetoothDevice, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        private ReceiptData _receiptData;
        @Override
        protected void onPreExecute()
        {
            _receiptData = receiptData;
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
                    PrintKOT(_receiptData);
                }
                catch (Exception ex)
                {
                    PassMsgToActivity("Error",ex.getMessage());
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
                PassMsgToActivity("Connection Failed","Failed to Connect Printer."+result+".\nPlease contact support team.");
            }
            super.onPostExecute(result);
        }
    }

    class ConnectPrinter extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        private ReceiptData _receiptData;
        private ArrayList<ItemsRpt> _itemsRpts;
        private ArrayList<SaleReport> _saleReports;
        private boolean _isItemWiseRptBill;
        @Override
        protected void onPreExecute()
        {
            _receiptData = receiptData;
            _itemsRpts = itemsRpts;
            _saleReports = saleReports;
            _isItemWiseRptBill = isItemWiseRptBill;
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
                        if(_isItemWiseRptBill){
                            PrintItemWiseSaleReport(_itemsRpts);
                        }
                        else if(printSale){
                            PrintBillData(_receiptData);
                        }
                        else{
                            PrintSaleReport(_saleReports);
                        }
                }
                catch (Exception ex)
                {
                    PassMsgToActivity("Error",ex.getMessage());
                }
                finally {
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
                PassMsgToActivity("Connection Failed","Failed to Connect Printer."+result+".\nPlease contact support team.");
            }
            super.onPostExecute(result);
        }
    }

    class ConnectToBluetoothPrinter extends AsyncTask<BluetoothDevice, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        BluetoothDevice btdevice = null;
        private ReceiptData _receiptData;
        private ArrayList<ItemsRpt> _itemsRpts;
        private ArrayList<SaleReport> _saleReports;
        private boolean _isItemWiseRptBill;
        @Override
        protected void onPreExecute()
        {
            _receiptData = receiptData;
            _itemsRpts = itemsRpts;
            _saleReports = saleReports;
            _isItemWiseRptBill = isItemWiseRptBill;
            dialog.setTitle("Printing");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Connecting to Printer.....");
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
                    if(_isItemWiseRptBill){
                        PrintItemWiseSaleReport(_itemsRpts);
                    }
                    else if(printSale){
                        PrintBillData(_receiptData);
                    }
                    else{
                        PrintSaleReport(_saleReports);
                    }
                }
                catch (Exception ex)
                {
                    PassMsgToActivity("Error",ex.getMessage());
                }
                finally {
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
                PassMsgToActivity("Connection Failed","Failed to Connect Printer."+result+"\nPlease contact support team.");
            }
            super.onPostExecute(result);
        }
    }

    class ConnectUSBPrinter extends AsyncTask<String, Void, USBPortConnection>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        private ReceiptData _receiptData;
        private ArrayList<ItemsRpt> _itemsRpts;
        private ArrayList<SaleReport> _saleReports;
        private boolean _isItemWiseRptBill;
        @Override
        protected void onPreExecute()
        {
            _receiptData = receiptData;
            _itemsRpts = itemsRpts;
            _saleReports = saleReports;
            _isItemWiseRptBill = isItemWiseRptBill;
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Printing.....");
            dialog.show();
            super.onPreExecute();
        }


        @Override
        protected USBPortConnection doInBackground(String... params)
        {
            USBPortConnection retVal = null;
            try
            {
                // ip
                retVal = usbPort.connect_device(usbDevice);
            }
            catch (Exception e)
            {
                retVal = null;
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(USBPortConnection usbconnection)
        {
            if(usbconnection!=null) {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                try {
                    posPtr = new ESCPOSPrinter(usbconnection);
                    posPtr.setAsync(true);
                    if (_isItemWiseRptBill) {
                        PrintItemWiseSaleReport(_itemsRpts);
                    } else if (printSale) {
                        PrintBillData(_receiptData);
                    } else {
                        PrintSaleReport(_saleReports);
                    }
                } catch (Exception ex) {
                    PassMsgToActivity("Error",ex.getMessage());
                } finally {
                    if (!onlyBill && printSale) {
                        if (Common.printKOT) {
                            try{
                                PrintKOT(_receiptData);
                            }
                            catch (Exception ex){
                                PassMsgToActivity("Error",ex.getMessage());
                            }
                            finally {
                                HomeActivity.getInstance().RefreshViews();
                            }
                        } else {
                            HomeActivity.getInstance().RefreshViews();
                        }
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();

                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                PassMsgToActivity("Connection Failed","Failed to Connect Printer.\nPlease contact support team.");
            }
            super.onPostExecute(usbconnection);
        }
    }
}
