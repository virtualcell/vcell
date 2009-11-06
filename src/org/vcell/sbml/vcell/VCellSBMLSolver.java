package org.vcell.sbml.vcell;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import org.vcell.sbml.SBMLSolver;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.SolverException;
import org.vcell.sbml.SBMLUtils.SBMLUnitParameter;
import org.vcell.sbml.test.SbmlConverter;
import org.vcell.util.Executable;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.math.Variable;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.CVodeFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class VCellSBMLSolver implements SBMLSolver {
	
	private boolean bRoundTrip = false;

	public void close() throws SolverException {
		// TODO Auto-generated method stub
	}

	public String getResultsFileColumnDelimiter() {
		// TODO Auto-generated method stub
		return ",";
	}

	public void open() throws SolverException {
		// TODO Auto-generated method stub

	}

	public File solve(String filePrefix, File outDir, String sbmlFileName, SimSpec testSpec) throws IOException, SolverException, SbmlException {
		try {
		    cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
		        private StringBuffer buffer = new StringBuffer();
		        public void sendMessage(int messageLevel, int messageType) {
		            String message = cbit.util.xml.VCLogger.getDefaultMessage(messageType);
		            sendMessage(messageLevel, messageType, message);	
		        }
		        public void sendMessage(int messageLevel, int messageType, String message) {
		            System.out.println("LOGGER: msgLevel="+messageLevel+", msgType="+messageType+", "+message);
		            if (messageLevel==VCLogger.HIGH_PRIORITY){
		            	throw new RuntimeException("SBML Import Error: "+message);
		            }
		        }
		        public void sendAllMessages() {
		        }
		        public boolean hasMessages() {
		            return false;
		        }
		    };
	
			//    
		    // Instantiate an SBMLImporter to get the speciesUnitsHash - to compute the conversion factor from VC->SB species units.
		    // and import SBML  (sbml->bioModel)
			org.vcell.sbml.vcell.SBMLImporter sbmlImporter = new org.vcell.sbml.vcell.SBMLImporter(sbmlFileName, logger);
			BioModel bioModel = sbmlImporter.getBioModel();
			Hashtable<String, SBMLImporter.SBVCConcentrationUnits> speciesUnitsHash = sbmlImporter.getSpeciesUnitsHash();
			double timeFactor = sbmlImporter.getSBMLTimeUnitsFactor();

		    String vcml_1 = XmlHelper.bioModelToXML(bioModel);
		    SBMLUtils.writeStringToFile(vcml_1, new File(outDir,filePrefix+".vcml").getAbsolutePath(), true);

		    if (bRoundTrip){
			    // Round trip the bioModel (bioModel->sbml->bioModel).
		    	
		    	// save imported "bioModel" as VCML
//			    String vcml_1 = XmlHelper.bioModelToXML(bioModel);
//			    SBMLUtils.writeStringToFile(vcml_1, new File(outDir,filePrefix+".vcml").getAbsolutePath());
			    
			    // export bioModel as sbml and save
			    // String vcml_sbml = cbit.vcell.xml.XmlHelper.exportSBML(bioModel, 2, 1, bioModel.getSimulationContexts(0).getName());
			    // SimulationJob simJob = new SimulationJob(bioModel.getSimulations(bioModel.getSimulationContexts(0))[0], null, 0);
			    String vcml_sbml = cbit.vcell.xml.XmlHelper.exportSBML(bioModel, 2, 1, bioModel.getSimulationContexts(0), null);
			    SBMLUtils.writeStringToFile(vcml_sbml, new File(outDir,filePrefix+".vcml.sbml").getAbsolutePath(), true);
			    
			    // re-import bioModel from exported sbml
			    XMLSource vcml_sbml_Src = new XMLSource(vcml_sbml);
			    BioModel newBioModel = (BioModel)XmlHelper.importSBML(logger, vcml_sbml_Src);
			    String vcml_sbml_vcml = XmlHelper.bioModelToXML(newBioModel);
			    SBMLUtils.writeStringToFile(vcml_sbml_vcml, new File(outDir,filePrefix+".vcml.sbml.vcml").getAbsolutePath(), true);
			    
			    // have rest of code use the round-tripped biomodel
			    bioModel = newBioModel;
		    }
			//
		    // select only Application, generate math, and create a single Simulation.
			//
		    cbit.vcell.mapping.SimulationContext simContext = bioModel.getSimulationContexts(0);
		    cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContext);
		    cbit.vcell.math.MathDescription mathDesc = mathMapping.getMathDescription();
		    simContext.setMathDescription(mathDesc);
		    SimulationVersion simVersion =
		        new SimulationVersion(
		            new KeyValue("100"),
		            "unnamed",
		            null,
		            null,
		            null,
		            null,
		            null,
		            null,
		            null,
		            null);
		    Simulation sim = new Simulation(simVersion, mathDesc);
		    
		    sim.setName("unnamed");
		    // if time factor from SBML is not 1 (i.e., it is not in secs but in minutes or hours), convert endTime to min/hr as : endTime*timeFactor
		   	double endTime = testSpec.getEndTime()*timeFactor;
		    	
		    sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0, endTime));
		    TimeStep timeStep = new TimeStep();
		    sim.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep.getMinimumTimeStep(),timeStep.getDefaultTimeStep(),endTime/10000));
		    sim.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/testSpec.getNumTimeSteps()));
		    sim.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));
		    // sim.getSolverTaskDescription().setErrorTolerance(new cbit.vcell.solver.ErrorTolerance(1e-10, 1e-12));
	
			// Generate .idaInput string
/*			IDAFileWriter idaFileWriter = new IDAFileWriter(sim);
			File idaInputFile = new File(filePathName.replace(".vcml", ".idaInput"));
			PrintWriter idaPW = new java.io.PrintWriter(idaInputFile);
			idaFileWriter.writeInputFile(idaPW);
			idaPW.close();

			// use the idastandalone solver
			File idaOutputFile = new File(filePathName.replace(".vcml", ".ida"));
			Executable executable = new Executable("IDAStandalone " + idaInputFile + " " + idaOutputFile);
			executable.start();
*/
			// Generate .cvodeInput string
			File cvodeFile = new File(outDir,filePrefix+SimDataConstants.CVODEINPUT_DATA_EXTENSION);
			PrintWriter cvodePW = new java.io.PrintWriter(cvodeFile);
			SimulationJob simJob = new SimulationJob(sim, 0, null);
		    CVodeFileWriter cvodeFileWriter = new CVodeFileWriter(cvodePW, simJob);
			cvodeFileWriter.write();
			cvodePW.close();

			// use the cvodeStandalone solver
			File cvodeOutputFile = new File(outDir,filePrefix+SimDataConstants.IDA_DATA_EXTENSION);
			Executable executable = new Executable(new String[]{"SundialsSolverStandalone", cvodeFile.getAbsolutePath(), cvodeOutputFile.getAbsolutePath()});
			executable.start();
	
		// get the result 
			ODESolverResultSet odeSolverResultSet = SbmlConverter.getODESolverResultSet(simJob, cvodeOutputFile.getPath());	
			//
		    // print header
		    //
			File outputFile = new File(outDir,filePrefix+".vcell.csv");
		    java.io.PrintStream outputStream = new java.io.PrintStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(outputFile)));
		    outputStream.print("time");
		    for (int i = 0; i < testSpec.getVarsList().length; i++) {
		        outputStream.print("," + testSpec.getVarsList()[i]);
		    }
		    outputStream.println();
		    //
		    // extract data for time and species
		    // 
		    double[][] data = new double[testSpec.getVarsList().length + 1][];
		    int column = odeSolverResultSet.findColumn("t");
		    data[0] = odeSolverResultSet.extractColumn(column);
		    int origDataLength = data[0].length;
		    for (int i = 0; i < testSpec.getVarsList().length; i++) {
		        column = odeSolverResultSet.findColumn(testSpec.getVarsList()[i]);
		        if (column == -1) {
		            Variable var = simJob.getSimulationSymbolTable().getVariable(testSpec.getVarsList()[i]);
		            data[i + 1] = new double[data[0].length];
		            if (var instanceof cbit.vcell.math.Constant) {
		                double value = ((cbit.vcell.math.Constant) var).getExpression().evaluateConstant();
		                for (int j = 0; j < data[i + 1].length; j++) {
		                    data[i + 1][j] = value;
		                }
		            } else {
		                throw new RuntimeException(
		                    "Did not find " + testSpec.getVarsList()[i] + " in simulation");
		            }
	
		        } else {
		            data[i + 1] = odeSolverResultSet.extractColumn(column);
		        }
		    }
	
		    //
		    // for each time, print row
		    //
		    int index = 0;
		    double[] sampleTimes = new double[testSpec.getNumTimeSteps() + 1];
		    for (int i = 0; i <= testSpec.getNumTimeSteps(); i++) {
		        sampleTimes[i] = endTime * i / testSpec.getNumTimeSteps();
		    }
	
		    for (int i = 0; i < sampleTimes.length; i++) {
		        //
		        // find largest index whose time is not past sample time
		        //
		        while (true) {
		            //
		            // if already at last time point, then if it equals the sampleTime, we're done if it doesn't then we don't have this time point.
		            //
		            if (index == odeSolverResultSet.getRowCount() - 1) {
		                if (data[0][index] == sampleTimes[i]) {
		                    break;
		                } else {
		                    throw new RuntimeException("sampleTime does not match at last time point");
		                }
		            }
		            //
		            // haven't gotten to last time point yet, stop when next time step is past sampleTime.
		            //
		            if (data[0][index + 1] > sampleTimes[i]) {
		                break;
		            }
		            //
		            // sampleTime must be later in our data list.
		            //
		            index++;
		        }
	
		        // if data[0][index] == sampleTime no need to interpolate
		        if (data[0][index] == sampleTimes[i]) {
		        	// if timeFactor is not 1.0, time is not in seconds (mins or hrs); if timeFactor is 60, divide sampleTime/60; if it is 3600, divide sampleTime/3600.
		        	if (timeFactor != 1.0) {
		        		outputStream.print(data[0][index]/timeFactor);
		        	} else {
		        		outputStream.print(data[0][index]);
		        	}
		            for (int j = 0; j < testSpec.getVarsList().length; j++) {
		            	SBMLImporter.SBVCConcentrationUnits spConcUnits = speciesUnitsHash.get(testSpec.getVarsList()[j]);
		            	if (spConcUnits != null) {
		            		VCUnitDefinition sbunits = spConcUnits.getSBConcentrationUnits();
		            		VCUnitDefinition vcunits = spConcUnits.getVCConcentrationUnits();
		            		SBMLUnitParameter unitFactor = SBMLUtils.getConcUnitFactor("spConcParam", vcunits, sbunits);
		                	outputStream.print("," + data[j + 1][index] * unitFactor.getExpression().evaluateConstant()); 		//earlier, hack unitfactor = 0.000001
		            	}
		            }
		            // System.out.println("No interpolation needed!");
		            outputStream.println();
		        } else {
		            // if data[0][index] < sampleTime, must interpolate
		            double fraction = (sampleTimes[i] - data[0][index]) / (data[0][index + 1] - data[0][index]);
		        	// if timeFactor is not 1.0, time is not in seconds (mins or hrs); if timeFactor is 60, divide sampleTime/60; if it is 3600, divide sampleTime/3600.
		        	if (timeFactor != 1.0) {
		        		outputStream.print(sampleTimes[i]/timeFactor);
		        	} else {
		        		outputStream.print(sampleTimes[i]);
		        	}
		            for (int j = 0; j < testSpec.getVarsList().length; j++) {
		                double interpolatedValue = 0.0;
		                double[] speciesVals = null;
		                double[] times = null;
		                
		                //Get the unit factor (VC->SBML units)
		                SBMLUnitParameter unitFactor = null;
		            	SBMLImporter.SBVCConcentrationUnits spConcUnits = speciesUnitsHash.get(testSpec.getVarsList()[j]);
		            	if (spConcUnits != null) {
		            		VCUnitDefinition sbunits = spConcUnits.getSBConcentrationUnits();
		            		VCUnitDefinition vcunits = spConcUnits.getVCConcentrationUnits();
		            		unitFactor = SBMLUtils.getConcUnitFactor("spConcFactor", vcunits, sbunits);
		            	} else {
		            		throw new RuntimeException("Could not convert units from VC - SBML, insufficient information");
		            	}
	
		                // For 3rd order interpolation, index <= origDataLen-3
		                // For 2nd order interpolation, index <= origDataLen-2
		                // For 1st order interpolation, index <= origDataLen-1
	
		                // Currently using 2nd order interpolation
		                if (index == 0) {
		                    // can only do 1st order interpolation
		                    times = new double[] { data[0][index], data[0][index + 1] };
		                    speciesVals = new double[] { data[j + 1][index], data[j + 1][index + 1] };
		                    interpolatedValue = SBMLUtils.taylorInterpolation(sampleTimes[i], times, speciesVals);
		                } else
		                    if (index >= 1 && index <= origDataLength - 3) {
		                        double val_1 = Math.abs(sampleTimes[i] - data[0][index - 1]);
		                        double val_2 = Math.abs(sampleTimes[i] - data[0][index + 2]);
	
		                        if (val_1 < val_2) {
		                            times = new double[] { data[0][index - 1], data[0][index], data[0][index + 1] };
		                            speciesVals =
		                                new double[] {
		                                    data[j + 1][index - 1],
		                                    data[j + 1][index],
		                                    data[j + 1][index + 1] };
		                        } else {
		                            times = new double[] { data[0][index], data[0][index + 1], data[0][index + 2] };
		                            speciesVals =
		                                new double[] {
		                                    data[j + 1][index],
		                                    data[j + 1][index + 1],
		                                    data[j + 1][index + 2] };
		                        }
		                        interpolatedValue = SBMLUtils.taylorInterpolation(sampleTimes[i], times, speciesVals);
		                    } else {
		                        times = new double[] { data[0][index - 1], data[0][index], data[0][index + 1] };
		                        speciesVals =
		                            new double[] {
		                                data[j + 1][index - 1],
		                                data[j + 1][index],
		                                data[j + 1][index + 1] };
		                        interpolatedValue = SBMLUtils.taylorInterpolation(sampleTimes[i], times, speciesVals);
		                    }
	
		                //// Currently using 1st order interpolation
		                //times = new double[] { data[0][index], data[0][index+1] };
		                //speciesVals = new double[] { data[j+1][index], data[j+1][index+1] };
		                //interpolatedValue = taylorInterpolation(sampleTimes[i], times, speciesVals);
	
		                interpolatedValue = interpolatedValue * unitFactor.getExpression().evaluateConstant(); 		//earlier, hack unitfactor = 0.000001 
		                // System.out.println("Sample time: " + sampleTimes[i] + ", between time[" + index + "]=" + data[0][index]+" and time["+(index+1)+"]="+(data[0][index+1])+", interpolated = "+interpolatedValue);
		                outputStream.print("," + interpolatedValue);
		            }
		            outputStream.println();
		        }
		    }
		    outputStream.close();
		    
		    return outputFile;
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new SolverException(e.getMessage());
		}
	}

	public boolean isRoundTrip() {
		return bRoundTrip;
	}

	public void setRoundTrip(boolean roundTrip) {
		bRoundTrip = roundTrip;
	}


}
