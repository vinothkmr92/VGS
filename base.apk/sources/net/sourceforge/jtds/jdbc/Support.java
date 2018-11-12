package net.sourceforge.jtds.jdbc;

import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class Support {
    private static final BigDecimal BIG_DECIMAL_ONE = new BigDecimal(1.0d);
    private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal(0.0d);
    private static final Date DATE_ZERO = new Date(0);
    private static final Double DOUBLE_ONE = new Double(1.0d);
    private static final Double DOUBLE_ZERO = new Double(0.0d);
    private static final Float FLOAT_ONE = new Float(1.0d);
    private static final Float FLOAT_ZERO = new Float(0.0d);
    private static final Integer INTEGER_ONE = new Integer(1);
    private static final Integer INTEGER_ZERO = new Integer(0);
    private static final Long LONG_ONE = new Long(1);
    private static final Long LONG_ZERO = new Long(0);
    private static final BigInteger MAX_VALUE_28 = new BigInteger("9999999999999999999999999999");
    private static final BigInteger MAX_VALUE_38 = new BigInteger("99999999999999999999999999999999999999");
    private static final BigDecimal MAX_VALUE_LONG_BD = new BigDecimal(String.valueOf(Long.MAX_VALUE));
    private static final BigInteger MAX_VALUE_LONG_BI = new BigInteger(String.valueOf(Long.MAX_VALUE));
    private static final BigDecimal MIN_VALUE_LONG_BD = new BigDecimal(String.valueOf(Long.MIN_VALUE));
    private static final BigInteger MIN_VALUE_LONG_BI = new BigInteger(String.valueOf(Long.MIN_VALUE));
    private static final Time TIME_ZERO = new Time(0);
    static /* synthetic */ Class array$B;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Throwable;
    static /* synthetic */ Class class$java$math$BigDecimal;
    static /* synthetic */ Class class$java$sql$Blob;
    static /* synthetic */ Class class$java$sql$Clob;
    static /* synthetic */ Class class$java$sql$Date;
    static /* synthetic */ Class class$java$sql$Time;
    static /* synthetic */ Class class$java$sql$Timestamp;
    static /* synthetic */ Class class$net$sourceforge$jtds$jdbc$BlobImpl;
    static /* synthetic */ Class class$net$sourceforge$jtds$jdbc$ClobImpl;
    private static final char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final HashMap typeMap = new HashMap();

    static int calculateNamedPipeBufferSize(int i, int i2) {
        return i2 == 0 ? i >= 3 ? 4096 : 512 : i2;
    }

    static Object castNumeric(Object obj, int i, int i2) {
        return null;
    }

    public static int convertLOBType(int i) {
        switch (i) {
            case 2004:
                return -4;
            case 2005:
                return -1;
            default:
                return i;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static String getClassName(int i) {
        if (i != 12) {
            if (i != 16) {
                switch (i) {
                    case -7:
                        break;
                    case -6:
                        break;
                    case -5:
                        return "java.lang.Long";
                    case -4:
                        break;
                    case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                    case -2:
                        return "[B";
                    case -1:
                        break;
                    default:
                        switch (i) {
                            case 1:
                                break;
                            case 2:
                            case 3:
                                return "java.math.BigDecimal";
                            case 4:
                            case 5:
                                break;
                            case 6:
                            case 8:
                                return "java.lang.Double";
                            case 7:
                                return "java.lang.Float";
                            default:
                                switch (i) {
                                    case 91:
                                        return "java.sql.Date";
                                    case 92:
                                        return "java.sql.Time";
                                    case 93:
                                        return "java.sql.Timestamp";
                                    default:
                                        switch (i) {
                                            case 2004:
                                                break;
                                            case 2005:
                                                break;
                                            default:
                                                return "java.lang.Object";
                                        }
                                }
                        }
                }
            }
            return "java.lang.Boolean";
        }
        return "java.lang.String";
    }

    static String getJdbcTypeName(int i) {
        if (i == 12) {
            return "VARCHAR";
        }
        if (i == 16) {
            return "BOOLEAN";
        }
        if (i == 70) {
            return "DATALINK";
        }
        if (i == 1111) {
            return "OTHER";
        }
        switch (i) {
            case -7:
                return "BIT";
            case -6:
                return "TINYINT";
            case -5:
                return "BIGINT";
            case -4:
                return "LONGVARBINARY";
            case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                return "VARBINARY";
            case -2:
                return "BINARY";
            case -1:
                return "LONGVARCHAR";
            case 0:
                return "NULL";
            case 1:
                return "CHAR";
            case 2:
                return "NUMERIC";
            case 3:
                return "DECIMAL";
            case 4:
                return "INTEGER";
            case 5:
                return "SMALLINT";
            case 6:
                return "FLOAT";
            case 7:
                return "REAL";
            case 8:
                return "DOUBLE";
            default:
                switch (i) {
                    case 91:
                        return "DATE";
                    case 92:
                        return "TIME";
                    case 93:
                        return "TIMESTAMP";
                    default:
                        switch (i) {
                            case 2000:
                                return "JAVA_OBJECT";
                            case 2001:
                                return "DISTINCT";
                            case 2002:
                                return "STRUCT";
                            case 2003:
                                return "ARRAY";
                            case 2004:
                                return "BLOB";
                            case 2005:
                                return "CLOB";
                            case 2006:
                                return "REF";
                            default:
                                return "ERROR";
                        }
                }
        }
    }

    static {
        Object class$;
        HashMap hashMap = typeMap;
        if (class$java$lang$Byte == null) {
            class$ = class$("java.lang.Byte");
            class$java$lang$Byte = class$;
        } else {
            class$ = class$java$lang$Byte;
        }
        hashMap.put(class$, new Integer(-6));
        hashMap = typeMap;
        if (class$java$lang$Short == null) {
            class$ = class$("java.lang.Short");
            class$java$lang$Short = class$;
        } else {
            class$ = class$java$lang$Short;
        }
        hashMap.put(class$, new Integer(5));
        hashMap = typeMap;
        if (class$java$lang$Integer == null) {
            class$ = class$("java.lang.Integer");
            class$java$lang$Integer = class$;
        } else {
            class$ = class$java$lang$Integer;
        }
        hashMap.put(class$, new Integer(4));
        hashMap = typeMap;
        if (class$java$lang$Long == null) {
            class$ = class$("java.lang.Long");
            class$java$lang$Long = class$;
        } else {
            class$ = class$java$lang$Long;
        }
        hashMap.put(class$, new Integer(-5));
        hashMap = typeMap;
        if (class$java$lang$Float == null) {
            class$ = class$("java.lang.Float");
            class$java$lang$Float = class$;
        } else {
            class$ = class$java$lang$Float;
        }
        hashMap.put(class$, new Integer(7));
        hashMap = typeMap;
        if (class$java$lang$Double == null) {
            class$ = class$("java.lang.Double");
            class$java$lang$Double = class$;
        } else {
            class$ = class$java$lang$Double;
        }
        hashMap.put(class$, new Integer(8));
        hashMap = typeMap;
        if (class$java$math$BigDecimal == null) {
            class$ = class$("java.math.BigDecimal");
            class$java$math$BigDecimal = class$;
        } else {
            class$ = class$java$math$BigDecimal;
        }
        hashMap.put(class$, new Integer(3));
        hashMap = typeMap;
        if (class$java$lang$Boolean == null) {
            class$ = class$("java.lang.Boolean");
            class$java$lang$Boolean = class$;
        } else {
            class$ = class$java$lang$Boolean;
        }
        hashMap.put(class$, new Integer(16));
        hashMap = typeMap;
        if (array$B == null) {
            class$ = class$("[B");
            array$B = class$;
        } else {
            class$ = array$B;
        }
        hashMap.put(class$, new Integer(-3));
        hashMap = typeMap;
        if (class$java$sql$Date == null) {
            class$ = class$("java.sql.Date");
            class$java$sql$Date = class$;
        } else {
            class$ = class$java$sql$Date;
        }
        hashMap.put(class$, new Integer(91));
        hashMap = typeMap;
        if (class$java$sql$Time == null) {
            class$ = class$("java.sql.Time");
            class$java$sql$Time = class$;
        } else {
            class$ = class$java$sql$Time;
        }
        hashMap.put(class$, new Integer(92));
        hashMap = typeMap;
        if (class$java$sql$Timestamp == null) {
            class$ = class$("java.sql.Timestamp");
            class$java$sql$Timestamp = class$;
        } else {
            class$ = class$java$sql$Timestamp;
        }
        hashMap.put(class$, new Integer(93));
        hashMap = typeMap;
        if (class$net$sourceforge$jtds$jdbc$BlobImpl == null) {
            class$ = class$("net.sourceforge.jtds.jdbc.BlobImpl");
            class$net$sourceforge$jtds$jdbc$BlobImpl = class$;
        } else {
            class$ = class$net$sourceforge$jtds$jdbc$BlobImpl;
        }
        hashMap.put(class$, new Integer(-4));
        hashMap = typeMap;
        if (class$net$sourceforge$jtds$jdbc$ClobImpl == null) {
            class$ = class$("net.sourceforge.jtds.jdbc.ClobImpl");
            class$net$sourceforge$jtds$jdbc$ClobImpl = class$;
        } else {
            class$ = class$net$sourceforge$jtds$jdbc$ClobImpl;
        }
        hashMap.put(class$, new Integer(-1));
        hashMap = typeMap;
        if (class$java$lang$String == null) {
            class$ = class$("java.lang.String");
            class$java$lang$String = class$;
        } else {
            class$ = class$java$lang$String;
        }
        hashMap.put(class$, new Integer(12));
        hashMap = typeMap;
        if (class$java$sql$Blob == null) {
            class$ = class$("java.sql.Blob");
            class$java$sql$Blob = class$;
        } else {
            class$ = class$java$sql$Blob;
        }
        hashMap.put(class$, new Integer(-4));
        hashMap = typeMap;
        if (class$java$sql$Clob == null) {
            class$ = class$("java.sql.Clob");
            class$java$sql$Clob = class$;
        } else {
            class$ = class$java$sql$Clob;
        }
        hashMap.put(class$, new Integer(-1));
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (String str2) {
            throw new NoClassDefFoundError(str2.getMessage());
        }
    }

    public static String toHex(byte[] bArr) {
        if (r0 <= 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(r0 * 2);
        for (byte b : bArr) {
            int i = b & 255;
            stringBuffer.append(hex[i >> 4]);
            stringBuffer.append(hex[i & 15]);
        }
        return stringBuffer.toString();
    }

    static BigDecimal normalizeBigDecimal(BigDecimal bigDecimal, int i) throws SQLException {
        if (bigDecimal == null) {
            return null;
        }
        if (bigDecimal.scale() < 0) {
            bigDecimal = bigDecimal.setScale(0);
        }
        if (bigDecimal.scale() > i) {
            bigDecimal = bigDecimal.setScale(i, 4);
        }
        BigInteger bigInteger = i == 28 ? MAX_VALUE_28 : MAX_VALUE_38;
        while (bigDecimal.abs().unscaledValue().compareTo(bigInteger) > 0) {
            int scale = bigDecimal.scale() - 1;
            if (scale < 0) {
                throw new SQLException(Messages.get("error.normalize.numtoobig", String.valueOf(i)), "22000");
            }
            bigDecimal = bigDecimal.setScale(scale, 4);
        }
        return bigDecimal;
    }

    static java.lang.Object convert(java.lang.Object r10, java.lang.Object r11, int r12, java.lang.String r13) throws java.sql.SQLException {
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
        r0 = 16;
        if (r11 != 0) goto L_0x001d;
    L_0x0004:
        if (r12 == r0) goto L_0x001a;
    L_0x0006:
        switch(r12) {
            case -7: goto L_0x001a;
            case -6: goto L_0x0017;
            case -5: goto L_0x0014;
            default: goto L_0x0009;
        };
    L_0x0009:
        switch(r12) {
            case 4: goto L_0x0017;
            case 5: goto L_0x0017;
            case 6: goto L_0x0011;
            case 7: goto L_0x000e;
            case 8: goto L_0x0011;
            default: goto L_0x000c;
        };
    L_0x000c:
        r10 = 0;
        return r10;
    L_0x000e:
        r10 = FLOAT_ZERO;
        return r10;
    L_0x0011:
        r10 = DOUBLE_ZERO;
        return r10;
    L_0x0014:
        r10 = LONG_ZERO;
        return r10;
    L_0x0017:
        r10 = INTEGER_ZERO;
        return r10;
    L_0x001a:
        r10 = java.lang.Boolean.FALSE;
        return r10;
    L_0x001d:
        r1 = 12;
        r2 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4 = 1;
        if (r12 == r1) goto L_0x0744;
    L_0x0026:
        if (r12 == r0) goto L_0x06f0;
    L_0x0028:
        r0 = 1111; // 0x457 float:1.557E-42 double:5.49E-321;
        if (r12 == r0) goto L_0x06ef;
    L_0x002c:
        r0 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;
        if (r12 == r0) goto L_0x06d5;
    L_0x0030:
        switch(r12) {
            case -7: goto L_0x06f0;
            case -6: goto L_0x0661;
            case -5: goto L_0x05ab;
            case -4: goto L_0x051a;
            case -3: goto L_0x04bd;
            case -2: goto L_0x04bd;
            case -1: goto L_0x0428;
            default: goto L_0x0033;
        };
    L_0x0033:
        switch(r12) {
            case 1: goto L_0x0744;
            case 2: goto L_0x03f5;
            case 3: goto L_0x03f5;
            case 4: goto L_0x036c;
            case 5: goto L_0x02e7;
            case 6: goto L_0x0299;
            case 7: goto L_0x024b;
            case 8: goto L_0x0299;
            default: goto L_0x0036;
        };
    L_0x0036:
        r0 = 58;
        r2 = 10;
        r3 = 2;
        r6 = 7;
        r7 = 45;
        r8 = 4;
        r9 = 0;
        switch(r12) {
            case 91: goto L_0x01a1;
            case 92: goto L_0x00f9;
            case 93: goto L_0x005c;
            default: goto L_0x0043;
        };
    L_0x0043:
        switch(r12) {
            case 2004: goto L_0x051a;
            case 2005: goto L_0x0428;
            default: goto L_0x0046;
        };
    L_0x0046:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.convert.badtypeconst";	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = java.lang.String.valueOf(r11);	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13, r0, r1);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "HY004";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x005c:
        r10 = r11 instanceof net.sourceforge.jtds.jdbc.DateTime;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0068;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0060:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (net.sourceforge.jtds.jdbc.DateTime) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.toTimestamp();	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0068:
        r10 = r11 instanceof java.sql.Timestamp;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x006d;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x006c:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x006d:
        r10 = r11 instanceof java.sql.Date;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x007e;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0071:
        r10 = new java.sql.Timestamp;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.sql.Date) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r13.getTime();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x007e:
        r10 = r11 instanceof java.sql.Time;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x008f;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0082:
        r10 = new java.sql.Timestamp;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.sql.Time) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r13.getTime();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x008f:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0093:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.String) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r10.length();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 <= r2) goto L_0x00ab;
    L_0x00a0:
        r1 = r10.charAt(r8);	 Catch:{ IllegalArgumentException -> 0x00e7 }
        if (r1 != r7) goto L_0x00ab;	 Catch:{ IllegalArgumentException -> 0x00e7 }
    L_0x00a6:
        r13 = java.sql.Timestamp.valueOf(r10);	 Catch:{ IllegalArgumentException -> 0x00e7 }
        return r13;	 Catch:{ IllegalArgumentException -> 0x00e7 }
    L_0x00ab:
        if (r13 <= r6) goto L_0x00c1;	 Catch:{ IllegalArgumentException -> 0x00e7 }
    L_0x00ad:
        r1 = r10.charAt(r8);	 Catch:{ IllegalArgumentException -> 0x00e7 }
        if (r1 != r7) goto L_0x00c1;	 Catch:{ IllegalArgumentException -> 0x00e7 }
    L_0x00b3:
        r13 = new java.sql.Timestamp;	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r0 = java.sql.Date.valueOf(r10);	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r0 = r0.getTime();	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r13.<init>(r0);	 Catch:{ IllegalArgumentException -> 0x00e7 }
        return r13;	 Catch:{ IllegalArgumentException -> 0x00e7 }
    L_0x00c1:
        if (r13 <= r6) goto L_0x00e7;	 Catch:{ IllegalArgumentException -> 0x00e7 }
    L_0x00c3:
        r13 = r10.charAt(r3);	 Catch:{ IllegalArgumentException -> 0x00e7 }
        if (r13 != r0) goto L_0x00e7;	 Catch:{ IllegalArgumentException -> 0x00e7 }
    L_0x00c9:
        r13 = new java.sql.Timestamp;	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r0 = r10.trim();	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r1 = "\\.";	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r0 = r0.split(r1);	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r0 = r0[r9];	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r0 = r0.trim();	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r0 = java.sql.Time.valueOf(r0);	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r0 = r0.getTime();	 Catch:{ IllegalArgumentException -> 0x00e7 }
        r13.<init>(r0);	 Catch:{ IllegalArgumentException -> 0x00e7 }
        return r13;
    L_0x00e7:
        r13 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "error.convert.badnumber";	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = net.sourceforge.jtds.jdbc.Messages.get(r0, r10, r1);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22000";	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r10, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x00f9:
        r10 = r11 instanceof net.sourceforge.jtds.jdbc.DateTime;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0105;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x00fd:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (net.sourceforge.jtds.jdbc.DateTime) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.toTime();	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0105:
        r10 = r11 instanceof java.sql.Time;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x010a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0109:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x010a:
        r10 = r11 instanceof java.sql.Date;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0111;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x010e:
        r10 = TIME_ZERO;	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0111:
        r10 = r11 instanceof java.sql.Timestamp;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = 1;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x013b;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0116:
        r10 = new java.util.GregorianCalendar;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>();	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = (java.util.Date) r0;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.setTime(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = 1970; // 0x7b2 float:2.76E-42 double:9.733E-321;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.set(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r10.set(r3, r9);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = 5;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.set(r0, r13);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = new java.sql.Time;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getTime();	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.getTime();	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x013b:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x013f:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.String) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = "\\.";	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.split(r1);	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10[r9];	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = r10.length();	 Catch:{ NumberFormatException -> 0x07bc }
        r4 = 8;
        if (r1 != r4) goto L_0x0165;
    L_0x015a:
        r3 = r10.charAt(r3);	 Catch:{ IllegalArgumentException -> 0x018f }
        if (r3 != r0) goto L_0x0165;	 Catch:{ IllegalArgumentException -> 0x018f }
    L_0x0160:
        r13 = java.sql.Time.valueOf(r10);	 Catch:{ IllegalArgumentException -> 0x018f }
        return r13;	 Catch:{ IllegalArgumentException -> 0x018f }
    L_0x0165:
        if (r1 <= r2) goto L_0x0181;	 Catch:{ IllegalArgumentException -> 0x018f }
    L_0x0167:
        r0 = r10.charAt(r8);	 Catch:{ IllegalArgumentException -> 0x018f }
        if (r0 != r7) goto L_0x0181;	 Catch:{ IllegalArgumentException -> 0x018f }
    L_0x016d:
        r0 = " ";	 Catch:{ IllegalArgumentException -> 0x018f }
        r0 = r10.split(r0);	 Catch:{ IllegalArgumentException -> 0x018f }
        r1 = r0.length;	 Catch:{ IllegalArgumentException -> 0x018f }
        if (r1 <= r13) goto L_0x018f;	 Catch:{ IllegalArgumentException -> 0x018f }
    L_0x0176:
        r13 = r0[r13];	 Catch:{ IllegalArgumentException -> 0x018f }
        r13 = r13.trim();	 Catch:{ IllegalArgumentException -> 0x018f }
        r13 = java.sql.Time.valueOf(r13);	 Catch:{ IllegalArgumentException -> 0x018f }
        return r13;	 Catch:{ IllegalArgumentException -> 0x018f }
    L_0x0181:
        if (r1 <= r6) goto L_0x018f;	 Catch:{ IllegalArgumentException -> 0x018f }
    L_0x0183:
        r13 = r10.charAt(r8);	 Catch:{ IllegalArgumentException -> 0x018f }
        if (r13 != r7) goto L_0x018f;	 Catch:{ IllegalArgumentException -> 0x018f }
    L_0x0189:
        java.sql.Date.valueOf(r10);	 Catch:{ IllegalArgumentException -> 0x018f }
        r13 = TIME_ZERO;	 Catch:{ IllegalArgumentException -> 0x018f }
        return r13;
    L_0x018f:
        r13 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "error.convert.badnumber";	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = net.sourceforge.jtds.jdbc.Messages.get(r0, r10, r1);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22000";	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r10, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01a1:
        r10 = r11 instanceof net.sourceforge.jtds.jdbc.DateTime;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x01ad;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01a5:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (net.sourceforge.jtds.jdbc.DateTime) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.toDate();	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01ad:
        r10 = r11 instanceof java.sql.Date;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x01b2;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01b1:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01b2:
        r10 = r11 instanceof java.sql.Time;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x01b9;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01b6:
        r10 = DATE_ZERO;	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01b9:
        r10 = r11 instanceof java.sql.Timestamp;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = 11;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x01e8;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01bf:
        r10 = new java.util.GregorianCalendar;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>();	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = (java.util.Date) r0;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.setTime(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r10.set(r13, r9);	 Catch:{ NumberFormatException -> 0x07bc }
        r10.set(r1, r9);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = 13;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.set(r13, r9);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = 14;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.set(r13, r9);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = new java.sql.Date;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getTime();	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.getTime();	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01e8:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x01ec:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.String) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = r10.length();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r1 <= r6) goto L_0x0206;
    L_0x01f9:
        if (r1 >= r13) goto L_0x0206;
    L_0x01fb:
        r13 = r10.charAt(r8);	 Catch:{ IllegalArgumentException -> 0x0239 }
        if (r13 != r7) goto L_0x0206;	 Catch:{ IllegalArgumentException -> 0x0239 }
    L_0x0201:
        r13 = java.sql.Date.valueOf(r10);	 Catch:{ IllegalArgumentException -> 0x0239 }
        return r13;	 Catch:{ IllegalArgumentException -> 0x0239 }
    L_0x0206:
        if (r1 <= r2) goto L_0x021f;	 Catch:{ IllegalArgumentException -> 0x0239 }
    L_0x0208:
        r13 = r10.charAt(r8);	 Catch:{ IllegalArgumentException -> 0x0239 }
        if (r13 != r7) goto L_0x021f;	 Catch:{ IllegalArgumentException -> 0x0239 }
    L_0x020e:
        r13 = " ";	 Catch:{ IllegalArgumentException -> 0x0239 }
        r13 = r10.split(r13);	 Catch:{ IllegalArgumentException -> 0x0239 }
        r13 = r13[r9];	 Catch:{ IllegalArgumentException -> 0x0239 }
        r13 = r13.trim();	 Catch:{ IllegalArgumentException -> 0x0239 }
        r13 = java.sql.Timestamp.valueOf(r13);	 Catch:{ IllegalArgumentException -> 0x0239 }
        return r13;	 Catch:{ IllegalArgumentException -> 0x0239 }
    L_0x021f:
        if (r1 <= r6) goto L_0x0239;	 Catch:{ IllegalArgumentException -> 0x0239 }
    L_0x0221:
        r13 = r10.charAt(r3);	 Catch:{ IllegalArgumentException -> 0x0239 }
        if (r13 != r0) goto L_0x0239;	 Catch:{ IllegalArgumentException -> 0x0239 }
    L_0x0227:
        r13 = "\\.";	 Catch:{ IllegalArgumentException -> 0x0239 }
        r13 = r10.split(r13);	 Catch:{ IllegalArgumentException -> 0x0239 }
        r13 = r13[r9];	 Catch:{ IllegalArgumentException -> 0x0239 }
        r13 = r13.trim();	 Catch:{ IllegalArgumentException -> 0x0239 }
        java.sql.Time.valueOf(r13);	 Catch:{ IllegalArgumentException -> 0x0239 }
        r13 = DATE_ZERO;	 Catch:{ IllegalArgumentException -> 0x0239 }
        return r13;
    L_0x0239:
        r13 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "error.convert.badnumber";	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = net.sourceforge.jtds.jdbc.Messages.get(r0, r10, r1);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22000";	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r10, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x024b:
        r10 = r11 instanceof java.lang.Float;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0250;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x024f:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0250:
        r10 = r11 instanceof java.lang.Byte;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0264;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0254:
        r10 = new java.lang.Float;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Byte) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.byteValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13 & 255;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (float) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0264:
        r10 = r11 instanceof java.lang.Number;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0275;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0268:
        r10 = new java.lang.Float;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Number) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.floatValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0275:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0286;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0279:
        r10 = new java.lang.Float;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.String) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0286:
        r10 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x028a:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Boolean) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.booleanValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0296;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0293:
        r10 = FLOAT_ONE;	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x0298;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0296:
        r10 = FLOAT_ZERO;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0298:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0299:
        r10 = r11 instanceof java.lang.Double;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x029e;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x029d:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x029e:
        r10 = r11 instanceof java.lang.Byte;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x02b2;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02a2:
        r10 = new java.lang.Double;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Byte) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.byteValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13 & 255;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = (double) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02b2:
        r10 = r11 instanceof java.lang.Number;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x02c3;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02b6:
        r10 = new java.lang.Double;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Number) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r13.doubleValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02c3:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x02d4;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02c7:
        r10 = new java.lang.Double;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.String) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02d4:
        r10 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02d8:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Boolean) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.booleanValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x02e4;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02e1:
        r10 = DOUBLE_ONE;	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x02e6;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02e4:
        r10 = DOUBLE_ZERO;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02e6:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02e7:
        r10 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x02fa;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02eb:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Boolean) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.booleanValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x02f7;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02f4:
        r10 = INTEGER_ONE;	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x02f9;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02f7:
        r10 = INTEGER_ZERO;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02f9:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02fa:
        r10 = r11 instanceof java.lang.Short;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x030b;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x02fe:
        r10 = new java.lang.Integer;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Short) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.shortValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x030b:
        r10 = r11 instanceof java.lang.Byte;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x031e;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x030f:
        r10 = new java.lang.Integer;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Byte) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.byteValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13 & 255;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x031e:
        r10 = r11 instanceof java.lang.Number;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x032a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0322:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Number) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.longValue();	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x033e;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x032a:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x032e:
        r10 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.String) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.longValue();	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x033e:
        r2 = -32768; // 0xffffffffffff8000 float:NaN double:NaN;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 < 0) goto L_0x035a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0344:
        r2 = 32767; // 0x7fff float:4.5916E-41 double:1.6189E-319;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 <= 0) goto L_0x034b;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x034a:
        goto L_0x035a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x034b:
        r10 = new java.lang.Integer;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.intValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x035a:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.convert.numericoverflow";	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13, r11, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22003";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x036c:
        r10 = r11 instanceof java.lang.Integer;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0371;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0370:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0371:
        r10 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0384;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0375:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Boolean) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.booleanValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0381;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x037e:
        r10 = INTEGER_ONE;	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x0383;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0381:
        r10 = INTEGER_ZERO;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0383:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0384:
        r10 = r11 instanceof java.lang.Short;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0395;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0388:
        r10 = new java.lang.Integer;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Short) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.shortValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0395:
        r10 = r11 instanceof java.lang.Byte;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x03a8;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0399:
        r10 = new java.lang.Integer;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Byte) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.byteValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13 & 255;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03a8:
        r10 = r11 instanceof java.lang.Number;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x03b4;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03ac:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Number) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.longValue();	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x03c8;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03b4:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03b8:
        r10 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.String) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.longValue();	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03c8:
        r4 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 < 0) goto L_0x03e3;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03cf:
        r10 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 <= 0) goto L_0x03d4;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03d3:
        goto L_0x03e3;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03d4:
        r10 = new java.lang.Integer;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.intValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03e3:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.convert.numericoverflow";	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13, r11, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22003";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03f5:
        r10 = r11 instanceof java.math.BigDecimal;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x03fa;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03f9:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03fa:
        r10 = r11 instanceof java.lang.Number;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0408;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x03fe:
        r10 = new java.math.BigDecimal;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11.toString();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0408:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0415;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x040c:
        r10 = new java.math.BigDecimal;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.String) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0415:
        r10 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0419:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Boolean) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.booleanValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0425;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0422:
        r10 = BIG_DECIMAL_ONE;	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x0427;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0425:
        r10 = BIG_DECIMAL_ZERO;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0427:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0428:
        r13 = r11 instanceof java.sql.Clob;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 == 0) goto L_0x042d;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x042c:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x042d:
        r13 = r11 instanceof java.sql.Blob;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 == 0) goto L_0x0478;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0431:
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.sql.Blob) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.getBinaryStream();	 Catch:{ IOException -> 0x0465 }
        r0 = new net.sourceforge.jtds.jdbc.ClobImpl;	 Catch:{ IOException -> 0x0465 }
        r10 = getConnection(r10);	 Catch:{ IOException -> 0x0465 }
        r0.<init>(r10);	 Catch:{ IOException -> 0x0465 }
        r10 = r0.setCharacterStream(r4);	 Catch:{ IOException -> 0x0465 }
    L_0x0445:
        r1 = r13.read();	 Catch:{ IOException -> 0x0465 }
        if (r1 < 0) goto L_0x045e;	 Catch:{ IOException -> 0x0465 }
    L_0x044b:
        r2 = hex;	 Catch:{ IOException -> 0x0465 }
        r3 = r1 >> 4;	 Catch:{ IOException -> 0x0465 }
        r2 = r2[r3];	 Catch:{ IOException -> 0x0465 }
        r10.write(r2);	 Catch:{ IOException -> 0x0465 }
        r2 = hex;	 Catch:{ IOException -> 0x0465 }
        r1 = r1 & 15;	 Catch:{ IOException -> 0x0465 }
        r1 = r2[r1];	 Catch:{ IOException -> 0x0465 }
        r10.write(r1);	 Catch:{ IOException -> 0x0465 }
        goto L_0x0445;	 Catch:{ IOException -> 0x0465 }
    L_0x045e:
        r10.close();	 Catch:{ IOException -> 0x0465 }
        r13.close();	 Catch:{ IOException -> 0x0465 }
        return r0;
    L_0x0465:
        r10 = move-exception;
        r13 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "error.generic.ioerror";	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getMessage();	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = net.sourceforge.jtds.jdbc.Messages.get(r0, r10);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "HY000";	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r10, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0478:
        r13 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 == 0) goto L_0x048b;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x047c:
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Boolean) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.booleanValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 == 0) goto L_0x0488;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0485:
        r13 = "1";	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x0493;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0488:
        r13 = "0";	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x0493;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x048b:
        r13 = r11 instanceof byte[];	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 != 0) goto L_0x0494;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x048f:
        r13 = r11.toString();	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0493:
        r11 = r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0494:
        r13 = r11 instanceof byte[];	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 == 0) goto L_0x04ac;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0498:
        r13 = new net.sourceforge.jtds.jdbc.ClobImpl;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = getConnection(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (byte[]) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = toHex(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        r13.setString(r4, r10);	 Catch:{ NumberFormatException -> 0x07bc }
        return r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04ac:
        r13 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04b0:
        r13 = new net.sourceforge.jtds.jdbc.ClobImpl;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = getConnection(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = (java.lang.String) r0;	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r10, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04bd:
        r10 = r11 instanceof byte[];	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x04c2;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04c1:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04c2:
        r10 = r11 instanceof java.sql.Blob;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x04d3;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04c6:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.sql.Blob) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.length();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (int) r0;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getBytes(r4, r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04d3:
        r10 = r11 instanceof java.sql.Clob;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x04f6;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04d7:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.sql.Clob) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.length();	 Catch:{ NumberFormatException -> 0x07bc }
        r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x07bc }
        if (r6 <= 0) goto L_0x04f0;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04e2:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.normalize.lobtoobig";	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22000";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04f0:
        r0 = (int) r0;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getSubString(r4, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r11 = r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04f6:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x050e;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04fa:
        if (r13 != 0) goto L_0x04fe;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04fc:
        r13 = "ISO-8859-1";	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x04fe:
        r10 = r11;	 Catch:{ UnsupportedEncodingException -> 0x0506 }
        r10 = (java.lang.String) r10;	 Catch:{ UnsupportedEncodingException -> 0x0506 }
        r10 = r10.getBytes(r13);	 Catch:{ UnsupportedEncodingException -> 0x0506 }
        return r10;
    L_0x0506:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.String) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getBytes();	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x050e:
        r10 = r11 instanceof net.sourceforge.jtds.jdbc.UniqueIdentifier;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0512:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (net.sourceforge.jtds.jdbc.UniqueIdentifier) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getBytes();	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x051a:
        r0 = r11 instanceof java.sql.Blob;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r0 == 0) goto L_0x051f;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x051e:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x051f:
        r0 = r11 instanceof byte[];	 Catch:{ NumberFormatException -> 0x07bc }
        if (r0 == 0) goto L_0x0530;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0523:
        r13 = new net.sourceforge.jtds.jdbc.BlobImpl;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = getConnection(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = (byte[]) r0;	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r10, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0530:
        r0 = r11 instanceof java.sql.Clob;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r0 == 0) goto L_0x0587;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0534:
        r0 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = (java.sql.Clob) r0;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 != 0) goto L_0x053f;
    L_0x0539:
        r1 = "ISO-8859-1";	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r13 = r1;	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        goto L_0x053f;	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
    L_0x053d:
        r10 = move-exception;	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        goto L_0x056b;	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
    L_0x053f:
        r1 = r0.getCharacterStream();	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r2 = new net.sourceforge.jtds.jdbc.BlobImpl;	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r3 = getConnection(r10);	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r2.<init>(r3);	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r3 = new java.io.BufferedWriter;	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r6 = new java.io.OutputStreamWriter;	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r7 = r2.setBinaryStream(r4);	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r6.<init>(r7, r13);	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r3.<init>(r6);	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
    L_0x055a:
        r6 = r1.read();	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        if (r6 < 0) goto L_0x0564;	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
    L_0x0560:
        r3.write(r6);	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        goto L_0x055a;	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
    L_0x0564:
        r3.close();	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        r1.close();	 Catch:{ UnsupportedEncodingException -> 0x057d, IOException -> 0x053d }
        return r2;
    L_0x056b:
        r13 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "error.generic.ioerror";	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getMessage();	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = net.sourceforge.jtds.jdbc.Messages.get(r0, r10);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "HY000";	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r10, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x057d:
        r1 = r0.length();	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = (int) r1;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r0.getSubString(r4, r1);	 Catch:{ NumberFormatException -> 0x07bc }
        r11 = r0;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0587:
        r0 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r0 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x058b:
        r0 = new net.sourceforge.jtds.jdbc.BlobImpl;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = getConnection(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        r0.<init>(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.String) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 != 0) goto L_0x059b;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0599:
        r13 = "ISO-8859-1";	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x059b:
        r13 = r10.getBytes(r13);	 Catch:{ UnsupportedEncodingException -> 0x05a3 }
        r0.setBytes(r4, r13);	 Catch:{ UnsupportedEncodingException -> 0x05a3 }
        goto L_0x05aa;
    L_0x05a3:
        r10 = r10.getBytes();	 Catch:{ NumberFormatException -> 0x07bc }
        r0.setBytes(r4, r10);	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05aa:
        return r0;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05ab:
        r10 = r11 instanceof java.math.BigDecimal;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x05df;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05af:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.math.BigDecimal) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = MIN_VALUE_LONG_BD;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r10.compareTo(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 < 0) goto L_0x05cd;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05ba:
        r13 = MAX_VALUE_LONG_BD;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r10.compareTo(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 <= 0) goto L_0x05c3;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05c2:
        goto L_0x05cd;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05c3:
        r13 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.longValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05cd:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.convert.numericoverflow";	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13, r11, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22003";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05df:
        r10 = r11 instanceof java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x05e4;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05e3:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05e4:
        r10 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x05f7;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05e8:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Boolean) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.booleanValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x05f4;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05f1:
        r10 = LONG_ONE;	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x05f6;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05f4:
        r10 = LONG_ZERO;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05f6:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05f7:
        r10 = r11 instanceof java.lang.Byte;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x060b;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x05fb:
        r10 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Byte) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.byteValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13 & 255;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = (long) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x060b:
        r10 = r11 instanceof java.math.BigInteger;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x063f;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x060f:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.math.BigInteger) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = MIN_VALUE_LONG_BI;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r10.compareTo(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 < 0) goto L_0x062d;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x061a:
        r13 = MAX_VALUE_LONG_BI;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r10.compareTo(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 <= 0) goto L_0x0623;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0622:
        goto L_0x062d;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0623:
        r13 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.longValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r13;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x062d:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.convert.numericoverflow";	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13, r11, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22003";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x063f:
        r10 = r11 instanceof java.lang.Number;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0650;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0643:
        r10 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Number) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r13.longValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0650:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0654:
        r10 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.String) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0661:
        r10 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0674;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0665:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Boolean) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.booleanValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0671;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x066e:
        r10 = INTEGER_ONE;	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x0673;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0671:
        r10 = INTEGER_ZERO;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0673:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0674:
        r10 = r11 instanceof java.lang.Byte;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0687;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0678:
        r10 = new java.lang.Integer;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.Byte) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.byteValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13 & 255;	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0687:
        r10 = r11 instanceof java.lang.Number;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0693;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x068b:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Number) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.longValue();	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x06a7;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0693:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0697:
        r10 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (java.lang.String) r13;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.longValue();	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06a7:
        r2 = -128; // 0xffffffffffffff80 float:NaN double:NaN;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 < 0) goto L_0x06c3;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06ad:
        r2 = 127; // 0x7f float:1.78E-43 double:6.27E-322;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 <= 0) goto L_0x06b4;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06b3:
        goto L_0x06c3;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06b4:
        r10 = new java.lang.Integer;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = new java.lang.Long;	 Catch:{ NumberFormatException -> 0x07bc }
        r13.<init>(r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.intValue();	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06c3:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.convert.numericoverflow";	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13, r11, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22003";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06d5:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.convert.badtypes";	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r11.getClass();	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r0.getName();	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13, r0, r1);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22005";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06ef:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06f0:
        r10 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x06f5;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06f4:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06f5:
        r10 = r11 instanceof java.lang.Number;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0708;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x06f9:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Number) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.intValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 != 0) goto L_0x0705;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0702:
        r10 = java.lang.Boolean.FALSE;	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x0707;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0705:
        r10 = java.lang.Boolean.TRUE;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0707:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0708:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x072a;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x070c:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.String) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.trim();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "1";	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = r13.equals(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 != 0) goto L_0x0727;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x071b:
        r13 = "true";	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r13.equalsIgnoreCase(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0724;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0723:
        goto L_0x0727;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0724:
        r10 = java.lang.Boolean.FALSE;	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x0729;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0727:
        r10 = java.lang.Boolean.TRUE;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0729:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x072a:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.convert.badtypes";	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r11.getClass();	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r0.getName();	 Catch:{ NumberFormatException -> 0x07bc }
        r1 = getJdbcTypeName(r12);	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13, r0, r1);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22005";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0744:
        r10 = r11 instanceof java.lang.String;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0749;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0748:
        return r11;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0749:
        r10 = r11 instanceof java.lang.Number;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0752;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x074d:
        r10 = r11.toString();	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0752:
        r10 = r11 instanceof java.lang.Boolean;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0765;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0756:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.lang.Boolean) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.booleanValue();	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0762;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x075f:
        r10 = "1";	 Catch:{ NumberFormatException -> 0x07bc }
        goto L_0x0764;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0762:
        r10 = "0";	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0764:
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0765:
        r10 = r11 instanceof java.sql.Clob;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x0788;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0769:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.sql.Clob) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.length();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 <= 0) goto L_0x0782;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0774:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.normalize.lobtoobig";	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22000";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0782:
        r13 = (int) r0;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getSubString(r4, r13);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0788:
        r10 = r11 instanceof java.sql.Blob;	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x07ab;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x078c:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (java.sql.Blob) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = r10.length();	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ NumberFormatException -> 0x07bc }
        if (r13 <= 0) goto L_0x07a5;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x0797:
        r10 = new java.sql.SQLException;	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = "error.normalize.lobtoobig";	 Catch:{ NumberFormatException -> 0x07bc }
        r13 = net.sourceforge.jtds.jdbc.Messages.get(r13);	 Catch:{ NumberFormatException -> 0x07bc }
        r0 = "22000";	 Catch:{ NumberFormatException -> 0x07bc }
        r10.<init>(r13, r0);	 Catch:{ NumberFormatException -> 0x07bc }
        throw r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x07a5:
        r13 = (int) r0;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = r10.getBytes(r4, r13);	 Catch:{ NumberFormatException -> 0x07bc }
        r11 = r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x07ab:
        r10 = r11 instanceof byte[];	 Catch:{ NumberFormatException -> 0x07bc }
        if (r10 == 0) goto L_0x07b7;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x07af:
        r10 = r11;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = (byte[]) r10;	 Catch:{ NumberFormatException -> 0x07bc }
        r10 = toHex(r10);	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;	 Catch:{ NumberFormatException -> 0x07bc }
    L_0x07b7:
        r10 = r11.toString();	 Catch:{ NumberFormatException -> 0x07bc }
        return r10;
    L_0x07bc:
        r10 = new java.sql.SQLException;
        r13 = "error.convert.badnumber";
        r11 = java.lang.String.valueOf(r11);
        r12 = getJdbcTypeName(r12);
        r11 = net.sourceforge.jtds.jdbc.Messages.get(r13, r11, r12);
        r12 = "22000";
        r10.<init>(r11, r12);
        throw r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.Support.convert(java.lang.Object, java.lang.Object, int, java.lang.String):java.lang.Object");
    }

    static int getJdbcType(Object obj) {
        return obj == null ? null : getJdbcType(obj.getClass());
    }

    static int getJdbcType(Class cls) {
        if (cls == null) {
            return 2000;
        }
        Object obj = typeMap.get(cls);
        if (obj == null) {
            return getJdbcType(cls.getSuperclass());
        }
        return ((Integer) obj).intValue();
    }

    static void embedData(StringBuffer stringBuffer, Object obj, boolean z, ConnectionJDBC2 connectionJDBC2) throws SQLException {
        stringBuffer.append(' ');
        if (obj == null) {
            stringBuffer.append("NULL ");
            return;
        }
        if (obj instanceof Blob) {
            Blob blob = (Blob) obj;
            obj = blob.getBytes(1, (int) blob.length());
        } else if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            obj = clob.getSubString(1, (int) clob.length());
        }
        if (obj instanceof DateTime) {
            stringBuffer.append('\'');
            stringBuffer.append(obj);
            stringBuffer.append('\'');
        } else {
            int i = 0;
            char c = '0';
            if (obj instanceof byte[]) {
                byte[] bArr = (byte[]) obj;
                z = bArr.length;
                if (z < false) {
                    stringBuffer.append('0');
                    stringBuffer.append('x');
                    if (z || connectionJDBC2.getTdsVersion() >= 3) {
                        boolean z2;
                        while (z2 < z) {
                            connectionJDBC2 = bArr[z2] & 255;
                            stringBuffer.append(hex[connectionJDBC2 >> 4]);
                            stringBuffer.append(hex[connectionJDBC2 & 15]);
                            z2++;
                        }
                    } else {
                        stringBuffer.append('0');
                        stringBuffer.append('0');
                    }
                }
            } else if (obj instanceof String) {
                String str = (String) obj;
                connectionJDBC2 = str.length();
                if (z) {
                    stringBuffer.append(true);
                }
                stringBuffer.append('\'');
                while (i < connectionJDBC2) {
                    z = str.charAt(i);
                    if (z) {
                        stringBuffer.append('\'');
                    }
                    stringBuffer.append(z);
                    i++;
                }
                stringBuffer.append('\'');
            } else if (obj instanceof Date) {
                z = new DateTime((Date) obj);
                stringBuffer.append('\'');
                stringBuffer.append(z);
                stringBuffer.append('\'');
            } else if (obj instanceof Time) {
                z = new DateTime((Time) obj);
                stringBuffer.append('\'');
                stringBuffer.append(z);
                stringBuffer.append('\'');
            } else if (obj instanceof Timestamp) {
                z = new DateTime((Timestamp) obj);
                stringBuffer.append('\'');
                stringBuffer.append(z);
                stringBuffer.append('\'');
            } else if (obj instanceof Boolean) {
                if (((Boolean) obj).booleanValue() != null) {
                    c = '1';
                }
                stringBuffer.append(c);
            } else if (obj instanceof BigDecimal) {
                obj = obj.toString();
                z = connectionJDBC2.getMaxPrecision();
                if (obj.charAt(0) == 45) {
                    z++;
                }
                if (obj.indexOf(46) >= null) {
                    z++;
                }
                if (obj.length() > z) {
                    stringBuffer.append(obj.substring(0, z));
                } else {
                    stringBuffer.append(obj);
                }
            } else {
                stringBuffer.append(obj.toString());
            }
        }
        stringBuffer.append(' ');
    }

    static String getStatementKey(String str, ParamInfo[] paramInfoArr, int i, String str2, boolean z, boolean z2) {
        if (i == 1) {
            i = new StringBuffer(((str2.length() + true) + str.length()) + (paramInfoArr.length * 11));
            i.append(z2 ? true : true);
            i.append(str2);
            i.append(str);
            for (String str22 : paramInfoArr) {
                i.append(str22.sqlType);
            }
        } else {
            i = new StringBuffer(str.length() + 2);
            i.append(z ? 84 : 70);
            i.append(str);
        }
        return i.toString();
    }

    static String getParameterDefinitions(ParamInfo[] paramInfoArr) {
        StringBuffer stringBuffer = new StringBuffer(paramInfoArr.length * 15);
        int i = 0;
        while (i < paramInfoArr.length) {
            if (paramInfoArr[i].name == null) {
                stringBuffer.append("@P");
                stringBuffer.append(i);
            } else {
                stringBuffer.append(paramInfoArr[i].name);
            }
            stringBuffer.append(' ');
            stringBuffer.append(paramInfoArr[i].sqlType);
            i++;
            if (i < paramInfoArr.length) {
                stringBuffer.append(',');
            }
        }
        return stringBuffer.toString();
    }

    static String substituteParamMarkers(String str, ParamInfo[] paramInfoArr) {
        char[] cArr = new char[(str.length() + (paramInfoArr.length * 7))];
        StringBuffer stringBuffer = new StringBuffer(4);
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < paramInfoArr.length; i3++) {
            int i4 = paramInfoArr[i3].markerPos;
            if (i4 > 0) {
                str.getChars(i, i4, cArr, i2);
                i2 += i4 - i;
                i4++;
                i = i2 + 1;
                cArr[i2] = ' ';
                i2 = i + 1;
                cArr[i] = '@';
                i = i2 + 1;
                cArr[i2] = 'P';
                stringBuffer.setLength(0);
                stringBuffer.append(i3);
                stringBuffer.getChars(0, stringBuffer.length(), cArr, i);
                i += stringBuffer.length();
                i2 = i + 1;
                cArr[i] = ' ';
                i = i4;
            }
        }
        if (i < str.length()) {
            str.getChars(i, str.length(), cArr, i2);
            i2 += str.length() - i;
        }
        return new String(cArr, 0, i2);
    }

    static String substituteParameters(String str, ParamInfo[] paramInfoArr, ConnectionJDBC2 connectionJDBC2) throws SQLException {
        int length = str.length();
        int i = 0;
        while (i < paramInfoArr.length) {
            if (paramInfoArr[i].isRetVal || paramInfoArr[i].isSet || paramInfoArr[i].isOutput) {
                Object obj = paramInfoArr[i].value;
                if ((obj instanceof InputStream) || (obj instanceof Reader)) {
                    try {
                        if (!(paramInfoArr[i].jdbcType == -1 || paramInfoArr[i].jdbcType == 2005)) {
                            if (paramInfoArr[i].jdbcType != 12) {
                                obj = paramInfoArr[i].getBytes("US-ASCII");
                                paramInfoArr[i].value = obj;
                            }
                        }
                        obj = paramInfoArr[i].getString("US-ASCII");
                        paramInfoArr[i].value = obj;
                    } catch (String str2) {
                        throw new SQLException(Messages.get("error.generic.ioerror", str2.getMessage()), "HY000");
                    }
                }
                length = obj instanceof String ? length + (((String) obj).length() + 5) : obj instanceof byte[] ? length + ((((byte[]) obj).length * 2) + 4) : length + 32;
                i++;
            } else {
                throw new SQLException(Messages.get("error.prepare.paramnotset", Integer.toString(i + 1)), "07000");
            }
        }
        StringBuffer stringBuffer = new StringBuffer(length + 16);
        int i2 = 0;
        for (length = 0; length < paramInfoArr.length; length++) {
            int i3 = paramInfoArr[length].markerPos;
            if (i3 > 0) {
                stringBuffer.append(str2.substring(i2, paramInfoArr[length].markerPos));
                i3++;
                boolean z = connectionJDBC2.getTdsVersion() >= 3 && paramInfoArr[length].isUnicode;
                embedData(stringBuffer, paramInfoArr[length].value, z, connectionJDBC2);
                i2 = i3;
            }
        }
        if (i2 < str2.length()) {
            stringBuffer.append(str2.substring(i2));
        }
        return stringBuffer.toString();
    }

    static byte[] encodeString(java.lang.String r0, java.lang.String r1) {
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
        r0 = r1.getBytes(r0);	 Catch:{ UnsupportedEncodingException -> 0x0005 }
        return r0;
    L_0x0005:
        r0 = r1.getBytes();
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.Support.encodeString(java.lang.String, java.lang.String):byte[]");
    }

    public static SQLWarning linkException(SQLWarning sQLWarning, Throwable th) {
        return (SQLWarning) linkException((Exception) sQLWarning, th);
    }

    public static SQLException linkException(SQLException sQLException, Throwable th) {
        return (SQLException) linkException((Exception) sQLException, th);
    }

    public static java.lang.Throwable linkException(java.lang.Exception r5, java.lang.Throwable r6) {
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
        r0 = r5.getClass();
        r1 = 1;
        r2 = new java.lang.Class[r1];
        r3 = class$java$lang$Throwable;
        if (r3 != 0) goto L_0x0014;
    L_0x000b:
        r3 = "java.lang.Throwable";
        r3 = class$(r3);
        class$java$lang$Throwable = r3;
        goto L_0x0016;
    L_0x0014:
        r3 = class$java$lang$Throwable;
    L_0x0016:
        r4 = 0;
        r2[r4] = r3;
        r1 = new java.lang.Object[r1];
        r1[r4] = r6;
        r3 = "initCause";	 Catch:{ NoSuchMethodException -> 0x0027, Exception -> 0x0032 }
        r0 = r0.getMethod(r3, r2);	 Catch:{ NoSuchMethodException -> 0x0027, Exception -> 0x0032 }
        r0.invoke(r5, r1);	 Catch:{ NoSuchMethodException -> 0x0027, Exception -> 0x0032 }
        goto L_0x0032;
    L_0x0027:
        r0 = net.sourceforge.jtds.util.Logger.isActive();
        if (r0 == 0) goto L_0x0032;
    L_0x002d:
        r6 = (java.lang.Exception) r6;
        net.sourceforge.jtds.util.Logger.logException(r6);
    L_0x0032:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.Support.linkException(java.lang.Exception, java.lang.Throwable):java.lang.Throwable");
    }

    public static long timeToZone(java.util.Date date, Calendar calendar) {
        java.util.Date time = calendar.getTime();
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            if (!Driver.JDBC3 && (date instanceof Timestamp)) {
                gregorianCalendar.set(14, ((Timestamp) date).getNanos() / 1000000);
            }
            calendar.set(11, gregorianCalendar.get(11));
            calendar.set(12, gregorianCalendar.get(12));
            calendar.set(13, gregorianCalendar.get(13));
            calendar.set(14, gregorianCalendar.get(14));
            calendar.set(1, gregorianCalendar.get(1));
            calendar.set(2, gregorianCalendar.get(2));
            calendar.set(5, gregorianCalendar.get(5));
            long time2 = calendar.getTime().getTime();
            return time2;
        } finally {
            calendar.setTime(time);
        }
    }

    public static long timeFromZone(java.util.Date date, Calendar calendar) {
        java.util.Date time = calendar.getTime();
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            calendar.setTime(date);
            if (!Driver.JDBC3 && (date instanceof Timestamp)) {
                calendar.set(14, ((Timestamp) date).getNanos() / 1000000);
            }
            gregorianCalendar.set(11, calendar.get(11));
            gregorianCalendar.set(12, calendar.get(12));
            gregorianCalendar.set(13, calendar.get(13));
            gregorianCalendar.set(14, calendar.get(14));
            gregorianCalendar.set(1, calendar.get(1));
            gregorianCalendar.set(2, calendar.get(2));
            gregorianCalendar.set(5, calendar.get(5));
            long time2 = gregorianCalendar.getTime().getTime();
            return time2;
        } finally {
            calendar.setTime(time);
        }
    }

    public static Object convertLOB(Object obj) throws SQLException {
        if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            return clob.getSubString(1, (int) clob.length());
        } else if (!(obj instanceof Blob)) {
            return obj;
        } else {
            Blob blob = (Blob) obj;
            return blob.getBytes(1, (int) blob.length());
        }
    }

    public static boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    private static ConnectionJDBC2 getConnection(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("callerReference cannot be null.");
        }
        try {
            if (obj instanceof Connection) {
                obj = (Connection) obj;
            } else if (obj instanceof Statement) {
                obj = ((Statement) obj).getConnection();
            } else if (obj instanceof ResultSet) {
                obj = ((ResultSet) obj).getStatement().getConnection();
            } else {
                throw new IllegalArgumentException("callerReference is invalid.");
            }
            return (ConnectionJDBC2) obj;
        } catch (Object obj2) {
            throw new IllegalStateException(obj2.getMessage());
        }
    }

    private Support() {
    }
}
