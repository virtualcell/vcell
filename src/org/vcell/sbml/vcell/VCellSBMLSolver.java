package org.vcell.sbml.vcell;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import org.vcell.sbml.SBMLSolver;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.SolverException;
import org.vcell.sbml.SBMLUtils.SBMLUnitParameter;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.units.VCUnitDefinition;
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

	public File solve(String filePrefix, File outDir, String sbmlString, SimSpec testSpec) throws IOException, SolverException, SbmlException {
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
			org.vcell.sbml.vcell.SBMLImporter sbmlImporter = new org.vcell.sbml.vcell.SBMLImporter(sbmlString, logger);
			BioModel bioModel = sbmlImporter.getBioModel();
			Hashtable<String, SBMLImporter.SBVCConcentrationUnits> speciesUnitsHash = sbmlImporter.getSpeciesUnitsHash();
			double timeFactor = sbmlImporter.getSBMLTimeUnitsFactor();
	
		    if (bRoundTrip){
			    // Round trip the bioModel (bioModel->sbml->bioModel).
		    	
		    	// save imported "bioModel" as VCML
			    String vcml_1 = XmlHelper.bioModelToXML(bioModel);
			    SBMLUtils.writeStringToFile(vcml_1, new File(outDir,filePrefix+".vcml").getAbsolutePath());
			    
			    // export bioModel as sbml and save
			    String vcml_sbml = cbit.vcell.xml.XmlHelper.exportSBML(bioModel, 2, 1, bioModel.getSimulationContexts(0).getName());
			    SBMLUtils.writeStringToFile(vcml_sbml, new File(outDir,filePrefix+".vcml.sbml").getAbsolutePath());
			    
			    // re-import bioModel from exported sbml
			    BioModel newBioModel = (BioModel)XmlHelper.importSBML(logger, vcml_sbml);
			    String vcml_sbml_vcml = XmlHelper.bioModelToXML(newBioModel);
			    SBMLUtils.writeStringToFile(vcml_sbml_vcml, new File(outDir,filePrefix+".vcml.sbml.vcml").getAbsolutePath());
			    
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
		    cbit.sql.SimulationVersion simVersion =
		        new cbit.sql.SimulationVersion(
		            new cbit.sql.KeyValue("100"),
		            "unnamed",
		            null,
		            null,
		            null,
		            null,
		            null,
		            null,
		            null,
		            null);
		    cbit.vcell.solver.Simulation sim = new cbit.vcell.solver.Simulation(simVersion, mathDesc);
		    sim.setName("unnamed");
		    // if time factor from SBML is not 1 (i.e., it is not in secs but in minutes or hours), convert endTime to min/hr as : endTime*timeFactor
		   	double endTime = testSpec.getEndTime()*timeFactor;
		    	
		    sim.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0, endTime));
		    cbit.vcell.solver.TimeStep timeStep = new cbit.vcell.solver.TimeStep();
		    sim.getSolverTaskDescription().setTimeStep(new cbit.vcell.solver.TimeStep(timeStep.getMinimumTimeStep(),timeStep.getDefaultTimeStep(),endTime/10000));
		    sim.getSolverTaskDescription().setOutputTimeSpec(new cbit.vcell.solver.UniformOutputTimeSpec((endTime-0)/testSpec.getNumTimeSteps()));
		    sim.getSolverTaskDescription().setErrorTolerance(new cbit.vcell.solver.ErrorTolerance(1e-9, 1e-9));
	//		    sim.getSolverTaskDescription().setErrorTolerance(new cbit.vcell.solver.ErrorTolerance(1e-10, 1e-12));
	
		    //        
			// solve simulation - USING NativeIDASolver ....
			//
			cbit.vcell.solver.ode.IDAFileWriter idaFileWriter = new cbit.vcell.solver.ode.IDAFileWriter(sim);
			idaFileWriter.initialize();
			java.io.StringWriter stringWriter = new java.io.StringWriter();
			idaFileWriter.writeInputFile(new java.io.PrintWriter(stringWriter,true));
			stringWriter.close();
			StringBuffer buffer = stringWriter.getBuffer();
			String idaInputString = buffer.toString();
	
			final cbit.vcell.solvers.NativeIDASolver nativeIDASolver = new cbit.vcell.solvers.NativeIDASolver();
			System.out.println(idaInputString);
			cbit.vcell.util.RowColumnResultSet rcResultSet = null;
			try {
				rcResultSet = nativeIDASolver.solve(idaInputString);
			}catch (Exception e){
				e.printStackTrace(System.out);
				throw new RuntimeException("NativeIDASolver: "+e.getMessage());
			}
	
			//
			// get simulation results - copy from RowColumnResultSet into OdeSolverResultSet
			//
			
			cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet = new cbit.vcell.solver.ode.ODESolverResultSet();
			for (int i = 0; i < rcResultSet.getDataColumnCount(); i++){
				odeSolverResultSet.addDataColumn(new cbit.vcell.solver.ode.ODESolverResultSetColumnDescription(rcResultSet.getColumnDescriptions(i).getName()));
			}
			for (int i = 0; i < rcResultSet.getRowCount(); i++){
				odeSolverResultSet.addRow(rcResultSet.getRow(i));
			}
			//
			// add appropriate Function columns to result set
			//
			cbit.vcell.math.Function functions[] = sim.getFunctions();
			for (int i = 0; i < functions.length; i++){
				if (cbit.vcell.solvers.AbstractSolver.isFunctionSaved(functions[i])){
					Expression exp1 = new Expression(functions[i].getExpression());
					try {
						exp1 = sim.substituteFunctions(exp1);
					} catch (cbit.vcell.math.MathException e) {
						e.printStackTrace(System.out);
						throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
					} catch (cbit.vcell.parser.ExpressionException e) {
						e.printStackTrace(System.out);
						throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
					}
					
					try {
						FunctionColumnDescription cd = new FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
						odeSolverResultSet.addFunctionColumn(cd);
					}catch (cbit.vcell.parser.ExpressionException e){
						e.printStackTrace(System.out);
					}
				}
			}
	
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
		            Variable var = sim.getVariable(testSpec.getVarsList()[i]);
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
