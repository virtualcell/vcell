package org.vcell.vcellij.api;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vcell.vcellij.SimulationServiceImpl;

public class SimulationServiceImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws URISyntaxException, ThriftDataAccessException, TException {
		SimulationServiceImpl simService = new SimulationServiceImpl();
		URL sbmlFileUrl = SimulationServiceImplTest.class.getResource("../../sbml/optoPlexin_PRG_rule_based.xml");
		File file = new File(sbmlFileUrl.toURI());
		file = new File("/Users/schaff/Documents/workspace-modular/VCell_6.2/src/test/resources/org/vcell/sbml/optoPlexin_PRG_rule_based.xml");
		Assert.assertTrue(file.exists());
		
		SBMLModel sbmlModel = new SBMLModel(file.getAbsolutePath());
		
		SimulationSpec simSpec = new SimulationSpec();
		SimulationInfo simInfo = simService.computeModel(sbmlModel, simSpec);
		
		long timeMS = System.currentTimeMillis();
		while (simService.getStatus(simInfo).simState==SimulationState.running){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			long time2MS = System.currentTimeMillis();
			if (time2MS-timeMS>10000){
				fail("timed out after 10 seconds");
			}
		}
		
		List<VariableInfo> vars = simService.getVariableList(simInfo);
		Assert.assertNotNull(vars);
	}

}
