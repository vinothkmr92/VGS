package com.google.zxing.pdf417.encoder;

import com.google.zxing.WriterException;
import com.google.zxing.common.CharacterSetECI;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

final class PDF417HighLevelEncoder {
    private static final int BYTE_COMPACTION = 1;
    private static final Charset DEFAULT_ENCODING = Charset.forName("ISO-8859-1");
    private static final int ECI_CHARSET = 927;
    private static final int ECI_GENERAL_PURPOSE = 926;
    private static final int ECI_USER_DEFINED = 925;
    private static final int LATCH_TO_BYTE = 924;
    private static final int LATCH_TO_BYTE_PADDED = 901;
    private static final int LATCH_TO_NUMERIC = 902;
    private static final int LATCH_TO_TEXT = 900;
    private static final byte[] MIXED = new byte[128];
    private static final int NUMERIC_COMPACTION = 2;
    private static final byte[] PUNCTUATION = new byte[128];
    private static final int SHIFT_TO_BYTE = 913;
    private static final int SUBMODE_ALPHA = 0;
    private static final int SUBMODE_LOWER = 1;
    private static final int SUBMODE_MIXED = 2;
    private static final int SUBMODE_PUNCTUATION = 3;
    private static final int TEXT_COMPACTION = 0;
    private static final byte[] TEXT_MIXED_RAW;
    private static final byte[] TEXT_PUNCTUATION_RAW;

    static {
        byte[] bArr = new byte[30];
        bArr[0] = 48;
        bArr[1] = 49;
        bArr[2] = 50;
        bArr[3] = 51;
        bArr[4] = 52;
        bArr[5] = 53;
        bArr[6] = 54;
        bArr[7] = 55;
        bArr[8] = 56;
        bArr[9] = 57;
        bArr[10] = 38;
        bArr[11] = 13;
        bArr[12] = 9;
        bArr[13] = 44;
        bArr[14] = 58;
        bArr[15] = 35;
        bArr[16] = 45;
        bArr[17] = 46;
        bArr[18] = 36;
        bArr[19] = 47;
        bArr[20] = 43;
        bArr[21] = 37;
        bArr[22] = 42;
        bArr[23] = 61;
        bArr[24] = 94;
        bArr[26] = 32;
        TEXT_MIXED_RAW = bArr;
        byte[] bArr2 = new byte[30];
        bArr2[0] = 59;
        bArr2[1] = 60;
        bArr2[2] = 62;
        bArr2[3] = 64;
        bArr2[4] = 91;
        bArr2[5] = 92;
        bArr2[6] = 93;
        bArr2[7] = 95;
        bArr2[8] = 96;
        bArr2[9] = 126;
        bArr2[10] = 33;
        bArr2[11] = 13;
        bArr2[12] = 9;
        bArr2[13] = 44;
        bArr2[14] = 58;
        bArr2[15] = 10;
        bArr2[16] = 45;
        bArr2[17] = 46;
        bArr2[18] = 36;
        bArr2[19] = 47;
        bArr2[20] = 34;
        bArr2[21] = 124;
        bArr2[22] = 42;
        bArr2[23] = 40;
        bArr2[24] = 41;
        bArr2[25] = 63;
        bArr2[26] = 123;
        bArr2[27] = 125;
        bArr2[28] = 39;
        TEXT_PUNCTUATION_RAW = bArr2;
        Arrays.fill(MIXED, -1);
        for (byte i = 0; i < TEXT_MIXED_RAW.length; i = (byte) (i + 1)) {
            byte b = TEXT_MIXED_RAW[i];
            if (b > 0) {
                MIXED[b] = i;
            }
        }
        Arrays.fill(PUNCTUATION, -1);
        for (byte i2 = 0; i2 < TEXT_PUNCTUATION_RAW.length; i2 = (byte) (i2 + 1)) {
            byte b2 = TEXT_PUNCTUATION_RAW[i2];
            if (b2 > 0) {
                PUNCTUATION[b2] = i2;
            }
        }
    }

    private PDF417HighLevelEncoder() {
    }

    static String encodeHighLevel(String msg, Compaction compaction, Charset encoding) throws WriterException {
        StringBuilder sb = new StringBuilder(msg.length());
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        } else if (!DEFAULT_ENCODING.equals(encoding)) {
            CharacterSetECI eci = CharacterSetECI.getCharacterSetECIByName(encoding.name());
            if (eci != null) {
                encodingECI(eci.getValue(), sb);
            }
        }
        int len = msg.length();
        int p = 0;
        int textSubMode = 0;
        byte[] bytes = null;
        if (compaction == Compaction.TEXT) {
            encodeText(msg, 0, len, sb, 0);
        } else if (compaction == Compaction.BYTE) {
            byte[] bytes2 = msg.getBytes(encoding);
            encodeBinary(bytes2, 0, bytes2.length, 1, sb);
        } else if (compaction == Compaction.NUMERIC) {
            sb.append(902);
            encodeNumeric(msg, 0, len, sb);
        } else {
            int encodingMode = 0;
            while (p < len) {
                int n = determineConsecutiveDigitCount(msg, p);
                if (n >= 13) {
                    sb.append(902);
                    encodingMode = 2;
                    textSubMode = 0;
                    encodeNumeric(msg, p, n, sb);
                    p += n;
                } else {
                    int t = determineConsecutiveTextCount(msg, p);
                    if (t >= 5 || n == len) {
                        if (encodingMode != 0) {
                            sb.append(900);
                            encodingMode = 0;
                            textSubMode = 0;
                        }
                        textSubMode = encodeText(msg, p, t, sb, textSubMode);
                        p += t;
                    } else {
                        if (bytes == null) {
                            bytes = msg.getBytes(encoding);
                        }
                        int b = determineConsecutiveBinaryCount(msg, bytes, p);
                        if (b == 0) {
                            b = 1;
                        }
                        if (b == 1 && encodingMode == 0) {
                            encodeBinary(bytes, p, 1, 0, sb);
                        } else {
                            encodeBinary(bytes, p, b, encodingMode, sb);
                            encodingMode = 1;
                            textSubMode = 0;
                        }
                        p += b;
                    }
                }
            }
        }
        return sb.toString();
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int encodeText(java.lang.CharSequence r11, int r12, int r13, java.lang.StringBuilder r14, int r15) {
        /*
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>(r13)
            r7 = r15
            r3 = 0
        L_0x0007:
            int r9 = r12 + r3
            char r0 = r11.charAt(r9)
            switch(r7) {
                case 0: goto L_0x0037;
                case 1: goto L_0x0076;
                case 2: goto L_0x00bc;
                default: goto L_0x0010;
            }
        L_0x0010:
            boolean r9 = isPunctuation(r0)
            if (r9 == 0) goto L_0x0113
            byte[] r9 = PUNCTUATION
            byte r9 = r9[r0]
            char r9 = (char) r9
            r8.append(r9)
        L_0x001e:
            int r3 = r3 + 1
            if (r3 < r13) goto L_0x0007
            r1 = 0
            int r4 = r8.length()
            r2 = 0
        L_0x0028:
            if (r2 < r4) goto L_0x011b
            int r9 = r4 % 2
            if (r9 == 0) goto L_0x0036
            int r9 = r1 * 30
            int r9 = r9 + 29
            char r9 = (char) r9
            r14.append(r9)
        L_0x0036:
            return r7
        L_0x0037:
            boolean r9 = isAlphaUpper(r0)
            if (r9 == 0) goto L_0x004e
            r9 = 32
            if (r0 != r9) goto L_0x0047
            r9 = 26
            r8.append(r9)
            goto L_0x001e
        L_0x0047:
            int r9 = r0 + -65
            char r9 = (char) r9
            r8.append(r9)
            goto L_0x001e
        L_0x004e:
            boolean r9 = isAlphaLower(r0)
            if (r9 == 0) goto L_0x005b
            r7 = 1
            r9 = 27
            r8.append(r9)
            goto L_0x0007
        L_0x005b:
            boolean r9 = isMixed(r0)
            if (r9 == 0) goto L_0x0068
            r7 = 2
            r9 = 28
            r8.append(r9)
            goto L_0x0007
        L_0x0068:
            r9 = 29
            r8.append(r9)
            byte[] r9 = PUNCTUATION
            byte r9 = r9[r0]
            char r9 = (char) r9
            r8.append(r9)
            goto L_0x001e
        L_0x0076:
            boolean r9 = isAlphaLower(r0)
            if (r9 == 0) goto L_0x008d
            r9 = 32
            if (r0 != r9) goto L_0x0086
            r9 = 26
            r8.append(r9)
            goto L_0x001e
        L_0x0086:
            int r9 = r0 + -97
            char r9 = (char) r9
            r8.append(r9)
            goto L_0x001e
        L_0x008d:
            boolean r9 = isAlphaUpper(r0)
            if (r9 == 0) goto L_0x009f
            r9 = 27
            r8.append(r9)
            int r9 = r0 + -65
            char r9 = (char) r9
            r8.append(r9)
            goto L_0x001e
        L_0x009f:
            boolean r9 = isMixed(r0)
            if (r9 == 0) goto L_0x00ad
            r7 = 2
            r9 = 28
            r8.append(r9)
            goto L_0x0007
        L_0x00ad:
            r9 = 29
            r8.append(r9)
            byte[] r9 = PUNCTUATION
            byte r9 = r9[r0]
            char r9 = (char) r9
            r8.append(r9)
            goto L_0x001e
        L_0x00bc:
            boolean r9 = isMixed(r0)
            if (r9 == 0) goto L_0x00cc
            byte[] r9 = MIXED
            byte r9 = r9[r0]
            char r9 = (char) r9
            r8.append(r9)
            goto L_0x001e
        L_0x00cc:
            boolean r9 = isAlphaUpper(r0)
            if (r9 == 0) goto L_0x00da
            r7 = 0
            r9 = 28
            r8.append(r9)
            goto L_0x0007
        L_0x00da:
            boolean r9 = isAlphaLower(r0)
            if (r9 == 0) goto L_0x00e8
            r7 = 1
            r9 = 27
            r8.append(r9)
            goto L_0x0007
        L_0x00e8:
            int r9 = r12 + r3
            int r9 = r9 + 1
            if (r9 >= r13) goto L_0x0104
            int r9 = r12 + r3
            int r9 = r9 + 1
            char r5 = r11.charAt(r9)
            boolean r9 = isPunctuation(r5)
            if (r9 == 0) goto L_0x0104
            r7 = 3
            r9 = 25
            r8.append(r9)
            goto L_0x0007
        L_0x0104:
            r9 = 29
            r8.append(r9)
            byte[] r9 = PUNCTUATION
            byte r9 = r9[r0]
            char r9 = (char) r9
            r8.append(r9)
            goto L_0x001e
        L_0x0113:
            r7 = 0
            r9 = 29
            r8.append(r9)
            goto L_0x0007
        L_0x011b:
            int r9 = r2 % 2
            if (r9 == 0) goto L_0x0131
            r6 = 1
        L_0x0120:
            if (r6 == 0) goto L_0x0133
            int r9 = r1 * 30
            char r10 = r8.charAt(r2)
            int r9 = r9 + r10
            char r1 = (char) r9
            r14.append(r1)
        L_0x012d:
            int r2 = r2 + 1
            goto L_0x0028
        L_0x0131:
            r6 = 0
            goto L_0x0120
        L_0x0133:
            char r1 = r8.charAt(r2)
            goto L_0x012d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.encoder.PDF417HighLevelEncoder.encodeText(java.lang.CharSequence, int, int, java.lang.StringBuilder, int):int");
    }

    private static void encodeBinary(byte[] bytes, int startpos, int count, int startmode, StringBuilder sb) {
        if (count == 1 && startmode == 0) {
            sb.append(913);
        } else {
            if (count % 6 == 0) {
                sb.append(924);
            } else {
                sb.append(901);
            }
        }
        int idx = startpos;
        if (count >= 6) {
            char[] chars = new char[5];
            while ((startpos + count) - idx >= 6) {
                long t = 0;
                for (int i = 0; i < 6; i++) {
                    t = (t << 8) + ((long) (bytes[idx + i] & 255));
                }
                for (int i2 = 0; i2 < 5; i2++) {
                    chars[i2] = (char) ((int) (t % 900));
                    t /= 900;
                }
                for (int i3 = chars.length - 1; i3 >= 0; i3--) {
                    sb.append(chars[i3]);
                }
                idx += 6;
            }
        }
        for (int i4 = idx; i4 < startpos + count; i4++) {
            sb.append((char) (bytes[i4] & 255));
        }
    }

    private static void encodeNumeric(String msg, int startpos, int count, StringBuilder sb) {
        int idx = 0;
        StringBuilder tmp = new StringBuilder((count / 3) + 1);
        BigInteger num900 = BigInteger.valueOf(900);
        BigInteger num0 = BigInteger.valueOf(0);
        while (idx < count) {
            tmp.setLength(0);
            int len = Math.min(44, count - idx);
            BigInteger bigint = new BigInteger(new StringBuilder(String.valueOf('1')).append(msg.substring(startpos + idx, startpos + idx + len)).toString());
            do {
                tmp.append((char) bigint.mod(num900).intValue());
                bigint = bigint.divide(num900);
            } while (!bigint.equals(num0));
            for (int i = tmp.length() - 1; i >= 0; i--) {
                sb.append(tmp.charAt(i));
            }
            idx += len;
        }
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isAlphaUpper(char ch) {
        return ch == ' ' || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isAlphaLower(char ch) {
        return ch == ' ' || (ch >= 'a' && ch <= 'z');
    }

    private static boolean isMixed(char ch) {
        return MIXED[ch] != -1;
    }

    private static boolean isPunctuation(char ch) {
        return PUNCTUATION[ch] != -1;
    }

    private static boolean isText(char ch) {
        return ch == 9 || ch == 10 || ch == 13 || (ch >= ' ' && ch <= '~');
    }

    private static int determineConsecutiveDigitCount(CharSequence msg, int startpos) {
        int count = 0;
        int len = msg.length();
        int idx = startpos;
        if (idx < len) {
            char ch = msg.charAt(idx);
            while (isDigit(ch) && idx < len) {
                count++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
        }
        return count;
    }

    private static int determineConsecutiveTextCount(CharSequence msg, int startpos) {
        int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch = msg.charAt(idx);
            int numericCount = 0;
            while (numericCount < 13 && isDigit(ch) && idx < len) {
                numericCount++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
            if (numericCount < 13) {
                if (numericCount <= 0) {
                    if (!isText(msg.charAt(idx))) {
                        break;
                    }
                    idx++;
                }
            } else {
                return (idx - startpos) - numericCount;
            }
        }
        return idx - startpos;
    }

    private static int determineConsecutiveBinaryCount(CharSequence msg, byte[] bytes, int startpos) throws WriterException {
        int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch = msg.charAt(idx);
            int numericCount = 0;
            while (numericCount < 13 && isDigit(ch)) {
                numericCount++;
                int i = idx + numericCount;
                if (i >= len) {
                    break;
                }
                ch = msg.charAt(i);
            }
            if (numericCount >= 13) {
                return idx - startpos;
            }
            char ch2 = msg.charAt(idx);
            if (bytes[idx] != 63 || ch2 == '?') {
                idx++;
            } else {
                throw new WriterException("Non-encodable character detected: " + ch2 + " (Unicode: " + ch2 + ')');
            }
        }
        return idx - startpos;
    }

    private static void encodingECI(int eci, StringBuilder sb) throws WriterException {
        if (eci >= 0 && eci < LATCH_TO_TEXT) {
            sb.append(927);
            sb.append((char) eci);
        } else if (eci < 810900) {
            sb.append(926);
            sb.append((char) ((eci / LATCH_TO_TEXT) - 1));
            sb.append((char) (eci % LATCH_TO_TEXT));
        } else if (eci < 811800) {
            sb.append(925);
            sb.append((char) (810900 - eci));
        } else {
            throw new WriterException("ECI number not in valid range from 0..811799, but was " + eci);
        }
    }
}
