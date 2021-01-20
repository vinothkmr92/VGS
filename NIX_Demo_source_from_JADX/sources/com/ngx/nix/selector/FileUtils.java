package com.ngx.nix.selector;

import java.io.File;

public class FileUtils {
    public static final String FILTER_ALLOW_ALL = "*.*";

    public static boolean accept(File file, String filter) {
        if (filter.compareTo(FILTER_ALLOW_ALL) == 0 || file.isDirectory()) {
            return true;
        }
        int lastIndexOfPoint = file.getName().lastIndexOf(46);
        if (lastIndexOfPoint == -1) {
            return false;
        }
        if (file.getName().substring(lastIndexOfPoint).toLowerCase().compareTo(filter) != 0) {
            return false;
        }
        return true;
    }
}
