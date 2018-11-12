package net.sourceforge.jtds.jdbc;

import android.support.v4.view.PointerIconCompat;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

public class JtdsStatement implements Statement {
    static final int BOOLEAN = 16;
    static final int CLOSE_ALL_RESULTS = 3;
    static final int CLOSE_CURRENT_RESULT = 1;
    static final int DATALINK = 70;
    static final int DEFAULT_FETCH_SIZE = 100;
    static final Integer EXECUTE_FAILED = new Integer(-3);
    static final String GENKEYCOL = "_JTDS_GENE_R_ATED_KEYS_";
    static final int KEEP_CURRENT_RESULT = 2;
    static final int NO_GENERATED_KEYS = 2;
    static final int RETURN_GENERATED_KEYS = 1;
    static final Integer SUCCESS_NO_INFO = new Integer(-2);
    protected ArrayList batchValues;
    private final int[] closed = new int[]{0};
    protected ColInfo[] colMetaData;
    protected ConnectionJDBC2 connection;
    protected JtdsResultSet currentResult;
    protected String cursorName;
    protected boolean escapeProcessing = true;
    protected int fetchDirection = 1000;
    protected int fetchSize = 100;
    protected CachedResultSet genKeyResultSet;
    protected int maxFieldSize;
    protected int maxRows;
    protected final SQLDiagnostic messages;
    protected ArrayList openResultSets;
    protected int queryTimeout;
    protected final LinkedList resultQueue = new LinkedList();
    protected int resultSetConcurrency = PointerIconCompat.TYPE_CROSSHAIR;
    protected int resultSetType = PointerIconCompat.TYPE_HELP;
    protected TdsCore tds;
    private int updateCount = -1;

    JtdsStatement(ConnectionJDBC2 connectionJDBC2, int i, int i2) throws SQLException {
        if (i >= PointerIconCompat.TYPE_HELP) {
            if (i <= PointerIconCompat.TYPE_CELL) {
                if (i2 >= PointerIconCompat.TYPE_CROSSHAIR) {
                    if (i2 <= PointerIconCompat.TYPE_ALIAS) {
                        this.connection = connectionJDBC2;
                        this.resultSetType = i;
                        this.resultSetConcurrency = i2;
                        this.tds = connectionJDBC2.getCachedTds();
                        if (this.tds == 0) {
                            this.messages = new SQLDiagnostic(connectionJDBC2.getServerType());
                            this.tds = new TdsCore(this.connection, this.messages);
                            return;
                        }
                        this.messages = this.tds.getMessages();
                        return;
                    }
                }
                connectionJDBC2 = (this instanceof JtdsCallableStatement) != null ? "prepareCall" : (this instanceof JtdsPreparedStatement) != null ? "prepareStatement" : "createStatement";
                throw new SQLException(Messages.get("error.generic.badparam", "resultSetConcurrency", connectionJDBC2), "HY092");
            }
        }
        connectionJDBC2 = (this instanceof JtdsCallableStatement) != null ? "prepareCall" : (this instanceof JtdsPreparedStatement) != null ? "prepareStatement" : "createStatement";
        throw new SQLException(Messages.get("error.generic.badparam", "resultSetType", connectionJDBC2), "HY092");
    }

    protected void finalize() throws java.lang.Throwable {
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
        r0 = this;
        super.finalize();
        r0.close();	 Catch:{ SQLException -> 0x0006 }
    L_0x0006:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsStatement.finalize():void");
    }

    TdsCore getTds() {
        return this.tds;
    }

    SQLDiagnostic getMessages() {
        return this.messages;
    }

    protected void checkOpen() throws SQLException {
        if (isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "Statement"), "HY010");
        }
    }

    protected void checkCursorException(SQLException sQLException) throws SQLException {
        if (!(this.connection == null || this.connection.isClosed() || "HYT00".equals(sQLException.getSQLState()))) {
            if (!"HY008".equals(sQLException.getSQLState())) {
                if (this.connection.getServerType() != 2) {
                    int errorCode = sQLException.getErrorCode();
                    if ((errorCode < 16900 || errorCode > 16999) && errorCode != 6819 && errorCode != 8654 && errorCode != 8162) {
                        throw sQLException;
                    }
                    return;
                }
                return;
            }
        }
        throw sQLException;
    }

    static void notImplemented(String str) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", (Object) str), "HYC00");
    }

    void closeCurrentResultSet() throws SQLException {
        try {
            if (this.currentResult != null) {
                this.currentResult.close();
            }
            this.currentResult = null;
        } catch (Throwable th) {
            this.currentResult = null;
        }
    }

    void closeAllResultSets() throws SQLException {
        try {
            if (this.openResultSets != null) {
                for (int i = 0; i < this.openResultSets.size(); i++) {
                    JtdsResultSet jtdsResultSet = (JtdsResultSet) this.openResultSets.get(i);
                    if (jtdsResultSet != null) {
                        jtdsResultSet.close();
                    }
                }
            }
            closeCurrentResultSet();
        } finally {
            this.openResultSets = null;
        }
    }

    void addWarning(SQLWarning sQLWarning) {
        this.messages.addWarning(sQLWarning);
    }

    protected SQLException executeMSBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        JtdsStatement jtdsStatement = this;
        int i3 = i;
        SQLException sQLException = null;
        int i4 = 0;
        while (i4 < i3) {
            boolean z;
            Object obj = jtdsStatement.batchValues.get(i4);
            i4++;
            if (i4 % i2 != 0) {
                if (i4 != i3) {
                    z = false;
                    jtdsStatement.tds.startBatch();
                    jtdsStatement.tds.executeSQL((String) obj, null, null, false, 0, -1, -1, z);
                    if (z) {
                        sQLException = jtdsStatement.tds.getBatchCounts(arrayList, sQLException);
                        if (!(sQLException == null || arrayList.size() == i4)) {
                            break;
                        }
                    }
                    ArrayList arrayList2 = arrayList;
                }
            }
            z = true;
            jtdsStatement.tds.startBatch();
            jtdsStatement.tds.executeSQL((String) obj, null, null, false, 0, -1, -1, z);
            if (z) {
                sQLException = jtdsStatement.tds.getBatchCounts(arrayList, sQLException);
                break;
            }
            ArrayList arrayList22 = arrayList;
        }
        return sQLException;
    }

    protected SQLException executeSybaseBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        JtdsStatement jtdsStatement = this;
        int i3 = i;
        StringBuffer stringBuffer = new StringBuffer(i3 * 32);
        SQLException sQLException = null;
        int i4 = 0;
        while (i4 < i3) {
            Object obj;
            Object obj2 = jtdsStatement.batchValues.get(i4);
            i4++;
            if (i4 % i2 != 0) {
                if (i4 != i3) {
                    obj = null;
                    stringBuffer.append((String) obj2);
                    stringBuffer.append(' ');
                    if (obj != null) {
                        jtdsStatement.tds.executeSQL(stringBuffer.toString(), null, null, false, 0, -1, -1, true);
                        stringBuffer.setLength(0);
                        sQLException = jtdsStatement.tds.getBatchCounts(arrayList, sQLException);
                        if (!(sQLException == null || arrayList.size() == i4)) {
                            break;
                        }
                    }
                    ArrayList arrayList2 = arrayList;
                }
            }
            obj = 1;
            stringBuffer.append((String) obj2);
            stringBuffer.append(' ');
            if (obj != null) {
                jtdsStatement.tds.executeSQL(stringBuffer.toString(), null, null, false, 0, -1, -1, true);
                stringBuffer.setLength(0);
                sQLException = jtdsStatement.tds.getBatchCounts(arrayList, sQLException);
                break;
            }
            ArrayList arrayList22 = arrayList;
        }
        return sQLException;
    }

    protected ResultSet executeSQLQuery(String str, String str2, ParamInfo[] paramInfoArr, boolean z) throws SQLException {
        JtdsStatement jtdsStatement = this;
        String stringBuffer;
        if (z) {
            try {
                if (jtdsStatement.connection.getServerType() == 1) {
                    jtdsStatement.currentResult = new MSCursorResultSet(jtdsStatement, str, str2, paramInfoArr, jtdsStatement.resultSetType, jtdsStatement.resultSetConcurrency);
                    return jtdsStatement.currentResult;
                }
                jtdsStatement.currentResult = new CachedResultSet(jtdsStatement, str, str2, paramInfoArr, jtdsStatement.resultSetType, jtdsStatement.resultSetConcurrency);
                return jtdsStatement.currentResult;
            } catch (SQLException e) {
                SQLException sQLException = e;
                checkCursorException(sQLException);
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append('[');
                stringBuffer2.append(sQLException.getSQLState());
                stringBuffer2.append("] ");
                stringBuffer2.append(sQLException.getMessage());
                stringBuffer = stringBuffer2.toString();
            }
        } else {
            stringBuffer = null;
            Object obj = stringBuffer;
            if (str2 != null && jtdsStatement.connection.getUseMetadataCache() && jtdsStatement.connection.getPrepareSql() == 3 && jtdsStatement.colMetaData != null && jtdsStatement.connection.getServerType() == 1) {
                jtdsStatement.tds.setColumns(jtdsStatement.colMetaData);
                jtdsStatement.tds.executeSQL(str, str2, paramInfoArr, true, jtdsStatement.queryTimeout, jtdsStatement.maxRows, jtdsStatement.maxFieldSize, true);
            } else {
                jtdsStatement.tds.executeSQL(str, str2, paramInfoArr, false, jtdsStatement.queryTimeout, jtdsStatement.maxRows, jtdsStatement.maxFieldSize, true);
            }
            if (obj != null) {
                addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", obj), "01000"));
            }
            while (!jtdsStatement.tds.getMoreResults() && !jtdsStatement.tds.isEndOfResponse()) {
            }
            jtdsStatement.messages.checkErrors();
            if (jtdsStatement.tds.isResultSet()) {
                jtdsStatement.currentResult = new JtdsResultSet(jtdsStatement, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR, jtdsStatement.tds.getColumns());
                return jtdsStatement.currentResult;
            }
            throw new SQLException(Messages.get("error.statement.noresult"), "24000");
        }
    }

    protected boolean executeSQL(String str, String str2, ParamInfo[] paramInfoArr, boolean z, boolean z2) throws SQLException {
        Object stringBuffer;
        boolean z3 = z;
        if (this.connection.getServerType() == 1 && !z3 && z2) {
            try {
                r8.currentResult = new MSCursorResultSet(r8, str, str2, paramInfoArr, r8.resultSetType, r8.resultSetConcurrency);
                return true;
            } catch (SQLException e) {
                SQLException sQLException = e;
                checkCursorException(sQLException);
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append('[');
                stringBuffer2.append(sQLException.getSQLState());
                stringBuffer2.append("] ");
                stringBuffer2.append(sQLException.getMessage());
                stringBuffer = stringBuffer2.toString();
            }
        } else {
            stringBuffer = null;
        }
        r8.tds.executeSQL(str, str2, paramInfoArr, false, r8.queryTimeout, r8.maxRows, r8.maxFieldSize, true);
        if (stringBuffer != null) {
            addWarning(new SQLWarning(Messages.get("warning.cursordowngraded", stringBuffer), "01000"));
        }
        if (!processResults(z3)) {
            return false;
        }
        stringBuffer = r8.resultQueue.removeFirst();
        if (stringBuffer instanceof Integer) {
            r8.updateCount = ((Integer) stringBuffer).intValue();
            return false;
        }
        r8.currentResult = (JtdsResultSet) stringBuffer;
        return true;
    }

    private boolean processResults(boolean z) throws SQLException {
        if (this.resultQueue.isEmpty()) {
            while (!this.tds.isEndOfResponse()) {
                if (this.tds.getMoreResults()) {
                    ColInfo[] columns = this.tds.getColumns();
                    if (columns.length == 1 && columns[0].name.equals(GENKEYCOL)) {
                        columns[0].name = "ID";
                        this.genKeyResultSet = null;
                        while (this.tds.getNextRow()) {
                            if (this.genKeyResultSet == null) {
                                this.genKeyResultSet = new CachedResultSet(this, this.tds.getColumns(), this.tds.getRowData());
                            } else {
                                this.genKeyResultSet.addRow(this.tds.getRowData());
                            }
                        }
                    } else if (z && this.resultQueue.isEmpty()) {
                        z = new SQLException(Messages.get("error.statement.nocount"), "07000");
                        z.setNextException(this.messages.exceptions);
                        throw z;
                    } else {
                        Object[] computedRowData = this.tds.getComputedRowData();
                        if (computedRowData == true) {
                            this.resultQueue.add(new CachedResultSet(this, this.tds.getComputedColumns(), computedRowData));
                        } else {
                            this.resultQueue.add(new JtdsResultSet(this, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR, this.tds.getColumns()));
                        }
                        getMessages().checkErrors();
                        return this.resultQueue.isEmpty() ^ true;
                    }
                } else if (this.tds.isUpdateCount()) {
                    if (z && this.connection.getLastUpdateCount()) {
                        this.resultQueue.clear();
                    }
                    this.resultQueue.addLast(new Integer(this.tds.getUpdateCount()));
                }
            }
            getMessages().checkErrors();
            return this.resultQueue.isEmpty() ^ true;
        }
        throw new IllegalStateException("There should be no queued results.");
    }

    protected void cacheResults() throws SQLException {
        processResults(false);
    }

    protected void reset() throws SQLException {
        this.updateCount = -1;
        this.resultQueue.clear();
        this.genKeyResultSet = null;
        this.tds.clearResponseQueue();
        this.messages.clearWarnings();
        this.messages.exceptions = null;
        closeAllResultSets();
    }

    private boolean executeImpl(String str, int i, boolean z) throws SQLException {
        reset();
        if (str != null) {
            if (str.length() != 0) {
                String str2 = "";
                if (this.escapeProcessing) {
                    str = SQLParser.parse(str, null, this.connection, false);
                    if (str[1].length() != 0) {
                        throw new SQLException(Messages.get("error.statement.badsql"), "07000");
                    }
                    String str3 = str[0];
                    str2 = str[2];
                    str = str3;
                } else {
                    str = str.trim();
                    if (str.length() > 5) {
                        str2 = str.substring(0, 6).toLowerCase();
                    }
                }
                if (i == 1) {
                    i = 1;
                } else if (i == 2) {
                    i = 0;
                } else {
                    throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "autoGeneratedKeys"), "HY092");
                }
                if (i != 0) {
                    StringBuffer stringBuffer;
                    if (this.connection.getServerType() != 1 || this.connection.getDatabaseMajorVersion() < 8) {
                        stringBuffer = new StringBuffer();
                        stringBuffer.append(str);
                        stringBuffer.append(" SELECT @@IDENTITY AS _JTDS_GENE_R_ATED_KEYS_");
                        str = stringBuffer.toString();
                    } else {
                        stringBuffer = new StringBuffer();
                        stringBuffer.append(str);
                        stringBuffer.append(" SELECT SCOPE_IDENTITY() AS _JTDS_GENE_R_ATED_KEYS_");
                        str = stringBuffer.toString();
                    }
                }
                String str4 = str;
                boolean z2 = (z || useCursor(i, str2) == null) ? false : true;
                return executeSQL(str4, null, null, z, z2);
            }
        }
        throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
    }

    protected boolean useCursor(boolean z, String str) {
        return ((this.resultSetType == PointerIconCompat.TYPE_HELP && this.resultSetConcurrency == PointerIconCompat.TYPE_CROSSHAIR && !this.connection.getUseCursors() && this.cursorName == null) || z || (str != null && !"select".equals(str) && !str.startsWith("exec"))) ? false : true;
    }

    int getDefaultFetchSize() {
        return (this.maxRows <= 0 || this.maxRows >= 100) ? 100 : this.maxRows;
    }

    public int getFetchDirection() throws SQLException {
        checkOpen();
        return this.fetchDirection;
    }

    public int getFetchSize() throws SQLException {
        checkOpen();
        return this.fetchSize;
    }

    public int getMaxFieldSize() throws SQLException {
        checkOpen();
        return this.maxFieldSize;
    }

    public int getMaxRows() throws SQLException {
        checkOpen();
        return this.maxRows;
    }

    public int getQueryTimeout() throws SQLException {
        checkOpen();
        return this.queryTimeout;
    }

    public int getResultSetConcurrency() throws SQLException {
        checkOpen();
        return this.resultSetConcurrency;
    }

    public int getResultSetHoldability() throws SQLException {
        checkOpen();
        return 1;
    }

    public int getResultSetType() throws SQLException {
        checkOpen();
        return this.resultSetType;
    }

    public int getUpdateCount() throws SQLException {
        checkOpen();
        return this.updateCount;
    }

    public void cancel() throws SQLException {
        checkOpen();
        if (this.tds != null) {
            this.tds.cancel(false);
        }
    }

    public void clearBatch() throws SQLException {
        checkOpen();
        if (this.batchValues != null) {
            this.batchValues.clear();
        }
    }

    public void clearWarnings() throws SQLException {
        checkOpen();
        this.messages.clearWarnings();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void close() throws SQLException {
        synchronized (this.closed) {
            if (this.closed[0] != 0) {
                return;
            }
            this.closed[0] = 1;
        }
        if (r0 != null) {
            throw r0;
        }
        throw r1;
        if (r0 != null) {
            throw r0;
        }
    }

    public boolean getMoreResults() throws SQLException {
        checkOpen();
        return getMoreResults(3);
    }

    public int[] executeBatch() throws SQLException, BatchUpdateException {
        checkOpen();
        reset();
        if (this.batchValues != null) {
            if (this.batchValues.size() != 0) {
                int size = this.batchValues.size();
                int batchSize = this.connection.getBatchSize();
                if (batchSize == 0) {
                    batchSize = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                }
                ArrayList arrayList = new ArrayList(size);
                BatchUpdateException batchUpdateException;
                try {
                    SQLException executeSybaseBatch;
                    synchronized (this.connection) {
                        if (this.connection.getServerType() == 2 && this.connection.getTdsVersion() == 2) {
                            executeSybaseBatch = executeSybaseBatch(size, batchSize, arrayList);
                        } else {
                            executeSybaseBatch = executeMSBatch(size, batchSize, arrayList);
                        }
                    }
                    int[] iArr = new int[size];
                    int size2 = arrayList.size();
                    int i = 0;
                    while (i < size2 && i < size) {
                        iArr[i] = ((Integer) arrayList.get(i)).intValue();
                        i++;
                    }
                    while (size2 < iArr.length) {
                        iArr[size2] = EXECUTE_FAILED.intValue();
                        size2++;
                    }
                    if (executeSybaseBatch != null) {
                        batchUpdateException = new BatchUpdateException(executeSybaseBatch.getMessage(), executeSybaseBatch.getSQLState(), executeSybaseBatch.getErrorCode(), iArr);
                        batchUpdateException.setNextException(executeSybaseBatch.getNextException());
                        throw batchUpdateException;
                    }
                    clearBatch();
                    return iArr;
                } catch (BatchUpdateException batchUpdateException2) {
                    throw batchUpdateException2;
                } catch (SQLException e) {
                    try {
                        throw new BatchUpdateException(e.getMessage(), e.getSQLState(), e.getErrorCode(), new int[0]);
                    } catch (Throwable th) {
                        clearBatch();
                    }
                }
            }
        }
        return new int[0];
    }

    public void setFetchDirection(int i) throws SQLException {
        checkOpen();
        switch (i) {
            case 1000:
            case PointerIconCompat.TYPE_CONTEXT_MENU /*1001*/:
            case PointerIconCompat.TYPE_HAND /*1002*/:
                this.fetchDirection = i;
                return;
            default:
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "direction"), "24000");
        }
    }

    public void setFetchSize(int i) throws SQLException {
        checkOpen();
        if (i < 0) {
            throw new SQLException(Messages.get("error.generic.optltzero", (Object) "setFetchSize"), "HY092");
        } else if (this.maxRows <= 0 || i <= this.maxRows) {
            if (i == 0) {
                i = getDefaultFetchSize();
            }
            this.fetchSize = i;
        } else {
            throw new SQLException(Messages.get("error.statement.gtmaxrows"), "HY092");
        }
    }

    public void setMaxFieldSize(int i) throws SQLException {
        checkOpen();
        if (i < 0) {
            throw new SQLException(Messages.get("error.generic.optltzero", (Object) "setMaxFieldSize"), "HY092");
        }
        this.maxFieldSize = i;
    }

    public void setMaxRows(int i) throws SQLException {
        checkOpen();
        if (i < 0) {
            throw new SQLException(Messages.get("error.generic.optltzero", (Object) "setMaxRows"), "HY092");
        }
        if (i > 0 && i < this.fetchSize) {
            this.fetchSize = i;
        }
        this.maxRows = i;
    }

    public void setQueryTimeout(int i) throws SQLException {
        checkOpen();
        if (i < 0) {
            throw new SQLException(Messages.get("error.generic.optltzero", (Object) "setQueryTimeout"), "HY092");
        }
        this.queryTimeout = i;
    }

    public boolean getMoreResults(int i) throws SQLException {
        checkOpen();
        switch (i) {
            case 1:
                this.updateCount = -1;
                closeCurrentResultSet();
                break;
            case 2:
                this.updateCount = -1;
                if (this.openResultSets == 0) {
                    this.openResultSets = new ArrayList();
                }
                if ((this.currentResult instanceof MSCursorResultSet) == 0) {
                    if ((this.currentResult instanceof CachedResultSet) == 0) {
                        if (this.currentResult != 0) {
                            this.currentResult.cacheResultSetRows();
                            this.openResultSets.add(this.currentResult);
                        }
                        this.currentResult = 0;
                        break;
                    }
                }
                this.openResultSets.add(this.currentResult);
                this.currentResult = 0;
            case 3:
                this.updateCount = -1;
                closeAllResultSets();
                break;
            default:
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "current"), "HY092");
        }
        this.messages.checkErrors();
        if (this.resultQueue.isEmpty() != 0) {
            if (processResults(false) == 0) {
                return false;
            }
        }
        i = this.resultQueue.removeFirst();
        if (i instanceof Integer) {
            this.updateCount = ((Integer) i).intValue();
            return false;
        }
        this.currentResult = (JtdsResultSet) i;
        return true;
    }

    public void setEscapeProcessing(boolean z) throws SQLException {
        checkOpen();
        this.escapeProcessing = z;
    }

    public int executeUpdate(String str) throws SQLException {
        return executeUpdate(str, 2);
    }

    public void addBatch(String str) throws SQLException {
        checkOpen();
        if (str == null) {
            throw new NullPointerException();
        }
        if (this.batchValues == null) {
            this.batchValues = new ArrayList();
        }
        if (this.escapeProcessing) {
            str = SQLParser.parse(str, null, this.connection, false);
            if (str[1].length() != 0) {
                throw new SQLException(Messages.get("error.statement.badsql"), "07000");
            }
            str = str[0];
        }
        this.batchValues.add(str);
    }

    public void setCursorName(String str) throws SQLException {
        checkOpen();
        this.cursorName = str;
        if (str != null) {
            this.resultSetType = PointerIconCompat.TYPE_HELP;
            this.fetchSize = 1;
        }
    }

    public boolean execute(String str) throws SQLException {
        checkOpen();
        return executeImpl(str, 2, false);
    }

    public int executeUpdate(String str, int i) throws SQLException {
        checkOpen();
        executeImpl(str, i, true);
        str = getUpdateCount();
        return str == -1 ? null : str;
    }

    public boolean execute(String str, int i) throws SQLException {
        checkOpen();
        return executeImpl(str, i, false);
    }

    public int executeUpdate(String str, int[] iArr) throws SQLException {
        checkOpen();
        if (iArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) "executeUpdate"), "HY092");
        } else if (iArr.length == 1) {
            return executeUpdate(str, 1);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolindex", (Object) "executeUpdate"), "HY092");
        }
    }

    public boolean execute(String str, int[] iArr) throws SQLException {
        checkOpen();
        if (iArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) "execute"), "HY092");
        } else if (iArr.length == 1) {
            return executeImpl(str, 1, null);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolindex", (Object) "execute"), "HY092");
        }
    }

    public Connection getConnection() throws SQLException {
        checkOpen();
        return this.connection;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        checkOpen();
        if (this.genKeyResultSet == null) {
            this.genKeyResultSet = new CachedResultSet(this, new String[]{"ID"}, new int[]{4});
        }
        this.genKeyResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return this.genKeyResultSet;
    }

    public ResultSet getResultSet() throws SQLException {
        checkOpen();
        if (!(this.currentResult instanceof MSCursorResultSet)) {
            if (!(this.currentResult instanceof CachedResultSet)) {
                if (this.currentResult != null) {
                    if (this.resultSetType != PointerIconCompat.TYPE_HELP || this.resultSetConcurrency != PointerIconCompat.TYPE_CROSSHAIR) {
                        this.currentResult = new CachedResultSet(this.currentResult, true);
                        return this.currentResult;
                    }
                }
                return this.currentResult;
            }
        }
        return this.currentResult;
    }

    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return this.messages.getWarnings();
    }

    public int executeUpdate(String str, String[] strArr) throws SQLException {
        checkOpen();
        if (strArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) "executeUpdate"), "HY092");
        } else if (strArr.length == 1) {
            return executeUpdate(str, 1);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolname", (Object) "executeUpdate"), "HY092");
        }
    }

    public boolean execute(String str, String[] strArr) throws SQLException {
        checkOpen();
        if (strArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) "execute"), "HY092");
        } else if (strArr.length == 1) {
            return executeImpl(str, 1, null);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolname", (Object) "execute"), "HY092");
        }
    }

    public ResultSet executeQuery(String str) throws SQLException {
        checkOpen();
        reset();
        if (str != null) {
            if (str.length() != 0) {
                if (this.escapeProcessing) {
                    str = SQLParser.parse(str, null, this.connection, false);
                    if (str[1].length() != 0) {
                        throw new SQLException(Messages.get("error.statement.badsql"), "07000");
                    }
                    str = str[0];
                }
                return executeSQLQuery(str, null, null, useCursor(false, null));
            }
        }
        throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
    }

    public boolean isClosed() throws SQLException {
        boolean z;
        synchronized (this.closed) {
            z = false;
            if (this.closed[0] == 2) {
                z = true;
            }
        }
        return z;
    }
}
