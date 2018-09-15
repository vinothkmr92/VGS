package com.example.vinoth.evergrn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
        traySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedTray = traySpinner.getSelectedItem().toString();
                String selectedPackage = packingSprinner.getSelectedItem().toString();
                ArrayList<String> pkList = new ArrayList<>();
                pkList.add("<SELECT PACKING>");
                for(int i=0;i<CommonUtil.packingsList.size();i++){

                        String pacName = CommonUtil.packingsList.get(i).getPacking_NAME();
                        pkList.add(pacName);
                }
                double weightToReduce = 0;
                switch (SelectedTray){
                    case "10KG_BIG_TRAY":
                        pkList.remove("WITHOUT_PUNNET");
                        pkList.remove("LOOSE");
                        pkList.remove("ST");
                    case "8KG_BIG_TRAY":
                        pkList.remove("WITHOUT_PUNNET");
                        pkList.remove("LOOSE");
                        pkList.remove("ST");
                    case "8KG_SMALL_TRAY":
                        pkList.remove("WITH_PUNNET");
                        pkList.remove("LOOSE");
                        pkList.remove("ST");
                }
                LoadPackingSpiner(pkList);
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
                float weightToReduce = 0;
                switch (SelectedTray){
                    case "15KG_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 1650;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=1250;
                                break;
                            case "LOOSE":
                                weightToReduce=980;
                                break;
                            case "ST":
                                weightToReduce=1650;
                                break;
                        }
                    case "10KG_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 1050;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=800;
                                break;
                            case "LOOSE":
                                weightToReduce=620;
                                break;
                            case "ST":
                                weightToReduce=1050;
                                break;
                        }
                    case "10KG_BIG_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 2585;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=2478;
                                break;
                        }
                    case "8KG_BIG_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 2478;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=2478;
                                break;
                        }
                    case "8KG_SMALL_TRAY":
                        switch (selectedPackage){
                            case "WITH_PUNNET":
                                weightToReduce = 2478;
                                break;
                            case "WITHOUT_PUNNET":
                                weightToReduce=2115;
                                break;
                        }
                }
                Float wt=Float.parseFloat(CommonUtil.WeightFromBlueTooth);
                Float result = wt-weightToReduce;
                netwtString.setText(wt.toString()+"-"+weightToReduce+" = ");
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
            case  R.id.btnAdd:
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
                    Float result=Float.parseFloat(netWeight);
                    DecimalFormat df = new DecimalFormat("0.000");
                    df.setMaximumFractionDigits(3);
                    se.setWeigth(df.format(result));
                    if(seExists != null){
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
                break;
            case  R.id.btnSave:
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
