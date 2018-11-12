package net.sourceforge.jtds.jdbcx;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import net.sourceforge.jtds.jdbc.XASupport;

public class JtdsXAConnection extends PooledConnection implements XAConnection {
    private final JtdsDataSource dataSource;
    private final XAResource resource;
    private final int xaConnectionId;

    public JtdsXAConnection(JtdsDataSource jtdsDataSource, Connection connection) throws SQLException {
        super(connection);
        this.resource = new JtdsXAResource(this, connection);
        this.dataSource = jtdsDataSource;
        this.xaConnectionId = XASupport.xa_open(connection);
    }

    int getXAConnectionID() {
        return this.xaConnectionId;
    }

    public XAResource getXAResource() throws SQLException {
        return this.resource;
    }

    public synchronized void close() throws java.sql.SQLException {
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
        r2 = this;
        monitor-enter(r2);
        r0 = r2.connection;	 Catch:{ SQLException -> 0x000b }
        r1 = r2.xaConnectionId;	 Catch:{ SQLException -> 0x000b }
        net.sourceforge.jtds.jdbc.XASupport.xa_close(r0, r1);	 Catch:{ SQLException -> 0x000b }
        goto L_0x000b;
    L_0x0009:
        r0 = move-exception;
        goto L_0x0010;
    L_0x000b:
        super.close();	 Catch:{ all -> 0x0009 }
        monitor-exit(r2);
        return;
    L_0x0010:
        monitor-exit(r2);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbcx.JtdsXAConnection.close():void");
    }

    protected JtdsDataSource getXADataSource() {
        return this.dataSource;
    }
}
