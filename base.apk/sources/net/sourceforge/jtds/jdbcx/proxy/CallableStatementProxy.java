package net.sourceforge.jtds.jdbcx.proxy;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import net.sourceforge.jtds.jdbc.JtdsCallableStatement;

public class CallableStatementProxy extends PreparedStatementProxy implements CallableStatement {
    private JtdsCallableStatement _callableStatement;

    CallableStatementProxy(ConnectionProxy connectionProxy, JtdsCallableStatement jtdsCallableStatement) {
        super(connectionProxy, jtdsCallableStatement);
        this._callableStatement = jtdsCallableStatement;
    }

    public void registerOutParameter(int i, int i2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(i, i2);
        } catch (int i3) {
            processSQLException(i3);
        }
    }

    public void registerOutParameter(int i, int i2, int i3) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(i, i2, i3);
        } catch (int i4) {
            processSQLException(i4);
        }
    }

    public boolean wasNull() throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.wasNull();
        } catch (SQLException e) {
            processSQLException(e);
            return false;
        }
    }

    public String getString(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getString(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public boolean getBoolean(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBoolean(i);
        } catch (int i2) {
            processSQLException(i2);
            return false;
        }
    }

    public byte getByte(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getByte(i);
        } catch (int i2) {
            processSQLException(i2);
            return Byte.MIN_VALUE;
        }
    }

    public short getShort(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getShort(i);
        } catch (int i2) {
            processSQLException(i2);
            return Short.MIN_VALUE;
        }
    }

    public int getInt(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getInt(i);
        } catch (int i2) {
            processSQLException(i2);
            return Integer.MIN_VALUE;
        }
    }

    public long getLong(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getLong(i);
        } catch (int i2) {
            processSQLException(i2);
            return Long.MIN_VALUE;
        }
    }

    public float getFloat(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getFloat(i);
        } catch (int i2) {
            processSQLException(i2);
            return 1;
        }
    }

    public double getDouble(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDouble(i);
        } catch (int i2) {
            processSQLException(i2);
            return Double.MIN_VALUE;
        }
    }

    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBigDecimal(i, i2);
        } catch (int i3) {
            processSQLException(i3);
            return 0;
        }
    }

    public byte[] getBytes(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBytes(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Date getDate(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDate(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Time getTime(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTime(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTimestamp(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Object getObject(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getObject(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBigDecimal(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Object getObject(int i, Map map) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getObject(i, map);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Ref getRef(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getRef(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Blob getBlob(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBlob(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Clob getClob(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getClob(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Array getArray(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getArray(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDate(i, calendar);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTime(i, calendar);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTimestamp(i, calendar);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public void registerOutParameter(int i, int i2, String str) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(i, i2, str);
        } catch (int i3) {
            processSQLException(i3);
        }
    }

    public void registerOutParameter(String str, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(str, i);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void registerOutParameter(String str, int i, int i2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(str, i, i2);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void registerOutParameter(String str, int i, String str2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.registerOutParameter(str, i, str2);
        } catch (String str3) {
            processSQLException(str3);
        }
    }

    public URL getURL(int i) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getURL(i);
        } catch (int i2) {
            processSQLException(i2);
            return 0;
        }
    }

    public void setURL(String str, URL url) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setURL(str, url);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setNull(String str, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setNull(str, i);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setBoolean(String str, boolean z) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setBoolean(str, z);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setByte(String str, byte b) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setByte(str, b);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setShort(String str, short s) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setShort(str, s);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setInt(String str, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setInt(str, i);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setLong(String str, long j) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setLong(str, j);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setFloat(String str, float f) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setFloat(str, f);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setDouble(String str, double d) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setDouble(str, d);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setBigDecimal(str, bigDecimal);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setString(String str, String str2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setString(str, str2);
        } catch (String str3) {
            processSQLException(str3);
        }
    }

    public void setBytes(String str, byte[] bArr) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setBytes(str, bArr);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setDate(String str, Date date) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setDate(str, date);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setTime(String str, Time time) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setTime(str, time);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setTimestamp(String str, Timestamp timestamp) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setTimestamp(str, timestamp);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setAsciiStream(String str, InputStream inputStream, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setAsciiStream(str, inputStream, i);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setBinaryStream(String str, InputStream inputStream, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setBinaryStream(str, inputStream, i);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setObject(String str, Object obj, int i, int i2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setObject(str, obj, i, i2);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setObject(String str, Object obj, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setObject(str, obj, i);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setObject(String str, Object obj) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setObject(str, obj);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setCharacterStream(String str, Reader reader, int i) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setCharacterStream(str, reader, i);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setDate(String str, Date date, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setDate(str, date, calendar);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setTime(String str, Time time, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setTime(str, time, calendar);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setTimestamp(String str, Timestamp timestamp, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setTimestamp(str, timestamp, calendar);
        } catch (String str2) {
            processSQLException(str2);
        }
    }

    public void setNull(String str, int i, String str2) throws SQLException {
        validateConnection();
        try {
            this._callableStatement.setNull(str, i, str2);
        } catch (String str3) {
            processSQLException(str3);
        }
    }

    public String getString(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getString(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public boolean getBoolean(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBoolean(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public byte getByte(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getByte(str);
        } catch (String str2) {
            processSQLException(str2);
            return Byte.MIN_VALUE;
        }
    }

    public short getShort(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getShort(str);
        } catch (String str2) {
            processSQLException(str2);
            return Short.MIN_VALUE;
        }
    }

    public int getInt(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getInt(str);
        } catch (String str2) {
            processSQLException(str2);
            return Integer.MIN_VALUE;
        }
    }

    public long getLong(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getLong(str);
        } catch (String str2) {
            processSQLException(str2);
            return Long.MIN_VALUE;
        }
    }

    public float getFloat(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getFloat(str);
        } catch (String str2) {
            processSQLException(str2);
            return Float.MIN_VALUE;
        }
    }

    public double getDouble(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDouble(str);
        } catch (String str2) {
            processSQLException(str2);
            return Double.MIN_VALUE;
        }
    }

    public byte[] getBytes(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBytes(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Date getDate(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDate(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Time getTime(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTime(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Timestamp getTimestamp(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTimestamp(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Object getObject(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getObject(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public BigDecimal getBigDecimal(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBigDecimal(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Object getObject(String str, Map map) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getObject(str, map);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Ref getRef(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getRef(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Blob getBlob(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getBlob(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Clob getClob(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getClob(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Array getArray(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getArray(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Date getDate(String str, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getDate(str, calendar);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Time getTime(String str, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTime(str, calendar);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getTimestamp(str, calendar);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }

    public URL getURL(String str) throws SQLException {
        validateConnection();
        try {
            return this._callableStatement.getURL(str);
        } catch (String str2) {
            processSQLException(str2);
            return null;
        }
    }
}
