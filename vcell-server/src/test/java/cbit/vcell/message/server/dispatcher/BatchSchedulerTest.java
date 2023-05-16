package cbit.vcell.message.server.dispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.SimulationExecutionStatus;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationQueueEntryStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import org.checkerframework.checker.units.qual.K;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

	ActiveJob job1 = new ActiveJob(makeStatus(relSite,owner1), owner1, SchedulerStatus.WAITING, age1, relSite, pde);
	ActiveJob job2 = new ActiveJob(makeStatus(otherSite,owner1), owner1, SchedulerStatus.WAITING, age1, otherSite, pde);
	ActiveJob job3 = new ActiveJob(makeStatus(otherSite,owner1), owner1, SchedulerStatus.RUNNING, age2, otherSite, ode);
	ActiveJob job4 = new ActiveJob(makeStatus(relSite,owner1), owner1, SchedulerStatus.WAITING, age2, relSite, ode);
	ActiveJob job5 = new ActiveJob(makeStatus(relSite,owner1), owner1, SchedulerStatus.WAITING, age1, relSite, pde);
	ActiveJob job6 = new ActiveJob(makeStatus(relSite,owner2), owner2, SchedulerStatus.WAITING, age2, relSite, pde);
	ActiveJob job7 = new ActiveJob(makeStatus(relSite,owner2), owner2, SchedulerStatus.DISPATCHED, age3, relSite, pde);
	ActiveJob job8 = new ActiveJob(makeStatus(relSite,owner2), owner2, SchedulerStatus.WAITING, age3, relSite, ode);
	ActiveJob job9 = new ActiveJob(makeStatus(otherSite,owner2), owner2, SchedulerStatus.RUNNING, age2, otherSite, ode);
	ActiveJob job10 = new ActiveJob(makeStatus(relSite,owner2), owner2, SchedulerStatus.WAITING, age2, relSite, pde);

	
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
		
		//
		// enhance BatchScheduler to give reason why not to run each job that is not run.
		//
	}

	private static SimulationJobStatus makeStatus(VCellServerID serverID, User owner) {
		Random r = new Random();
		int taskID = r.nextInt(100);
		int jobIndex = 0;
		KeyValue simKey = new KeyValue(Long.toString(Math.abs(r.nextLong())));
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
