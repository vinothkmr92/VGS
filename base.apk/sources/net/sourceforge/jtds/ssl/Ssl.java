package net.sourceforge.jtds.ssl;

public interface Ssl {
    public static final int HS_HEADER_SIZE = 4;
    public static final String SSL_AUTHENTICATE = "authenticate";
    public static final String SSL_OFF = "off";
    public static final String SSL_REQUEST = "request";
    public static final String SSL_REQUIRE = "require";
    public static final int TLS_HEADER_SIZE = 5;
    public static final byte TYPE_ALERT = (byte) 21;
    public static final byte TYPE_APPLICATIONDATA = (byte) 23;
    public static final byte TYPE_CHANGECIPHERSPEC = (byte) 20;
    public static final int TYPE_CLIENTHELLO = 1;
    public static final int TYPE_CLIENTKEYEXCHANGE = 16;
    public static final byte TYPE_HANDSHAKE = (byte) 22;
}
