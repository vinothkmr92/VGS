package com.example.vinoth.pms;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PDFUtil {
    public ReceiptData rptData;
    public Context context;
    public Activity activity;
    public String pdfFileName;
    public void CreatePDF(){
        new GeneratePDF().execute();
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
    class GeneratePDF extends AsyncTask<String, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(context);
        boolean isSuccess = false;
        String error = "";
        private ReceiptData _receiptData;

        @Override
        protected void onPreExecute()
        {
            _receiptData = rptData;
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Generating PDF.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String filePath = "";
            try
            {
                int headerHeight = 200;
                int footerHeight = 150;
                int itemHeight = 30; // height per line item
                int totalItems = rptData.itemsCarts.size();
                int pageHeight = headerHeight + (itemHeight * totalItems) + footerHeight;
                int pageWidth = Common.RptSize.equals("2") ? 164: 226; // 80mm width
                PdfDocument document = new PdfDocument();

                // Create page info with your calculated height
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                Paint headerPaint = new Paint();
                headerPaint.setTextAlign(Paint.Align.CENTER);
                headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                headerPaint.setTextSize(15);

                Paint addresslinePaint = new Paint();
                addresslinePaint.setTextAlign(Paint.Align.CENTER);
                addresslinePaint.setTextSize(12);

                Paint columnPaint = new Paint();
                columnPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                columnPaint.setTextAlign(Paint.Align.LEFT);
                columnPaint.setTextSize(10);

                Paint linesPaint = new Paint();
                linesPaint.setTextSize(10);

                Paint leftPaint = new Paint();
                leftPaint.setTextAlign(Paint.Align.LEFT);
                leftPaint.setTextSize(10);

                Paint centerPaintBold = new Paint();
                centerPaintBold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                centerPaintBold.setTextAlign(Paint.Align.CENTER);
                centerPaintBold.setTextSize(12);

                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa", Locale.getDefault());
                String dateStr = format.format(rptData.billDate);
                Bitmap bitmapIcon = Common.shopLogo;
                //posPtr.printNormal(ESC+" F");
                if(bitmapIcon!=null){
                    try {
                        canvas.drawBitmap(bitmapIcon,0f,0f,null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                DecimalFormat formater = new DecimalFormat("#.###");
                // Draw Header
                canvas.drawText(Common.headerMeg, pageWidth/2, 30, headerPaint);
                canvas.drawText(Common.addressline,pageWidth/2,50,addresslinePaint);
                canvas.drawText("BILL NO : "+rptData.billno,10,70,leftPaint);
                if(!rptData.waiter.isEmpty() && !rptData.waiter.equals("NONE")){
                    canvas.drawText("NAME : "+rptData.waiter,10,90,leftPaint);
                }
                canvas.drawText("DATE : "+dateStr,10,110,leftPaint);

                if(Common.RptSize.equals("2")){
                    canvas.drawText("--------------------------------",10,130,leftPaint);
                    canvas.drawText("ITEM        QTY    RATE   AMOUNT",10,150,columnPaint);
                    canvas.drawText("--------------------------------",10,170,leftPaint);
                }
                else {
                    canvas.drawText("---------------------------------------------------",10,130,columnPaint);
                    canvas.drawText("ITEM NAME             QTY      PRICE    AMOUNT",10,150,columnPaint);
                    canvas.drawText("---------------------------------------------------",10,170,columnPaint);
                }


                // Draw Items
                int currentY = headerHeight;
                int lineh = 15;
                double totalAmt = 0d;
                double billAmt = 0d;
                for(int k=0;k<rptData.itemsCarts.size();k++){
                    String name = rptData.itemsCarts.get(k).getItem_Name();
                    String qty = formater.format(rptData.itemsCarts.get(k).Qty);

                    String price = formater.format(rptData.itemsCarts.get(k).getPrice());
                    Double amt = rptData.itemsCarts.get(k).getPrice()*rptData.itemsCarts.get(k).Qty;
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
                            name = name.substring(0,51);
                        }
                        qty = StringUtils.leftPad(qty,35);
                        price = StringUtils.leftPad(price,11);
                        amts = StringUtils.leftPad(amts,10);
                        line = qty+price+amts+"\n";
                    }
                    canvas.drawText(name,10,currentY,linesPaint);
                    currentY+=lineh;
                    canvas.drawText(line,10,currentY,linesPaint);
                    currentY += lineh;
                }
                currentY+=lineh;
                if(Common.RptSize.equals("2")){
                    canvas.drawText("--------------------------------",10,currentY,columnPaint);
                }
                else {
                    canvas.drawText("---------------------------------------------------",10,currentY,columnPaint);
                }
                currentY=currentY+itemHeight;
                Integer totalItemst = rptData.itemsCarts.size();
                Double totalQty = rptData.itemsCarts.stream().mapToDouble(c->c.Qty).sum();
                canvas.drawText("Total Items : "+totalItemst,10,currentY,leftPaint);
                currentY+=itemHeight;
                canvas.drawText("Total Qty : "+formater.format(totalQty),10,currentY,leftPaint);
                currentY+=itemHeight;
                if(rptData.discount>0 || rptData.advance>0){
                    String tt = String.format("%.0f",billAmt);
                    tt = StringUtils.leftPad(tt,6);
                    String discount = "(-)"+formater.format(rptData.discount);
                    discount = StringUtils.leftPad(discount,6);
                    String advance = "(-)"+formater.format(rptData.advance);
                    advance = StringUtils.leftPad(advance,6);
                    String ttstring = "Sub Total:";
                    String discstring = "Discount:";
                    String advancestring = "Advance:";
                    int padleft = Common.RptSize.equals("2") ? 26:40;
                    ttstring = StringUtils.leftPad(ttstring,padleft);
                    discstring = StringUtils.leftPad(discstring,padleft);
                    advancestring= StringUtils.leftPad(advancestring,padleft);
                    canvas.drawText(ttstring+tt,10,currentY,leftPaint);
                    currentY+=itemHeight;
                    if(rptData.discount>0){
                        canvas.drawText(discstring+discount,10,currentY,leftPaint);
                        currentY+=itemHeight;
                    }
                    if(rptData.advance>0){
                        canvas.drawText(advancestring+advance,10,currentY,leftPaint);
                        currentY+=itemHeight;
                    }
                }
                totalAmt = billAmt-rptData.discount-rptData.advance;
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                formatter.setMaximumFractionDigits(0);
                String symbol = formatter.getCurrency().getSymbol();

                Double gst = totalAmt*(2.5/100);
                NumberFormat gstformater = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                gstformater.setMaximumFractionDigits(2);
                gstformater.setMinimumFractionDigits(2);
                String subTotal = "Sub Total :"+ gstformater.format(totalAmt).replace(symbol,"");
                String sgst = "SGST (2.5%) :"+ gstformater.format(gst).replace(symbol,"");
                String cgst = "CGST (2.5%) :"+ gstformater.format(gst).replace(symbol,"");
                if(!rptData.isGST){
                    gst = 0d;
                }
                String totalamt = formatter.format(totalAmt+gst+gst).replace(symbol,symbol+" ");
                String txttotal = "Grand Total  "+totalamt+"/-";
                //currentY+=itemHeight;
                if(rptData.isGST){
                    canvas.drawText(subTotal,10,currentY,leftPaint);
                    currentY+=itemHeight;
                    canvas.drawText(sgst,10,currentY,leftPaint);
                    currentY+=itemHeight;
                    canvas.drawText(cgst,10,currentY,leftPaint);
                    currentY+=itemHeight;
                }
                //currentY=currentY+itemHeight;
                canvas.drawText(txttotal,pageWidth/2,currentY,centerPaintBold);
                currentY=currentY+itemHeight;
                canvas.drawText(Common.footerMsg,pageWidth/2,currentY,addresslinePaint);
                // Finish and Save
                document.finishPage(page);
                File file = new File(context.getExternalFilesDir(DOWNLOAD_SERVICE), pdfFileName);
                try {
                    document.writeTo(new FileOutputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                document.close();
                filePath = file.getAbsolutePath();
                isSuccess = true;
            }
            catch (Exception e)
            {
                Log.e("Error",e.getMessage(),e);
                error = "ERROR:"+e.getMessage();
            }
            return  filePath;
        }

        @Override
        protected void onPostExecute(String filePath)
        {
            if(dialog.isShowing()){
                dialog.hide();
            }
            if(isSuccess){
                SaleReportActivity.getInstance().OpenPDF(pdfFileName);
            }
            else {
                if(error.isEmpty()){
                    PassMsgToActivity("Error","Unable to Generate PDF. Please Contact Support Team.");
                }
                else {
                    PassMsgToActivity("Error",error);
                }
            }
            super.onPostExecute(filePath);
        }
    }
}
