package com.imin.printer;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;


public class PrinterApplication extends Application {
    private int orientation;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initWindow();
    }

    private void initWindow() {
//        orientation = getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            AutoSizeConfig.getInstance().setDesignWidthInDp(1280).setDesignHeightInDp(720);
//        } else {
//            AutoSizeConfig.getInstance().setDesignWidthInDp(720).setDesignHeightInDp(1280);
//        }
        Log.e("imin_printer"," Utils.getScreenWidth(this)==>"+Utils.getWidth(this)+",Utils.getScreenHeight(this)==>"+Utils.getHeight(this));
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (Utils.getWidth(this)<=1360){
            dm.density = 1.8f;
            dm.scaledDensity = 1.8f;
            dm.densityDpi = 200;
        }else{
            dm.density = 2.0f;
            dm.scaledDensity = 2.0f;
            dm.densityDpi = 220;
        }

        this.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(@NonNull Configuration newConfig) {
                //字体发生更改，重新对scaleDensity进行赋值
                if (newConfig != null && newConfig.fontScale > 0){
                    Log.e("AndroidAutoSize"," AndroidAutoSize1111==>"+getResources().getDisplayMetrics().density+"  "+getResources().getDisplayMetrics().scaledDensity);

                    //appScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    //替换Activity的density, scaleDensity, densityDpi
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    if (Utils.getWidth(context)<=1360){
                        dm.density = 1.8f;
                        dm.scaledDensity = 1.8f;
                        dm.densityDpi = 200;
                    }else{
                        dm.density = 2.0f;
                        dm.scaledDensity = 2.0f;
                        dm.densityDpi = 220;
                    }
                }
            }

            @Override
            public void onLowMemory() {

            }
        });
    }
}

