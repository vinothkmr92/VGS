package net.sourceforge.jtds.jdbc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class DefaultProperties {
    public static final String APP_NAME = "jTDS";
    public static final String AUTO_COMMIT = "true";
    public static final String BATCH_SIZE_SQLSERVER = "0";
    public static final String BATCH_SIZE_SYBASE = "1000";
    public static final String BIND_ADDRESS = "";
    public static final String BUFFER_DIR = new File(System.getProperty("java.io.tmpdir")).toString();
    public static final String BUFFER_MAX_MEMORY = "1024";
    public static final String BUFFER_MIN_PACKETS = "8";
    public static final String CACHEMETA = "false";
    public static final String CHARSET = "";
    public static final String DATABASE_NAME = "";
    public static final String DOMAIN = "";
    public static final String INSTANCE = "";
    public static final String LANGUAGE = "";
    public static final String LAST_UPDATE_COUNT = "true";
    public static final String LOB_BUFFER_SIZE = "32768";
    public static final String LOGFILE = "";
    public static final String LOGIN_TIMEOUT = "0";
    public static final String MAC_ADDRESS = "000000000000";
    public static final String MAX_STATEMENTS = "500";
    public static final String NAMED_PIPE = "false";
    public static final String NAMED_PIPE_PATH_SQLSERVER = "/sql/query";
    public static final String NAMED_PIPE_PATH_SYBASE = "/sybase/query";
    public static final String PACKET_SIZE_42 = String.valueOf(512);
    public static final String PACKET_SIZE_50 = "0";
    public static final String PACKET_SIZE_70_80 = "0";
    public static final String PASSWORD = "";
    public static final String PORT_NUMBER_SQLSERVER = "1433";
    public static final String PORT_NUMBER_SYBASE = "7100";
    public static final String PREPARE_SQLSERVER = String.valueOf(3);
    public static final String PREPARE_SYBASE = String.valueOf(1);
    public static final String PROCESS_ID = "123";
    public static final String PROG_NAME = "jTDS";
    public static final String SERVER_TYPE_SQLSERVER = "sqlserver";
    public static final String SERVER_TYPE_SYBASE = "sybase";
    public static final String SOCKET_KEEPALIVE = "false";
    public static final String SOCKET_TIMEOUT = "0";
    public static final String SSL = "off";
    public static final String TCP_NODELAY = "true";
    public static final String TDS_VERSION_42 = "4.2";
    public static final String TDS_VERSION_50 = "5.0";
    public static final String TDS_VERSION_70 = "7.0";
    public static final String TDS_VERSION_80 = "8.0";
    public static final String USECURSORS = "false";
    public static final String USEJCIFS = "false";
    public static final String USELOBS = "true";
    public static final String USENTLMV2 = "false";
    public static final String USER = "";
    public static final String USE_UNICODE = "true";
    public static final String WSID = "";
    public static final String XAEMULATION = "true";
    private static final HashMap batchSizeDefaults = new HashMap(2);
    private static final HashMap packetSizeDefaults = new HashMap(4);
    private static final HashMap portNumberDefaults = new HashMap(2);
    private static final HashMap prepareSQLDefaults = new HashMap(2);
    private static final HashMap tdsDefaults = new HashMap(2);

    public static String getServerType(int i) {
        return i == 1 ? SERVER_TYPE_SQLSERVER : i == 2 ? SERVER_TYPE_SYBASE : null;
    }

    static {
        tdsDefaults.put(String.valueOf(1), TDS_VERSION_80);
        tdsDefaults.put(String.valueOf(2), TDS_VERSION_50);
        portNumberDefaults.put(String.valueOf(1), PORT_NUMBER_SQLSERVER);
        portNumberDefaults.put(String.valueOf(2), PORT_NUMBER_SYBASE);
        packetSizeDefaults.put(TDS_VERSION_42, PACKET_SIZE_42);
        packetSizeDefaults.put(TDS_VERSION_50, "0");
        packetSizeDefaults.put(TDS_VERSION_70, "0");
        packetSizeDefaults.put(TDS_VERSION_80, "0");
        batchSizeDefaults.put(String.valueOf(1), "0");
        batchSizeDefaults.put(String.valueOf(2), BATCH_SIZE_SYBASE);
        prepareSQLDefaults.put(String.valueOf(1), PREPARE_SQLSERVER);
        prepareSQLDefaults.put(String.valueOf(2), PREPARE_SYBASE);
    }

    public static Properties addDefaultProperties(Properties properties) {
        if (properties.getProperty(Messages.get(Driver.SERVERTYPE)) == null) {
            return null;
        }
        addDefaultPropertyIfNotSet(properties, Driver.TDS, Driver.SERVERTYPE, tdsDefaults);
        addDefaultPropertyIfNotSet(properties, Driver.PORTNUMBER, Driver.SERVERTYPE, portNumberDefaults);
        addDefaultPropertyIfNotSet(properties, Driver.USER, "");
        addDefaultPropertyIfNotSet(properties, Driver.PASSWORD, "");
        addDefaultPropertyIfNotSet(properties, Driver.DATABASENAME, "");
        addDefaultPropertyIfNotSet(properties, Driver.INSTANCE, "");
        addDefaultPropertyIfNotSet(properties, Driver.DOMAIN, "");
        addDefaultPropertyIfNotSet(properties, Driver.APPNAME, "jTDS");
        addDefaultPropertyIfNotSet(properties, Driver.AUTOCOMMIT, "true");
        addDefaultPropertyIfNotSet(properties, Driver.PROGNAME, "jTDS");
        addDefaultPropertyIfNotSet(properties, Driver.WSID, "");
        addDefaultPropertyIfNotSet(properties, Driver.BATCHSIZE, Driver.SERVERTYPE, batchSizeDefaults);
        addDefaultPropertyIfNotSet(properties, Driver.LASTUPDATECOUNT, "true");
        addDefaultPropertyIfNotSet(properties, Driver.LOBBUFFER, LOB_BUFFER_SIZE);
        addDefaultPropertyIfNotSet(properties, Driver.LOGINTIMEOUT, "0");
        addDefaultPropertyIfNotSet(properties, Driver.SOTIMEOUT, "0");
        addDefaultPropertyIfNotSet(properties, Driver.SOKEEPALIVE, "false");
        addDefaultPropertyIfNotSet(properties, Driver.PROCESSID, PROCESS_ID);
        addDefaultPropertyIfNotSet(properties, Driver.MACADDRESS, MAC_ADDRESS);
        addDefaultPropertyIfNotSet(properties, Driver.MAXSTATEMENTS, MAX_STATEMENTS);
        addDefaultPropertyIfNotSet(properties, Driver.NAMEDPIPE, "false");
        addDefaultPropertyIfNotSet(properties, Driver.PACKETSIZE, Driver.TDS, packetSizeDefaults);
        addDefaultPropertyIfNotSet(properties, Driver.CACHEMETA, "false");
        addDefaultPropertyIfNotSet(properties, Driver.CHARSET, "");
        addDefaultPropertyIfNotSet(properties, Driver.LANGUAGE, "");
        addDefaultPropertyIfNotSet(properties, Driver.PREPARESQL, Driver.SERVERTYPE, prepareSQLDefaults);
        addDefaultPropertyIfNotSet(properties, Driver.SENDSTRINGPARAMETERSASUNICODE, "true");
        addDefaultPropertyIfNotSet(properties, Driver.TCPNODELAY, "true");
        addDefaultPropertyIfNotSet(properties, Driver.XAEMULATION, "true");
        addDefaultPropertyIfNotSet(properties, Driver.LOGFILE, "");
        addDefaultPropertyIfNotSet(properties, Driver.SSL, "off");
        addDefaultPropertyIfNotSet(properties, Driver.USECURSORS, "false");
        addDefaultPropertyIfNotSet(properties, Driver.USENTLMV2, "false");
        addDefaultPropertyIfNotSet(properties, Driver.BUFFERMAXMEMORY, BUFFER_MAX_MEMORY);
        addDefaultPropertyIfNotSet(properties, Driver.BUFFERMINPACKETS, BUFFER_MIN_PACKETS);
        addDefaultPropertyIfNotSet(properties, Driver.USELOBS, "true");
        addDefaultPropertyIfNotSet(properties, Driver.BINDADDRESS, "");
        addDefaultPropertyIfNotSet(properties, Driver.USEJCIFS, "false");
        addDefaultPropertyIfNotSet(properties, Driver.BUFFERDIR, BUFFER_DIR);
        return properties;
    }

    private static void addDefaultPropertyIfNotSet(Properties properties, String str, String str2) {
        str = Messages.get(str);
        if (properties.getProperty(str) == null) {
            properties.setProperty(str, str2);
        }
    }

    private static void addDefaultPropertyIfNotSet(Properties properties, String str, String str2, Map map) {
        str2 = properties.getProperty(Messages.get(str2));
        if (str2 != null) {
            str = Messages.get(str);
            if (properties.getProperty(str) == null) {
                str2 = map.get(str2);
                if (str2 != null) {
                    properties.setProperty(str, String.valueOf(str2));
                }
            }
        }
    }

    public static String getNamedPipePath(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return NAMED_PIPE_PATH_SYBASE;
                }
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Unknown serverType: ");
                stringBuffer.append(i);
                throw new IllegalArgumentException(stringBuffer.toString());
            }
        }
        return NAMED_PIPE_PATH_SQLSERVER;
    }

    public static Integer getServerType(String str) {
        if (SERVER_TYPE_SQLSERVER.equals(str)) {
            return new Integer(1);
        }
        return SERVER_TYPE_SYBASE.equals(str) != null ? new Integer(2) : null;
    }

    public static String getServerTypeWithDefault(int i) {
        if (i == 0) {
            return SERVER_TYPE_SQLSERVER;
        }
        if (i != 1) {
            if (i != 2) {
                throw new IllegalArgumentException("Only 0, 1 and 2 accepted for serverType");
            }
        }
        return getServerType(i);
    }

    public static Integer getTdsVersion(String str) {
        if (TDS_VERSION_42.equals(str)) {
            return new Integer(1);
        }
        if (TDS_VERSION_50.equals(str)) {
            return new Integer(2);
        }
        if (TDS_VERSION_70.equals(str)) {
            return new Integer(3);
        }
        return TDS_VERSION_80.equals(str) != null ? new Integer(4) : null;
    }
}
