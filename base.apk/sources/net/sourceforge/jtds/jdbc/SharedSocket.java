package net.sourceforge.jtds.jdbc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import net.sourceforge.jtds.ssl.SocketFactories;
import net.sourceforge.jtds.ssl.SocketFactoriesSUN;
import net.sourceforge.jtds.util.Logger;

class SharedSocket {
    private static final int TDS_DONE_LEN = 9;
    private static final int TDS_DONE_TOKEN = 253;
    private static final int TDS_HDR_LEN = 8;
    static /* synthetic */ Class class$java$lang$String = null;
    static /* synthetic */ Class class$java$net$Socket = null;
    private static int globalMemUsage = 0;
    private static int memoryBudget = 100000;
    private static int minMemPkts = 8;
    private static int peakMemUsage;
    private static boolean securityViolation;
    private final File bufferDir;
    private final Object cancelMonitor;
    private boolean cancelPending;
    private CharsetInfo charsetInfo;
    private final byte[] doneBuffer;
    private int doneBufferFrag;
    private final byte[] hdrBuf;
    private String host;
    private DataInputStream in;
    private int maxBufSize;
    private DataOutputStream out;
    private int packetCount;
    private int port;
    private VirtualSocket responseOwner;
    protected final int serverType;
    private Socket socket;
    private final ArrayList socketTable;
    private Socket sslSocket;
    private int tdsVersion;

    static class VirtualSocket {
        RandomAccessFile diskQueue;
        final int id;
        int inputPkts;
        final LinkedList pktQueue;
        int pktsOnDisk;
        File queueFile;

        private VirtualSocket(int i) {
            this.id = i;
            this.pktQueue = new LinkedList();
        }
    }

    protected SharedSocket(File file, int i, int i2) {
        this.maxBufSize = 512;
        this.socketTable = new ArrayList();
        this.hdrBuf = new byte[8];
        this.cancelMonitor = new Object();
        this.doneBuffer = new byte[9];
        this.doneBufferFrag = 0;
        this.bufferDir = file;
        this.tdsVersion = i;
        this.serverType = i2;
    }

    SharedSocket(ConnectionJDBC2 connectionJDBC2) throws IOException, UnknownHostException {
        this(connectionJDBC2.getBufferDir(), connectionJDBC2.getTdsVersion(), connectionJDBC2.getServerType());
        this.host = connectionJDBC2.getServerName();
        this.port = connectionJDBC2.getPortNumber();
        if (Driver.JDBC3) {
            this.socket = createSocketForJDBC3(connectionJDBC2);
        } else {
            this.socket = new Socket(this.host, this.port);
        }
        setOut(new DataOutputStream(this.socket.getOutputStream()));
        setIn(new DataInputStream(this.socket.getInputStream()));
        this.socket.setTcpNoDelay(connectionJDBC2.getTcpNoDelay());
        this.socket.setSoTimeout(connectionJDBC2.getSocketTimeout() * 1000);
        this.socket.setKeepAlive(connectionJDBC2.getSocketKeepAlive());
    }

    private java.net.Socket createSocketForJDBC3(net.sourceforge.jtds.jdbc.ConnectionJDBC2 r13) throws java.io.IOException {
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
        r12 = this;
        r0 = r13.getServerName();
        r1 = r13.getPortNumber();
        r2 = r13.getLoginTimeout();
        r13 = r13.getBindAddress();
        r3 = class$java$net$Socket;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        if (r3 != 0) goto L_0x001d;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x0014:
        r3 = "java.net.Socket";	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r3 = class$(r3);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        class$java$net$Socket = r3;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        goto L_0x001f;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x001d:
        r3 = class$java$net$Socket;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x001f:
        r4 = 0;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5 = new java.lang.Class[r4];	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r3 = r3.getConstructor(r5);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5 = new java.lang.Object[r4];	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r3 = r3.newInstance(r5);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r3 = (java.net.Socket) r3;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5 = "java.net.InetSocketAddress";	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5 = java.lang.Class.forName(r5);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r6 = 2;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r7 = new java.lang.Class[r6];	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8 = class$java$lang$String;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        if (r8 != 0) goto L_0x0044;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x003b:
        r8 = "java.lang.String";	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8 = class$(r8);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        class$java$lang$String = r8;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        goto L_0x0046;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x0044:
        r8 = class$java$lang$String;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x0046:
        r7[r4] = r8;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8 = java.lang.Integer.TYPE;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r9 = 1;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r7[r9] = r8;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5 = r5.getConstructor(r7);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r7 = new java.lang.Object[r6];	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r7[r4] = r0;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8 = new java.lang.Integer;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8.<init>(r1);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r7[r9] = r8;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r7 = r5.newInstance(r7);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        if (r13 == 0) goto L_0x009d;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x0062:
        r8 = r13.length();	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        if (r8 <= 0) goto L_0x009d;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x0068:
        r8 = new java.lang.Object[r6];	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8[r4] = r13;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r13 = new java.lang.Integer;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r13.<init>(r4);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8[r9] = r13;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r13 = r5.newInstance(r8);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5 = class$java$net$Socket;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        if (r5 != 0) goto L_0x0084;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x007b:
        r5 = "java.net.Socket";	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5 = class$(r5);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        class$java$net$Socket = r5;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        goto L_0x0086;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x0084:
        r5 = class$java$net$Socket;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x0086:
        r8 = "bind";	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r10 = new java.lang.Class[r9];	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r11 = "java.net.SocketAddress";	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r11 = java.lang.Class.forName(r11);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r10[r4] = r11;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5 = r5.getMethod(r8, r10);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8 = new java.lang.Object[r9];	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8[r4] = r13;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5.invoke(r3, r8);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x009d:
        r13 = class$java$net$Socket;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        if (r13 != 0) goto L_0x00aa;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x00a1:
        r13 = "java.net.Socket";	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r13 = class$(r13);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        class$java$net$Socket = r13;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        goto L_0x00ac;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x00aa:
        r13 = class$java$net$Socket;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
    L_0x00ac:
        r5 = "connect";	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8 = new java.lang.Class[r6];	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r10 = "java.net.SocketAddress";	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r10 = java.lang.Class.forName(r10);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8[r4] = r10;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r10 = java.lang.Integer.TYPE;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r8[r9] = r10;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r13 = r13.getMethod(r5, r8);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5 = new java.lang.Object[r6];	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5[r4] = r7;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r4 = new java.lang.Integer;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r2 = r2 * 1000;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r4.<init>(r2);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r5[r9] = r4;	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        r13.invoke(r3, r5);	 Catch:{ InvocationTargetException -> 0x00d7, Exception -> 0x00d1 }
        return r3;
    L_0x00d1:
        r13 = new java.net.Socket;
        r13.<init>(r0, r1);
        return r13;
    L_0x00d7:
        r13 = move-exception;
        r13 = r13.getTargetException();
        r0 = r13 instanceof java.io.IOException;
        if (r0 == 0) goto L_0x00e3;
    L_0x00e0:
        r13 = (java.io.IOException) r13;
        throw r13;
    L_0x00e3:
        r0 = new java.io.IOException;
        r1 = "Could not create socket";
        r0.<init>(r1);
        r13 = net.sourceforge.jtds.jdbc.Support.linkException(r0, r13);
        r13 = (java.io.IOException) r13;
        throw r13;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedSocket.createSocketForJDBC3(net.sourceforge.jtds.jdbc.ConnectionJDBC2):java.net.Socket");
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (String str2) {
            throw new NoClassDefFoundError(str2.getMessage());
        }
    }

    void enableEncryption(String str) throws IOException {
        Logger.println("Enabling TLS encryption");
        this.sslSocket = (Driver.JDBC3 ? SocketFactories.getSocketFactory(str, this.socket) : SocketFactoriesSUN.getSocketFactory(str, this.socket)).createSocket(getHost(), getPort());
        setOut(new DataOutputStream(this.sslSocket.getOutputStream()));
        setIn(new DataInputStream(this.sslSocket.getInputStream()));
    }

    void disableEncryption() throws IOException {
        Logger.println("Disabling TLS encryption");
        this.sslSocket.close();
        this.sslSocket = null;
        setOut(new DataOutputStream(this.socket.getOutputStream()));
        setIn(new DataInputStream(this.socket.getInputStream()));
    }

    void setCharsetInfo(CharsetInfo charsetInfo) {
        this.charsetInfo = charsetInfo;
    }

    CharsetInfo getCharsetInfo() {
        return this.charsetInfo;
    }

    String getCharset() {
        return this.charsetInfo.getCharset();
    }

    RequestStream getRequestStream(int i, int i2) {
        RequestStream requestStream;
        synchronized (this.socketTable) {
            int i3 = 0;
            while (i3 < this.socketTable.size()) {
                if (this.socketTable.get(i3) == null) {
                    break;
                }
                i3++;
            }
            VirtualSocket virtualSocket = new VirtualSocket(i3);
            if (i3 >= this.socketTable.size()) {
                this.socketTable.add(virtualSocket);
            } else {
                this.socketTable.set(i3, virtualSocket);
            }
            requestStream = new RequestStream(this, virtualSocket, i, i2);
        }
        return requestStream;
    }

    ResponseStream getResponseStream(RequestStream requestStream, int i) {
        return new ResponseStream(this, requestStream.getVirtualSocket(), i);
    }

    int getTdsVersion() {
        return this.tdsVersion;
    }

    protected void setTdsVersion(int i) {
        this.tdsVersion = i;
    }

    static void setMemoryBudget(int i) {
        memoryBudget = i;
    }

    static int getMemoryBudget() {
        return memoryBudget;
    }

    static void setMinMemPkts(int i) {
        minMemPkts = i;
    }

    static int getMinMemPkts() {
        return minMemPkts;
    }

    boolean isConnected() {
        return this.socket != null;
    }

    boolean cancel(net.sourceforge.jtds.jdbc.SharedSocket.VirtualSocket r9) {
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
        r8 = this;
        r0 = r8.cancelMonitor;
        monitor-enter(r0);
        r1 = r8.responseOwner;	 Catch:{ all -> 0x004f }
        r2 = 0;	 Catch:{ all -> 0x004f }
        if (r1 != r9) goto L_0x004d;	 Catch:{ all -> 0x004f }
    L_0x0008:
        r1 = r8.cancelPending;	 Catch:{ all -> 0x004f }
        if (r1 != 0) goto L_0x004d;
    L_0x000c:
        r1 = 1;
        r8.cancelPending = r1;	 Catch:{ IOException -> 0x004d }
        r8.doneBufferFrag = r2;	 Catch:{ IOException -> 0x004d }
        r3 = 8;	 Catch:{ IOException -> 0x004d }
        r4 = new byte[r3];	 Catch:{ IOException -> 0x004d }
        r5 = 6;	 Catch:{ IOException -> 0x004d }
        r4[r2] = r5;	 Catch:{ IOException -> 0x004d }
        r4[r1] = r1;	 Catch:{ IOException -> 0x004d }
        r6 = 2;	 Catch:{ IOException -> 0x004d }
        r4[r6] = r2;	 Catch:{ IOException -> 0x004d }
        r6 = 3;	 Catch:{ IOException -> 0x004d }
        r4[r6] = r3;	 Catch:{ IOException -> 0x004d }
        r7 = 4;	 Catch:{ IOException -> 0x004d }
        r4[r7] = r2;	 Catch:{ IOException -> 0x004d }
        r7 = 5;	 Catch:{ IOException -> 0x004d }
        r4[r7] = r2;	 Catch:{ IOException -> 0x004d }
        r7 = r8.tdsVersion;	 Catch:{ IOException -> 0x004d }
        if (r7 < r6) goto L_0x002c;	 Catch:{ IOException -> 0x004d }
    L_0x002a:
        r6 = 1;	 Catch:{ IOException -> 0x004d }
        goto L_0x002d;	 Catch:{ IOException -> 0x004d }
    L_0x002c:
        r6 = 0;	 Catch:{ IOException -> 0x004d }
    L_0x002d:
        r4[r5] = r6;	 Catch:{ IOException -> 0x004d }
        r5 = 7;	 Catch:{ IOException -> 0x004d }
        r4[r5] = r2;	 Catch:{ IOException -> 0x004d }
        r5 = r8.getOut();	 Catch:{ IOException -> 0x004d }
        r5.write(r4, r2, r3);	 Catch:{ IOException -> 0x004d }
        r3 = r8.getOut();	 Catch:{ IOException -> 0x004d }
        r3.flush();	 Catch:{ IOException -> 0x004d }
        r3 = net.sourceforge.jtds.util.Logger.isActive();	 Catch:{ IOException -> 0x004d }
        if (r3 == 0) goto L_0x004b;	 Catch:{ IOException -> 0x004d }
    L_0x0046:
        r9 = r9.id;	 Catch:{ IOException -> 0x004d }
        net.sourceforge.jtds.util.Logger.logPacket(r9, r2, r4);	 Catch:{ IOException -> 0x004d }
    L_0x004b:
        monitor-exit(r0);	 Catch:{ all -> 0x004f }
        return r1;	 Catch:{ all -> 0x004f }
    L_0x004d:
        monitor-exit(r0);	 Catch:{ all -> 0x004f }
        return r2;	 Catch:{ all -> 0x004f }
    L_0x004f:
        r9 = move-exception;	 Catch:{ all -> 0x004f }
        monitor-exit(r0);	 Catch:{ all -> 0x004f }
        throw r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedSocket.cancel(net.sourceforge.jtds.jdbc.SharedSocket$VirtualSocket):boolean");
    }

    void close() throws java.io.IOException {
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
        r4 = this;
        r0 = net.sourceforge.jtds.util.Logger.isActive();
        if (r0 == 0) goto L_0x0023;
    L_0x0006:
        r0 = new java.lang.StringBuffer;
        r0.<init>();
        r1 = "TdsSocket: Max buffer memory used = ";
        r0.append(r1);
        r1 = peakMemUsage;
        r1 = r1 / 1024;
        r0.append(r1);
        r1 = "KB";
        r0.append(r1);
        r0 = r0.toString();
        net.sourceforge.jtds.util.Logger.println(r0);
    L_0x0023:
        r0 = r4.socketTable;
        monitor-enter(r0);
        r1 = 0;
    L_0x0027:
        r2 = r4.socketTable;	 Catch:{ all -> 0x006c }
        r2 = r2.size();	 Catch:{ all -> 0x006c }
        if (r1 >= r2) goto L_0x004a;	 Catch:{ all -> 0x006c }
    L_0x002f:
        r2 = r4.socketTable;	 Catch:{ all -> 0x006c }
        r2 = r2.get(r1);	 Catch:{ all -> 0x006c }
        r2 = (net.sourceforge.jtds.jdbc.SharedSocket.VirtualSocket) r2;	 Catch:{ all -> 0x006c }
        if (r2 == 0) goto L_0x0047;	 Catch:{ all -> 0x006c }
    L_0x0039:
        r3 = r2.diskQueue;	 Catch:{ all -> 0x006c }
        if (r3 == 0) goto L_0x0047;
    L_0x003d:
        r3 = r2.diskQueue;	 Catch:{ IOException -> 0x0047 }
        r3.close();	 Catch:{ IOException -> 0x0047 }
        r2 = r2.queueFile;	 Catch:{ IOException -> 0x0047 }
        r2.delete();	 Catch:{ IOException -> 0x0047 }
    L_0x0047:
        r1 = r1 + 1;
        goto L_0x0027;
    L_0x004a:
        r1 = r4.sslSocket;	 Catch:{ all -> 0x0061 }
        if (r1 == 0) goto L_0x0056;	 Catch:{ all -> 0x0061 }
    L_0x004e:
        r1 = r4.sslSocket;	 Catch:{ all -> 0x0061 }
        r1.close();	 Catch:{ all -> 0x0061 }
        r1 = 0;	 Catch:{ all -> 0x0061 }
        r4.sslSocket = r1;	 Catch:{ all -> 0x0061 }
    L_0x0056:
        r1 = r4.socket;	 Catch:{ all -> 0x006c }
        if (r1 == 0) goto L_0x005f;	 Catch:{ all -> 0x006c }
    L_0x005a:
        r1 = r4.socket;	 Catch:{ all -> 0x006c }
        r1.close();	 Catch:{ all -> 0x006c }
    L_0x005f:
        monitor-exit(r0);	 Catch:{ all -> 0x006c }
        return;	 Catch:{ all -> 0x006c }
    L_0x0061:
        r1 = move-exception;	 Catch:{ all -> 0x006c }
        r2 = r4.socket;	 Catch:{ all -> 0x006c }
        if (r2 == 0) goto L_0x006b;	 Catch:{ all -> 0x006c }
    L_0x0066:
        r2 = r4.socket;	 Catch:{ all -> 0x006c }
        r2.close();	 Catch:{ all -> 0x006c }
    L_0x006b:
        throw r1;	 Catch:{ all -> 0x006c }
    L_0x006c:
        r1 = move-exception;	 Catch:{ all -> 0x006c }
        monitor-exit(r0);	 Catch:{ all -> 0x006c }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedSocket.close():void");
    }

    void forceClose() {
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
        r2 = this;
        r0 = r2.socket;
        if (r0 == 0) goto L_0x0015;
    L_0x0004:
        r0 = 0;
        r1 = r2.socket;	 Catch:{ IOException -> 0x0011, all -> 0x000b }
        r1.close();	 Catch:{ IOException -> 0x0011, all -> 0x000b }
        goto L_0x0011;
    L_0x000b:
        r1 = move-exception;
        r2.sslSocket = r0;
        r2.socket = r0;
        throw r1;
    L_0x0011:
        r2.sslSocket = r0;
        r2.socket = r0;
    L_0x0015:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedSocket.forceClose():void");
    }

    void closeStream(net.sourceforge.jtds.jdbc.SharedSocket.VirtualSocket r4) {
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
        r3 = this;
        r0 = r3.socketTable;
        r1 = r4.id;
        r2 = 0;
        r0.set(r1, r2);
        r0 = r4.diskQueue;
        if (r0 == 0) goto L_0x0016;
    L_0x000c:
        r0 = r4.diskQueue;	 Catch:{ IOException -> 0x0016 }
        r0.close();	 Catch:{ IOException -> 0x0016 }
        r4 = r4.queueFile;	 Catch:{ IOException -> 0x0016 }
        r4.delete();	 Catch:{ IOException -> 0x0016 }
    L_0x0016:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedSocket.closeStream(net.sourceforge.jtds.jdbc.SharedSocket$VirtualSocket):void");
    }

    byte[] sendNetPacket(VirtualSocket virtualSocket, byte[] bArr) throws IOException {
        synchronized (this.socketTable) {
            while (virtualSocket.inputPkts > 0) {
                if (Logger.isActive()) {
                    Logger.println("TdsSocket: Unread data in input packet queue");
                }
                dequeueInput(virtualSocket);
            }
            if (this.responseOwner == null) {
                getOut().write(bArr, 0, getPktLen(bArr));
            } else {
                Object obj = this.responseOwner == virtualSocket ? 1 : null;
                VirtualSocket virtualSocket2 = this.responseOwner;
                byte[] bArr2 = null;
                do {
                    if (obj == null) {
                        bArr2 = null;
                    }
                    bArr2 = readPacket(bArr2);
                    if (obj == null) {
                        enqueueInput(virtualSocket2, bArr2);
                    }
                } while (bArr2[1] == (byte) 0);
            }
            getOut().write(bArr, 0, getPktLen(bArr));
            if (bArr[1] != (byte) 0) {
                getOut().flush();
                this.responseOwner = virtualSocket;
            }
        }
        return bArr;
    }

    byte[] getNetPacket(VirtualSocket virtualSocket, byte[] bArr) throws IOException {
        synchronized (this.socketTable) {
            if (virtualSocket.inputPkts > 0) {
                virtualSocket = dequeueInput(virtualSocket);
                return virtualSocket;
            } else if (this.responseOwner == null) {
                r1 = new StringBuffer();
                r1.append("Stream ");
                r1.append(virtualSocket.id);
                r1.append(" attempting to read when no request has been sent");
                throw new IOException(r1.toString());
            } else if (this.responseOwner != virtualSocket) {
                r1 = new StringBuffer();
                r1.append("Stream ");
                r1.append(virtualSocket.id);
                r1.append(" is trying to read data that belongs to stream ");
                r1.append(this.responseOwner.id);
                throw new IOException(r1.toString());
            } else {
                virtualSocket = readPacket(bArr);
                return virtualSocket;
            }
        }
    }

    private void enqueueInput(net.sourceforge.jtds.jdbc.SharedSocket.VirtualSocket r6, byte[] r7) throws java.io.IOException {
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
        r5 = this;
        r0 = globalMemUsage;
        r1 = r7.length;
        r0 = r0 + r1;
        r1 = memoryBudget;
        r2 = 0;
        r3 = 1;
        if (r0 <= r1) goto L_0x0059;
    L_0x000a:
        r0 = r6.pktQueue;
        r0 = r0.size();
        r1 = minMemPkts;
        if (r0 < r1) goto L_0x0059;
    L_0x0014:
        r0 = securityViolation;
        if (r0 != 0) goto L_0x0059;
    L_0x0018:
        r0 = r6.diskQueue;
        if (r0 != 0) goto L_0x0059;
    L_0x001c:
        r0 = "jtds";	 Catch:{ SecurityException -> 0x0052 }
        r1 = ".tmp";	 Catch:{ SecurityException -> 0x0052 }
        r4 = r5.bufferDir;	 Catch:{ SecurityException -> 0x0052 }
        r0 = java.io.File.createTempFile(r0, r1, r4);	 Catch:{ SecurityException -> 0x0052 }
        r6.queueFile = r0;	 Catch:{ SecurityException -> 0x0052 }
        r0 = new java.io.RandomAccessFile;	 Catch:{ SecurityException -> 0x0052 }
        r1 = r6.queueFile;	 Catch:{ SecurityException -> 0x0052 }
        r4 = "rw";	 Catch:{ SecurityException -> 0x0052 }
        r0.<init>(r1, r4);	 Catch:{ SecurityException -> 0x0052 }
        r6.diskQueue = r0;	 Catch:{ SecurityException -> 0x0052 }
    L_0x0033:
        r0 = r6.pktQueue;	 Catch:{ SecurityException -> 0x0052 }
        r0 = r0.size();	 Catch:{ SecurityException -> 0x0052 }
        if (r0 <= 0) goto L_0x0059;	 Catch:{ SecurityException -> 0x0052 }
    L_0x003b:
        r0 = r6.pktQueue;	 Catch:{ SecurityException -> 0x0052 }
        r0 = r0.removeFirst();	 Catch:{ SecurityException -> 0x0052 }
        r0 = (byte[]) r0;	 Catch:{ SecurityException -> 0x0052 }
        r1 = r6.diskQueue;	 Catch:{ SecurityException -> 0x0052 }
        r4 = getPktLen(r0);	 Catch:{ SecurityException -> 0x0052 }
        r1.write(r0, r2, r4);	 Catch:{ SecurityException -> 0x0052 }
        r0 = r6.pktsOnDisk;	 Catch:{ SecurityException -> 0x0052 }
        r0 = r0 + r3;	 Catch:{ SecurityException -> 0x0052 }
        r6.pktsOnDisk = r0;	 Catch:{ SecurityException -> 0x0052 }
        goto L_0x0033;
    L_0x0052:
        securityViolation = r3;
        r0 = 0;
        r6.queueFile = r0;
        r6.diskQueue = r0;
    L_0x0059:
        r0 = r6.diskQueue;
        if (r0 == 0) goto L_0x006c;
    L_0x005d:
        r0 = r6.diskQueue;
        r1 = getPktLen(r7);
        r0.write(r7, r2, r1);
        r7 = r6.pktsOnDisk;
        r7 = r7 + r3;
        r6.pktsOnDisk = r7;
        goto L_0x0081;
    L_0x006c:
        r0 = r6.pktQueue;
        r0.addLast(r7);
        r0 = globalMemUsage;
        r7 = r7.length;
        r0 = r0 + r7;
        globalMemUsage = r0;
        r7 = globalMemUsage;
        r0 = peakMemUsage;
        if (r7 <= r0) goto L_0x0081;
    L_0x007d:
        r7 = globalMemUsage;
        peakMemUsage = r7;
    L_0x0081:
        r7 = r6.inputPkts;
        r7 = r7 + r3;
        r6.inputPkts = r7;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedSocket.enqueueInput(net.sourceforge.jtds.jdbc.SharedSocket$VirtualSocket, byte[]):void");
    }

    private byte[] dequeueInput(VirtualSocket virtualSocket) throws IOException {
        byte[] bArr = null;
        if (virtualSocket.pktsOnDisk > 0) {
            if (virtualSocket.diskQueue.getFilePointer() == virtualSocket.diskQueue.length()) {
                virtualSocket.diskQueue.seek(0);
            }
            virtualSocket.diskQueue.readFully(this.hdrBuf, 0, 8);
            int pktLen = getPktLen(this.hdrBuf);
            Object obj = new byte[pktLen];
            System.arraycopy(this.hdrBuf, 0, obj, 0, 8);
            virtualSocket.diskQueue.readFully(obj, 8, pktLen - 8);
            virtualSocket.pktsOnDisk--;
            if (virtualSocket.pktsOnDisk < 1) {
                try {
                    virtualSocket.diskQueue.close();
                    virtualSocket.queueFile.delete();
                } finally {
                    virtualSocket.queueFile = null;
                    virtualSocket.diskQueue = null;
                }
            }
            bArr = obj;
        } else if (virtualSocket.pktQueue.size() > 0) {
            bArr = (byte[]) virtualSocket.pktQueue.removeFirst();
            globalMemUsage -= bArr.length;
        }
        if (bArr != null) {
            virtualSocket.inputPkts--;
        }
        return bArr;
    }

    private byte[] readPacket(byte[] r10) throws java.io.IOException {
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
        r9 = this;
        r0 = r9.getIn();	 Catch:{ EOFException -> 0x00f9 }
        r1 = r9.hdrBuf;	 Catch:{ EOFException -> 0x00f9 }
        r0.readFully(r1);	 Catch:{ EOFException -> 0x00f9 }
        r0 = r9.hdrBuf;
        r1 = 0;
        r0 = r0[r1];
        r2 = 2;
        r3 = 1;
        if (r0 == r2) goto L_0x0038;
    L_0x0012:
        if (r0 == r3) goto L_0x0038;
    L_0x0014:
        r2 = 15;
        if (r0 == r2) goto L_0x0038;
    L_0x0018:
        r2 = 4;
        if (r0 == r2) goto L_0x0038;
    L_0x001b:
        r10 = new java.io.IOException;
        r1 = new java.lang.StringBuffer;
        r1.<init>();
        r2 = "Unknown packet type 0x";
        r1.append(r2);
        r0 = r0 & 255;
        r0 = java.lang.Integer.toHexString(r0);
        r1.append(r0);
        r0 = r1.toString();
        r10.<init>(r0);
        throw r10;
    L_0x0038:
        r0 = r9.hdrBuf;
        r0 = getPktLen(r0);
        r2 = 8;
        if (r0 < r2) goto L_0x00e2;
    L_0x0042:
        r4 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        if (r0 <= r4) goto L_0x0048;
    L_0x0046:
        goto L_0x00e2;
    L_0x0048:
        if (r10 == 0) goto L_0x004d;
    L_0x004a:
        r4 = r10.length;
        if (r0 <= r4) goto L_0x0055;
    L_0x004d:
        r10 = new byte[r0];
        r4 = r9.maxBufSize;
        if (r0 <= r4) goto L_0x0055;
    L_0x0053:
        r9.maxBufSize = r0;
    L_0x0055:
        r4 = r9.hdrBuf;
        java.lang.System.arraycopy(r4, r1, r10, r1, r2);
        r4 = r9.getIn();	 Catch:{ EOFException -> 0x00da }
        r5 = r0 + -8;	 Catch:{ EOFException -> 0x00da }
        r4.readFully(r10, r2, r5);	 Catch:{ EOFException -> 0x00da }
        r2 = r9.packetCount;
        r2 = r2 + r3;
        r9.packetCount = r2;
        if (r2 != r3) goto L_0x0080;
    L_0x006a:
        r2 = r9.serverType;
        if (r2 != r3) goto L_0x0080;
    L_0x006e:
        r2 = "NTLMSSP";
        r4 = new java.lang.String;
        r6 = 11;
        r7 = 7;
        r4.<init>(r10, r6, r7);
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x0080;
    L_0x007e:
        r10[r3] = r3;
    L_0x0080:
        r2 = r9.cancelMonitor;
        monitor-enter(r2);
        r4 = r9.cancelPending;	 Catch:{ all -> 0x00d7 }
        if (r4 == 0) goto L_0x00ce;	 Catch:{ all -> 0x00d7 }
    L_0x0087:
        r4 = 9;	 Catch:{ all -> 0x00d7 }
        r5 = java.lang.Math.min(r4, r5);	 Catch:{ all -> 0x00d7 }
        r6 = 9 - r5;	 Catch:{ all -> 0x00d7 }
        r7 = r9.doneBuffer;	 Catch:{ all -> 0x00d7 }
        r8 = r9.doneBuffer;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r7, r5, r8, r1, r6);	 Catch:{ all -> 0x00d7 }
        r0 = r0 - r5;	 Catch:{ all -> 0x00d7 }
        r7 = r9.doneBuffer;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r10, r0, r7, r6, r5);	 Catch:{ all -> 0x00d7 }
        r0 = r9.doneBufferFrag;	 Catch:{ all -> 0x00d7 }
        r0 = r0 + r5;	 Catch:{ all -> 0x00d7 }
        r0 = java.lang.Math.min(r4, r0);	 Catch:{ all -> 0x00d7 }
        r9.doneBufferFrag = r0;	 Catch:{ all -> 0x00d7 }
        r0 = r9.doneBufferFrag;	 Catch:{ all -> 0x00d7 }
        if (r0 >= r4) goto L_0x00ab;	 Catch:{ all -> 0x00d7 }
    L_0x00a9:
        r10[r3] = r1;	 Catch:{ all -> 0x00d7 }
    L_0x00ab:
        r0 = r10[r3];	 Catch:{ all -> 0x00d7 }
        if (r0 != r3) goto L_0x00ce;	 Catch:{ all -> 0x00d7 }
    L_0x00af:
        r0 = r9.doneBuffer;	 Catch:{ all -> 0x00d7 }
        r0 = r0[r1];	 Catch:{ all -> 0x00d7 }
        r0 = r0 & 255;	 Catch:{ all -> 0x00d7 }
        r4 = 253; // 0xfd float:3.55E-43 double:1.25E-321;	 Catch:{ all -> 0x00d7 }
        if (r0 >= r4) goto L_0x00c1;	 Catch:{ all -> 0x00d7 }
    L_0x00b9:
        r10 = new java.io.IOException;	 Catch:{ all -> 0x00d7 }
        r0 = "Expecting a TDS_DONE or TDS_DONEPROC.";	 Catch:{ all -> 0x00d7 }
        r10.<init>(r0);	 Catch:{ all -> 0x00d7 }
        throw r10;	 Catch:{ all -> 0x00d7 }
    L_0x00c1:
        r0 = r9.doneBuffer;	 Catch:{ all -> 0x00d7 }
        r0 = r0[r3];	 Catch:{ all -> 0x00d7 }
        r0 = r0 & 32;	 Catch:{ all -> 0x00d7 }
        if (r0 == 0) goto L_0x00cc;	 Catch:{ all -> 0x00d7 }
    L_0x00c9:
        r9.cancelPending = r1;	 Catch:{ all -> 0x00d7 }
        goto L_0x00ce;	 Catch:{ all -> 0x00d7 }
    L_0x00cc:
        r10[r3] = r1;	 Catch:{ all -> 0x00d7 }
    L_0x00ce:
        r0 = r10[r3];	 Catch:{ all -> 0x00d7 }
        if (r0 == 0) goto L_0x00d5;	 Catch:{ all -> 0x00d7 }
    L_0x00d2:
        r0 = 0;	 Catch:{ all -> 0x00d7 }
        r9.responseOwner = r0;	 Catch:{ all -> 0x00d7 }
    L_0x00d5:
        monitor-exit(r2);	 Catch:{ all -> 0x00d7 }
        return r10;	 Catch:{ all -> 0x00d7 }
    L_0x00d7:
        r10 = move-exception;	 Catch:{ all -> 0x00d7 }
        monitor-exit(r2);	 Catch:{ all -> 0x00d7 }
        throw r10;
    L_0x00da:
        r10 = new java.io.IOException;
        r0 = "DB server closed connection.";
        r10.<init>(r0);
        throw r10;
    L_0x00e2:
        r10 = new java.io.IOException;
        r1 = new java.lang.StringBuffer;
        r1.<init>();
        r2 = "Invalid network packet length ";
        r1.append(r2);
        r1.append(r0);
        r0 = r1.toString();
        r10.<init>(r0);
        throw r10;
    L_0x00f9:
        r10 = new java.io.IOException;
        r0 = "DB server closed connection.";
        r10.<init>(r0);
        throw r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedSocket.readPacket(byte[]):byte[]");
    }

    static int getPktLen(byte[] bArr) {
        return ((bArr[2] & 255) << 8) | (bArr[3] & 255);
    }

    protected void setTimeout(int i) throws SocketException {
        this.socket.setSoTimeout(i);
    }

    protected void setKeepAlive(boolean z) throws SocketException {
        this.socket.setKeepAlive(z);
    }

    protected DataInputStream getIn() {
        return this.in;
    }

    protected void setIn(DataInputStream dataInputStream) {
        this.in = dataInputStream;
    }

    protected DataOutputStream getOut() {
        return this.out;
    }

    protected void setOut(DataOutputStream dataOutputStream) {
        this.out = dataOutputStream;
    }

    protected String getHost() {
        return this.host;
    }

    protected int getPort() {
        return this.port;
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
