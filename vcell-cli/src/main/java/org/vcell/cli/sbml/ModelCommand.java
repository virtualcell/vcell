package org.vcell.cli.sbml;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.vcell.cli.vcml.ModelFormat;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(name = "model", description = "translate between a BioModel application and an SBML model")
public class ModelCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(ModelCommand.class);

    @Option(names = { "-i", "--inputFile" }, description = "input file  (xml model file)")
    private File inputFile;

    @Option(names = { "-a", "--app-index" }, defaultValue = "0", description = "index of BioModel application (0..N-1), defaults to 0")
    private int appIndex = 0;

    @Option(names = { "-o", "--outputFile"}, description = "output file  (xml model file)")
    private File outputFile;

    @Option(names = { "-v", "--validate"}, defaultValue = "false", description = "perform round-trip validation for math equivalence")
    private boolean roundTripValidation = false;

    @Option(names = {"-f", "--format"}, defaultValue = "SBML", description = "output format (SBML or VCML)")
    private ModelFormat format = ModelFormat.SBML;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        try {

            if (inputFile == null || !inputFile.isFile() || !inputFile.exists()) {
                RuntimeException e = new RuntimeException("inputFile not found or not a directory "+inputFile);
                logger.error(e.getMessage(), e);
                throw e;
            }
            if (format == ModelFormat.VCML){
                File sbmlInputFile = inputFile;
                MemoryLogger logger = new MemoryLogger();
                boolean bValidateSBML = true;
                SBMLImporter importer = new SBMLImporter(sbmlInputFile.getAbsolutePath(), logger, bValidateSBML);
                BioModel bioModel = importer.getBioModel();
                Files.write(outputFile.toPath(), XmlHelper.bioModelToXML(bioModel, false).getBytes(StandardCharsets.UTF_8));
            } else {
                BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(inputFile));
                if (bioModel.getNumSimulationContexts() > appIndex) {
                    SimulationContext application = bioModel.getSimulationContext(appIndex);
                    boolean isSpatial = application.getGeometry().getDimension() > 0;
                    String resultString = XmlHelper.exportSBML(bioModel, 3, 2, 0, isSpatial, application, null, roundTripValidation);
                    XmlUtil.writeXMLStringToFile(resultString, outputFile.getAbsolutePath(), true);
                }
            }
            return 0;
        } catch (Exception e) {
            logger.error("Exception encountered while processing CLI Model request");
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
