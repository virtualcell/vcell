package cbit.vcell.message.server.htc;

import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.message.server.htc.HtcProxy.SimTaskInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.KeyValue;

import static cbit.vcell.message.server.htc.HtcProxy.getSimTaskInfoFromSimJobName;
import static cbit.vcell.message.server.htc.HtcProxy.isMySimulationJob;
import static cbit.vcell.server.HtcJobID.BatchSystemType.SLURM;
import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class HtcProxyTest {

	@Test
	public void test_getSimTaskInfoFromSimJobName() throws NumberFormatException, HtcException {
//		CommandServiceLocal cmdService = new CommandServiceLocal();
//		String htcUser = "vcell";
//		HtcProxy htcProxy = new SlurmProxy(cmdService,htcUser);
		PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, "ALPHA");
		assertEquals(new SimTaskInfo(new KeyValue("115785823"), 0, 0), getSimTaskInfoFromSimJobName("V_ALPHA_115785823_0_0"));
		assertEquals(new SimTaskInfo(new KeyValue("115785823"), 0, 0), getSimTaskInfoFromSimJobName("V_BETA_115785823_0_0"));
		assertEquals(new SimTaskInfo(new KeyValue("115785823"), 0, 0), getSimTaskInfoFromSimJobName("V_REL_115785823_0_0"));
		assertEquals(new SimTaskInfo(new KeyValue("115785823"), 0, 0), getSimTaskInfoFromSimJobName("V_TEST_115785823_0_0"));
		assertEquals(new SimTaskInfo(new KeyValue("115785823"), 0, 0), getSimTaskInfoFromSimJobName("V_TEST2_115785823_0_0"));
		assertEquals(new SimTaskInfo(new KeyValue("115785823"), 0, 0), getSimTaskInfoFromSimJobName("V_TEST3_115785823_0_0"));
		assertEquals(new SimTaskInfo(new KeyValue("115785823"), 0, 0), getSimTaskInfoFromSimJobName("V_TEST4_115785823_0_0"));
		assertEquals(new SimTaskInfo(new KeyValue("115785823"), 0, 0), getSimTaskInfoFromSimJobName("V_JUNK_115785823_0_0"));
		assertNotEquals(new SimTaskInfo(new KeyValue("555"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_ALPHA_115785823_0_0"));
		
		// failing ones
		try {
			HtcProxy.getSimTaskInfoFromSimJobName("V_ALPHA_115785823_0");
			fail("SimTaskInfo " + "V_ALPHA_115785823_0" + " should have failed");
		}catch (HtcException e){
		}
		
		// failing ones
		try {
			HtcProxy.getSimTaskInfoFromSimJobName("ALPHA_115785823_0_0");
			fail("SimTaskInfo " + "V_ALPHA_115785823_0" + " should have failed");
		}catch (HtcException e){
		}
	}
	
	@Test
	public void test_isMyJob(){
		PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, "ALPHA");
		assertTrue(HtcProxy.isMySimulationJob(new HtcJobInfo(new HtcJobID("1200725", BatchSystemType.SLURM), "V_ALPHA_115785823_0_0")));
		assertFalse(isMySimulationJob(new HtcJobInfo(new HtcJobID("1200725", SLURM), "V_BETA_115785823_0_0")));

		PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, "BETA");
		assertTrue(HtcProxy.isMySimulationJob(new HtcJobInfo(new HtcJobID("1200725", BatchSystemType.SLURM), "V_BETA_115785823_0_0")));
	}

}
