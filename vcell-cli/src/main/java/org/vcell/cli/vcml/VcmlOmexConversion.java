package org.vcell.cli.vcml;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.util.NativeLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

import org.apache.commons.io.FilenameUtils;
import org.sbml.libcombine.CombineArchive;
import org.sbml.libcombine.KnownFormats;
import org.vcell.cli.CLIHandler;
import org.vcell.sedml.SEDMLExporter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;

public class VcmlOmexConversion {

    public static void parseArgsAndConvert(String[] args) {
        File input = null;
        try {
            // TODO: handle if it's not valid PATH
            input = new File(args[1]);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (input != null && input.isDirectory()) {
            FilenameFilter filterVcmlFiles = (f, name) -> name.endsWith(".vcml");
            String[] inputFiles = input.list(filterVcmlFiles);
            if (inputFiles == null) {
                System.err.println("No VCML files found in the directory `" + input + "`");
            }
//            assert inputFiles != null;
            for (String inputFile : inputFiles) {
                File file = new File(input, inputFile);
                System.out.println(file);
                args[1] = file.toString();
                try {
                    if (inputFile.endsWith(".vcml")) {
                        boolean isCreated = vcmlToOmexConversion(args);
                        if (isCreated) System.out.println("Combine archive created for file(s) `" + input + "`");
                        else System.err.println("Failed converting VCML to OMEX archive for `" + input + "`");
                    } else System.err.println("No VCML files found in the directory `" + input + "`");
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    System.exit(1);
                }
            }
        } else {
            try {
                assert input != null;
                if (input.toString().endsWith(".vcml")) {
                    boolean isCreated = vcmlToOmexConversion(args);
                    if (isCreated) System.out.println("Combine archive created for `" + input + "`");
                    else System.err.println("Failed converting VCML to OMEX archive for `" + input + "`");
                } else System.err.println("No input files found in the directory `" + input + "`");
            } catch (IOException | XmlParseException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static boolean vcmlToOmexConversion(String[] args) throws XmlParseException, IOException {
        CLIHandler cliHandler = new CLIHandler(args);

        // Get VCML file path from -i flag
        String inputVcmlFile = cliHandler.getInputFilePath();

        // Get directory file path from -o flag
        String outputDir = cliHandler.getOutputDirPath();

        // get VCML name from VCML path
        //String vcmlName = inputVcmlFile.split(File.separator, 10)[inputVcmlFile.split(File.separator, 10).length - 1].split("\\.", 5)[0];
        String vcmlName = FilenameUtils.getBaseName(inputVcmlFile);		// platform independent, strips extension too
        

        File vcmlFilePath = new File(inputVcmlFile);

        // Create biomodel
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlFilePath));
        bioModel.refreshDependencies();

        // NOTE: SEDML exporter exports both SEDML as well as required SBML
        SEDMLExporter sedmlExporter = new SEDMLExporter(bioModel, 1, 1);
        String sedmlString = sedmlExporter.getSEDMLFile(outputDir, false, true);
        XmlUtil.writeXMLStringToFile(sedmlString, String.valueOf(Paths.get(outputDir, vcmlName + ".sedml")), true);

        // libCombine needs native lib
        ResourceUtil.setNativeLibraryDirectory();
        NativeLoader.load("combinej");

        boolean isDeleted = false;
        boolean isCreated;

        try {
            CombineArchive combineArchive = new CombineArchive();

            String[] files;

            // TODO: try-catch if no files
            File dir = new File(outputDir);
            files = dir.list();

            for (String sd : files) {
                if (sd.endsWith(".sedml")) {
                    combineArchive.addFile(
                            Paths.get(outputDir, sd).toString(),
                            sd, // target file name
                            KnownFormats.lookupFormat("sedml"),
                            true // mark file as master
                    );
                } else if (sd.endsWith(".sbml") || sd.endsWith(".xml")) {
                    combineArchive.addFile(
                            Paths.get(outputDir, sd).toString(),
                            sd, // target file name
                            KnownFormats.lookupFormat("sbml"),
                            false // mark file as master
                    );
                }
            }

            combineArchive.addFile(
                    Paths.get(String.valueOf(vcmlFilePath)).toString(),
                    vcmlName + ".vcml",
                    KnownFormats.lookupFormat("vcml"),
                    false
            );


            // writing into combine archive
            String omexPath = Paths.get(outputDir, vcmlName + ".omex").toString();
            File omexFile = new File(omexPath);

            // Deleting file if already exists with same name
            if(omexFile.exists()) {
                omexFile.delete();
            }
            isCreated = combineArchive.writeToFile(omexPath);

            // Removing all other files(like SEDML, XML, SBML) after archiving
            for (String sd : files) {
                // removing
                if (sd.endsWith(".sedml") || sd.endsWith(".sbml") || sd.endsWith("xml") || sd.endsWith("vcml")) {
                    isDeleted = Paths.get(outputDir, sd).toFile().delete();
                }
            }

            if (isDeleted) System.out.println("Removed intermediary files");

        } catch (Exception e) {
            throw new RuntimeException("createZipArchive threw exception: " + e.getMessage());
        }
        return isCreated;
    }
}
