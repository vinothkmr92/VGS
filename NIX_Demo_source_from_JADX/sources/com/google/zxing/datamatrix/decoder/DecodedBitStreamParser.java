package com.google.zxing.datamatrix.decoder;

import android.support.p006v7.widget.helper.ItemTouchHelper.Callback;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitSource;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

final class DecodedBitStreamParser {

    /* renamed from: $SWITCH_TABLE$com$google$zxing$datamatrix$decoder$DecodedBitStreamParser$Mode */
    private static /* synthetic */ int[] f39xd23d60a3;
    private static final char[] C40_BASIC_SET_CHARS = {'*', '*', '*', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] C40_SHIFT2_SET_CHARS = {'!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_'};
    private static final char[] TEXT_BASIC_SET_CHARS = {'*', '*', '*', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] TEXT_SHIFT2_SET_CHARS = C40_SHIFT2_SET_CHARS;
    private static final char[] TEXT_SHIFT3_SET_CHARS = {'`', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '{', '|', '}', '~', 127};

    private enum Mode {
        PAD_ENCODE,
        ASCII_ENCODE,
        C40_ENCODE,
        TEXT_ENCODE,
        ANSIX12_ENCODE,
        EDIFACT_ENCODE,
        BASE256_ENCODE
    }

    /* renamed from: $SWITCH_TABLE$com$google$zxing$datamatrix$decoder$DecodedBitStreamParser$Mode */
    static /* synthetic */ int[] m86xd23d60a3() {
        int[] iArr = f39xd23d60a3;
        if (iArr == null) {
            iArr = new int[Mode.values().length];
            try {
                iArr[Mode.ANSIX12_ENCODE.ordinal()] = 5;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Mode.ASCII_ENCODE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Mode.BASE256_ENCODE.ordinal()] = 7;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Mode.C40_ENCODE.ordinal()] = 3;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Mode.EDIFACT_ENCODE.ordinal()] = 6;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[Mode.PAD_ENCODE.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[Mode.TEXT_ENCODE.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            f39xd23d60a3 = iArr;
        }
        return iArr;
    }

    private DecodedBitStreamParser() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0042  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.google.zxing.common.DecoderResult decode(byte[] r9) throws com.google.zxing.FormatException {
        /*
            r5 = 0
            com.google.zxing.common.BitSource r0 = new com.google.zxing.common.BitSource
            r0.<init>(r9)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r6 = 100
            r3.<init>(r6)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r6 = 0
            r4.<init>(r6)
            java.util.ArrayList r1 = new java.util.ArrayList
            r6 = 1
            r1.<init>(r6)
            com.google.zxing.datamatrix.decoder.DecodedBitStreamParser$Mode r2 = com.google.zxing.datamatrix.decoder.DecodedBitStreamParser.Mode.ASCII_ENCODE
        L_0x001b:
            com.google.zxing.datamatrix.decoder.DecodedBitStreamParser$Mode r6 = com.google.zxing.datamatrix.decoder.DecodedBitStreamParser.Mode.ASCII_ENCODE
            if (r2 != r6) goto L_0x0047
            com.google.zxing.datamatrix.decoder.DecodedBitStreamParser$Mode r2 = decodeAsciiSegment(r0, r3, r4)
        L_0x0023:
            com.google.zxing.datamatrix.decoder.DecodedBitStreamParser$Mode r6 = com.google.zxing.datamatrix.decoder.DecodedBitStreamParser.Mode.PAD_ENCODE
            if (r2 == r6) goto L_0x002d
            int r6 = r0.available()
            if (r6 > 0) goto L_0x001b
        L_0x002d:
            int r6 = r4.length()
            if (r6 <= 0) goto L_0x0036
            r3.append(r4)
        L_0x0036:
            com.google.zxing.common.DecoderResult r6 = new com.google.zxing.common.DecoderResult
            java.lang.String r7 = r3.toString()
            boolean r8 = r1.isEmpty()
            if (r8 == 0) goto L_0x0043
            r1 = r5
        L_0x0043:
            r6.<init>(r9, r7, r1, r5)
            return r6
        L_0x0047:
            int[] r6 = m86xd23d60a3()
            int r7 = r2.ordinal()
            r6 = r6[r7]
            switch(r6) {
                case 3: goto L_0x0059;
                case 4: goto L_0x005f;
                case 5: goto L_0x0063;
                case 6: goto L_0x0067;
                case 7: goto L_0x006b;
                default: goto L_0x0054;
            }
        L_0x0054:
            com.google.zxing.FormatException r5 = com.google.zxing.FormatException.getFormatInstance()
            throw r5
        L_0x0059:
            decodeC40Segment(r0, r3)
        L_0x005c:
            com.google.zxing.datamatrix.decoder.DecodedBitStreamParser$Mode r2 = com.google.zxing.datamatrix.decoder.DecodedBitStreamParser.Mode.ASCII_ENCODE
            goto L_0x0023
        L_0x005f:
            decodeTextSegment(r0, r3)
            goto L_0x005c
        L_0x0063:
            decodeAnsiX12Segment(r0, r3)
            goto L_0x005c
        L_0x0067:
            decodeEdifactSegment(r0, r3)
            goto L_0x005c
        L_0x006b:
            decodeBase256Segment(r0, r3, r1)
            goto L_0x005c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.datamatrix.decoder.DecodedBitStreamParser.decode(byte[]):com.google.zxing.common.DecoderResult");
    }

    private static Mode decodeAsciiSegment(BitSource bits, StringBuilder result, StringBuilder resultTrailer) throws FormatException {
        boolean upperShift = false;
        do {
            int oneByte = bits.readBits(8);
            if (oneByte == 0) {
                throw FormatException.getFormatInstance();
            } else if (oneByte <= 128) {
                if (upperShift) {
                    oneByte += 128;
                }
                result.append((char) (oneByte - 1));
                return Mode.ASCII_ENCODE;
            } else if (oneByte == 129) {
                return Mode.PAD_ENCODE;
            } else {
                if (oneByte <= 229) {
                    int value = oneByte - 130;
                    if (value < 10) {
                        result.append('0');
                    }
                    result.append(value);
                } else if (oneByte == 230) {
                    return Mode.C40_ENCODE;
                } else {
                    if (oneByte == 231) {
                        return Mode.BASE256_ENCODE;
                    }
                    if (oneByte == 232) {
                        result.append(29);
                    } else if (!(oneByte == 233 || oneByte == 234)) {
                        if (oneByte == 235) {
                            upperShift = true;
                        } else if (oneByte == 236) {
                            result.append("[)>\u001e05\u001d");
                            resultTrailer.insert(0, "\u001e\u0004");
                        } else if (oneByte == 237) {
                            result.append("[)>\u001e06\u001d");
                            resultTrailer.insert(0, "\u001e\u0004");
                        } else if (oneByte == 238) {
                            return Mode.ANSIX12_ENCODE;
                        } else {
                            if (oneByte == 239) {
                                return Mode.TEXT_ENCODE;
                            }
                            if (oneByte == 240) {
                                return Mode.EDIFACT_ENCODE;
                            }
                            if (!(oneByte == 241 || oneByte < 242 || (oneByte == 254 && bits.available() == 0))) {
                                throw FormatException.getFormatInstance();
                            }
                        }
                    }
                }
            }
        } while (bits.available() > 0);
        return Mode.ASCII_ENCODE;
    }

    private static void decodeC40Segment(BitSource bits, StringBuilder result) throws FormatException {
        boolean upperShift = false;
        int[] cValues = new int[3];
        int shift = 0;
        while (bits.available() != 8) {
            int firstByte = bits.readBits(8);
            if (firstByte != 254) {
                parseTwoBytes(firstByte, bits.readBits(8), cValues);
                for (int i = 0; i < 3; i++) {
                    int cValue = cValues[i];
                    switch (shift) {
                        case 0:
                            if (cValue < 3) {
                                shift = cValue + 1;
                                break;
                            } else if (cValue < C40_BASIC_SET_CHARS.length) {
                                char c40char = C40_BASIC_SET_CHARS[cValue];
                                if (!upperShift) {
                                    result.append(c40char);
                                    break;
                                } else {
                                    result.append((char) (c40char + 128));
                                    upperShift = false;
                                    break;
                                }
                            } else {
                                throw FormatException.getFormatInstance();
                            }
                        case 1:
                            if (upperShift) {
                                result.append((char) (cValue + 128));
                                upperShift = false;
                            } else {
                                result.append((char) cValue);
                            }
                            shift = 0;
                            break;
                        case 2:
                            if (cValue < C40_SHIFT2_SET_CHARS.length) {
                                char c40char2 = C40_SHIFT2_SET_CHARS[cValue];
                                if (upperShift) {
                                    result.append((char) (c40char2 + 128));
                                    upperShift = false;
                                } else {
                                    result.append(c40char2);
                                }
                            } else if (cValue == 27) {
                                result.append(29);
                            } else if (cValue == 30) {
                                upperShift = true;
                            } else {
                                throw FormatException.getFormatInstance();
                            }
                            shift = 0;
                            break;
                        case 3:
                            if (upperShift) {
                                result.append((char) (cValue + 224));
                                upperShift = false;
                            } else {
                                result.append((char) (cValue + 96));
                            }
                            shift = 0;
                            break;
                        default:
                            throw FormatException.getFormatInstance();
                    }
                }
                if (bits.available() <= 0) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private static void decodeTextSegment(BitSource bits, StringBuilder result) throws FormatException {
        boolean upperShift = false;
        int[] cValues = new int[3];
        int shift = 0;
        while (bits.available() != 8) {
            int firstByte = bits.readBits(8);
            if (firstByte != 254) {
                parseTwoBytes(firstByte, bits.readBits(8), cValues);
                for (int i = 0; i < 3; i++) {
                    int cValue = cValues[i];
                    switch (shift) {
                        case 0:
                            if (cValue < 3) {
                                shift = cValue + 1;
                                break;
                            } else if (cValue < TEXT_BASIC_SET_CHARS.length) {
                                char textChar = TEXT_BASIC_SET_CHARS[cValue];
                                if (!upperShift) {
                                    result.append(textChar);
                                    break;
                                } else {
                                    result.append((char) (textChar + 128));
                                    upperShift = false;
                                    break;
                                }
                            } else {
                                throw FormatException.getFormatInstance();
                            }
                        case 1:
                            if (upperShift) {
                                result.append((char) (cValue + 128));
                                upperShift = false;
                            } else {
                                result.append((char) cValue);
                            }
                            shift = 0;
                            break;
                        case 2:
                            if (cValue < TEXT_SHIFT2_SET_CHARS.length) {
                                char textChar2 = TEXT_SHIFT2_SET_CHARS[cValue];
                                if (upperShift) {
                                    result.append((char) (textChar2 + 128));
                                    upperShift = false;
                                } else {
                                    result.append(textChar2);
                                }
                            } else if (cValue == 27) {
                                result.append(29);
                            } else if (cValue == 30) {
                                upperShift = true;
                            } else {
                                throw FormatException.getFormatInstance();
                            }
                            shift = 0;
                            break;
                        case 3:
                            if (cValue < TEXT_SHIFT3_SET_CHARS.length) {
                                char textChar3 = TEXT_SHIFT3_SET_CHARS[cValue];
                                if (upperShift) {
                                    result.append((char) (textChar3 + 128));
                                    upperShift = false;
                                } else {
                                    result.append(textChar3);
                                }
                                shift = 0;
                                break;
                            } else {
                                throw FormatException.getFormatInstance();
                            }
                        default:
                            throw FormatException.getFormatInstance();
                    }
                }
                if (bits.available() <= 0) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private static void decodeAnsiX12Segment(BitSource bits, StringBuilder result) throws FormatException {
        int[] cValues = new int[3];
        while (bits.available() != 8) {
            int firstByte = bits.readBits(8);
            if (firstByte != 254) {
                parseTwoBytes(firstByte, bits.readBits(8), cValues);
                for (int i = 0; i < 3; i++) {
                    int cValue = cValues[i];
                    if (cValue == 0) {
                        result.append(13);
                    } else if (cValue == 1) {
                        result.append('*');
                    } else if (cValue == 2) {
                        result.append('>');
                    } else if (cValue == 3) {
                        result.append(' ');
                    } else if (cValue < 14) {
                        result.append((char) (cValue + 44));
                    } else if (cValue < 40) {
                        result.append((char) (cValue + 51));
                    } else {
                        throw FormatException.getFormatInstance();
                    }
                }
                if (bits.available() <= 0) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private static void parseTwoBytes(int firstByte, int secondByte, int[] result) {
        int fullBitValue = ((firstByte << 8) + secondByte) - 1;
        int temp = fullBitValue / 1600;
        result[0] = temp;
        int fullBitValue2 = fullBitValue - (temp * 1600);
        int temp2 = fullBitValue2 / 40;
        result[1] = temp2;
        result[2] = fullBitValue2 - (temp2 * 40);
    }

    private static void decodeEdifactSegment(BitSource bits, StringBuilder result) {
        while (bits.available() > 16) {
            for (int i = 0; i < 4; i++) {
                int edifactValue = bits.readBits(6);
                if (edifactValue == 31) {
                    int bitsLeft = 8 - bits.getBitOffset();
                    if (bitsLeft != 8) {
                        bits.readBits(bitsLeft);
                        return;
                    }
                    return;
                }
                if ((edifactValue & 32) == 0) {
                    edifactValue |= 64;
                }
                result.append((char) edifactValue);
            }
            if (bits.available() <= 0) {
                return;
            }
        }
    }

    private static void decodeBase256Segment(BitSource bits, StringBuilder result, Collection<byte[]> byteSegments) throws FormatException {
        int codewordPosition;
        int count;
        int codewordPosition2 = bits.getByteOffset() + 1;
        int codewordPosition3 = codewordPosition2 + 1;
        int d1 = unrandomize255State(bits.readBits(8), codewordPosition2);
        if (d1 == 0) {
            count = bits.available() / 8;
            codewordPosition = codewordPosition3;
        } else if (d1 < 250) {
            count = d1;
            codewordPosition = codewordPosition3;
        } else {
            codewordPosition = codewordPosition3 + 1;
            count = ((d1 - 249) * Callback.DEFAULT_SWIPE_ANIMATION_DURATION) + unrandomize255State(bits.readBits(8), codewordPosition3);
        }
        if (count < 0) {
            throw FormatException.getFormatInstance();
        }
        byte[] bytes = new byte[count];
        int i = 0;
        int codewordPosition4 = codewordPosition;
        while (i < count) {
            if (bits.available() < 8) {
                throw FormatException.getFormatInstance();
            }
            int codewordPosition5 = codewordPosition4 + 1;
            bytes[i] = (byte) unrandomize255State(bits.readBits(8), codewordPosition4);
            i++;
            codewordPosition4 = codewordPosition5;
        }
        byteSegments.add(bytes);
        try {
            result.append(new String(bytes, "ISO8859_1"));
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException("Platform does not support required encoding: " + uee);
        }
    }

    private static int unrandomize255State(int randomizedBase256Codeword, int base256CodewordPosition) {
        int tempVariable = randomizedBase256Codeword - (((base256CodewordPosition * 149) % 255) + 1);
        return tempVariable >= 0 ? tempVariable : tempVariable + 256;
    }
}
