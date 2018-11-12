package net.sourceforge.jtds.jdbc;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import jcifs.Config;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbNamedPipe;

public class SharedNamedPipe extends SharedSocket {
    private SmbNamedPipe pipe;

    protected void setTimeout(int i) {
    }

    public SharedNamedPipe(ConnectionJDBC2 connectionJDBC2) throws IOException {
        super(connectionJDBC2.getBufferDir(), connectionJDBC2.getTdsVersion(), connectionJDBC2.getServerType());
        int socketTimeout = connectionJDBC2.getSocketTimeout() * 1000;
        if (socketTimeout <= 0) {
            socketTimeout = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
        String valueOf = String.valueOf(socketTimeout);
        Config.setProperty("jcifs.smb.client.responseTimeout", valueOf);
        Config.setProperty("jcifs.smb.client.soTimeout", valueOf);
        NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(connectionJDBC2.getDomainName(), connectionJDBC2.getUser(), connectionJDBC2.getPassword());
        StringBuffer stringBuffer = new StringBuffer(32);
        stringBuffer.append("smb://");
        stringBuffer.append(connectionJDBC2.getServerName());
        stringBuffer.append("/IPC$");
        String instanceName = connectionJDBC2.getInstanceName();
        if (!(instanceName == null || instanceName.length() == 0)) {
            stringBuffer.append("/MSSQL$");
            stringBuffer.append(instanceName);
        }
        stringBuffer.append(DefaultProperties.getNamedPipePath(connectionJDBC2.getServerType()));
        setPipe(new SmbNamedPipe(stringBuffer.toString(), 3, ntlmPasswordAuthentication));
        setOut(new DataOutputStream(getPipe().getNamedPipeOutputStream()));
        setIn(new DataInputStream(new BufferedInputStream(getPipe().getNamedPipeInputStream(), Support.calculateNamedPipeBufferSize(connectionJDBC2.getTdsVersion(), connectionJDBC2.getPacketSize()))));
    }

    boolean isConnected() {
        return getPipe() != null;
    }

    void close() throws IOException {
        super.close();
        getOut().close();
        getIn().close();
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
        r1 = r2.getOut();	 Catch:{ IOException -> 0x000e, all -> 0x0009 }
        r1.close();	 Catch:{ IOException -> 0x000e, all -> 0x0009 }
        goto L_0x000e;
    L_0x0009:
        r1 = move-exception;
        r2.setOut(r0);
        throw r1;
    L_0x000e:
        r2.setOut(r0);
        r1 = r2.getIn();	 Catch:{ IOException -> 0x001e, all -> 0x0019 }
        r1.close();	 Catch:{ IOException -> 0x001e, all -> 0x0019 }
        goto L_0x001e;
    L_0x0019:
        r1 = move-exception;
        r2.setIn(r0);
        throw r1;
    L_0x001e:
        r2.setIn(r0);
        r2.setPipe(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.SharedNamedPipe.forceClose():void");
    }

    private SmbNamedPipe getPipe() {
        return this.pipe;
    }

    private void setPipe(SmbNamedPipe smbNamedPipe) {
        this.pipe = smbNamedPipe;
    }
}
