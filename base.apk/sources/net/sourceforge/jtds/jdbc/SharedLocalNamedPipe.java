package net.sourceforge.jtds.jdbc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SharedLocalNamedPipe extends SharedSocket {
    RandomAccessFile pipe;

    protected void setTimeout(int i) {
    }

    public SharedLocalNamedPipe(ConnectionJDBC2 connectionJDBC2) throws IOException {
        super(connectionJDBC2.getBufferDir(), connectionJDBC2.getTdsVersion(), connectionJDBC2.getServerType());
        String serverName = connectionJDBC2.getServerName();
        String instanceName = connectionJDBC2.getInstanceName();
        StringBuffer stringBuffer = new StringBuffer(64);
        stringBuffer.append("\\\\");
        if (serverName != null) {
            if (serverName.length() != 0) {
                stringBuffer.append(serverName);
                stringBuffer.append("\\pipe");
                if (!(instanceName == null || instanceName.length() == 0)) {
                    stringBuffer.append("\\MSSQL$");
                    stringBuffer.append(instanceName);
                }
                stringBuffer.append(DefaultProperties.getNamedPipePath(connectionJDBC2.getServerType()).replace('/', '\\'));
                this.pipe = new RandomAccessFile(stringBuffer.toString(), "rw");
                connectionJDBC2 = Support.calculateNamedPipeBufferSize(connectionJDBC2.getTdsVersion(), connectionJDBC2.getPacketSize());
                setOut(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.pipe.getFD()), connectionJDBC2)));
                setIn(new DataInputStream(new BufferedInputStream(new FileInputStream(this.pipe.getFD()), connectionJDBC2)));
            }
        }
        stringBuffer.append('.');
        stringBuffer.append("\\pipe");
        stringBuffer.append("\\MSSQL$");
        stringBuffer.append(instanceName);
        stringBuffer.append(DefaultProperties.getNamedPipePath(connectionJDBC2.getServerType()).replace('/', '\\'));
        this.pipe = new RandomAccessFile(stringBuffer.toString(), "rw");
        connectionJDBC2 = Support.calculateNamedPipeBufferSize(connectionJDBC2.getTdsVersion(), connectionJDBC2.getPacketSize());
        setOut(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.pipe.getFD()), connectionJDBC2)));
        setIn(new DataInputStream(new BufferedInputStream(new FileInputStream(this.pipe.getFD()), connectionJDBC2)));
    }

    boolean isConnected() {
        return this.pipe != null;
    }

    byte[] sendNetPacket(VirtualSocket virtualSocket, byte[] bArr) throws IOException {
        virtualSocket = super.sendNetPacket(virtualSocket, bArr);
        getOut().flush();
        return virtualSocket;
    }

    void close() throws IOException {
        try {
            super.close();
            getOut().close();
            setOut(null);
            getIn().close();
            setIn(null);
            if (this.pipe != null) {
                this.pipe.close();
            }
            this.pipe = null;
        } catch (Throwable th) {
            this.pipe = null;
        }
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = this;
        r0 = 0;
        r1 = r2.getOut();	 Catch:{ Exception -> 0x000e, all -> 0x0009 }
        r1.close();	 Catch:{ Exception -> 0x000e, all -> 0x0009 }
        goto L_0x000e;
    L_0x0009:
        r1 = move-exception;
        r2.setOut(r0);
        throw r1;
    L_0x000e:
        r2.setOut(r0);
        r1 = r2.getIn();	 Catch:{ Exception -> 0x001e, all -> 0x0019 }
        r1.close();	 Catch:{ Exception -> 0x001e, all -> 0x0019 }
        goto L_0x001e;
    L_0x0019:
        r1 = move-exception;
        r2.setIn(r0);
        throw r1;
    L_0x001e:
        r2.setIn(r0);
        r1 = r2.pipe;	 Catch:{ IOException -> 0x002f, all -> 0x002b }
        if (r1 == 0) goto L_0x002f;	 Catch:{ IOException -> 0x002f, all -> 0x002b }
    L_0x0025:
        r1 = r2.pipe;	 Catch:{ IOException -> 0x002f, all -> 0x002b }
        r1.close();	 Catch:{ IOException -> 0x002f, all -> 0x002b }
        goto L_0x002f;
    L_0x002b:
        r1 = move-exception;
        r2.pipe = r0;
        throw r1;
    L_0x002f:
        r2.pipe = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedLocalNamedPipe.forceClose():void");
    }
}
