package net.sourceforge.jtds.jdbcx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import net.sourceforge.jtds.jdbc.DefaultProperties;
import net.sourceforge.jtds.jdbc.Driver;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.util.Logger;

public class JtdsDataSource implements DataSource, ConnectionPoolDataSource, XADataSource, Referenceable, Serializable {
    static final String DESCRIPTION = "description";
    private static final Driver _Driver = new Driver();
    static /* synthetic */ Class class$net$sourceforge$jtds$jdbcx$JtdsObjectFactory = null;
    static final long serialVersionUID = 266241;
    private final HashMap _Config;

    JtdsDataSource(HashMap hashMap) {
        this._Config = hashMap;
    }

    public JtdsDataSource() {
        this._Config = new HashMap();
    }

    public XAConnection getXAConnection() throws SQLException {
        return new JtdsXAConnection(this, getConnection((String) this._Config.get(Driver.USER), (String) this._Config.get(Driver.PASSWORD)));
    }

    public XAConnection getXAConnection(String str, String str2) throws SQLException {
        return new JtdsXAConnection(this, getConnection(str, str2));
    }

    public Connection getConnection() throws SQLException {
        return getConnection((String) this._Config.get(Driver.USER), (String) this._Config.get(Driver.PASSWORD));
    }

    public Connection getConnection(String str, String str2) throws SQLException {
        String str3 = (String) this._Config.get(Driver.SERVERNAME);
        String str4 = (String) this._Config.get(Driver.SERVERTYPE);
        String str5 = (String) this._Config.get(Driver.LOGFILE);
        if (str3 != null) {
            if (str3.length() != 0) {
                if (getLogWriter() == null && str5 != null && str5.length() > 0) {
                    try {
                        setLogWriter(new PrintWriter(new FileOutputStream(str5), true));
                    } catch (IOException e) {
                        PrintStream printStream = System.err;
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("jTDS: Failed to set log file ");
                        stringBuffer.append(e);
                        printStream.println(stringBuffer.toString());
                    }
                }
                Properties properties = new Properties();
                addNonNullProperties(properties, str, str2);
                if (str4 == null) {
                    str = null;
                } else {
                    try {
                        str = Integer.parseInt(str4);
                    } catch (Throwable e2) {
                        SQLException sQLException = new SQLException(Messages.get("error.connection.servertype", e2.toString()), "08001");
                        Support.linkException(sQLException, e2);
                        throw sQLException;
                    }
                }
                str2 = new StringBuffer();
                str2.append("jdbc:jtds:");
                str2.append(DefaultProperties.getServerTypeWithDefault(str));
                str2.append(':');
                return _Driver.connect(str2.toString(), properties);
            }
        }
        throw new SQLException(Messages.get("error.connection.nohost"), "08001");
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (String str2) {
            throw new NoClassDefFoundError(str2.getMessage());
        }
    }

    public Reference getReference() {
        Class class$;
        String name = getClass().getName();
        if (class$net$sourceforge$jtds$jdbcx$JtdsObjectFactory == null) {
            class$ = class$("net.sourceforge.jtds.jdbcx.JtdsObjectFactory");
            class$net$sourceforge$jtds$jdbcx$JtdsObjectFactory = class$;
        } else {
            class$ = class$net$sourceforge$jtds$jdbcx$JtdsObjectFactory;
        }
        Reference reference = new Reference(name, class$.getName(), null);
        for (Entry entry : this._Config.entrySet()) {
            reference.add(new StringRefAddr((String) entry.getKey(), (String) entry.getValue()));
        }
        return reference;
    }

    public PooledConnection getPooledConnection() throws SQLException {
        return getPooledConnection((String) this._Config.get(Driver.USER), (String) this._Config.get(Driver.PASSWORD));
    }

    public synchronized PooledConnection getPooledConnection(String str, String str2) throws SQLException {
        return new PooledConnection(getConnection(str, str2));
    }

    public void setLogWriter(PrintWriter printWriter) {
        Logger.setLogWriter(printWriter);
    }

    public PrintWriter getLogWriter() {
        return Logger.getLogWriter();
    }

    public void setLoginTimeout(int i) {
        this._Config.put(Driver.LOGINTIMEOUT, String.valueOf(i));
    }

    public int getLoginTimeout() {
        return getIntProperty(Driver.LOGINTIMEOUT);
    }

    public void setSocketTimeout(int i) {
        this._Config.put(Driver.SOTIMEOUT, String.valueOf(i));
    }

    public int getSocketTimeout() {
        return getIntProperty(Driver.SOTIMEOUT);
    }

    public void setSocketKeepAlive(boolean z) {
        this._Config.put(Driver.SOKEEPALIVE, String.valueOf(z));
    }

    public boolean getSocketKeepAlive() {
        return Boolean.valueOf((String) this._Config.get(Driver.SOKEEPALIVE)).booleanValue();
    }

    public void setProcessId(String str) {
        this._Config.put(Driver.PROCESSID, str);
    }

    public String getProcessId() {
        return (String) this._Config.get(Driver.PROCESSID);
    }

    public void setDatabaseName(String str) {
        this._Config.put(Driver.DATABASENAME, str);
    }

    public String getDatabaseName() {
        return (String) this._Config.get(Driver.DATABASENAME);
    }

    public void setDescription(String str) {
        this._Config.put(DESCRIPTION, str);
    }

    public String getDescription() {
        return (String) this._Config.get(DESCRIPTION);
    }

    public void setPassword(String str) {
        this._Config.put(Driver.PASSWORD, str);
    }

    public String getPassword() {
        return (String) this._Config.get(Driver.PASSWORD);
    }

    public void setPortNumber(int i) {
        this._Config.put(Driver.PORTNUMBER, String.valueOf(i));
    }

    public int getPortNumber() {
        return getIntProperty(Driver.PORTNUMBER);
    }

    public void setServerName(String str) {
        this._Config.put(Driver.SERVERNAME, str);
    }

    public String getServerName() {
        return (String) this._Config.get(Driver.SERVERNAME);
    }

    public void setAutoCommit(boolean z) {
        this._Config.put(Driver.AUTOCOMMIT, String.valueOf(z));
    }

    public boolean getAutoCommit() {
        return Boolean.valueOf((String) this._Config.get(Driver.AUTOCOMMIT)).booleanValue();
    }

    public void setUser(String str) {
        this._Config.put(Driver.USER, str);
    }

    public String getUser() {
        return (String) this._Config.get(Driver.USER);
    }

    public void setTds(String str) {
        this._Config.put(Driver.TDS, str);
    }

    public String getTds() {
        return (String) this._Config.get(Driver.TDS);
    }

    public void setServerType(int i) {
        this._Config.put(Driver.SERVERTYPE, String.valueOf(i));
    }

    public int getServerType() {
        return getIntProperty(Driver.SERVERTYPE);
    }

    public void setDomain(String str) {
        this._Config.put(Driver.DOMAIN, str);
    }

    public String getDomain() {
        return (String) this._Config.get(Driver.DOMAIN);
    }

    public void setUseNTLMV2(boolean z) {
        this._Config.put(Driver.USENTLMV2, String.valueOf(z));
    }

    public boolean getUseNTLMV2() {
        return Boolean.valueOf((String) this._Config.get(Driver.USENTLMV2)).booleanValue();
    }

    public void setInstance(String str) {
        this._Config.put(Driver.INSTANCE, str);
    }

    public String getInstance() {
        return (String) this._Config.get(Driver.INSTANCE);
    }

    public void setSendStringParametersAsUnicode(boolean z) {
        this._Config.put(Driver.SENDSTRINGPARAMETERSASUNICODE, String.valueOf(z));
    }

    public boolean getSendStringParametersAsUnicode() {
        return Boolean.valueOf((String) this._Config.get(Driver.SENDSTRINGPARAMETERSASUNICODE)).booleanValue();
    }

    public void setNamedPipe(boolean z) {
        this._Config.put(Driver.NAMEDPIPE, String.valueOf(z));
    }

    public boolean getNamedPipe() {
        return Boolean.valueOf((String) this._Config.get(Driver.NAMEDPIPE)).booleanValue();
    }

    public void setLastUpdateCount(boolean z) {
        this._Config.put(Driver.LASTUPDATECOUNT, String.valueOf(z));
    }

    public boolean getLastUpdateCount() {
        return Boolean.valueOf((String) this._Config.get(Driver.LASTUPDATECOUNT)).booleanValue();
    }

    public void setXaEmulation(boolean z) {
        this._Config.put(Driver.XAEMULATION, String.valueOf(z));
    }

    public boolean getXaEmulation() {
        return Boolean.valueOf((String) this._Config.get(Driver.XAEMULATION)).booleanValue();
    }

    public void setCharset(String str) {
        this._Config.put(Driver.CHARSET, str);
    }

    public String getCharset() {
        return (String) this._Config.get(Driver.CHARSET);
    }

    public void setLanguage(String str) {
        this._Config.put(Driver.LANGUAGE, str);
    }

    public String getLanguage() {
        return (String) this._Config.get(Driver.LANGUAGE);
    }

    public void setMacAddress(String str) {
        this._Config.put(Driver.MACADDRESS, str);
    }

    public String getMacAddress() {
        return (String) this._Config.get(Driver.MACADDRESS);
    }

    public void setPacketSize(int i) {
        this._Config.put(Driver.PACKETSIZE, String.valueOf(i));
    }

    public int getPacketSize() {
        return getIntProperty(Driver.PACKETSIZE);
    }

    public void setTcpNoDelay(boolean z) {
        this._Config.put(Driver.TCPNODELAY, String.valueOf(z));
    }

    public boolean getTcpNoDelay() {
        return Boolean.valueOf((String) this._Config.get(Driver.TCPNODELAY)).booleanValue();
    }

    public void setPrepareSql(int i) {
        this._Config.put(Driver.PREPARESQL, String.valueOf(i));
    }

    public int getPrepareSql() {
        return getIntProperty(Driver.PREPARESQL);
    }

    public void setLobBuffer(long j) {
        this._Config.put(Driver.LOBBUFFER, String.valueOf(j));
    }

    public long getLobBuffer() {
        return getLongProperty(Driver.LOBBUFFER);
    }

    public void setMaxStatements(int i) {
        this._Config.put(Driver.MAXSTATEMENTS, String.valueOf(i));
    }

    public int getMaxStatements() {
        return getIntProperty(Driver.MAXSTATEMENTS);
    }

    public void setAppName(String str) {
        this._Config.put(Driver.APPNAME, str);
    }

    public String getAppName() {
        return (String) this._Config.get(Driver.APPNAME);
    }

    public void setProgName(String str) {
        this._Config.put(Driver.PROGNAME, str);
    }

    public String getProgName() {
        return (String) this._Config.get(Driver.PROGNAME);
    }

    public void setWsid(String str) {
        this._Config.put(Driver.WSID, str);
    }

    public String getWsid() {
        return (String) this._Config.get(Driver.WSID);
    }

    public void setLogFile(String str) {
        this._Config.put(Driver.LOGFILE, str);
    }

    public String getLogFile() {
        return (String) this._Config.get(Driver.LOGFILE);
    }

    public void setSsl(String str) {
        this._Config.put(Driver.SSL, str);
    }

    public String getSsl() {
        return (String) this._Config.get(Driver.SSL);
    }

    public void setBatchSize(int i) {
        this._Config.put(Driver.BATCHSIZE, String.valueOf(i));
    }

    public int getBatchSize() {
        return getIntProperty(Driver.BATCHSIZE);
    }

    public void setBufferDir(String str) {
        this._Config.put(Driver.BUFFERDIR, str);
    }

    public String getBufferDir() {
        return (String) this._Config.get(Driver.BUFFERDIR);
    }

    public int getBufferMaxMemory() {
        return getIntProperty(Driver.BUFFERMAXMEMORY);
    }

    public void setBufferMaxMemory(int i) {
        this._Config.put(Driver.BUFFERMAXMEMORY, String.valueOf(i));
    }

    public void setBufferMinPackets(int i) {
        this._Config.put(Driver.BUFFERMINPACKETS, String.valueOf(i));
    }

    public int getBufferMinPackets() {
        return getIntProperty(Driver.BUFFERMINPACKETS);
    }

    public void setCacheMetaData(boolean z) {
        this._Config.put(Driver.CACHEMETA, String.valueOf(z));
    }

    public boolean getCacheMetaData() {
        return Boolean.valueOf((String) this._Config.get(Driver.CACHEMETA)).booleanValue();
    }

    public void setUseCursors(boolean z) {
        this._Config.put(Driver.USECURSORS, String.valueOf(z));
    }

    public boolean getUseCursors() {
        return Boolean.valueOf((String) this._Config.get(Driver.USECURSORS)).booleanValue();
    }

    public void setUseLOBs(boolean z) {
        this._Config.put(Driver.USELOBS, String.valueOf(z));
    }

    public boolean getUseLOBs() {
        return Boolean.valueOf((String) this._Config.get(Driver.USELOBS)).booleanValue();
    }

    public void setBindAddress(String str) {
        this._Config.put(Driver.BINDADDRESS, str);
    }

    public String getBindAddress() {
        return (String) this._Config.get(Driver.BINDADDRESS);
    }

    public void setUseJCIFS(boolean z) {
        this._Config.put(Driver.USEJCIFS, String.valueOf(z));
    }

    public boolean getUseJCIFS() {
        return Boolean.valueOf((String) this._Config.get(Driver.USEJCIFS)).booleanValue();
    }

    private void addNonNullProperties(Properties properties, String str, String str2) {
        for (Entry entry : this._Config.entrySet()) {
            String str3 = (String) entry.getKey();
            String str4 = (String) entry.getValue();
            if (!(str3.equals(DESCRIPTION) || str4 == null)) {
                properties.setProperty(Messages.get(str3), str4);
            }
        }
        if (str != null) {
            properties.setProperty(Messages.get(Driver.USER), str);
        }
        if (str2 != null) {
            properties.setProperty(Messages.get(Driver.PASSWORD), str2);
        }
    }

    private int getIntProperty(String str) {
        return new Long(getLongProperty(str)).intValue();
    }

    private long getLongProperty(String str) {
        str = (String) this._Config.get(str);
        if (str == null) {
            return 0;
        }
        return Long.parseLong(str);
    }
}
