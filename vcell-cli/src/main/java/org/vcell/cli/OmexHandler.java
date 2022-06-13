package org.vcell.cli;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;
import org.sbml.libcombine.CaContent;
import org.sbml.libcombine.CaListOfContents;
import org.sbml.libcombine.CaOmexManifest;
import org.sbml.libcombine.CombineArchive;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OmexHandler {
    CLIUtils utils;
    String tempPath;
    String omexPath;
    String omexName;
    String outDirPath;
    CombineArchive archive;

    // Assuming omexPath will always be absolute path
    public OmexHandler(String omexPath, String outDir) throws IOException {
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
            String[] omexNameArray = omexPath.split("/", -2);
            String omexName = omexNameArray[omexNameArray.length - 1];
            System.err.println("Provided OMEX `" + omexName + "` is not present");
            System.exit(1);
        }
        int indexOfLastSlash = omexPath.lastIndexOf("/");
        this.omexName = omexPath.substring(indexOfLastSlash + 1);

        this.utils = CLIUtils.getCLIUtils();
        this.tempPath = utils.getTempDir();

        this.archive = new CombineArchive();
        boolean isInitialized = archive.initializeFromArchive(omexPath);

        if (!isInitialized) {
            System.err.println("Unable to initialise OMEX archive, archive maybe corrupted");
            System.exit(1);
        }
    }

    public static void rename(String zipFileName, String entryOldName, String entryNewName) throws Exception {

        /* Define ZIP File System Properties in HashMap */
        Map<String, String> zip_properties = new HashMap<>();
        /* We want to read an existing ZIP File, so we set this to False */
        zip_properties.put("create", "false");

        /* Specify the path to the ZIP File that you want to read as a File System */
        URI zip_disk = URI.create("jar:file:/" + zipFileName);

        /* Create ZIP file System */
        try (FileSystem zipfs = FileSystems.newFileSystem(zip_disk, zip_properties)) {
            /* Access file that needs to be renamed */
            Path pathInZipfile = zipfs.getPath(entryOldName);
            /* Specify new file name */
            Path renamedZipEntry = zipfs.getPath(entryNewName);
            System.out.println("About to rename an entry from ZIP File" + pathInZipfile.toUri());
            /* Execute rename */
            Files.move(pathInZipfile, renamedZipEntry, StandardCopyOption.ATOMIC_MOVE);
            System.out.println("File successfully renamed");
        }
    }

    public ArrayList<String> getSedmlLocationsRelative() throws Exception {
        ArrayList<String> sedmlList = new ArrayList<>();
        CaOmexManifest manifest = this.archive.getManifest();
        CaListOfContents contents = manifest.getListOfContents();

        int masterCount = 0;
        for (int contentIndex = 0; contentIndex < contents.getNumContents(); contentIndex++) {
            CaContent contentFile = (CaContent) contents.get(contentIndex);

            if (contentFile.isFormat("sedml") && contentFile.getMaster()) {
                masterCount++;
            }

            if(!contentFile.isFormat("sedml") && contentFile.getMaster()) {
                throw new Exception("No SED-ML's are intended to be executed (non SED-ML file is set to be master)");
            }
        }

        if( masterCount > 1) {
            throw new Exception("More than two master SED-ML's found");
        }

        for (int contentIndex = 0; contentIndex < contents.getNumContents(); contentIndex++) {
            CaContent contentFile = (CaContent) contents.get(contentIndex);

            if(masterCount == 0 ) {
                if (contentFile.isFormat("sedml")) {
                    sedmlList.add(contentFile.getLocation());
                }
            } else {
                if (contentFile.isFormat("sedml") && contentFile.getMaster()) {
                    sedmlList.add(contentFile.getLocation());
                }
            }
        }

        return sedmlList;
    }


    public ArrayList<String> getSedmlLocationsAbsolute() throws Exception {
        ArrayList<String> sedmlListAbsolute = new ArrayList<>();
        ArrayList<String> sedmlListRelative = this.getSedmlLocationsRelative();

        for (String sedmlFileRelative : sedmlListRelative) {
            sedmlListAbsolute.add(Paths.get(this.tempPath, sedmlFileRelative).normalize().toString());
        }
        return sedmlListAbsolute;
    }

    public String getOutputPathFromSedml(String absoluteSedmlPath) throws Exception {
        String outputPath = "";
        //String sedmlName = absoluteSedmlPath.substring(absoluteSedmlPath.lastIndexOf(File.separator) + 1);
        ArrayList<String> sedmlListRelative = this.getSedmlLocationsRelative();
        for (String sedmlFileRelative : sedmlListRelative) {
            boolean check = absoluteSedmlPath.contains(Paths.get(sedmlFileRelative).normalize().toString());
            if (check) {
                outputPath = Paths.get(this.outDirPath, sedmlFileRelative).normalize().toString();
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
        boolean isRemoved = CLIUtils.removeDirs(new File(this.tempPath));
        if (!isRemoved) {
            System.err.println("Unable to remove temp directory: " + this.tempPath);
        }
    }
}