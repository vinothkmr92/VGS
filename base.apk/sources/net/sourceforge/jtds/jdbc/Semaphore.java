package net.sourceforge.jtds.jdbc;

public class Semaphore {
    protected long permits;

    public Semaphore(long j) {
        this.permits = j;
    }

    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            while (this.permits <= 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    notify();
                    throw e;
                }
            }
            this.permits--;
        }
    }

    public boolean attempt(long j) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            if (this.permits > 0) {
                this.permits--;
                return true;
            } else if (j <= 0) {
                return false;
            } else {
                try {
                    long currentTimeMillis = System.currentTimeMillis();
                    long j2 = j;
                    do {
                        wait(j2);
                        if (this.permits > 0) {
                            this.permits--;
                            return true;
                        }
                        j2 = j - (System.currentTimeMillis() - currentTimeMillis);
                    } while (j2 > 0);
                    return false;
                } catch (long j3) {
                    notify();
                    throw j3;
                }
            }
        }
    }

    public synchronized void release() {
        this.permits++;
        notify();
    }

    public synchronized void release(long j) {
        long j2 = 0;
        if (j < 0) {
            throw new IllegalArgumentException("Negative argument");
        }
        this.permits += j;
        while (j2 < j) {
            notify();
            j2++;
        }
    }

    public synchronized long permits() {
        return this.permits;
    }
}
