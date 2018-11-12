package net.sourceforge.jtds.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConnectionJDBC3 extends ConnectionJDBC2 {
    private int savepointId;
    private Map savepointProcInTran;
    private ArrayList savepoints;

    ConnectionJDBC3(String str, Properties properties) throws SQLException {
        super(str, properties);
    }

    private void setSavepoint(SavepointImpl savepointImpl) throws SQLException {
        Statement createStatement;
        try {
            createStatement = createStatement();
            try {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("IF @@TRANCOUNT=0 BEGIN SET IMPLICIT_TRANSACTIONS OFF; BEGIN TRAN; SET IMPLICIT_TRANSACTIONS ON; END SAVE TRAN jtds");
                stringBuffer.append(savepointImpl.getId());
                createStatement.execute(stringBuffer.toString());
                if (createStatement != null) {
                    createStatement.close();
                }
                synchronized (this) {
                    if (this.savepoints == null) {
                        this.savepoints = new ArrayList();
                    }
                    this.savepoints.add(savepointImpl);
                }
            } catch (Throwable th) {
                savepointImpl = th;
                if (createStatement != null) {
                    createStatement.close();
                }
                throw savepointImpl;
            }
        } catch (Throwable th2) {
            savepointImpl = th2;
            createStatement = null;
            if (createStatement != null) {
                createStatement.close();
            }
            throw savepointImpl;
        }
    }

    synchronized void clearSavepoints() {
        if (this.savepoints != null) {
            this.savepoints.clear();
        }
        if (this.savepointProcInTran != null) {
            this.savepointProcInTran.clear();
        }
        this.savepointId = 0;
    }

    public synchronized void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkOpen();
        if (this.savepoints == null) {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        }
        int indexOf = this.savepoints.indexOf(savepoint);
        if (indexOf == -1) {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        }
        Object remove = this.savepoints.remove(indexOf);
        if (this.savepointProcInTran != null) {
            if (indexOf != 0) {
                List list = (List) this.savepointProcInTran.get(savepoint);
                if (list != null) {
                    Savepoint savepoint2 = (Savepoint) this.savepoints.get(indexOf - 1);
                    List list2 = (List) this.savepointProcInTran.get(savepoint2);
                    if (list2 == null) {
                        list2 = new ArrayList();
                    }
                    list2.addAll(list);
                    this.savepointProcInTran.put(savepoint2, list2);
                }
            }
            this.savepointProcInTran.remove(remove);
        }
    }

    public synchronized void rollback(Savepoint savepoint) throws SQLException {
        Statement createStatement;
        checkOpen();
        checkLocal("rollback");
        if (this.savepoints == null) {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        }
        int indexOf = this.savepoints.indexOf(savepoint);
        if (indexOf == -1) {
            throw new SQLException(Messages.get("error.connection.badsavep"), "25000");
        } else if (getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.savenorollback"), "25000");
        } else {
            try {
                createStatement = createStatement();
                try {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("ROLLBACK TRAN jtds");
                    stringBuffer.append(((SavepointImpl) savepoint).getId());
                    createStatement.execute(stringBuffer.toString());
                    if (createStatement != null) {
                        createStatement.close();
                    }
                    for (int size = this.savepoints.size() - 1; size >= indexOf; size--) {
                        Object remove = this.savepoints.remove(size);
                        if (this.savepointProcInTran != null) {
                            List<String> list = (List) this.savepointProcInTran.get(remove);
                            if (list != null) {
                                for (String removeCachedProcedure : list) {
                                    removeCachedProcedure(removeCachedProcedure);
                                }
                            }
                        }
                    }
                    setSavepoint((SavepointImpl) savepoint);
                } catch (Throwable th) {
                    savepoint = th;
                    if (createStatement != null) {
                        createStatement.close();
                    }
                    throw savepoint;
                }
            } catch (Throwable th2) {
                savepoint = th2;
                createStatement = null;
                if (createStatement != null) {
                    createStatement.close();
                }
                throw savepoint;
            }
        }
    }

    public synchronized Savepoint setSavepoint() throws SQLException {
        SavepointImpl savepointImpl;
        checkOpen();
        checkLocal("setSavepoint");
        if (getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.savenoset"), "25000");
        }
        savepointImpl = new SavepointImpl(getNextSavepointId());
        setSavepoint(savepointImpl);
        return savepointImpl;
    }

    public synchronized Savepoint setSavepoint(String str) throws SQLException {
        SavepointImpl savepointImpl;
        checkOpen();
        checkLocal("setSavepoint");
        if (getAutoCommit()) {
            throw new SQLException(Messages.get("error.connection.savenoset"), "25000");
        } else if (str == null) {
            throw new SQLException(Messages.get("error.connection.savenullname", (Object) "savepoint"), "25000");
        } else {
            savepointImpl = new SavepointImpl(getNextSavepointId(), str);
            setSavepoint(savepointImpl);
        }
        return savepointImpl;
    }

    private int getNextSavepointId() {
        int i = this.savepointId + 1;
        this.savepointId = i;
        return i;
    }

    void addCachedProcedure(String str, ProcEntry procEntry) {
        super.addCachedProcedure(str, procEntry);
        if (getServerType() == 1 && procEntry.getType() == 1) {
            addCachedProcedure(str);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    synchronized void addCachedProcedure(String str) {
        if (this.savepoints != null) {
            if (this.savepoints.size() != 0) {
                if (this.savepointProcInTran == null) {
                    this.savepointProcInTran = new HashMap();
                }
                Object obj = this.savepoints.get(this.savepoints.size() - 1);
                List list = (List) this.savepointProcInTran.get(obj);
                if (list == null) {
                    list = new ArrayList();
                }
                list.add(str);
                this.savepointProcInTran.put(obj, list);
            }
        }
    }
}
