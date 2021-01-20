package com.ngx.mp100sdk;

import com.ngx.mp100sdk.Enums.NGXBarcodeCommands;

/* renamed from: com.ngx.mp100sdk.b */
final /* synthetic */ class C0393b {

    /* renamed from: a */
    static final /* synthetic */ int[] f85a = new int[NGXBarcodeCommands.values().length];

    static {
        try {
            f85a[NGXBarcodeCommands.CODE39.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            f85a[NGXBarcodeCommands.UPCA.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            f85a[NGXBarcodeCommands.JAN13_EAN13.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            f85a[NGXBarcodeCommands.JAN8_EAN8.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            f85a[NGXBarcodeCommands.ITF.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            f85a[NGXBarcodeCommands.CODABAR.ordinal()] = 6;
        } catch (NoSuchFieldError e6) {
        }
        try {
            f85a[NGXBarcodeCommands.CODE128.ordinal()] = 7;
        } catch (NoSuchFieldError e7) {
        }
        try {
            f85a[NGXBarcodeCommands.QRCode.ordinal()] = 8;
        } catch (NoSuchFieldError e8) {
        }
        try {
            f85a[NGXBarcodeCommands.PDF417.ordinal()] = 9;
        } catch (NoSuchFieldError e9) {
        }
        try {
            f85a[NGXBarcodeCommands.AZTEC.ordinal()] = 10;
        } catch (NoSuchFieldError e10) {
        }
        try {
            f85a[NGXBarcodeCommands.DATAMATRIX.ordinal()] = 11;
        } catch (NoSuchFieldError e11) {
        }
    }
}
