package net.sourceforge.jtds.ssl;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import net.sourceforge.jtds.util.Logger;

public class SocketFactoriesSUN {

    private static class TdsTlsSocketFactory extends SocketFactory {
        private static SSLSocketFactory factorySingleton;
        private final Socket socket;
        private final String ssl;

        public Socket createSocket(String str, int i, InetAddress inetAddress, int i2) throws IOException, UnknownHostException {
            return null;
        }

        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
            return null;
        }

        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) throws IOException {
            return null;
        }

        public TdsTlsSocketFactory(String str, Socket socket) {
            this.ssl = str;
            this.socket = socket;
        }

        public Socket createSocket(String str, int i) throws IOException, UnknownHostException {
            SSLSocket sSLSocket = (SSLSocket) getFactory().createSocket(new TdsTlsSocket(this.socket), str, i, true);
            sSLSocket.startHandshake();
            sSLSocket.getSession().invalidate();
            return sSLSocket;
        }

        private SSLSocketFactory getFactory() throws IOException {
            try {
                if (Ssl.SSL_AUTHENTICATE.equals(this.ssl)) {
                    return (SSLSocketFactory) SSLSocketFactory.getDefault();
                }
                return factory();
            } catch (Exception e) {
                Logger.logException(e);
                throw new IOException(e.getMessage());
            }
        }

        private static SSLSocketFactory factory() throws NoSuchAlgorithmException, KeyManagementException {
            if (factorySingleton == null) {
                SSLContext instance = SSLContext.getInstance("TLS");
                instance.init(null, trustManagers(), null);
                factorySingleton = instance.getSocketFactory();
            }
            return factorySingleton;
        }

        private static TrustManager[] trustManagers() {
            return new X509TrustManager[]{new X509TrustManager() {
                public boolean isClientTrusted(X509Certificate[] x509CertificateArr) {
                    return true;
                }

                public boolean isServerTrusted(X509Certificate[] x509CertificateArr) {
                    return true;
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
        }
    }

    public static SocketFactory getSocketFactory(String str, Socket socket) {
        return new TdsTlsSocketFactory(str, socket);
    }
}
