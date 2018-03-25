package cbit.vcell.simdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.DataOperationResults.DataProcessingOutputInfo;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import junit.framework.TestCase;

public class DataSetControllerImplTest extends TestCase {
	DataSetControllerImpl dsc = null;
	VCSimulationDataIdentifier vcDataIdentifier = null;
	OutputContext outputContext = new OutputContext(new AnnotatedFunction[] {});

	@Before
	public void setUp() throws Exception {
		System.setProperty(PropertyLoader.installationRoot,new File("../").getAbsolutePath());
		String message = "installation directory is "+PropertyLoader.getRequiredProperty(PropertyLoader.installationRoot);
		ResourceUtil.setNativeLibraryDirectory();
		NativeLib.HDF5.load();

		Cachetable cachetable = new Cachetable(2000);
		File resourcesDirectory = new File("src/test/resources/simdata");
		File primarydir = resourcesDirectory;
		File secondarydir = resourcesDirectory;
		KeyValue simKey = new KeyValue("1771409053");
		VCSimulationIdentifier vcSimId = new VCSimulationIdentifier(new KeyValue("1771409053"), User.tempUser);
		vcDataIdentifier = new VCSimulationDataIdentifier(vcSimId, 0);
		dsc = new DataSetControllerImpl(cachetable, primarydir, secondarydir);
	}

	@After
	public void tearDown() throws Exception {
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

		Assert.assertArrayEquals(varNames,expectedVarNames);
		Assert.assertArrayEquals(expectedTimePoints, results.getVariableTimePoints(), 1e-8);
		for (int i = 0; i<expectedVarNames.length; i++){
			Assert.assertEquals(results.getVariableUnits(expectedVarNames[i]), expectedUnits[i]);
		}
		Assert.assertArrayEquals(expectedStatistics_RanC_cyt_max, results.getVariableStatValues().get("RanC_cyt_max"), 1e-8);
	}
	
	@Test
	public void testGetDataIdentifiers() throws FileNotFoundException, DataAccessException, IOException {
		DataIdentifier[] dataIdentifiers = dsc.getDataIdentifiers(outputContext, vcDataIdentifier);
		String[] varNames = Arrays.asList(dataIdentifiers)
				.stream()
				.map (a -> a.getName()) 
				.collect(Collectors.toList()).toArray(new String[0]);
		String[] expectedVarNames = { 
				"C_cyt","Ran_cyt","RanC_cyt","RanC_nuc","s2",
				"vcRegionVolume","vcRegionArea","vcRegionVolume_subdomain1","vcRegionVolume_subdomain0","J_flux0",
				"J_r0","KFlux_nm_cyt","KFlux_nm_nuc","RanC_cyt_init_uM","Size_cyt",
				"Size_EC","Size_nm","Size_nuc","Size_pm","sobj_subdomain11_subdomain00_size",
				"vobj_subdomain00_size","vobj_subdomain11_size" };
		Assert.assertArrayEquals(expectedVarNames, varNames);
	}

	//
//	@Ignore
//	@Test
//	public void testFieldDataFileOperation() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetDataSetTimes() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetFunction() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetFunctions() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetLineScan() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetMesh() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetODEDataBlock() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetParticleDataBlock() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetParticleDataExists() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetSimDataBlock() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetTimeSeriesValues() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testWriteFieldFunctionData() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetDataSetMetadata() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetDataSetTimeSeries() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetIsChombo() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetIsMovingBoundary() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetIsComsol() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetChomboFiles() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVCellSimFiles() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetMovingBoundarySimFiles() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetComsolSimFiles() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetEmptyVtuMeshFilesComsolSimFilesVCDataIdentifierInt() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetEmptyVtuMeshFilesChomboFilesVCDataIdentifierInt() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetEmptyVtuMeshFilesVCellSimFilesVCDataIdentifierInt() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetEmptyVtuMeshFilesMovingBoundarySimFilesVCDataIdentifierInt() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVtuTimes() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVtuMeshDataComsolSimFilesOutputContextVCDataIdentifierVtuVarInfoDouble() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVtuMeshDataChomboFilesOutputContextVCDataIdentifierVtuVarInfoDouble() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVtuMeshDataMovingBoundarySimFilesOutputContextVCDataIdentifierVtuVarInfoDouble() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVtuMeshDataVCellSimFilesOutputContextVCDataIdentifierVtuVarInfoDouble() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVtuVarInfosVCellSimFilesOutputContextVCDataIdentifier() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVtuVarInfosChomboFilesOutputContextVCDataIdentifier() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVtuVarInfosMovingBoundarySimFilesOutputContextVCDataIdentifier() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetVtuVarInfosComsolSimFilesOutputContextVCDataIdentifier() {
//		fail("Not yet implemented");
//	}
//
//	@Ignore
//	@Test
//	public void testGetNFSimMolecularConfigurations() {
//		fail("Not yet implemented");
//	}
}
