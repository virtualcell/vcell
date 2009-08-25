package cbit.vcell.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.MathDescription;
import cbit.vcell.parser.Expression;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class VCMLTest {
	
public static void main(String[] args) {
	// 1. Read in VCML from specified folder; 
	// 2. For non-spatial simContext, get math description, generate new math description using MathMapping
	// 3. Compare the two math descriptions.
	
	if (args.length!=2){
		System.out.println("usage: VCMLTest inputDirectory logFileSpec");
		System.exit(-1);
	}
	File dataDir = new File(args[0]);
	if (!dataDir.exists()){
		throw new RuntimeException("inputDirectory "+dataDir.getAbsolutePath()+" doesn't exist");
	}

	BioModel bioModel = null;
	PrintStream logFilePrintStream = System.out;
	try {
		if (!args[1].equals("-")){
			logFilePrintStream = new java.io.PrintStream(new FileOutputStream(args[1], true), true);
		}

		String directoryForNewVcmlFiles = dataDir.getParent() + "\\tempLes";
		File[] vcmlFiles = dataDir.listFiles();
		for (File vcmlFile : vcmlFiles){
			// String vcmlText = XmlUtil.getXMLString(vcmlFile.getAbsolutePath());
			bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlFile));
			
			SimulationContext[] simContexts = bioModel.getSimulationContexts();
			for (int k = 0; k < simContexts.length; k++) {
				// consider only spatial applications
				int dim = simContexts[k].getGeometry().getDimension();
				if (dim > 0) {
					// original bioModel math description
					// MathDescription mathDesc = simContexts[k].getMathDescription();
					// Get speciesContextSpec and set Velocity (x,y,z) parameters for speciesContextSpecs in Features
					// choose every third species (i%3 == 0) to set velocity parameter.
					SpeciesContextSpec[] scs = simContexts[k].getReactionContext().getSpeciesContextSpecs();
					for (int i = 0; i < scs.length; i++) {
						if (scs[i].getSpeciesContext().getStructure() instanceof Feature) {
							if (i % 3 == 0) {
								switch (dim) {
									case 1 : {
										scs[i].getVelocityXParameter().setExpression(new Expression("0.5"));
										break;
									}
									case 2 : { 
										scs[i].getVelocityXParameter().setExpression(new Expression("1.5"));
										scs[i].getVelocityYParameter().setExpression(new Expression("0.5"));
										break;
									}
									case 3 : { 
										scs[i].getVelocityXParameter().setExpression(new Expression("1.5"));
										scs[i].getVelocityYParameter().setExpression(new Expression("0.5"));
										scs[i].getVelocityZParameter().setExpression(new Expression("0.25"));
										break;
									}
									default : {
										break;
									}
								}
							}
						}
					}
					
					// After altering speciesContextSpecs, regenerate math
					MathDescription altMathDesc = null;
					try {
						altMathDesc = (new MathMapping(simContexts[k])).getMathDescription();
						simContexts[k].setMathDescription(altMathDesc);
						logFilePrintStream.println("Math re-genenerated successfully for simContext '" + simContexts[k].getName() + " (" + dim + "d geometry) in Biomodel : " + bioModel.getName() + "; Version : " + bioModel.getVersion().getDate().toString());
						// System.out.println("\n\nMath re-generated successfully!\n\n");
					} catch (Exception e) {
						logFilePrintStream.println("Biomodel : " + bioModel.getName() + ";\t (" + bioModel.getVersion().getDate() + ");\t SimContextName : '" + simContexts[k].getName() + "' : Error generating MathDesciption." + e.getMessage());					
					}
					
					// compare original math desc with altBiomodel math description.
//					if (mathDesc.compareEqual(altMathDesc)) {
//						logFilePrintStream.println("MathDescriptions for simContext '" + simContexts[k].getName() + " (" + dim + "d geometry) in Biomodel : " + bioModel.getName() + "; Version : " + bioModel.getVersion().getDate().toString() + " are equivalent");
//					} else {
//						logFilePrintStream.println("MathDescriptions for simContext '" + simContexts[k].getName() + " (" + dim + "d geometry) in Biomodel : " + bioModel.getName() + "; Version : " + bioModel.getVersion().getDate().toString() + " are NOT equivalent");
//					}
				}	// end - 'if' dim > 0
			}	// end - 'for' simContexts

			// after regenerating math for all spatial simContexts, write out new vcml file for bioModel (in different directory)
			File biomodelFile = new File(directoryForNewVcmlFiles, "alt__"+bioModel.getVersion().getName()+".vcml");
			XmlUtil.writeXMLStringToFile(XmlHelper.bioModelToXML(bioModel), biomodelFile.getAbsolutePath(),true);
		}
	} catch (Exception e) {
		// e.printStackTrace(System.out);
		logFilePrintStream.println("Biomodel : " + bioModel.getName() + ";\t (" + bioModel.getVersion().getDate() + ") : Error generating MathDesciption.");		
	}

	logFilePrintStream.println("\n\n!!!!!! VCMLTest completed successfully !!!!!!!!!\n\n");
	System.err.println("\n\nVCMLTest completed successfully!\n\n");
}

}
