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

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.vcell.sbml.SBMLSolver;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.SolverException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


public class CopasiSBMLSolver implements SBMLSolver {
	
	private String columnDelimiter = ";";
	
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
		if (!outDir.exists()){
			throw new RuntimeException("directory "+outDir.getAbsolutePath()+" doesn't exist");
		}
		File sbmlFile = new File(outDir,filePrefix+".sbml");
		SBMLUtils.writeStringToFile(sbmlText,sbmlFile.getAbsolutePath());
		
		File origCopasiFile = new File(outDir,filePrefix+".orig.cps");
		File modifiedCopasiFile = new File(outDir,filePrefix+".mod.cps");
		File copasiReportFile = new File(outDir,filePrefix+".copasi.csv");
		if (copasiReportFile.exists()){
			copasiReportFile.delete();  // don't want to create it, just want the availlable name (it should still be availlable when I need it).
		}

		ProcessBuilder processBuilderImport = new ProcessBuilder(new String[] {
				"cmd", 
				"/c", 
				"CopasiSE.exe", 
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
		SBMLUtils.writeStringToFile(preamble+modifiedCopasiText, modifiedCopasiFile.getAbsolutePath());
		
		ProcessBuilder processBuilderCopasiSim = new ProcessBuilder(new String[] {
				"cmd",
				"/c",
				"CopasiSE.exe", 
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
