package net.sourceforge.jtds.jdbc;

import android.support.v4.view.MotionEventCompat;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class JtdsPreparedStatement extends JtdsStatement implements PreparedStatement {
    static /* synthetic */ Class array$Lnet$sourceforge$jtds$jdbc$ParamInfo;
    static /* synthetic */ Class class$net$sourceforge$jtds$jdbc$ConnectionJDBC2;
    private static final NumberFormat f = NumberFormat.getInstance();
    Collection handles;
    private final String originalSql;
    protected ParamInfo[] paramMetaData;
    protected ParamInfo[] parameters;
    protected String procName;
    private boolean returnKeys;
    protected final String sql;
    protected String sqlWord;

    JtdsPreparedStatement(ConnectionJDBC2 connectionJDBC2, String str, int i, int i2, boolean z) throws SQLException {
        super(connectionJDBC2, i, i2);
        this.originalSql = str;
        i = this instanceof JtdsCallableStatement;
        if (i != 0) {
            str = normalizeCall(str);
        }
        i2 = new ArrayList();
        str = SQLParser.parse(str, i2, connectionJDBC2, false);
        if (str[0].length() == 0) {
            throw new SQLException(Messages.get("error.prepare.nosql"), "07000");
        }
        if (str[1].length() > 1 && i != 0) {
            this.procName = str[1];
        }
        this.sqlWord = str[2];
        if (z) {
            if (connectionJDBC2.getServerType() != 1 || connectionJDBC2.getDatabaseMajorVersion() < 8) {
                connectionJDBC2 = new StringBuffer();
                connectionJDBC2.append(str[0]);
                connectionJDBC2.append(" SELECT @@IDENTITY AS ");
                connectionJDBC2.append("_JTDS_GENE_R_ATED_KEYS_");
                this.sql = connectionJDBC2.toString();
            } else {
                connectionJDBC2 = new StringBuffer();
                connectionJDBC2.append(str[0]);
                connectionJDBC2.append(" SELECT SCOPE_IDENTITY() AS ");
                connectionJDBC2.append("_JTDS_GENE_R_ATED_KEYS_");
                this.sql = connectionJDBC2.toString();
            }
            this.returnKeys = true;
        } else {
            this.sql = str[0];
            this.returnKeys = false;
        }
        this.parameters = (ParamInfo[]) i2.toArray(new ParamInfo[i2.size()]);
    }

    public String toString() {
        return this.originalSql;
    }

    protected static String normalizeCall(String str) throws SQLException {
        try {
            return normalize(str, 0);
        } catch (SQLException e) {
            if (e.getSQLState() == null) {
                return str;
            }
            throw e;
        }
    }

    private static String normalize(String str, int i) throws SQLException {
        if (i > 1) {
            throw new SQLException();
        }
        int length = str.length();
        int i2 = 0;
        int i3 = -1;
        int i4 = -1;
        int i5 = -1;
        while (i2 < length && i3 < 0) {
            while (Character.isWhitespace(str.charAt(i2))) {
                i2++;
            }
            char charAt = str.charAt(i2);
            int i6;
            if (charAt == '-') {
                i6 = i2 + 1;
                if (i6 < length && str.charAt(i6) == '-') {
                    i2 += 2;
                    while (i2 < length && str.charAt(i2) != '\n' && str.charAt(i2) != '\r') {
                        i2++;
                    }
                }
            } else if (charAt == '/') {
                i6 = i2 + 1;
                if (i6 < length && str.charAt(i6) == '*') {
                    i2 = 1;
                    while (i6 < length - 1) {
                        int i7;
                        i6++;
                        if (str.charAt(i6) == '/') {
                            i7 = i6 + 1;
                            if (str.charAt(i7) == '*') {
                                i2++;
                                i6 = i7;
                                continue;
                                if (i2 <= 0) {
                                    i2 = i6;
                                }
                            }
                        }
                        if (str.charAt(i6) == '*') {
                            i7 = i6 + 1;
                            if (str.charAt(i7) == '/') {
                                i2--;
                                i6 = i7;
                                continue;
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                        if (i2 <= 0) {
                            i2 = i6;
                        }
                    }
                    throw new SQLException(Messages.get("error.parsesql.missing", (Object) "*/"), "22025");
                }
            } else if (charAt != '=') {
                if (charAt != '?') {
                    if (charAt == '{') {
                        return str;
                    }
                    i3 = length - i2;
                    StringBuffer stringBuffer;
                    if ((i3 > 4 && str.substring(i2, i2 + 5).equalsIgnoreCase("exec ")) || str.substring(i2, i2 + 5).equalsIgnoreCase("call ")) {
                        stringBuffer = new StringBuffer();
                        stringBuffer.append(str.substring(0, i2));
                        stringBuffer.append(str.substring(i2 + 4, str.length()));
                        return normalize(stringBuffer.toString(), i);
                    } else if (i3 <= 7 || !str.substring(i2, i2 + 8).equalsIgnoreCase("execute ")) {
                        i3 = i2;
                    } else {
                        stringBuffer = new StringBuffer();
                        stringBuffer.append(str.substring(0, i2));
                        stringBuffer.append(str.substring(i2 + 7, str.length()));
                        return normalize(stringBuffer.toString(), i);
                    }
                } else if (i5 == -1) {
                    i5 = i2;
                } else {
                    throw new SQLException();
                }
            } else if (i4 != -1 || i5 < 0) {
                throw new SQLException();
            } else {
                i4 = i2;
            }
            i2++;
        }
        if (i4 != -1 || i5 == -1) {
            i = i3 + 7;
            if (i < length) {
                i = str.substring(i3, i);
                if (i != 0 && (i.equalsIgnoreCase("insert ") || i.equalsIgnoreCase("update ") || i.equalsIgnoreCase("delete ") != 0)) {
                    throw new SQLException(Messages.get("error.parsesql.noprocedurecall"), "07000");
                }
            }
            i = new StringBuffer();
            i.append("{");
            i.append(str.substring(0, i3));
            i.append("call ");
            i.append(str.substring(i3));
            i.append(openComment(str, i3) != null ? "\n" : "");
            i.append("}");
            return i.toString();
        }
        throw new SQLException();
    }

    private static boolean openComment(String str, int i) throws SQLException {
        int length = str.length();
        while (i < length) {
            char charAt = str.charAt(i);
            int i2;
            if (charAt == '-') {
                i2 = i + 1;
                if (i2 < length && str.charAt(i2) == '-') {
                    i += 2;
                    while (i < length && str.charAt(i) != '\n' && str.charAt(i) != '\r') {
                        i++;
                    }
                    if (i == length) {
                        return true;
                    }
                }
            } else if (charAt == '/') {
                i2 = i + 1;
                if (i2 < length && str.charAt(i2) == '*') {
                    i = 1;
                    while (i2 < length - 1) {
                        int i3;
                        i2++;
                        if (str.charAt(i2) == '/') {
                            i3 = i2 + 1;
                            if (str.charAt(i3) == '*') {
                                i++;
                                i2 = i3;
                                continue;
                                if (i <= 0) {
                                    i = i2;
                                }
                            }
                        }
                        if (str.charAt(i2) == '*') {
                            i3 = i2 + 1;
                            if (str.charAt(i3) == '/') {
                                i--;
                                i2 = i3;
                                continue;
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                        if (i <= 0) {
                            i = i2;
                        }
                    }
                    throw new SQLException(Messages.get("error.parsesql.missing", (Object) "*/"), "22025");
                }
            }
            i++;
        }
        return null;
    }

    protected void checkOpen() throws SQLException {
        if (isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "PreparedStatement"), "HY010");
        }
    }

    protected void notSupported(String str) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notsup", (Object) str), "HYC00");
    }

    protected SQLException executeMSBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        int i3 = i;
        if (this.parameters.length == 0) {
            return super.executeMSBatch(i, i2, arrayList);
        }
        String[] strArr;
        int i4;
        Object obj;
        String str;
        boolean z;
        ArrayList arrayList2;
        SQLException sQLException = null;
        if (r6.connection.getPrepareSql() != 1) {
            if (r6.connection.getPrepareSql() != 3) {
                strArr = null;
                i4 = 0;
                while (i4 < i3) {
                    obj = r6.batchValues.get(i4);
                    str = strArr != null ? r6.procName : strArr[i4];
                    i4++;
                    if (i4 % i2 != 0) {
                        if (i4 == i3) {
                            z = false;
                            r6.tds.startBatch();
                            r6.tds.executeSQL(r6.sql, str, (ParamInfo[]) obj, false, 0, -1, -1, z);
                            if (!z) {
                                sQLException = r6.tds.getBatchCounts(arrayList, sQLException);
                                if (!(sQLException == null || arrayList.size() == i4)) {
                                    break;
                                }
                            }
                            arrayList2 = arrayList;
                        }
                    }
                    z = true;
                    r6.tds.startBatch();
                    r6.tds.executeSQL(r6.sql, str, (ParamInfo[]) obj, false, 0, -1, -1, z);
                    if (!z) {
                        sQLException = r6.tds.getBatchCounts(arrayList, sQLException);
                        break;
                    }
                    arrayList2 = arrayList;
                }
                return sQLException;
            }
        }
        strArr = new String[i3];
        for (int i5 = 0; i5 < i3; i5++) {
            strArr[i5] = r6.connection.prepareSQL(r6, r6.sql, (ParamInfo[]) r6.batchValues.get(i5), false, false);
        }
        i4 = 0;
        while (i4 < i3) {
            obj = r6.batchValues.get(i4);
            if (strArr != null) {
            }
            str = strArr != null ? r6.procName : strArr[i4];
            i4++;
            if (i4 % i2 != 0) {
                if (i4 == i3) {
                    z = false;
                    r6.tds.startBatch();
                    r6.tds.executeSQL(r6.sql, str, (ParamInfo[]) obj, false, 0, -1, -1, z);
                    if (!z) {
                        sQLException = r6.tds.getBatchCounts(arrayList, sQLException);
                        break;
                    }
                    arrayList2 = arrayList;
                }
            }
            z = true;
            r6.tds.startBatch();
            r6.tds.executeSQL(r6.sql, str, (ParamInfo[]) obj, false, 0, -1, -1, z);
            if (!z) {
                sQLException = r6.tds.getBatchCounts(arrayList, sQLException);
                break;
            }
            arrayList2 = arrayList;
        }
        return sQLException;
    }

    protected SQLException executeSybaseBatch(int i, int i2, ArrayList arrayList) throws SQLException {
        int i3 = i;
        if (this.parameters.length == 0) {
            return super.executeSybaseBatch(i, i2, arrayList);
        }
        int i4;
        StringBuffer stringBuffer;
        ArrayList arrayList2;
        SQLException sQLException;
        int i5;
        Object obj;
        Object obj2;
        int length;
        int i6;
        ParamInfo paramInfo;
        ArrayList arrayList3;
        if (r0.connection.getDatabaseMajorVersion() >= 12) {
            if (r0.connection.getDatabaseMajorVersion() != 12 || r0.connection.getDatabaseMinorVersion() >= 50) {
                i4 = 1000;
                stringBuffer = new StringBuffer(i3 * 32);
                if (r0.parameters.length * i2 <= i4) {
                    i4 /= r0.parameters.length;
                    if (i4 == 0) {
                        i4 = 1;
                    }
                } else {
                    i4 = i2;
                }
                arrayList2 = new ArrayList();
                sQLException = null;
                i5 = 0;
                while (i5 < i3) {
                    obj = r0.batchValues.get(i5);
                    i5++;
                    if (i5 % i4 != 0) {
                        if (i5 == i3) {
                            obj2 = null;
                            length = stringBuffer.length();
                            stringBuffer.append(r0.sql);
                            stringBuffer.append(' ');
                            for (i6 = 0; i6 < r0.parameters.length; i6++) {
                                paramInfo = ((ParamInfo[]) obj)[i6];
                                paramInfo.markerPos += length;
                                arrayList2.add(paramInfo);
                            }
                            if (obj2 == null) {
                                r0.tds.executeSQL(stringBuffer.toString(), null, (ParamInfo[]) arrayList2.toArray(new ParamInfo[arrayList2.size()]), false, 0, -1, -1, true);
                                stringBuffer.setLength(0);
                                arrayList2.clear();
                                sQLException = r0.tds.getBatchCounts(arrayList, sQLException);
                                if (!(sQLException == null || arrayList.size() == i5)) {
                                    break;
                                }
                            }
                            arrayList3 = arrayList;
                        }
                    }
                    obj2 = 1;
                    length = stringBuffer.length();
                    stringBuffer.append(r0.sql);
                    stringBuffer.append(' ');
                    for (i6 = 0; i6 < r0.parameters.length; i6++) {
                        paramInfo = ((ParamInfo[]) obj)[i6];
                        paramInfo.markerPos += length;
                        arrayList2.add(paramInfo);
                    }
                    if (obj2 == null) {
                        r0.tds.executeSQL(stringBuffer.toString(), null, (ParamInfo[]) arrayList2.toArray(new ParamInfo[arrayList2.size()]), false, 0, -1, -1, true);
                        stringBuffer.setLength(0);
                        arrayList2.clear();
                        sQLException = r0.tds.getBatchCounts(arrayList, sQLException);
                        break;
                    }
                    arrayList3 = arrayList;
                }
                return sQLException;
            }
        }
        i4 = 200;
        stringBuffer = new StringBuffer(i3 * 32);
        if (r0.parameters.length * i2 <= i4) {
            i4 = i2;
        } else {
            i4 /= r0.parameters.length;
            if (i4 == 0) {
                i4 = 1;
            }
        }
        arrayList2 = new ArrayList();
        sQLException = null;
        i5 = 0;
        while (i5 < i3) {
            obj = r0.batchValues.get(i5);
            i5++;
            if (i5 % i4 != 0) {
                if (i5 == i3) {
                    obj2 = null;
                    length = stringBuffer.length();
                    stringBuffer.append(r0.sql);
                    stringBuffer.append(' ');
                    for (i6 = 0; i6 < r0.parameters.length; i6++) {
                        paramInfo = ((ParamInfo[]) obj)[i6];
                        paramInfo.markerPos += length;
                        arrayList2.add(paramInfo);
                    }
                    if (obj2 == null) {
                        r0.tds.executeSQL(stringBuffer.toString(), null, (ParamInfo[]) arrayList2.toArray(new ParamInfo[arrayList2.size()]), false, 0, -1, -1, true);
                        stringBuffer.setLength(0);
                        arrayList2.clear();
                        sQLException = r0.tds.getBatchCounts(arrayList, sQLException);
                        break;
                    }
                    arrayList3 = arrayList;
                }
            }
            obj2 = 1;
            length = stringBuffer.length();
            stringBuffer.append(r0.sql);
            stringBuffer.append(' ');
            for (i6 = 0; i6 < r0.parameters.length; i6++) {
                paramInfo = ((ParamInfo[]) obj)[i6];
                paramInfo.markerPos += length;
                arrayList2.add(paramInfo);
            }
            if (obj2 == null) {
                r0.tds.executeSQL(stringBuffer.toString(), null, (ParamInfo[]) arrayList2.toArray(new ParamInfo[arrayList2.size()]), false, 0, -1, -1, true);
                stringBuffer.setLength(0);
                arrayList2.clear();
                sQLException = r0.tds.getBatchCounts(arrayList, sQLException);
                break;
            }
            arrayList3 = arrayList;
        }
        return sQLException;
    }

    protected ParamInfo getParameter(int i) throws SQLException {
        checkOpen();
        if (i >= 1) {
            if (i <= this.parameters.length) {
                return this.parameters[i - 1];
            }
        }
        throw new SQLException(Messages.get("error.prepare.paramindex", Integer.toString(i)), "07009");
    }

    public void setObjectBase(int i, Object obj, int i2, int i3) throws SQLException {
        Object obj2;
        int i4;
        checkOpen();
        int i5 = i2 == 2005 ? -1 : i2 == 2004 ? -4 : i2;
        if (obj != null) {
            obj = Support.convert(this, obj, i5, this.connection.getCharset());
            if (i3 >= 0) {
                if (obj instanceof BigDecimal) {
                    obj = ((BigDecimal) obj).setScale(i3, 4);
                } else if (obj instanceof Number) {
                    synchronized (f) {
                        f.setGroupingUsed(0);
                        f.setMaximumFractionDigits(i3);
                        obj = Support.convert(this, f.format(obj), i5, this.connection.getCharset());
                    }
                }
            }
            if (obj instanceof Blob) {
                Blob blob = (Blob) obj;
                i2 = (int) blob.length();
                obj = blob.getBinaryStream();
            } else if (obj instanceof Clob) {
                Clob clob = (Clob) obj;
                i2 = (int) clob.length();
                obj = clob.getCharacterStream();
            }
            obj2 = obj;
            i4 = i2;
            setParameter(i, obj2, i5, i3, i4);
        }
        obj2 = obj;
        i4 = 0;
        setParameter(i, obj2, i5, i3, i4);
    }

    protected void setParameter(int i, Object obj, int i2, int i3, int i4) throws SQLException {
        i = getParameter(i);
        if ("ERROR".equals(Support.getJdbcTypeName(i2))) {
            throw new SQLException(Messages.get("error.generic.badtype", Integer.toString(i2)), "HY092");
        }
        if (i2 != 3) {
            if (i2 != 2) {
                if (i3 < 0) {
                    i3 = 0;
                }
                i.scale = i3;
                if ((obj instanceof String) != 0) {
                    i.length = ((String) obj).length();
                } else if ((obj instanceof byte[]) == 0) {
                    i.length = ((byte[]) obj).length;
                } else {
                    i.length = i4;
                }
                i3 = (obj instanceof Date) == 0 ? new DateTime((Date) obj) : (obj instanceof Time) == 0 ? new DateTime((Time) obj) : (obj instanceof Timestamp) == 0 ? new DateTime((Timestamp) obj) : obj;
                i.value = i3;
                i.jdbcType = i2;
                i.isSet = true;
                i.isUnicode = this.connection.getUseUnicode();
            }
        }
        i.precision = this.connection.getMaxPrecision();
        if (obj instanceof BigDecimal) {
            obj = Support.normalizeBigDecimal((BigDecimal) obj, i.precision);
            i.scale = ((BigDecimal) obj).scale();
        } else {
            if (i3 < 0) {
                i3 = 10;
            }
            i.scale = i3;
        }
        if ((obj instanceof String) != 0) {
            i.length = ((String) obj).length();
        } else if ((obj instanceof byte[]) == 0) {
            i.length = i4;
        } else {
            i.length = ((byte[]) obj).length;
        }
        if ((obj instanceof Date) == 0) {
            if ((obj instanceof Time) == 0) {
                if ((obj instanceof Timestamp) == 0) {
                }
            }
        }
        i.value = i3;
        i.jdbcType = i2;
        i.isSet = true;
        i.isUnicode = this.connection.getUseUnicode();
    }

    void setColMetaData(ColInfo[] colInfoArr) {
        this.colMetaData = colInfoArr;
    }

    void setParamMetaData(ParamInfo[] paramInfoArr) {
        int i = 0;
        while (i < paramInfoArr.length && i < this.parameters.length) {
            if (!this.parameters[i].isSet) {
                this.parameters[i].jdbcType = paramInfoArr[i].jdbcType;
                this.parameters[i].isOutput = paramInfoArr[i].isOutput;
                this.parameters[i].precision = paramInfoArr[i].precision;
                this.parameters[i].scale = paramInfoArr[i].scale;
                this.parameters[i].sqlType = paramInfoArr[i].sqlType;
            }
            i++;
        }
    }

    public void close() throws SQLException {
        try {
            super.close();
        } finally {
            this.handles = null;
            this.parameters = null;
        }
    }

    public int executeUpdate() throws SQLException {
        checkOpen();
        reset();
        if (this.procName != null || (this instanceof JtdsCallableStatement)) {
            executeSQL(this.sql, this.procName, this.parameters, true, false);
        } else {
            synchronized (this.connection) {
                executeSQL(this.sql, this.connection.prepareSQL(this, this.sql, this.parameters, this.returnKeys, false), this.parameters, true, false);
            }
        }
        int updateCount = getUpdateCount();
        return updateCount == -1 ? 0 : updateCount;
    }

    public void addBatch() throws SQLException {
        checkOpen();
        if (this.batchValues == null) {
            this.batchValues = new ArrayList();
        }
        if (this.parameters.length == 0) {
            this.batchValues.add(this.sql);
            return;
        }
        this.batchValues.add(this.parameters);
        ParamInfo[] paramInfoArr = new ParamInfo[this.parameters.length];
        for (int i = 0; i < this.parameters.length; i++) {
            paramInfoArr[i] = (ParamInfo) this.parameters[i].clone();
        }
        this.parameters = paramInfoArr;
    }

    public void clearParameters() throws SQLException {
        checkOpen();
        for (ParamInfo clearInValue : this.parameters) {
            clearInValue.clearInValue();
        }
    }

    public boolean execute() throws SQLException {
        checkOpen();
        reset();
        boolean useCursor = useCursor(this.returnKeys, this.sqlWord);
        if (this.procName != null || (this instanceof JtdsCallableStatement)) {
            return executeSQL(this.sql, this.procName, this.parameters, false, useCursor);
        }
        synchronized (this.connection) {
            useCursor = executeSQL(this.sql, this.connection.prepareSQL(this, this.sql, this.parameters, this.returnKeys, useCursor), this.parameters, false, useCursor);
        }
        return useCursor;
    }

    public void setByte(int i, byte b) throws SQLException {
        setParameter(i, new Integer(b & 255), -6, 0, 0);
    }

    public void setDouble(int i, double d) throws SQLException {
        setParameter(i, new Double(d), 8, 0, 0);
    }

    public void setFloat(int i, float f) throws SQLException {
        setParameter(i, new Float(f), 7, 0, 0);
    }

    public void setInt(int i, int i2) throws SQLException {
        setParameter(i, new Integer(i2), 4, 0, 0);
    }

    public void setNull(int i, int i2) throws SQLException {
        int i3 = i2 == 2005 ? -1 : i2 == 2004 ? -4 : i2;
        setParameter(i, null, i3, -1, 0);
    }

    public void setLong(int i, long j) throws SQLException {
        setParameter(i, new Long(j), -5, 0, 0);
    }

    public void setShort(int i, short s) throws SQLException {
        setParameter(i, new Integer(s), 5, 0, 0);
    }

    public void setBoolean(int i, boolean z) throws SQLException {
        setParameter(i, z ? Boolean.TRUE : Boolean.FALSE, 16, 0, 0);
    }

    public void setBytes(int i, byte[] bArr) throws SQLException {
        setParameter(i, bArr, -2, 0, 0);
    }

    public void setAsciiStream(int r9, java.io.InputStream r10, int r11) throws java.sql.SQLException {
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
        r8 = this;
        if (r10 == 0) goto L_0x0010;
    L_0x0002:
        if (r11 >= 0) goto L_0x0005;
    L_0x0004:
        goto L_0x0010;
    L_0x0005:
        r0 = new java.io.InputStreamReader;	 Catch:{ UnsupportedEncodingException -> 0x0019 }
        r1 = "US-ASCII";	 Catch:{ UnsupportedEncodingException -> 0x0019 }
        r0.<init>(r10, r1);	 Catch:{ UnsupportedEncodingException -> 0x0019 }
        r8.setCharacterStream(r9, r0, r11);	 Catch:{ UnsupportedEncodingException -> 0x0019 }
        goto L_0x0019;
    L_0x0010:
        r4 = 0;
        r5 = -1;
        r6 = 0;
        r7 = 0;
        r2 = r8;
        r3 = r9;
        r2.setParameter(r3, r4, r5, r6, r7);
    L_0x0019:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsPreparedStatement.setAsciiStream(int, java.io.InputStream, int):void");
    }

    public void setBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        checkOpen();
        if (inputStream != null) {
            if (i2 >= 0) {
                setParameter(i, inputStream, -4, 0, i2);
                return;
            }
        }
        setBytes(i, null);
    }

    public void setUnicodeStream(int i, InputStream inputStream, int i2) throws SQLException {
        checkOpen();
        if (inputStream != null) {
            if (i2 >= 0) {
                try {
                    i2 /= 2;
                    char[] cArr = new char[i2];
                    int read = inputStream.read();
                    int read2 = inputStream.read();
                    int i3 = 0;
                    while (read >= 0 && read2 >= 0 && i3 < i2) {
                        int i4 = i3 + 1;
                        cArr[i3] = (char) (((read << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (read2 & 255));
                        read = inputStream.read();
                        read2 = inputStream.read();
                        i3 = i4;
                    }
                    setString(i, new String(cArr, 0, i3));
                    return;
                } catch (int i5) {
                    throw new SQLException(Messages.get("error.generic.ioerror", i5.getMessage()), "HY000");
                }
            }
        }
        setString(i5, null);
    }

    public void setCharacterStream(int i, Reader reader, int i2) throws SQLException {
        if (reader != null) {
            if (i2 >= 0) {
                setParameter(i, reader, -1, 0, i2);
                return;
            }
        }
        setParameter(i, null, -1, 0, 0);
    }

    public void setObject(int i, Object obj) throws SQLException {
        setObjectBase(i, obj, Support.getJdbcType(obj), -1);
    }

    public void setObject(int i, Object obj, int i2) throws SQLException {
        setObjectBase(i, obj, i2, -1);
    }

    public void setObject(int i, Object obj, int i2, int i3) throws SQLException {
        checkOpen();
        if (i3 >= 0) {
            if (i3 <= this.connection.getMaxPrecision()) {
                setObjectBase(i, obj, i2, i3);
                return;
            }
        }
        throw new SQLException(Messages.get("error.generic.badscale"), "HY092");
    }

    public void setNull(int i, int i2, String str) throws SQLException {
        JtdsStatement.notImplemented("PreparedStatement.setNull(int, int, String)");
    }

    public void setString(int i, String str) throws SQLException {
        setParameter(i, str, 12, 0, 0);
    }

    public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        setParameter(i, bigDecimal, 3, -1, 0);
    }

    public void setURL(int i, URL url) throws SQLException {
        setString(i, url == null ? null : url.toString());
    }

    public void setArray(int i, Array array) throws SQLException {
        JtdsStatement.notImplemented("PreparedStatement.setArray");
    }

    public void setBlob(int i, Blob blob) throws SQLException {
        if (blob == null) {
            setBytes(i, null);
        } else if (blob.length() > 2147483647L) {
            throw new SQLException(Messages.get("error.resultset.longblob"), "24000");
        } else {
            setBinaryStream(i, blob.getBinaryStream(), (int) blob.length());
        }
    }

    public void setClob(int i, Clob clob) throws SQLException {
        if (clob == null) {
            setString(i, null);
        } else if (clob.length() > 2147483647L) {
            throw new SQLException(Messages.get("error.resultset.longclob"), "24000");
        } else {
            setCharacterStream(i, clob.getCharacterStream(), (int) clob.length());
        }
    }

    public void setDate(int i, Date date) throws SQLException {
        setParameter(i, date, 91, 0, 0);
    }

    public java.sql.ParameterMetaData getParameterMetaData() throws java.sql.SQLException {
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
        r9 = this;
        r9.checkOpen();
        r0 = r9.connection;
        r0 = r0.getServerType();
        r1 = 0;
        r2 = 2;
        if (r0 != r2) goto L_0x0019;
    L_0x000d:
        r3 = r9.connection;
        r5 = r9.sql;
        r6 = new net.sourceforge.jtds.jdbc.ParamInfo[r1];
        r7 = 0;
        r8 = 0;
        r4 = r9;
        r3.prepareSQL(r4, r5, r6, r7, r8);
    L_0x0019:
        r0 = "net.sourceforge.jtds.jdbc.ParameterMetaDataImpl";	 Catch:{ Exception -> 0x0059 }
        r0 = java.lang.Class.forName(r0);	 Catch:{ Exception -> 0x0059 }
        r3 = new java.lang.Class[r2];	 Catch:{ Exception -> 0x0059 }
        r4 = array$Lnet$sourceforge$jtds$jdbc$ParamInfo;	 Catch:{ Exception -> 0x0059 }
        if (r4 != 0) goto L_0x002e;	 Catch:{ Exception -> 0x0059 }
    L_0x0025:
        r4 = "[Lnet.sourceforge.jtds.jdbc.ParamInfo;";	 Catch:{ Exception -> 0x0059 }
        r4 = class$(r4);	 Catch:{ Exception -> 0x0059 }
        array$Lnet$sourceforge$jtds$jdbc$ParamInfo = r4;	 Catch:{ Exception -> 0x0059 }
        goto L_0x0030;	 Catch:{ Exception -> 0x0059 }
    L_0x002e:
        r4 = array$Lnet$sourceforge$jtds$jdbc$ParamInfo;	 Catch:{ Exception -> 0x0059 }
    L_0x0030:
        r3[r1] = r4;	 Catch:{ Exception -> 0x0059 }
        r4 = class$net$sourceforge$jtds$jdbc$ConnectionJDBC2;	 Catch:{ Exception -> 0x0059 }
        if (r4 != 0) goto L_0x003f;	 Catch:{ Exception -> 0x0059 }
    L_0x0036:
        r4 = "net.sourceforge.jtds.jdbc.ConnectionJDBC2";	 Catch:{ Exception -> 0x0059 }
        r4 = class$(r4);	 Catch:{ Exception -> 0x0059 }
        class$net$sourceforge$jtds$jdbc$ConnectionJDBC2 = r4;	 Catch:{ Exception -> 0x0059 }
        goto L_0x0041;	 Catch:{ Exception -> 0x0059 }
    L_0x003f:
        r4 = class$net$sourceforge$jtds$jdbc$ConnectionJDBC2;	 Catch:{ Exception -> 0x0059 }
    L_0x0041:
        r5 = 1;	 Catch:{ Exception -> 0x0059 }
        r3[r5] = r4;	 Catch:{ Exception -> 0x0059 }
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x0059 }
        r4 = r9.parameters;	 Catch:{ Exception -> 0x0059 }
        r2[r1] = r4;	 Catch:{ Exception -> 0x0059 }
        r1 = r9.connection;	 Catch:{ Exception -> 0x0059 }
        r2[r5] = r1;	 Catch:{ Exception -> 0x0059 }
        r0 = r0.getConstructor(r3);	 Catch:{ Exception -> 0x0059 }
        r0 = r0.newInstance(r2);	 Catch:{ Exception -> 0x0059 }
        r0 = (java.sql.ParameterMetaData) r0;	 Catch:{ Exception -> 0x0059 }
        return r0;
    L_0x0059:
        r0 = "PreparedStatement.getParameterMetaData";
        net.sourceforge.jtds.jdbc.JtdsStatement.notImplemented(r0);
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsPreparedStatement.getParameterMetaData():java.sql.ParameterMetaData");
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (String str2) {
            throw new NoClassDefFoundError(str2.getMessage());
        }
    }

    public void setRef(int i, Ref ref) throws SQLException {
        JtdsStatement.notImplemented("PreparedStatement.setRef");
    }

    public ResultSet executeQuery() throws SQLException {
        checkOpen();
        reset();
        boolean useCursor = useCursor(false, null);
        if (this.procName != null || (this instanceof JtdsCallableStatement)) {
            return executeSQLQuery(this.sql, this.procName, this.parameters, useCursor);
        }
        ResultSet executeSQLQuery;
        synchronized (this.connection) {
            executeSQLQuery = executeSQLQuery(this.sql, this.connection.prepareSQL(this, this.sql, this.parameters, false, useCursor), this.parameters, useCursor);
        }
        return executeSQLQuery;
    }

    public java.sql.ResultSetMetaData getMetaData() throws java.sql.SQLException {
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
        r9 = this;
        r9.checkOpen();
        r0 = r9.colMetaData;
        if (r0 != 0) goto L_0x0097;
    L_0x0007:
        r0 = r9.currentResult;
        if (r0 == 0) goto L_0x0013;
    L_0x000b:
        r0 = r9.currentResult;
        r0 = r0.columns;
        r9.colMetaData = r0;
        goto L_0x0097;
    L_0x0013:
        r0 = r9.connection;
        r0 = r0.getServerType();
        r1 = 2;
        r2 = 0;
        if (r0 != r1) goto L_0x002a;
    L_0x001d:
        r3 = r9.connection;
        r5 = r9.sql;
        r6 = new net.sourceforge.jtds.jdbc.ParamInfo[r2];
        r7 = 0;
        r8 = 0;
        r4 = r9;
        r3.prepareSQL(r4, r5, r6, r7, r8);
        goto L_0x0097;
    L_0x002a:
        r0 = "select";
        r1 = r9.sqlWord;
        r0 = r0.equals(r1);
        if (r0 != 0) goto L_0x003e;
    L_0x0034:
        r0 = "with";
        r1 = r9.sqlWord;
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0097;
    L_0x003e:
        r0 = r9.parameters;
        r0 = r0.length;
        r0 = new net.sourceforge.jtds.jdbc.ParamInfo[r0];
        r1 = 0;
    L_0x0044:
        r3 = r0.length;
        if (r1 >= r3) goto L_0x005c;
    L_0x0047:
        r3 = new net.sourceforge.jtds.jdbc.ParamInfo;
        r4 = r9.parameters;
        r4 = r4[r1];
        r4 = r4.markerPos;
        r3.<init>(r4, r2);
        r0[r1] = r3;
        r3 = r0[r1];
        r4 = 1;
        r3.isSet = r4;
        r1 = r1 + 1;
        goto L_0x0044;
    L_0x005c:
        r1 = new java.lang.StringBuffer;
        r2 = r9.sql;
        r2 = r2.length();
        r2 = r2 + 128;
        r1.<init>(r2);
        r2 = "SET FMTONLY ON; ";
        r1.append(r2);
        r2 = r9.sql;
        r3 = r9.connection;
        r0 = net.sourceforge.jtds.jdbc.Support.substituteParameters(r2, r0, r3);
        r1.append(r0);
        r0 = "; SET FMTONLY OFF";
        r1.append(r0);
        r0 = r9.tds;	 Catch:{ SQLException -> 0x0090 }
        r1 = r1.toString();	 Catch:{ SQLException -> 0x0090 }
        r0.submitSQL(r1);	 Catch:{ SQLException -> 0x0090 }
        r0 = r9.tds;	 Catch:{ SQLException -> 0x0090 }
        r0 = r0.getColumns();	 Catch:{ SQLException -> 0x0090 }
        r9.colMetaData = r0;	 Catch:{ SQLException -> 0x0090 }
        goto L_0x0097;
    L_0x0090:
        r0 = r9.tds;
        r1 = "SET FMTONLY OFF";
        r0.submitSQL(r1);
    L_0x0097:
        r0 = r9.colMetaData;
        if (r0 != 0) goto L_0x009d;
    L_0x009b:
        r0 = 0;
        goto L_0x00b0;
    L_0x009d:
        r0 = new net.sourceforge.jtds.jdbc.JtdsResultSetMetaData;
        r1 = r9.colMetaData;
        r2 = r9.colMetaData;
        r2 = net.sourceforge.jtds.jdbc.JtdsResultSet.getColumnCount(r2);
        r3 = r9.connection;
        r3 = r3.getUseLOBs();
        r0.<init>(r1, r2, r3);
    L_0x00b0:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.JtdsPreparedStatement.getMetaData():java.sql.ResultSetMetaData");
    }

    public void setTime(int i, Time time) throws SQLException {
        setParameter(i, time, 92, 0, 0);
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        setParameter(i, timestamp, 93, 0, 0);
    }

    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        if (!(date == null || calendar == null)) {
            date = new Date(Support.timeFromZone(date, calendar));
        }
        setDate(i, date);
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        if (!(time == null || calendar == null)) {
            time = new Time(Support.timeFromZone(time, calendar));
        }
        setTime(i, time);
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        if (!(timestamp == null || calendar == null)) {
            timestamp = new Timestamp(Support.timeFromZone(timestamp, calendar));
        }
        setTimestamp(i, timestamp);
    }

    public int executeUpdate(String str) throws SQLException {
        notSupported("executeUpdate(String)");
        return null;
    }

    public void addBatch(String str) throws SQLException {
        notSupported("executeBatch(String)");
    }

    public boolean execute(String str) throws SQLException {
        notSupported("execute(String)");
        return null;
    }

    public int executeUpdate(String str, int i) throws SQLException {
        notSupported("executeUpdate(String, int)");
        return null;
    }

    public boolean execute(String str, int i) throws SQLException {
        notSupported("execute(String, int)");
        return null;
    }

    public int executeUpdate(String str, int[] iArr) throws SQLException {
        notSupported("executeUpdate(String, int[])");
        return null;
    }

    public boolean execute(String str, int[] iArr) throws SQLException {
        notSupported("execute(String, int[])");
        return null;
    }

    public int executeUpdate(String str, String[] strArr) throws SQLException {
        notSupported("executeUpdate(String, String[])");
        return null;
    }

    public boolean execute(String str, String[] strArr) throws SQLException {
        notSupported("execute(String, String[])");
        return null;
    }

    public ResultSet executeQuery(String str) throws SQLException {
        notSupported("executeQuery(String)");
        return null;
    }
}
