package com.example.vinoth.vgspos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.request.android.RequestHandler;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrintWifi {
    private Context context;
    private Thread hThread;
    private WiFiPort wifiPort;
    public ESCPOSPrinter posPtr;
    private int rtn;
    private boolean printSale;
    // 0x1B
    private final char ESC = ESCPOS.ESC;
    public  PrintWifi(Context cntx,boolean prtSale) {
        posPtr=new ESCPOSPrinter();
        wifiPort = WiFiPort.getInstance();
        context = cntx;
        printSale = prtSale;
    }
    @Override
    protected void finalize() throws Throwable {
        try
        {
            if(wifiPort != null)
                wifiPort.disconnect();
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
            int copiesprinted = Common.billcopies;
            while (copiesprinted>0){
                PrintBill();
                copiesprinted--;
            }
            wifiPort.disconnect();
            return 1;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            HomeActivity.getInstance().showCustomDialog("Error",e.getMessage());
        }
        return 0;
    }
    private void PrintBill() throws UnsupportedEncodingException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
        Date date = new Date();
        String dateStr = format.format(date);
        posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+Common.headerMeg+"\r\n");
        posPtr.printNormal("\n");
        posPtr.printNormal(ESC+"|lABILL NO: "+Common.billNo+"\n");
        if(!Common.waiter.equals("NONE")){
            posPtr.printNormal(ESC+"|lAUSER: "+Common.waiter);
            posPtr.printNormal("\n");
        }
        posPtr.printNormal(ESC+"|lADate: "+dateStr+"\n\n");
        posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME             QTY      PRICE    AMOUNT\n");
        double totalQty = 0d;
        double totalAmt = 0d;
        for(int k=0;k<Common.itemsCarts.size();k++){
            String name = Common.itemsCarts.get(k).getItem_Name();
            String qty = String.valueOf(Common.itemsCarts.get(k).getQty());
            String price = String.format("%.0f",Common.itemsCarts.get(k).getPrice());
            Double amt = Common.itemsCarts.get(k).getPrice()*Common.itemsCarts.get(k).getQty();
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
        posPtr.lineFeed(1);
        posPtr.printNormal(ESC+"|cA"+Common.footerMsg+"\n");
        posPtr.lineFeed(5);
        posPtr.cutPaper();
    }
    public int PrintKOT() throws InterruptedException
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
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            String dateStr = format.format(date);
            posPtr.printNormal(ESC+"|cA"+ESC+"|2CKOT\r\n");
            posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+Common.headerMeg+"\r\n");
            posPtr.printNormal("\n");
            posPtr.printNormal(ESC+"|lABILL NO: "+Common.billNo+"\n");
            if(!Common.waiter.equals("NONE")){
                posPtr.printNormal(ESC+"|lAUSER: "+Common.waiter);
                posPtr.printNormal("\n");
            }
            posPtr.printNormal(ESC+"|lADate: "+dateStr+"\n\n");
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME                                   QTY\n");
            double totalQty = 0d;
            double totalAmt = 0d;
            for(int k=0;k<Common.itemsCarts.size();k++){
                String name = Common.itemsCarts.get(k).getItem_Name();
                String qty = String.valueOf(Common.itemsCarts.get(k).getQty());
                name = StringUtils.rightPad(name,42);
                qty = StringUtils.leftPad(qty,5);
                String line = name+qty+"\n";
                posPtr.printNormal(line);
            }
            posPtr.lineFeed(5);
            posPtr.cutPaper();
            wifiPort.disconnect();
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
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            String dateStr = format.format(date);
            posPtr.printNormal(ESC+"|cA"+ESC+"|3CITEM WISE REPORT\r\n");
            posPtr.printNormal("\n");
            posPtr.printNormal(ESC+"|lAFROM DATE: "+Common.saleReportFrmDate+"\n");
            posPtr.printNormal(ESC+"|lATO   DATE: "+Common.saleReportToDate+"\n\n");
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"ITEM NAME             QTY\n");
            double totalQty = 0d;
            double totalAmt = 0d;
            for(int k=0;k<Common.itemsRpts.size();k++){
                ItemsRpt itemsRpt = Common.itemsRpts.get(k);
                String itemname = itemsRpt.getItemName();
                Double qty = itemsRpt.getQuantity();
                String qtystr=String.format("%.0f",qty);
                itemname = StringUtils.rightPad(itemname,20);
                qtystr = StringUtils.leftPad(qtystr,5);
                String line = itemname+qtystr+"\n";
                posPtr.printNormal(line);
            }
            String totalamt = String.format("%.0f",totalAmt);
            String txttotal = "TOTAL AMOUNT: "+totalamt+"/-";
            //posPtr.lineFeed(1);
            //posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
            posPtr.lineFeed(5);
            posPtr.cutPaper();
            wifiPort.disconnect();
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
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            String dateStr = format.format(date);
            posPtr.printNormal(ESC+"|cA"+ESC+"|3CSALE REPORT\r\n");
            posPtr.printNormal("\n");
            posPtr.printNormal(ESC+"|lAFROM DATE: "+Common.saleReportFrmDate+"\n");
            posPtr.printNormal(ESC+"|lATO   DATE: "+Common.saleReportToDate+"\n\n");
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+"BILL NO         BILL_DATE               AMOUNT\n");
            double totalQty = 0d;
            double totalAmt = 0d;
            for(int k=0;k<Common.saleReports.size();k++){
                SaleReport saleReport = Common.saleReports.get(k);
                String billno = saleReport.getBillNo();
                String billDate = saleReport.getBillDate();
                Double amt = saleReport.getBillAmount();
                totalAmt+=amt;
                String amts=String.format("%.0f",amt);
                billno = StringUtils.rightPad(billno,7);
                billDate = StringUtils.leftPad(billDate,18);
                amts = StringUtils.leftPad(amts,21);
                String line = billno+billDate+amts+"\n";
                posPtr.printNormal(line);
            }
            String totalamt = String.format("%.0f",totalAmt);
            String txttotal = "TOTAL AMOUNT: "+totalamt+"/-";
            posPtr.lineFeed(1);
            posPtr.printNormal(ESC+"|cA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
            posPtr.lineFeed(5);
            posPtr.cutPaper();
            wifiPort.disconnect();
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
            new ConnectPrinter().execute(Common.printerIP);
        }
        catch (Exception ex){
            throw  ex;
        }
    }
    class ConnectPrinterKOT extends AsyncTask<String, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        @Override
        protected void onPreExecute()
        {
            dialog.setTitle("Please Wait");
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
                retVal = new Integer(0);
            }
            catch (IOException e)
            {
                Log.e("Wifi-connection:",e.getMessage(),e);
                //Toast.makeText(HomeActivity.getInstance().getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG);
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
                HomeActivity.getInstance().showCustomDialog("Error","Printer Connection Failed.");
            }
            super.onPostExecute(result);
        }
    }
    class ConnectPrinter extends AsyncTask<String, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        @Override
        protected void onPreExecute()
        {
            dialog.setTitle("Please Wait");
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
                retVal = new Integer(0);
            }
            catch (IOException e)
            {
                Log.e("Wifi-connection:",e.getMessage(),e);
                //Toast.makeText(HomeActivity.getInstance().getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG);
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
                    if(printSale){
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
                HomeActivity.getInstance().showCustomDialog("Error","Printer Connection Failed.");
            }
            super.onPostExecute(result);
        }
    }
}
