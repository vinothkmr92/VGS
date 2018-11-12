package net.sourceforge.jtds.jdbcx.proxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
import net.sourceforge.jtds.jdbc.ConnectionJDBC2;
import net.sourceforge.jtds.jdbc.JtdsCallableStatement;
import net.sourceforge.jtds.jdbc.JtdsPreparedStatement;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbcx.PooledConnection;

public class ConnectionProxy implements Connection {
    private boolean _closed;
    private ConnectionJDBC2 _connection;
    private PooledConnection _pooledConnection;

    public ConnectionProxy(PooledConnection pooledConnection, Connection connection) {
        this._pooledConnection = pooledConnection;
        this._connection = (ConnectionJDBC2) connection;
    }

    public void clearWarnings() throws SQLException {
        validateConnection();
        try {
            this._connection.clearWarnings();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void close() {
        if (!this._closed) {
            this._pooledConnection.fireConnectionEvent(true, null);
            this._closed = true;
        }
    }

    public void commit() throws SQLException {
        validateConnection();
        try {
            this._connection.commit();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public Statement createStatement() throws SQLException {
        validateConnection();
        try {
            return new StatementProxy(this, (JtdsStatement) this._connection.createStatement());
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Statement createStatement(int i, int i2) throws SQLException {
        validateConnection();
        try {
            return new StatementProxy(this, (JtdsStatement) this._connection.createStatement(i, i2));
        } catch (int i3) {
            processSQLException(i3);
            return 0;
        }
    }

    public Statement createStatement(int i, int i2, int i3) throws SQLException {
        validateConnection();
        try {
            return new StatementProxy(this, (JtdsStatement) this._connection.createStatement(i, i2, i3));
        } catch (int i4) {
            processSQLException(i4);
            return 0;
        }
    }

    public boolean getAutoCommit() throws SQLException {
        validateConnection();
        try {
            return this._connection.getAutoCommit();
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public String getCatalog() throws SQLException {
        validateConnection();
        try {
            return this._connection.getCatalog();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public int getHoldability() throws SQLException {
        validateConnection();
        try {
            return this._connection.getHoldability();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public int getTransactionIsolation() throws SQLException {
        validateConnection();
        try {
            return this._connection.getTransactionIsolation();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public Map getTypeMap() throws SQLException {
        validateConnection();
        try {
            return this._connection.getTypeMap();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        validateConnection();
        try {
            return this._connection.getWarnings();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        validateConnection();
        try {
            return this._connection.getMetaData();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public boolean isClosed() throws SQLException {
        if (this._closed) {
            return true;
        }
        try {
            return this._connection.isClosed();
        } catch (SQLException e) {
            processSQLException(e);
            return this._closed;
        }
    }

    public boolean isReadOnly() throws SQLException {
        validateConnection();
        try {
            return this._connection.isReadOnly();
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public String nativeSQL(String str) throws SQLException {
        validateConnection();
        try {
            return this._connection.nativeSQL(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public CallableStatement prepareCall(String str) throws SQLException {
        validateConnection();
        try {
            return new CallableStatementProxy(this, (JtdsCallableStatement) this._connection.prepareCall(str));
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public CallableStatement prepareCall(String str, int i, int i2) throws SQLException {
        validateConnection();
        try {
            return new CallableStatementProxy(this, (JtdsCallableStatement) this._connection.prepareCall(str, i, i2));
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public CallableStatement prepareCall(String str, int i, int i2, int i3) throws SQLException {
        validateConnection();
        try {
            return new CallableStatementProxy(this, (JtdsCallableStatement) this._connection.prepareCall(str, i, i2, i3));
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str));
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, int i) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, i));
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, int[] iArr) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, iArr));
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, String[] strArr) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, strArr));
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, int i, int i2) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, i, i2));
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String str, int i, int i2, int i3) throws SQLException {
        validateConnection();
        try {
            return new PreparedStatementProxy(this, (JtdsPreparedStatement) this._connection.prepareStatement(str, i, i2, i3));
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        validateConnection();
        try {
            this._connection.releaseSavepoint(savepoint);
        } catch (Savepoint savepoint2) {
            processSQLException(savepoint2);
        }
    }

    public void rollback() throws SQLException {
        validateConnection();
        try {
            this._connection.rollback();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        validateConnection();
        try {
            this._connection.rollback(savepoint);
        } catch (Savepoint savepoint2) {
            processSQLException(savepoint2);
        }
    }

    public void setAutoCommit(boolean z) throws SQLException {
        validateConnection();
        try {
            this._connection.setAutoCommit(z);
        } catch (boolean z2) {
            processSQLException(z2);
        }
    }

    public void setCatalog(String str) throws SQLException {
        validateConnection();
        try {
            this._connection.setCatalog(str);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setHoldability(int i) throws SQLException {
        validateConnection();
        try {
            this._connection.setHoldability(i);
        } catch (int i2) {
            processSQLException(i2);
        }
    }

    public void setReadOnly(boolean z) throws SQLException {
        validateConnection();
        try {
            this._connection.setReadOnly(z);
        } catch (boolean z2) {
            processSQLException(z2);
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        validateConnection();
        try {
            return this._connection.setSavepoint();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Savepoint setSavepoint(String str) throws SQLException {
        validateConnection();
        try {
            return this._connection.setSavepoint(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public void setTransactionIsolation(int i) throws SQLException {
        validateConnection();
        try {
            this._connection.setTransactionIsolation(i);
        } catch (int i2) {
            processSQLException(i2);
        }
    }

    public void setTypeMap(Map map) throws SQLException {
        validateConnection();
        try {
            this._connection.setTypeMap(map);
        } catch (Map map2) {
            processSQLException(map2);
        }
    }

    private void validateConnection() throws SQLException {
        if (this._closed) {
            throw new SQLException(Messages.get("error.conproxy.noconn"), "HY010");
        }
    }

    void processSQLException(SQLException sQLException) throws SQLException {
        this._pooledConnection.fireConnectionEvent(false, sQLException);
        throw sQLException;
    }

    protected void finalize() {
        close();
    }
}
