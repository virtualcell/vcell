package cbit.vcell.message.server.batch.sim;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.SundialsSolver;
import cbit.vcell.util.ColumnDescription;
import cbit.vcell.xml.XmlHelper;

public class AvgStochMultiTrial  implements PortableCommand{

	private final String primaryUserDirInternal;
	private final String xmlSimTask;
	private transient Exception exc = null; 

	public AvgStochMultiTrial(String primaryUserDirInternal, String xmlSimTask) {
		super();
		this.primaryUserDirInternal = primaryUserDirInternal;
		this.xmlSimTask = xmlSimTask;
	}
	@Override
	public int execute() {
		try {
			SimulationTask simTask = XmlHelper.XMLToSimTask(xmlSimTask);
			String s = avgAll(simTask, new File(primaryUserDirInternal));
			String allAvgIDA = ResourceUtil.forceUnixPath(new File(primaryUserDirInternal ,simTask.getSimulationJobID()+SimDataConstants.IDA_DATA_EXTENSION).toString());
			FileUtils.write(new File(allAvgIDA), s, Charset.forName(StandardCharsets.UTF_8.name()));
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			exc = e;
			return 1;
		}
	}				
	@Override
	public Exception exception() {
		return exc;
	}

	public static String avgAll(SimulationTask simTask, File primaryUserDirInternal) throws Exception{
		String idaSlurmRoot = simTask.getSimulationJobID()+SimDataConstants.IDA_DATA_EXTENSION;
		Exception lastError = null;
		int doCnt = 0;
		while(doCnt < 20) {
			try {
				File[] slurm_Task_Run_Files = primaryUserDirInternal.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.getName().startsWith(idaSlurmRoot+"_");
					}
				});
				if(simTask.getSimulation().getSolverTaskDescription().getStochOpt().getNumOfTrials() != slurm_Task_Run_Files.length) {
					throw new Exception("Expecting trialCount to match output files "+simTask.getSimulation().getSolverTaskDescription().getStochOpt().getNumOfTrials()+" != "+slurm_Task_Run_Files.length);
				}
				ODESolverResultSet odeMaster = null;
				for (int i = 0; i < slurm_Task_Run_Files.length; i++) {
					ODESolverResultSet odeSolverResultSet2 = new ODESolverResultSet();
					try (FileInputStream inputStream = new FileInputStream(slurm_Task_Run_Files[i])){
						SundialsSolver.readIDA(odeSolverResultSet2, inputStream);
						if(i==0) {
							odeMaster = odeSolverResultSet2;
							continue;
						}
						for (int r = 0; r < odeSolverResultSet2.getRowCount(); r++) {
							double[] row2 = odeSolverResultSet2.getRow(r);
							double[] rowMaster = odeMaster.getRow(r);
							for (int c = 1; c < odeSolverResultSet2.getDataColumnCount(); c++) {
								odeMaster.setValue(r, c, rowMaster[c]+row2[c]);
							}
						}
					} catch (IOException e) {
						throw e;
					}
				}
				StringBuffer sb = new StringBuffer();
				ColumnDescription[] columnDescriptions = odeMaster.getDataColumnDescriptions();
				for (int i = 0; i < columnDescriptions.length; i++) {
					sb.append((i>0?":":"")+columnDescriptions[i].getName());
				}
				sb.append("\n");
				for (int r = 0; r < odeMaster.getRowCount(); r++) {
					double[] rowMaster = odeMaster.getRow(r);
					for (int c = 0; c < odeMaster.getDataColumnCount(); c++) {
						sb.append((c>0?"\t":"")+(rowMaster[c]/(c>0?simTask.getSimulation().getSolverTaskDescription().getStochOpt().getNumOfTrials():1)));
					}
					sb.append("\n");
				}
		//		System.out.println(sb.toString());
		//		String allAvgIDA = ResourceUtil.forceUnixPath(new File(primaryUserDirInternal ,simTask.getSimulationJobID()+SimDataConstants.IDA_DATA_EXTENSION).toString());
		//		FileUtils.write(new File(allAvgIDA), sb.toString(), Charset.forName(StandardCharsets.UTF_8.name()));
				return sb.toString();
			}catch(Exception e) {
				doCnt++;
				e.printStackTrace();
				lastError = e;
			}
			Thread.sleep(10000);
		}
		throw new Exception("Exceed trys to AvgStochMultiTrial, last error = ",lastError);
	}

}
