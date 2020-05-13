package org.vcell.cli;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class CLIUtils {
//    private String tempDirPath = null;
    private String extractedOmexPath = null;

    public CLIUtils() {

    }

    @SuppressWarnings("UnstableApiUsage")
    public String getTempDir() {
        return Files.createTempDir().getAbsolutePath();
    }


    public static boolean removeDirs(File f) {
        try {
            deleteRecursively(f);
        } catch(IOException ex) {
            System.err.println("Failed to delete the file: " + f);
            return false;
        }
        return true;
    }

    public static boolean makeDirs(File f) {
        if(f.exists()) {
            boolean isRemoved = removeDirs(f);
            if(!isRemoved)
                return false;
        }
        return f.mkdir();
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles())) {
                deleteRecursively(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }
}
