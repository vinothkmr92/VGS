package net.sourceforge.jtds.jdbcx.proxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;

public class StatementProxy implements Statement {
    private ConnectionProxy _connection;
    private JtdsStatement _statement;

    StatementProxy(ConnectionProxy connectionProxy, JtdsStatement jtdsStatement) {
        this._connection = connectionProxy;
        this._statement = jtdsStatement;
    }

    public ResultSet executeQuery(String str) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeQuery(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public int executeUpdate(String str) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeUpdate(str);
        } catch (String str2) {
            processSQLException(str2);
            return Integer.MIN_VALUE;
        }
    }

    public void close() throws SQLException {
        validateConnection();
        try {
            this._statement.close();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public int getMaxFieldSize() throws SQLException {
        validateConnection();
        try {
            return this._statement.getMaxFieldSize();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void setMaxFieldSize(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setMaxFieldSize(i);
        } catch (int i2) {
            processSQLException(i2);
        }
    }

    public int getMaxRows() throws SQLException {
        validateConnection();
        try {
            return this._statement.getMaxRows();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void setMaxRows(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setMaxRows(i);
        } catch (int i2) {
            processSQLException(i2);
        }
    }

    public void setEscapeProcessing(boolean z) throws SQLException {
        validateConnection();
        try {
            this._statement.setEscapeProcessing(z);
        } catch (boolean z2) {
            processSQLException(z2);
        }
    }

    public int getQueryTimeout() throws SQLException {
        validateConnection();
        try {
            return this._statement.getQueryTimeout();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void setQueryTimeout(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setQueryTimeout(i);
        } catch (int i2) {
            processSQLException(i2);
        }
    }

    public void cancel() throws SQLException {
        validateConnection();
        try {
            this._statement.cancel();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        validateConnection();
        try {
            return this._statement.getWarnings();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public void clearWarnings() throws SQLException {
        validateConnection();
        try {
            this._statement.clearWarnings();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public void setCursorName(String str) throws SQLException {
        validateConnection();
        try {
            this._statement.setCursorName(str);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public boolean execute(String str) throws SQLException {
        validateConnection();
        try {
            return this._statement.execute(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public ResultSet getResultSet() throws SQLException {
        validateConnection();
        try {
            return this._statement.getResultSet();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public int getUpdateCount() throws SQLException {
        validateConnection();
        try {
            return this._statement.getUpdateCount();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public boolean getMoreResults() throws SQLException {
        validateConnection();
        try {
            return this._statement.getMoreResults();
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public void setFetchDirection(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setFetchDirection(i);
        } catch (int i2) {
            processSQLException(i2);
        }
    }

    public int getFetchDirection() throws SQLException {
        validateConnection();
        try {
            return this._statement.getFetchDirection();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void setFetchSize(int i) throws SQLException {
        validateConnection();
        try {
            this._statement.setFetchSize(i);
        } catch (int i2) {
            processSQLException(i2);
        }
    }

    public int getFetchSize() throws SQLException {
        validateConnection();
        try {
            return this._statement.getFetchSize();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        validateConnection();
        try {
            return this._statement.getResultSetConcurrency();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public int getResultSetType() throws SQLException {
        validateConnection();
        try {
            return this._statement.getResultSetType();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    public void addBatch(String str) throws SQLException {
        validateConnection();
        try {
            this._statement.addBatch(str);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void clearBatch() throws SQLException {
        validateConnection();
        try {
            this._statement.clearBatch();
        } catch (SQLException e) {
            processSQLException(e);
        }
    }

    public int[] executeBatch() throws SQLException {
        validateConnection();
        try {
            return this._statement.executeBatch();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public Connection getConnection() throws SQLException {
        validateConnection();
        try {
            return this._statement.getConnection();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public boolean getMoreResults(int i) throws SQLException {
        validateConnection();
        try {
            return this._statement.getMoreResults(i);
        } catch (int i2) {
            processSQLException(i2);
            return false;
        }
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        validateConnection();
        try {
            return this._statement.getGeneratedKeys();
        } catch (SQLException e) {
            processSQLException(e);
            return null;
        }
    }

    public int executeUpdate(String str, int i) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeUpdate(str, i);
        } catch (String str2) {
            processSQLException(str2);
            return Integer.MIN_VALUE;
        }
    }

    public int executeUpdate(String str, int[] iArr) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeUpdate(str, iArr);
        } catch (String str2) {
            processSQLException(str2);
            return Integer.MIN_VALUE;
        }
    }

    public int executeUpdate(String str, String[] strArr) throws SQLException {
        validateConnection();
        try {
            return this._statement.executeUpdate(str, strArr);
        } catch (String str2) {
            processSQLException(str2);
            return Integer.MIN_VALUE;
        }
    }

    public boolean execute(String str, int i) throws SQLException {
        validateConnection();
        try {
            return this._statement.execute(str, i);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public boolean execute(String str, int[] iArr) throws SQLException {
        validateConnection();
        try {
            return this._statement.execute(str, iArr);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public boolean execute(String str, String[] strArr) throws SQLException {
        validateConnection();
        try {
            return this._statement.execute(str, strArr);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public int getResultSetHoldability() throws SQLException {
        validateConnection();
        try {
            return this._statement.getResultSetHoldability();
        } catch (SQLException e) {
            processSQLException(e);
            return Integer.MIN_VALUE;
        }
    }

    protected void validateConnection() throws SQLException {
        if (this._connection.isClosed()) {
            throw new SQLException(Messages.get("error.conproxy.noconn"), "HY010");
        }
    }

    protected void processSQLException(SQLException sQLException) throws SQLException {
        this._connection.processSQLException(sQLException);
        throw sQLException;
    }
}
