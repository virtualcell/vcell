package cbit.vcell.vcml;
import cbit.vcell.biomodel.BioModel;
import org.jdom.Element;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ReservedSymbol;

import java.io.File;
import java.util.Hashtable;

import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.server.SessionLog;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XmlDialect;
import cbit.vcell.xml.XMLTags;

/**
 * Driver class for the SBML test suite.
 * Creation date: (9/23/2004 2:22:47 PM)
 * @author: Jim Schaff
 */
public class VCellSemanticTest {
/**
 * Insert the method's description here.
 * Creation date: (12/28/2004 12:21:16 PM)
 * @return int
 * @param n int
 */
public static int factorial(int n) {
    if (n < 0) {
        throw new RuntimeException("Cannot evaluate factorial of negative number");
    }

    int factorial = 1;
    int index = 1;
    while (index <= n) {
        factorial *= index;
        index++;
    }

    return factorial;
}

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
        cbit.vcell.server.PropertyLoader.loadProperties();
        java.io.File sbmlFile = new java.io.File(args[0]);
        double endTime = Double.parseDouble(args[1]);
        int numTimeSteps = Integer.parseInt(args[2]);
        java.io.File outputFile = new java.io.File(args[3]);
        java.io.File tempDir = new java.io.File(args[4]);

        //
        // get list of species in order of output
        //
        String speciesNames[] = new String[args.length - 5];
        for (int i = 0; i < args.length - 5; i++) {
            speciesNames[i] = args[i + 5];
        }

        //
        // parse XML file into a BioModel
        //
        String sbmlString = cbit.util.xml.XmlUtil.getXMLString(sbmlFile.getPath());
        XmlDialect fromDialect = XmlDialect.SBML_L2V1;
        
        cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
            private StringBuffer buffer = new StringBuffer();
            public void sendMessage(int messageLevel, int messageType) {
                // String message = cbit.vcell.vcml.TranslationMessage.getDefaultMessage(messageType);
                // sendMessage(messageLevel, messageType, message);	
            }
            public void sendMessage(int messageLevel, int messageType, String message) {
                // System.out.println("LOGGER: msgLevel="+messageLevel+", msgType="+messageType+", "+message);
            }
            public void sendAllMessages() {
            }
            public boolean hasMessages() {
                return false;
            }
        };

        // Round trip the sbml model : import -> export -> import the same.

        // Importing the SBML document.
        BioModel bioModel_1 = (BioModel) cbit.vcell.xml.XmlHelper.importXMLVerbose(logger, sbmlString, fromDialect);

        // Export the previously imported document.
//        String exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportXML(bioModel_1, fromDialect, bioModel_1.getSimulationContexts(0).getName());
//
//        // Import the exported biomodel
//        BioModel bioModel_2 = (BioModel) cbit.vcell.xml.XmlHelper.importXMLVerbose(logger, exportedSBMLStr, fromDialect);
        
        // Instantiate an SBMLImporter to get the speciesUnitsHash - to compute the conversion factor from VC->SB species units.
		cbit.vcell.vcml.SBMLImporter sbmlImporter = new cbit.vcell.vcml.SBMLImporter(sbmlString, logger);
		BioModel tempBioModel = sbmlImporter.getBioModel();
		Hashtable<String, SBMLImporter.SBVCConcentrationUnits> speciesUnitsHash = sbmlImporter.getSpeciesUnitsHash();
		double timeFactor = sbmlImporter.getSBMLTimeUnitsFactor();

		//
        // select only Application, generate math, and create a single Simulation.
		//
        cbit.vcell.mapping.SimulationContext simContext = bioModel_1.getSimulationContexts(0);
        cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContext);
        cbit.vcell.math.MathDescription mathDesc = mathMapping.getMathDescription();
        simContext.setMathDescription(mathDesc);
        cbit.sql.SimulationVersion simVersion =
            new cbit.sql.SimulationVersion(
                new cbit.sql.KeyValue("100"),
                sbmlFile.getName(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        cbit.vcell.solver.Simulation sim = new cbit.vcell.solver.Simulation(simVersion, mathDesc);
        sim.setName(sbmlFile.getName());
        // if time factor from SBML is not 1 (i.e., it is not in secs but in minutes or hours), convert endTime to min/hr as : endTime*timeFactor
       	endTime = endTime*timeFactor;
        	
        sim.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0, endTime));
        cbit.vcell.solver.TimeStep timeStep = new cbit.vcell.solver.TimeStep();
        sim.getSolverTaskDescription().setTimeStep(new cbit.vcell.solver.TimeStep(timeStep.getMinimumTimeStep(),timeStep.getDefaultTimeStep(),endTime/10000));
        sim.getSolverTaskDescription().setOutputTimeSpec(new cbit.vcell.solver.UniformOutputTimeSpec((endTime-0)/numTimeSteps));
        sim.getSolverTaskDescription().setErrorTolerance(new cbit.vcell.solver.ErrorTolerance(1e-10, 1e-12));

        //        
		// solve simulation - USING NativeIDASolver ....
		//
		cbit.vcell.solver.ode.IDAFileWriter idaFileWriter = new cbit.vcell.solver.ode.IDAFileWriter(sim);
		idaFileWriter.initialize();
		java.io.StringWriter stringWriter = new java.io.StringWriter();
		idaFileWriter.writeIDAFile(new java.io.PrintWriter(stringWriter,true));
		stringWriter.close();
		StringBuffer buffer = stringWriter.getBuffer();
		String idaInputString = buffer.toString();

		final cbit.vcell.solvers.NativeIDASolver nativeIDASolver = new cbit.vcell.solvers.NativeIDASolver();
		System.out.println(idaInputString);
		cbit.vcell.util.RowColumnResultSet rcResultSet = nativeIDASolver.solve(idaInputString);

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
        java.io.PrintStream outputStream = new java.io.PrintStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(outputFile)));
        outputStream.print("time");
        for (int i = 0; i < speciesNames.length; i++) {
            outputStream.print("," + speciesNames[i]);
        }
        outputStream.println();
        //
        // extract data for time and species
        // 
        double[][] data = new double[speciesNames.length + 1][];
        int column = odeSolverResultSet.findColumn("t");
        data[0] = odeSolverResultSet.extractColumn(column);
        int origDataLength = data[0].length;
        for (int i = 0; i < speciesNames.length; i++) {
            column = odeSolverResultSet.findColumn(speciesNames[i]);
            if (column == -1) {
                Variable var = sim.getVariable(speciesNames[i]);
                data[i + 1] = new double[data[0].length];
                if (var instanceof cbit.vcell.math.Constant) {
                    double value = ((cbit.vcell.math.Constant) var).getExpression().evaluateConstant();
                    for (int j = 0; j < data[i + 1].length; j++) {
                        data[i + 1][j] = value;
                    }
                } else {
                    throw new RuntimeException(
                        "Did not find " + speciesNames[i] + " in simulation");
                }

            } else {
                data[i + 1] = odeSolverResultSet.extractColumn(column);
            }
        }

        //
        // for each time, print row
        //
        int index = 0;
        double[] sampleTimes = new double[numTimeSteps + 1];
        for (int i = 0; i <= numTimeSteps; i++) {
            sampleTimes[i] = endTime * i / numTimeSteps;
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
                for (int j = 0; j < speciesNames.length; j++) {
                	SBMLImporter.SBVCConcentrationUnits spConcUnits = speciesUnitsHash.get(speciesNames[j]);
                	if (spConcUnits != null) {
                		VCUnitDefinition sbunits = spConcUnits.getSBConcentrationUnits();
                		VCUnitDefinition vcunits = spConcUnits.getVCConcentrationUnits();
                		double unitFactor = SBMLImporter.getSpeciesConcUnitFactor(vcunits, sbunits);
                    	outputStream.print("," + data[j + 1][index] * unitFactor); 		//earlier, hack unitfactor = 0.000001
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
                for (int j = 0; j < speciesNames.length; j++) {
                    double interpolatedValue = 0.0;
                    double[] speciesVals = null;
                    double[] times = null;
                    
                    //Get the unit factor (VC->SBML units)
                    double unitFactor;
                	SBMLImporter.SBVCConcentrationUnits spConcUnits = speciesUnitsHash.get(speciesNames[j]);
                	if (spConcUnits != null) {
                		VCUnitDefinition sbunits = spConcUnits.getSBConcentrationUnits();
                		VCUnitDefinition vcunits = spConcUnits.getVCConcentrationUnits();
                		unitFactor = SBMLImporter.getSpeciesConcUnitFactor(vcunits, sbunits);
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
                        interpolatedValue = taylorInterpolation(sampleTimes[i], times, speciesVals);
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
                            interpolatedValue = taylorInterpolation(sampleTimes[i], times, speciesVals);
                        } else {
                            times = new double[] { data[0][index - 1], data[0][index], data[0][index + 1] };
                            speciesVals =
                                new double[] {
                                    data[j + 1][index - 1],
                                    data[j + 1][index],
                                    data[j + 1][index + 1] };
                            interpolatedValue = taylorInterpolation(sampleTimes[i], times, speciesVals);
                        }

                    //// Currently using 1st order interpolation
                    //times = new double[] { data[0][index], data[0][index+1] };
                    //speciesVals = new double[] { data[j+1][index], data[j+1][index+1] };
                    //interpolatedValue = taylorInterpolation(sampleTimes[i], times, speciesVals);

                    interpolatedValue = interpolatedValue * unitFactor; 		//earlier, hack unitfactor = 0.000001 
                    // System.out.println("Sample time: " + sampleTimes[i] + ", between time[" + index + "]=" + data[0][index]+" and time["+(index+1)+"]="+(data[0][index+1])+", interpolated = "+interpolatedValue);
                    outputStream.print("," + interpolatedValue);
                }
                outputStream.println();
            }
        }
        outputStream.close();
    } catch (Throwable e) {
        e.printStackTrace(System.out);
        System.exit(1);
    }

    System.exit(0);
}


/**
 * Insert the method's description here.
 * Creation date: (12/28/2004 10:38:32 AM)
 */
public static double taylorInterpolation(
    double reqdTimePt,
    double[] neighboringTimePts,
    double[] neighboringValues) {
    //
    // This method applies a Taylor's series approximation to interpolate the function value at a required time point.
    // 'reqdTimePt' is the point at which the value of the function is required.
    // The 'neighboringTimePts' array contains the time points before and after the 'reqdTimePt' at which the value
    // of the function is known, using which the value of fn at 'reqdTimePt' has to be interpolated.
    // The 'neighboringValues' array contains the values of function at the time points provided in 'neighboringTimePts'.
    //

    if (neighboringTimePts.length != neighboringValues.length) {
        throw new RuntimeException("Number of values provided in the 2 arrays are not equal, cannot proceed!");
    }

    // 
    // Create a matrix (A_matrix) with the neighboring time points. The matrix is of the form :
    //
    //		1	del_t1	(del_t1^2)/2!	(del_t1^3)/3!
    //		1	del_t2  (del_t2^2)/2!	(del_t2^3)/3!
    //		1	del_t3  (del_t3^2)/2!	(del_t3^3)/3!
    //		1	del_t4  (del_t4^2)/2!	(del_t4^3)/3!
    //
    // if interpolation is done using 4 points; 
    // where del_ti is the difference between reqdTimePt and time points used for interpolation.
    // 
    int dim = neighboringTimePts.length;
    Jama.Matrix A_matrix = new Jama.Matrix(dim, dim);
    for (int i = 0; i < dim; i++) {
        for (int j = 0; j < dim; j++) {
            double val = neighboringTimePts[i] - reqdTimePt;
            val = Math.pow(val, j);
            val = val / factorial(j);
            A_matrix.set(i, j, val);
        }
    }

    // B_matrix is a column matrix containing the values of functions at the known time points (neighboringTimePts).
    Jama.Matrix B_matrix = new Jama.Matrix(neighboringValues, dim);

    // Solve A_matrix * F = B_matrix to obtain F, which is the transpose of [ f(t)	f'(t)	f''(t)	f'''(t) ]
    Jama.Matrix solutionMatrix = A_matrix.solve(B_matrix);

    // The required interpolated value at 'reqdTimePt' is the first value in solutionMatrix (F)
    double reqdValue = solutionMatrix.get(0, 0);

    return reqdValue;
}
}