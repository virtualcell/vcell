package cbit.vcell.message.server.dispatcher;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.slurm.SlurmJobStatus;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.solvers.ExecutableCommand;
import org.vcell.util.DataAccessException;
import org.vcell.util.exe.ExecutableException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class MockHtcProxy extends HtcProxy {
    private final MockSimulationDB mockSimulationDB;
    public MockHtcProxy(CommandService commandService, String htcUser, MockSimulationDB mockSimulationDB) {
        super(commandService, htcUser);
        this.mockSimulationDB = mockSimulationDB;
    }
    public final ArrayList<HtcJobInfo> jobsKilledSafely = new ArrayList<>();

    @Override
    public void killJobSafe(HtcJobInfo htcJobInfo) throws ExecutableException, HtcJobNotFoundException, HtcException {
        jobsKilledSafely.add(htcJobInfo);
    }

    @Override
    public void killJobUnsafe(HtcJobID htcJobId) throws ExecutableException, HtcJobNotFoundException, HtcException {

    }

    @Override
    public void killJobs(String htcJobSubstring) throws ExecutableException, HtcJobNotFoundException, HtcException {

    }

    @Override
    public Map<HtcJobInfo, HtcJobStatus> getJobStatus(List<HtcJobInfo> requestedHtcJobInfos) throws ExecutableException, IOException {
        return Map.of();
    }

    @Override
    public HtcJobID submitJob(String jobName, File sub_file_internal, File sub_file_external, ExecutableCommand.Container commandSet, int ncpus, double memSize, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask, File primaryUserDirExternal) throws ExecutableException {
        return null;
    }

    @Override
    public HtcJobID submitOptimizationJob(String jobName, File sub_file_internal, File sub_file_external, File optProblemInputFile, File optProblemOutputFile, File optReportFile) throws ExecutableException {
        return null;
    }

    @Override
    public HtcProxy cloneThreadsafe() {
        return null;
    }

    @Override
    public Map<HtcJobInfo, HtcJobStatus> getRunningJobs() throws ExecutableException, IOException {
        HashMap<HtcJobInfo, HtcJobStatus> map = new HashMap<>();
        SimulationJobStatus[] statuses;
        try {
            statuses = mockSimulationDB.getActiveJobs(DispatcherTestUtils.testVCellServerID);
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        for (SimulationJobStatus status : statuses){
            if (status.getSchedulerStatus().isRunning()){
                HtcJobInfo jobInfo = new HtcJobInfo(DispatcherTestUtils.htcJobID, HtcProxy.createHtcSimJobName(new SimTaskInfo(status.getVCSimulationIdentifier().getSimulationKey(), status.getJobIndex(), status.getTaskID())));
                HtcJobStatus jobStatus = new HtcJobStatus(SlurmJobStatus.RUNNING);
                map.put(jobInfo, jobStatus);
            }
        }
        return map;
    }

    @Override
    public PartitionStatistics getPartitionStatistics() throws HtcException, ExecutableException, IOException {
        return new PartitionStatistics(1, 20, 100);
    }

    @Override
    public String getSubmissionFileExtension() {
        return "";
    }
}
