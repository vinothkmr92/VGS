package net.sourceforge.jtds.jdbc.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import net.sourceforge.jtds.jdbc.ProcEntry;

public class ProcedureCache implements StatementCache {
    private static final int MAX_INITIAL_SIZE = 50;
    private HashMap cache;
    int cacheSize;
    ArrayList free = new ArrayList();
    CacheEntry head = new CacheEntry(null, null);
    CacheEntry tail = new CacheEntry(null, null);

    private static class CacheEntry {
        String key;
        CacheEntry next = this.tail;
        CacheEntry prior = this.head;
        ProcEntry value;

        CacheEntry(String str, ProcEntry procEntry) {
            this.key = str;
            this.value = procEntry;
        }

        void unlink() {
            this.next.prior = this.prior;
            this.prior.next = this.next;
        }

        void link(CacheEntry cacheEntry) {
            this.next = cacheEntry.next;
            this.prior = cacheEntry;
            this.next.prior = this;
            cacheEntry.next = this;
        }
    }

    public ProcedureCache(int i) {
        this.cacheSize = i;
        this.cache = new HashMap(Math.min(50, i) + 1);
    }

    public synchronized Object get(String str) {
        CacheEntry cacheEntry = (CacheEntry) this.cache.get(str);
        if (cacheEntry == null) {
            return null;
        }
        cacheEntry.unlink();
        cacheEntry.link(this.head);
        cacheEntry.value.addRef();
        return cacheEntry.value;
    }

    public synchronized void put(String str, Object obj) {
        ((ProcEntry) obj).addRef();
        CacheEntry cacheEntry = new CacheEntry(str, (ProcEntry) obj);
        this.cache.put(str, cacheEntry);
        cacheEntry.link(this.head);
        scavengeCache();
    }

    public synchronized void remove(String str) {
        CacheEntry cacheEntry = (CacheEntry) this.cache.get(str);
        if (cacheEntry != null) {
            cacheEntry.unlink();
            this.cache.remove(str);
        }
    }

    public synchronized Collection getObsoleteHandles(Collection collection) {
        if (collection != null) {
            for (ProcEntry release : collection) {
                release.release();
            }
        }
        scavengeCache();
        if (this.free.size() <= null) {
            return null;
        }
        collection = this.free;
        this.free = new ArrayList();
        return collection;
    }

    private void scavengeCache() {
        for (CacheEntry cacheEntry = this.tail.prior; cacheEntry != this.head && this.cache.size() > this.cacheSize; cacheEntry = cacheEntry.prior) {
            if (cacheEntry.value.getRefCount() == 0) {
                cacheEntry.unlink();
                this.free.add(cacheEntry.value);
                this.cache.remove(cacheEntry.key);
            }
        }
    }
}
