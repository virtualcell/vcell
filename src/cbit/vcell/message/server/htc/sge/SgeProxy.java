package cbit.vcell.message.server.htc.sge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;

import cbit.util.xml.XmlUtil;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.tools.PortableCommand;
import cbit.vcell.tools.PortableCommandWrapper;

public class SgeProxy extends HtcProxy {
	private final static int QDEL_JOB_NOT_FOUND_RETURN_CODE = 1;
	private final static String QDEL_UNKNOWN_JOB_RESPONSE = "does not exist";
	protected final static String SGE_SUBMISSION_FILE_EXT = ".sge.sub";
	private Map<HtcJobID, JobInfoAndStatus> statusMap;
	/**
	 * jobs that start with {@link HTC_SIMULATION_JOB_NAME_PREFIX}
	 */
	private List<HtcJobID> cachedList;
	

	// note: full commands use the PropertyLoader.htcPbsHome path.
	private final static String JOB_CMD_SUBMIT = "qsub";
	private final static String JOB_CMD_DELETE = "qdel";
	private final static String JOB_CMD_STATUS = "qstat";
	//private final static String JOB_CMD_QACCT = "qacct";
	private static String SGE_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcSgeHome);
	private static String htcLogDirString = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDir);
	private static String MPI_HOME= PropertyLoader.getRequiredProperty(PropertyLoader.MPI_HOME);
	static {
		if (!SGE_HOME.endsWith("/")){
			SGE_HOME += "/";
		}
	    if (!htcLogDirString.endsWith("/")){
	    	htcLogDirString = htcLogDirString+"/";
	    }
	}
	
	public SgeProxy(CommandService commandService, String htcUser) {
		super(commandService, htcUser);
		statusMap = new HashMap<HtcJobID,JobInfoAndStatus>( );
		cachedList = new ArrayList<>(); 
	}

	@Override
	public HtcJobStatus getJobStatus(HtcJobID htcJobId) throws HtcException, ExecutableException {
		if (statusMap.containsKey(htcJobId)) {
			return statusMap.get(htcJobId).status;
		}
		throw new HtcJobNotFoundException("job not found", htcJobId);
	}
	
	/**
	 * qdel 6894
	 * 
vcell has registered the job 6894 for deletion
	 *
	 * qdel 6894
	 * 
job 6894 is already in deletion
	 *
	 * qdel 6894
	 * 
denied: job "6894" does not exist

	 */
	
	
	@Override
	public void killJob(HtcJobID htcJobId) throws ExecutableException, HtcException {

		String[] cmd = new String[]{SGE_HOME + JOB_CMD_DELETE, Long.toString(htcJobId.getJobNumber())};
		try {
			//CommandOutput commandOutput = commandService.command(cmd, new int[] { 0, QDEL_JOB_NOT_FOUND_RETURN_CODE });
			
			CommandOutput commandOutput = commandService.command(cmd,new int[] { 0, QDEL_JOB_NOT_FOUND_RETURN_CODE });
			
			Integer exitStatus = commandOutput.getExitStatus();
			String standardOut = commandOutput.getStandardOutput();
			if (exitStatus!=null && exitStatus.intValue()==QDEL_JOB_NOT_FOUND_RETURN_CODE && standardOut!=null && standardOut.toLowerCase().contains(QDEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw new HtcJobNotFoundException(standardOut, htcJobId);
			}
		}catch (ExecutableException e){
			e.printStackTrace();
			if (!e.getMessage().toLowerCase().contains(QDEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw e;
			}else{
				throw new HtcJobNotFoundException(e.getMessage(), htcJobId);
			}
		}
	}
	
	/**
	 * build numerics command, adding MPICH command if necessary
	 * @param ncpus if != 1, {@link #MPI_HOME} command prepended
	 * @param cmds command set
	 * @return new String
	 */
	private final String buildExeCommand(int ncpus,String cmds[]) {
		if (ncpus == 1) {
			return CommandOutput.concatCommandStrings(cmds);
		}
		final char SPACE = ' ';
		StringBuilder sb = new StringBuilder( );
		sb.append(MPI_HOME);
		sb.append("/bin/mpiexec -np ");
		sb.append(ncpus);
		sb.append(SPACE);
		for (String s: cmds) {
			sb.append(s);
			sb.append(SPACE);
		}
		return sb.toString().trim( );
	}
	
	@Override
	protected SgeJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSize, String[] secondCommand, String[] exitCommand, String exitCodeReplaceTag, Collection<PortableCommand> postProcessingCommands) throws ExecutableException {
		try {
			final boolean isParallel = ncpus > 1;

			
			StringBuilder sb = new StringBuilder(); 

		    sb.append("#!/bin/csh\n");
		    sb.append("#$ -N " + jobName + "\n");
		    sb.append("#$ -o " + htcLogDirString+jobName+".sge.log\n");
//			sw.append("#$ -l mem=" + (int)(memSize + SGE_MEM_OVERHEAD_MB) + "mb");

			//int JOB_MEM_OVERHEAD_MB = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jobMemoryOverheadMB));

		    //long jobMemoryMB = (JOB_MEM_OVERHEAD_MB+((long)memSize));
		    sb.append("#$ -j y\n");
//		    sw.append("#$ -l h_vmem="+jobMemoryMB+"m\n");
		    sb.append("#$ -cwd\n");
		    if (isParallel) {
		    	final char NEWLINE = '\n';
			    sb.append("#$ -pe mpich ");
			    sb.append(ncpus);
			    sb.append(NEWLINE);
			    sb.append("#$ -v LD_LIBRARY_PATH=");
			    sb.append(MPI_HOME);
			    sb.append("/lib");
			    sb.append(NEWLINE);
		    }
		    sb.append("# the commands to be executed\n");
			sb.append("echo\n");
			sb.append("echo\n");
			sb.append("echo \"command1 = '"+CommandOutput.concatCommandStrings(command)+"'\"\n");
			sb.append("echo\n");
			sb.append("echo\n");
		    sb.append(CommandOutput.concatCommandStrings(command)+"\n");
		    sb.append("set retcode1 = $status\n");
		    sb.append("echo\n");
		    sb.append("echo\n");
		    sb.append("echo command1 returned $retcode1\n");
			if (secondCommand!=null){
				final String exeString  = buildExeCommand(ncpus, secondCommand);
				sb.append("if ( $retcode1 == 0 ) then\n");
				sb.append("		echo\n");
				sb.append("		echo\n");
				sb.append("     echo \"command2 = '" + exeString +"'\"\n");
				sb.append("		echo\n");
				sb.append("		echo\n");
				sb.append("     " + exeString + "\n");
				sb.append("     set retcode2 = $status\n");
				sb.append("		echo\n");
				sb.append("		echo\n");
				sb.append("     echo command2 returned $retcode2\n");
				sb.append("     echo returning return code $retcode2 to PBS\n");
				if (exitCommand!=null && exitCodeReplaceTag!=null){
					sb.append("		echo\n");
					sb.append("		echo\n");
					sb.append("     echo \"exitCommand = '"+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode2")+"'\"\n");
					sb.append("		echo\n");
					sb.append("		echo\n");
					sb.append("     "+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode2")+"\n");
					sb.append("		echo\n");
					sb.append("		echo\n");
				}
				sb.append("     exit $retcode2\n");
				sb.append("else\n");
				sb.append("		echo \"command1 failed, skipping command2\"\n");
				sb.append("     echo returning return code $retcode1 to SGE\n");
				if (exitCommand!=null && exitCodeReplaceTag!=null){
					sb.append("		echo\n");
					sb.append("		echo\n");
					sb.append("     echo \"exitCommand = '"+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode1")+"'\"\n");
					sb.append("		echo\n");
					sb.append("		echo\n");
					sb.append("     "+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode1")+"\n");
					sb.append("		echo\n");
					sb.append("		echo\n");
				}
				sb.append("     exit $retcode1\n");
				sb.append("endif\n");
			}else{
				sb.append("echo returning return code $retcode1 to SGE\n");
				if (exitCommand!=null && exitCodeReplaceTag!=null){
					sb.append("		echo\n");
					sb.append("		echo\n");
					sb.append("     echo \"exitCommand = '"+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode1")+"'\"\n");
					sb.append("		echo\n");
					sb.append("		echo\n");
					sb.append("     "+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode1")+"\n");
					sb.append("		echo\n");
					sb.append("		echo\n");
				}
				sb.append("exit $retcode1\n");
			}
			if (postProcessingCommands != null) {
				PortableCommandWrapper.insertCommands(sb, postProcessingCommands); 
			}
			
			File tempFile = File.createTempFile("tempSubFile", ".sub");

			writeUnixStyleTextFile(tempFile, sb.toString());
			
			// move submission file to final location (either locally or remotely).
			System.out.println("<<<SUBMISSION FILE>>> ... moving local file '"+tempFile.getAbsolutePath()+"' to remote file '"+sub_file+"'");
			commandService.pushFile(tempFile,sub_file);
			System.out.println("<<<SUBMISSION FILE START>>>\n"+FileUtils.readFileToString(tempFile)+"\n<<<SUBMISSION FILE END>>>\n");
			tempFile.delete();
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			return null;
		}

		String[] completeCommand = new String[] {SGE_HOME + JOB_CMD_SUBMIT, "-terse", sub_file};
		CommandOutput commandOutput = commandService.command(completeCommand);
		String jobid = commandOutput.getStandardOutput().trim();
		
		return new SgeJobID(jobid);
	}

	@Override
	public HtcProxy cloneThreadsafe() {
		return new SgeProxy(getCommandService().clone(), getHtcUser());
	}

	@Override
	public String getSubmissionFileExtension() {
		return SGE_SUBMISSION_FILE_EXT;
	}
	
	@Override
	public List<HtcJobID> getRunningJobIDs(String jobNamePrefix) throws ExecutableException {
		String[] cmds = {SGE_HOME + JOB_CMD_STATUS,"-f","-xml"};
		CommandOutput commandOutput = commandService.command(cmds);
		
		String output = commandOutput.getStandardOutput();
		parseXML(output,jobNamePrefix);
		return cachedList;
	}

	@Override
	public Map<HtcJobID,HtcJobInfo> getJobInfos(List<HtcJobID> htcJobIDs) throws ExecutableException {
			HashMap<HtcJobID,HtcJobInfo> jobInfoMap = new HashMap<HtcJobID,HtcJobInfo>();
			for (HtcJobID htcJobID : htcJobIDs){
				HtcJobInfo htcJobInfo = getJobInfo(htcJobID);
				if (htcJobInfo!=null){
					jobInfoMap.put(htcJobID,htcJobInfo);
				}
			}
			return jobInfoMap;
	}
	
	private static final String PSYM_QINFO = "queue_info";
	private static final String PSYM_QLIST = "Queue-List";
	//private static final String PSYM_NAME = "name";
	private static final String PSYM_JLIST = "job_list";
	private static final String PSYM_JNAME = "JB_name";
	private static final String PSYM_JNUMBER = "JB_job_number";
	private static final String PSYM_STATE = "state";
	private void parseXML(String xmlString, String prefix) {
		statusMap.clear();
		cachedList.clear();
		Document qstatDoc = XmlUtil.stringToXML(xmlString, null);
		Element rootElement = qstatDoc.getRootElement();
		Element qElement = rootElement.getChild(PSYM_QINFO);
		for (Element qList : XmlUtil.getChildren(qElement,PSYM_QLIST,Element.class) ) {
			//String name = qList.getChildText(PSYM_NAME);
			for (Element ji : XmlUtil.getChildren(qList, PSYM_JLIST, Element.class)) {
				String jname = ji.getChildText(PSYM_JNAME);
				if (prefix != null  && !jname.startsWith(prefix)) {
					continue;
				}
				String jn = ji.getChildText(PSYM_JNUMBER);
				String state = ji.getAttributeValue(PSYM_STATE);
				SgeJobID id = new SgeJobID(jn);
				HtcJobInfo hji = new HtcJobInfo(id,true,jname,null,null);
				HtcJobStatus status = new HtcJobStatus(SGEJobStatus.parseStatus(state));
				statusMap.put(id, new JobInfoAndStatus(hji, status));
				cachedList.add(id);
			}
		}
	}
	
	/**
	 * @param htcJobID
	 * @return job info or null
	 */
	public HtcJobInfo getJobInfo(HtcJobID htcJobID) {
		return statusMap.get(htcJobID).info;
	}
	
	public String[] getEnvironmentModuleCommandPrefix() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add("source");
		ar.add("/etc/profile.d/modules.sh;");
		ar.add("module");
		ar.add("load");
		ar.add(PropertyLoader.getProperty(PropertyLoader.sgeModulePath, "htc/sge")+";");
		return ar.toArray(new String[0]);
	}
	
	private static class JobInfoAndStatus {
		final HtcJobInfo info;
		final HtcJobStatus status;
		JobInfoAndStatus(HtcJobInfo info, HtcJobStatus status) {
			this.info = info;
			this.status = status;
		}
		
	}
}
