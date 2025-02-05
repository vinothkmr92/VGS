package com.example.inventoryslip;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;


public class ConnectionClass {
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String ConnURL = "";
    private String server = "";
    private String db="";
    private String userName = "";
    private String Password = "";
    public ConnectionClass(String _server, String _db, String _userName, String _Password){
        server = _server;
        db = _db;
        userName = _userName;
        Password = _Password;
    }


    @SuppressLint("NewApi")
    public Connection CONN() {
        Connection conn = null;
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Log.e("MSG","JDBC Class registration start.");
            Class.forName(classs);
            Log.e("MSG","JDBC Class registered successfully");
            Log.e("MSG","Server: "+server);
            ConnURL = String.format("jdbc:jtds:sqlserver://%s;databaseName=%s;user=%s;password=%s",server,db,userName,Password);
            conn = DriverManager.getConnection(ConnURL);
        }
        catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
        return conn;
    }
}
