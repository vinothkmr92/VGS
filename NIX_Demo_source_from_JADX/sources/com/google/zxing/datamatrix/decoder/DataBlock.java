package com.google.zxing.datamatrix.decoder;

final class DataBlock {
    private final byte[] codewords;
    private final int numDataCodewords;

    private DataBlock(int numDataCodewords2, byte[] codewords2) {
        this.numDataCodewords = numDataCodewords2;
        this.codewords = codewords2;
    }

    static DataBlock[] getDataBlocks(byte[] rawCodewords, Version version) {
        int numLongerBlocks;
        int jOffset;
        int iOffset;
        ECBlocks ecBlocks = version.getECBlocks();
        int totalBlocks = 0;
        ECB[] ecBlockArray = ecBlocks.getECBlocks();
        for (int i = 0; i < ecBlockArray.length; i++) {
            totalBlocks += ecBlockArray[i].getCount();
        }
        DataBlock[] result = new DataBlock[totalBlocks];
        int numResultBlocks = 0;
        int length = ecBlockArray.length;
        for (int i2 = 0; i2 < length; i2++) {
            ECB ecBlock = ecBlockArray[i2];
            int i3 = 0;
            while (i3 < ecBlock.getCount()) {
                int numDataCodewords2 = ecBlock.getDataCodewords();
                int numResultBlocks2 = numResultBlocks + 1;
                DataBlock dataBlock = new DataBlock(numDataCodewords2, new byte[(ecBlocks.getECCodewords() + numDataCodewords2)]);
                result[numResultBlocks] = dataBlock;
                i3++;
                numResultBlocks = numResultBlocks2;
            }
        }
        int longerBlocksNumDataCodewords = result[0].codewords.length - ecBlocks.getECCodewords();
        int shorterBlocksNumDataCodewords = longerBlocksNumDataCodewords - 1;
        int rawCodewordsOffset = 0;
        int i4 = 0;
        while (i4 < shorterBlocksNumDataCodewords) {
            int j = 0;
            int rawCodewordsOffset2 = rawCodewordsOffset;
            while (j < numResultBlocks) {
                int rawCodewordsOffset3 = rawCodewordsOffset2 + 1;
                result[j].codewords[i4] = rawCodewords[rawCodewordsOffset2];
                j++;
                rawCodewordsOffset2 = rawCodewordsOffset3;
            }
            i4++;
            rawCodewordsOffset = rawCodewordsOffset2;
        }
        boolean specialVersion = version.getVersionNumber() == 24;
        if (specialVersion) {
            numLongerBlocks = 8;
        } else {
            numLongerBlocks = numResultBlocks;
        }
        int j2 = 0;
        int rawCodewordsOffset4 = rawCodewordsOffset;
        while (j2 < numLongerBlocks) {
            int rawCodewordsOffset5 = rawCodewordsOffset4 + 1;
            result[j2].codewords[longerBlocksNumDataCodewords - 1] = rawCodewords[rawCodewordsOffset4];
            j2++;
            rawCodewordsOffset4 = rawCodewordsOffset5;
        }
        int max = result[0].codewords.length;
        int i5 = longerBlocksNumDataCodewords;
        int rawCodewordsOffset6 = rawCodewordsOffset4;
        while (i5 < max) {
            int j3 = 0;
            int rawCodewordsOffset7 = rawCodewordsOffset6;
            while (j3 < numResultBlocks) {
                if (specialVersion) {
                    jOffset = (j3 + 8) % numResultBlocks;
                } else {
                    jOffset = j3;
                }
                if (!specialVersion || jOffset <= 7) {
                    iOffset = i5;
                } else {
                    iOffset = i5 - 1;
                }
                int rawCodewordsOffset8 = rawCodewordsOffset7 + 1;
                result[jOffset].codewords[iOffset] = rawCodewords[rawCodewordsOffset7];
                j3++;
                rawCodewordsOffset7 = rawCodewordsOffset8;
            }
            i5++;
            rawCodewordsOffset6 = rawCodewordsOffset7;
        }
        if (rawCodewordsOffset6 == rawCodewords.length) {
            return result;
        }
        throw new IllegalArgumentException();
    }

    /* access modifiers changed from: 0000 */
    public int getNumDataCodewords() {
        return this.numDataCodewords;
    }

    /* access modifiers changed from: 0000 */
    public byte[] getCodewords() {
        return this.codewords;
    }
}
