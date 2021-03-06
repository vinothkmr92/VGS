package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class HighLevelEncoder {
    private static final int[][] CHAR_MAP = ((int[][]) Array.newInstance(Integer.TYPE, new int[]{5, 256}));
    static final int[][] LATCH_TABLE;
    static final int MODE_DIGIT = 2;
    static final int MODE_LOWER = 1;
    static final int MODE_MIXED = 3;
    static final String[] MODE_NAMES = {"UPPER", "LOWER", "DIGIT", "MIXED", "PUNCT"};
    static final int MODE_PUNCT = 4;
    static final int MODE_UPPER = 0;
    static final int[][] SHIFT_TABLE = ((int[][]) Array.newInstance(Integer.TYPE, new int[]{6, 6}));
    private final byte[] text;

    static {
        int[] iArr = new int[5];
        iArr[1] = 327708;
        iArr[2] = 327710;
        iArr[3] = 327709;
        iArr[4] = 656318;
        int[] iArr2 = new int[5];
        iArr2[0] = 590318;
        iArr2[2] = 327710;
        iArr2[3] = 327709;
        iArr2[4] = 656318;
        int[] iArr3 = new int[5];
        iArr3[0] = 262158;
        iArr3[1] = 590300;
        iArr3[3] = 590301;
        iArr3[4] = 932798;
        int[] iArr4 = new int[5];
        iArr4[0] = 327709;
        iArr4[1] = 327708;
        iArr4[2] = 656318;
        iArr4[4] = 327710;
        int[] iArr5 = new int[5];
        iArr5[0] = 327711;
        iArr5[1] = 656380;
        iArr5[2] = 656382;
        iArr5[3] = 656381;
        LATCH_TABLE = new int[][]{iArr, iArr2, iArr3, iArr4, iArr5};
        CHAR_MAP[0][32] = 1;
        for (int c = 65; c <= 90; c++) {
            CHAR_MAP[0][c] = (c - 65) + 2;
        }
        CHAR_MAP[1][32] = 1;
        for (int c2 = 97; c2 <= 122; c2++) {
            CHAR_MAP[1][c2] = (c2 - 97) + 2;
        }
        CHAR_MAP[2][32] = 1;
        for (int c3 = 48; c3 <= 57; c3++) {
            CHAR_MAP[2][c3] = (c3 - 48) + 2;
        }
        CHAR_MAP[2][44] = 12;
        CHAR_MAP[2][46] = 13;
        int[] mixedTable = new int[28];
        mixedTable[1] = 32;
        mixedTable[2] = 1;
        mixedTable[3] = 2;
        mixedTable[4] = 3;
        mixedTable[5] = 4;
        mixedTable[6] = 5;
        mixedTable[7] = 6;
        mixedTable[8] = 7;
        mixedTable[9] = 8;
        mixedTable[10] = 9;
        mixedTable[11] = 10;
        mixedTable[12] = 11;
        mixedTable[13] = 12;
        mixedTable[14] = 13;
        mixedTable[15] = 27;
        mixedTable[16] = 28;
        mixedTable[17] = 29;
        mixedTable[18] = 30;
        mixedTable[19] = 31;
        mixedTable[20] = 64;
        mixedTable[21] = 92;
        mixedTable[22] = 94;
        mixedTable[23] = 95;
        mixedTable[24] = 96;
        mixedTable[25] = 124;
        mixedTable[26] = 126;
        mixedTable[27] = 127;
        for (int i = 0; i < mixedTable.length; i++) {
            CHAR_MAP[3][mixedTable[i]] = i;
        }
        int[] punctTable = new int[31];
        punctTable[1] = 13;
        punctTable[6] = 33;
        punctTable[7] = 39;
        punctTable[8] = 35;
        punctTable[9] = 36;
        punctTable[10] = 37;
        punctTable[11] = 38;
        punctTable[12] = 39;
        punctTable[13] = 40;
        punctTable[14] = 41;
        punctTable[15] = 42;
        punctTable[16] = 43;
        punctTable[17] = 44;
        punctTable[18] = 45;
        punctTable[19] = 46;
        punctTable[20] = 47;
        punctTable[21] = 58;
        punctTable[22] = 59;
        punctTable[23] = 60;
        punctTable[24] = 61;
        punctTable[25] = 62;
        punctTable[26] = 63;
        punctTable[27] = 91;
        punctTable[28] = 93;
        punctTable[29] = 123;
        punctTable[30] = 125;
        for (int i2 = 0; i2 < punctTable.length; i2++) {
            if (punctTable[i2] > 0) {
                CHAR_MAP[4][punctTable[i2]] = i2;
            }
        }
        for (int[] table : SHIFT_TABLE) {
            Arrays.fill(table, -1);
        }
        SHIFT_TABLE[0][4] = 0;
        SHIFT_TABLE[1][4] = 0;
        SHIFT_TABLE[1][0] = 28;
        SHIFT_TABLE[3][4] = 0;
        SHIFT_TABLE[2][4] = 0;
        SHIFT_TABLE[2][0] = 15;
    }

    public HighLevelEncoder(byte[] text2) {
        this.text = text2;
    }

    public BitArray encode() {
        byte nextChar;
        int pairCode;
        Collection<State> states = Collections.singletonList(State.INITIAL_STATE);
        int index = 0;
        while (index < this.text.length) {
            if (index + 1 < this.text.length) {
                nextChar = this.text[index + 1];
            } else {
                nextChar = 0;
            }
            switch (this.text[index]) {
                case 13:
                    if (nextChar != 10) {
                        pairCode = 0;
                        break;
                    } else {
                        pairCode = 2;
                        break;
                    }
                case 44:
                    if (nextChar != 32) {
                        pairCode = 0;
                        break;
                    } else {
                        pairCode = 4;
                        break;
                    }
                case 46:
                    if (nextChar != 32) {
                        pairCode = 0;
                        break;
                    } else {
                        pairCode = 3;
                        break;
                    }
                case 58:
                    if (nextChar != 32) {
                        pairCode = 0;
                        break;
                    } else {
                        pairCode = 5;
                        break;
                    }
                default:
                    pairCode = 0;
                    break;
            }
            if (pairCode > 0) {
                states = updateStateListForPair(states, index, pairCode);
                index++;
            } else {
                states = updateStateListForChar(states, index);
            }
            index++;
        }
        return ((State) Collections.min(states, new Comparator<State>() {
            public int compare(State a, State b) {
                return a.getBitCount() - b.getBitCount();
            }
        })).toBitArray(this.text);
    }

    private Collection<State> updateStateListForChar(Iterable<State> states, int index) {
        Collection<State> result = new LinkedList<>();
        for (State state : states) {
            updateStateForChar(state, index, result);
        }
        return simplifyStates(result);
    }

    private void updateStateForChar(State state, int index, Collection<State> result) {
        char ch = (char) (this.text[index] & 255);
        boolean charInCurrentTable = CHAR_MAP[state.getMode()][ch] > 0;
        State stateNoBinary = null;
        for (int mode = 0; mode <= 4; mode++) {
            int charInMode = CHAR_MAP[mode][ch];
            if (charInMode > 0) {
                if (stateNoBinary == null) {
                    stateNoBinary = state.endBinaryShift(index);
                }
                if (!charInCurrentTable || mode == state.getMode() || mode == 2) {
                    result.add(stateNoBinary.latchAndAppend(mode, charInMode));
                }
                if (!charInCurrentTable && SHIFT_TABLE[state.getMode()][mode] >= 0) {
                    result.add(stateNoBinary.shiftAndAppend(mode, charInMode));
                }
            }
        }
        if (state.getBinaryShiftByteCount() > 0 || CHAR_MAP[state.getMode()][ch] == 0) {
            result.add(state.addBinaryShiftChar(index));
        }
    }

    private static Collection<State> updateStateListForPair(Iterable<State> states, int index, int pairCode) {
        Collection<State> result = new LinkedList<>();
        for (State state : states) {
            updateStateForPair(state, index, pairCode, result);
        }
        return simplifyStates(result);
    }

    private static void updateStateForPair(State state, int index, int pairCode, Collection<State> result) {
        State stateNoBinary = state.endBinaryShift(index);
        result.add(stateNoBinary.latchAndAppend(4, pairCode));
        if (state.getMode() != 4) {
            result.add(stateNoBinary.shiftAndAppend(4, pairCode));
        }
        if (pairCode == 3 || pairCode == 4) {
            result.add(stateNoBinary.latchAndAppend(2, 16 - pairCode).latchAndAppend(2, 1));
        }
        if (state.getBinaryShiftByteCount() > 0) {
            result.add(state.addBinaryShiftChar(index).addBinaryShiftChar(index + 1));
        }
    }

    private static Collection<State> simplifyStates(Iterable<State> states) {
        List<State> result = new LinkedList<>();
        for (State newState : states) {
            boolean add = true;
            Iterator<State> iterator = result.iterator();
            while (true) {
                if (!iterator.hasNext()) {
                    break;
                }
                State oldState = (State) iterator.next();
                if (oldState.isBetterThanOrEqualTo(newState)) {
                    add = false;
                    break;
                } else if (newState.isBetterThanOrEqualTo(oldState)) {
                    iterator.remove();
                }
            }
            if (add) {
                result.add(newState);
            }
        }
        return result;
    }
}
