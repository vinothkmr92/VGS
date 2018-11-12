package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@RestrictTo({Scope.LIBRARY_GROUP})
public class TypefaceCompatUtil {
    private static final String CACHE_FILE_PREFIX = ".font";
    private static final String TAG = "TypefaceCompatUtil";

    private TypefaceCompatUtil() {
    }

    public static java.io.File getTempFile(android.content.Context r5) {
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
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = ".font";
        r0.append(r1);
        r1 = android.os.Process.myPid();
        r0.append(r1);
        r1 = "-";
        r0.append(r1);
        r1 = android.os.Process.myTid();
        r0.append(r1);
        r1 = "-";
        r0.append(r1);
        r0 = r0.toString();
        r1 = 0;
    L_0x0027:
        r2 = 100;
        if (r1 >= r2) goto L_0x004d;
    L_0x002b:
        r2 = new java.io.File;
        r3 = r5.getCacheDir();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r0);
        r4.append(r1);
        r4 = r4.toString();
        r2.<init>(r3, r4);
        r3 = r2.createNewFile();	 Catch:{ IOException -> 0x004a }
        if (r3 == 0) goto L_0x004a;
    L_0x0049:
        return r2;
    L_0x004a:
        r1 = r1 + 1;
        goto L_0x0027;
    L_0x004d:
        r5 = 0;
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.getTempFile(android.content.Context):java.io.File");
    }

    @android.support.annotation.RequiresApi(19)
    private static java.nio.ByteBuffer mmap(java.io.File r9) {
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
        r0 = 0;
        r1 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0036 }
        r1.<init>(r9);	 Catch:{ IOException -> 0x0036 }
        r2 = r1.getChannel();	 Catch:{ Throwable -> 0x001f, all -> 0x001c }
        r6 = r2.size();	 Catch:{ Throwable -> 0x001f, all -> 0x001c }
        r3 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x001f, all -> 0x001c }
        r4 = 0;	 Catch:{ Throwable -> 0x001f, all -> 0x001c }
        r9 = r2.map(r3, r4, r6);	 Catch:{ Throwable -> 0x001f, all -> 0x001c }
        if (r1 == 0) goto L_0x001b;
    L_0x0018:
        r1.close();	 Catch:{ IOException -> 0x0036 }
    L_0x001b:
        return r9;
    L_0x001c:
        r9 = move-exception;
        r2 = r0;
        goto L_0x0025;
    L_0x001f:
        r9 = move-exception;
        throw r9;	 Catch:{ all -> 0x0021 }
    L_0x0021:
        r2 = move-exception;
        r8 = r2;
        r2 = r9;
        r9 = r8;
    L_0x0025:
        if (r1 == 0) goto L_0x0035;
    L_0x0027:
        if (r2 == 0) goto L_0x0032;
    L_0x0029:
        r1.close();	 Catch:{ Throwable -> 0x002d }
        goto L_0x0035;
    L_0x002d:
        r1 = move-exception;
        r2.addSuppressed(r1);	 Catch:{ IOException -> 0x0036 }
        goto L_0x0035;	 Catch:{ IOException -> 0x0036 }
    L_0x0032:
        r1.close();	 Catch:{ IOException -> 0x0036 }
    L_0x0035:
        throw r9;	 Catch:{ IOException -> 0x0036 }
    L_0x0036:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(java.io.File):java.nio.ByteBuffer");
    }

    @android.support.annotation.RequiresApi(19)
    public static java.nio.ByteBuffer mmap(android.content.Context r8, android.os.CancellationSignal r9, android.net.Uri r10) {
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
        r8 = r8.getContentResolver();
        r0 = 0;
        r1 = "r";	 Catch:{ IOException -> 0x0063 }
        r8 = r8.openFileDescriptor(r10, r1, r9);	 Catch:{ IOException -> 0x0063 }
        r9 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
        r10 = r8.getFileDescriptor();	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
        r9.<init>(r10);	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
        r1 = r9.getChannel();	 Catch:{ Throwable -> 0x0032, all -> 0x002f }
        r5 = r1.size();	 Catch:{ Throwable -> 0x0032, all -> 0x002f }
        r2 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x0032, all -> 0x002f }
        r3 = 0;	 Catch:{ Throwable -> 0x0032, all -> 0x002f }
        r10 = r1.map(r2, r3, r5);	 Catch:{ Throwable -> 0x0032, all -> 0x002f }
        if (r9 == 0) goto L_0x0029;
    L_0x0026:
        r9.close();	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
    L_0x0029:
        if (r8 == 0) goto L_0x002e;
    L_0x002b:
        r8.close();	 Catch:{ IOException -> 0x0063 }
    L_0x002e:
        return r10;
    L_0x002f:
        r10 = move-exception;
        r1 = r0;
        goto L_0x0038;
    L_0x0032:
        r10 = move-exception;
        throw r10;	 Catch:{ all -> 0x0034 }
    L_0x0034:
        r1 = move-exception;
        r7 = r1;
        r1 = r10;
        r10 = r7;
    L_0x0038:
        if (r9 == 0) goto L_0x0048;
    L_0x003a:
        if (r1 == 0) goto L_0x0045;
    L_0x003c:
        r9.close();	 Catch:{ Throwable -> 0x0040, all -> 0x0049 }
        goto L_0x0048;
    L_0x0040:
        r9 = move-exception;
        r1.addSuppressed(r9);	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
        goto L_0x0048;	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
    L_0x0045:
        r9.close();	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
    L_0x0048:
        throw r10;	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
    L_0x0049:
        r9 = move-exception;
        r10 = r0;
        goto L_0x0052;
    L_0x004c:
        r9 = move-exception;
        throw r9;	 Catch:{ all -> 0x004e }
    L_0x004e:
        r10 = move-exception;
        r7 = r10;
        r10 = r9;
        r9 = r7;
    L_0x0052:
        if (r8 == 0) goto L_0x0062;
    L_0x0054:
        if (r10 == 0) goto L_0x005f;
    L_0x0056:
        r8.close();	 Catch:{ Throwable -> 0x005a }
        goto L_0x0062;
    L_0x005a:
        r8 = move-exception;
        r10.addSuppressed(r8);	 Catch:{ IOException -> 0x0063 }
        goto L_0x0062;	 Catch:{ IOException -> 0x0063 }
    L_0x005f:
        r8.close();	 Catch:{ IOException -> 0x0063 }
    L_0x0062:
        throw r9;	 Catch:{ IOException -> 0x0063 }
    L_0x0063:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(android.content.Context, android.os.CancellationSignal, android.net.Uri):java.nio.ByteBuffer");
    }

    @RequiresApi(19)
    public static ByteBuffer copyToDirectBuffer(Context context, Resources resources, int i) {
        context = getTempFile(context);
        if (context == null) {
            return null;
        }
        try {
            if (copyToFile(context, resources, i) == null) {
                return null;
            }
            resources = mmap(context);
            context.delete();
            return resources;
        } finally {
            context.delete();
        }
    }

    public static boolean copyToFile(File file, InputStream inputStream) {
        StringBuilder stringBuilder;
        Closeable closeable = null;
        try {
            Closeable fileOutputStream = new FileOutputStream(file, false);
            try {
                file = new byte[1024];
                while (true) {
                    int read = inputStream.read(file);
                    if (read != -1) {
                        fileOutputStream.write(file, 0, read);
                    } else {
                        closeQuietly(fileOutputStream);
                        return true;
                    }
                }
            } catch (IOException e) {
                file = e;
                closeable = fileOutputStream;
                try {
                    inputStream = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Error copying resource contents to temp file: ");
                    stringBuilder.append(file.getMessage());
                    Log.e(inputStream, stringBuilder.toString());
                    closeQuietly(closeable);
                    return false;
                } catch (Throwable th) {
                    file = th;
                    closeQuietly(closeable);
                    throw file;
                }
            } catch (Throwable th2) {
                file = th2;
                closeable = fileOutputStream;
                closeQuietly(closeable);
                throw file;
            }
        } catch (IOException e2) {
            file = e2;
            inputStream = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Error copying resource contents to temp file: ");
            stringBuilder.append(file.getMessage());
            Log.e(inputStream, stringBuilder.toString());
            closeQuietly(closeable);
            return false;
        }
    }

    public static boolean copyToFile(File file, Resources resources, int i) {
        try {
            resources = resources.openRawResource(i);
            try {
                file = copyToFile(file, resources);
                closeQuietly(resources);
                return file;
            } catch (Throwable th) {
                file = th;
                closeQuietly(resources);
                throw file;
            }
        } catch (Throwable th2) {
            file = th2;
            resources = null;
            closeQuietly(resources);
            throw file;
        }
    }

    public static void closeQuietly(java.io.Closeable r0) {
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
        if (r0 == 0) goto L_0x0005;
    L_0x0002:
        r0.close();	 Catch:{ IOException -> 0x0005 }
    L_0x0005:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.closeQuietly(java.io.Closeable):void");
    }
}
