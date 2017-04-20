package org.vcell.sbml.test;

import java.io.File;
import java.io.IOException;

import org.COPASI.CCopasiDataModel;
import org.COPASI.CCopasiMessage;
import org.COPASI.CCopasiMethod;
import org.COPASI.CCopasiObjectName;
import org.COPASI.CCopasiParameter;
import org.COPASI.CCopasiReportSeparator;
import org.COPASI.CCopasiStaticString;
import org.COPASI.CCopasiTask;
import org.COPASI.CMetab;
import org.COPASI.CModel;
import org.COPASI.CRegisteredObjectName;
import org.COPASI.CReportDefinition;
import org.COPASI.CReportDefinitionVector;
import org.COPASI.CTimeSeries;
import org.COPASI.CTrajectoryMethod;
import org.COPASI.CTrajectoryProblem;
import org.COPASI.CTrajectoryTask;
import org.COPASI.ReportItemVector;
import org.vcell.sbml.SBMLSolver;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SimSpec;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.solver.SolverException;


public class CopasiSBMLSolver implements SBMLSolver {
	
	public static final String COLUMN_DELIMITER = ", ";
	
	static
	{
		try {
			NativeLib.SBML.load();
			NativeLib.COPASI.load();
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	@Override
	public String getResultsFileColumnDelimiter(){
		return COLUMN_DELIMITER;
	}
	
	public File solve(String filePrefix, File outDir, String sbmlText, SimSpec simSpec) throws IOException, SolverException, SbmlException {
	    File outputFile = null;
	    if (sbmlText == null) {
	    	throw new RuntimeException("Model XML string is null : Unable to proceed.");
	    }
	    CCopasiDataModel copasiModel = CCopasiDataModel.getGlobal();
	    boolean bImported = false;
		try {
			bImported = copasiModel.importSBMLFromString(sbmlText);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	    if (!bImported) {
	    	throw new RuntimeException("Could not import SBML model into Copasi.");
	    }
	    CModel model = copasiModel.getModel();
	    if (model != null) {
		    // create a report with the correct filename and all the species against time.
		    CReportDefinitionVector reports = copasiModel.getReportDefinitionList();
		    // create a new report definition object
		    CReportDefinition report = reports.createReportDefinition("Report", "Output for - timecourse");
		    // set the task type for the report definition to timecourse
		    report.setTaskType(CCopasiTask.timeCourse);
		    // we don�t want a table
		    report.setIsTable(false);
		    // the entries in the output should be seperated by a ", "
		    report.setSeparator(new CCopasiReportSeparator(", "));
		    // we need a handle to the header and the body the header will display the ids of the metabolites 
		    // and "time" for the first column; the body will contain the actual timecourse data
		    ReportItemVector header = report.getHeaderAddr();
		    ReportItemVector body = report.getBodyAddr();
		    body.add(new CRegisteredObjectName(model.getObject(new CCopasiObjectName("Reference=Time")).getCN().getString()));
		    body.add(new CRegisteredObjectName(report.getSeparator().getCN().getString()));
		    header.add(new CRegisteredObjectName(new CCopasiStaticString("time").getCN().getString()));
		    header.add(new CRegisteredObjectName(report.getSeparator().getCN().getString()));
		    
		    int i, iMax =(int) model.getMetabolites().size();
		    for (i = 0;i < iMax;++i) {
		    	CMetab metab = model.getMetabolite(i);
		    	assert metab != null;
			    // we don�t want output for FIXED metabolites right now
//			    if (metab.getStatus() != CModelEntity.FIXED) {
				    // we want the concentration oin the output alternatively, we could use "Reference=Amount" to get the
				    // particle number
				    body.add(new CRegisteredObjectName(metab.getObject(new CCopasiObjectName("Reference=Concentration")).getCN().getString()));
				    // add the corresponding id to the header
				    header.add(new CRegisteredObjectName(new CCopasiStaticString(metab.getSBMLId()).getCN().getString()));
				    // after each entry, we need a seperator
				    if(i!=iMax-1) {
					    body.add(new CRegisteredObjectName(report.getSeparator().getCN().getString()));
					    header.add(new CRegisteredObjectName(report.getSeparator().getCN().getString()));
			    	}
//		    	}
		   	}
		    
		   	double startTime = 0.0;
		   	double endTime = simSpec.getEndTime();
		   	double duration = endTime - startTime;
		   	int stepNumber = simSpec.getNumTimeSteps();

		    // get the trajectory task object
		    CTrajectoryTask trajectoryTask = (CTrajectoryTask)copasiModel.getTask("Time-Course");
		    // if there isn�t one
		    if (trajectoryTask == null) {
			    // create a new one
			    trajectoryTask = new CTrajectoryTask();
			    // add the new time course task to the task list this method makes sure that the object is now owned
			    // by the list and that it does not get deleted by SWIG
			    copasiModel.getTaskList().addAndOwn(trajectoryTask);
		    }
		    // run a deterministic time course
		    trajectoryTask.setMethodType(CCopasiMethod.deterministic);
		    // pass a pointer of the model to the problem
		    trajectoryTask.getProblem().setModel(model);
		    // activate the task so that it will be run when the model is saved and passed to CopasiSE
		    trajectoryTask.setScheduled(true);
		    // set the report for the task
		    trajectoryTask.getReport().setReportDefinition(report);
		    // set the output filename
		    outputFile = new File(outDir,filePrefix+".copasi.csv");
		    trajectoryTask.getReport().setTarget(outputFile.getAbsolutePath());
		    // don�t append output if the file exists, but overwrite the file
		    trajectoryTask.getReport().setAppend(false);
		    // get the problem for the task to set some parameters
		    CTrajectoryProblem problem = (CTrajectoryProblem)trajectoryTask.getProblem();
		    // simulate 100 steps
		    problem.setStepNumber(stepNumber);
		    // start at time 0
		    copasiModel.getModel().setInitialTime(startTime);
		    // simulate a duration of 10 time units
		    problem.setDuration(duration);
		    // tell the problem to actually generate time series data
		    problem.setTimeSeriesRequested(true);
		    // set some parameters for the LSODA method through the method
		    CTrajectoryMethod method = (CTrajectoryMethod)trajectoryTask.getMethod();
		    CCopasiParameter parameter = method.getParameter("Absolute Tolerance");
		    if (parameter != null) {
			    if (parameter.getType() == CCopasiParameter.UDOUBLE) {
			    	parameter.setDblValue(1.0e-12);
			    } else {
			    	System.err.println("Absolute tolerance parameter is not of type double. Setting it to double");
			    	parameter.setDblValue(1.0e-12);
			    }
		    } else {
		    	CCopasiParameter absTolParam = new CCopasiParameter("Absolute Tolerance", CCopasiParameter.DOUBLE);
		    	absTolParam.setDblValue(1.0e-12);
		    	model.add(absTolParam);
		    }
		    boolean result=true;
		    
		    try {
			    // now we run the actual trajectory
			    result=trajectoryTask.process(true);
		    } catch (Exception ex) {
		    	ex.printStackTrace(System.out);
		    	System.err.println( "Error. Running the time course simulation failed." );
			    // check if there are additional error messages
			    if (CCopasiMessage.size() > 0) {
				    // print the messages in chronological order
				    System.err.println(CCopasiMessage.getAllMessageText(true));
			    }
			    System.exit(1);
		    }
		    if(result==false) {
			    System.err.println( "An error occured while running the time course simulation." );
			    // check if there are additional error messages
			    if (CCopasiMessage.size() > 0) {
				    // print the messages in chronological order
				    System.err.println(CCopasiMessage.getAllMessageText(true));
			    }
			    System.exit(1);
		    }
		    // look at the timeseries
		    CTimeSeries timeSeries = trajectoryTask.getTimeSeries();
		    // we simulated 100 steps, including the initial state, this should be 101 step in the timeseries
		    System.out.println( "The time series consists of " + (new Long(timeSeries.getRecordedSteps())).toString() + "." );
		    System.out.println( "Each step contains " + (new Long(timeSeries.getNumVariables())).toString() + " variables." );
		    System.out.println( "The final state is: " );
		    iMax = (int)timeSeries.getNumVariables();
		    int lastIndex = (int)timeSeries.getRecordedSteps() - 1;
		    for (i = 0;i < iMax;++i) {
			    // here we get the particle number (at least for the species) the unit of the other variables 
			    // may not be particle numbers the concentration data can be acquired with getConcentrationData
			    System.out.println(timeSeries.getTitle(i) + ": " + (new Double(timeSeries.getData(lastIndex, i))).toString() );
		    }
	    } else {
	    	throw new RuntimeException("Model is null");
	    }
		return outputFile;		
	}
	
	/*public static void main(String[] args){
	try {
		if (args.length!=2){
			System.out.println("usage: CopasiSBMLSolver sbmlDirectory outputDirectory");
			System.exit(-1);
		}
		File dataDir = new File(args[0]);
		if (!dataDir.exists()){
			throw new RuntimeException("inputDirectory "+dataDir.getAbsolutePath()+" doesn't exist");
		}
		File outDir = new File(args[1]);
		if (!outDir.exists()){
			outDir.mkdirs();
		}
		PrintWriter printWriter = new PrintWriter(new FileWriter(new File(outDir, "summary.log")));
		try {
			printWriter.println(" | *BIOMODEL ID* | *BioModel name* | *PASS* | *Rel Error* | *Exception* | *Comments* |");
			File[] sbmlFiles = dataDir.listFiles();
			for (File sbmlFile : sbmlFiles){
				StringTokenizer stringTokenizer = new StringTokenizer(sbmlFile.getName(),".",false);
				String sbmlID = stringTokenizer.nextToken();
				String filePrefix = sbmlID;
				String bioModelsPrefix = "http://www.ebi.ac.uk/compneur-srv/biomodels-main/publ-model.do?mid=";
				String sbmlLink = "[["+bioModelsPrefix+sbmlID+"]["+sbmlID+"]]";
				PrintStream saved_sysout = System.out;
				PrintStream saved_syserr = System.err;
				PrintStream new_sysout = null;
				PrintStream new_syserr = null;
				try {
					File logFile = new File(outDir,filePrefix+".log");
					new_sysout = new PrintStream(logFile);
					new_syserr = new_sysout;
					System.setOut(new_sysout);
					System.setErr(new_syserr);
					StringBuffer combinedErrorBuffer = new StringBuffer();
					String sbmlText = SBMLUtils.readStringFromFile(sbmlFile.getAbsolutePath());
					//
					// get SBML model "name" (or "id")
					//
					SimSpec simSpec = SimSpec.fromSBML(sbmlText);
					
					String passedString = "%NO%";
					String relErrorString = "";
					try {
						//
						// get COPASI solution (time and species concentrations)
						//
						try {
							CopasiSBMLSolver copasiSolver = new CopasiSBMLSolver();
							copasiSolver.solve(filePrefix, outDir, sbmlText, simSpec);
						}catch (Exception e){
							printWriter.println("solveCopasi() failed");
							e.printStackTrace(printWriter);
							System.out.println("solveCopasi() failed");
							e.printStackTrace(System.out);
							combinedErrorBuffer.append(" *COPASI* _"+e.getMessage()+"_ ");
						}

					}catch (Exception e){
						e.printStackTrace(printWriter);
						combinedErrorBuffer.append(" *UNKNOWN* _"+e.getMessage()+"_ ");
					}
					printWriter.println(" | "+sbmlLink+" | "+sbmlID+" | "+passedString+" | "+relErrorString+" | "+combinedErrorBuffer.toString()+" | |");
					printWriter.flush();
				}finally{
					if (new_sysout!=null){
						new_sysout.close();
						new_sysout=null;
					}
					if (new_syserr!=null){
						new_syserr.close();
						new_syserr=null;
					}
					System.setOut(saved_sysout);
					System.setOut(saved_syserr);
				}
			}
		}finally{
			printWriter.close();
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
	}
	System.exit(0);
}*/

}
