package net.sourceforge.jtds.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

class TdsTlsSocket extends Socket {
    private final Socket delegate;
    private final InputStream istm;
    private final OutputStream ostm;

    public boolean isConnected() {
        return true;
    }

    TdsTlsSocket(Socket socket) throws IOException {
        this.delegate = socket;
        this.istm = new TdsTlsInputStream(socket.getInputStream());
        this.ostm = new TdsTlsOutputStream(socket.getOutputStream());
    }

    public synchronized void close() throws IOException {
    }

    public InputStream getInputStream() throws IOException {
        return this.istm;
    }

    public OutputStream getOutputStream() throws IOException {
        return this.ostm;
    }

    public synchronized void setSoTimeout(int i) throws SocketException {
        this.delegate.setSoTimeout(i);
    }

    public synchronized void setKeepAlive(boolean z) throws SocketException {
        this.delegate.setKeepAlive(z);
    }

    public void setTcpNoDelay(boolean z) throws SocketException {
        this.delegate.setTcpNoDelay(z);
    }
}
