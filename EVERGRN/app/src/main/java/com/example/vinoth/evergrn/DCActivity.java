package com.example.vinoth.evergrn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.ngx.mp100sdk.Enums.Alignments;
import com.ngx.mp100sdk.Intefaces.INGXCallback;
import com.ngx.mp100sdk.NGXPrinter;

import static com.example.vinoth.evergrn.MainActivity.weightFromBluetooth;

public class DCActivity extends Activity implements View.OnClickListener {


    EditText totalWeight;
    Spinner customerSpinner;
    Spinner traySpinner;
    Spinner packingSprinner;
    EditText wt;
    TextView netwtString;
    EditText netWt;
    ConnectionClass connectionClass;
    Dialog progressBar;
    Button addBtn;
    Button saveBtn;
    ArrayList<Sale_Tray> saleTrays;
    public static NGXPrinter ngxPrinter = NGXPrinter.getNgxPrinterInstance();
    private INGXCallback ingxCallback;
    ImageButton btnGetWeight;
    Float weightToReduce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dc);
        totalWeight = (EditText)findViewById(R.id.totWeight);
        customerSpinner = (Spinner)findViewById(R.id.customerSpinner);
        traySpinner = (Spinner)findViewById(R.id.traySpinner);
        packingSprinner = (Spinner)findViewById(R.id.packingSpinner);
        wt = (EditText)findViewById(R.id.wtDC);
        netwtString = (TextView)findViewById(R.id.netwtstringDC);
        netWt = (EditText)findViewById(R.id.netweightDC);
        connectionClass = new ConnectionClass();
        progressBar = new Dialog(DCActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        saleTrays = new ArrayList<Sale_Tray>();
        btnGetWeight = (ImageButton) findViewById(R.id.btnGetWeightDC);
        btnGetWeight.setOnClickListener(this);
        traySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedTray = traySpinner.getSelectedItem().toString();
                String selectedPackage = packingSprinner.getSelectedItem().toString();
                ArrayList<String> pkList = new ArrayList<>();
                pkList.add("<SELECT PACKING>");
                for(int i=0;i<CommonUtil.packingsList.size();i++){

                        String pacName = CommonUtil.packingsList.get(i).getPacking_NAME();
                    if (!pkList.contains(pacName)) {
                        pkList.add(pacName);
                    }

                }
                switch (SelectedTray){
                    case "10KG_BIG_TRAY":
                        pkList.remove("WITHOUT_PUNNET");
                        pkList.remove("LOOSE");
                        pkList.remove("ST");
                        break;
                    case "8KG_BIG_TRAY":
                        pkList.remove("WITHOUT_PUNNET");
                        pkList.remove("LOOSE");
                        pkList.remove("ST");
                        break;
                    case "8KG_SMALL_TRAY":
                        pkList.remove("WITH_PUNNET");
                        pkList.remove("LOOSE");
                        pkList.remove("ST");
                        break;
                }
                LoadPackingSpiner(pkList);
                String st = CommonUtil.WeightFromBlueTooth;
                Float f1 = Float.parseFloat(st);
                DecimalFormat d1 = new DecimalFormat("0.000");
                wt.setText(d1.format(f1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        packingSprinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedTray = traySpinner.getSelectedItem().toString();
                String selectedPackage = packingSprinner.getSelectedItem().toString();
                weightToReduce = 0.000f;
                switch (SelectedTray){
                    case "15KG_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 1.650f;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=1.250f;
                                break;
                            case "LOOSE":
                                weightToReduce=0.980f;
                                break;
                            case "ST":
                                weightToReduce=1.650f;
                                break;
                        }
                    case "10KG_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 1.050f;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=0.800f;
                                break;
                            case "LOOSE":
                                weightToReduce=0.620f;
                                break;
                            case "ST":
                                weightToReduce=1.050f;
                                break;
                        }
                    case "10KG_BIG_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 2.585f;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=2.478f;
                                break;
                        }
                    case "8KG_BIG_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 2.478f;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=2.478f;
                                break;
                        }
                    case "8KG_SMALL_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 2.478f;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=2.115f;
                                break;
                        }
                }
                String st = CommonUtil.WeightFromBlueTooth;
                Float f1 = Float.parseFloat(st);
                DecimalFormat d1 = new DecimalFormat("0.000");
                wt.setText(d1.format(f1));
                Float wtt = Float.parseFloat(wt.getText().toString());
                Float result = wtt - weightToReduce;
                netwtString.setText(wtt.toString() + "-" + weightToReduce + " = ");
                DecimalFormat df = new DecimalFormat("0.000");
                df.setMaximumFractionDigits(3);
                netWt.setText(df.format(result));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addBtn = (Button)findViewById(R.id.btnAdd);
        saveBtn = (Button)findViewById(R.id.btnSave);
        addBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        new LoadDropDownData().execute("");
        try{
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
            String st = CommonUtil.WeightFromBlueTooth;
            Float f1 = Float.parseFloat(st);
            DecimalFormat d1 = new DecimalFormat("0.000");
            wt.setText(d1.format(f1));
        }
        catch (Exception ex){
            showCustomDialog("Warning", ex.getMessage());
        }
    }
    private  Tray GetTray(int TrayID){
        Tray tray = null;
        for(int i=0;i<CommonUtil.trayList.size();i++){
            if(TrayID == CommonUtil.trayList.get(i).getTray_ID()){
                tray = CommonUtil.trayList.get(i);
                break;
            }
        }
        return  tray;
    }


    private Packings GetPacking(int PackingID){
        Packings packings = null;
        for(int i=0;i<CommonUtil.packingsList.size();i++){
            if(PackingID == CommonUtil.packingsList.get(i).getPacking_ID()){
                packings = CommonUtil.packingsList.get(i);
                break;
            }
        }
        return packings;
    }
    private Customer GetCustomer(int CustomerID){
        Customer customer = null;
        for(int i=0;i<CommonUtil.customerList.size();i++){
            if(CustomerID == CommonUtil.customerList.get(i).getCustomer_ID()){
                customer = CommonUtil.customerList.get(i);
                break;
            }
        }
        return  customer;
    }
    private int PrintDC(int DcNo){
        int returnCode = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
            Date date = new Date();
            Customer customer = GetCustomer(saleTrays.get(0).getCustomer_ID());
            ngxPrinter.setDefault();
            ngxPrinter.setStyleDoubleWidth();
            ngxPrinter.printText("DELIVERY CHALLAN", Alignments.CENTER, 24);
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("EVERGREEN HARVEST ARGO", Alignments.CENTER, 28);
            ngxPrinter.setStyleBold();
            ngxPrinter.setStyleBold();
            ngxPrinter.printText("DC NO : " + DcNo, Alignments.LEFT, 24);
            ngxPrinter.printText("DATE  : " + format.format(date), Alignments.LEFT, 24);
            ngxPrinter.printText("TO", Alignments.LEFT, 24);
            ngxPrinter.printText(customer.getCustomer_Name(), Alignments.CENTER, 24);
            ngxPrinter.printText(customer.getAddress(), Alignments.CENTER, 24);
            ngxPrinter.printText("--------------------------------", Alignments.LEFT, 24);
            ngxPrinter.printText("SNO         NAME           QTY", Alignments.LEFT, 24);
            ngxPrinter.printText("--------------------------------", Alignments.LEFT, 24);
            for (int i = 0; i < saleTrays.size(); i++) {
                Sale_Tray sr = saleTrays.get(i);
                Integer sno = i + 1;
                Tray tray = GetTray(sr.getTray_ID());
                Packings packings = GetPacking(sr.getPacking_ID());
                String shortName = tray.getShort_Name() + "-" + packings.getShort_Name();
                shortName = padRight(shortName, 15);
                if (!shortName.isEmpty()) {
                    ngxPrinter.printText(sno + " " + shortName + "      " + sr.getWeigth(), Alignments.LEFT, 24);
                }
            }
            ngxPrinter.printText("--------------------------------", Alignments.LEFT, 24);
            ngxPrinter.printText("        TOTAL WEIGTH:" + totalWeight.getText(), Alignments.LEFT, 24);
            ngxPrinter.printText("                              ");
            ngxPrinter.printText("*** THANK YOU ***", Alignments.CENTER, 24);
            ngxPrinter.printText("                              ");
            ngxPrinter.printText("                              ");
            ngxPrinter.setDefault();
        }
        catch (Exception ex){
            returnCode = 8;
            showCustomDialog("Error",ex.getMessage());
        }
        return  returnCode;
    }

    private String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
    private  boolean ValidateInput(){
        Boolean isValid = false;
        String customer = customerSpinner.getSelectedItem().toString();
        String tray = traySpinner.getSelectedItem().toString();
        String packing = packingSprinner.getSelectedItem().toString();
        if(customer.equals("<SELECT CUSTOMER>") || tray.equals("<SELECT TRAY>") || packing.equals("<SELECT PACKING>")){
            isValid = false;
        }
        else {
            isValid = true;
        }
        return  isValid;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGetWeightDC:
                String st = CommonUtil.WeightFromBlueTooth;
                Float f1 = Float.parseFloat(st);
                DecimalFormat d1 = new DecimalFormat("0.000");
                wt.setText(d1.format(f1));
                Float wtt = Float.parseFloat(wt.getText().toString());
                Float result = wtt - weightToReduce;
                netwtString.setText(wtt.toString() + "-" + weightToReduce + " = ");
                DecimalFormat df = new DecimalFormat("0.000");
                df.setMaximumFractionDigits(3);
                netWt.setText(df.format(result));
                break;
            case  R.id.btnAdd:
                progressBar.show();
                if(ValidateInput()){
                    String customer = customerSpinner.getSelectedItem().toString();
                    String tray = traySpinner.getSelectedItem().toString();
                    String packing = packingSprinner.getSelectedItem().toString();
                    Sale_Tray se = new Sale_Tray();
                    for(int i=0;i<CommonUtil.customerList.size();i++){
                        Customer c = CommonUtil.customerList.get(i);
                        if(c.getCustomer_Name().equals(customer)){
                            se.setCustomer_ID(c.getCustomer_ID());
                            break;
                        }
                    }
                    for(int i=0;i<CommonUtil.trayList.size();i++){
                        Tray tr = CommonUtil.trayList.get(i);
                        if(tr.getTray_Name().equals(tray)){
                            se.setTray_ID(tr.getTray_ID());
                            break;
                        }
                    }
                    for(int i=0;i<CommonUtil.packingsList.size();i++){
                        Packings pc = CommonUtil.packingsList.get(i);
                        if(pc.getPacking_NAME().equals(packing)){
                            se.setPacking_ID(pc.getPacking_ID());
                            break;
                        }
                    }
                    Sale_Tray seExists = null;
                    for(int i=0;i<saleTrays.size();i++){
                        Sale_Tray s = saleTrays.get(i);
                        if(s.getTray_ID() == se.getTray_ID() && s.getPacking_ID() == se.getPacking_ID()){
                            seExists = s;
                            break;
                        }
                    }
                    String netWeight = netWt.getText().toString();
                    result = Float.parseFloat(netWeight);
                    df = new DecimalFormat("0.000");
                    df.setMaximumFractionDigits(3);
                    se.setWeigth(df.format(result));
                    if(seExists != null){
                        Float newWt = Float.parseFloat(seExists.getWeigth());
                        Float tt = newWt + result;
                        se.setWeigth(df.format(tt));
                       saleTrays.remove(seExists);
                    }
                    saleTrays.add(se);
                    float TT = 0;
                    for(int i=0;i<saleTrays.size();i++){
                        Float wt =Float.parseFloat(saleTrays.get(i).getWeigth());
                        TT+=wt;
                    }
                    totalWeight.setText(df.format(TT));
                }
                else {
                showCustomDialog("Warning","Please Check the Input");
                }
                progressBar.cancel();
                break;
            case  R.id.btnSave:
                if(saleTrays.size()>0){
                    new SaveSaleEntry().execute("");
                }
                else {
                    showCustomDialog("Warning","No Data Added in Queue. Please Add Data To Save");
                }
                break;

        }

    }
    private  void  LoadPackingSpiner(ArrayList<String> pkList){
        ArrayAdapter adapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,pkList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packingSprinner.setAdapter(adapter2);
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

    public void showCustomPopup(String title, String Message, final int index) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                finish();
                //do something with edt.getText().toString();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void LoadSpiners(List<String> customerList,List<String> trayList,List<String> packingList) {
        progressBar.show();
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,customerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerSpinner.setAdapter(adapter);

        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,trayList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        traySpinner.setAdapter(adapter1);

        ArrayAdapter adapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,packingList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packingSprinner.setAdapter(adapter2);
        progressBar.cancel();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class LoadDropDownData extends AsyncTask<String,String,String>
    {

        ArrayList<String> custList = new ArrayList<>();
        ArrayList<String> trayList = new ArrayList<>();
        ArrayList<String> packList = new ArrayList<>();
        @Override
        protected void onPreExecute()
        {

            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            if(r.startsWith("ERROR")){
                showCustomDialog("Exception",r);
            }
            else {
                LoadSpiners(custList,trayList,packList);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String z="";
            try {
                String imi = params[0];
                Connection con = connectionClass.CONN(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT * FROM CUSTOMER";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    custList.add("<SELECT CUSTOMER>");
                    while (rs.next())
                    {
                        Customer customer = new Customer();
                        customer.setCustomer_ID(rs.getInt("CUSTOMER_ID"));
                        customer.setCustomer_Name(rs.getString("CUSTOMER_NAME"));
                        customer.setAddress(rs.getString("ADDRESS"));
                        CommonUtil.customerList.add(customer);
                        custList.add(customer.getCustomer_Name());
                    }
                    rs.close();
                    stmt.close();
                    query = "SELECT * FROM TRAYS";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    trayList.add("<SELECT TRAY>");
                    while (rs.next()){
                        Tray tray = new Tray();
                        tray.setTray_ID(rs.getInt("TRAY_ID"));
                        tray.setTray_Name(rs.getString("TRAY_NAME"));
                        tray.setShort_Name(rs.getString("SHORT_NAME"));
                        CommonUtil.trayList.add(tray);
                        trayList.add(tray.getTray_Name());
                    }
                    rs.close();
                    stmt.close();
                    query="SELECT * FROM PACKINGS";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    packList.add("<SELECT PACKING>");
                    while (rs.next()){
                        Packings packings = new Packings();
                        packings.setPacking_ID(rs.getInt("PACKING_ID"));
                        packings.setPacking_NAME(rs.getString("PACKING"));
                        packings.setShort_Name(rs.getString("SHORT_NAME"));
                        CommonUtil.packingsList.add(packings);
                        packList.add(packings.getPacking_NAME());
                    }
                    z = "SUCCESS";
                }
            }
            catch (Exception ex)
            {

                z = "ERROR:"+ex.getMessage();
            }
            return z;
        }
    }
    public class SaveSaleEntry extends AsyncTask<String,String,String>
    {
        ArrayList<Sale_Tray> sr = new ArrayList<Sale_Tray>();
        String totalwt = "";
        Integer dcNo = 0;
        @Override
        protected void onPreExecute()
        {
            sr = saleTrays;
            totalwt = totalWeight.getText().toString();
            progressBar.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            if(r.startsWith("ERROR")){
                showCustomDialog("Exception",r);
            }
            else {
                showCustomDialog("Message","Data Saved Sucessfully.!");
                int returnCode = PrintDC(dcNo);
                if(returnCode == 0){
                    customerSpinner.setSelection(0);
                    traySpinner.setSelection(0);
                    packingSprinner.setSelection(0);
                    totalWeight.setText("000");
                    saleTrays.clear();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String z="";
            try {
                String imi = params[0];
                Connection con = connectionClass.CONN(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "SELECT MAX(SALE_ID) AS SALE_ID FROM SALE";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    Integer saleId = 0;
                    while (rs.next())
                    {
                        saleId = rs.getInt("SALE_ID");
                    }
                    saleId+=1;
                    dcNo = saleId;
                    rs.close();
                    stmt.close();
                    Integer cusId = sr.get(0).getCustomer_ID();
                    query = "INSERT INTO SALE VALUES (?,GETDATE(),?,"+totalwt+")";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.setInt(1,saleId);
                    preparedStatement.setInt(2,cusId);
                    preparedStatement.execute();
                    preparedStatement.close();
                    for(int i=0;i<sr.size();i++){
                        Sale_Tray s = sr.get(i);
                        String qty = s.getWeigth();
                        query = "INSERT INTO SALE_TRAY VALUES (?,?,?,"+qty+")";
                        PreparedStatement preparedStatement1 = con.prepareStatement(query);
                        preparedStatement1.setInt(1,saleId);
                        preparedStatement1.setInt(2,s.getTray_ID());
                        preparedStatement1.setInt(3,s.getPacking_ID());
                        preparedStatement1.execute();
                        preparedStatement1.close();
                    }
                    con.close();
                    z = "SUCCESS";
                }
            }
            catch (Exception ex)
            {

                z = "ERROR:"+ex.getMessage();
            }
            return z;
        }
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                return true;
            case R.id.dcentry:
                Intent dcpage = new Intent(this,DCActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
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
