package cbit.vcell.message.server.dispatcher;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;

import com.google.gson.Gson;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationJobStatus;

//@Ignore
public class SimulationDatabaseDirectTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try (ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory();){
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, conFactory.getKeyFactory());
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
			boolean bCache = false;
			SimulationDatabaseDirect simulationDatabaseDirect = new SimulationDatabaseDirect(adminDbTopLevel, databaseServerImpl, bCache);
			SimpleJobStatusQuerySpec querySpec = new SimpleJobStatusQuerySpec();
			querySpec.submitLowMS	= null;
			querySpec.submitHighMS	= null;
			querySpec.startLowMS	= null;
			querySpec.startHighMS	= null;
			querySpec.endLowMS		= null;
			querySpec.endHighMS		= null;
			querySpec.startRow		= 0;
			querySpec.maxRows		= 100;
			querySpec.serverId		= null;
			querySpec.computeHost	= null;
			querySpec.userid		= null;
			querySpec.simId			= null;
			querySpec.jobId			= null;
			querySpec.taskId		= null;
			querySpec.hasData		= null;
			querySpec.waiting		= true;
			querySpec.queued		= true;
			querySpec.dispatched	= true;
			querySpec.running		= true;
			querySpec.completed		= true;
			querySpec.failed		= true;
			querySpec.stopped		= true;

			SimulationJobStatus[] resultList = simulationDatabaseDirect.queryJobs(querySpec);
			Gson gson = new Gson();
			for (SimulationJobStatus status : resultList) {
				System.out.println(gson.toJson(status.toRep()));
			}
			// test that there are no repeated jobs
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception: "+e.getMessage());
		}
	}

}
