package com.example.activator;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Activator {
    private Context context;

    public Activator(Context ctx){
        context = ctx;
    }

    class GetActivationStatus extends AsyncTask<String,Void,String> {
        private final ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPostExecute(String res){
            if(!res.isEmpty()){
                try{
                    if(!res.startsWith("ERROR")){

                    }
                    else{

                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
                finally {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }
        }
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle(" ");
            dialog.setMessage("Checking Activation Status.....");
            dialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(String... params){
            String res="";
            try
            {
                String host= context.getApplicationContext().getString(R.string.ActivationAPIHost);

                String imei = params[0];
                java.net.URL url = new URL("http://"+host+"/api/ActivationAPI?imei="+imei);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                connection.setDoOutput(true);
                String responseData = "";
                InputStream is = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                if ((responseData = responseReader.readLine()) != null) {
                    System.out.append("Response: " + responseData);
                    res = responseData;
                }
                responseReader.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                res = "ERROR: "+e.getMessage().toString();
            }
            return  res;
        }
    }
}
