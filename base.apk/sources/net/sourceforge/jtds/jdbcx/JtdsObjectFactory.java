package net.sourceforge.jtds.jdbcx;

import java.util.HashMap;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import net.sourceforge.jtds.jdbc.Driver;

public class JtdsObjectFactory implements ObjectFactory {
    static /* synthetic */ Class class$net$sourceforge$jtds$jdbcx$JtdsDataSource;

    public Object getObjectInstance(Object obj, Name name, Context context, Hashtable hashtable) throws Exception {
        Reference reference = (Reference) obj;
        name = reference.getClassName();
        if (class$net$sourceforge$jtds$jdbcx$JtdsDataSource == null) {
            context = class$("net.sourceforge.jtds.jdbcx.JtdsDataSource");
            class$net$sourceforge$jtds$jdbcx$JtdsDataSource = context;
        } else {
            context = class$net$sourceforge$jtds$jdbcx$JtdsDataSource;
        }
        if (name.equals(context.getName()) == null) {
            return null;
        }
        return new JtdsDataSource(loadProps(reference, new String[]{"description", Driver.APPNAME, Driver.AUTOCOMMIT, Driver.BATCHSIZE, Driver.BINDADDRESS, Driver.BUFFERDIR, Driver.BUFFERMAXMEMORY, Driver.BUFFERMINPACKETS, Driver.CACHEMETA, Driver.CHARSET, Driver.DATABASENAME, Driver.DOMAIN, Driver.INSTANCE, Driver.LANGUAGE, Driver.LASTUPDATECOUNT, Driver.LOBBUFFER, Driver.LOGFILE, Driver.LOGINTIMEOUT, Driver.MACADDRESS, Driver.MAXSTATEMENTS, Driver.NAMEDPIPE, Driver.PACKETSIZE, Driver.PASSWORD, Driver.PORTNUMBER, Driver.PREPARESQL, Driver.PROGNAME, Driver.SERVERNAME, Driver.SERVERTYPE, Driver.SOTIMEOUT, Driver.SOKEEPALIVE, Driver.PROCESSID, Driver.SSL, Driver.TCPNODELAY, Driver.TDS, Driver.USECURSORS, Driver.USEJCIFS, Driver.USENTLMV2, Driver.USELOBS, Driver.USER, Driver.SENDSTRINGPARAMETERSASUNICODE, Driver.WSID, Driver.XAEMULATION}));
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (String str2) {
            throw new NoClassDefFoundError(str2.getMessage());
        }
    }

    private HashMap loadProps(Reference reference, String[] strArr) {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        reference = reference.getAll();
        while (reference.hasMoreElements()) {
            RefAddr refAddr = (RefAddr) reference.nextElement();
            hashMap2.put(refAddr.getType().toLowerCase(), refAddr.getContent());
        }
        for (reference = null; reference < strArr.length; reference++) {
            String str = (String) hashMap2.get(strArr[reference].toLowerCase());
            if (str != null) {
                hashMap.put(strArr[reference], str);
            }
        }
        return hashMap;
    }
}
