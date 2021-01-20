package com.ngx.mp100sdk;

import android.util.Log;

public class DebugLog {

    /* renamed from: a */
    private static boolean f62a = false;

    /* renamed from: b */
    private static char[] f63b = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte b) {
        byte b2 = b & 255;
        return new String(new char[]{f63b[b2 >>> 4], f63b[b2 & 15], ' '});
    }

    public static String bytesToHex(byte[] bArr) {
        char[] cArr = new char[(bArr.length * 3)];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i] & 255;
            cArr[i * 3] = f63b[b >>> 4];
            cArr[(i * 3) + 1] = f63b[b & 15];
            cArr[(i * 3) + 2] = ' ';
        }
        return new String(cArr);
    }

    public static boolean isDebugMode() {
        return f62a;
    }

    public static void logException(Exception exc) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Log.d(stackTrace[3].getClassName(), stackTrace[3].getMethodName() + " --- " + exc.toString());
        Log.e(stackTrace[3].getMethodName() + " --- ", exc.toString(), exc);
    }

    public static void logException(String str, Exception exc) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Log.d(stackTrace[3].getClassName(), stackTrace[3].getMethodName() + " --- " + exc.toString());
        Log.e(stackTrace[3].getMethodName() + " --- ", str, exc);
    }

    public static void logTrace() {
        if (f62a) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            Log.d(stackTrace[3].getClassName(), stackTrace[3].getMethodName());
        }
    }

    public static void logTrace(String str) {
        if (f62a) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            Log.d(stackTrace[3].getClassName(), stackTrace[3].getMethodName() + " --- " + str);
        }
    }

    public static void setDebugMode(boolean z) {
        f62a = z;
    }
}
