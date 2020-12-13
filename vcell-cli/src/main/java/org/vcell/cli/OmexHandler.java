package org.vcell.cli;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;
import org.sbml.libcombine.CaContent;
import org.sbml.libcombine.CaListOfContents;
import org.sbml.libcombine.CaOmexManifest;
import org.sbml.libcombine.CombineArchive;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class OmexHandler {
    CLIUtils utils = null;
    String tempPath = null;
    String omexPath = null;
    String omexName = null;
    String outDirPath = null;
    CombineArchive archive = null;

    // Assuming omexPath will always be absolute path
    public OmexHandler(String omexPath, String outDir) {
        try {
            ResourceUtil.setNativeLibraryDirectory();
            NativeLib.combinej.load();
//            System.load("combinej");
        } catch (UnsatisfiedLinkError ex) {
            System.err.println("Unable to link to native 'libCombine' lib, check native lib: " + ex.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error occurred while importing libCombine: " + e.getMessage());
            System.exit(1);
        }
        this.omexPath = omexPath;
        this.outDirPath = outDir;

        if (!new File(omexPath).exists()) {
            System.err.println("Provided OMEX is not present");
            System.exit(1);
        }
        int indexOfLastSlash = omexPath.lastIndexOf("/");
        this.omexName = omexPath.substring(indexOfLastSlash + 1);

        this.utils = new CLIUtils();
        this.tempPath = utils.getTempDir();

        this.archive = new CombineArchive();
        boolean isInitialized = archive.initializeFromArchive(omexPath);

        if (!isInitialized) {
            System.err.println("Unable to initialise OMEX archive, archive maybe corrupted");
            System.exit(1);
        }
    }

    public ArrayList<String> getSedmlLocationsRelative() {
        ArrayList<String> sedmlList = new ArrayList<>();
        CaOmexManifest manifest = this.archive.getManifest();
        CaListOfContents contents = manifest.getListOfContents();
        for (int contentIndex = 0; contentIndex < contents.getNumContents(); contentIndex++) {
            CaContent contentFile = (CaContent) contents.get(contentIndex);
            if (contentFile.isFormat("sedml")) {
                sedmlList.add(contentFile.getLocation());
            }
        }
        return sedmlList;
    }


    public ArrayList<String> getSedmlLocationsAbsolute() {
        ArrayList<String> sedmlListAbsolute = new ArrayList<>();
        ArrayList<String> sedmlListRelative = this.getSedmlLocationsRelative();

        for (String sedmlFileRelative : sedmlListRelative) {
            sedmlListAbsolute.add(Paths.get(this.tempPath, sedmlFileRelative).normalize().toString());
        }
        return sedmlListAbsolute;
    }

    public String getOutputPathFromSedml(String absoluteSedmlPath) {
        String outputPath = "";
        String sedmlName = absoluteSedmlPath.substring(absoluteSedmlPath.lastIndexOf(File.separator) + 1);
        ArrayList<String> sedmlListRelative = this.getSedmlLocationsRelative();
        for (String sedmlFileRelative : sedmlListRelative) {
        	boolean check = absoluteSedmlPath.contains(Paths.get(sedmlFileRelative).normalize().toString());
            if (check) {
                outputPath = Paths.get(
                        this.outDirPath,
                        sedmlFileRelative.substring(
                                0, sedmlFileRelative.indexOf(".sedml")
                        )).normalize().toString();
            }
        }

        return outputPath;
    }

    public void extractOmex() {
        boolean isExtracted = this.archive.extractTo(this.tempPath);
        if (!isExtracted) {
            System.err.println("Unable to extract OMEX archive, archive maybe corrupted");
            System.exit(1);
        }
    }

    public void deleteExtractedOmex() {
        boolean isRemoved = this.utils.removeDirs(new File(this.tempPath));
        if (!isRemoved) {
            System.err.println("Unable to remove temp directory: " + this.tempPath);
        }
    }
}
