package org.vcell.cli.vcml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.vcell.sbml.vcell.SBMLExporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class VcmlOmexConversion {

    public static void vcmlToBiomodel(String vcmlPath, String xmlPath) throws Exception {
        String sbmlString = null;
        File vcmlFilePath = new File(vcmlPath);

        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlFilePath));

        bioModel.refreshDependencies();

        int simContextLength = bioModel.getNumSimulations();
        for (int i = 0; i < simContextLength; i++) {
            System.out.println();
            SimulationContext sc2 = bioModel.getSimulationContext(i);
            boolean isSpatial = sc2.getGeometry().getDimension()>0;
            SBMLExporter exporter = new SBMLExporter(bioModel,3,1,isSpatial);
            sc2.refreshMathDescription(null, SimulationContext.NetworkGenerationRequirements.ComputeFullNoTimeout);
            exporter.setSelectedSimContext(sc2);
            sbmlString = exporter.getSBMLString();
            try {
                PrintWriter out = new PrintWriter(new File(String.valueOf(Paths.get(System.getProperty("user.dir"),xmlPath))));
                out.print(sbmlString);
                out.flush();
            } catch (FileNotFoundException e) {
                System.err.println("Unable to find path, failed with err: " + e.getMessage());
            }
        }

    }

    public static void main(String[] args) throws Exception {
        vcmlToBiomodel("/Users/akhilteja/projects/virtualCell/vcell/sample_omex_files/_00_omex_test_cvode.vcml", "sbml.xml");
    }


}
