package org.vcell.cli;

import com.google.common.io.Files;

import java.io.File;

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

    public String getOutputPathFromSedml(String sedmlName) {
        // Scan extractedOmexPath for file
        // Get intermediate folders
        // Strip .sedml from end
        // return resultant path
        return "";
    }

    public void setExtractedOmexPath(String path) {
        this.extractedOmexPath = path;
    }

    public boolean removeDirs(String path) {
        return true;
    }

    public boolean makeDirs(String path) {
        return true;
    }
}
