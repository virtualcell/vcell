package org.vcell.sbml.mathsbml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.vcell.sbml.SBMLSolver;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.SolverException;

import cbit.vcell.opt.solvers.OptSolverCallbacks.EvaluationHolder;

import com.wolfram.jlink.Expr;
import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

/**	Create a mathematica notebook containing SBMLRead, SBMLNDSolve and dataTable methods 
 *	for each SBML file in 'fileNames' which, when executed in Mathematica, will generate a corresponding .csv file
 *	for each SBML file in the specified location.
 * 
 * @author anu
 */

public class MathSBMLSolver implements SBMLSolver {
	
	private KernelLink ml = null;
	private File mathKernelExecutable = null;
	
	public MathSBMLSolver(File argMathKernelExecutable){
		this.mathKernelExecutable = argMathKernelExecutable;
	}

	public static void main(String[] args) {
		try {
			if (args.length!=2){
				System.out.println("Usage:\n MathSBMLSolver workingDir sbmlFilename");
				System.exit(0);
			}
			File workingDir = new File(args[0]);
			File sbmlFile = new File(args[1]);
			String sbmlText = SBMLUtils.readStringFromFile(sbmlFile.getAbsolutePath());
	
			MathSBMLSolver mathSBMLSolver = new MathSBMLSolver(new File("c:\\program files\\wolfram research\\mathematica\\7.0\\mathkernel.exe"));
			
			SimSpec simSpec = SimSpec.fromSBML(sbmlText);
			String prefixName = sbmlFile.getName();
			File resultsFile1 = mathSBMLSolver.solve(prefixName+"1", workingDir, sbmlText, simSpec);
			File resultsFile2 = mathSBMLSolver.solve(prefixName+"2", workingDir, sbmlText, simSpec);
			File resultsFile3 = mathSBMLSolver.solve(prefixName+"3", workingDir, sbmlText, simSpec);
			File resultsFile4 = mathSBMLSolver.solve(prefixName+"4", workingDir, sbmlText, simSpec);
			mathSBMLSolver.close();
			
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
public String getResultsFileColumnDelimiter(){
	return ",";
}

public void close() throws SolverException {
	try {
		if (ml!=null){
			ml.close();
			ml = null;
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new SolverException("failed to close MathSBMLSolver");
	}
}

public void open() throws SolverException {
	if (ml!=null){
		return;
	}
	
	try {
		ml = MathLinkFactory.createKernelLink(new String[] { "-linkmode", "launch", "-linkname", mathKernelExecutable.getAbsolutePath().replace('\\', '/') });
	} catch (MathLinkException e) {
		e.printStackTrace(System.out);
		throw new SolverException("Fatal error opening Mathematica link: " + e.getMessage());
	}

	try {
		// Get rid of the initial InputNamePacket the kernel will send
		// when it is launched.
		String command;
		String retcode;
		
		System.out.println("starting MathSBML commands");
		command = "<< mathSBML.m";
		retcode = ml.evaluateToOutputForm(command,0);
		System.out.println("> "+command);
		System.out.println(">>> "+retcode);
	}catch (Exception e){
		close();
		e.printStackTrace(System.out);
		throw new SolverException("failed to initialize MathSBML: "+e.getMessage());
	}
}

public File solve(String filePrefix, File outDir, String sbmlText, SimSpec testSpec) throws IOException, SolverException, SbmlException {
	if (!outDir.exists()){
		outDir.mkdirs();
	}
	File sbmlFile = new File(outDir,filePrefix+".sbml");
	SBMLUtils.writeStringToFile(sbmlText, sbmlFile.getAbsolutePath(), true);
	
	File csvFile = new File(outDir,filePrefix+".csv");
	if (csvFile.exists()){
		csvFile.delete();
	}
	String CONTEXT = "ABC";
	String CONTEXT_PREFIX = CONTEXT+"`";

	String varStringList = testSpec.getMathematicaVarsListString(CONTEXT_PREFIX);
	double endTime = testSpec.getEndTime();
	double interval = testSpec.getStepSize();

	open();

	try {
		// Get rid of the initial InputNamePacket the kernel will send
		// when it is launched.
		String command;
		String retcode;
		
		System.out.println("starting MathSBML commands");
		command = "SetDirectory[\""+outDir.getAbsolutePath().replace('\\', '/')+"\"]";
		retcode = ml.evaluateToOutputForm(command,0);
		System.out.println("> "+command);
		System.out.println(">>> "+retcode);
		System.out.println("XXXX "+ml.errorMessage());
		//command = "m = SBMLRead[\"" + sbmlFile.getName() + "\", context -> None, return->{SBMLUnitDefinitions->False,SBMLUnitAssociations->False,SBMLModelName->False,SBMLReactions->False,SBMLParameters->False,SBMLAlgebraicRules->False,SBMLODES->False,SBMLIC->False,SBMLCompartments->False,SBMLBoundaryConditions->False,SBMLConstants->False,SBMLAssignmentRules->False,SBMLNumericalSolution->"+endTime+"}]";
		command = "m = SBMLRead[\"" + sbmlFile.getName() + "\", context -> "+CONTEXT+"]";
		retcode = ml.evaluateToOutputForm(command,0);
		System.out.println("> "+command);
		//System.out.println(">>> "+retcode);
		System.out.println("XXXX "+ml.errorMessage());
		command = "n = SBMLNDSolve[m, " + testSpec.getEndTime() + ", MaxSteps -> Infinity]";
		retcode = ml.evaluateToOutputForm(command,0);
		System.out.println("> "+command);
		//System.out.println(">>> "+retcode);
		System.out.println("XXXX "+ml.errorMessage());
		command = "dataTable[" + varStringList + ", {t, 0, "+endTime+", "+interval+"}, n, file->\"" + csvFile.getName() + "\", format->\"CSV\"]";
		retcode = ml.evaluateToOutputForm(command,0);
		System.out.println("> "+command);
		//System.out.println(">>> "+retcode);
		System.out.println("XXXX "+ml.errorMessage());
		ml.waitForAnswer();
	} catch (MathLinkException e) {
		e.printStackTrace(System.out);
		throw new SolverException("MathLinkException occurred: " + e.getMessage());
		
	}
	
	close();
	// Write the mathematica notebook.
	/** Structure of the MathSBML code written out for each SBML file in filenames.

		<< mathsbml.m
		SetDirectory["C:/VCell/SBML_Testing/New_SBMLRepModels/"]
		m0 = SBMLRead["Bindschadler2001_coupled_Ca_oscillators.xml", context -> None]
		n0 = SBMLNDSolve[m0, 30.0, MaxSteps -> Infinity];
		dataTable[{c1, c2}, {t, 0, 30.0, 0.1}, Flatten[n0], file -> "Bindschadler2001_coupled_Ca_oscillators.csv", format -> "CSV"]

	**/
	if (!csvFile.exists()){
		throw new SolverException("mathSBML output file "+csvFile.getAbsolutePath()+" not found");
	}else{
		//
		// change header to remove context and rename time
		//
		String results = SBMLUtils.readStringFromFile(csvFile.getAbsolutePath());
		results = results.replace(CONTEXT_PREFIX, "");
		SBMLUtils.writeStringToFile(results, csvFile.getAbsolutePath(), false);
	}
	return csvFile;
}

public void finalize(){
	try {
		close();
	}catch (Exception e){
		e.printStackTrace(System.out);
	}
}
}

