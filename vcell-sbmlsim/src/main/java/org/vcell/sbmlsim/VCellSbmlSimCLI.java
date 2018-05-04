package org.vcell.sbmlsim;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.vcell.sbmlsim.api.common.SBMLModel;
import org.vcell.sbmlsim.api.common.SimData;
import org.vcell.sbmlsim.api.common.SimulationInfo;
import org.vcell.sbmlsim.api.common.SimulationSpec;
import org.vcell.sbmlsim.api.common.SimulationStatus;
import org.vcell.sbmlsim.api.common.TimePoints;
import org.vcell.sbmlsim.api.common.VariableInfo;
import org.vcell.sbmlsim.api.common.VcmlToSbmlResults;
import org.vcell.sbmlsim.service.SimulationService;
import org.vcell.sbmlsim.service.SimulationServiceImpl;

import com.google.gson.Gson;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class VCellSbmlSimCLI {
	
	private static class CliException extends Exception {

		public CliException(String message, Throwable cause) {
			super(message, cause);
		}

		public CliException(String message) {
			super(message);
		}

	}
	
    @Option(names = { "-v", "--verbose" }, description = "Be verbose.")
    private boolean verbose = false;
    
    @Option(names = { "-h", "--help" }, description = "Show help")
    private boolean showHelp = false;
    
    @Parameters(arity = "1", index = "0", paramLabel = "command", description = "[submit | meshinfo | simdata | status | timpoints | tosbml]")
    private String command;

    @Option(names = { "--simhandle" }, description = "Simulation Handle integer")
    private Integer simhandle;

    @Option(names = { "--sbmlfile" }, description = "sbml file")
    private File sbmlfile;
    
    @Option(names = { "--simspec" }, description = "simulation spec (JSON)")
	private String simspecJSON;
    
    @Option(names = { "--vcmlfile" }, description = "vcml file")
	private File vcmlfile;

    @Option(names = { "--application" }, description = "VCML Application Name")
	private String application;

    @Option(names = { "--timepoint" }, description = "timepoint index")
    private Integer timepointIndex;

    @Option(names = { "--varinfo" }, description = "variable info (JSON)")
    private String varinfoJSON;
    
    @Option(names = { "--outputfile" }, description = "output file (JSON)")
    private File outputfile;
    
	private static enum Command {
		submit,
		meshinfo,
		simdata,
		status,
		timepoints,
		varlist,
		tosbml
	};
	
	private Command cmd;
	
	private final SimulationService simService;
	
	public VCellSbmlSimCLI(SimulationService simService) {
		this.simService = simService;
	}

     
    public static void main(String[] args) {
		try {
			SimulationService simService = new SimulationServiceImpl();
			VCellSbmlSimCLI vCellSbmlSimCLI = new VCellSbmlSimCLI(simService);
			vCellSbmlSimCLI.parse(args);
			vCellSbmlSimCLI.process(simService);
			System.exit(0);
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
    }
    
    void parse(String[] args) {
		new CommandLine(this).parse(args);
		
		String parsedCommand = this.command;
		if (parsedCommand == null || Command.valueOf(parsedCommand) == null) {
			throw new RuntimeException("command '"+parsedCommand+"' not found");
		}
		this.cmd = Command.valueOf(parsedCommand);

    }
    
    void process(SimulationService simService) throws Exception {
		switch(cmd) {
		case meshinfo:{
			throw new RuntimeException("command 'meshinfo' not yet implemented");
//			SimulationServiceImpl simService = new SimulationServiceImpl();
//			simService.
//			break;
		}
		case simdata:{
			process_simdata();
			break;
		}
		case status:{
			process_status();
			break;
		}
		case submit:{
			process_submit();
			break;
		}
		case timepoints:{
			process_timepoints();
			break;
		}
		case tosbml:{
			process_tosbml();
			break;
		}
		case varlist:{
			break;
		}
		}
//		String cmd = vCellSbmlSimCLI.command[0];
    }
    
    private void process_simdata() throws Exception {
		Gson gson = new Gson();
		SimulationInfo simInfo = null;
		if (simhandle!=null) {
			simInfo = new SimulationInfo(simhandle);
		} else {
			throw new CliException("simhandle (simulation handle) not specified");
		}
		
		VariableInfo varInfo = null;
		if (varinfoJSON!=null) {
			try {
				varInfo = gson.fromJson(varinfoJSON, VariableInfo.class);
			}catch(Exception e) {
				throw new CliException("failed to parse JSON text for varInfo: "+e.getMessage(),e);
			}
		} else {
			throw new CliException("varinfo (variableInfo) not specified");
		}
		
		if (timepointIndex==null) {
			throw new CliException("timepoint index not specified");
		}
		
		if (outputfile==null) {
			throw new CliException("outputfile not specified");
		}
		
		SimData simdata = simService.getData(simInfo, varInfo, timepointIndex);
		
		String simdataJSON = gson.toJson(simdata);
		FileUtils.write(outputfile, simdataJSON);
    }
	
    private void process_status() throws Exception {
		Gson gson = new Gson();
		SimulationInfo simInfo = null;
		if (simhandle!=null) {
			simInfo = new SimulationInfo(simhandle);
		} else {
			throw new CliException("simhandle (simulation handle) not specified");
		}
		
		if (outputfile==null) {
			throw new CliException("output file not specified");
		}
		
		SimulationStatus simstatus = simService.getStatus(simInfo);
		
		String simstatusJSON = gson.toJson(simstatus);
		FileUtils.write(outputfile, simstatusJSON);
    }
	
    private void process_submit() throws Exception {
		Gson gson = new Gson();
		SimulationSpec simspec = null;
		if (simspecJSON!=null) {
			try {
				simspec = gson.fromJson(simspecJSON,SimulationSpec.class);
			}catch(Exception e) {
				throw new CliException("failed to parse JSON text for simspec (Simulation Specification): "+e.getMessage(),e);
			}
		} else {
			throw new CliException("simhandle (simulation handle option) not specified");
		}
		
		SBMLModel model = null;
		if (sbmlfile!=null) {
			model = new SBMLModel(sbmlfile);
		}else {
			throw new CliException("sbmlfile (SBML file option) not specified");
		}

		if (outputfile==null) {
			throw new CliException("output file not specified");
		}
				
		SimulationInfo siminfo = simService.computeModel(model, simspec);
		
		String siminfoJSON = gson.toJson(siminfo);
		FileUtils.write(outputfile, siminfoJSON);
    }
	
    private void process_timepoints() throws Exception {
		Gson gson = new Gson();
		SimulationInfo simInfo = null;
		if (simhandle!=null) {
			simInfo = new SimulationInfo(simhandle);
		} else {
			throw new CliException("simhandle (simulation handle) not specified");
		}
		
		if (outputfile==null) {
			throw new CliException("output file not specified");
		}
		
		TimePoints timePoints = simService.getTimePoints(simInfo);
		
		String timepointsJSON = gson.toJson(timePoints);
		FileUtils.write(outputfile, timepointsJSON);
    }
	
    private void process_tosbml() throws Exception {
		Gson gson = new Gson();
		if (vcmlfile==null) {
			throw new CliException("vcmlfile (simulation handle option) not specified");
		}else if (!vcmlfile.exists()) {
			throw new CliException("simhandle (simulation handle option) not specified");
		}
		
		if (outputfile==null) {
			throw new CliException("vcmlfile (simulation handle option) not specified");
		}else if (!outputfile.exists()) {
			throw new CliException("simhandle (simulation handle option) not specified");
		}
		
		if (application==null) {
			throw new CliException("application (VCML Application name option) not specified");
		}
		
		SBMLModel model = null;
		if (sbmlfile!=null) {
			model = new SBMLModel(sbmlfile);
		}else {
			throw new CliException("sbmlfile (SBML file option) not specified");
		}

		if (outputfile==null) {
			throw new CliException("output file not specified");
		}
				
		VcmlToSbmlResults vcmlToSbmlResults = simService.getSBML(vcmlfile, application, outputfile);
		String vcmlToSbmlResultsJSON = gson.toJson(vcmlToSbmlResults);
		FileUtils.write(outputfile, vcmlToSbmlResultsJSON);
    }
	

}