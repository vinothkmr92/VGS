package com.example.vinoth.vgspos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    public  static  final String DATABASE_NAME = "VGSPOS.db";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create application database");
      db.execSQL("CREATE TABLE USERS (USER_ID INTEGER PRIMARY KEY,USER_NAME TEXT,MOBILE_NUMBER TEXT,PASSWORD TEXT)");
      db.execSQL("CREATE TABLE TAX (TAX_ID INTEGER PRIMARY KEY,TAX_VALUE NUMERIC)");
      db.execSQL("CREATE TABLE ITEMS (ITEM_NO INTEGER PRIMARY KEY,ITEM_NAME TEXT,N_PRICE NUMERIC,AC_PRICE NUMERIC)");
      db.execSQL("CREATE TABLE STOCKS (ITEM_NO INTEGER PRIMARY KEY,STOCK NUMERIC)");
      db.execSQL("CREATE TABLE BILLS (BILL_NO INTEGER,BILL_DATE TEXT,SALE_AMT NUMERIC,USER_ID INTEGER,PRIMARY KEY (BILL_NO,BILL_DATE))");
      db.execSQL("CREATE TABLE BILLS_ITEM (BILL_NO INTEGER,BILL_DATE TEXT,ITEM_NO INTEGER,QUANTITY NUMERIC,PRICE NUMERIC,PRIMARY KEY(BILL_NO,BILL_DATE,ITEM_NO))");
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
        onCreate(db);
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
    public ArrayList<Item> GetItems(){
        ArrayList<Item> ItemList = new ArrayList<Item>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT I.* FROM ITEMS I",null);
         if(cur.getCount()>0){
            while (cur.moveToNext()){
                Item itm = new Item();
                itm.setItem_No(cur.getInt(0));
                itm.setItem_Name(cur.getString(1));
                ItemList.add(itm);
            }
         }
        return ItemList;
    }
    public String GetItemName(Integer itemNo){
        String itemName = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT ITEM_NAME FROM ITEMS WHERE ITEM_NO="+itemNo,null);
        if(cur.getCount()>0){
            if(cur.moveToNext()){
                itemName = cur.getString(0);
            }
        }
        return itemName;
    }
    public  void  Delete_Item(Integer itemNo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("ITEMS","ITEM_NO="+itemNo,null);
    }
    public void Insert_Item(Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put("ITEM_NO",item.getItem_No());
        cont.put("ITEM_NAME",item.getItem_Name());
        db.insert("ITEMS",null,cont);
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
        cont.put("BILL_DATE",billsItem.getBill_DateStr());
        cont.put("ITEM_NO",billsItem.getItem_No());
        cont.put("QUANTITY",billsItem.getQty());
        cont.put("PRICE",billsItem.getPrice());
        db.insert("BILLS_ITEM",null,cont);
    }
    public void Insert_Bills(Bills bills){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cnt = new ContentValues();
        cnt.put("",bills.getBill_No());
        cnt.put("",bills.getBill_DtStr());
        cnt.put("",bills.getSale_Amt());
        cnt.put("",bills.getUser().getUser_Id());
        db.insert("BILLS",null,cnt);
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
