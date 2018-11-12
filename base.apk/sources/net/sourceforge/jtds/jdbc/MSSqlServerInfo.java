package net.sourceforge.jtds.jdbc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;
import net.sourceforge.jtds.util.Logger;

public class MSSqlServerInfo {
    private int numRetries = 3;
    private String[] serverInfoStrings;
    private int timeout = 2000;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MSSqlServerInfo(String str) throws SQLException {
        Exception e;
        DatagramSocket datagramSocket;
        try {
            InetAddress byName = InetAddress.getByName(str);
            datagramSocket = new DatagramSocket();
            try {
                byte[] bArr = new byte[1];
                int i = 0;
                bArr[0] = (byte) 2;
                DatagramPacket datagramPacket = new DatagramPacket(bArr, bArr.length, byName, 1434);
                bArr = new byte[4096];
                DatagramPacket datagramPacket2 = new DatagramPacket(bArr, bArr.length);
                datagramSocket.setSoTimeout(this.timeout);
                while (i < this.numRetries) {
                    try {
                        datagramSocket.send(datagramPacket);
                        datagramSocket.receive(datagramPacket2);
                        this.serverInfoStrings = split(extractString(bArr, datagramPacket2.getLength()), 59);
                        if (datagramSocket != null) {
                            datagramSocket.close();
                            return;
                        }
                        return;
                    } catch (Exception e2) {
                        if (Logger.isActive()) {
                            Logger.logException(e2);
                        }
                        i++;
                    }
                }
                if (datagramSocket != null) {
                    datagramSocket.close();
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Exception e4) {
            datagramSocket = null;
            e = e4;
            try {
                if (Logger.isActive()) {
                    Logger.logException(e);
                }
            } catch (Throwable th) {
                str = th;
                if (datagramSocket != null) {
                    datagramSocket.close();
                }
                throw str;
            }
        } catch (Throwable th2) {
            str = th2;
            datagramSocket = null;
            if (datagramSocket != null) {
                datagramSocket.close();
            }
            throw str;
        }
        throw new SQLException(Messages.get("error.msinfo.badinfo", (Object) str), "HY000");
    }

    public int getPortForInstance(java.lang.String r9) throws java.sql.SQLException {
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r8 = this;
        r0 = r8.serverInfoStrings;
        r1 = -1;
        if (r0 != 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        if (r9 == 0) goto L_0x000e;
    L_0x0008:
        r0 = r9.length();
        if (r0 != 0) goto L_0x0010;
    L_0x000e:
        r9 = "MSSQLSERVER";
    L_0x0010:
        r0 = 0;
        r2 = 0;
        r3 = r2;
        r4 = r3;
    L_0x0014:
        r5 = r8.serverInfoStrings;
        r5 = r5.length;
        if (r0 >= r5) goto L_0x0069;
    L_0x0019:
        r5 = r8.serverInfoStrings;
        r5 = r5[r0];
        r5 = r5.length();
        if (r5 != 0) goto L_0x0026;
    L_0x0023:
        r3 = r2;
        r4 = r3;
        goto L_0x0066;
    L_0x0026:
        r5 = r8.serverInfoStrings;
        r5 = r5[r0];
        r6 = "";
        r0 = r0 + 1;
        r7 = r8.serverInfoStrings;
        r7 = r7.length;
        if (r0 >= r7) goto L_0x0037;
    L_0x0033:
        r6 = r8.serverInfoStrings;
        r6 = r6[r0];
    L_0x0037:
        r7 = "InstanceName";
        r7 = r7.equals(r5);
        if (r7 == 0) goto L_0x0040;
    L_0x003f:
        r3 = r6;
    L_0x0040:
        r7 = "tcp";
        r5 = r7.equals(r5);
        if (r5 == 0) goto L_0x0049;
    L_0x0048:
        r4 = r6;
    L_0x0049:
        if (r3 == 0) goto L_0x0066;
    L_0x004b:
        if (r4 == 0) goto L_0x0066;
    L_0x004d:
        r5 = r3.equalsIgnoreCase(r9);
        if (r5 == 0) goto L_0x0066;
    L_0x0053:
        r0 = java.lang.Integer.parseInt(r4);	 Catch:{ NumberFormatException -> 0x0058 }
        return r0;
    L_0x0058:
        r0 = new java.sql.SQLException;
        r1 = "error.msinfo.badport";
        r9 = net.sourceforge.jtds.jdbc.Messages.get(r1, r9);
        r1 = "HY000";
        r0.<init>(r9, r1);
        throw r0;
    L_0x0066:
        r0 = r0 + 1;
        goto L_0x0014;
    L_0x0069:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.MSSqlServerInfo.getPortForInstance(java.lang.String):int");
    }

    private static final String extractString(byte[] bArr, int i) {
        return new String(bArr, 3, i - 3);
    }

    public static String[] split(String str, int i) {
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i3 != -1) {
            i3 = str.indexOf(i, i3 + 1);
            i4++;
        }
        String[] strArr = new String[i4];
        int indexOf = str.indexOf(i);
        i4 = 0;
        while (true) {
            int i5 = i2 + 1;
            strArr[i2] = str.substring(i4, indexOf == -1 ? str.length() : indexOf);
            i4 = indexOf + 1;
            indexOf = str.indexOf(i, i4);
            if (i4 == 0) {
                return strArr;
            }
            i2 = i5;
        }
    }
}
