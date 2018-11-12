package net.sourceforge.jtds.jdbc;

import android.support.v4.view.PointerIconCompat;
import java.math.BigDecimal;
import java.sql.SQLException;

public class MSCursorResultSet extends JtdsResultSet {
    private static final int CURSOR_CONCUR_OPTIMISTIC = 4;
    private static final int CURSOR_CONCUR_OPTIMISTIC_VALUES = 8;
    private static final int CURSOR_CONCUR_READ_ONLY = 1;
    private static final int CURSOR_CONCUR_SCROLL_LOCKS = 2;
    private static final Integer CURSOR_OP_DELETE = new Integer(34);
    private static final Integer CURSOR_OP_INSERT = new Integer(4);
    private static final Integer CURSOR_OP_UPDATE = new Integer(33);
    private static final int CURSOR_TYPE_AUTO_FETCH = 8192;
    private static final int CURSOR_TYPE_DYNAMIC = 2;
    private static final int CURSOR_TYPE_FASTFORWARDONLY = 16;
    private static final int CURSOR_TYPE_FORWARD = 4;
    private static final int CURSOR_TYPE_KEYSET = 1;
    private static final int CURSOR_TYPE_PARAMETERIZED = 4096;
    private static final int CURSOR_TYPE_STATIC = 8;
    private static final Integer FETCH_ABSOLUTE = new Integer(16);
    private static final Integer FETCH_FIRST = new Integer(1);
    private static final Integer FETCH_INFO = new Integer(256);
    private static final Integer FETCH_LAST = new Integer(8);
    private static final Integer FETCH_NEXT = new Integer(2);
    private static final Integer FETCH_PREVIOUS = new Integer(4);
    private static final Integer FETCH_RELATIVE = new Integer(32);
    private static final Integer FETCH_REPEAT = new Integer(128);
    private static final Integer SQL_ROW_DELETED = new Integer(2);
    private static final Integer SQL_ROW_DIRTY = new Integer(0);
    private static final Integer SQL_ROW_SUCCESS = new Integer(1);
    private final ParamInfo PARAM_CURSOR_HANDLE = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_FETCHTYPE = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_NUMROWS_IN = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_NUMROWS_OUT = new ParamInfo(4, null, 1);
    private final ParamInfo PARAM_OPTYPE = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_ROWNUM = new ParamInfo(4, new Integer(1), 0);
    private final ParamInfo PARAM_ROWNUM_IN = new ParamInfo(4, null, 0);
    private final ParamInfo PARAM_ROWNUM_OUT = new ParamInfo(4, null, 1);
    private final ParamInfo PARAM_TABLE = new ParamInfo(12, "", 4);
    private boolean asyncCursor;
    private int cursorPos;
    private ParamInfo[] insertRow;
    private boolean onInsertRow;
    private Object[][] rowCache = new Object[this.fetchSize][];
    private ParamInfo[] updateRow;

    static int getCursorConcurrencyOpt(int i) {
        switch (i) {
            case PointerIconCompat.TYPE_TEXT /*1008*/:
                return 4;
            case PointerIconCompat.TYPE_VERTICAL_TEXT /*1009*/:
                return 2;
            case PointerIconCompat.TYPE_ALIAS /*1010*/:
                return 8;
            default:
                return 1;
        }
    }

    static int getCursorScrollOpt(int i, int i2, boolean z) {
        switch (i) {
            case PointerIconCompat.TYPE_WAIT /*1004*/:
                i = 8;
                break;
            case 1005:
                i = 1;
                break;
            case PointerIconCompat.TYPE_CELL /*1006*/:
                i = 2;
                break;
            default:
                if (i2 != PointerIconCompat.TYPE_CROSSHAIR) {
                    i = 4;
                    break;
                }
                i = 8208;
                break;
        }
        return z ? i | 4096 : i;
    }

    MSCursorResultSet(JtdsStatement jtdsStatement, String str, String str2, ParamInfo[] paramInfoArr, int i, int i2) throws SQLException {
        super(jtdsStatement, i, i2, null);
        cursorCreate(str, str2, paramInfoArr);
        if (this.asyncCursor != null) {
            cursorFetch(FETCH_REPEAT, 0);
        }
    }

    protected Object setColValue(int i, int i2, Object obj, int i3) throws SQLException {
        obj = super.setColValue(i, i2, obj, i3);
        if (this.onInsertRow || getCurrentRow() != null) {
            ParamInfo paramInfo;
            i--;
            ColInfo colInfo = this.columns[i];
            if (this.onInsertRow) {
                paramInfo = this.insertRow[i];
            } else {
                if (this.updateRow == null) {
                    this.updateRow = new ParamInfo[this.columnCount];
                }
                paramInfo = this.updateRow[i];
            }
            if (paramInfo == null) {
                paramInfo = new ParamInfo(-1, TdsData.isUnicode(colInfo));
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append('@');
                stringBuffer.append(colInfo.realName);
                paramInfo.name = stringBuffer.toString();
                paramInfo.collation = colInfo.collation;
                paramInfo.charsetInfo = colInfo.charsetInfo;
                if (this.onInsertRow) {
                    this.insertRow[i] = paramInfo;
                } else {
                    this.updateRow[i] = paramInfo;
                }
            }
            i = 1;
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
                if ("ntext".equals(colInfo.sqlType) == 0 && "nchar".equals(colInfo.sqlType) == 0) {
                    if ("nvarchar".equals(colInfo.sqlType) == 0) {
                        i = 0;
                    }
                }
                paramInfo.isUnicode = i;
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

    protected Object getColumn(int i) throws SQLException {
        checkOpen();
        boolean z = true;
        if (i >= 1) {
            if (i <= this.columnCount) {
                if (!this.onInsertRow) {
                    Object[] currentRow = getCurrentRow();
                    if (currentRow != null) {
                        if (SQL_ROW_DIRTY.equals(currentRow[this.columns.length - 1])) {
                            cursorFetch(FETCH_REPEAT, 0);
                            currentRow = getCurrentRow();
                        }
                        i = currentRow[i - 1];
                        if (i != 0) {
                            z = false;
                        }
                        this.wasNull = z;
                        return i;
                    }
                }
                throw new SQLException(Messages.get("error.resultset.norow"), "24000");
            }
        }
        throw new SQLException(Messages.get("error.resultset.colindex", Integer.toString(i)), "07009");
    }

    private void cursorCreate(java.lang.String r24, java.lang.String r25, net.sourceforge.jtds.jdbc.ParamInfo[] r26) throws java.sql.SQLException {
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
        r23 = this;
        r0 = r23;
        r1 = r26;
        r2 = r0.statement;
        r2 = r2.getTds();
        r3 = r0.statement;
        r3 = r3.connection;
        r3 = r3.getPrepareSql();
        r4 = r0.cursorName;
        r12 = 1008; // 0x3f0 float:1.413E-42 double:4.98E-321;
        r13 = 1007; // 0x3ef float:1.411E-42 double:4.975E-321;
        r14 = 1003; // 0x3eb float:1.406E-42 double:4.955E-321;
        if (r4 == 0) goto L_0x0026;
    L_0x001c:
        r4 = r0.resultSetType;
        if (r4 != r14) goto L_0x0026;
    L_0x0020:
        r4 = r0.concurrency;
        if (r4 != r13) goto L_0x0026;
    L_0x0024:
        r0.concurrency = r12;
    L_0x0026:
        r4 = 0;
        if (r1 == 0) goto L_0x002d;
    L_0x0029:
        r5 = r1.length;
        if (r5 != 0) goto L_0x002d;
    L_0x002c:
        r1 = r4;
    L_0x002d:
        r5 = r2.getTdsVersion();
        r11 = 1;
        if (r5 != r11) goto L_0x003c;
    L_0x0034:
        if (r1 == 0) goto L_0x0038;
    L_0x0036:
        r5 = r4;
        goto L_0x003a;
    L_0x0038:
        r5 = r25;
    L_0x003a:
        r3 = 0;
        goto L_0x003e;
    L_0x003c:
        r5 = r25;
    L_0x003e:
        if (r1 == 0) goto L_0x004f;
    L_0x0040:
        if (r3 != 0) goto L_0x004f;
    L_0x0042:
        r6 = r0.statement;
        r6 = r6.connection;
        r7 = r24;
        r1 = net.sourceforge.jtds.jdbc.Support.substituteParameters(r7, r1, r6);
        r7 = r1;
        r1 = r4;
        goto L_0x0051;
    L_0x004f:
        r7 = r24;
    L_0x0051:
        if (r1 == 0) goto L_0x0061;
    L_0x0053:
        if (r5 == 0) goto L_0x005d;
    L_0x0055:
        r6 = "#jtds";
        r6 = r5.startsWith(r6);
        if (r6 != 0) goto L_0x0061;
    L_0x005d:
        r7 = net.sourceforge.jtds.jdbc.Support.substituteParamMarkers(r7, r1);
    L_0x0061:
        r10 = 16;
        r6 = 5;
        if (r5 == 0) goto L_0x00db;
    L_0x0066:
        r8 = "#jtds";
        r8 = r5.startsWith(r8);
        if (r8 == 0) goto L_0x00b8;
    L_0x006e:
        r7 = new java.lang.StringBuffer;
        r8 = r5.length();
        r8 = r8 + r10;
        if (r1 == 0) goto L_0x007b;
    L_0x0077:
        r9 = r1.length;
        r9 = r9 * 5;
        goto L_0x007c;
    L_0x007b:
        r9 = 0;
    L_0x007c:
        r8 = r8 + r9;
        r7.<init>(r8);
        r8 = "EXEC ";
        r7.append(r8);
        r7.append(r5);
        r5 = 32;
        r7.append(r5);
        r5 = 0;
    L_0x008e:
        if (r1 == 0) goto L_0x00b3;
    L_0x0090:
        r8 = r1.length;
        if (r5 >= r8) goto L_0x00b3;
    L_0x0093:
        if (r5 == 0) goto L_0x009a;
    L_0x0095:
        r8 = 44;
        r7.append(r8);
    L_0x009a:
        r8 = r1[r5];
        r8 = r8.name;
        if (r8 == 0) goto L_0x00a8;
    L_0x00a0:
        r8 = r1[r5];
        r8 = r8.name;
        r7.append(r8);
        goto L_0x00b0;
    L_0x00a8:
        r8 = "@P";
        r7.append(r8);
        r7.append(r5);
    L_0x00b0:
        r5 = r5 + 1;
        goto L_0x008e;
    L_0x00b3:
        r7 = r7.toString();
        goto L_0x00db;
    L_0x00b8:
        r8 = net.sourceforge.jtds.jdbc.TdsCore.isPreparedProcedureName(r5);
        if (r8 == 0) goto L_0x00db;
    L_0x00be:
        r8 = new java.lang.Integer;	 Catch:{ NumberFormatException -> 0x00c4 }
        r8.<init>(r5);	 Catch:{ NumberFormatException -> 0x00c4 }
        goto L_0x00dc;
    L_0x00c4:
        r1 = new java.lang.IllegalStateException;
        r2 = new java.lang.StringBuffer;
        r2.<init>();
        r3 = "Invalid prepared statement handle: ";
        r2.append(r3);
        r2.append(r5);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x00db:
        r8 = r4;
    L_0x00dc:
        r5 = r0.resultSetType;
        r9 = r0.concurrency;
        if (r1 == 0) goto L_0x00e4;
    L_0x00e2:
        r10 = 1;
        goto L_0x00e5;
    L_0x00e4:
        r10 = 0;
    L_0x00e5:
        r10 = getCursorScrollOpt(r5, r9, r10);
        r5 = r0.concurrency;
        r9 = getCursorConcurrencyOpt(r5);
        r5 = new net.sourceforge.jtds.jdbc.ParamInfo;
        r12 = new java.lang.Integer;
        r12.<init>(r10);
        r13 = 4;
        r5.<init>(r13, r12, r11);
        r12 = new net.sourceforge.jtds.jdbc.ParamInfo;
        r14 = new java.lang.Integer;
        r14.<init>(r9);
        r12.<init>(r13, r14, r11);
        r14 = new net.sourceforge.jtds.jdbc.ParamInfo;
        r15 = new java.lang.Integer;
        r6 = r0.fetchSize;
        r15.<init>(r6);
        r14.<init>(r13, r15, r11);
        r15 = new net.sourceforge.jtds.jdbc.ParamInfo;
        r15.<init>(r13, r4, r11);
        r6 = 3;
        if (r3 != r6) goto L_0x011e;
    L_0x0118:
        r4 = new net.sourceforge.jtds.jdbc.ParamInfo;
        r4.<init>(r13, r8, r11);
        goto L_0x011f;
    L_0x011e:
        r4 = 0;
    L_0x011f:
        if (r1 == 0) goto L_0x0142;
    L_0x0121:
        r6 = 0;
    L_0x0122:
        r11 = r1.length;
        if (r6 >= r11) goto L_0x0132;
    L_0x0125:
        r11 = r0.statement;
        r11 = r11.connection;
        r13 = r1[r6];
        net.sourceforge.jtds.jdbc.TdsData.getNativeType(r11, r13);
        r6 = r6 + 1;
        r13 = 4;
        goto L_0x0122;
    L_0x0132:
        r6 = new net.sourceforge.jtds.jdbc.ParamInfo;
        r11 = net.sourceforge.jtds.jdbc.Support.getParameterDefinitions(r1);
        r19 = r9;
        r9 = -1;
        r13 = 4;
        r6.<init>(r9, r11, r13);
        r17 = r6;
        goto L_0x0147;
    L_0x0142:
        r19 = r9;
        r9 = -1;
        r17 = 0;
    L_0x0147:
        r6 = new net.sourceforge.jtds.jdbc.ParamInfo;
        r6.<init>(r9, r7, r13);
        r7 = 3;
        if (r3 != r7) goto L_0x017b;
    L_0x014f:
        if (r8 == 0) goto L_0x017b;
    L_0x0151:
        r3 = "sp_cursorexecute";
        if (r1 != 0) goto L_0x015a;
    L_0x0155:
        r6 = 5;
        r1 = new net.sourceforge.jtds.jdbc.ParamInfo[r6];
        r11 = 0;
        goto L_0x0165;
    L_0x015a:
        r6 = 5;
        r7 = r1.length;
        r7 = r7 + r6;
        r7 = new net.sourceforge.jtds.jdbc.ParamInfo[r7];
        r9 = r1.length;
        r11 = 0;
        java.lang.System.arraycopy(r1, r11, r7, r6, r9);
        r1 = r7;
    L_0x0165:
        r4.isOutput = r11;
        r4.value = r8;
        r1[r11] = r4;
        r4 = 1;
        r1[r4] = r15;
        r4 = new java.lang.Integer;
        r6 = r10 & -4097;
        r4.<init>(r6);
        r5.value = r4;
        r6 = r1;
        r1 = r3;
        r11 = 1;
        goto L_0x0199;
    L_0x017b:
        r3 = "sp_cursoropen";
        if (r1 != 0) goto L_0x0184;
    L_0x017f:
        r4 = 5;
        r1 = new net.sourceforge.jtds.jdbc.ParamInfo[r4];
        r11 = 0;
        goto L_0x0192;
    L_0x0184:
        r4 = 5;
        r7 = r1.length;
        r8 = 6;
        r7 = r7 + r8;
        r7 = new net.sourceforge.jtds.jdbc.ParamInfo[r7];
        r9 = r1.length;
        r11 = 0;
        java.lang.System.arraycopy(r1, r11, r7, r8, r9);
        r7[r4] = r17;
        r1 = r7;
    L_0x0192:
        r1[r11] = r15;
        r11 = 1;
        r1[r11] = r6;
        r6 = r1;
        r1 = r3;
    L_0x0199:
        r13 = 2;
        r6[r13] = r5;
        r7 = 3;
        r6[r7] = r12;
        r3 = 4;
        r6[r3] = r14;
        r4 = 0;
        r8 = 0;
        r3 = r0.statement;
        r9 = r3.getQueryTimeout();
        r3 = r0.statement;
        r16 = r3.getMaxRows();
        r3 = r0.statement;
        r17 = r3.getMaxFieldSize();
        r18 = 1;
        r3 = r2;
        r20 = r5;
        r5 = r1;
        r1 = 3;
        r7 = r8;
        r8 = r9;
        r21 = r19;
        r9 = r16;
        r1 = r10;
        r10 = r17;
        r13 = 1;
        r11 = r18;
        r3.executeSQL(r4, r5, r6, r7, r8, r9, r10, r11);
        r0.processOutput(r2, r13);
        r3 = r1 & 8192;
        if (r3 == 0) goto L_0x01d5;
    L_0x01d3:
        r0.cursorPos = r13;
    L_0x01d5:
        r3 = r2.getReturnStatus();
        if (r3 == 0) goto L_0x0339;
    L_0x01db:
        r4 = r3.intValue();
        if (r4 == 0) goto L_0x01ea;
    L_0x01e1:
        r4 = r3.intValue();
        r5 = 2;
        if (r4 == r5) goto L_0x01eb;
    L_0x01e8:
        goto L_0x0339;
    L_0x01ea:
        r5 = 2;
    L_0x01eb:
        r3 = r3.intValue();
        if (r3 != r5) goto L_0x01f3;
    L_0x01f1:
        r3 = 1;
        goto L_0x01f4;
    L_0x01f3:
        r3 = 0;
    L_0x01f4:
        r0.asyncCursor = r3;
        r3 = r0.PARAM_CURSOR_HANDLE;
        r4 = r15.getOutValue();
        r3.value = r4;
        r3 = r20;
        r3 = r3.getOutValue();
        r3 = (java.lang.Integer) r3;
        r15 = r3.intValue();
        r3 = r12.getOutValue();
        r3 = (java.lang.Integer) r3;
        r12 = r3.intValue();
        r3 = r14.getOutValue();
        r3 = (java.lang.Integer) r3;
        r3 = r3.intValue();
        r0.rowsInResult = r3;
        r3 = r0.cursorName;
        if (r3 == 0) goto L_0x0280;
    L_0x0224:
        r3 = 3;
        r6 = new net.sourceforge.jtds.jdbc.ParamInfo[r3];
        r3 = r0.PARAM_CURSOR_HANDLE;
        r18 = 0;
        r6[r18] = r3;
        r3 = r0.PARAM_OPTYPE;
        r4 = new java.lang.Integer;
        r5 = 2;
        r4.<init>(r5);
        r3.value = r4;
        r3 = r0.PARAM_OPTYPE;
        r6[r13] = r3;
        r3 = new net.sourceforge.jtds.jdbc.ParamInfo;
        r4 = 12;
        r7 = r0.cursorName;
        r8 = 4;
        r3.<init>(r4, r7, r8);
        r6[r5] = r3;
        r4 = 0;
        r5 = "sp_cursoroption";
        r7 = 1;
        r8 = 0;
        r9 = -1;
        r10 = -1;
        r11 = 1;
        r3 = r2;
        r3.executeSQL(r4, r5, r6, r7, r8, r9, r10, r11);
        r2.clearResponseQueue();
        r2 = r2.getReturnStatus();
        r2 = r2.intValue();
        if (r2 == 0) goto L_0x0276;
    L_0x0260:
        r2 = r0.statement;
        r2 = r2.getMessages();
        r3 = new java.sql.SQLException;
        r4 = "error.resultset.openfail";
        r4 = net.sourceforge.jtds.jdbc.Messages.get(r4);
        r5 = "24000";
        r3.<init>(r4, r5);
        r2.addException(r3);
    L_0x0276:
        r2 = r0.statement;
        r2 = r2.getMessages();
        r2.checkErrors();
        goto L_0x0282;
    L_0x0280:
        r18 = 0;
    L_0x0282:
        r2 = r1 & 4095;
        if (r15 != r2) goto L_0x028b;
    L_0x0286:
        r2 = r21;
        if (r12 == r2) goto L_0x0338;
    L_0x028a:
        goto L_0x028d;
    L_0x028b:
        r2 = r21;
    L_0x028d:
        r3 = 8;
        if (r15 == r1) goto L_0x02cf;
    L_0x0291:
        r1 = 4;
        if (r15 == r1) goto L_0x02c3;
    L_0x0294:
        if (r15 == r3) goto L_0x02c0;
    L_0x0296:
        r1 = 16;
        if (r15 == r1) goto L_0x02c3;
    L_0x029a:
        switch(r15) {
            case 1: goto L_0x02bd;
            case 2: goto L_0x02ba;
            default: goto L_0x029d;
        };
    L_0x029d:
        r14 = r0.resultSetType;
        r1 = r0.statement;
        r1 = r1.getMessages();
        r4 = new java.sql.SQLWarning;
        r5 = "warning.cursortype";
        r6 = java.lang.Integer.toString(r15);
        r5 = net.sourceforge.jtds.jdbc.Messages.get(r5, r6);
        r6 = "01000";
        r4.<init>(r5, r6);
        r1.addWarning(r4);
        goto L_0x02c5;
    L_0x02ba:
        r14 = 1006; // 0x3ee float:1.41E-42 double:4.97E-321;
        goto L_0x02c5;
    L_0x02bd:
        r14 = 1005; // 0x3ed float:1.408E-42 double:4.965E-321;
        goto L_0x02c5;
    L_0x02c0:
        r14 = 1004; // 0x3ec float:1.407E-42 double:4.96E-321;
        goto L_0x02c5;
    L_0x02c3:
        r14 = 1003; // 0x3eb float:1.406E-42 double:4.955E-321;
    L_0x02c5:
        r1 = r0.resultSetType;
        if (r14 >= r1) goto L_0x02cb;
    L_0x02c9:
        r15 = 1;
        goto L_0x02cc;
    L_0x02cb:
        r15 = 0;
    L_0x02cc:
        r0.resultSetType = r14;
        goto L_0x02d0;
    L_0x02cf:
        r15 = 0;
    L_0x02d0:
        if (r12 == r2) goto L_0x030c;
    L_0x02d2:
        r1 = 4;
        if (r12 == r1) goto L_0x0301;
    L_0x02d5:
        if (r12 == r3) goto L_0x02fe;
    L_0x02d7:
        switch(r12) {
            case 1: goto L_0x02fb;
            case 2: goto L_0x02f8;
            default: goto L_0x02da;
        };
    L_0x02da:
        r1 = r0.concurrency;
        r2 = r0.statement;
        r2 = r2.getMessages();
        r3 = new java.sql.SQLWarning;
        r4 = "warning.concurrtype";
        r5 = java.lang.Integer.toString(r12);
        r4 = net.sourceforge.jtds.jdbc.Messages.get(r4, r5);
        r5 = "01000";
        r3.<init>(r4, r5);
        r2.addWarning(r3);
        r12 = r1;
        goto L_0x0303;
    L_0x02f8:
        r12 = 1009; // 0x3f1 float:1.414E-42 double:4.985E-321;
        goto L_0x0303;
    L_0x02fb:
        r12 = 1007; // 0x3ef float:1.411E-42 double:4.975E-321;
        goto L_0x0303;
    L_0x02fe:
        r12 = 1010; // 0x3f2 float:1.415E-42 double:4.99E-321;
        goto L_0x0303;
    L_0x0301:
        r12 = 1008; // 0x3f0 float:1.413E-42 double:4.98E-321;
    L_0x0303:
        r1 = r0.concurrency;
        if (r12 >= r1) goto L_0x0309;
    L_0x0307:
        r15 = 1;
        goto L_0x030a;
    L_0x0309:
        r15 = 0;
    L_0x030a:
        r0.concurrency = r12;
    L_0x030c:
        if (r15 == 0) goto L_0x0338;
    L_0x030e:
        r1 = r0.statement;
        r2 = new java.sql.SQLWarning;
        r3 = "warning.cursordowngraded";
        r4 = new java.lang.StringBuffer;
        r4.<init>();
        r5 = r0.resultSetType;
        r4.append(r5);
        r5 = "/";
        r4.append(r5);
        r5 = r0.concurrency;
        r4.append(r5);
        r4 = r4.toString();
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r3, r4);
        r4 = "01000";
        r2.<init>(r3, r4);
        r1.addWarning(r2);
    L_0x0338:
        return;
    L_0x0339:
        r1 = new java.sql.SQLException;
        r2 = "error.resultset.openfail";
        r2 = net.sourceforge.jtds.jdbc.Messages.get(r2);
        r3 = "24000";
        r1.<init>(r2, r3);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.MSCursorResultSet.cursorCreate(java.lang.String, java.lang.String, net.sourceforge.jtds.jdbc.ParamInfo[]):void");
    }

    private boolean cursorFetch(Integer num, int i) throws SQLException {
        Integer num2 = num;
        TdsCore tds = this.statement.getTds();
        this.statement.clearWarnings();
        int i2 = (num2 == FETCH_ABSOLUTE || num2 == FETCH_RELATIVE) ? i : 1;
        ParamInfo[] paramInfoArr = new ParamInfo[4];
        paramInfoArr[0] = r1.PARAM_CURSOR_HANDLE;
        r1.PARAM_FETCHTYPE.value = num2;
        paramInfoArr[1] = r1.PARAM_FETCHTYPE;
        r1.PARAM_ROWNUM_IN.value = new Integer(i2);
        paramInfoArr[2] = r1.PARAM_ROWNUM_IN;
        if (((Integer) r1.PARAM_NUMROWS_IN.value).intValue() != r1.fetchSize) {
            r1.PARAM_NUMROWS_IN.value = new Integer(r1.fetchSize);
            r1.rowCache = new Object[r1.fetchSize][];
        }
        paramInfoArr[3] = r1.PARAM_NUMROWS_IN;
        synchronized (tds) {
            try {
                tds.executeSQL(null, "sp_cursorfetch", paramInfoArr, true, 0, 0, r1.statement.getMaxFieldSize(), false);
                r1.PARAM_FETCHTYPE.value = FETCH_INFO;
                paramInfoArr[1] = r1.PARAM_FETCHTYPE;
                r1.PARAM_ROWNUM_OUT.clearOutValue();
                paramInfoArr[2] = r1.PARAM_ROWNUM_OUT;
                r1.PARAM_NUMROWS_OUT.clearOutValue();
                paramInfoArr[3] = r1.PARAM_NUMROWS_OUT;
                tds.executeSQL(null, "sp_cursorfetch", paramInfoArr, true, r1.statement.getQueryTimeout(), -1, -1, true);
            } catch (Throwable th) {
                while (true) {
                    Throwable th2 = th;
                }
            }
        }
        processOutput(tds, false);
        r1.cursorPos = ((Integer) r1.PARAM_ROWNUM_OUT.getOutValue()).intValue();
        if (num2 != FETCH_REPEAT) {
            r1.pos = r1.cursorPos;
        }
        r1.rowsInResult = ((Integer) r1.PARAM_NUMROWS_OUT.getOutValue()).intValue();
        if (r1.rowsInResult < 0) {
            r1.rowsInResult = 0 - r1.rowsInResult;
        }
        if (getCurrentRow() != null) {
            return true;
        }
        return false;
    }

    private void cursor(Integer num, ParamInfo[] paramInfoArr) throws SQLException {
        Object obj;
        Object obj2;
        Object obj3;
        int i;
        Object[] currentRow;
        Integer num2 = num;
        ParamInfo[] paramInfoArr2 = paramInfoArr;
        TdsCore tds = this.statement.getTds();
        this.statement.clearWarnings();
        if (num2 == CURSOR_OP_DELETE) {
            obj = new ParamInfo[3];
        } else if (paramInfoArr2 == null) {
            throw new SQLException(Messages.get("error.resultset.update"), "24000");
        } else {
            obj = new ParamInfo[(r1.columnCount + 4)];
        }
        obj[0] = r1.PARAM_CURSOR_HANDLE;
        r1.PARAM_OPTYPE.value = num2;
        obj[1] = r1.PARAM_OPTYPE;
        ParamInfo[] paramInfoArr3 = (r1.pos - r1.cursorPos) + 1;
        r1.PARAM_ROWNUM.value = new Integer(paramInfoArr3);
        obj[2] = r1.PARAM_ROWNUM;
        if (paramInfoArr2 != null) {
            obj[3] = r1.PARAM_TABLE;
            int i2 = r1.columnCount;
            String str = null;
            paramInfoArr3 = 4;
            for (int i3 = 0; i3 < i2; i3++) {
                ParamInfo paramInfo = paramInfoArr2[i3];
                ColInfo colInfo = r1.columns[i3];
                if (paramInfo != null && paramInfo.isSet) {
                    if (colInfo.isWriteable) {
                        int i4 = paramInfoArr3 + 1;
                        obj[paramInfoArr3] = paramInfo;
                        paramInfoArr3 = i4;
                    } else {
                        throw new SQLException(Messages.get("error.resultset.insert", Integer.toString(i3 + 1), colInfo.realName), "24000");
                    }
                }
                if (str == null && colInfo.tableName != null) {
                    if (colInfo.catalog == null) {
                        if (colInfo.schema == null) {
                            str = colInfo.tableName;
                        }
                    }
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(colInfo.catalog != null ? colInfo.catalog : "");
                    stringBuffer.append('.');
                    stringBuffer.append(colInfo.schema != null ? colInfo.schema : "");
                    stringBuffer.append('.');
                    stringBuffer.append(colInfo.tableName);
                    str = stringBuffer.toString();
                }
            }
            if (paramInfoArr3 == 4) {
                if (num2 == CURSOR_OP_INSERT) {
                    StringBuffer stringBuffer2 = new StringBuffer();
                    stringBuffer2.append("insert ");
                    stringBuffer2.append(str);
                    stringBuffer2.append(" default values");
                    obj[paramInfoArr3] = new ParamInfo(12, stringBuffer2.toString(), 4);
                    paramInfoArr3++;
                } else {
                    return;
                }
            }
            if (paramInfoArr3 != i2 + 4) {
                Object obj4 = new ParamInfo[paramInfoArr3];
                System.arraycopy(obj, 0, obj4, 0, paramInfoArr3);
                obj2 = obj4;
                synchronized (tds) {
                    try {
                        paramInfoArr3 = obj2;
                        obj3 = obj2;
                        tds.executeSQL(null, "sp_cursor", paramInfoArr3, false, 0, -1, -1, false);
                        if (obj3.length == 4) {
                            new ParamInfo[4][0] = r1.PARAM_CURSOR_HANDLE;
                        } else {
                            paramInfoArr3 = obj3;
                        }
                        r1.PARAM_FETCHTYPE.value = FETCH_INFO;
                        paramInfoArr3[1] = r1.PARAM_FETCHTYPE;
                        r1.PARAM_ROWNUM_OUT.clearOutValue();
                        paramInfoArr3[2] = r1.PARAM_ROWNUM_OUT;
                        r1.PARAM_NUMROWS_OUT.clearOutValue();
                        paramInfoArr3[3] = r1.PARAM_NUMROWS_OUT;
                        tds.executeSQL(null, "sp_cursorfetch", paramInfoArr3, true, r1.statement.getQueryTimeout(), -1, -1, true);
                    } finally {
                        num2 = r0;
                    }
                }
                tds.consumeOneResponse();
                r1.statement.getMessages().checkErrors();
                if (tds.getReturnStatus().intValue() == 0) {
                    throw new SQLException(Messages.get("error.resultset.cursorfail"), "24000");
                }
                if (paramInfoArr2 != null) {
                    for (i = 0; i < paramInfoArr2.length; i++) {
                        if (paramInfoArr2[i] != null) {
                            paramInfoArr2[i].clearInValue();
                        }
                    }
                }
                tds.clearResponseQueue();
                r1.statement.getMessages().checkErrors();
                r1.cursorPos = ((Integer) r1.PARAM_ROWNUM_OUT.getOutValue()).intValue();
                r1.rowsInResult = ((Integer) r1.PARAM_NUMROWS_OUT.getOutValue()).intValue();
                if (num2 == CURSOR_OP_DELETE || num2 == CURSOR_OP_UPDATE) {
                    currentRow = getCurrentRow();
                    if (currentRow != null) {
                        throw new SQLException(Messages.get("error.resultset.updatefail"), "24000");
                    }
                    currentRow[r1.columns.length - 1] = num2 != CURSOR_OP_DELETE ? SQL_ROW_DELETED : SQL_ROW_DIRTY;
                }
                return;
            }
        }
        obj2 = obj;
        synchronized (tds) {
            paramInfoArr3 = obj2;
            obj3 = obj2;
            tds.executeSQL(null, "sp_cursor", paramInfoArr3, false, 0, -1, -1, false);
            if (obj3.length == 4) {
                paramInfoArr3 = obj3;
            } else {
                new ParamInfo[4][0] = r1.PARAM_CURSOR_HANDLE;
            }
            r1.PARAM_FETCHTYPE.value = FETCH_INFO;
            paramInfoArr3[1] = r1.PARAM_FETCHTYPE;
            r1.PARAM_ROWNUM_OUT.clearOutValue();
            paramInfoArr3[2] = r1.PARAM_ROWNUM_OUT;
            r1.PARAM_NUMROWS_OUT.clearOutValue();
            paramInfoArr3[3] = r1.PARAM_NUMROWS_OUT;
            tds.executeSQL(null, "sp_cursorfetch", paramInfoArr3, true, r1.statement.getQueryTimeout(), -1, -1, true);
        }
        tds.consumeOneResponse();
        r1.statement.getMessages().checkErrors();
        if (tds.getReturnStatus().intValue() == 0) {
            if (paramInfoArr2 != null) {
                for (i = 0; i < paramInfoArr2.length; i++) {
                    if (paramInfoArr2[i] != null) {
                        paramInfoArr2[i].clearInValue();
                    }
                }
            }
            tds.clearResponseQueue();
            r1.statement.getMessages().checkErrors();
            r1.cursorPos = ((Integer) r1.PARAM_ROWNUM_OUT.getOutValue()).intValue();
            r1.rowsInResult = ((Integer) r1.PARAM_NUMROWS_OUT.getOutValue()).intValue();
            currentRow = getCurrentRow();
            if (currentRow != null) {
                if (num2 != CURSOR_OP_DELETE) {
                }
                currentRow[r1.columns.length - 1] = num2 != CURSOR_OP_DELETE ? SQL_ROW_DELETED : SQL_ROW_DIRTY;
                return;
            }
            throw new SQLException(Messages.get("error.resultset.updatefail"), "24000");
        }
        throw new SQLException(Messages.get("error.resultset.cursorfail"), "24000");
    }

    private void cursorClose() throws SQLException {
        TdsCore tds = this.statement.getTds();
        this.statement.clearWarnings();
        tds.clearResponseQueue();
        SQLException sQLException = this.statement.getMessages().exceptions;
        TdsCore tdsCore = tds;
        tdsCore.executeSQL(null, "sp_cursorclose", new ParamInfo[]{this.PARAM_CURSOR_HANDLE}, false, this.statement.getQueryTimeout(), -1, -1, true);
        tds.clearResponseQueue();
        if (sQLException != null) {
            sQLException.setNextException(this.statement.getMessages().exceptions);
            throw sQLException;
        } else {
            this.statement.getMessages().checkErrors();
        }
    }

    private void processOutput(TdsCore tdsCore, boolean z) throws SQLException {
        while (!tdsCore.getMoreResults() && !tdsCore.isEndOfResponse()) {
        }
        boolean z2 = false;
        if (tdsCore.isResultSet()) {
            if (z) {
                this.columns = copyInfo(tdsCore.getColumns());
                this.columnCount = JtdsResultSet.getColumnCount(this.columns);
            }
            if (tdsCore.isRowData() || tdsCore.getNextRow()) {
                boolean z3;
                while (true) {
                    z3 = r0 + 1;
                    this.rowCache[r0] = copyRow(tdsCore.getRowData());
                    if (!tdsCore.getNextRow()) {
                        break;
                    }
                    z2 = z3;
                }
                z2 = z3;
            }
        } else if (z) {
            this.statement.getMessages().addException(new SQLException(Messages.get("error.statement.noresult"), "24000"));
        }
        while (z2 < this.rowCache.length) {
            this.rowCache[z2] = null;
            z2++;
        }
        tdsCore.clearResponseQueue();
        this.statement.messages.checkErrors();
    }

    public void afterLast() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos != -1) {
            cursorFetch(FETCH_ABSOLUTE, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        }
    }

    public void beforeFirst() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos != 0) {
            cursorFetch(FETCH_ABSOLUTE, 0);
        }
    }

    public void cancelRowUpdates() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        int i = 0;
        while (this.updateRow != null && i < this.updateRow.length) {
            if (this.updateRow[i] != null) {
                this.updateRow[i].clearInValue();
            }
            i++;
        }
    }

    public void close() throws SQLException {
        if (!this.closed) {
            try {
                if (!this.statement.getConnection().isClosed()) {
                    cursorClose();
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
        if (getCurrentRow() == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        } else if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        } else {
            cursor(CURSOR_OP_DELETE, null);
        }
    }

    public void insertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (this.onInsertRow) {
            cursor(CURSOR_OP_INSERT, this.insertRow);
            return;
        }
        throw new SQLException(Messages.get("error.resultset.notinsrow"), "24000");
    }

    public void moveToCurrentRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        this.onInsertRow = false;
    }

    public void moveToInsertRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (this.insertRow == null) {
            this.insertRow = new ParamInfo[this.columnCount];
        }
        this.onInsertRow = true;
    }

    public void refreshRow() throws SQLException {
        checkOpen();
        if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        }
        cursorFetch(FETCH_REPEAT, 0);
    }

    public void updateRow() throws SQLException {
        checkOpen();
        checkUpdateable();
        if (getCurrentRow() == null) {
            throw new SQLException(Messages.get("error.resultset.norow"), "24000");
        } else if (this.onInsertRow) {
            throw new SQLException(Messages.get("error.resultset.insrow"), "24000");
        } else if (this.updateRow != null) {
            cursor(CURSOR_OP_UPDATE, this.updateRow);
        }
    }

    public boolean first() throws SQLException {
        checkOpen();
        checkScrollable();
        this.pos = 1;
        if (getCurrentRow() == null) {
            return cursorFetch(FETCH_FIRST, 0);
        }
        return true;
    }

    public boolean isLast() throws SQLException {
        checkOpen();
        return this.pos == this.rowsInResult && this.rowsInResult != 0;
    }

    public boolean last() throws SQLException {
        checkOpen();
        checkScrollable();
        this.pos = this.rowsInResult;
        if (!this.asyncCursor) {
            if (getCurrentRow() != null) {
                return true;
            }
        }
        if (!cursorFetch(FETCH_LAST, 0)) {
            return false;
        }
        this.pos = this.rowsInResult;
        return true;
    }

    public boolean next() throws SQLException {
        checkOpen();
        this.pos++;
        if (getCurrentRow() == null) {
            return cursorFetch(FETCH_NEXT, 0);
        }
        return true;
    }

    public boolean previous() throws SQLException {
        checkOpen();
        checkScrollable();
        if (this.pos == 0) {
            return false;
        }
        int i = this.pos;
        this.pos--;
        if (i != -1) {
            if (getCurrentRow() != null) {
                return true;
            }
        }
        boolean cursorFetch = cursorFetch(FETCH_PREVIOUS, 0);
        this.pos = i == -1 ? this.rowsInResult : i - 1;
        return cursorFetch;
    }

    public boolean rowDeleted() throws SQLException {
        checkOpen();
        Object[] currentRow = getCurrentRow();
        if (currentRow == null) {
            return false;
        }
        if (SQL_ROW_DIRTY.equals(currentRow[this.columns.length - 1])) {
            cursorFetch(FETCH_REPEAT, 0);
            currentRow = getCurrentRow();
        }
        return SQL_ROW_DELETED.equals(currentRow[this.columns.length - 1]);
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
        int i2;
        checkOpen();
        checkScrollable();
        if (i >= 0) {
            i2 = i;
        } else {
            i2 = (this.rowsInResult - i) + 1;
        }
        this.pos = i2;
        if (getCurrentRow() != null) {
            return true;
        }
        boolean cursorFetch = cursorFetch(FETCH_ABSOLUTE, i);
        if (this.cursorPos == 1 && i + this.rowsInResult < 0) {
            this.pos = 0;
            cursorFetch = false;
        }
        return cursorFetch;
    }

    public boolean relative(int i) throws SQLException {
        checkOpen();
        checkScrollable();
        this.pos = (this.pos == -1 ? this.rowsInResult + 1 : this.pos) + i;
        if (getCurrentRow() != 0) {
            return true;
        }
        if (this.pos >= this.cursorPos) {
            return cursorFetch(FETCH_RELATIVE, this.pos - this.cursorPos);
        }
        i = this.pos;
        boolean cursorFetch = cursorFetch(FETCH_RELATIVE, ((this.pos - this.cursorPos) - this.fetchSize) + 1);
        if (cursorFetch) {
            this.pos = i;
        } else {
            this.pos = 0;
        }
        return cursorFetch;
    }

    protected Object[] getCurrentRow() {
        if (this.pos >= this.cursorPos) {
            if (this.pos < this.cursorPos + this.rowCache.length) {
                return this.rowCache[this.pos - this.cursorPos];
            }
        }
        return null;
    }
}
