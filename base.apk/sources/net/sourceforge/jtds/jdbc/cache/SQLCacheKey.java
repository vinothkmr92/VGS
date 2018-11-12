package net.sourceforge.jtds.jdbc.cache;

import net.sourceforge.jtds.jdbc.ConnectionJDBC2;

public class SQLCacheKey {
    private final int hashCode;
    private final int majorVersion;
    private final int minorVersion;
    private final int serverType;
    private final String sql;

    public SQLCacheKey(String str, ConnectionJDBC2 connectionJDBC2) {
        this.sql = str;
        this.serverType = connectionJDBC2.getServerType();
        this.majorVersion = connectionJDBC2.getDatabaseMajorVersion();
        this.minorVersion = connectionJDBC2.getDatabaseMinorVersion();
        this.hashCode = str.hashCode() ^ (((this.serverType << 24) | (this.majorVersion << 16)) | this.minorVersion);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(java.lang.Object r4) {
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
        r3 = this;
        r0 = 0;
        r4 = (net.sourceforge.jtds.jdbc.cache.SQLCacheKey) r4;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        r1 = r3.hashCode;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        r2 = r4.hashCode;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        if (r1 != r2) goto L_0x0026;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
    L_0x0009:
        r1 = r3.majorVersion;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        r2 = r4.majorVersion;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        if (r1 != r2) goto L_0x0026;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
    L_0x000f:
        r1 = r3.minorVersion;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        r2 = r4.minorVersion;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        if (r1 != r2) goto L_0x0026;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
    L_0x0015:
        r1 = r3.serverType;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        r2 = r4.serverType;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        if (r1 != r2) goto L_0x0026;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
    L_0x001b:
        r1 = r3.sql;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        r4 = r4.sql;	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        r4 = r1.equals(r4);	 Catch:{ ClassCastException -> 0x0028, NullPointerException -> 0x0027 }
        if (r4 == 0) goto L_0x0026;
    L_0x0025:
        r0 = 1;
    L_0x0026:
        return r0;
    L_0x0027:
        return r0;
    L_0x0028:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.cache.SQLCacheKey.equals(java.lang.Object):boolean");
    }
}
