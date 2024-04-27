package com.example.vinoth.vgspos;

import static java.nio.charset.StandardCharsets.US_ASCII;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;
import org.fintrace.core.drivers.tspl.commands.label.Barcode;
import org.fintrace.core.drivers.tspl.commands.label.BarcodeAlignment;
import org.fintrace.core.drivers.tspl.commands.label.BarcodeRotation;
import org.fintrace.core.drivers.tspl.commands.label.BarcodeType;
import org.fintrace.core.drivers.tspl.commands.label.ErrorCorrectionLevel;
import org.fintrace.core.drivers.tspl.commands.label.QRCode;
import org.fintrace.core.drivers.tspl.commands.label.QREncodeMode;
import org.fintrace.core.drivers.tspl.commands.label.QRMask;
import org.fintrace.core.drivers.tspl.commands.label.QRModel;
import org.fintrace.core.drivers.tspl.commands.label.TSPLLabel;
import org.fintrace.core.drivers.tspl.commands.label.Text;
import org.fintrace.core.drivers.tspl.commands.system.Print;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PrintBluetooth {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private OutputStream mOutputStream;
    Set<BluetoothDevice> pairedDevices;
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH
    };
    private Context context;
    public boolean isReprint;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public PrintBluetooth(Context ctx){
        context = ctx;
    }

    public void GetBluetoothNameAndConnect() throws Exception{
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!checkBluetoothPermission()) {
            ActivityCompat.requestPermissions(HomeActivity.getInstance(), PERMISSIONS_BLUETOOTH
                    , 1);
        }
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        String getName = mBluetoothAdapter.getName();
        for (BluetoothDevice device : pairedDevices) {
            // Add the name and address to an array adapter to show in a ListView
            if (device.getName().contains(Common.bluetoothDeviceName)) {
                getName = device.getAddress();
                break;
            }
        }
        ConnectBluetooth(getName);
    }
    public  void CloseBT(){
        try {
            if (mOutputStream != null)
                mOutputStream.close();
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("ERR", e.getMessage(), e);
        }
    }
    @Override
    protected void finalize() throws Throwable {
        try {
            if (mOutputStream != null)
                mOutputStream.close();
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("ERR", e.getMessage(), e);
        }
        super.finalize();
    }

    private boolean checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int bluetooth = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT);
            return bluetooth == PackageManager.PERMISSION_GRANTED;
        } else {
            int bluetooth = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
            return bluetooth == PackageManager.PERMISSION_GRANTED;
        }
    }
    public void ConnectBluetooth(String btName) throws Exception {
        try {
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(btName);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(HomeActivity.getInstance(), PERMISSIONS_BLUETOOTH
                        , 1);
            }
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
            mBluetoothSocket.connect();

        }
        catch (Exception ex){
            throw ex;
        }
    }
    public void PaperCut(){
        try{
            byte[] cut = new byte[]{ 0x1D,
                    0x56,
                    66,
                    0x00};
            mOutputStream = mBluetoothSocket.getOutputStream();
            mOutputStream.write(cut);
            mOutputStream.flush();
        }
        catch (Exception ex){
            Log.e("ERR","Exception during write",ex);
            HomeActivity.getInstance().showCustomDialog("Error",ex.getMessage());
        }
    }
    public  byte[] BitMaptoBytesArray(Bitmap bitmap){
        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] byteArray = byteBuffer.array();
        return  byteArray;
        /*int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        String format = bitmap.getConfig().name();*/
    }

    public void PrintImage(Bitmap image) throws Exception{
        try {
            byte[] command = PrinterUtils.decodeBitmap(image);
            if(command!=null){
                //PrintWithFormat(command,new Formatter().get(),Formatter.centerAlign());
                mOutputStream = mBluetoothSocket.getOutputStream();
                mOutputStream.write(Formatter.centerAlign());
                mOutputStream.write(new Formatter().get());
                mOutputStream.write(command);
                mOutputStream.flush();
            }
        } catch (Exception ex) {
            Log.e("ERR", "Exception during write", ex);
            throw ex;
        }
    }
    private String GetTSCLCommand(String inputdata){
        String[] str = new String[]{"SIZE 52 mm, 38 mm","GAP 3 mm, 0 mm","SET RIBBON OFF","DIRECTION 0,0","REFERENCE 0,0","OFFSET 0 mm","SET PEEL OFF","SET CUTTER OFF","SET TEAR ON","CLS"};
        TSPLLabel barcodelabel = TSPLLabel.builder()
                .element(Barcode.builder().xCoordinate(400f).yCoordinate(168f).codeType(BarcodeType.CODE_128M).height(46).rotation(BarcodeRotation.DEGREES_180)
                        .alignment(BarcodeAlignment.LEFT)
                        .narrow(3).wide(6).content("!10512345678").build()).build();
        TSPLLabel tsplLabel = TSPLLabel.builder()
                //.element(Size.builder().labelWidth(52f).labelLength(38f).build())
                //.element(Gap.builder().labelDistance(0f).labelOffsetDistance(0f).build())
                //.element(Direction.builder().printPositionAsFeed(Boolean.TRUE).build())
                //.element(ClearBuffer.builder().build())
                .element(Text.builder().xCoordinate(346).yCoordinate(114).fontName("3").rotation(BarcodeRotation.DEGREES_180).xMultiplicationFactor(1f)
                        .yMultiplicationFactor(1f).content("12345678").build())
                .element(Text.builder().xCoordinate(386).yCoordinate(259).fontName("3").rotation(BarcodeRotation.DEGREES_180).xMultiplicationFactor(1f)
                        .yMultiplicationFactor(1f).content("PRODUCTS NAME").build())
                .element(QRCode.builder().xCoordinate(152).yCoordinate(231).errorCorrectionLevel(ErrorCorrectionLevel.L).cellWidth(7).mode(QREncodeMode.A)
                        .rotation(BarcodeRotation.DEGREES_180).model(QRModel.M2).mask(QRMask.S7).content("12345678").build())
                .element(Text.builder().xCoordinate(142).yCoordinate(64).fontName("3").rotation(BarcodeRotation.DEGREES_180).xMultiplicationFactor(1f)
                        .yMultiplicationFactor(1f).content("12345678").build())
                .element(Print.builder().nbLabels(1).nbCopies(4).build())
                .build();
        String header = StringUtils.join(str,"\r\n");
        header = header+"\r\n"+barcodelabel.getTsplCode()+"CODEPAGE 1252"+"\r\n"+tsplLabel.getTsplCode();
        return  header;
    }
    public  void PrintLabel(String inputdata){
        try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            String txt = GetTSCLCommand(inputdata);
            mOutputStream.write((txt).getBytes(US_ASCII));
            mOutputStream.flush();
        } catch (Exception ex) {
            Log.e("ERR", "Exception during write", ex);
            HomeActivity.getInstance().showCustomDialog("Error",ex.getMessage());
        }
    }
    public void PrintData(String txt,final byte[] pFormat, final byte[] pAlignment) {
        try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            mOutputStream.write(pAlignment);
            mOutputStream.write(pFormat);
            mOutputStream.write((txt+"\n").getBytes("GBK"));
            mOutputStream.flush();
        } catch (Exception ex) {
            Log.e("ERR", "Exception during write", ex);
            HomeActivity.getInstance().showCustomDialog("Error",ex.getMessage());
        }
    }
    public void PrintDataTamil(String txt,final byte[] pFormat, final byte[] pAlignment) {
        try {
            mOutputStream = mBluetoothSocket.getOutputStream();
            mOutputStream.write(pAlignment);
            mOutputStream.write(pFormat);
            mOutputStream.write((txt+"\n").getBytes("US-ASCII"));
            mOutputStream.flush();
        } catch (Exception ex) {
            Log.e("ERR", "Exception during write", ex);
            HomeActivity.getInstance().showCustomDialog("Error",ex.getMessage());
        }
    }
    public byte[] GetBytes(String str)
    {
        char[] chars = str.toCharArray();
        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++)
        {
            bytes[i * 2] = (byte) (chars[i] >> 8);
            bytes[i * 2 + 1] = (byte) chars[i];
        }

        return bytes;
    }
    public void printImageReciept() throws Exception{
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String pathName = dir+"/print.jpg";
        int width = 0;
        int level =50;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = 1;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        try {
            // sleep(1000);
            if (bitmap == null) {
                return;
            }
            int MAX_WIDTH = 500;
            if (width == -1) {
                width = MAX_WIDTH;
            } else if ((width == 0) || (width < 0)) {
                width = bitmap.getWidth();
            }
            if (width > MAX_WIDTH) {
                width = MAX_WIDTH;
            }
            byte[] printerData = null;

            printerData = PrinterUtils.decodeBitmap(bitmap);

            //Toast.makeText(getApplicationContext(), "Invoice Sent to Printer. Please wait...", Toast.LENGTH_LONG).show();
            mOutputStream = mBluetoothSocket.getOutputStream();
            mOutputStream.write(printerData);
            mOutputStream.flush();

        } catch (Exception ioe) {

            throw ioe;

        }
    }
    void createImageFromString(final String text) throws IOException{
        String fontPath = "Bamini.ttf"; /* You can use any font or       use default */
        Typeface tf = Typeface.createFromAsset(HomeActivity.getInstance().getAssets(), fontPath);
        int height=0;
        String [] rows = text.split("\n\r");
        height=32*rows.length;/* Specify Length of Image File */
        FileOutputStream fop = null;
        File file;
        /* Specify the path where you want to save the image */
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        file = new File(dir+"/print.jpg");
        final Paint textPaint = new Paint() {
            {
                setColor(Color.WHITE);
                setTextAlign(Paint.Align.LEFT);
                setTextSize(25f);
                setAntiAlias(true);
            }
        };
        /* Optional to set Rect */
        final Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        final Bitmap bmp = Bitmap.createBitmap(800, height, Bitmap.Config.ARGB_8888); //use ARGB_8888 for better quality
        final Canvas canvas = new Canvas(bmp);
        textPaint.setStyle(Paint.Style.FILL); //fill the background with blue color
        canvas.drawRect(0, 0, 800, height, textPaint);
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(tf);
        float y=28;
        /* Custom your layout here */
        for(int i =0;i<rows.length;i++){
            if(i==(rows.length-8)){
                textPaint.setTypeface(Typeface.DEFAULT_BOLD);
                textPaint.setTextSize(28f);
            }else{
                textPaint.setTypeface(tf);
                textPaint.setTextSize(20f);
            }
            canvas.drawText(rows[i], 5, y, textPaint);
            y=y+28;
        }
        FileOutputStream stream = new FileOutputStream(file); //create your FileOutputStream here
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        bmp.recycle();
        stream.close();
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
            HomeActivity.getInstance().showCustomDialog("Error",e.getMessage());
            return false;
        }
    }
    public  String padLeft(String s, int n) {
        return StringUtils.leftPad(s,n);
    }
    public String GetFormatedString(String txt,int maxLength){
        String res = padLeft(txt,maxLength);
        return res.substring(0,maxLength);
    }
    public void PrintSaleRpt(){
        String msg = "SALE REPORT\n";
        PrintData("   ",new Formatter().get(),Formatter.leftAlign());
        //PrintWithFormat(msg.getBytes(StandardCharsets.UTF_8),new Formatter().height().get(),Formatter.centerAlign());
        PrintData(msg,new Formatter().height().get(),Formatter.centerAlign());
        PrintData("FROM DATE :"+Common.saleReportFrmDate,new Formatter().get(),Formatter.leftAlign());
        PrintData("TO DATE   :"+Common.saleReportToDate,new Formatter().get(),Formatter.leftAlign());
        if(Common.RptSize.equals("3")){
            PrintData("----------------------------------------------",new Formatter().get(),Formatter.leftAlign());
            String hed =  "BILL NO         BILL_DATE               AMOUNT";
            PrintData(hed,new Formatter().bold().get(),Formatter.leftAlign());
            PrintData("----------------------------------------------",new Formatter().get(),Formatter.leftAlign());
        }
        else{
            PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
            String hed =  "BILL NO   BILL_DATE       AMOUNT";
            PrintData(hed,new Formatter().get(),Formatter.leftAlign());
            PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
        }

        double totalAmt = 0;
        for(int k=0;k<Common.saleReports.size();k++){
            SaleReport sr = Common.saleReports.get(k);
            String billno = sr.getBillNo();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
            Date dt = format.parse(sr.getBillDate(),new ParsePosition(0));
            SimpleDateFormat formatdt = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa",Locale.getDefault());
            String billdate = formatdt.format(dt);
            if(!Common.RptSize.equals("3")){
                billdate = billdate.substring(0,10);
            }
            Double saleAmt = sr.getBillAmount();
            String billAmt = String.format("%.0f",saleAmt);
            totalAmt+=saleAmt;
            if(Common.RptSize.equals("3")){
                billno = StringUtils.rightPad(billno,7);
                billdate = StringUtils.leftPad(billdate,18);
                billAmt = StringUtils.leftPad(billAmt,21);
                String line = billno+billdate+billAmt;
                PrintData(line,new Formatter().get(),Formatter.leftAlign());
            }
            else{
                String line = StringUtils.rightPad(billno,10)+StringUtils.rightPad(billdate,11)+StringUtils.leftPad(billAmt,11);
                PrintData(line,new Formatter().get(),Formatter.leftAlign());
            }

        }
        PrintData("   ",new Formatter().get(),Formatter.leftAlign());
        PrintData("   ",new Formatter().get(),Formatter.leftAlign());
        String ttAmtTxt = "TOTAL SALE :"+String.format("%.0f",totalAmt)+"/-";
        PrintData(ttAmtTxt,new Formatter().height().get(),Formatter.centerAlign());
        PrintData("  ",new Formatter().get(),Formatter.leftAlign());
        PrintData("  ",new Formatter().get(),Formatter.leftAlign());
        if(Common.RptSize.equals("3")){
            PaperCut();
        }
    }
    public void PrintItemWiseRpt(){
        String msg = "ITEM WISE REPORT\n";
        DecimalFormat formater = new DecimalFormat("#.###");
        PrintWithFormat(msg.getBytes(StandardCharsets.UTF_8),new Formatter().height().get(),Formatter.centerAlign());
        PrintData("FROM DATE :"+Common.saleReportFrmDate,new Formatter().get(),Formatter.leftAlign());
        PrintData("TO DATE   :"+Common.saleReportToDate,new Formatter().get(),Formatter.leftAlign());
        if(Common.RptSize.equals("3")){
            PrintData("----------------------------------------------",new Formatter().get(),Formatter.leftAlign());
            String hed =  "ITEM NAME                             QTY SOLD";
            PrintData(hed,new Formatter().bold().get(),Formatter.leftAlign());
            PrintData("----------------------------------------------",new Formatter().get(),Formatter.leftAlign());
        }
        else{
            PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
            String hed =  "ITEM NAME               QTY SOLD";
            PrintData(hed,new Formatter().bold().get(),Formatter.leftAlign());
            PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
        }
        double totalAmt = 0;
        for(int k=0;k<Common.itemsRpts.size();k++){
            ItemsRpt sr = Common.itemsRpts.get(k);
            String name = sr.getItemName();
            String[] englishname = name.split("/");
            if(englishname.length>0){
                name = englishname[0];
            }
            Double qty = sr.getQuantity();
            String qtystr = formater.format(qty);
            int namepadlength=0;
            if(Common.RptSize.equals("3")){
                namepadlength=38;
            }
            else{
                namepadlength = 24;
            }
            String line = StringUtils.rightPad(name,namepadlength)+StringUtils.leftPad(qtystr,8);
            PrintData(line,new Formatter().get(),Formatter.leftAlign());
        }
        PrintData("   ",new Formatter().get(),Formatter.leftAlign());
        PrintData("   ",new Formatter().get(),Formatter.leftAlign());
        if(Common.RptSize.equals("3")){
            PaperCut();
        }
    }
    public void PrintKOT(){
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
            Date date = Common.billDate;
            String msg = Common.headerMeg+"\n";
            PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            PrintWithFormat("KOT\n".getBytes(StandardCharsets.UTF_8),new Formatter().get(),Formatter.centerAlign());
            PrintWithFormat(msg.getBytes(StandardCharsets.UTF_8),new Formatter().height().get(),Formatter.centerAlign());
            String address = Common.addressline+"\n";
            PrintWithFormat(address.getBytes(StandardCharsets.UTF_8),new Formatter().get(),Formatter.centerAlign());
            if(!Common.waiter.isEmpty() && !Common.waiter.equals("NONE")){
                PrintData("NAME     :"+Common.waiter,new Formatter().get(),Formatter.leftAlign());
            }
            PrintData("BILL NO  :"+Common.billNo,new Formatter().get(),Formatter.leftAlign());
            PrintData("DATE     : " + format.format(date),new Formatter().get(),Formatter.leftAlign());
            if(Common.RptSize.equals("3")){
                PrintData("----------------------------------------------",new Formatter().get(),Formatter.leftAlign());
                String hed =  "ITEM NAME             QTY      PRICE    AMOUNT";
                PrintData(hed,new Formatter().get(),Formatter.leftAlign());
                PrintData("----------------------------------------------",new Formatter().get(),Formatter.leftAlign());
            }
            else if(Common.RptSize.equals("4")){
                PrintData("-------------------------------------------------------------",new Formatter().get(),Formatter.leftAlign());
                String hed =  "ITEM NAME                            QTY      PRICE    AMOUNT";
                PrintData(hed,new Formatter().bold().get(),Formatter.leftAlign());
                PrintData("-------------------------------------------------------------",new Formatter().get(),Formatter.leftAlign());
            }
            else{
                PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
                String hed =  "ITEM        QTY    RATE   AMOUNT";
                PrintData(hed,new Formatter().bold().get(),Formatter.leftAlign());
                PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
            }
            DecimalFormat formater = new DecimalFormat("#.###");
            double totalAmt=0;
            for(int k=0;k<Common.itemsCarts.size();k++){
                String name = Common.itemsCarts.get(k).getItem_Name();
                String[] englishname = name.split("/");
                if(englishname.length>0){
                    name = englishname[0];
                }
                String qty = formater.format(Common.itemsCarts.get(k).getQty());
                String price = formater.format(Common.itemsCarts.get(k).getPrice());
                Double amt = Common.itemsCarts.get(k).getPrice()*Common.itemsCarts.get(k).getQty();
                totalAmt+=amt;
                String amts=String.format("%.0f",amt);
                if(Common.RptSize.equals("3")){
                    name = StringUtils.rightPad(name,20);
                    name = name.substring(0,20);
                    qty = StringUtils.leftPad(qty,5);
                    price = StringUtils.leftPad(price,11);
                    amts = StringUtils.leftPad(amts,10);
                    String line = name+qty+price+amts;
                    PrintData(line,new Formatter().get(),Formatter.leftAlign());
                }
                else if(Common.RptSize.equals("4")){
                    name = StringUtils.rightPad(name,35);
                    name = name.substring(0,35);
                    qty = StringUtils.leftPad(qty,5);
                    price = StringUtils.leftPad(price,11);
                    amts = StringUtils.leftPad(amts,10);
                    String line = name+qty+price+amts;
                    PrintData(line,new Formatter().get(),Formatter.leftAlign());
                }
                else{
                    PrintData(name,new Formatter().get(),Formatter.leftAlign());
                    PrintData("           "+GetFormatedString(qty,4)+GetFormatedString(price,9)+GetFormatedString(amts,8),
                            new Formatter().get(),Formatter.leftAlign());
                }

            }
            PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            //String ttAmtTxt = "TOTAL AMT:"+String.format("%.0f",totalAmt)+"/-";
            //PrintData(ttAmtTxt,new Formatter().bold().get(),Formatter.centerAlign());
            PrintData("  ",new Formatter().get(),Formatter.leftAlign());
            //PrintData(Common.footerMsg,new Formatter().get(),Formatter.centerAlign());
            PrintData(" ",new Formatter().get(),Formatter.leftAlign());
            PrintData("  ",new Formatter().get(),Formatter.leftAlign());
            if(Common.RptSize.equals("3")){
                PaperCut();
            }
            Toast.makeText(context, "Print Queued Successfully.!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            //Toast.makeText(MainActivity.this, e.getMessage(), 1).show();
            e.printStackTrace();
            HomeActivity.getInstance().showCustomDialog("Error",e.getMessage());
        }
    }
    public String PrintBill(){
        String retVal = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
            Date date = Common.billDate;
            Bitmap bitmapIcon = Common.shopLogo;
            PrintData(" ",new Formatter().get(),Formatter.leftAlign());
            if(bitmapIcon!=null){
                PrintImage(bitmapIcon);
                PrintData(" ",new Formatter().get(),Formatter.leftAlign());
            }
            String msg = Common.headerMeg+"\n";
            if(isReprint){
                PrintData("COPY BILL\n",new Formatter().underlined().get(),Formatter.centerAlign());
            }
            PrintData(msg,new Formatter().height().get(),Formatter.centerAlign());
            String address = Common.addressline+"\n";
            PrintData(address,new Formatter().get(),Formatter.centerAlign());
            if(!Common.waiter.isEmpty() && !Common.waiter.equals("NONE")){
                PrintData("NAME     :"+Common.waiter,new Formatter().get(),Formatter.leftAlign());
            }
            PrintData("BILL NO  :"+Common.billNo,new Formatter().get(),Formatter.leftAlign());
            PrintData("DATE     : " + format.format(date),new Formatter().get(),Formatter.leftAlign());
            if(Common.RptSize.equals("3")){
                String hed =  "ITEM NAME             QTY      PRICE    AMOUNT";
                if(Common.includeMRPinReceipt){
                    hed =  "ITEM NAME       QTY    MRP     PRICE    AMOUNT";
                }
                PrintData("----------------------------------------------",new Formatter().get(),Formatter.leftAlign());
                PrintData(hed,new Formatter().get(),Formatter.leftAlign());
                PrintData("----------------------------------------------",new Formatter().get(),Formatter.leftAlign());
            }
            else if(Common.RptSize.equals("4")){
                String hed =  "ITEM NAME                            QTY      PRICE    AMOUNT";
                if(Common.includeMRPinReceipt){
                    hed =  "ITEM NAME                      QTY    MRP     PRICE    AMOUNT";
                }
                PrintData("-------------------------------------------------------------",new Formatter().get(),Formatter.leftAlign());
                PrintData(hed,new Formatter().bold().get(),Formatter.leftAlign());
                PrintData("-------------------------------------------------------------",new Formatter().get(),Formatter.leftAlign());
            }
            else{
                PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
                String hed =  "ITEM        QTY    RATE   AMOUNT";
                PrintData(hed,new Formatter().bold().get(),Formatter.leftAlign());
                PrintData("--------------------------------",new Formatter().get(),Formatter.leftAlign());
            }
            double billAmt=0;
            double totalAmt=0;
            double mrpTotalAmt = 0d;
            double discountAmt = 0d;
            double totalQty=0;
            DecimalFormat formater = new DecimalFormat("#.###");
            for(int k=0;k<Common.itemsCarts.size();k++){
                String name = Common.itemsCarts.get(k).getItem_Name();
                String[] englishname = name.split("/");
                if(englishname.length>0){
                    name = englishname[0];
                }
                //name = TamilUtil.convertToTamil(TamilUtil.TSCII, name);
                double qt = Common.itemsCarts.get(k).getQty();
                totalQty+=qt;
                String qty = formater.format(qt);
                String price = formater.format(Common.itemsCarts.get(k).getPrice());
                Double amt = Common.itemsCarts.get(k).getPrice()*Common.itemsCarts.get(k).getQty();
                String mrp = formater.format(Common.itemsCarts.get(k).getMRP());
                double mrpd = Common.itemsCarts.get(k).getMRP();
                double mrpamt = mrpd*Common.itemsCarts.get(k).getQty();
                mrpTotalAmt+=mrpamt;
                billAmt+=amt;
                String amts=String.format("%.0f",amt);
                if(Common.RptSize.equals("3")){
                    String line = "";
                    if(Common.includeMRPinReceipt){
                        name = StringUtils.rightPad(name,14);
                        name = name.substring(0,14);
                        qty = StringUtils.leftPad(qty,5);
                        mrp = StringUtils.leftPad(mrp,7);
                        price = StringUtils.leftPad(price,10);
                        amts = StringUtils.leftPad(amts,10);
                        line = name+qty+mrp+price+amts;
                    }
                    else{
                        name = StringUtils.rightPad(name,20);
                        name = name.substring(0,20);
                        qty = StringUtils.leftPad(qty,5);
                        price = StringUtils.leftPad(price,11);
                        amts = StringUtils.leftPad(amts,10);
                        line = name+qty+price+amts;
                    }
                    createImageFromString(line);
                    printImageReciept();
                    //PrintData(line,new Formatter().get(),Formatter.leftAlign());
                }
                else if(Common.RptSize.equals("4")){
                    String line = "";
                    if(Common.includeMRPinReceipt){
                        name = StringUtils.rightPad(name,29);
                        name = name.substring(0,14);
                        qty = StringUtils.leftPad(qty,5);
                        mrp = StringUtils.leftPad(mrp,7);
                        price = StringUtils.leftPad(price,10);
                        amts = StringUtils.leftPad(amts,10);
                        line = name+qty+mrp+price+amts;
                    }
                    else{
                        name = StringUtils.rightPad(name,35);
                        name = name.substring(0,35);
                        qty = StringUtils.leftPad(qty,5);
                        price = StringUtils.leftPad(price,11);
                        amts = StringUtils.leftPad(amts,10);
                        line = name+qty+price+amts;
                    }
                    PrintData(line,new Formatter().get(),Formatter.leftAlign());
                }
                else{
                    PrintData(name,new Formatter().get(),Formatter.leftAlign());
                    PrintData("           "+GetFormatedString(qty,4)+GetFormatedString(price,9)+GetFormatedString(amts,8),
                            new Formatter().get(),Formatter.leftAlign());
                }

            }
            PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            PrintData("TOTAL ITEMS   : "+Common.itemsCarts.size(),new Formatter().get(),Formatter.leftAlign());
            PrintData("TOTAL QUANTITY: "+formater.format(totalQty),new Formatter().get(),Formatter.leftAlign());
            totalAmt = billAmt-Common.discount;
            if(Common.discount>0){
                PrintData("TOTAL:"+String.format("%.0f",billAmt),new Formatter().get(),Formatter.rightAlign());
                PrintData("DISCOUNT(-):"+formater.format(Common.discount),new Formatter().get(),Formatter.rightAlign());
                PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            }
            else {
                PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            }
            if((Common.RptSize.equals("3") || Common.RptSize.equals("4")) && Common.includeMRPinReceipt &&
                    Common.discount==0){
                discountAmt = mrpTotalAmt-totalAmt;
                String discountAmtStr = formater.format(discountAmt);
                String discountxt = "AMOUNT YOU HAVE SAVED: "+discountAmtStr+"/-";
                PrintData(discountxt,new Formatter().get(),Formatter.rightAlign());
                PrintData("   ",new Formatter().get(),Formatter.leftAlign());
            }
            String ttAmtTxt = "NET AMOUNT:"+String.format("%.0f",totalAmt)+"/-";
            PrintData(ttAmtTxt,new Formatter().height().get(),Formatter.centerAlign());
            PrintData("  ",new Formatter().get(),Formatter.leftAlign());
            PrintData(Common.footerMsg,new Formatter().get(),Formatter.centerAlign());
            PrintData(" ",new Formatter().get(),Formatter.leftAlign());
            PrintData("  ",new Formatter().get(),Formatter.leftAlign());
            if(Common.RptSize.equals("3")){
                PaperCut();
            }
            if(Common.printKOT && !isReprint){
                PrintKOT();
            }
        } catch (Exception e) {
            e.printStackTrace();
            retVal = e.getMessage();
        }
        return retVal;
    }
    public  void  PrintItemWiseReport() throws  Exception{
        try{
            new BluetoothPrintItemWiseReport().execute("");
        }
        catch (Exception ex){
            throw  ex;
        }
    }
    public  void  PrintSaleReport() throws  Exception{
        try{
            new BluetoothPrintSaleReport().execute("");
        }
        catch (Exception ex){
            throw ex;
        }
    }
    public  void Print() throws Exception {
        try{
            new BluetoothPrintBill().execute("");
        }
        catch (Exception ex){
            throw  ex;
        }
    }
    class BluetoothPrintItemWiseReport extends AsyncTask<String, Void, String>
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
            String ret = "";
            try{
                GetBluetoothNameAndConnect();
                PrintItemWiseRpt();
            }
            catch (Exception ex){
                ret = ex.getMessage();
            }
            return ret;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(dialog.isShowing()){
                dialog.hide();
            }
            if(!result.isEmpty()){
               ItemReport.getInstance().showCustomDialog("Error",result);
            }
            else{
                Toast.makeText(context, "Print Queued Successfully.!", Toast.LENGTH_LONG).show();
                CloseBT();
            }
            super.onPostExecute(result);
        }
    }
    class BluetoothPrintSaleReport extends AsyncTask<String, Void, String>
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
            String ret = "";
            try{
                GetBluetoothNameAndConnect();
                PrintSaleRpt();
            }
            catch (Exception ex){
                ret = ex.getMessage();
            }
            return ret;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(dialog.isShowing()){
                dialog.hide();
            }
            if(!result.isEmpty()){
                SaleReportActivity.getInstance().showCustomDialog("Error",result);
            }
            else{
                Toast.makeText(context, "Print Queued Successfully.!", Toast.LENGTH_LONG).show();
                CloseBT();
            }
            super.onPostExecute(result);
        }
    }
    class BluetoothPrintBill extends AsyncTask<String, Void, String>
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
            String ret = "";
            try{
                GetBluetoothNameAndConnect();
            }
            catch (Exception ex){
                ret = ex.getMessage();
            }
            if(!ret.isEmpty()){
                return  ret;
            }
            int copiesprinted = Common.billcopies;
            while (copiesprinted>0){
                ret = PrintBill();
                if(!ret.isEmpty()){
                    break;
                }
                copiesprinted--;
            }
            return ret;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(dialog.isShowing()){
                dialog.hide();
            }
            if(!result.isEmpty()){
                HomeActivity.getInstance().showCustomDialog("Error",result);
            }
            else{
                Toast.makeText(context, "Print Queued Successfully.!", Toast.LENGTH_LONG).show();
                CloseBT();
                HomeActivity.getInstance().RefreshViews();
            }
            super.onPostExecute(result);
        }
    }

}
