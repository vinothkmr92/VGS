package com.ngx.nix;

import android.os.Bundle;
import android.support.p006v7.app.AppCompatActivity;
import android.support.p006v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.ngx.mp100sdk.Intefaces.INGXCallback;
import com.ngx.mp100sdk.NGXPrinter;

public class MP100Main extends AppCompatActivity {
    public static NGXPrinter ngxPrinter = NGXPrinter.getNgxPrinterInstance();
    private INGXCallback ingxCallback;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0425R.layout.activity_mp100_main);
        setSupportActionBar((Toolbar) findViewById(C0425R.C0427id.toolbar));
        try {
            this.ingxCallback = new INGXCallback() {
                public void onRunResult(boolean isSuccess) {
                    Log.i("NGX", "onRunResult:" + isSuccess);
                }

                public void onReturnString(String result) {
                    Log.i("NGX", "onReturnString:" + result);
                }

                public void onRaiseException(int code, String msg) {
                    Log.i("NGX", "onRaiseException:" + code + ":" + msg);
                }
            };
            ngxPrinter.initService(this, this.ingxCallback);
        } catch (Exception e) {
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0425R.C0428menu.menu_mp100_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        Toast.makeText(this, "Destroy Called", 0).show();
        ngxPrinter.onActivityDestroy();
        super.onDestroy();
    }
}
