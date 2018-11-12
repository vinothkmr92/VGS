package net.sourceforge.jtds.jdbc;

import java.util.Arrays;

public class ColInfo implements Cloneable {
    int bufferSize;
    String catalog;
    CharsetInfo charsetInfo;
    byte[] collation;
    int displaySize;
    boolean isCaseSensitive;
    boolean isHidden;
    boolean isIdentity;
    boolean isKey;
    boolean isWriteable;
    int jdbcType;
    String name;
    int nullable;
    int precision;
    String realName;
    int scale;
    String schema;
    String sqlType;
    String tableName;
    int tdsType;
    int userType;

    public String toString() {
        return this.name;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ColInfo)) {
            return false;
        }
        boolean z = true;
        if (obj == this) {
            return true;
        }
        ColInfo colInfo = (ColInfo) obj;
        if (this.tdsType != colInfo.tdsType || this.jdbcType != colInfo.jdbcType || this.nullable != colInfo.nullable || this.userType != colInfo.userType || this.displaySize != colInfo.displaySize || this.bufferSize != colInfo.bufferSize || this.precision != colInfo.precision || this.scale != colInfo.scale || this.isCaseSensitive != colInfo.isCaseSensitive || this.isWriteable != colInfo.isWriteable || this.isIdentity != colInfo.isIdentity || this.isKey != colInfo.isKey || this.isHidden != colInfo.isHidden || !compare(this.realName, colInfo.realName) || !compare(this.name, colInfo.name) || !compare(this.tableName, colInfo.tableName) || !compare(this.catalog, colInfo.catalog) || !compare(this.schema, colInfo.schema) || !compare(this.sqlType, colInfo.sqlType) || !compare(this.charsetInfo, colInfo.charsetInfo) || Arrays.equals(this.collation, colInfo.collation) == null) {
            z = false;
        }
        return z;
    }

    private final boolean compare(Object obj, Object obj2) {
        if (obj != obj2) {
            if (obj == null || obj.equals(obj2) == null) {
                return null;
            }
        }
        return true;
    }
}
