package com.example.mpos_orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pg.merchant.PaytmChecksum;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    TextView amountPayingTxtview;
    TextView paymentStatusTextView;
    ImageView paymentStatusImageView;
    Button payBtn;
    Dialog progressBar;
    Integer ActivityRequestCode = 2;
    public String merchantID = "mlcMRC76831209884014";
    public String merchantKey = "zT6kAm6Ca53prWYM";
    public String orderID = "";
    public String checksum="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        amountPayingTxtview = (TextView) findViewById(R.id.amountpaying);
        payBtn = (Button) findViewById(R.id.payButton);
        progressBar = new Dialog(PaymentActivity.this);
        paymentStatusTextView = (TextView)findViewById(R.id.paymentstatustxt);
        paymentStatusImageView = (ImageView)findViewById(R.id.paymentstatus);
        paymentStatusImageView.setVisibility(View.INVISIBLE);
        paymentStatusTextView.setVisibility(View.INVISIBLE);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        payBtn.setOnClickListener(PaymentActivity.this);
        if(CommonUtil.paymentMember==null){
            showCustomDialog("Login","Please Login to Proceed with Payment.");
            LoadLogin();
        }
        else{
            if(CommonUtil.pendingAmt>0){
                amountPayingTxtview.setText(CommonUtil.pendingAmt.toString());
            }
            else{
                showCustomDialog("Warning","You don't have any pending amount to pay.");
                LoadLogin();
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.payButton:
                btnProcessEvent();
                break;
        }
    }
    public JSONObject GetPaytmReqBody(){
        String txnAmountString =amountPayingTxtview.getText().toString();
        Random rand = new Random();
        int r = rand.nextInt(100);
        String orderIdString = "order_"+CommonUtil.paymentMember.getMemberID()+r;
        orderID = orderIdString;
        JSONObject body = new JSONObject();
        try{
            body.put("requestType", "Payment");
            body.put("mid", merchantID);
            body.put("websiteName", "DEFAULT");
            body.put("orderId", orderIdString);
            //body.put("callbackUrl", "");

            JSONObject txnAmount = new JSONObject();
            txnAmount.put("value", txnAmountString);
            txnAmount.put("currency", "INR");

            JSONObject userInfo = new JSONObject();
            userInfo.put("custId", "CUST_"+CommonUtil.paymentMember.getMemberID());
            //userInfo.put("")
            body.put("txnAmount", txnAmount);
            body.put("userInfo", userInfo);
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        return body;
    }
    public String GetTxnToken(String checksum,JSONObject body){
        String txnTokenString = "";
        try{

            JSONObject paytmParams = new JSONObject();
            JSONObject head = new JSONObject();
            head.put("signature", checksum);

            paytmParams.put("body", body);
            paytmParams.put("head", head);

            String post_data = paytmParams.toString();
            String orderid = body.getString("orderId");

            /* for Staging */
            //URL url = new URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid="+midString+"&orderId="+orderIdString);

            /* for Production */
            URL url = new URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid="+merchantID+"orderId="+orderid);

            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
                requestWriter.writeBytes(post_data);
                requestWriter.close();
                String responseData = "";
                InputStream is = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                if ((responseData = responseReader.readLine()) != null) {
                    System.out.append("Response: " + responseData);
                    JSONObject res = new JSONObject(responseData);
                    String bodyjson = res.get("body").toString();
                    JSONObject bodyr = new JSONObject(bodyjson);
                    txnTokenString = bodyr.get("txnToken").toString();
                }
                responseReader.close();
            } catch (Exception exception) {
                Log.e("ERROR", exception.getMessage());
                showCustomDialog("Error",exception.getMessage());
            }
        }
        catch (Exception ex){
            Log.e("ERROR", ex.getMessage());
            showCustomDialog("Error",ex.getMessage());
        }
        return  txnTokenString;
    }
    public  void ProcessPayment(String txnToken){
        String txnAmountString =amountPayingTxtview.getText().toString();
        //String host = "https://securegw-stage.paytm.in/";
        String host = "https://securegw.paytm.in/";
        String errors = "";
        if(txnToken.equalsIgnoreCase("")){
            errors +="Invalid Transaction Token";
        }
        if(txnAmountString.equalsIgnoreCase("")){
            errors +="Enter valid Amount here\n";
        }
        Toast.makeText(this, errors, Toast.LENGTH_SHORT).show();
        if(errors.equalsIgnoreCase("")){
            String orderDetails = "MID: " + merchantID + ", OrderId: " + orderID + ", TxnToken: " + txnToken + ", Amount: " + txnAmountString;
            Toast.makeText(this, orderDetails, Toast.LENGTH_SHORT).show();
            String callBackUrl = host + "theia/paytmCallback?ORDER_ID="+orderID;
            PaytmOrder paytmOrder = new PaytmOrder(orderID, merchantID, txnToken, txnAmountString, callBackUrl);
            TransactionManager transactionManager = new TransactionManager(paytmOrder,
                    new PaytmPaymentTransactionCallback(){

                        @Override
                        public void onTransactionResponse(Bundle bundle) {
                            String sr = "sr";
                            Toast.makeText(PaymentActivity.this, "Response (onTransactionResponse) : "+bundle.toString(), Toast.LENGTH_SHORT).show();
                            showCustomDialog("Result",bundle.toString());
                            new GetPaytmTransactionStatus().execute();
                        }

                        @Override
                        public void networkNotAvailable() {

                        }

                        @Override
                        public void onErrorProceed(String s) {

                        }

                        @Override
                        public void clientAuthenticationFailed(String s) {

                        }

                        @Override
                        public void someUIErrorOccurred(String s) {

                        }

                        @Override
                        public void onErrorLoadingWebPage(int i, String s, String s1) {

                        }

                        @Override
                        public void onBackPressedCancelTransaction() {

                        }

                        @Override
                        public void onTransactionCancel(String s, Bundle bundle) {

                        }
                    });
            transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
            transactionManager.startTransaction(this, ActivityRequestCode);
        }
    }
    public void btnProcessEvent (){
      JSONObject bodystring = GetPaytmReqBody();
      new GetPaytmChecksum().execute(bodystring.toString());

    }
    public class GetPaytmTransactionStatus extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            if(r.startsWith("ERROR")){
                showCustomDialog("Exception",r);
            }
            else {
                try{
                    JSONObject result = new JSONObject(r);
                    JSONObject resultbody = result.getJSONObject("body");
                    JSONObject resultInfo = resultbody.getJSONObject("resultInfo");
                    String status = resultInfo.getString("resultMsg").toString();
                    showCustomDialog("Result",status);
                    showCustomDialog("TransactionID",resultbody.getString("txnId").toString());
                    showCustomDialog("paymentMode",resultbody.getString("paymentMode").toString());
                }
                catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage());
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String z="";
            try{
                JSONObject paytmParams = new JSONObject();
                JSONObject head = new JSONObject();
                head.put("signature", checksum);
                JSONObject body = new JSONObject();
                /* Find your MID in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys */
                body.put("mid", merchantID);

                /* Enter your order id which needs to be check status for */
                body.put("orderId", orderID);

                paytmParams.put("body", body);
                paytmParams.put("head", head);

                String post_data = paytmParams.toString();
                String orderid = body.getString("orderId");
                /* for Staging */
                //URL url = new URL("https://securegw-stage.paytm.in/v3/order/status");

                /* for Production */
                URL url = new URL("https://securegw.paytm.in/v3/order/status");



                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
                    requestWriter.writeBytes(post_data);
                    requestWriter.close();
                    String responseData = "";
                    InputStream is = connection.getInputStream();
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                    if ((responseData = responseReader.readLine()) != null) {
                        System.out.append("Response: " + responseData);
                        z = responseData;
                    }
                    responseReader.close();
                } catch (Exception exception) {
                    z = "ERROR:"+exception.getMessage();
                }
            }
            catch (Exception ex){
                z = "ERROR:"+ex.getMessage();
            }
            return z;
        }
    }
    public class GetPaytmTransactionToken extends AsyncTask<String,String,String>
    {
        String bodystring;
        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            if(r.startsWith("ERROR")){
                showCustomDialog("Exception",r);
            }
            else {
                try{
                    String txnToken = r;
                    try{
                        ProcessPayment(txnToken);
                    }
                    catch (Exception ex){
                        showCustomDialog("Error",ex.getMessage());
                    }
                }
                catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage());
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String z="";
            try{
                bodystring = params[0];
                String checksum = params[1];
                JSONObject paytmParams = new JSONObject();
                JSONObject head = new JSONObject();
                head.put("signature", checksum);
                JSONObject body = new JSONObject(bodystring);
                paytmParams.put("body", body);
                paytmParams.put("head", head);

                String post_data = paytmParams.toString();

                /* for Staging */
                //URL url = new URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid="+midString+"&orderId="+orderIdString);

                /* for Production */
                URL url = new URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid="+merchantID+"&orderId="+orderID);

                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
                    requestWriter.writeBytes(post_data);
                    requestWriter.close();
                    String responseData = "";
                    InputStream is = connection.getInputStream();
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                    if ((responseData = responseReader.readLine()) != null) {
                        System.out.append("Response: " + responseData);
                        JSONObject res = new JSONObject(responseData);
                        String bodyjson = res.get("body").toString();
                        JSONObject bodyr = new JSONObject(bodyjson);
                        z = bodyr.get("txnToken").toString();
                    }
                    // System.out.append("Request: " + post_data);
                    responseReader.close();
                } catch (Exception exception) {
                    z = "ERROR:"+exception.getMessage();
                }
            }
            catch (Exception ex){
                z = "ERROR:"+ex.getMessage();
            }
            return z;
        }
    }
    public class GetPaytmChecksum extends AsyncTask<String,String,String>
    {
        String bodystring;
        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            if(r.startsWith("ERROR")){
                showCustomDialog("Exception",r);
            }
            else {
                checksum = r;
                try{
                    new GetPaytmTransactionToken().execute(bodystring,checksum);
                }
                catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage());
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String z="";
            try {
                bodystring = params[0];
                URL url = new URL("http://192.168.1.8:5221/api/Checksum?param="+bodystring+"&merchantKey="+merchantKey);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                connection.setDoOutput(true);
                String responseData = "";
                InputStream is = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                if ((responseData = responseReader.readLine()) != null) {
                    System.out.append("Response: " + responseData);
                    z = responseData;
                }
                responseReader.close();
            }
            catch (Exception ex)
            {
                z = "ERROR:"+ex.getMessage();
            }
            return z;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            progressBar.show();
            String merchantMessage = data.getStringExtra("nativeSdkForMerchantMessage");
            String response = data.getStringExtra("response");
            try{
                JSONObject res = new JSONObject(response);
                String paymentstatus = res.getString("STATUS");
                paymentStatusTextView.setText(paymentstatus);
                paymentStatusTextView.setVisibility(View.VISIBLE);
                paymentStatusImageView.setVisibility(View.VISIBLE);
                if(paymentstatus.equalsIgnoreCase("TXN_SUCCESS")){
                   paymentStatusImageView.setImageResource(R.drawable.success);
                   paymentStatusTextView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                }
                else{
                    paymentStatusImageView.setImageResource(R.drawable.failed);
                    paymentStatusTextView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                }
                progressBar.cancel();
            }
            catch (Exception ex){
                progressBar.cancel();
                showCustomDialog("Exception:",ex.getMessage());
            }

        }
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        //dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int whichButton) {
        //   //pass
        //}
        //});
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }
    private void LoadLogin(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finishAffinity();
                return true;
            case R.id.Settings:
                Intent settingsPage = new Intent(this,Settings.class);
                //settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return true;
            case R.id.homemenu:
                Intent page = new Intent(this,MainActivity.class);
                page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(page);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}