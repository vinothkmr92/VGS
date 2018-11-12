package net.sourceforge.jtds.jdbc;

import java.sql.SQLException;
import java.util.HashMap;

public final class CharsetInfo {
    private static final String CHARSETS_RESOURCE_NAME = "net/sourceforge/jtds/jdbc/Charsets.properties";
    private static final HashMap charsets = new HashMap();
    static /* synthetic */ Class class$net$sourceforge$jtds$jdbc$CharsetInfo;
    private static final HashMap lcidToCharsetMap = new HashMap();
    private static final CharsetInfo[] sortToCharsetMap = new CharsetInfo[256];
    private final String charset;
    private final boolean wideChars;

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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = new java.util.HashMap;
        r0.<init>();
        charsets = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        lcidToCharsetMap = r0;
        r0 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
        r0 = new net.sourceforge.jtds.jdbc.CharsetInfo[r0];
        sortToCharsetMap = r0;
        r0 = 0;
        r1 = java.lang.Thread.currentThread();	 Catch:{ IOException -> 0x00bb }
        r1 = r1.getContextClassLoader();	 Catch:{ IOException -> 0x00bb }
        if (r1 == 0) goto L_0x0026;	 Catch:{ IOException -> 0x00bb }
    L_0x001f:
        r2 = "net/sourceforge/jtds/jdbc/Charsets.properties";	 Catch:{ IOException -> 0x00bb }
        r1 = r1.getResourceAsStream(r2);	 Catch:{ IOException -> 0x00bb }
        r0 = r1;	 Catch:{ IOException -> 0x00bb }
    L_0x0026:
        if (r0 != 0) goto L_0x0034;	 Catch:{ IOException -> 0x00bb }
    L_0x0028:
        r1 = new net.sourceforge.jtds.jdbc.CharsetInfo$1;	 Catch:{ IOException -> 0x00bb }
        r1.<init>();	 Catch:{ IOException -> 0x00bb }
        r1 = java.security.AccessController.doPrivileged(r1);	 Catch:{ IOException -> 0x00bb }
        r1 = (java.io.InputStream) r1;	 Catch:{ IOException -> 0x00bb }
        r0 = r1;	 Catch:{ IOException -> 0x00bb }
    L_0x0034:
        if (r0 == 0) goto L_0x00ae;	 Catch:{ IOException -> 0x00bb }
    L_0x0036:
        r1 = new java.util.Properties;	 Catch:{ IOException -> 0x00bb }
        r1.<init>();	 Catch:{ IOException -> 0x00bb }
        r1.load(r0);	 Catch:{ IOException -> 0x00bb }
        r2 = new java.util.HashMap;	 Catch:{ IOException -> 0x00bb }
        r2.<init>();	 Catch:{ IOException -> 0x00bb }
        r3 = r1.propertyNames();	 Catch:{ IOException -> 0x00bb }
    L_0x0047:
        r4 = r3.hasMoreElements();	 Catch:{ IOException -> 0x00bb }
        if (r4 == 0) goto L_0x00b3;	 Catch:{ IOException -> 0x00bb }
    L_0x004d:
        r4 = r3.nextElement();	 Catch:{ IOException -> 0x00bb }
        r4 = (java.lang.String) r4;	 Catch:{ IOException -> 0x00bb }
        r5 = new net.sourceforge.jtds.jdbc.CharsetInfo;	 Catch:{ IOException -> 0x00bb }
        r6 = r1.getProperty(r4);	 Catch:{ IOException -> 0x00bb }
        r5.<init>(r6);	 Catch:{ IOException -> 0x00bb }
        r6 = r5.getCharset();	 Catch:{ IOException -> 0x00bb }
        r6 = r2.get(r6);	 Catch:{ IOException -> 0x00bb }
        r6 = (net.sourceforge.jtds.jdbc.CharsetInfo) r6;	 Catch:{ IOException -> 0x00bb }
        if (r6 == 0) goto L_0x007b;	 Catch:{ IOException -> 0x00bb }
    L_0x0068:
        r7 = r6.isWideChars();	 Catch:{ IOException -> 0x00bb }
        r5 = r5.isWideChars();	 Catch:{ IOException -> 0x00bb }
        if (r7 == r5) goto L_0x007a;	 Catch:{ IOException -> 0x00bb }
    L_0x0072:
        r1 = new java.lang.IllegalStateException;	 Catch:{ IOException -> 0x00bb }
        r2 = "Inconsistent Charsets.properties";	 Catch:{ IOException -> 0x00bb }
        r1.<init>(r2);	 Catch:{ IOException -> 0x00bb }
        throw r1;	 Catch:{ IOException -> 0x00bb }
    L_0x007a:
        r5 = r6;	 Catch:{ IOException -> 0x00bb }
    L_0x007b:
        r6 = "LCID_";	 Catch:{ IOException -> 0x00bb }
        r6 = r4.startsWith(r6);	 Catch:{ IOException -> 0x00bb }
        r7 = 5;	 Catch:{ IOException -> 0x00bb }
        if (r6 == 0) goto L_0x0093;	 Catch:{ IOException -> 0x00bb }
    L_0x0084:
        r6 = new java.lang.Integer;	 Catch:{ IOException -> 0x00bb }
        r4 = r4.substring(r7);	 Catch:{ IOException -> 0x00bb }
        r6.<init>(r4);	 Catch:{ IOException -> 0x00bb }
        r4 = lcidToCharsetMap;	 Catch:{ IOException -> 0x00bb }
        r4.put(r6, r5);	 Catch:{ IOException -> 0x00bb }
        goto L_0x0047;	 Catch:{ IOException -> 0x00bb }
    L_0x0093:
        r6 = "SORT_";	 Catch:{ IOException -> 0x00bb }
        r6 = r4.startsWith(r6);	 Catch:{ IOException -> 0x00bb }
        if (r6 == 0) goto L_0x00a8;	 Catch:{ IOException -> 0x00bb }
    L_0x009b:
        r6 = sortToCharsetMap;	 Catch:{ IOException -> 0x00bb }
        r4 = r4.substring(r7);	 Catch:{ IOException -> 0x00bb }
        r4 = java.lang.Integer.parseInt(r4);	 Catch:{ IOException -> 0x00bb }
        r6[r4] = r5;	 Catch:{ IOException -> 0x00bb }
        goto L_0x0047;	 Catch:{ IOException -> 0x00bb }
    L_0x00a8:
        r6 = charsets;	 Catch:{ IOException -> 0x00bb }
        r6.put(r4, r5);	 Catch:{ IOException -> 0x00bb }
        goto L_0x0047;	 Catch:{ IOException -> 0x00bb }
    L_0x00ae:
        r1 = "Can't load Charsets.properties";	 Catch:{ IOException -> 0x00bb }
        net.sourceforge.jtds.util.Logger.println(r1);	 Catch:{ IOException -> 0x00bb }
    L_0x00b3:
        if (r0 == 0) goto L_0x00c2;
    L_0x00b5:
        r0.close();	 Catch:{ Exception -> 0x00c2 }
        goto L_0x00c2;
    L_0x00b9:
        r1 = move-exception;
        goto L_0x00c3;
    L_0x00bb:
        r1 = move-exception;
        net.sourceforge.jtds.util.Logger.logException(r1);	 Catch:{ all -> 0x00b9 }
        if (r0 == 0) goto L_0x00c2;
    L_0x00c1:
        goto L_0x00b5;
    L_0x00c2:
        return;
    L_0x00c3:
        if (r0 == 0) goto L_0x00c8;
    L_0x00c5:
        r0.close();	 Catch:{ Exception -> 0x00c8 }
    L_0x00c8:
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.CharsetInfo.<clinit>():void");
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (String str2) {
            throw new NoClassDefFoundError(str2.getMessage());
        }
    }

    public static CharsetInfo getCharset(String str) {
        return (CharsetInfo) charsets.get(str.toUpperCase());
    }

    public static CharsetInfo getCharsetForLCID(int i) {
        return (CharsetInfo) lcidToCharsetMap.get(new Integer(i));
    }

    public static CharsetInfo getCharsetForSortOrder(int i) {
        return sortToCharsetMap[i];
    }

    public static CharsetInfo getCharset(byte[] bArr) throws SQLException {
        CharsetInfo charsetForSortOrder;
        if (bArr[4] != (byte) 0) {
            charsetForSortOrder = getCharsetForSortOrder(bArr[4] & 255);
        } else {
            charsetForSortOrder = getCharsetForLCID((((bArr[2] & 15) << 16) | ((bArr[1] & 255) << 8)) | (bArr[0] & 255));
        }
        if (charsetForSortOrder != null) {
            return charsetForSortOrder;
        }
        throw new SQLException(Messages.get("error.charset.nocollation", Support.toHex(bArr)), "2C000");
    }

    public CharsetInfo(String str) {
        this.wideChars = "1".equals(str.substring(0, 1)) ^ true;
        this.charset = str.substring(2);
    }

    public String getCharset() {
        return this.charset;
    }

    public boolean isWideChars() {
        return this.wideChars;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharsetInfo)) {
            return false;
        }
        return this.charset.equals(((CharsetInfo) obj).charset) != null;
    }

    public int hashCode() {
        return this.charset.hashCode();
    }

    public String toString() {
        return this.charset;
    }
}
