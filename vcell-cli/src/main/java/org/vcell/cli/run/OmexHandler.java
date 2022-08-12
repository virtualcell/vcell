package org.vcell.cli.run;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sbml.libcombine.CaContent;
import org.sbml.libcombine.CaListOfContents;
import org.sbml.libcombine.CaOmexManifest;
import org.sbml.libcombine.CombineArchive;
import org.vcell.cli.CLIUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OmexHandler {
    String tempPath;
    String omexPath;
    String omexName;
    String outDirPath;
    CombineArchive archive;

    private final static Logger logger = LogManager.getLogger(OmexHandler.class);
    
    // Assuming omexPath will always be absolute path
    // NB: Need to convert class to use Log4j2
    public OmexHandler(String omexPath, String outDir) throws IOException {
        try {
            try {
                ResourceUtil.setNativeLibraryDirectory();
                NativeLib.combinej.load();
            } catch (Exception e){
            	logger.error("Unable to link to native 'libCombine' lib, check native lib. Attemping alternate solution...");
                NativeLib.combinej.directLoad();
            }
        } catch (UnsatisfiedLinkError ex) {
            logger.error("Unable to link to native 'libCombine' lib, check native lib: " + ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Error occurred while importing libCombine: " + ex.getMessage());
            throw ex;
        }
        
        this.omexPath = omexPath;
        this.outDirPath = outDir;

        if (!new File(omexPath).exists()) {
            String[] omexNameArray = omexPath.split("/", -2);
            String omexName = omexNameArray[omexNameArray.length - 1];
            
            try{
                throw new RuntimeException("OmexPathException");
            } catch(RuntimeException e){
            	logger.error("Provided OMEX `" + omexName + "` is not present at path: " + omexPath, e);
            	throw e;
            }
        }
        int indexOfLastSlash = omexPath.lastIndexOf("/");
        this.omexName = omexPath.substring(indexOfLastSlash + 1);

        this.tempPath = RunUtils.getTempDir();

        this.archive = new CombineArchive();
        boolean isInitialized = archive.initializeFromArchive(omexPath);

        if (!isInitialized) {
        	String message = String.format("Unable to initialise OMEX archive \"%s\", archive maybe corrupted", this.omexName);
            logger.error(message);
            throw new RuntimeException(message);
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
            logger.trace("About to rename an entry from ZIP File" + pathInZipfile.toUri());
            /* Execute rename */
            Files.move(pathInZipfile, renamedZipEntry, StandardCopyOption.ATOMIC_MOVE);
            logger.trace("File successfully renamed");
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
        	String message = String.format("Unable to initialise OMEX archive \"%s\", archive maybe corrupted", this.omexName);
            logger.error(message);
            throw new RuntimeException(message);
        }
    }

    public void deleteExtractedOmex() {
        boolean isRemoved = CLIUtils.removeDirs(new File(this.tempPath));
        if (!isRemoved) {
            logger.error("Unable to remove temp directory: " + this.tempPath);
        }
    }
}