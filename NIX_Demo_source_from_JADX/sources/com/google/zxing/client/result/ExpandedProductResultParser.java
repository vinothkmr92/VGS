package com.google.zxing.client.result;

public final class ExpandedProductResultParser extends ResultParser {
    /* JADX WARNING: Code restructure failed: missing block: B:116:?, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0059, code lost:
        r18.put(r19, r22);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00c8, code lost:
        if (r19.equals("3100") != false) goto L_0x00ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00ca, code lost:
        r12 = r22;
        r13 = com.google.zxing.client.result.ExpandedProductParsedResult.KILOGRAM;
        r14 = r19.substring(3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00df, code lost:
        if (r19.equals("3101") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00eb, code lost:
        if (r19.equals("3102") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00f7, code lost:
        if (r19.equals("3103") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0103, code lost:
        if (r19.equals("3104") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x010f, code lost:
        if (r19.equals("3105") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x011b, code lost:
        if (r19.equals("3106") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0127, code lost:
        if (r19.equals("3107") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0133, code lost:
        if (r19.equals("3108") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x013f, code lost:
        if (r19.equals("3109") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x014b, code lost:
        if (r19.equals("3200") != false) goto L_0x014d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x014d, code lost:
        r12 = r22;
        r13 = com.google.zxing.client.result.ExpandedProductParsedResult.POUND;
        r14 = r19.substring(3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0162, code lost:
        if (r19.equals("3201") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x016e, code lost:
        if (r19.equals("3202") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x017a, code lost:
        if (r19.equals("3203") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0186, code lost:
        if (r19.equals("3204") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0192, code lost:
        if (r19.equals("3205") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x019e, code lost:
        if (r19.equals("3206") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x01aa, code lost:
        if (r19.equals("3207") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x01b6, code lost:
        if (r19.equals("3208") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x01c2, code lost:
        if (r19.equals("3209") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x01ce, code lost:
        if (r19.equals("3920") != false) goto L_0x01d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x01d0, code lost:
        r15 = r22;
        r16 = r19.substring(3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x01e3, code lost:
        if (r19.equals("3921") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x01ef, code lost:
        if (r19.equals("3922") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01fb, code lost:
        if (r19.equals("3923") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0207, code lost:
        if (r19.equals("3930") != false) goto L_0x0209;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0211, code lost:
        if (r22.length() >= 4) goto L_0x023a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x021e, code lost:
        if (r19.equals("3931") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x022a, code lost:
        if (r19.equals("3932") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0236, code lost:
        if (r19.equals("3933") == false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x023a, code lost:
        r15 = r22.substring(3);
        r17 = r22.substring(0, 3);
        r16 = r19.substring(3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.zxing.client.result.ExpandedProductParsedResult parse(com.google.zxing.Result r25) {
        /*
            r24 = this;
            com.google.zxing.BarcodeFormat r20 = r25.getBarcodeFormat()
            com.google.zxing.BarcodeFormat r3 = com.google.zxing.BarcodeFormat.RSS_EXPANDED
            r0 = r20
            if (r0 == r3) goto L_0x000c
            r3 = 0
        L_0x000b:
            return r3
        L_0x000c:
            java.lang.String r4 = getMassagedText(r25)
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            java.util.HashMap r18 = new java.util.HashMap
            r18.<init>()
            r21 = 0
        L_0x0026:
            int r3 = r4.length()
            r0 = r21
            if (r0 < r3) goto L_0x0034
            com.google.zxing.client.result.ExpandedProductParsedResult r3 = new com.google.zxing.client.result.ExpandedProductParsedResult
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            goto L_0x000b
        L_0x0034:
            r0 = r21
            java.lang.String r19 = findAIvalue(r0, r4)
            if (r19 != 0) goto L_0x003e
            r3 = 0
            goto L_0x000b
        L_0x003e:
            int r3 = r19.length()
            int r3 = r3 + 2
            int r21 = r21 + r3
            r0 = r21
            java.lang.String r22 = findValue(r0, r4)
            int r3 = r22.length()
            int r21 = r21 + r3
            int r3 = r19.hashCode()
            switch(r3) {
                case 1536: goto L_0x0063;
                case 1537: goto L_0x0070;
                case 1567: goto L_0x007d;
                case 1568: goto L_0x008a;
                case 1570: goto L_0x0097;
                case 1572: goto L_0x00a4;
                case 1574: goto L_0x00b2;
                case 1567966: goto L_0x00c0;
                case 1567967: goto L_0x00d7;
                case 1567968: goto L_0x00e3;
                case 1567969: goto L_0x00ef;
                case 1567970: goto L_0x00fb;
                case 1567971: goto L_0x0107;
                case 1567972: goto L_0x0113;
                case 1567973: goto L_0x011f;
                case 1567974: goto L_0x012b;
                case 1567975: goto L_0x0137;
                case 1568927: goto L_0x0143;
                case 1568928: goto L_0x015a;
                case 1568929: goto L_0x0166;
                case 1568930: goto L_0x0172;
                case 1568931: goto L_0x017e;
                case 1568932: goto L_0x018a;
                case 1568933: goto L_0x0196;
                case 1568934: goto L_0x01a2;
                case 1568935: goto L_0x01ae;
                case 1568936: goto L_0x01ba;
                case 1575716: goto L_0x01c6;
                case 1575717: goto L_0x01db;
                case 1575718: goto L_0x01e7;
                case 1575719: goto L_0x01f3;
                case 1575747: goto L_0x01ff;
                case 1575748: goto L_0x0216;
                case 1575749: goto L_0x0222;
                case 1575750: goto L_0x022e;
                default: goto L_0x0059;
            }
        L_0x0059:
            r0 = r18
            r1 = r19
            r2 = r22
            r0.put(r1, r2)
            goto L_0x0026
        L_0x0063:
            java.lang.String r3 = "00"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
            r6 = r22
            goto L_0x0026
        L_0x0070:
            java.lang.String r3 = "01"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
            r5 = r22
            goto L_0x0026
        L_0x007d:
            java.lang.String r3 = "10"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
            r7 = r22
            goto L_0x0026
        L_0x008a:
            java.lang.String r3 = "11"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
            r8 = r22
            goto L_0x0026
        L_0x0097:
            java.lang.String r3 = "13"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
            r9 = r22
            goto L_0x0026
        L_0x00a4:
            java.lang.String r3 = "15"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
            r10 = r22
            goto L_0x0026
        L_0x00b2:
            java.lang.String r3 = "17"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
            r11 = r22
            goto L_0x0026
        L_0x00c0:
            java.lang.String r3 = "3100"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
        L_0x00ca:
            r12 = r22
            java.lang.String r13 = "KG"
            r3 = 3
            r0 = r19
            java.lang.String r14 = r0.substring(r3)
            goto L_0x0026
        L_0x00d7:
            java.lang.String r3 = "3101"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x00ca
            goto L_0x0059
        L_0x00e3:
            java.lang.String r3 = "3102"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x00ca
            goto L_0x0059
        L_0x00ef:
            java.lang.String r3 = "3103"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x00ca
            goto L_0x0059
        L_0x00fb:
            java.lang.String r3 = "3104"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x00ca
            goto L_0x0059
        L_0x0107:
            java.lang.String r3 = "3105"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x00ca
            goto L_0x0059
        L_0x0113:
            java.lang.String r3 = "3106"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x00ca
            goto L_0x0059
        L_0x011f:
            java.lang.String r3 = "3107"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x00ca
            goto L_0x0059
        L_0x012b:
            java.lang.String r3 = "3108"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x00ca
            goto L_0x0059
        L_0x0137:
            java.lang.String r3 = "3109"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x00ca
            goto L_0x0059
        L_0x0143:
            java.lang.String r3 = "3200"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
        L_0x014d:
            r12 = r22
            java.lang.String r13 = "LB"
            r3 = 3
            r0 = r19
            java.lang.String r14 = r0.substring(r3)
            goto L_0x0026
        L_0x015a:
            java.lang.String r3 = "3201"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x014d
            goto L_0x0059
        L_0x0166:
            java.lang.String r3 = "3202"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x014d
            goto L_0x0059
        L_0x0172:
            java.lang.String r3 = "3203"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x014d
            goto L_0x0059
        L_0x017e:
            java.lang.String r3 = "3204"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x014d
            goto L_0x0059
        L_0x018a:
            java.lang.String r3 = "3205"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x014d
            goto L_0x0059
        L_0x0196:
            java.lang.String r3 = "3206"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x014d
            goto L_0x0059
        L_0x01a2:
            java.lang.String r3 = "3207"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x014d
            goto L_0x0059
        L_0x01ae:
            java.lang.String r3 = "3208"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x014d
            goto L_0x0059
        L_0x01ba:
            java.lang.String r3 = "3209"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x014d
            goto L_0x0059
        L_0x01c6:
            java.lang.String r3 = "3920"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
        L_0x01d0:
            r15 = r22
            r3 = 3
            r0 = r19
            java.lang.String r16 = r0.substring(r3)
            goto L_0x0026
        L_0x01db:
            java.lang.String r3 = "3921"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x01d0
            goto L_0x0059
        L_0x01e7:
            java.lang.String r3 = "3922"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x01d0
            goto L_0x0059
        L_0x01f3:
            java.lang.String r3 = "3923"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x01d0
            goto L_0x0059
        L_0x01ff:
            java.lang.String r3 = "3930"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x0059
        L_0x0209:
            int r3 = r22.length()
            r23 = 4
            r0 = r23
            if (r3 >= r0) goto L_0x023a
            r3 = 0
            goto L_0x000b
        L_0x0216:
            java.lang.String r3 = "3931"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x0209
            goto L_0x0059
        L_0x0222:
            java.lang.String r3 = "3932"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x0209
            goto L_0x0059
        L_0x022e:
            java.lang.String r3 = "3933"
            r0 = r19
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x0209
            goto L_0x0059
        L_0x023a:
            r3 = 3
            r0 = r22
            java.lang.String r15 = r0.substring(r3)
            r3 = 0
            r23 = 3
            r0 = r22
            r1 = r23
            java.lang.String r17 = r0.substring(r3, r1)
            r3 = 3
            r0 = r19
            java.lang.String r16 = r0.substring(r3)
            goto L_0x0026
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.result.ExpandedProductResultParser.parse(com.google.zxing.Result):com.google.zxing.client.result.ExpandedProductParsedResult");
    }

    private static String findAIvalue(int i, String rawText) {
        if (rawText.charAt(i) != '(') {
            return null;
        }
        CharSequence rawTextAux = rawText.substring(i + 1);
        StringBuilder buf = new StringBuilder();
        for (int index = 0; index < rawTextAux.length(); index++) {
            char currentChar = rawTextAux.charAt(index);
            if (currentChar == ')') {
                return buf.toString();
            }
            if (currentChar < '0' || currentChar > '9') {
                return null;
            }
            buf.append(currentChar);
        }
        return buf.toString();
    }

    private static String findValue(int i, String rawText) {
        StringBuilder buf = new StringBuilder();
        String rawTextAux = rawText.substring(i);
        for (int index = 0; index < rawTextAux.length(); index++) {
            char c = rawTextAux.charAt(index);
            if (c == '(') {
                if (findAIvalue(index, rawTextAux) != null) {
                    break;
                }
                buf.append('(');
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
