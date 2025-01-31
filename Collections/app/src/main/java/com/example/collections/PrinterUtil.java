package com.example.collections;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.request.android.RequestHandler;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PrinterUtil {
    private final Context context;
    private Thread hThread;
    private WiFiPort wifiPort;
    public ESCPOSPrinter posPtr;
    private static final String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN
    };
    private BluetoothPort bluetoothPort;
    private final boolean isWifi;
    // 0x1B
    private final char ESC = ESCPOS.ESC;
    private final char ESC2 = ESCPOS.SP;
    private String memberName;
    private String LoanNo;
    private String outStanding;
    private String paidAmt;
    private String balAmt;
    private String paymentMode;
    private String paymentID;
    private Boolean rePrint;
    private Date paymentDate;
    private boolean printCollectionReport;
    private CollectionsRpt collectionsRpt;
    public PrinterUtil(Context cntx,
                       ReceiptDtl receiptDtl,
                       CollectionsRpt collectionsRpt,
                       Boolean isReprint,
                       Boolean collectionPrint) {
        posPtr=new ESCPOSPrinter();
        rePrint = isReprint;
        isWifi = false;
        printCollectionReport = collectionPrint;
        if(isWifi){
            wifiPort = WiFiPort.getInstance();
        }
        else {
            bluetoothPort = BluetoothPort.getInstance();
        }
        context = cntx;
        if(receiptDtl!=null){
            this.memberName = receiptDtl.MemberName;
            this.LoanNo = receiptDtl.LoanNo;
            this.outStanding = receiptDtl.Outstanding;
            this.paidAmt = receiptDtl.Paid;
            this.balAmt = receiptDtl.Balance;
            this.paymentMode = receiptDtl.PaymentMode;
            this.paymentID = receiptDtl.PaymentID;
            this.paymentDate = receiptDtl.PaidDate;
        }
        this.collectionsRpt = collectionsRpt;
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


    Bitmap getMultiLangTextAsImage(String text, float textSize, Typeface typeface)  {
        try{
            TextPaint mPaint = new TextPaint();
            mPaint.setColor(Color.BLACK);
            if (typeface != null) mPaint.setTypeface(typeface);
            mPaint.setTextSize(textSize);
            Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
            int widthm = 540;
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
    private void PrintCollectionReport() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr = format.format(collectionsRpt.frmDate);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        SimpleDateFormat dtformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale.getDefault());
        String currentDt = dtformat.format(new Date());

        posPtr.printNormal("\n");
        posPtr.printNormal(ESC+"|cA"+ESC+"|1CBACKYALAKSHMI MICRO\r\n");
        posPtr.printNormal(ESC+"|cA"+ESC+"|1CFINANCE PVT. LTD\r\n");
        posPtr.printNormal("\n");
        posPtr.printNormal(ESC+"|cA"+ESC+"|1CCOLLECTIONS REPORT\r\n");
        posPtr.printNormal("--------------------------------");
        posPtr.printNormal("        DATE: "+dateStr+"\r\n");
        posPtr.printNormal("  AGENT NAME: "+CommonUtil.loggedinUser+"\r\n");
        posPtr.printNormal("NO. OF BILLS: "+collectionsRpt.NoofBills+"\r\n");
        posPtr.printNormal("  CASH BILLS: "+collectionsRpt.CashBills+"\r\n");
        posPtr.printNormal("  UPI  BILLS: "+collectionsRpt.UpiBills+"\r\n");
        String cashAmt = formatter.format(collectionsRpt.CashAmt).replace(symbol,"Rs. ");
        cashAmt = StringUtils.leftPad(cashAmt,19);
        posPtr.printNormal(" CASH AMOUNT:"+cashAmt);
        String upiAmt = formatter.format(collectionsRpt.UpiAmt).replace(symbol,"Rs. ");
        upiAmt = StringUtils.leftPad(upiAmt,19);
        posPtr.printNormal("  UPI AMOUNT:"+upiAmt);
        posPtr.printNormal("--------------------------------");
        String totalAmt = formatter.format(collectionsRpt.TotalAmt).replace(symbol,"Rs. ");
        totalAmt = StringUtils.leftPad(totalAmt,19);
        posPtr.printNormal("TOTAL AMOUNT:"+totalAmt);
        posPtr.printNormal("--------------------------------");
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|cA"+ESC+"|1CPrinted on "+currentDt+"\r\n");
        posPtr.lineFeed(4);
    }
    private void PrintBill() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm aaa", Locale.getDefault());
        String dateStr = format.format(paymentDate);
        if(rePrint){
            posPtr.printNormal(ESC+"|cARECEIPT-COPY\r\n");
        }
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(),R.drawable.appicon);
        posPtr.printBitmap(logo,1,150);
        posPtr.printNormal("\n");
        posPtr.printNormal(ESC+"|cA"+ESC+"|1CBACKYALAKSHMI MICRO\r\n");
        posPtr.printNormal(ESC+"|cA"+ESC+"|1CFINANCE PVT. LTD\r\n");
        //posPtr.printNormal(ESC+"|cA32/37, EZHIL NAGAR, 4TH ST,\r\n");
        //posPtr.printNormal(ESC+"|cAB BLK, KODUNGAIYUR, CH-118.\r\n");

        posPtr.printNormal("--------------------------------");
        posPtr.printNormal(ESC+"|cA"+dateStr+"\r\n");
        String receiptNo = StringUtils.leftPad(paymentID,21);
        posPtr.printNormal("RECEIPT NO:"+receiptNo);
        String customerName = StringUtils.leftPad(memberName,21);
        customerName = customerName.substring(0,21);
        posPtr.printNormal("MEM.  NAME:"+customerName);
        String acNo = StringUtils.leftPad(LoanNo,21);
        posPtr.printNormal("LOAN NO   :"+acNo);
        String payMode = StringUtils.leftPad(paymentMode,21);
        posPtr.printNormal("PAY MODE  :"+payMode);
        String osBal = StringUtils.leftPad(outStanding,21);
        /*String osLine = "O/S BAL    :"+osBal;
        Bitmap xb = getMultiLangTextAsImage(osLine, 25, Typeface.DEFAULT);
        if(xb!=null){
            posPtr.printBitmap(xb,0);
        }*/
        posPtr.printNormal("O/S BAL   :"+osBal);
        String coll = StringUtils.leftPad(paidAmt,21);
        /*String paidLine = "PAID AMT   :"+coll;
        Bitmap paidAmtImage = getMultiLangTextAsImage(paidLine, 25, Typeface.DEFAULT);
        if(paidAmtImage!=null){
            posPtr.printBitmap(paidAmtImage,0);
        }*/
        posPtr.printNormal("PAID AMT  :"+coll);
        posPtr.printNormal("--------------------------------");
        String total = StringUtils.leftPad(balAmt,21);
        /*String balLine = "BAL. AMT  :"+total;
        Bitmap balImage = getMultiLangTextAsImage(balLine, 25, Typeface.DEFAULT);
        if(balImage!=null){
            posPtr.printBitmap(balImage,0);
        }*/
        posPtr.printNormal("BAL. AMT  :"+total);
        posPtr.printNormal("--------------------------------");
        posPtr.lineFeed(1);
        String agent = StringUtils.leftPad(CommonUtil.loggedinUser,21);
        posPtr.printNormal(ESC+"|cA"+ESC+"|1CCUSTOMER CARE: 7418899988\r\n");
        posPtr.lineFeed(4);
    }

    public void Print() throws Exception{
        try{
            if(isWifi){
                //new ConnectPrinter().execute(Common.printerIP);
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
            ActivityCompat.requestPermissions(HomeActivity.getInstance(), PERMISSIONS_BLUETOOTH
                    , 1);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice mydevice =null;
        for (BluetoothDevice device : pairedDevices) {
            // Add the name and address to an array adapter to show in a ListView
            if (device.getName().contains(CommonUtil.Printer)) {
                mydevice = device;
                break;
            }
        }

        return  mydevice;
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
            dialog.setTitle("Printing");
            dialog.setMessage("Please Wait...");
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
                    if(printCollectionReport){
                        PrintCollectionReport();
                    }
                    else {
                        PrintBill();
                    }
                }
                catch (Exception ex)
                {
                    if(printCollectionReport){
                        CollectionReportActivity history = (CollectionReportActivity) context;
                        history.showCustomDialog("Error",ex.getMessage());
                    }
                    else if(rePrint){
                        PaymentHistoryActivity history = (PaymentHistoryActivity) context;
                        history.showCustomDialog("Error",ex.getMessage());
                    }
                    else {
                        HomeActivity home = (HomeActivity) context;
                        home.showCustomDialog("Error",ex.getMessage());
                    }
                }
                finally {
                    if(dialog.isShowing())
                        dialog.dismiss();
                    if(!rePrint && !printCollectionReport){
                        HomeActivity home = (HomeActivity) context;
                        home.RefreshViews();
                    }

                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
                String errMsg = "Failed to connect to Printer:"+result;
                if(printCollectionReport){
                    CollectionReportActivity history = (CollectionReportActivity) context;
                    history.showCustomDialog("Error",errMsg);
                }
                else if(rePrint){
                    PaymentHistoryActivity history = (PaymentHistoryActivity) context;
                    history.showCustomDialog("Error",errMsg);
                }
                else {
                    HomeActivity home = (HomeActivity) context;
                    home.showCustomDialog("Error",errMsg);
                    home.RefreshViews();
                }
            }
            super.onPostExecute(result);
        }
    }
}
