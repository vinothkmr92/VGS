package com.example.gatepass.gatepass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GateEntry extends AppCompatActivity implements View.OnClickListener
{

    private IntentIntegrator qrScan;
    private Button btnScan;
    private EditText truckNumber;
    private Dialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_entry);
        qrScan = new IntentIntegrator(this);
        btnScan = (Button)findViewById(R.id.snTruckNo);
        btnScan.setOnClickListener(this);
        truckNumber = (EditText)findViewById(R.id.trNo);
        progressBar = new Dialog(GateEntry.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle(R.string.dialog_title);
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
                dialog.cancel();
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
    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    truckNumber.setText(result.getContents().toString());


                    String request = truckNumber.getText().toString();
                    new CallWebService().execute(request);



                } catch (Exception e) {
                    showCustomDialog("WebService Error",e.getMessage());
                    truckNumber.setText("");
                    e.printStackTrace();

                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

                }
                finally {
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class CallWebService extends AsyncTask<String,Void,String> {
        public  static final String NAMESPACE = "http://tempuri.org/";
        public  static final   String METHOD_NAME = "TruckOutEntry";
        public  static final  String SOAP_ACTION = "http://tempuri.org/IGATEPASS_WCF/TruckOutEntry";
        public  static final  String URL = "http://10.54.203.152/hmindia/GATEPASS_WCF.svc";
        // public  static final  String URL = "http://192.168.0.3//GATEPASS_WCF.svc";
        public  int Timeout = 30000;
        String response;
        @Override
        protected void onPostExecute(String res){
            //  super.onPostExecute(res);
            progressBar.cancel();
            if(!res.isEmpty()){
                try{
                    String result = res;
                    if(res.contains("Contact")){
                       showCustomDialog("Invalid Truck","Error Message from SAP: "+res);
                    }
                    else {

                        showCustomDialog("Sucess","Message From SAP: "+res);
                    }
                    truckNumber.setText("");
                }
                catch (Exception ex){
                    showCustomDialog("SAP CALL Error",ex.getMessage());
                    ex.printStackTrace();
                }
            }
            else {
                showCustomDialog("SAP CALL Error","Unable to get Response from SAP. Please Contact IT team.");
                // Toast.makeText(MainActivity.this,"Error in WCF CAll",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        protected String doInBackground(String... params){


            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);



            /*
             * Set the category to be the argument of the web service method
             *
             * */

            Request.addProperty("truckNumber",params[0]);

            /*
             * Set the web service envelope
             *
             * */
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(Request);

            //envelope.addMapping(NAMESPACE, "GatePassResponse",new GatePassResponse().getClass());
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            // AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(URL);
            /*
             * Call the web service and retrieve result ... how luvly <3
             *
             * */
            String res="";
            try
            {

                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapPrimitive results = (SoapPrimitive)envelope.getResponse();
                res = results.toString();

            }
            catch(Exception e)
            {

                e.printStackTrace();
                res = " ~ ~ ~ ~ ~ ~ ~" + e.getMessage().toString();

            }
            return  res;
        }
    }
    @Override
    public void onClick(View v) {
        qrScan.initiateScan();
    }
}
