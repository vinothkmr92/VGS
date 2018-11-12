package net.sourceforge.jtds.jdbcx;

import java.sql.Connection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import net.sourceforge.jtds.jdbc.ConnectionJDBC2;
import net.sourceforge.jtds.jdbc.XASupport;
import net.sourceforge.jtds.util.Logger;

public class JtdsXAResource implements XAResource {
    private final Connection connection;
    private final String rmHost;
    private final JtdsXAConnection xaConnection;

    public JtdsXAResource(JtdsXAConnection jtdsXAConnection, Connection connection) {
        this.xaConnection = jtdsXAConnection;
        this.connection = connection;
        this.rmHost = ((ConnectionJDBC2) connection).getRmHost();
        Logger.println("JtdsXAResource created");
    }

    protected JtdsXAConnection getResourceManager() {
        return this.xaConnection;
    }

    protected String getRmHost() {
        return this.rmHost;
    }

    public int getTransactionTimeout() throws XAException {
        Logger.println("XAResource.getTransactionTimeout()");
        return 0;
    }

    public boolean setTransactionTimeout(int i) throws XAException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XAResource.setTransactionTimeout(");
        stringBuffer.append(i);
        stringBuffer.append(41);
        Logger.println(stringBuffer.toString());
        return false;
    }

    public boolean isSameRM(XAResource xAResource) throws XAException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XAResource.isSameRM(");
        stringBuffer.append(xAResource.toString());
        stringBuffer.append(')');
        Logger.println(stringBuffer.toString());
        return (!(xAResource instanceof JtdsXAResource) || ((JtdsXAResource) xAResource).getRmHost().equals(this.rmHost) == null) ? null : true;
    }

    public Xid[] recover(int i) throws XAException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XAResource.recover(");
        stringBuffer.append(i);
        stringBuffer.append(')');
        Logger.println(stringBuffer.toString());
        return XASupport.xa_recover(this.connection, this.xaConnection.getXAConnectionID(), i);
    }

    public int prepare(Xid xid) throws XAException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XAResource.prepare(");
        stringBuffer.append(xid.toString());
        stringBuffer.append(')');
        Logger.println(stringBuffer.toString());
        return XASupport.xa_prepare(this.connection, this.xaConnection.getXAConnectionID(), xid);
    }

    public void forget(Xid xid) throws XAException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XAResource.forget(");
        stringBuffer.append(xid);
        stringBuffer.append(')');
        Logger.println(stringBuffer.toString());
        XASupport.xa_forget(this.connection, this.xaConnection.getXAConnectionID(), xid);
    }

    public void rollback(Xid xid) throws XAException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XAResource.rollback(");
        stringBuffer.append(xid.toString());
        stringBuffer.append(')');
        Logger.println(stringBuffer.toString());
        XASupport.xa_rollback(this.connection, this.xaConnection.getXAConnectionID(), xid);
    }

    public void end(Xid xid, int i) throws XAException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XAResource.end(");
        stringBuffer.append(xid.toString());
        stringBuffer.append(')');
        Logger.println(stringBuffer.toString());
        XASupport.xa_end(this.connection, this.xaConnection.getXAConnectionID(), xid, i);
    }

    public void start(Xid xid, int i) throws XAException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XAResource.start(");
        stringBuffer.append(xid.toString());
        stringBuffer.append(',');
        stringBuffer.append(i);
        stringBuffer.append(')');
        Logger.println(stringBuffer.toString());
        XASupport.xa_start(this.connection, this.xaConnection.getXAConnectionID(), xid, i);
    }

    public void commit(Xid xid, boolean z) throws XAException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("XAResource.commit(");
        stringBuffer.append(xid.toString());
        stringBuffer.append(',');
        stringBuffer.append(z);
        stringBuffer.append(')');
        Logger.println(stringBuffer.toString());
        XASupport.xa_commit(this.connection, this.xaConnection.getXAConnectionID(), xid, z);
    }
}
