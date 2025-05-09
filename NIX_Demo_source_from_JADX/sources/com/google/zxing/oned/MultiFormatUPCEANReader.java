package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public final class MultiFormatUPCEANReader extends OneDReader {
    private final UPCEANReader[] readers;

    public MultiFormatUPCEANReader(Map<DecodeHintType, ?> hints) {
        Collection collection;
        if (hints == null) {
            collection = null;
        } else {
            collection = (Collection) hints.get(DecodeHintType.POSSIBLE_FORMATS);
        }
        Collection<UPCEANReader> readers2 = new ArrayList<>();
        if (collection != null) {
            if (collection.contains(BarcodeFormat.EAN_13)) {
                readers2.add(new EAN13Reader());
            } else if (collection.contains(BarcodeFormat.UPC_A)) {
                readers2.add(new UPCAReader());
            }
            if (collection.contains(BarcodeFormat.EAN_8)) {
                readers2.add(new EAN8Reader());
            }
            if (collection.contains(BarcodeFormat.UPC_E)) {
                readers2.add(new UPCEReader());
            }
        }
        if (readers2.isEmpty()) {
            readers2.add(new EAN13Reader());
            readers2.add(new EAN8Reader());
            readers2.add(new UPCEReader());
        }
        this.readers = (UPCEANReader[]) readers2.toArray(new UPCEANReader[readers2.size()]);
    }

    public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException {
        boolean ean13MayBeUPCA;
        boolean canReturnUPCA = false;
        int[] startGuardPattern = UPCEANReader.findStartGuardPattern(row);
        UPCEANReader[] uPCEANReaderArr = this.readers;
        int length = uPCEANReaderArr.length;
        int i = 0;
        while (i < length) {
            try {
                Result result = uPCEANReaderArr[i].decodeRow(rowNumber, row, startGuardPattern, hints);
                if (result.getBarcodeFormat() == BarcodeFormat.EAN_13 && result.getText().charAt(0) == '0') {
                    ean13MayBeUPCA = true;
                } else {
                    ean13MayBeUPCA = false;
                }
                Collection collection = hints == null ? null : (Collection) hints.get(DecodeHintType.POSSIBLE_FORMATS);
                if (collection == null || collection.contains(BarcodeFormat.UPC_A)) {
                    canReturnUPCA = true;
                }
                if (!ean13MayBeUPCA || !canReturnUPCA) {
                    return result;
                }
                Result resultUPCA = new Result(result.getText().substring(1), result.getRawBytes(), result.getResultPoints(), BarcodeFormat.UPC_A);
                resultUPCA.putAllMetadata(result.getResultMetadata());
                return resultUPCA;
            } catch (ReaderException e) {
                i++;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    public void reset() {
        for (Reader reader : this.readers) {
            reader.reset();
        }
    }
}
