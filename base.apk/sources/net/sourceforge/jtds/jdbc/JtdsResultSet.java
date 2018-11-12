package net.sourceforge.jtds.jdbc;

import android.support.v4.view.PointerIconCompat;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JtdsResultSet implements ResultSet {
    static final int CLOSE_CURSORS_AT_COMMIT = 2;
    static final int HOLD_CURSORS_OVER_COMMIT = 1;
    protected static final int INITIAL_ROW_COUNT = 1000;
    protected static final int POS_AFTER_LAST = -1;
    protected static final int POS_BEFORE_FIRST = 0;
    private static NumberFormat f = NumberFormat.getInstance();
    protected boolean cancelled;
    protected boolean closed;
    protected int columnCount;
    private HashMap columnMap;
    protected ColInfo[] columns;
    protected int concurrency;
    protected Object[] currentRow;
    protected String cursorName;
    protected int direction = 1000;
    protected int fetchDirection = 1000;
    protected int fetchSize;
    protected int pos = 0;
    protected int resultSetType;
    protected ArrayList rowData;
    protected int rowPtr;
    protected int rowsInResult;
    protected JtdsStatement statement;
    protected boolean wasNull;

    JtdsResultSet(JtdsStatement jtdsStatement, int i, int i2, ColInfo[] colInfoArr) throws SQLException {
        if (jtdsStatement == null) {
            throw new IllegalArgumentException("Statement parameter must not be null");
        }
        this.statement = jtdsStatement;
        this.resultSetType = i;
        this.concurrency = i2;
        this.columns = colInfoArr;
        this.fetchSize = jtdsStatement.fetchSize;
        this.fetchDirection = jtdsStatement.fetchDirection;
        this.cursorName = jtdsStatement.cursorName;
        if (colInfoArr != null) {
            this.columnCount = getColumnCount(colInfoArr);
            this.rowsInResult = jtdsStatement.getTds().isDataInResultSet();
        }
    }

    protected static int getColumnCount(ColInfo[] colInfoArr) {
        int length = colInfoArr.length - 1;
        while (length >= 0 && colInfoArr[length].isHidden) {
            length--;
        }
        return length + 1;
    }

    protected ColInfo[] getColumns() {
        return this.columns;
    }

    protected void setColName(int i, String str) {
        if (i >= 1) {
            if (i <= this.columns.length) {
                this.columns[i - 1].realName = str;
                return;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("columnIndex ");
        stringBuffer.append(i);
        stringBuffer.append(" invalid");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    protected void setColLabel(int i, String str) {
        if (i >= 1) {
            if (i <= this.columns.length) {
                this.columns[i - 1].name = str;
                return;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("columnIndex ");
        stringBuffer.append(i);
        stringBuffer.append(" invalid");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    protected void setColType(int i, int i2) {
        if (i >= 1) {
            if (i <= this.columns.length) {
                this.columns[i - 1].jdbcType = i2;
                return;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("columnIndex ");
        stringBuffer.append(i);
        stringBuffer.append(" invalid");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    protected Object setColValue(int i, int i2, Object obj, int i3) throws SQLException {
        checkOpen();
        checkUpdateable();
        if (i >= 1) {
            if (i <= this.columnCount) {
                if ((obj instanceof Timestamp) != 0) {
                    return new DateTime((Timestamp) obj);
                }
                if ((obj instanceof Date) != 0) {
                    return new DateTime((Date) obj);
                }
                return (obj instanceof Time) != 0 ? new DateTime((Time) obj) : obj;
            }
        }
        throw new SQLException(Messages.get("error.resultset.colindex", Integer.toString(i)), "07009");
    }

    protected void setColumnCount(int i) {
        if (i >= 1) {
            if (i <= this.columns.length) {
                this.columnCount = i;
                return;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("columnCount ");
        stringBuffer.append(i);
        stringBuffer.append(" is invalid");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    protected Object getColumn(int i) throws SQLException {
        checkOpen();
        boolean z = true;
        if (i >= 1) {
            if (i <= this.columnCount) {
                if (this.currentRow == null) {
                    throw new SQLException(Messages.get("error.resultset.norow"), "24000");
                }
                i = this.currentRow[i - 1];
                if (i != 0) {
                    z = false;
                }
                this.wasNull = z;
                return i;
            }
        }
        throw new SQLException(Messages.get("error.resultset.colindex", Integer.toString(i)), "07009");
    }

    protected void checkOpen() throws SQLException {
        if (this.closed) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "ResultSet"), "HY010");
        } else if (this.cancelled) {
            throw new SQLException(Messages.get("error.generic.cancelled", (Object) "ResultSet"), "HY010");
        }
    }

    protected void checkScrollable() throws SQLException {
        if (this.resultSetType == PointerIconCompat.TYPE_HELP) {
            throw new SQLException(Messages.get("error.resultset.fwdonly"), "24000");
        }
    }

    protected void checkUpdateable() throws SQLException {
        if (this.concurrency == PointerIconCompat.TYPE_CROSSHAIR) {
            throw new SQLException(Messages.get("error.resultset.readonly"), "24000");
        }
    }

    protected static void notImplemented(String str) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", (Object) str), "HYC00");
    }

    protected Object[] newRow() {
        return new Object[this.columns.length];
    }

    protected Object[] copyRow(Object[] objArr) {
        Object obj = new Object[this.columns.length];
        System.arraycopy(objArr, 0, obj, 0, objArr.length);
        return obj;
    }

    protected ColInfo[] copyInfo(ColInfo[] colInfoArr) {
        Object obj = new ColInfo[colInfoArr.length];
        System.arraycopy(colInfoArr, 0, obj, 0, colInfoArr.length);
        return obj;
    }

    protected Object[] getCurrentRow() {
        return this.currentRow;
    }

    protected void cacheResultSetRows() throws SQLException {
        if (this.rowData == null) {
            this.rowData = new ArrayList(1000);
        }
        if (this.currentRow != null) {
            this.currentRow = copyRow(this.currentRow);
        }
        while (this.statement.getTds().getNextRow()) {
            this.rowData.add(copyRow(this.statement.getTds().getRowData()));
        }
        this.statement.cacheResults();
    }

    private ConnectionJDBC2 getConnection() throws SQLException {
        return (ConnectionJDBC2) this.statement.getConnection();
    }

    public int getConcurrency() throws SQLException {
        checkOpen();
        return this.concurrency;
    }

    public int getFetchDirection() throws SQLException {
        checkOpen();
        return this.fetchDirection;
    }

    public int getFetchSize() throws SQLException {
        checkOpen();
        return this.fetchSize;
    }

    public int getRow() throws SQLException {
        checkOpen();
        return this.pos > 0 ? this.pos : 0;
    }

    public int getType() throws SQLException {
        checkOpen();
        return this.resultSetType;
    }

    public void afterLast() throws SQLException {
        checkOpen();
        checkScrollable();
    }

    public void beforeFirst() throws SQLException {
        checkOpen();
        checkScrollable();
    }

    public void cancelRowUpdates() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void clearWarnings() throws SQLException {
        checkOpen();
        this.statement.clearWarnings();
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void close() throws SQLException {
        if (!this.closed) {
            try {
                if (!getConnection().isClosed()) {
                    while (next()) {
                    }
                }
                this.closed = true;
                this.statement = null;
            } catch (Throwable th) {
                this.closed = true;
                this.statement = null;
            }
        }
    }

    public void deleteRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void insertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void moveToCurrentRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void moveToInsertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void refreshRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public void updateRow() throws SQLException {
        checkOpen();
        checkUpdateable();
    }

    public boolean first() throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public boolean isAfterLast() throws SQLException {
        checkOpen();
        return this.pos == -1 && this.rowsInResult != 0;
    }

    public boolean isBeforeFirst() throws SQLException {
        checkOpen();
        return this.pos == 0 && this.rowsInResult != 0;
    }

    public boolean isFirst() throws SQLException {
        checkOpen();
        return this.pos == 1;
    }

    public boolean isLast() throws SQLException {
        checkOpen();
        if (this.statement.getTds().isDataInResultSet()) {
            this.rowsInResult = this.pos + 1;
        }
        if (this.pos != this.rowsInResult || this.rowsInResult == 0) {
            return false;
        }
        return true;
    }

    public boolean last() throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public boolean next() throws java.sql.SQLException {
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
        r6 = this;
        r6.checkOpen();
        r0 = r6.pos;
        r1 = 0;
        r2 = -1;
        if (r0 != r2) goto L_0x000a;
    L_0x0009:
        return r1;
    L_0x000a:
        r0 = r6.rowData;	 Catch:{ NullPointerException -> 0x007a }
        r3 = 0;	 Catch:{ NullPointerException -> 0x007a }
        r4 = 1;	 Catch:{ NullPointerException -> 0x007a }
        if (r0 == 0) goto L_0x0040;	 Catch:{ NullPointerException -> 0x007a }
    L_0x0010:
        r0 = r6.rowPtr;	 Catch:{ NullPointerException -> 0x007a }
        r5 = r6.rowData;	 Catch:{ NullPointerException -> 0x007a }
        r5 = r5.size();	 Catch:{ NullPointerException -> 0x007a }
        if (r0 >= r5) goto L_0x003b;	 Catch:{ NullPointerException -> 0x007a }
    L_0x001a:
        r0 = r6.rowData;	 Catch:{ NullPointerException -> 0x007a }
        r2 = r6.rowPtr;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r0.get(r2);	 Catch:{ NullPointerException -> 0x007a }
        r0 = (java.lang.Object[]) r0;	 Catch:{ NullPointerException -> 0x007a }
        r6.currentRow = r0;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r6.rowData;	 Catch:{ NullPointerException -> 0x007a }
        r2 = r6.rowPtr;	 Catch:{ NullPointerException -> 0x007a }
        r5 = r2 + 1;	 Catch:{ NullPointerException -> 0x007a }
        r6.rowPtr = r5;	 Catch:{ NullPointerException -> 0x007a }
        r0.set(r2, r3);	 Catch:{ NullPointerException -> 0x007a }
        r0 = r6.pos;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r0 + r4;	 Catch:{ NullPointerException -> 0x007a }
        r6.pos = r0;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r6.pos;	 Catch:{ NullPointerException -> 0x007a }
        r6.rowsInResult = r0;	 Catch:{ NullPointerException -> 0x007a }
        goto L_0x006b;	 Catch:{ NullPointerException -> 0x007a }
    L_0x003b:
        r6.pos = r2;	 Catch:{ NullPointerException -> 0x007a }
        r6.currentRow = r3;	 Catch:{ NullPointerException -> 0x007a }
        goto L_0x006b;	 Catch:{ NullPointerException -> 0x007a }
    L_0x0040:
        r0 = r6.statement;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r0.getTds();	 Catch:{ NullPointerException -> 0x007a }
        r0 = r0.getNextRow();	 Catch:{ NullPointerException -> 0x007a }
        if (r0 != 0) goto L_0x0056;	 Catch:{ NullPointerException -> 0x007a }
    L_0x004c:
        r0 = r6.statement;	 Catch:{ NullPointerException -> 0x007a }
        r0.cacheResults();	 Catch:{ NullPointerException -> 0x007a }
        r6.pos = r2;	 Catch:{ NullPointerException -> 0x007a }
        r6.currentRow = r3;	 Catch:{ NullPointerException -> 0x007a }
        goto L_0x006b;	 Catch:{ NullPointerException -> 0x007a }
    L_0x0056:
        r0 = r6.statement;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r0.getTds();	 Catch:{ NullPointerException -> 0x007a }
        r0 = r0.getRowData();	 Catch:{ NullPointerException -> 0x007a }
        r6.currentRow = r0;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r6.pos;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r0 + r4;	 Catch:{ NullPointerException -> 0x007a }
        r6.pos = r0;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r6.pos;	 Catch:{ NullPointerException -> 0x007a }
        r6.rowsInResult = r0;	 Catch:{ NullPointerException -> 0x007a }
    L_0x006b:
        r0 = r6.statement;	 Catch:{ NullPointerException -> 0x007a }
        r0 = r0.getMessages();	 Catch:{ NullPointerException -> 0x007a }
        r0.checkErrors();	 Catch:{ NullPointerException -> 0x007a }
        r0 = r6.currentRow;
        if (r0 == 0) goto L_0x0079;
    L_0x0078:
        r1 = 1;
    L_0x0079:
        return r1;
    L_0x007a:
        r0 = new java.sql.SQLException;
        r1 = "error.generic.closed";
        r2 = "ResultSet";
        r1 = net.sourceforge.jtds.jdbc.Messages.get(r1, r2);
        r2 = "HY010";
        r0.<init>(r1, r2);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsResultSet.next():boolean");
    }

    public boolean previous() throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public boolean rowDeleted() throws SQLException {
        checkOpen();
        checkUpdateable();
        return false;
    }

    public boolean rowInserted() throws SQLException {
        checkOpen();
        checkUpdateable();
        return false;
    }

    public boolean rowUpdated() throws SQLException {
        checkOpen();
        checkUpdateable();
        return false;
    }

    public boolean wasNull() throws SQLException {
        checkOpen();
        return this.wasNull;
    }

    public byte getByte(int i) throws SQLException {
        return ((Integer) Support.convert(this, getColumn(i), -6, null)).byteValue();
    }

    public short getShort(int i) throws SQLException {
        return ((Integer) Support.convert(this, getColumn(i), 5, null)).shortValue();
    }

    public int getInt(int i) throws SQLException {
        return ((Integer) Support.convert(this, getColumn(i), 4, null)).intValue();
    }

    public long getLong(int i) throws SQLException {
        return ((Long) Support.convert(this, getColumn(i), -5, null)).longValue();
    }

    public float getFloat(int i) throws SQLException {
        return ((Float) Support.convert(this, getColumn(i), 7, null)).floatValue();
    }

    public double getDouble(int i) throws SQLException {
        return ((Double) Support.convert(this, getColumn(i), 8, null)).doubleValue();
    }

    public void setFetchDirection(int i) throws SQLException {
        checkOpen();
        switch (i) {
            case 1000:
                break;
            case PointerIconCompat.TYPE_CONTEXT_MENU /*1001*/:
            case PointerIconCompat.TYPE_HAND /*1002*/:
                if (this.resultSetType == PointerIconCompat.TYPE_HELP) {
                    throw new SQLException(Messages.get("error.resultset.fwdonly"), "24000");
                }
                break;
            default:
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "direction"), "24000");
        }
        this.fetchDirection = i;
    }

    public void setFetchSize(int i) throws SQLException {
        checkOpen();
        if (i >= 0) {
            if (this.statement.getMaxRows() <= 0 || i <= this.statement.getMaxRows()) {
                if (i == 0) {
                    i = this.statement.getDefaultFetchSize();
                }
                this.fetchSize = i;
                return;
            }
        }
        throw new SQLException(Messages.get("error.generic.badparam", Integer.toString(i), "rows"), "HY092");
    }

    public void updateNull(int i) throws SQLException {
        setColValue(i, 0, null, 0);
    }

    public boolean absolute(int i) throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public boolean getBoolean(int i) throws SQLException {
        return ((Boolean) Support.convert(this, getColumn(i), 16, null)).booleanValue();
    }

    public boolean relative(int i) throws SQLException {
        checkOpen();
        checkScrollable();
        return false;
    }

    public byte[] getBytes(int i) throws SQLException {
        checkOpen();
        return (byte[]) Support.convert(this, getColumn(i), -2, getConnection().getCharset());
    }

    public void updateByte(int i, byte b) throws SQLException {
        setColValue(i, (byte) 4, new Integer(b & 255), 0);
    }

    public void updateDouble(int i, double d) throws SQLException {
        setColValue(i, 4.0E-323d, new Double(d), 0);
    }

    public void updateFloat(int i, float f) throws SQLException {
        setColValue(i, 9.8E-45f, new Float(f), 0);
    }

    public void updateInt(int i, int i2) throws SQLException {
        setColValue(i, 4, new Integer(i2), 0);
    }

    public void updateLong(int i, long j) throws SQLException {
        setColValue(i, -5, new Long(j), 0);
    }

    public void updateShort(int i, short s) throws SQLException {
        setColValue(i, (short) 4, new Integer(s), 0);
    }

    public void updateBoolean(int i, boolean z) throws SQLException {
        setColValue(i, -7, z ? Boolean.TRUE : Boolean.FALSE, 0);
    }

    public void updateBytes(int i, byte[] bArr) throws SQLException {
        setColValue(i, -3, bArr, bArr != null ? bArr.length : 0);
    }

    public InputStream getAsciiStream(int i) throws SQLException {
        i = getClob(i);
        if (i == 0) {
            return 0;
        }
        return i.getAsciiStream();
    }

    public InputStream getBinaryStream(int i) throws SQLException {
        i = getBlob(i);
        if (i == 0) {
            return 0;
        }
        return i.getBinaryStream();
    }

    public InputStream getUnicodeStream(int i) throws SQLException {
        ClobImpl clobImpl = (ClobImpl) getClob(i);
        if (clobImpl == null) {
            return 0;
        }
        return clobImpl.getBlobBuffer().getUnicodeStream();
    }

    public void updateAsciiStream(int r3, java.io.InputStream r4, int r5) throws java.sql.SQLException {
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
        r2 = this;
        if (r4 == 0) goto L_0x0010;
    L_0x0002:
        if (r5 >= 0) goto L_0x0005;
    L_0x0004:
        goto L_0x0010;
    L_0x0005:
        r0 = new java.io.InputStreamReader;	 Catch:{ UnsupportedEncodingException -> 0x0015 }
        r1 = "US-ASCII";	 Catch:{ UnsupportedEncodingException -> 0x0015 }
        r0.<init>(r4, r1);	 Catch:{ UnsupportedEncodingException -> 0x0015 }
        r2.updateCharacterStream(r3, r0, r5);	 Catch:{ UnsupportedEncodingException -> 0x0015 }
        goto L_0x0015;
    L_0x0010:
        r4 = 0;
        r5 = 0;
        r2.updateCharacterStream(r3, r4, r5);
    L_0x0015:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsResultSet.updateAsciiStream(int, java.io.InputStream, int):void");
    }

    public void updateBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        if (inputStream != null) {
            if (i2 >= 0) {
                setColValue(i, -3, inputStream, i2);
                return;
            }
        }
        updateBytes(i, (byte[]) null);
    }

    public Reader getCharacterStream(int i) throws SQLException {
        i = getClob(i);
        if (i == 0) {
            return 0;
        }
        return i.getCharacterStream();
    }

    public void updateCharacterStream(int i, Reader reader, int i2) throws SQLException {
        if (reader != null) {
            if (i2 >= 0) {
                setColValue(i, 12, reader, i2);
                return;
            }
        }
        updateString(i, (String) null);
    }

    public Object getObject(int i) throws SQLException {
        i = getColumn(i);
        if (i instanceof UniqueIdentifier) {
            return i.toString();
        }
        if (i instanceof DateTime) {
            return ((DateTime) i).toObject();
        }
        if (!getConnection().getUseLOBs()) {
            i = Support.convertLOB(i);
        }
        return i;
    }

    public void updateObject(int i, Object obj) throws SQLException {
        int jdbcType;
        checkOpen();
        int i2 = 0;
        if (obj != null) {
            jdbcType = Support.getJdbcType(obj);
            if (obj instanceof BigDecimal) {
                obj = Support.normalizeBigDecimal((BigDecimal) obj, getConnection().getMaxPrecision());
            } else {
                InputStream binaryStream;
                if (obj instanceof Blob) {
                    Blob blob = (Blob) obj;
                    binaryStream = blob.getBinaryStream();
                    obj = (int) blob.length();
                } else if (obj instanceof Clob) {
                    Clob clob = (Clob) obj;
                    binaryStream = clob.getCharacterStream();
                    obj = (int) clob.length();
                } else if (obj instanceof String) {
                    i2 = ((String) obj).length();
                } else if (obj instanceof byte[]) {
                    i2 = ((byte[]) obj).length;
                }
                InputStream inputStream = binaryStream;
                i2 = obj;
                obj = inputStream;
            }
            if (jdbcType == 2000) {
                if (i >= 1) {
                    if (i <= this.columnCount) {
                        throw new SQLException(Messages.get("error.convert.badtypes", obj.getClass().getName(), Support.getJdbcTypeName(this.columns[i - 1].jdbcType)), "22005");
                    }
                }
                throw new SQLException(Messages.get("error.resultset.colindex", Integer.toString(i)), "07009");
            }
        }
        jdbcType = 12;
        setColValue(i, jdbcType, obj, i2);
    }

    public void updateObject(int i, Object obj, int i2) throws SQLException {
        checkOpen();
        if (i2 >= 0) {
            if (i2 <= getConnection().getMaxPrecision()) {
                if (obj instanceof BigDecimal) {
                    updateObject(i, ((BigDecimal) obj).setScale(i2, 4));
                    return;
                } else if (obj instanceof Number) {
                    synchronized (f) {
                        f.setGroupingUsed(false);
                        f.setMaximumFractionDigits(i2);
                        updateObject(i, f.format(obj));
                    }
                    return;
                } else {
                    updateObject(i, obj);
                    return;
                }
            }
        }
        throw new SQLException(Messages.get("error.generic.badscale"), "HY092");
    }

    public String getCursorName() throws SQLException {
        checkOpen();
        if (this.cursorName != null) {
            return this.cursorName;
        }
        throw new SQLException(Messages.get("error.resultset.noposupdate"), "24000");
    }

    public String getString(int i) throws SQLException {
        i = getColumn(i);
        if (i instanceof String) {
            return (String) i;
        }
        return (String) Support.convert(this, i, 12, getConnection().getCharset());
    }

    public void updateString(int i, String str) throws SQLException {
        setColValue(i, 12, str, str != null ? str.length() : 0);
    }

    public byte getByte(String str) throws SQLException {
        return getByte(findColumn(str));
    }

    public double getDouble(String str) throws SQLException {
        return getDouble(findColumn(str));
    }

    public float getFloat(String str) throws SQLException {
        return getFloat(findColumn(str));
    }

    public int findColumn(String str) throws SQLException {
        checkOpen();
        if (this.columnMap == null) {
            this.columnMap = new HashMap(this.columnCount);
        } else {
            Object obj = this.columnMap.get(str);
            if (obj != null) {
                return ((Integer) obj).intValue();
            }
        }
        for (int i = 0; i < this.columnCount; i++) {
            if (this.columns[i].name.equalsIgnoreCase(str)) {
                i++;
                this.columnMap.put(str, new Integer(i));
                return i;
            }
        }
        throw new SQLException(Messages.get("error.resultset.colname", (Object) str), "07009");
    }

    public int getInt(String str) throws SQLException {
        return getInt(findColumn(str));
    }

    public long getLong(String str) throws SQLException {
        return getLong(findColumn(str));
    }

    public short getShort(String str) throws SQLException {
        return getShort(findColumn(str));
    }

    public void updateNull(String str) throws SQLException {
        updateNull(findColumn(str));
    }

    public boolean getBoolean(String str) throws SQLException {
        return getBoolean(findColumn(str));
    }

    public byte[] getBytes(String str) throws SQLException {
        return getBytes(findColumn(str));
    }

    public void updateByte(String str, byte b) throws SQLException {
        updateByte(findColumn(str), b);
    }

    public void updateDouble(String str, double d) throws SQLException {
        updateDouble(findColumn(str), d);
    }

    public void updateFloat(String str, float f) throws SQLException {
        updateFloat(findColumn(str), f);
    }

    public void updateInt(String str, int i) throws SQLException {
        updateInt(findColumn(str), i);
    }

    public void updateLong(String str, long j) throws SQLException {
        updateLong(findColumn(str), j);
    }

    public void updateShort(String str, short s) throws SQLException {
        updateShort(findColumn(str), s);
    }

    public void updateBoolean(String str, boolean z) throws SQLException {
        updateBoolean(findColumn(str), z);
    }

    public void updateBytes(String str, byte[] bArr) throws SQLException {
        updateBytes(findColumn(str), bArr);
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        return (BigDecimal) Support.convert(this, getColumn(i), 3, null);
    }

    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        BigDecimal bigDecimal = (BigDecimal) Support.convert(this, getColumn(i), 3, null);
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(i2, 4);
    }

    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        checkOpen();
        checkUpdateable();
        if (bigDecimal != null) {
            bigDecimal = Support.normalizeBigDecimal(bigDecimal, getConnection().getMaxPrecision());
        }
        setColValue(i, 3, bigDecimal, 0);
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = this;
        r3 = r2.getString(r3);
        r0 = new java.net.URL;	 Catch:{ MalformedURLException -> 0x000a }
        r0.<init>(r3);	 Catch:{ MalformedURLException -> 0x000a }
        return r0;
    L_0x000a:
        r0 = new java.sql.SQLException;
        r1 = "error.resultset.badurl";
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r1, r3);
        r1 = "22000";
        r0.<init>(r3, r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsResultSet.getURL(int):java.net.URL");
    }

    public Array getArray(int i) throws SQLException {
        checkOpen();
        notImplemented("ResultSet.getArray()");
        return 0;
    }

    public void updateArray(int i, Array array) throws SQLException {
        checkOpen();
        checkUpdateable();
        notImplemented("ResultSet.updateArray()");
    }

    public Blob getBlob(int i) throws SQLException {
        return (Blob) Support.convert(this, getColumn(i), 2004, null);
    }

    public void updateBlob(int i, Blob blob) throws SQLException {
        if (blob == null) {
            updateBinaryStream(i, (InputStream) null, 0);
        } else {
            updateBinaryStream(i, blob.getBinaryStream(), (int) blob.length());
        }
    }

    public Clob getClob(int i) throws SQLException {
        return (Clob) Support.convert(this, getColumn(i), 2005, null);
    }

    public void updateClob(int i, Clob clob) throws SQLException {
        if (clob == null) {
            updateCharacterStream(i, (Reader) null, 0);
        } else {
            updateCharacterStream(i, clob.getCharacterStream(), (int) clob.length());
        }
    }

    public Date getDate(int i) throws SQLException {
        return (Date) Support.convert(this, getColumn(i), 91, null);
    }

    public void updateDate(int i, Date date) throws SQLException {
        setColValue(i, 91, date, 0);
    }

    public Ref getRef(int i) throws SQLException {
        checkOpen();
        notImplemented("ResultSet.getRef()");
        return 0;
    }

    public void updateRef(int i, Ref ref) throws SQLException {
        checkOpen();
        checkUpdateable();
        notImplemented("ResultSet.updateRef()");
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        checkOpen();
        boolean useLOBs = ((this instanceof CachedResultSet) && this.statement.isClosed()) ? false : getConnection().getUseLOBs();
        return new JtdsResultSetMetaData(this.columns, this.columnCount, useLOBs);
    }

    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return this.statement.getWarnings();
    }

    public Statement getStatement() throws SQLException {
        checkOpen();
        return this.statement;
    }

    public Time getTime(int i) throws SQLException {
        return (Time) Support.convert(this, getColumn(i), 92, null);
    }

    public void updateTime(int i, Time time) throws SQLException {
        setColValue(i, 92, time, 0);
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        return (Timestamp) Support.convert(this, getColumn(i), 93, null);
    }

    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        setColValue(i, 93, timestamp, 0);
    }

    public InputStream getAsciiStream(String str) throws SQLException {
        return getAsciiStream(findColumn(str));
    }

    public InputStream getBinaryStream(String str) throws SQLException {
        return getBinaryStream(findColumn(str));
    }

    public InputStream getUnicodeStream(String str) throws SQLException {
        return getUnicodeStream(findColumn(str));
    }

    public void updateAsciiStream(String str, InputStream inputStream, int i) throws SQLException {
        updateAsciiStream(findColumn(str), inputStream, i);
    }

    public void updateBinaryStream(String str, InputStream inputStream, int i) throws SQLException {
        updateBinaryStream(findColumn(str), inputStream, i);
    }

    public Reader getCharacterStream(String str) throws SQLException {
        return getCharacterStream(findColumn(str));
    }

    public void updateCharacterStream(String str, Reader reader, int i) throws SQLException {
        updateCharacterStream(findColumn(str), reader, i);
    }

    public Object getObject(String str) throws SQLException {
        return getObject(findColumn(str));
    }

    public void updateObject(String str, Object obj) throws SQLException {
        updateObject(findColumn(str), obj);
    }

    public void updateObject(String str, Object obj, int i) throws SQLException {
        updateObject(findColumn(str), obj, i);
    }

    public Object getObject(int i, Map map) throws SQLException {
        notImplemented("ResultSet.getObject(int, Map)");
        return 0;
    }

    public String getString(String str) throws SQLException {
        return getString(findColumn(str));
    }

    public void updateString(String str, String str2) throws SQLException {
        updateString(findColumn(str), str2);
    }

    public BigDecimal getBigDecimal(String str) throws SQLException {
        return getBigDecimal(findColumn(str));
    }

    public BigDecimal getBigDecimal(String str, int i) throws SQLException {
        return getBigDecimal(findColumn(str), i);
    }

    public void updateBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
        updateObject(findColumn(str), (Object) bigDecimal);
    }

    public URL getURL(String str) throws SQLException {
        return getURL(findColumn(str));
    }

    public Array getArray(String str) throws SQLException {
        return getArray(findColumn(str));
    }

    public void updateArray(String str, Array array) throws SQLException {
        updateArray(findColumn(str), array);
    }

    public Blob getBlob(String str) throws SQLException {
        return getBlob(findColumn(str));
    }

    public void updateBlob(String str, Blob blob) throws SQLException {
        updateBlob(findColumn(str), blob);
    }

    public Clob getClob(String str) throws SQLException {
        return getClob(findColumn(str));
    }

    public void updateClob(String str, Clob clob) throws SQLException {
        updateClob(findColumn(str), clob);
    }

    public Date getDate(String str) throws SQLException {
        return getDate(findColumn(str));
    }

    public void updateDate(String str, Date date) throws SQLException {
        updateDate(findColumn(str), date);
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        i = getDate(i);
        return (i == 0 || calendar == null) ? i : new Date(Support.timeToZone(i, calendar));
    }

    public Ref getRef(String str) throws SQLException {
        return getRef(findColumn(str));
    }

    public void updateRef(String str, Ref ref) throws SQLException {
        updateRef(findColumn(str), ref);
    }

    public Time getTime(String str) throws SQLException {
        return getTime(findColumn(str));
    }

    public void updateTime(String str, Time time) throws SQLException {
        updateTime(findColumn(str), time);
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        checkOpen();
        i = getTime(i);
        return (i == 0 || calendar == null) ? i : new Time(Support.timeToZone(i, calendar));
    }

    public Timestamp getTimestamp(String str) throws SQLException {
        return getTimestamp(findColumn(str));
    }

    public void updateTimestamp(String str, Timestamp timestamp) throws SQLException {
        updateTimestamp(findColumn(str), timestamp);
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        checkOpen();
        i = getTimestamp(i);
        return (i == 0 || calendar == null) ? i : new Timestamp(Support.timeToZone(i, calendar));
    }

    public Object getObject(String str, Map map) throws SQLException {
        return getObject(findColumn(str), map);
    }

    public Date getDate(String str, Calendar calendar) throws SQLException {
        return getDate(findColumn(str), calendar);
    }

    public Time getTime(String str, Calendar calendar) throws SQLException {
        return getTime(findColumn(str), calendar);
    }

    public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
        return getTimestamp(findColumn(str), calendar);
    }
}
