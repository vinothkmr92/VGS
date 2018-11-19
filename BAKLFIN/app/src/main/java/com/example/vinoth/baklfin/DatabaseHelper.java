package com.example.vinoth.baklfin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.PortUnreachableException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    public  static  final  String DATABASE_NAME = "VG_FIN.db";
    public  static  final  String USER_TABLE_NAME = "USER";
    public  static  final  String CUSTOMER_TABLE_NAME = "CUSTOMER";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create application database");
      db.execSQL("CREATE TABLE USERS (USER_ID INTEGER PRIMARY KEY,USER_NAME TEXT,MOBILE_NUMBER TEXT,PASSWORD TEXT)");
      db.execSQL("CREATE TABLE CUSTOMERS (CUSTOMER_ID INTEGER PRIMARY KEY,CUSTOMER_NAME TEXT,MOBILE_NUMBER TEXT,ADDRESS TEXT,ID_PROOF TEXT)");
      db.execSQL("CREATE TABLE GROUPS (GR_ID INTEGER PRIMARY KEY,GR_NAME TEXT,AMOUNT NUMERIC,MAX_CUST INTEGER,INTEREST NUMERIC,TENURE INTEGER)");
      db.execSQL("CREATE TABLE CUST_GR (GR_ID INTEGER,CUSTOMER_ID INTEGER,PRIMARY KEY (GR_ID,CUSTOMER_ID))");
      db.execSQL("CREATE TABLE GRP_PAYMENT_DETAILS (PAYMENT_ID INTEGER PRIMARY KEY,PAYMENT_DATE TEXT,CUSTOMER_ID INTEGER,GR_ID INTEGER,PAID_AMT NUMERIC,RECEIVED_BY TEXT,BALANCE_AMT NUMERIC,PAYMENT_TYPE TEXT,SYNC_STS INTEGER)");
      InsertMasterUser(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.w(TAG, "Upgrading database");
        db.execSQL("DROP TABLE IF EXISTS USERS");
        db.execSQL("DROP TABLE IF EXISTS CUSTOMERS");
        db.execSQL("DROP TABLE IF EXISTS GROUPS");
        db.execSQL("DROP TABLE IF EXISTS CUST_GR");
        db.execSQL("DROP TABLE IF EXISTS GRP_PAYMENT_DETAILS");
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
        cntVal.put("USER_ID",10001);
        cntVal.put("USER_NAME","VINOTH");
        cntVal.put("MOBILE_NUMBER","9043106020");
        cntVal.put("PASSWORD","1@Knowbut");
        db.insert("USERS",null,cntVal);
    }
    public void ClearAllRecrods(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM GRP_PAYMENT_DETAILS");
        db.execSQL("DELETE FROM USERS WHERE USER_ID>10001");
        db.execSQL("DELETE FROM CUSTOMERS");
        db.execSQL("DELETE FROM GROUPS");
        db.execSQL("DELETE FROM CUST_GR");
        db.close();
    }
    public int GetNextPaymentID(){
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT MAX(PAYMENT_ID) FROM GRP_PAYMENT_DETAILS",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                id = cur.getInt(0);
            }
        }
        id++;
        return  id;
    }
    public ArrayList<Customers> GetCustomerOnGroupList(int grpId){
        ArrayList<Customers> custList = new ArrayList<Customers>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT CUSTOMER_ID FROM CUST_GR WHERE GR_ID="+grpId,null);
        ArrayList<Integer> cuIDList = new ArrayList<Integer>();
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                cuIDList.add(cur.getInt(0));
            }
        }
        ArrayList<Customers> allCustomer = GetCustomers();
        for(Customers c: allCustomer){
            int tem = c.getCustomerID();
            if(cuIDList.contains(tem)){
                custList.add(c);
            }
        }
        return  custList;
    }
    public ArrayList<Groups> GetGroups(){
        ArrayList<Groups> groupList = new ArrayList<Groups>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM GROUPS",null);
         if(cur.getCount()>0){
            while (cur.moveToNext()){
                Groups gr = new Groups();
                gr.setGroupID(cur.getInt(0));
                gr.setGroupName(cur.getString(1));
                gr.setAmount(cur.getDouble(2));
                gr.setMaxCount(cur.getInt(3));
                gr.setInterst(cur.getDouble(4));
                gr.setTenure(cur.getInt(5));
                gr.setCustomerOnGrp(GetCustomerOnGroupList(gr.getGroupID()));
                groupList.add(gr);
            }
         }
        return groupList;
    }
    public void Insert_Groups(Groups gr){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put("GR_ID",gr.getGroupID());
        cont.put("GR_NAME",gr.getGroupName());
        cont.put("AMOUNT",gr.getAmount());
        cont.put("MAX_CUST",gr.getMaxCount());
        cont.put("INTEREST",gr.getInterst());
        cont.put("TENURE",gr.getTenure());
        db.insert("GROUPS",null,cont);
        for(int i=0;i<gr.getCustomerOnGrp().size();i++){
            int cid = gr.getCustomerOnGrp().get(i).getCustomerID();
            ContentValues c = new ContentValues();
            c.put("GR_ID",gr.getGroupID());
            c.put("CUSTOMER_ID",cid);
            db.insert("CUST_GR",null,c);
        }
    }
    public void Insert_Customers(Customers cus){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put("CUSTOMER_ID",cus.getCustomerID());
        cont.put("CUSTOMER_NAME",cus.getCustomerName());
        cont.put("MOBILE_NUMBER",cus.getMobileNumber());
        cont.put("ID_PROOF",cus.getID_Proof());
        db.insert("CUSTOMERS",null,cont);
    }
    public void  Insert_Payment_Details(Grp_Payment_Details grp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put("PAYMENT_ID",grp.getPayment_ID());
        cont.put("PAYMENT_DATE",grp.getPayment_Date_str());
        cont.put("CUSTOMER_ID",grp.getCustomer_ID());
        cont.put("GR_ID",grp.getGroupID());
        cont.put("PAID_AMT",grp.getPaid_Amt());
        cont.put("RECEIVED_BY",grp.getReceivedBy());
        cont.put("BALANCE_AMT",grp.getBalance_Amt());
        cont.put("PAYMENT_TYPE",grp.getPayment_Type());
        cont.put("SYNC_STS",grp.getSyncSts());
        db.insert("GRP_PAYMENT_DETAILS",null,cont);
    }
    public ArrayList<Customers> GetCustomers(){
        ArrayList<Customers> custList = new ArrayList<Customers>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM CUSTOMERS",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                Customers c = new Customers();
                c.setCustomerID(cur.getInt(0));
                c.setCustomerName(cur.getString(1));
                c.setMobileNumber(cur.getString(2));
                c.setAddress(cur.getString(3));
                c.setID_Proof(cur.getString(4));
                custList.add(c);
            }
        }
        return custList;
    }
    public ArrayList<Grp_Payment_Details> GetPaymentDetails(){
        ArrayList<Grp_Payment_Details> paymentDtList = new ArrayList<Grp_Payment_Details>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM GRP_PAYMENT_DETAILS",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                Grp_Payment_Details p = new Grp_Payment_Details();
                p.setPayment_ID(cur.getInt(0));
                p.setPayment_Date_str(cur.getString(1));
                p.setCustomer_ID(cur.getInt(2));
                p.setGroupID(cur.getInt(3));
                p.setPaid_Amt(cur.getDouble(4));
                p.setReceivedBy(cur.getString(5));
                p.setBalance_Amt(cur.getDouble(6));
                p.setPayment_Type(cur.getString(7));
                p.setSyncSts(cur.getInt(8));
                paymentDtList.add(p);
            }
        }
        return paymentDtList;
    }
    public ArrayList<Grp_Payment_Details> GetAllPaymentDetails(){
        ArrayList<Grp_Payment_Details> paymentDtList = new ArrayList<Grp_Payment_Details>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM GRP_PAYMENT_DETAILS WHERE PAYMENT_TYPE='NGX' AND SYNC_STS=0",null);
        if(cur.getCount()>0){
            while (cur.moveToNext()){
                Grp_Payment_Details p = new Grp_Payment_Details();
                p.setPayment_ID(cur.getInt(0));
                p.setPayment_Date_str(cur.getString(1));
                p.setCustomer_ID(cur.getInt(2));
                p.setGroupID(cur.getInt(3));
                p.setPaid_Amt(cur.getDouble(4));
                p.setReceivedBy(cur.getString(5));
                p.setBalance_Amt(cur.getDouble(6));
                p.setPayment_Type(cur.getString(7));
                p.setSyncSts(cur.getInt(8));
                paymentDtList.add(p);
            }
        }
        return paymentDtList;
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
