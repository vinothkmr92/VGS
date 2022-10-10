package com.example.easypos;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.port.android.WiFiPortConnection;
import com.sewoo.request.android.RequestHandler;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton printButton;
    public static boolean ISCONNECT;
    public static String DISCONNECT="com.posconsend.net.disconnetct";
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String COMPANYNAME = "COMPANYNAME";
    public static final String ADDRESS= "ADDRESS";
    public static final String PRINTERIP = "PRINTERIP";
    public static final String ISACTIVATED = "ISACTIVATED";
    private WiFiPort wifiPort;

    CoordinatorLayout container;
    GridLayout gridLayout;
    DynamicView dynamicView;
    public  int snonumber = 0;
    EditText priceEditText;
    EditText qtyEditText;
    TextView totalAmtTextView;
    ArrayList<Items> itemsList = new ArrayList<>();
    Dialog progressBar;
    String companyname;
    String address;
    String printeripaddress;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;
    private Button btn9;
    private Button btn0;
    private Button btnDot;
    private Button btnClear;
    private AlertDialog alert;
    private Thread hThread;
    private boolean showConfirmBox;
    private static MainActivity instance;
    private ImageButton btnShareBill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = new Dialog(MainActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading...");
        printButton = (ImageButton)findViewById(R.id.printbtn);
        printButton.setOnClickListener(this);
        printButton.setBackgroundResource(R.drawable.print);
        printButton.setEnabled(true);
        wifiPort = WiFiPort.getInstance();
        gridLayout = (GridLayout) findViewById(R.id.gridData);
        priceEditText = (EditText) findViewById(R.id.price);
        totalAmtTextView = (TextView) findViewById(R.id.totalAmt);
        qtyEditText = (EditText)findViewById(R.id.qty);
        btnShareBill = (ImageButton)findViewById(R.id.btnshare);
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
        companyname = sharedpreferences.getString(COMPANYNAME,"");
        address = sharedpreferences.getString(ADDRESS,"");
        printeripaddress = sharedpreferences.getString(PRINTERIP,"");
        String isactivated = sharedpreferences.getString(ISACTIVATED,"");
        if(!isactivated.equals("true")){
            GoActivation();
        }
        btn1 = (Button)findViewById(R.id._1);
        btn2= (Button)findViewById(R.id._2);
        btn3= (Button)findViewById(R.id._3);
        btn4= (Button)findViewById(R.id._4);
        btn5= (Button)findViewById(R.id._5);
        btn6= (Button)findViewById(R.id._6);
        btn7= (Button)findViewById(R.id._7);
        btn8= (Button)findViewById(R.id._8);
        btn9= (Button)findViewById(R.id._9);
        btn0= (Button)findViewById(R.id._0);
        btnDot= (Button)findViewById(R.id._dot);
        btnClear = (Button)findViewById(R.id._clr);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn0.setOnClickListener(this);
        btnDot.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnShareBill.setOnClickListener(this);
        qtyEditText.requestFocus();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Click Yes to Print Second Copy ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                if(!wifiPort.isConnected()){
                    try{
                        new connectPrinter().execute(printeripaddress);
                    }
                    catch (Exception ex){
                        showCustomDialog("Exception",ex.getMessage().toString());
                    }
                }
                showConfirmBox = false;
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                ClearDetails();
                dialog.dismiss();
            }
        });
        alert = builder.create();
        showConfirmBox = true;
        instance = this;
        if(!checkPermission()){
            requestStoragePermission();
        }
    }
    private void viewPdf(String filepath) {

        File pdfFile = new File(filepath);
        //Uri path = Uri.fromFile(pdfFile);
        Uri path = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",pdfFile);
        // Setting the intent for pdf reader
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        // sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");

        Uri uri = Uri.fromFile(pdfFile);
        sendIntent.putExtra(Intent.EXTRA_STREAM, path);
        sendIntent.setType("application/pdf");
        startActivity(sendIntent);
    }
    private PdfDocument.Page GetFooter(PdfDocument.Page page,String txt,int y){
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mPaint.setTextSize(getResources().getDimension(R.dimen.btn_textsize));
        Rect areaRect = new Rect(0, 0, 300, 20);
        mPaint.setColor(Color.WHITE);
        page.getCanvas().drawRect(areaRect, mPaint);
        String pageTitle = txt;
        RectF bounds = new RectF(areaRect);
        bounds.right = mPaint.measureText(pageTitle, 0, pageTitle.length());
        bounds.bottom = mPaint.descent() - mPaint.ascent();
        bounds.left += (areaRect.width() - bounds.right) / 2.0f;
        bounds.top += (areaRect.height() - bounds.bottom) / 2.0f;
        mPaint.setColor(Color.BLACK);
        page.getCanvas().drawText(pageTitle, bounds.left, y, mPaint);
        return page;
    }
    private PdfDocument.Page GetHeader(PdfDocument.Page page){
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mPaint.setTextSize(getResources().getDimension(R.dimen.btn_textsize));
        Rect areaRect = new Rect(0, 0, 300, 20);
        mPaint.setColor(Color.WHITE);
        page.getCanvas().drawRect(areaRect, mPaint);
        String pageTitle = companyname;
        RectF bounds = new RectF(areaRect);
        bounds.right = mPaint.measureText(pageTitle, 0, pageTitle.length());
        bounds.bottom = mPaint.descent() - mPaint.ascent();
        bounds.left += (areaRect.width() - bounds.right) / 2.0f;
        bounds.top += (areaRect.height() - bounds.bottom) / 2.0f;
        mPaint.setColor(Color.BLACK);
        page.getCanvas().drawText(pageTitle, bounds.left, bounds.top - mPaint.ascent(), mPaint);
        return page;
    }
    private PdfDocument.Page GetBillPage(PdfDocument.Page page){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setTextSize(3);
        int x=10;
        int y=35;
        page = GetFooter(page,companyname,y);
        y+=paint.descent()-paint.ascent();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
        Date date = new Date();
        String dateStr = format.format(date);
        paint.setTextAlign(Paint.Align.LEFT);
        page.getCanvas().drawText("Date: "+dateStr,x,y,paint);
        y+=10;
        y+=paint.descent()-paint.ascent();
        //paint.setTextAlign(Paint.Align.LEFT);
        String snoTxt = StringUtils.rightPad("SNO",5);
        String qtyTxt = StringUtils.leftPad("QTY",11);
        String priceTxt = StringUtils.leftPad("PRICE",11);
        String amtTxt = StringUtils.leftPad("AMOUNT",21);
        String headertxt = snoTxt+qtyTxt+priceTxt+amtTxt;
        page.getCanvas().drawText(headertxt,x,y,paint);
        double totalQty = 0d;
        y+=paint.descent()-paint.ascent();
        int k = y;
        for(int i=0;i<itemsList.size();i++){
            Items item = itemsList.get(i);
            String sno = org.apache.commons.lang3.StringUtils.rightPad(item.getSno(),5);
            String price = org.apache.commons.lang3.StringUtils.leftPad(item.getPrice(),11);
            String qty = org.apache.commons.lang3.StringUtils.leftPad(item.getQty(),11);
            String amt = org.apache.commons.lang3.StringUtils.leftPad(item.getAmt(),21);
            String line = sno+qty+price+amt;
            page.getCanvas().drawText(line,x,k,paint);
            double q = Double.parseDouble(item.getQty());
            totalQty+=q;
            k+=paint.descent()-paint.ascent();
        }
        k+=10;
        String totalamt = totalAmtTextView.getText().toString();
        String txttotal = "TOTAL AMT: "+totalamt+"/-";
        page = GetFooter(page,txttotal,k);
        String totalqty =String.format("%.0f",totalQty);
        String txttotalqty = "TOTAL QTY: "+totalqty;
        k+=paint.descent()-paint.ascent();
        page = GetFooter(page,txttotalqty,k);
        return page;
    }
    private void GeneratePDF(){
        if(itemsList.size()>0){
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300,600,1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            page = GetBillPage(page);
            pdfDocument.finishPage(page);
            String myfilepath = Environment.getExternalStorageDirectory().getPath()+"/myPDFFile.pdf";
            File myfile = new File(myfilepath);
            try{
                pdfDocument.writeTo(new FileOutputStream(myfile));
                viewPdf(myfilepath);
            }
            catch (Exception ex){
                ex.printStackTrace();
                showCustomDialog("Error",ex.getMessage().toString());
            }
        }
        else{
            showCustomDialog("Msg","No bill details to share.");
        }
    }
    public void requestStoragePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            storeageActivitytResultLanucher.launch(intent);
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,1);
        }
    }
    public boolean checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            return  Environment.isExternalStorageManager();
        }
        else{
            int write = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }
    private ActivityResultLauncher<Intent> storeageActivitytResultLanucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){

                    }
                    else{

                    }
                }
            });
    public  void  GoActivation(){
        Intent page = new Intent(this,Activation.class);
        page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(page);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
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
    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    public int PrintData() throws InterruptedException
    {
        try
        {
            posPtr.setAsync(true);
            rtn = posPtr.printerSts();

            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            String dateStr = format.format(date);
            posPtr.printNormal(ESC+"|cA"+ESC+"|2C"+companyname+"\r\n");
            //posPtr.printNormal(ESC+"|rATEL (123)-456-7890\n\n\n");
            posPtr.printNormal(ESC+"|cA"+address+"\n\n");
            posPtr.printNormal(ESC+"|lADate: "+dateStr+"\n\n");
            posPtr.printNormal("SNO          QTY      PRICE               AMOUNT\n");
            double totalQty = 0d;
            for(int i=0;i<itemsList.size();i++){
                Items item = itemsList.get(i);
                String sno = org.apache.commons.lang3.StringUtils.rightPad(item.getSno(),5," ");
                String price = org.apache.commons.lang3.StringUtils.leftPad(item.getPrice(),10," ");
                String qty = org.apache.commons.lang3.StringUtils.leftPad(item.getQty(),12," ");
                String amt = org.apache.commons.lang3.StringUtils.leftPad(item.getAmt(),21," ");
                String line = sno+qty+price+amt+"\n";
                posPtr.printNormal(line);
                double q = Double.parseDouble(item.getQty());
                totalQty+=q;
            }
            String totalamt = totalAmtTextView.getText().toString();
            String txttotal = "TOTAL: "+totalamt+"/-";
            posPtr.printNormal(ESC+"|rA"+ESC+"|bC"+ESC+"|2C"+txttotal+"\n");
            posPtr.lineFeed(1);
            String totalqty =String.format("%.0f",totalQty);
            String txttotalqty = "TOTAL QTY: "+totalqty;
            posPtr.printNormal(ESC+"|bC"+ESC+"|1C"+txttotalqty+"\n\n");
            posPtr.lineFeed(4);
            posPtr.cutPaper();
            wifiPort.disconnect();
            if(showConfirmBox){
                alert.show();
            }
            else{
                ClearDetails();
                alert.dismiss();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }
    private  void ClearDetails(){
        gridLayout.removeViews(5,itemsList.size()*5);
        itemsList.clear();
        totalAmtTextView.setText("000");
        snonumber=0;
    }
    @Override
    protected void onDestroy() {
        try
        {
            if(wifiPort != null)
                wifiPort.disconnect();
        }
        catch (IOException e)
        {
            Log.e("ERR", e.getMessage(), e);
        }
        catch (InterruptedException e)
        {
            Log.e("ERR", e.getMessage(), e);
        }
        if((hThread != null) && (hThread.isAlive()))
        {
            hThread.interrupt();
            hThread = null;
        }
        super.onDestroy();
    }

    private void PrintWifi(){
        try
        {
            if(!wifiPort.isConnected()){
                try{
                    new connectPrinter().execute(printeripaddress);
                }
                catch (Exception ex){
                    showCustomDialog("Exception",ex.getMessage().toString());
                }
            }

        }
        catch (Exception ex){
            showCustomDialog("Exception",ex.getMessage().toString());
        }
        if(progressBar.isShowing()){
            progressBar.cancel();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnshare:
                GeneratePDF();
                break;
            case R.id.printbtn:
                progressBar.show();
                PrintWifi();
                if(progressBar.isShowing()){
                    progressBar.cancel();
                }
                showConfirmBox =true;
                break;
            case R.id._clr:
                if(priceEditText.isFocused()){
                    String sr = priceEditText.getText().toString();
                    if(sr.length()>0){
                        sr = sr.substring(0,sr.length()-1);
                        priceEditText.setText(sr);
                    }
                }
                else if(qtyEditText.isFocused()){
                    String sr = qtyEditText.getText().toString();
                    if(sr.length()>0){
                        sr = sr.substring(0,sr.length()-1);
                        qtyEditText.setText(sr);
                    }
                }
                break;
            case  R.id._dot:
                if(qtyEditText.isFocused()){
                    priceEditText.requestFocus();
                    break;
                }
                if(priceEditText.isFocused()){
                    if(qtyEditText.getText().toString().isEmpty()  ){
                        showCustomDialog("Warning","Enter Valid Quantity");
                    }
                    else {
                        progressBar.show();
                        snonumber++;
                        Items item =new Items();
                        item.setPrice(priceEditText.getText().toString());
                        item.setQty(qtyEditText.getText().toString());
                        item.setSno(String.valueOf(snonumber));
                        double priced = Double.parseDouble(item.getPrice());
                        double qtyd = Double.parseDouble(item.getQty());
                        double amt = priced*qtyd;
                        String amtstr = String.format("%.0f", amt);
                        item.setAmt(amtstr);
                        LoadGrid(item);
                        progressBar.hide();
                        qtyEditText.requestFocus();
                    }
                }
                break;
            default:
                String addstr = getResources().getResourceEntryName(view.getId());
                addstr =  addstr.replace("_","");
                if(priceEditText.isFocused()){
                    String sr = priceEditText.getText().toString();
                    sr+=addstr;
                    priceEditText.setText(sr);
                }
                else if(qtyEditText.isFocused()){
                    String sr = qtyEditText.getText().toString();
                    sr+=addstr;
                    qtyEditText.setText(sr);
                }
                break;
        }
    }
    public  void RemoveLine(int index){
        gridLayout.removeViews(5,itemsList.size()*5);
        itemsList.remove(index);
        for(int i=0;i<itemsList.size();i++){
            itemsList.get(i).setSno(String.valueOf(i+1));
        }
        LoadGridAsync();
    }
    public static MainActivity getInstance() {
        return instance;
    }
    private void LoadGridAsync(){
        double totalAmt = 0;
        for(int i=0;i<itemsList.size();i++){
            Items item = itemsList.get(i);
            dynamicView = new DynamicView(this.getApplicationContext());
            gridLayout.addView(dynamicView.clrButton(getApplicationContext(),i));
            gridLayout.addView(dynamicView.snoTextView(getApplicationContext(),item.getSno()));
            gridLayout.addView(dynamicView.qtyTextView(getApplicationContext(),item.getQty()));
            gridLayout.addView(dynamicView.priceTextView(getApplicationContext(),item.getPrice()));
            gridLayout.addView(dynamicView.amtTextView(getApplicationContext(),item.getAmt()));
            double amt = Double.parseDouble(itemsList.get(i).getAmt());
            totalAmt+=amt;
        }
        String totalstr = String.format("%.0f", totalAmt);
        totalAmtTextView.setText(totalstr);
    }
    private void LoadGrid(Items it){
        if(gridLayout.getRowCount()>0){
            gridLayout.removeViews(5,itemsList.size()*5);
        }
        itemsList.add(it);
        LoadGridAsync();
        priceEditText.setText("");
        qtyEditText.setText("");
        instance = this;
    }

    private void wifiConn(String ipAddr) throws IOException
    {
        new connTask().execute(ipAddr);
    }
    // WiFi Disconnection method.
    private void wifiDisConn() throws IOException, InterruptedException
    {
        wifiPort.disconnect();
        hThread.interrupt();
        Toast toast = Toast.makeText(MainActivity.this, "Printer Disconnected", Toast.LENGTH_SHORT);
        toast.show();
    }
    class connectPrinter extends AsyncTask<String, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private WiFiPortConnection connection;
        @Override
        protected void onPreExecute()
        {
            dialog.setTitle("Print Status");
            dialog.setMessage("Printing.....");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            Integer retVal = null;
            try
            {
                // ip
                wifiPort.connect(params[0]);
                //connection = wifiPort.open(params[0]);

                //lastConnAddr = params[0];
                retVal = new Integer(0);
            }
            catch (IOException e)
            {
                Log.e("Wificonnection:",e.getMessage(),e);
                retVal = new Integer(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(result.intValue() == 0)
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                if(dialog.isShowing())
                    dialog.dismiss();
                posPtr = new ESCPOSPrinter();
                posPtr.setAsync(true);
                try{
                    PrintData();
                }
                catch (Exception ex){
                    showCustomDialog("Exception",ex.getMessage().toString());
                }
            }
            else{
                if(dialog.isShowing())
                    dialog.dismiss();
            }

            super.onPostExecute(result);
        }
    }
    // WiFi Connection Task.
    class connTask extends AsyncTask<String, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private WiFiPortConnection connection;
        @Override
        protected void onPreExecute()
        {
            dialog.setTitle("Wifi Connection");
            dialog.setMessage("Connecting...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            Integer retVal = null;
            try
            {
                // ip
                wifiPort.connect(params[0]);
                //connection = wifiPort.open(params[0]);

                //lastConnAddr = params[0];
                retVal = new Integer(0);
            }
            catch (IOException e)
            {
                Log.e("Wificonnection:",e.getMessage(),e);
                retVal = new Integer(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(result.intValue() == 0)
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                if(dialog.isShowing())
                    dialog.dismiss();
                showCustomDialog("Wifi","Printer Connected");
                printButton.setEnabled(true);
                printButton.setBackgroundResource(R.drawable.print);
                //posPtr = new ESCPOSPrinter(connection);
                posPtr.setAsync(true);
            }
            else
            {
                showCustomDialog("Wifi","Printer Connection Failed");
                if(dialog.isShowing())
                    dialog.dismiss();
            }

            super.onPostExecute(result);
        }
    }

    public ESCPOSPrinter posPtr;
    // 0x1B
    private final char ESC = ESCPOS.ESC;

    public  MainActivity() {posPtr=new ESCPOSPrinter();}
    //	private final String TAG = "PrinterStsChecker";
    private int rtn;

    public int sample1() throws InterruptedException
    {
        try
        {
            rtn = posPtr.printerSts();
            // Do not check the paper near empty.
            // if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
            // check the paper near empty.
            if( rtn != 0 )  return rtn;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return rtn;
        }

        try
        {
            posPtr.printNormal(ESC+"|cA"+ESC+"|2CReceipt\r\n\r\n\r\n");
            posPtr.printNormal(ESC+"|rATEL (123)-456-7890\n\n\n");
            posPtr.printNormal(ESC+"|cAThank you for coming to our shop!\n");
            posPtr.printNormal(ESC+"|cADate\n\n");
            posPtr.printNormal("Chicken                             $10.00\n");
            posPtr.printNormal("Hamburger                           $20.00\n");
            posPtr.printNormal("Pizza                               $30.00\n");
            posPtr.printNormal("Lemons                              $40.00\n");
            posPtr.printNormal("Drink                               $50.00\n");
            posPtr.printNormal("Excluded tax                       $150.00\n");
            posPtr.printNormal(ESC+"|uCTax(5%)                              $7.50\n");
            posPtr.printNormal(ESC+"|bC"+ESC+"|2CTotal         $157.50\n\n");
            posPtr.printNormal("Payment                            $200.00\n");
            posPtr.printNormal("Change                              $42.50\n\n");
            posPtr.printBarCode("{Babc456789012", LKPrint.LK_BCS_Code128, 40, 512, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW); // Print Barcode
            posPtr.lineFeed(4);
            posPtr.cutPaper();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return 0;
    }
}