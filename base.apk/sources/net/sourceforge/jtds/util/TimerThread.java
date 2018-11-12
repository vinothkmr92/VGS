package net.sourceforge.jtds.util;

import java.util.LinkedList;

public class TimerThread extends Thread {
    private static TimerThread instance;
    private long nextTimeout;
    private final LinkedList timerList = new LinkedList();

    public interface TimerListener {
        void timerExpired();
    }

    private static class TimerRequest {
        final TimerListener target;
        final long time;

        TimerRequest(int i, TimerListener timerListener) {
            if (i <= 0) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Invalid timeout parameter ");
                stringBuffer.append(i);
                throw new IllegalArgumentException(stringBuffer.toString());
            }
            this.time = System.currentTimeMillis() + ((long) i);
            this.target = timerListener;
        }
    }

    public static synchronized TimerThread getInstance() {
        TimerThread timerThread;
        synchronized (TimerThread.class) {
            if (instance == null) {
                instance = new TimerThread();
                instance.start();
            }
            timerThread = instance;
        }
        return timerThread;
    }

    public TimerThread() {
        super("jTDS TimerThread");
        setDaemon(true);
    }

    public void run() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r10 = this;
        r0 = r10.timerList;
        monitor-enter(r0);
        r1 = 1;
    L_0x0004:
        if (r1 == 0) goto L_0x005d;
    L_0x0006:
        r2 = r10.nextTimeout;	 Catch:{ InterruptedException -> 0x0056 }
        r4 = java.lang.System.currentTimeMillis();	 Catch:{ InterruptedException -> 0x0056 }
        r6 = 0;	 Catch:{ InterruptedException -> 0x0056 }
        r6 = r2 - r4;	 Catch:{ InterruptedException -> 0x0056 }
        r2 = 0;	 Catch:{ InterruptedException -> 0x0056 }
        r4 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));	 Catch:{ InterruptedException -> 0x0056 }
        if (r4 > 0) goto L_0x0046;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x0015:
        r4 = r10.nextTimeout;	 Catch:{ InterruptedException -> 0x0056 }
        r8 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));	 Catch:{ InterruptedException -> 0x0056 }
        if (r8 != 0) goto L_0x001c;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x001b:
        goto L_0x0046;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x001c:
        r2 = java.lang.System.currentTimeMillis();	 Catch:{ InterruptedException -> 0x0056 }
    L_0x0020:
        r4 = r10.timerList;	 Catch:{ InterruptedException -> 0x0056 }
        r4 = r4.isEmpty();	 Catch:{ InterruptedException -> 0x0056 }
        if (r4 != 0) goto L_0x0042;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x0028:
        r4 = r10.timerList;	 Catch:{ InterruptedException -> 0x0056 }
        r4 = r4.getFirst();	 Catch:{ InterruptedException -> 0x0056 }
        r4 = (net.sourceforge.jtds.util.TimerThread.TimerRequest) r4;	 Catch:{ InterruptedException -> 0x0056 }
        r5 = r4.time;	 Catch:{ InterruptedException -> 0x0056 }
        r7 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1));	 Catch:{ InterruptedException -> 0x0056 }
        if (r7 <= 0) goto L_0x0037;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x0036:
        goto L_0x0042;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x0037:
        r4 = r4.target;	 Catch:{ InterruptedException -> 0x0056 }
        r4.timerExpired();	 Catch:{ InterruptedException -> 0x0056 }
        r4 = r10.timerList;	 Catch:{ InterruptedException -> 0x0056 }
        r4.removeFirst();	 Catch:{ InterruptedException -> 0x0056 }
        goto L_0x0020;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x0042:
        r10.updateNextTimeout();	 Catch:{ InterruptedException -> 0x0056 }
        goto L_0x0004;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x0046:
        r4 = r10.timerList;	 Catch:{ InterruptedException -> 0x0056 }
        r8 = r10.nextTimeout;	 Catch:{ InterruptedException -> 0x0056 }
        r5 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1));	 Catch:{ InterruptedException -> 0x0056 }
        if (r5 != 0) goto L_0x004f;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x004e:
        goto L_0x0050;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x004f:
        r2 = r6;	 Catch:{ InterruptedException -> 0x0056 }
    L_0x0050:
        r4.wait(r2);	 Catch:{ InterruptedException -> 0x0056 }
        goto L_0x0006;
    L_0x0054:
        r1 = move-exception;
        goto L_0x005f;
    L_0x0056:
        r1 = 0;
        r2 = r10.timerList;	 Catch:{ all -> 0x0054 }
        r2.clear();	 Catch:{ all -> 0x0054 }
        goto L_0x0004;	 Catch:{ all -> 0x0054 }
    L_0x005d:
        monitor-exit(r0);	 Catch:{ all -> 0x0054 }
        return;	 Catch:{ all -> 0x0054 }
    L_0x005f:
        monitor-exit(r0);	 Catch:{ all -> 0x0054 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.util.TimerThread.run():void");
    }

    public Object setTimer(int i, TimerListener timerListener) {
        TimerListener timerRequest = new TimerRequest(i, timerListener);
        synchronized (this.timerList) {
            if (this.timerList.isEmpty() == null) {
                if (timerRequest.time < ((TimerRequest) this.timerList.getLast()).time) {
                    timerListener = this.timerList.listIterator();
                    while (timerListener.hasNext()) {
                        if (timerRequest.time < ((TimerRequest) timerListener.next()).time) {
                            timerListener.previous();
                            timerListener.add(timerRequest);
                            break;
                        }
                    }
                }
                this.timerList.addLast(timerRequest);
            } else {
                this.timerList.add(timerRequest);
            }
            if (this.timerList.getFirst() == timerRequest) {
                this.nextTimeout = timerRequest.time;
                this.timerList.notifyAll();
            }
        }
        return timerRequest;
    }

    public boolean cancelTimer(Object obj) {
        boolean remove;
        TimerRequest timerRequest = (TimerRequest) obj;
        synchronized (this.timerList) {
            remove = this.timerList.remove(timerRequest);
            if (this.nextTimeout == timerRequest.time) {
                updateNextTimeout();
            }
        }
        return remove;
    }

    public static synchronized void stopTimer() {
        synchronized (TimerThread.class) {
            if (instance != null) {
                instance.interrupt();
                instance = null;
            }
        }
    }

    public boolean hasExpired(Object obj) {
        TimerRequest timerRequest = (TimerRequest) obj;
        synchronized (this.timerList) {
            obj = this.timerList.contains(timerRequest) ^ 1;
        }
        return obj;
    }

    private void updateNextTimeout() {
        this.nextTimeout = this.timerList.isEmpty() ? 0 : ((TimerRequest) this.timerList.getFirst()).time;
    }
}
