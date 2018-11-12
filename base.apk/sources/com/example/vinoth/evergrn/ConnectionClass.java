package com.example.vinoth.evergrn;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionClass {
    String classs = "net.sourceforge.jtds.jdbc.Driver";

    @SuppressLint({"NewApi"})
    public Connection CONN(String str, String str2, String str3, String str4) {
        StrictMode.setThreadPolicy(new Builder().permitAll().build());
        try {
            Class.forName(this.classs);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("jdbc:jtds:sqlserver://");
            stringBuilder.append(str);
            stringBuilder.append(";databaseName=");
            stringBuilder.append(str2);
            stringBuilder.append(";user=");
            stringBuilder.append(str3);
            stringBuilder.append(";password=");
            stringBuilder.append(str4);
            stringBuilder.append(";");
            return DriverManager.getConnection(stringBuilder.toString());
        } catch (String str5) {
            Log.e("ERRO", str5.getMessage());
            return null;
        } catch (String str52) {
            Log.e("ERRO", str52.getMessage());
            return null;
        } catch (String str522) {
            Log.e("ERRO", str522.getMessage());
            return null;
        }
    }
}
