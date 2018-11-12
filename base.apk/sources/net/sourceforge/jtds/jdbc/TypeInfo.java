package net.sourceforge.jtds.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TypeInfo implements Comparable {
    static final int NUM_COLS = 18;
    private final boolean autoIncrement;
    private final boolean caseSensitive;
    private final String createParams;
    private final int dataType;
    private final int distanceFromJdbcType;
    private final boolean fixedPrecScale;
    private final String literalPrefix;
    private final String literalSuffix;
    private final String localTypeName;
    private final short maximumScale;
    private final short minimumScale;
    private final int normalizedType;
    private final short nullable;
    private final int numPrecRadix;
    private final int precision;
    private final short searchable;
    private final int sqlDataType;
    private final int sqlDatetimeSub;
    private final String typeName;
    private final boolean unsigned;

    private int compare(int i, int i2) {
        return i < i2 ? -1 : i == i2 ? 0 : 1;
    }

    public static int normalizeDataType(int i, boolean z) {
        if (i == -150) {
            return 12;
        }
        int i2 = -4;
        if (i != -4) {
            i2 = 2005;
            if (i == -1) {
                if (!z) {
                    i2 = -1;
                }
                return i2;
            } else if (i == 6) {
                return 8;
            } else {
                if (i == 35) {
                    return 12;
                }
                switch (i) {
                    case -11:
                        return 1;
                    case -10:
                        if (!z) {
                            i2 = -1;
                        }
                        return i2;
                    case -9:
                        return 12;
                    case -8:
                        return 1;
                    default:
                        switch (i) {
                            case 9:
                                return 91;
                            case 10:
                                return 92;
                            case 11:
                                return 93;
                            default:
                                return i;
                        }
                }
            }
        }
        if (z) {
            i2 = 2004;
        }
        return i2;
    }

    public TypeInfo(ResultSet resultSet, boolean z) throws SQLException {
        this.typeName = resultSet.getString(1);
        this.dataType = resultSet.getInt(2);
        this.precision = resultSet.getInt(3);
        this.literalPrefix = resultSet.getString(4);
        this.literalSuffix = resultSet.getString(5);
        this.createParams = resultSet.getString(6);
        this.nullable = resultSet.getShort(7);
        this.caseSensitive = resultSet.getBoolean(8);
        this.searchable = resultSet.getShort(9);
        this.unsigned = resultSet.getBoolean(10);
        this.fixedPrecScale = resultSet.getBoolean(11);
        this.autoIncrement = resultSet.getBoolean(12);
        this.localTypeName = resultSet.getString(13);
        if (resultSet.getMetaData().getColumnCount() >= 18) {
            this.minimumScale = resultSet.getShort(14);
            this.maximumScale = resultSet.getShort(15);
            this.sqlDataType = resultSet.getInt(16);
            this.sqlDatetimeSub = resultSet.getInt(17);
            this.numPrecRadix = resultSet.getInt(18);
        } else {
            this.minimumScale = (short) 0;
            this.maximumScale = (short) 0;
            this.sqlDataType = 0;
            this.sqlDatetimeSub = 0;
            this.numPrecRadix = 0;
        }
        this.normalizedType = normalizeDataType(this.dataType, z);
        this.distanceFromJdbcType = determineDistanceFromJdbcType();
    }

    public TypeInfo(String str, int i, boolean z) {
        this.typeName = str;
        this.dataType = i;
        this.autoIncrement = z;
        this.precision = 0;
        this.literalPrefix = null;
        this.literalSuffix = null;
        this.createParams = null;
        this.nullable = (short) 0;
        this.caseSensitive = false;
        this.searchable = (short) 0;
        this.unsigned = false;
        this.fixedPrecScale = false;
        this.localTypeName = null;
        this.minimumScale = (short) 0;
        this.maximumScale = (short) 0;
        this.sqlDataType = 0;
        this.sqlDatetimeSub = 0;
        this.numPrecRadix = 0;
        this.normalizedType = normalizeDataType(i, true);
        this.distanceFromJdbcType = determineDistanceFromJdbcType();
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof TypeInfo)) {
            return false;
        }
        if (compareTo(obj) == null) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return (this.normalizedType * this.dataType) * (this.autoIncrement ? 7 : 11);
    }

    public String toString() {
        String stringBuffer;
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(this.typeName);
        stringBuffer2.append(" (");
        if (this.dataType != this.normalizedType) {
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append(this.dataType);
            stringBuffer3.append("->");
            stringBuffer = stringBuffer3.toString();
        } else {
            stringBuffer = "";
        }
        stringBuffer2.append(stringBuffer);
        stringBuffer2.append(this.normalizedType);
        stringBuffer2.append(')');
        return stringBuffer2.toString();
    }

    public void update(ResultSet resultSet) throws SQLException {
        resultSet.updateString(1, this.typeName);
        resultSet.updateInt(2, this.normalizedType);
        resultSet.updateInt(3, this.precision);
        resultSet.updateString(4, this.literalPrefix);
        resultSet.updateString(5, this.literalSuffix);
        resultSet.updateString(6, this.createParams);
        resultSet.updateShort(7, this.nullable);
        resultSet.updateBoolean(8, this.caseSensitive);
        resultSet.updateShort(9, this.searchable);
        resultSet.updateBoolean(10, this.unsigned);
        resultSet.updateBoolean(11, this.fixedPrecScale);
        resultSet.updateBoolean(12, this.autoIncrement);
        resultSet.updateString(13, this.localTypeName);
        if (resultSet.getMetaData().getColumnCount() >= 18) {
            resultSet.updateShort(14, this.minimumScale);
            resultSet.updateShort(15, this.maximumScale);
            resultSet.updateInt(16, this.sqlDataType);
            resultSet.updateInt(17, this.sqlDatetimeSub);
            resultSet.updateInt(18, this.numPrecRadix);
        }
    }

    public int compareTo(Object obj) {
        TypeInfo typeInfo = (TypeInfo) obj;
        return (compare(this.normalizedType, typeInfo.normalizedType) * 10) + compare(this.distanceFromJdbcType, typeInfo.distanceFromJdbcType);
    }

    private int determineDistanceFromJdbcType() {
        int i = this.dataType;
        if (i == -150) {
            return 8;
        }
        if (i == -11) {
            return 9;
        }
        if (i != -9) {
            int i2 = 0;
            if (i != 6) {
                switch (i) {
                    case 9:
                    case 10:
                    case 11:
                        break;
                    case 12:
                        if (this.typeName.equalsIgnoreCase("varchar")) {
                            return 0;
                        }
                        return this.typeName.equalsIgnoreCase("nvarchar") ? 1 : 2;
                    default:
                        if (this.dataType != this.normalizedType || this.autoIncrement) {
                            i2 = 5;
                        }
                        return i2;
                }
            }
            return 0;
        }
        return this.typeName.equalsIgnoreCase("sysname") ? 4 : 3;
    }
}
