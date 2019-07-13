package com.example.hyndsticker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ngx.mp100sdk.Enums.Alignments;
import com.ngx.mp100sdk.Intefaces.INGXCallback;
import com.ngx.mp100sdk.NGXPrinter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static NGXPrinter ngxPrinter = NGXPrinter.getNgxPrinterInstance();
    private INGXCallback ingxCallback;
    DatabaseHelper dbHelper;
    ImageButton printBT;
    AutoCompleteTextView shop;
    AutoCompleteTextView partNum;
    AutoCompleteTextView partNam;
    EditText ngq;
    AutoCompleteTextView reasonc;
    AutoCompleteTextView reasonNam;
    AutoCompleteTextView depart;
    EditText entered;
    EditText approvedby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        shop = (AutoCompleteTextView)findViewById(R.id.shopName);
        partNum = (AutoCompleteTextView)findViewById(R.id.partNo);
        partNam = (AutoCompleteTextView)findViewById(R.id.partName);
        ngq = (EditText)findViewById(R.id.ngQty);
        reasonc = (AutoCompleteTextView)findViewById(R.id.reasonCd);
        reasonNam = (AutoCompleteTextView)findViewById(R.id.reason);
        depart = (AutoCompleteTextView)findViewById(R.id.deptCd);
        entered = (EditText)findViewById(R.id.enterBy);
        approvedby = (EditText)findViewById(R.id.approvedBy);
        LoadDropDowns();
        try{

            printBT = (ImageButton) findViewById(R.id.printBtn);
            printBT.setOnClickListener(this);
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
           Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }
    }

    private void LoadDropDowns(){
        ArrayAdapter<String> adaptershopList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dbHelper.GetShops());

        ArrayAdapter<String> adapterpartNumList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dbHelper.GetPartNo());

        ArrayAdapter<String> adapterpartNameList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dbHelper.GetPartName());

        ArrayAdapter<String> adapterreasonCdList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dbHelper.GetReasonCd());

        ArrayAdapter<String> adapterreasonList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dbHelper.GetReason());

        ArrayAdapter<String> adapterdepartList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dbHelper.GetDepartnmentCd());

        shop.setAdapter(adaptershopList);
        partNum.setAdapter(adapterpartNumList);
        partNam.setAdapter(adapterpartNameList);
        reasonc.setAdapter(adapterreasonCdList);
        reasonNam.setAdapter(adapterreasonList);
        depart.setAdapter(adapterdepartList);
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
                finish();
                System.exit(0);
                return true;
            case R.id.uploadExcel:
                Intent dcpage = new Intent(this,UploadExcel.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            case R.id.homemenu:
                Intent page = new Intent(this,MainActivity.class);
                page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(page);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
       try{
           SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
           Date date = new Date();
           Entry entry = new Entry();
           entry.setENTRY_ID(dbHelper.GetNextEntryID());
           entry.setENTRY_DATE(format.format(date));
           entry.setSHOP(shop.getText().toString());
           entry.setPART_NO(partNum.getText().toString());
           entry.setPART_NAME(partNam.getText().toString());
           entry.setREASON_CD(reasonc.getText().toString());
           entry.setREASON(reasonNam.getText().toString());
           Integer qty = Integer.parseInt(ngq.getText().toString());
           entry.setQTY(qty);
           entry.setDEPART_CD(depart.getText().toString());
           entry.setENTERED_BY(entered.getText().toString());
           entry.setAPPROVED_BY(approvedby.getText().toString());
           dbHelper.InsesrtEntry(entry);
           Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/droidsansmono.ttf");
           TextPaint tp = new TextPaint();
           tp.setColor(Color.BLACK);
           tp.setTextSize(23f);
           tp.setTypeface(Typeface.create(tf,Typeface.BOLD));
           ngxPrinter.setStyleBold();
           ngxPrinter.setStyleDoubleWidth();
           ngxPrinter.printUnicodeText("HYUNDAI MOTOR INDIA LIMITED", Layout.Alignment.ALIGN_CENTER,tp);
           ngxPrinter.setStyleDoubleWidth();
           ngxPrinter.setStyleBold();
           tp.setTextSize(30f);
           ngxPrinter.printUnicodeText("OS&D TAG", Layout.Alignment.ALIGN_CENTER, tp);
           tp.setTextSize(20f);
           ngxPrinter.printUnicodeText("DATE: "+format.format(date), Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printText("-----------------------------------", Alignments.LEFT, 20);
           tp.setTypeface(tf);
           ngxPrinter.printUnicodeText("SHOP       :"+entry.getSHOP(), Layout.Alignment.ALIGN_LEFT,tp);
           ngxPrinter.printUnicodeText("PART NO    :" + entry.getPART_NO(), Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printUnicodeText("PART NAME  :"+ entry.getPART_NAME(), Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printUnicodeText("NG QTY     :"+ entry.getQTY(), Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printUnicodeText("REASON CD  :" + entry.getREASON_CD(),  Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printUnicodeText("REASON     :" + entry.getREASON(),  Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printUnicodeText("DEPT CD    :" + entry.getDEPART_CD(),  Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printUnicodeText("ENTER BY   :" + entry.getENTERED_BY(),  Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printUnicodeText("APPROVED BY:" + entry.getAPPROVED_BY(),  Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printUnicodeText("TAG NO     :"+ entry.getENTRY_ID() ,  Layout.Alignment.ALIGN_LEFT, tp);
           ngxPrinter.printText("-----------------------------------", Alignments.LEFT, 20);
           ngxPrinter.lineFeed(4);
           ngxPrinter.setDefault();
       }
       catch (Exception ex){
           Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG);
           ngxPrinter.printText("Excpetion: "+ex.getMessage());
       }
    }
}
