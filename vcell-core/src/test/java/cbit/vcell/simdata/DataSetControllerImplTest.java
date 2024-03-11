package cbit.vcell.simdata;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.junit.jupiter.api.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@Tag("Fast")
public class DataSetControllerImplTest {
	DataSetControllerImpl dsc = null;
	VCSimulationDataIdentifier vcDataIdentifier = null;
	OutputContext outputContext = new OutputContext(new AnnotatedFunction[] {});

	@BeforeEach
	public void setUp() throws Exception {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("../").getAbsolutePath());
		String message = "installation directory is "+PropertyLoader.getRequiredProperty(PropertyLoader.installationRoot);

		Cachetable cachetable = new Cachetable(2000,1000000L);
		File resourcesDirectory = new File("src/test/resources/simdata");
		File primarydir = resourcesDirectory;
		File secondarydir = resourcesDirectory;
		KeyValue simKey = new KeyValue("1771409053");
		VCSimulationIdentifier vcSimId = new VCSimulationIdentifier(new KeyValue("1771409053"), User.tempUser);
		vcDataIdentifier = new VCSimulationDataIdentifier(vcSimId, 0);
		dsc = new DataSetControllerImpl(cachetable, primarydir, secondarydir);
	}

	@AfterEach
	public void tearDown() {
	}

	@Test
	public void testDoDataOperation() throws DataAccessException {
		boolean bIncludeStartValuesInfo = true;
		DataOperation dataOperation = new DataOperation.DataProcessingOutputInfoOP(vcDataIdentifier,bIncludeStartValuesInfo, outputContext);
		DataProcessingOutputInfo results = (DataProcessingOutputInfo)dsc.doDataOperation(dataOperation);
		String[] varNames = results.getVariableNames();
		String[] expectedVarNames = {
				"C_cyt_average", "C_cyt_total", "C_cyt_min", "C_cyt_max", "Ran_cyt_average", 
				"Ran_cyt_total", "Ran_cyt_min", "Ran_cyt_max", "RanC_cyt_average", "RanC_cyt_total", 
				"RanC_cyt_min", "RanC_cyt_max", "RanC_nuc_average", "RanC_nuc_total", "RanC_nuc_min", 
				"RanC_nuc_max", "s2_average", "s2_total", "s2_min", "s2_max"};
		String[] expectedUnits = { 
				"uM", "molecules", "uM", "uM", "uM", 
				"molecules", "uM", "uM", "uM", "molecules", 
				"uM", "uM", "uM", "molecules", "uM", "uM", 
				"molecules.um-2", "molecules", "uM", "uM"};
		double[] expectedTimePoints = {0.0, 0.5, 1.0};
		double[] expectedStatistics_RanC_cyt_max = {8.9, 3.5890337679723476, 3.057119332620108};

		assertArrayEquals(varNames,expectedVarNames);
		assertArrayEquals(expectedTimePoints, results.getVariableTimePoints(), 1e-8);
		for (int i = 0; i<expectedVarNames.length; i++){
            Assertions.assertEquals(results.getVariableUnits(expectedVarNames[i]), expectedUnits[i]);
		}
		assertArrayEquals(expectedStatistics_RanC_cyt_max, results.getVariableStatValues().get("RanC_cyt_max"), 1e-8);
	}
	
	@Test
	public void testGetDataIdentifiers() throws DataAccessException, IOException {
		DataIdentifier[] dataIdentifiers = dsc.getDataIdentifiers(outputContext, vcDataIdentifier);
		String[] varNames = Arrays.stream(dataIdentifiers)
				.map(DataIdentifier::getName)
				.toList().toArray(new String[0]);
		String[] expectedVarNames = { 
				"C_cyt","Ran_cyt","RanC_cyt","RanC_nuc","s2",
				"vcRegionVolume","vcRegionArea","vcRegionVolume_subdomain1","vcRegionVolume_subdomain0","J_flux0",
				"J_r0","KFlux_nm_cyt","KFlux_nm_nuc","RanC_cyt_init_uM","Size_cyt",
				"Size_EC","Size_nm","Size_nuc","Size_pm","sobj_subdomain11_subdomain00_size",
				"vobj_subdomain00_size","vobj_subdomain11_size" };
		assertArrayEquals(expectedVarNames, varNames);
	}
}
