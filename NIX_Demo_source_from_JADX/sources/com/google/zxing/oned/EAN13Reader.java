package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class EAN13Reader extends UPCEANReader {
    static final int[] FIRST_DIGIT_ENCODINGS;
    private final int[] decodeMiddleCounters = new int[4];

    static {
        int[] iArr = new int[10];
        iArr[1] = 11;
        iArr[2] = 13;
        iArr[3] = 14;
        iArr[4] = 19;
        iArr[5] = 25;
        iArr[6] = 28;
        iArr[7] = 21;
        iArr[8] = 22;
        iArr[9] = 26;
        FIRST_DIGIT_ENCODINGS = iArr;
    }

    /* access modifiers changed from: protected */
    public int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString) throws NotFoundException {
        int[] counters = this.decodeMiddleCounters;
        counters[0] = 0;
        counters[1] = 0;
        counters[2] = 0;
        counters[3] = 0;
        int end = row.getSize();
        int rowOffset = startRange[1];
        int lgPatternFound = 0;
        for (int x = 0; x < 6 && rowOffset < end; x++) {
            int bestMatch = decodeDigit(row, counters, rowOffset, L_AND_G_PATTERNS);
            resultString.append((char) ((bestMatch % 10) + 48));
            for (int counter : counters) {
                rowOffset += counter;
            }
            if (bestMatch >= 10) {
                lgPatternFound |= 1 << (5 - x);
            }
        }
        determineFirstDigit(resultString, lgPatternFound);
        int rowOffset2 = findGuardPattern(row, rowOffset, true, MIDDLE_PATTERN)[1];
        for (int x2 = 0; x2 < 6 && rowOffset2 < end; x2++) {
            resultString.append((char) (decodeDigit(row, counters, rowOffset2, L_PATTERNS) + 48));
            for (int counter2 : counters) {
                rowOffset2 += counter2;
            }
        }
        return rowOffset2;
    }

    /* access modifiers changed from: 0000 */
    public BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.EAN_13;
    }

    private static void determineFirstDigit(StringBuilder resultString, int lgPatternFound) throws NotFoundException {
        for (int d = 0; d < 10; d++) {
            if (lgPatternFound == FIRST_DIGIT_ENCODINGS[d]) {
                resultString.insert(0, (char) (d + 48));
                return;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
}
