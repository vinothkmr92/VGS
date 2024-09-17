package com.imin.printer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;

import com.imin.printerlib.IminPrintUtils;

/**
 * @author xghsir
 * Created by 肖根华 on 2020/4/29.
 */
public class TestService extends Service {

    String text = "";
//    private IminPrintUtils TestPrintActivity.mIminPrintUtils;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        text = getString(R.string.text_print);

//        TestPrintActivity.mIminPrintUtils = TestPrintActivity.TestPrintActivity.mIminPrintUtils;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        int connectType = intent.getIntExtra("connectType",0);
//        if(connectType == 1){
//            TestPrintActivity.mIminPrintUtils.initPrinter(IminPrintUtils.PrintConnectType.USB);
//        }else if(connectType == 2){
//            TestPrintActivity.mIminPrintUtils.initPrinter(IminPrintUtils.PrintConnectType.BLUETOOTH);
//        }else  if(connectType == 3){
//            TestPrintActivity.mIminPrintUtils.initPrinter(IminPrintUtils.PrintConnectType.SPI);
//        }

//        new Thread(new Runnable() {
//            @SuppressLint("NewApi")
//            @Override
//            public void run() {

                TestPrintActivity.mIminPrintUtils.printText(text);
                TestPrintActivity.mIminPrintUtils.setAlignment(1);
                TestPrintActivity.mIminPrintUtils.printText(text);
                TestPrintActivity.mIminPrintUtils.setAlignment(0);
                TestPrintActivity.mIminPrintUtils.setTextSize(38);
                TestPrintActivity.mIminPrintUtils.printText(text);
                TestPrintActivity.mIminPrintUtils.setTextSize(28);
                TestPrintActivity.mIminPrintUtils.setTextWidth(300);
                TestPrintActivity.mIminPrintUtils.printText(text);
                TestPrintActivity.mIminPrintUtils.setTextWidth(576);
                TestPrintActivity.mIminPrintUtils.setTextLineSpacing(1.5f);
                TestPrintActivity.mIminPrintUtils.printText(text);
                TestPrintActivity.mIminPrintUtils.setTextLineSpacing(1.0f);
                TestPrintActivity.mIminPrintUtils.setTextStyle(Typeface.BOLD_ITALIC);
                TestPrintActivity.mIminPrintUtils.printText(text);
                TestPrintActivity.mIminPrintUtils.setTextStyle(Typeface.NORMAL);
                TestPrintActivity.mIminPrintUtils.setTextTypeface(Typeface.MONOSPACE);
                TestPrintActivity.mIminPrintUtils.printText(text);
                TestPrintActivity.mIminPrintUtils.setTextTypeface(Typeface.DEFAULT);


                String[] strings3 = new String[]{"Description", "Description Description Description Description Description@48", "192.00"};
                int[] colsWidthArr3 = new int[]{1, 2, 1};
                int[] colsAlign3 = new int[]{0, 1, 2};
                int[] colsSize3 = new int[]{26, 26, 26};
                TestPrintActivity.mIminPrintUtils.printColumnsText(strings3, colsWidthArr3,
                        colsAlign3, colsSize3);
                TestPrintActivity.mIminPrintUtils.printColumnsText(strings3, colsWidthArr3,
                        colsAlign3, colsSize3);
                TestPrintActivity.mIminPrintUtils.printColumnsText(strings3, colsWidthArr3,
                        colsAlign3, colsSize3);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rabit);
                TestPrintActivity.mIminPrintUtils.printSingleBitmap(bitmap);
                TestPrintActivity.mIminPrintUtils.printSingleBitmap(bitmap,1);
                TestPrintActivity.mIminPrintUtils.printSingleBitmap(bitmap,2);
                TestPrintActivity.mIminPrintUtils.printAndFeedPaper(100);
                if (Build.MODEL.contains("M2") || Build.MODEL.equals("D1") || Build.MODEL.equals("D1-Pro") || Build.MODEL.contains("Swift")|| Build.MODEL.contains("D3-510")) {
                    TestPrintActivity.mIminPrintUtils.setPageFormat(1);
                }else {
                    TestPrintActivity.mIminPrintUtils.setPageFormat(0);
                }
                TestPrintActivity.mIminPrintUtils.sendRAWData(new byte[]{0x1B,0x40});
//            }
//        }).start();


        return super.onStartCommand(intent, flags, startId);
    }
}
