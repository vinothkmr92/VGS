package net.sourceforge.jtds.jdbc;

import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import android.support.v4.view.PointerIconCompat;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class JtdsDatabaseMetaData implements DatabaseMetaData {
    static final int sqlStateXOpen = 1;
    Boolean caseSensitive;
    private final ConnectionJDBC2 connection;
    private final int serverType;
    int sysnameLength = 30;
    private final int tdsVersion;

    public boolean allProceduresAreCallable() throws SQLException {
        return true;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    public boolean deletesAreDetected(int i) throws SQLException {
        return true;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return false;
    }

    public String getCatalogSeparator() throws SQLException {
        return ".";
    }

    public String getCatalogTerm() throws SQLException {
        return "database";
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        return 2;
    }

    public int getDriverMajorVersion() {
        return 1;
    }

    public int getDriverMinorVersion() {
        return 2;
    }

    public String getDriverName() throws SQLException {
        return "jTDS Type 4 JDBC Driver for MS SQL Server and Sybase";
    }

    public String getExtraNameCharacters() throws SQLException {
        return "$#@";
    }

    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }

    public int getJDBCMajorVersion() throws SQLException {
        return 3;
    }

    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        return 131072;
    }

    public int getMaxCharLiteralLength() throws SQLException {
        return 131072;
    }

    public int getMaxColumnsInIndex() throws SQLException {
        return 16;
    }

    public int getMaxColumnsInSelect() throws SQLException {
        return 4096;
    }

    public int getMaxConnections() throws SQLException {
        return 32767;
    }

    public int getMaxStatementLength() throws SQLException {
        return 0;
    }

    public int getMaxStatements() throws SQLException {
        return 0;
    }

    public String getNumericFunctions() throws SQLException {
        return "abs,acos,asin,atan,atan2,ceiling,cos,cot,degrees,exp,floor,log,log10,mod,pi,power,radians,rand,round,sign,sin,sqrt,tan";
    }

    public String getProcedureTerm() throws SQLException {
        return "stored procedure";
    }

    public int getResultSetHoldability() throws SQLException {
        return 1;
    }

    public String getSQLKeywords() throws SQLException {
        return "ARITH_OVERFLOW,BREAK,BROWSE,BULK,CHAR_CONVERT,CHECKPOINT,CLUSTERED,COMPUTE,CONFIRM,CONTROLROW,DATA_PGS,DATABASE,DBCC,DISK,DUMMY,DUMP,ENDTRAN,ERRLVL,ERRORDATA,ERROREXIT,EXIT,FILLFACTOR,HOLDLOCK,IDENTITY_INSERT,IF,INDEX,KILL,LINENO,LOAD,MAX_ROWS_PER_PAGE,MIRROR,MIRROREXIT,NOHOLDLOCK,NONCLUSTERED,NUMERIC_TRUNCATION,OFF,OFFSETS,ONCE,ONLINE,OVER,PARTITION,PERM,PERMANENT,PLAN,PRINT,PROC,PROCESSEXIT,RAISERROR,READ,READTEXT,RECONFIGURE,REPLACE,RESERVED_PGS,RETURN,ROLE,ROWCNT,ROWCOUNT,RULE,SAVE,SETUSER,SHARED,SHUTDOWN,SOME,STATISTICS,STRIPE,SYB_IDENTITY,SYB_RESTREE,SYB_TERMINATE,TEMP,TEXTSIZE,TRAN,TRIGGER,TRUNCATE,TSEQUAL,UNPARTITION,USE,USED_PGS,USER_OPTION,WAITFOR,WHILE,WRITETEXT";
    }

    public int getSQLStateType() throws SQLException {
        return 1;
    }

    public String getSchemaTerm() throws SQLException {
        return "owner";
    }

    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    public String getSystemFunctions() throws SQLException {
        return "database,ifnull,user,convert";
    }

    public String getTimeDateFunctions() throws SQLException {
        return "curdate,curtime,dayname,dayofmonth,dayofweek,dayofyear,hour,minute,month,monthname,now,quarter,timestampadd,timestampdiff,second,week,year";
    }

    public boolean insertsAreDetected(int i) throws SQLException {
        return false;
    }

    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    public boolean isReadOnly() throws SQLException {
        return false;
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        return true;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedLow() throws SQLException {
        return true;
    }

    public boolean othersDeletesAreVisible(int i) throws SQLException {
        return i >= 1005;
    }

    public boolean othersInsertsAreVisible(int i) throws SQLException {
        return i == PointerIconCompat.TYPE_CELL;
    }

    public boolean othersUpdatesAreVisible(int i) throws SQLException {
        return i >= 1005;
    }

    public boolean ownDeletesAreVisible(int i) throws SQLException {
        return true;
    }

    public boolean ownInsertsAreVisible(int i) throws SQLException {
        return true;
    }

    public boolean ownUpdatesAreVisible(int i) throws SQLException {
        return true;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return true;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return true;
    }

    public boolean supportsBatchUpdates() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    public boolean supportsConvert() throws SQLException {
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean supportsConvert(int i, int i2) throws SQLException {
        boolean z = true;
        if (!(i == i2 || i == 12)) {
            switch (i) {
                case -7:
                case -6:
                case -5:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    break;
                case -4:
                    break;
                case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                case -2:
                    if (i2 == 6 || i2 == 7 || i2 == 8) {
                        z = false;
                    }
                    return z;
                case -1:
                    break;
                case 0:
                case 1:
                    break;
                default:
                    switch (i) {
                        case 91:
                        case 92:
                        case 93:
                            break;
                        default:
                            switch (i) {
                                case 2004:
                                    break;
                                case 2005:
                                    break;
                                default:
                                    return false;
                            }
                    }
            }
        }
        return true;
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return true;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return true;
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
        return true;
    }

    public boolean supportsGroupBy() throws SQLException {
        return true;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true;
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        return true;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        return true;
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        return true;
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        return true;
    }

    public boolean supportsNamedParameters() throws SQLException {
        return true;
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return true;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return true;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return true;
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        return true;
    }

    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }

    public boolean supportsPositionedDelete() throws SQLException {
        return true;
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        return true;
    }

    public boolean supportsResultSetHoldability(int i) throws SQLException {
        return false;
    }

    public boolean supportsResultSetType(int i) throws SQLException {
        return i >= PointerIconCompat.TYPE_HELP && i <= PointerIconCompat.TYPE_CELL;
    }

    public boolean supportsSavepoints() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    public boolean supportsStatementPooling() throws SQLException {
        return true;
    }

    public boolean supportsStoredProcedures() throws SQLException {
        return true;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return true;
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        return true;
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        return true;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return true;
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        return true;
    }

    public boolean supportsTransactionIsolationLevel(int i) throws SQLException {
        if (!(i == 4 || i == 8)) {
            switch (i) {
                case 1:
                case 2:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public boolean supportsTransactions() throws SQLException {
        return true;
    }

    public boolean supportsUnion() throws SQLException {
        return true;
    }

    public boolean supportsUnionAll() throws SQLException {
        return true;
    }

    public boolean updatesAreDetected(int i) throws SQLException {
        return false;
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    public boolean usesLocalFiles() throws SQLException {
        return false;
    }

    public JtdsDatabaseMetaData(ConnectionJDBC2 connectionJDBC2) {
        this.connection = connectionJDBC2;
        this.tdsVersion = connectionJDBC2.getTdsVersion();
        this.serverType = connectionJDBC2.getServerType();
        if (this.tdsVersion >= 3) {
            this.sysnameLength = 128;
        }
    }

    public boolean allTablesAreSelectable() throws SQLException {
        return this.connection.getServerType() == 1;
    }

    public ResultSet getBestRowIdentifier(String str, String str2, String str3, int i, boolean z) throws SQLException {
        String[] strArr = new String[]{"SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN"};
        int[] iArr = new int[]{5, 12, 4, 12, 4, 4, 5, 5};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_special_columns ?, ?, ?, ?, ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str);
        prepareCall.setString(4, "R");
        prepareCall.setString(5, "T");
        prepareCall.setString(6, "U");
        prepareCall.setInt(7, 3);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        str2 = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        str2.moveToInsertRow();
        str3 = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next() != 0) {
            for (i = 1; i <= str3; i++) {
                if (i == 3) {
                    str2.updateInt(i, (int) TypeInfo.normalizeDataType(jtdsResultSet.getInt(i), this.connection.getUseLOBs()));
                } else {
                    str2.updateObject(i, (Object) jtdsResultSet.getObject(i));
                }
            }
            str2.insertRow();
        }
        jtdsResultSet.close();
        str2.moveToCurrentRow();
        str2.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return str2;
    }

    public ResultSet getCatalogs() throws SQLException {
        JtdsResultSet jtdsResultSet = (JtdsResultSet) this.connection.createStatement().executeQuery("exec sp_tables '', '', '%', NULL");
        jtdsResultSet.setColumnCount(1);
        jtdsResultSet.setColLabel(1, "TABLE_CAT");
        upperCaseColumnNames(jtdsResultSet);
        return jtdsResultSet;
    }

    public ResultSet getColumnPrivileges(String str, String str2, String str3, String str4) throws SQLException {
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_column_privileges ?, ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str);
        prepareCall.setString(4, processEscapes(str4));
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        jtdsResultSet.setColLabel(1, "TABLE_CAT");
        jtdsResultSet.setColLabel(2, "TABLE_SCHEM");
        upperCaseColumnNames(jtdsResultSet);
        return jtdsResultSet;
    }

    public ResultSet getColumns(String str, String str2, String str3, String str4) throws SQLException {
        String str5 = str;
        r3 = new String[23];
        int i = 2;
        r3[2] = "TABLE_NAME";
        r3[3] = "COLUMN_NAME";
        int i2 = 4;
        r3[4] = "DATA_TYPE";
        r3[5] = "TYPE_NAME";
        r3[6] = "COLUMN_SIZE";
        r3[7] = "BUFFER_LENGTH";
        r3[8] = "DECIMAL_DIGITS";
        r3[9] = "NUM_PREC_RADIX";
        r3[10] = "NULLABLE";
        r3[11] = "REMARKS";
        r3[12] = "COLUMN_DEF";
        r3[13] = "SQL_DATA_TYPE";
        r3[14] = "SQL_DATETIME_SUB";
        r3[15] = "CHAR_OCTET_LENGTH";
        r3[16] = "ORDINAL_POSITION";
        r3[17] = "IS_NULLABLE";
        r3[18] = "SCOPE_CATALOG";
        r3[19] = "SCOPE_SCHEMA";
        r3[20] = "SCOPE_TABLE";
        r3[21] = "SOURCE_DATA_TYPE";
        r3[22] = "IS_AUTOINCREMENT";
        int[] iArr = new int[]{12, 12, 12, 12, 4, 12, 4, 4, 4, 4, 4, 12, 12, 4, 4, 4, 4, 12, 12, 12, 12, 5, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str5, "sp_columns ?, ?, ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str5);
        prepareCall.setString(4, processEscapes(str4));
        prepareCall.setInt(5, 3);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        ResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, r3, iArr);
        cachedResultSet.moveToInsertRow();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            String string = jtdsResultSet.getString(6);
            if (r0.serverType == i) {
                int i3;
                for (i3 = 1; i3 <= i2; i3++) {
                    cachedResultSet.updateObject(i3, jtdsResultSet.getObject(i3));
                }
                cachedResultSet.updateInt(5, TypeInfo.normalizeDataType(jtdsResultSet.getInt(5), r0.connection.getUseLOBs()));
                cachedResultSet.updateString(6, string);
                for (i3 = 8; i3 <= 12; i3++) {
                    cachedResultSet.updateObject(i3, jtdsResultSet.getObject(i3));
                }
                if (columnCount >= 20) {
                    for (i3 = 13; i3 <= 18; i3++) {
                        cachedResultSet.updateObject(i3, jtdsResultSet.getObject(i3 + 2));
                    }
                } else {
                    cachedResultSet.updateObject(16, jtdsResultSet.getObject(8));
                    cachedResultSet.updateObject(17, jtdsResultSet.getObject(14));
                }
                if (!"image".equals(string)) {
                    if (!"text".equals(string)) {
                        if (!"univarchar".equals(string)) {
                            if (!"unichar".equals(string)) {
                                cachedResultSet.updateInt(7, jtdsResultSet.getInt(7));
                                cachedResultSet.updateString(23, string.toLowerCase().indexOf("identity") > -1 ? "YES" : "NO");
                            }
                        }
                        cachedResultSet.updateInt(7, jtdsResultSet.getInt(7) / i);
                        cachedResultSet.updateObject(16, jtdsResultSet.getObject(7));
                        if (string.toLowerCase().indexOf("identity") > -1) {
                        }
                        cachedResultSet.updateString(23, string.toLowerCase().indexOf("identity") > -1 ? "YES" : "NO");
                    }
                }
                cachedResultSet.updateInt(7, (int) ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                cachedResultSet.updateInt(16, (int) ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                if (string.toLowerCase().indexOf("identity") > -1) {
                }
                cachedResultSet.updateString(23, string.toLowerCase().indexOf("identity") > -1 ? "YES" : "NO");
            } else {
                for (int i4 = 1; i4 <= columnCount; i4++) {
                    if (i4 == 5) {
                        cachedResultSet.updateInt(i4, TypeInfo.normalizeDataType(jtdsResultSet.getInt(i4), r0.connection.getUseLOBs()));
                    } else if (i4 == 19) {
                        cachedResultSet.updateString(6, TdsData.getMSTypeName(jtdsResultSet.getString(6), jtdsResultSet.getInt(19)));
                    } else {
                        cachedResultSet.updateObject(i4, jtdsResultSet.getObject(i4));
                    }
                }
                cachedResultSet.updateString(23, string.toLowerCase().indexOf("identity") > -1 ? "YES" : "NO");
            }
            cachedResultSet.insertRow();
            i = 2;
            i2 = 4;
        }
        jtdsResultSet.close();
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public ResultSet getCrossReference(String str, String str2, String str3, String str4, String str5, String str6) throws SQLException {
        JtdsDatabaseMetaData jtdsDatabaseMetaData = this;
        String str7 = str;
        String str8 = str4;
        String[] strArr = new String[]{"PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME", "FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME", "FKCOLUMN_NAME", "KEY_SEQ", "UPDATE_RULE", "DELETE_RULE", "FK_NAME", "PK_NAME", "DEFERRABILITY"};
        int[] iArr = new int[]{12, 12, 12, 12, 12, 12, 12, 12, 5, 5, 5, 12, 12, 5};
        String str9 = "sp_fkeys ?, ?, ?, ?, ?, ?";
        if (str7 != null) {
            str9 = syscall(str7, str9);
        } else if (str8 != null) {
            str9 = syscall(str8, str9);
        } else {
            str9 = syscall(null, str9);
        }
        CallableStatement prepareCall = jtdsDatabaseMetaData.connection.prepareCall(str9);
        prepareCall.setString(1, str3);
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str7);
        prepareCall.setString(4, str6);
        prepareCall.setString(5, processEscapes(str5));
        prepareCall.setString(6, str8);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        int columnCount = jtdsResultSet.getMetaData().getColumnCount();
        ResultSet cachedResultSet = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        cachedResultSet.moveToInsertRow();
        while (jtdsResultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                cachedResultSet.updateObject(i, jtdsResultSet.getObject(i));
            }
            if (columnCount < 14) {
                cachedResultSet.updateShort(14, (short) 7);
            }
            cachedResultSet.insertRow();
        }
        jtdsResultSet.close();
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public String getDatabaseProductName() throws SQLException {
        return this.connection.getDatabaseProductName();
    }

    public String getDatabaseProductVersion() throws SQLException {
        return this.connection.getDatabaseProductVersion();
    }

    public String getDriverVersion() throws SQLException {
        return Driver.getVersion();
    }

    public ResultSet getExportedKeys(String str, String str2, String str3) throws SQLException {
        return getCrossReference(str, str2, str3, null, null, null);
    }

    public ResultSet getImportedKeys(String str, String str2, String str3) throws SQLException {
        return getCrossReference(null, null, null, str, str2, str3);
    }

    public ResultSet getIndexInfo(String str, String str2, String str3, boolean z, boolean z2) throws SQLException {
        String[] strArr = new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "NON_UNIQUE", "INDEX_QUALIFIER", "INDEX_NAME", "TYPE", "ORDINAL_POSITION", "COLUMN_NAME", "ASC_OR_DESC", "CARDINALITY", "PAGES", "FILTER_CONDITION"};
        int[] iArr = new int[]{12, 12, 12, -7, 12, 12, 5, 5, 12, 12, 4, 4, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_statistics ?, ?, ?, ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str);
        prepareCall.setString(4, "%");
        prepareCall.setString(5, z ? "Y" : "N");
        prepareCall.setString(6, z2 ? "Q" : "E");
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        str2 = jtdsResultSet.getMetaData().getColumnCount();
        str3 = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        str3.moveToInsertRow();
        while (jtdsResultSet.next()) {
            for (int i = true; i <= str2; i++) {
                str3.updateObject(i, (Object) jtdsResultSet.getObject(i));
            }
            str3.insertRow();
        }
        jtdsResultSet.close();
        str3.moveToCurrentRow();
        str3.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return str3;
    }

    public int getMaxCatalogNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxColumnNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        return this.tdsVersion >= 3 ? 0 : 16;
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        return this.tdsVersion >= 3 ? 0 : 16;
    }

    public int getMaxColumnsInTable() throws SQLException {
        return this.tdsVersion >= 3 ? 1024 : 250;
    }

    public int getMaxCursorNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxIndexLength() throws SQLException {
        return this.tdsVersion >= 3 ? 900 : 255;
    }

    public int getMaxProcedureNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxRowSize() throws SQLException {
        return this.tdsVersion >= 3 ? 8060 : 1962;
    }

    public int getMaxSchemaNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxTableNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public int getMaxTablesInSelect() throws SQLException {
        return this.tdsVersion > 2 ? 256 : 16;
    }

    public int getMaxUserNameLength() throws SQLException {
        return this.sysnameLength;
    }

    public ResultSet getPrimaryKeys(String str, String str2, String str3) throws SQLException {
        String[] strArr = new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "KEY_SEQ", "PK_NAME"};
        int[] iArr = new int[]{12, 12, 12, 12, 5, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_pkeys ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        str2 = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        str2.moveToInsertRow();
        str3 = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            for (int i = 1; i <= str3; i++) {
                str2.updateObject(i, jtdsResultSet.getObject(i));
            }
            str2.insertRow();
        }
        jtdsResultSet.close();
        str2.moveToCurrentRow();
        str2.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return str2;
    }

    public ResultSet getProcedureColumns(String str, String str2, String str3, String str4) throws SQLException {
        String[] strArr = new String[]{"PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "COLUMN_NAME", "COLUMN_TYPE", "DATA_TYPE", "TYPE_NAME", "PRECISION", "LENGTH", "SCALE", "RADIX", "NULLABLE", "REMARKS"};
        int[] iArr = new int[]{12, 12, 12, 12, 5, 4, 12, 4, 4, 5, 5, 5, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_sproc_columns ?, ?, ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str);
        prepareCall.setString(4, processEscapes(str4));
        prepareCall.setInt(5, 3);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        str2 = jtdsResultSet.getMetaData();
        str3 = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        str3.moveToInsertRow();
        while (jtdsResultSet.next() != null) {
            int i = true;
            int i2 = 0;
            while (i + i2 <= strArr.length) {
                if (i == 5 && !"column_type".equalsIgnoreCase(str2.getColumnName(i))) {
                    if ("RETURN_VALUE".equals(jtdsResultSet.getString(4))) {
                        str3.updateInt(i, 5);
                    } else {
                        str3.updateInt(i, 0);
                    }
                    i2 = 1;
                }
                if (i == 3) {
                    String string = jtdsResultSet.getString(i);
                    if (string != null && string.length() > 0) {
                        int lastIndexOf = string.lastIndexOf(59);
                        if (lastIndexOf >= 0) {
                            string = string.substring(0, lastIndexOf);
                        }
                    }
                    str3.updateString(i + i2, string);
                } else if ("data_type".equalsIgnoreCase(str2.getColumnName(i))) {
                    str3.updateInt(i + i2, TypeInfo.normalizeDataType(jtdsResultSet.getInt(i), this.connection.getUseLOBs()));
                } else {
                    str3.updateObject(i + i2, jtdsResultSet.getObject(i));
                }
                i++;
            }
            if (this.serverType == 2 && str2.getColumnCount() >= 22) {
                str4 = jtdsResultSet.getString(22);
                if (str4 != null) {
                    if (str4.equalsIgnoreCase("in")) {
                        str3.updateInt(5, 1);
                    } else if (str4.equalsIgnoreCase("out") != null) {
                        str3.updateInt(5, 2);
                    }
                }
            }
            if (this.serverType == 2 || this.tdsVersion == 1 || this.tdsVersion == 3) {
                if ("RETURN_VALUE".equals(jtdsResultSet.getString(4)) != null) {
                    str3.updateString(4, "@RETURN_VALUE");
                }
            }
            str3.insertRow();
        }
        jtdsResultSet.close();
        str3.moveToCurrentRow();
        str3.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return str3;
    }

    public ResultSet getProcedures(String str, String str2, String str3) throws SQLException {
        String[] strArr = new String[]{"PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "RESERVED_1", "RESERVED_2", "RESERVED_3", "REMARKS", "PROCEDURE_TYPE"};
        int[] iArr = new int[]{12, 12, 12, 4, 4, 4, 12, 5};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_stored_procedures ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        str2 = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        str2.moveToInsertRow();
        str3 = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            str2.updateString(1, jtdsResultSet.getString(1));
            str2.updateString(2, jtdsResultSet.getString(2));
            String string = jtdsResultSet.getString(3);
            if (string != null && string.endsWith(";1")) {
                string = string.substring(0, string.length() - 2);
            }
            str2.updateString(3, string);
            for (int i = 4; i <= str3; i++) {
                str2.updateObject(i, jtdsResultSet.getObject(i));
            }
            if (str3 < 8) {
                str2.updateShort(8, (short) 2);
            }
            str2.insertRow();
        }
        str2.moveToCurrentRow();
        str2.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        jtdsResultSet.close();
        return str2;
    }

    public ResultSet getSchemas() throws SQLException {
        String str;
        StringBuffer stringBuffer;
        Statement createStatement = this.connection.createStatement();
        if (this.connection.getServerType() != 1 || this.connection.getDatabaseMajorVersion() < 9) {
            str = Driver.JDBC3 ? "SELECT name AS TABLE_SCHEM, NULL as TABLE_CATALOG FROM dbo.sysusers" : "SELECT name AS TABLE_SCHEM FROM dbo.sysusers";
            if (this.tdsVersion >= 3) {
                stringBuffer = new StringBuffer();
                stringBuffer.append(str);
                stringBuffer.append(" WHERE islogin=1");
                str = stringBuffer.toString();
            } else {
                stringBuffer = new StringBuffer();
                stringBuffer.append(str);
                stringBuffer.append(" WHERE uid>0");
                str = stringBuffer.toString();
            }
        } else {
            str = Driver.JDBC3 ? "SELECT name AS TABLE_SCHEM, NULL as TABLE_CATALOG FROM sys.schemas" : "SELECT name AS TABLE_SCHEM FROM sys.schemas";
        }
        stringBuffer = new StringBuffer();
        stringBuffer.append(str);
        stringBuffer.append(" ORDER BY TABLE_SCHEM");
        return createStatement.executeQuery(stringBuffer.toString());
    }

    public String getStringFunctions() throws SQLException {
        return this.connection.getServerType() == 1 ? "ascii,char,concat,difference,insert,lcase,left,length,locate,ltrim,repeat,replace,right,rtrim,soundex,space,substring,ucase" : "ascii,char,concat,difference,insert,lcase,length,ltrim,repeat,right,rtrim,soundex,space,substring,ucase";
    }

    public ResultSet getTablePrivileges(String str, String str2, String str3) throws SQLException {
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_table_privileges ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        jtdsResultSet.setColLabel(1, "TABLE_CAT");
        jtdsResultSet.setColLabel(2, "TABLE_SCHEM");
        upperCaseColumnNames(jtdsResultSet);
        return jtdsResultSet;
    }

    public ResultSet getTables(String str, String str2, String str3, String[] strArr) throws SQLException {
        r1 = new String[10];
        int i = 0;
        r1[0] = "TABLE_CAT";
        r1[1] = "TABLE_SCHEM";
        r1[2] = "TABLE_NAME";
        r1[3] = "TABLE_TYPE";
        r1[4] = "REMARKS";
        r1[5] = "TYPE_CAT";
        r1[6] = "TYPE_SCHEM";
        r1[7] = "TYPE_NAME";
        r1[8] = "SELF_REFERENCING_COL_NAME";
        r1[9] = "REF_GENERATION";
        int[] iArr = new int[]{12, 12, 12, 12, 12, 12, 12, 12, 12, 12};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_tables ?, ?, ?, ?"));
        prepareCall.setString(1, processEscapes(str3));
        prepareCall.setString(2, processEscapes(str2));
        prepareCall.setString(3, str);
        if (strArr == null) {
            prepareCall.setString(4, null);
        } else {
            str = new StringBuffer(64);
            str.append('\"');
            while (i < strArr.length) {
                str.append('\'');
                str.append(strArr[i]);
                str.append("',");
                i++;
            }
            if (str.length() > 1) {
                str.setLength(str.length() - 1);
            }
            str.append('\"');
            prepareCall.setString(4, str.toString());
        }
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        str2 = new CachedResultSet((JtdsStatement) prepareCall, r1, iArr);
        str2.moveToInsertRow();
        str3 = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next() != null) {
            for (int i2 = 1; i2 <= str3; i2++) {
                str2.updateObject(i2, jtdsResultSet.getObject(i2));
            }
            str2.insertRow();
        }
        str2.moveToCurrentRow();
        str2.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        jtdsResultSet.close();
        return str2;
    }

    public ResultSet getTableTypes() throws SQLException {
        return this.connection.createStatement().executeQuery("select 'SYSTEM TABLE' TABLE_TYPE union select 'TABLE' TABLE_TYPE union select 'VIEW' TABLE_TYPE order by TABLE_TYPE");
    }

    public ResultSet getTypeInfo() throws SQLException {
        Statement createStatement = this.connection.createStatement();
        try {
            JtdsResultSet jtdsResultSet = (JtdsResultSet) createStatement.executeQuery("exec sp_datatype_info @ODBCVer=3");
            try {
                ResultSet createTypeInfoResultSet = createTypeInfoResultSet(jtdsResultSet, this.connection.getUseLOBs());
                return createTypeInfoResultSet;
            } finally {
                jtdsResultSet.close();
            }
        } catch (SQLException e) {
            createStatement.close();
            throw e;
        }
    }

    public ResultSet getUDTs(String str, String str2, String str3, int[] iArr) throws SQLException {
        iArr = new CachedResultSet((JtdsStatement) this.connection.createStatement(), new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "CLASS_NAME", "DATA_TYPE", "REMARKS", "BASE_TYPE"}, new int[]{12, 12, 12, 12, 4, 12, 5});
        iArr.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return iArr;
    }

    public String getURL() throws SQLException {
        return this.connection.getURL();
    }

    public String getUserName() throws SQLException {
        Throwable th;
        ResultSet resultSet = null;
        Statement createStatement;
        try {
            createStatement = this.connection.createStatement();
            try {
                ResultSet executeQuery;
                if (this.connection.getServerType() == 2) {
                    executeQuery = createStatement.executeQuery("select suser_name()");
                } else {
                    executeQuery = createStatement.executeQuery("select system_user");
                }
                resultSet = executeQuery;
                if (resultSet.next()) {
                    String string = resultSet.getString(1);
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (createStatement != null) {
                        createStatement.close();
                    }
                    return string;
                }
                throw new SQLException(Messages.get("error.dbmeta.nouser"), "HY000");
            } catch (Throwable th2) {
                th = th2;
                if (resultSet != null) {
                    resultSet.close();
                }
                if (createStatement != null) {
                    createStatement.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            createStatement = resultSet;
            if (resultSet != null) {
                resultSet.close();
            }
            if (createStatement != null) {
                createStatement.close();
            }
            throw th;
        }
    }

    public ResultSet getVersionColumns(String str, String str2, String str3) throws SQLException {
        String[] strArr = new String[]{"SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN"};
        int[] iArr = new int[]{5, 12, 4, 12, 4, 4, 5, 5};
        CallableStatement prepareCall = this.connection.prepareCall(syscall(str, "sp_special_columns ?, ?, ?, ?, ?, ?, ?"));
        prepareCall.setString(1, str3);
        prepareCall.setString(2, str2);
        prepareCall.setString(3, str);
        prepareCall.setString(4, "V");
        prepareCall.setString(5, "C");
        prepareCall.setString(6, "O");
        prepareCall.setInt(7, 3);
        JtdsResultSet jtdsResultSet = (JtdsResultSet) prepareCall.executeQuery();
        str2 = new CachedResultSet((JtdsStatement) prepareCall, strArr, iArr);
        str2.moveToInsertRow();
        str3 = jtdsResultSet.getMetaData().getColumnCount();
        while (jtdsResultSet.next()) {
            for (int i = 1; i <= str3; i++) {
                str2.updateObject(i, jtdsResultSet.getObject(i));
            }
            str2.insertRow();
        }
        str2.moveToCurrentRow();
        str2.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        jtdsResultSet.close();
        return str2;
    }

    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        setCaseSensitiveFlag();
        return this.caseSensitive.booleanValue() ^ 1;
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        setCaseSensitiveFlag();
        return this.caseSensitive.booleanValue() ^ 1;
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        boolean z = true;
        if (this.connection.getServerType() != 2) {
            return true;
        }
        if (getDatabaseMajorVersion() < 12) {
            z = false;
        }
        return z;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        setCaseSensitiveFlag();
        return this.caseSensitive.booleanValue();
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        setCaseSensitiveFlag();
        return this.caseSensitive.booleanValue();
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return this.connection.getServerType() == 2;
    }

    public boolean supportsResultSetConcurrency(int i, int i2) throws SQLException {
        boolean z = false;
        if (supportsResultSetType(i) && i2 >= PointerIconCompat.TYPE_CROSSHAIR) {
            if (i2 <= PointerIconCompat.TYPE_ALIAS) {
                if (i != PointerIconCompat.TYPE_WAIT || i2 == PointerIconCompat.TYPE_CROSSHAIR) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    private void setCaseSensitiveFlag() throws SQLException {
        if (this.caseSensitive == null) {
            Statement createStatement = this.connection.createStatement();
            ResultSet executeQuery = createStatement.executeQuery("sp_server_info 16");
            executeQuery.next();
            this.caseSensitive = "MIXED".equalsIgnoreCase(executeQuery.getString(3)) ? Boolean.FALSE : Boolean.TRUE;
            createStatement.close();
        }
    }

    public ResultSet getAttributes(String str, String str2, String str3, String str4) throws SQLException {
        str4 = new CachedResultSet((JtdsStatement) this.connection.createStatement(), new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "ATTR_NAME", "DATA_TYPE", "ATTR_TYPE_NAME", "ATTR_SIZE", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "NULLABLE", "REMARKS", "ATTR_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SCOPE_CATALOG", "SCOPE_SCHEMA", "SCOPE_TABLE", "SOURCE_DATA_TYPE"}, new int[]{12, 12, 12, 12, 4, 12, 4, 4, 4, 4, 12, 12, 4, 4, 4, 4, 12, 12, 12, 12, 5});
        str4.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return str4;
    }

    public int getDatabaseMajorVersion() throws SQLException {
        return this.connection.getDatabaseMajorVersion();
    }

    public int getDatabaseMinorVersion() throws SQLException {
        return this.connection.getDatabaseMinorVersion();
    }

    public ResultSet getSuperTables(String str, String str2, String str3) throws SQLException {
        ResultSet cachedResultSet = new CachedResultSet((JtdsStatement) this.connection.createStatement(), new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "SUPERTABLE_NAME"}, new int[]{12, 12, 12, 12});
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    public ResultSet getSuperTypes(String str, String str2, String str3) throws SQLException {
        ResultSet cachedResultSet = new CachedResultSet((JtdsStatement) this.connection.createStatement(), new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SUPERTYPE_CAT", "SUPERTYPE_SCHEM", "SUPERTYPE_NAME"}, new int[]{12, 12, 12, 12, 12, 12});
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    private static String processEscapes(String str) {
        if (str != null) {
            if (str.indexOf(92) != -1) {
                int length = str.length();
                StringBuffer stringBuffer = new StringBuffer(length + 10);
                int i = 0;
                while (i < length) {
                    if (str.charAt(i) != '\\') {
                        stringBuffer.append(str.charAt(i));
                    } else if (i < length - 1) {
                        stringBuffer.append('[');
                        i++;
                        stringBuffer.append(str.charAt(i));
                        stringBuffer.append(']');
                    }
                    i++;
                }
                return stringBuffer.toString();
            }
        }
        return str;
    }

    private String syscall(String str, String str2) {
        StringBuffer stringBuffer = new StringBuffer(str2.length() + 30);
        stringBuffer.append("{call ");
        if (str != null) {
            if (this.tdsVersion >= 3) {
                stringBuffer.append('[');
                stringBuffer.append(str);
                stringBuffer.append(']');
            } else {
                stringBuffer.append(str);
            }
            stringBuffer.append("..");
        }
        stringBuffer.append(str2);
        stringBuffer.append('}');
        return stringBuffer.toString();
    }

    private static void upperCaseColumnNames(JtdsResultSet jtdsResultSet) throws SQLException {
        ResultSetMetaData metaData = jtdsResultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnLabel = metaData.getColumnLabel(i);
            if (columnLabel != null && columnLabel.length() > 0) {
                jtdsResultSet.setColLabel(i, columnLabel.toUpperCase());
            }
        }
    }

    private static CachedResultSet createTypeInfoResultSet(JtdsResultSet jtdsResultSet, boolean z) throws SQLException {
        CachedResultSet cachedResultSet = new CachedResultSet(jtdsResultSet, false);
        if (cachedResultSet.getMetaData().getColumnCount() > 18) {
            cachedResultSet.setColumnCount(18);
        }
        cachedResultSet.setColLabel(3, "PRECISION");
        cachedResultSet.setColLabel(11, "FIXED_PREC_SCALE");
        upperCaseColumnNames(cachedResultSet);
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_TEXT);
        cachedResultSet.moveToInsertRow();
        for (TypeInfo update : getSortedTypes(jtdsResultSet, z)) {
            update.update(cachedResultSet);
            cachedResultSet.insertRow();
        }
        cachedResultSet.moveToCurrentRow();
        cachedResultSet.setConcurrency(PointerIconCompat.TYPE_CROSSHAIR);
        return cachedResultSet;
    }

    private static Collection getSortedTypes(ResultSet resultSet, boolean z) throws SQLException {
        Collection arrayList = new ArrayList(40);
        while (resultSet.next()) {
            arrayList.add(new TypeInfo(resultSet, z));
        }
        Collections.sort(arrayList);
        return arrayList;
    }
}
