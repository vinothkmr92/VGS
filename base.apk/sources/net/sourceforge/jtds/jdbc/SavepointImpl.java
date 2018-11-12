package net.sourceforge.jtds.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

class SavepointImpl implements Savepoint {
    private final int id;
    private final String name;

    SavepointImpl(int i) {
        this(i, null);
    }

    SavepointImpl(int i, String str) {
        this.id = i;
        this.name = str;
    }

    public int getSavepointId() throws SQLException {
        if (this.name == null) {
            return this.id;
        }
        throw new SQLException(Messages.get("error.savepoint.named"), "HY024");
    }

    public String getSavepointName() throws SQLException {
        if (this.name != null) {
            return this.name;
        }
        throw new SQLException(Messages.get("error.savepoint.unnamed"), "HY024");
    }

    int getId() {
        return this.id;
    }
}
