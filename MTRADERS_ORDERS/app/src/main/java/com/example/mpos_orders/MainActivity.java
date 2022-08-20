package com.example.mpos_orders;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText qty;
    TextView logedinuser;
    TextView estimatedAmt;
    Spinner productDropdown;
    ArrayList<String> productnamelist;
    Dialog progressBar;
    ConnectionClass connectionClass;
    TableLayout dataGrid;
    ArrayList<OrderProducts> orderProductsArraList;
    Button clearBtn;
    Button saveBtn;
    double totalAmt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(CommonUtil.loggedUserName.isEmpty()){
            LoadLogin();
        }
        else{
            totalAmt = 0;
            progressBar = new Dialog(MainActivity.this);
            progressBar.setContentView(R.layout.custom_progress_dialog);
            progressBar.setTitle("Loading");
            connectionClass = new ConnectionClass();
            clearBtn = (Button)findViewById(R.id.clearButton);
            saveBtn = (Button)findViewById(R.id.saveButton);
            estimatedAmt = (TextView)findViewById(R.id.estimateAmt);
            clearBtn.setOnClickListener(this);
            saveBtn.setOnClickListener(this);
            qty = (EditText)findViewById(R.id.qty);
            logedinuser = (TextView)findViewById(R.id.loggedinusr);
            logedinuser.setText("Username: "+CommonUtil.loggedUserName);
            productDropdown = (Spinner)findViewById(R.id.productDropdown);
            dataGrid = (TableLayout)findViewById(R.id.datagrid);
            progressBar.show();
            productnamelist = new ArrayList<>();
            orderProductsArraList = new ArrayList<OrderProducts>();
            for(int i=0;i<CommonUtil.productsList.size();i++){
                productnamelist.add(CommonUtil.productsList.get(i).getProductName());
            }
            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,productnamelist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            productDropdown.setAdapter(adapter);
            progressBar.cancel();
            productDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Your code here
                    if(productDropdown.getSelectedItemPosition()>0){
                        qty.setFocusableInTouchMode(true);
                        qty.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(qty, InputMethodManager.SHOW_IMPLICIT);
                    }
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });
            qty.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        //  Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                        String prname = productDropdown.getSelectedItem().toString();
                        if(prname.startsWith("<SELECT")){
                            showCustomDialog("Warning","Please Select Valid Product");
                            return  false;
                        }
                        String qtys = qty.getText().toString();
                        Double qt = Double.parseDouble(qtys);
                        if(qt<=0){
                            showCustomDialog("Warning","Please enter valid Quantity");
                            return  false;
                        }
                        OrderProducts orp = new OrderProducts();
                        String prid = "";
                        Double sellingprice = 0d;
                        for(Product p:CommonUtil.productsList){
                            if(p.getProductName().equals(prname)){
                                prid = p.getProductID();
                                sellingprice = p.getSellingPrice();
                                break;
                            }
                        }
                        orp.setProductID(prid);
                        orp.setProductName(prname);
                        double amt = sellingprice*qt;
                        orp.setAmt(amt);
                        orp.setQuantity(qt);
                        int indexs = 0;
                        boolean prodexists = false;
                        for(int i=0;i<orderProductsArraList.size();i++){
                            OrderProducts o = orderProductsArraList.get(i);
                            if(o.getProductID().equals(orp.getProductID())){
                                indexs = i;
                                prodexists = true;
                                break;
                            }
                        }
                        if(prodexists){
                            orderProductsArraList.remove(indexs);
                        }
                        orderProductsArraList.add(orp);
                        LoadDataGrid();
                        qty.setText("");
                        productDropdown.setSelection(0);
                        return true;
                    }
                    return false;
                }
            });
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



    public  void  LoadDataGrid(){
        dataGrid.removeAllViews();
        totalAmt = 0;
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setPadding(10,10,10,10);
        TextView textView = new TextView(this);
        String s="PRODUCT_NAME";
        s = StringUtils.rightPad(s,35,' ');
        textView.setText(s);
        textView.setTextColor(Color.BLUE);
        textView.setBackgroundResource(R.drawable.cellborder);
        textView.setBackgroundColor(Color.WHITE);
        row.addView(textView);
        TextView textView1 = new TextView(this);
        textView1.setText("QTY");
        //textView1.setTextSize(30);
        textView1.setPadding(10,0,10,0);
        textView1.setTextColor(Color.BLUE);
        textView1.setBackgroundResource(R.drawable.cellborder);
        textView1.setBackgroundColor(Color.WHITE);
        row.addView(textView1);
        dataGrid.addView(row,new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        for(int j=orderProductsArraList.size()-1;j>=0;j--)
        {
            OrderProducts b = orderProductsArraList.get(j);
            // Inflate your row "template" and fill out the fields.
            totalAmt+=b.getAmt();
            TableRow row1 = new TableRow(this);
            row1.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row1.setPadding(5,5,5,5);
            TextView textView3 = new TextView(this);
            String prname = b.getProductName();
            if(prname.length()>35){
                prname = prname.substring(0,35);
            }
            String pr = StringUtils.rightPad(prname,35,' ');
            textView3.setText(pr);
            textView3.setTextColor(Color.BLACK);
            textView3.setBackgroundResource(R.drawable.cellborder);
            textView3.setBackgroundColor(Color.WHITE);
            row1.addView(textView3);
            TextView textView4 = new TextView(this);
            textView4.setText(String.valueOf("   "+b.getQuantity()));
            textView4.setTextColor(Color.BLACK);
            textView4.setBackgroundResource(R.drawable.cellborder);
            textView4.setBackgroundColor(Color.WHITE);
            textView4.setPadding(10,0,10,0);
            row1.addView(textView4);
            dataGrid.addView(row1,new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
        estimatedAmt.setText("₹ "+totalAmt);
    }
    public void SaveOrder(){
       if(orderProductsArraList.size()>0){
           new SaveOrderDb().execute("");
       }
       else{
           showCustomDialog("Warning","No items selected for Order");
       }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearButton:
                if(orderProductsArraList.size()>0){
                    orderProductsArraList.remove(orderProductsArraList.size()-1);
                    LoadDataGrid();
                    qty.setText("");
                    productDropdown.setSelection(0);
                }
                break;
            case R.id.saveButton:
                SaveOrder();
                break;
        }
    }
    public class SaveOrderDb extends AsyncTask<String,String,String>
    {

        ArrayList<OrderProducts> op = new ArrayList<>();
        int orderNumber = 0;
        double pendingAmt = 0;
        @Override
        protected void onPreExecute() {
            progressBar.show();
            op = orderProductsArraList;
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.cancel();
            if(r.startsWith("ERROR")){
                showCustomDialog("Exception",r);
            }
            else if(pendingAmt>0){
                showCustomDialog("FAILED","Please Pay your balance amount to proceed. Pending Amount:"+pendingAmt);
            }
            else {
                showCustomDialog("Msg","Your order no "+orderNumber+" has been Successfully Saved.");
                orderProductsArraList.clear();
                productDropdown.setSelection(0);
                LoadDataGrid();
            }

            // if(isSuccess) {
            //   Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();
            //}

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
                    String querybal = "SELECT Current_Balance FROM MEMBERS WHERE MEMBER_ID="+CommonUtil.member.getMemberID();
                    Statement balstmt = con.createStatement();
                    ResultSet rsbal = balstmt.executeQuery(querybal);
                    try{
                        if (rsbal.next())
                        {
                            pendingAmt = rsbal.getDouble("Current_Balance");
                        }
                    }
                    catch(Exception ex) {
                        //
                        Log.e("MSG",ex.getMessage());

                    }
                    if(pendingAmt==0){
                        String query = "SELECT MAX(ORDER_ID) AS ORDER_ID FROM ORDERS";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        int orderID = 0;
                        try{
                            while (rs.next())
                            {
                                orderID = rs.getInt("ORDER_ID");
                            }
                        }
                        catch(Exception ex) {
                            //
                            Log.e("MSG",ex.getMessage());

                        }
                        orderID = orderID+1;
                        stmt.close();
                        rs.close();
                        String orderinsertQuery = "INSERT INTO ORDERS VALUES (?,GETDATE(),?,?)";
                        PreparedStatement ps =con.prepareStatement(orderinsertQuery);
                        ps.setInt(1,orderID);
                        ps.setInt(2,CommonUtil.member.getMemberID());
                        ps.setString(3,"OPEN");
                        ps.execute();
                        ps.close();
                        for(OrderProducts ops:orderProductsArraList){
                            String opsinsertquery = "INSERT INTO ORDER_PRODUCTS VALUES (?,?,?)";
                            PreparedStatement p1 = con.prepareStatement(opsinsertquery);
                            p1.setInt(1,orderID);
                            p1.setString(2,ops.getProductID());
                            p1.setDouble(3,ops.getQuantity());
                            p1.execute();
                            p1.close();
                        }
                        orderNumber = orderID;
                        z = "SUCCESS";
                    }

                }
            }
            catch (Exception ex)
            {
                z = "ERROR:"+ex.getMessage();
            }
            return z;
        }
    }
}