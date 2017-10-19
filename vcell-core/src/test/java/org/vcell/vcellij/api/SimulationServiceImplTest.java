package org.vcell.vcellij.api;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.vcell.vcellij.SimulationServiceImpl;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;

/**
 * Exercises the {@link SimulationServiceImpl}.
 * <p>
 * Requires {@code localsolvers/installSolvers.sh} to have been run beforehand
 * so that native solver executables are available. As such, it is ignored by
 * default; remove the {@code @Ignore} annotation to try it.
 * </p>
 */
@Ignore
public class SimulationServiceImplTest {

	@Test
	public void test() throws URISyntaxException, ThriftDataAccessException, TException {
		SimulationServiceImpl simService = new SimulationServiceImpl();
		URL sbmlFileUrl = SimulationServiceImplTest.class.getResource("../../sbml/optoPlexin_PRG_rule_based.xml");
		File file = new File(sbmlFileUrl.toURI());
		VCMongoMessage.enabled = false;

		System.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
		ResourceUtil.setNativeLibraryDirectory();
		NativeLib.HDF5.load();

		file = new File("src/test/resources/org/vcell/sbml/optoPlexin_PRG_rule_based.xml");
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

		final List<Double> timePoints = simService.getTimePoints(simInfo);
		vars.stream().forEach(var -> {
			try {
				System.out.println(var.getVariableDisplayName() + "[0] = " + //
					simService.getData(simInfo, var, 0));
			}
			catch (Exception exc) {
				exc.printStackTrace();
			}
		});
	}

}
