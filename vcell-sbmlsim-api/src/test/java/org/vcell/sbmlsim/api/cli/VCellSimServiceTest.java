package org.vcell.sbmlsim.api.cli;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.vcell.sbmlsim.api.common.SBMLModel;
import org.vcell.sbmlsim.api.common.SimulationSpec;
import org.vcell.sbmlsim.api.common.SimulationState;
import org.vcell.sbmlsim.api.common.TimePoints;
import org.vcell.sbmlsim.api.common.VariableList;

/**
 * Exercises the {@link SimulationServiceImpl}.
 */
@Ignore
public class VCellSimServiceTest {

	@Test
	public void test() throws URISyntaxException, Exception {
		VCellSimService simService = new VCellSimService();

		URL sbmlFileUrl = VCellSimServiceTest.class.getResource("../optoPlexin_PRG_rule_based.xml");
		File file = new File(sbmlFileUrl.toURI());

		Assert.assertTrue(file.exists());

		SBMLModel sbmlModel = new SBMLModel(file);

		SimulationSpec simSpec = new SimulationSpec();
		SimulationHandle simulationHandle = simService.submit(sbmlModel, simSpec);

		long timeMS = System.currentTimeMillis();
		while (simService.getStatus(simulationHandle).getSimState() != SimulationState.done
				&& simService.getStatus(simulationHandle).getSimState() != SimulationState.failed) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			long time2MS = System.currentTimeMillis();
			if (time2MS - timeMS > 10000) {
				fail("timed out after 10 seconds");
			}
		}

		VariableList vars = simService.getVariableList(simulationHandle);
		Assert.assertNotNull(vars);
		// TODO - Assert more things.

		final TimePoints timePoints = simService.getTimePoints(simulationHandle);
		
		Arrays.asList(vars.varInfos).stream().forEach(var -> {
			try {
				System.out.println(var.getVariableDisplayName() + "[0] = " + //
				simService.getSimData(simulationHandle, var, 0));
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
	}

}
