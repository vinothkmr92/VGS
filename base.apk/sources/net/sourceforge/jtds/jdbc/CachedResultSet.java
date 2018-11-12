package net.sourceforge.jtds.jdbc;

import android.support.v4.view.PointerIconCompat;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashSet;

public class CachedResultSet extends JtdsResultSet {
    protected ConnectionJDBC2 connection;
    protected final TdsCore cursorTds;
    protected ParamInfo[] insertRow;
    protected boolean isKeyed;
    protected boolean isSybase;
    protected boolean onInsertRow;
    protected final String procName;
    protected final ParamInfo[] procedureParams;
    protected boolean rowDeleted;
    protected boolean rowUpdated;
    protected boolean sizeChanged;
    protected String sql;
    protected String tableName;
    protected final boolean tempResultSet;
    protected ParamInfo[] updateRow;
    protected final TdsCore updateTds;

    CachedResultSet(JtdsStatement jtdsStatement, String str, String str2, ParamInfo[] paramInfoArr, int i, int i2) throws SQLException {
        super(jtdsStatement, i, i2, null);
        this.connection = (ConnectionJDBC2) jtdsStatement.getConnection();
        this.cursorTds = jtdsStatement.getTds();
        this.sql = str;
        this.procName = str2;
        this.procedureParams = paramInfoArr;
        if (i != PointerIconCompat.TYPE_HELP || i2 == PointerIconCompat.TYPE_CROSSHAIR || this.cursorName == null) {
            this.updateTds = this.cursorTds;
        } else {
            this.updateTds = new TdsCore(this.connection, jtdsStatement.getMessages());
        }
        this.isSybase = 2 == this.connection.getServerType() ? true : null;
        this.tempResultSet = null;
        cursorCreate();
    }

    CachedResultSet(JtdsStatement jtdsStatement, String[] strArr, int[] iArr) throws SQLException {
        super(jtdsStatement, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_TEXT, null);
        this.columns = new ColInfo[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            ColInfo colInfo = new ColInfo();
            colInfo.name = strArr[i];
            colInfo.realName = strArr[i];
            colInfo.jdbcType = iArr[i];
            colInfo.isCaseSensitive = false;
            colInfo.isIdentity = false;
            colInfo.isWriteable = false;
            colInfo.nullable = 2;
            colInfo.scale = 0;
            TdsData.fillInType(colInfo);
            this.columns[i] = colInfo;
        }
        this.columnCount = JtdsResultSet.getColumnCount(this.columns);
        this.rowData = new ArrayList(1000);
        this.rowsInResult = 0;
        this.pos = 0;
        this.tempResultSet = true;
        this.cursorName = null;
        this.cursorTds = null;
        this.updateTds = null;
        this.procName = null;
        this.procedureParams = null;
    }

    CachedResultSet(JtdsResultSet jtdsResultSet, boolean z) throws SQLException {
        super((JtdsStatement) jtdsResultSet.getStatement(), jtdsResultSet.getStatement().getResultSetType(), jtdsResultSet.getStatement().getResultSetConcurrency(), null);
        JtdsStatement jtdsStatement = (JtdsStatement) jtdsResultSet.getStatement();
        if (this.concurrency != PointerIconCompat.TYPE_CROSSHAIR) {
            this.concurrency = PointerIconCompat.TYPE_CROSSHAIR;
            jtdsStatement.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", (Object) "CONCUR_READ_ONLY"), "01000"));
        }
        if (this.resultSetType >= 1005) {
            this.resultSetType = PointerIconCompat.TYPE_WAIT;
            jtdsStatement.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", (Object) "TYPE_SCROLL_INSENSITIVE"), "01000"));
        }
        this.columns = jtdsResultSet.getColumns();
        this.columnCount = JtdsResultSet.getColumnCount(this.columns);
        this.rowData = new ArrayList(1000);
        this.rowsInResult = 0;
        this.pos = 0;
        this.tempResultSet = true;
        this.cursorName = null;
        this.cursorTds = null;
        this.updateTds = null;
        this.procName = null;
        this.procedureParams = null;
        if (z) {
            while (jtdsResultSet.next()) {
                this.rowData.add(copyRow(jtdsResultSet.getCurrentRow()));
            }
            this.rowsInResult = this.rowData.size();
        }
    }

    CachedResultSet(JtdsStatement jtdsStatement, ColInfo[] colInfoArr, Object[] objArr) throws SQLException {
        super(jtdsStatement, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR, null);
        this.columns = colInfoArr;
        this.columnCount = JtdsResultSet.getColumnCount(colInfoArr);
        this.rowData = new ArrayList(1);
        this.rowsInResult = 1;
        this.pos = null;
        this.tempResultSet = true;
        this.cursorName = null;
        this.rowData.add(copyRow(objArr));
        this.cursorTds = null;
        this.updateTds = null;
        this.procName = null;
        this.procedureParams = null;
    }

    void addRow(Object[] objArr) {
        this.rowsInResult++;
        this.rowData.add(copyRow(objArr));
    }

    void setConcurrency(int i) {
        this.concurrency = i;
    }

    private void cursorCreate() throws SQLException {
        Object obj;
        StringBuffer stringBuffer;
        ParamInfo[] paramInfoArr;
        int length;
        int i;
        SQLException sQLException;
        int i2 = this.concurrency;
        int i3 = this.resultSetType;
        if (this.cursorName == null && r0.connection.getUseCursors() && r0.resultSetType == PointerIconCompat.TYPE_HELP && r0.concurrency == PointerIconCompat.TYPE_CROSSHAIR) {
            r0.cursorName = r0.connection.getCursorName();
        }
        if (!(r0.resultSetType == PointerIconCompat.TYPE_HELP && r0.concurrency == PointerIconCompat.TYPE_CROSSHAIR && r0.cursorName == null)) {
            String[] parse = SQLParser.parse(r0.sql, new ArrayList(), (ConnectionJDBC2) r0.statement.getConnection(), true);
            if ("select".equals(parse[2])) {
                if (parse[3] == null || parse[3].length() <= 0) {
                    r0.concurrency = PointerIconCompat.TYPE_CROSSHAIR;
                } else {
                    r0.tableName = parse[3];
                }
                obj = 1;
                if (r0.cursorName == null) {
                    stringBuffer = new StringBuffer((r0.sql.length() + r0.cursorName.length()) + 128);
                    stringBuffer.append("DECLARE ");
                    stringBuffer.append(r0.cursorName);
                    stringBuffer.append(" CURSOR FOR ");
                    paramInfoArr = r0.procedureParams;
                    if (r0.procedureParams != null && r0.procedureParams.length > 0) {
                        paramInfoArr = new ParamInfo[r0.procedureParams.length];
                        length = stringBuffer.length();
                        for (i = 0; i < paramInfoArr.length; i++) {
                            paramInfoArr[i] = (ParamInfo) r0.procedureParams[i].clone();
                            ParamInfo paramInfo = paramInfoArr[i];
                            paramInfo.markerPos += length;
                        }
                    }
                    ParamInfo[] paramInfoArr2 = paramInfoArr;
                    stringBuffer.append(r0.sql);
                    r0.cursorTds.executeSQL(stringBuffer.toString(), null, paramInfoArr2, false, r0.statement.getQueryTimeout(), r0.statement.getMaxRows(), r0.statement.getMaxFieldSize(), true);
                    r0.cursorTds.clearResponseQueue();
                    r0.cursorTds.getMessages().checkErrors();
                    stringBuffer.setLength(0);
                    stringBuffer.append("\r\nOPEN ");
                    stringBuffer.append(r0.cursorName);
                    if (r0.fetchSize > 1 && r0.isSybase) {
                        stringBuffer.append("\r\nSET CURSOR ROWS ");
                        stringBuffer.append(r0.fetchSize);
                        stringBuffer.append(" FOR ");
                        stringBuffer.append(r0.cursorName);
                    }
                    stringBuffer.append("\r\nFETCH ");
                    stringBuffer.append(r0.cursorName);
                    r0.cursorTds.executeSQL(stringBuffer.toString(), null, null, false, r0.statement.getQueryTimeout(), r0.statement.getMaxRows(), r0.statement.getMaxFieldSize(), true);
                    while (!r0.cursorTds.getMoreResults() && !r0.cursorTds.isEndOfResponse()) {
                    }
                    if (r0.cursorTds.isResultSet()) {
                        sQLException = new SQLException(Messages.get("error.statement.noresult"), "24000");
                        sQLException.setNextException(r0.statement.getMessages().exceptions);
                        throw sQLException;
                    }
                    r0.columns = r0.cursorTds.getColumns();
                    if (r0.connection.getServerType() == 1 && r0.columns.length > 0) {
                        r0.columns[r0.columns.length - 1].isHidden = true;
                    }
                    r0.columnCount = JtdsResultSet.getColumnCount(r0.columns);
                    r0.rowsInResult = r0.cursorTds.isDataInResultSet();
                } else if (obj != null || (r0.concurrency == PointerIconCompat.TYPE_CROSSHAIR && r0.resultSetType < 1005)) {
                    r0.cursorTds.executeSQL(r0.sql, r0.procName, r0.procedureParams, false, r0.statement.getQueryTimeout(), r0.statement.getMaxRows(), r0.statement.getMaxFieldSize(), true);
                    while (!r0.cursorTds.getMoreResults() && !r0.cursorTds.isEndOfResponse()) {
                    }
                    if (r0.cursorTds.isResultSet()) {
                        sQLException = new SQLException(Messages.get("error.statement.noresult"), "24000");
                        sQLException.setNextException(r0.statement.getMessages().exceptions);
                        throw sQLException;
                    }
                    r0.columns = r0.cursorTds.getColumns();
                    r0.columnCount = JtdsResultSet.getColumnCount(r0.columns);
                    r0.rowData = new ArrayList(1000);
                    cacheResultSetRows();
                    r0.rowsInResult = r0.rowData.size();
                    r0.pos = 0;
                } else {
                    TdsCore tdsCore = r0.cursorTds;
                    stringBuffer = new StringBuffer();
                    stringBuffer.append(r0.sql);
                    stringBuffer.append(" FOR BROWSE");
                    tdsCore.executeSQL(stringBuffer.toString(), null, r0.procedureParams, false, r0.statement.getQueryTimeout(), r0.statement.getMaxRows(), r0.statement.getMaxFieldSize(), true);
                    while (!r0.cursorTds.getMoreResults() && !r0.cursorTds.isEndOfResponse()) {
                    }
                    if (r0.cursorTds.isResultSet()) {
                        r0.columns = r0.cursorTds.getColumns();
                        r0.columnCount = JtdsResultSet.getColumnCount(r0.columns);
                        r0.rowData = new ArrayList(1000);
                        cacheResultSetRows();
                        r0.rowsInResult = r0.rowData.size();
                        r0.pos = 0;
                        if (!isCursorUpdateable()) {
                            r0.concurrency = PointerIconCompat.TYPE_CROSSHAIR;
                            if (r0.resultSetType != PointerIconCompat.TYPE_HELP) {
                                r0.resultSetType = PointerIconCompat.TYPE_WAIT;
                            }
                        }
                    } else {
                        sQLException = new SQLException(Messages.get("error.statement.noresult"), "24000");
                        sQLException.setNextException(r0.statement.getMessages().exceptions);
                        throw sQLException;
                    }
                }
                if (r0.concurrency < i2) {
                    r0.statement.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", (Object) "CONCUR_READ_ONLY"), "01000"));
                }
                if (r0.resultSetType < i3) {
                    r0.statement.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", (Object) "TYPE_SCROLL_INSENSITIVE"), "01000"));
                }
                r0.statement.getMessages().checkErrors();
            }
            r0.cursorName = null;
            r0.concurrency = PointerIconCompat.TYPE_CROSSHAIR;
            if (r0.resultSetType != PointerIconCompat.TYPE_HELP) {
                r0.resultSetType = PointerIconCompat.TYPE_WAIT;
            }
        }
        obj = null;
        if (r0.cursorName == null) {
            if (obj != null) {
            }
            r0.cursorTds.executeSQL(r0.sql, r0.procName, r0.procedureParams, false, r0.statement.getQueryTimeout(), r0.statement.getMaxRows(), r0.statement.getMaxFieldSize(), true);
            while (!r0.cursorTds.getMoreResults()) {
            }
            if (r0.cursorTds.isResultSet()) {
                r0.columns = r0.cursorTds.getColumns();
                r0.columnCount = JtdsResultSet.getColumnCount(r0.columns);
                r0.rowData = new ArrayList(1000);
                cacheResultSetRows();
                r0.rowsInResult = r0.rowData.size();
                r0.pos = 0;
            } else {
                sQLException = new SQLException(Messages.get("error.statement.noresult"), "24000");
                sQLException.setNextException(r0.statement.getMessages().exceptions);
                throw sQLException;
            }
        }
        stringBuffer = new StringBuffer((r0.sql.length() + r0.cursorName.length()) + 128);
        stringBuffer.append("DECLARE ");
        stringBuffer.append(r0.cursorName);
        stringBuffer.append(" CURSOR FOR ");
        paramInfoArr = r0.procedureParams;
        paramInfoArr = new ParamInfo[r0.procedureParams.length];
        length = stringBuffer.length();
        for (i = 0; i < paramInfoArr.length; i++) {
            paramInfoArr[i] = (ParamInfo) r0.procedureParams[i].clone();
            ParamInfo paramInfo2 = paramInfoArr[i];
            paramInfo2.markerPos += length;
        }
        ParamInfo[] paramInfoArr22 = paramInfoArr;
        stringBuffer.append(r0.sql);
        r0.cursorTds.executeSQL(stringBuffer.toString(), null, paramInfoArr22, false, r0.statement.getQueryTimeout(), r0.statement.getMaxRows(), r0.statement.getMaxFieldSize(), true);
        r0.cursorTds.clearResponseQueue();
        r0.cursorTds.getMessages().checkErrors();
        stringBuffer.setLength(0);
        stringBuffer.append("\r\nOPEN ");
        stringBuffer.append(r0.cursorName);
        stringBuffer.append("\r\nSET CURSOR ROWS ");
        stringBuffer.append(r0.fetchSize);
        stringBuffer.append(" FOR ");
        stringBuffer.append(r0.cursorName);
        stringBuffer.append("\r\nFETCH ");
        stringBuffer.append(r0.cursorName);
        r0.cursorTds.executeSQL(stringBuffer.toString(), null, null, false, r0.statement.getQueryTimeout(), r0.statement.getMaxRows(), r0.statement.getMaxFieldSize(), true);
        while (!r0.cursorTds.getMoreResults()) {
        }
        if (r0.cursorTds.isResultSet()) {
            r0.columns = r0.cursorTds.getColumns();
            r0.columns[r0.columns.length - 1].isHidden = true;
            r0.columnCount = JtdsResultSet.getColumnCount(r0.columns);
            r0.rowsInResult = r0.cursorTds.isDataInResultSet();
        } else {
            sQLException = new SQLException(Messages.get("error.statement.noresult"), "24000");
            sQLException.setNextException(r0.statement.getMessages().exceptions);
            throw sQLException;
        }
        if (r0.concurrency < i2) {
            r0.statement.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", (Object) "CONCUR_READ_ONLY"), "01000"));
        }
        if (r0.resultSetType < i3) {
            r0.statement.addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", (Object) "TYPE_SCROLL_INSENSITIVE"), "01000"));
        }
        r0.statement.getMessages().checkErrors();
    }

    boolean isCursorUpdateable() throws SQLException {
        int i;
        this.isKeyed = false;
        HashSet hashSet = new HashSet();
        for (ColInfo colInfo : this.columns) {
            if (colInfo.isKey) {
                if (!"text".equals(colInfo.sqlType)) {
                    if (!"image".equals(colInfo.sqlType)) {
                        this.isKeyed = true;
                    }
                }
                colInfo.isKey = false;
            } else if (colInfo.isIdentity) {
                colInfo.isKey = true;
                this.isKeyed = true;
            }
            StringBuffer stringBuffer = new StringBuffer();
            if (colInfo.tableName != null && colInfo.tableName.length() > 0) {
                stringBuffer.setLength(0);
                if (colInfo.catalog != null) {
                    stringBuffer.append(colInfo.catalog);
                    stringBuffer.append('.');
                    if (colInfo.schema == null) {
                        stringBuffer.append('.');
                    }
                }
                if (colInfo.schema != null) {
                    stringBuffer.append(colInfo.schema);
                    stringBuffer.append('.');
                }
                stringBuffer.append(colInfo.tableName);
                this.tableName = stringBuffer.toString();
                hashSet.add(this.tableName);
            }
        }
        if (this.tableName.startsWith("#") && this.cursorTds.getTdsVersion() >= 3) {
            int i2;
            StringBuffer stringBuffer2 = new StringBuffer(1024);
            stringBuffer2.append("SELECT ");
            for (i2 = 1; i2 <= 8; i2++) {
                if (i2 > 1) {
                    stringBuffer2.append(',');
                }
                stringBuffer2.append("index_col('tempdb..");
                stringBuffer2.append(this.tableName);
                stringBuffer2.append("', indid, ");
                stringBuffer2.append(i2);
                stringBuffer2.append(')');
            }
            stringBuffer2.append(" FROM tempdb..sysindexes WHERE id = object_id('tempdb..");
            stringBuffer2.append(this.tableName);
            stringBuffer2.append("') AND indid > 0 AND ");
            stringBuffer2.append("(status & 2048) = 2048");
            this.cursorTds.executeSQL(stringBuffer2.toString(), null, null, false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            while (!this.cursorTds.getMoreResults() && !this.cursorTds.isEndOfResponse()) {
            }
            if (this.cursorTds.isResultSet() && this.cursorTds.getNextRow()) {
                Object[] rowData = this.cursorTds.getRowData();
                for (Object obj : rowData) {
                    String str = (String) obj;
                    if (str != null) {
                        int i3 = 0;
                        while (i3 < this.columns.length) {
                            if (this.columns[i3].realName != null && this.columns[i3].realName.equalsIgnoreCase(str)) {
                                this.columns[i3].isKey = true;
                                this.isKeyed = true;
                                break;
                            }
                            i3++;
                        }
                    }
                }
            }
            this.statement.getMessages().checkErrors();
        }
        if (!this.isKeyed) {
            i = 0;
            while (i < this.columns.length) {
                String str2 = this.columns[i].sqlType;
                if (!("ntext".equals(str2) || "text".equals(str2) || "image".equals(str2) || "timestamp".equals(str2) || this.columns[i].tableName == null)) {
                    this.columns[i].isKey = true;
                    this.isKeyed = true;
                }
                i++;
            }
        }
        if (hashSet.size() == 1 && this.isKeyed) {
            return true;
        }
        return false;
    }

    private boolean cursorFetch(int i) throws SQLException {
        boolean z = false;
        this.rowUpdated = false;
        if (this.cursorName != null) {
            if (this.cursorTds.getNextRow() == 0) {
                i = new StringBuffer(128);
                if (this.isSybase && this.sizeChanged) {
                    i.append("SET CURSOR ROWS ");
                    i.append(this.fetchSize);
                    i.append(" FOR ");
                    i.append(this.cursorName);
                    i.append("\r\n");
                }
                i.append("FETCH ");
                i.append(this.cursorName);
                this.cursorTds.executeSQL(i.toString(), null, null, false, this.statement.getQueryTimeout(), this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
                while (this.cursorTds.getMoreResults() == 0 && this.cursorTds.isEndOfResponse() == 0) {
                }
                this.sizeChanged = false;
                if (this.cursorTds.isResultSet() == 0 || this.cursorTds.getNextRow() == 0) {
                    this.pos = -1;
                    this.currentRow = null;
                    this.statement.getMessages().checkErrors();
                    return false;
                }
            }
            this.currentRow = this.statement.getTds().getRowData();
            this.pos++;
            this.rowsInResult = this.pos;
            this.statement.getMessages().checkErrors();
            if (this.currentRow != 0) {
                z = true;
            }
            return z;
        } else if (this.rowsInResult == 0) {
            this.pos = 0;
            this.currentRow = null;
            return false;
        } else if (i == this.pos) {
            return true;
        } else {
            if (i < 1) {
                this.currentRow = null;
                this.pos = 0;
                return false;
            } else if (i > this.rowsInResult) {
                this.currentRow = null;
                this.pos = -1;
                return false;
            } else {
                this.pos = i;
                this.currentRow = (Object[]) this.rowData.get(i - 1);
                if (this.currentRow == 0) {
                    z = true;
                }
                this.rowDeleted = z;
                if (this.resultSetType >= 1005 && this.currentRow != 0) {
                    refreshRow();
                }
                return true;
            }
        }
    }

    private void cursorClose() throws SQLException {
        if (this.cursorName != null) {
            String stringBuffer;
            this.statement.clearWarnings();
            StringBuffer stringBuffer2;
            if (this.isSybase) {
                stringBuffer2 = new StringBuffer();
                stringBuffer2.append("CLOSE ");
                stringBuffer2.append(this.cursorName);
                stringBuffer2.append("\r\nDEALLOCATE CURSOR ");
                stringBuffer2.append(this.cursorName);
                stringBuffer = stringBuffer2.toString();
            } else {
                stringBuffer2 = new StringBuffer();
                stringBuffer2.append("CLOSE ");
                stringBuffer2.append(this.cursorName);
                stringBuffer2.append("\r\nDEALLOCATE ");
                stringBuffer2.append(this.cursorName);
                stringBuffer = stringBuffer2.toString();
            }
            this.cursorTds.submitSQL(stringBuffer);
        }
        this.rowData = null;
    }

    protected static ParamInfo buildParameter(int i, ColInfo colInfo, Object obj, boolean z) throws SQLException {
        int length;
        boolean z2 = false;
        if (obj instanceof String) {
            length = ((String) obj).length();
        } else if (obj instanceof byte[]) {
            length = ((byte[]) obj).length;
        } else {
            InputStream binaryStream;
            if (obj instanceof BlobImpl) {
                BlobImpl blobImpl = (BlobImpl) obj;
                binaryStream = blobImpl.getBinaryStream();
                obj = (int) blobImpl.length();
            } else if (obj instanceof ClobImpl) {
                ClobImpl clobImpl = (ClobImpl) obj;
                binaryStream = clobImpl.getCharacterStream();
                obj = (int) clobImpl.length();
            } else {
                length = 0;
            }
            InputStream inputStream = binaryStream;
            length = obj;
            obj = inputStream;
        }
        ParamInfo paramInfo = new ParamInfo(colInfo, null, obj, length);
        if (!("nvarchar".equals(colInfo.sqlType) == null && "nchar".equals(colInfo.sqlType) == null && "ntext".equals(colInfo.sqlType) == null && !z)) {
            z2 = true;
        }
        paramInfo.isUnicode = z2;
        paramInfo.markerPos = i;
        return paramInfo;
    }

    protected Object setColValue(int i, int i2, Object obj, int i3) throws SQLException {
        obj = super.setColValue(i, i2, obj, i3);
        if (this.onInsertRow || this.currentRow != null) {
            ParamInfo paramInfo;
            i--;
            ColInfo colInfo = this.columns[i];
            boolean isUnicode = TdsData.isUnicode(colInfo);
            if (this.onInsertRow) {
                paramInfo = this.insertRow[i];
                if (paramInfo == null) {
                    paramInfo = new ParamInfo(-1, isUnicode);
                    paramInfo.collation = colInfo.collation;
                    paramInfo.charsetInfo = colInfo.charsetInfo;
                    this.insertRow[i] = paramInfo;
                }
            } else {
                if (this.updateRow == null) {
                    this.updateRow = new ParamInfo[this.columnCount];
                }
                paramInfo = this.updateRow[i];
                if (paramInfo == null) {
                    paramInfo = new ParamInfo(-1, isUnicode);
                    paramInfo.collation = colInfo.collation;
                    paramInfo.charsetInfo = colInfo.charsetInfo;
                    this.updateRow[i] = paramInfo;
                }
            }
            if (obj == null) {
                paramInfo.value = 0;
                paramInfo.length = 0;
                paramInfo.jdbcType = colInfo.jdbcType;
                paramInfo.isSet = true;
                if (paramInfo.jdbcType != 2) {
                    if (paramInfo.jdbcType != 3) {
                        paramInfo.scale = 0;
                    }
                }
                paramInfo.scale = 10;
            } else {
                paramInfo.value = obj;
                paramInfo.length = i3;
                paramInfo.isSet = true;
                paramInfo.jdbcType = i2;
                if ((paramInfo.value instanceof BigDecimal) != 0) {
                    paramInfo.scale = ((BigDecimal) paramInfo.value).scale();
                } else {
                    paramInfo.scale = 0;
                }
            }
            return obj;
        }
        throw new SQLException(Messages.get("error.resultset.norow"), "24000");
    }

    ParamInfo[] buildWhereClause(StringBuffer stringBuffer, ArrayList arrayList, boolean z) throws SQLException {
        stringBuffer.append(" WHERE ");
        if (this.cursorName != null) {
            stringBuffer.append(" CURRENT OF ");
            stringBuffer.append(this.cursorName);
        } else {
            int i = 0;
            int i2 = 0;
            while (i < this.columns.length) {
                if (this.currentRow[i] == null) {
                    if (!("text".equals(this.columns[i].sqlType) || "ntext".equals(this.columns[i].sqlType) || "image".equals(this.columns[i].sqlType) || this.columns[i].tableName == null)) {
                        if (i2 > 0) {
                            stringBuffer.append(" AND ");
                        }
                        stringBuffer.append(this.columns[i].realName);
                        stringBuffer.append(" IS NULL");
                    }
                } else if (this.isKeyed && z) {
                    if (this.columns[i].isKey) {
                        if (i2 > 0) {
                            stringBuffer.append(" AND ");
                        }
                        stringBuffer.append(this.columns[i].realName);
                        stringBuffer.append("=?");
                        i2++;
                        arrayList.add(buildParameter(stringBuffer.length() - 1, this.columns[i], this.currentRow[i], this.connection.getUseUnicode()));
                    }
                } else if (!("text".equals(this.columns[i].sqlType) || "ntext".equals(this.columns[i].sqlType) || "image".equals(this.columns[i].sqlType) || this.columns[i].tableName == null)) {
                    if (i2 > 0) {
                        stringBuffer.append(" AND ");
                    }
                    stringBuffer.append(this.columns[i].realName);
                    stringBuffer.append("=?");
                    i2++;
                    arrayList.add(buildParameter(stringBuffer.length() - 1, this.columns[i], this.currentRow[i], this.connection.getUseUnicode()));
                }
                i++;
            }
        }
        return (ParamInfo[]) arrayList.toArray(new ParamInfo[arrayList.size()]);
    }

    protected void refreshKeyedRows() throws SQLException {
        StringBuffer stringBuffer = new StringBuffer((this.columns.length * 10) + 100);
        stringBuffer.append("SELECT ");
        int i = 0;
        int i2 = 0;
        while (i < this.columns.length) {
            if (!(this.columns[i].isKey || this.columns[i].tableName == null)) {
                if (i2 > 0) {
                    stringBuffer.append(',');
                }
                stringBuffer.append(this.columns[i].realName);
                i2++;
            }
            i++;
        }
        if (i2 != 0) {
            stringBuffer.append(" FROM ");
            stringBuffer.append(this.tableName);
            ArrayList arrayList = new ArrayList();
            buildWhereClause(stringBuffer, arrayList, true);
            ParamInfo[] paramInfoArr = (ParamInfo[]) arrayList.toArray(new ParamInfo[arrayList.size()]);
            TdsCore tds = this.statement.getTds();
            tds.executeSQL(stringBuffer.toString(), null, paramInfoArr, false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            if (tds.isEndOfResponse()) {
                this.currentRow = null;
            } else if (tds.getMoreResults() && tds.getNextRow()) {
                Object[] rowData = tds.getRowData();
                int i3 = 0;
                for (int i4 = 0; i4 < this.columns.length; i4++) {
                    if (!this.columns[i4].isKey) {
                        int i5 = i3 + 1;
                        this.currentRow[i4] = rowData[i3];
                        i3 = i5;
                    }
                }
            } else {
                this.currentRow = null;
            }
            tds.clearResponseQueue();
            this.statement.getMessages().checkErrors();
            if (this.currentRow == null) {
                this.rowData.set(this.pos - 1, null);
                this.rowDeleted = true;
            }
        }
    }

    protected void refreshReRead() throws SQLException {
        int i = this.pos;
        cursorCreate();
        absolute(i);
    }

    public void setFetchSize(int i) throws SQLException {
        this.sizeChanged = i != this.fetchSize;
        super.setFetchSize(i);
    }

    public void afterLast() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos != -1) {
            cursorFetch(this.rowsInResult + 1);
        }
    }

    public void beforeFirst() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos != 0) {
            cursorFetch(0);
        }
    }

    public void cancelRowUpdates() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        } else if (this.updateRow != null) {
            int i = 0;
            this.rowUpdated = false;
            while (i < this.updateRow.length) {
                if (this.updateRow[i] != null) {
                    this.updateRow[i].clearInValue();
                }
                i++;
            }
        }
    }

    public void close() throws SQLException {
        if (!this.closed) {
            try {
                cursorClose();
            } finally {
                this.closed = true;
                this.statement = null;
            }
        }
    }

    public void deleteRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (this.currentRow == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        } else if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        } else {
            StringBuffer stringBuffer = new StringBuffer(128);
            ArrayList arrayList = new ArrayList();
            stringBuffer.append("DELETE FROM ");
            stringBuffer.append(this.tableName);
            boolean z = false;
            this.updateTds.executeSQL(stringBuffer.toString(), null, buildWhereClause(stringBuffer, arrayList, false), false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
            while (!this.updateTds.isEndOfResponse()) {
                if (!this.updateTds.getMoreResults() && this.updateTds.isUpdateCount()) {
                    z = this.updateTds.getUpdateCount();
                }
            }
            this.updateTds.clearResponseQueue();
            this.statement.getMessages().checkErrors();
            if (z) {
                this.rowDeleted = true;
                this.currentRow = null;
                if (this.resultSetType != PointerIconCompat.TYPE_HELP) {
                    this.rowData.set(this.pos - 1, null);
                    return;
                }
                return;
            }
            throw new SQLException(Messages.get("error.resultset.deletefail"), "24000");
        }
    }

    public void insertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (this.onInsertRow) {
            int length;
            int i = 0;
            if (!this.tempResultSet) {
                int i2;
                StringBuffer stringBuffer = new StringBuffer(128);
                ArrayList arrayList = new ArrayList();
                stringBuffer.append("INSERT INTO ");
                stringBuffer.append(this.tableName);
                length = stringBuffer.length();
                stringBuffer.append(" (");
                int i3 = 0;
                for (i2 = 0; i2 < this.columnCount; i2++) {
                    if (this.insertRow[i2] != null) {
                        if (i3 > 0) {
                            stringBuffer.append(", ");
                        }
                        stringBuffer.append(this.columns[i2].realName);
                        i3++;
                    }
                }
                stringBuffer.append(") VALUES(");
                i3 = 0;
                for (i2 = 0; i2 < this.columnCount; i2++) {
                    if (this.insertRow[i2] != null) {
                        if (i3 > 0) {
                            stringBuffer.append(", ");
                        }
                        stringBuffer.append('?');
                        this.insertRow[i2].markerPos = stringBuffer.length() - 1;
                        arrayList.add(this.insertRow[i2]);
                        i3++;
                    }
                }
                stringBuffer.append(')');
                if (i3 == 0) {
                    stringBuffer.setLength(length);
                    if (this.isSybase) {
                        stringBuffer.append(" VALUES()");
                    } else {
                        stringBuffer.append(" DEFAULT VALUES");
                    }
                }
                this.updateTds.executeSQL(stringBuffer.toString(), null, (ParamInfo[]) arrayList.toArray(new ParamInfo[arrayList.size()]), false, 0, this.statement.getMaxRows(), this.statement.getMaxFieldSize(), true);
                int i4 = 0;
                while (!this.updateTds.isEndOfResponse()) {
                    if (!this.updateTds.getMoreResults() && this.updateTds.isUpdateCount()) {
                        i4 = this.updateTds.getUpdateCount();
                    }
                }
                this.updateTds.clearResponseQueue();
                this.statement.getMessages().checkErrors();
                if (i4 < 1) {
                    throw new SQLException(Messages.get("error.resultset.insertfail"), "24000");
                }
            }
            if (this.resultSetType >= 1005 || (this.resultSetType == PointerIconCompat.TYPE_HELP && this.cursorName == null)) {
                ConnectionJDBC2 connectionJDBC2 = (ConnectionJDBC2) this.statement.getConnection();
                Object newRow = newRow();
                for (length = 0; length < this.insertRow.length; length++) {
                    if (this.insertRow[length] != null) {
                        newRow[length] = Support.convert(connectionJDBC2, this.insertRow[length].value, this.columns[length].jdbcType, connectionJDBC2.getCharset());
                    }
                }
                this.rowData.add(newRow);
            }
            this.rowsInResult++;
            while (this.insertRow != null && i < this.insertRow.length) {
                if (this.insertRow[i] != null) {
                    this.insertRow[i].clearInValue();
                }
                i++;
            }
            return;
        }
        throw new SQLException(Messages.get("error.resultset.notinsrow"), "24000");
    }

    public void moveToCurrentRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        this.insertRow = null;
        this.onInsertRow = false;
    }

    public void moveToInsertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        this.insertRow = new ParamInfo[this.columnCount];
        this.onInsertRow = true;
    }

    public void refreshRow() throws SQLException {
        checkOpen();
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        if (this.concurrency != PointerIconCompat.TYPE_CROSSHAIR) {
            cancelRowUpdates();
            this.rowUpdated = false;
        }
        if (this.resultSetType != PointerIconCompat.TYPE_HELP) {
            if (this.currentRow != null) {
                if (this.isKeyed) {
                    refreshKeyedRows();
                } else {
                    refreshReRead();
                }
            }
        }
    }

    public void updateRow() throws java.sql.SQLException {
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
        r18 = this;
        r0 = r18;
        r18.checkOpen();
        r18.checkUpdateable();
        r1 = 0;
        r0.rowUpdated = r1;
        r0.rowDeleted = r1;
        r2 = r0.currentRow;
        if (r2 != 0) goto L_0x001f;
    L_0x0011:
        r1 = new java.sql.SQLException;
        r2 = "error.resultset.norow";
        r2 = net.sourceforge.jtds.jdbc.Messages.get(r2);
        r3 = "24000";
        r1.<init>(r2, r3);
        throw r1;
    L_0x001f:
        r2 = r0.onInsertRow;
        if (r2 == 0) goto L_0x0031;
    L_0x0023:
        r1 = new java.sql.SQLException;
        r2 = "error.resultset.insrow";
        r2 = net.sourceforge.jtds.jdbc.Messages.get(r2);
        r3 = "24000";
        r1.<init>(r2, r3);
        throw r1;
    L_0x0031:
        r2 = r0.updateRow;
        if (r2 != 0) goto L_0x0036;
    L_0x0035:
        return;
    L_0x0036:
        r2 = new java.lang.StringBuffer;
        r3 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r2.<init>(r3);
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = "UPDATE ";
        r2.append(r4);
        r4 = r0.tableName;
        r2.append(r4);
        r4 = " SET ";
        r2.append(r4);
        r4 = 0;
        r5 = 0;
        r6 = 0;
    L_0x0054:
        r7 = r0.columnCount;
        r8 = 1;
        if (r4 >= r7) goto L_0x0094;
    L_0x0059:
        r7 = r0.updateRow;
        r7 = r7[r4];
        if (r7 == 0) goto L_0x0091;
    L_0x005f:
        if (r5 <= 0) goto L_0x0066;
    L_0x0061:
        r7 = ", ";
        r2.append(r7);
    L_0x0066:
        r7 = r0.columns;
        r7 = r7[r4];
        r7 = r7.realName;
        r2.append(r7);
        r7 = "=?";
        r2.append(r7);
        r7 = r0.updateRow;
        r7 = r7[r4];
        r9 = r2.length();
        r9 = r9 - r8;
        r7.markerPos = r9;
        r7 = r0.updateRow;
        r7 = r7[r4];
        r3.add(r7);
        r5 = r5 + 1;
        r7 = r0.columns;
        r7 = r7[r4];
        r7 = r7.isKey;
        if (r7 == 0) goto L_0x0091;
    L_0x0090:
        r6 = 1;
    L_0x0091:
        r4 = r4 + 1;
        goto L_0x0054;
    L_0x0094:
        if (r5 != 0) goto L_0x0097;
    L_0x0096:
        return;
    L_0x0097:
        r12 = r0.buildWhereClause(r2, r3, r1);
        r9 = r0.updateTds;
        r10 = r2.toString();
        r11 = 0;
        r13 = 0;
        r14 = 0;
        r2 = r0.statement;
        r15 = r2.getMaxRows();
        r2 = r0.statement;
        r16 = r2.getMaxFieldSize();
        r17 = 1;
        r9.executeSQL(r10, r11, r12, r13, r14, r15, r16, r17);
        r2 = 0;
    L_0x00b6:
        r3 = r0.updateTds;
        r3 = r3.isEndOfResponse();
        if (r3 != 0) goto L_0x00d5;
    L_0x00be:
        r3 = r0.updateTds;
        r3 = r3.getMoreResults();
        if (r3 != 0) goto L_0x00b6;
    L_0x00c6:
        r3 = r0.updateTds;
        r3 = r3.isUpdateCount();
        if (r3 == 0) goto L_0x00b6;
    L_0x00ce:
        r2 = r0.updateTds;
        r2 = r2.getUpdateCount();
        goto L_0x00b6;
    L_0x00d5:
        r3 = r0.updateTds;
        r3.clearResponseQueue();
        r3 = r0.statement;
        r3 = r3.getMessages();
        r3.checkErrors();
        if (r2 != 0) goto L_0x00f3;
    L_0x00e5:
        r1 = new java.sql.SQLException;
        r2 = "error.resultset.updatefail";
        r2 = net.sourceforge.jtds.jdbc.Messages.get(r2);
        r3 = "24000";
        r1.<init>(r2, r3);
        throw r1;
    L_0x00f3:
        r2 = r0.resultSetType;
        r3 = 1004; // 0x3ec float:1.407E-42 double:4.96E-321;
        if (r2 == r3) goto L_0x0174;
    L_0x00f9:
        r2 = r0.statement;
        r2 = r2.getConnection();
        r2 = (net.sourceforge.jtds.jdbc.ConnectionJDBC2) r2;
    L_0x0101:
        r3 = r0.updateRow;
        r3 = r3.length;
        if (r1 >= r3) goto L_0x0174;
    L_0x0106:
        r3 = r0.updateRow;
        r3 = r3[r1];
        if (r3 == 0) goto L_0x0171;
    L_0x010c:
        r3 = r0.updateRow;
        r3 = r3[r1];
        r3 = r3.value;
        r3 = r3 instanceof byte[];
        if (r3 == 0) goto L_0x0159;
    L_0x0116:
        r3 = r0.columns;
        r3 = r3[r1];
        r3 = r3.jdbcType;
        if (r3 == r8) goto L_0x0131;
    L_0x011e:
        r3 = r0.columns;
        r3 = r3[r1];
        r3 = r3.jdbcType;
        r4 = 12;
        if (r3 == r4) goto L_0x0131;
    L_0x0128:
        r3 = r0.columns;
        r3 = r3[r1];
        r3 = r3.jdbcType;
        r4 = -1;
        if (r3 != r4) goto L_0x0159;
    L_0x0131:
        r3 = r0.currentRow;	 Catch:{ UnsupportedEncodingException -> 0x0147 }
        r4 = new java.lang.String;	 Catch:{ UnsupportedEncodingException -> 0x0147 }
        r5 = r0.updateRow;	 Catch:{ UnsupportedEncodingException -> 0x0147 }
        r5 = r5[r1];	 Catch:{ UnsupportedEncodingException -> 0x0147 }
        r5 = r5.value;	 Catch:{ UnsupportedEncodingException -> 0x0147 }
        r5 = (byte[]) r5;	 Catch:{ UnsupportedEncodingException -> 0x0147 }
        r7 = r2.getCharset();	 Catch:{ UnsupportedEncodingException -> 0x0147 }
        r4.<init>(r5, r7);	 Catch:{ UnsupportedEncodingException -> 0x0147 }
        r3[r1] = r4;	 Catch:{ UnsupportedEncodingException -> 0x0147 }
        goto L_0x0171;
    L_0x0147:
        r3 = r0.currentRow;
        r4 = new java.lang.String;
        r5 = r0.updateRow;
        r5 = r5[r1];
        r5 = r5.value;
        r5 = (byte[]) r5;
        r4.<init>(r5);
        r3[r1] = r4;
        goto L_0x0171;
    L_0x0159:
        r3 = r0.currentRow;
        r4 = r0.updateRow;
        r4 = r4[r1];
        r4 = r4.value;
        r5 = r0.columns;
        r5 = r5[r1];
        r5 = r5.jdbcType;
        r7 = r2.getCharset();
        r4 = net.sourceforge.jtds.jdbc.Support.convert(r2, r4, r5, r7);
        r3[r1] = r4;
    L_0x0171:
        r1 = r1 + 1;
        goto L_0x0101;
    L_0x0174:
        if (r6 == 0) goto L_0x0199;
    L_0x0176:
        r1 = r0.resultSetType;
        r2 = 1005; // 0x3ed float:1.408E-42 double:4.965E-321;
        if (r1 < r2) goto L_0x0199;
    L_0x017c:
        r1 = r0.rowData;
        r2 = r0.currentRow;
        r1.add(r2);
        r1 = r0.rowData;
        r1 = r1.size();
        r0.rowsInResult = r1;
        r1 = r0.rowData;
        r2 = r0.pos;
        r2 = r2 - r8;
        r3 = 0;
        r1.set(r2, r3);
        r0.currentRow = r3;
        r0.rowDeleted = r8;
        goto L_0x019b;
    L_0x0199:
        r0.rowUpdated = r8;
    L_0x019b:
        r18.cancelRowUpdates();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.CachedResultSet.updateRow():void");
    }

    public boolean first() throws SQLException {
        checkOpen();
        checkScrollable();
        return cursorFetch(1);
    }

    public boolean isLast() throws SQLException {
        checkOpen();
        return this.pos == this.rowsInResult && this.rowsInResult != 0;
    }

    public boolean last() throws SQLException {
        checkOpen();
        checkScrollable();
        return cursorFetch(this.rowsInResult);
    }

    public boolean next() throws SQLException {
        checkOpen();
        return this.pos != -1 ? cursorFetch(this.pos + 1) : false;
    }

    public boolean previous() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos == -1) {
            this.pos = this.rowsInResult + 1;
        }
        return cursorFetch(this.pos - 1);
    }

    public boolean rowDeleted() throws SQLException {
        checkOpen();
        return this.rowDeleted;
    }

    public boolean rowInserted() throws SQLException {
        checkOpen();
        return false;
    }

    public boolean rowUpdated() throws SQLException {
        checkOpen();
        return false;
    }

    public boolean absolute(int i) throws SQLException {
        checkOpen();
        checkScrollable();
        if (i < 1) {
            i += this.rowsInResult + 1;
        }
        return cursorFetch(i);
    }

    public boolean relative(int i) throws SQLException {
        checkScrollable();
        if (this.pos == -1) {
            return absolute((this.rowsInResult + 1) + i);
        }
        return absolute(this.pos + i);
    }

    public String getCursorName() throws SQLException {
        checkOpen();
        if (this.cursorName != null && !this.cursorName.startsWith("_jtds")) {
            return this.cursorName;
        }
        throw new SQLException(Messages.get("error.resultset.noposupdate"), "24000");
    }
}
