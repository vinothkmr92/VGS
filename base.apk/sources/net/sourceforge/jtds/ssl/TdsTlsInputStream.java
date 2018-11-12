package net.sourceforge.jtds.ssl;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.sourceforge.jtds.jdbc.TdsCore;

class TdsTlsInputStream extends FilterInputStream {
    InputStream bufferStream;
    int bytesOutstanding;
    boolean pureSSL;
    final byte[] readBuffer = new byte[6144];

    public TdsTlsInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.pureSSL && this.bufferStream == null) {
            return this.in.read(bArr, i, i2);
        }
        if (!this.pureSSL && this.bufferStream == null) {
            primeBuffer();
        }
        bArr = this.bufferStream.read(bArr, i, i2);
        this.bytesOutstanding -= bArr < null ? 0 : bArr;
        if (this.bytesOutstanding == 0) {
            this.bufferStream = 0;
        }
        return bArr;
    }

    private void primeBuffer() throws IOException {
        int i;
        readFully(this.readBuffer, 0, 5);
        if (this.readBuffer[0] != (byte) 4) {
            if (this.readBuffer[0] != TdsCore.PRELOGIN_PKT) {
                i = ((this.readBuffer[3] & 255) << 8) | (this.readBuffer[4] & 255);
                readFully(this.readBuffer, 5, i - 5);
                this.pureSSL = true;
                this.bufferStream = new ByteArrayInputStream(this.readBuffer, 0, i);
                this.bytesOutstanding = i;
            }
        }
        i = ((this.readBuffer[2] & 255) << 8) | (this.readBuffer[3] & 255);
        readFully(this.readBuffer, 5, 3);
        i -= 8;
        readFully(this.readBuffer, 0, i);
        this.bufferStream = new ByteArrayInputStream(this.readBuffer, 0, i);
        this.bytesOutstanding = i;
    }

    private void readFully(byte[] bArr, int i, int i2) throws IOException {
        int i3 = 0;
        while (i2 > 0) {
            i3 = this.in.read(bArr, i, i2);
            if (i3 < 0) {
                break;
            }
            i += i3;
            i2 -= i3;
        }
        if (i3 < 0) {
            throw new IOException();
        }
    }
}
