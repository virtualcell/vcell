package org.vcell.cli.vcml;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.util.NativeLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.sbml.libcombine.CombineArchive;
import org.sbml.libcombine.KnownFormats;
import org.vcell.sedml.SEDMLExporter;

import java.io.File;
import java.nio.file.Paths;

public class VcmlOmexConversion {

    public static void vcmlToSedmlSbml(String vcmlPath, String xmlPath) throws Exception {
        File vcmlFilePath = new File(vcmlPath);
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlFilePath));
        bioModel.refreshDependencies();

        // NOTE: SEDML exporter exports both SEDML as well as required SBML
        SEDMLExporter sedmlExporter = new SEDMLExporter(bioModel, 1, 1);
        String sedmlString = sedmlExporter.getSEDMLFile(String.valueOf(Paths.get(System.getProperty("user.dir"), xmlPath)));
        XmlUtil.writeXMLStringToFile(sedmlString, String.valueOf(Paths.get(System.getProperty("user.dir"), xmlPath, xmlPath + ".sedml")), true);
        createCliOmexArchive(String.valueOf(Paths.get(System.getProperty("user.dir"), xmlPath)), "try", vcmlPath);

    }

    public static void createCliOmexArchive(String srcFolder, String sFileName, String vcmlPath) {
        ResourceUtil.setNativeLibraryDirectory();
        NativeLoader.load("combinej");
        try {
            CombineArchive archive = new CombineArchive();

            String[] files;

            File dir = new File(srcFolder);
            files = dir.list();

            for (String sd : files) {
                if (sd.endsWith(".sedml")) {
                    archive.addFile(
                            Paths.get(srcFolder, sd).toString(),
                            sd, // target file name
                            KnownFormats.lookupFormat("sedml"),
                            true // mark file as master
                    );
                } else {
                    archive.addFile(
                            Paths.get(srcFolder, sd).toString(),
                            sd, // target file name
                            KnownFormats.lookupFormat("sbml"),
                            false // mark file as master
                    );
                }
            }

            archive.addFile(
                    Paths.get(vcmlPath).toString(),
                    sFileName + ".vcml",
                    KnownFormats.lookupFormat("vcml"),
                    false
            );


            archive.writeToFile(Paths.get(srcFolder, sFileName + ".omex").toString());

            // Removing files after archiving
            boolean isDeleted = false;
            for (String sd : files) {
                isDeleted = Paths.get(srcFolder, sd).toFile().delete();
            }

        } catch (Exception e) {
            throw new RuntimeException("createZipArchive threw exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        vcmlToSedmlSbml("/Users/akhilteja/projects/virtualCell/vcell/sample_omex_files/_00_omex_test_cvode.vcml", "model");
    }


}
