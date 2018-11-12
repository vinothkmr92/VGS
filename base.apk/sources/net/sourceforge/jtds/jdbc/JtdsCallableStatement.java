package net.sourceforge.jtds.jdbc;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class JtdsCallableStatement extends JtdsPreparedStatement implements CallableStatement {
    protected boolean paramWasNull;

    JtdsCallableStatement(ConnectionJDBC2 connectionJDBC2, String str, int i, int i2) throws SQLException {
        super(connectionJDBC2, str, i, i2, false);
    }

    final int findParameter(String str, boolean z) throws SQLException {
        Object stringBuffer;
        checkOpen();
        if (!str.startsWith("@")) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("@");
            stringBuffer2.append(str);
            stringBuffer = stringBuffer2.toString();
        }
        boolean z2 = false;
        int i = 0;
        while (i < this.parameters.length) {
            if (this.parameters[i].name != null && this.parameters[i].name.equalsIgnoreCase(stringBuffer)) {
                return i + 1;
            }
            i++;
        }
        if (z && !stringBuffer.equalsIgnoreCase("@return_status")) {
            while (z2 < this.parameters.length) {
                if (this.parameters[z2].name) {
                    z2++;
                } else {
                    this.parameters[z2].name = stringBuffer;
                    return z2 + 1;
                }
            }
        }
        throw new SQLException(Messages.get("error.callable.noparam", stringBuffer), "07000");
    }

    protected Object getOutputValue(int i) throws SQLException {
        checkOpen();
        ParamInfo parameter = getParameter(i);
        if (parameter.isOutput) {
            i = parameter.getOutValue();
            this.paramWasNull = i == 0;
            return i;
        }
        throw new SQLException(Messages.get("error.callable.notoutput", new Integer(i)), "07000");
    }

    protected void checkOpen() throws SQLException {
        if (isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "CallableStatement"), "HY010");
        }
    }

    protected SQLException executeMSBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        int i3 = i;
        if (this.parameters.length == 0) {
            return super.executeMSBatch(i, i2, arrayList);
        }
        SQLException sQLException = null;
        int i4 = 0;
        while (i4 < i3) {
            boolean z;
            Object obj = r0.batchValues.get(i4);
            i4++;
            if (i4 % i2 != 0) {
                if (i4 != i3) {
                    z = false;
                    r0.tds.startBatch();
                    r0.tds.executeSQL(r0.sql, r0.procName, (ParamInfo[]) obj, false, 0, -1, -1, z);
                    if (z) {
                        sQLException = r0.tds.getBatchCounts(arrayList, sQLException);
                        if (!(sQLException == null || arrayList.size() == i4)) {
                            break;
                        }
                    }
                    ArrayList arrayList2 = arrayList;
                }
            }
            z = true;
            r0.tds.startBatch();
            r0.tds.executeSQL(r0.sql, r0.procName, (ParamInfo[]) obj, false, 0, -1, -1, z);
            if (z) {
                sQLException = r0.tds.getBatchCounts(arrayList, sQLException);
                break;
            }
            ArrayList arrayList22 = arrayList;
        }
        return sQLException;
    }

    protected SQLException executeSybaseBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        if (this.parameters.length == 0) {
            return super.executeSybaseBatch(i, i2, arrayList);
        }
        i2 = 0;
        int i3 = 0;
        while (i3 < i) {
            Object obj = this.batchValues.get(i3);
            i3++;
            this.tds.executeSQL(this.sql, this.procName, (ParamInfo[]) obj, false, 0, -1, -1, true);
            i2 = this.tds.getBatchCounts(arrayList, i2);
            if (i2 != 0 && arrayList.size() != i3) {
                break;
            }
        }
        return i2;
    }

    public boolean wasNull() throws SQLException {
        checkOpen();
        return this.paramWasNull;
    }

    public byte getByte(int i) throws SQLException {
        return ((Integer) Support.convert(this, getOutputValue(i), -6, null)).byteValue();
    }

    public double getDouble(int i) throws SQLException {
        return ((Double) Support.convert(this, getOutputValue(i), 8, null)).doubleValue();
    }

    public float getFloat(int i) throws SQLException {
        return ((Float) Support.convert(this, getOutputValue(i), 7, null)).floatValue();
    }

    public int getInt(int i) throws SQLException {
        return ((Integer) Support.convert(this, getOutputValue(i), 4, null)).intValue();
    }

    public long getLong(int i) throws SQLException {
        return ((Long) Support.convert(this, getOutputValue(i), -5, null)).longValue();
    }

    public short getShort(int i) throws SQLException {
        return ((Integer) Support.convert(this, getOutputValue(i), 5, null)).shortValue();
    }

    public boolean getBoolean(int i) throws SQLException {
        return ((Boolean) Support.convert(this, getOutputValue(i), 16, null)).booleanValue();
    }

    public byte[] getBytes(int i) throws SQLException {
        checkOpen();
        return (byte[]) Support.convert(this, getOutputValue(i), -3, this.connection.getCharset());
    }

    public void registerOutParameter(int i, int i2) throws SQLException {
        if (i2 != 3) {
            if (i2 != 2) {
                registerOutParameter(i, i2, 0);
                return;
            }
        }
        registerOutParameter(i, i2, 10);
    }

    public void registerOutParameter(int i, int i2, int i3) throws SQLException {
        checkOpen();
        if (i3 >= 0) {
            if (i3 <= this.connection.getMaxPrecision()) {
                i = getParameter(i);
                i.isOutput = true;
                if ("ERROR".equals(Support.getJdbcTypeName(i2))) {
                    throw new SQLException(Messages.get("error.generic.badtype", Integer.toString(i2)), "HY092");
                }
                if (i2 == 2005) {
                    i.jdbcType = -1;
                } else if (i2 == 2004) {
                    i.jdbcType = -4;
                } else {
                    i.jdbcType = i2;
                }
                i.scale = i3;
                return;
            }
        }
        throw new SQLException(Messages.get("error.generic.badscale"), "HY092");
    }

    public Object getObject(int i) throws SQLException {
        i = getOutputValue(i);
        if (i instanceof UniqueIdentifier) {
            return i.toString();
        }
        if (!this.connection.getUseLOBs()) {
            i = Support.convertLOB(i);
        }
        return i;
    }

    public String getString(int i) throws SQLException {
        checkOpen();
        return (String) Support.convert(this, getOutputValue(i), 12, this.connection.getCharset());
    }

    public void registerOutParameter(int i, int i2, String str) throws SQLException {
        JtdsStatement.notImplemented("CallableStatement.registerOutParameter(int, int, String");
    }

    public byte getByte(String str) throws SQLException {
        return getByte(findParameter(str, false));
    }

    public double getDouble(String str) throws SQLException {
        return getDouble(findParameter(str, false));
    }

    public float getFloat(String str) throws SQLException {
        return getFloat(findParameter(str, false));
    }

    public int getInt(String str) throws SQLException {
        return getInt(findParameter(str, false));
    }

    public long getLong(String str) throws SQLException {
        return getLong(findParameter(str, false));
    }

    public short getShort(String str) throws SQLException {
        return getShort(findParameter(str, false));
    }

    public boolean getBoolean(String str) throws SQLException {
        return getBoolean(findParameter(str, false));
    }

    public byte[] getBytes(String str) throws SQLException {
        return getBytes(findParameter(str, false));
    }

    public void setByte(String str, byte b) throws SQLException {
        setByte(findParameter(str, true), b);
    }

    public void setDouble(String str, double d) throws SQLException {
        setDouble(findParameter(str, true), d);
    }

    public void setFloat(String str, float f) throws SQLException {
        setFloat(findParameter(str, true), f);
    }

    public void registerOutParameter(String str, int i) throws SQLException {
        registerOutParameter(findParameter(str, true), i);
    }

    public void setInt(String str, int i) throws SQLException {
        setInt(findParameter(str, true), i);
    }

    public void setNull(String str, int i) throws SQLException {
        setNull(findParameter(str, true), i);
    }

    public void registerOutParameter(String str, int i, int i2) throws SQLException {
        registerOutParameter(findParameter(str, true), i, i2);
    }

    public void setLong(String str, long j) throws SQLException {
        setLong(findParameter(str, true), j);
    }

    public void setShort(String str, short s) throws SQLException {
        setShort(findParameter(str, true), s);
    }

    public void setBoolean(String str, boolean z) throws SQLException {
        setBoolean(findParameter(str, true), z);
    }

    public void setBytes(String str, byte[] bArr) throws SQLException {
        setBytes(findParameter(str, true), bArr);
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        return (BigDecimal) Support.convert(this, getOutputValue(i), 3, null);
    }

    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        return ((BigDecimal) Support.convert(this, getOutputValue(i), 3, null)).setScale(i2);
    }

    public java.net.URL getURL(int r3) throws java.sql.SQLException {
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
        r2.checkOpen();
        r3 = r2.getOutputValue(r3);
        r0 = r2.connection;
        r0 = r0.getCharset();
        r1 = 12;
        r3 = net.sourceforge.jtds.jdbc.Support.convert(r2, r3, r1, r0);
        r3 = (java.lang.String) r3;
        r0 = new java.net.URL;	 Catch:{ MalformedURLException -> 0x001b }
        r0.<init>(r3);	 Catch:{ MalformedURLException -> 0x001b }
        return r0;
    L_0x001b:
        r0 = new java.sql.SQLException;
        r1 = "error.resultset.badurl";
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r1, r3);
        r1 = "22000";
        r0.<init>(r3, r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsCallableStatement.getURL(int):java.net.URL");
    }

    public Array getArray(int i) throws SQLException {
        JtdsStatement.notImplemented("CallableStatement.getArray");
        return 0;
    }

    public Blob getBlob(int i) throws SQLException {
        i = getBytes(i);
        if (i == 0) {
            return 0;
        }
        return new BlobImpl(this.connection, i);
    }

    public Clob getClob(int i) throws SQLException {
        i = getString(i);
        if (i == 0) {
            return 0;
        }
        return new ClobImpl(this.connection, i);
    }

    public Date getDate(int i) throws SQLException {
        return (Date) Support.convert(this, getOutputValue(i), 91, null);
    }

    public Ref getRef(int i) throws SQLException {
        JtdsStatement.notImplemented("CallableStatement.getRef");
        return 0;
    }

    public Time getTime(int i) throws SQLException {
        return (Time) Support.convert(this, getOutputValue(i), 92, null);
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        return (Timestamp) Support.convert(this, getOutputValue(i), 93, null);
    }

    public void setAsciiStream(String str, InputStream inputStream, int i) throws SQLException {
        setAsciiStream(findParameter(str, true), inputStream, i);
    }

    public void setBinaryStream(String str, InputStream inputStream, int i) throws SQLException {
        setBinaryStream(findParameter(str, true), inputStream, i);
    }

    public void setCharacterStream(String str, Reader reader, int i) throws SQLException {
        setCharacterStream(findParameter(str, true), reader, i);
    }

    public Object getObject(String str) throws SQLException {
        return getObject(findParameter(str, false));
    }

    public void setObject(String str, Object obj) throws SQLException {
        setObject(findParameter(str, true), obj);
    }

    public void setObject(String str, Object obj, int i) throws SQLException {
        setObject(findParameter(str, true), obj, i);
    }

    public void setObject(String str, Object obj, int i, int i2) throws SQLException {
        setObject(findParameter(str, true), obj, i, i2);
    }

    public Object getObject(int i, Map map) throws SQLException {
        JtdsStatement.notImplemented("CallableStatement.getObject(int, Map)");
        return 0;
    }

    public String getString(String str) throws SQLException {
        return getString(findParameter(str, false));
    }

    public void registerOutParameter(String str, int i, String str2) throws SQLException {
        JtdsStatement.notImplemented("CallableStatement.registerOutParameter(String, int, String");
    }

    public void setNull(String str, int i, String str2) throws SQLException {
        JtdsStatement.notImplemented("CallableStatement.setNull(String, int, String");
    }

    public void setString(String str, String str2) throws SQLException {
        setString(findParameter(str, true), str2);
    }

    public BigDecimal getBigDecimal(String str) throws SQLException {
        return getBigDecimal(findParameter(str, false));
    }

    public void setBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
        setBigDecimal(findParameter(str, true), bigDecimal);
    }

    public URL getURL(String str) throws SQLException {
        return getURL(findParameter(str, false));
    }

    public void setURL(String str, URL url) throws SQLException {
        setObject(findParameter(str, true), url);
    }

    public Array getArray(String str) throws SQLException {
        return getArray(findParameter(str, false));
    }

    public Blob getBlob(String str) throws SQLException {
        return getBlob(findParameter(str, false));
    }

    public Clob getClob(String str) throws SQLException {
        return getClob(findParameter(str, false));
    }

    public Date getDate(String str) throws SQLException {
        return getDate(findParameter(str, false));
    }

    public void setDate(String str, Date date) throws SQLException {
        setDate(findParameter(str, true), date);
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        i = getDate(i);
        return (i == 0 || calendar == null) ? i : new Date(Support.timeToZone(i, calendar));
    }

    public Ref getRef(String str) throws SQLException {
        return getRef(findParameter(str, false));
    }

    public Time getTime(String str) throws SQLException {
        return getTime(findParameter(str, false));
    }

    public void setTime(String str, Time time) throws SQLException {
        setTime(findParameter(str, true), time);
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        i = getTime(i);
        return (i == 0 || calendar == null) ? i : new Time(Support.timeToZone(i, calendar));
    }

    public Timestamp getTimestamp(String str) throws SQLException {
        return getTimestamp(findParameter(str, false));
    }

    public void setTimestamp(String str, Timestamp timestamp) throws SQLException {
        setTimestamp(findParameter(str, true), timestamp);
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        i = getTimestamp(i);
        return (i == 0 || calendar == null) ? i : new Timestamp(Support.timeToZone(i, calendar));
    }

    public Object getObject(String str, Map map) throws SQLException {
        return getObject(findParameter(str, false), map);
    }

    public Date getDate(String str, Calendar calendar) throws SQLException {
        return getDate(findParameter(str, false), calendar);
    }

    public Time getTime(String str, Calendar calendar) throws SQLException {
        return getTime(findParameter(str, false), calendar);
    }

    public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
        return getTimestamp(findParameter(str, false), calendar);
    }

    public void setDate(String str, Date date, Calendar calendar) throws SQLException {
        setDate(findParameter(str, true), date, calendar);
    }

    public void setTime(String str, Time time, Calendar calendar) throws SQLException {
        setTime(findParameter(str, true), time, calendar);
    }

    public void setTimestamp(String str, Timestamp timestamp, Calendar calendar) throws SQLException {
        setTimestamp(findParameter(str, true), timestamp, calendar);
    }
}
