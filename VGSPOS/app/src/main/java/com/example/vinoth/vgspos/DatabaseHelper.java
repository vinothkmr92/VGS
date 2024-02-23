package com.example.vinoth.vgspos;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public  static  final String DATABASE_NAME = "VGSPOS.db";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 11);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create application database");
        db.execSQL("CREATE TABLE ICONS (IMAGE BLOB)");
      db.execSQL("CREATE TABLE USERS (USER_ID INTEGER PRIMARY KEY,USER_NAME TEXT,MOBILE_NUMBER TEXT,PASSWORD TEXT)");
      db.execSQL("CREATE TABLE TAX (TAX_ID INTEGER PRIMARY KEY,TAX_VALUE NUMERIC)");
      db.execSQL("CREATE TABLE ITEMS (ITEM_NO INTEGER PRIMARY KEY,ITEM_NAME TEXT,PRICE NUMERIC,AC_PRICE NUMERIC,STOCK NUMERIC)");
      db.execSQL("CREATE TABLE STOCKS (ITEM_NO INTEGER PRIMARY KEY,STOCK NUMERIC)");
      db.execSQL("CREATE TABLE BILLS (BILL_NO INTEGER,BILL_DATE TEXT,SALE_AMT NUMERIC,WAITER TEXT,PRIMARY KEY (BILL_NO,BILL_DATE))");
      db.execSQL("CREATE TABLE BILLS_ITEM (BILL_NO INTEGER,BILL_DATE TEXT,ITEM_NAME TEXT,QUANTITY NUMERIC,WAITER TEXT,PRICE DOUBLE)");
      db.execSQL("CREATE TABLE CUSTOMERS (NAME TEXT,MOBILE_NUMBER TEXT,ADDRESS TEXT,PRIMARY KEY (MOBILE_NUMBER))");
      InsertMasterUser(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.w(TAG, "Upgrading database");
        db.execSQL("DROP TABLE IF EXISTS USERS");
        db.execSQL("DROP TABLE IF EXISTS TAX");
        db.execSQL("DROP TABLE IF EXISTS ITEMS");
        db.execSQL("DROP TABLE IF EXISTS STOCKS");
        db.execSQL("DROP TABLE IF EXISTS BILLS");
        db.execSQL("DROP TABLE IF EXISTS BILLS_ITEM");
        db.execSQL("DROP TABLE IF EXISTS CUSTOMERS");
        db.execSQL("DROP TABLE IF EXISTS ICONS");
        onCreate(db);
    }
    public byte[] GetReceiptIcon(){
        byte[] icon =null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM ICONS",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                icon = cur.getBlob(0);
            }
        }
        return icon;
    }
    public void DeleteIcon(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("ICONS",null,null);
    }
    public void InsertIcon(byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cntVal = new ContentValues();
        cntVal.put("IMAGE",image);
        byte[] im = GetReceiptIcon();
        if(im!=null){
            db.update("ICONS",cntVal,null,null);
        }
        else{
            db.insert("ICONS",null,cntVal);
        }

    }
    public void InsertCustomer(Customer customer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cntVal = new ContentValues();
        cntVal.put("NAME",customer.getCustomerName());
        cntVal.put("MOBILE_NUMBER",customer.getMobileNumber());
        cntVal.put("ADDRESS",customer.getAddress());
        Customer c = GetCustomer(customer.getMobileNumber());
        if(c==null){
            db.insert("CUSTOMERS",null,cntVal);
        }
        else {
            db.update("CUSTOMERS",cntVal,"MOBILE_NUMBER",new String[]{customer.getMobileNumber()});
        }

    }
    public  Item GetItem(String itemname){
        Item itm =null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM ITEMS WHERE ITEM_NAME='"+itemname+"'",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                itm = new Item();
                itm.setItem_No(cur.getInt(0));
                itm.setItem_Name(cur.getString(1));
                itm.setPrice(cur.getDouble(2));
                itm.setAcPrice(cur.getDouble(3));
                itm.setStocks(cur.getDouble(4));
            }
        }
        return itm;
    }
    public ArrayList<String> GetItemNames(){
        ArrayList<String> nameList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT ITEM_NAME FROM ITEMS",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                String itm = cur.getString(0);
                nameList.add(itm);
            }
        }
        return nameList;
    }
    public void DeleteBill(String billdt,String billno){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("BILLS_ITEM","DATE(BILL_DATE)='"+billdt+"' AND BILL_NO="+billno,null);
        db.delete("BILLS","DATE(BILL_DATE)='"+billdt+"' AND BILL_NO="+billno,null);
    }
    public  ArrayList<Bills_Item> GetBills_Item(String billdt,String billno){
        ArrayList<Bills_Item> report = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM BILLS_ITEM WHERE DATE(BILL_DATE)='"+billdt+"' AND BILL_NO="+billno,null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                Bills_Item r = new Bills_Item();
                r.setBill_No(cur.getInt(0));
                r.setBill_DateStr(cur.getString(1));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date bd = format.parse(r.getBill_DateStr(),new ParsePosition(0));
                r.setBill_Date(bd);
                r.setItem_Name(cur.getString(2));
                r.setQty(cur.getDouble(3));
                r.setWaiter(cur.getString(4));
                r.setPrice(cur.getDouble(5));
                Item item = GetItem(r.getItem_Name());
                if(null!=item){
                    r.setItem_No(item.getItem_No());
                }
                report.add(r);
            }
        }
        return  report;
    }
    public Customer GetCustomer(String mobileNumber){
        Customer customer =null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM CUSTOMERS WHERE MOBILE_NUMBER='"+mobileNumber+"'",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                customer = new Customer();
                customer.setCustomerName(cur.getString(0));
                customer.setMobileNumber(cur.getString(1));
                customer.setAddress(cur.getString(2));
            }
        }
        return customer;
    }
    public  ArrayList<Customer> GetCustomers(){
        ArrayList<Customer> customers = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM CUSTOMERS",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                Customer customer = new Customer();
                customer.setCustomerName(cur.getString(0));
                customer.setMobileNumber(cur.getString(1));
                customer.setAddress(cur.getString(2));
                customers.add(customer);
            }
        }
        return customers;
    }
    public void InsertUser(Users usr){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cntVal = new ContentValues();
        cntVal.put("USER_ID", usr.getUser_Id());
        cntVal.put("USER_NAME",usr.getUser_Name());
        cntVal.put("MOBILE_NUMBER",usr.getMobile_Number());
        cntVal.put("PASSWORD",usr.getPassword());
        db.insert("USERS",null,cntVal);
    }
    public void InsertMasterUser(SQLiteDatabase db){
        ContentValues cntVal = new ContentValues();
        cntVal.put("USER_ID",1);
        cntVal.put("USER_NAME","VINOTH");
        cntVal.put("MOBILE_NUMBER","9043106020");
        cntVal.put("PASSWORD","1234");
        db.insert("USERS",null,cntVal);
    }
    public int GetNextBillNo(){
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Date dt = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String sr = simpleDateFormat.format(dt);
        Cursor cur = db.rawQuery("SELECT MAX(BILL_NO) FROM BILLS WHERE DATE(BILL_DATE)='"+sr+"'",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                id = cur.getInt(0);
            }
        }
        id++;
        return  id;
    }

    public int GetNextItemNO(){
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT MAX(ITEM_NO) FROM ITEMS",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                id = cur.getInt(0);
            }
        }
        id++;
        return  id;
    }

    public  String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    public  String GetformattedItemName(String name){
        String res = padRight(name,25);
        return res.substring(0,25);
    }
    public ArrayList<ItemsRpt> GetAllItemsReports(String waiter){
        ArrayList<ItemsRpt> report = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT ITEM_NAME,SUM(QUANTITY) FROM BILLS_ITEM GROUP BY ITEM_NAME";
        if(!waiter.equals("ALL")){
            query = "SELECT ITEM_NAME,SUM(QUANTITY) FROM BILLS_ITEM WHERE WAITER='"+waiter+"' GROUP BY ITEM_NAME";
        }
        Cursor cur = db.rawQuery(query,null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                ItemsRpt r = new ItemsRpt();
                String name = cur.getString(0);
                r.setItemName(name);
                r.setQuantity(cur.getDouble(1));
                report.add(r);
            }
        }
        return  report;
    }
    public  ArrayList<SaleReport> GetSalesReport(String frmDate,String toDate,String waiter){
        ArrayList<SaleReport> report = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT BILL_NO,BILL_DATE,SALE_AMT FROM BILLS WHERE DATE(BILL_DATE)>='"+frmDate+"' AND DATE(BILL_DATE)<='"+toDate+"'";
        if(!waiter.equals("ALL")){
            query = query+" AND USER_ID='"+waiter+"'";
        }
        Cursor cur = db.rawQuery(query,null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                SaleReport r = new SaleReport();
                String billno = cur.getString(0);
                r.setBillNo(billno);
                String billDate = cur.getString(1);
                r.setBillDate(billDate);
                r.setBillAmount(cur.getDouble(2));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date bd = format.parse(r.getBillDate(),new ParsePosition(0));
                r.setBillDt(bd);
                report.add(r);
            }
        }
        return  report;
    }

    public ArrayList<ItemsRpt> GetReports(String frmDt,String toDt,String waiter,boolean isStockRpt){
       ArrayList<ItemsRpt> report = new ArrayList<>();
       if(isStockRpt){
           ArrayList<Item> items = GetItems();
           for (Item item:
                items) {
               ItemsRpt rpt = new ItemsRpt();
               rpt.setItemName(item.getItem_Name());
               rpt.setQuantity(item.getStocks());
               report.add(rpt);
           }
       }
       else{
           SQLiteDatabase db = this.getWritableDatabase();
           String query = "SELECT ITEM_NAME,SUM(QUANTITY) FROM BILLS_ITEM WHERE DATE(BILL_DATE)>='"+frmDt+"' AND DATE(BILL_DATE)<='"+toDt+"' GROUP BY ITEM_NAME";
           if(!waiter.equals("ALL")){
               query = query+" AND WAITER='"+waiter+"' GROUP BY ITEM_NAME";
           }
           Cursor cur = db.rawQuery(query,null);
           if(cur.getCount()>0){
               while (cur.moveToNext()){
                   ItemsRpt r = new ItemsRpt();
                   String name = cur.getString(0);
                   r.setItemName(name);
                   r.setQuantity(cur.getDouble(1));
                   report.add(r);
               }
           }   
       }
       return  report;
    }
    public ArrayList<Item> GetItems(){
        ArrayList<Item> ItemList = new ArrayList<Item>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT I.* FROM ITEMS I",null);
         if(cur.getCount()>0){
            while (cur.moveToNext()){
                Item itm = new Item();
                itm.setItem_No(cur.getInt(0));
                itm.setItem_Name(cur.getString(1));
                itm.setPrice(cur.getDouble(2));
                itm.setAcPrice(cur.getDouble(3));
                itm.setStocks(cur.getDouble(4));
                ItemList.add(itm);
            }
         }
        return ItemList;
    }

    public Item GetItem(Integer itemNo){
        Item item=null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT ITEM_NAME,PRICE,AC_PRICE,STOCK FROM ITEMS WHERE ITEM_NO="+itemNo,null);
        if(cur.getCount()>0){
            if(cur.moveToNext()){
                item = new Item();
                item.setItem_Name( cur.getString(0));
                item.setItem_No(itemNo);
                item.setPrice(cur.getDouble(1));
                item.setAcPrice(cur.getDouble(2));
                item.setStocks(cur.getDouble(3));
            }
        }
        return item;
    }
    public  void  Delete_Item(Integer itemNo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("ITEMS","ITEM_NO="+itemNo,null);
    }
    public void DeleteAllItem(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("ITEMS",null,null);
    }
    public void Insert_Item(Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put("ITEM_NO",item.getItem_No());
        cont.put("ITEM_NAME",item.getItem_Name());
        cont.put("PRICE",item.getPrice());
        cont.put("AC_PRICE",item.getAcPrice());
        cont.put("STOCK",item.getStocks());
        Item it = GetItem(item.getItem_No());
        if(it!=null){
            db.update("ITEMS",cont,"ITEM_NO = ?",new String[]{String.valueOf(item.getItem_No())});
        }
        else {
            db.insert("ITEMS",null,cont);
        }

    }
    public void Insert_Tax(Tax t){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cnt = new ContentValues();
        cnt.put("TAX_ID",t.getTax_Id());
        cnt.put("TAX_VALUE",t.getTax_Value());
        db.insert("TAX",null,cnt);
    }
    public void Insert_Stock(Stock stock){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put("ITEM_NO",stock.getItem_No());
        cont.put("STOCK",stock.getQty());
        db.insert("STOCKS",null,cont);
    }

    public void  Insert_Bill_Items(Bills_Item billsItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put("BILL_NO",billsItem.getBill_No());
        //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' & 'hh:mm:aaa", Locale.getDefault());
        cont.put("BILL_DATE",billsItem.getBill_DateStr());
        cont.put("ITEM_NAME",billsItem.getItem_Name());
        cont.put("QUANTITY",billsItem.getQty());
        cont.put("WAITER",billsItem.getWaiter());
        cont.put("PRICE",billsItem.getPrice());
        long s = db.insert("BILLS_ITEM",null,cont);
        if(s>0){
            Log.println(Log.ASSERT,"","Successfully inserted bill items");
            UpdateStock(billsItem.getItem_No(),billsItem.getQty());
        }
        else {
            Log.println(Log.ASSERT,"","Failed to insert bill items...ITEM_NAME: "+billsItem.getItem_Name());
        }
    }
    public void UpdateStock(int itemno,double qty){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE ITEMS SET STOCK=STOCK-"+qty+" WHERE ITEM_NO="+itemno);
    }
    public void Insert_Bills(Bills bills){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cnt = new ContentValues();
        cnt.put("BILL_NO",bills.getBill_No());
        cnt.put("BILL_DATE",bills.getBill_Date());
        cnt.put("SALE_AMT",bills.getSale_Amt());
        cnt.put("WAITER",bills.getUser());
        long s =db.insert("BILLS",null,cnt);
        if(s>0){
            Log.println(Log.ASSERT,"","Successfully inserted bills");
        }
        else {
            Log.println(Log.ASSERT,"","Failed to insert bill no: "+bills.getBill_No());
        }
    }
    public ArrayList<Stock> GetStocks(){
        ArrayList<Stock> stocks = new ArrayList<Stock>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM STOCKS",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                Stock s = new Stock();
                s.setItem_No(cur.getInt(0));
                s.setQty(cur.getDouble(1));
                stocks.add(s);
            }
        }
        return stocks;
    }
    public Users GetUser(String userId){
        Users usr = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM USERS WHERE USER_ID="+userId, null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                usr = new Users();
                usr.setUser_Id(cur.getInt(0));
                usr.setUser_Name(cur.getString(1));
                usr.setMobile_Number(cur.getString(2));
                usr.setPassword(cur.getString(3));
            }
        }
        return  usr;
    }
}
