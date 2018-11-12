package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import net.sourceforge.jtds.util.Logger;

public class RequestStream {
    private final VirtualSocket _VirtualSocket;
    private byte[] buffer;
    private int bufferPtr = 8;
    private final int bufferSize;
    private boolean isClosed;
    private final int maxPrecision;
    private byte pktType;
    private final SharedSocket socket;

    RequestStream(SharedSocket sharedSocket, VirtualSocket virtualSocket, int i, int i2) {
        this._VirtualSocket = virtualSocket;
        this.socket = sharedSocket;
        this.bufferSize = i;
        this.buffer = new byte[i];
        this.maxPrecision = i2;
    }

    void setBufferSize(int i) {
        if (i >= this.bufferPtr) {
            if (i != this.bufferSize) {
                if (i >= 512) {
                    if (i <= 32768) {
                        i = new byte[i];
                        System.arraycopy(this.buffer, 0, i, 0, this.bufferPtr);
                        this.buffer = i;
                        return;
                    }
                }
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Invalid buffer size parameter ");
                stringBuffer.append(i);
                throw new IllegalArgumentException(stringBuffer.toString());
            }
        }
    }

    int getBufferSize() {
        return this.bufferSize;
    }

    int getMaxPrecision() {
        return this.maxPrecision;
    }

    byte getMaxDecimalBytes() {
        return (byte) (this.maxPrecision <= 28 ? 13 : 17);
    }

    VirtualSocket getVirtualSocket() {
        return this._VirtualSocket;
    }

    void setPacketType(byte b) {
        this.pktType = b;
    }

    void write(byte b) throws IOException {
        if (this.bufferPtr == this.buffer.length) {
            putPacket(0);
        }
        byte[] bArr = this.buffer;
        int i = this.bufferPtr;
        this.bufferPtr = i + 1;
        bArr[i] = b;
    }

    void write(byte[] bArr) throws IOException {
        int length = bArr.length;
        int i = 0;
        while (length > 0) {
            int length2 = this.buffer.length - this.bufferPtr;
            if (length2 == 0) {
                putPacket(0);
            } else {
                if (length2 > length) {
                    length2 = length;
                }
                System.arraycopy(bArr, i, this.buffer, this.bufferPtr, length2);
                i += length2;
                this.bufferPtr += length2;
                length -= length2;
            }
        }
    }

    void write(byte[] bArr, int i, int i2) throws IOException {
        int i3 = i + i2;
        if (i3 > bArr.length) {
            i3 = bArr.length;
        }
        i3 -= i;
        while (i3 > 0) {
            int length = this.buffer.length - this.bufferPtr;
            if (length == 0) {
                putPacket(0);
            } else {
                if (length > i3) {
                    length = i3;
                }
                System.arraycopy(bArr, i, this.buffer, this.bufferPtr, length);
                i += length;
                this.bufferPtr += length;
                i3 -= length;
            }
        }
        for (i2 -= i3; i2 > 0; i2--) {
            write((byte) 0);
        }
    }

    void write(int i) throws IOException {
        write((byte) i);
        write((byte) (i >> 8));
        write((byte) (i >> 16));
        write((byte) (i >> 24));
    }

    void write(short s) throws IOException {
        write((byte) s);
        write((byte) (s >> 8));
    }

    void write(long j) throws IOException {
        write((byte) ((int) j));
        write((byte) ((int) (j >> 8)));
        write((byte) ((int) (j >> 16)));
        write((byte) ((int) (j >> 24)));
        write((byte) ((int) (j >> 32)));
        write((byte) ((int) (j >> 40)));
        write((byte) ((int) (j >> 48)));
        write((byte) ((int) (j >> 56)));
    }

    void write(double d) throws IOException {
        d = Double.doubleToLongBits(d);
        write((byte) ((int) d));
        write((byte) ((int) (d >> 8)));
        write((byte) ((int) (d >> 16)));
        write((byte) ((int) (d >> 24)));
        write((byte) ((int) (d >> 32)));
        write((byte) ((int) (d >> 40)));
        write((byte) ((int) (d >> 48)));
        write((byte) ((int) (d >> 56)));
    }

    void write(float f) throws IOException {
        f = Float.floatToIntBits(f);
        write((byte) f);
        write((byte) (f >> 8));
        write((byte) (f >> 16));
        write((byte) (f >> 24));
    }

    void write(String str) throws IOException {
        if (this.socket.getTdsVersion() >= 3) {
            int length = str.length();
            for (int i = 0; i < length; i++) {
                char charAt = str.charAt(i);
                if (this.bufferPtr == this.buffer.length) {
                    putPacket(0);
                }
                byte[] bArr = this.buffer;
                int i2 = this.bufferPtr;
                this.bufferPtr = i2 + 1;
                bArr[i2] = (byte) charAt;
                if (this.bufferPtr == this.buffer.length) {
                    putPacket(0);
                }
                bArr = this.buffer;
                i2 = this.bufferPtr;
                this.bufferPtr = i2 + 1;
                bArr[i2] = (byte) (charAt >> 8);
            }
            return;
        }
        writeAscii(str);
    }

    void write(char[] cArr, int i, int i2) throws IOException {
        i2 += i;
        if (i2 > cArr.length) {
            i2 = cArr.length;
        }
        while (i < i2) {
            char c = cArr[i];
            if (this.bufferPtr == this.buffer.length) {
                putPacket(0);
            }
            byte[] bArr = this.buffer;
            int i3 = this.bufferPtr;
            this.bufferPtr = i3 + 1;
            bArr[i3] = (byte) c;
            if (this.bufferPtr == this.buffer.length) {
                putPacket(0);
            }
            bArr = this.buffer;
            i3 = this.bufferPtr;
            this.bufferPtr = i3 + 1;
            bArr[i3] = (byte) (c >> 8);
            i++;
        }
    }

    void writeAscii(java.lang.String r2) throws java.io.IOException {
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
        r1 = this;
        r0 = r1.socket;
        r0 = r0.getCharset();
        if (r0 == 0) goto L_0x0018;
    L_0x0008:
        r0 = r2.getBytes(r0);	 Catch:{ UnsupportedEncodingException -> 0x0010 }
        r1.write(r0);	 Catch:{ UnsupportedEncodingException -> 0x0010 }
        goto L_0x001f;
    L_0x0010:
        r2 = r2.getBytes();
        r1.write(r2);
        goto L_0x001f;
    L_0x0018:
        r2 = r2.getBytes();
        r1.write(r2);
    L_0x001f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.RequestStream.writeAscii(java.lang.String):void");
    }

    void writeStreamBytes(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[1024];
        while (i > 0) {
            int read = inputStream.read(bArr);
            if (read < 0) {
                throw new IOException("Data in stream less than specified by length");
            }
            write(bArr, 0, read);
            i -= read;
        }
        if (i >= 0) {
            if (inputStream.read() < null) {
                return;
            }
        }
        throw new IOException("More data in stream than specified by length");
    }

    void writeReaderChars(Reader reader, int i) throws IOException {
        char[] cArr = new char[512];
        byte[] bArr = new byte[1024];
        while (i > 0) {
            int read = reader.read(cArr);
            if (read < 0) {
                throw new IOException("Data in stream less than specified by length");
            }
            int i2 = -1;
            for (int i3 = 0; i3 < read; i3++) {
                i2++;
                bArr[i2] = (byte) cArr[i3];
                i2++;
                bArr[i2] = (byte) (cArr[i3] >> 8);
            }
            write(bArr, 0, read * 2);
            i -= read;
        }
        if (i >= 0) {
            if (reader.read() < null) {
                return;
            }
        }
        throw new IOException("More data in stream than specified by length");
    }

    void writeReaderBytes(Reader reader, int i) throws IOException {
        char[] cArr = new char[1024];
        int i2 = 0;
        while (i2 < i) {
            int read = reader.read(cArr);
            if (read == -1) {
                throw new IOException("Data in stream less than specified by length");
            }
            i2 += read;
            if (i2 > i) {
                throw new IOException("More data in stream than specified by length");
            }
            write(Support.encodeString(this.socket.getCharset(), new String(cArr, 0, read)));
        }
    }

    void write(BigDecimal bigDecimal) throws IOException {
        int i = 0;
        if (bigDecimal == null) {
            write((byte) 0);
            return;
        }
        int i2 = 1;
        byte b = (byte) (bigDecimal.signum() < 0 ? 0 : 1);
        bigDecimal = bigDecimal.unscaledValue().abs().toByteArray();
        byte length = (byte) (bigDecimal.length + 1);
        if (length > getMaxDecimalBytes()) {
            throw new IOException("BigDecimal to big to send");
        } else if (this.socket.serverType == 2) {
            write(length);
            if (b != (byte) 0) {
                i2 = 0;
            }
            write((byte) i2);
            while (i < bigDecimal.length) {
                write(bigDecimal[i]);
                i++;
            }
        } else {
            write(length);
            write(b);
            for (i = bigDecimal.length - 1; i >= 0; i--) {
                write(bigDecimal[i]);
            }
        }
    }

    void flush() throws IOException {
        putPacket(1);
    }

    void close() {
        this.isClosed = true;
    }

    int getTdsVersion() {
        return this.socket.getTdsVersion();
    }

    int getServerType() {
        return this.socket.serverType;
    }

    private void putPacket(int i) throws IOException {
        if (this.isClosed) {
            throw new IOException("RequestStream is closed");
        }
        this.buffer[0] = this.pktType;
        int i2 = 1;
        this.buffer[1] = (byte) i;
        this.buffer[2] = (byte) (this.bufferPtr >> 8);
        this.buffer[3] = (byte) this.bufferPtr;
        this.buffer[4] = null;
        this.buffer[5] = null;
        i = this.buffer;
        if (this.socket.getTdsVersion() < 3) {
            i2 = 0;
        }
        i[6] = (byte) i2;
        this.buffer[7] = null;
        if (Logger.isActive() != 0) {
            Logger.logPacket(this._VirtualSocket.id, false, this.buffer);
        }
        this.buffer = this.socket.sendNetPacket(this._VirtualSocket, this.buffer);
        this.bufferPtr = 8;
    }
}
