package net.sourceforge.jtds.jdbcx;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.ConnectionEventListener;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbcx.proxy.ConnectionProxy;

public class PooledConnection implements javax.sql.PooledConnection {
    protected Connection connection;
    private ArrayList listeners = new ArrayList();

    public PooledConnection(Connection connection) {
        this.connection = connection;
    }

    public synchronized void addConnectionEventListener(ConnectionEventListener connectionEventListener) {
        this.listeners = (ArrayList) this.listeners.clone();
        this.listeners.add(connectionEventListener);
    }

    public synchronized void close() throws SQLException {
        this.connection.close();
        this.connection = null;
    }

    public synchronized void fireConnectionEvent(boolean r4, java.sql.SQLException r5) {
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
        r3 = this;
        monitor-enter(r3);
        r0 = r3.listeners;	 Catch:{ all -> 0x0038 }
        r0 = r0.size();	 Catch:{ all -> 0x0038 }
        if (r0 <= 0) goto L_0x0036;	 Catch:{ all -> 0x0038 }
    L_0x0009:
        r0 = new javax.sql.ConnectionEvent;	 Catch:{ all -> 0x0038 }
        r0.<init>(r3, r5);	 Catch:{ all -> 0x0038 }
        r5 = r3.listeners;	 Catch:{ all -> 0x0038 }
        r5 = r5.iterator();	 Catch:{ all -> 0x0038 }
    L_0x0014:
        r1 = r5.hasNext();	 Catch:{ all -> 0x0038 }
        if (r1 == 0) goto L_0x0036;	 Catch:{ all -> 0x0038 }
    L_0x001a:
        r1 = r5.next();	 Catch:{ all -> 0x0038 }
        r1 = (javax.sql.ConnectionEventListener) r1;	 Catch:{ all -> 0x0038 }
        if (r4 == 0) goto L_0x0026;	 Catch:{ all -> 0x0038 }
    L_0x0022:
        r1.connectionClosed(r0);	 Catch:{ all -> 0x0038 }
        goto L_0x0014;
    L_0x0026:
        r2 = r3.connection;	 Catch:{ SQLException -> 0x0014 }
        if (r2 == 0) goto L_0x0032;	 Catch:{ SQLException -> 0x0014 }
    L_0x002a:
        r2 = r3.connection;	 Catch:{ SQLException -> 0x0014 }
        r2 = r2.isClosed();	 Catch:{ SQLException -> 0x0014 }
        if (r2 == 0) goto L_0x0014;	 Catch:{ SQLException -> 0x0014 }
    L_0x0032:
        r1.connectionErrorOccurred(r0);	 Catch:{ SQLException -> 0x0014 }
        goto L_0x0014;
    L_0x0036:
        monitor-exit(r3);
        return;
    L_0x0038:
        r4 = move-exception;
        monitor-exit(r3);
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbcx.PooledConnection.fireConnectionEvent(boolean, java.sql.SQLException):void");
    }

    public synchronized Connection getConnection() throws SQLException {
        if (this.connection == null) {
            fireConnectionEvent(false, new SQLException(Messages.get("error.jdbcx.conclosed"), "08003"));
            return null;
        }
        return new ConnectionProxy(this, this.connection);
    }

    public synchronized void removeConnectionEventListener(ConnectionEventListener connectionEventListener) {
        this.listeners = (ArrayList) this.listeners.clone();
        this.listeners.remove(connectionEventListener);
    }
}
