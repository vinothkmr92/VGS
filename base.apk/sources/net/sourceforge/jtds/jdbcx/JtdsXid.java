package net.sourceforge.jtds.jdbcx;

import javax.transaction.xa.Xid;
import net.sourceforge.jtds.jdbc.Support;

public class JtdsXid implements Xid {
    public static final int XID_SIZE = 140;
    private final byte[] bqual;
    public final int fmtId;
    private final byte[] gtran;
    public int hash;

    public JtdsXid(byte[] bArr, int i) {
        this.fmtId = (((bArr[i] & 255) | ((bArr[i + 1] & 255) << 8)) | ((bArr[i + 2] & 255) << 16)) | ((bArr[i + 3] & 255) << 24);
        byte b = bArr[i + 4];
        byte b2 = bArr[i + 8];
        this.gtran = new byte[b];
        this.bqual = new byte[b2];
        System.arraycopy(bArr, i + 12, this.gtran, 0, b);
        System.arraycopy(bArr, (b + 12) + i, this.bqual, 0, b2);
        calculateHash();
    }

    public JtdsXid(byte[] bArr, byte[] bArr2) {
        this.fmtId = 0;
        this.gtran = bArr;
        this.bqual = bArr2;
        calculateHash();
    }

    public JtdsXid(Xid xid) {
        this.fmtId = xid.getFormatId();
        this.gtran = new byte[xid.getGlobalTransactionId().length];
        System.arraycopy(xid.getGlobalTransactionId(), 0, this.gtran, 0, this.gtran.length);
        this.bqual = new byte[xid.getBranchQualifier().length];
        System.arraycopy(xid.getBranchQualifier(), 0, this.bqual, 0, this.bqual.length);
        calculateHash();
    }

    private void calculateHash() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Integer.toString(this.fmtId));
        stringBuffer.append(new String(this.gtran));
        stringBuffer.append(new String(this.bqual));
        this.hash = stringBuffer.toString().hashCode();
    }

    public int hashCode() {
        return this.hash;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof JtdsXid) {
            JtdsXid jtdsXid = (JtdsXid) obj;
            if (this.gtran.length + this.bqual.length == jtdsXid.gtran.length + jtdsXid.bqual.length && this.fmtId == jtdsXid.fmtId) {
                int i;
                for (i = 0; i < this.gtran.length; i++) {
                    if (this.gtran[i] != jtdsXid.gtran[i]) {
                        return false;
                    }
                }
                for (i = 0; i < this.bqual.length; i++) {
                    if (this.bqual[i] != jtdsXid.bqual[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int getFormatId() {
        return this.fmtId;
    }

    public byte[] getBranchQualifier() {
        return this.bqual;
    }

    public byte[] getGlobalTransactionId() {
        return this.gtran;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(256);
        stringBuffer.append("XID[Format=");
        stringBuffer.append(this.fmtId);
        stringBuffer.append(", Global=0x");
        stringBuffer.append(Support.toHex(this.gtran));
        stringBuffer.append(", Branch=0x");
        stringBuffer.append(Support.toHex(this.bqual));
        stringBuffer.append(']');
        return stringBuffer.toString();
    }
}
