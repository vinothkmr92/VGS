package net.sourceforge.jtds.util;

import java.io.PrintWriter;
import java.sql.DriverManager;

public class Logger {
    private static final char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static PrintWriter log;

    public static void setLogWriter(PrintWriter printWriter) {
        log = printWriter;
    }

    public static PrintWriter getLogWriter() {
        return log;
    }

    public static boolean isActive() {
        if (log == null) {
            if (DriverManager.getLogWriter() == null) {
                return false;
            }
        }
        return true;
    }

    public static void println(String str) {
        if (log != null) {
            log.println(str);
        } else {
            DriverManager.println(str);
        }
    }

    public static void logPacket(int i, boolean z, byte[] bArr) {
        boolean z2 = ((bArr[2] & 255) << 8) | (bArr[3] & 255);
        StringBuffer stringBuffer = new StringBuffer(80);
        stringBuffer.append("----- Stream #");
        stringBuffer.append(i);
        stringBuffer.append(z ? " read" : " send");
        stringBuffer.append(bArr[1] != 0 ? " last " : " ");
        z = bArr[0];
        if (!z) {
            switch (z) {
                case true:
                    stringBuffer.append("Request packet ");
                    break;
                case true:
                    stringBuffer.append("Login packet ");
                    break;
                case true:
                    stringBuffer.append("RPC packet ");
                    break;
                case true:
                    stringBuffer.append("Reply packet ");
                    break;
                default:
                    switch (z) {
                        case true:
                            stringBuffer.append("XA control packet ");
                            break;
                        case true:
                            stringBuffer.append("TDS5 Request packet ");
                            break;
                        case true:
                            stringBuffer.append("MS Login packet ");
                            break;
                        case true:
                            stringBuffer.append("NTLM Authentication packet ");
                            break;
                        case true:
                            stringBuffer.append("MS Prelogin packet ");
                            break;
                        default:
                            stringBuffer.append("Invalid packet ");
                            break;
                    }
            }
        }
        stringBuffer.append("Cancel packet ");
        println(stringBuffer.toString());
        println("");
        stringBuffer.setLength(0);
        for (z = false; z < z2; z += 16) {
            int i2;
            if (z < true) {
                stringBuffer.append(' ');
            }
            if (z < true) {
                stringBuffer.append(' ');
            }
            if (z < true) {
                stringBuffer.append(' ');
            }
            stringBuffer.append(z);
            stringBuffer.append(':');
            stringBuffer.append(' ');
            int i3 = 0;
            while (i3 < 16) {
                boolean z3;
                int i4;
                boolean z4 = z + i3;
                if (z4 < z2) {
                    int i5 = bArr[z4] & 255;
                    stringBuffer.append(hex[i5 >> 4]);
                    stringBuffer.append(hex[i5 & 15]);
                    stringBuffer.append(' ');
                    i3++;
                } else {
                    while (i3 < 16) {
                        stringBuffer.append("   ");
                        i3++;
                    }
                    stringBuffer.append('|');
                    i2 = 0;
                    while (i2 < 16) {
                        z3 = z + i2;
                        if (z3 >= z2) {
                            i4 = bArr[z3] & 255;
                            if (i4 > 31 || i4 >= 127) {
                                stringBuffer.append(' ');
                            } else {
                                stringBuffer.append((char) i4);
                            }
                            i2++;
                        } else {
                            stringBuffer.append('|');
                            println(stringBuffer.toString());
                            stringBuffer.setLength(0);
                        }
                    }
                    stringBuffer.append('|');
                    println(stringBuffer.toString());
                    stringBuffer.setLength(0);
                }
            }
            while (i3 < 16) {
                stringBuffer.append("   ");
                i3++;
            }
            stringBuffer.append('|');
            i2 = 0;
            while (i2 < 16) {
                z3 = z + i2;
                if (z3 >= z2) {
                    stringBuffer.append('|');
                    println(stringBuffer.toString());
                    stringBuffer.setLength(0);
                } else {
                    i4 = bArr[z3] & 255;
                    if (i4 > 31) {
                    }
                    stringBuffer.append(' ');
                    i2++;
                }
            }
            stringBuffer.append('|');
            println(stringBuffer.toString());
            stringBuffer.setLength(0);
        }
        println("");
    }

    public static void logException(Exception exception) {
        if (log != null) {
            exception.printStackTrace(log);
        } else if (DriverManager.getLogWriter() != null) {
            exception.printStackTrace(DriverManager.getLogWriter());
        }
    }

    public static void setActive(boolean r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        if (r2 == 0) goto L_0x0019;
    L_0x0002:
        r2 = log;
        if (r2 != 0) goto L_0x0019;
    L_0x0006:
        r2 = new java.io.PrintWriter;	 Catch:{ IOException -> 0x0016 }
        r0 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x0016 }
        r1 = "log.out";	 Catch:{ IOException -> 0x0016 }
        r0.<init>(r1);	 Catch:{ IOException -> 0x0016 }
        r1 = 1;	 Catch:{ IOException -> 0x0016 }
        r2.<init>(r0, r1);	 Catch:{ IOException -> 0x0016 }
        log = r2;	 Catch:{ IOException -> 0x0016 }
        goto L_0x0019;
    L_0x0016:
        r2 = 0;
        log = r2;
    L_0x0019:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.util.Logger.setActive(boolean):void");
    }
}
