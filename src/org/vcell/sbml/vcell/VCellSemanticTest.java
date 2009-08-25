package org.vcell.sbml.vcell;
import java.io.File;

import org.jdom.Element;
import org.vcell.sbml.SimSpec;

import cbit.util.xml.XmlUtil;

/**
 * Driver class for the SBML test suite.
 * Creation date: (9/23/2004 2:22:47 PM)
 * @author: Jim Schaff
 */
public class VCellSemanticTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
    try {
        if (args.length < 6) {
            System.out.println("Insufficient number of arguments: " + args.length);
            for (int i = 0; i < args.length; i++) {
                System.out.println(args[i]);
            }
            System.out.println(
                "usage: VCellSemanticTest sbmlfile endTime numTimeSteps outputfile tempdir species1 species2 ...");
            System.exit(1);
        }
        org.vcell.util.PropertyLoader.loadProperties();
        java.io.File sbmlFile = new java.io.File(args[0]);
        double endTime = Double.parseDouble(args[1]);
        int numTimeSteps = Integer.parseInt(args[2]);
        java.io.File outputFile = new java.io.File(args[3]);
        java.io.File tempDir = new java.io.File(args[4]); // ignored

        //
        // get list of species in order of output
        //
        String speciesNames[] = new String[args.length - 5];
        for (int i = 0; i < args.length - 5; i++) {
            speciesNames[i] = args[i + 5];
        }
        Element sbmlroot = XmlUtil.readXML(sbmlFile).getRootElement();
        String sbmlString = XmlUtil.xmlToString(sbmlroot);
        VCellSBMLSolver vcellSBMLSolver = new VCellSBMLSolver();
        File solverOutputFile = vcellSBMLSolver.solve(outputFile.getName(), outputFile.getParentFile(), sbmlString, new SimSpec(speciesNames,endTime,numTimeSteps));
        if (!solverOutputFile.equals(outputFile)){
        	solverOutputFile.renameTo(outputFile);
        }
    } catch (Throwable e) {
        e.printStackTrace(System.out);
        System.exit(1);
    }

    System.exit(0);
}

}