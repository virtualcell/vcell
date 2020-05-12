package org.vcell.cli;

import java.util.ArrayList;

public class OmexHandler {
    CLIUtils utils = null;
    String basePath = null;
    String omexPath = null;
    String omexName = null;

    // Assuming omexPath will always be absolute path
    public OmexHandler(String omexPath) {
        this.omexPath = omexPath;
        int indexOfLastSlash = omexPath.lastIndexOf("/");
        this.omexName = omexPath.substring(indexOfLastSlash + 1);

        this.utils = new CLIUtils();
        this.basePath = utils.getTempDir();

    }

    public ArrayList<String> getSedmlLocations() {
        return new ArrayList<>();
    }

    private boolean extractOmex(String inPath, String outPath) {
        return true;
    }
}
