package com.ngx.nix.selector;

public class FileData implements Comparable<FileData> {
    public static final int DIRECTORY = 1;
    public static final int FILE = 2;
    public static final int UP_FOLDER = 0;
    private final String mFileName;
    private final int mFileType;

    public FileData(String fileName, int fileType) {
        if (fileType == 0 || fileType == 1 || fileType == 2) {
            this.mFileName = fileName;
            this.mFileType = fileType;
            return;
        }
        throw new IllegalArgumentException("Illegel type of file");
    }

    public int compareTo(FileData another) {
        if (this.mFileType != another.mFileType) {
            return this.mFileType - another.mFileType;
        }
        return this.mFileName.compareTo(another.mFileName);
    }

    public String getFileName() {
        return this.mFileName;
    }

    public int getFileType() {
        return this.mFileType;
    }
}
