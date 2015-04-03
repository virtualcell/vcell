package org.vcell.sbml;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.vcell.sbml.test.VCellSBMLSolver;
import org.vcell.sbml.vcell.StructureSizeSolver;
import org.vcell.util.Executable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;
import org.vcell.util.logging.Logging;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Variable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.Structure;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.CVodeFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

/**
 * Command line utility to convert between SBML and VCML
 * Optionally runs SundialsStandalone with default settings
 */
public class SbmlVcmlConverter {
	private static double endTime = 10.0;
	private static int numTimeSteps = 1000;

	/**
	 * @param args -import or -export
	 */
public static void main(String[] args) {
	Logging.init( );
	ResourceUtil.setNativeLibraryDirectory();
	if (args.length < 2 || args.length > 3) {
		System.out.println("Usage:\n\t -export path_of_input_file\n\tOR\n\t -import path_of_input_file [-simulate]" );
        System.exit(1);
	} 
	if (args.length > 2 && args[0].equals("-export")) {
		System.out.println("Export cannot have arguments other than input file.");
		System.out.println("Usage:\n\t -export path_of_input_file\n\tOR\n\t-import path_of_input_file [-simulate]" );
        System.exit(1);
	}

	try {
		String pathName = args[1];
		// Read xml file (Sbml or Vcml) into stringbuffer, pass the string into VCell's importer/exporter
		String xmlString = getXMLString(pathName);
		if (args[0].equals("-import")) {
			if (args.length > 3 || (args.length == 3 && !args[2].equals("-simulate") )) {
				System.out.println(args[2] + " : Unknown arguments for import; please check and re-run import command");
				System.out.println("Usage:\n\t -import path_of_input_file [-simulate]" );
		        System.exit(1);
			}
			boolean bSimulate = false;
			if (args.length == 3 && args[2].equals("-simulate")) {
				bSimulate = true;
			}
			// Create a default VCLogger - SBMLImporter needs it
	        cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
	        	
	        	
	            @Override
				public void sendMessage(Priority p, ErrorType et, String message) {
	                System.err.println("LOGGER: msgLevel="+p+", msgType="+et+", "+message);
	                if (p == VCLogger.Priority.HighPriority) {
	                	throw new RuntimeException("Import failed : " + message);
	                }
	            }
	            public void sendAllMessages() {
	            }
	            public boolean hasMessages() {
	                return false;
	            }
	        };

	        // invoke SBMLImporter, which returns a Biomodel, convert that to VCML using XMLHelper
	        try {
	        	// import SBML model into VCML, store VCML string in 'fileName.vcml'
			    // Instantiate an SBMLImporter to get the speciesUnitsHash - to compute the conversion factor from VC->SB species units.
			    // and import SBML  (sbml->bioModel)
				org.vcell.sbml.vcell.SBMLImporter sbmlImporter = new org.vcell.sbml.vcell.SBMLImporter(pathName, logger, false);
				BioModel bioModel = sbmlImporter.getBioModel();
//				Hashtable<String, SBMLImporter.SBVCConcentrationUnits> speciesUnitsHash = sbmlImporter.getSpeciesUnitsHash();
//				double timeFactor = sbmlImporter.getSBMLTimeUnitsFactor();

				// convert biomodel to vcml and save to file.
				String vcmlString = XmlHelper.bioModelToXML(bioModel);
				String fileExtensionStr;
				if (pathName.endsWith(".xml")) {
					fileExtensionStr = ".xml";
				} else if (pathName.endsWith(".sbml")) {
					fileExtensionStr = ".sbml";
				} else {
					throw new RuntimeException("Unknown file extension for SBML file name; Exiting SbmlConverter.");
				}
				String vcmlFileName = pathName.replace(fileExtensionStr, ".vcml");
				File vcmlFile = new File(vcmlFileName);
				XmlUtil.writeXMLStringToFile(vcmlString, vcmlFile.getAbsolutePath(), true);

				// If user doesn't choose to simulate, you are done.
				if (!bSimulate) {
					return;
				}
				// Generate math for lone simContext
				SimulationContext simContext = (SimulationContext)bioModel.getSimulationContext(0);  
				MathDescription mathDesc = simContext.createNewMathMapping().getMathDescription();
				simContext.setMathDescription(mathDesc);

				// Create basic simulation, with IDA solver (set in solve method) and other defaults, and end time 'Te'
				org.vcell.util.document.SimulationVersion simVersion = new org.vcell.util.document.SimulationVersion(
						new KeyValue("12345"),
						"name",
						new org.vcell.util.document.User("user",new KeyValue("123")),
						new org.vcell.util.document.GroupAccessNone(),
						null, // versionBranchPointRef
						new java.math.BigDecimal(1.0), // branchID
						new java.util.Date(),
						org.vcell.util.document.VersionFlag.Archived,
						"",
						null);

				Simulation sim1 = new Simulation(simVersion, simContext.getMathDescription());
				simContext.addSimulation(sim1);
				
				sim1.setName("sim1");
		        // using a default end time of 10.0 secs, and a forcing default output time step of 0.01.
		        // If user wants anything different, user can modify the .idaInput file and re-run simulation separately.
//				double newEndTime = endTime * timeFactor;
				double newEndTime = endTime;
		        sim1.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0, newEndTime));
		        TimeStep timeStep_1 = new TimeStep();
		        sim1.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_1.getMinimumTimeStep(),timeStep_1.getDefaultTimeStep(),newEndTime/10000));
		        sim1.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec( (newEndTime-0)/numTimeSteps) );
		        sim1.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));
				
				// save the new vcml with generated math and new sim in file named : fileName_IDA_simulation.vcml
				String newVcmlString = XmlHelper.bioModelToXML(bioModel);
				String newVcmlFileName = vcmlFile.getPath().replace(".vcml", "_IDA_simulation.vcml");
				File newVcmlFile = new File(newVcmlFileName);
				XmlUtil.writeXMLStringToFile(newVcmlString, newVcmlFile.getAbsolutePath(), true);
				
		        // Solve simulation, which also generates and saves the .idainput file and .csv
				// SimSpec to get vars to solve and output in .csv
				SimSpec simSpec = SimSpec.fromSBML(xmlString);
				solveSimulation(new SimulationJob(sim1, 0, null), vcmlFile.getPath(), simSpec /* speciesUnitsHash, timeFactor, kMole */);
	        } catch (Exception e) {
	        	e.printStackTrace(System.err);
	        	// create a 'blank' vcml file with only a Biomodel element with name of model 
	        	// and an annotation saying why it failed.
	        	try {
					String fileExtensionStr;
					if (pathName.endsWith(".xml")) {
						fileExtensionStr = ".xml";
					} else if (pathName.endsWith(".sbml")) {
						fileExtensionStr = ".sbml";
					} else {
						throw new RuntimeException("Unknown file extension for SBML file name; Exiting SbmlConverter.");
					}

					String dummyVcmlFileName = pathName.replace(fileExtensionStr, ".vcml");
					File dummyVcmlFile = new File(dummyVcmlFileName);
		        	BioModel dummy_biomodel = new BioModel(null);
		        	String dummyName = dummyVcmlFile.getName().substring(0, dummyVcmlFile.getName().indexOf(".vcml"));
		        	dummy_biomodel.setName(dummyName);
		        	dummy_biomodel.setDescription("SBML Model could not be automatically converted to VCML : " + e.getMessage());
					String vcmlString = XmlHelper.bioModelToXML(dummy_biomodel);
					XmlUtil.writeXMLStringToFile(vcmlString, dummyVcmlFile.getAbsolutePath(), true);
	        	} catch (Exception e1) {
	        		e.printStackTrace(System.err);
	        	}
	        }
		} else if (args[0].equals("-export")) {
	        try {
				BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(xmlString));
				for (int i = 0; i < bioModel.getSimulationContexts().length; i++) {
					SimulationContext simContext = bioModel.getSimulationContext(i);
					if (simContext.getGeometry().getDimension() == 0 && !simContext.isStoch()) {
						// check if structure sizes are set. If not, get a structure from the model, and set its size 
						// (thro' the structureMappings in the geometry of the simContext); invoke the structureSizeEvaluator 
						// to compute and set the sizes of the remaining structures.
						
						if (!simContext.getGeometryContext().isAllSizeSpecifiedPositive()) {
							Structure structure = simContext.getModel().getStructure(0);
							double structureSize = 1.0;
							StructureMapping structMapping = simContext.getGeometryContext().getStructureMapping(structure); 
							StructureSizeSolver.updateAbsoluteStructureSizes(simContext, structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());
						}
						
						// Export the application itself, with default overrides
						String sbmlString = XmlHelper.exportSBML(bioModel, 2, 4, 0, false, simContext, null);
						String filePath = pathName.substring(0, pathName.lastIndexOf("\\")+1);
						String sbmlFileName = TokenMangler.mangleToSName(bioModel.getName() + "_" + i);
						File sbmlFile = new File(filePath + sbmlFileName + ".xml");
						XmlUtil.writeXMLStringToFile(sbmlString, sbmlFile.getAbsolutePath(), true);
						
						// Now export each simulation in the application that has overrides
						Simulation[] simulations = bioModel.getSimulations(simContext);
						for (int j = 0; j < simulations.length; j++) {
							if (simulations[j].getMathOverrides().hasOverrides()) {
								// Check for parameter scan and create simJob to pass into exporter
								for (int k = 0; k < simulations[j].getScanCount(); k++) {
									SimulationJob simJob = new SimulationJob(simulations[j], k, null);
									sbmlString = XmlHelper.exportSBML(bioModel, 2, 4, 0, false, simContext, simJob);
									String fileName = TokenMangler.mangleToSName(sbmlFileName + "_" + j + "_" + k); 
									sbmlFile = new File(filePath + fileName + ".xml");
									XmlUtil.writeXMLStringToFile(sbmlString, sbmlFile.getAbsolutePath(), true);
								}
							}
						}
					}
				}
				System.out.println("Successfully translated model : " + pathName);
	        } catch (Exception e) {
	        	e.printStackTrace(System.err);
	        }				
		}
		System.exit(0);
	}catch (IOException e) {
		e.printStackTrace(System.err);
		System.exit(1);
	}
}

public static String getXMLString(String fileName) throws IOException {

	BufferedReader br = new BufferedReader(new FileReader(fileName));
	String temp;
	StringBuffer buf = new StringBuffer();
	while ((temp = br.readLine()) != null) {
		buf.append(temp);
		buf.append("\n");
	}
	
	br.close();
	return buf.toString();
}

/**
 * solves the simulation that is passed in throught the simulation job. 
 */
private static void solveSimulation(SimulationJob simJob, String filePathName,  SimSpec argSimSpec /* Hashtable argSpUnitsHash, double argTimeFactor, Model.ReservedSymbol kMoleSymbol*/) {
	ODESolverResultSet odeSolverResultSet = null;
	try {

/*		// Generate .idaInput string		
		File idaInputFile = new File(filePathName.replace(".vcml", ".idaInput"));
		PrintWriter idaPW = new java.io.PrintWriter(idaInputFile);
		IDAFileWriter idaFileWriter = new IDAFileWriter(idaPW, simJob);
		idaFileWriter.write();
		idaPW.close();

		// use the SundialsStandaloneSolver
		File idaOutputFile = new File(filePathName.replace(".vcml", ".ida"));
		Executable executable = new Executable(new String[]{"SundialsSolverStandalone", idaInputFile.getAbsolutePath(), idaOutputFile.getAbsolutePath()});
		executable.start();
*/
		// Generate .cvodeInput string
		File cvodeInputFile = new File(filePathName.replace(".vcml", ".cvodeInput"));
		PrintWriter cvodePW = new java.io.PrintWriter(cvodeInputFile);
		SimulationTask simTask = new SimulationTask(simJob, 0);

		CVodeFileWriter cvodeFileWriter = new CVodeFileWriter(cvodePW, simTask);
		cvodeFileWriter.write();
		cvodePW.close();

		// use the cvodeStandalone solver
		// use the SundialsStandaloneSolver
		File cvodeOutputFile = new File(filePathName.replace(".vcml", ".ida"));
		/*
		Executable executable = new Executable(new String[]{"d:/workspace/VCell_5.3_VCell/SundialsSolverStandalone_x64", cvodeInputFile.getAbsolutePath(), cvodeOutputFile.getAbsolutePath()});
		executable.start();
		*/
		// use the cvodeStandalone solver
		// use the SundialsStandaloneSolver
		File exes[] = SolverUtilities.getExes(SolverDescription.CombinedSundials);
		assert exes.length == 1 : "one and only one smoldyn solver expected";
		File sundialsExe = exes[0];
		Executable executable = new Executable(new String[]{sundialsExe.getAbsolutePath(), cvodeInputFile.getAbsolutePath(), cvodeOutputFile.getAbsolutePath()});
		executable.start();


		// get the result 
		odeSolverResultSet = VCellSBMLSolver.getODESolverResultSet(simJob, cvodeOutputFile.getPath());

	} catch (Exception e) {
        e.printStackTrace(System.out);
        throw new RuntimeException("Error running NativeIDA solver : " + e.getMessage());
	}

	// now write out the results into CSV file
	try {
		if (odeSolverResultSet != null) {
			File csvFile = new File(filePathName.replace(".vcml", ".csv"));
			PrintStream outputStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(csvFile)));
			outputStream.print("time");
			for (int i = 0; i < argSimSpec.getVarsList().length; i++) {
				outputStream.print("," + argSimSpec.getVarsList()[i]);
			}
			outputStream.println();
		
			// extract data for time and species
		    double[][] data = new double[argSimSpec.getVarsList().length + 1][];
		    int column = odeSolverResultSet.findColumn("t");
		    data[0] = odeSolverResultSet.extractColumn(column);
		    int origDataLength = data[0].length;
		    for (int i = 0; i < argSimSpec.getVarsList().length; i++) {
		        column = odeSolverResultSet.findColumn(argSimSpec.getVarsList()[i]);
		        if (column == -1) {
		            Variable var = simJob.getSimulationSymbolTable().getVariable(argSimSpec.getVarsList()[i]);
		            data[i + 1] = new double[data[0].length];
		            if (var instanceof cbit.vcell.math.Constant) {
		                double value = ((cbit.vcell.math.Constant) var).getExpression().evaluateConstant();
		                for (int j = 0; j < data[i + 1].length; j++) {
		                    data[i + 1][j] = value;
		                }
		            } else {
		                throw new RuntimeException(
		                    "Did not find " + argSimSpec.getVarsList()[i] + " in simulation");
		            }
		        } else {
		            data[i + 1] = odeSolverResultSet.extractColumn(column);
		        }
		    }
		
			double endTime = (simJob.getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime());
			// for each time, print row
		    int index = 0;
			double[] sampleTimes = new double[numTimeSteps + 1];
			for (int i = 0; i <= numTimeSteps; i++) {
				sampleTimes[i] = endTime * i / numTimeSteps;
			}

			for (int i = 0; i < sampleTimes.length; i++) {
		        // find largest index whose time is not past sample time
		        while (true) {
		            // if already at last time point, then if it equals the sampleTime, we're done if it doesn't then we don't have this time point.
		            if (index == odeSolverResultSet.getRowCount() - 1) {
		            	if (data[0][index] == sampleTimes[i]) {
		            		break;
			            } else {
			            	throw new RuntimeException("sampleTime does not match at last time point");
			            }
			        }
		            // haven't gotten to last time point yet, stop when next time step is past sampleTime.
		            if (data[0][index + 1] > sampleTimes[i]) {
		                break;
		            }
		            // sampleTime must be later in our data list.
		            index++;
		        }
		
		        // if data[0][index] == sampleTime no need to interpolate
			    // if (data[0][index] == sampleTimes[i]) {
		        if (Math.abs(data[0][index]-sampleTimes[i]) < 1e-12) {
		        	// if timeFactor is not 1.0, time is not in seconds (mins or hrs); if timeFactor is 60, divide sampleTime/60; if it is 3600, divide sampleTime/3600.
//		        	if (argTimeFactor != 1.0) {
//		        		outputStream.print(data[0][index]/argTimeFactor);
//		        	} else {
		        		outputStream.print(data[0][index]);
//		        	}
		            for (int j = 0; j < argSimSpec.getVarsList().length; j++) {
//		            	SBMLImporter.SBVCConcentrationUnits spConcUnits = (SBMLImporter.SBVCConcentrationUnits)argSpUnitsHash.get(argSimSpec.getVarsList()[j]);
//		            	if (spConcUnits != null) {
//		            		VCUnitDefinition sbunits = spConcUnits.getSBConcentrationUnits();
//		            		VCUnitDefinition vcunits = spConcUnits.getVCConcentrationUnits();
//		            		SBMLUnitParameter unitFactor = SBMLUtils.getConcUnitFactor("spConcParam", vcunits, sbunits, kMoleSymbol);
//		                	outputStream.print("," + data[j + 1][index] * unitFactor.getExpression().evaluateConstant()); 		//earlier, hack unitfactor = 0.000001
		                	outputStream.print("," + data[j + 1][index]); 		//earlier, hack unitfactor = 0.000001
		            	}
//		            }
		            outputStream.println();
		        } else {
		            // if data[0][index] < sampleTime, must interpolate
		            double fraction = (sampleTimes[i] - data[0][index]) / (data[0][index + 1] - data[0][index]);
		        	// if argTimeFactor is not 1.0, time is not in seconds (mins or hrs); if argTimeFactor is 60, divide sampleTime/60; if it is 3600, divide sampleTime/3600.
//		        	if (argTimeFactor != 1.0) {
//		        		outputStream.print(sampleTimes[i]/argTimeFactor);
//		        	} else {
		        		outputStream.print(sampleTimes[i]);
//		        	}
		            for (int j = 0; j < argSimSpec.getVarsList().length; j++) {
		                double interpolatedValue = 0.0;
		                double[] speciesVals = null;
		                double[] times = null;
			                
		                //Get the unit factor (VC->SBML units)
//		                SBMLUnitParameter unitFactor = null;
//		            	SBMLImporter.SBVCConcentrationUnits spConcUnits = (SBMLImporter.SBVCConcentrationUnits)argSpUnitsHash.get(argSimSpec.getVarsList()[j]);
//		            	if (spConcUnits != null) {
//		            		VCUnitDefinition sbunits = spConcUnits.getSBConcentrationUnits();
//		            		VCUnitDefinition vcunits = spConcUnits.getVCConcentrationUnits();
//		            		unitFactor = SBMLUtils.getConcUnitFactor("spConcFactor", vcunits, sbunits, kMoleSymbol);
//		            	} else {
//		            		throw new RuntimeException("Could not convert units from VC - SBML, insufficient information");
//		            	}
		
		                // For 3rd order interpolation, index <= origDataLen-3
		                // For 2nd order interpolation, index <= origDataLen-2
		                // For 1st order interpolation, index <= origDataLen-1
		
		                // Currently using 2nd order interpolation
		                if (index == 0) {
		                    // can only do 1st order interpolation
		                    times = new double[] { data[0][index], data[0][index + 1] };
		                    speciesVals = new double[] { data[j + 1][index], data[j + 1][index + 1] };
		                    interpolatedValue = MathTestingUtilities.taylorInterpolation(sampleTimes[i], times, speciesVals);
		                } else {
		                    if (index >= 1 && index <= origDataLength - 3) {
		                        double val_1 = Math.abs(sampleTimes[i] - data[0][index - 1]);
		                        double val_2 = Math.abs(sampleTimes[i] - data[0][index + 2]);
		
		                        if (val_1 < val_2) {
		                            times = new double[] { data[0][index - 1], data[0][index], data[0][index + 1] };
		                            speciesVals = new double[] {data[j + 1][index - 1], data[j + 1][index], data[j + 1][index + 1] };
		                        } else {
		                            times = new double[] { data[0][index], data[0][index + 1], data[0][index + 2] };
		                            speciesVals = new double[] {data[j + 1][index], data[j + 1][index + 1], data[j + 1][index + 2] };
			                    }
			                    interpolatedValue = MathTestingUtilities.taylorInterpolation(sampleTimes[i], times, speciesVals);
			                } else {
			                	times = new double[] { data[0][index - 1], data[0][index], data[0][index + 1] };
			                    speciesVals = new double[] {data[j + 1][index - 1], data[j + 1][index], data[j + 1][index + 1] };
		                        interpolatedValue = MathTestingUtilities.taylorInterpolation(sampleTimes[i], times, speciesVals);
		                    }
		                }	// end if-else - calc of interpolated value
//		                interpolatedValue = interpolatedValue * unitFactor.getExpression().evaluateConstant(); 		
		                outputStream.print("," + interpolatedValue);
		            }	// end for - sp values for each time point (row)
		            outputStream.println();
		        }
		    }
		    outputStream.close();
		} else {
			throw new RuntimeException("Result set was null, could not write to CSV file");
		}
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Errors saving results to CSV file" + e.getMessage());
	}
}

}

