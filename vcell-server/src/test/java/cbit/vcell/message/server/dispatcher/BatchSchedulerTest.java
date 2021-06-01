package cbit.vcell.message.server.dispatcher;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.dispatcher.BatchScheduler.ActiveJob;
import cbit.vcell.message.server.dispatcher.BatchScheduler.SchedulerDecisions;
import cbit.vcell.message.server.htc.HtcProxy.PartitionStatistics;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;

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
	ActiveJob job1 = new ActiveJob("job1", owner1, SchedulerStatus.WAITING, age1, relSite, pde);
	ActiveJob job2 = new ActiveJob("job2", owner1, SchedulerStatus.WAITING, age1, otherSite, pde);
	ActiveJob job3 = new ActiveJob("job3", owner1, SchedulerStatus.RUNNING, age2, otherSite, ode);
	ActiveJob job4 = new ActiveJob("job4", owner1, SchedulerStatus.WAITING, age2, relSite, ode);
	ActiveJob job5 = new ActiveJob("job5", owner1, SchedulerStatus.WAITING, age1, relSite, pde);
	ActiveJob job6 = new ActiveJob("job6", owner2, SchedulerStatus.WAITING, age2, relSite, pde);
	ActiveJob job7 = new ActiveJob("job7", owner2, SchedulerStatus.DISPATCHED, age3, relSite, pde);
	ActiveJob job8 = new ActiveJob("job8", owner2, SchedulerStatus.WAITING, age3, relSite, ode);
	ActiveJob job9 = new ActiveJob("job9", owner2, SchedulerStatus.RUNNING, age2, otherSite, ode);
	ActiveJob job10 = new ActiveJob("job10", owner2, SchedulerStatus.WAITING, age2, relSite, pde);

	
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
		schedulerDecisions.show();
		
		//
		// enhance BatchScheduler to give reason why not to run each job that is not run.
		//
	}
}
