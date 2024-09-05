package cbit.vcell.message.server.htc.slurm;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.solvers.ExecutableCommand;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.jupiter.api.*;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Tag("Fast")
public class SlurmProxyTest {

	private final HashMap<String,String> originalProperties = new HashMap<>();

	private void setProperty(String key, String value) {
		originalProperties.put(key, System.getProperty(key));
		System.setProperty(key, value);
	}

	private void restoreProperties() {
		for (String key : originalProperties.keySet()) {
			String originalPropertyValue = originalProperties.get(key);
			if (originalPropertyValue == null) {
				System.clearProperty(key);
			} else {
				System.setProperty(key, originalPropertyValue);
			}
		}
		originalProperties.clear();
	}

    @BeforeEach
    public void setup()
    {
		setProperty(PropertyLoader.vcellServerIDProperty,"REL");
		setProperty(PropertyLoader.htcLogDirExternal,"/share/apps/vcell3/htclogs");
		setProperty(PropertyLoader.slurm_partition,"vcell");
		setProperty(PropertyLoader.slurm_reservation,"");
		setProperty(PropertyLoader.slurm_qos,"vcell");
		setProperty(PropertyLoader.primarySimDataDirExternalProperty,"/share/apps/vcell3/users");
		setProperty(PropertyLoader.secondarySimDataDirExternalProperty,"/share/apps/vcell7/users");
		setProperty(PropertyLoader.jmsSimHostExternal, "rke-wn-01.cam.uchc.edu");
		setProperty(PropertyLoader.jmsSimPortExternal, "31618");
		setProperty(PropertyLoader.jmsSimRestPortExternal, "30163");
		setProperty(PropertyLoader.jmsUser, "clientUser");
		setProperty(PropertyLoader.jmsPasswordValue, "dummy");
		setProperty(PropertyLoader.mongodbHostExternal, "rke-wn-01.cam.uchc.edu");
		setProperty(PropertyLoader.mongodbPortExternal, "30019");
		setProperty(PropertyLoader.mongodbDatabase, "test");
		setProperty(PropertyLoader.vcellSoftwareVersion, "Rel_Version_7.6.0_build_28");
		setProperty(PropertyLoader.vcellbatch_singularity_image, "/state/partition1/singularityImages/ghcr.io_virtualcell_vcell-batch_d6825f4.img");
		setProperty(PropertyLoader.slurm_tmpdir, "/scratch/vcell");
		setProperty(PropertyLoader.slurm_central_singularity_dir, "/share/apps/vcell3/singularityImages");
		setProperty(PropertyLoader.slurm_local_singularity_dir, "/state/partition1/singularityImages");
		setProperty(PropertyLoader.slurm_singularity_module_name, "singularity/vcell-3.10.0");
		setProperty(PropertyLoader.simDataDirArchiveExternal, "/share/apps/vcell12/users");
		setProperty(PropertyLoader.simDataDirArchiveInternal, "/share/apps/vcell12/users");
		setProperty(PropertyLoader.nativeSolverDir_External, "/share/apps/vcell3/nativesolvers");
		setProperty(PropertyLoader.jmsBlobMessageMinSize, "100000");
		setProperty(PropertyLoader.simulationPostprocessor, "JavaPostprocessor64");
		setProperty(PropertyLoader.simulationPreprocessor, "JavaPreprocessor64");

		setProperty(PropertyLoader.primarySimDataDirInternalProperty, "/share/apps/vcell3/users");
		setProperty(PropertyLoader.vcellopt_singularity_image, "/state/partition1/singularityImages/ghcr.io_virtualcell_vcell-opt_d6825f4.img");

		setProperty(PropertyLoader.htcPowerUserMemoryFloorMB, "51200");
		setProperty(PropertyLoader.htcMinMemoryMB, "4096");
		setProperty(PropertyLoader.htcMaxMemoryMB, "81920");
	}

	@AfterEach
	public void teardown() {
		restoreProperties();
	}

	public String createScriptForNativeSolvers(String simTaskResourcePath, String[] command, String JOB_NAME) throws IOException, XmlParseException, ExpressionException {

		SimulationTask simTask = XmlHelper.XMLToSimTask(readTextFileFromResource(simTaskResourcePath));
		KeyValue simKey = simTask.getSimKey();

		SlurmProxy slurmProxy = new SlurmProxy(null, "vcell");
		File subFileExternal = new File("/share/apps/vcell3/htclogs/V_REL_"+simKey+"_0_0.slurm.sub");

		User simOwner = simTask.getSimulation().getVersion().getOwner();
		final int jobId = simTask.getSimulationJob().getJobIndex();

		// preprocessor
		String simTaskFilePathExternal = "/share/apps/vcell3/users/schaff/SimID_"+simKey+"_0__0.simtask.xml";
		File primaryUserDirExternal = new File("/share/apps/vcell3/users/schaff");
		List<String> args = new ArrayList<>( 4 );
		args.add( PropertyLoader.getRequiredProperty(PropertyLoader.simulationPreprocessor) );
		args.add( simTaskFilePathExternal );
		args.add( primaryUserDirExternal.getAbsolutePath() );
		ExecutableCommand preprocessorCmd = new ExecutableCommand(null, false, false,args);

		ExecutableCommand.LibraryPath libraryPath = new ExecutableCommand.LibraryPath("/usr/local/app/localsolvers/linux64");
		final ExecutableCommand solverCmd = new ExecutableCommand(libraryPath, command);

		// postprocessor
		final String SOLVER_EXIT_CODE_REPLACE_STRING = "SOLVER_EXIT_CODE_REPLACE_STRING";
		ExecutableCommand postprocessorCmd = new ExecutableCommand(null,false, false,
				PropertyLoader.getRequiredProperty(PropertyLoader.simulationPostprocessor),
				simKey.toString(),
				simOwner.getName(),
				simOwner.getID().toString(),
				Integer.toString(jobId),
				Integer.toString(simTask.getTaskID()),
				SOLVER_EXIT_CODE_REPLACE_STRING,
				subFileExternal.getAbsolutePath());
		postprocessorCmd.setExitCodeToken(SOLVER_EXIT_CODE_REPLACE_STRING);

		ExecutableCommand.Container commandSet = new ExecutableCommand.Container();
		commandSet.add(preprocessorCmd);
		commandSet.add(solverCmd);
		commandSet.add(postprocessorCmd);

		int NUM_CPUs = 1;
		int MEM_SIZE_MB = 1000;
		ArrayList<PortableCommand> postProcessingCommands = new ArrayList<>();
		return slurmProxy.createJobScriptText(JOB_NAME, commandSet, NUM_CPUs, MEM_SIZE_MB, postProcessingCommands, simTask);
	}

	public String createScriptForJavaSolvers(String simTaskResourcePath, String JOB_NAME) throws IOException, XmlParseException, ExpressionException {

		SimulationTask simTask = XmlHelper.XMLToSimTask(readTextFileFromResource(simTaskResourcePath));
		KeyValue simKey = simTask.getSimKey();

		SlurmProxy slurmProxy = new SlurmProxy(null, "vcell");
		File subFileExternal = new File("/share/apps/vcell3/htclogs/V_REL_"+simKey+"_0_0.slurm.sub");

		User simOwner = simTask.getSimulation().getVersion().getOwner();
		final int jobId = simTask.getSimulationJob().getJobIndex();

		final ExecutableCommand.LibraryPath libraryPath = null;
		String command = "JavaSimExe64";
		String userDir = "/share/apps/vcell3/users/schaff";
		String simTaskRemoteFilename = "/share/apps/vcell3/users/schaff/SimID_"+simKey+"_0__0.simtask.xml";
        final ExecutableCommand solverCmd = new ExecutableCommand(libraryPath, command, simTaskRemoteFilename, userDir);

		// postprocessor
		final String SOLVER_EXIT_CODE_REPLACE_STRING = "SOLVER_EXIT_CODE_REPLACE_STRING";
		ExecutableCommand postprocessorCmd = new ExecutableCommand(null,false, false,
				PropertyLoader.getRequiredProperty(PropertyLoader.simulationPostprocessor),
				simKey.toString(),
				simOwner.getName(),
				simOwner.getID().toString(),
				Integer.toString(jobId),
				Integer.toString(simTask.getTaskID()),
				SOLVER_EXIT_CODE_REPLACE_STRING,
				subFileExternal.getAbsolutePath());
		postprocessorCmd.setExitCodeToken(SOLVER_EXIT_CODE_REPLACE_STRING);

		ExecutableCommand.Container commandSet = new ExecutableCommand.Container();
		commandSet.add(solverCmd);
		commandSet.add(postprocessorCmd);

		int NUM_CPUs = 1;
		int MEM_SIZE_MB = 1000;
		ArrayList<PortableCommand> postProcessingCommands = new ArrayList<>();
		return slurmProxy.createJobScriptText(JOB_NAME, commandSet, NUM_CPUs, MEM_SIZE_MB, postProcessingCommands, simTask);
	}

	public String createScriptForOptimizations(String JOB_NAME, int job_id) throws IOException, XmlParseException, ExpressionException {
		SlurmProxy slurmProxy = new SlurmProxy(null, "vcell");
		File optProblemInputFile = new File("/share/apps/vcell3/users/parest_data/CopasiParest_"+job_id+"_optProblem.json");
		File optProblemOutputFile = new File("/share/apps/vcell3/users/parest_data/CopasiParest_"+job_id+"_optRun.json");
		File optProblemReportFile = new File("/share/apps/vcell3/users/parest_data/CopasiParest_"+job_id+"_optReport.txt");
		return slurmProxy.createOptJobScript(JOB_NAME, optProblemInputFile, optProblemOutputFile, optProblemReportFile);
	}

	@Test
	public void testOptimization() throws IOException, XmlParseException, ExpressionException {
		String JOB_NAME = "CopasiParest_152878";
		int job_id = 152878;
		String slurmScript = createScriptForOptimizations(JOB_NAME, job_id);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/opt/CopasiParest_152878.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptFiniteVolume() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/finite_volume/SimID_274514696_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274514696_0_0";

		String executable = "/usr/local/app/localsolvers/linux64/FiniteVolume_x64";
		String inputFilePath = "/share/apps/vcell3/users/schaff/SimID_274514696_0_.fvinput";
		String[] command = new String[] { executable, inputFilePath, "-tid", "0" };

		String slurmScript = createScriptForNativeSolvers(simTaskResourcePath, command, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/finite_volume/V_REL_274514696_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptSmoldyn() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/smoldyn/SimID_274630052_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274630052_0_0";

		String executable = "/usr/local/app/localsolvers/linux64/smoldyn_x64";
		String inputFilePath = "/share/apps/vcell3/users/schaff/SimID_274630052_0_.smoldynInput";
		String[] command = new String[] { executable, inputFilePath, "-tid", "0" };

		String slurmScript = createScriptForNativeSolvers(simTaskResourcePath, command, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/smoldyn/V_REL_274630052_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptCVODE() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/cvode/SimID_274630682_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274630682_0_0";

		String executable = "/usr/local/app/localsolvers/linux64/SundialsSolverStandalone_x64";
		String inputFilePath = "/share/apps/vcell3/users/schaff/SimID_274630682_0_.cvodeInput";
		String outputFilePath = "/share/apps/vcell3/users/schaff/SimID_274630682_0_.ida";
		String[] command = new String[] { executable, inputFilePath, outputFilePath, "-tid", "0" };

		String slurmScript = createScriptForNativeSolvers(simTaskResourcePath, command, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/cvode/V_REL_274630682_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptLangevin() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/langevin/SimID_274672135_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274672135_0_0";

		String executable = "/usr/local/app/localsolvers/linux64/langevin_x64";
		String outputLog = "/share/apps/vcell3/users/schaff/SimID_274672135_0_.log";
		String messagingConfig = "/share/apps/vcell3/users/schaff/SimID_274672135_0_.langevinMessagingConfig";
		String inputFilePath = "/share/apps/vcell3/users/schaff/SimID_274672135_0_.langevinInput";
		String[] command = new String[] { executable, "simulate", "--output-log="+outputLog,
				"--vc-send-status-config="+messagingConfig, inputFilePath, "0", "-tid", "0" };

		String slurmScript = createScriptForNativeSolvers(simTaskResourcePath, command, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/langevin/V_REL_274672135_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptNFsim() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/nfsim/SimID_274642453_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274642453_0_0";

		String executable = "/usr/local/app/localsolvers/linux64/NFsim_x64";
		String inputFilePath = "/share/apps/vcell3/users/schaff/SimID_274642453_0_.nfsimInput";
		String gdatFilePath = "/share/apps/vcell3/users/schaff/SimID_274642453_0_.gdat";
		String speciesFilePath = "/share/apps/vcell3/users/schaff/SimID_274642453_0_.species";
		String[] command = new String[] { executable, "-seed", "716746135", "-vcell", "-xml", inputFilePath,
				"-o", gdatFilePath, "-sim", "1.0", "-ss", speciesFilePath, "-oSteps", "20", "-notf", "-utl", "1000",
				"-cb", "-pcmatch", "-tid", "0" };

		String slurmScript = createScriptForNativeSolvers(simTaskResourcePath, command, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/nfsim/V_REL_274642453_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptGibsonMilstein() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/gibson_milstein/SimID_274641698_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274641698_0_0";

		String executable = "/usr/local/app/localsolvers/linux64/Hybrid_EM_x64";
		String inputFilePath = "/share/apps/vcell3/users/schaff/SimID_274641698_0_.nc";
		String[] command = new String[] { executable, inputFilePath, "100.0", "10.0", "0.01", "0.1", "-OV", "-tid", "0" };

		String slurmScript = createScriptForNativeSolvers(simTaskResourcePath, command, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/gibson_milstein/V_REL_274641698_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptMovingBoundary() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/moving_boundary/SimID_274641196_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274641196_0_0";

		String executable = "/usr/local/app/localsolvers/linux64/MovingBoundary_x64";
		String inputFilePath = "/share/apps/vcell3/users/schaff/SimID_274641196_0_mb.xml";
		String[] command = new String[] { executable, "--config", inputFilePath, "-tid", "0" };

		String slurmScript = createScriptForNativeSolvers(simTaskResourcePath, command, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/moving_boundary/V_REL_274641196_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptGibson() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/gibson/SimID_274635122_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274635122_0_0";

		String executable = "/usr/local/app/localsolvers/linux64/VCellStoch_x64";
		String subcommand = "gibson";
		String inputFilePath = "/share/apps/vcell3/users/schaff/SimID_274635122_0_.stochInput";
		String outputFilePath = "/share/apps/vcell3/users/schaff/SimID_274635122_0_.ida";
		String[] command = new String[] { executable, subcommand, inputFilePath, outputFilePath, "-tid", "0" };

		String slurmScript = createScriptForNativeSolvers(simTaskResourcePath, command, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/gibson/V_REL_274635122_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptRK45() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/runge_kutta_fehlberg/SimID_274631114_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274631114_0_0";
		String slurmScript = createScriptForJavaSolvers(simTaskResourcePath, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/runge_kutta_fehlberg/V_REL_274631114_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	@Test
	public void testSimJobScriptAdamsMoulton() throws IOException, XmlParseException, ExpressionException {
		String simTaskResourcePath = "slurm_fixtures/adams_moulton/SimID_274633859_0__0.simtask.xml";
		String JOB_NAME = "V_REL_274633859_0_0";
		String slurmScript = createScriptForJavaSolvers(simTaskResourcePath, JOB_NAME);
		String expectedSlurmScript = readTextFileFromResource("slurm_fixtures/adams_moulton/V_REL_274633859_0_0.slurm.sub");
		Assertions.assertEquals(expectedSlurmScript.trim(), slurmScript.trim());
	}

	private String readTextFileFromResource(String filename) throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
		if (inputStream == null) {
			throw new IOException("Resource not found: " + filename);
		}
		String xmlString;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			xmlString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
		}
		return xmlString;
	}

//	@Disabled // this test is disabled because it requires a running slurm server
//	@Test
//	public void testSingularitySupport() {
//		CommandServiceSshNative cmd = null;
//		try {
//			Random r = new Random();
//			System.setProperty("log4j2.trace","true");
//			PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, "Test2");
//			PropertyLoader.setProperty(PropertyLoader.htcLogDirExternal, "/Volumes/vcell/htclogs");
//			VCMongoMessage.enabled=false;
//			String partitions[] = new String[] { "vcell", "vcell2" };
//			PropertyLoader.setProperty(PropertyLoader.slurm_partition, partitions[1]);
//
//
//			cmd = new CommandServiceSshNative(new String[] {"vcell-service.cam.uchc.edu"}, "vcell", new File("/Users/schaff/.ssh/schaff_rsa"));
//			SlurmProxy slurmProxy = new SlurmProxy(cmd, "vcell");
//
//			String jobName = "V_TEST2_999999999_0_"+r.nextInt(10000);
//			System.out.println("job name is "+jobName);
//			File sub_file_localpath = new File("/Volumes/vcell/htclogs/"+jobName+".slurm.sub");
//			File sub_file_remotepath = new File("/share/apps/vcell3/htclogs/"+jobName+".slurm.sub");
//
//			StringBuilder subfileContent = new StringBuilder();
//			subfileContent.append("#!/usr/bin/bash\n");
//			String partition = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition);
//			subfileContent.append("#SBATCH --partition="+partition+"\n");
//			subfileContent.append("#SBATCH -J "+jobName+"\n");
//			subfileContent.append("#SBATCH -o /share/apps/vcell3/htclogs/"+jobName+".slurm.log\n");
//			subfileContent.append("#SBATCH -e /share/apps/vcell3/htclogs/"+jobName+".slurm.log\n");
//			subfileContent.append("#SBATCH --mem=1000M\n");
//			subfileContent.append("#SBATCH --no-kill\n");
//			subfileContent.append("#SBATCH --no-requeue\n");
//			subfileContent.append("env\n");
//			subfileContent.append("echo `hostname`\n");
//			subfileContent.append("python -c \"some_str = ' ' * 51200000\"\n");
//			subfileContent.append("retcode=$?\n");
//			subfileContent.append("echo \"return code was $retcode\"\n");
//			subfileContent.append("if [[ $retcode == 137 ]]; then\n");
//			subfileContent.append("   echo \"job was killed via kill -9 (probably out of memory)\"\n");
//			subfileContent.append("fi\n");
//			subfileContent.append("sleep 20\n");
//			subfileContent.append("exit $retcode\n");
//			//subfileContent.append("export MODULEPATH=/isg/shared/modulefiles:/tgcapps/modulefiles\n");
//			//subfileContent.append("source /usr/share/Modules/init/bash\n");
////			subfileContent.append("module load singularity\n");
////			subfileContent.append("if command -v singularity >/dev/null 2>&1; then\n");
////			subfileContent.append("   echo 'singularity command exists'\n");
////			subfileContent.append("   exit 0\n");
////			subfileContent.append("else\n");
////			subfileContent.append("   echo 'singularity command not found'\n");
////			subfileContent.append("   exit 1\n");
////			subfileContent.append("fi\n");
//
//			FileUtils.writeStringToFile(sub_file_localpath, subfileContent.toString());
//			HtcJobID htcJobId = slurmProxy.submitJobFile(sub_file_remotepath);
//			System.out.println("running job "+htcJobId);
//			HtcJobInfo htcJobInfo = new HtcJobInfo(htcJobId, jobName);
//
//			ArrayList<HtcJobInfo> jobInfos = new ArrayList<HtcJobInfo>();
//			jobInfos.add(htcJobInfo);
//
//			Map<HtcJobInfo, HtcJobStatus> jobStatusMap = slurmProxy.getJobStatus(jobInfos);
//
//			int attempts = 0;
//			while (attempts<80 && (jobStatusMap.get(htcJobInfo)==null || !jobStatusMap.get(htcJobInfo).isDone())){
//				try { Thread.sleep(1000); } catch (InterruptedException e){}
//				jobStatusMap = slurmProxy.getJobStatus(jobInfos);
//				System.out.println(jobStatusMap.get(htcJobInfo));
//				if (attempts==5) {
//					slurmProxy.killJobs(jobName);
//				}
//				attempts++;
//			}
//			System.out.println(jobStatusMap.get(htcJobInfo));
//
//		}catch (Exception e) {
//			e.printStackTrace();
//            Assertions.fail(e.getMessage());
//		}finally {
//			if (cmd != null) {
//				cmd.close();
//			}
//		}
//	}
//
//
//	@Disabled // this test is disabled because it requires a running slurm server
//	@Test
//	public void testSLURM() throws IOException, ExecutableException {
//		System.setProperty("log4j2.trace","true");
//		PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, "Test2");
//		VCMongoMessage.enabled=false;
//		String partitions[] = new String[] { "vcell", "vcell2" };
//		PropertyLoader.setProperty(PropertyLoader.slurm_partition, partitions[0]);
//
//		CommandServiceSshNative cmd = null;
//		try {
//			cmd = new CommandServiceSshNative(new String[] {"vcell-service.cam.uchc.edu"}, "vcell", new File("/Users/schaff/.ssh/schaff_rsa"));
//			SlurmProxy slurmProxy = new SlurmProxy(cmd, "vcell");
//			Map<HtcJobInfo, HtcJobStatus> runningJobs = slurmProxy.getRunningJobs();
//			for (HtcJobInfo jobInfo : runningJobs.keySet()) {
//				HtcJobStatus jobStatus = runningJobs.get(jobInfo);
//				System.out.println("job "+jobInfo.getHtcJobID()+" "+jobInfo.getJobName()+", status="+jobStatus.toString());
//			}
//			for (String partition : partitions) {
//				PropertyLoader.setProperty(PropertyLoader.slurm_partition, partition);
//				PartitionStatistics partitionStatistics = slurmProxy.getPartitionStatistics();
//				System.out.println("partition statistics for partition "+partition+": "+partitionStatistics);
//				System.out.println("number of cpus allocated = "+partitionStatistics.numCpusAllocated);
//				System.out.println("load = "+partitionStatistics.load);
//				System.out.println("number of cpus total = "+partitionStatistics.numCpusTotal);
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//            Assertions.fail(e.getMessage());
//		}finally {
//			if (cmd != null) {
//				cmd.close();
//			}
//		}
//	}

}
