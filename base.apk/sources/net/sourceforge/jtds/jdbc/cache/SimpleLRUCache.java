package net.sourceforge.jtds.jdbc.cache;

import java.util.HashMap;
import java.util.LinkedList;

public class SimpleLRUCache extends HashMap {
    private final LinkedList list = new LinkedList();
    private final int maxCacheSize;

    public SimpleLRUCache(int i) {
        super(i);
        this.maxCacheSize = Math.max(0, i);
    }

    public synchronized void clear() {
        super.clear();
        this.list.clear();
    }

    public synchronized Object put(Object obj, Object obj2) {
        if (this.maxCacheSize == 0) {
            return null;
        }
        if (!(super.containsKey(obj) || this.list.isEmpty() || this.list.size() + 1 <= this.maxCacheSize)) {
            super.remove(this.list.removeLast());
        }
        freshenKey(obj);
        return super.put(obj, obj2);
    }

    public synchronized Object get(Object obj) {
        Object obj2;
        obj2 = super.get(obj);
        if (obj2 != null) {
            freshenKey(obj);
        }
        return obj2;
    }

    public synchronized Object remove(Object obj) {
        this.list.remove(obj);
        return super.remove(obj);
    }

    private void freshenKey(Object obj) {
        this.list.remove(obj);
        this.list.addFirst(obj);
    }
}
