package net.sourceforge.jtds.jdbc;

import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import net.sourceforge.jtds.ssl.Ssl;
import net.sourceforge.jtds.util.Logger;
import net.sourceforge.jtds.util.SSPIJNIClient;
import net.sourceforge.jtds.util.TimerThread;
import net.sourceforge.jtds.util.TimerThread.TimerListener;

public class TdsCore {
    private static final byte ALTMETADATA_TOKEN = (byte) -120;
    private static final int ASYNC_CANCEL = 0;
    public static final byte CANCEL_PKT = (byte) 6;
    public static final int DEFAULT_MIN_PKT_SIZE_TDS70 = 4096;
    static final byte DONE_CANCEL = (byte) 32;
    private static final byte DONE_END_OF_RESPONSE = Byte.MIN_VALUE;
    private static final byte DONE_ERROR = (byte) 2;
    private static final byte DONE_MORE_RESULTS = (byte) 1;
    private static final byte DONE_ROW_COUNT = (byte) 16;
    private static final ParamInfo[] EMPTY_PARAMETER_INFO = new ParamInfo[0];
    public static final int EXECUTE_SQL = 2;
    public static final byte LOGIN_PKT = (byte) 2;
    public static final int MAX_PKT_SIZE = 32768;
    public static final int MIN_PKT_SIZE = 512;
    public static final byte MSDTC_PKT = (byte) 14;
    public static final byte MSLOGIN_PKT = (byte) 16;
    public static final byte NTLMAUTH_PKT = (byte) 17;
    public static final int PKT_HDR_LEN = 8;
    public static final byte PRELOGIN_PKT = (byte) 18;
    public static final int PREPARE = 3;
    public static final byte QUERY_PKT = (byte) 1;
    public static final byte REPLY_PKT = (byte) 4;
    public static final byte RPC_PKT = (byte) 3;
    public static final int SSL_CLIENT_FORCE_ENCRYPT = 1;
    public static final int SSL_ENCRYPT_LOGIN = 0;
    public static final int SSL_NO_ENCRYPT = 2;
    public static final int SSL_SERVER_FORCE_ENCRYPT = 3;
    public static final byte SYBQUERY_PKT = (byte) 15;
    static final int SYB_BIGINT = 64;
    static final int SYB_BITNULL = 4;
    static final int SYB_DATETIME = 2;
    static final int SYB_EXTCOLINFO = 8;
    static final int SYB_LONGDATA = 1;
    static final int SYB_UNICODE = 16;
    static final int SYB_UNITEXT = 32;
    private static final byte TDS5_DYNAMIC_TOKEN = (byte) -25;
    private static final byte TDS5_PARAMFMT2_TOKEN = (byte) 32;
    private static final byte TDS5_PARAMFMT_TOKEN = (byte) -20;
    private static final byte TDS5_PARAMS_TOKEN = (byte) -41;
    private static final byte TDS5_WIDE_RESULT = (byte) 97;
    private static final byte TDS7_RESULT_TOKEN = (byte) -127;
    private static final byte TDS_ALTROW = (byte) -45;
    private static final byte TDS_AUTH_TOKEN = (byte) -19;
    private static final byte TDS_CAP_TOKEN = (byte) -30;
    private static final byte TDS_CLOSE_TOKEN = (byte) 113;
    private static final byte TDS_COLFMT_TOKEN = (byte) -95;
    private static final byte TDS_COLINFO_TOKEN = (byte) -91;
    private static final byte TDS_COLNAME_TOKEN = (byte) -96;
    private static final byte TDS_COMP_NAMES_TOKEN = (byte) -89;
    private static final byte TDS_COMP_RESULT_TOKEN = (byte) -88;
    private static final byte TDS_CONTROL_TOKEN = (byte) -82;
    private static final byte TDS_DBRPC_TOKEN = (byte) -26;
    private static final byte TDS_DONEINPROC_TOKEN = (byte) -1;
    private static final byte TDS_DONEPROC_TOKEN = (byte) -2;
    private static final byte TDS_DONE_TOKEN = (byte) -3;
    private static final byte TDS_ENVCHANGE_TOKEN = (byte) -29;
    private static final byte TDS_ENV_CHARSET = (byte) 3;
    private static final byte TDS_ENV_DATABASE = (byte) 1;
    private static final byte TDS_ENV_LANG = (byte) 2;
    private static final byte TDS_ENV_LCID = (byte) 5;
    private static final byte TDS_ENV_PACKSIZE = (byte) 4;
    private static final byte TDS_ENV_SQLCOLLATION = (byte) 7;
    private static final byte TDS_ERROR_TOKEN = (byte) -86;
    private static final byte TDS_INFO_TOKEN = (byte) -85;
    private static final byte TDS_LANG_TOKEN = (byte) 33;
    private static final byte TDS_LOGINACK_TOKEN = (byte) -83;
    private static final byte TDS_MSG50_TOKEN = (byte) -27;
    private static final byte TDS_OFFSETS_TOKEN = (byte) 120;
    private static final byte TDS_ORDER_TOKEN = (byte) -87;
    private static final byte TDS_PARAM_TOKEN = (byte) -84;
    private static final byte TDS_PROCID = (byte) 124;
    private static final byte TDS_RESULT_TOKEN = (byte) -18;
    private static final byte TDS_RETURNSTATUS_TOKEN = (byte) 121;
    private static final byte TDS_ROW_TOKEN = (byte) -47;
    private static final byte TDS_TABNAME_TOKEN = (byte) -92;
    public static final int TEMPORARY_STORED_PROCEDURES = 1;
    private static final int TIMEOUT_CANCEL = 1;
    public static final int UNPREPARED = 0;
    private static String hostName;
    private static SSPIJNIClient sspiJNIClient;
    private static HashMap tds8SpNames = new HashMap();
    private final int[] cancelMonitor = new int[1];
    private boolean cancelPending;
    private ColInfo[] columns;
    private ColInfo[] computedColumns;
    private Object[] computedRowData;
    private final ConnectionJDBC2 connection;
    private Semaphore connectionLock;
    private final TdsToken currentToken = new TdsToken();
    private boolean endOfResponse = true;
    private boolean endOfResults = true;
    private boolean fatalError;
    private final ResponseStream in;
    private boolean inBatch;
    private boolean isClosed;
    private final SQLDiagnostic messages;
    private int nextParam = -1;
    private boolean ntlmAuthSSO;
    private final RequestStream out;
    private ParamInfo[] parameters;
    private ParamInfo returnParam;
    private Integer returnStatus;
    private Object[] rowData;
    private final int serverType;
    private final SharedSocket socket;
    private int sslMode = 2;
    private TableMetaData[] tables;
    private int tdsVersion;

    private static class TableMetaData {
        String catalog;
        String name;
        String schema;

        private TableMetaData() {
        }
    }

    private static class TdsToken {
        Object[] dynamParamData;
        ColInfo[] dynamParamInfo;
        byte[] nonce;
        byte[] ntlmMessage;
        byte[] ntlmTarget;
        byte operation;
        byte status;
        byte token;
        int updateCount;

        private TdsToken() {
        }

        boolean isUpdateCount() {
            return (this.token == TdsCore.TDS_DONE_TOKEN || this.token == TdsCore.TDS_DONEINPROC_TOKEN) && (this.status & 16) != 0;
        }

        boolean isEndToken() {
            if (!(this.token == TdsCore.TDS_DONE_TOKEN || this.token == TdsCore.TDS_DONEINPROC_TOKEN)) {
                if (this.token != TdsCore.TDS_DONEPROC_TOKEN) {
                    return false;
                }
            }
            return true;
        }

        boolean isAuthToken() {
            return this.token == TdsCore.TDS_AUTH_TOKEN;
        }

        boolean resultsPending() {
            if (isEndToken()) {
                return (this.status & 1) != 0;
            } else {
                return true;
            }
        }

        boolean isResultSet() {
            if (!(this.token == TdsCore.TDS_COLFMT_TOKEN || this.token == TdsCore.TDS7_RESULT_TOKEN || this.token == TdsCore.TDS_RESULT_TOKEN || this.token == TdsCore.TDS5_WIDE_RESULT || this.token == TdsCore.TDS_COLINFO_TOKEN || this.token == TdsCore.TDS_ROW_TOKEN || this.token == TdsCore.ALTMETADATA_TOKEN)) {
                if (this.token != TdsCore.TDS_ALTROW) {
                    return false;
                }
            }
            return true;
        }

        public boolean isRowData() {
            if (this.token != TdsCore.TDS_ROW_TOKEN) {
                if (this.token != TdsCore.TDS_ALTROW) {
                    return false;
                }
            }
            return true;
        }
    }

    static {
        tds8SpNames.put("sp_cursor", new Integer(1));
        tds8SpNames.put("sp_cursoropen", new Integer(2));
        tds8SpNames.put("sp_cursorprepare", new Integer(3));
        tds8SpNames.put("sp_cursorexecute", new Integer(4));
        tds8SpNames.put("sp_cursorprepexec", new Integer(5));
        tds8SpNames.put("sp_cursorunprepare", new Integer(6));
        tds8SpNames.put("sp_cursorfetch", new Integer(7));
        tds8SpNames.put("sp_cursoroption", new Integer(8));
        tds8SpNames.put("sp_cursorclose", new Integer(9));
        tds8SpNames.put("sp_executesql", new Integer(10));
        tds8SpNames.put("sp_prepare", new Integer(11));
        tds8SpNames.put("sp_execute", new Integer(12));
        tds8SpNames.put("sp_prepexec", new Integer(13));
        tds8SpNames.put("sp_prepexecrpc", new Integer(14));
        tds8SpNames.put("sp_unprepare", new Integer(15));
    }

    TdsCore(ConnectionJDBC2 connectionJDBC2, SQLDiagnostic sQLDiagnostic) {
        this.connection = connectionJDBC2;
        this.socket = connectionJDBC2.getSocket();
        this.messages = sQLDiagnostic;
        this.serverType = connectionJDBC2.getServerType();
        this.tdsVersion = this.socket.getTdsVersion();
        this.out = this.socket.getRequestStream(connectionJDBC2.getNetPacketSize(), connectionJDBC2.getMaxPrecision());
        this.in = this.socket.getResponseStream(this.out, connectionJDBC2.getNetPacketSize());
    }

    private void checkOpen() throws SQLException {
        if (this.connection.isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "Connection"), "HY010");
        }
    }

    int getTdsVersion() {
        return this.tdsVersion;
    }

    ColInfo[] getColumns() {
        return this.columns;
    }

    void setColumns(ColInfo[] colInfoArr) {
        this.columns = colInfoArr;
        this.rowData = new Object[colInfoArr.length];
        this.tables = null;
    }

    ParamInfo[] getParameters() {
        if (this.currentToken.dynamParamInfo == null) {
            return EMPTY_PARAMETER_INFO;
        }
        ParamInfo[] paramInfoArr = new ParamInfo[this.currentToken.dynamParamInfo.length];
        for (int i = 0; i < paramInfoArr.length; i++) {
            ColInfo colInfo = this.currentToken.dynamParamInfo[i];
            paramInfoArr[i] = new ParamInfo(colInfo, colInfo.realName, null, 0);
        }
        return paramInfoArr;
    }

    Object[] getRowData() {
        return this.rowData;
    }

    void negotiateSSL(String str, String str2) throws IOException, SQLException {
        if (!str2.equalsIgnoreCase("off")) {
            if (!str2.equalsIgnoreCase(Ssl.SSL_REQUIRE)) {
                if (!str2.equalsIgnoreCase(Ssl.SSL_AUTHENTICATE)) {
                    sendPreLoginPacket(str, false);
                    this.sslMode = readPreLoginPacket();
                    if (this.sslMode != 2) {
                        this.socket.enableEncryption(str2);
                    }
                }
            }
            sendPreLoginPacket(str, true);
            this.sslMode = readPreLoginPacket();
            if (!(this.sslMode == 1 || this.sslMode == 3)) {
                throw new SQLException(Messages.get("error.ssl.encryptionoff"), "08S01");
            }
            if (this.sslMode != 2) {
                this.socket.enableEncryption(str2);
            }
        }
    }

    void login(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, int i) throws SQLException {
        TdsCore tdsCore = this;
        try {
            String hostName = str9.length() == 0 ? getHostName() : str9;
            if (tdsCore.tdsVersion >= 3) {
                sendMSLoginPkt(str, str2, str3, str4, str5, str7, str8, hostName, str10, str11, i);
            } else if (tdsCore.tdsVersion == 2) {
                send50LoginPkt(str, str3, str4, str6, str7, str8, hostName, str10, i);
            } else {
                send42LoginPkt(str, str3, str4, str6, str7, str8, hostName, str10, i);
            }
            if (tdsCore.sslMode == 0) {
                tdsCore.socket.disableEncryption();
            }
            nextToken();
            while (!tdsCore.endOfResponse) {
                if (tdsCore.currentToken.isAuthToken()) {
                    sendNtlmChallengeResponse(tdsCore.currentToken.nonce, str3, str4, str5);
                } else {
                    String str12 = str3;
                    String str13 = str4;
                    String str14 = str5;
                }
                nextToken();
            }
            tdsCore.messages.checkErrors();
        } catch (Throwable e) {
            Throwable th = e;
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", th.getMessage()), "08S01"), th);
        }
    }

    boolean getMoreResults() throws SQLException {
        checkOpen();
        nextToken();
        while (!this.endOfResponse && !this.currentToken.isUpdateCount() && !this.currentToken.isResultSet()) {
            nextToken();
        }
        if (this.currentToken.isResultSet()) {
            byte b = this.currentToken.token;
            try {
                byte peek = (byte) this.in.peek();
                while (true) {
                    if (!(peek == TDS_TABNAME_TOKEN || peek == TDS_COLINFO_TOKEN)) {
                        if (peek != TDS_CONTROL_TOKEN) {
                            break;
                        }
                    }
                    nextToken();
                    peek = (byte) this.in.peek();
                }
                this.currentToken.token = b;
            } catch (Throwable e) {
                this.connection.setClosed();
                throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "08S01"), e);
            }
        }
        return this.currentToken.isResultSet();
    }

    boolean isResultSet() {
        return this.currentToken.isResultSet();
    }

    boolean isRowData() {
        return this.currentToken.isRowData();
    }

    boolean isUpdateCount() {
        return this.currentToken.isUpdateCount();
    }

    int getUpdateCount() {
        return this.currentToken.isEndToken() ? this.currentToken.updateCount : -1;
    }

    boolean isEndOfResponse() {
        return this.endOfResponse;
    }

    void clearResponseQueue() throws SQLException {
        checkOpen();
        while (!this.endOfResponse) {
            nextToken();
        }
    }

    void consumeOneResponse() throws SQLException {
        checkOpen();
        while (!this.endOfResponse) {
            nextToken();
            if (this.currentToken.isEndToken() && (this.currentToken.status & -128) != 0) {
                return;
            }
        }
    }

    boolean getNextRow() throws SQLException {
        if (!this.endOfResponse) {
            if (!this.endOfResults) {
                checkOpen();
                nextToken();
                while (!this.currentToken.isRowData() && !this.currentToken.isEndToken()) {
                    nextToken();
                }
                if (this.endOfResults) {
                    return false;
                }
                return this.currentToken.isRowData();
            }
        }
        return false;
    }

    boolean isDataInResultSet() throws SQLException {
        checkOpen();
        try {
            byte peek = this.endOfResponse ? TDS_DONE_TOKEN : (byte) this.in.peek();
            while (peek != TDS_ROW_TOKEN && peek != TDS_ALTROW && peek != TDS_DONE_TOKEN && peek != TDS_DONEINPROC_TOKEN && peek != TDS_DONEPROC_TOKEN) {
                nextToken();
                peek = (byte) this.in.peek();
            }
            this.messages.checkErrors();
            if (peek != TDS_ROW_TOKEN) {
                if (peek != TDS_ALTROW) {
                    return false;
                }
            }
            return true;
        } catch (Throwable e) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "08S01"), e);
        }
    }

    Integer getReturnStatus() {
        return this.returnStatus;
    }

    synchronized void closeConnection() {
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
        monitor-enter(r2);
        r0 = r2.tdsVersion;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r1 = 2;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        if (r0 != r1) goto L_0x002f;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
    L_0x0006:
        r0 = r2.socket;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r1 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r0.setTimeout(r1);	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r0 = r2.out;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r1 = 15;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r0.setPacketType(r1);	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r0 = r2.out;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r1 = 113; // 0x71 float:1.58E-43 double:5.6E-322;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r0.write(r1);	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r0 = r2.out;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r1 = 0;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r0.write(r1);	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r0 = r2.out;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r0.flush();	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r2.endOfResponse = r1;	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        r2.clearResponseQueue();	 Catch:{ Exception -> 0x002f, all -> 0x002c }
        goto L_0x002f;
    L_0x002c:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
    L_0x002f:
        monitor-exit(r2);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.closeConnection():void");
    }

    void close() throws SQLException {
        if (!this.isClosed) {
            try {
                clearResponseQueue();
                this.out.close();
                this.in.close();
            } finally {
                this.isClosed = true;
            }
        }
    }

    void cancel(boolean z) {
        Semaphore mutex;
        try {
            mutex = this.connection.getMutex();
            try {
                synchronized (this.cancelMonitor) {
                    if (!(this.cancelPending || this.endOfResponse)) {
                        this.cancelPending = this.socket.cancel(this.out.getVirtualSocket());
                    }
                    if (this.cancelPending) {
                        this.cancelMonitor[0] = z;
                        this.endOfResponse = false;
                    }
                }
                if (mutex != null) {
                    mutex.release();
                }
            } catch (Throwable th) {
                z = th;
            }
        } catch (Throwable th2) {
            z = th2;
            mutex = null;
            if (mutex != null) {
                mutex.release();
            }
            throw z;
        }
    }

    void submitSQL(String str) throws SQLException {
        checkOpen();
        this.messages.clearWarnings();
        if (str.length() == 0) {
            throw new IllegalArgumentException("submitSQL() called with empty SQL String");
        }
        executeSQL(str, null, null, false, 0, -1, -1, true);
        clearResponseQueue();
        this.messages.checkErrors();
    }

    void startBatch() {
        this.inBatch = true;
    }

    synchronized void executeSQL(String str, String str2, ParamInfo[] paramInfoArr, boolean z, int i, int i2, int i3, boolean z2) throws SQLException {
        Throwable th;
        TdsCore tdsCore = this;
        String str3 = str;
        ParamInfo[] paramInfoArr2 = paramInfoArr;
        synchronized (this) {
            boolean z3 = true;
            try {
                String substituteParameters;
                ParamInfo[] paramInfoArr3;
                StringBuffer stringBuffer;
                if (tdsCore.connectionLock == null) {
                    tdsCore.connectionLock = tdsCore.connection.getMutex();
                }
                clearResponseQueue();
                tdsCore.messages.exceptions = null;
                setRowCountAndTextSize(i2, i3);
                tdsCore.messages.clearWarnings();
                tdsCore.returnStatus = null;
                if (paramInfoArr2 != null && paramInfoArr2.length == 0) {
                    paramInfoArr2 = null;
                }
                tdsCore.parameters = paramInfoArr2;
                String str4 = (str2 == null || str2.length() != 0) ? str2 : null;
                if (paramInfoArr2 == null || !paramInfoArr2[0].isRetVal) {
                    tdsCore.returnParam = null;
                    tdsCore.nextParam = -1;
                } else {
                    tdsCore.returnParam = paramInfoArr2[0];
                    tdsCore.nextParam = 0;
                }
                if (paramInfoArr2 != null) {
                    int i4;
                    if (str4 == null && str3.startsWith("EXECUTE ")) {
                        i4 = 0;
                        while (i4 < paramInfoArr2.length) {
                            if (paramInfoArr2[i4].isRetVal || !paramInfoArr2[i4].isOutput) {
                                i4++;
                            } else {
                                throw new SQLException(Messages.get("error.prepare.nooutparam", Integer.toString(i4 + 1)), "07000");
                            }
                        }
                        substituteParameters = Support.substituteParameters(str3, paramInfoArr2, tdsCore.connection);
                        paramInfoArr3 = null;
                        switch (tdsCore.tdsVersion) {
                            case 1:
                                executeSQL42(substituteParameters, str4, paramInfoArr3, z, z2);
                                break;
                            case 2:
                                executeSQL50(substituteParameters, str4, paramInfoArr3);
                                break;
                            case 3:
                            case 4:
                            case 5:
                                executeSQL70(substituteParameters, str4, paramInfoArr3, z, z2);
                                break;
                            default:
                                stringBuffer = new StringBuffer();
                                stringBuffer.append("Unknown TDS version ");
                                stringBuffer.append(tdsCore.tdsVersion);
                                throw new IllegalStateException(stringBuffer.toString());
                        }
                        if (z2) {
                            tdsCore.out.flush();
                            tdsCore.connectionLock.release();
                            tdsCore.connectionLock = null;
                            try {
                                tdsCore.endOfResponse = false;
                                tdsCore.endOfResults = true;
                                wait(i);
                            } catch (Throwable e) {
                                th = e;
                                z3 = false;
                                tdsCore.connection.setClosed();
                                throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", th.getMessage()), "08S01"), th);
                            } catch (Throwable e2) {
                                th = e2;
                                z3 = false;
                                if (tdsCore.connectionLock != null) {
                                    tdsCore.connectionLock.release();
                                    tdsCore.connectionLock = null;
                                }
                                if (z2) {
                                    tdsCore.inBatch = false;
                                }
                                throw th;
                            }
                        }
                        if (!z2) {
                            try {
                                if (tdsCore.connectionLock != null) {
                                    tdsCore.connectionLock.release();
                                    tdsCore.connectionLock = null;
                                }
                            } catch (Throwable e22) {
                                th = e22;
                            }
                        }
                        if (z2) {
                            tdsCore.inBatch = false;
                        }
                    } else {
                        i4 = 0;
                        while (i4 < paramInfoArr2.length) {
                            if (paramInfoArr2[i4].isSet || paramInfoArr2[i4].isOutput) {
                                paramInfoArr2[i4].clearOutValue();
                                TdsData.getNativeType(tdsCore.connection, paramInfoArr2[i4]);
                                i4++;
                            } else {
                                throw new SQLException(Messages.get("error.prepare.paramnotset", Integer.toString(i4 + 1)), "07000");
                            }
                        }
                    }
                }
                paramInfoArr3 = paramInfoArr2;
                substituteParameters = str3;
                try {
                    switch (tdsCore.tdsVersion) {
                        case 1:
                            executeSQL42(substituteParameters, str4, paramInfoArr3, z, z2);
                            break;
                        case 2:
                            executeSQL50(substituteParameters, str4, paramInfoArr3);
                            break;
                        case 3:
                        case 4:
                        case 5:
                            executeSQL70(substituteParameters, str4, paramInfoArr3, z, z2);
                            break;
                        default:
                            stringBuffer = new StringBuffer();
                            stringBuffer.append("Unknown TDS version ");
                            stringBuffer.append(tdsCore.tdsVersion);
                            throw new IllegalStateException(stringBuffer.toString());
                    }
                    if (z2) {
                        tdsCore.out.flush();
                        tdsCore.connectionLock.release();
                        tdsCore.connectionLock = null;
                        tdsCore.endOfResponse = false;
                        tdsCore.endOfResults = true;
                        wait(i);
                    }
                    if (!z2) {
                        if (tdsCore.connectionLock != null) {
                            tdsCore.connectionLock.release();
                            tdsCore.connectionLock = null;
                        }
                    }
                    if (z2) {
                        tdsCore.inBatch = false;
                    }
                } catch (Throwable e222) {
                    th = e222;
                    tdsCore.connection.setClosed();
                    throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", th.getMessage()), "08S01"), th);
                }
            } catch (Throwable e2222) {
                th = e2222;
                if (z2 || r8) {
                    if (tdsCore.connectionLock != null) {
                        tdsCore.connectionLock.release();
                        tdsCore.connectionLock = null;
                    }
                }
                if (z2) {
                    tdsCore.inBatch = false;
                }
                throw th;
            }
        }
    }

    String microsoftPrepare(String str, ParamInfo[] paramInfoArr, boolean z, int i, int i2) throws SQLException {
        Throwable th;
        ParamInfo[] paramInfoArr2 = paramInfoArr;
        checkOpen();
        this.messages.clearWarnings();
        int prepareSql = this.connection.getPrepareSql();
        int i3 = 0;
        if (prepareSql == 1) {
            StringBuffer stringBuffer = new StringBuffer((str.length() + 32) + (paramInfoArr2.length * 15));
            String procName = r10.connection.getProcName();
            stringBuffer.append("create proc ");
            stringBuffer.append(procName);
            stringBuffer.append(' ');
            while (i3 < paramInfoArr2.length) {
                stringBuffer.append("@P");
                stringBuffer.append(i3);
                stringBuffer.append(' ');
                stringBuffer.append(paramInfoArr2[i3].sqlType);
                i3++;
                if (i3 < paramInfoArr2.length) {
                    stringBuffer.append(',');
                }
            }
            stringBuffer.append(" as ");
            stringBuffer.append(Support.substituteParamMarkers(str, paramInfoArr));
            try {
                submitSQL(stringBuffer.toString());
                return procName;
            } catch (Throwable e) {
                th = e;
                if ("08S01".equals(th.getSQLState())) {
                    throw th;
                }
                r10.messages.addWarning(Support.linkException(new SQLWarning(Messages.get("error.prepare.prepfailed", th.getMessage()), th.getSQLState(), th.getErrorCode()), th));
            }
        } else {
            if (prepareSql == 3) {
                int cursorScrollOpt;
                String str2;
                ParamInfo[] paramInfoArr3 = new ParamInfo[(z ? 6 : 4)];
                paramInfoArr3[0] = new ParamInfo(4, null, 1);
                paramInfoArr3[1] = new ParamInfo(-1, Support.getParameterDefinitions(paramInfoArr), 4);
                paramInfoArr3[2] = new ParamInfo(-1, Support.substituteParamMarkers(str, paramInfoArr), 4);
                paramInfoArr3[3] = new ParamInfo(4, new Integer(1), 0);
                if (z) {
                    cursorScrollOpt = MSCursorResultSet.getCursorScrollOpt(i, i2, true);
                    int cursorConcurrencyOpt = MSCursorResultSet.getCursorConcurrencyOpt(i2);
                    paramInfoArr3[4] = new ParamInfo(4, new Integer(cursorScrollOpt), 1);
                    paramInfoArr3[5] = new ParamInfo(4, new Integer(cursorConcurrencyOpt), 1);
                }
                r10.columns = null;
                if (z) {
                    try {
                        str2 = "sp_cursorprepare";
                    } catch (Throwable e2) {
                        th = e2;
                        if ("08S01".equals(th.getSQLState())) {
                            throw th;
                        }
                        r10.messages.addWarning(Support.linkException(new SQLWarning(Messages.get("error.prepare.prepfailed", th.getMessage()), th.getSQLState(), th.getErrorCode()), th));
                    }
                } else {
                    str2 = "sp_prepare";
                }
                executeSQL(null, str2, paramInfoArr3, false, 0, -1, -1, true);
                cursorScrollOpt = 0;
                while (!r10.endOfResponse) {
                    nextToken();
                    if (isResultSet()) {
                        cursorScrollOpt++;
                    }
                }
                if (cursorScrollOpt != 1) {
                    r10.columns = null;
                }
                Integer num = (Integer) paramInfoArr3[0].getOutValue();
                if (num != null) {
                    return num.toString();
                }
                r10.messages.checkErrors();
            }
            return null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    synchronized String sybasePrepare(String str, ParamInfo[] paramInfoArr) throws SQLException {
        checkOpen();
        this.messages.clearWarnings();
        if (str != null) {
            if (str.length() != 0) {
                String procName = this.connection.getProcName();
                if (procName != null) {
                    if (procName.length() == 11) {
                        int i = 0;
                        while (true) {
                            String str2 = null;
                            if (i < paramInfoArr.length) {
                                if ("text".equals(paramInfoArr[i].sqlType) || "unitext".equals(paramInfoArr[i].sqlType)) {
                                    break;
                                } else if ("image".equals(paramInfoArr[i].sqlType)) {
                                    break;
                                } else {
                                    i++;
                                }
                            } else {
                                try {
                                    break;
                                } catch (IOException e) {
                                    Throwable e2 = e;
                                    try {
                                        this.connection.setClosed();
                                        throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e2.getMessage()), "08S01"), e2);
                                    } catch (Throwable th) {
                                        str = th;
                                        paramInfoArr = str2;
                                        if (paramInfoArr != null) {
                                            paramInfoArr.release();
                                        }
                                        throw str;
                                    }
                                } catch (SQLException e3) {
                                    str = e3;
                                    paramInfoArr = null;
                                    try {
                                        if ("08S01".equals(str.getSQLState())) {
                                            throw str;
                                        }
                                        if (paramInfoArr != null) {
                                            paramInfoArr.release();
                                        }
                                        return null;
                                    } catch (Throwable th2) {
                                        str = th2;
                                        if (paramInfoArr != null) {
                                            paramInfoArr.release();
                                        }
                                        throw str;
                                    }
                                }
                            }
                        }
                    }
                }
                throw new IllegalArgumentException("procName parameter must be 11 characters long.");
            }
        }
        throw new IllegalArgumentException("sql parameter must be at least 1 character long.");
    }

    synchronized void sybaseUnPrepare(String str) throws SQLException {
        Throwable e;
        checkOpen();
        this.messages.clearWarnings();
        if (str != null) {
            if (str.length() == 11) {
                Semaphore semaphore = null;
                try {
                    Semaphore mutex = this.connection.getMutex();
                    try {
                        this.out.setPacketType(SYBQUERY_PKT);
                        this.out.write((byte) TDS5_DYNAMIC_TOKEN);
                        this.out.write((short) 15);
                        this.out.write((byte) 4);
                        this.out.write((byte) 0);
                        this.out.write((byte) 10);
                        this.out.writeAscii(str.substring(1));
                        this.out.write((short) 0);
                        this.out.flush();
                        this.endOfResponse = false;
                        clearResponseQueue();
                        this.messages.checkErrors();
                        if (mutex != null) {
                            mutex.release();
                        }
                    } catch (IOException e2) {
                        e = e2;
                        semaphore = mutex;
                        this.connection.setClosed();
                        throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "08S01"), e);
                    } catch (SQLException e3) {
                        str = e3;
                        semaphore = mutex;
                        try {
                            if ("08S01".equals(str.getSQLState())) {
                                if (semaphore != null) {
                                    semaphore.release();
                                }
                            }
                            throw str;
                        } catch (Throwable th) {
                            str = th;
                            mutex = semaphore;
                            if (mutex != null) {
                                mutex.release();
                            }
                            throw str;
                        }
                    } catch (Throwable th2) {
                        str = th2;
                        if (mutex != null) {
                            mutex.release();
                        }
                        throw str;
                    }
                } catch (IOException e4) {
                    e = e4;
                    this.connection.setClosed();
                    throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "08S01"), e);
                } catch (SQLException e5) {
                    str = e5;
                    if ("08S01".equals(str.getSQLState())) {
                        if (semaphore != null) {
                            semaphore.release();
                        }
                    }
                    throw str;
                }
            }
        }
        throw new IllegalArgumentException("procName parameter must be 11 characters long.");
    }

    synchronized byte[] enlistConnection(int i, byte[] bArr) throws SQLException {
        byte[] bArr2;
        Throwable e;
        bArr2 = null;
        try {
            Semaphore mutex = this.connection.getMutex();
            try {
                this.out.setPacketType(MSDTC_PKT);
                this.out.write((short) i);
                switch (i) {
                    case 0:
                        this.out.write((short) 0);
                        break;
                    case 1:
                        if (bArr == null) {
                            this.out.write((short) 0);
                            break;
                        }
                        this.out.write((short) bArr.length);
                        this.out.write(bArr);
                        break;
                    default:
                        break;
                }
                this.out.flush();
                this.endOfResponse = false;
                this.endOfResults = true;
                if (mutex != null) {
                    mutex.release();
                }
                if (!(getMoreResults() == null || getNextRow() == null || this.rowData.length != 1)) {
                    i = this.rowData[0];
                    if ((i instanceof byte[]) != null) {
                        bArr2 = (byte[]) i;
                    }
                }
                clearResponseQueue();
                this.messages.checkErrors();
            } catch (IOException e2) {
                e = e2;
                bArr2 = mutex;
                try {
                    this.connection.setClosed();
                    throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "08S01"), e);
                } catch (Throwable th) {
                    i = th;
                    mutex = bArr2;
                    if (mutex != null) {
                        mutex.release();
                    }
                    throw i;
                }
            } catch (Throwable th2) {
                i = th2;
                if (mutex != null) {
                    mutex.release();
                }
                throw i;
            }
        } catch (IOException e3) {
            e = e3;
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "08S01"), e);
        }
        return bArr2;
    }

    SQLException getBatchCounts(ArrayList arrayList, SQLException sQLException) throws SQLException {
        Object obj = JtdsStatement.SUCCESS_NO_INFO;
        try {
            checkOpen();
            while (!this.endOfResponse) {
                nextToken();
                if (!this.currentToken.isResultSet()) {
                    switch (this.currentToken.token) {
                        case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                            if ((this.currentToken.status & 2) == 0) {
                                if (obj != JtdsStatement.EXECUTE_FAILED) {
                                    if (this.currentToken.isUpdateCount()) {
                                        arrayList.add(new Integer(this.currentToken.updateCount));
                                    } else {
                                        arrayList.add(obj);
                                    }
                                    obj = JtdsStatement.SUCCESS_NO_INFO;
                                    break;
                                }
                            }
                            arrayList.add(JtdsStatement.EXECUTE_FAILED);
                            obj = JtdsStatement.SUCCESS_NO_INFO;
                        case (byte) -2:
                            if ((this.currentToken.status & 2) == 0) {
                                if (obj != JtdsStatement.EXECUTE_FAILED) {
                                    arrayList.add(obj);
                                    obj = JtdsStatement.SUCCESS_NO_INFO;
                                    break;
                                }
                            }
                            arrayList.add(JtdsStatement.EXECUTE_FAILED);
                            obj = JtdsStatement.SUCCESS_NO_INFO;
                        case (byte) -1:
                            if ((this.currentToken.status & 2) == 0) {
                                if (!this.currentToken.isUpdateCount()) {
                                    break;
                                }
                                obj = new Integer(this.currentToken.updateCount);
                                break;
                            }
                            obj = JtdsStatement.EXECUTE_FAILED;
                            break;
                        default:
                            break;
                    }
                }
                throw new SQLException(Messages.get("error.statement.batchnocount"), "07000");
            }
            this.messages.checkErrors();
            while (this.endOfResponse == null) {
                try {
                    nextToken();
                } catch (ArrayList arrayList2) {
                    checkOpen();
                    if (sQLException != null) {
                        sQLException.setNextException(arrayList2);
                    } else {
                        sQLException = arrayList2;
                    }
                }
            }
        } catch (SQLException e) {
            arrayList2 = e;
            if (sQLException != null) {
                sQLException.setNextException(arrayList2);
                arrayList2 = sQLException;
            }
            loop3:
            while (true) {
                sQLException = arrayList2;
                while (this.endOfResponse == null) {
                    try {
                        nextToken();
                    } catch (SQLException e2) {
                        arrayList2 = e2;
                        checkOpen();
                        if (sQLException != null) {
                            sQLException.setNextException(arrayList2);
                        }
                    }
                }
                break loop3;
            }
        } catch (Throwable th) {
            while (!this.endOfResponse) {
                try {
                    nextToken();
                } catch (SQLException e3) {
                    checkOpen();
                    if (sQLException != null) {
                        sQLException.setNextException(e3);
                    } else {
                        sQLException = e3;
                    }
                }
            }
        }
        return sQLException;
    }

    ColInfo[] getComputedColumns() {
        return this.computedColumns;
    }

    Object[] getComputedRowData() {
        try {
            Object[] objArr = this.computedRowData;
            return objArr;
        } finally {
            this.computedRowData = null;
        }
    }

    private void putLoginString(String str, int i) throws IOException {
        byte[] encodeString = Support.encodeString(this.connection.getCharset(), str);
        this.out.write(encodeString, 0, i);
        RequestStream requestStream = this.out;
        if (encodeString.length < i) {
            i = encodeString.length;
        }
        requestStream.write((byte) i);
    }

    private void sendPreLoginPacket(String str, boolean z) throws IOException {
        this.out.setPacketType(PRELOGIN_PKT);
        this.out.write((short) 0);
        this.out.write((short) 21);
        this.out.write((byte) 6);
        this.out.write((short) 1);
        this.out.write((short) 27);
        this.out.write((byte) 1);
        this.out.write((short) 2);
        this.out.write((short) 28);
        this.out.write((byte) (str.length() + 1));
        this.out.write((short) 3);
        this.out.write((short) ((str.length() + 28) + 1));
        this.out.write((byte) 4);
        this.out.write((byte) TDS_DONEINPROC_TOKEN);
        this.out.write(new byte[]{(byte) 8, (byte) 0, (byte) 1, (byte) 85, (byte) 0, (byte) 0});
        this.out.write((byte) z);
        this.out.writeAscii(str);
        this.out.write((byte) 0);
        this.out.write(new byte[]{(byte) 1, (byte) 2, (byte) 0, (byte) 0});
        this.out.flush();
    }

    private int readPreLoginPacket() throws IOException {
        byte[][] bArr = new byte[8][];
        byte[][] bArr2 = new byte[8][];
        byte[] bArr3 = new byte[5];
        bArr3[0] = (byte) this.in.read();
        int i = 0;
        while ((bArr3[0] & 255) != 255) {
            if (i == bArr.length) {
                throw new IOException("Pre Login packet has more than 8 entries");
            }
            this.in.read(bArr3, 1, 4);
            int i2 = i + 1;
            bArr[i] = bArr3;
            bArr3 = new byte[5];
            bArr3[0] = (byte) this.in.read();
            i = i2;
        }
        for (int i3 = 0; i3 < i; i3++) {
            bArr3 = new byte[bArr[i3][4]];
            this.in.read(bArr3);
            bArr2[i3] = bArr3;
        }
        if (Logger.isActive()) {
            Logger.println("PreLogin server response");
            for (int i4 = 0; i4 < i; i4++) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Record ");
                stringBuffer.append(i4);
                stringBuffer.append(" = ");
                stringBuffer.append(Support.toHex(bArr2[i4]));
                Logger.println(stringBuffer.toString());
            }
        }
        return i > 1 ? bArr2[1][0] : 2;
    }

    private void send42LoginPkt(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i) throws IOException {
        String str9 = str3;
        byte[] bArr = new byte[0];
        this.out.setPacketType((byte) 2);
        putLoginString(str7, 30);
        putLoginString(str2, 30);
        putLoginString(str9, 30);
        putLoginString(String.valueOf(this.connection.getProcessId()), 30);
        this.out.write((byte) 3);
        this.out.write((byte) 1);
        this.out.write((byte) 6);
        this.out.write((byte) 10);
        this.out.write((byte) 9);
        this.out.write((byte) 1);
        this.out.write((byte) 1);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        this.out.write(bArr, 0, 7);
        putLoginString(str5, 30);
        putLoginString(str, 30);
        this.out.write((byte) 0);
        this.out.write((byte) str9.length());
        byte[] encodeString = Support.encodeString(this.connection.getCharset(), str9);
        this.out.write(encodeString, 0, 253);
        this.out.write((byte) (encodeString.length + 2));
        this.out.write((byte) 4);
        this.out.write((byte) 2);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        putLoginString(str6, 10);
        this.out.write((byte) 6);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        this.out.write((byte) 13);
        this.out.write((byte) NTLMAUTH_PKT);
        putLoginString(str8, 30);
        this.out.write((byte) 1);
        this.out.write((short) 0);
        this.out.write((byte) 0);
        this.out.write(bArr, 0, 8);
        this.out.write((short) 0);
        putLoginString(str4, 30);
        this.out.write((byte) 1);
        putLoginString(String.valueOf(i), 6);
        this.out.write(bArr, 0, 8);
        this.out.flush();
        this.endOfResponse = false;
    }

    private void send50LoginPkt(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i) throws IOException {
        String str9 = str3;
        byte[] bArr = new byte[0];
        this.out.setPacketType((byte) 2);
        putLoginString(str7, 30);
        putLoginString(str2, 30);
        putLoginString(str9, 30);
        putLoginString(String.valueOf(this.connection.getProcessId()), 30);
        this.out.write((byte) 3);
        this.out.write((byte) 1);
        this.out.write((byte) 6);
        this.out.write((byte) 10);
        this.out.write((byte) 9);
        this.out.write((byte) 1);
        this.out.write((byte) 1);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        this.out.write(bArr, 0, 7);
        putLoginString(str5, 30);
        putLoginString(str, 30);
        this.out.write((byte) 0);
        this.out.write((byte) str9.length());
        byte[] encodeString = Support.encodeString(this.connection.getCharset(), str9);
        this.out.write(encodeString, 0, 253);
        this.out.write((byte) (encodeString.length + 2));
        this.out.write((byte) TDS_ENV_LCID);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        putLoginString(str6, 10);
        this.out.write((byte) TDS_ENV_LCID);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        this.out.write((byte) 0);
        this.out.write((byte) 13);
        this.out.write((byte) NTLMAUTH_PKT);
        putLoginString(str8, 30);
        this.out.write((byte) 1);
        this.out.write((short) 0);
        this.out.write((byte) 0);
        this.out.write(bArr, 0, 8);
        this.out.write((short) 0);
        putLoginString(str4, 30);
        this.out.write((byte) 1);
        if (i > 0) {
            putLoginString(String.valueOf(i), 6);
        } else {
            putLoginString(String.valueOf(512), 6);
        }
        r0.out.write(bArr, 0, 4);
        encodeString = new byte[]{(byte) 1, (byte) 11, (byte) 79, TDS_DONEINPROC_TOKEN, (byte) -123, TDS_RESULT_TOKEN, (byte) -17, (byte) 101, Byte.MAX_VALUE, TDS_DONEINPROC_TOKEN, TDS_DONEINPROC_TOKEN, TDS_DONEINPROC_TOKEN, (byte) -42, (byte) 2, (byte) 10, (byte) 0, (byte) 2, (byte) 4, (byte) 6, DONE_END_OF_RESPONSE, (byte) 6, (byte) 72, (byte) 0, (byte) 0, (byte) 12};
        if (i == 0) {
            encodeString[17] = (byte) 0;
        }
        r0.out.write((byte) TDS_CAP_TOKEN);
        r0.out.write((short) encodeString.length);
        r0.out.write(encodeString);
        r0.out.flush();
        r0.endOfResponse = false;
    }

    private void sendMSLoginPkt(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, int i) throws IOException, SQLException {
        Object obj;
        byte[] invokePrepareSSORequest;
        short length;
        short length2;
        int i2;
        byte b;
        short length3;
        String tds7CryptPass;
        byte[] bytes;
        TdsCore tdsCore = this;
        String str11 = str3;
        String str12 = str5;
        byte[] bArr = new byte[0];
        if (str11 != null) {
            if (str3.length() != 0) {
                if (str12 == null || str5.length() <= 0) {
                    obj = null;
                    if (tdsCore.ntlmAuthSSO) {
                        try {
                            sspiJNIClient = SSPIJNIClient.getInstance();
                            invokePrepareSSORequest = sspiJNIClient.invokePrepareSSORequest();
                        } catch (Exception e) {
                            Exception exception = e;
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("SSO Failed: ");
                            stringBuffer.append(exception.getMessage());
                            throw new IOException(stringBuffer.toString());
                        }
                    }
                    invokePrepareSSORequest = null;
                    length = (short) (((((((str8.length() + str6.length()) + str.length()) + str7.length()) + str2.length()) + str9.length()) * 2) + 86);
                    if (obj != null) {
                        if (tdsCore.ntlmAuthSSO || invokePrepareSSORequest == null) {
                            length2 = (short) (str5.length() + 32);
                        } else {
                            length2 = (short) invokePrepareSSORequest.length;
                        }
                        i2 = (short) (length + length2);
                    } else {
                        i2 = (short) (length + ((str3.length() + str4.length()) * 2));
                        length2 = (short) 0;
                    }
                    tdsCore.out.setPacketType((byte) 16);
                    tdsCore.out.write(i2);
                    b = (byte) 3;
                    if (tdsCore.tdsVersion == 3) {
                        tdsCore.out.write(1879048192);
                    } else {
                        tdsCore.out.write(1895825409);
                    }
                    tdsCore.out.write(i);
                    tdsCore.out.write(7);
                    tdsCore.out.write(tdsCore.connection.getProcessId());
                    tdsCore.out.write(0);
                    tdsCore.out.write((byte) -32);
                    if (obj != null) {
                        b = (byte) 131;
                    }
                    tdsCore.out.write(b);
                    tdsCore.out.write((byte) 0);
                    tdsCore.out.write((byte) 0);
                    tdsCore.out.write(bArr, 0, 4);
                    tdsCore.out.write(bArr, 0, 4);
                    tdsCore.out.write((short) 86);
                    tdsCore.out.write((short) str8.length());
                    length3 = (short) ((str8.length() * 2) + 86);
                    if (obj == null) {
                        tdsCore.out.write(length3);
                        tdsCore.out.write((short) str3.length());
                        length3 = (short) (length3 + (str3.length() * 2));
                        tdsCore.out.write(length3);
                        tdsCore.out.write((short) str4.length());
                        length3 = (short) (length3 + (str4.length() * 2));
                    } else {
                        tdsCore.out.write(length3);
                        tdsCore.out.write((short) 0);
                        tdsCore.out.write(length3);
                        tdsCore.out.write((short) 0);
                    }
                    tdsCore.out.write(length3);
                    tdsCore.out.write((short) str6.length());
                    length3 = (short) (length3 + (str6.length() * 2));
                    tdsCore.out.write(length3);
                    tdsCore.out.write((short) str.length());
                    length3 = (short) (length3 + (str.length() * 2));
                    tdsCore.out.write((short) 0);
                    tdsCore.out.write((short) 0);
                    tdsCore.out.write(length3);
                    tdsCore.out.write((short) str7.length());
                    length3 = (short) (length3 + (str7.length() * 2));
                    tdsCore.out.write(length3);
                    tdsCore.out.write((short) str9.length());
                    length3 = (short) (length3 + (str9.length() * 2));
                    tdsCore.out.write(length3);
                    tdsCore.out.write((short) str2.length());
                    length3 = (short) (length3 + (str2.length() * 2));
                    tdsCore.out.write(getMACAddress(str10));
                    tdsCore.out.write(length3);
                    tdsCore.out.write(length2);
                    tdsCore.out.write(i2);
                    tdsCore.out.write(str8);
                    if (obj == null) {
                        tds7CryptPass = tds7CryptPass(str4);
                        tdsCore.out.write(str11);
                        tdsCore.out.write(tds7CryptPass);
                    }
                    tdsCore.out.write(str6);
                    tdsCore.out.write(str);
                    tdsCore.out.write(str7);
                    tdsCore.out.write(str9);
                    tdsCore.out.write(str2);
                    if (obj != null) {
                        if (tdsCore.ntlmAuthSSO) {
                            tdsCore.out.write(invokePrepareSSORequest);
                        } else {
                            bytes = str12.getBytes("UTF8");
                            tdsCore.out.write(new byte[]{(byte) 78, (byte) 84, (byte) 76, (byte) 77, (byte) 83, (byte) 83, (byte) 80, (byte) 0});
                            tdsCore.out.write(1);
                            if (tdsCore.connection.getUseNTLMv2()) {
                                tdsCore.out.write(569861);
                            } else {
                                tdsCore.out.write(45569);
                            }
                            tdsCore.out.write((short) bytes.length);
                            tdsCore.out.write((short) bytes.length);
                            tdsCore.out.write(32);
                            tdsCore.out.write((short) 0);
                            tdsCore.out.write((short) 0);
                            tdsCore.out.write(32);
                            tdsCore.out.write(bytes);
                        }
                    }
                    tdsCore.out.flush();
                    tdsCore.endOfResponse = false;
                    return;
                }
                obj = 1;
                if (tdsCore.ntlmAuthSSO) {
                    invokePrepareSSORequest = null;
                } else {
                    sspiJNIClient = SSPIJNIClient.getInstance();
                    invokePrepareSSORequest = sspiJNIClient.invokePrepareSSORequest();
                }
                length = (short) (((((((str8.length() + str6.length()) + str.length()) + str7.length()) + str2.length()) + str9.length()) * 2) + 86);
                if (obj != null) {
                    i2 = (short) (length + ((str3.length() + str4.length()) * 2));
                    length2 = (short) 0;
                } else {
                    if (tdsCore.ntlmAuthSSO) {
                    }
                    length2 = (short) (str5.length() + 32);
                    i2 = (short) (length + length2);
                }
                tdsCore.out.setPacketType((byte) 16);
                tdsCore.out.write(i2);
                b = (byte) 3;
                if (tdsCore.tdsVersion == 3) {
                    tdsCore.out.write(1895825409);
                } else {
                    tdsCore.out.write(1879048192);
                }
                tdsCore.out.write(i);
                tdsCore.out.write(7);
                tdsCore.out.write(tdsCore.connection.getProcessId());
                tdsCore.out.write(0);
                tdsCore.out.write((byte) -32);
                if (obj != null) {
                    b = (byte) 131;
                }
                tdsCore.out.write(b);
                tdsCore.out.write((byte) 0);
                tdsCore.out.write((byte) 0);
                tdsCore.out.write(bArr, 0, 4);
                tdsCore.out.write(bArr, 0, 4);
                tdsCore.out.write((short) 86);
                tdsCore.out.write((short) str8.length());
                length3 = (short) ((str8.length() * 2) + 86);
                if (obj == null) {
                    tdsCore.out.write(length3);
                    tdsCore.out.write((short) 0);
                    tdsCore.out.write(length3);
                    tdsCore.out.write((short) 0);
                } else {
                    tdsCore.out.write(length3);
                    tdsCore.out.write((short) str3.length());
                    length3 = (short) (length3 + (str3.length() * 2));
                    tdsCore.out.write(length3);
                    tdsCore.out.write((short) str4.length());
                    length3 = (short) (length3 + (str4.length() * 2));
                }
                tdsCore.out.write(length3);
                tdsCore.out.write((short) str6.length());
                length3 = (short) (length3 + (str6.length() * 2));
                tdsCore.out.write(length3);
                tdsCore.out.write((short) str.length());
                length3 = (short) (length3 + (str.length() * 2));
                tdsCore.out.write((short) 0);
                tdsCore.out.write((short) 0);
                tdsCore.out.write(length3);
                tdsCore.out.write((short) str7.length());
                length3 = (short) (length3 + (str7.length() * 2));
                tdsCore.out.write(length3);
                tdsCore.out.write((short) str9.length());
                length3 = (short) (length3 + (str9.length() * 2));
                tdsCore.out.write(length3);
                tdsCore.out.write((short) str2.length());
                length3 = (short) (length3 + (str2.length() * 2));
                tdsCore.out.write(getMACAddress(str10));
                tdsCore.out.write(length3);
                tdsCore.out.write(length2);
                tdsCore.out.write(i2);
                tdsCore.out.write(str8);
                if (obj == null) {
                    tds7CryptPass = tds7CryptPass(str4);
                    tdsCore.out.write(str11);
                    tdsCore.out.write(tds7CryptPass);
                }
                tdsCore.out.write(str6);
                tdsCore.out.write(str);
                tdsCore.out.write(str7);
                tdsCore.out.write(str9);
                tdsCore.out.write(str2);
                if (obj != null) {
                    if (tdsCore.ntlmAuthSSO) {
                        bytes = str12.getBytes("UTF8");
                        tdsCore.out.write(new byte[]{(byte) 78, (byte) 84, (byte) 76, (byte) 77, (byte) 83, (byte) 83, (byte) 80, (byte) 0});
                        tdsCore.out.write(1);
                        if (tdsCore.connection.getUseNTLMv2()) {
                            tdsCore.out.write(45569);
                        } else {
                            tdsCore.out.write(569861);
                        }
                        tdsCore.out.write((short) bytes.length);
                        tdsCore.out.write((short) bytes.length);
                        tdsCore.out.write(32);
                        tdsCore.out.write((short) 0);
                        tdsCore.out.write((short) 0);
                        tdsCore.out.write(32);
                        tdsCore.out.write(bytes);
                    } else {
                        tdsCore.out.write(invokePrepareSSORequest);
                    }
                }
                tdsCore.out.flush();
                tdsCore.endOfResponse = false;
                return;
            }
        }
        if (Support.isWindowsOS()) {
            tdsCore.ntlmAuthSSO = true;
            obj = 1;
            if (tdsCore.ntlmAuthSSO) {
                sspiJNIClient = SSPIJNIClient.getInstance();
                invokePrepareSSORequest = sspiJNIClient.invokePrepareSSORequest();
            } else {
                invokePrepareSSORequest = null;
            }
            length = (short) (((((((str8.length() + str6.length()) + str.length()) + str7.length()) + str2.length()) + str9.length()) * 2) + 86);
            if (obj != null) {
                if (tdsCore.ntlmAuthSSO) {
                }
                length2 = (short) (str5.length() + 32);
                i2 = (short) (length + length2);
            } else {
                i2 = (short) (length + ((str3.length() + str4.length()) * 2));
                length2 = (short) 0;
            }
            tdsCore.out.setPacketType((byte) 16);
            tdsCore.out.write(i2);
            b = (byte) 3;
            if (tdsCore.tdsVersion == 3) {
                tdsCore.out.write(1879048192);
            } else {
                tdsCore.out.write(1895825409);
            }
            tdsCore.out.write(i);
            tdsCore.out.write(7);
            tdsCore.out.write(tdsCore.connection.getProcessId());
            tdsCore.out.write(0);
            tdsCore.out.write((byte) -32);
            if (obj != null) {
                b = (byte) 131;
            }
            tdsCore.out.write(b);
            tdsCore.out.write((byte) 0);
            tdsCore.out.write((byte) 0);
            tdsCore.out.write(bArr, 0, 4);
            tdsCore.out.write(bArr, 0, 4);
            tdsCore.out.write((short) 86);
            tdsCore.out.write((short) str8.length());
            length3 = (short) ((str8.length() * 2) + 86);
            if (obj == null) {
                tdsCore.out.write(length3);
                tdsCore.out.write((short) str3.length());
                length3 = (short) (length3 + (str3.length() * 2));
                tdsCore.out.write(length3);
                tdsCore.out.write((short) str4.length());
                length3 = (short) (length3 + (str4.length() * 2));
            } else {
                tdsCore.out.write(length3);
                tdsCore.out.write((short) 0);
                tdsCore.out.write(length3);
                tdsCore.out.write((short) 0);
            }
            tdsCore.out.write(length3);
            tdsCore.out.write((short) str6.length());
            length3 = (short) (length3 + (str6.length() * 2));
            tdsCore.out.write(length3);
            tdsCore.out.write((short) str.length());
            length3 = (short) (length3 + (str.length() * 2));
            tdsCore.out.write((short) 0);
            tdsCore.out.write((short) 0);
            tdsCore.out.write(length3);
            tdsCore.out.write((short) str7.length());
            length3 = (short) (length3 + (str7.length() * 2));
            tdsCore.out.write(length3);
            tdsCore.out.write((short) str9.length());
            length3 = (short) (length3 + (str9.length() * 2));
            tdsCore.out.write(length3);
            tdsCore.out.write((short) str2.length());
            length3 = (short) (length3 + (str2.length() * 2));
            tdsCore.out.write(getMACAddress(str10));
            tdsCore.out.write(length3);
            tdsCore.out.write(length2);
            tdsCore.out.write(i2);
            tdsCore.out.write(str8);
            if (obj == null) {
                tds7CryptPass = tds7CryptPass(str4);
                tdsCore.out.write(str11);
                tdsCore.out.write(tds7CryptPass);
            }
            tdsCore.out.write(str6);
            tdsCore.out.write(str);
            tdsCore.out.write(str7);
            tdsCore.out.write(str9);
            tdsCore.out.write(str2);
            if (obj != null) {
                if (tdsCore.ntlmAuthSSO) {
                    tdsCore.out.write(invokePrepareSSORequest);
                } else {
                    bytes = str12.getBytes("UTF8");
                    tdsCore.out.write(new byte[]{(byte) 78, (byte) 84, (byte) 76, (byte) 77, (byte) 83, (byte) 83, (byte) 80, (byte) 0});
                    tdsCore.out.write(1);
                    if (tdsCore.connection.getUseNTLMv2()) {
                        tdsCore.out.write(569861);
                    } else {
                        tdsCore.out.write(45569);
                    }
                    tdsCore.out.write((short) bytes.length);
                    tdsCore.out.write((short) bytes.length);
                    tdsCore.out.write(32);
                    tdsCore.out.write((short) 0);
                    tdsCore.out.write((short) 0);
                    tdsCore.out.write(32);
                    tdsCore.out.write(bytes);
                }
            }
            tdsCore.out.flush();
            tdsCore.endOfResponse = false;
            return;
        }
        throw new SQLException(Messages.get("error.connection.sso"), "08001");
    }

    private void sendNtlmChallengeResponse(byte[] bArr, String str, String str2, String str3) throws IOException {
        this.out.setPacketType(NTLMAUTH_PKT);
        if (this.ntlmAuthSSO) {
            try {
                this.out.write(sspiJNIClient.invokePrepareSSOSubmit(this.currentToken.ntlmMessage));
            } catch (byte[] bArr2) {
                str2 = new StringBuffer();
                str2.append("SSO Failed: ");
                str2.append(bArr2.getMessage());
                throw new IOException(str2.toString());
            }
        }
        byte[] answerLmv2Challenge;
        if (this.connection.getUseNTLMv2()) {
            byte[] bArr3 = new byte[8];
            new Random().nextBytes(bArr3);
            answerLmv2Challenge = NtlmAuth.answerLmv2Challenge(str3, str, str2, bArr2, bArr3);
            bArr2 = NtlmAuth.answerNtlmv2Challenge(str3, str, str2, bArr2, this.currentToken.ntlmTarget, bArr3);
        } else {
            answerLmv2Challenge = NtlmAuth.answerLmChallenge(str2, bArr2);
            bArr2 = NtlmAuth.answerNtChallenge(str2, bArr2);
        }
        this.out.write(new byte[]{(byte) 78, (byte) 84, (byte) 76, (byte) 77, (byte) 83, (byte) 83, (byte) 80, (byte) 0});
        this.out.write(3);
        str2 = str3.length() * 2;
        int length = str.length() * 2;
        int i = str2 + 64;
        int i2 = i + length;
        int i3 = i2 + 0;
        this.out.write((short) answerLmv2Challenge.length);
        this.out.write((short) answerLmv2Challenge.length);
        this.out.write(i3);
        int length2 = answerLmv2Challenge.length + i3;
        this.out.write((short) bArr2.length);
        this.out.write((short) bArr2.length);
        this.out.write(length2);
        short s = (short) str2;
        this.out.write(s);
        this.out.write(s);
        this.out.write(64);
        short s2 = (short) length;
        this.out.write(s2);
        this.out.write(s2);
        this.out.write(i);
        this.out.write((short) 0);
        this.out.write((short) 0);
        this.out.write(i2);
        this.out.write((short) 0);
        this.out.write((short) 0);
        this.out.write(i3);
        if (this.connection.getUseNTLMv2() != null) {
            this.out.write(557569);
        } else {
            this.out.write(33281);
        }
        this.out.write(str3);
        this.out.write(str);
        this.out.write(answerLmv2Challenge);
        this.out.write(bArr2);
        this.out.flush();
    }

    private void nextToken() throws SQLException {
        checkOpen();
        if (this.endOfResponse) {
            this.currentToken.token = TDS_DONE_TOKEN;
            this.currentToken.status = (byte) 0;
            return;
        }
        try {
            if (this.computedColumns != null) {
                byte peek = (byte) this.in.peek();
                if (peek != TDS_ROW_TOKEN) {
                    if (peek == TDS_ALTROW) {
                        if (!this.endOfResults) {
                            this.endOfResults = true;
                            return;
                        }
                    }
                } else if (this.endOfResults) {
                    this.endOfResults = false;
                    return;
                }
            }
            this.currentToken.token = (byte) this.in.read();
            switch (this.currentToken.token) {
                case (byte) -127:
                    tds7ResultToken();
                    break;
                case (byte) -120:
                    tdsComputedResultToken();
                    break;
                case (byte) -96:
                    tds4ColNamesToken();
                    break;
                case (byte) -95:
                    tds4ColFormatToken();
                    break;
                case (byte) -92:
                    tdsTableNameToken();
                    break;
                case (byte) -91:
                    tdsColumnInfoToken();
                    break;
                case (byte) -89:
                    tdsInvalidToken();
                    break;
                case (byte) -88:
                    tdsInvalidToken();
                    break;
                case (byte) -87:
                    tdsOrderByToken();
                    break;
                case (byte) -86:
                case (byte) -85:
                    tdsErrorToken();
                    break;
                case (byte) -84:
                    tdsOutputParamToken();
                    break;
                case (byte) -83:
                    tdsLoginAckToken();
                    break;
                case (byte) -82:
                    tdsControlToken();
                    break;
                case (byte) -47:
                    tdsRowToken();
                    break;
                case (byte) -45:
                    tdsComputedRowToken();
                    break;
                case (byte) -41:
                    tds5ParamsToken();
                    break;
                case (byte) -30:
                    tdsCapabilityToken();
                    break;
                case (byte) -29:
                    tdsEnvChangeToken();
                    break;
                case (byte) -27:
                    tds5ErrorToken();
                    break;
                case (byte) -25:
                    tds5DynamicToken();
                    break;
                case (byte) -20:
                    tds5ParamFmtToken();
                    break;
                case (byte) -19:
                    tdsNtlmAuthToken();
                    break;
                case (byte) -18:
                    tds5ResultToken();
                    break;
                case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                case (byte) -2:
                case (byte) -1:
                    tdsDoneToken();
                    break;
                case (byte) 32:
                    tds5ParamFmt2Token();
                    break;
                case (byte) 33:
                    tdsInvalidToken();
                    break;
                case (byte) 97:
                    tds5WideResultToken();
                    break;
                case (byte) 113:
                    tdsInvalidToken();
                    break;
                case (byte) 120:
                    tdsOffsetsToken();
                    break;
                case (byte) 121:
                    tdsReturnStatusToken();
                    break;
                case (byte) 124:
                    tdsProcIdToken();
                    break;
                default:
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Invalid packet type 0x");
                    stringBuffer.append(Integer.toHexString(this.currentToken.token & 255));
                    throw new ProtocolException(stringBuffer.toString());
            }
        } catch (Throwable e) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "08S01"), e);
        } catch (Throwable e2) {
            this.connection.setClosed();
            throw Support.linkException(new SQLException(Messages.get("error.generic.tdserror", e2.getMessage()), "08S01"), e2);
        } catch (OutOfMemoryError e3) {
            this.in.skipToEnd();
            this.endOfResponse = true;
            this.endOfResults = true;
            this.cancelPending = false;
            throw e3;
        }
    }

    private void tdsInvalidToken() throws IOException, ProtocolException {
        this.in.skip(this.in.readShort());
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Unsupported TDS token: 0x");
        stringBuffer.append(Integer.toHexString(this.currentToken.token & 255));
        throw new ProtocolException(stringBuffer.toString());
    }

    private void tds5ParamFmt2Token() throws IOException, ProtocolException {
        this.in.readInt();
        short readShort = this.in.readShort();
        ColInfo[] colInfoArr = new ColInfo[readShort];
        for (short s = (short) 0; s < readShort; s++) {
            ColInfo colInfo = new ColInfo();
            colInfo.realName = this.in.readNonUnicodeString(this.in.read());
            int readInt = this.in.readInt();
            colInfo.isCaseSensitive = false;
            colInfo.nullable = (readInt & 32) != 0 ? 1 : 0;
            colInfo.isWriteable = (readInt & 16) != 0;
            colInfo.isIdentity = (readInt & 64) != 0;
            colInfo.isKey = (readInt & 2) != 0;
            colInfo.isHidden = (readInt & 1) != 0;
            colInfo.userType = this.in.readInt();
            TdsData.readType(this.in, colInfo);
            this.in.skip(1);
            colInfoArr[s] = colInfo;
        }
        this.currentToken.dynamParamInfo = colInfoArr;
        this.currentToken.dynamParamData = new Object[readShort];
    }

    private void tds5WideResultToken() throws IOException, ProtocolException {
        this.in.readInt();
        short readShort = this.in.readShort();
        this.columns = new ColInfo[readShort];
        this.rowData = new Object[readShort];
        this.tables = null;
        for (short s = (short) 0; s < readShort; s++) {
            ColInfo colInfo = new ColInfo();
            colInfo.name = this.in.readNonUnicodeString(this.in.read());
            colInfo.catalog = this.in.readNonUnicodeString(this.in.read());
            colInfo.schema = this.in.readNonUnicodeString(this.in.read());
            colInfo.tableName = this.in.readNonUnicodeString(this.in.read());
            colInfo.realName = this.in.readNonUnicodeString(this.in.read());
            if (colInfo.name == null || colInfo.name.length() == 0) {
                colInfo.name = colInfo.realName;
            }
            int readInt = this.in.readInt();
            colInfo.isCaseSensitive = false;
            colInfo.nullable = (readInt & 32) != 0 ? 1 : 0;
            colInfo.isWriteable = (readInt & 16) != 0;
            colInfo.isIdentity = (readInt & 64) != 0;
            colInfo.isKey = (readInt & 2) != 0;
            colInfo.isHidden = (readInt & 1) != 0;
            colInfo.userType = this.in.readInt();
            TdsData.readType(this.in, colInfo);
            this.in.skip(1);
            this.columns[s] = colInfo;
        }
        this.endOfResults = false;
    }

    private void tdsReturnStatusToken() throws IOException, SQLException {
        this.returnStatus = new Integer(this.in.readInt());
        if (this.returnParam != null) {
            this.returnParam.setOutValue(Support.convert(this.connection, this.returnStatus, this.returnParam.jdbcType, this.connection.getCharset()));
        }
    }

    private void tdsProcIdToken() throws IOException {
        this.in.skip(8);
    }

    private void tdsOffsetsToken() throws IOException {
        this.in.read();
        this.in.read();
        this.in.readShort();
    }

    private void tds7ResultToken() throws IOException, ProtocolException, SQLException {
        this.endOfResults = false;
        short readShort = this.in.readShort();
        if (readShort >= (short) 0) {
            this.columns = new ColInfo[readShort];
            this.rowData = new Object[readShort];
            this.tables = null;
            for (short s = (short) 0; s < readShort; s++) {
                ColInfo colInfo = new ColInfo();
                colInfo.userType = this.in.readShort();
                short readShort2 = this.in.readShort();
                boolean z = true;
                colInfo.nullable = (readShort2 & 1) != 0 ? 1 : 0;
                colInfo.isCaseSensitive = (readShort2 & 2) != 0;
                colInfo.isIdentity = (readShort2 & 16) != 0;
                if ((readShort2 & 12) == 0) {
                    z = false;
                }
                colInfo.isWriteable = z;
                TdsData.readType(this.in, colInfo);
                if (this.tdsVersion >= 4 && colInfo.collation != null) {
                    TdsData.setColumnCharset(colInfo, this.connection);
                }
                colInfo.realName = this.in.readUnicodeString(this.in.read());
                colInfo.name = colInfo.realName;
                this.columns[s] = colInfo;
            }
        }
    }

    private void tds4ColNamesToken() throws IOException {
        ArrayList arrayList = new ArrayList();
        short readShort = this.in.readShort();
        this.tables = null;
        short s = (short) 0;
        while (s < readShort) {
            ColInfo colInfo = new ColInfo();
            int read = this.in.read();
            String readNonUnicodeString = this.in.readNonUnicodeString(read);
            s = (s + 1) + read;
            colInfo.realName = readNonUnicodeString;
            colInfo.name = readNonUnicodeString;
            arrayList.add(colInfo);
        }
        int size = arrayList.size();
        this.columns = (ColInfo[]) arrayList.toArray(new ColInfo[size]);
        this.rowData = new Object[size];
    }

    private void tds4ColFormatToken() throws IOException, ProtocolException {
        short readShort = this.in.readShort();
        short s = (short) 0;
        int i = 0;
        while (s < readShort) {
            if (i > this.columns.length) {
                throw new ProtocolException("Too many columns in TDS_COL_FMT packet");
            }
            ColInfo colInfo = this.columns[i];
            boolean z = true;
            if (this.serverType == 1) {
                colInfo.userType = this.in.readShort();
                short readShort2 = this.in.readShort();
                colInfo.nullable = (readShort2 & 1) != 0 ? 1 : 0;
                colInfo.isCaseSensitive = (readShort2 & 2) != 0;
                colInfo.isWriteable = (readShort2 & 12) != 0;
                if ((readShort2 & 16) == 0) {
                    z = false;
                }
                colInfo.isIdentity = z;
            } else {
                colInfo.isCaseSensitive = false;
                colInfo.isWriteable = true;
                if (colInfo.nullable == 0) {
                    colInfo.nullable = 2;
                }
                colInfo.userType = this.in.readInt();
            }
            s = (s + 4) + TdsData.readType(this.in, colInfo);
            i++;
        }
        if (i != this.columns.length) {
            throw new ProtocolException("Too few columns in TDS_COL_FMT packet");
        }
        this.endOfResults = false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void tdsTableNameToken() throws IOException, ProtocolException {
        short readShort = this.in.readShort();
        ArrayList arrayList = new ArrayList();
        short s = (short) 0;
        while (s < readShort) {
            Object tableMetaData;
            if (this.tdsVersion >= 5) {
                tableMetaData = new TableMetaData();
                s++;
                int read = this.in.read();
                switch (read) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        short readShort2 = this.in.readShort();
                        int i = s + ((readShort2 * 2) + 2);
                        this.in.readUnicodeString(readShort2);
                        break;
                    default:
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("Invalid table TAB_NAME_TOKEN: ");
                        stringBuffer.append(read);
                        throw new ProtocolException(stringBuffer.toString());
                }
            }
            String readUnicodeString;
            if (this.tdsVersion >= 3) {
                short readShort3 = this.in.readShort();
                s += (readShort3 * 2) + 2;
                readUnicodeString = this.in.readUnicodeString(readShort3);
            } else {
                int read2 = this.in.read();
                s++;
                if (read2 != 0) {
                    s += read2;
                    readUnicodeString = this.in.readNonUnicodeString(read2);
                }
            }
            TableMetaData tableMetaData2 = new TableMetaData();
            int lastIndexOf = readUnicodeString.lastIndexOf(46);
            if (lastIndexOf > 0) {
                tableMetaData2.name = readUnicodeString.substring(lastIndexOf + 1);
                int lastIndexOf2 = readUnicodeString.lastIndexOf(46, lastIndexOf - 1);
                int i2 = lastIndexOf2 + 1;
                if (i2 < lastIndexOf) {
                    tableMetaData2.schema = readUnicodeString.substring(i2, lastIndexOf);
                }
                int lastIndexOf3 = readUnicodeString.lastIndexOf(46, lastIndexOf2 - 1) + 1;
                if (lastIndexOf3 < lastIndexOf2) {
                    tableMetaData2.catalog = readUnicodeString.substring(lastIndexOf3, lastIndexOf2);
                }
            } else {
                tableMetaData2.name = readUnicodeString;
            }
            tableMetaData = tableMetaData2;
            arrayList.add(tableMetaData);
        }
        if (arrayList.size() > 0) {
            this.tables = (TableMetaData[]) arrayList.toArray(new TableMetaData[arrayList.size()]);
        }
    }

    private void tdsColumnInfoToken() throws IOException, ProtocolException {
        short readShort = this.in.readShort();
        short s = (short) 0;
        int i = 0;
        while (s < readShort) {
            this.in.read();
            boolean z = true;
            if (i >= this.columns.length) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Column index ");
                stringBuffer.append(i + 1);
                stringBuffer.append(" invalid in TDS_COLINFO packet");
                throw new ProtocolException(stringBuffer.toString());
            }
            int i2 = i + 1;
            ColInfo colInfo = this.columns[i];
            int read = this.in.read();
            if (this.tables == null || read <= this.tables.length) {
                byte read2 = (byte) this.in.read();
                s += 3;
                if (!(read == 0 || this.tables == null)) {
                    TableMetaData tableMetaData = this.tables[read - 1];
                    colInfo.catalog = tableMetaData.catalog;
                    colInfo.schema = tableMetaData.schema;
                    colInfo.tableName = tableMetaData.name;
                }
                colInfo.isKey = (read2 & 8) != 0;
                if ((read2 & 16) == 0) {
                    z = false;
                }
                colInfo.isHidden = z;
                if ((read2 & 32) != 0) {
                    read = this.in.read();
                    int i3 = s + 1;
                    String readString = this.in.readString(read);
                    if (this.tdsVersion >= 3) {
                        read *= 2;
                    }
                    s = i3 + read;
                    colInfo.realName = readString;
                }
                i = i2;
            } else {
                stringBuffer = new StringBuffer();
                stringBuffer.append("Table index ");
                stringBuffer.append(read);
                stringBuffer.append(" invalid in TDS_COLINFO packet");
                throw new ProtocolException(stringBuffer.toString());
            }
        }
    }

    private void tdsOrderByToken() throws IOException {
        this.in.skip(this.in.readShort());
    }

    private void tdsErrorToken() throws IOException {
        short readShort = this.in.readShort();
        int readInt = this.in.readInt();
        int read = this.in.read();
        int read2 = this.in.read();
        int readShort2 = this.in.readShort();
        String readString = this.in.readString(readShort2);
        if (this.tdsVersion >= 3) {
            readShort2 *= 2;
        }
        readShort2 = (readShort2 + 2) + 6;
        int read3 = this.in.read();
        String readString2 = this.in.readString(read3);
        if (this.tdsVersion >= 3) {
            read3 *= 2;
        }
        readShort2 += read3 + 1;
        read3 = this.in.read();
        String readString3 = this.in.readString(read3);
        if (this.tdsVersion >= 3) {
            read3 *= 2;
        }
        readShort2 += read3 + 1;
        short readShort3 = this.in.readShort();
        int i = readShort - (readShort2 + 2);
        if (i > 0) {
            this.in.skip(i);
        }
        read3 = 9;
        if (this.currentToken.token == TDS_ERROR_TOKEN) {
            if (read2 < 10) {
                read2 = 11;
            }
            if (read2 >= 20) {
                this.fatalError = true;
            }
        } else if (read2 > 9) {
            this.messages.addDiagnostic(readInt, read, read3, readString, readString2, readString3, readShort3);
        }
        read3 = read2;
        this.messages.addDiagnostic(readInt, read, read3, readString, readString2, readString3, readShort3);
    }

    private void tdsOutputParamToken() throws IOException, ProtocolException, SQLException {
        this.in.readShort();
        String readString = this.in.readString(this.in.read());
        Object obj = this.in.read() == 2 ? 1 : null;
        this.in.read();
        this.in.skip(3);
        ColInfo colInfo = new ColInfo();
        TdsData.readType(this.in, colInfo);
        if (this.tdsVersion >= 4 && colInfo.collation != null) {
            TdsData.setColumnCharset(colInfo, this.connection);
        }
        Object readData = TdsData.readData(this.connection, this.in, colInfo);
        if (this.parameters == null) {
            return;
        }
        if (readString.length() != 0 && !readString.startsWith("@")) {
            return;
        }
        if (this.tdsVersion < 4 || obj == null) {
            do {
                int i = this.nextParam + 1;
                this.nextParam = i;
                if (i >= this.parameters.length) {
                    return;
                }
            } while (!this.parameters[this.nextParam].isOutput);
            if (readData != null) {
                this.parameters[this.nextParam].setOutValue(Support.convert(this.connection, readData, this.parameters[this.nextParam].jdbcType, this.connection.getCharset()));
                this.parameters[this.nextParam].collation = colInfo.collation;
                this.parameters[this.nextParam].charsetInfo = colInfo.charsetInfo;
                return;
            }
            this.parameters[this.nextParam].setOutValue(null);
        } else if (this.returnParam == null) {
        } else {
            if (readData != null) {
                this.returnParam.setOutValue(Support.convert(this.connection, readData, this.returnParam.jdbcType, this.connection.getCharset()));
                this.returnParam.collation = colInfo.collation;
                this.returnParam.charsetInfo = colInfo.charsetInfo;
                return;
            }
            this.returnParam.setOutValue(null);
        }
    }

    private void tdsLoginAckToken() throws IOException {
        int read;
        int read2;
        int read3;
        this.in.readShort();
        int read4 = this.in.read();
        this.tdsVersion = TdsData.getTdsVersion((((this.in.read() << 24) | (this.in.read() << 16)) | (this.in.read() << 8)) | this.in.read());
        this.socket.setTdsVersion(this.tdsVersion);
        String readString = this.in.readString(this.in.read());
        if (this.tdsVersion >= 3) {
            read = this.in.read();
            read2 = this.in.read();
            read3 = (this.in.read() << 8) + this.in.read();
        } else {
            if (readString.toLowerCase().startsWith("microsoft")) {
                this.in.skip(1);
                read = this.in.read();
                read2 = this.in.read();
            } else {
                read = this.in.read();
                read2 = (this.in.read() * 10) + this.in.read();
            }
            this.in.skip(1);
            read3 = 0;
        }
        if (readString.length() > 1 && -1 != readString.indexOf(0)) {
            readString = readString.substring(0, readString.indexOf(0));
        }
        this.connection.setDBServerInfo(readString, read, read2, read3);
        if (this.tdsVersion != 2 || read4 == 5) {
            this.messages.clearWarnings();
            for (SQLException sQLException = this.messages.exceptions; sQLException != null; sQLException = sQLException.getNextException()) {
                this.messages.addWarning(new SQLWarning(sQLException.getMessage(), sQLException.getSQLState(), sQLException.getErrorCode()));
            }
            this.messages.exceptions = null;
            return;
        }
        this.messages.addDiagnostic(4002, 0, 14, "Login failed", "", "", 0);
        this.currentToken.token = TDS_ERROR_TOKEN;
    }

    private void tdsControlToken() throws IOException {
        this.in.skip(this.in.readShort());
    }

    private void tdsRowToken() throws IOException, ProtocolException {
        for (int i = 0; i < this.columns.length; i++) {
            this.rowData[i] = TdsData.readData(this.connection, this.in, this.columns[i]);
        }
        this.endOfResults = false;
    }

    private void tds5ParamsToken() throws IOException, ProtocolException, SQLException {
        if (this.currentToken.dynamParamInfo == null) {
            throw new ProtocolException("TDS 5 Param results token (0xD7) not preceded by param format (0xEC or 0X20).");
        }
        for (int i = 0; i < this.currentToken.dynamParamData.length; i++) {
            this.currentToken.dynamParamData[i] = TdsData.readData(this.connection, this.in, this.currentToken.dynamParamInfo[i]);
            String str = this.currentToken.dynamParamInfo[i].realName;
            if (this.parameters != null && (str.length() == 0 || str.startsWith("@"))) {
                do {
                    int i2 = this.nextParam + 1;
                    this.nextParam = i2;
                    if (i2 >= this.parameters.length) {
                        break;
                    }
                } while (!this.parameters[this.nextParam].isOutput);
                Object obj = this.currentToken.dynamParamData[i];
                if (obj != null) {
                    this.parameters[this.nextParam].setOutValue(Support.convert(this.connection, obj, this.parameters[this.nextParam].jdbcType, this.connection.getCharset()));
                } else {
                    this.parameters[this.nextParam].setOutValue(null);
                }
            }
        }
    }

    private void tdsCapabilityToken() throws IOException, ProtocolException {
        this.in.readShort();
        if (this.in.read() != 1) {
            throw new ProtocolException("TDS_CAPABILITY: expected request string");
        }
        int read = this.in.read();
        if (read == 11 || read == 0) {
            byte[] bArr = new byte[11];
            if (read == 0) {
                Logger.println("TDS_CAPABILITY: Invalid request length");
            } else {
                this.in.read(bArr);
            }
            if (this.in.read() != 2) {
                throw new ProtocolException("TDS_CAPABILITY: expected response string");
            }
            read = this.in.read();
            if (read == 10 || read == 0) {
                byte[] bArr2 = new byte[10];
                if (read == 0) {
                    Logger.println("TDS_CAPABILITY: Invalid response length");
                } else {
                    this.in.read(bArr2);
                }
                read = 0;
                if ((bArr[0] & 2) == 2) {
                    read = 32;
                }
                if ((bArr[1] & 3) == 3) {
                    read |= 2;
                }
                if ((bArr[2] & 128) == 128) {
                    read |= 16;
                }
                if ((bArr[3] & 2) == 2) {
                    read |= 8;
                }
                if ((bArr[2] & 1) == 1) {
                    read |= 64;
                }
                if ((bArr[4] & 4) == 4) {
                    read |= 4;
                }
                if ((bArr[7] & 48) == 48) {
                    read |= 1;
                }
                this.connection.setSybaseInfo(read);
                return;
            }
            throw new ProtocolException("TDS_CAPABILITY: byte count not 10");
        }
        throw new ProtocolException("TDS_CAPABILITY: byte count not 11");
    }

    private void tdsEnvChangeToken() throws IOException, SQLException {
        short readShort = this.in.readShort();
        int read = this.in.read();
        if (read != 7) {
            StringBuffer stringBuffer;
            switch (read) {
                case 1:
                    this.connection.setDatabase(this.in.readString(this.in.read()), this.in.readString(this.in.read()));
                    return;
                case 2:
                    String readString = this.in.readString(this.in.read());
                    String readString2 = this.in.readString(this.in.read());
                    if (Logger.isActive()) {
                        stringBuffer = new StringBuffer();
                        stringBuffer.append("Language changed from ");
                        stringBuffer.append(readString2);
                        stringBuffer.append(" to ");
                        stringBuffer.append(readString);
                        Logger.println(stringBuffer.toString());
                        return;
                    }
                    return;
                case 3:
                    read = this.in.read();
                    String readString3 = this.in.readString(read);
                    if (this.tdsVersion >= 3) {
                        this.in.skip((readShort - 2) - (read * 2));
                    } else {
                        this.in.skip((readShort - 2) - read);
                    }
                    this.connection.setServerCharset(readString3);
                    return;
                case 4:
                    read = this.in.read();
                    int parseInt = Integer.parseInt(this.in.readString(read));
                    if (this.tdsVersion >= 3) {
                        this.in.skip((readShort - 2) - (read * 2));
                    } else {
                        this.in.skip((readShort - 2) - read);
                    }
                    this.connection.setNetPacketSize(parseInt);
                    this.out.setBufferSize(parseInt);
                    if (Logger.isActive()) {
                        StringBuffer stringBuffer2 = new StringBuffer();
                        stringBuffer2.append("Changed blocksize to ");
                        stringBuffer2.append(parseInt);
                        Logger.println(stringBuffer2.toString());
                        return;
                    }
                    return;
                case 5:
                    this.in.skip(readShort - 1);
                    return;
                default:
                    if (Logger.isActive()) {
                        stringBuffer = new StringBuffer();
                        stringBuffer.append("Unknown environment change type 0x");
                        stringBuffer.append(Integer.toHexString(read));
                        Logger.println(stringBuffer.toString());
                    }
                    this.in.skip(readShort - 1);
                    return;
            }
        }
        int read2 = this.in.read();
        byte[] bArr = new byte[5];
        if (read2 == 5) {
            this.in.read(bArr);
            this.connection.setCollation(bArr);
        } else {
            this.in.skip(read2);
        }
        this.in.skip(this.in.read());
    }

    private void tds5ErrorToken() throws IOException {
        short readShort = this.in.readShort();
        int readInt = this.in.readInt();
        int read = this.in.read();
        int read2 = this.in.read();
        int read3 = this.in.read();
        this.in.readNonUnicodeString(read3);
        this.in.read();
        this.in.readShort();
        read3 = (read3 + 4) + 6;
        short readShort2 = this.in.readShort();
        String readNonUnicodeString = this.in.readNonUnicodeString(readShort2);
        read3 += readShort2 + 2;
        int read4 = this.in.read();
        String readNonUnicodeString2 = this.in.readNonUnicodeString(read4);
        read3 += read4 + 1;
        read4 = this.in.read();
        String readNonUnicodeString3 = this.in.readNonUnicodeString(read4);
        read3 += read4 + 1;
        short readShort3 = this.in.readShort();
        int i = readShort - (read3 + 2);
        if (i > 0) {
            this.in.skip(i);
        }
        if (read2 > 10) {
            this.messages.addDiagnostic(readInt, read, read2, readNonUnicodeString, readNonUnicodeString2, readNonUnicodeString3, readShort3);
        } else {
            this.messages.addDiagnostic(readInt, read, read2, readNonUnicodeString, readNonUnicodeString2, readNonUnicodeString3, readShort3);
        }
    }

    private void tds5DynamicToken() throws IOException {
        short readShort = this.in.readShort();
        byte read = (byte) this.in.read();
        this.in.read();
        int i = readShort - 2;
        if (read == (byte) 32) {
            int read2 = this.in.read();
            this.in.skip(read2);
            i -= read2 + 1;
        }
        this.in.skip(i);
    }

    private void tds5ParamFmtToken() throws IOException, ProtocolException {
        this.in.readShort();
        short readShort = this.in.readShort();
        ColInfo[] colInfoArr = new ColInfo[readShort];
        for (short s = (short) 0; s < readShort; s++) {
            ColInfo colInfo = new ColInfo();
            colInfo.realName = this.in.readNonUnicodeString(this.in.read());
            int read = this.in.read();
            colInfo.isCaseSensitive = false;
            colInfo.nullable = (read & 32) != 0 ? 1 : 0;
            colInfo.isWriteable = (read & 16) != 0;
            colInfo.isIdentity = (read & 64) != 0;
            colInfo.isKey = (read & 2) != 0;
            colInfo.isHidden = (read & 1) != 0;
            colInfo.userType = this.in.readInt();
            if (((byte) this.in.peek()) == TDS_DONE_TOKEN) {
                this.currentToken.dynamParamInfo = null;
                this.currentToken.dynamParamData = null;
                this.messages.addDiagnostic(9999, 0, 16, "Prepare failed", "", "", 0);
                return;
            }
            TdsData.readType(this.in, colInfo);
            this.in.skip(1);
            colInfoArr[s] = colInfo;
        }
        this.currentToken.dynamParamInfo = colInfoArr;
        this.currentToken.dynamParamData = new Object[readShort];
    }

    private void tdsNtlmAuthToken() throws IOException, ProtocolException {
        short readShort = this.in.readShort();
        if (readShort < (short) 40) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("NTLM challenge: packet is too small:");
            stringBuffer.append(readShort);
            throw new ProtocolException(stringBuffer.toString());
        }
        byte[] bArr = new byte[readShort];
        this.in.read(bArr);
        int intFromBuffer = getIntFromBuffer(bArr, 8);
        if (intFromBuffer != 2) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("NTLM challenge: got unexpected sequence number:");
            stringBuffer2.append(intFromBuffer);
            throw new ProtocolException(stringBuffer2.toString());
        }
        getIntFromBuffer(bArr, 20);
        int shortFromBuffer = getShortFromBuffer(bArr, 40);
        intFromBuffer = getIntFromBuffer(bArr, 44);
        this.currentToken.ntlmTarget = new byte[shortFromBuffer];
        System.arraycopy(bArr, intFromBuffer, this.currentToken.ntlmTarget, 0, shortFromBuffer);
        this.currentToken.nonce = new byte[8];
        this.currentToken.ntlmMessage = bArr;
        System.arraycopy(bArr, 24, this.currentToken.nonce, 0, 8);
    }

    private static int getIntFromBuffer(byte[] bArr, int i) {
        return ((((bArr[i + 3] & 255) << 24) | ((bArr[i + 2] & 255) << 16)) | ((bArr[i + 1] & 255) << 8)) | (bArr[i] & 255);
    }

    private static int getShortFromBuffer(byte[] bArr, int i) {
        return ((bArr[i + 1] & 255) << 8) | (bArr[i] & 255);
    }

    private void tds5ResultToken() throws IOException, ProtocolException {
        this.in.readShort();
        short readShort = this.in.readShort();
        this.columns = new ColInfo[readShort];
        this.rowData = new Object[readShort];
        this.tables = null;
        for (short s = (short) 0; s < readShort; s++) {
            ColInfo colInfo = new ColInfo();
            colInfo.realName = this.in.readNonUnicodeString(this.in.read());
            colInfo.name = colInfo.realName;
            int read = this.in.read();
            colInfo.isCaseSensitive = false;
            colInfo.nullable = (read & 32) != 0 ? 1 : 0;
            colInfo.isWriteable = (read & 16) != 0;
            colInfo.isIdentity = (read & 64) != 0;
            colInfo.isKey = (read & 2) != 0;
            colInfo.isHidden = (read & 1) != 0;
            colInfo.userType = this.in.readInt();
            TdsData.readType(this.in, colInfo);
            this.in.skip(1);
            this.columns[s] = colInfo;
        }
        this.endOfResults = false;
    }

    private void tdsDoneToken() throws IOException {
        this.currentToken.status = (byte) this.in.read();
        this.in.skip(1);
        this.currentToken.operation = (byte) this.in.read();
        this.in.skip(1);
        this.currentToken.updateCount = this.in.readInt();
        if (!this.endOfResults) {
            TdsToken tdsToken = this.currentToken;
            tdsToken.status = (byte) (tdsToken.status & -17);
            this.endOfResults = true;
        }
        if ((this.currentToken.status & 32) != 0) {
            synchronized (this.cancelMonitor) {
                this.cancelPending = false;
                if (this.cancelMonitor[0] == 0) {
                    this.messages.addException(new SQLException(Messages.get("error.generic.cancelled", (Object) "Statement"), "HY008"));
                }
            }
        }
        if ((this.currentToken.status & 1) == 0) {
            this.endOfResponse = this.cancelPending ^ true;
            if (this.fatalError) {
                this.connection.setClosed();
            }
        }
        if (this.serverType == 1 && this.currentToken.operation == (byte) -63) {
            tdsToken = this.currentToken;
            tdsToken.status = (byte) (tdsToken.status & -17);
        }
    }

    private void executeSQL42(String str, String str2, ParamInfo[] paramInfoArr, boolean z, boolean z2) throws IOException, SQLException {
        if (str2 != null) {
            this.out.setPacketType((byte) 3);
            byte[] encodeString = Support.encodeString(this.connection.getCharset(), str2);
            this.out.write((byte) encodeString.length);
            this.out.write(encodeString);
            this.out.write((short) (z ? true : false));
            if (paramInfoArr != null) {
                for (str = this.nextParam + 1; str < paramInfoArr.length; str++) {
                    if (paramInfoArr[str].name) {
                        byte[] encodeString2 = Support.encodeString(this.connection.getCharset(), paramInfoArr[str].name);
                        this.out.write((byte) encodeString2.length);
                        this.out.write(encodeString2);
                    } else {
                        this.out.write((byte) null);
                    }
                    this.out.write((byte) paramInfoArr[str].isOutput);
                    TdsData.writeParam(this.out, this.connection.getCharsetInfo(), null, paramInfoArr[str]);
                }
            }
            if (!z2) {
                this.out.write((byte) DONE_END_OF_RESPONSE);
            }
        } else if (str.length() > null) {
            if (paramInfoArr != null) {
                str = Support.substituteParameters(str, paramInfoArr, this.connection);
            }
            this.out.setPacketType((byte) 1);
            this.out.write(str);
            if (!z2) {
                this.out.write(" ");
            }
        }
    }

    private void executeSQL50(String str, String str2, ParamInfo[] paramInfoArr) throws IOException, SQLException {
        boolean z = false;
        int i = paramInfoArr != null ? 1 : 0;
        this.currentToken.dynamParamInfo = null;
        this.currentToken.dynamParamData = null;
        int i2 = 0;
        while (i != 0 && i2 < paramInfoArr.length) {
            if ("text".equals(paramInfoArr[i2].sqlType) || "image".equals(paramInfoArr[i2].sqlType) || "unitext".equals(paramInfoArr[i2].sqlType)) {
                if (str2 != null && str2.length() > 0) {
                    if ("text".equals(paramInfoArr[i2].sqlType) == null) {
                        if ("unitext".equals(paramInfoArr[i2].sqlType) == null) {
                            throw new SQLException(Messages.get("error.bintoolong"), "HY000");
                        }
                    }
                    throw new SQLException(Messages.get("error.chartoolong"), "HY000");
                } else if (paramInfoArr[i2].tdsType != 36) {
                    str = Support.substituteParameters(str, paramInfoArr, this.connection);
                    str2 = null;
                    i = 0;
                    break;
                }
            }
            i2++;
        }
        this.out.setPacketType(SYBQUERY_PKT);
        i2 = 2;
        byte[] encodeString;
        if (str2 == null) {
            this.out.write((byte) TDS_LANG_TOKEN);
            if (i != 0) {
                str = Support.substituteParamMarkers(str, paramInfoArr);
            }
            if (this.connection.isWideChar() != null) {
                encodeString = Support.encodeString(this.connection.getCharset(), str);
                this.out.write(encodeString.length + 1);
                this.out.write((byte) i);
                this.out.write(encodeString);
            } else {
                this.out.write(str.length() + 1);
                this.out.write((byte) i);
                this.out.write(str);
            }
        } else if (str2.startsWith("#jtds") != null) {
            this.out.write((byte) TDS5_DYNAMIC_TOKEN);
            this.out.write((short) (str2.length() + 4));
            this.out.write((byte) 2);
            this.out.write((byte) i);
            this.out.write((byte) (str2.length() - 1));
            this.out.write(str2.substring(1));
            this.out.write((short) 0);
        } else {
            int i3;
            encodeString = Support.encodeString(this.connection.getCharset(), str2);
            this.out.write((byte) TDS_DBRPC_TOKEN);
            this.out.write((short) (encodeString.length + 3));
            this.out.write((byte) encodeString.length);
            this.out.write(encodeString);
            str = this.out;
            if (i != 0) {
                i3 = 2;
            }
            str.write((short) i3);
            z = true;
        }
        if (i != 0) {
            this.out.write((byte) TDS5_PARAMFMT_TOKEN);
            for (str = this.nextParam + 1; str < paramInfoArr.length; str++) {
                i2 += TdsData.getTds5ParamSize(this.connection.getCharset(), this.connection.isWideChar(), paramInfoArr[str], z);
            }
            this.out.write((short) i2);
            this.out.write((short) (this.nextParam < null ? paramInfoArr.length : paramInfoArr.length - 1));
            for (str = this.nextParam + 1; str < paramInfoArr.length; str++) {
                TdsData.writeTds5ParamFmt(this.out, this.connection.getCharset(), this.connection.isWideChar(), paramInfoArr[str], z);
            }
            this.out.write((byte) TDS5_PARAMS_TOKEN);
            for (str = this.nextParam + 1; str < paramInfoArr.length; str++) {
                TdsData.writeTds5Param(this.out, this.connection.getCharsetInfo(), paramInfoArr[str]);
            }
        }
    }

    public static boolean isPreparedProcedureName(String str) {
        return (str == null || str.length() <= 0 || Character.isDigit(str.charAt(0)) == null) ? false : true;
    }

    private void executeSQL70(String str, String str2, ParamInfo[] paramInfoArr, boolean z, boolean z2) throws IOException, SQLException {
        int prepareSql = this.connection.getPrepareSql();
        int i = 2;
        if (paramInfoArr == null && prepareSql == 2) {
            prepareSql = 0;
        }
        if (this.inBatch) {
            prepareSql = 2;
        }
        if (str2 == null) {
            if (paramInfoArr != null) {
                if (prepareSql == 0) {
                    str = Support.substituteParameters(str, paramInfoArr, this.connection);
                } else {
                    str2 = new ParamInfo[(paramInfoArr.length + 2)];
                    System.arraycopy(paramInfoArr, 0, str2, 2, paramInfoArr.length);
                    str2[0] = new ParamInfo(-1, Support.substituteParamMarkers(str, paramInfoArr), 4);
                    TdsData.getNativeType(this.connection, str2[0]);
                    str2[1] = new ParamInfo(-1, Support.getParameterDefinitions(paramInfoArr), 4);
                    TdsData.getNativeType(this.connection, str2[1]);
                    paramInfoArr = str2;
                    str2 = "sp_executesql";
                }
            }
        } else if (isPreparedProcedureName(str2)) {
            if (paramInfoArr != null) {
                Object obj = new ParamInfo[(paramInfoArr.length + 1)];
                System.arraycopy(paramInfoArr, 0, obj, 1, paramInfoArr.length);
                paramInfoArr = obj;
            } else {
                paramInfoArr = new ParamInfo[1];
            }
            paramInfoArr[0] = new ParamInfo(4, new Integer(str2), 0);
            TdsData.getNativeType(this.connection, paramInfoArr[0]);
            str2 = "sp_execute";
        }
        if (str2 != null) {
            this.out.setPacketType((byte) 3);
            if (this.tdsVersion >= 4) {
                Integer num = (Integer) tds8SpNames.get(str2);
                if (num != null) {
                    this.out.write((short) -1);
                    this.out.write(num.shortValue());
                    str = this.out;
                    if (z) {
                        i = 0;
                    }
                    str.write((short) i);
                    if (paramInfoArr != null) {
                        for (str = this.nextParam + 1; str < paramInfoArr.length; str++) {
                            if (paramInfoArr[str].name == null) {
                                this.out.write((byte) paramInfoArr[str].name.length());
                                this.out.write(paramInfoArr[str].name);
                            } else {
                                this.out.write((byte) 0);
                            }
                            this.out.write((byte) paramInfoArr[str].isOutput);
                            TdsData.writeParam(this.out, this.connection.getCharsetInfo(), this.connection.getCollation(), paramInfoArr[str]);
                        }
                    }
                    if (!z2) {
                        this.out.write((byte) DONE_END_OF_RESPONSE);
                    }
                }
            }
            this.out.write((short) str2.length());
            this.out.write(str2);
            str = this.out;
            if (z) {
                i = 0;
            }
            str.write((short) i);
            if (paramInfoArr != null) {
                for (str = this.nextParam + 1; str < paramInfoArr.length; str++) {
                    if (paramInfoArr[str].name == null) {
                        this.out.write((byte) 0);
                    } else {
                        this.out.write((byte) paramInfoArr[str].name.length());
                        this.out.write(paramInfoArr[str].name);
                    }
                    this.out.write((byte) paramInfoArr[str].isOutput);
                    TdsData.writeParam(this.out, this.connection.getCharsetInfo(), this.connection.getCollation(), paramInfoArr[str]);
                }
            }
            if (!z2) {
                this.out.write((byte) DONE_END_OF_RESPONSE);
            }
        } else if (str.length() > null) {
            this.out.setPacketType((byte) 1);
            this.out.write(str);
            if (!z2) {
                this.out.write(" ");
            }
        }
    }

    private void setRowCountAndTextSize(int i, int i2) throws SQLException {
        Object obj = (i < 0 || i == this.connection.getRowCount()) ? null : 1;
        Object obj2 = (i2 < 0 || i2 == this.connection.getTextSize()) ? null : 1;
        if (obj != null || obj2 != null) {
            try {
                StringBuffer stringBuffer = new StringBuffer(64);
                if (obj != null) {
                    stringBuffer.append("SET ROWCOUNT ");
                    stringBuffer.append(i);
                }
                if (obj2 != null) {
                    stringBuffer.append(" SET TEXTSIZE ");
                    stringBuffer.append(i2 == 0 ? ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED : i2);
                }
                this.out.setPacketType((byte) 1);
                this.out.write(stringBuffer.toString());
                this.out.flush();
                this.endOfResponse = false;
                this.endOfResults = true;
                wait(0);
                clearResponseQueue();
                this.messages.checkErrors();
                this.connection.setRowCount(i);
                this.connection.setTextSize(i2);
            } catch (int i3) {
                throw new SQLException(Messages.get("error.generic.ioerror", i3.getMessage()), "08S01");
            }
        }
    }

    private void wait(int i) throws IOException, SQLException {
        Object obj = null;
        if (i > 0) {
            try {
                obj = TimerThread.getInstance().setTimer(i * 1000, new TimerListener() {
                    public void timerExpired() {
                        TdsCore.this.cancel(true);
                    }
                });
            } catch (Throwable th) {
                if (null != null && !TimerThread.getInstance().cancelTimer(null)) {
                    i = new SQLException(Messages.get("error.generic.timeout"), "HYT00");
                }
            }
        }
        this.in.peek();
        if (obj != null && TimerThread.getInstance().cancelTimer(obj) == 0) {
            throw new SQLException(Messages.get("error.generic.timeout"), "HYT00");
        }
    }

    public void cleanUp() {
        if (this.endOfResponse) {
            this.returnParam = null;
            this.parameters = null;
            this.columns = null;
            this.rowData = null;
            this.tables = null;
            this.computedColumns = null;
            this.computedRowData = null;
            this.messages.clearWarnings();
        }
    }

    public SQLDiagnostic getMessages() {
        return this.messages;
    }

    private static byte[] getMACAddress(java.lang.String r7) {
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
        r0 = 6;
        r1 = new byte[r0];
        r2 = 0;
        if (r7 == 0) goto L_0x0027;
    L_0x0006:
        r3 = r7.length();
        r4 = 12;
        if (r3 != r4) goto L_0x0027;
    L_0x000e:
        r3 = 0;
        r4 = 0;
    L_0x0010:
        if (r3 >= r0) goto L_0x0025;
    L_0x0012:
        r5 = r4 + 2;
        r4 = r7.substring(r4, r5);	 Catch:{ Exception -> 0x0027 }
        r6 = 16;	 Catch:{ Exception -> 0x0027 }
        r4 = java.lang.Integer.parseInt(r4, r6);	 Catch:{ Exception -> 0x0027 }
        r4 = (byte) r4;	 Catch:{ Exception -> 0x0027 }
        r1[r3] = r4;	 Catch:{ Exception -> 0x0027 }
        r3 = r3 + 1;
        r4 = r5;
        goto L_0x0010;
    L_0x0025:
        r7 = 1;
        goto L_0x0028;
    L_0x0027:
        r7 = 0;
    L_0x0028:
        if (r7 != 0) goto L_0x002d;
    L_0x002a:
        java.util.Arrays.fill(r1, r2);
    L_0x002d:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.getMACAddress(java.lang.String):byte[]");
    }

    private static java.lang.String getHostName() {
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
        r0 = hostName;
        if (r0 == 0) goto L_0x0007;
    L_0x0004:
        r0 = hostName;
        return r0;
    L_0x0007:
        r0 = java.net.InetAddress.getLocalHost();	 Catch:{ UnknownHostException -> 0x003a }
        r0 = r0.getHostName();	 Catch:{ UnknownHostException -> 0x003a }
        r0 = r0.toUpperCase();	 Catch:{ UnknownHostException -> 0x003a }
        r1 = 46;
        r1 = r0.indexOf(r1);
        if (r1 < 0) goto L_0x0020;
    L_0x001b:
        r2 = 0;
        r0 = r0.substring(r2, r1);
    L_0x0020:
        r1 = r0.length();
        if (r1 != 0) goto L_0x002d;
    L_0x0026:
        r0 = "UNKNOWN";
        hostName = r0;
        r0 = hostName;
        return r0;
    L_0x002d:
        java.lang.Integer.parseInt(r0);	 Catch:{ NumberFormatException -> 0x0037 }
        r1 = "UNKNOWN";	 Catch:{ NumberFormatException -> 0x0037 }
        hostName = r1;	 Catch:{ NumberFormatException -> 0x0037 }
        r1 = hostName;	 Catch:{ NumberFormatException -> 0x0037 }
        return r1;
    L_0x0037:
        hostName = r0;
        return r0;
    L_0x003a:
        r0 = "UNKNOWN";
        hostName = r0;
        r0 = hostName;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsCore.getHostName():java.lang.String");
    }

    private static String tds7CryptPass(String str) {
        int length = str.length();
        char[] cArr = new char[length];
        for (int i = 0; i < length; i++) {
            int charAt = str.charAt(i) ^ 23130;
            cArr[i] = (char) (((charAt << 4) & 61680) | ((charAt >> 4) & 3855));
        }
        return new String(cArr);
    }

    private void tdsComputedResultToken() throws IOException, ProtocolException {
        short readShort = this.in.readShort();
        this.computedColumns = new ColInfo[readShort];
        this.in.readShort();
        this.in.skip(this.in.read() * 2);
        for (short s = (short) 0; s < readShort; s++) {
            ColInfo colInfo = new ColInfo();
            this.computedColumns[s] = colInfo;
            int read = this.in.read();
            if (read == 9) {
                colInfo.name = "count_big";
            } else if (read == 75) {
                colInfo.name = "count";
            } else if (read == 77) {
                colInfo.name = "sum";
            } else if (read != 79) {
                switch (read) {
                    case 48:
                        colInfo.name = "stdev";
                        break;
                    case 49:
                        colInfo.name = "stdevp";
                        break;
                    case 50:
                        colInfo.name = "var";
                        break;
                    case 51:
                        colInfo.name = "varp";
                        break;
                    default:
                        switch (read) {
                            case 81:
                                colInfo.name = "min";
                                break;
                            case 82:
                                colInfo.name = "max";
                                break;
                            default:
                                StringBuffer stringBuffer = new StringBuffer();
                                stringBuffer.append("unsupported aggregation type 0x");
                                stringBuffer.append(Integer.toHexString(read));
                                throw new ProtocolException(stringBuffer.toString());
                        }
                }
            } else {
                colInfo.name = "avg";
            }
            boolean z = true;
            read = this.in.readShort() - 1;
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append(colInfo.name);
            stringBuffer2.append("(");
            stringBuffer2.append(this.columns[read].name);
            stringBuffer2.append(")");
            colInfo.name = stringBuffer2.toString();
            colInfo.realName = colInfo.name;
            colInfo.tableName = this.columns[read].tableName;
            colInfo.catalog = this.columns[read].catalog;
            colInfo.schema = this.columns[read].schema;
            colInfo.userType = this.in.readShort();
            short readShort2 = this.in.readShort();
            colInfo.nullable = (readShort2 & 1) != 0 ? 1 : 0;
            colInfo.isCaseSensitive = (readShort2 & 2) != 0;
            colInfo.isIdentity = (readShort2 & 16) != 0;
            if ((readShort2 & 12) == 0) {
                z = false;
            }
            colInfo.isWriteable = z;
            TdsData.readType(this.in, colInfo);
            this.in.readString(this.in.read());
        }
    }

    private void tdsComputedRowToken() throws IOException, ProtocolException, SQLException {
        this.in.readShort();
        this.computedRowData = new Object[this.computedColumns.length];
        for (int i = 0; i < this.computedRowData.length; i++) {
            this.computedRowData[i] = TdsData.readData(this.connection, this.in, this.computedColumns[i]);
        }
    }
}
