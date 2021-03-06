package com.google.zxing;

import com.google.zxing.aztec.AztecReader;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.maxicode.MaxiCodeReader;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.google.zxing.pdf417.PDF417Reader;
import com.google.zxing.qrcode.QRCodeReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public final class MultiFormatReader implements Reader {
    private Map<DecodeHintType, ?> hints;
    private Reader[] readers;

    public Result decode(BinaryBitmap image) throws NotFoundException {
        setHints(null);
        return decodeInternal(image);
    }

    public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints2) throws NotFoundException {
        setHints(hints2);
        return decodeInternal(image);
    }

    public Result decodeWithState(BinaryBitmap image) throws NotFoundException {
        if (this.readers == null) {
            setHints(null);
        }
        return decodeInternal(image);
    }

    public void setHints(Map<DecodeHintType, ?> hints2) {
        boolean tryHarder;
        boolean addOneDReader = false;
        this.hints = hints2;
        if (hints2 == null || !hints2.containsKey(DecodeHintType.TRY_HARDER)) {
            tryHarder = false;
        } else {
            tryHarder = true;
        }
        Collection collection = hints2 == null ? null : (Collection) hints2.get(DecodeHintType.POSSIBLE_FORMATS);
        Collection<Reader> readers2 = new ArrayList<>();
        if (collection != null) {
            if (collection.contains(BarcodeFormat.UPC_A) || collection.contains(BarcodeFormat.UPC_E) || collection.contains(BarcodeFormat.EAN_13) || collection.contains(BarcodeFormat.EAN_8) || collection.contains(BarcodeFormat.CODABAR) || collection.contains(BarcodeFormat.CODE_39) || collection.contains(BarcodeFormat.CODE_93) || collection.contains(BarcodeFormat.CODE_128) || collection.contains(BarcodeFormat.ITF) || collection.contains(BarcodeFormat.RSS_14) || collection.contains(BarcodeFormat.RSS_EXPANDED)) {
                addOneDReader = true;
            }
            if (addOneDReader && !tryHarder) {
                readers2.add(new MultiFormatOneDReader(hints2));
            }
            if (collection.contains(BarcodeFormat.QR_CODE)) {
                readers2.add(new QRCodeReader());
            }
            if (collection.contains(BarcodeFormat.DATA_MATRIX)) {
                readers2.add(new DataMatrixReader());
            }
            if (collection.contains(BarcodeFormat.AZTEC)) {
                readers2.add(new AztecReader());
            }
            if (collection.contains(BarcodeFormat.PDF_417)) {
                readers2.add(new PDF417Reader());
            }
            if (collection.contains(BarcodeFormat.MAXICODE)) {
                readers2.add(new MaxiCodeReader());
            }
            if (addOneDReader && tryHarder) {
                readers2.add(new MultiFormatOneDReader(hints2));
            }
        }
        if (readers2.isEmpty()) {
            if (!tryHarder) {
                readers2.add(new MultiFormatOneDReader(hints2));
            }
            readers2.add(new QRCodeReader());
            readers2.add(new DataMatrixReader());
            readers2.add(new AztecReader());
            readers2.add(new PDF417Reader());
            readers2.add(new MaxiCodeReader());
            if (tryHarder) {
                readers2.add(new MultiFormatOneDReader(hints2));
            }
        }
        this.readers = (Reader[]) readers2.toArray(new Reader[readers2.size()]);
    }

    public void reset() {
        if (this.readers != null) {
            for (Reader reader : this.readers) {
                reader.reset();
            }
        }
    }

    private Result decodeInternal(BinaryBitmap image) throws NotFoundException {
        if (this.readers != null) {
            Reader[] readerArr = this.readers;
            int length = readerArr.length;
            int i = 0;
            while (i < length) {
                try {
                    return readerArr[i].decode(image, this.hints);
                } catch (ReaderException e) {
                    i++;
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
}
