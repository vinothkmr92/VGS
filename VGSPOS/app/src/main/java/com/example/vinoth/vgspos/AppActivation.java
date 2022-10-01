package com.example.vinoth.vgspos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppActivation {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String EXPIRE_DT = "EXPIRE_DT";
    private String imeiNumber;
    private Context context;
    private MySharedPreferences sharedpreferences;
    public AppActivation(Context ctx,String imei){
        imeiNumber = imei;
        context = ctx;
        sharedpreferences = MySharedPreferences.getInstance(ctx,MyPREFERENCES);
    }
    public void CheckActivationStatus(){
        new GetActivationStatus().execute(imeiNumber);

    }
    class GetActivationStatus extends AsyncTask<String,Void,String> {
        private final ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPostExecute(String res){
            if(!res.isEmpty()){
                try{
                    Common.isActivated = !res.startsWith("ERROR");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date expireDate = simpleDateFormat.parse(res);
                    Common.expireDate = expireDate;
                    sharedpreferences.putString(EXPIRE_DT,res);
                    sharedpreferences.commit();
                    HomeActivity.getInstance().ValidateActivationResponse(res);
                }
                catch (ParseException e){
                    HomeActivity.getInstance().ValidateActivationResponse(res);
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
                java.net.URL url = new URL("http://"+host+":9092/api/ActivationAPI?imei="+imei);
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
