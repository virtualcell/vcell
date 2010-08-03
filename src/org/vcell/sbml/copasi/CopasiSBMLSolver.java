package org.vcell.sbml.copasi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

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
import org.COPASI.CModelEntity;
import org.COPASI.CRegisteredObjectName;
import org.COPASI.CReportDefinition;
import org.COPASI.CReportDefinitionVector;
import org.COPASI.CTimeSeries;
import org.COPASI.CTrajectoryMethod;
import org.COPASI.CTrajectoryProblem;
import org.COPASI.CTrajectoryTask;
import org.COPASI.ReportItemVector;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.vcell.sbml.SBMLSolver;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.SolverException;
import org.vcell.util.PropertyLoader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


public class CopasiSBMLSolver implements SBMLSolver {
	
	private String columnDelimiter = ", ";
	
	static
	{
		try {
			System.loadLibrary("sbmlj");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	public static void main(String[] args){
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
								File resultsFile = copasiSolver.solve(filePrefix, outDir, sbmlText, simSpec);
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
	}
	
	public String getResultsFileColumnDelimiter(){
		return columnDelimiter;
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
		    // we don’t want a table
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
			    // we don’t want output for FIXED metabolites right now
			    if (metab.getStatus() != CModelEntity.FIXED) {
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
		    	}
		   	}
		    
		   	double startTime = 0.0;
		   	double endTime = simSpec.getEndTime();
		   	double duration = endTime - startTime;
		   	int stepNumber = simSpec.getNumTimeSteps();

		    // get the trajectory task object
		    CTrajectoryTask trajectoryTask = (CTrajectoryTask)copasiModel.getTask("Time-Course");
		    // if there isn’t one
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
		    // don’t append output if the file exists, but overwrite the file
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
	
	public File solve0(String filePrefix, File outDir, String sbmlText, SimSpec simSpec) throws IOException, SolverException, SbmlException {
		if (!outDir.exists()){
			throw new RuntimeException("directory "+outDir.getAbsolutePath()+" doesn't exist");
		}
		File sbmlFile = new File(outDir,filePrefix+".sbml");
		SBMLUtils.writeStringToFile(sbmlText,sbmlFile.getAbsolutePath(), true);
		
		File origCopasiFile = new File(outDir,filePrefix+".orig.cps");
		File modifiedCopasiFile = new File(outDir,filePrefix+".mod.cps");
		File copasiReportFile = new File(outDir,filePrefix+".copasi.csv");
		if (copasiReportFile.exists()){
			copasiReportFile.delete();  // don't want to create it, just want the availlable name (it should still be availlable when I need it).
		}

		String copasiExecutable = PropertyLoader.getRequiredProperty(PropertyLoader.COPASIExecutable);
		ProcessBuilder processBuilderImport = new ProcessBuilder(new String[] {
				"cmd", 
				"/c", 
				copasiExecutable, 
				"--importSBML", 
				sbmlFile.getAbsolutePath(), 
				"--save", 
				origCopasiFile.getAbsolutePath()
		});
		processBuilderImport.redirectErrorStream();
		Process processImport = processBuilderImport.start();
		try {
			processImport.waitFor();
		}catch (InterruptedException e2){
			e2.printStackTrace(System.out);
		}
		int exitValueImport = processImport.exitValue();
		String origCopasiXML = SBMLUtils.readStringFromFile(origCopasiFile.getAbsolutePath());
		Element copasiRootElement = SBMLUtils.readXML(new StringReader(origCopasiXML));
		
		//
		// specify task for timeCourse
		//
		int numOriginalReports = copasiRootElement.getChild("ListOfReports").getChildren("Report").size();
		String newReportName = "Report_"+numOriginalReports;
		Element listOfTasks = copasiRootElement.getChild("ListOfTasks");
		List<Element> taskList = listOfTasks.getChildren("Task");
		for (Element taskElement : taskList){
			Attribute nameAttr = taskElement.getAttribute("type");
			if (nameAttr != null && nameAttr.getValue()!=null && nameAttr.getValue().equals("timeCourse")){
				taskElement.setAttribute("scheduled", "true");
				Element problemElement = taskElement.getChild("Problem");
				List<Element> parameterList = problemElement.getChildren("Parameter");
				for (Element parameterElement : parameterList){
					if (parameterElement.getAttributeValue("name").equals("StepNumber")){
						parameterElement.setAttribute("value", Integer.toString(simSpec.getNumTimeSteps()));
					}
					if (parameterElement.getAttributeValue("name").equals("StepSize")){
						parameterElement.setAttribute("value", Double.toString(simSpec.getStepSize()));
					}
					if (parameterElement.getAttributeValue("name").equals("Duration")){
						parameterElement.setAttribute("value", Double.toString(simSpec.getEndTime()));
					}
					if (parameterElement.getAttributeValue("name").equals("TimeSeriesRequested")){
						parameterElement.setAttribute("value", "0");
					}
				}
				Element reportElement = new Element("Report");
				reportElement.setAttribute("reference",newReportName);
				reportElement.setAttribute("target",copasiReportFile.getName());
				reportElement.setAttribute("append","1");
				taskElement.getChildren().add(0, reportElement);
				break;
			}
		}
		
		//
		// add report for timeCourse table
		//
		Element reportElement = new Element("Report");
		reportElement.setAttribute("key",newReportName);
		reportElement.setAttribute("name","Time, Concentrations and Global Quantity Values");
		reportElement.setAttribute("taskType","timeCourse");
		reportElement.setAttribute("separator",columnDelimiter);
		reportElement.setAttribute("precision","12");
		Element headerElement = new Element("Header");
		Element bodyElement = new Element("Body");
		
		String modelName = copasiRootElement.getChild("Model").getAttributeValue("name");
		
		// add time column to Header
		Element timeLabelElement = new Element("Object");
		timeLabelElement.setAttribute("cn","String=\\[Time\\]");
		headerElement.addContent(timeLabelElement);
		
		// add time column to Body
		Element timeObjectElement = new Element("Object");
		timeObjectElement.setAttribute("cn","CN=Root,Model="+modelName+",Reference=Time");
		bodyElement.addContent(timeObjectElement);
		
		// add column for each metabolite
		Element listOfMetabolitesElement = copasiRootElement.getChild("Model").getChild("ListOfMetabolites");
		if (listOfMetabolitesElement==null){
			throw new RuntimeException("COPASI model doesn't have metabolites - skipping");
		}
		List<Element> metaboliteList = listOfMetabolitesElement.getChildren("Metabolite");
		HashMap<String, String> metaboliteKeyHash = new HashMap<String, String>();
		for (Element metaboliteElement : metaboliteList){
			String metaboliteKey = metaboliteElement.getAttributeValue("key");
			String metaboliteName = metaboliteElement.getAttributeValue("name");
			String compartmentKey = metaboliteElement.getAttributeValue("compartment");
			metaboliteKeyHash.put(metaboliteName, metaboliteKey);
			// look up compartment name
			String compartmentName = null;
			List<Element> compartmentList = copasiRootElement.getChild("Model").getChild("ListOfCompartments").getChildren("Compartment");
			for (Element compartmentElement : compartmentList){
				if (compartmentElement.getAttributeValue("key").equals(compartmentKey)){
					compartmentName = compartmentElement.getAttributeValue("name");
				}
			}
			// header: separator
			Element headerSeparatorElement = new Element("Object");
			headerSeparatorElement.setAttribute("cn","Separator="+columnDelimiter);
			headerElement.addContent(headerSeparatorElement);

			// header: column name (SBMLId)
			Element labelElement = new Element("Object");
			labelElement.setAttribute("cn","CN=Root,Model="+modelName+",Vector=Compartments["+compartmentName+"],Vector=Metabolites["+metaboliteName+"],Reference=SBMLId");
			headerElement.addContent(labelElement);

			// body: separator
			Element objectSeparatorElement = new Element("Object");
			objectSeparatorElement.setAttribute("cn","Separator="+columnDelimiter);
			bodyElement.addContent(objectSeparatorElement);

			// body: value (concentration)
			Element objectElement = new Element("Object");
			objectElement.setAttribute("cn","CN=Root,Model="+modelName+",Vector=Compartments["+compartmentName+"],Vector=Metabolites["+metaboliteName+"],Reference=Concentration");
			bodyElement.addContent(objectElement);
		}
		reportElement.addContent(headerElement);
		reportElement.addContent(bodyElement);
		copasiRootElement.getChild("ListOfReports").addContent(reportElement);
		
		String modifiedCopasiText = SBMLUtils.xmlToString(copasiRootElement,true);
		String preamble = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- generated with COPASI 4.2 (Build 22) (http://www.copasi.org) at 2007-09-14 13:38:52 UTC -->\n";
		SBMLUtils.writeStringToFile(preamble+modifiedCopasiText, modifiedCopasiFile.getAbsolutePath(), true);
		
		ProcessBuilder processBuilderCopasiSim = new ProcessBuilder(new String[] {
				"cmd",
				"/c",
				copasiExecutable, 
				modifiedCopasiFile.getAbsolutePath()
		});
		processBuilderCopasiSim.redirectErrorStream(true);
		Process processCopasiSim = processBuilderCopasiSim.start();
		try {
			processCopasiSim.waitFor();
		}catch (InterruptedException e2){
			e2.printStackTrace(System.out);
		}
		int exitValueSim = processCopasiSim.exitValue();
		
		return copasiReportFile;
	}
	
	public static class GenericXMLErrorHandler implements ErrorHandler {

		private StringBuffer logBuffer = new StringBuffer();

	/**
	 * GenericXMLErrorHandler constructor comment.
	 */
	protected GenericXMLErrorHandler() {
		super();
	}


	    public void error(SAXParseException e) {

	       logBuffer.append("SAXParseException: Error: "+e.getMessage());
	       logBuffer.append("\n");
	       logBuffer.append("\tCol number: " + e.getColumnNumber() + " Line number: " + e.getLineNumber() +
		       				  " PublicID: " + e.getPublicId() + " SystemID: " + e.getSystemId());
	       logBuffer.append("\n");
	    }


	    public void fatalError(SAXParseException e) {

	       logBuffer.append("SAXParseException: FatalError: "+e.getMessage());
	       logBuffer.append("\n");
	       logBuffer.append("\tCol number: " + e.getColumnNumber() + " Line number: " + e.getLineNumber() +
		       				  " PublicID: " + e.getPublicId() + " SystemID: " + e.getSystemId());
	       logBuffer.append("\n");
	    }


		protected String getErrorLog() {

			return logBuffer.toString();
		}


	    public void warning(SAXParseException e) {

	       logBuffer.append("SAXParseException: Warning: "+e.getMessage());
	       logBuffer.append("\n");
	       logBuffer.append("\tCol number: " + e.getColumnNumber() + " Line number: " + e.getLineNumber() +
		       				  " PublicID: " + e.getPublicId() + " SystemID: " + e.getSystemId());
	       logBuffer.append("\n");
	    }
	}

	public void close() throws SolverException {
		// TODO Auto-generated method stub
		
	}

	public void open() throws SolverException {
		// TODO Auto-generated method stub
		
	}


}
