package com.epson.epos2_printer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.GetPrinterSettingExListener;
import com.epson.epos2.printer.SetPrinterSettingExListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PrinterSettingExActivity extends Activity implements View.OnClickListener, GetPrinterSettingExListener, SetPrinterSettingExListener{

    private static final int DISCONNECT_INTERVAL = 500;//millseconds
    private Context mContext = null;
    private Printer mPrinter = null;

    private ProgressDialog mProgressDialog = null;
    private EditText mEditText = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printersetting_ex);

        mContext = this;
        mProgressDialog = new ProgressDialog(this);
        mEditText = findViewById(R.id.edtJsonText);
        int[] target = {
                R.id.btnGetPrinterSettingEx,
                R.id.btnSetPrinterSettingEx
        };

        for (int i = 0; i < target.length; i++) {
            Button button = (Button)findViewById(target[i]);
            button.setOnClickListener(this);
            button.setEnabled(false);
        }

        if(MainActivity.mEditTarget.getText().toString().contains("[")) {
            ShowMsg.showMsg(getString(R.string.error_msg_firm_update), mContext);
            return;
        }

        if(!initializeObject()) {
            return;
        }

        if(!connectPrinter()) {
            finalizeObject();
            return;
        }

        for (int i = 0; i < target.length; i++) {
            Button button = (Button)findViewById(target[i]);
            button.setOnClickListener(this);
            button.setEnabled(true);
        }
    }

    public void onDestroy() {
        disconnectPrinter();
        finalizeObject();
        super.onDestroy();
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnGetPrinterSettingEx:
                getPrinterSettingEx();
                break;
            case R.id.btnSetPrinterSettingEx:
                setPrinterSettingEx();
                break;
            default:
                break;
        }
    }

    // Get printer settings at once.
    protected void getPrinterSettingEx() {
        try {
            mPrinter.getPrinterSettingEx(Printer.PARAM_DEFAULT);
        }
        catch(Exception e) {
            ShowMsg.showException(e, "getPrinterSettingEx", mContext);
        }
    }

    // Set printer settings at once.
    protected void setPrinterSettingEx() {
        try {
            mPrinter.setPrinterSettingEx(Printer.PARAM_DEFAULT, mEditText.getText().toString());

            // Show wating restart message. onSetPrinterSetting stop this message.
            showPrinterRestartMessage();
        }
        catch(Exception e) {
            ShowMsg.showException(e, "setPrinterSettingEx", mContext);
        }
    }

    public void onGetPrinterSettingEx(Printer printer, final int code, final String jsonString) {
        runOnUiThread(new Runnable() {
            public synchronized void run() {

                if(code != Epos2CallbackCode.CODE_SUCCESS) {
                    ShowMsg.showResult(code, "onGetPrinterSettingEx", mContext);
                    return;
                }
                try {
                    JSONObject json = new JSONObject(jsonString);
                    mEditText.setText(json.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onSetPrinterSettingEx(Printer printer, final int code) {
        runOnUiThread(new Runnable() {
            public synchronized void run() {

                if(code != Epos2CallbackCode.CODE_SUCCESS) {
                    hidePrinterRestartMessage();
                    ShowMsg.showResult(code, "onSetPrinterSettingEx", mContext);
                    return;
                }
                mEditText.setText("");
                new Thread(new Runnable() {
                    public void run() {
                        reconnectPrinter_And_GetPrinterSetting();
                    }
                }).start();
            }
        });
    }

    protected void reconnectPrinter_And_GetPrinterSetting() {
        // Reconect printer with 3 sec interval until 120 sec done.
        boolean ret = reconnect(120, 3);
        // Hide wating restart message
        hidePrinterRestartMessage();

        // If error, back connection setting view to confirm connection setting.
        if(!ret) {
            runOnUiThread(new Runnable() {
                public synchronized void run() {
                    String msg = String.format(
                            "\t%s\n\t%s\n\n\t%s\n\t%s\n",
                            mContext.getString(R.string.title_msg_result),
                            "ERR_CONNECT",
                            mContext.getString(R.string.title_msg_description),
                            "reconnect");
                    ShowMsg.showMsg(msg, mContext);
                }
            });
            return;
        }

        // When restart success, get current setting to confirmation.
        try {
            mPrinter.getPrinterSettingEx(Printer.PARAM_DEFAULT);
        }
        catch(Exception e) {
            ShowMsg.showException(e, "getPrinterSettingEx", mContext);
            return;
        }

        //Wait onGetPrinterSetting callback to avoid multiple API calling.
        try {
            Thread.sleep(200);
        }
        catch(InterruptedException e) {
        }

    }

    protected boolean reconnect(int maxWait, int interval) {
        int timeout = 0;
        do{
            try {
                mPrinter.disconnect();
                break;
            }
            catch(Exception e) {
                try {
                    Thread.sleep(interval*1000);
                }
                catch(InterruptedException ex) {
                }
            }
            timeout += interval;    // Not correct measuring. When result is not TIMEOUT, wraptime do not equal interval.

            if(timeout > maxWait) {
                return false;
            }
        }while(true);


        // Sleep 30 sec due to some printer do not available immediately after power on. see your printer's spec sheet.
        // Please set the sleep time according to the printer.
        try {
            Thread.sleep(30000);
        }
        catch(InterruptedException e) {
        }

        timeout += 30;


        do {
            try {
                // For USB, change the target to "USB:".
                // Because USB port changes each time the printer restarts.
                // Please refer to the manual for details.
                mPrinter.connect(MainActivity.mEditTarget.getText().toString(), interval*1000);
                break;
            }
            catch (Exception e) {
                if(((Epos2Exception) e).getErrorStatus()== Epos2Exception.ERR_CONNECT){
                    try {
                        Thread.sleep(interval*1000);
                    }
                    catch(InterruptedException ex) {
                    }
                }
            }

            timeout += interval;    // Not correct measuring. When result is not TIMEOUT, wraptime do not equal interval.

            if(timeout > maxWait) {
                return false;
            }
        }while(true);

        return true;
    }

    // Restarting Message
    protected void showPrinterRestartMessage(){
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(getString(R.string.restart_SettingEx_msg));
        mProgressDialog.show();
    }

    protected void hidePrinterRestartMessage(){
        mProgressDialog.dismiss();
    }

    // Printer control
    private boolean initializeObject() {
        try {
            mPrinter = new Printer(((SpnModelsItem) MainActivity.mSpnSeries.getSelectedItem()).getModelConstant(),
                    ((SpnModelsItem) MainActivity.mSpnLang.getSelectedItem()).getModelConstant(),
                    mContext);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", mContext);
            return false;
        }
        mPrinter.setGetPrinterSettingExListener(this);
        mPrinter.setSetPrinterSettingExListener(this);

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.setGetPrinterSettingExListener(null);
        mPrinter.setSetPrinterSettingExListener(null);
        mPrinter = null;
    }

    protected boolean connectPrinter(){
        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect(MainActivity.mEditTarget.getText().toString(), Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", mContext);
            return false;
        }
        return true;
    }

    protected void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        while (true) {
            try {
                mPrinter.disconnect();
                break;
            } catch (final Exception e) {
                if (e instanceof Epos2Exception) {
                    //Note: If printer is processing such as printing and so on, the disconnect API returns ERR_PROCESSING.
                    if (((Epos2Exception) e).getErrorStatus() == Epos2Exception.ERR_PROCESSING) {
                        try {
                            Thread.sleep(DISCONNECT_INTERVAL);
                        } catch (Exception ex) {
                        }
                    }else{
                        runOnUiThread(new Runnable() {
                            public synchronized void run() {
                                ShowMsg.showException(e, "disconnect", mContext);
                            }
                        });
                        break;
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        public synchronized void run() {
                            ShowMsg.showException(e, "disconnect", mContext);
                        }
                    });
                    break;
                }
            }
        }
    }

}
