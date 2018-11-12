package net.sourceforge.jtds.jdbc;

import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import java.sql.Connection;
import java.sql.SQLException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import net.sourceforge.jtds.jdbcx.JtdsXid;
import net.sourceforge.jtds.util.Logger;

public class XASupport {
    private static final String TM_ID = "TM=JTDS,RmRecoveryGuid=434CDE1A-F747-4942-9584-04937455CAB4";
    private static final int XA_CLOSE = 2;
    private static final int XA_COMMIT = 7;
    private static final int XA_COMPLETE = 10;
    private static final int XA_END = 4;
    private static final int XA_FORGET = 9;
    private static final int XA_OPEN = 1;
    private static final int XA_PREPARE = 6;
    private static final int XA_RECOVER = 8;
    private static final int XA_RMID = 1;
    private static final int XA_ROLLBACK = 5;
    private static final int XA_START = 3;
    private static final int XA_TRACE = 0;

    public static int xa_open(Connection connection) throws SQLException {
        ConnectionJDBC2 connectionJDBC2 = (ConnectionJDBC2) connection;
        if (connectionJDBC2.isXaEmulation()) {
            Logger.println("xa_open: emulating distributed transaction support");
            if (connectionJDBC2.getXid() != null) {
                throw new SQLException(Messages.get("error.xasupport.activetran", (Object) "xa_open"), "HY000");
            }
            connectionJDBC2.setXaState(1);
            return 0;
        }
        if (connectionJDBC2.getServerType() == 1) {
            if (connectionJDBC2.getTdsVersion() >= 4) {
                Logger.println("xa_open: Using SQL2000 MSDTC to support distributed transactions");
                int[] iArr = new int[5];
                iArr[1] = 1;
                iArr[2] = 0;
                iArr[3] = 1;
                iArr[4] = 0;
                connection = connectionJDBC2.sendXaPacket(iArr, TM_ID.getBytes());
                if (!(iArr[0] != 0 || connection == null || connection[0] == null)) {
                    if (connection[0].length == 4) {
                        return ((connection[0][3] & 255) << 24) | (((connection[0][0] & 255) | ((connection[0][1] & 255) << 8)) | ((connection[0][2] & 255) << 16));
                    }
                }
                throw new SQLException(Messages.get("error.xasupport.badopen"), "HY000");
            }
        }
        throw new SQLException(Messages.get("error.xasupport.nodist"), "HY000");
    }

    public static void xa_close(Connection connection, int i) throws SQLException {
        ConnectionJDBC2 connectionJDBC2 = (ConnectionJDBC2) connection;
        if (connectionJDBC2.isXaEmulation()) {
            connectionJDBC2.setXaState(0);
            if (connectionJDBC2.getXid() != 0) {
                connectionJDBC2.setXid(0);
                try {
                    connectionJDBC2.rollback();
                } catch (int i2) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("xa_close: rollback() returned ");
                    stringBuffer.append(i2);
                    Logger.println(stringBuffer.toString());
                }
                try {
                    connectionJDBC2.setAutoCommit(true);
                } catch (Connection connection2) {
                    i2 = new StringBuffer();
                    i2.append("xa_close: setAutoCommit() returned ");
                    i2.append(connection2);
                    Logger.println(i2.toString());
                }
                throw new SQLException(Messages.get("error.xasupport.activetran", (Object) "xa_close"), "HY000");
            }
            return;
        }
        int[] iArr = new int[5];
        iArr[1] = 2;
        iArr[2] = i2;
        iArr[3] = 1;
        iArr[4] = 0;
        connectionJDBC2.sendXaPacket(iArr, TM_ID.getBytes());
    }

    public static void xa_start(java.sql.Connection r5, int r6, javax.transaction.xa.Xid r7, int r8) throws javax.transaction.xa.XAException {
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
        r0 = r5;
        r0 = (net.sourceforge.jtds.jdbc.ConnectionJDBC2) r0;
        r1 = r0.isXaEmulation();
        r2 = 3;
        r3 = 0;
        if (r1 == 0) goto L_0x0045;
    L_0x000b:
        r6 = new net.sourceforge.jtds.jdbcx.JtdsXid;
        r6.<init>(r7);
        r7 = r0.getXaState();
        r1 = -6;
        if (r7 != 0) goto L_0x001a;
    L_0x0017:
        raiseXAException(r1);
    L_0x001a:
        r7 = r0.getXid();
        r7 = (net.sourceforge.jtds.jdbcx.JtdsXid) r7;
        if (r7 == 0) goto L_0x0030;
    L_0x0022:
        r7 = r7.equals(r6);
        if (r7 == 0) goto L_0x002d;
    L_0x0028:
        r7 = -8;
        raiseXAException(r7);
        goto L_0x0030;
    L_0x002d:
        raiseXAException(r1);
    L_0x0030:
        if (r8 == 0) goto L_0x0036;
    L_0x0032:
        r7 = -5;
        raiseXAException(r7);
    L_0x0036:
        r5.setAutoCommit(r3);	 Catch:{ SQLException -> 0x003a }
        goto L_0x003e;
    L_0x003a:
        r5 = -3;
        raiseXAException(r5);
    L_0x003e:
        r0.setXid(r6);
        r0.setXaState(r2);
        return;
    L_0x0045:
        r0 = 5;
        r0 = new int[r0];
        r1 = 1;
        r0[r1] = r2;
        r4 = 2;
        r0[r4] = r6;
        r0[r2] = r1;
        r6 = 4;
        r0[r6] = r8;
        r6 = r5;	 Catch:{ SQLException -> 0x006c }
        r6 = (net.sourceforge.jtds.jdbc.ConnectionJDBC2) r6;	 Catch:{ SQLException -> 0x006c }
        r7 = toBytesXid(r7);	 Catch:{ SQLException -> 0x006c }
        r6 = r6.sendXaPacket(r0, r7);	 Catch:{ SQLException -> 0x006c }
        r7 = r0[r3];	 Catch:{ SQLException -> 0x006c }
        if (r7 != 0) goto L_0x0070;	 Catch:{ SQLException -> 0x006c }
    L_0x0062:
        if (r6 == 0) goto L_0x0070;	 Catch:{ SQLException -> 0x006c }
    L_0x0064:
        r5 = (net.sourceforge.jtds.jdbc.ConnectionJDBC2) r5;	 Catch:{ SQLException -> 0x006c }
        r6 = r6[r3];	 Catch:{ SQLException -> 0x006c }
        r5.enlistConnection(r6);	 Catch:{ SQLException -> 0x006c }
        goto L_0x0070;
    L_0x006c:
        r5 = move-exception;
        raiseXAException(r5);
    L_0x0070:
        r5 = r0[r3];
        if (r5 == 0) goto L_0x0079;
    L_0x0074:
        r5 = r0[r3];
        raiseXAException(r5);
    L_0x0079:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.XASupport.xa_start(java.sql.Connection, int, javax.transaction.xa.Xid, int):void");
    }

    public static void xa_end(Connection connection, int i, Xid xid, int i2) throws XAException {
        ConnectionJDBC2 connectionJDBC2 = (ConnectionJDBC2) connection;
        if (connectionJDBC2.isXaEmulation()) {
            connection = new JtdsXid(xid);
            if (connectionJDBC2.getXaState() != 3) {
                raiseXAException(-6);
            }
            JtdsXid jtdsXid = (JtdsXid) connectionJDBC2.getXid();
            if (jtdsXid == null || jtdsXid.equals(connection) == null) {
                raiseXAException(-4);
            }
            if (!(i2 == 67108864 || i2 == 536870912)) {
                raiseXAException(-5);
            }
            connectionJDBC2.setXaState(4);
            return;
        }
        int[] iArr = new int[5];
        iArr[1] = 4;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = i2;
        try {
            ((ConnectionJDBC2) connection).sendXaPacket(iArr, toBytesXid(xid));
            ((ConnectionJDBC2) connection).enlistConnection(0);
        } catch (SQLException e) {
            raiseXAException(e);
        }
        if (iArr[0] != 0) {
            raiseXAException(iArr[0]);
        }
    }

    public static int xa_prepare(Connection connection, int i, Xid xid) throws XAException {
        ConnectionJDBC2 connectionJDBC2 = (ConnectionJDBC2) connection;
        if (connectionJDBC2.isXaEmulation()) {
            connection = new JtdsXid(xid);
            if (connectionJDBC2.getXaState() != 4) {
                raiseXAException(-6);
            }
            JtdsXid jtdsXid = (JtdsXid) connectionJDBC2.getXid();
            if (jtdsXid == null || jtdsXid.equals(connection) == null) {
                raiseXAException(-4);
            }
            connectionJDBC2.setXaState(6);
            Logger.println("xa_prepare: Warning: Two phase commit not available in XA emulation mode.");
            return 0;
        }
        int[] iArr = new int[5];
        iArr[1] = 6;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = 0;
        try {
            ((ConnectionJDBC2) connection).sendXaPacket(iArr, toBytesXid(xid));
        } catch (SQLException e) {
            raiseXAException(e);
        }
        if (!(iArr[0] == null || iArr[0] == 3)) {
            raiseXAException(iArr[0]);
        }
        return iArr[0];
    }

    public static void xa_commit(java.sql.Connection r5, int r6, javax.transaction.xa.Xid r7, boolean r8) throws javax.transaction.xa.XAException {
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
        r0 = r5;
        r0 = (net.sourceforge.jtds.jdbc.ConnectionJDBC2) r0;
        r1 = r0.isXaEmulation();
        r2 = 4;
        r3 = 0;
        r4 = 1;
        if (r1 == 0) goto L_0x0090;
    L_0x000c:
        r6 = r0.getXid();
        r6 = (net.sourceforge.jtds.jdbcx.JtdsXid) r6;
        if (r6 != 0) goto L_0x001d;
    L_0x0014:
        r5.setAutoCommit(r3);	 Catch:{ SQLException -> 0x0018 }
        goto L_0x003d;
    L_0x0018:
        r5 = -3;
        raiseXAException(r5);
        goto L_0x003d;
    L_0x001d:
        r5 = r0.getXaState();
        if (r5 == r2) goto L_0x002e;
    L_0x0023:
        r5 = r0.getXaState();
        r8 = 6;
        if (r5 == r8) goto L_0x002e;
    L_0x002a:
        r5 = -6;
        raiseXAException(r5);
    L_0x002e:
        r5 = new net.sourceforge.jtds.jdbcx.JtdsXid;
        r5.<init>(r7);
        r5 = r6.equals(r5);
        if (r5 != 0) goto L_0x003d;
    L_0x0039:
        r5 = -4;
        raiseXAException(r5);
    L_0x003d:
        r5 = 0;
        r0.setXid(r5);
        r0.commit();	 Catch:{ SQLException -> 0x0063 }
        r0.setAutoCommit(r4);	 Catch:{ SQLException -> 0x0048 }
        goto L_0x005d;
    L_0x0048:
        r5 = move-exception;
        r6 = new java.lang.StringBuffer;
        r6.<init>();
    L_0x004e:
        r7 = "xa_close: setAutoCommit() returned ";
        r6.append(r7);
        r6.append(r5);
        r5 = r6.toString();
        net.sourceforge.jtds.util.Logger.println(r5);
    L_0x005d:
        r0.setXaState(r4);
        goto L_0x0072;
    L_0x0061:
        r5 = move-exception;
        goto L_0x0073;
    L_0x0063:
        r5 = move-exception;
        raiseXAException(r5);	 Catch:{ all -> 0x0061 }
        r0.setAutoCommit(r4);	 Catch:{ SQLException -> 0x006b }
        goto L_0x005d;
    L_0x006b:
        r5 = move-exception;
        r6 = new java.lang.StringBuffer;
        r6.<init>();
        goto L_0x004e;
    L_0x0072:
        return;
    L_0x0073:
        r0.setAutoCommit(r4);	 Catch:{ SQLException -> 0x0077 }
        goto L_0x008c;
    L_0x0077:
        r6 = move-exception;
        r7 = new java.lang.StringBuffer;
        r7.<init>();
        r8 = "xa_close: setAutoCommit() returned ";
        r7.append(r8);
        r7.append(r6);
        r6 = r7.toString();
        net.sourceforge.jtds.util.Logger.println(r6);
    L_0x008c:
        r0.setXaState(r4);
        throw r5;
    L_0x0090:
        r0 = 5;
        r0 = new int[r0];
        r1 = 7;
        r0[r4] = r1;
        r1 = 2;
        r0[r1] = r6;
        r6 = 3;
        r0[r6] = r4;
        if (r8 == 0) goto L_0x00a1;
    L_0x009e:
        r6 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x00a2;
    L_0x00a1:
        r6 = 0;
    L_0x00a2:
        r0[r2] = r6;
        r5 = (net.sourceforge.jtds.jdbc.ConnectionJDBC2) r5;	 Catch:{ SQLException -> 0x00ae }
        r6 = toBytesXid(r7);	 Catch:{ SQLException -> 0x00ae }
        r5.sendXaPacket(r0, r6);	 Catch:{ SQLException -> 0x00ae }
        goto L_0x00b2;
    L_0x00ae:
        r5 = move-exception;
        raiseXAException(r5);
    L_0x00b2:
        r5 = r0[r3];
        if (r5 == 0) goto L_0x00bb;
    L_0x00b6:
        r5 = r0[r3];
        raiseXAException(r5);
    L_0x00bb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.XASupport.xa_commit(java.sql.Connection, int, javax.transaction.xa.Xid, boolean):void");
    }

    public static void xa_rollback(Connection connection, int i, Xid xid) throws XAException {
        ConnectionJDBC2 connectionJDBC2 = (ConnectionJDBC2) connection;
        if (connectionJDBC2.isXaEmulation()) {
            connection = new JtdsXid(xid);
            if (!(connectionJDBC2.getXaState() == 4 || connectionJDBC2.getXaState() == 6)) {
                raiseXAException(-6);
            }
            JtdsXid jtdsXid = (JtdsXid) connectionJDBC2.getXid();
            if (jtdsXid == null || jtdsXid.equals(connection) == null) {
                raiseXAException(-4);
            }
            connectionJDBC2.setXid(null);
            try {
                connectionJDBC2.rollback();
                try {
                    connectionJDBC2.setAutoCommit(true);
                } catch (SQLException e) {
                    connection = e;
                    i = new StringBuffer();
                    i.append("xa_close: setAutoCommit() returned ");
                    i.append(connection);
                    Logger.println(i.toString());
                    connectionJDBC2.setXaState(1);
                    return;
                }
            } catch (SQLException e2) {
                raiseXAException(e2);
                try {
                    connectionJDBC2.setAutoCommit(true);
                } catch (SQLException e3) {
                    connection = e3;
                    i = new StringBuffer();
                    i.append("xa_close: setAutoCommit() returned ");
                    i.append(connection);
                    Logger.println(i.toString());
                    connectionJDBC2.setXaState(1);
                    return;
                }
            } catch (Connection connection2) {
                try {
                    connectionJDBC2.setAutoCommit(true);
                } catch (int i2) {
                    xid = new StringBuffer();
                    xid.append("xa_close: setAutoCommit() returned ");
                    xid.append(i2);
                    Logger.println(xid.toString());
                }
                connectionJDBC2.setXaState(1);
                throw connection2;
            }
            connectionJDBC2.setXaState(1);
            return;
        }
        int[] iArr = new int[5];
        iArr[1] = 5;
        iArr[2] = i2;
        iArr[3] = 1;
        iArr[4] = 0;
        try {
            ((ConnectionJDBC2) connection2).sendXaPacket(iArr, toBytesXid(xid));
        } catch (SQLException e22) {
            raiseXAException(e22);
        }
        if (iArr[0] != null) {
            raiseXAException(iArr[0]);
        }
    }

    public static Xid[] xa_recover(Connection connection, int i, int i2) throws XAException {
        SQLException e;
        if (((ConnectionJDBC2) connection).isXaEmulation()) {
            if (!(i2 == 16777216 || i2 == 8388608 || i2 == 25165824 || i2 == 0)) {
                raiseXAException(-5);
            }
            return new JtdsXid[0];
        }
        int[] iArr = new int[5];
        iArr[1] = 8;
        iArr[2] = i;
        iArr[3] = 1;
        iArr[4] = 0;
        if (i2 != 16777216) {
            return new JtdsXid[0];
        }
        i = 0;
        try {
            connection = ((ConnectionJDBC2) connection).sendXaPacket(iArr, null);
            if (iArr[0] >= 0) {
                i2 = connection.length;
                JtdsXid[] jtdsXidArr = new JtdsXid[i2];
                i = 0;
                while (i < i2) {
                    try {
                        jtdsXidArr[i] = new JtdsXid(connection[i], 0);
                        i++;
                    } catch (SQLException e2) {
                        e = e2;
                        i = jtdsXidArr;
                    }
                }
                i = jtdsXidArr;
            }
        } catch (SQLException e3) {
            e = e3;
            raiseXAException(e);
            if (iArr[0] < null) {
                raiseXAException(iArr[0]);
            }
            if (i == 0) {
                i = new JtdsXid[0];
            }
            return i;
        }
        if (iArr[0] < null) {
            raiseXAException(iArr[0]);
        }
        if (i == 0) {
            i = new JtdsXid[0];
        }
        return i;
    }

    public static void xa_forget(Connection connection, int i, Xid xid) throws XAException {
        ConnectionJDBC2 connectionJDBC2 = (ConnectionJDBC2) connection;
        if (connectionJDBC2.isXaEmulation()) {
            connection = new JtdsXid(xid);
            JtdsXid jtdsXid = (JtdsXid) connectionJDBC2.getXid();
            if (jtdsXid != null && jtdsXid.equals(connection) == null) {
                raiseXAException(-4);
            }
            if (!(connectionJDBC2.getXaState() == 4 || connectionJDBC2.getXaState() == 6)) {
                raiseXAException(-6);
            }
            connectionJDBC2.setXid(null);
            try {
                connectionJDBC2.rollback();
                try {
                    connectionJDBC2.setAutoCommit(true);
                } catch (SQLException e) {
                    connection = e;
                    i = new StringBuffer();
                    i.append("xa_close: setAutoCommit() returned ");
                    i.append(connection);
                    Logger.println(i.toString());
                    connectionJDBC2.setXaState(1);
                    return;
                }
            } catch (SQLException e2) {
                raiseXAException(e2);
                try {
                    connectionJDBC2.setAutoCommit(true);
                } catch (SQLException e3) {
                    connection = e3;
                    i = new StringBuffer();
                    i.append("xa_close: setAutoCommit() returned ");
                    i.append(connection);
                    Logger.println(i.toString());
                    connectionJDBC2.setXaState(1);
                    return;
                }
            } catch (Connection connection2) {
                try {
                    connectionJDBC2.setAutoCommit(true);
                } catch (int i2) {
                    xid = new StringBuffer();
                    xid.append("xa_close: setAutoCommit() returned ");
                    xid.append(i2);
                    Logger.println(xid.toString());
                }
                connectionJDBC2.setXaState(1);
                throw connection2;
            }
            connectionJDBC2.setXaState(1);
            return;
        }
        int[] iArr = new int[5];
        iArr[1] = 9;
        iArr[2] = i2;
        iArr[3] = 1;
        iArr[4] = 0;
        try {
            ((ConnectionJDBC2) connection2).sendXaPacket(iArr, toBytesXid(xid));
        } catch (SQLException e22) {
            raiseXAException(e22);
        }
        if (iArr[0] != null) {
            raiseXAException(iArr[0]);
        }
    }

    public static void raiseXAException(SQLException sQLException) throws XAException {
        XAException xAException = new XAException(sQLException.getMessage());
        xAException.errorCode = -7;
        sQLException = new StringBuffer();
        sQLException.append("XAException: ");
        sQLException.append(xAException.getMessage());
        Logger.println(sQLException.toString());
        throw xAException;
    }

    public static void raiseXAException(int i) throws XAException {
        String str = "xaerunknown";
        switch (i) {
            case -9:
                str = "xaeroutside";
                break;
            case -8:
                str = "xaerdupid";
                break;
            case -7:
                str = "xaerrmfail";
                break;
            case -6:
                str = "xaerproto";
                break;
            case -5:
                str = "xaerinval";
                break;
            case -4:
                str = "xaernota";
                break;
            case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                str = "xaerrmerr";
                break;
            case -2:
                str = "xaerasync";
                break;
            default:
                switch (i) {
                    case 3:
                        str = "xardonly";
                        break;
                    case 4:
                        str = "xaretry";
                        break;
                    case 5:
                        str = "xaheurmix";
                        break;
                    case 6:
                        str = "xaheurrb";
                        break;
                    case 7:
                        str = "xaheurcom";
                        break;
                    case 8:
                        str = "xaheurhaz";
                        break;
                    case 9:
                        str = "xanomigrate";
                        break;
                    default:
                        switch (i) {
                            case 100:
                                str = "xarbrollback";
                                break;
                            case 101:
                                str = "xarbcommfail";
                                break;
                            case 102:
                                str = "xarbdeadlock";
                                break;
                            case 103:
                                str = "xarbintegrity";
                                break;
                            case 104:
                                str = "xarbother";
                                break;
                            case 105:
                                str = "xarbproto";
                                break;
                            case 106:
                                str = "xarbtimeout";
                                break;
                            case 107:
                                str = "xarbtransient";
                                break;
                            default:
                                break;
                        }
                }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("error.xaexception.");
        stringBuffer.append(str);
        XAException xAException = new XAException(Messages.get(stringBuffer.toString()));
        xAException.errorCode = i;
        i = new StringBuffer();
        i.append("XAException: ");
        i.append(xAException.getMessage());
        Logger.println(i.toString());
        throw xAException;
    }

    private static byte[] toBytesXid(Xid xid) {
        Object obj = new byte[((xid.getGlobalTransactionId().length + 12) + xid.getBranchQualifier().length)];
        int formatId = xid.getFormatId();
        obj[0] = (byte) formatId;
        obj[1] = (byte) (formatId >> 8);
        obj[2] = (byte) (formatId >> 16);
        obj[3] = (byte) (formatId >> 24);
        obj[4] = (byte) xid.getGlobalTransactionId().length;
        obj[8] = (byte) xid.getBranchQualifier().length;
        System.arraycopy(xid.getGlobalTransactionId(), 0, obj, 12, obj[4]);
        System.arraycopy(xid.getBranchQualifier(), 0, obj, obj[4] + 12, obj[8]);
        return obj;
    }

    private XASupport() {
    }
}
