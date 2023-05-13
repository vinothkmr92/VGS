package com.example.mpos_orders;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionClass {
    String classs = "net.sourceforge.jtds.jdbc.Driver";


    @SuppressLint("NewApi")
    public Connection CONN(String server, String db, String userName, String Password) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Log.e("MSG","JDBC Class registeration start.");
            Class.forName(classs);
            Log.e("MSG","JDBC Class registered sucessfuly");
            Log.e("MSG","Server: "+server);
            //ConnURL = "jdbc:sqlserver://localhost;databaseName=<DATABASE_NAME>;socketFactoryClass=com.google.cloud.sql.sqlserver.SocketFactory;socketFactoryConstructorArg=<INSTANCE_CONNECTION_NAME>;user=<USER_NAME>;password=<PASSWORD>

            ConnURL = "jdbc:jtds:sqlserver://" + server + ";"
                    + "databaseName=" + db + ";user=" + userName + ";password="
                    + Password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERROR", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERROR", e.getMessage());
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
        return conn;
    }
}
