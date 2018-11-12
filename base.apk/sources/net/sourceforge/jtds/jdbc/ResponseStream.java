package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import net.sourceforge.jtds.util.Logger;

public class ResponseStream {
    private final VirtualSocket _VirtualSocket;
    private byte[] buffer;
    private int bufferLen;
    private int bufferPtr;
    private final byte[] byteBuffer = new byte[255];
    private final char[] charBuffer = new char[255];
    private boolean isClosed;
    private final SharedSocket socket;

    private static class TdsInputStream extends InputStream {
        int maxLen;
        ResponseStream tds;

        public TdsInputStream(ResponseStream responseStream, int i) {
            this.tds = responseStream;
            this.maxLen = i;
        }

        public int read() throws IOException {
            int i = this.maxLen;
            this.maxLen = i - 1;
            return i > 0 ? this.tds.read() : -1;
        }

        public int read(byte[] bArr, int i, int i2) throws IOException {
            if (this.maxLen < 1) {
                return -1;
            }
            i2 = Math.min(this.maxLen, i2);
            if (i2 > 0) {
                i2 = this.tds.read(bArr, i, i2);
                this.maxLen -= i2 == -1 ? 0 : i2;
            }
            return i2;
        }
    }

    ResponseStream(SharedSocket sharedSocket, VirtualSocket virtualSocket, int i) {
        this._VirtualSocket = virtualSocket;
        this.socket = sharedSocket;
        this.buffer = new byte[i];
        this.bufferLen = i;
        this.bufferPtr = i;
    }

    VirtualSocket getVirtualSocket() {
        return this._VirtualSocket;
    }

    int peek() throws IOException {
        int read = read();
        this.bufferPtr--;
        return read;
    }

    int read() throws IOException {
        if (this.bufferPtr >= this.bufferLen) {
            getPacket();
        }
        byte[] bArr = this.buffer;
        int i = this.bufferPtr;
        this.bufferPtr = i + 1;
        return bArr[i] & 255;
    }

    int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    int read(byte[] bArr, int i, int i2) throws IOException {
        int i3 = i;
        i = i2;
        while (i > 0) {
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            int i4 = this.bufferLen - this.bufferPtr;
            if (i4 > i) {
                i4 = i;
            }
            System.arraycopy(this.buffer, this.bufferPtr, bArr, i3, i4);
            i3 += i4;
            i -= i4;
            this.bufferPtr += i4;
        }
        return i2;
    }

    int read(char[] cArr) throws IOException {
        for (int i = 0; i < cArr.length; i++) {
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            byte[] bArr = this.buffer;
            int i2 = this.bufferPtr;
            this.bufferPtr = i2 + 1;
            int i3 = bArr[i2] & 255;
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            byte[] bArr2 = this.buffer;
            int i4 = this.bufferPtr;
            this.bufferPtr = i4 + 1;
            cArr[i] = (char) (i3 | (bArr2[i4] << 8));
        }
        return cArr.length;
    }

    String readString(int i) throws IOException {
        if (this.socket.getTdsVersion() >= 3) {
            return readUnicodeString(i);
        }
        return readNonUnicodeString(i);
    }

    void skipString(int i) throws IOException {
        if (i > 0) {
            if (this.socket.getTdsVersion() >= 3) {
                skip(i * 2);
            } else {
                skip(i);
            }
        }
    }

    String readUnicodeString(int i) throws IOException {
        char[] cArr = i > this.charBuffer.length ? new char[i] : this.charBuffer;
        for (int i2 = 0; i2 < i; i2++) {
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            byte[] bArr = this.buffer;
            int i3 = this.bufferPtr;
            this.bufferPtr = i3 + 1;
            int i4 = bArr[i3] & 255;
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            byte[] bArr2 = this.buffer;
            int i5 = this.bufferPtr;
            this.bufferPtr = i5 + 1;
            cArr[i2] = (char) (i4 | (bArr2[i5] << 8));
        }
        return new String(cArr, 0, i);
    }

    String readNonUnicodeString(int i) throws IOException {
        return readString(i, this.socket.getCharsetInfo());
    }

    String readNonUnicodeString(int i, CharsetInfo charsetInfo) throws IOException {
        return readString(i, charsetInfo);
    }

    java.lang.String readString(int r4, net.sourceforge.jtds.jdbc.CharsetInfo r5) throws java.io.IOException {
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
        r3 = this;
        r5 = r5.getCharset();
        r0 = r3.byteBuffer;
        r0 = r0.length;
        if (r4 <= r0) goto L_0x000c;
    L_0x0009:
        r0 = new byte[r4];
        goto L_0x000e;
    L_0x000c:
        r0 = r3.byteBuffer;
    L_0x000e:
        r1 = 0;
        r3.read(r0, r1, r4);
        r2 = new java.lang.String;	 Catch:{ UnsupportedEncodingException -> 0x0018 }
        r2.<init>(r0, r1, r4, r5);	 Catch:{ UnsupportedEncodingException -> 0x0018 }
        return r2;
    L_0x0018:
        r5 = new java.lang.String;
        r5.<init>(r0, r1, r4);
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ResponseStream.readString(int, net.sourceforge.jtds.jdbc.CharsetInfo):java.lang.String");
    }

    short readShort() throws IOException {
        return (short) (read() | (read() << 8));
    }

    int readInt() throws IOException {
        return read() | ((read() << 8) | ((read() << 16) | (read() << 24)));
    }

    long readLong() throws IOException {
        long read = ((long) read()) << 16;
        long read2 = ((long) read()) << 24;
        long read3 = ((long) read()) << 32;
        long read4 = ((long) read()) << 40;
        long read5 = ((long) read()) << 48;
        return ((((((((long) read()) | (((long) read()) << 8)) | read) | read2) | read3) | read4) | read5) | (((long) read()) << 56);
    }

    BigDecimal readUnsignedLong() throws IOException {
        long read = ((long) read()) << 16;
        long read2 = ((long) read()) << 24;
        long read3 = ((long) read()) << 32;
        long read4 = ((long) read()) << 40;
        return new BigDecimal(Long.toString((((((((long) read()) | (((long) read()) << 8)) | read) | read2) | read3) | read4) | (((long) read()) << 48))).multiply(new BigDecimal(256.0d)).add(new BigDecimal((double) (read() & 255)));
    }

    int skip(int i) throws IOException {
        int i2 = i;
        while (i2 > 0) {
            if (this.bufferPtr >= this.bufferLen) {
                getPacket();
            }
            int i3 = this.bufferLen - this.bufferPtr;
            if (i2 > i3) {
                i2 -= i3;
                this.bufferPtr = this.bufferLen;
            } else {
                this.bufferPtr += i2;
                i2 = 0;
            }
        }
        return i;
    }

    void skipToEnd() {
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
        r3 = this;
        r0 = r3.bufferLen;	 Catch:{ IOException -> 0x0011 }
        r3.bufferPtr = r0;	 Catch:{ IOException -> 0x0011 }
    L_0x0004:
        r0 = r3.socket;	 Catch:{ IOException -> 0x0011 }
        r1 = r3._VirtualSocket;	 Catch:{ IOException -> 0x0011 }
        r2 = r3.buffer;	 Catch:{ IOException -> 0x0011 }
        r0 = r0.getNetPacket(r1, r2);	 Catch:{ IOException -> 0x0011 }
        r3.buffer = r0;	 Catch:{ IOException -> 0x0011 }
        goto L_0x0004;
    L_0x0011:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ResponseStream.skipToEnd():void");
    }

    void close() {
        this.isClosed = true;
        this.socket.closeStream(this._VirtualSocket);
    }

    int getTdsVersion() {
        return this.socket.getTdsVersion();
    }

    int getServerType() {
        return this.socket.serverType;
    }

    InputStream getInputStream(int i) {
        return new TdsInputStream(this, i);
    }

    private void getPacket() throws IOException {
        while (this.bufferPtr >= this.bufferLen) {
            if (this.isClosed) {
                throw new IOException("ResponseStream is closed");
            }
            this.buffer = this.socket.getNetPacket(this._VirtualSocket, this.buffer);
            this.bufferLen = ((this.buffer[2] & 255) << 8) | (this.buffer[3] & 255);
            this.bufferPtr = 8;
            if (Logger.isActive()) {
                Logger.logPacket(this._VirtualSocket.id, true, this.buffer);
            }
        }
    }
}
