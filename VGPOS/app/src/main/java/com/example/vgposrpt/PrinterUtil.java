package com.example.vgposrpt;

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
import com.sewoo.port.android.WiFiPort;
import com.sewoo.request.android.RequestHandler;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PrinterUtil {
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
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
    private boolean isWifi;
    public BillDetails billDetail;
    public ArrayList<KotDetails> kotDetails;
    public PrinterUtil(Context cntx) {
        posPtr=new ESCPOSPrinter();
        isWifi = CommonUtil.PrintOption.equalsIgnoreCase("WIFI");
        if(isWifi){
            wifiPort = WiFiPort.getInstance();
        }
        else {
            bluetoothPort = BluetoothPort.getInstance();
        }
        context = cntx;
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
            if(CommonUtil.includeMRP){
                PrintBillWithMRP();
            }
            else{
                PrintBill();
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
            Toast.makeText(context,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return 0;
    }
    private void PrintBillWithMRP() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
        String dateStr = format.format(new Date());
        /*Bitmap bitmapIcon = Common.shopLogo;
        if(bitmapIcon!=null){
            try {
                posPtr.printBitmap(bitmapIcon,1,400);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+CommonUtil.ReceiptHeader+"\r\n");
        posPtr.printNormal(ESC+"|cA"+CommonUtil.ReceiptAddress+"\r\n");
        posPtr.printNormal("\n");
        posPtr.printNormal(ESC+"|lABILL NO  : "+ billDetail.BillNo+"\n");
        if(!billDetail.billUser.isEmpty()){
            posPtr.printNormal(ESC+"|lAUSER     : "+billDetail.billUser);
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
        for(int k=0;k<billDetail.billProducts.size();k++){
            String name = billDetail.billProducts.get(k).getProductName();
            String qty = formater.format(billDetail.billProducts.get(k).getQty());
            String price = formater.format(billDetail.billProducts.get(k).getPrice());
            String mrp = formater.format(billDetail.billProducts.get(k).getMRP());
            double mrpd = billDetail.billProducts.get(k).getMRP();
            Double amt = billDetail.billProducts.get(k).getAmount();
            totalAmt+=amt;
            double mrpamt = mrpd*billDetail.billProducts.get(k).getQty();
            mrpTotalAmt+=mrpamt;
            String amts=String.format("%.0f",amt);
            qty = StringUtils.leftPad(qty,19);
            mrp = StringUtils.leftPad(mrp,7);
            price = StringUtils.leftPad(price,10);
            amts = StringUtils.leftPad(amts,10);
            String line = qty+mrp+price+amts+"\n";
            if(CommonUtil.MultiLang){
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
        Bitmap mbp = getTextAsImage(mrptxt,25, Layout.Alignment.ALIGN_NORMAL);
        if(mbp!=null){
            posPtr.printBitmap(mbp,0);
        }
        else {
            posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|1C"+mrptxt+"\n");
        }
        if(discountAmt>0){
            Bitmap dbp = getTextAsImage(discountTxt,25, Layout.Alignment.ALIGN_NORMAL);
            if(dbp!=null){
                posPtr.printBitmap(dbp,0);
            }
            else {
                posPtr.printNormal(ESC+"|lA"+ESC+"|bC"+ESC+"|1C"+discountTxt+"\n");
            }
        }
        posPtr.lineFeed(1);
        Bitmap bp = getTextAsImage(txttotal,30, Layout.Alignment.ALIGN_CENTER);
        if(bp!=null){
            posPtr.printBitmap(bp,0);
        }
        else {
            posPtr.printNormal(ESC+"|rA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
        }
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|cA"+CommonUtil.ReceiptFooter+"\n");
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
            int widthm = CommonUtil.ReceiptSize.equals("2") ? 400:600;
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
    Bitmap getTextAsImage(String text, float textSize,Layout.Alignment alignment)  {
        try{
            TextPaint mPaint = new TextPaint();
            mPaint.setColor(Color.BLACK);
            Typeface typeface = ResourcesCompat.getFont(context,R.font.orienta);
            Typeface boldTypeface = Typeface.create(typeface,Typeface.BOLD);
            mPaint.setTypeface(boldTypeface);
            mPaint.setTextSize(textSize);
            int widthm = CommonUtil.ReceiptSize.equals("2") ? 380:600;
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
    private void PrintKot() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
        String dateStr = format.format(new Date());
        DecimalFormat formater = new DecimalFormat("#.###");
        KotDetails firstitme = kotDetails.stream().findFirst().orElse(null);

        posPtr.printNormal(ESC+"|cA"+ESC+"|2CKOT - "+firstitme.KotID+"\r\n");
        posPtr.printNormal("\n");
        if(!firstitme.KotUser.isEmpty()){
            posPtr.printNormal(ESC+"|lAUSER     : "+firstitme.KotUser);
            posPtr.printNormal("\n");
        }
        Tables tb = CommonUtil.tablesList.stream().filter(c->c.TableID==firstitme.TableID).findFirst().orElse(null);
        posPtr.printNormal(ESC+"|lADATE     : "+dateStr+"\n");
        if(tb!=null){
            posPtr.printNormal(ESC+"|lATABLE    : "+tb.TableName+"\n\n");
        }
        if(CommonUtil.ReceiptSize.equals("2")){
            posPtr.printNormal("--------------------------------");
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME               QUANTITY\n");
            posPtr.printNormal("--------------------------------");
        }
        else {
            posPtr.printNormal("----------------------------------------------\n");
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME                             QUANTITY\n");
            posPtr.printNormal("----------------------------------------------\n");
        }

        posPtr.setAlignment(0);

        for(int k=0;k<kotDetails.size();k++){
            KotDetails pr = kotDetails.get(k);
            String name = pr.ProductName;
            String qty = String.valueOf(pr.Qty);
            String line = "";
            if(CommonUtil.ReceiptSize.equals("2")){
                if(name.length()>33){
                    name = name.substring(0,32);
                }
                qty = StringUtils.leftPad(qty,27);
                line = qty+"\n";
            }
            else {
                if(name.length()>47){
                    name = name.substring(0,46);
                }
                qty = StringUtils.leftPad(qty,41);
                line = qty+"\n";
            }
            if(CommonUtil.MultiLang){
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
        if(CommonUtil.ReceiptSize.equals("2")){
            posPtr.printNormal("--------------------------------");
        }
        else {
            posPtr.printNormal("----------------------------------------------\n\n\n");
        }
        posPtr.lineFeed(4);
        posPtr.cutPaper();
        if(bluetoothPort!=null){
            try {
                bluetoothPort.disconnect();
            } catch (InterruptedException e) {
                //
            }
        }
        if(wifiPort != null){
            try {
                wifiPort.disconnect();
            } catch (InterruptedException e) {
                //
            }
        }
    }
    private void PrintBill() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
        String dateStr = format.format(new Date());
        /*Bitmap bitmapIcon = Common.shopLogo;
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
        }*/
        DecimalFormat formater = new DecimalFormat("#.###");
        posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+CommonUtil.ReceiptHeader+"\r\n");
        posPtr.printNormal(ESC+"|cA"+CommonUtil.ReceiptAddress+"\r\n");
        posPtr.printNormal("\n");
        posPtr.printNormal(ESC+"|lABILL NO  : "+billDetail.BillNo+"\n");
        if(!billDetail.billUser.isEmpty()){
            posPtr.printNormal(ESC+"|lAUSER     : "+billDetail.billUser);
            posPtr.printNormal("\n");
        }
        posPtr.printNormal(ESC+"|lADATE     : "+dateStr+"\n\n");
        if(CommonUtil.ReceiptSize.equals("2")){
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
        for(int k=0;k<billDetail.billProducts.size();k++){
            Product pr = billDetail.billProducts.get(k);
            String name = pr.getProductName();
            String qty = formater.format(pr.getQty());
            String price = formater.format(pr.getPrice());
            Double amt = pr.getAmount();
            billAmt+=amt;
            String amts=String.format("%.0f",amt);
            String line = "";
            if(CommonUtil.ReceiptSize.equals("2")){
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
            if(CommonUtil.MultiLang){
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
        if(CommonUtil.ReceiptSize.equals("2")){
            posPtr.printNormal("--------------------------------");
        }
        else {
            posPtr.printNormal("----------------------------------------------\n");
        }

        totalAmt = billAmt;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        String totalamt = formatter.format(totalAmt).replace(symbol,symbol+" ");
        String txttotal = "Grand Total  "+totalamt+"/-";
        posPtr.lineFeed(1);
        Bitmap bp = getTextAsImage(txttotal,30, Layout.Alignment.ALIGN_CENTER);
        if(bp!=null){
            posPtr.printBitmap(bp,0);
        }
        else {
            if(CommonUtil.ReceiptSize.equals("2")){
                posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|1C"+txttotal+"\n");
            }
            else {
                posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
            }
        }
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|cA"+CommonUtil.ReceiptFooter+"\n");
        posPtr.lineFeed(4);
        posPtr.cutPaper();
    }

    public void Print() throws Exception{
        try{
            if(isWifi){
                new ConnectPrinter().execute(CommonUtil.PrinterIP);
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
            int bluetooth = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT);
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
            Toast.makeText(context,"Please enable Bluetooth permission.",Toast.LENGTH_LONG).show();
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice mydevice =null;
        for (BluetoothDevice device : pairedDevices) {
            // Add the name and address to an array adapter to show in a ListView
            if (device.getName().contains(CommonUtil.printer)) {
                mydevice = device;
                break;
            }
        }

        return  mydevice;
    }

    class ConnectPrinter extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context,R.style.CustomProgressStyle);
        @Override
        protected void onPreExecute()
        {
            dialog.setTitle("Printing");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Connecting to Printer.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String retVal = null;
            try
            {
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
                    if(billDetail!=null){
                        PrintBillData();
                    }
                    if(kotDetails!=null){
                        PrintKot();
                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(context,"Error: "+ex.getMessage(),Toast.LENGTH_LONG).show();
                }
                finally {
                    if(dialog.isShowing())
                        dialog.dismiss();

                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(context,"Failed to Connect Printer",Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }
    class ConnectToBluetoothPrinter extends AsyncTask<BluetoothDevice, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context,R.style.CustomProgressStyle);
        BluetoothDevice btdevice = null;
        @Override
        protected void onPreExecute()
        {
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
                    if(billDetail!=null){
                        PrintBillData();
                    }
                    if(kotDetails!=null){
                        PrintKot();
                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(context,"Error: "+ex.getMessage(),Toast.LENGTH_LONG).show();
                }
                finally {
                    if(dialog.isShowing())
                        dialog.dismiss();

                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(context,"Failed to Connect Printer",Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }
}
