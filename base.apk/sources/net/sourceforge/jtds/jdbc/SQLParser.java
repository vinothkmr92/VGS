package net.sourceforge.jtds.jdbc;

import android.support.v4.app.NotificationCompat;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import net.sourceforge.jtds.jdbc.cache.SQLCacheKey;
import net.sourceforge.jtds.jdbc.cache.SimpleLRUCache;

class SQLParser {
    private static SimpleLRUCache cache;
    private static HashMap cvMap = new HashMap();
    private static final byte[] dateMask = new byte[]{(byte) 35, (byte) 35, (byte) 35, (byte) 35, (byte) 45, (byte) 35, (byte) 35, (byte) 45, (byte) 35, (byte) 35};
    private static HashMap fnMap = new HashMap();
    private static boolean[] identifierChar = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false};
    private static HashMap msFnMap = new HashMap();
    private static final byte[] timeMask = new byte[]{(byte) 35, (byte) 35, (byte) 58, (byte) 35, (byte) 35, (byte) 58, (byte) 35, (byte) 35};
    static final byte[] timestampMask = new byte[]{(byte) 35, (byte) 35, (byte) 35, (byte) 35, (byte) 45, (byte) 35, (byte) 35, (byte) 45, (byte) 35, (byte) 35, (byte) 32, (byte) 35, (byte) 35, (byte) 58, (byte) 35, (byte) 35, (byte) 58, (byte) 35, (byte) 35};
    private final ConnectionJDBC2 connection;
    private int d;
    private final char[] in = this.sql.toCharArray();
    private String keyWord;
    private final int len = this.in.length;
    private char[] out = new char[this.len];
    private final ArrayList params;
    private String procName;
    private int s;
    private final String sql;
    private String tableName;
    private char terminator;

    private static class CachedSQLQuery {
        final boolean[] paramIsRetVal;
        final boolean[] paramIsUnicode;
        final int[] paramMarkerPos;
        final String[] paramNames;
        final String[] parsedSql;

        CachedSQLQuery(String[] strArr, ArrayList arrayList) {
            this.parsedSql = strArr;
            if (arrayList != null) {
                strArr = arrayList.size();
                this.paramNames = new String[strArr];
                this.paramMarkerPos = new int[strArr];
                this.paramIsRetVal = new boolean[strArr];
                this.paramIsUnicode = new boolean[strArr];
                for (int i = 0; i < strArr; i++) {
                    ParamInfo paramInfo = (ParamInfo) arrayList.get(i);
                    this.paramNames[i] = paramInfo.name;
                    this.paramMarkerPos[i] = paramInfo.markerPos;
                    this.paramIsRetVal[i] = paramInfo.isRetVal;
                    this.paramIsUnicode[i] = paramInfo.isUnicode;
                }
                return;
            }
            this.paramNames = null;
            this.paramMarkerPos = null;
            this.paramIsRetVal = null;
            this.paramIsUnicode = null;
        }
    }

    static String[] parse(String str, ArrayList arrayList, ConnectionJDBC2 connectionJDBC2, boolean z) throws SQLException {
        if (z) {
            return new SQLParser(str, arrayList, connectionJDBC2).parse(z);
        }
        SimpleLRUCache cache = getCache(connectionJDBC2);
        SQLCacheKey sQLCacheKey = new SQLCacheKey(str, connectionJDBC2);
        CachedSQLQuery cachedSQLQuery = (CachedSQLQuery) cache.get(sQLCacheKey);
        if (cachedSQLQuery == null) {
            str = new CachedSQLQuery(new SQLParser(str, arrayList, connectionJDBC2).parse(z), arrayList);
            cache.put(sQLCacheKey, str);
        } else {
            connectionJDBC2 = null;
            ConnectionJDBC2 length = cachedSQLQuery.paramNames == null ? null : cachedSQLQuery.paramNames.length;
            while (connectionJDBC2 < length) {
                arrayList.add(new ParamInfo(cachedSQLQuery.paramNames[connectionJDBC2], cachedSQLQuery.paramMarkerPos[connectionJDBC2], cachedSQLQuery.paramIsRetVal[connectionJDBC2], cachedSQLQuery.paramIsUnicode[connectionJDBC2]));
                connectionJDBC2++;
            }
            str = cachedSQLQuery;
        }
        return str.parsedSql;
    }

    private static synchronized SimpleLRUCache getCache(ConnectionJDBC2 connectionJDBC2) {
        synchronized (SQLParser.class) {
            if (cache == null) {
                cache = new SimpleLRUCache(Math.min(1000, Math.max(0, connectionJDBC2.getMaxStatements())));
            }
            connectionJDBC2 = cache;
        }
        return connectionJDBC2;
    }

    static {
        msFnMap.put("length", "len($)");
        msFnMap.put("truncate", "round($, 1)");
        fnMap.put("user", "user_name($)");
        fnMap.put("database", "db_name($)");
        fnMap.put("ifnull", "isnull($)");
        fnMap.put("now", "getdate($)");
        fnMap.put("atan2", "atn2($)");
        fnMap.put("mod", "($)");
        fnMap.put("length", "char_length($)");
        fnMap.put("locate", "charindex($)");
        fnMap.put("repeat", "replicate($)");
        fnMap.put("insert", "stuff($)");
        fnMap.put("lcase", "lower($)");
        fnMap.put("ucase", "upper($)");
        fnMap.put("concat", "($)");
        fnMap.put("curdate", "convert(datetime, convert(varchar, getdate(), 112))");
        fnMap.put("curtime", "convert(datetime, convert(varchar, getdate(), 108))");
        fnMap.put("dayname", "datename(weekday,$)");
        fnMap.put("dayofmonth", "datepart(day,$)");
        fnMap.put("dayofweek", "((datepart(weekday,$)+@@DATEFIRST-1)%7+1)");
        fnMap.put("dayofyear", "datepart(dayofyear,$)");
        fnMap.put("hour", "datepart(hour,$)");
        fnMap.put("minute", "datepart(minute,$)");
        fnMap.put("second", "datepart(second,$)");
        fnMap.put("year", "datepart(year,$)");
        fnMap.put("quarter", "datepart(quarter,$)");
        fnMap.put("month", "datepart(month,$)");
        fnMap.put("week", "datepart(week,$)");
        fnMap.put("monthname", "datename(month,$)");
        fnMap.put("timestampadd", "dateadd($)");
        fnMap.put("timestampdiff", "datediff($)");
        cvMap.put("binary", "varbinary");
        cvMap.put("char", "varchar");
        cvMap.put("date", "datetime");
        cvMap.put("double", "float");
        cvMap.put("longvarbinary", "image");
        cvMap.put("longvarchar", "text");
        cvMap.put("time", "datetime");
        cvMap.put("timestamp", "timestamp");
    }

    private static boolean isIdentifier(int i) {
        if (i <= 127) {
            if (identifierChar[i] == 0) {
                return false;
            }
        }
        return true;
    }

    private SQLParser(String str, ArrayList arrayList, ConnectionJDBC2 connectionJDBC2) {
        this.sql = str;
        this.params = arrayList;
        this.procName = "";
        this.connection = connectionJDBC2;
    }

    private void copyLiteral(String str) throws SQLException {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt == '?') {
                if (this.params == null) {
                    throw new SQLException(Messages.get("error.parsesql.unexpectedparam", String.valueOf(this.s)), "2A000");
                }
                this.params.add(new ParamInfo(this.d, this.connection.getUseUnicode()));
            }
            append(charAt);
        }
    }

    private void copyString() {
        char c = this.terminator;
        char c2 = this.in[this.s];
        if (c2 == '[') {
            c2 = ']';
        }
        this.terminator = c2;
        char[] cArr = this.in;
        int i = this.s;
        this.s = i + 1;
        append(cArr[i]);
        while (this.in[this.s] != c2) {
            cArr = this.in;
            i = this.s;
            this.s = i + 1;
            append(cArr[i]);
        }
        char[] cArr2 = this.in;
        int i2 = this.s;
        this.s = i2 + 1;
        append(cArr2[i2]);
        this.terminator = c;
    }

    private String copyKeyWord() {
        int i = this.d;
        while (this.s < this.len && isIdentifier(this.in[this.s])) {
            char[] cArr = this.in;
            int i2 = this.s;
            this.s = i2 + 1;
            append(cArr[i2]);
        }
        return String.valueOf(this.out, i, this.d - i).toLowerCase();
    }

    private void copyParam(String str, int i) throws SQLException {
        if (this.params == null) {
            throw new SQLException(Messages.get("error.parsesql.unexpectedparam", String.valueOf(this.s)), "2A000");
        }
        ParamInfo paramInfo = new ParamInfo(i, this.connection.getUseUnicode());
        paramInfo.name = str;
        if (i >= 0) {
            str = this.in;
            i = this.s;
            this.s = i + 1;
            append(str[i]);
        } else {
            paramInfo.isRetVal = true;
            this.s++;
        }
        this.params.add(paramInfo);
    }

    private String copyProcName() throws SQLException {
        int i = this.d;
        while (true) {
            char[] cArr;
            if (this.in[this.s] != '\"') {
                if (this.in[this.s] != '[') {
                    cArr = this.in;
                    int i2 = this.s;
                    this.s = i2 + 1;
                    char c = cArr[i2];
                    while (true) {
                        if (!isIdentifier(c)) {
                            if (c != ';') {
                                break;
                            }
                        }
                        append(c);
                        cArr = this.in;
                        i2 = this.s;
                        this.s = i2 + 1;
                        c = cArr[i2];
                    }
                    this.s--;
                    if (this.in[this.s] == '.') {
                        break;
                    }
                    while (this.in[this.s] == '.') {
                        cArr = this.in;
                        int i3 = this.s;
                        this.s = i3 + 1;
                        append(cArr[i3]);
                    }
                }
            }
            copyString();
            if (this.in[this.s] == '.') {
                break;
            }
            while (this.in[this.s] == '.') {
                cArr = this.in;
                int i32 = this.s;
                this.s = i32 + 1;
                append(cArr[i32]);
            }
        }
        if (this.d != i) {
            return new String(this.out, i, this.d - i);
        }
        throw new SQLException(Messages.get("error.parsesql.syntax", NotificationCompat.CATEGORY_CALL, String.valueOf(this.s)), "22025");
    }

    private String copyParamName() {
        int i = this.d;
        char[] cArr = this.in;
        int i2 = this.s;
        this.s = i2 + 1;
        char c = cArr[i2];
        while (isIdentifier(c)) {
            append(c);
            cArr = this.in;
            i2 = this.s;
            this.s = i2 + 1;
            c = cArr[i2];
        }
        this.s--;
        return new String(this.out, i, this.d - i);
    }

    private void copyWhiteSpace() {
        while (this.s < this.in.length && Character.isWhitespace(this.in[this.s])) {
            char[] cArr = this.in;
            int i = this.s;
            this.s = i + 1;
            append(cArr[i]);
        }
    }

    private void mustbe(char c, boolean z) throws SQLException {
        if (this.in[this.s] != c) {
            throw new SQLException(Messages.get("error.parsesql.mustbe", String.valueOf(this.s), String.valueOf(c)), "22019");
        } else if (z) {
            c = this.in;
            z = this.s;
            this.s = z + 1;
            append(c[z]);
        } else {
            this.s++;
        }
    }

    private void skipWhiteSpace() throws SQLException {
        while (this.s < this.len) {
            while (Character.isWhitespace(this.sql.charAt(this.s))) {
                this.s++;
            }
            char charAt = this.sql.charAt(this.s);
            char[] cArr;
            if (charAt != '-') {
                if (charAt == '/') {
                    if (this.s + 1 < this.len && this.in[this.s + 1] == '*') {
                        cArr = this.in;
                        int i = this.s;
                        this.s = i + 1;
                        append(cArr[i]);
                        cArr = this.in;
                        i = this.s;
                        this.s = i + 1;
                        append(cArr[i]);
                        int i2 = 1;
                        while (this.s < this.len - 1) {
                            char[] cArr2;
                            int i3;
                            if (this.in[this.s] == '/' && this.s + 1 < this.len && this.in[this.s + 1] == '*') {
                                cArr2 = this.in;
                                i3 = this.s;
                                this.s = i3 + 1;
                                append(cArr2[i3]);
                                i2++;
                            } else if (this.in[this.s] == '*' && this.s + 1 < this.len && this.in[this.s + 1] == '/') {
                                cArr2 = this.in;
                                i3 = this.s;
                                this.s = i3 + 1;
                                append(cArr2[i3]);
                                i2--;
                            }
                            cArr2 = this.in;
                            i3 = this.s;
                            this.s = i3 + 1;
                            append(cArr2[i3]);
                            if (i2 <= 0) {
                            }
                        }
                        throw new SQLException(Messages.get("error.parsesql.missing", (Object) "*/"), "22025");
                    }
                }
                return;
            } else if (this.s + 1 < this.len && this.in[this.s + 1] == '-') {
                cArr = this.in;
                int i4 = this.s;
                this.s = i4 + 1;
                append(cArr[i4]);
                cArr = this.in;
                i4 = this.s;
                this.s = i4 + 1;
                append(cArr[i4]);
                while (this.s < this.len && this.in[this.s] != '\n' && this.in[this.s] != '\r') {
                    cArr = this.in;
                    i4 = this.s;
                    this.s = i4 + 1;
                    append(cArr[i4]);
                }
            }
        }
    }

    private void skipSingleComments() {
        while (this.s < this.len && this.in[this.s] != '\n' && this.in[this.s] != '\r') {
            char[] cArr = this.in;
            int i = this.s;
            this.s = i + 1;
            append(cArr[i]);
        }
    }

    private void skipMultiComments() throws SQLException {
        int i = 0;
        while (this.s < this.len - 1) {
            char[] cArr;
            int i2;
            if (this.in[this.s] == '/' && this.in[this.s + 1] == '*') {
                cArr = this.in;
                i2 = this.s;
                this.s = i2 + 1;
                append(cArr[i2]);
                i++;
            } else if (this.in[this.s] == '*' && this.in[this.s + 1] == '/') {
                cArr = this.in;
                i2 = this.s;
                this.s = i2 + 1;
                append(cArr[i2]);
                i--;
            }
            cArr = this.in;
            i2 = this.s;
            this.s = i2 + 1;
            append(cArr[i2]);
            if (i <= 0) {
                return;
            }
        }
        throw new SQLException(Messages.get("error.parsesql.missing", (Object) "*/"), "22025");
    }

    private void callEscape() throws SQLException {
        copyLiteral("EXECUTE ");
        this.keyWord = "execute";
        this.procName = copyProcName();
        skipWhiteSpace();
        if (this.in[this.s] == '(') {
            this.s++;
            this.terminator = ')';
            skipWhiteSpace();
        } else {
            this.terminator = '}';
        }
        append(' ');
        while (this.in[this.s] != this.terminator) {
            if (this.in[this.s] == '@') {
                String copyParamName = copyParamName();
                skipWhiteSpace();
                mustbe('=', true);
                skipWhiteSpace();
                if (this.in[this.s] == '?') {
                    copyParam(copyParamName, this.d);
                } else {
                    this.procName = "";
                }
            } else if (this.in[this.s] == '?') {
                copyParam(null, this.d);
            } else {
                this.procName = "";
            }
            skipWhiteSpace();
            while (this.in[this.s] != this.terminator && this.in[this.s] != ',') {
                if (this.in[this.s] == '{') {
                    escape();
                } else {
                    if (!(this.in[this.s] == '\'' || this.in[this.s] == '[')) {
                        if (this.in[this.s] != '\"') {
                            char[] cArr = this.in;
                            int i = this.s;
                            this.s = i + 1;
                            append(cArr[i]);
                        }
                    }
                    copyString();
                }
            }
            if (this.in[this.s] == ',') {
                cArr = this.in;
                i = this.s;
                this.s = i + 1;
                append(cArr[i]);
            }
            skipWhiteSpace();
        }
        if (this.terminator == ')') {
            this.s++;
        }
        this.terminator = '}';
        skipWhiteSpace();
    }

    private boolean getDateTimeField(byte[] bArr) throws SQLException {
        skipWhiteSpace();
        boolean z = false;
        if (this.in[this.s] == '?') {
            copyParam(null, this.d);
            skipWhiteSpace();
            if (this.in[this.s] == this.terminator) {
                z = true;
            }
            return z;
        }
        char c;
        int i;
        char[] cArr;
        int i2;
        char c2;
        int i3;
        append("convert(datetime,".toCharArray());
        append('\'');
        if (this.in[this.s] != '\'') {
            if (this.in[this.s] != '\"') {
                c = '}';
                this.terminator = c;
                skipWhiteSpace();
                i = 0;
                while (i < bArr.length) {
                    cArr = this.in;
                    i2 = this.s;
                    this.s = i2 + 1;
                    c2 = cArr[i2];
                    if (c2 == ' ' || this.out[this.d - 1] != ' ') {
                        if (bArr[i] != (byte) 35) {
                            if (!Character.isDigit(c2)) {
                                return false;
                            }
                        } else if (bArr[i] != c2) {
                            return false;
                        }
                        if (c2 != '-') {
                            append(c2);
                        }
                        i++;
                    }
                }
                if (bArr.length == 19) {
                    if (this.in[this.s] != 46) {
                        bArr = this.in;
                        i3 = this.s;
                        this.s = i3 + 1;
                        append(bArr[i3]);
                        bArr = null;
                        while (Character.isDigit(this.in[this.s])) {
                            if (bArr >= 3) {
                                cArr = this.in;
                                i2 = this.s;
                                this.s = i2 + 1;
                                append(cArr[i2]);
                                bArr++;
                            } else {
                                this.s++;
                            }
                        }
                    } else {
                        append('.');
                        bArr = null;
                    }
                    while (bArr < 3) {
                        append('0');
                        bArr++;
                    }
                }
                skipWhiteSpace();
                if (this.in[this.s] != this.terminator) {
                    return false;
                }
                if (this.terminator != 125) {
                    this.s += 1;
                }
                skipWhiteSpace();
                append('\'');
                append((char) 41);
                return true;
            }
        }
        char[] cArr2 = this.in;
        i3 = this.s;
        this.s = i3 + 1;
        c = cArr2[i3];
        this.terminator = c;
        skipWhiteSpace();
        i = 0;
        while (i < bArr.length) {
            cArr = this.in;
            i2 = this.s;
            this.s = i2 + 1;
            c2 = cArr[i2];
            if (c2 == ' ') {
            }
            if (bArr[i] != (byte) 35) {
                if (bArr[i] != c2) {
                    return false;
                }
            } else if (Character.isDigit(c2)) {
                return false;
            }
            if (c2 != '-') {
                append(c2);
            }
            i++;
        }
        if (bArr.length == 19) {
            if (this.in[this.s] != 46) {
                append('.');
                bArr = null;
            } else {
                bArr = this.in;
                i3 = this.s;
                this.s = i3 + 1;
                append(bArr[i3]);
                bArr = null;
                while (Character.isDigit(this.in[this.s])) {
                    if (bArr >= 3) {
                        this.s++;
                    } else {
                        cArr = this.in;
                        i2 = this.s;
                        this.s = i2 + 1;
                        append(cArr[i2]);
                        bArr++;
                    }
                }
            }
            while (bArr < 3) {
                append('0');
                bArr++;
            }
        }
        skipWhiteSpace();
        if (this.in[this.s] != this.terminator) {
            return false;
        }
        if (this.terminator != 125) {
            this.s += 1;
        }
        skipWhiteSpace();
        append('\'');
        append((char) 41);
        return true;
    }

    private void outerJoinEscape() throws SQLException {
        while (this.in[this.s] != '}') {
            char c = this.in[this.s];
            if (!(c == '\"' || c == '\'')) {
                if (c == '?') {
                    copyParam(null, this.d);
                } else if (c != '[') {
                    if (c != '{') {
                        append(c);
                        this.s++;
                    } else {
                        escape();
                    }
                }
            }
            copyString();
        }
    }

    private void functionEscape() throws SQLException {
        int i;
        char c = this.terminator;
        skipWhiteSpace();
        StringBuffer stringBuffer = new StringBuffer();
        while (isIdentifier(this.in[this.s])) {
            char[] cArr = this.in;
            i = this.s;
            this.s = i + 1;
            stringBuffer.append(cArr[i]);
        }
        String toLowerCase = stringBuffer.toString().toLowerCase();
        skipWhiteSpace();
        i = 0;
        mustbe('(', false);
        int i2 = this.d;
        this.terminator = ')';
        int i3 = 1;
        int i4 = 0;
        while (true) {
            if (this.in[this.s] == ')') {
                if (i3 <= 1) {
                    String trim = String.valueOf(this.out, i2, this.d - i2).trim();
                    this.d = i2;
                    mustbe(')', false);
                    this.terminator = c;
                    skipWhiteSpace();
                    String str;
                    if (!"convert".equals(toLowerCase) || i4 >= trim.length() - 1) {
                        if (this.connection.getServerType() == 1) {
                            str = (String) msFnMap.get(toLowerCase);
                            if (str == null) {
                                str = (String) fnMap.get(toLowerCase);
                            }
                        } else {
                            str = (String) fnMap.get(toLowerCase);
                        }
                        if (str == null) {
                            copyLiteral(toLowerCase);
                            append('(');
                            copyLiteral(trim);
                            append(')');
                            return;
                        }
                        if (trim.length() > 8 && trim.substring(0, 8).equalsIgnoreCase("sql_tsi_")) {
                            trim = trim.substring(8);
                            if (trim.length() > 11 && trim.substring(0, 11).equalsIgnoreCase("frac_second")) {
                                stringBuffer = new StringBuffer();
                                stringBuffer.append("millisecond");
                                stringBuffer.append(trim.substring(11));
                                trim = stringBuffer.toString();
                            }
                        }
                        int length = str.length();
                        while (i < length) {
                            char charAt = str.charAt(i);
                            if (charAt == '$') {
                                copyLiteral(trim);
                            } else {
                                append(charAt);
                            }
                            i++;
                        }
                        return;
                    }
                    str = trim.substring(i4 + 1).trim().toLowerCase();
                    toLowerCase = (String) cvMap.get(str);
                    if (toLowerCase != null) {
                        str = toLowerCase;
                    }
                    copyLiteral("convert(");
                    copyLiteral(str);
                    append(',');
                    copyLiteral(trim.substring(0, i4));
                    append(')');
                    return;
                }
            }
            char c2 = this.in[this.s];
            if (c2 != '\"') {
                if (c2 != ',') {
                    if (c2 != '[') {
                        if (c2 != '{') {
                            switch (c2) {
                                case '\'':
                                    break;
                                case '(':
                                    i3++;
                                    append(c2);
                                    this.s++;
                                    continue;
                                case ')':
                                    i3--;
                                    append(c2);
                                    this.s++;
                                    continue;
                                default:
                                    append(c2);
                                    this.s++;
                                    continue;
                            }
                        } else {
                            escape();
                        }
                    }
                } else if (i3 == 1) {
                    if (i4 == 0) {
                        i4 = this.d - i2;
                    }
                    if ("concat".equals(toLowerCase)) {
                        append('+');
                        this.s++;
                    } else if ("mod".equals(toLowerCase)) {
                        append('%');
                        this.s++;
                    } else {
                        append(c2);
                        this.s++;
                    }
                } else {
                    append(c2);
                    this.s++;
                }
            }
            copyString();
        }
    }

    private void likeEscape() throws SQLException {
        copyLiteral("escape ");
        skipWhiteSpace();
        if (this.in[this.s] != '\'') {
            if (this.in[this.s] != '\"') {
                mustbe('\'', true);
                skipWhiteSpace();
            }
        }
        copyString();
        skipWhiteSpace();
    }

    private void escape() throws SQLException {
        char c = this.terminator;
        this.terminator = '}';
        StringBuffer stringBuffer = new StringBuffer();
        this.s++;
        skipWhiteSpace();
        if (this.in[this.s] == '?') {
            copyParam("@return_status", -1);
            skipWhiteSpace();
            mustbe('=', false);
            skipWhiteSpace();
            while (Character.isLetter(this.in[this.s])) {
                char[] cArr = this.in;
                int i = this.s;
                this.s = i + 1;
                stringBuffer.append(Character.toLowerCase(cArr[i]));
            }
            skipWhiteSpace();
            if (NotificationCompat.CATEGORY_CALL.equals(stringBuffer.toString())) {
                callEscape();
            } else {
                throw new SQLException(Messages.get("error.parsesql.syntax", NotificationCompat.CATEGORY_CALL, String.valueOf(this.s)), "22019");
            }
        }
        while (Character.isLetter(this.in[this.s])) {
            cArr = this.in;
            i = this.s;
            this.s = i + 1;
            stringBuffer.append(Character.toLowerCase(cArr[i]));
        }
        skipWhiteSpace();
        String stringBuffer2 = stringBuffer.toString();
        if (NotificationCompat.CATEGORY_CALL.equals(stringBuffer2)) {
            callEscape();
        } else if ("t".equals(stringBuffer2)) {
            if (!getDateTimeField(timeMask)) {
                throw new SQLException(Messages.get("error.parsesql.syntax", "time", String.valueOf(this.s)), "22019");
            }
        } else if ("d".equals(stringBuffer2)) {
            if (!getDateTimeField(dateMask)) {
                throw new SQLException(Messages.get("error.parsesql.syntax", "date", String.valueOf(this.s)), "22019");
            }
        } else if ("ts".equals(stringBuffer2)) {
            if (!getDateTimeField(timestampMask)) {
                throw new SQLException(Messages.get("error.parsesql.syntax", "timestamp", String.valueOf(this.s)), "22019");
            }
        } else if ("oj".equals(stringBuffer2)) {
            outerJoinEscape();
        } else if ("fn".equals(stringBuffer2)) {
            functionEscape();
        } else if ("escape".equals(stringBuffer2)) {
            likeEscape();
        } else {
            throw new SQLException(Messages.get("error.parsesql.badesc", stringBuffer2, String.valueOf(this.s)), "22019");
        }
        mustbe('}', false);
        this.terminator = c;
    }

    private java.lang.String getTableName() throws java.sql.SQLException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:37)
	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:61)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:33)
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
        r0 = new java.lang.StringBuffer;
        r1 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r0.<init>(r1);
        r8.copyWhiteSpace();
        r1 = r8.s;
        r2 = r8.len;
        r3 = 32;
        if (r1 >= r2) goto L_0x0019;
    L_0x0012:
        r1 = r8.in;
        r2 = r8.s;
        r1 = r1[r2];
        goto L_0x001b;
    L_0x0019:
        r1 = 32;
    L_0x001b:
        r2 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
        if (r1 != r2) goto L_0x0022;
    L_0x001f:
        r0 = "";
        return r0;
    L_0x0022:
        r4 = 45;
        r5 = 47;
        if (r1 == r5) goto L_0x0032;
    L_0x0028:
        if (r1 != r4) goto L_0x0064;
    L_0x002a:
        r6 = r8.s;
        r6 = r6 + 1;
        r7 = r8.len;
        if (r6 >= r7) goto L_0x0064;
    L_0x0032:
        if (r1 != r5) goto L_0x0044;
    L_0x0034:
        r4 = r8.in;
        r5 = r8.s;
        r5 = r5 + 1;
        r4 = r4[r5];
        r5 = 42;
        if (r4 != r5) goto L_0x0064;
    L_0x0040:
        r8.skipMultiComments();
        goto L_0x0051;
    L_0x0044:
        r5 = r8.in;
        r6 = r8.s;
        r6 = r6 + 1;
        r5 = r5[r6];
        if (r5 != r4) goto L_0x0064;
    L_0x004e:
        r8.skipSingleComments();
    L_0x0051:
        r8.copyWhiteSpace();
        r1 = r8.s;
        r4 = r8.len;
        if (r1 >= r4) goto L_0x0061;
    L_0x005a:
        r1 = r8.in;
        r4 = r8.s;
        r1 = r1[r4];
        goto L_0x0022;
    L_0x0061:
        r1 = 32;
        goto L_0x0022;
    L_0x0064:
        if (r1 != r2) goto L_0x0069;
    L_0x0066:
        r0 = "";
        return r0;
    L_0x0069:
        r2 = r8.s;
        r4 = r8.len;
        if (r2 >= r4) goto L_0x0118;
    L_0x006f:
        r2 = 91;
        r4 = 46;
        if (r1 == r2) goto L_0x00d4;
    L_0x0075:
        r2 = 34;
        if (r1 != r2) goto L_0x007a;
    L_0x0079:
        goto L_0x00d4;
    L_0x007a:
        r1 = r8.d;
        r2 = r8.s;
        r5 = r8.len;
        if (r2 >= r5) goto L_0x008d;
    L_0x0082:
        r2 = r8.in;
        r5 = r8.s;
        r6 = r5 + 1;
        r8.s = r6;
        r2 = r2[r5];
        goto L_0x008f;
    L_0x008d:
        r2 = 32;
    L_0x008f:
        r5 = isIdentifier(r2);
        if (r5 == 0) goto L_0x00af;
    L_0x0095:
        if (r2 == r4) goto L_0x00af;
    L_0x0097:
        r5 = 44;
        if (r2 == r5) goto L_0x00af;
    L_0x009b:
        r8.append(r2);
        r2 = r8.s;
        r5 = r8.len;
        if (r2 >= r5) goto L_0x008d;
    L_0x00a4:
        r2 = r8.in;
        r5 = r8.s;
        r6 = r5 + 1;
        r8.s = r6;
        r2 = r2[r5];
        goto L_0x008f;
    L_0x00af:
        r2 = r8.out;
        r5 = r8.d;
        r5 = r5 - r1;
        r1 = java.lang.String.valueOf(r2, r1, r5);
        r0.append(r1);
        r1 = r8.s;
        r1 = r1 + -1;
        r8.s = r1;
        r8.copyWhiteSpace();
        r1 = r8.s;
        r2 = r8.len;
        if (r1 >= r2) goto L_0x00d1;
    L_0x00ca:
        r1 = r8.in;
        r2 = r8.s;
        r1 = r1[r2];
        goto L_0x00f4;
    L_0x00d1:
        r1 = 32;
        goto L_0x00f4;
    L_0x00d4:
        r1 = r8.d;
        r8.copyString();
        r2 = r8.out;
        r5 = r8.d;
        r5 = r5 - r1;
        r1 = java.lang.String.valueOf(r2, r1, r5);
        r0.append(r1);
        r8.copyWhiteSpace();
        r1 = r8.s;
        r2 = r8.len;
        if (r1 >= r2) goto L_0x00d1;
    L_0x00ee:
        r1 = r8.in;
        r2 = r8.s;
        r1 = r1[r2];
    L_0x00f4:
        if (r1 == r4) goto L_0x00f7;
    L_0x00f6:
        goto L_0x0118;
    L_0x00f7:
        r0.append(r1);
        r8.append(r1);
        r1 = r8.s;
        r1 = r1 + 1;
        r8.s = r1;
        r8.copyWhiteSpace();
        r1 = r8.s;
        r2 = r8.len;
        if (r1 >= r2) goto L_0x0114;
    L_0x010c:
        r1 = r8.in;
        r2 = r8.s;
        r1 = r1[r2];
        goto L_0x0069;
    L_0x0114:
        r1 = 32;
        goto L_0x0069;
    L_0x0118:
        r0 = r0.toString();
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SQLParser.getTableName():java.lang.String");
    }

    private final void append(char[] cArr) {
        for (char append : cArr) {
            append(append);
        }
    }

    private final void append(char r5) {
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
        r4 = this;
        r0 = r4.out;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x000b }
        r1 = r4.d;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x000b }
        r2 = r1 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x000b }
        r4.d = r2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x000b }
        r0[r1] = r5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x000b }
        goto L_0x0025;
    L_0x000b:
        r0 = r4.out;
        r0 = r0.length;
        r0 = r0 + 256;
        r0 = new char[r0];
        r1 = r4.out;
        r2 = r4.out;
        r2 = r2.length;
        r3 = 0;
        java.lang.System.arraycopy(r1, r3, r0, r3, r2);
        r4.out = r0;
        r0 = r4.out;
        r1 = r4.d;
        r1 = r1 + -1;
        r0[r1] = r5;
    L_0x0025:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SQLParser.append(char):void");
    }

    java.lang.String[] parse(boolean r10) throws java.sql.SQLException {
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
        r0 = 0;
        r1 = 1;
        r2 = 1;
        r3 = 0;
        r4 = 0;
    L_0x0005:
        r5 = r9.s;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = r9.len;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r5 >= r6) goto L_0x00c3;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x000b:
        r5 = r9.in;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = r9.s;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r5[r6];	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = 34;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r5 == r6) goto L_0x00be;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0015:
        r6 = 39;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r5 == r6) goto L_0x00be;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0019:
        r6 = 45;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r5 == r6) goto L_0x009f;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x001d:
        r6 = 47;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r5 == r6) goto L_0x007e;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0021:
        r6 = 63;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r5 == r6) goto L_0x0077;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0025:
        r6 = 91;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r5 == r6) goto L_0x00be;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0029:
        r6 = 123; // 0x7b float:1.72E-43 double:6.1E-322;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r5 == r6) goto L_0x0072;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x002d:
        if (r2 == 0) goto L_0x0069;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x002f:
        r6 = java.lang.Character.isLetter(r5);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r6 == 0) goto L_0x0069;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0035:
        r6 = r9.keyWord;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r6 != 0) goto L_0x0052;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0039:
        r2 = r9.copyKeyWord();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r9.keyWord = r2;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r2 = "select";	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r9.keyWord;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r2 = r2.equals(r5);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r2 == 0) goto L_0x004a;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0049:
        r3 = 1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x004a:
        if (r10 == 0) goto L_0x0050;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x004c:
        if (r3 == 0) goto L_0x0050;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x004e:
        r2 = 1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0050:
        r2 = 0;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0052:
        if (r10 == 0) goto L_0x0069;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0054:
        if (r3 == 0) goto L_0x0069;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0056:
        r5 = r9.copyKeyWord();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = "from";	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r6.equals(r5);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r5 == 0) goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0062:
        r2 = r9.getTableName();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r9.tableName = r2;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0050;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0069:
        r9.append(r5);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r9.s;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r5 + r1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r9.s = r5;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0072:
        r9.escape();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r4 = 1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0077:
        r5 = 0;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = r9.d;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r9.copyParam(r5, r6);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x007e:
        r6 = r9.s;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = r6 + r1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r7 = r9.len;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r6 >= r7) goto L_0x0095;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0085:
        r6 = r9.in;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r7 = r9.s;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r7 = r7 + r1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = r6[r7];	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r7 = 42;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r6 != r7) goto L_0x0095;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0090:
        r9.skipMultiComments();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0095:
        r9.append(r5);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r9.s;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r5 + r1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r9.s = r5;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x009f:
        r7 = r9.s;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r7 = r7 + r1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r8 = r9.len;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r7 >= r8) goto L_0x00b4;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00a6:
        r7 = r9.in;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r8 = r9.s;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r8 = r8 + r1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r7 = r7[r8];	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r7 != r6) goto L_0x00b4;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00af:
        r9.skipSingleComments();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00b4:
        r9.append(r5);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r9.s;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r5 + r1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r9.s = r5;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00be:
        r9.copyString();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0005;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00c3:
        r10 = r9.params;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r2 = 2;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 == 0) goto L_0x0136;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00c8:
        r10 = r9.params;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = r10.size();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 <= r3) goto L_0x0136;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00d2:
        r10 = r9.connection;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = r10.getPrepareSql();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 == 0) goto L_0x0136;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00da:
        r10 = r9.procName;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 == 0) goto L_0x0136;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00de:
        r10 = r9.connection;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = r10.getServerType();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 != r2) goto L_0x0107;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00e8:
        r10 = r9.connection;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = r10.getDatabaseMajorVersion();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = 12;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 > r6) goto L_0x0104;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00f2:
        r10 = r9.connection;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = r10.getDatabaseMajorVersion();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 != r6) goto L_0x011c;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x00fa:
        r10 = r9.connection;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = r10.getDatabaseMinorVersion();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = 50;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 < r6) goto L_0x011c;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0104:
        r3 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x011c;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0107:
        r10 = r9.connection;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = r10.getDatabaseMajorVersion();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r6 = 7;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 != r6) goto L_0x0113;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0110:
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x011c;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0113:
        r10 = r9.connection;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = r10.getDatabaseMajorVersion();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 <= r6) goto L_0x011c;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x011b:
        goto L_0x0104;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x011c:
        r10 = r9.params;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = r10.size();	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r10 <= r3) goto L_0x0136;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0124:
        r10 = new java.sql.SQLException;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r0 = "error.parsesql.toomanyparams";	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r1 = java.lang.Integer.toString(r3);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r0 = net.sourceforge.jtds.jdbc.Messages.get(r0, r1);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r1 = "22025";	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10.<init>(r0, r1);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        throw r10;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0136:
        r10 = 4;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10 = new java.lang.String[r10];	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r4 == 0) goto L_0x0145;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x013b:
        r3 = new java.lang.String;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r4 = r9.out;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r5 = r9.d;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r3.<init>(r4, r0, r5);	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0147;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0145:
        r3 = r9.sql;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0147:
        r10[r0] = r3;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r0 = r9.procName;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10[r1] = r0;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r0 = r9.keyWord;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        if (r0 != 0) goto L_0x0154;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0151:
        r0 = "";	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        goto L_0x0156;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0154:
        r0 = r9.keyWord;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
    L_0x0156:
        r10[r2] = r0;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r0 = 3;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r1 = r9.tableName;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        r10[r0] = r1;	 Catch:{ IndexOutOfBoundsException -> 0x015e }
        return r10;
    L_0x015e:
        r10 = new java.sql.SQLException;
        r0 = "error.parsesql.missing";
        r1 = r9.terminator;
        r1 = java.lang.String.valueOf(r1);
        r0 = net.sourceforge.jtds.jdbc.Messages.get(r0, r1);
        r1 = "22025";
        r10.<init>(r0, r1);
        throw r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SQLParser.parse(boolean):java.lang.String[]");
    }
}
