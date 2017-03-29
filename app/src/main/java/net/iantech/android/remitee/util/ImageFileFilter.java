package net.iantech.android.remitee.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by andres on 28/03/17.
 */

public class ImageFileFilter implements FileFilter {
    File file;

    static final Set<String> okFileExtensions = new TreeSet<>();

    static {
        okFileExtensions.add("3gp");
        okFileExtensions.add("mp3");
        okFileExtensions.add("mp4");
        okFileExtensions.add("ts");
        okFileExtensions.add("webm");
        okFileExtensions.add("mkv");
    }


    /**
     *
     */
    public ImageFileFilter(File newfile) {
        this.file = newfile;
    }

    public boolean accept(File file) {

        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return false;
            }
        }

        return true;
    }

}