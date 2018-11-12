package net.sourceforge.jtds.jdbc;

public class UniqueIdentifier {
    private final byte[] bytes;

    public UniqueIdentifier(byte[] bArr) {
        this.bytes = bArr;
    }

    public byte[] getBytes() {
        return (byte[]) this.bytes.clone();
    }

    public String toString() {
        byte[] bArr = this.bytes;
        if (this.bytes.length == 16) {
            bArr = new byte[this.bytes.length];
            System.arraycopy(this.bytes, 0, bArr, 0, this.bytes.length);
            bArr[0] = this.bytes[3];
            bArr[1] = this.bytes[2];
            bArr[2] = this.bytes[1];
            bArr[3] = this.bytes[0];
            bArr[4] = this.bytes[5];
            bArr[5] = this.bytes[4];
            bArr[6] = this.bytes[7];
            bArr[7] = this.bytes[6];
        }
        byte[] bArr2 = new byte[1];
        StringBuffer stringBuffer = new StringBuffer(36);
        int i = 0;
        while (i < this.bytes.length) {
            bArr2[0] = bArr[i];
            stringBuffer.append(Support.toHex(bArr2));
            if (i == 3 || i == 5 || i == 7 || i == 9) {
                stringBuffer.append('-');
            }
            i++;
        }
        return stringBuffer.toString();
    }
}
