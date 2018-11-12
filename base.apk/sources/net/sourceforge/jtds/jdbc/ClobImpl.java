package net.sourceforge.jtds.jdbc;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Clob;
import java.sql.SQLException;
import net.sourceforge.jtds.util.BlobBuffer;

public class ClobImpl implements Clob {
    private static final String EMPTY_CLOB = "";
    private final BlobBuffer blobBuffer;

    ClobImpl(ConnectionJDBC2 connectionJDBC2) {
        this(connectionJDBC2, "");
    }

    ClobImpl(net.sourceforge.jtds.jdbc.ConnectionJDBC2 r5, java.lang.String r6) {
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
        r4 = this;
        r4.<init>();
        if (r6 != 0) goto L_0x000d;
    L_0x0005:
        r5 = new java.lang.IllegalArgumentException;
        r6 = "str cannot be null";
        r5.<init>(r6);
        throw r5;
    L_0x000d:
        r0 = new net.sourceforge.jtds.util.BlobBuffer;
        r1 = r5.getBufferDir();
        r2 = r5.getLobBuffer();
        r0.<init>(r1, r2);
        r4.blobBuffer = r0;
        r5 = "UTF-16LE";	 Catch:{ UnsupportedEncodingException -> 0x0029 }
        r5 = r6.getBytes(r5);	 Catch:{ UnsupportedEncodingException -> 0x0029 }
        r6 = r4.blobBuffer;	 Catch:{ UnsupportedEncodingException -> 0x0029 }
        r0 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0029 }
        r6.setBuffer(r5, r0);	 Catch:{ UnsupportedEncodingException -> 0x0029 }
        return;
    L_0x0029:
        r5 = new java.lang.IllegalStateException;
        r6 = "UTF-16LE encoding is not supported.";
        r5.<init>(r6);
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ClobImpl.<init>(net.sourceforge.jtds.jdbc.ConnectionJDBC2, java.lang.String):void");
    }

    BlobBuffer getBlobBuffer() {
        return this.blobBuffer;
    }

    public InputStream getAsciiStream() throws SQLException {
        return this.blobBuffer.getBinaryStream(true);
    }

    public java.io.Reader getCharacterStream() throws java.sql.SQLException {
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
        r4 = this;
        r0 = new java.io.BufferedReader;	 Catch:{ UnsupportedEncodingException -> 0x0014 }
        r1 = new java.io.InputStreamReader;	 Catch:{ UnsupportedEncodingException -> 0x0014 }
        r2 = r4.blobBuffer;	 Catch:{ UnsupportedEncodingException -> 0x0014 }
        r3 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0014 }
        r2 = r2.getBinaryStream(r3);	 Catch:{ UnsupportedEncodingException -> 0x0014 }
        r3 = "UTF-16LE";	 Catch:{ UnsupportedEncodingException -> 0x0014 }
        r1.<init>(r2, r3);	 Catch:{ UnsupportedEncodingException -> 0x0014 }
        r0.<init>(r1);	 Catch:{ UnsupportedEncodingException -> 0x0014 }
        return r0;
    L_0x0014:
        r0 = new java.lang.IllegalStateException;
        r1 = "UTF-16LE encoding is not supported.";
        r0.<init>(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ClobImpl.getCharacterStream():java.io.Reader");
    }

    public String getSubString(long j, int i) throws SQLException {
        if (i == 0) {
            return "";
        }
        try {
            return new String(this.blobBuffer.getBytes(((j - 1) * 2) + 1, i * 2), "UTF-16LE");
        } catch (long j2) {
            throw new SQLException(Messages.get("error.generic.ioerror", j2.getMessage()), "HY000");
        }
    }

    public long length() throws SQLException {
        return this.blobBuffer.getLength() / 2;
    }

    public long position(java.lang.String r6, long r7) throws java.sql.SQLException {
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
        r5 = this;
        if (r6 != 0) goto L_0x0010;
    L_0x0002:
        r6 = new java.sql.SQLException;
        r7 = "error.clob.searchnull";
        r7 = net.sourceforge.jtds.jdbc.Messages.get(r7);
        r8 = "HY009";
        r6.<init>(r7, r8);
        throw r6;
    L_0x0010:
        r0 = "UTF-16LE";	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r6 = r6.getBytes(r0);	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r0 = r5.blobBuffer;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r1 = 1;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r3 = r7 - r1;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r7 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r3 = r3 * r7;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r7 = r3 + r1;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r6 = r0.position(r6, r7);	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        if (r6 >= 0) goto L_0x002a;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
    L_0x0028:
        r6 = (long) r6;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        goto L_0x0031;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
    L_0x002a:
        r6 = r6 + -1;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r6 = r6 / 2;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r6 = r6 + 1;
        goto L_0x0028;
    L_0x0031:
        return r6;
    L_0x0032:
        r6 = new java.lang.IllegalStateException;
        r7 = "UTF-16LE encoding is not supported.";
        r6.<init>(r7);
        throw r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ClobImpl.position(java.lang.String, long):long");
    }

    public long position(Clob clob, long j) throws SQLException {
        if (clob == null) {
            throw new SQLException(Messages.get("error.clob.searchnull"), "HY009");
        }
        clob = ((ClobImpl) clob).getBlobBuffer();
        clob = this.blobBuffer.position(clob.getBytes(1, (int) clob.getLength()), ((j - 1) * 2) + 1);
        if (clob >= null) {
            clob = ((clob - 1) / 2) + 1;
        }
        return (long) clob;
    }

    public OutputStream setAsciiStream(long j) throws SQLException {
        return this.blobBuffer.setBinaryStream(((j - 1) * 2) + 1, true);
    }

    public java.io.Writer setCharacterStream(long r8) throws java.sql.SQLException {
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
        r7 = this;
        r0 = new java.io.BufferedWriter;	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r1 = new java.io.OutputStreamWriter;	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r2 = r7.blobBuffer;	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r3 = 1;	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r5 = r8 - r3;	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r8 = 2;	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r5 = r5 * r8;	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r8 = r5 + r3;	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r3 = 0;	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r8 = r2.setBinaryStream(r8, r3);	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r9 = "UTF-16LE";	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r1.<init>(r8, r9);	 Catch:{ UnsupportedEncodingException -> 0x001e }
        r0.<init>(r1);	 Catch:{ UnsupportedEncodingException -> 0x001e }
        return r0;
    L_0x001e:
        r8 = new java.lang.IllegalStateException;
        r9 = "UTF-16LE encoding is not supported.";
        r8.<init>(r9);
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ClobImpl.setCharacterStream(long):java.io.Writer");
    }

    public int setString(long j, String str) throws SQLException {
        if (str == null) {
            throw new SQLException(Messages.get("error.clob.strnull"), "HY009");
        }
        return setString(j, str, 0, str.length());
    }

    public int setString(long r8, java.lang.String r10, int r11, int r12) throws java.sql.SQLException {
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
        r7 = this;
        if (r11 < 0) goto L_0x0048;
    L_0x0002:
        r0 = r10.length();
        if (r11 <= r0) goto L_0x0009;
    L_0x0008:
        goto L_0x0048;
    L_0x0009:
        if (r12 < 0) goto L_0x003a;
    L_0x000b:
        r12 = r12 + r11;
        r0 = r10.length();
        if (r12 <= r0) goto L_0x0013;
    L_0x0012:
        goto L_0x003a;
    L_0x0013:
        r10 = r10.substring(r11, r12);	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r11 = "UTF-16LE";	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r3 = r10.getBytes(r11);	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r0 = r7.blobBuffer;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r10 = 1;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r1 = r8 - r10;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r8 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r1 = r1 * r8;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r8 = r1 + r10;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r4 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r5 = r3.length;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r6 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r1 = r8;	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        r8 = r0.setBytes(r1, r3, r4, r5, r6);	 Catch:{ UnsupportedEncodingException -> 0x0032 }
        return r8;
    L_0x0032:
        r8 = new java.lang.IllegalStateException;
        r9 = "UTF-16LE encoding is not supported.";
        r8.<init>(r9);
        throw r8;
    L_0x003a:
        r8 = new java.sql.SQLException;
        r9 = "error.blobclob.badlen";
        r9 = net.sourceforge.jtds.jdbc.Messages.get(r9);
        r10 = "HY090";
        r8.<init>(r9, r10);
        throw r8;
    L_0x0048:
        r8 = new java.sql.SQLException;
        r9 = "error.blobclob.badoffset";
        r9 = net.sourceforge.jtds.jdbc.Messages.get(r9);
        r10 = "HY090";
        r8.<init>(r9, r10);
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ClobImpl.setString(long, java.lang.String, int, int):int");
    }

    public void truncate(long j) throws SQLException {
        this.blobBuffer.truncate(j * 2);
    }
}
