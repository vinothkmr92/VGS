package net.sourceforge.jtds.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

public class ParameterMetaDataImpl implements ParameterMetaData {
    private final int maxPrecision;
    private final ParamInfo[] parameterList;
    private final boolean useLOBs;

    public int isNullable(int i) throws SQLException {
        return 2;
    }

    public ParameterMetaDataImpl(ParamInfo[] paramInfoArr, ConnectionJDBC2 connectionJDBC2) {
        if (paramInfoArr == null) {
            paramInfoArr = new ParamInfo[null];
        }
        this.parameterList = paramInfoArr;
        this.maxPrecision = connectionJDBC2.getMaxPrecision();
        this.useLOBs = connectionJDBC2.getUseLOBs();
    }

    public int getParameterCount() throws SQLException {
        return this.parameterList.length;
    }

    public int getParameterType(int i) throws SQLException {
        if (this.useLOBs) {
            return getParameter(i).jdbcType;
        }
        return Support.convertLOBType(getParameter(i).jdbcType);
    }

    public int getScale(int i) throws SQLException {
        i = getParameter(i);
        return i.scale >= 0 ? i.scale : 0;
    }

    public boolean isSigned(int i) throws SQLException {
        i = getParameter(i).jdbcType;
        if (i != -5) {
            switch (i) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public int getPrecision(int i) throws SQLException {
        i = getParameter(i);
        return i.precision >= 0 ? i.precision : this.maxPrecision;
    }

    public String getParameterTypeName(int i) throws SQLException {
        return getParameter(i).sqlType;
    }

    public String getParameterClassName(int i) throws SQLException {
        return Support.getClassName(getParameterType(i));
    }

    public int getParameterMode(int i) throws SQLException {
        i = getParameter(i);
        if (!i.isOutput) {
            return i.isSet;
        }
        return i.isSet != 0 ? 2 : 4;
    }

    private ParamInfo getParameter(int i) throws SQLException {
        if (i >= 1) {
            if (i <= this.parameterList.length) {
                return this.parameterList[i - 1];
            }
        }
        throw new SQLException(Messages.get("error.prepare.paramindex", Integer.toString(i)), "07009");
    }
}
