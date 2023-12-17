package cbit.vcell.message.server.dispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import cbit.vcell.server.*;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Tag;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.dispatcher.BatchScheduler.ActiveJob;
import cbit.vcell.message.server.dispatcher.BatchScheduler.SchedulerDecisions;
import cbit.vcell.message.server.htc.HtcProxy.PartitionStatistics;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;

@Category(Fast.class)
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
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		ArrayList<BatchScheduler.ActiveJob> activeJobs = new ArrayList<BatchScheduler.ActiveJob>();
		activeJobs.addAll(Arrays.asList(job1,job2,job3,job4,job5,job6,job7,job8,job9,job10));

		int numCpusAllocated = 5;
		int numCpusTotal = 10;
		double load = 0.4;
		PartitionStatistics partitionStatistics = new PartitionStatistics(numCpusAllocated, numCpusTotal, load);
		
		int userQuotaOde = 2;
		int userQuotaPde = 2;
		VCellServerID systemID = relSite;
		
		
		SchedulerDecisions schedulerDecisions = BatchScheduler.schedule(activeJobs, partitionStatistics, userQuotaOde, userQuotaPde, systemID,null);

		schedulerDecisions.show(null,null);

		Assert.assertEquals(schedulerDecisions.getDecisionType(job1), BatchScheduler.SchedulerDecisionType.RUNNABLE_THIS_SITE);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job1), new Integer(1));

		Assert.assertEquals(schedulerDecisions.getDecisionType(job2), BatchScheduler.SchedulerDecisionType.RUNNABLE_OTHER_SITE);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job2), new Integer(2));

		Assert.assertEquals(schedulerDecisions.getDecisionType(job3), BatchScheduler.SchedulerDecisionType.ALREADY_RUNNING_OR_QUEUED);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job3), new Integer(-1));

		Assert.assertEquals(schedulerDecisions.getDecisionType(job4), BatchScheduler.SchedulerDecisionType.RUNNABLE_THIS_SITE);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job4), new Integer(0));

		Assert.assertEquals(schedulerDecisions.getDecisionType(job5), BatchScheduler.SchedulerDecisionType.HELD_USER_QUOTA_PDE);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job5), new Integer(3));

		Assert.assertEquals(schedulerDecisions.getDecisionType(job6), BatchScheduler.SchedulerDecisionType.HELD_CLUSTER_RESOURCES);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job6), new Integer(5));

		Assert.assertEquals(schedulerDecisions.getDecisionType(job7), BatchScheduler.SchedulerDecisionType.ALREADY_RUNNING_OR_QUEUED);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job7), new Integer(-1));

		Assert.assertEquals(schedulerDecisions.getDecisionType(job8), BatchScheduler.SchedulerDecisionType.RUNNABLE_THIS_SITE);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job8), new Integer(4));

		Assert.assertEquals(schedulerDecisions.getDecisionType(job9), BatchScheduler.SchedulerDecisionType.ALREADY_RUNNING_OR_QUEUED);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job9), new Integer(-1));

		Assert.assertEquals(schedulerDecisions.getDecisionType(job10), BatchScheduler.SchedulerDecisionType.HELD_USER_QUOTA_PDE);
		Assert.assertEquals(schedulerDecisions.getOrdinal(job10), new Integer(6));
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
