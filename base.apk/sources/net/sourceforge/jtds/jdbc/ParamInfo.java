package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;

class ParamInfo implements Cloneable {
    static final int INPUT = 0;
    static final int OUTPUT = 1;
    static final int RETVAL = 2;
    static final int UNICODE = 4;
    CharsetInfo charsetInfo;
    byte[] collation;
    boolean isOutput;
    boolean isRetVal;
    boolean isSet;
    boolean isSetOut;
    boolean isUnicode;
    int jdbcType;
    int length = -1;
    int markerPos = -1;
    String name;
    Object outValue;
    int precision = -1;
    int scale = -1;
    String sqlType;
    int tdsType;
    Object value;

    ParamInfo(int i, boolean z) {
        this.markerPos = i;
        this.isUnicode = z;
    }

    ParamInfo(String str, int i, boolean z, boolean z2) {
        this.name = str;
        this.markerPos = i;
        this.isRetVal = z;
        this.isUnicode = z2;
    }

    ParamInfo(int i, Object obj, int i2) {
        boolean z;
        this.jdbcType = i;
        this.value = obj;
        i = 1;
        this.isSet = true;
        if ((i2 & 1) <= 0) {
            if ((i2 & 2) <= 0) {
                z = false;
                this.isOutput = z;
                this.isRetVal = (i2 & 2) <= 0;
                if ((i2 & 4) > 0) {
                    i = 0;
                }
                this.isUnicode = i;
                if ((obj instanceof String) != 0) {
                    this.length = ((String) obj).length();
                } else if ((obj instanceof byte[]) != 0) {
                    this.length = ((byte[]) obj).length;
                }
            }
        }
        z = true;
        this.isOutput = z;
        if ((i2 & 2) <= 0) {
        }
        this.isRetVal = (i2 & 2) <= 0;
        if ((i2 & 4) > 0) {
            i = 0;
        }
        this.isUnicode = i;
        if ((obj instanceof String) != 0) {
            this.length = ((String) obj).length();
        } else if ((obj instanceof byte[]) != 0) {
            this.length = ((byte[]) obj).length;
        }
    }

    ParamInfo(ColInfo colInfo, String str, Object obj, int i) {
        this.name = str;
        this.tdsType = colInfo.tdsType;
        this.scale = colInfo.scale;
        this.precision = colInfo.precision;
        this.jdbcType = colInfo.jdbcType;
        this.sqlType = colInfo.sqlType;
        this.collation = colInfo.collation;
        this.charsetInfo = colInfo.charsetInfo;
        this.isUnicode = TdsData.isUnicode(colInfo);
        this.isSet = true;
        this.value = obj;
        this.length = i;
    }

    Object getOutValue() throws SQLException {
        if (this.isSetOut) {
            return this.outValue;
        }
        throw new SQLException(Messages.get("error.callable.outparamnotset"), "HY010");
    }

    void setOutValue(Object obj) {
        this.outValue = obj;
        this.isSetOut = true;
    }

    void clearOutValue() {
        this.outValue = null;
        this.isSetOut = false;
    }

    void clearInValue() {
        this.value = null;
        this.isSet = false;
    }

    String getString(String str) throws IOException {
        if (this.value != null) {
            if (!(this.value instanceof String)) {
                if (this.value instanceof InputStream) {
                    try {
                        this.value = loadFromReader(new InputStreamReader((InputStream) this.value, str), this.length);
                        this.length = this.value.length();
                        return this.value;
                    } catch (String str2) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("I/O Error: UnsupportedEncodingException: ");
                        stringBuffer.append(str2.getMessage());
                        throw new IOException(stringBuffer.toString());
                    }
                } else if ((this.value instanceof Reader) == null) {
                    return this.value.toString();
                } else {
                    this.value = loadFromReader((Reader) this.value, this.length);
                    return this.value;
                }
            }
        }
        return this.value;
    }

    byte[] getBytes(String str) throws IOException {
        if (this.value != null) {
            if (!(this.value instanceof byte[])) {
                if (this.value instanceof InputStream) {
                    this.value = loadFromStream((InputStream) this.value, this.length);
                    return (byte[]) this.value;
                } else if (this.value instanceof Reader) {
                    this.value = Support.encodeString(str, loadFromReader((Reader) this.value, this.length));
                    return (byte[]) this.value;
                } else if (this.value instanceof String) {
                    return Support.encodeString(str, (String) this.value);
                } else {
                    return new byte[null];
                }
            }
        }
        return (byte[]) this.value;
    }

    private static byte[] loadFromStream(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 != i) {
            int read = inputStream.read(bArr, i2, i - i2);
            if (read == -1) {
                break;
            }
            i2 += read;
        }
        if (i2 != i) {
            throw new IOException("Data in stream less than specified by length");
        } else if (inputStream.read() < null) {
            return bArr;
        } else {
            throw new IOException("More data in stream than specified by length");
        }
    }

    private static String loadFromReader(Reader reader, int i) throws IOException {
        char[] cArr = new char[i];
        int i2 = 0;
        while (i2 != i) {
            int read = reader.read(cArr, i2, i - i2);
            if (read == -1) {
                break;
            }
            i2 += read;
        }
        if (i2 != i) {
            throw new IOException("Data in stream less than specified by length");
        } else if (reader.read() < null) {
            return new String(cArr);
        } else {
            throw new IOException("More data in stream than specified by length");
        }
    }

    public java.lang.Object clone() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        r0 = super.clone();	 Catch:{ CloneNotSupportedException -> 0x0005 }
        return r0;
    L_0x0005:
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.ParamInfo.clone():java.lang.Object");
    }
}
