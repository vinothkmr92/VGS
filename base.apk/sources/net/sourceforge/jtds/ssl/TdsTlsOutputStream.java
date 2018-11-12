package net.sourceforge.jtds.ssl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.jtds.jdbc.TdsCore;

class TdsTlsOutputStream extends FilterOutputStream {
    private final List bufferedRecords = new ArrayList();
    private int totalSize;

    TdsTlsOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    private void deferRecord(byte[] bArr, int i) {
        Object obj = new byte[i];
        System.arraycopy(bArr, 0, obj, 0, i);
        this.bufferedRecords.add(obj);
        this.totalSize += i;
    }

    private void flushBufferedRecords() throws IOException {
        Object obj = new byte[this.totalSize];
        int i = 0;
        for (int i2 = 0; i2 < this.bufferedRecords.size(); i2++) {
            byte[] bArr = (byte[]) this.bufferedRecords.get(i2);
            System.arraycopy(bArr, 0, obj, i, bArr.length);
            i += bArr.length;
        }
        putTdsPacket(obj, i);
        this.bufferedRecords.clear();
        this.totalSize = 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void write(byte[] bArr, int i, int i2) throws IOException {
        if (i2 >= 5) {
            if (i <= 0) {
                int i3 = bArr[0] & 255;
                int i4 = ((bArr[3] & 255) << 8) | (bArr[4] & 255);
                if (i3 >= 20 && i3 <= 23) {
                    if (i4 == i2 - 5) {
                        switch (i3) {
                            case 20:
                                deferRecord(bArr, i2);
                                break;
                            case 21:
                                break;
                            case 22:
                                if (i2 >= 9) {
                                    i = bArr[5];
                                    int i5 = (((bArr[6] & 255) << 16) | ((bArr[7] & 255) << 8)) | (bArr[8] & 255);
                                    i4 = i2 - 9;
                                    if (i5 != i4 || i != 1) {
                                        deferRecord(bArr, i2);
                                        if (!(i5 == i4 && i == 16)) {
                                            flushBufferedRecords();
                                            break;
                                        }
                                    }
                                    putTdsPacket(bArr, i2);
                                    break;
                                }
                                break;
                            case 23:
                                this.out.write(bArr, i, i2);
                                break;
                            default:
                        }
                        this.out.write(bArr, i, i2);
                        return;
                    }
                }
                putTdsPacket(bArr, i2);
                return;
            }
        }
        this.out.write(bArr, i, i2);
    }

    void putTdsPacket(byte[] bArr, int i) throws IOException {
        byte[] bArr2 = new byte[8];
        bArr2[0] = TdsCore.PRELOGIN_PKT;
        bArr2[1] = (byte) 1;
        int i2 = i + 8;
        bArr2[2] = (byte) (i2 >> 8);
        bArr2[3] = (byte) i2;
        this.out.write(bArr2, 0, bArr2.length);
        this.out.write(bArr, 0, i);
    }

    public void flush() throws IOException {
        super.flush();
    }
}
