package com.example.hyndsticker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    public  static  final String DATABASE_NAME = "HYD_STKR.db";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create application database");
        db.execSQL("CREATE TABLE REF (REF_ID INTEGER PRIMARY KEY,SHOP TEXT,PART_NO TEXT,PART_NAME TEXT,REASON_CD TEXT,REASON TEXT,DEPART_CD TEXT,APPROVED_BY TEXT,ENTERED_BY TEXT)");
      db.execSQL("CREATE TABLE ENTRY (ENTRY_ID INTEGER PRIMARY KEY,ENTRY_DATE TEXT,SHOP TEXT,PART_NO TEXT,PART_NAME TEXT,QTY INTEGER,REASON_CD TEXT,REASON TEXT,DEPART_CD TEXT,APPROVED_BY TEXT,ENTERED_BY TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.w(TAG, "Upgrading database");
        db.execSQL("DROP TABLE IF EXISTS ENTRY");
        db.execSQL("DROP TABLE IF EXISTS REF");
        onCreate(db);
    }
    public void InsesrtRef(Entry entry){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cntVal = new ContentValues();
        cntVal.put("REF_ID",entry.getENTRY_ID());
        cntVal.put("SHOP",entry.getSHOP());
        cntVal.put("PART_NO",entry.getPART_NO());
        cntVal.put("PART_NAME",entry.getPART_NAME());
        cntVal.put("REASON_CD",entry.getREASON_CD());
        cntVal.put("REASON",entry.getREASON());
        cntVal.put("DEPART_CD",entry.getDEPART_CD());
        cntVal.put("APPROVED_BY",entry.getAPPROVED_BY());
        cntVal.put("ENTERED_BY",entry.getENTERED_BY());
        db.insert("REF",null,cntVal);
    }
    public void InsesrtEntry(Entry entry){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cntVal = new ContentValues();
        cntVal.put("ENTRY_ID", entry.getENTRY_ID());
        cntVal.put("ENTRY_DATE",entry.getENTRY_DATE());
        cntVal.put("SHOP",entry.getSHOP());
        cntVal.put("PART_NO",entry.getPART_NO());
        cntVal.put("PART_NAME",entry.getPART_NAME());
        cntVal.put("QTY",entry.getQTY());
        cntVal.put("REASON_CD",entry.getREASON_CD());
        cntVal.put("REASON",entry.getREASON());
        cntVal.put("DEPART_CD",entry.getDEPART_CD());
        cntVal.put("APPROVED_BY",entry.getAPPROVED_BY());
        cntVal.put("ENTERED_BY",entry.getENTERED_BY());
        db.insert("ENTRY",null,cntVal);
    }
    public int GetNextEntryID(){
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT MAX(ENTRY_ID) FROM ENTRY",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                id = cur.getInt(0);
            }
        }
        id++;
        return  id;
    }
    public int GetNextRefID(){
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT MAX(REF_ID) FROM REF",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                id = cur.getInt(0);
            }
        }
        id++;
        return  id;
    }
    public ArrayList<String> GetShops(){
        ArrayList<String> shopList = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT(SHOP) FROM REF",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
               shopList.add(cur.getString(0));
            }
        }
        return shopList;
    }
    public ArrayList<String> GetPartNo(){
        ArrayList<String> partNoList = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT(PART_NO) FROM REF",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                partNoList.add(cur.getString(0));
            }
        }
        return partNoList;
    }
    public ArrayList<String> GetPartName(){
        ArrayList<String> partNameList = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT(PART_NAME) FROM REF",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                partNameList.add(cur.getString(0));
            }
        }
        return partNameList;
    }
    public ArrayList<String> GetReasonCd(){
        ArrayList<String> reasonCdList = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT(REASON_CD) FROM REF",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                reasonCdList.add(cur.getString(0));
            }
        }
        return reasonCdList;
    }
    public ArrayList<String> GetReason(){
        ArrayList<String> reasonList = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT(REASON) FROM REF",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                reasonList.add(cur.getString(0));
            }
        }
        return reasonList;
    }
    public ArrayList<String> GetDepartnmentCd(){
        ArrayList<String> departList = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT(DEPART_CD) FROM REF",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                departList.add(cur.getString(0));
            }
        }
        return departList;
    }
    public ArrayList<String> GetApprovedBy(){
        ArrayList<String> approvedList = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT(APPROVED_BY) FROM REF",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                approvedList.add(cur.getString(0));
            }
        }
        return approvedList;
    }
    public ArrayList<String> GetEnteredBy(){
        ArrayList<String> enteredByList = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT DISTINCT(ENTERED_BY) FROM REF",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                enteredByList.add(cur.getString(0));
            }
        }
        return enteredByList;
    }
    public ArrayList<Entry> GetEntry(){
        ArrayList<Entry> entries = new ArrayList<Entry>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM ENTRY",null);
         if(cur.getCount()>0){
            while (cur.moveToNext()){
                Entry gr = new Entry();
                gr.setENTRY_ID(cur.getInt(0));
                gr.setENTRY_DATE(cur.getString(1));
                gr.setSHOP(cur.getString(2));
                gr.setPART_NO(cur.getString(3));
                gr.setPART_NAME(cur.getString(4));
                gr.setQTY(cur.getInt(5));
                gr.setREASON_CD(cur.getString(6));
                gr.setREASON(cur.getString(7));
                gr.setDEPART_CD(cur.getString(8));
                gr.setAPPROVED_BY(cur.getString(9));
                gr.setENTERED_BY(cur.getString(10));
                entries.add(gr);
            }
         }
        return entries;
    }
}
