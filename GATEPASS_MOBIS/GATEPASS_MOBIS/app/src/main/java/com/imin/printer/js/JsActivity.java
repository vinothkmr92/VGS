package com.imin.printer.js;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;

import com.imin.library.SystemPropManager;
import com.imin.printer.R;
import android.util.Base64;



public class JsActivity extends Activity {
    private WebView mWebView;
    private Context mContext;
    private String TAG="JsActivity lsy===";
    PrinterPluginUtils printerPluginUtils;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_print);
        this.mContext =this;
        this.mWebView =  (WebView)findViewById(R.id.webview);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(this, "JsActivity");
        doWebViewPrint();
        printerPluginUtils =new PrinterPluginUtils(this);
    }

    public void doWebViewPrint(){

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        if (SystemPropManager.getPlaform().startsWith("mt")){
            Log.e(TAG,"lsy===mtk");
            mWebView.loadUrl("https://mp.imin.sg/PrintHtml/AppMtkPrint.html");//("http://192.168.220.229:8848/233master/PrintHtml/AppMTKPrint.html");//
        } else {
            Log.e(TAG,"lsy===rk");
            mWebView.loadUrl("https://mp.imin.sg/PrintHtml/AppRkPrint.html");//("http://192.168.220.229:8848/233master/PrintHtml/AppRKPrint.html");//
        }


    }


    @SuppressLint("SetJavaScriptEnabled")
    public void callJS(){
        mWebView.loadUrl("javascript:test()");
    }

    @JavascriptInterface
    public void callAndroid(byte[] msg){
        Log.e(TAG,"contrast = 2222" );
        callPrint(msg,25);

    }

    private void callPrint(byte[] msg,double contrast){
        try {
            //byte[] msgByte = Base64.decode(msg,Base64.NO_PADDING);
            byte[] msgByte = Base64.decode(msg,Base64.DEFAULT);
            printerPluginUtils.print(msgByte,contrast);
        } catch (Exception e){
            Log.i("lsy====","lsy==Exception="+e.getMessage());
        }
    }
}
