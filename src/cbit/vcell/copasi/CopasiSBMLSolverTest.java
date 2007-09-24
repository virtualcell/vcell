package cbit.vcell.copasi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

import cbit.util.Executable;
import cbit.util.ExecutableException;
import cbit.util.ExecutableStatus;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.util.xml.XmlUtil;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
import cbit.vcell.solvers.MathExecutable;
import cbit.vcell.util.ColumnDescription;
import cbit.vcell.vcml.VCellSemanticTest;

public class CopasiSBMLSolverTest {
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
						float duration = 10f;
						int numTimeSteps = 100;
						float timeStep = 0.1f;
						String sbmlText = XmlUtil.getXMLString(sbmlFile.getAbsolutePath());
						//
						// get SBML model "name" (or "id")
						//
						Element rootSBML = XmlUtil.stringToXML(sbmlText,null);
						Namespace sbmlNamespace = Namespace.getNamespace("http://www.sbml.org/sbml/level2");
						Element sbmlModelElement = rootSBML.getChild("model",sbmlNamespace);
						String sbmlModelName = sbmlModelElement.getAttributeValue("name");
						if (sbmlModelName==null || sbmlModelName.length()==0){
							sbmlModelName = sbmlModelElement.getAttributeValue("id");
						}
						//
						// collect names of Species 
						//
						Vector<String> varNames = new Vector<String>();
						Element listOfSpeciesElement = sbmlModelElement.getChild("listOfSpecies",sbmlNamespace);
						if (listOfSpeciesElement!=null){
							List<Element> speciesElementList = listOfSpeciesElement.getChildren("species", sbmlNamespace);
							for (Element speciesElement : speciesElementList){
								varNames.add(speciesElement.getAttributeValue("id"));
							}
						}else{
							System.out.println("NO species in SBML model (parameters only) ... must be rate-rules only");
						}
						String[] varsToTest = varNames.toArray(new String[varNames.size()]);
						
						
						String passedString = "%NO%";
						String relErrorString = "";
						try {
							File vcellOutputFile = new File(outDir,filePrefix+".vcell.csv");
							//
							// get COPASI solution (time and species concentrations)
							//
							ODESolverResultSet copasiResults = null;
							try {
								copasiResults = solveCopasi(filePrefix, outDir, sbmlText, numTimeSteps, timeStep, duration);
							}catch (Exception e){
								printWriter.println("solveCopasi() failed");
								e.printStackTrace(printWriter);
								System.out.println("solveCopasi() failed");
								e.printStackTrace(System.out);
								combinedErrorBuffer.append(" *COPASI* _"+e.getMessage()+"_ ");
							}

							//
							// get VCell solution (time and species concentrations)
							//
							ODESolverResultSet vcellResults = null;
							try {
								VCellSemanticTest.vcell_simulate(sbmlText, duration, numTimeSteps, vcellOutputFile, varsToTest);
								vcellResults = readResultFile(vcellOutputFile, ","); 
							}catch (Exception e){
								printWriter.println("vcell_simulate failed");
								e.printStackTrace(printWriter);
								System.out.println("vcell_simulate failed");
								e.printStackTrace(System.out);
								combinedErrorBuffer.append(" *VCELL* _"+e.getMessage()+"_ ");
							}
							//
							// compare results from COPASI and VCELL
							//
							if (copasiResults!=null && vcellResults!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(copasiResults, vcellResults, varsToTest, 1e-5, 1e-5);
									double maxRelError = summary.getMaxRelativeError();
									relErrorString = Double.toString(maxRelError);
									if (maxRelError<1){
										passedString = "%Y%";
									}else{
										VariableComparisonSummary[] vcSummaries = summary.getVariableComparisonSummaries();
										for (VariableComparisonSummary vcSummary : vcSummaries){
											System.out.println(vcSummary.toShortString());
										}
									}
								} catch (Exception e) {
									printWriter.println("COMPARE failed");
									e.printStackTrace(printWriter);
									System.out.println("COMPARE failed");
									e.printStackTrace(System.out);
									combinedErrorBuffer.append(" *COMPARE* _"+e.getMessage()+"_ ");
								}
							}
						}catch (Exception e){
							e.printStackTrace(printWriter);
							combinedErrorBuffer.append(" *UNKNOWN* _"+e.getMessage()+"_ ");
						}
						printWriter.println(" | "+sbmlLink+" | "+sbmlModelName+" | "+passedString+" | "+relErrorString+" | "+combinedErrorBuffer.toString()+" | |");
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
	
	
	
	public static ODESolverResultSet solveCopasi(String filePrefix, File outDir, String sbmlText, int stepNumber, float stepSize, float duration) throws IOException, ExecutableException, DataAccessException{
		String delimiter = ",";
		if (!outDir.exists()){
			throw new RuntimeException("directory "+outDir.getAbsolutePath()+" doesn't exist");
		}
		File sbmlFile = new File(outDir,filePrefix+".sbml");
		XmlUtil.writeXMLString(sbmlText,sbmlFile.getAbsolutePath());
		
		File origCopasiFile = new File(outDir,filePrefix+".orig.cps");
		File modifiedCopasiFile = new File(outDir,filePrefix+".mod.cps");
		File copasiReportFile = new File(outDir,filePrefix+".copasi.csv");
		if (copasiReportFile.exists()){
			copasiReportFile.delete();  // don't want to create it, just want the availlable name (it should still be availlable when I need it).
		}

		Executable preprocess = new Executable(
				" cmd /c " +
				" CopasiSE.exe " + 
				" --importSBML "+sbmlFile.getAbsolutePath()+
				" --save "+origCopasiFile.getAbsolutePath());
		try {
			preprocess.start();
			while (preprocess.getStatus().equals(ExecutableStatus.READY) || preprocess.getStatus().equals(ExecutableStatus.RUNNING)){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}catch (ExecutableException e2){
			e2.printStackTrace(System.out);
		}
		String origCopasiXML = XmlUtil.getXMLString(origCopasiFile.getAbsolutePath());
		Element copasiRootElement = XmlUtil.stringToXML(origCopasiXML, null);
		
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
						parameterElement.setAttribute("value", Integer.toString(stepNumber));
					}
					if (parameterElement.getAttributeValue("name").equals("StepSize")){
						parameterElement.setAttribute("value", Float.toString(stepSize));
					}
					if (parameterElement.getAttributeValue("name").equals("Duration")){
						parameterElement.setAttribute("value", Float.toString(duration));
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
		reportElement.setAttribute("separator",delimiter);
		reportElement.setAttribute("precision","6");
		Element tableElement = new Element("Table");
		tableElement.setAttribute("printTitle","1");
		
		
		// add time column
		String modelName = copasiRootElement.getChild("Model").getAttributeValue("name");
		Element timeObjectElement = new Element("Object");
		timeObjectElement.setAttribute("cn","CN=Root,Model="+modelName+",Reference=Time");
		tableElement.addContent(timeObjectElement);
		
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
			Element objectElement = new Element("Object");
			objectElement.setAttribute("cn","CN=Root,Model="+modelName+",Vector=Compartments["+compartmentName+"],Vector=Metabolites["+metaboliteName+"],Reference=Concentration");
			tableElement.addContent(objectElement);
		}
		reportElement.addContent(tableElement);
		copasiRootElement.getChild("ListOfReports").addContent(reportElement);
		
		String modifiedCopasiText = XmlUtil.xmlToString(copasiRootElement,true);
		String preamble = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- generated with COPASI 4.2 (Build 22) (http://www.copasi.org) at 2007-09-14 13:38:52 UTC -->\n";
		XmlUtil.writeXMLString(preamble+modifiedCopasiText, modifiedCopasiFile.getAbsolutePath());
		
		MathExecutable simulation = new MathExecutable(
				"cmd /c "+
				"CopasiSE.exe "+ 
				" "+modifiedCopasiFile.getAbsolutePath());
		try {
			simulation.start();
			while (simulation.getStatus().equals(ExecutableStatus.READY) || simulation.getStatus().equals(ExecutableStatus.RUNNING)){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}catch (ExecutableException e2){
			e2.printStackTrace(System.out);
		}
		ODESolverResultSet copasiResultSet = readResultFile(copasiReportFile, delimiter);
		//
		// change the name of the metabolite concentrations to match the SBML ids of the species.
		//
		ColumnDescription[] columnDescriptions = copasiResultSet.getColumnDescriptions();
		for (int i=1; i<columnDescriptions.length;i++){
			String metaboliteKey = metaboliteKeyHash.get(columnDescriptions[i].getName().substring(0, columnDescriptions[i].getName().indexOf("[")));
			String sbmlID = null;
			if (metaboliteKey!=null){
				List<Element> sbmlMapElementList = copasiRootElement.getChild("SBMLReference").getChildren("SBMLMap");
				for (Element sbmlMapElement : sbmlMapElementList){
					if (sbmlMapElement.getAttributeValue("COPASIkey").equals(metaboliteKey)){
						sbmlID = sbmlMapElement.getAttributeValue("SBMLid");
					}
				}
				columnDescriptions[i] = new ODESolverResultSetColumnDescription(sbmlID);
			}
		}
		return copasiResultSet;
	}
	
	public static ODESolverResultSet readResultFile(File resultFile, String delimiter) throws IOException{
		String reportText = XmlUtil.getXMLString(resultFile.getAbsolutePath());
		//System.out.println(reportText);
		ODESolverResultSet odeResultSet = new ODESolverResultSet();
		StringTokenizer lineTokenizer = new StringTokenizer(reportText,"\n",false);
		int lineCount = 0;
		while (lineTokenizer.hasMoreElements()){
			lineCount++;
			String line = lineTokenizer.nextToken();
			StringTokenizer columnTokenizer = new StringTokenizer(line,delimiter,false);
			if (lineCount==1){
				while (columnTokenizer.hasMoreTokens()){
					String label = columnTokenizer.nextToken();
					odeResultSet.addDataColumn(new ODESolverResultSetColumnDescription(label));
				}
			}else{
				double[] values = new double[odeResultSet.getDataColumnCount()];
				for (int i = 0; i < values.length; i++) {
					values[i] = Double.parseDouble(columnTokenizer.nextToken());
				}
				odeResultSet.addRow(values);
			}
		}		
		//
		// replace column header for time (force name to "t")
		//
		odeResultSet.getColumnDescriptions()[0] = new ODESolverResultSetColumnDescription("t");
		return odeResultSet;
	}

}
