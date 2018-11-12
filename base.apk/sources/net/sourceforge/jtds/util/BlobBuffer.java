package net.sourceforge.jtds.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.Messages;

public class BlobBuffer {
    private static final int BYTE_MASK = 1023;
    private static final byte[] EMPTY_BUFFER = new byte[0];
    private static final int INVALID_PAGE = -1;
    private static final int MAX_BUF_INC = 16384;
    private static final int PAGE_MASK = -1024;
    private static final int PAGE_SIZE = 1024;
    private File blobFile;
    private byte[] buffer = EMPTY_BUFFER;
    private final File bufferDir;
    private boolean bufferDirty;
    private int currentPage;
    private boolean isMemOnly;
    private int length;
    private final int maxMemSize;
    private int openCount;
    private RandomAccessFile raFile;

    private class AsciiInputStream extends InputStream {
        private boolean open = true;
        private int readPtr;

        public AsciiInputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.readPtr = (int) j;
        }

        protected void finalize() throws java.lang.Throwable {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r1 = this;
            r0 = r1.open;
            if (r0 == 0) goto L_0x0010;
        L_0x0004:
            r1.close();	 Catch:{ IOException -> 0x0007, all -> 0x000b }
        L_0x0007:
            super.finalize();
            goto L_0x0010;
        L_0x000b:
            r0 = move-exception;
            super.finalize();
            throw r0;
        L_0x0010:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.util.BlobBuffer.AsciiInputStream.finalize():void");
        }

        public int available() throws IOException {
            return (((int) BlobBuffer.this.getLength()) - this.readPtr) / 2;
        }

        public int read() throws IOException {
            int read = BlobBuffer.this.read(this.readPtr);
            if (read >= 0) {
                this.readPtr++;
                int read2 = BlobBuffer.this.read(this.readPtr);
                if (read2 >= 0) {
                    this.readPtr++;
                    if (read2 != 0 || read > 127) {
                        read = 63;
                    }
                    return read;
                }
            }
            return -1;
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class AsciiOutputStream extends OutputStream {
        private boolean open = true;
        private int writePtr;

        AsciiOutputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.writePtr = (int) j;
        }

        protected void finalize() throws java.lang.Throwable {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r1 = this;
            r0 = r1.open;
            if (r0 == 0) goto L_0x0010;
        L_0x0004:
            r1.close();	 Catch:{ IOException -> 0x0007, all -> 0x000b }
        L_0x0007:
            super.finalize();
            goto L_0x0010;
        L_0x000b:
            r0 = move-exception;
            super.finalize();
            throw r0;
        L_0x0010:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.util.BlobBuffer.AsciiOutputStream.finalize():void");
        }

        public void write(int i) throws IOException {
            BlobBuffer blobBuffer = BlobBuffer.this;
            int i2 = this.writePtr;
            this.writePtr = i2 + 1;
            blobBuffer.write(i2, i);
            i = BlobBuffer.this;
            int i3 = this.writePtr;
            this.writePtr = i3 + 1;
            i.write(i3, 0);
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class BlobInputStream extends InputStream {
        private boolean open = true;
        private int readPtr;

        public BlobInputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.readPtr = (int) j;
        }

        protected void finalize() throws java.lang.Throwable {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r1 = this;
            r0 = r1.open;
            if (r0 == 0) goto L_0x0010;
        L_0x0004:
            r1.close();	 Catch:{ IOException -> 0x0007, all -> 0x000b }
        L_0x0007:
            super.finalize();
            goto L_0x0010;
        L_0x000b:
            r0 = move-exception;
            super.finalize();
            throw r0;
        L_0x0010:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.util.BlobBuffer.BlobInputStream.finalize():void");
        }

        public int available() throws IOException {
            return ((int) BlobBuffer.this.getLength()) - this.readPtr;
        }

        public int read() throws IOException {
            int read = BlobBuffer.this.read(this.readPtr);
            if (read >= 0) {
                this.readPtr++;
            }
            return read;
        }

        public int read(byte[] bArr, int i, int i2) throws IOException {
            bArr = BlobBuffer.this.read(this.readPtr, bArr, i, i2);
            if (bArr > null) {
                this.readPtr += bArr;
            }
            return bArr;
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class BlobOutputStream extends OutputStream {
        private boolean open = true;
        private int writePtr;

        BlobOutputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.writePtr = (int) j;
        }

        protected void finalize() throws java.lang.Throwable {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r1 = this;
            r0 = r1.open;
            if (r0 == 0) goto L_0x0010;
        L_0x0004:
            r1.close();	 Catch:{ IOException -> 0x0007, all -> 0x000b }
        L_0x0007:
            super.finalize();
            goto L_0x0010;
        L_0x000b:
            r0 = move-exception;
            super.finalize();
            throw r0;
        L_0x0010:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.util.BlobBuffer.BlobOutputStream.finalize():void");
        }

        public void write(int i) throws IOException {
            BlobBuffer blobBuffer = BlobBuffer.this;
            int i2 = this.writePtr;
            this.writePtr = i2 + 1;
            blobBuffer.write(i2, i);
        }

        public void write(byte[] bArr, int i, int i2) throws IOException {
            BlobBuffer.this.write(this.writePtr, bArr, i, i2);
            this.writePtr += i2;
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class UnicodeInputStream extends InputStream {
        private boolean open = true;
        private int readPtr;

        public UnicodeInputStream(long j) throws IOException {
            BlobBuffer.this.open();
            this.readPtr = (int) j;
        }

        protected void finalize() throws java.lang.Throwable {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r1 = this;
            r0 = r1.open;
            if (r0 == 0) goto L_0x0010;
        L_0x0004:
            r1.close();	 Catch:{ IOException -> 0x0007, all -> 0x000b }
        L_0x0007:
            super.finalize();
            goto L_0x0010;
        L_0x000b:
            r0 = move-exception;
            super.finalize();
            throw r0;
        L_0x0010:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.util.BlobBuffer.UnicodeInputStream.finalize():void");
        }

        public int available() throws IOException {
            return ((int) BlobBuffer.this.getLength()) - this.readPtr;
        }

        public int read() throws IOException {
            int read = BlobBuffer.this.read(this.readPtr ^ 1);
            if (read >= 0) {
                this.readPtr++;
            }
            return read;
        }

        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    public BlobBuffer(File file, long j) {
        this.bufferDir = file;
        this.maxMemSize = (int) j;
    }

    protected void finalize() throws java.lang.Throwable {
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
        r2 = this;
        r0 = r2.raFile;	 Catch:{ IOException -> 0x001e, all -> 0x0013 }
        if (r0 == 0) goto L_0x0009;	 Catch:{ IOException -> 0x001e, all -> 0x0013 }
    L_0x0004:
        r0 = r2.raFile;	 Catch:{ IOException -> 0x001e, all -> 0x0013 }
        r0.close();	 Catch:{ IOException -> 0x001e, all -> 0x0013 }
    L_0x0009:
        r0 = r2.blobFile;
        if (r0 == 0) goto L_0x0023;
    L_0x000d:
        r0 = r2.blobFile;
        r0.delete();
        goto L_0x0023;
    L_0x0013:
        r0 = move-exception;
        r1 = r2.blobFile;
        if (r1 == 0) goto L_0x001d;
    L_0x0018:
        r1 = r2.blobFile;
        r1.delete();
    L_0x001d:
        throw r0;
    L_0x001e:
        r0 = r2.blobFile;
        if (r0 == 0) goto L_0x0023;
    L_0x0022:
        goto L_0x000d;
    L_0x0023:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.util.BlobBuffer.finalize():void");
    }

    public void createBlobFile() {
        try {
            this.blobFile = File.createTempFile("jtds", ".tmp", this.bufferDir);
            this.raFile = new RandomAccessFile(this.blobFile, "rw");
            if (this.length > 0) {
                this.raFile.write(this.buffer, 0, this.length);
            }
            this.buffer = new byte[1024];
            this.currentPage = -1;
            this.openCount = 0;
        } catch (Exception e) {
            this.blobFile = null;
            this.raFile = null;
            this.isMemOnly = true;
            Logger.println("SecurityException creating BLOB file:");
            Logger.logException(e);
        } catch (Exception e2) {
            this.blobFile = null;
            this.raFile = null;
            this.isMemOnly = true;
            Logger.println("IOException creating BLOB file:");
            Logger.logException(e2);
        }
    }

    public void open() throws IOException {
        if (this.raFile != null || this.blobFile == null) {
            if (this.raFile != null) {
                this.openCount++;
            }
            return;
        }
        this.raFile = new RandomAccessFile(this.blobFile, "rw");
        this.openCount = 1;
        this.currentPage = -1;
        this.buffer = new byte[1024];
    }

    public int read(int i) throws IOException {
        if (i >= this.length) {
            return -1;
        }
        if (this.raFile == null) {
            return this.buffer[i] & 255;
        }
        if (this.currentPage != (i & PAGE_MASK)) {
            readPage(i);
        }
        return this.buffer[i & BYTE_MASK] & 255;
    }

    public int read(int i, byte[] bArr, int i2, int i3) throws IOException {
        if (bArr == null) {
            throw new NullPointerException();
        }
        if (i2 >= 0 && i2 <= bArr.length && i3 >= 0) {
            int i4 = i2 + i3;
            if (i4 <= bArr.length) {
                if (i4 >= 0) {
                    if (i3 == 0) {
                        return 0;
                    }
                    if (i >= this.length) {
                        return -1;
                    }
                    if (this.raFile != null) {
                        i3 = Math.min(this.length - i, i3);
                        if (i3 >= 1024) {
                            if (this.bufferDirty) {
                                writePage(this.currentPage);
                            }
                            this.currentPage = -1;
                            this.raFile.seek((long) i);
                            this.raFile.readFully(bArr, i2, i3);
                        } else {
                            int i5 = i2;
                            i2 = i;
                            i = i3;
                            while (i > 0) {
                                if (this.currentPage != (i2 & PAGE_MASK)) {
                                    readPage(i2);
                                }
                                int i6 = i2 & BYTE_MASK;
                                int min = Math.min(1024 - i6, i);
                                System.arraycopy(this.buffer, i6, bArr, i5, min);
                                i5 += min;
                                i2 += min;
                                i -= min;
                            }
                        }
                    } else {
                        i3 = Math.min(this.length - i, i3);
                        System.arraycopy(this.buffer, i, bArr, i2, i3);
                    }
                    return i3;
                }
            }
        }
        throw new IndexOutOfBoundsException();
    }

    public void write(int i, int i2) throws IOException {
        if (i >= this.length) {
            if (i > this.length) {
                throw new IOException("BLOB buffer has been truncated");
            }
            int i3 = this.length + 1;
            this.length = i3;
            if (i3 < 0) {
                throw new IOException("BLOB may not exceed 2GB in size");
            }
        }
        if (this.raFile != null) {
            if (this.currentPage != (i & PAGE_MASK)) {
                readPage(i);
            }
            this.buffer[i & BYTE_MASK] = (byte) i2;
            this.bufferDirty = true;
            return;
        }
        if (i >= this.buffer.length) {
            growBuffer(i + 1);
        }
        this.buffer[i] = (byte) i2;
    }

    void write(int i, byte[] bArr, int i2, int i3) throws IOException {
        if (bArr == null) {
            throw new NullPointerException();
        }
        if (i2 >= 0 && i2 <= bArr.length && i3 >= 0) {
            int i4 = i2 + i3;
            if (i4 <= bArr.length) {
                if (i4 >= 0) {
                    if (i3 != 0) {
                        long j = (long) i;
                        if (j + ((long) i3) > 2147483647L) {
                            throw new IOException("BLOB may not exceed 2GB in size");
                        } else if (i > this.length) {
                            throw new IOException("BLOB buffer has been truncated");
                        } else {
                            if (this.raFile == null) {
                                i4 = i + i3;
                                if (i4 > this.buffer.length) {
                                    growBuffer(i4);
                                }
                                System.arraycopy(bArr, i2, this.buffer, i, i3);
                                i = i4;
                            } else if (i3 >= 1024) {
                                if (this.bufferDirty) {
                                    writePage(this.currentPage);
                                }
                                this.currentPage = -1;
                                this.raFile.seek(j);
                                this.raFile.write(bArr, i2, i3);
                                i += i3;
                            } else {
                                while (i3 > 0) {
                                    if (this.currentPage != (i & PAGE_MASK)) {
                                        readPage(i);
                                    }
                                    i4 = i & BYTE_MASK;
                                    int min = Math.min(1024 - i4, i3);
                                    System.arraycopy(bArr, i2, this.buffer, i4, min);
                                    this.bufferDirty = true;
                                    i2 += min;
                                    i += min;
                                    i3 -= min;
                                }
                            }
                            if (i > this.length) {
                                this.length = i;
                            }
                            return;
                        }
                    }
                    return;
                }
            }
        }
        throw new IndexOutOfBoundsException();
    }

    public void readPage(int i) throws IOException {
        i &= PAGE_MASK;
        if (this.bufferDirty) {
            writePage(this.currentPage);
        }
        if (((long) i) > this.raFile.length()) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("readPage: Invalid page number ");
            stringBuffer.append(i);
            throw new IOException(stringBuffer.toString());
        }
        this.currentPage = i;
        this.raFile.seek((long) this.currentPage);
        int i2 = 0;
        int read;
        do {
            read = this.raFile.read(this.buffer, i2, this.buffer.length - i2);
            i2 += read == -1 ? 0 : read;
            if (i2 >= 1024) {
                return;
            }
        } while (read != -1);
    }

    public void writePage(int i) throws IOException {
        i &= PAGE_MASK;
        long j = (long) i;
        if (j > this.raFile.length()) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("writePage: Invalid page number ");
            stringBuffer.append(i);
            throw new IOException(stringBuffer.toString());
        } else if (this.buffer.length != 1024) {
            throw new IllegalStateException("writePage: buffer size invalid");
        } else {
            this.raFile.seek(j);
            this.raFile.write(this.buffer);
            this.bufferDirty = false;
        }
    }

    public void close() throws IOException {
        if (this.openCount > 0) {
            int i = this.openCount - 1;
            this.openCount = i;
            if (i == 0 && this.raFile != null) {
                if (this.bufferDirty) {
                    writePage(this.currentPage);
                }
                this.raFile.close();
                this.raFile = null;
                this.buffer = EMPTY_BUFFER;
                this.currentPage = -1;
            }
        }
    }

    public void growBuffer(int i) {
        if (this.buffer.length == 0) {
            this.buffer = new byte[Math.max(1024, i)];
            return;
        }
        if (this.buffer.length * 2 <= i || this.buffer.length > 16384) {
            i = new byte[(i + 16384)];
        } else {
            i = new byte[(this.buffer.length * 2)];
        }
        System.arraycopy(this.buffer, 0, i, 0, this.buffer.length);
        this.buffer = i;
    }

    public void setBuffer(byte[] bArr, boolean z) {
        if (z) {
            this.buffer = new byte[bArr.length];
            System.arraycopy(bArr, 0, this.buffer, 0, this.buffer.length);
        } else {
            this.buffer = bArr;
        }
        this.length = this.buffer.length;
    }

    public byte[] getBytes(long j, int i) throws SQLException {
        long j2 = j - 1;
        if (j2 < 0) {
            throw new SQLException(Messages.get("error.blobclob.badpos"), "HY090");
        } else if (j2 > ((long) this.length)) {
            throw new SQLException(Messages.get("error.blobclob.badposlen"), "HY090");
        } else if (i < 0) {
            throw new SQLException(Messages.get("error.blobclob.badlen"), "HY090");
        } else {
            if (j2 + ((long) i) > ((long) this.length)) {
                i = (int) (((long) this.length) - j2);
            }
            try {
                j = new byte[i];
                if (this.blobFile == null) {
                    System.arraycopy(this.buffer, (int) j2, j, 0, i);
                } else {
                    InputStream blobInputStream = new BlobInputStream(j2);
                    i = blobInputStream.read(j);
                    blobInputStream.close();
                    if (i != j.length) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("Unexpected EOF on BLOB data file bc=");
                        stringBuffer.append(i);
                        stringBuffer.append(" data.len=");
                        stringBuffer.append(j.length);
                        throw new IOException(stringBuffer.toString());
                    }
                }
                return j;
            } catch (long j3) {
                throw new SQLException(Messages.get("error.generic.ioerror", j3.getMessage()), "HY000");
            }
        }
    }

    public InputStream getBinaryStream(boolean z) throws SQLException {
        if (!z) {
            return new BlobInputStream(0);
        }
        try {
            return new AsciiInputStream(0);
        } catch (boolean z2) {
            throw new SQLException(Messages.get("error.generic.ioerror", z2.getMessage()), "HY000");
        }
    }

    public InputStream getUnicodeStream() throws SQLException {
        try {
            return new UnicodeInputStream(0);
        } catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
        }
    }

    public OutputStream setBinaryStream(long j, boolean z) throws SQLException {
        long j2 = j - 1;
        if (j2 < 0) {
            throw new SQLException(Messages.get("error.blobclob.badpos"), "HY090");
        } else if (j2 > ((long) this.length)) {
            throw new SQLException(Messages.get("error.blobclob.badposlen"), "HY090");
        } else {
            try {
                if (this.isMemOnly == null && this.blobFile == null) {
                    createBlobFile();
                }
                if (z) {
                    return new AsciiOutputStream(j2);
                }
                return new BlobOutputStream(j2);
            } catch (long j3) {
                throw new SQLException(Messages.get("error.generic.ioerror", j3.getMessage()), "HY000");
            }
        }
    }

    public int setBytes(long j, byte[] bArr, int i, int i2, boolean z) throws SQLException {
        long j2 = j - 1;
        if (j2 < 0) {
            throw new SQLException(Messages.get("error.blobclob.badpos"), "HY090");
        } else if (j2 > ((long) this.length)) {
            throw new SQLException(Messages.get("error.blobclob.badposlen"), "HY090");
        } else if (bArr == null) {
            throw new SQLException(Messages.get("error.blob.bytesnull"), "HY009");
        } else {
            if (i >= 0) {
                if (i <= bArr.length) {
                    if (i2 >= 0 && j2 + ((long) i2) <= 2147483647L) {
                        if (i + i2 <= bArr.length) {
                            if (this.blobFile != null || j2 != 0 || i2 < this.length || i2 > this.maxMemSize) {
                                try {
                                    if (this.isMemOnly == null && this.blobFile == null) {
                                        createBlobFile();
                                    }
                                    open();
                                    write((int) j2, bArr, i, i2);
                                    close();
                                    return i2;
                                } catch (long j3) {
                                    throw new SQLException(Messages.get("error.generic.ioerror", j3.getMessage()), "HY000");
                                }
                            }
                            if (z) {
                                this.buffer = new byte[i2];
                                System.arraycopy(bArr, i, this.buffer, 0, i2);
                            } else {
                                this.buffer = bArr;
                            }
                            this.length = i2;
                            return i2;
                        }
                    }
                    throw new SQLException(Messages.get("error.blobclob.badlen"), "HY090");
                }
            }
            throw new SQLException(Messages.get("error.blobclob.badoffset"), "HY090");
        }
    }

    public long getLength() {
        return (long) this.length;
    }

    public void setLength(long j) {
        this.length = (int) j;
    }

    public void truncate(long j) throws SQLException {
        if (j < 0) {
            throw new SQLException(Messages.get("error.blobclob.badlen"), "HY090");
        } else if (j > ((long) this.length)) {
            throw new SQLException(Messages.get("error.blobclob.lentoolong"), "HY090");
        } else {
            this.length = (int) j;
            if (j == 0) {
                try {
                    if (this.blobFile != null) {
                        if (this.raFile != null) {
                            this.raFile.close();
                        }
                        this.blobFile.delete();
                    }
                    this.buffer = EMPTY_BUFFER;
                    this.blobFile = null;
                    this.raFile = null;
                    this.openCount = 0;
                    this.currentPage = -1;
                } catch (IOException e) {
                    throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
                } catch (Throwable th) {
                    this.buffer = EMPTY_BUFFER;
                    this.blobFile = null;
                    this.raFile = null;
                    this.openCount = 0;
                    this.currentPage = -1;
                }
            }
        }
    }

    public int position(byte[] bArr, long j) throws SQLException {
        long j2 = j - 1;
        if (j2 < 0) {
            try {
                throw new SQLException(Messages.get("error.blobclob.badpos"), "HY090");
            } catch (byte[] bArr2) {
                throw new SQLException(Messages.get("error.generic.ioerror", bArr2.getMessage()), "HY000");
            }
        } else if (j2 >= ((long) this.length)) {
            throw new SQLException(Messages.get("error.blobclob.badposlen"), "HY090");
        } else if (bArr2 == null) {
            throw new SQLException(Messages.get("error.blob.badpattern"), "HY009");
        } else {
            if (!(bArr2.length == null || this.length == null)) {
                if (bArr2.length <= this.length) {
                    j = this.length - bArr2.length;
                    int i;
                    int i2;
                    if (this.blobFile == null) {
                        i = (int) j2;
                        while (i <= j) {
                            i2 = 0;
                            while (i2 < bArr2.length && this.buffer[i + i2] == bArr2[i2]) {
                                i2++;
                            }
                            if (i2 == bArr2.length) {
                                return i + 1;
                            }
                            i++;
                        }
                    } else {
                        open();
                        i = (int) j2;
                        while (i <= j) {
                            i2 = 0;
                            while (i2 < bArr2.length && read(i + i2) == (bArr2[i2] & 255)) {
                                i2++;
                            }
                            if (i2 == bArr2.length) {
                                close();
                                return i + 1;
                            }
                            i++;
                        }
                        close();
                    }
                    return -1;
                }
            }
            return -1;
        }
    }
}
