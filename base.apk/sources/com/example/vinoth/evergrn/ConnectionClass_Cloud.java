package com.example.vinoth.evergrn;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass_Cloud {
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "VGSTDAT";
    String ip = "VGSTDAT.mssql.somee.com";
    String password = "kw4ofqw2zd";
    String un = "vino_kmr92_SQLLogin_1";

    @SuppressLint({"NewApi"})
    public Connection CONN() {
        StrictMode.setThreadPolicy(new Builder().permitAll().build());
        try {
            Class.forName(this.classs);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("jdbc:jtds:sqlserver://");
            stringBuilder.append(this.ip);
            stringBuilder.append(";databaseName=");
            stringBuilder.append(this.db);
            stringBuilder.append(";user=");
            stringBuilder.append(this.un);
            stringBuilder.append(";password=");
            stringBuilder.append(this.password);
            stringBuilder.append(";");
            return DriverManager.getConnection(stringBuilder.toString());
        } catch (SQLException e) {
            Log.e("ERROR", e.getMessage());
            return null;
        } catch (ClassNotFoundException e2) {
            Log.e("ERROR", e2.getMessage());
            return null;
        } catch (Exception e3) {
            Log.e("ERROR", e3.getMessage());
            return null;
        }
    }
}
