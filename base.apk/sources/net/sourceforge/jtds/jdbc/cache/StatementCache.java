package net.sourceforge.jtds.jdbc.cache;

import java.util.Collection;

public interface StatementCache {
    Object get(String str);

    Collection getObsoleteHandles(Collection collection);

    void put(String str, Object obj);

    void remove(String str);
}
