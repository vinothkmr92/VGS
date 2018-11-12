package net.sourceforge.jtds.jdbc;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import net.sourceforge.jtds.util.BlobBuffer;

public class TdsData {
    static final int DEFAULT_PRECISION_28 = 28;
    static final int DEFAULT_PRECISION_38 = 38;
    static final int DEFAULT_SCALE = 10;
    private static final int MS_LONGVAR_MAX = 8000;
    private static final int SYBBINARY = 45;
    private static final int SYBBIT = 50;
    private static final int SYBBITN = 104;
    private static final int SYBCHAR = 47;
    private static final int SYBDATE = 49;
    private static final int SYBDATEN = 123;
    private static final int SYBDATETIME = 61;
    private static final int SYBDATETIME4 = 58;
    private static final int SYBDATETIMN = 111;
    private static final int SYBDECIMAL = 106;
    private static final int SYBFLT8 = 62;
    private static final int SYBFLTN = 109;
    private static final int SYBIMAGE = 34;
    private static final int SYBINT1 = 48;
    private static final int SYBINT2 = 52;
    private static final int SYBINT4 = 56;
    private static final int SYBINT8 = 127;
    private static final int SYBINTN = 38;
    private static final int SYBLONGBINARY = 225;
    static final int SYBLONGDATA = 36;
    private static final int SYBMONEY = 60;
    private static final int SYBMONEY4 = 122;
    private static final int SYBMONEYN = 110;
    private static final int SYBNTEXT = 99;
    private static final int SYBNUMERIC = 108;
    private static final int SYBNVARCHAR = 103;
    private static final int SYBREAL = 59;
    private static final int SYBSINT1 = 64;
    private static final int SYBSINT8 = 191;
    private static final int SYBTEXT = 35;
    private static final int SYBTIME = 51;
    private static final int SYBTIMEN = 147;
    private static final int SYBUINT2 = 65;
    private static final int SYBUINT4 = 66;
    private static final int SYBUINT8 = 67;
    private static final int SYBUINTN = 68;
    private static final int SYBUNIQUE = 36;
    private static final int SYBUNITEXT = 174;
    private static final int SYBVARBINARY = 37;
    private static final int SYBVARCHAR = 39;
    private static final int SYBVARIANT = 98;
    private static final int SYBVOID = 31;
    private static final int SYB_CHUNK_SIZE = 8192;
    private static final int SYB_LONGVAR_MAX = 16384;
    private static final int UDT_BINARY = 3;
    private static final int UDT_CHAR = 1;
    private static final int UDT_LONGSYSNAME = 42;
    private static final int UDT_NCHAR = 24;
    private static final int UDT_NEWSYSNAME = 256;
    private static final int UDT_NVARCHAR = 25;
    private static final int UDT_SYSNAME = 18;
    private static final int UDT_TIMESTAMP = 80;
    private static final int UDT_UNICHAR = 34;
    private static final int UDT_UNITEXT = 36;
    private static final int UDT_UNIVARCHAR = 35;
    private static final int UDT_VARBINARY = 4;
    private static final int UDT_VARCHAR = 2;
    private static final int VAR_MAX = 255;
    private static final int XSYBBINARY = 173;
    private static final int XSYBCHAR = 175;
    private static final int XSYBNCHAR = 239;
    private static final int XSYBNVARCHAR = 231;
    private static final int XSYBVARBINARY = 165;
    private static final int XSYBVARCHAR = 167;
    private static final TypeInfo[] types = new TypeInfo[256];

    private static class TypeInfo {
        public final int displaySize;
        public final boolean isCollation;
        public final boolean isSigned;
        public final int jdbcType;
        public final int precision;
        public final int size;
        public final String sqlType;

        TypeInfo(String str, int i, int i2, int i3, boolean z, boolean z2, int i4) {
            this.sqlType = str;
            this.size = i;
            this.precision = i2;
            this.displaySize = i3;
            this.isSigned = z;
            this.isCollation = z2;
            this.jdbcType = i4;
        }
    }

    public static int getTdsVersion(int i) {
        return i >= 1895825409 ? 5 : i >= 117506048 ? 4 : i >= 117440512 ? 3 : i >= 83886080 ? 2 : 1;
    }

    static {
        types[47] = new TypeInfo("char", -1, -1, 1, false, false, 1);
        types[39] = new TypeInfo("varchar", -1, -1, 1, false, false, 12);
        types[38] = new TypeInfo("int", -1, 10, 11, true, false, 4);
        types[48] = new TypeInfo("tinyint", 1, 3, 4, false, false, -6);
        types[52] = new TypeInfo("smallint", 2, 5, 6, true, false, 5);
        types[56] = new TypeInfo("int", 4, 10, 11, true, false, 4);
        types[SYBINT8] = new TypeInfo("bigint", 8, 19, 20, true, false, -5);
        types[62] = new TypeInfo("float", 8, 15, 24, true, false, 8);
        types[61] = new TypeInfo("datetime", 8, 23, 23, false, false, 93);
        types[50] = new TypeInfo("bit", 1, 1, 1, false, false, -7);
        types[35] = new TypeInfo("text", -4, -1, -1, false, true, 2005);
        types[99] = new TypeInfo("ntext", -4, -1, -1, false, true, 2005);
        types[SYBUNITEXT] = new TypeInfo("unitext", -4, -1, -1, false, true, 2005);
        types[34] = new TypeInfo("image", -4, -1, -1, false, false, 2004);
        types[SYBMONEY4] = new TypeInfo("smallmoney", 4, 10, 12, true, false, 3);
        types[60] = new TypeInfo("money", 8, 19, 21, true, false, 3);
        types[58] = new TypeInfo("smalldatetime", 4, 16, 19, false, false, 93);
        types[59] = new TypeInfo("real", 4, 7, 14, true, false, 7);
        types[45] = new TypeInfo("binary", -1, -1, 2, false, false, -2);
        types[31] = new TypeInfo("void", -1, 1, 1, false, false, 0);
        types[37] = new TypeInfo("varbinary", -1, -1, -1, false, false, -3);
        types[103] = new TypeInfo("nvarchar", -1, -1, -1, false, false, 12);
        types[104] = new TypeInfo("bit", -1, 1, 1, false, false, -7);
        types[108] = new TypeInfo("numeric", -1, -1, -1, true, false, 2);
        types[106] = new TypeInfo("decimal", -1, -1, -1, true, false, 3);
        types[109] = new TypeInfo("float", -1, 15, 24, true, false, 8);
        types[110] = new TypeInfo("money", -1, 19, 21, true, false, 3);
        types[111] = new TypeInfo("datetime", -1, 23, 23, false, false, 93);
        types[49] = new TypeInfo("date", 4, 10, 10, false, false, 91);
        types[51] = new TypeInfo("time", 4, 8, 8, false, false, 92);
        types[SYBDATEN] = new TypeInfo("date", -1, 10, 10, false, false, 91);
        types[SYBTIMEN] = new TypeInfo("time", -1, 8, 8, false, false, 92);
        types[XSYBCHAR] = new TypeInfo("char", -2, -1, -1, false, true, 1);
        types[XSYBVARCHAR] = new TypeInfo("varchar", -2, -1, -1, false, true, 12);
        types[XSYBNVARCHAR] = new TypeInfo("nvarchar", -2, -1, -1, false, true, 12);
        types[XSYBNCHAR] = new TypeInfo("nchar", -2, -1, -1, false, true, 1);
        types[XSYBVARBINARY] = new TypeInfo("varbinary", -2, -1, -1, false, false, -3);
        types[XSYBBINARY] = new TypeInfo("binary", -2, -1, -1, false, false, -2);
        types[SYBLONGBINARY] = new TypeInfo("varbinary", -5, -1, 2, false, false, -2);
        types[64] = new TypeInfo("tinyint", 1, 2, 3, false, false, -6);
        types[65] = new TypeInfo("unsigned smallint", 2, 5, 6, false, false, 4);
        types[66] = new TypeInfo("unsigned int", 4, 10, 11, false, false, -5);
        types[67] = new TypeInfo("unsigned bigint", 8, 20, 20, false, false, 3);
        types[68] = new TypeInfo("unsigned int", -1, 10, 11, true, false, -5);
        types[36] = new TypeInfo("uniqueidentifier", -1, 36, 36, false, false, 1);
        types[98] = new TypeInfo("sql_variant", -5, 0, MS_LONGVAR_MAX, false, false, 12);
        types[SYBSINT8] = new TypeInfo("bigint", 8, 19, 20, true, false, -5);
    }

    static int getCollation(ResponseStream responseStream, ColInfo colInfo) throws IOException {
        if (!isCollation(colInfo)) {
            return null;
        }
        colInfo.collation = new byte[5];
        responseStream.read(colInfo.collation);
        return 5;
    }

    static void setColumnCharset(ColInfo colInfo, ConnectionJDBC2 connectionJDBC2) throws SQLException {
        if (connectionJDBC2.isCharsetSpecified()) {
            colInfo.charsetInfo = connectionJDBC2.getCharsetInfo();
        } else if (colInfo.collation != null) {
            byte[] bArr = colInfo.collation;
            byte[] collation = connectionJDBC2.getCollation();
            int i = 0;
            while (i < 5) {
                if (bArr[i] != collation[i]) {
                    break;
                }
                i++;
            }
            if (i == 5) {
                colInfo.charsetInfo = connectionJDBC2.getCharsetInfo();
            } else {
                colInfo.charsetInfo = CharsetInfo.getCharset(bArr);
            }
        }
    }

    static int readType(ResponseStream responseStream, ColInfo colInfo) throws IOException, ProtocolException {
        int tdsVersion = responseStream.getTdsVersion();
        Object obj = tdsVersion >= 4 ? 1 : null;
        Object obj2 = tdsVersion >= 3 ? 1 : null;
        Object obj3 = tdsVersion == 2 ? 1 : null;
        Object obj4 = tdsVersion == 1 ? 1 : null;
        int read = responseStream.read();
        if (types[read] != null) {
            if (obj3 == null || read != 36) {
                int i;
                colInfo.tdsType = read;
                colInfo.jdbcType = types[read].jdbcType;
                colInfo.bufferSize = types[read].size;
                if (colInfo.bufferSize == -5) {
                    colInfo.bufferSize = responseStream.readInt();
                    i = 5;
                } else if (colInfo.bufferSize == -4) {
                    colInfo.bufferSize = responseStream.readInt();
                    i = obj != null ? getCollation(responseStream, colInfo) + 1 : 1;
                    r10 = responseStream.readShort();
                    colInfo.tableName = responseStream.readString(r10);
                    if (responseStream.getTdsVersion() >= 3) {
                        r10 *= 2;
                    }
                    i += r10 + 6;
                } else if (colInfo.bufferSize == -2) {
                    if (obj3 == null || colInfo.tdsType != XSYBCHAR) {
                        colInfo.bufferSize = responseStream.readShort();
                        r10 = 3;
                    } else {
                        colInfo.bufferSize = responseStream.readInt();
                        r10 = 5;
                    }
                    i = obj != null ? getCollation(responseStream, colInfo) + r10 : r10;
                } else if (colInfo.bufferSize == -1) {
                    colInfo.bufferSize = responseStream.read();
                    i = 2;
                } else {
                    i = 1;
                }
                colInfo.displaySize = types[read].displaySize;
                colInfo.precision = types[read].precision;
                colInfo.sqlType = types[read].sqlType;
                switch (read) {
                    case 34:
                        colInfo.precision = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                        colInfo.displaySize = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                        break;
                    case 35:
                    case 39:
                    case 47:
                    case 103:
                    case XSYBVARCHAR /*167*/:
                    case XSYBCHAR /*175*/:
                        colInfo.precision = colInfo.bufferSize;
                        colInfo.displaySize = colInfo.precision;
                        break;
                    case 37:
                    case 45:
                    case XSYBVARBINARY /*165*/:
                    case XSYBBINARY /*173*/:
                    case SYBLONGBINARY /*225*/:
                        colInfo.precision = colInfo.bufferSize;
                        colInfo.displaySize = colInfo.precision * 2;
                        break;
                    case 38:
                        if (colInfo.bufferSize != 8) {
                            if (colInfo.bufferSize != 4) {
                                if (colInfo.bufferSize != 2) {
                                    colInfo.displaySize = types[48].displaySize;
                                    colInfo.precision = types[48].precision;
                                    colInfo.jdbcType = -6;
                                    colInfo.sqlType = types[48].sqlType;
                                    break;
                                }
                                colInfo.displaySize = types[52].displaySize;
                                colInfo.precision = types[52].precision;
                                colInfo.jdbcType = 5;
                                colInfo.sqlType = types[52].sqlType;
                                break;
                            }
                            colInfo.displaySize = types[56].displaySize;
                            colInfo.precision = types[56].precision;
                            break;
                        }
                        colInfo.displaySize = types[SYBINT8].displaySize;
                        colInfo.precision = types[SYBINT8].precision;
                        colInfo.jdbcType = -5;
                        colInfo.sqlType = types[SYBINT8].sqlType;
                        break;
                    case 60:
                    case SYBMONEY4 /*122*/:
                        colInfo.scale = 4;
                        break;
                    case 61:
                        colInfo.scale = 3;
                        break;
                    case 68:
                        if (colInfo.bufferSize == 8) {
                            colInfo.displaySize = types[67].displaySize;
                            colInfo.precision = types[67].precision;
                            colInfo.jdbcType = types[67].jdbcType;
                            colInfo.sqlType = types[67].sqlType;
                            break;
                        } else if (colInfo.bufferSize == 4) {
                            colInfo.displaySize = types[66].displaySize;
                            colInfo.precision = types[66].precision;
                            break;
                        } else if (colInfo.bufferSize == 2) {
                            colInfo.displaySize = types[65].displaySize;
                            colInfo.precision = types[65].precision;
                            colInfo.jdbcType = types[65].jdbcType;
                            colInfo.sqlType = types[65].sqlType;
                            break;
                        } else {
                            throw new ProtocolException("unsigned int null (size 1) not supported");
                        }
                    case 99:
                        colInfo.precision = 1073741823;
                        colInfo.displaySize = 1073741823;
                        break;
                    case 106:
                    case 108:
                        colInfo.precision = responseStream.read();
                        colInfo.scale = responseStream.read();
                        colInfo.displaySize = (colInfo.scale > null ? 2 : true) + colInfo.precision;
                        i += 2;
                        colInfo.sqlType = types[read].sqlType;
                        break;
                    case 109:
                        if (colInfo.bufferSize != 8) {
                            colInfo.displaySize = types[59].displaySize;
                            colInfo.precision = types[59].precision;
                            colInfo.jdbcType = 7;
                            colInfo.sqlType = types[59].sqlType;
                            break;
                        }
                        colInfo.displaySize = types[62].displaySize;
                        colInfo.precision = types[62].precision;
                        break;
                    case 110:
                        if (colInfo.bufferSize == 8) {
                            colInfo.displaySize = types[60].displaySize;
                            colInfo.precision = types[60].precision;
                        } else {
                            colInfo.displaySize = types[SYBMONEY4].displaySize;
                            colInfo.precision = types[SYBMONEY4].precision;
                            colInfo.sqlType = types[SYBMONEY4].sqlType;
                        }
                        colInfo.scale = 4;
                        break;
                    case 111:
                        if (colInfo.bufferSize != 8) {
                            colInfo.displaySize = types[58].displaySize;
                            colInfo.precision = types[58].precision;
                            colInfo.sqlType = types[58].sqlType;
                            colInfo.scale = 0;
                            break;
                        }
                        colInfo.displaySize = types[61].displaySize;
                        colInfo.precision = types[61].precision;
                        colInfo.scale = 3;
                        break;
                    case SYBUNITEXT /*174*/:
                        colInfo.precision = 1073741823;
                        colInfo.displaySize = 1073741823;
                        break;
                    case XSYBNVARCHAR /*231*/:
                    case XSYBNCHAR /*239*/:
                        colInfo.displaySize = colInfo.bufferSize / 2;
                        colInfo.precision = colInfo.displaySize;
                        break;
                    default:
                        break;
                }
                if (colInfo.isIdentity != null) {
                    responseStream = new StringBuffer();
                    responseStream.append(colInfo.sqlType);
                    responseStream.append(" identity");
                    colInfo.sqlType = responseStream.toString();
                }
                if (!(obj4 == null && obj3 == null)) {
                    tdsVersion = colInfo.userType;
                    if (tdsVersion == 18) {
                        colInfo.sqlType = "sysname";
                        colInfo.displaySize = colInfo.bufferSize;
                        colInfo.jdbcType = 12;
                    } else if (tdsVersion != 80) {
                        switch (tdsVersion) {
                            case 1:
                                colInfo.sqlType = "char";
                                colInfo.displaySize = colInfo.bufferSize;
                                colInfo.jdbcType = 1;
                                break;
                            case 2:
                                colInfo.sqlType = "varchar";
                                colInfo.displaySize = colInfo.bufferSize;
                                colInfo.jdbcType = 12;
                                break;
                            case 3:
                                colInfo.sqlType = "binary";
                                colInfo.displaySize = colInfo.bufferSize * 2;
                                colInfo.jdbcType = -2;
                                break;
                            case 4:
                                colInfo.sqlType = "varbinary";
                                colInfo.displaySize = colInfo.bufferSize * 2;
                                colInfo.jdbcType = -3;
                                break;
                            default:
                                break;
                        }
                    } else {
                        colInfo.sqlType = "timestamp";
                        colInfo.displaySize = colInfo.bufferSize * 2;
                        colInfo.jdbcType = -3;
                    }
                }
                if (obj3 != null) {
                    switch (colInfo.userType) {
                        case 24:
                            colInfo.sqlType = "nchar";
                            colInfo.displaySize = colInfo.bufferSize;
                            colInfo.jdbcType = 1;
                            break;
                        case 25:
                            colInfo.sqlType = "nvarchar";
                            colInfo.displaySize = colInfo.bufferSize;
                            colInfo.jdbcType = 12;
                            break;
                        case 34:
                            colInfo.sqlType = "unichar";
                            colInfo.displaySize = colInfo.bufferSize / 2;
                            colInfo.precision = colInfo.displaySize;
                            colInfo.jdbcType = 1;
                            break;
                        case 35:
                            colInfo.sqlType = "univarchar";
                            colInfo.displaySize = colInfo.bufferSize / 2;
                            colInfo.precision = colInfo.displaySize;
                            colInfo.jdbcType = 12;
                            break;
                        case 42:
                            colInfo.sqlType = "longsysname";
                            colInfo.jdbcType = 12;
                            colInfo.displaySize = colInfo.bufferSize;
                            break;
                        default:
                            break;
                    }
                }
                if (obj2 != null) {
                    tdsVersion = colInfo.userType;
                    if (tdsVersion == 80) {
                        colInfo.sqlType = "timestamp";
                        colInfo.jdbcType = -2;
                    } else if (tdsVersion == 256) {
                        colInfo.sqlType = "sysname";
                        colInfo.jdbcType = 12;
                    }
                }
                return i;
            }
        }
        colInfo = new StringBuffer();
        colInfo.append("Invalid TDS data type 0x");
        colInfo.append(Integer.toHexString(read & 255));
        throw new ProtocolException(colInfo.toString());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static Object readData(ConnectionJDBC2 connectionJDBC2, ResponseStream responseStream, ColInfo colInfo) throws IOException, ProtocolException {
        ConnectionJDBC2 connectionJDBC22 = connectionJDBC2;
        ResponseStream responseStream2 = responseStream;
        ColInfo colInfo2 = colInfo;
        int i = colInfo2.tdsType;
        int i2 = -1;
        int i3 = 0;
        int readInt;
        byte[] bArr;
        int read;
        int read2;
        byte[] bArr2;
        switch (i) {
            case 34:
                if (responseStream.read() > 0) {
                    responseStream2.skip(24);
                    readInt = responseStream.readInt();
                    if (readInt == 0 && responseStream.getTdsVersion() <= 2) {
                        break;
                    }
                    Object blobImpl;
                    if (((long) readInt) <= connectionJDBC2.getLobBuffer()) {
                        bArr = new byte[readInt];
                        responseStream2.read(bArr);
                        blobImpl = new BlobImpl(connectionJDBC22, bArr);
                    } else {
                        try {
                            BlobImpl blobImpl2 = new BlobImpl(connectionJDBC22);
                            OutputStream binaryStream = blobImpl2.setBinaryStream(1);
                            byte[] bArr3 = new byte[1024];
                            while (true) {
                                read = responseStream2.read(bArr3, 0, Math.min(readInt, bArr3.length));
                                if (read == -1 || readInt == 0) {
                                    binaryStream.close();
                                    blobImpl = blobImpl2;
                                } else {
                                    binaryStream.write(bArr3, 0, read);
                                    readInt -= read;
                                }
                            }
                        } catch (SQLException e) {
                            throw new IOException(e.getMessage());
                        }
                    }
                    return blobImpl;
                }
                break;
            case 35:
                if (responseStream.read() > 0) {
                    String charset;
                    if (colInfo2.charsetInfo != null) {
                        charset = colInfo2.charsetInfo.getCharset();
                    } else {
                        charset = connectionJDBC2.getCharset();
                    }
                    responseStream2.skip(24);
                    i = responseStream.readInt();
                    if (i == 0 && responseStream.getTdsVersion() <= 2) {
                        break;
                    }
                    ClobImpl clobImpl = new ClobImpl(connectionJDBC22);
                    BlobBuffer blobBuffer = clobImpl.getBlobBuffer();
                    BufferedReader bufferedReader;
                    if (((long) i) <= connectionJDBC2.getLobBuffer()) {
                        bufferedReader = new BufferedReader(new InputStreamReader(responseStream2.getInputStream(i), charset), 1024);
                        bArr = new byte[(i * 2)];
                        i = 0;
                        while (true) {
                            int read3 = bufferedReader.read();
                            if (read3 >= 0) {
                                read = i + 1;
                                bArr[i] = (byte) read3;
                                i = read + 1;
                                bArr[read] = (byte) (read3 >> 8);
                            } else {
                                bufferedReader.close();
                                blobBuffer.setBuffer(bArr, false);
                                if (i == 2 && bArr[0] == (byte) 32 && bArr[1] == (byte) 0 && responseStream.getTdsVersion() < 3) {
                                    i = 0;
                                }
                                blobBuffer.setLength((long) i);
                            }
                        }
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(responseStream2.getInputStream(i), charset), 1024);
                        try {
                            OutputStream binaryStream2 = blobBuffer.setBinaryStream(1, false);
                            while (true) {
                                readInt = bufferedReader.read();
                                if (readInt >= 0) {
                                    binaryStream2.write(readInt);
                                    binaryStream2.write(readInt >> 8);
                                } else {
                                    binaryStream2.close();
                                    bufferedReader.close();
                                }
                            }
                        } catch (SQLException e2) {
                            throw new IOException(e2.getMessage());
                        }
                    }
                    return clobImpl;
                }
                break;
            case 36:
                read2 = responseStream.read();
                if (read2 > 0) {
                    bArr2 = new byte[read2];
                    responseStream2.read(bArr2);
                    return new UniqueIdentifier(bArr2);
                }
                break;
            case 37:
                break;
            case 38:
                read2 = responseStream.read();
                if (read2 == 4) {
                    return new Integer(responseStream.readInt());
                }
                if (read2 != 8) {
                    switch (read2) {
                        case 1:
                            return new Integer(responseStream.read() & 255);
                        case 2:
                            return new Integer(responseStream.readShort());
                        default:
                            break;
                    }
                }
                return new Long(responseStream.readLong());
            case 39:
                break;
            default:
                switch (i) {
                    case 47:
                        break;
                    case 48:
                        return new Integer(responseStream.read() & 255);
                    case 49:
                        break;
                    case 50:
                        return responseStream.read() != 0 ? Boolean.TRUE : Boolean.FALSE;
                    case 51:
                        break;
                    case 52:
                        return new Integer(responseStream.readShort());
                    default:
                        switch (i) {
                            case 58:
                            case 61:
                                break;
                            case 59:
                                return new Float(Float.intBitsToFloat(responseStream.readInt()));
                            case 60:
                                break;
                            case 62:
                                return new Double(Double.longBitsToDouble(responseStream.readLong()));
                            default:
                                switch (i) {
                                    case 65:
                                        return new Integer(responseStream.readShort() & SupportMenu.USER_MASK);
                                    case 66:
                                        return new Long(((long) responseStream.readInt()) & 4294967295L);
                                    case 67:
                                        return responseStream.readUnsignedLong();
                                    case 68:
                                        read2 = responseStream.read();
                                        if (read2 == 4) {
                                            return new Long(((long) responseStream.readInt()) & 4294967295L);
                                        }
                                        if (read2 != 8) {
                                            switch (read2) {
                                                case 1:
                                                    return new Integer(responseStream.read() & 255);
                                                case 2:
                                                    return new Integer(responseStream.readShort() & SupportMenu.USER_MASK);
                                                default:
                                                    break;
                                            }
                                        }
                                        return responseStream.readUnsignedLong();
                                    default:
                                        switch (i) {
                                            case 98:
                                                return getVariant(connectionJDBC2, responseStream);
                                            case 99:
                                                break;
                                            default:
                                                switch (i) {
                                                    case 103:
                                                        read2 = responseStream.read();
                                                        if (read2 > 0) {
                                                            return responseStream2.readUnicodeString(read2 / 2);
                                                        }
                                                        break;
                                                    case 104:
                                                        if (responseStream.read() > 0) {
                                                            return responseStream.read() != 0 ? Boolean.TRUE : Boolean.FALSE;
                                                        }
                                                        break;
                                                    default:
                                                        switch (i) {
                                                            case 108:
                                                                break;
                                                            case 109:
                                                                read2 = responseStream.read();
                                                                if (read2 == 4) {
                                                                    return new Float(Float.intBitsToFloat(responseStream.readInt()));
                                                                }
                                                                if (read2 == 8) {
                                                                    return new Double(Double.longBitsToDouble(responseStream.readLong()));
                                                                }
                                                                break;
                                                            case 110:
                                                                break;
                                                            case 111:
                                                                break;
                                                            default:
                                                                switch (i) {
                                                                    case SYBMONEY4 /*122*/:
                                                                        break;
                                                                    case SYBDATEN /*123*/:
                                                                        break;
                                                                    default:
                                                                        switch (i) {
                                                                            case XSYBBINARY /*173*/:
                                                                                break;
                                                                            case SYBUNITEXT /*174*/:
                                                                                break;
                                                                            case XSYBCHAR /*175*/:
                                                                                break;
                                                                            default:
                                                                                switch (i) {
                                                                                    case 45:
                                                                                        break;
                                                                                    case 56:
                                                                                        return new Integer(responseStream.readInt());
                                                                                    case 106:
                                                                                        break;
                                                                                    case SYBINT8 /*127*/:
                                                                                        return new Long(responseStream.readLong());
                                                                                    case SYBTIMEN /*147*/:
                                                                                        break;
                                                                                    case XSYBVARBINARY /*165*/:
                                                                                        break;
                                                                                    case XSYBVARCHAR /*167*/:
                                                                                        break;
                                                                                    case SYBSINT8 /*191*/:
                                                                                        return new Long(responseStream.readLong());
                                                                                    case SYBLONGBINARY /*225*/:
                                                                                        read2 = responseStream.readInt();
                                                                                        if (read2 != 0) {
                                                                                            if (!"unichar".equals(colInfo2.sqlType)) {
                                                                                                if (!"univarchar".equals(colInfo2.sqlType)) {
                                                                                                    bArr2 = new byte[read2];
                                                                                                    responseStream2.read(bArr2);
                                                                                                    return bArr2;
                                                                                                }
                                                                                            }
                                                                                            char[] cArr = new char[(read2 / 2)];
                                                                                            responseStream2.read(cArr);
                                                                                            if ((read2 & 1) != 0) {
                                                                                                responseStream2.skip(1);
                                                                                            }
                                                                                            if (read2 == 2 && cArr[0] == ' ') {
                                                                                                return "";
                                                                                            }
                                                                                            return new String(cArr);
                                                                                        }
                                                                                        break;
                                                                                    case XSYBNVARCHAR /*231*/:
                                                                                    case XSYBNCHAR /*239*/:
                                                                                        short readShort = responseStream.readShort();
                                                                                        if (readShort != (short) -1) {
                                                                                            return responseStream2.readUnicodeString(readShort / 2);
                                                                                        }
                                                                                        break;
                                                                                    default:
                                                                                        StringBuffer stringBuffer = new StringBuffer();
                                                                                        stringBuffer.append("Unsupported TDS data type 0x");
                                                                                        stringBuffer.append(Integer.toHexString(colInfo2.tdsType & 255));
                                                                                        throw new ProtocolException(stringBuffer.toString());
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
    }

    static boolean isSigned(ColInfo colInfo) {
        int i = colInfo.tdsType;
        if (i >= 0 && i <= 255) {
            if (types[i] != null) {
                if (i == 38 && colInfo.bufferSize == 1) {
                    i = 48;
                }
                return types[i].isSigned;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("TDS data type ");
        stringBuffer.append(i);
        stringBuffer.append(" invalid");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    static boolean isCollation(ColInfo colInfo) {
        colInfo = colInfo.tdsType;
        if (colInfo >= null && colInfo <= 255) {
            if (types[colInfo] != null) {
                return types[colInfo].isCollation;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("TDS data type ");
        stringBuffer.append(colInfo);
        stringBuffer.append(" invalid");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    static boolean isCurrency(ColInfo colInfo) {
        colInfo = colInfo.tdsType;
        if (colInfo >= null && colInfo <= 255) {
            if (types[colInfo] != null) {
                if (!(colInfo == 60 || colInfo == SYBMONEY4)) {
                    if (colInfo != 110) {
                        return null;
                    }
                }
                return true;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("TDS data type ");
        stringBuffer.append(colInfo);
        stringBuffer.append(" invalid");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    static boolean isSearchable(ColInfo colInfo) {
        colInfo = colInfo.tdsType;
        if (colInfo >= null && colInfo <= 255) {
            if (types[colInfo] != null) {
                return types[colInfo].size != -4 ? true : null;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("TDS data type ");
        stringBuffer.append(colInfo);
        stringBuffer.append(" invalid");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    static boolean isUnicode(ColInfo colInfo) {
        colInfo = colInfo.tdsType;
        if (colInfo >= null && colInfo <= 255) {
            if (types[colInfo] != null) {
                if (!(colInfo == 103 || colInfo == XSYBCHAR || colInfo == XSYBNVARCHAR || colInfo == XSYBNCHAR)) {
                    switch (colInfo) {
                        case 98:
                        case 99:
                            break;
                        default:
                            return null;
                    }
                }
                return true;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("TDS data type ");
        stringBuffer.append(colInfo);
        stringBuffer.append(" invalid");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    static void fillInType(ColInfo colInfo) throws SQLException {
        int i = colInfo.jdbcType;
        if (i == -7) {
            colInfo.tdsType = 50;
            colInfo.bufferSize = 1;
            colInfo.displaySize = 1;
            colInfo.precision = 1;
        } else if (i != 12) {
            switch (i) {
                case 4:
                    colInfo.tdsType = 56;
                    colInfo.bufferSize = 4;
                    colInfo.displaySize = 11;
                    colInfo.precision = 10;
                    break;
                case 5:
                    colInfo.tdsType = 52;
                    colInfo.bufferSize = 2;
                    colInfo.displaySize = 6;
                    colInfo.precision = 5;
                    break;
                default:
                    throw new SQLException(Messages.get("error.baddatatype", Integer.toString(colInfo.jdbcType)), "HY000");
            }
        } else {
            colInfo.tdsType = 39;
            colInfo.bufferSize = MS_LONGVAR_MAX;
            colInfo.displaySize = MS_LONGVAR_MAX;
            colInfo.precision = MS_LONGVAR_MAX;
        }
        colInfo.sqlType = types[colInfo.tdsType].sqlType;
        colInfo.scale = 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void getNativeType(ConnectionJDBC2 connectionJDBC2, ParamInfo paramInfo) throws SQLException {
        int i = paramInfo.jdbcType;
        if (i == 1111) {
            i = Support.getJdbcType(paramInfo.value);
        }
        int i2 = 0;
        if (i != 12) {
            if (i != 16) {
                if (i != 1111) {
                    switch (i) {
                        case -7:
                            break;
                        case -6:
                        case 4:
                        case 5:
                            paramInfo.tdsType = 38;
                            paramInfo.sqlType = "int";
                            return;
                        case -5:
                            if (connectionJDBC2.getTdsVersion() < 4) {
                                if (!connectionJDBC2.getSybaseInfo(64)) {
                                    paramInfo.tdsType = 106;
                                    StringBuffer stringBuffer = new StringBuffer();
                                    stringBuffer.append("decimal(");
                                    stringBuffer.append(connectionJDBC2.getMaxPrecision());
                                    stringBuffer.append(')');
                                    paramInfo.sqlType = stringBuffer.toString();
                                    paramInfo.scale = 0;
                                    return;
                                }
                            }
                            paramInfo.tdsType = 38;
                            paramInfo.sqlType = "bigint";
                            return;
                        case -4:
                        case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                        case -2:
                            break;
                        case -1:
                        case 1:
                            break;
                        case 0:
                            break;
                        case 2:
                        case 3:
                            paramInfo.tdsType = 106;
                            connectionJDBC2 = connectionJDBC2.getMaxPrecision();
                            i = 10;
                            if (paramInfo.value instanceof BigDecimal) {
                                i = ((BigDecimal) paramInfo.value).scale();
                            } else if (paramInfo.scale >= 0 && paramInfo.scale <= connectionJDBC2) {
                                i = paramInfo.scale;
                            }
                            StringBuffer stringBuffer2 = new StringBuffer();
                            stringBuffer2.append("decimal(");
                            stringBuffer2.append(connectionJDBC2);
                            stringBuffer2.append(',');
                            stringBuffer2.append(i);
                            stringBuffer2.append(')');
                            paramInfo.sqlType = stringBuffer2.toString();
                            return;
                        case 6:
                        case 8:
                            paramInfo.tdsType = 109;
                            paramInfo.sqlType = "float";
                            return;
                        case 7:
                            paramInfo.tdsType = 109;
                            paramInfo.sqlType = "real";
                            return;
                        default:
                            switch (i) {
                                case 91:
                                    if (connectionJDBC2.getSybaseInfo(2) != null) {
                                        paramInfo.tdsType = SYBDATEN;
                                        paramInfo.sqlType = "date";
                                        return;
                                    }
                                    paramInfo.tdsType = 111;
                                    paramInfo.sqlType = "datetime";
                                    return;
                                case 92:
                                    if (connectionJDBC2.getSybaseInfo(2) != null) {
                                        paramInfo.tdsType = SYBTIMEN;
                                        paramInfo.sqlType = "time";
                                        return;
                                    }
                                    paramInfo.tdsType = 111;
                                    paramInfo.sqlType = "datetime";
                                    return;
                                case 93:
                                    paramInfo.tdsType = 111;
                                    paramInfo.sqlType = "datetime";
                                    return;
                                default:
                                    switch (i) {
                                        case 2004:
                                            break;
                                        case 2005:
                                            break;
                                        default:
                                            throw new SQLException(Messages.get("error.baddatatype", Integer.toString(paramInfo.jdbcType)), "HY000");
                                    }
                            }
                    }
                }
                paramInfo.tdsType = 39;
                paramInfo.sqlType = "varchar(255)";
                return;
            }
            if (connectionJDBC2.getTdsVersion() < 3) {
                if (connectionJDBC2.getSybaseInfo(4) == null) {
                    paramInfo.tdsType = 50;
                    paramInfo.sqlType = "bit";
                    return;
                }
            }
            paramInfo.tdsType = 104;
            paramInfo.sqlType = "bit";
            return;
        }
        if (paramInfo.value == null) {
            i = 0;
        } else {
            i = paramInfo.length;
        }
        if (connectionJDBC2.getTdsVersion() < 3) {
            String charset = connectionJDBC2.getCharset();
            if (i > 0 && ((i <= 8192 || connectionJDBC2.getSybaseInfo(32)) && connectionJDBC2.getSybaseInfo(16) && connectionJDBC2.getUseUnicode() && !"UTF-8".equals(charset))) {
                try {
                    String string = paramInfo.getString(charset);
                    if (!canEncode(string, charset)) {
                        paramInfo.length = string.length();
                        if (paramInfo.length > 8192) {
                            paramInfo.sqlType = "unitext";
                            paramInfo.tdsType = 36;
                            return;
                        }
                        connectionJDBC2 = new StringBuffer();
                        connectionJDBC2.append("univarchar(");
                        connectionJDBC2.append(paramInfo.length);
                        connectionJDBC2.append(')');
                        paramInfo.sqlType = connectionJDBC2.toString();
                        paramInfo.tdsType = SYBLONGBINARY;
                        return;
                    }
                } catch (ConnectionJDBC2 connectionJDBC22) {
                    throw new SQLException(Messages.get("error.generic.ioerror", connectionJDBC22.getMessage()), "HY000");
                }
            }
            if (connectionJDBC22.isWideChar() && i <= 16384) {
                try {
                    byte[] bytes = paramInfo.getBytes(charset);
                    if (bytes == null) {
                        i = 0;
                    } else {
                        i = bytes.length;
                    }
                } catch (ConnectionJDBC2 connectionJDBC222) {
                    throw new SQLException(Messages.get("error.generic.ioerror", connectionJDBC222.getMessage()), "HY000");
                }
            }
            if (i <= 255) {
                paramInfo.tdsType = 39;
                paramInfo.sqlType = "varchar(255)";
            } else if (connectionJDBC222.getSybaseInfo(1) == null) {
                paramInfo.tdsType = 35;
                paramInfo.sqlType = "text";
            } else if (i > 16384) {
                paramInfo.tdsType = 36;
                paramInfo.sqlType = "text";
            } else {
                paramInfo.tdsType = XSYBCHAR;
                connectionJDBC222 = new StringBuffer();
                connectionJDBC222.append("varchar(");
                connectionJDBC222.append(i);
                connectionJDBC222.append(')');
                paramInfo.sqlType = connectionJDBC222.toString();
            }
        } else if (paramInfo.isUnicode && i <= 4000) {
            paramInfo.tdsType = XSYBNVARCHAR;
            paramInfo.sqlType = "nvarchar(4000)";
        } else if (!paramInfo.isUnicode && i <= MS_LONGVAR_MAX) {
            connectionJDBC222 = connectionJDBC222.getCharsetInfo();
            if (i > 0) {
                try {
                    if (connectionJDBC222.isWideChars() && paramInfo.getBytes(connectionJDBC222.getCharset()).length > 8000) {
                        paramInfo.tdsType = 35;
                        paramInfo.sqlType = "text";
                        return;
                    }
                } catch (ConnectionJDBC2 connectionJDBC2222) {
                    throw new SQLException(Messages.get("error.generic.ioerror", connectionJDBC2222.getMessage()), "HY000");
                }
            }
            paramInfo.tdsType = XSYBVARCHAR;
            paramInfo.sqlType = "varchar(8000)";
        } else if (paramInfo.isOutput != null) {
            throw new SQLException(Messages.get("error.textoutparam"), "HY000");
        } else if (paramInfo.isUnicode != null) {
            paramInfo.tdsType = 99;
            paramInfo.sqlType = "ntext";
        } else {
            paramInfo.tdsType = 35;
            paramInfo.sqlType = "text";
        }
    }

    static int getTds5ParamSize(String str, boolean z, ParamInfo paramInfo, boolean z2) {
        int i = 8;
        if (paramInfo.name != null && z2) {
            i = z ? 8 + Support.encodeString(str, paramInfo.name).length : 8 + paramInfo.name.length();
        }
        str = paramInfo.tdsType;
        if (str == true) {
            return i;
        }
        if (str != true) {
            if (!(str == true || str == true || str == true || str == true)) {
                if (str == true || str == true) {
                    return i + 4;
                }
                switch (str) {
                    case 36:
                        break;
                    case 37:
                    case 38:
                    case 39:
                        break;
                    default:
                        z = new StringBuffer();
                        z.append("Unsupported output TDS type 0x");
                        z.append(Integer.toHexString(paramInfo.tdsType));
                        throw new IllegalStateException(z.toString());
                }
            }
            return i + 1;
        }
        return i + 3;
    }

    static void writeTds5ParamFmt(RequestStream requestStream, String str, boolean z, ParamInfo paramInfo, boolean z2) throws IOException {
        if (paramInfo.name == null || !z2) {
            requestStream.write((byte) 0);
        } else if (z) {
            byte[] encodeString = Support.encodeString(str, paramInfo.name);
            requestStream.write((byte) encodeString.length);
            requestStream.write(encodeString);
        } else {
            requestStream.write((byte) paramInfo.name.length());
            requestStream.write(paramInfo.name);
        }
        requestStream.write((byte) paramInfo.isOutput);
        if (paramInfo.sqlType.startsWith("univarchar") != null) {
            requestStream.write(35);
        } else if ("unitext".equals(paramInfo.sqlType) != null) {
            requestStream.write(36);
        } else {
            requestStream.write(0);
        }
        requestStream.write((byte) paramInfo.tdsType);
        str = paramInfo.tdsType;
        if (str != true) {
            if (str != true) {
                byte b = true;
                byte b2 = (byte) 4;
                if (str != true) {
                    if (str == true) {
                        requestStream.write((byte) 8);
                    } else if (str == true || str == true) {
                        requestStream.write((byte) 4);
                    } else if (str == true) {
                        requestStream.write((int) ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                    } else if (str != true) {
                        switch (str) {
                            case 36:
                                if ("text".equals(paramInfo.sqlType) != null) {
                                    b2 = (byte) 3;
                                }
                                requestStream.write(b2);
                                requestStream.write((byte) 0);
                                requestStream.write((byte) 0);
                                break;
                            case 37:
                            case 39:
                                requestStream.write((byte) -1);
                                break;
                            case 38:
                                if ("bigint".equals(paramInfo.sqlType) == null) {
                                    b = true;
                                }
                                requestStream.write(b);
                                break;
                            default:
                                str = new StringBuffer();
                                str.append("Unsupported output TDS type ");
                                str.append(Integer.toHexString(paramInfo.tdsType));
                                throw new IllegalStateException(str.toString());
                        }
                    } else {
                        requestStream.write((int) ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                    }
                } else if ((paramInfo.value instanceof Float) != null) {
                    requestStream.write((byte) 4);
                } else {
                    requestStream.write((byte) 8);
                }
            } else {
                requestStream.write((byte) TdsCore.NTLMAUTH_PKT);
                requestStream.write((byte) 38);
                if (paramInfo.jdbcType) {
                    requestStream.write((byte) 0);
                } else if (paramInfo.value instanceof BigDecimal) {
                    requestStream.write((byte) ((BigDecimal) paramInfo.value).scale());
                } else if (paramInfo.scale >= false || paramInfo.scale > true) {
                    requestStream.write((byte) 10);
                } else {
                    requestStream.write((byte) paramInfo.scale);
                }
            }
        }
        requestStream.write((byte) 0);
    }

    static void writeTds5Param(RequestStream requestStream, CharsetInfo charsetInfo, ParamInfo paramInfo) throws IOException, SQLException {
        if (paramInfo.charsetInfo == null) {
            paramInfo.charsetInfo = charsetInfo;
        }
        charsetInfo = paramInfo.tdsType;
        if (charsetInfo != 50) {
            if (charsetInfo != 106) {
                if (charsetInfo == 111) {
                    putDateTimeValue(requestStream, (DateTime) paramInfo.value);
                    return;
                } else if (charsetInfo != SYBDATEN) {
                    if (charsetInfo != SYBTIMEN) {
                        byte[] bArr;
                        if (charsetInfo != XSYBCHAR) {
                            if (charsetInfo != SYBLONGBINARY) {
                                switch (charsetInfo) {
                                    case 36:
                                        requestStream.write((byte) 0);
                                        requestStream.write((byte) 0);
                                        requestStream.write((byte) 0);
                                        int read;
                                        if ((paramInfo.value instanceof InputStream) != null) {
                                            bArr = new byte[8192];
                                            read = ((InputStream) paramInfo.value).read(bArr);
                                            while (read > 0) {
                                                requestStream.write((byte) read);
                                                requestStream.write((byte) (read >> 8));
                                                requestStream.write((byte) (read >> 16));
                                                requestStream.write((byte) ((read >> 24) | 128));
                                                requestStream.write(bArr, 0, read);
                                                read = ((InputStream) paramInfo.value).read(bArr);
                                            }
                                        } else if ((paramInfo.value instanceof Reader) != null && paramInfo.charsetInfo.isWideChars() == null) {
                                            charsetInfo = new char[8192];
                                            read = ((Reader) paramInfo.value).read(charsetInfo);
                                            while (read > 0) {
                                                requestStream.write((byte) read);
                                                requestStream.write((byte) (read >> 8));
                                                requestStream.write((byte) (read >> 16));
                                                requestStream.write((byte) ((read >> 24) | 128));
                                                requestStream.write(Support.encodeString(paramInfo.charsetInfo.getCharset(), new String(charsetInfo, 0, read)));
                                                read = ((Reader) paramInfo.value).read(charsetInfo);
                                            }
                                        } else if (paramInfo.value != null) {
                                            int i;
                                            if ("unitext".equals(paramInfo.sqlType) != null) {
                                                charsetInfo = paramInfo.getString(paramInfo.charsetInfo.getCharset());
                                                paramInfo = null;
                                                while (paramInfo < charsetInfo.length()) {
                                                    i = 4096;
                                                    if (charsetInfo.length() - paramInfo < 4096) {
                                                        i = charsetInfo.length() - paramInfo;
                                                    }
                                                    read = i * 2;
                                                    requestStream.write((byte) read);
                                                    requestStream.write((byte) (read >> 8));
                                                    requestStream.write((byte) (read >> 16));
                                                    requestStream.write((byte) ((read >> 24) | 128));
                                                    read = paramInfo + i;
                                                    requestStream.write(charsetInfo.substring(paramInfo, read).toCharArray(), 0, i);
                                                    paramInfo = read;
                                                }
                                            } else {
                                                charsetInfo = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                                paramInfo = null;
                                                while (paramInfo < charsetInfo.length) {
                                                    i = charsetInfo.length - paramInfo >= 8192 ? 8192 : charsetInfo.length - paramInfo;
                                                    requestStream.write((byte) i);
                                                    requestStream.write((byte) (i >> 8));
                                                    requestStream.write((byte) (i >> 16));
                                                    requestStream.write((byte) ((i >> 24) | 128));
                                                    int i2 = paramInfo;
                                                    paramInfo = null;
                                                    while (paramInfo < i) {
                                                        int i3 = i2 + 1;
                                                        requestStream.write(charsetInfo[i2]);
                                                        paramInfo++;
                                                        i2 = i3;
                                                    }
                                                    paramInfo = i2;
                                                }
                                            }
                                        }
                                        requestStream.write(0);
                                        return;
                                    case 37:
                                        if (paramInfo.value == null) {
                                            requestStream.write((byte) 0);
                                            return;
                                        }
                                        bArr = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                        if (requestStream.getTdsVersion() >= 3 || bArr.length != null) {
                                            requestStream.write((byte) bArr.length);
                                            requestStream.write(bArr);
                                            return;
                                        }
                                        requestStream.write((byte) 1);
                                        requestStream.write((byte) 0);
                                        return;
                                    case 38:
                                        if (paramInfo.value == null) {
                                            requestStream.write((byte) 0);
                                            return;
                                        } else if ("bigint".equals(paramInfo.sqlType) != null) {
                                            requestStream.write((byte) 8);
                                            requestStream.write(((Number) paramInfo.value).longValue());
                                            return;
                                        } else {
                                            requestStream.write((byte) 4);
                                            requestStream.write(((Number) paramInfo.value).intValue());
                                            return;
                                        }
                                    case 39:
                                        if (paramInfo.value == null) {
                                            requestStream.write((byte) 0);
                                            return;
                                        }
                                        bArr = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                        if (bArr.length == null) {
                                            bArr = new byte[]{(byte) 32};
                                        }
                                        if (bArr.length > 255) {
                                            throw new SQLException(Messages.get("error.generic.truncmbcs"), "HY000");
                                        }
                                        requestStream.write((byte) bArr.length);
                                        requestStream.write(bArr);
                                        return;
                                    default:
                                        switch (charsetInfo) {
                                            case 108:
                                                break;
                                            case 109:
                                                if (paramInfo.value == null) {
                                                    requestStream.write((byte) 0);
                                                    return;
                                                } else if ((paramInfo.value instanceof Float) != null) {
                                                    requestStream.write((byte) 4);
                                                    requestStream.write(((Number) paramInfo.value).floatValue());
                                                    return;
                                                } else {
                                                    requestStream.write((byte) 8);
                                                    requestStream.write(((Number) paramInfo.value).doubleValue());
                                                    return;
                                                }
                                            default:
                                                charsetInfo = new StringBuffer();
                                                charsetInfo.append("Unsupported output TDS type ");
                                                charsetInfo.append(Integer.toHexString(paramInfo.tdsType));
                                                throw new IllegalStateException(charsetInfo.toString());
                                        }
                                }
                            } else if (paramInfo.value == null) {
                                requestStream.write(0);
                                return;
                            } else if (paramInfo.sqlType.startsWith("univarchar") != null) {
                                charsetInfo = paramInfo.getString(paramInfo.charsetInfo.getCharset());
                                if (charsetInfo.length() == null) {
                                    charsetInfo = " ";
                                }
                                requestStream.write(charsetInfo.length() * 2);
                                requestStream.write(charsetInfo.toCharArray(), 0, charsetInfo.length());
                                return;
                            } else {
                                bArr = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                if (bArr.length > null) {
                                    requestStream.write(bArr.length);
                                    requestStream.write(bArr);
                                    return;
                                }
                                requestStream.write(1);
                                requestStream.write((byte) 0);
                                return;
                            }
                        } else if (paramInfo.value == null) {
                            requestStream.write((byte) 0);
                            return;
                        } else {
                            bArr = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                            if (bArr.length == null) {
                                bArr = new byte[]{(byte) 32};
                            }
                            requestStream.write(bArr.length);
                            requestStream.write(bArr);
                            return;
                        }
                    } else if (paramInfo.value == null) {
                        requestStream.write((byte) 0);
                        return;
                    } else {
                        requestStream.write((byte) 4);
                        requestStream.write(((DateTime) paramInfo.value).getTime());
                        return;
                    }
                } else if (paramInfo.value == null) {
                    requestStream.write((byte) 0);
                    return;
                } else {
                    requestStream.write((byte) 4);
                    requestStream.write(((DateTime) paramInfo.value).getDate());
                    return;
                }
            }
            BigDecimal bigDecimal = null;
            if (paramInfo.value != null) {
                if ((paramInfo.value instanceof Long) != null) {
                    bigDecimal = new BigDecimal(paramInfo.value.toString());
                } else {
                    bigDecimal = (BigDecimal) paramInfo.value;
                }
            }
            requestStream.write(bigDecimal);
        } else if (paramInfo.value == null) {
            requestStream.write((byte) 0);
        } else {
            requestStream.write((byte) ((Boolean) paramInfo.value).booleanValue());
        }
    }

    static void putCollation(RequestStream requestStream, ParamInfo paramInfo) throws IOException {
        if (!types[paramInfo.tdsType].isCollation) {
            return;
        }
        if (paramInfo.collation != null) {
            requestStream.write(paramInfo.collation);
        } else {
            requestStream.write(new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0});
        }
    }

    static void writeParam(RequestStream requestStream, CharsetInfo charsetInfo, byte[] bArr, ParamInfo paramInfo) throws IOException {
        byte b = (byte) 4;
        int i = 0;
        Object obj = requestStream.getTdsVersion() >= 4 ? 1 : null;
        if (obj != null && paramInfo.collation == null) {
            paramInfo.collation = bArr;
        }
        if (paramInfo.charsetInfo == null) {
            paramInfo.charsetInfo = charsetInfo;
        }
        int i2;
        byte[] bytes;
        String string;
        switch (paramInfo.tdsType) {
            case 34:
                if (paramInfo.value == null) {
                    i2 = null;
                } else {
                    i2 = paramInfo.length;
                }
                requestStream.write((byte) paramInfo.tdsType);
                if (i2 > null) {
                    if ((paramInfo.value instanceof InputStream) != null) {
                        requestStream.write(i2);
                        requestStream.write(i2);
                        requestStream.writeStreamBytes((InputStream) paramInfo.value, i2);
                        return;
                    }
                    bytes = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                    requestStream.write(bytes.length);
                    requestStream.write(bytes.length);
                    requestStream.write(bytes);
                    return;
                } else if (requestStream.getTdsVersion() < 3) {
                    requestStream.write(1);
                    requestStream.write(1);
                    requestStream.write((byte) 0);
                    return;
                } else {
                    requestStream.write(i2);
                    requestStream.write(i2);
                    return;
                }
            case 35:
                if (paramInfo.value == null) {
                    i2 = null;
                } else {
                    i2 = paramInfo.length;
                    if (i2 == null && requestStream.getTdsVersion() < 3) {
                        paramInfo.value = " ";
                        i2 = true;
                    }
                }
                requestStream.write((byte) paramInfo.tdsType);
                if (i2 <= null) {
                    requestStream.write(i2);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(i2);
                    return;
                } else if ((paramInfo.value instanceof InputStream) != null) {
                    requestStream.write(i2);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(i2);
                    requestStream.writeStreamBytes((InputStream) paramInfo.value, i2);
                    return;
                } else if ((paramInfo.value instanceof Reader) == null || paramInfo.charsetInfo.isWideChars() != null) {
                    bytes = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                    requestStream.write(bytes.length);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(bytes.length);
                    requestStream.write(bytes);
                    return;
                } else {
                    requestStream.write(i2);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(i2);
                    requestStream.writeReaderBytes((Reader) paramInfo.value, i2);
                    return;
                }
            case 37:
                requestStream.write((byte) paramInfo.tdsType);
                requestStream.write((byte) -1);
                if (paramInfo.value == null) {
                    requestStream.write((byte) 0);
                    return;
                }
                bytes = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                if (requestStream.getTdsVersion() >= 3 || bytes.length != null) {
                    requestStream.write((byte) bytes.length);
                    requestStream.write(bytes);
                    return;
                }
                requestStream.write((byte) 1);
                requestStream.write((byte) 0);
                return;
            case 38:
                requestStream.write((byte) paramInfo.tdsType);
                if (paramInfo.value == null) {
                    if ("bigint".equals(paramInfo.sqlType) != null) {
                        b = (byte) 8;
                    }
                    requestStream.write(b);
                    requestStream.write((byte) 0);
                    return;
                } else if ("bigint".equals(paramInfo.sqlType) != null) {
                    requestStream.write((byte) 8);
                    requestStream.write((byte) 8);
                    requestStream.write(((Number) paramInfo.value).longValue());
                    return;
                } else {
                    requestStream.write((byte) 4);
                    requestStream.write((byte) 4);
                    requestStream.write(((Number) paramInfo.value).intValue());
                    return;
                }
            case 39:
                if (paramInfo.value == null) {
                    requestStream.write((byte) paramInfo.tdsType);
                    requestStream.write((byte) -1);
                    requestStream.write((byte) 0);
                    return;
                }
                bytes = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                if (bytes.length <= 255) {
                    if (bytes.length == null) {
                        bytes = new byte[]{32};
                    }
                    requestStream.write((byte) paramInfo.tdsType);
                    requestStream.write((byte) -1);
                    requestStream.write((byte) bytes.length);
                    requestStream.write(bytes);
                    return;
                } else if (bytes.length > MS_LONGVAR_MAX || requestStream.getTdsVersion() < 3) {
                    requestStream.write((byte) 35);
                    requestStream.write(bytes.length);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(bytes.length);
                    requestStream.write(bytes);
                    return;
                } else {
                    requestStream.write((byte) -89);
                    requestStream.write((short) 8000);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write((short) bytes.length);
                    requestStream.write(bytes);
                    return;
                }
            case 50:
                requestStream.write((byte) paramInfo.tdsType);
                if (paramInfo.value == null) {
                    requestStream.write((byte) 0);
                    return;
                } else {
                    requestStream.write((byte) ((Boolean) paramInfo.value).booleanValue());
                    return;
                }
            case 99:
                if (paramInfo.value != null) {
                    i = paramInfo.length;
                }
                requestStream.write((byte) paramInfo.tdsType);
                if (i <= 0) {
                    requestStream.write(i);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(i);
                    return;
                } else if ((paramInfo.value instanceof Reader) != null) {
                    requestStream.write(i);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(i * 2);
                    requestStream.writeReaderChars((Reader) paramInfo.value, i);
                    return;
                } else if ((paramInfo.value instanceof InputStream) == null || paramInfo.charsetInfo.isWideChars() != null) {
                    string = paramInfo.getString(paramInfo.charsetInfo.getCharset());
                    int length = string.length();
                    requestStream.write(length);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(length * 2);
                    requestStream.write(string);
                    return;
                } else {
                    requestStream.write(i);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(i * 2);
                    requestStream.writeReaderChars(new InputStreamReader((InputStream) paramInfo.value, paramInfo.charsetInfo.getCharset()), i);
                    return;
                }
            case 104:
                requestStream.write((byte) 104);
                requestStream.write((byte) 1);
                if (paramInfo.value == null) {
                    requestStream.write((byte) 0);
                    return;
                }
                requestStream.write((byte) 1);
                requestStream.write((byte) ((Boolean) paramInfo.value).booleanValue());
                return;
            case 106:
            case 108:
                requestStream.write((byte) paramInfo.tdsType);
                BigDecimal bigDecimal = null;
                bArr = requestStream.getMaxPrecision();
                if (paramInfo.value == null) {
                    if (paramInfo.jdbcType != -5) {
                        i = (paramInfo.scale < 0 || paramInfo.scale > bArr) ? 10 : paramInfo.scale;
                    }
                } else if ((paramInfo.value instanceof Long) != null) {
                    bigDecimal = new BigDecimal(((Long) paramInfo.value).toString());
                } else {
                    bigDecimal = (BigDecimal) paramInfo.value;
                    i = bigDecimal.scale();
                }
                requestStream.write(requestStream.getMaxDecimalBytes());
                requestStream.write((byte) bArr);
                requestStream.write((byte) i);
                requestStream.write(bigDecimal);
                return;
            case 109:
                requestStream.write((byte) paramInfo.tdsType);
                if ((paramInfo.value instanceof Float) != null) {
                    requestStream.write((byte) 4);
                    requestStream.write((byte) 4);
                    requestStream.write(((Number) paramInfo.value).floatValue());
                    return;
                }
                requestStream.write((byte) 8);
                if (paramInfo.value == null) {
                    requestStream.write((byte) 0);
                    return;
                }
                requestStream.write((byte) 8);
                requestStream.write(((Number) paramInfo.value).doubleValue());
                return;
            case 111:
                requestStream.write((byte) 111);
                requestStream.write((byte) 8);
                putDateTimeValue(requestStream, (DateTime) paramInfo.value);
                return;
            case XSYBVARBINARY /*165*/:
                requestStream.write((byte) paramInfo.tdsType);
                requestStream.write((short) 8000);
                if (paramInfo.value == null) {
                    requestStream.write((short) -1);
                    return;
                }
                bytes = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                requestStream.write((short) bytes.length);
                requestStream.write(bytes);
                return;
            case XSYBVARCHAR /*167*/:
                if (paramInfo.value == null) {
                    requestStream.write((byte) paramInfo.tdsType);
                    requestStream.write((short) 8000);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write((short) -1);
                    return;
                }
                bytes = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                if (bytes.length > MS_LONGVAR_MAX) {
                    requestStream.write((byte) 35);
                    requestStream.write(bytes.length);
                    if (obj != null) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(bytes.length);
                    requestStream.write(bytes);
                    return;
                }
                requestStream.write((byte) paramInfo.tdsType);
                requestStream.write((short) 8000);
                if (obj != null) {
                    putCollation(requestStream, paramInfo);
                }
                requestStream.write((short) bytes.length);
                requestStream.write(bytes);
                return;
            case XSYBNVARCHAR /*231*/:
                requestStream.write((byte) paramInfo.tdsType);
                requestStream.write((short) 8000);
                if (obj != null) {
                    putCollation(requestStream, paramInfo);
                }
                if (paramInfo.value == null) {
                    requestStream.write((short) -1);
                    return;
                }
                string = paramInfo.getString(paramInfo.charsetInfo.getCharset());
                requestStream.write((short) (string.length() * 2));
                requestStream.write(string);
                return;
            default:
                charsetInfo = new StringBuffer();
                charsetInfo.append("Unsupported output TDS type ");
                charsetInfo.append(Integer.toHexString(paramInfo.tdsType));
                throw new IllegalStateException(charsetInfo.toString());
        }
    }

    private TdsData() {
    }

    private static Object getDatetimeValue(ResponseStream responseStream, int i) throws IOException, ProtocolException {
        i = i == 111 ? responseStream.read() : i == 58 ? 4 : 8;
        if (i == 0) {
            return null;
        }
        if (i == 4) {
            return new DateTime((short) (responseStream.readShort() & SupportMenu.USER_MASK), (short) responseStream.readShort());
        } else if (i == 8) {
            return new DateTime(responseStream.readInt(), responseStream.readInt());
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Invalid DATETIME value with size of ");
            stringBuffer.append(i);
            stringBuffer.append(" bytes.");
            throw new ProtocolException(stringBuffer.toString());
        }
    }

    private static void putDateTimeValue(RequestStream requestStream, DateTime dateTime) throws IOException {
        if (dateTime == null) {
            requestStream.write((byte) null);
            return;
        }
        requestStream.write((byte) 8);
        requestStream.write(dateTime.getDate());
        requestStream.write(dateTime.getTime());
    }

    private static Object getMoneyValue(ResponseStream responseStream, int i) throws IOException, ProtocolException {
        i = i == 60 ? 8 : i == 110 ? responseStream.read() : 4;
        if (i == 4) {
            responseStream = BigInteger.valueOf((long) responseStream.readInt());
        } else if (i == 8) {
            responseStream = BigInteger.valueOf(((((((((long) (((byte) responseStream.read()) & 255)) + (((long) (((byte) responseStream.read()) & 255)) << 8)) + (((long) (((byte) responseStream.read()) & 255)) << 16)) + (((long) (((byte) responseStream.read()) & 255)) << 24)) + (((long) (((byte) responseStream.read()) & 255)) << 32)) + (((long) (((byte) responseStream.read()) & 255)) << 40)) + (((long) (((byte) responseStream.read()) & 255)) << 48)) + (((long) (((byte) responseStream.read()) & 255)) << 56));
        } else if (i != 0) {
            throw new ProtocolException("Invalid money value.");
        } else {
            responseStream = null;
        }
        if (responseStream == null) {
            return null;
        }
        return new BigDecimal(responseStream, 4);
    }

    private static Object getVariant(ConnectionJDBC2 connectionJDBC2, ResponseStream responseStream) throws IOException, ProtocolException {
        int readInt = responseStream.readInt();
        if (readInt == 0) {
            return null;
        }
        ColInfo colInfo = new ColInfo();
        readInt -= 2;
        colInfo.tdsType = responseStream.read();
        readInt -= responseStream.read();
        int i = colInfo.tdsType;
        switch (i) {
            case 58:
            case 61:
                return getDatetimeValue(responseStream, colInfo.tdsType);
            case 59:
                return new Float(Float.intBitsToFloat(responseStream.readInt()));
            case 60:
                break;
            case 62:
                return new Double(Double.longBitsToDouble(responseStream.readLong()));
            default:
                byte[] bArr;
                switch (i) {
                    case 36:
                        bArr = new byte[readInt];
                        responseStream.read(bArr);
                        return new UniqueIdentifier(bArr);
                    case 48:
                        return new Integer(responseStream.read() & 255);
                    case 50:
                        return responseStream.read() != null ? Boolean.TRUE : Boolean.FALSE;
                    case 52:
                        return new Integer(responseStream.readShort());
                    case 56:
                        return new Integer(responseStream.readInt());
                    case 106:
                    case 108:
                        colInfo.precision = responseStream.read();
                        colInfo.scale = responseStream.read();
                        connectionJDBC2 = responseStream.read();
                        i = -1;
                        readInt--;
                        byte[] bArr2 = new byte[readInt];
                        while (true) {
                            int i2 = readInt - 1;
                            if (readInt > 0) {
                                bArr2[i2] = (byte) responseStream.read();
                                readInt = i2;
                            } else {
                                if (connectionJDBC2 != null) {
                                    i = 1;
                                }
                                return new BigDecimal(new BigInteger(i, bArr2), colInfo.scale);
                            }
                        }
                    case SYBMONEY4 /*122*/:
                        break;
                    case SYBINT8 /*127*/:
                        return new Long(responseStream.readLong());
                    case XSYBVARBINARY /*165*/:
                    case XSYBBINARY /*173*/:
                        responseStream.skip(2);
                        bArr = new byte[readInt];
                        responseStream.read(bArr);
                        return bArr;
                    case XSYBVARCHAR /*167*/:
                    case XSYBCHAR /*175*/:
                        getCollation(responseStream, colInfo);
                        try {
                            setColumnCharset(colInfo, connectionJDBC2);
                            responseStream.skip(2);
                            return responseStream.readNonUnicodeString(readInt);
                        } catch (ConnectionJDBC2 connectionJDBC22) {
                            responseStream.skip(readInt + 2);
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append(connectionJDBC22.toString());
                            stringBuffer.append(" [SQLState: ");
                            stringBuffer.append(connectionJDBC22.getSQLState());
                            stringBuffer.append(']');
                            throw new ProtocolException(stringBuffer.toString());
                        }
                    case XSYBNVARCHAR /*231*/:
                    case XSYBNCHAR /*239*/:
                        responseStream.skip(7);
                        return responseStream.readUnicodeString(readInt / 2);
                    default:
                        responseStream = new StringBuffer();
                        responseStream.append("Unsupported TDS data type 0x");
                        responseStream.append(Integer.toHexString(colInfo.tdsType));
                        responseStream.append(" in sql_variant");
                        throw new ProtocolException(responseStream.toString());
                }
                break;
        }
        return getMoneyValue(responseStream, colInfo.tdsType);
    }

    public static String getMSTypeName(String str, int i) {
        if (str.equalsIgnoreCase("text") && i != 35) {
            return "varchar";
        }
        if (!str.equalsIgnoreCase("ntext") || i == 35) {
            return (!str.equalsIgnoreCase("image") || i == 34) ? str : "varbinary";
        } else {
            return "nvarchar";
        }
    }

    private static boolean canEncode(java.lang.String r5, java.lang.String r6) {
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
        r0 = 1;
        if (r5 != 0) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = "UTF-8";
        r1 = r1.equals(r6);
        if (r1 == 0) goto L_0x000d;
    L_0x000c:
        return r0;
    L_0x000d:
        r1 = "ISO-8859-1";
        r1 = r1.equals(r6);
        r2 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r3 = 0;
        if (r1 == 0) goto L_0x002a;
    L_0x0018:
        r6 = r5.length();
        r6 = r6 - r0;
    L_0x001d:
        if (r6 < 0) goto L_0x0029;
    L_0x001f:
        r1 = r5.charAt(r6);
        if (r1 <= r2) goto L_0x0026;
    L_0x0025:
        return r3;
    L_0x0026:
        r6 = r6 + -1;
        goto L_0x001d;
    L_0x0029:
        return r0;
    L_0x002a:
        r1 = "ISO-8859-15";
        r1 = r1.equals(r6);
        if (r1 != 0) goto L_0x0066;
    L_0x0032:
        r1 = "Cp1252";
        r1 = r1.equals(r6);
        if (r1 == 0) goto L_0x003b;
    L_0x003a:
        goto L_0x0066;
    L_0x003b:
        r1 = "US-ASCII";
        r1 = r1.equals(r6);
        if (r1 == 0) goto L_0x0057;
    L_0x0043:
        r6 = r5.length();
        r6 = r6 - r0;
    L_0x0048:
        if (r6 < 0) goto L_0x0056;
    L_0x004a:
        r1 = r5.charAt(r6);
        r2 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        if (r1 <= r2) goto L_0x0053;
    L_0x0052:
        return r3;
    L_0x0053:
        r6 = r6 + -1;
        goto L_0x0048;
    L_0x0056:
        return r0;
    L_0x0057:
        r0 = new java.lang.String;	 Catch:{ UnsupportedEncodingException -> 0x0065 }
        r1 = r5.getBytes(r6);	 Catch:{ UnsupportedEncodingException -> 0x0065 }
        r0.<init>(r1, r6);	 Catch:{ UnsupportedEncodingException -> 0x0065 }
        r5 = r0.equals(r5);	 Catch:{ UnsupportedEncodingException -> 0x0065 }
        return r5;
    L_0x0065:
        return r3;
    L_0x0066:
        r6 = r5.length();
        r6 = r6 - r0;
    L_0x006b:
        if (r6 < 0) goto L_0x007b;
    L_0x006d:
        r1 = r5.charAt(r6);
        if (r1 <= r2) goto L_0x0078;
    L_0x0073:
        r4 = 8364; // 0x20ac float:1.172E-41 double:4.1324E-320;
        if (r1 == r4) goto L_0x0078;
    L_0x0077:
        return r3;
    L_0x0078:
        r6 = r6 + -1;
        goto L_0x006b;
    L_0x007b:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsData.canEncode(java.lang.String, java.lang.String):boolean");
    }
}
