package net.sourceforge.jtds.jdbc;

public class ProcEntry {
    public static final int CURSOR = 3;
    public static final int PREPARE = 2;
    public static final int PREP_FAILED = 4;
    public static final int PROCEDURE = 1;
    private ColInfo[] colMetaData;
    private String name;
    private ParamInfo[] paramMetaData;
    private int refCount;
    private int type;

    public final String toString() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setHandle(int i) {
        this.name = Integer.toString(i);
    }

    public ColInfo[] getColMetaData() {
        return this.colMetaData;
    }

    public void setColMetaData(ColInfo[] colInfoArr) {
        this.colMetaData = colInfoArr;
    }

    public ParamInfo[] getParamMetaData() {
        return this.paramMetaData;
    }

    public void setParamMetaData(ParamInfo[] paramInfoArr) {
        this.paramMetaData = paramInfoArr;
    }

    public void setType(int i) {
        this.type = i;
    }

    public int getType() {
        return this.type;
    }

    public void appendDropSQL(StringBuffer stringBuffer) {
        switch (this.type) {
            case 1:
                stringBuffer.append("DROP PROC ");
                stringBuffer.append(this.name);
                stringBuffer.append('\n');
                return;
            case 2:
                stringBuffer.append("EXEC sp_unprepare ");
                stringBuffer.append(this.name);
                stringBuffer.append('\n');
                return;
            case 3:
                stringBuffer.append("EXEC sp_cursorunprepare ");
                stringBuffer.append(this.name);
                stringBuffer.append('\n');
                return;
            case 4:
                return;
            default:
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Invalid cached statement type ");
                stringBuffer2.append(this.type);
                throw new IllegalStateException(stringBuffer2.toString());
        }
    }

    public void addRef() {
        this.refCount++;
    }

    public void release() {
        if (this.refCount > 0) {
            this.refCount--;
        }
    }

    public int getRefCount() {
        return this.refCount;
    }
}
