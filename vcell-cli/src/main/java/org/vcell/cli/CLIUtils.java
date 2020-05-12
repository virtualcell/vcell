package org.vcell.cli;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CLIUtils {
//    private String tempDirPath = null;
    private String extractedOmexPath = null;

    public CLIUtils() {

    }

    @SuppressWarnings("UnstableApiUsage")
    public String getTempDir() {
//        this.tempDirPath = Files.createTempDir().getAbsolutePath();
//        return this.tempDirPath;
        return Files.createTempDir().getAbsolutePath();
    }

    public boolean setupOutputDir(String outDirPath) {
        return true;
    }


    public void setExtractedOmexPath(String path) {
        this.extractedOmexPath = path;
    }

    public boolean removeDirs(File f) {
        return true;
    }

    public boolean makeDirs(File f) {
        return true;
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteRecursively(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }
}
