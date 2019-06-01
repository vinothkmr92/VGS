package com.example.bwq;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    public  static  final String DATABASE_NAME = "VG_BWQ.db";
    public  static  final String PRODUCT_TABLE_NAME = "PRODUCT";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create application database");
      db.execSQL("CREATE TABLE PRODUCT (PRODUCT_ID INTEGER PRIMARY KEY,PRODUCT_NAME TEXT,WT NUMBER,QTY NUMBER,PD_DATE TEXT,SUPPLIER TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.w(TAG, "Upgrading database");
        db.execSQL("DROP TABLE IF EXISTS PRODUCT");
        onCreate(db);
    }
    public void InsesrtProduct(Product product){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cntVal = new ContentValues();
        cntVal.put("PRODUCT_ID", product.getProductID());
        cntVal.put("PRODUCT_NAME",product.getProductName());
        cntVal.put("WT",product.getWeight());
        cntVal.put("QTY",product.getQty());
        cntVal.put("PD_DATE",product.getDtString());
        cntVal.put("SUPPLIER",product.getSupplier());
        db.insert("PRODUCT",null,cntVal);
    }
    public int GetNextProductID(){
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT MAX(PRODUCT_ID) FROM PRODUCT",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                id = cur.getInt(0);
            }
        }
        id++;
        return  id;
    }
    public ArrayList<String> GetProductNames(){
        ArrayList<String> productList = new ArrayList<String>();
        for(Product p :GetProducts()){
           productList.add(p.getProductName());
        }
        return productList;
    }
    public ArrayList<Product> GetProducts(){
        ArrayList<Product> productList = new ArrayList<Product>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM PRODUCT",null);
         if(cur.getCount()>0){
            while (cur.moveToNext()){
                Product gr = new Product();
                gr.setProductID(cur.getInt(0));
                gr.setProductName(cur.getString(1));
                gr.setWeight(cur.getDouble(2));
                gr.setQty(cur.getInt(3));
                gr.setDtString(cur.getString(4));
                gr.setSupplier(cur.getString(5));
                productList.add(gr);
            }
         }
        return productList;
    }
}
