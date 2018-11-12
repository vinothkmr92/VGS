package net.sourceforge.jtds.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import net.sourceforge.jtds.ssl.Ssl;

public class Driver implements java.sql.Driver {
    public static final String APPNAME = "prop.appname";
    public static final String AUTOCOMMIT = "prop.autocommit";
    public static final String BATCHSIZE = "prop.batchsize";
    public static final String BINDADDRESS = "prop.bindaddress";
    public static final String BUFFERDIR = "prop.bufferdir";
    public static final String BUFFERMAXMEMORY = "prop.buffermaxmemory";
    public static final String BUFFERMINPACKETS = "prop.bufferminpackets";
    public static final String CACHEMETA = "prop.cachemetadata";
    public static final String CHARSET = "prop.charset";
    public static final String DATABASENAME = "prop.databasename";
    public static final String DOMAIN = "prop.domain";
    public static final String INSTANCE = "prop.instance";
    public static final boolean JDBC3;
    public static final String LANGUAGE = "prop.language";
    public static final String LASTUPDATECOUNT = "prop.lastupdatecount";
    public static final String LOBBUFFER = "prop.lobbuffer";
    public static final String LOGFILE = "prop.logfile";
    public static final String LOGINTIMEOUT = "prop.logintimeout";
    public static final String MACADDRESS = "prop.macaddress";
    static final int MAJOR_VERSION = 1;
    public static final String MAXSTATEMENTS = "prop.maxstatements";
    static final int MINOR_VERSION = 2;
    static final String MISC_VERSION = ".7";
    public static final String NAMEDPIPE = "prop.namedpipe";
    public static final String PACKETSIZE = "prop.packetsize";
    public static final String PASSWORD = "prop.password";
    public static final String PORTNUMBER = "prop.portnumber";
    public static final String PREPARESQL = "prop.preparesql";
    public static final String PROCESSID = "prop.processid";
    public static final String PROGNAME = "prop.progname";
    public static final String SENDSTRINGPARAMETERSASUNICODE = "prop.useunicode";
    public static final String SERVERNAME = "prop.servername";
    public static final String SERVERTYPE = "prop.servertype";
    public static final String SOKEEPALIVE = "prop.sokeepalive";
    public static final String SOTIMEOUT = "prop.sotimeout";
    public static final int SQLSERVER = 1;
    public static final String SSL = "prop.ssl";
    public static final int SYBASE = 2;
    public static final String TCPNODELAY = "prop.tcpnodelay";
    public static final String TDS = "prop.tds";
    public static final int TDS42 = 1;
    public static final int TDS50 = 2;
    public static final int TDS70 = 3;
    public static final int TDS80 = 4;
    public static final int TDS81 = 5;
    public static final String USECURSORS = "prop.usecursors";
    public static final String USEJCIFS = "prop.usejcifs";
    public static final String USELOBS = "prop.uselobs";
    public static final String USENTLMV2 = "prop.usentlmv2";
    public static final String USER = "prop.user";
    public static final String WSID = "prop.wsid";
    public static final String XAEMULATION = "prop.xaemulation";
    private static String driverPrefix = "jdbc:jtds:";

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 2;
    }

    public boolean jdbcCompliant() {
        return false;
    }

    static {
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
        r0 = "1.4";
        r1 = "java.specification.version";
        r1 = java.lang.System.getProperty(r1);
        r0 = r0.compareTo(r1);
        if (r0 > 0) goto L_0x0010;
    L_0x000e:
        r0 = 1;
        goto L_0x0011;
    L_0x0010:
        r0 = 0;
    L_0x0011:
        JDBC3 = r0;
        r0 = new net.sourceforge.jtds.jdbc.Driver;	 Catch:{ SQLException -> 0x001b }
        r0.<init>();	 Catch:{ SQLException -> 0x001b }
        java.sql.DriverManager.registerDriver(r0);	 Catch:{ SQLException -> 0x001b }
    L_0x001b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.Driver.<clinit>():void");
    }

    public static final String getVersion() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("1.2");
        stringBuffer.append(MISC_VERSION == null ? "" : MISC_VERSION);
        return stringBuffer.toString();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("jTDS ");
        stringBuffer.append(getVersion());
        return stringBuffer.toString();
    }

    public boolean acceptsURL(String str) throws SQLException {
        return str == null ? null : str.toLowerCase().startsWith(driverPrefix);
    }

    public Connection connect(String str, Properties properties) throws SQLException {
        if (str != null) {
            if (str.toLowerCase().startsWith(driverPrefix)) {
                properties = setupConnectProperties(str, properties);
                if (JDBC3) {
                    return new ConnectionJDBC3(str, properties);
                }
                return new ConnectionJDBC2(str, properties);
            }
        }
        return null;
    }

    public DriverPropertyInfo[] getPropertyInfo(String str, Properties properties) throws SQLException {
        if (properties == null) {
            properties = new Properties();
        }
        properties = parseURL(str, properties);
        if (properties == null) {
            throw new SQLException(Messages.get("error.driver.badurl", (Object) str), "08001");
        }
        str = DefaultProperties.addDefaultProperties(properties);
        properties = new HashMap();
        Map hashMap = new HashMap();
        Messages.loadDriverProperties(properties, hashMap);
        Map createChoicesMap = createChoicesMap();
        Map createRequiredTrueMap = createRequiredTrueMap();
        DriverPropertyInfo[] driverPropertyInfoArr = new DriverPropertyInfo[properties.size()];
        int i = 0;
        for (Entry entry : properties.entrySet()) {
            String str2 = (String) entry.getKey();
            String str3 = (String) entry.getValue();
            DriverPropertyInfo driverPropertyInfo = new DriverPropertyInfo(str3, str.getProperty(str3));
            driverPropertyInfo.description = (String) hashMap.get(str2);
            driverPropertyInfo.required = createRequiredTrueMap.containsKey(str3);
            if (createChoicesMap.containsKey(str3)) {
                driverPropertyInfo.choices = (String[]) createChoicesMap.get(str3);
            }
            driverPropertyInfoArr[i] = driverPropertyInfo;
            i++;
        }
        return driverPropertyInfoArr;
    }

    private Properties setupConnectProperties(String str, Properties properties) throws SQLException {
        properties = parseURL(str, properties);
        if (properties == null) {
            throw new SQLException(Messages.get("error.driver.badurl", (Object) str), "08001");
        }
        if (properties.getProperty(Messages.get(LOGINTIMEOUT)) == null) {
            properties.setProperty(Messages.get(LOGINTIMEOUT), Integer.toString(DriverManager.getLoginTimeout()));
        }
        return DefaultProperties.addDefaultProperties(properties);
    }

    private static Map createChoicesMap() {
        Map hashMap = new HashMap();
        Object obj = new String[]{"true", "false"};
        hashMap.put(Messages.get(CACHEMETA), obj);
        hashMap.put(Messages.get(LASTUPDATECOUNT), obj);
        hashMap.put(Messages.get(NAMEDPIPE), obj);
        hashMap.put(Messages.get(TCPNODELAY), obj);
        hashMap.put(Messages.get(SENDSTRINGPARAMETERSASUNICODE), obj);
        hashMap.put(Messages.get(USECURSORS), obj);
        hashMap.put(Messages.get(USELOBS), obj);
        hashMap.put(Messages.get(XAEMULATION), obj);
        hashMap.put(Messages.get(PREPARESQL), new String[]{String.valueOf(0), String.valueOf(1), String.valueOf(2), String.valueOf(3)});
        hashMap.put(Messages.get(SERVERTYPE), new String[]{String.valueOf(1), String.valueOf(2)});
        hashMap.put(Messages.get(TDS), new String[]{DefaultProperties.TDS_VERSION_42, DefaultProperties.TDS_VERSION_50, DefaultProperties.TDS_VERSION_70, DefaultProperties.TDS_VERSION_80});
        hashMap.put(Messages.get(SSL), new String[]{"off", Ssl.SSL_REQUEST, Ssl.SSL_REQUIRE, Ssl.SSL_AUTHENTICATE});
        return hashMap;
    }

    private static Map createRequiredTrueMap() {
        Map hashMap = new HashMap();
        hashMap.put(Messages.get(SERVERNAME), null);
        hashMap.put(Messages.get(SERVERTYPE), null);
        return hashMap;
    }

    private static java.util.Properties parseURL(java.lang.String r6, java.util.Properties r7) {
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
        r0 = new java.util.Properties;
        r0.<init>();
        r1 = r7.propertyNames();
    L_0x0009:
        r2 = r1.hasMoreElements();
        if (r2 == 0) goto L_0x0023;
    L_0x000f:
        r2 = r1.nextElement();
        r2 = (java.lang.String) r2;
        r3 = r7.getProperty(r2);
        if (r3 == 0) goto L_0x0009;
    L_0x001b:
        r2 = r2.toUpperCase();
        r0.setProperty(r2, r3);
        goto L_0x0009;
    L_0x0023:
        r7 = new java.lang.StringBuffer;
        r1 = 16;
        r7.<init>(r1);
        r1 = 0;
        r2 = nextToken(r6, r1, r7);
        r3 = "jdbc";
        r4 = r7.toString();
        r3 = r3.equalsIgnoreCase(r4);
        r4 = 0;
        if (r3 != 0) goto L_0x003d;
    L_0x003c:
        return r4;
    L_0x003d:
        r2 = nextToken(r6, r2, r7);
        r3 = "jtds";
        r5 = r7.toString();
        r3 = r3.equalsIgnoreCase(r5);
        if (r3 != 0) goto L_0x004e;
    L_0x004d:
        return r4;
    L_0x004e:
        r2 = nextToken(r6, r2, r7);
        r3 = r7.toString();
        r3 = r3.toLowerCase();
        r3 = net.sourceforge.jtds.jdbc.DefaultProperties.getServerType(r3);
        if (r3 != 0) goto L_0x0061;
    L_0x0060:
        return r4;
    L_0x0061:
        r5 = "prop.servertype";
        r5 = net.sourceforge.jtds.jdbc.Messages.get(r5);
        r3 = java.lang.String.valueOf(r3);
        r0.setProperty(r5, r3);
        r2 = nextToken(r6, r2, r7);
        r3 = r7.length();
        if (r3 <= 0) goto L_0x0079;
    L_0x0078:
        return r4;
    L_0x0079:
        r2 = nextToken(r6, r2, r7);
        r3 = r7.toString();
        r5 = r3.length();
        if (r5 != 0) goto L_0x009a;
    L_0x0087:
        r3 = "prop.servername";
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r3);
        r3 = r0.getProperty(r3);
        if (r3 == 0) goto L_0x0099;
    L_0x0093:
        r5 = r3.length();
        if (r5 != 0) goto L_0x009a;
    L_0x0099:
        return r4;
    L_0x009a:
        r5 = "prop.servername";
        r5 = net.sourceforge.jtds.jdbc.Messages.get(r5);
        r0.setProperty(r5, r3);
        r3 = r2 + -1;
        r3 = r6.charAt(r3);
        r5 = 58;
        if (r3 != r5) goto L_0x00ce;
    L_0x00ad:
        r3 = r6.length();
        if (r2 >= r3) goto L_0x00ce;
    L_0x00b3:
        r2 = nextToken(r6, r2, r7);
        r3 = r7.toString();	 Catch:{ NumberFormatException -> 0x00cd }
        r3 = java.lang.Integer.parseInt(r3);	 Catch:{ NumberFormatException -> 0x00cd }
        r5 = "prop.portnumber";	 Catch:{ NumberFormatException -> 0x00cd }
        r5 = net.sourceforge.jtds.jdbc.Messages.get(r5);	 Catch:{ NumberFormatException -> 0x00cd }
        r3 = java.lang.Integer.toString(r3);	 Catch:{ NumberFormatException -> 0x00cd }
        r0.setProperty(r5, r3);	 Catch:{ NumberFormatException -> 0x00cd }
        goto L_0x00ce;
    L_0x00cd:
        return r4;
    L_0x00ce:
        r3 = r2 + -1;
        r3 = r6.charAt(r3);
        r4 = 47;
        if (r3 != r4) goto L_0x00ef;
    L_0x00d8:
        r3 = r6.length();
        if (r2 >= r3) goto L_0x00ef;
    L_0x00de:
        r2 = nextToken(r6, r2, r7);
        r3 = "prop.databasename";
        r3 = net.sourceforge.jtds.jdbc.Messages.get(r3);
        r4 = r7.toString();
        r0.setProperty(r3, r4);
    L_0x00ef:
        r3 = r2 + -1;
        r3 = r6.charAt(r3);
        r4 = 59;
        if (r3 != r4) goto L_0x0133;
    L_0x00f9:
        r3 = r6.length();
        if (r2 >= r3) goto L_0x0133;
    L_0x00ff:
        r2 = nextToken(r6, r2, r7);
        r3 = r7.toString();
        r4 = 61;
        r4 = r3.indexOf(r4);
        if (r4 <= 0) goto L_0x0129;
    L_0x010f:
        r5 = r3.length();
        r5 = r5 + -1;
        if (r4 >= r5) goto L_0x0129;
    L_0x0117:
        r5 = r3.substring(r1, r4);
        r5 = r5.toUpperCase();
        r4 = r4 + 1;
        r3 = r3.substring(r4);
        r0.setProperty(r5, r3);
        goto L_0x00ef;
    L_0x0129:
        r3 = r3.toUpperCase();
        r4 = "";
        r0.setProperty(r3, r4);
        goto L_0x00ef;
    L_0x0133:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.Driver.parseURL(java.lang.String, java.util.Properties):java.util.Properties");
    }

    private static int nextToken(String str, int i, StringBuffer stringBuffer) {
        int i2;
        stringBuffer.setLength(0);
        loop0:
        while (true) {
            Object obj = null;
            while (i < str.length()) {
                i2 = i + 1;
                i = str.charAt(i);
                if (obj == null) {
                    if (i != 58) {
                        if (i != 59) {
                            if (i == 47) {
                                break loop0;
                            }
                        }
                        break loop0;
                    }
                    break loop0;
                }
                if (i == 91) {
                    obj = 1;
                } else if (i == 93) {
                    i = i2;
                } else {
                    stringBuffer.append(i);
                }
                i = i2;
            }
            return i;
        }
        if (i2 < str.length() && str.charAt(i2) == 47) {
            return i2 + 1;
        }
        return i2;
    }

    public static void main(String[] strArr) {
        strArr = System.out;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("jTDS ");
        stringBuffer.append(getVersion());
        strArr.println(stringBuffer.toString());
    }
}
