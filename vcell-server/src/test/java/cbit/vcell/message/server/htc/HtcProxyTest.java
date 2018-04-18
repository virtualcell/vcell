package cbit.vcell.message.server.htc;

import org.junit.Assert;
import org.junit.Test;
import org.vcell.util.document.KeyValue;

import cbit.vcell.message.server.htc.HtcProxy.HtcJobInfo;
import cbit.vcell.message.server.htc.HtcProxy.SimTaskInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;

public class HtcProxyTest {

	@Test
	public void test_getSimTaskInfoFromSimJobName() throws NumberFormatException, HtcException {
//		CommandServiceLocal cmdService = new CommandServiceLocal();
//		String htcUser = "vcell";
//		HtcProxy htcProxy = new SlurmProxy(cmdService,htcUser);
		System.setProperty(PropertyLoader.vcellServerIDProperty,"ALPHA");
		Assert.assertEquals(new SimTaskInfo(new KeyValue("115785823"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_ALPHA_115785823_0_0"));
		Assert.assertEquals(new SimTaskInfo(new KeyValue("115785823"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_BETA_115785823_0_0"));
		Assert.assertEquals(new SimTaskInfo(new KeyValue("115785823"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_REL_115785823_0_0"));
		Assert.assertEquals(new SimTaskInfo(new KeyValue("115785823"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_TEST_115785823_0_0"));
		Assert.assertEquals(new SimTaskInfo(new KeyValue("115785823"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_TEST2_115785823_0_0"));
		Assert.assertEquals(new SimTaskInfo(new KeyValue("115785823"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_TEST3_115785823_0_0"));
		Assert.assertEquals(new SimTaskInfo(new KeyValue("115785823"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_TEST4_115785823_0_0"));
		Assert.assertEquals(new SimTaskInfo(new KeyValue("115785823"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_JUNK_115785823_0_0"));
		Assert.assertNotEquals(new SimTaskInfo(new KeyValue("555"),0,0), HtcProxy.getSimTaskInfoFromSimJobName("V_ALPHA_115785823_0_0"));
		
		// failing ones
		try {
			HtcProxy.getSimTaskInfoFromSimJobName("V_ALPHA_115785823_0");
			Assert.fail("SimTaskInfo "+"V_ALPHA_115785823_0"+" should have failed");
		}catch (HtcException e){
		}
		
		// failing ones
		try {
			HtcProxy.getSimTaskInfoFromSimJobName("ALPHA_115785823_0_0");
			Assert.fail("SimTaskInfo "+"V_ALPHA_115785823_0"+" should have failed");
		}catch (HtcException e){
		}
	}
	
	@Test
	public void test_isMyJob(){
		System.setProperty(PropertyLoader.vcellServerIDProperty,"ALPHA");
		Assert.assertTrue(HtcProxy.isMyJob(new HtcJobInfo(new HtcJobID("1200725", BatchSystemType.SLURM),"V_ALPHA_115785823_0_0")));
		Assert.assertFalse(HtcProxy.isMyJob(new HtcJobInfo(new HtcJobID("1200725", BatchSystemType.SLURM),"V_BETA_115785823_0_0")));
		
		System.setProperty(PropertyLoader.vcellServerIDProperty,"BETA");
		Assert.assertTrue(HtcProxy.isMyJob(new HtcJobInfo(new HtcJobID("1200725", BatchSystemType.SLURM),"V_BETA_115785823_0_0")));
	}

}
