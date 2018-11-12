package net.sourceforge.jtds.jdbc;

import android.support.v4.view.PointerIconCompat;
import java.io.File;
import java.lang.ref.WeakReference;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import net.sourceforge.jtds.jdbc.cache.ProcedureCache;
import net.sourceforge.jtds.jdbc.cache.StatementCache;
import net.sourceforge.jtds.util.Logger;

public class ConnectionJDBC2 implements Connection {
    private static final String SQL_SERVER_65_CHARSET_QUERY = "select name from master.dbo.syscharsets where id = (select csid from master.dbo.syscharsets, master.dbo.sysconfigures where config=1123 and id = value)";
    private static final String SQL_SERVER_INITIAL_SQL = "SELECT @@MAX_PRECISION\r\nSET TRANSACTION ISOLATION LEVEL READ COMMITTED\r\nSET IMPLICIT_TRANSACTIONS OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647";
    private static final String SYBASE_INITIAL_SQL = "SET TRANSACTION ISOLATION LEVEL 1\r\nSET CHAINED OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647";
    private static final String SYBASE_SERVER_CHARSET_QUERY = "select name from master.dbo.syscharsets where id = (select value from master.dbo.sysconfigures where config=131)";
    public static final int TRANSACTION_SNAPSHOT = 4096;
    private static int[] connections = new int[1];
    private static Integer processId;
    private String appName;
    private boolean autoCommit;
    private final TdsCore baseTds;
    private int batchSize;
    private String bindAddress;
    private File bufferDir;
    private int bufferMaxMemory;
    private int bufferMinPackets;
    private TdsCore cachedTds;
    private CharsetInfo charsetInfo;
    private boolean charsetSpecified;
    private boolean closed;
    private byte[] collation;
    private String currentDatabase;
    private int cursorSequenceNo;
    private int databaseMajorVersion;
    private int databaseMinorVersion;
    private String databaseName;
    private String databaseProductName;
    private String databaseProductVersion;
    private String domainName;
    private String instanceName;
    private String language;
    private boolean lastUpdateCount;
    private long lobBuffer;
    private int loginTimeout;
    private String macAddress;
    private int maxPrecision;
    private int maxStatements;
    private final SQLDiagnostic messages;
    private final Semaphore mutex;
    private boolean namedPipe;
    private int netPacketSize;
    private int packetSize;
    private String password;
    private int portNumber;
    private int prepareSql;
    private final ArrayList procInTran;
    private String progName;
    private boolean readOnly;
    private int rowCount;
    private String serverCharset;
    private String serverName;
    private int serverType;
    private final SharedSocket socket;
    private boolean socketKeepAlive;
    private int socketTimeout;
    private int spSequenceNo;
    private String ssl;
    private StatementCache statementCache;
    private final ArrayList statements;
    private int sybaseInfo;
    private boolean tcpNoDelay;
    private int tdsVersion;
    private int textSize;
    private int transactionIsolation;
    private final String url;
    private boolean useCursors;
    private boolean useJCIFS;
    private boolean useLOBs;
    private boolean useMetadataCache;
    private boolean useNTLMv2;
    private boolean useUnicode;
    private String user;
    private String wsid;
    private boolean xaEmulation;
    private int xaState;
    private boolean xaTransaction;
    private Object xid;

    void clearSavepoints() {
    }

    synchronized java.lang.String prepareSQL(net.sourceforge.jtds.jdbc.JtdsPreparedStatement r16, java.lang.String r17, net.sourceforge.jtds.jdbc.ParamInfo[] r18, boolean r19, boolean r20) throws java.sql.SQLException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Not initialized variable reg: 12, insn: 0x0128: INVOKE  (r1_0 net.sourceforge.jtds.jdbc.ConnectionJDBC2), (r12 java.lang.String), (r13 net.sourceforge.jtds.jdbc.ProcEntry) net.sourceforge.jtds.jdbc.ConnectionJDBC2.addCachedProcedure(java.lang.String, net.sourceforge.jtds.jdbc.ProcEntry):void type: VIRTUAL, block:B:65:0x0128, method: net.sourceforge.jtds.jdbc.ConnectionJDBC2.prepareSQL(net.sourceforge.jtds.jdbc.JtdsPreparedStatement, java.lang.String, net.sourceforge.jtds.jdbc.ParamInfo[], boolean, boolean):java.lang.String
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:168)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:132)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
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
        r15 = this;
        r1 = r15;
        r2 = r16;
        r9 = r18;
        monitor-enter(r15);
        r3 = r1.prepareSql;	 Catch:{ all -> 0x0145 }
        r4 = 0;	 Catch:{ all -> 0x0145 }
        if (r3 == 0) goto L_0x0143;	 Catch:{ all -> 0x0145 }
    L_0x000b:
        r3 = r1.prepareSql;	 Catch:{ all -> 0x0145 }
        r10 = 2;	 Catch:{ all -> 0x0145 }
        if (r3 != r10) goto L_0x0012;	 Catch:{ all -> 0x0145 }
    L_0x0010:
        goto L_0x0143;	 Catch:{ all -> 0x0145 }
    L_0x0012:
        r3 = r1.serverType;	 Catch:{ all -> 0x0145 }
        if (r3 != r10) goto L_0x0024;	 Catch:{ all -> 0x0145 }
    L_0x0016:
        r3 = r1.tdsVersion;	 Catch:{ all -> 0x0145 }
        if (r3 == r10) goto L_0x001c;
    L_0x001a:
        monitor-exit(r15);
        return r4;
    L_0x001c:
        if (r19 == 0) goto L_0x0020;
    L_0x001e:
        monitor-exit(r15);
        return r4;
    L_0x0020:
        if (r20 == 0) goto L_0x0024;
    L_0x0022:
        monitor-exit(r15);
        return r4;
    L_0x0024:
        r3 = 0;
    L_0x0025:
        r5 = r9.length;	 Catch:{ all -> 0x0145 }
        r11 = 1;	 Catch:{ all -> 0x0145 }
        if (r3 >= r5) goto L_0x0068;	 Catch:{ all -> 0x0145 }
    L_0x0029:
        r5 = r9[r3];	 Catch:{ all -> 0x0145 }
        r5 = r5.isSet;	 Catch:{ all -> 0x0145 }
        if (r5 != 0) goto L_0x0042;	 Catch:{ all -> 0x0145 }
    L_0x002f:
        r2 = new java.sql.SQLException;	 Catch:{ all -> 0x0145 }
        r4 = "error.prepare.paramnotset";	 Catch:{ all -> 0x0145 }
        r3 = r3 + r11;	 Catch:{ all -> 0x0145 }
        r3 = java.lang.Integer.toString(r3);	 Catch:{ all -> 0x0145 }
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r4, r3);	 Catch:{ all -> 0x0145 }
        r4 = "07000";	 Catch:{ all -> 0x0145 }
        r2.<init>(r3, r4);	 Catch:{ all -> 0x0145 }
        throw r2;	 Catch:{ all -> 0x0145 }
    L_0x0042:
        r5 = r9[r3];	 Catch:{ all -> 0x0145 }
        net.sourceforge.jtds.jdbc.TdsData.getNativeType(r1, r5);	 Catch:{ all -> 0x0145 }
        r5 = r1.serverType;	 Catch:{ all -> 0x0145 }
        if (r5 != r10) goto L_0x0065;	 Catch:{ all -> 0x0145 }
    L_0x004b:
        r5 = "text";	 Catch:{ all -> 0x0145 }
        r6 = r9[r3];	 Catch:{ all -> 0x0145 }
        r6 = r6.sqlType;	 Catch:{ all -> 0x0145 }
        r5 = r5.equals(r6);	 Catch:{ all -> 0x0145 }
        if (r5 != 0) goto L_0x0063;	 Catch:{ all -> 0x0145 }
    L_0x0057:
        r5 = "image";	 Catch:{ all -> 0x0145 }
        r6 = r9[r3];	 Catch:{ all -> 0x0145 }
        r6 = r6.sqlType;	 Catch:{ all -> 0x0145 }
        r5 = r5.equals(r6);	 Catch:{ all -> 0x0145 }
        if (r5 == 0) goto L_0x0065;
    L_0x0063:
        monitor-exit(r15);
        return r4;
    L_0x0065:
        r3 = r3 + 1;
        goto L_0x0025;
    L_0x0068:
        r5 = r1.serverType;	 Catch:{ all -> 0x0145 }
        r6 = getCatalog();	 Catch:{ all -> 0x0145 }
        r7 = r1.autoCommit;	 Catch:{ all -> 0x0145 }
        r3 = r17;	 Catch:{ all -> 0x0145 }
        r4 = r9;	 Catch:{ all -> 0x0145 }
        r8 = r20;	 Catch:{ all -> 0x0145 }
        r12 = net.sourceforge.jtds.jdbc.Support.getStatementKey(r3, r4, r5, r6, r7, r8);	 Catch:{ all -> 0x0145 }
        r3 = r1.statementCache;	 Catch:{ all -> 0x0145 }
        r3 = r3.get(r12);	 Catch:{ all -> 0x0145 }
        r3 = (net.sourceforge.jtds.jdbc.ProcEntry) r3;	 Catch:{ all -> 0x0145 }
        if (r3 == 0) goto L_0x00a7;	 Catch:{ all -> 0x0145 }
    L_0x0083:
        r4 = r2.handles;	 Catch:{ all -> 0x0145 }
        if (r4 == 0) goto L_0x0092;	 Catch:{ all -> 0x0145 }
    L_0x0087:
        r4 = r2.handles;	 Catch:{ all -> 0x0145 }
        r4 = r4.contains(r3);	 Catch:{ all -> 0x0145 }
        if (r4 == 0) goto L_0x0092;	 Catch:{ all -> 0x0145 }
    L_0x008f:
        r3.release();	 Catch:{ all -> 0x0145 }
    L_0x0092:
        r4 = r3.getColMetaData();	 Catch:{ all -> 0x0145 }
        r2.setColMetaData(r4);	 Catch:{ all -> 0x0145 }
        r4 = r1.serverType;	 Catch:{ all -> 0x0145 }
        if (r4 != r10) goto L_0x00a4;	 Catch:{ all -> 0x0145 }
    L_0x009d:
        r4 = r3.getParamMetaData();	 Catch:{ all -> 0x0145 }
        r2.setParamMetaData(r4);	 Catch:{ all -> 0x0145 }
    L_0x00a4:
        r13 = r3;	 Catch:{ all -> 0x0145 }
        goto L_0x012b;	 Catch:{ all -> 0x0145 }
    L_0x00a7:
        r13 = new net.sourceforge.jtds.jdbc.ProcEntry;	 Catch:{ all -> 0x0145 }
        r13.<init>();	 Catch:{ all -> 0x0145 }
        r3 = r1.serverType;	 Catch:{ all -> 0x0145 }
        r14 = 4;	 Catch:{ all -> 0x0145 }
        if (r3 != r11) goto L_0x00f0;	 Catch:{ all -> 0x0145 }
    L_0x00b1:
        r3 = r1.baseTds;	 Catch:{ all -> 0x0145 }
        r7 = r16.getResultSetType();	 Catch:{ all -> 0x0145 }
        r8 = r16.getResultSetConcurrency();	 Catch:{ all -> 0x0145 }
        r4 = r17;	 Catch:{ all -> 0x0145 }
        r5 = r9;	 Catch:{ all -> 0x0145 }
        r6 = r20;	 Catch:{ all -> 0x0145 }
        r3 = r3.microsoftPrepare(r4, r5, r6, r7, r8);	 Catch:{ all -> 0x0145 }
        r13.setName(r3);	 Catch:{ all -> 0x0145 }
        r3 = r13.toString();	 Catch:{ all -> 0x0145 }
        if (r3 != 0) goto L_0x00d1;	 Catch:{ all -> 0x0145 }
    L_0x00cd:
        r13.setType(r14);	 Catch:{ all -> 0x0145 }
        goto L_0x0128;	 Catch:{ all -> 0x0145 }
    L_0x00d1:
        r3 = r1.prepareSql;	 Catch:{ all -> 0x0145 }
        if (r3 != r11) goto L_0x00d9;	 Catch:{ all -> 0x0145 }
    L_0x00d5:
        r13.setType(r11);	 Catch:{ all -> 0x0145 }
        goto L_0x0128;	 Catch:{ all -> 0x0145 }
    L_0x00d9:
        if (r20 == 0) goto L_0x00dc;	 Catch:{ all -> 0x0145 }
    L_0x00db:
        r10 = 3;	 Catch:{ all -> 0x0145 }
    L_0x00dc:
        r13.setType(r10);	 Catch:{ all -> 0x0145 }
        r3 = r1.baseTds;	 Catch:{ all -> 0x0145 }
        r3 = r3.getColumns();	 Catch:{ all -> 0x0145 }
        r13.setColMetaData(r3);	 Catch:{ all -> 0x0145 }
        r3 = r13.getColMetaData();	 Catch:{ all -> 0x0145 }
        r2.setColMetaData(r3);	 Catch:{ all -> 0x0145 }
        goto L_0x0128;	 Catch:{ all -> 0x0145 }
    L_0x00f0:
        r3 = r1.baseTds;	 Catch:{ all -> 0x0145 }
        r4 = r17;	 Catch:{ all -> 0x0145 }
        r3 = r3.sybasePrepare(r4, r9);	 Catch:{ all -> 0x0145 }
        r13.setName(r3);	 Catch:{ all -> 0x0145 }
        r3 = r13.toString();	 Catch:{ all -> 0x0145 }
        if (r3 != 0) goto L_0x0105;	 Catch:{ all -> 0x0145 }
    L_0x0101:
        r13.setType(r14);	 Catch:{ all -> 0x0145 }
        goto L_0x0108;	 Catch:{ all -> 0x0145 }
    L_0x0105:
        r13.setType(r11);	 Catch:{ all -> 0x0145 }
    L_0x0108:
        r3 = r1.baseTds;	 Catch:{ all -> 0x0145 }
        r3 = r3.getColumns();	 Catch:{ all -> 0x0145 }
        r13.setColMetaData(r3);	 Catch:{ all -> 0x0145 }
        r3 = r1.baseTds;	 Catch:{ all -> 0x0145 }
        r3 = r3.getParameters();	 Catch:{ all -> 0x0145 }
        r13.setParamMetaData(r3);	 Catch:{ all -> 0x0145 }
        r3 = r13.getColMetaData();	 Catch:{ all -> 0x0145 }
        r2.setColMetaData(r3);	 Catch:{ all -> 0x0145 }
        r3 = r13.getParamMetaData();	 Catch:{ all -> 0x0145 }
        r2.setParamMetaData(r3);	 Catch:{ all -> 0x0145 }
    L_0x0128:
        addCachedProcedure(r12, r13);	 Catch:{ all -> 0x0145 }
    L_0x012b:
        r3 = r2.handles;	 Catch:{ all -> 0x0145 }
        if (r3 != 0) goto L_0x0138;	 Catch:{ all -> 0x0145 }
    L_0x012f:
        r3 = new java.util.HashSet;	 Catch:{ all -> 0x0145 }
        r4 = 10;	 Catch:{ all -> 0x0145 }
        r3.<init>(r4);	 Catch:{ all -> 0x0145 }
        r2.handles = r3;	 Catch:{ all -> 0x0145 }
    L_0x0138:
        r2 = r2.handles;	 Catch:{ all -> 0x0145 }
        r2.add(r13);	 Catch:{ all -> 0x0145 }
        r2 = r13.toString();	 Catch:{ all -> 0x0145 }
        monitor-exit(r15);
        return r2;
    L_0x0143:
        monitor-exit(r15);
        return r4;
    L_0x0145:
        r0 = move-exception;
        r2 = r0;
        monitor-exit(r15);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ConnectionJDBC2.prepareSQL(net.sourceforge.jtds.jdbc.JtdsPreparedStatement, java.lang.String, net.sourceforge.jtds.jdbc.ParamInfo[], boolean, boolean):java.lang.String");
    }

    private ConnectionJDBC2() {
        this.netPacketSize = 512;
        this.statements = new ArrayList();
        this.transactionIsolation = 2;
        this.autoCommit = true;
        this.maxPrecision = 38;
        this.spSequenceNo = 1;
        this.cursorSequenceNo = 1;
        this.procInTran = new ArrayList();
        this.useUnicode = true;
        this.tcpNoDelay = true;
        this.xaEmulation = true;
        this.mutex = new Semaphore(1);
        this.useNTLMv2 = false;
        synchronized (connections) {
            int[] iArr = connections;
            iArr[0] = iArr[0] + 1;
        }
        this.url = null;
        this.socket = null;
        this.baseTds = null;
        this.messages = null;
    }

    ConnectionJDBC2(java.lang.String r25, java.util.Properties r26) throws java.sql.SQLException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.utils.BlockUtils.getBlockByInsn(BlockUtils.java:171)
	at jadx.core.dex.visitors.ssa.EliminatePhiNodes.replaceMerge(EliminatePhiNodes.java:90)
	at jadx.core.dex.visitors.ssa.EliminatePhiNodes.replaceMergeInstructions(EliminatePhiNodes.java:68)
	at jadx.core.dex.visitors.ssa.EliminatePhiNodes.visit(EliminatePhiNodes.java:31)
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
        r24 = this;
        r1 = r24;
        r24.<init>();
        r2 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        r1.netPacketSize = r2;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r1.statements = r2;
        r2 = 2;
        r1.transactionIsolation = r2;
        r3 = 1;
        r1.autoCommit = r3;
        r4 = 38;
        r1.maxPrecision = r4;
        r1.spSequenceNo = r3;
        r1.cursorSequenceNo = r3;
        r4 = new java.util.ArrayList;
        r4.<init>();
        r1.procInTran = r4;
        r1.useUnicode = r3;
        r1.tcpNoDelay = r3;
        r1.xaEmulation = r3;
        r4 = new net.sourceforge.jtds.jdbc.Semaphore;
        r5 = 1;
        r4.<init>(r5);
        r1.mutex = r4;
        r4 = 0;
        r1.useNTLMv2 = r4;
        r5 = connections;
        monitor-enter(r5);
        r6 = connections;	 Catch:{ all -> 0x0258 }
        r7 = r6[r4];	 Catch:{ all -> 0x0258 }
        r7 = r7 + r3;	 Catch:{ all -> 0x0258 }
        r6[r4] = r7;	 Catch:{ all -> 0x0258 }
        monitor-exit(r5);	 Catch:{ all -> 0x0258 }
        r5 = r25;
        r1.url = r5;
        r5 = r26;
        r1.unpackProperties(r5);
        r5 = new net.sourceforge.jtds.jdbc.SQLDiagnostic;
        r6 = r1.serverType;
        r5.<init>(r6);
        r1.messages = r5;
        r5 = r1.instanceName;
        r5 = r5.length();
        if (r5 <= 0) goto L_0x008e;
    L_0x005c:
        r5 = r1.namedPipe;
        if (r5 != 0) goto L_0x008e;
    L_0x0060:
        r5 = new net.sourceforge.jtds.jdbc.MSSqlServerInfo;	 Catch:{ SQLException -> 0x0070 }
        r6 = r1.serverName;	 Catch:{ SQLException -> 0x0070 }
        r5.<init>(r6);	 Catch:{ SQLException -> 0x0070 }
        r6 = r1.instanceName;	 Catch:{ SQLException -> 0x0070 }
        r5 = r5.getPortForInstance(r6);	 Catch:{ SQLException -> 0x0070 }
        r1.portNumber = r5;	 Catch:{ SQLException -> 0x0070 }
        goto L_0x0077;
    L_0x0070:
        r0 = move-exception;
        r5 = r0;
        r6 = r1.portNumber;
        if (r6 > 0) goto L_0x0077;
    L_0x0076:
        throw r5;
    L_0x0077:
        r5 = r1.portNumber;
        r6 = -1;
        if (r5 != r6) goto L_0x008e;
    L_0x007c:
        r2 = new java.sql.SQLException;
        r3 = "error.msinfo.badinst";
        r4 = r1.serverName;
        r5 = r1.instanceName;
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r3, r4, r5);
        r4 = "08003";
        r2.<init>(r3, r4);
        throw r2;
    L_0x008e:
        r5 = r1.bufferMaxMemory;
        r5 = r5 * 1024;
        net.sourceforge.jtds.jdbc.SharedSocket.setMemoryBudget(r5);
        r5 = r1.bufferMinPackets;
        net.sourceforge.jtds.jdbc.SharedSocket.setMinMemPkts(r5);
        r5 = 0;
        r6 = r1.loginTimeout;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        if (r6 <= 0) goto L_0x00c6;
    L_0x009f:
        r6 = net.sourceforge.jtds.util.TimerThread.getInstance();	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r7 = r1.loginTimeout;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r7 = r7 * 1000;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r8 = new net.sourceforge.jtds.jdbc.ConnectionJDBC2$1;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r8.<init>();	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r6 = r6.setTimer(r7, r8);	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r5 = r6;
        goto L_0x00c6;
    L_0x00b2:
        r0 = move-exception;
        r2 = r0;
        r6 = 1;
        goto L_0x01c9;
    L_0x00b7:
        r0 = move-exception;
        r2 = r0;
        r6 = 1;
        goto L_0x01cd;
    L_0x00bc:
        r0 = move-exception;
        r2 = r0;
        r6 = 1;
        goto L_0x01f3;
    L_0x00c1:
        r0 = move-exception;
        r2 = r0;
        r6 = 1;
        goto L_0x022e;
    L_0x00c6:
        r6 = r1.namedPipe;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        if (r6 == 0) goto L_0x00d1;
    L_0x00ca:
        r6 = r1.createNamedPipe(r1);	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r1.socket = r6;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        goto L_0x00d8;
    L_0x00d1:
        r6 = new net.sourceforge.jtds.jdbc.SharedSocket;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r6.<init>(r1);	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r1.socket = r6;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
    L_0x00d8:
        if (r5 == 0) goto L_0x00f1;
    L_0x00da:
        r6 = net.sourceforge.jtds.util.TimerThread.getInstance();	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r6 = r6.hasExpired(r5);	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        if (r6 == 0) goto L_0x00f1;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
    L_0x00e4:
        r2 = r1.socket;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r2.forceClose();	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r2 = new java.io.IOException;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r6 = "Login timed out";	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r2.<init>(r6);	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        throw r2;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
    L_0x00f1:
        r6 = r1.charsetSpecified;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        if (r6 == 0) goto L_0x00fb;
    L_0x00f5:
        r6 = r1.serverCharset;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r1.loadCharset(r6);	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        goto L_0x0104;
    L_0x00fb:
        r6 = "iso_1";	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r1.loadCharset(r6);	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r6 = "";	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r1.serverCharset = r6;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
    L_0x0104:
        r6 = new net.sourceforge.jtds.jdbc.TdsCore;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r7 = r1.messages;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r6.<init>(r1, r7);	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r1.baseTds = r6;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r6 = r1.tdsVersion;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r7 = 4;
        if (r6 < r7) goto L_0x011f;
    L_0x0112:
        r6 = r1.namedPipe;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        if (r6 != 0) goto L_0x011f;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
    L_0x0116:
        r6 = r1.baseTds;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r7 = r1.instanceName;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r8 = r1.ssl;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r6.negotiateSSL(r7, r8);	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
    L_0x011f:
        r9 = r1.baseTds;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r10 = r1.serverName;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r11 = r1.databaseName;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r12 = r1.user;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r13 = r1.password;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r14 = r1.domainName;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r15 = r1.serverCharset;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r6 = r1.appName;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r7 = r1.progName;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r8 = r1.wsid;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r4 = r1.language;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r3 = r1.macAddress;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r2 = r1.packetSize;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r16 = r6;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r17 = r7;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r18 = r8;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r19 = r4;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r20 = r3;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r21 = r2;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r9.login(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21);	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r2 = r1.messages;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r2 = r2.warnings;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r3 = r1.baseTds;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r3 = r3.getTdsVersion();	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r1.tdsVersion = r3;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r3 = r1.tdsVersion;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r4 = 3;
        if (r3 >= r4) goto L_0x0166;
    L_0x0159:
        r3 = r1.databaseName;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r3 = r3.length();	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        if (r3 <= 0) goto L_0x0166;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
    L_0x0161:
        r3 = r1.databaseName;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r1.setCatalog(r3);	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
    L_0x0166:
        r3 = r1.serverCharset;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        if (r3 == 0) goto L_0x0172;
    L_0x016a:
        r3 = r1.serverCharset;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r3 = r3.length();	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        if (r3 != 0) goto L_0x017d;
    L_0x0172:
        r3 = r1.collation;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        if (r3 != 0) goto L_0x017d;
    L_0x0176:
        r3 = r24.determineServerCharset();	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r1.loadCharset(r3);	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
    L_0x017d:
        r3 = r1.serverType;	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r4 = 2;
        if (r3 != r4) goto L_0x018a;
    L_0x0182:
        r3 = r1.baseTds;	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r4 = "SET TRANSACTION ISOLATION LEVEL 1\r\nSET CHAINED OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647";	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        r3.submitSQL(r4);	 Catch:{ UnknownHostException -> 0x00c1, IOException -> 0x00bc, SQLException -> 0x00b7, RuntimeException -> 0x00b2, all -> 0x01c0 }
        goto L_0x01b2;
    L_0x018a:
        r3 = r24.createStatement();	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r4 = "SELECT @@MAX_PRECISION\r\nSET TRANSACTION ISOLATION LEVEL READ COMMITTED\r\nSET IMPLICIT_TRANSACTIONS OFF\r\nSET QUOTED_IDENTIFIER ON\r\nSET TEXTSIZE 2147483647";	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r4 = r3.executeQuery(r4);	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        r6 = r4.next();	 Catch:{ UnknownHostException -> 0x022b, IOException -> 0x01f0, SQLException -> 0x01ca, RuntimeException -> 0x01c6, all -> 0x01c0 }
        if (r6 == 0) goto L_0x01ab;
    L_0x019a:
        r6 = 1;
        r7 = r4.getByte(r6);	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
        r1.maxPrecision = r7;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
        goto L_0x01ac;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
    L_0x01a2:
        r0 = move-exception;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
        goto L_0x01c8;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
    L_0x01a4:
        r0 = move-exception;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
        goto L_0x01cc;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
    L_0x01a6:
        r0 = move-exception;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
        goto L_0x01f2;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
    L_0x01a8:
        r0 = move-exception;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
        goto L_0x022d;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
    L_0x01ab:
        r6 = 1;	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
    L_0x01ac:
        r4.close();	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
        r3.close();	 Catch:{ UnknownHostException -> 0x01a8, IOException -> 0x01a6, SQLException -> 0x01a4, RuntimeException -> 0x01a2, all -> 0x01c0 }
    L_0x01b2:
        if (r5 == 0) goto L_0x01bb;
    L_0x01b4:
        r3 = net.sourceforge.jtds.util.TimerThread.getInstance();
        r3.cancelTimer(r5);
    L_0x01bb:
        r3 = r1.messages;
        r3.warnings = r2;
        return;
    L_0x01c0:
        r0 = move-exception;
        r2 = r0;
        r22 = 0;
        goto L_0x0248;
    L_0x01c6:
        r0 = move-exception;
        r6 = 1;
    L_0x01c8:
        r2 = r0;
    L_0x01c9:
        throw r2;	 Catch:{ all -> 0x0244 }
    L_0x01ca:
        r0 = move-exception;	 Catch:{ all -> 0x0244 }
        r6 = 1;	 Catch:{ all -> 0x0244 }
    L_0x01cc:
        r2 = r0;	 Catch:{ all -> 0x0244 }
    L_0x01cd:
        r3 = r1.loginTimeout;	 Catch:{ all -> 0x0244 }
        if (r3 <= 0) goto L_0x01ef;	 Catch:{ all -> 0x0244 }
    L_0x01d1:
        r3 = r2.getMessage();	 Catch:{ all -> 0x0244 }
        r4 = "socket closed";	 Catch:{ all -> 0x0244 }
        r3 = r3.indexOf(r4);	 Catch:{ all -> 0x0244 }
        if (r3 < 0) goto L_0x01ef;	 Catch:{ all -> 0x0244 }
    L_0x01dd:
        r3 = new java.sql.SQLException;	 Catch:{ all -> 0x0244 }
        r4 = "error.connection.timeout";	 Catch:{ all -> 0x0244 }
        r4 = net.sourceforge.jtds.jdbc.Messages.get(r4);	 Catch:{ all -> 0x0244 }
        r7 = "HYT01";	 Catch:{ all -> 0x0244 }
        r3.<init>(r4, r7);	 Catch:{ all -> 0x0244 }
        r2 = net.sourceforge.jtds.jdbc.Support.linkException(r3, r2);	 Catch:{ all -> 0x0244 }
        throw r2;	 Catch:{ all -> 0x0244 }
    L_0x01ef:
        throw r2;	 Catch:{ all -> 0x0244 }
    L_0x01f0:
        r0 = move-exception;	 Catch:{ all -> 0x0244 }
        r6 = 1;	 Catch:{ all -> 0x0244 }
    L_0x01f2:
        r2 = r0;	 Catch:{ all -> 0x0244 }
    L_0x01f3:
        r3 = r1.loginTimeout;	 Catch:{ all -> 0x0244 }
        if (r3 <= 0) goto L_0x0215;	 Catch:{ all -> 0x0244 }
    L_0x01f7:
        r3 = r2.getMessage();	 Catch:{ all -> 0x0244 }
        r4 = "timed out";	 Catch:{ all -> 0x0244 }
        r3 = r3.indexOf(r4);	 Catch:{ all -> 0x0244 }
        if (r3 < 0) goto L_0x0215;	 Catch:{ all -> 0x0244 }
    L_0x0203:
        r3 = new java.sql.SQLException;	 Catch:{ all -> 0x0244 }
        r4 = "error.connection.timeout";	 Catch:{ all -> 0x0244 }
        r4 = net.sourceforge.jtds.jdbc.Messages.get(r4);	 Catch:{ all -> 0x0244 }
        r7 = "HYT01";	 Catch:{ all -> 0x0244 }
        r3.<init>(r4, r7);	 Catch:{ all -> 0x0244 }
        r2 = net.sourceforge.jtds.jdbc.Support.linkException(r3, r2);	 Catch:{ all -> 0x0244 }
        throw r2;	 Catch:{ all -> 0x0244 }
    L_0x0215:
        r3 = new java.sql.SQLException;	 Catch:{ all -> 0x0244 }
        r4 = "error.connection.ioerror";	 Catch:{ all -> 0x0244 }
        r7 = r2.getMessage();	 Catch:{ all -> 0x0244 }
        r4 = net.sourceforge.jtds.jdbc.Messages.get(r4, r7);	 Catch:{ all -> 0x0244 }
        r7 = "08S01";	 Catch:{ all -> 0x0244 }
        r3.<init>(r4, r7);	 Catch:{ all -> 0x0244 }
        r2 = net.sourceforge.jtds.jdbc.Support.linkException(r3, r2);	 Catch:{ all -> 0x0244 }
        throw r2;	 Catch:{ all -> 0x0244 }
    L_0x022b:
        r0 = move-exception;	 Catch:{ all -> 0x0244 }
        r6 = 1;	 Catch:{ all -> 0x0244 }
    L_0x022d:
        r2 = r0;	 Catch:{ all -> 0x0244 }
    L_0x022e:
        r3 = new java.sql.SQLException;	 Catch:{ all -> 0x0244 }
        r4 = "error.connection.badhost";	 Catch:{ all -> 0x0244 }
        r7 = r2.getMessage();	 Catch:{ all -> 0x0244 }
        r4 = net.sourceforge.jtds.jdbc.Messages.get(r4, r7);	 Catch:{ all -> 0x0244 }
        r7 = "08S03";	 Catch:{ all -> 0x0244 }
        r3.<init>(r4, r7);	 Catch:{ all -> 0x0244 }
        r2 = net.sourceforge.jtds.jdbc.Support.linkException(r3, r2);	 Catch:{ all -> 0x0244 }
        throw r2;	 Catch:{ all -> 0x0244 }
    L_0x0244:
        r0 = move-exception;
        r2 = r0;
        r22 = 1;
    L_0x0248:
        if (r22 == 0) goto L_0x024e;
    L_0x024a:
        r24.close();
        goto L_0x0257;
    L_0x024e:
        if (r5 == 0) goto L_0x0257;
    L_0x0250:
        r3 = net.sourceforge.jtds.util.TimerThread.getInstance();
        r3.cancelTimer(r5);
    L_0x0257:
        throw r2;
    L_0x0258:
        r0 = move-exception;
        r2 = r0;
        monitor-exit(r5);	 Catch:{ all -> 0x0258 }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ConnectionJDBC2.<init>(java.lang.String, java.util.Properties):void");
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private net.sourceforge.jtds.jdbc.SharedSocket createNamedPipe(net.sourceforge.jtds.jdbc.ConnectionJDBC2 r14) throws java.io.IOException {
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
        r13 = this;
        r0 = r14.getLoginTimeout();
        r0 = (long) r0;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 <= 0) goto L_0x000c;
    L_0x000b:
        goto L_0x000e;
    L_0x000c:
        r0 = 20;
    L_0x000e:
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r0 * r2;
        r2 = java.lang.System.currentTimeMillis();
        r4 = new java.util.Random;
        r4.<init>(r2);
        r5 = net.sourceforge.jtds.jdbc.Support.isWindowsOS();
        r6 = 0;
        r7 = 0;
        r8 = r7;
    L_0x0022:
        if (r5 == 0) goto L_0x0032;
    L_0x0024:
        r9 = r14.getUseJCIFS();	 Catch:{ IOException -> 0x0030 }
        if (r9 != 0) goto L_0x0032;	 Catch:{ IOException -> 0x0030 }
    L_0x002a:
        r9 = new net.sourceforge.jtds.jdbc.SharedLocalNamedPipe;	 Catch:{ IOException -> 0x0030 }
        r9.<init>(r14);	 Catch:{ IOException -> 0x0030 }
        goto L_0x0037;	 Catch:{ IOException -> 0x0030 }
    L_0x0030:
        r8 = move-exception;	 Catch:{ IOException -> 0x0030 }
        goto L_0x0039;	 Catch:{ IOException -> 0x0030 }
    L_0x0032:
        r9 = new net.sourceforge.jtds.jdbc.SharedNamedPipe;	 Catch:{ IOException -> 0x0030 }
        r9.<init>(r14);	 Catch:{ IOException -> 0x0030 }
    L_0x0037:
        r7 = r9;
        goto L_0x0085;
    L_0x0039:
        r6 = r6 + 1;
        r9 = r8.getMessage();
        r9 = r9.toLowerCase();
        r10 = "all pipe instances are busy";
        r9 = r9.indexOf(r10);
        if (r9 < 0) goto L_0x009f;
    L_0x004b:
        r9 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        r9 = r4.nextInt(r9);
        r9 = r9 + 200;
        r10 = net.sourceforge.jtds.util.Logger.isActive();
        if (r10 == 0) goto L_0x0081;
    L_0x0059:
        r10 = new java.lang.StringBuffer;
        r10.<init>();
        r11 = "Retry #";
        r10.append(r11);
        r10.append(r6);
        r11 = " Wait ";
        r10.append(r11);
        r10.append(r9);
        r11 = " ms: ";
        r10.append(r11);
        r11 = r8.getMessage();
        r10.append(r11);
        r10 = r10.toString();
        net.sourceforge.jtds.util.Logger.println(r10);
    L_0x0081:
        r9 = (long) r9;
        java.lang.Thread.sleep(r9);	 Catch:{ InterruptedException -> 0x0085 }
    L_0x0085:
        if (r7 != 0) goto L_0x0091;
    L_0x0087:
        r9 = java.lang.System.currentTimeMillis();
        r11 = r9 - r2;
        r9 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1));
        if (r9 < 0) goto L_0x0022;
    L_0x0091:
        if (r7 != 0) goto L_0x009e;
    L_0x0093:
        r14 = new java.io.IOException;
        r0 = "Connection timed out to named pipe";
        r14.<init>(r0);
        net.sourceforge.jtds.jdbc.Support.linkException(r14, r8);
        throw r14;
    L_0x009e:
        return r7;
    L_0x009f:
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ConnectionJDBC2.createNamedPipe(net.sourceforge.jtds.jdbc.ConnectionJDBC2):net.sourceforge.jtds.jdbc.SharedSocket");
    }

    SharedSocket getSocket() {
        return this.socket;
    }

    int getTdsVersion() {
        return this.tdsVersion;
    }

    String getProcName() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("000000");
        int i = this.spSequenceNo;
        this.spSequenceNo = i + 1;
        stringBuffer.append(Integer.toHexString(i).toUpperCase());
        String stringBuffer2 = stringBuffer.toString();
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append("#jtds");
        stringBuffer3.append(stringBuffer2.substring(stringBuffer2.length() - 6, stringBuffer2.length()));
        return stringBuffer3.toString();
    }

    synchronized String getCursorName() {
        StringBuffer stringBuffer;
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("000000");
        int i = this.cursorSequenceNo;
        this.cursorSequenceNo = i + 1;
        stringBuffer2.append(Integer.toHexString(i).toUpperCase());
        String stringBuffer3 = stringBuffer2.toString();
        stringBuffer = new StringBuffer();
        stringBuffer.append("_jtds");
        stringBuffer.append(stringBuffer3.substring(stringBuffer3.length() - 6, stringBuffer3.length()));
        return stringBuffer.toString();
    }

    void addCachedProcedure(String str, ProcEntry procEntry) {
        this.statementCache.put(str, procEntry);
        if (!this.autoCommit && procEntry.getType() == 1 && this.serverType == 1) {
            this.procInTran.add(str);
        }
    }

    void removeCachedProcedure(String str) {
        this.statementCache.remove(str);
        if (!this.autoCommit) {
            this.procInTran.remove(str);
        }
    }

    int getMaxStatements() {
        return this.maxStatements;
    }

    public int getServerType() {
        return this.serverType;
    }

    void setNetPacketSize(int i) {
        this.netPacketSize = i;
    }

    int getNetPacketSize() {
        return this.netPacketSize;
    }

    int getRowCount() {
        return this.rowCount;
    }

    void setRowCount(int i) {
        this.rowCount = i;
    }

    public int getTextSize() {
        return this.textSize;
    }

    public void setTextSize(int i) {
        this.textSize = i;
    }

    boolean getLastUpdateCount() {
        return this.lastUpdateCount;
    }

    int getMaxPrecision() {
        return this.maxPrecision;
    }

    long getLobBuffer() {
        return this.lobBuffer;
    }

    int getPrepareSql() {
        return this.prepareSql;
    }

    int getBatchSize() {
        return this.batchSize;
    }

    boolean getUseMetadataCache() {
        return this.useMetadataCache;
    }

    boolean getUseCursors() {
        return this.useCursors;
    }

    boolean getUseLOBs() {
        return this.useLOBs;
    }

    boolean getUseNTLMv2() {
        return this.useNTLMv2;
    }

    String getAppName() {
        return this.appName;
    }

    String getBindAddress() {
        return this.bindAddress;
    }

    File getBufferDir() {
        return this.bufferDir;
    }

    int getBufferMaxMemory() {
        return this.bufferMaxMemory;
    }

    int getBufferMinPackets() {
        return this.bufferMinPackets;
    }

    String getDatabaseName() {
        return this.databaseName;
    }

    String getDomainName() {
        return this.domainName;
    }

    String getInstanceName() {
        return this.instanceName;
    }

    int getLoginTimeout() {
        return this.loginTimeout;
    }

    int getSocketTimeout() {
        return this.socketTimeout;
    }

    boolean getSocketKeepAlive() {
        return this.socketKeepAlive;
    }

    int getProcessId() {
        return processId.intValue();
    }

    String getMacAddress() {
        return this.macAddress;
    }

    boolean getNamedPipe() {
        return this.namedPipe;
    }

    int getPacketSize() {
        return this.packetSize;
    }

    String getPassword() {
        return this.password;
    }

    int getPortNumber() {
        return this.portNumber;
    }

    String getProgName() {
        return this.progName;
    }

    String getServerName() {
        return this.serverName;
    }

    boolean getTcpNoDelay() {
        return this.tcpNoDelay;
    }

    boolean getUseJCIFS() {
        return this.useJCIFS;
    }

    String getUser() {
        return this.user;
    }

    String getWsid() {
        return this.wsid;
    }

    protected void unpackProperties(Properties properties) throws SQLException {
        this.serverName = properties.getProperty(Messages.get(Driver.SERVERNAME));
        this.portNumber = parseIntegerProperty(properties, Driver.PORTNUMBER);
        this.serverType = parseIntegerProperty(properties, Driver.SERVERTYPE);
        this.databaseName = properties.getProperty(Messages.get(Driver.DATABASENAME));
        this.instanceName = properties.getProperty(Messages.get(Driver.INSTANCE));
        this.domainName = properties.getProperty(Messages.get(Driver.DOMAIN));
        this.user = properties.getProperty(Messages.get(Driver.USER));
        this.password = properties.getProperty(Messages.get(Driver.PASSWORD));
        this.macAddress = properties.getProperty(Messages.get(Driver.MACADDRESS));
        this.appName = properties.getProperty(Messages.get(Driver.APPNAME));
        this.progName = properties.getProperty(Messages.get(Driver.PROGNAME));
        this.wsid = properties.getProperty(Messages.get(Driver.WSID));
        this.serverCharset = properties.getProperty(Messages.get(Driver.CHARSET));
        this.language = properties.getProperty(Messages.get(Driver.LANGUAGE));
        this.bindAddress = properties.getProperty(Messages.get(Driver.BINDADDRESS));
        this.lastUpdateCount = parseBooleanProperty(properties, Driver.LASTUPDATECOUNT);
        this.useUnicode = parseBooleanProperty(properties, Driver.SENDSTRINGPARAMETERSASUNICODE);
        this.namedPipe = parseBooleanProperty(properties, Driver.NAMEDPIPE);
        this.tcpNoDelay = parseBooleanProperty(properties, Driver.TCPNODELAY);
        boolean z = this.serverType == 1 && parseBooleanProperty(properties, Driver.USECURSORS);
        this.useCursors = z;
        this.useLOBs = parseBooleanProperty(properties, Driver.USELOBS);
        this.useMetadataCache = parseBooleanProperty(properties, Driver.CACHEMETA);
        this.xaEmulation = parseBooleanProperty(properties, Driver.XAEMULATION);
        this.useJCIFS = parseBooleanProperty(properties, Driver.USEJCIFS);
        this.charsetSpecified = this.serverCharset.length() > 0;
        this.useNTLMv2 = parseBooleanProperty(properties, Driver.USENTLMV2);
        if (this.domainName != null) {
            this.domainName = this.domainName.toUpperCase();
        }
        Integer tdsVersion = DefaultProperties.getTdsVersion(properties.getProperty(Messages.get(Driver.TDS)));
        if (tdsVersion == null) {
            throw new SQLException(Messages.get("error.connection.badprop", Messages.get(Driver.TDS)), "08001");
        }
        this.tdsVersion = tdsVersion.intValue();
        this.packetSize = parseIntegerProperty(properties, Driver.PACKETSIZE);
        if (this.packetSize < 512) {
            if (this.tdsVersion >= 3) {
                this.packetSize = this.packetSize == 0 ? 0 : 4096;
            } else if (this.tdsVersion == 1) {
                this.packetSize = 512;
            }
        }
        if (this.packetSize > 32768) {
            this.packetSize = 32768;
        }
        this.packetSize = (this.packetSize / 512) * 512;
        this.loginTimeout = parseIntegerProperty(properties, Driver.LOGINTIMEOUT);
        this.socketTimeout = parseIntegerProperty(properties, Driver.SOTIMEOUT);
        this.socketKeepAlive = parseBooleanProperty(properties, Driver.SOKEEPALIVE);
        this.autoCommit = parseBooleanProperty(properties, Driver.AUTOCOMMIT);
        String property = properties.getProperty(Messages.get(Driver.PROCESSID));
        if ("compute".equals(property)) {
            if (processId == null) {
                processId = new Integer(new Random(System.currentTimeMillis()).nextInt(32768));
            }
        } else if (property.length() > 0) {
            processId = new Integer(parseIntegerProperty(properties, Driver.PROCESSID));
        }
        this.lobBuffer = parseLongProperty(properties, Driver.LOBBUFFER);
        this.maxStatements = parseIntegerProperty(properties, Driver.MAXSTATEMENTS);
        this.statementCache = new ProcedureCache(this.maxStatements);
        this.prepareSql = parseIntegerProperty(properties, Driver.PREPARESQL);
        if (this.prepareSql < 0) {
            this.prepareSql = 0;
        } else if (this.prepareSql > 3) {
            this.prepareSql = 3;
        }
        if (this.tdsVersion < 3 && this.prepareSql == 3) {
            this.prepareSql = 2;
        }
        if (this.tdsVersion < 2 && this.prepareSql == 2) {
            this.prepareSql = 1;
        }
        this.ssl = properties.getProperty(Messages.get(Driver.SSL));
        this.batchSize = parseIntegerProperty(properties, Driver.BATCHSIZE);
        if (this.batchSize < 0) {
            throw new SQLException(Messages.get("error.connection.badprop", Messages.get(Driver.BATCHSIZE)), "08001");
        }
        this.bufferDir = new File(properties.getProperty(Messages.get(Driver.BUFFERDIR)));
        if (this.bufferDir.isDirectory() || this.bufferDir.mkdirs()) {
            this.bufferMaxMemory = parseIntegerProperty(properties, Driver.BUFFERMAXMEMORY);
            if (this.bufferMaxMemory < 0) {
                throw new SQLException(Messages.get("error.connection.badprop", Messages.get(Driver.BUFFERMAXMEMORY)), "08001");
            }
            this.bufferMinPackets = parseIntegerProperty(properties, Driver.BUFFERMINPACKETS);
            if (this.bufferMinPackets < 1) {
                throw new SQLException(Messages.get("error.connection.badprop", Messages.get(Driver.BUFFERMINPACKETS)), "08001");
            }
            return;
        }
        throw new SQLException(Messages.get("error.connection.badprop", Messages.get(Driver.BUFFERDIR)), "08001");
    }

    private static boolean parseBooleanProperty(Properties properties, String str) throws SQLException {
        Object obj = Messages.get(str);
        properties = properties.getProperty(obj);
        if (properties == null || "true".equalsIgnoreCase(properties) || "false".equalsIgnoreCase(properties)) {
            return "true".equalsIgnoreCase(properties);
        }
        throw new SQLException(Messages.get("error.connection.badprop", obj), "08001");
    }

    private static int parseIntegerProperty(java.util.Properties r1, java.lang.String r2) throws java.sql.SQLException {
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
        r2 = net.sourceforge.jtds.jdbc.Messages.get(r2);
        r1 = r1.getProperty(r2);	 Catch:{ NumberFormatException -> 0x000d }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ NumberFormatException -> 0x000d }
        return r1;
    L_0x000d:
        r1 = new java.sql.SQLException;
        r0 = "error.connection.badprop";
        r2 = net.sourceforge.jtds.jdbc.Messages.get(r0, r2);
        r0 = "08001";
        r1.<init>(r2, r0);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ConnectionJDBC2.parseIntegerProperty(java.util.Properties, java.lang.String):int");
    }

    private static long parseLongProperty(java.util.Properties r2, java.lang.String r3) throws java.sql.SQLException {
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
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r3);
        r2 = r2.getProperty(r3);	 Catch:{ NumberFormatException -> 0x000d }
        r0 = java.lang.Long.parseLong(r2);	 Catch:{ NumberFormatException -> 0x000d }
        return r0;
    L_0x000d:
        r2 = new java.sql.SQLException;
        r0 = "error.connection.badprop";
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r0, r3);
        r0 = "08001";
        r2.<init>(r3, r0);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ConnectionJDBC2.parseLongProperty(java.util.Properties, java.lang.String):long");
    }

    protected String getCharset() {
        return this.charsetInfo.getCharset();
    }

    protected boolean isWideChar() {
        return this.charsetInfo.isWideChars();
    }

    protected CharsetInfo getCharsetInfo() {
        return this.charsetInfo;
    }

    protected boolean getUseUnicode() {
        return this.useUnicode;
    }

    protected boolean getSybaseInfo(int i) {
        return (i & this.sybaseInfo) != 0;
    }

    protected void setSybaseInfo(int i) {
        this.sybaseInfo = i;
    }

    protected void setServerCharset(String str) throws SQLException {
        if (this.charsetSpecified) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Server charset ");
            stringBuffer.append(str);
            stringBuffer.append(". Ignoring as user requested ");
            stringBuffer.append(this.serverCharset);
            stringBuffer.append('.');
            Logger.println(stringBuffer.toString());
            return;
        }
        if (!str.equals(this.serverCharset)) {
            loadCharset(str);
            if (Logger.isActive() != null) {
                str = new StringBuffer();
                str.append("Set charset to ");
                str.append(this.serverCharset);
                str.append('/');
                str.append(this.charsetInfo);
                Logger.println(str.toString());
            }
        }
    }

    private void loadCharset(String str) throws SQLException {
        Object obj;
        if (getServerType() == 1 && str.equalsIgnoreCase("iso_1")) {
            obj = "Cp1252";
        }
        CharsetInfo charset = CharsetInfo.getCharset((String) obj);
        if (charset == null) {
            throw new SQLException(Messages.get("error.charset.nomapping", obj), "2C000");
        }
        loadCharset(charset, obj);
        this.serverCharset = obj;
    }

    private void loadCharset(net.sourceforge.jtds.jdbc.CharsetInfo r3, java.lang.String r4) throws java.sql.SQLException {
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
        r0 = "This is a test";	 Catch:{ UnsupportedEncodingException -> 0x0013 }
        r1 = r3.getCharset();	 Catch:{ UnsupportedEncodingException -> 0x0013 }
        r0.getBytes(r1);	 Catch:{ UnsupportedEncodingException -> 0x0013 }
        r2.charsetInfo = r3;	 Catch:{ UnsupportedEncodingException -> 0x0013 }
        r3 = r2.socket;
        r4 = r2.charsetInfo;
        r3.setCharsetInfo(r4);
        return;
    L_0x0013:
        r0 = new java.sql.SQLException;
        r1 = "error.charset.invalid";
        r3 = r3.getCharset();
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r1, r4, r3);
        r4 = "2C000";
        r0.<init>(r3, r4);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ConnectionJDBC2.loadCharset(net.sourceforge.jtds.jdbc.CharsetInfo, java.lang.String):void");
    }

    private String determineServerCharset() throws SQLException {
        String str;
        switch (this.serverType) {
            case 1:
                if (this.databaseProductVersion.indexOf("6.5") >= 0) {
                    str = SQL_SERVER_65_CHARSET_QUERY;
                    break;
                }
                throw new SQLException("Please use TDS protocol version 7.0 or higher");
            case 2:
                str = SYBASE_SERVER_CHARSET_QUERY;
                break;
            default:
                str = null;
                break;
        }
        Statement createStatement = createStatement();
        ResultSet executeQuery = createStatement.executeQuery(str);
        executeQuery.next();
        String string = executeQuery.getString(1);
        executeQuery.close();
        createStatement.close();
        return string;
    }

    void setCollation(byte[] bArr) throws SQLException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("0x");
        stringBuffer.append(Support.toHex(bArr));
        String stringBuffer2 = stringBuffer.toString();
        if (this.charsetSpecified) {
            bArr = new StringBuffer();
            bArr.append("Server collation ");
            bArr.append(stringBuffer2);
            bArr.append(". Ignoring as user requested ");
            bArr.append(this.serverCharset);
            bArr.append('.');
            Logger.println(bArr.toString());
            return;
        }
        loadCharset(CharsetInfo.getCharset(bArr), stringBuffer2);
        this.collation = bArr;
        if (Logger.isActive() != null) {
            bArr = new StringBuffer();
            bArr.append("Set collation to ");
            bArr.append(stringBuffer2);
            bArr.append('/');
            bArr.append(this.charsetInfo);
            Logger.println(bArr.toString());
        }
    }

    byte[] getCollation() {
        return this.collation;
    }

    boolean isCharsetSpecified() {
        return this.charsetSpecified;
    }

    protected void setDatabase(String str, String str2) throws SQLException {
        if (this.currentDatabase == null || str2.equalsIgnoreCase(this.currentDatabase)) {
            this.currentDatabase = str;
            if (Logger.isActive()) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Changed database from ");
                stringBuffer.append(str2);
                stringBuffer.append(" to ");
                stringBuffer.append(str);
                Logger.println(stringBuffer.toString());
                return;
            }
            return;
        }
        throw new SQLException(Messages.get("error.connection.dbmismatch", str2, this.databaseName), "HY096");
    }

    protected void setDBServerInfo(String str, int i, int i2, int i3) {
        this.databaseProductName = str;
        this.databaseMajorVersion = i;
        this.databaseMinorVersion = i2;
        if (this.tdsVersion >= 3) {
            str = new StringBuffer(10);
            if (i < 10) {
                str.append('0');
            }
            str.append(i);
            str.append('.');
            if (i2 < 10) {
                str.append('0');
            }
            str.append(i2);
            str.append('.');
            str.append(i3);
            while (str.length() < 10) {
                str.insert(6, '0');
            }
            this.databaseProductVersion = str.toString();
            return;
        }
        str = new StringBuffer();
        str.append(i);
        str.append(".");
        str.append(i2);
        this.databaseProductVersion = str.toString();
    }

    synchronized void removeStatement(JtdsStatement jtdsStatement) throws SQLException {
        synchronized (this.statements) {
            for (int i = 0; i < this.statements.size(); i++) {
                WeakReference weakReference = (WeakReference) this.statements.get(i);
                if (weakReference != null) {
                    Object obj = (Statement) weakReference.get();
                    if (obj == null || obj == jtdsStatement) {
                        this.statements.set(i, null);
                    }
                }
            }
        }
        if (jtdsStatement instanceof JtdsPreparedStatement) {
            JtdsStatement<ProcEntry> obsoleteHandles = this.statementCache.getObsoleteHandles(((JtdsPreparedStatement) jtdsStatement).handles);
            if (obsoleteHandles != null) {
                if (this.serverType == 1) {
                    StringBuffer stringBuffer = new StringBuffer(obsoleteHandles.size() * 32);
                    for (ProcEntry appendDropSQL : obsoleteHandles) {
                        appendDropSQL.appendDropSQL(stringBuffer);
                    }
                    if (stringBuffer.length() > null) {
                        this.baseTds.executeSQL(stringBuffer.toString(), null, null, true, 0, -1, -1, true);
                        this.baseTds.clearResponseQueue();
                    }
                } else {
                    for (ProcEntry procEntry : obsoleteHandles) {
                        if (procEntry.toString() != null) {
                            this.baseTds.sybaseUnPrepare(procEntry.toString());
                        }
                    }
                }
            }
        }
    }

    void addStatement(JtdsStatement jtdsStatement) {
        synchronized (this.statements) {
            int i = 0;
            while (i < this.statements.size()) {
                WeakReference weakReference = (WeakReference) this.statements.get(i);
                if (weakReference != null) {
                    if (weakReference.get() != null) {
                        i++;
                    }
                }
                this.statements.set(i, new WeakReference(jtdsStatement));
                return;
            }
            this.statements.add(new WeakReference(jtdsStatement));
        }
    }

    void checkOpen() throws SQLException {
        if (this.closed) {
            throw new SQLException(Messages.get("error.generic.closed", (Object) "Connection"), "HY010");
        }
    }

    void checkLocal(String str) throws SQLException {
        if (this.xaTransaction) {
            throw new SQLException(Messages.get("error.connection.badxaop", (Object) str), "HY010");
        }
    }

    static void notImplemented(String str) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", (Object) str), "HYC00");
    }

    public int getDatabaseMajorVersion() {
        return this.databaseMajorVersion;
    }

    public int getDatabaseMinorVersion() {
        return this.databaseMinorVersion;
    }

    String getDatabaseProductName() {
        return this.databaseProductName;
    }

    String getDatabaseProductVersion() {
        return this.databaseProductVersion;
    }

    String getURL() {
        return this.url;
    }

    public String getRmHost() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.serverName);
        stringBuffer.append(':');
        stringBuffer.append(this.portNumber);
        return stringBuffer.toString();
    }

    void setClosed() {
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
        r1 = this;
        r0 = r1.closed;
        if (r0 != 0) goto L_0x000c;
    L_0x0004:
        r0 = 1;
        r1.closed = r0;
        r0 = r1.socket;	 Catch:{ IOException -> 0x000c }
        r0.close();	 Catch:{ IOException -> 0x000c }
    L_0x000c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ConnectionJDBC2.setClosed():void");
    }

    synchronized byte[][] sendXaPacket(int[] iArr, byte[] bArr) throws SQLException {
        r0 = new ParamInfo[6];
        int i = 0;
        r0[0] = new ParamInfo(4, null, 2);
        r0[1] = new ParamInfo(4, new Integer(iArr[1]), 0);
        r0[2] = new ParamInfo(4, new Integer(iArr[2]), 0);
        r0[3] = new ParamInfo(4, new Integer(iArr[3]), 0);
        r0[4] = new ParamInfo(4, new Integer(iArr[4]), 0);
        r0[5] = new ParamInfo(-3, bArr, 1);
        this.baseTds.executeSQL(null, "master..xp_jtdsxa", r0, false, 0, -1, -1, true);
        ArrayList arrayList = new ArrayList();
        while (!this.baseTds.isEndOfResponse()) {
            if (this.baseTds.getMoreResults()) {
                while (this.baseTds.getNextRow()) {
                    Object[] rowData = this.baseTds.getRowData();
                    if (rowData.length == 1 && (rowData[0] instanceof byte[])) {
                        arrayList.add(rowData[0]);
                    }
                }
            }
        }
        this.messages.checkErrors();
        if (r0[0].getOutValue() instanceof Integer) {
            iArr[0] = ((Integer) r0[0].getOutValue()).intValue();
        } else {
            iArr[0] = -7;
        }
        if (arrayList.size() > null) {
            iArr = new byte[arrayList.size()][];
            while (i < arrayList.size()) {
                iArr[i] = (byte[]) arrayList.get(i);
                i++;
            }
            return iArr;
        } else if ((r0[5].getOutValue() instanceof byte[]) != null) {
            return new byte[][]{(byte[]) r0[5].getOutValue()};
        } else {
            return (byte[][]) null;
        }
    }

    synchronized void enlistConnection(byte[] bArr) throws SQLException {
        if (bArr != null) {
            this.prepareSql = 2;
            this.baseTds.enlistConnection(1, bArr);
            this.xaTransaction = true;
        } else {
            this.baseTds.enlistConnection(1, null);
            this.xaTransaction = null;
        }
    }

    void setXid(Object obj) {
        this.xid = obj;
        this.xaTransaction = obj != null ? true : null;
    }

    Object getXid() {
        return this.xid;
    }

    void setXaState(int i) {
        this.xaState = i;
    }

    int getXaState() {
        return this.xaState;
    }

    boolean isXaEmulation() {
        return this.xaEmulation;
    }

    net.sourceforge.jtds.jdbc.Semaphore getMutex() {
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
        r0 = java.lang.Thread.interrupted();
        r1 = r2.mutex;	 Catch:{ InterruptedException -> 0x0015 }
        r1.acquire();	 Catch:{ InterruptedException -> 0x0015 }
        if (r0 == 0) goto L_0x0012;
    L_0x000b:
        r0 = java.lang.Thread.currentThread();
        r0.interrupt();
    L_0x0012:
        r0 = r2.mutex;
        return r0;
    L_0x0015:
        r0 = new java.lang.IllegalStateException;
        r1 = "Thread execution interrupted";
        r0.<init>(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ConnectionJDBC2.getMutex():net.sourceforge.jtds.jdbc.Semaphore");
    }

    synchronized void releaseTds(TdsCore tdsCore) throws SQLException {
        if (this.cachedTds != null) {
            tdsCore.close();
        } else {
            tdsCore.clearResponseQueue();
            tdsCore.cleanUp();
            this.cachedTds = tdsCore;
        }
    }

    synchronized TdsCore getCachedTds() {
        TdsCore tdsCore;
        tdsCore = this.cachedTds;
        this.cachedTds = null;
        return tdsCore;
    }

    public int getHoldability() throws SQLException {
        checkOpen();
        return 1;
    }

    public synchronized int getTransactionIsolation() throws SQLException {
        checkOpen();
        return this.transactionIsolation;
    }

    public synchronized void clearWarnings() throws SQLException {
        checkOpen();
        this.messages.clearWarnings();
    }

    public synchronized void close() throws java.sql.SQLException {
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
        r6 = this;
        monitor-enter(r6);
        r0 = r6.closed;	 Catch:{ all -> 0x009f }
        if (r0 != 0) goto L_0x009d;
    L_0x0005:
        r0 = 0;
        r1 = 1;
        r2 = r6.statements;	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
        monitor-enter(r2);	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
        r3 = new java.util.ArrayList;	 Catch:{ all -> 0x006d }
        r4 = r6.statements;	 Catch:{ all -> 0x006d }
        r3.<init>(r4);	 Catch:{ all -> 0x006d }
        r4 = r6.statements;	 Catch:{ all -> 0x006d }
        r4.clear();	 Catch:{ all -> 0x006d }
        monitor-exit(r2);	 Catch:{ all -> 0x006d }
        r2 = 0;
    L_0x0018:
        r4 = r3.size();	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
        if (r2 >= r4) goto L_0x0034;	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
    L_0x001e:
        r4 = r3.get(r2);	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
        r4 = (java.lang.ref.WeakReference) r4;	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
        if (r4 == 0) goto L_0x0031;	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
    L_0x0026:
        r4 = r4.get();	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
        r4 = (java.sql.Statement) r4;	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
        if (r4 == 0) goto L_0x0031;
    L_0x002e:
        r4.close();	 Catch:{ SQLException -> 0x0031 }
    L_0x0031:
        r2 = r2 + 1;
        goto L_0x0018;
    L_0x0034:
        r2 = r6.baseTds;	 Catch:{ SQLException -> 0x004e }
        if (r2 == 0) goto L_0x0042;	 Catch:{ SQLException -> 0x004e }
    L_0x0038:
        r2 = r6.baseTds;	 Catch:{ SQLException -> 0x004e }
        r2.closeConnection();	 Catch:{ SQLException -> 0x004e }
        r2 = r6.baseTds;	 Catch:{ SQLException -> 0x004e }
        r2.close();	 Catch:{ SQLException -> 0x004e }
    L_0x0042:
        r2 = r6.cachedTds;	 Catch:{ SQLException -> 0x004e }
        if (r2 == 0) goto L_0x004e;	 Catch:{ SQLException -> 0x004e }
    L_0x0046:
        r2 = r6.cachedTds;	 Catch:{ SQLException -> 0x004e }
        r2.close();	 Catch:{ SQLException -> 0x004e }
        r2 = 0;	 Catch:{ SQLException -> 0x004e }
        r6.cachedTds = r2;	 Catch:{ SQLException -> 0x004e }
    L_0x004e:
        r2 = r6.socket;	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
        if (r2 == 0) goto L_0x0057;	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
    L_0x0052:
        r2 = r6.socket;	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
        r2.close();	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
    L_0x0057:
        r6.closed = r1;	 Catch:{ all -> 0x009f }
        r2 = connections;	 Catch:{ all -> 0x009f }
        monitor-enter(r2);	 Catch:{ all -> 0x009f }
        r3 = connections;	 Catch:{ all -> 0x006a }
        r4 = r3[r0];	 Catch:{ all -> 0x006a }
        r4 = r4 - r1;	 Catch:{ all -> 0x006a }
        r3[r0] = r4;	 Catch:{ all -> 0x006a }
        if (r4 != 0) goto L_0x0068;	 Catch:{ all -> 0x006a }
    L_0x0065:
        net.sourceforge.jtds.util.TimerThread.stopTimer();	 Catch:{ all -> 0x006a }
    L_0x0068:
        monitor-exit(r2);	 Catch:{ all -> 0x006a }
        goto L_0x009d;	 Catch:{ all -> 0x006a }
    L_0x006a:
        r0 = move-exception;	 Catch:{ all -> 0x006a }
        monitor-exit(r2);	 Catch:{ all -> 0x006a }
        throw r0;	 Catch:{ all -> 0x009f }
    L_0x006d:
        r3 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x006d }
        throw r3;	 Catch:{ IOException -> 0x0087, all -> 0x0070 }
    L_0x0070:
        r2 = move-exception;
        r6.closed = r1;	 Catch:{ all -> 0x009f }
        r3 = connections;	 Catch:{ all -> 0x009f }
        monitor-enter(r3);	 Catch:{ all -> 0x009f }
        r4 = connections;	 Catch:{ all -> 0x0084 }
        r5 = r4[r0];	 Catch:{ all -> 0x0084 }
        r5 = r5 - r1;	 Catch:{ all -> 0x0084 }
        r4[r0] = r5;	 Catch:{ all -> 0x0084 }
        if (r5 != 0) goto L_0x0082;	 Catch:{ all -> 0x0084 }
    L_0x007f:
        net.sourceforge.jtds.util.TimerThread.stopTimer();	 Catch:{ all -> 0x0084 }
    L_0x0082:
        monitor-exit(r3);	 Catch:{ all -> 0x0084 }
        throw r2;	 Catch:{ all -> 0x009f }
    L_0x0084:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0084 }
        throw r0;	 Catch:{ all -> 0x009f }
    L_0x0087:
        r6.closed = r1;	 Catch:{ all -> 0x009f }
        r2 = connections;	 Catch:{ all -> 0x009f }
        monitor-enter(r2);	 Catch:{ all -> 0x009f }
        r3 = connections;	 Catch:{ all -> 0x009a }
        r4 = r3[r0];	 Catch:{ all -> 0x009a }
        r4 = r4 - r1;	 Catch:{ all -> 0x009a }
        r3[r0] = r4;	 Catch:{ all -> 0x009a }
        if (r4 != 0) goto L_0x0098;	 Catch:{ all -> 0x009a }
    L_0x0095:
        net.sourceforge.jtds.util.TimerThread.stopTimer();	 Catch:{ all -> 0x009a }
    L_0x0098:
        monitor-exit(r2);	 Catch:{ all -> 0x009a }
        goto L_0x009d;	 Catch:{ all -> 0x009a }
    L_0x009a:
        r0 = move-exception;	 Catch:{ all -> 0x009a }
        monitor-exit(r2);	 Catch:{ all -> 0x009a }
        throw r0;	 Catch:{ all -> 0x009f }
    L_0x009d:
        monitor-exit(r6);
        return;
    L_0x009f:
        r0 = move-exception;
        monitor-exit(r6);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ConnectionJDBC2.close():void");
    }

    public synchronized void commit() throws SQLException {
        checkOpen();
        checkLocal("commit");
        if (getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.autocommit", (Object) "commit"), "25000");
        }
        this.baseTds.submitSQL("IF @@TRANCOUNT > 0 COMMIT TRAN");
        this.procInTran.clear();
        clearSavepoints();
    }

    public synchronized void rollback() throws SQLException {
        checkOpen();
        checkLocal("rollback");
        if (getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.autocommit", (Object) "rollback"), "25000");
        }
        this.baseTds.submitSQL("IF @@TRANCOUNT > 0 ROLLBACK TRAN");
        for (int i = 0; i < this.procInTran.size(); i++) {
            String str = (String) this.procInTran.get(i);
            if (str != null) {
                this.statementCache.remove(str);
            }
        }
        this.procInTran.clear();
        clearSavepoints();
    }

    public synchronized boolean getAutoCommit() throws SQLException {
        checkOpen();
        return this.autoCommit;
    }

    public boolean isClosed() throws SQLException {
        return this.closed;
    }

    public boolean isReadOnly() throws SQLException {
        checkOpen();
        return this.readOnly;
    }

    public void setHoldability(int i) throws SQLException {
        checkOpen();
        switch (i) {
            case 1:
                return;
            case 2:
                throw new SQLException(Messages.get("error.generic.optvalue", "CLOSE_CURSORS_AT_COMMIT", "setHoldability"), "HY092");
            default:
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "holdability"), "HY092");
        }
    }

    public synchronized void setTransactionIsolation(int i) throws SQLException {
        checkOpen();
        if (this.transactionIsolation != i) {
            String str = "SET TRANSACTION ISOLATION LEVEL ";
            Object obj = this.serverType == 2 ? 1 : null;
            StringBuffer stringBuffer;
            if (i == 4) {
                stringBuffer = new StringBuffer();
                stringBuffer.append(str);
                stringBuffer.append(obj != null ? "2" : "REPEATABLE READ");
                str = stringBuffer.toString();
            } else if (i == 8) {
                stringBuffer = new StringBuffer();
                stringBuffer.append(str);
                stringBuffer.append(obj != null ? "3" : "SERIALIZABLE");
                str = stringBuffer.toString();
            } else if (i != 4096) {
                switch (i) {
                    case 0:
                        throw new SQLException(Messages.get("error.generic.optvalue", "TRANSACTION_NONE", "setTransactionIsolation"), "HY024");
                    case 1:
                        stringBuffer = new StringBuffer();
                        stringBuffer.append(str);
                        stringBuffer.append(obj != null ? "0" : "READ UNCOMMITTED");
                        str = stringBuffer.toString();
                        break;
                    case 2:
                        stringBuffer = new StringBuffer();
                        stringBuffer.append(str);
                        stringBuffer.append(obj != null ? "1" : "READ COMMITTED");
                        str = stringBuffer.toString();
                        break;
                    default:
                        throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "level"), "HY092");
                }
            } else if (obj != null) {
                throw new SQLException(Messages.get("error.generic.optvalue", "TRANSACTION_SNAPSHOT", "setTransactionIsolation"), "HY024");
            } else {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append(str);
                stringBuffer2.append("SNAPSHOT");
                str = stringBuffer2.toString();
            }
            this.transactionIsolation = i;
            this.baseTds.submitSQL(str);
        }
    }

    public synchronized void setAutoCommit(boolean z) throws SQLException {
        checkOpen();
        checkLocal("setAutoCommit");
        if (this.autoCommit != z) {
            StringBuffer stringBuffer = new StringBuffer(70);
            if (!this.autoCommit) {
                stringBuffer.append("IF @@TRANCOUNT > 0 COMMIT TRAN\r\n");
            }
            if (this.serverType == 2) {
                if (z) {
                    stringBuffer.append("SET CHAINED OFF");
                } else {
                    stringBuffer.append("SET CHAINED ON");
                }
            } else if (z) {
                stringBuffer.append("SET IMPLICIT_TRANSACTIONS OFF");
            } else {
                stringBuffer.append("SET IMPLICIT_TRANSACTIONS ON");
            }
            this.baseTds.submitSQL(stringBuffer.toString());
            this.autoCommit = z;
        }
    }

    public void setReadOnly(boolean z) throws SQLException {
        checkOpen();
        this.readOnly = z;
    }

    public synchronized String getCatalog() throws SQLException {
        checkOpen();
        return this.currentDatabase;
    }

    public synchronized void setCatalog(String str) throws SQLException {
        checkOpen();
        if (this.currentDatabase == null || !this.currentDatabase.equals(str)) {
            if (str.length() <= (this.tdsVersion >= 3 ? 128 : 30)) {
                if (str.length() >= 1) {
                    StringBuffer stringBuffer;
                    if (this.tdsVersion >= 3) {
                        stringBuffer = new StringBuffer();
                        stringBuffer.append("use [");
                        stringBuffer.append(str);
                        stringBuffer.append(']');
                    } else {
                        stringBuffer = new StringBuffer();
                        stringBuffer.append("use ");
                        stringBuffer.append(str);
                    }
                    this.baseTds.submitSQL(stringBuffer.toString());
                    return;
                }
            }
            throw new SQLException(Messages.get("error.generic.badparam", str, "catalog"), "3D000");
        }
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        checkOpen();
        return new JtdsDatabaseMetaData(this);
    }

    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return this.messages.getWarnings();
    }

    public Savepoint setSavepoint() throws SQLException {
        checkOpen();
        notImplemented("Connection.setSavepoint()");
        return null;
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkOpen();
        notImplemented("Connection.releaseSavepoint(Savepoint)");
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        checkOpen();
        notImplemented("Connection.rollback(Savepoint)");
    }

    public Statement createStatement() throws SQLException {
        checkOpen();
        return createStatement(PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR);
    }

    public synchronized Statement createStatement(int i, int i2) throws SQLException {
        Statement jtdsStatement;
        checkOpen();
        jtdsStatement = new JtdsStatement(this, i, i2);
        addStatement(jtdsStatement);
        return jtdsStatement;
    }

    public Statement createStatement(int i, int i2, int i3) throws SQLException {
        checkOpen();
        setHoldability(i3);
        return createStatement(i, i2);
    }

    public Map getTypeMap() throws SQLException {
        checkOpen();
        return new HashMap();
    }

    public void setTypeMap(Map map) throws SQLException {
        checkOpen();
        notImplemented("Connection.setTypeMap(Map)");
    }

    public String nativeSQL(String str) throws SQLException {
        checkOpen();
        if (str != null) {
            if (str.length() != 0) {
                return SQLParser.parse(str, new ArrayList(), this, false)[0];
            }
        }
        throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
    }

    public CallableStatement prepareCall(String str) throws SQLException {
        checkOpen();
        return prepareCall(str, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR);
    }

    public synchronized CallableStatement prepareCall(String str, int i, int i2) throws SQLException {
        Object jtdsCallableStatement;
        checkOpen();
        if (str != null) {
            if (str.length() != 0) {
                jtdsCallableStatement = new JtdsCallableStatement(this, str, i, i2);
                addStatement(jtdsCallableStatement);
            }
        }
        throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        return jtdsCallableStatement;
    }

    public CallableStatement prepareCall(String str, int i, int i2, int i3) throws SQLException {
        checkOpen();
        setHoldability(i3);
        return prepareCall(str, i, i2);
    }

    public PreparedStatement prepareStatement(String str) throws SQLException {
        checkOpen();
        return prepareStatement(str, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR);
    }

    public PreparedStatement prepareStatement(String str, int i) throws SQLException {
        checkOpen();
        if (str != null) {
            if (str.length() != 0) {
                if (i == 1 || i == 2) {
                    JtdsPreparedStatement jtdsPreparedStatement = new JtdsPreparedStatement(this, str, PointerIconCompat.TYPE_HELP, PointerIconCompat.TYPE_CROSSHAIR, i == 1);
                    addStatement(jtdsPreparedStatement);
                    return jtdsPreparedStatement;
                }
                throw new SQLException(Messages.get("error.generic.badoption", Integer.toString(i), "autoGeneratedKeys"), "HY092");
            }
        }
        throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
    }

    public synchronized PreparedStatement prepareStatement(String str, int i, int i2) throws SQLException {
        JtdsPreparedStatement jtdsPreparedStatement;
        checkOpen();
        if (str != null) {
            if (str.length() != 0) {
                jtdsPreparedStatement = new JtdsPreparedStatement(this, str, i, i2, false);
                addStatement(jtdsPreparedStatement);
            }
        }
        throw new SQLException(Messages.get("error.generic.nosql"), "HY000");
        return jtdsPreparedStatement;
    }

    public PreparedStatement prepareStatement(String str, int i, int i2, int i3) throws SQLException {
        checkOpen();
        setHoldability(i3);
        return prepareStatement(str, i, i2);
    }

    public PreparedStatement prepareStatement(String str, int[] iArr) throws SQLException {
        if (iArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) "prepareStatement"), "HY092");
        } else if (iArr.length == 1) {
            return prepareStatement(str, 1);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolindex", (Object) "prepareStatement"), "HY092");
        }
    }

    public Savepoint setSavepoint(String str) throws SQLException {
        checkOpen();
        notImplemented("Connection.setSavepoint(String)");
        return null;
    }

    public PreparedStatement prepareStatement(String str, String[] strArr) throws SQLException {
        if (strArr == null) {
            throw new SQLException(Messages.get("error.generic.nullparam", (Object) "prepareStatement"), "HY092");
        } else if (strArr.length == 1) {
            return prepareStatement(str, 1);
        } else {
            throw new SQLException(Messages.get("error.generic.needcolname", (Object) "prepareStatement"), "HY092");
        }
    }
}
