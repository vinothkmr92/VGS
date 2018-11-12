package net.sourceforge.jtds.jdbc;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import net.sourceforge.jtds.util.DESEngine;
import net.sourceforge.jtds.util.MD4Digest;
import net.sourceforge.jtds.util.MD5Digest;

public class NtlmAuth {
    public static byte[] answerNtChallenge(String str, byte[] bArr) throws UnsupportedEncodingException {
        return encryptNonce(ntHash(str), bArr);
    }

    public static byte[] answerLmChallenge(String str, byte[] bArr) throws UnsupportedEncodingException {
        str = convertPassword(str);
        DESEngine dESEngine = new DESEngine(true, makeDESkey(str, 0));
        DESEngine dESEngine2 = new DESEngine(true, makeDESkey(str, 7));
        str = new byte[21];
        Arrays.fill(str, (byte) 0);
        dESEngine.processBlock(bArr, 0, str, 0);
        dESEngine2.processBlock(bArr, 0, str, 8);
        return encryptNonce(str, bArr);
    }

    public static byte[] answerNtlmv2Challenge(String str, String str2, String str3, byte[] bArr, byte[] bArr2, byte[] bArr3) throws UnsupportedEncodingException {
        return answerNtlmv2Challenge(str, str2, str3, bArr, bArr2, bArr3, System.currentTimeMillis());
    }

    public static byte[] answerNtlmv2Challenge(String str, String str2, String str3, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) throws UnsupportedEncodingException {
        return lmv2Response(ntv2Hash(str, str2, str3), createBlob(bArr2, bArr3, bArr4), bArr);
    }

    public static byte[] answerNtlmv2Challenge(String str, String str2, String str3, byte[] bArr, byte[] bArr2, byte[] bArr3, long j) throws UnsupportedEncodingException {
        return answerNtlmv2Challenge(str, str2, str3, bArr, bArr2, bArr3, createTimestamp(j));
    }

    public static byte[] answerLmv2Challenge(String str, String str2, String str3, byte[] bArr, byte[] bArr2) throws UnsupportedEncodingException {
        return lmv2Response(ntv2Hash(str, str2, str3), bArr2, bArr);
    }

    private static byte[] ntv2Hash(String str, String str2, String str3) throws UnsupportedEncodingException {
        str3 = ntHash(str3);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str2.toUpperCase());
        stringBuffer.append(str.toUpperCase());
        return hmacMD5(stringBuffer.toString().getBytes("UnicodeLittleUnmarked"), str3);
    }

    private static byte[] lmv2Response(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        Object obj = new byte[(bArr3.length + bArr2.length)];
        System.arraycopy(bArr3, 0, obj, 0, bArr3.length);
        System.arraycopy(bArr2, 0, obj, bArr3.length, bArr2.length);
        bArr = hmacMD5(obj, bArr);
        bArr3 = new byte[(bArr.length + bArr2.length)];
        System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
        System.arraycopy(bArr2, 0, bArr3, bArr.length, bArr2.length);
        return bArr3;
    }

    private static byte[] hmacMD5(byte[] bArr, byte[] bArr2) {
        int i;
        Object obj = new byte[64];
        Object obj2 = new byte[64];
        for (i = 0; i < 64; i++) {
            obj[i] = (byte) 54;
            obj2[i] = (byte) 92;
        }
        for (i = bArr2.length - 1; i >= 0; i--) {
            obj[i] = (byte) (obj[i] ^ bArr2[i]);
            obj2[i] = (byte) (obj2[i] ^ bArr2[i]);
        }
        bArr2 = new byte[(bArr.length + 64)];
        System.arraycopy(obj, 0, bArr2, 0, 64);
        System.arraycopy(bArr, 0, bArr2, 64, bArr.length);
        bArr = md5(bArr2);
        bArr2 = new byte[(bArr.length + 64)];
        System.arraycopy(obj2, 0, bArr2, 0, 64);
        System.arraycopy(bArr, 0, bArr2, 64, bArr.length);
        return md5(bArr2);
    }

    private static byte[] md5(byte[] bArr) {
        MD5Digest mD5Digest = new MD5Digest();
        mD5Digest.update(bArr, 0, bArr.length);
        bArr = new byte[16];
        mD5Digest.doFinal(bArr, 0);
        return bArr;
    }

    public static byte[] createTimestamp(long j) {
        long j2 = (j + 11644473600000L) * 10000;
        byte[] bArr = new byte[8];
        for (int i = 0; i < 8; i++) {
            bArr[i] = (byte) ((int) j2);
            j2 >>>= 8;
        }
        return bArr;
    }

    private static byte[] createBlob(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        Object obj = new byte[]{(byte) 1, (byte) 1, (byte) 0, (byte) 0};
        Object obj2 = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0};
        Object obj3 = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0};
        Object obj4 = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0};
        Object obj5 = new byte[((((((obj.length + obj2.length) + bArr3.length) + bArr2.length) + obj3.length) + bArr.length) + obj4.length)];
        System.arraycopy(obj, 0, obj5, 0, obj.length);
        int length = obj.length + 0;
        System.arraycopy(obj2, 0, obj5, length, obj2.length);
        length += obj2.length;
        System.arraycopy(bArr3, 0, obj5, length, bArr3.length);
        length += bArr3.length;
        System.arraycopy(bArr2, 0, obj5, length, bArr2.length);
        length += bArr2.length;
        System.arraycopy(obj3, 0, obj5, length, obj3.length);
        length += obj3.length;
        System.arraycopy(bArr, 0, obj5, length, bArr.length);
        System.arraycopy(obj4, 0, obj5, length + bArr.length, obj4.length);
        return obj5;
    }

    private static byte[] encryptNonce(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[24];
        DESEngine dESEngine = new DESEngine(true, makeDESkey(bArr, 0));
        DESEngine dESEngine2 = new DESEngine(true, makeDESkey(bArr, 7));
        DESEngine dESEngine3 = new DESEngine(true, makeDESkey(bArr, 14));
        dESEngine.processBlock(bArr2, 0, bArr3, 0);
        dESEngine2.processBlock(bArr2, 0, bArr3, 8);
        dESEngine3.processBlock(bArr2, 0, bArr3, 16);
        return bArr3;
    }

    private static byte[] ntHash(String str) throws UnsupportedEncodingException {
        byte[] bArr = new byte[21];
        Arrays.fill(bArr, (byte) 0);
        str = str.getBytes("UnicodeLittleUnmarked");
        MD4Digest mD4Digest = new MD4Digest();
        mD4Digest.update(str, 0, str.length);
        mD4Digest.doFinal(bArr, 0);
        return bArr;
    }

    private static byte[] convertPassword(String str) throws UnsupportedEncodingException {
        str = str.toUpperCase().getBytes("UTF8");
        int i = 14;
        Object obj = new byte[14];
        Arrays.fill(obj, (byte) 0);
        if (str.length <= 14) {
            i = str.length;
        }
        System.arraycopy(str, 0, obj, 0, i);
        return obj;
    }

    private static byte[] makeDESkey(byte[] bArr, int i) {
        byte[] bArr2 = new byte[8];
        int i2 = i + 0;
        int i3 = 0;
        bArr2[0] = (byte) ((bArr[i2] >> 1) & 255);
        int i4 = i + 1;
        bArr2[1] = (byte) ((((bArr[i2] & 1) << 6) | (((bArr[i4] & 255) >> 2) & 255)) & 255);
        int i5 = i + 2;
        bArr2[2] = (byte) ((((bArr[i4] & 3) << 5) | (((bArr[i5] & 255) >> 3) & 255)) & 255);
        int i6 = i + 3;
        bArr2[3] = (byte) ((((bArr[i5] & 7) << 4) | (((bArr[i6] & 255) >> 4) & 255)) & 255);
        i4 = i + 4;
        bArr2[4] = (byte) ((((bArr[i6] & 15) << 3) | (((bArr[i4] & 255) >> 5) & 255)) & 255);
        i4 = i + 5;
        bArr2[5] = (byte) ((((bArr[i4] & 31) << 2) | (((bArr[i4] & 255) >> 6) & 255)) & 255);
        i += 6;
        bArr2[6] = (byte) ((((bArr[i4] & 63) << 1) | (((bArr[i] & 255) >> 7) & 255)) & 255);
        bArr2[7] = (byte) (bArr[i] & 127);
        while (i3 < 8) {
            bArr2[i3] = (byte) (bArr2[i3] << 1);
            i3++;
        }
        return bArr2;
    }
}
