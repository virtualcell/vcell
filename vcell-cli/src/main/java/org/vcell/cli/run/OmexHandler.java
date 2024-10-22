package org.vcell.cli.run;

import de.unirostock.sems.cbarchive.ArchiveEntry;
import de.unirostock.sems.cbarchive.CombineArchive;
import de.unirostock.sems.cbarchive.CombineArchiveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.vcell.cli.CLIUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.text.ParseException;
import java.util.*;

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
        this.omexPath = omexPath;
        this.outDirPath = outDir;

        if (!new File(omexPath).exists()) {
            String[] omexNameArray = omexPath.split("/", -2);
            String omexName = omexNameArray[omexNameArray.length - 1];
            IOException e = new IOException("Provided OMEX `" + omexName + "` is not present at path: " + omexPath);
            logger.error(e.getMessage(), e);
            throw e;
        }
        int indexOfLastSlash = omexPath.lastIndexOf("/");
        this.omexName = omexPath.substring(indexOfLastSlash + 1);
        this.tempPath = RunUtils.getTempDir();
        try {
            replaceMetadataRdfFiles(Paths.get(omexPath));
            this.archive = new CombineArchive(new File(omexPath));
            if (this.archive.hasErrors()){
                String message = "Unable to initialise OMEX archive "+this.omexName+": "+this.archive.getErrors();
                logger.error(message);
                throw new IOException(message);
            }
        }catch (CombineArchiveException | JDOMException | ParseException e) {
            String message = String.format("Unable to initialise OMEX archive \"%s\", archive maybe corrupted", this.omexName);
            logger.error(message+": "+e.getMessage(), e);
            throw new IOException(e);
        }
    }

    private void replaceMetadataRdfFiles(Path zipFilePath) throws IOException {
        try( FileSystem fs = FileSystems.newFileSystem(zipFilePath) ) {
            for (Path root : fs.getRootDirectories()) {
                Files.walk(root)
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".rdf"))
                        .forEach(path -> {
                            try {
                                // write empty RDF file to temp file and replace the file inside the zip
                                Path tempFile = Files.createTempFile("temp", ".rdf");
                                String new_rdf_content =
                                    """
                                    <?xml version='1.0' encoding='UTF-8'?>
                                    <rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>
                                    </rdf:RDF>
                                    """;
                                Files.write(tempFile, new_rdf_content.getBytes());
                                // replace fileInsideZipPath with temp file
                                Files.delete(path);
                                Files.copy(tempFile, path);
                                Files.delete(tempFile);
                            } catch (IOException e) {
                                logger.error("Unable to delete metadata.rdf file from OMEX archive: " + e.getMessage(), e);
                            }
                        });
            }

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

    public List<String> getSedmlLocationsRelative(){
        Collection<ArchiveEntry> entries = this.archive.getEntries();

        int MASTER = 1;
        int REGULAR = 2;
        int masterCount = 0;
        Map<Integer, ArrayList<ArchiveEntry>> sedmlMap = new HashMap<>();
        sedmlMap.put(MASTER, new ArrayList<>());
        sedmlMap.put(REGULAR, new ArrayList<>());


        for (ArchiveEntry entry : entries) {
            boolean isMaster = entry.isMainEntry();
            if (isMaster) masterCount++;
            if (!this.isSedmlFormat(entry)) continue;
            sedmlMap.get(isMaster ? MASTER : REGULAR).add(entry);
        }

        // Test corner cases
        if (!sedmlMap.get(MASTER).isEmpty()) return sedmlMap.get(MASTER).stream().map(ArchiveEntry::getFilePath).toList();

        if (masterCount > 0)
            logger.warn("No SED-MLs are intended to be executed (non SED-ML file is set to be master)");
        else
            logger.warn("No files in the OMEX archive are set with \"master\" flag.");
        if (sedmlMap.get(REGULAR).isEmpty())
            throw new UnsupportedOperationException("There are no SED-MLs in the archive to execute");
        logger.warn("Regular SED-ML found; executing regular SED-MLs in lieu of master files");
        return sedmlMap.get(REGULAR).stream().map(ArchiveEntry::getFilePath).toList();
    }

    private boolean isSedmlFormat(ArchiveEntry entry) {
        URI format = entry.getFormat();
        return format.getPath().contains("sedml") || format.getPath().contains("sed-ml");
    }


    public ArrayList<String> getSedmlLocationsAbsolute(){
        ArrayList<String> sedmlListAbsolute = new ArrayList<>();
        List<String> sedmlListRelative = this.getSedmlLocationsRelative();

        for (String sedmlFileRelative : sedmlListRelative) {
            sedmlListAbsolute.add(Paths.get(this.tempPath, sedmlFileRelative).normalize().toString());
        }
        return sedmlListAbsolute;
    }

    public String getOutputPathFromSedml(String absoluteSedmlPath) {
        String outputPath = "";
        //String sedmlName = absoluteSedmlPath.substring(absoluteSedmlPath.lastIndexOf(File.separator) + 1);
        List<String> sedmlListRelative = this.getSedmlLocationsRelative();
        for (String sedmlFileRelative : sedmlListRelative) {
            boolean check = absoluteSedmlPath.contains(Paths.get(sedmlFileRelative).normalize().toString());
            if (check) {
                outputPath = Paths.get(this.outDirPath, sedmlFileRelative).normalize().toString();
            }
        }
        return outputPath;
    }

    public void extractOmex() {
        try {
            this.archive.extractTo(new File(this.tempPath));
        } catch (IOException e) {
            String message = String.format("Unable to initialise OMEX archive \"%s\", archive maybe corrupted", this.omexName);
            logger.error(message+": "+e.getMessage(), e);
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