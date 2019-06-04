package com.example.hyndsticker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    ImageButton printBT;
    AutoCompleteTextView shop;
    AutoCompleteTextView partNum;
    AutoCompleteTextView partNam;
    EditText ngq;
    AutoCompleteTextView reasonc;
    AutoCompleteTextView reasonNam;
    AutoCompleteTextView depart;
    EditText entered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shop = (AutoCompleteTextView)findViewById(R.id.shopName);
        partNum = (AutoCompleteTextView)findViewById(R.id.partNo);
        partNam = (AutoCompleteTextView)findViewById(R.id.partName);
        ngq = (EditText)findViewById(R.id.ngQty);
        reasonc = (AutoCompleteTextView)findViewById(R.id.reasonCd);
        reasonNam = (AutoCompleteTextView)findViewById(R.id.reason);
        depart = (AutoCompleteTextView)findViewById(R.id.deptCd);
        entered = (EditText)findViewById(R.id.enterBy);
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
        ArrayList<String> shopList = new ArrayList<>();
        shopList.add("SHOP NAME ONE");
        shopList.add("SHOP NAME TWO");
        shopList.add("SHOP NAME THREE");
        shopList.add("SHOP NAME FOUR");
        shopList.add("SHOP NAME FIVE");
        ArrayAdapter<String> adaptershopList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,shopList);

        ArrayList<String> partNumList = new ArrayList<>();
        partNumList.add("PART NUMBER ONE");
        partNumList.add("PART NUMBER TWO");
        partNumList.add("PART NUMBER THREE");
        partNumList.add("PART NUMBER FOUR");
        partNumList.add("PART NUMBER FIVE");
        ArrayAdapter<String> adapterpartNumList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,partNumList);

        ArrayList<String> partNameList = new ArrayList<>();
        partNameList.add("PART NAME ONE");
        partNameList.add("PART NAME TWO");
        partNameList.add("PART NAME THREE");
        partNameList.add("PART NAME FOUR");
        partNameList.add("PART NAME FIVE");
        ArrayAdapter<String> adapterpartNameList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,partNameList);

        ArrayList<String> reasonCdList = new ArrayList<>();
        reasonCdList.add("RC 1");
        reasonCdList.add("RC 2");
        reasonCdList.add("RC 3");
        reasonCdList.add("RC 4");
        reasonCdList.add("RC 5");
        ArrayAdapter<String> adapterreasonCdList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,reasonCdList);

        ArrayList<String> reasonList = new ArrayList<>();
        reasonList.add("REASON ONE");
        reasonList.add("REASON TWO");
        reasonList.add("REASON THREE");
        reasonList.add("REASON FOUR");
        reasonList.add("REASON FIVE");
        ArrayAdapter<String> adapterreasonList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,reasonList);

        ArrayList<String> departList = new ArrayList<>();
        departList.add("DEPARTMENT ONE");
        departList.add("DEPARTMENT TWO");
        departList.add("DEPARTMENT THREE");
        departList.add("DEPARTMENT FOUR");
        departList.add("DEPARTMENT FIVE");
        ArrayAdapter<String> adapterdepartList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,departList);

        shop.setAdapter(adaptershopList);
        partNum.setAdapter(adapterpartNumList);
        partNam.setAdapter(adapterpartNameList);
        reasonc.setAdapter(adapterreasonCdList);
        reasonNam.setAdapter(adapterreasonList);
        depart.setAdapter(adapterdepartList);
    }

    @Override
    public void onClick(View v) {
       try{
           SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
           Date date = new Date();
           ngxPrinter.setDefault();
           ngxPrinter.setStyleBold();
           ngxPrinter.printText("HYUNDAI MOTOR INDIA LIMITED",Alignments.CENTER,28);
           ngxPrinter.setStyleDoubleWidth();
           ngxPrinter.setStyleBold();
           ngxPrinter.printText("OS&D TAG", Alignments.CENTER, 30);
           ngxPrinter.printText("-----------------------------------", Alignments.LEFT, 20);
           ngxPrinter.printText("SHOP       :"+shop.getText(),Alignments.LEFT,20);
           ngxPrinter.printText("PART NO    :" + partNum.getText(), Alignments.LEFT, 20);
           ngxPrinter.printText("PART NAME  :"+ partNam.getText(),Alignments.LEFT,20);
           ngxPrinter.printText("NG QTY     :"+ngq.getText(),Alignments.LEFT,20);
           ngxPrinter.printText("REASON CD  :" + reasonc.getText(), Alignments.LEFT, 20);
           ngxPrinter.printText("REASON     :" + reasonNam.getText(), Alignments.LEFT, 20);
           ngxPrinter.printText("DEPT CD    :" + depart.getText(), Alignments.LEFT, 20);
           ngxPrinter.printText("ENTER BY   :" + entered.getText(), Alignments.LEFT, 20);
           ngxPrinter.printText("TAG NO     :"+"0001" , Alignments.LEFT, 20);
           ngxPrinter.printText("-----------------------------------", Alignments.LEFT, 20);
           ngxPrinter.printText("                   ");
           ngxPrinter.printText("                   ");
           ngxPrinter.setDefault();
       }
       catch (Exception ex){
           Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG);
           ngxPrinter.printText("Excpetion: "+ex.getMessage());
       }
    }
}
