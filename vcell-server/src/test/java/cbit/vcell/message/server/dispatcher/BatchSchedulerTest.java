package cbit.vcell.message.server.dispatcher;

import cbit.vcell.message.server.dispatcher.BatchScheduler.ActiveJob;
import cbit.vcell.message.server.dispatcher.BatchScheduler.SchedulerDecisions;
import cbit.vcell.message.server.htc.HtcProxy.PartitionStatistics;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimulationExecutionStatus;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.server.SimulationQueueEntryStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static cbit.vcell.message.server.dispatcher.BatchScheduler.SchedulerDecisionType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Fast")
public class BatchSchedulerTest {

	VCellServerID relSite = VCellServerID.getServerID("REL");
	VCellServerID otherSite = VCellServerID.getServerID("OTH");
	User owner1 = new User("user1", new KeyValue("1"));
	User owner2 = new User("user2", new KeyValue("2"));
	boolean pde = true;
	boolean ode = false;
	long age1 = 1;  // oldest
	long age2 = 2;
	long age3 = 3;  // newest

	ActiveJob job1 = makeJob(relSite, owner1,1, SchedulerStatus.WAITING, age1, pde);
	ActiveJob job2 = makeJob(otherSite, owner1,2, SchedulerStatus.WAITING, age1, pde);
	ActiveJob job3 = makeJob(otherSite, owner1,3, SchedulerStatus.RUNNING, age2, ode);
	ActiveJob job4 = makeJob(relSite, owner1, 4, SchedulerStatus.WAITING, age2, ode);
	ActiveJob job5 = makeJob(relSite, owner1, 5, SchedulerStatus.WAITING, age1, pde);
	ActiveJob job6 = makeJob(relSite, owner2, 6, SchedulerStatus.WAITING, age2, pde);
	ActiveJob job7 = makeJob(relSite, owner2, 7, SchedulerStatus.DISPATCHED, age3, pde);
	ActiveJob job8 = makeJob(relSite, owner2, 8, SchedulerStatus.WAITING, age3, ode);
	ActiveJob job9 = makeJob(otherSite, owner2, 9, SchedulerStatus.RUNNING, age2, ode);
	ActiveJob job10 = makeJob(relSite, owner2, 10, SchedulerStatus.WAITING, age2, pde);

	@Test
	public void test() {

        ArrayList<ActiveJob> activeJobs = new ArrayList<ActiveJob>(Arrays.asList(job1, job2, job3, job4, job5, job6, job7, job8, job9, job10));

		int numCpusAllocated = 5;
		int numCpusTotal = 10;
		double load = 0.4;
		PartitionStatistics partitionStatistics = new PartitionStatistics(numCpusAllocated, numCpusTotal, load);
		
		int userQuotaOde = 2;
		int userQuotaPde = 2;
		VCellServerID systemID = relSite;
		
		
		SchedulerDecisions schedulerDecisions = BatchScheduler.schedule(activeJobs, partitionStatistics, userQuotaOde, userQuotaPde, systemID,null);

		schedulerDecisions.show(null,null);

		assertEquals(schedulerDecisions.getDecisionType(job1), RUNNABLE_THIS_SITE);
		assertEquals(schedulerDecisions.getOrdinal(job1), 1);

		assertEquals(schedulerDecisions.getDecisionType(job2), RUNNABLE_OTHER_SITE);
		assertEquals(schedulerDecisions.getOrdinal(job2), 2);

		assertEquals(schedulerDecisions.getDecisionType(job3), ALREADY_RUNNING_OR_QUEUED);
		assertEquals(schedulerDecisions.getOrdinal(job3), -1);

		assertEquals(schedulerDecisions.getDecisionType(job4), RUNNABLE_THIS_SITE);
		assertEquals(schedulerDecisions.getOrdinal(job4), 0);

		assertEquals(schedulerDecisions.getDecisionType(job5), HELD_USER_QUOTA_PDE);
		assertEquals(schedulerDecisions.getOrdinal(job5), 3);

		assertEquals(schedulerDecisions.getDecisionType(job6), HELD_CLUSTER_RESOURCES);
		assertEquals(schedulerDecisions.getOrdinal(job6), 5);

		assertEquals(schedulerDecisions.getDecisionType(job7), ALREADY_RUNNING_OR_QUEUED);
		assertEquals(schedulerDecisions.getOrdinal(job7), -1);

		assertEquals(schedulerDecisions.getDecisionType(job8), RUNNABLE_THIS_SITE);
		assertEquals(schedulerDecisions.getOrdinal(job8), 4);

		assertEquals(schedulerDecisions.getDecisionType(job9), ALREADY_RUNNING_OR_QUEUED);
		assertEquals(schedulerDecisions.getOrdinal(job9), -1);

		assertEquals(schedulerDecisions.getDecisionType(job10), HELD_USER_QUOTA_PDE);
		assertEquals(schedulerDecisions.getOrdinal(job10), 6);
	}

	private static ActiveJob makeJob(VCellServerID serverID, User owner, int key,
									 SchedulerStatus schedulerStatus, long timestamp, boolean bPde) {
		SimulationJobStatus jobStatus = makeStatus(serverID, owner, key);
		return new ActiveJob(jobStatus,owner, schedulerStatus, timestamp, serverID, bPde);
	}

	private static SimulationJobStatus makeStatus(VCellServerID serverID, User owner, int key) {
		int taskID = 0;
		int jobIndex = 0;
		KeyValue simKey = new KeyValue(String.valueOf(key));
		SimulationMessage.DetailedState detailedState = null;
		HtcJobID htcJobID = null;
		SimulationQueueEntryStatus simQueueEntryStatus = null;
		SimulationExecutionStatus simExecStatus = null;
		return new SimulationJobStatus(
				serverID,
				new VCSimulationIdentifier(simKey,owner),
				jobIndex,
				new Date(),
				SimulationJobStatus.SchedulerStatus.WAITING,
				taskID,
				SimulationMessage.create(detailedState,"message",htcJobID),
				simQueueEntryStatus,
				simExecStatus);
	}

}
