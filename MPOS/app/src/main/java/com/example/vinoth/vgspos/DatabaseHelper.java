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
        super(context, DATABASE_NAME, null, 15);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create application database");
        db.execSQL("CREATE TABLE ICONS (IMAGE BLOB)");
      db.execSQL("CREATE TABLE USERS (USER_ID INTEGER PRIMARY KEY,USER_NAME TEXT,MOBILE_NUMBER TEXT,PASSWORD TEXT)");
      db.execSQL("CREATE TABLE TAX (TAX_ID INTEGER PRIMARY KEY,TAX_VALUE NUMERIC)");
      db.execSQL("CREATE TABLE ITEMS (ITEM_NO TEXT PRIMARY KEY,ITEM_NAME TEXT,PRICE NUMERIC,AC_PRICE NUMERIC,STOCK NUMERIC)");
      db.execSQL("CREATE TABLE STOCKS (ITEM_NO INTEGER PRIMARY KEY,STOCK NUMERIC)");
      db.execSQL("CREATE TABLE BILLS (BILL_NO INTEGER,BILL_DATE TEXT,SALE_AMT NUMERIC,WAITER TEXT,DISCOUNT NUMERIC,PAYMENT_MODE TEXT,PRIMARY KEY (BILL_NO,BILL_DATE))");
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
        Cursor cur = db.rawQuery("SELECT * FROM ITEMS WHERE ITEM_NO='"+itemname+"' OR ITEM_NAME='"+itemname+"'",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                itm = new Item();
                itm.setItem_No(cur.getString(0));
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
        ArrayList<Bills_Item> billsItems = GetBills_Item(billdt,billno);
        for(int i=0;i<billsItems.size();i++){
            Bills_Item bi = billsItems.get(i);
            AddStock(bi.getItem_No(),bi.getQty());
        }
        db.delete("BILLS_ITEM","DATE(BILL_DATE)='"+billdt+"' AND BILL_NO="+billno,null);
        db.delete("BILLS","DATE(BILL_DATE)='"+billdt+"' AND BILL_NO="+billno,null);
    }
    public double GetDiscountOnBill(String billdt,String billno){
        double discount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT DISCOUNT FROM BILLS WHERE DATE(BILL_DATE)='"+billdt+"' AND BILL_NO="+billno,null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                discount = cur.getDouble(0);
            }
        }
        return  discount;
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
    public  ArrayList<SaleReport> GetSalesReport(String frmDate,String toDate,String waiter){
        ArrayList<SaleReport> report = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT BILL_NO,BILL_DATE,SALE_AMT,DISCOUNT,PAYMENT_MODE FROM BILLS WHERE DATE(BILL_DATE)>='"+frmDate+"' AND DATE(BILL_DATE)<='"+toDate+"'";
        if(!waiter.equals("ALL")){
            query = query+" AND WAITER='"+waiter+"'";
        }

        Cursor cur = db.rawQuery(query,null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                SaleReport r = new SaleReport();
                String billno = cur.getString(0);
                r.setBillNo(billno);
                String billDate = cur.getString(1);
                r.setBillDate(billDate);
                double saleAmt = cur.getDouble(2);
                double discount = cur.getDouble(3);
                r.setPaymentMode(cur.getString(4));
                double billAmt = saleAmt-discount;
                r.setBillAmount(billAmt);
                r.setSaleAmount(saleAmt);
                r.setDiscount(discount);
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
        ArrayList<Item> items = GetItems();
       if(isStockRpt){
           for (Item item:
                items) {
               ItemsRpt rpt = new ItemsRpt();
               rpt.setItemID(String.valueOf(item.getItem_No()));
               rpt.setItemName(item.getItem_Name());
               rpt.setQuantity(item.getStocks());
               rpt.setAmount(item.getPrice()*item.getStocks());
               report.add(rpt);
           }
       }
       else{
           SQLiteDatabase db = this.getWritableDatabase();
           String query = "SELECT ITEM_NAME,SUM(QUANTITY),SUM(QUANTITY*PRICE) AS AMT FROM " +
                   "BILLS_ITEM WHERE DATE(BILL_DATE)>='"+frmDt+"' AND DATE(BILL_DATE)<='"+toDt+"'";
           if(!waiter.equals("ALL")){
               query = query+" AND WAITER='"+waiter+"' GROUP BY ITEM_NAME";
           }
           else {
               query = query+" GROUP BY ITEM_NAME";
           }
           Cursor cur = db.rawQuery(query,null);

           if(cur.getCount()>0){
               while (cur.moveToNext()){
                   ItemsRpt r = new ItemsRpt();
                   String name = cur.getString(0);
                   Item item = null;
                   for (Item i:
                        items) {
                       if(i.getItem_Name().equals(name)){
                           r.setItemID(String.valueOf(i.getItem_No()));
                           break;
                       }
                   }
                   r.setItemName(name);
                   r.setQuantity(cur.getDouble(1));
                   r.setAmount(cur.getDouble(2));
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
                itm.setItem_No(cur.getString(0));
                itm.setItem_Name(cur.getString(1));
                itm.setPrice(cur.getDouble(2));
                itm.setAcPrice(cur.getDouble(3));
                itm.setStocks(cur.getDouble(4));
                ItemList.add(itm);
            }
         }
        return ItemList;
    }
    public ArrayList<ItemsCart> GetItemsAsItemCart(){
        ArrayList<ItemsCart> ItemList = new ArrayList<ItemsCart>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT I.* FROM ITEMS I",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                ItemsCart cart = new ItemsCart();
                cart.setItem_No(cur.getString(0));
                cart.setItem_Name(cur.getString(1));
                cart.setPrice(cur.getDouble(2));
                cart.setMRP(cur.getDouble(3));
                ItemList.add(cart);
            }
        }
        return ItemList;
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
        BILLS_ITEM (BILL_NO INTEGER,BILL_DATE TEXT,ITEM_NAME TEXT,QUANTITY NUMERIC,WAITER TEXT,PRICE DOUBLE)
        long s = db.insert("BILLS_ITEM",null,cont);
        if(s>0){
            Log.println(Log.ASSERT,"","Successfully inserted bill items");
            UpdateStock(billsItem.getItem_No(),billsItem.getQty());
        }
        else {
            Log.println(Log.ASSERT,"","Failed to insert bill items...ITEM_NAME: "+billsItem.getItem_Name());
        }
    }
    public void UpdateStock(String itemno,double qty){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE ITEMS SET STOCK=STOCK-"+qty+" WHERE ITEM_NO='"+itemno+"'");
    }
    public void AddStock(String itemno,double qty){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE ITEMS SET STOCK=STOCK+"+qty+" WHERE ITEM_NO='"+itemno+"'");
    }
    public void Insert_Bills(Bills bills){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cnt = new ContentValues();
        cnt.put("BILL_NO",bills.getBill_No());
        cnt.put("BILL_DATE",bills.getBill_Date());
        cnt.put("SALE_AMT",bills.getSale_Amt());
        cnt.put("WAITER",bills.getUser());
        cnt.put("DISCOUNT",bills.getDiscount());
        cnt.put("PAYMENT_MODE",bills.getPaymentMode());
        long s =db.insert("BILLS",null,cnt);
        if(s>0){
            Log.println(Log.ASSERT,"","Successfully inserted bills");
        }
        else {
            Log.println(Log.ASSERT,"","Failed to insert bill no: "+bills.getBill_No());
        }
    }
}
