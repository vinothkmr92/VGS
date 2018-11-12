package net.sourceforge.jtds.jdbc;

import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

public final class Messages {
    private static final String DEFAULT_RESOURCE = "net.sourceforge.jtds.jdbc.Messages";
    private static ResourceBundle defaultResource;

    private Messages() {
    }

    public static String get(String str) {
        return get(str, null);
    }

    public static String get(String str, Object obj) {
        return get(str, new Object[]{obj});
    }

    static String get(String str, Object obj, Object obj2) {
        return get(str, new Object[]{obj, obj2});
    }

    private static java.lang.String get(java.lang.String r2, java.lang.Object[] r3) {
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
        r0 = loadResourceBundle();	 Catch:{ MissingResourceException -> 0x0019 }
        r0 = r0.getString(r2);	 Catch:{ MissingResourceException -> 0x0019 }
        if (r3 == 0) goto L_0x0018;	 Catch:{ MissingResourceException -> 0x0019 }
    L_0x000a:
        r1 = r3.length;	 Catch:{ MissingResourceException -> 0x0019 }
        if (r1 != 0) goto L_0x000e;	 Catch:{ MissingResourceException -> 0x0019 }
    L_0x000d:
        goto L_0x0018;	 Catch:{ MissingResourceException -> 0x0019 }
    L_0x000e:
        r1 = new java.text.MessageFormat;	 Catch:{ MissingResourceException -> 0x0019 }
        r1.<init>(r0);	 Catch:{ MissingResourceException -> 0x0019 }
        r3 = r1.format(r3);	 Catch:{ MissingResourceException -> 0x0019 }
        return r3;
    L_0x0018:
        return r0;
    L_0x0019:
        r3 = new java.lang.RuntimeException;
        r0 = new java.lang.StringBuffer;
        r0.<init>();
        r1 = "No message resource found for message property ";
        r0.append(r1);
        r0.append(r2);
        r2 = r0.toString();
        r3.<init>(r2);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.Messages.get(java.lang.String, java.lang.Object[]):java.lang.String");
    }

    static void loadDriverProperties(Map map, Map map2) {
        ResourceBundle loadResourceBundle = loadResourceBundle();
        Enumeration keys = loadResourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String str = (String) keys.nextElement();
            if (str.startsWith("prop.desc.")) {
                map2.put(str.substring("prop.desc.".length()), loadResourceBundle.getString(str));
            } else if (str.startsWith("prop.")) {
                map.put(str.substring("prop.".length()), loadResourceBundle.getString(str));
            }
        }
    }

    private static ResourceBundle loadResourceBundle() {
        if (defaultResource == null) {
            defaultResource = ResourceBundle.getBundle(DEFAULT_RESOURCE);
        }
        return defaultResource;
    }
}
