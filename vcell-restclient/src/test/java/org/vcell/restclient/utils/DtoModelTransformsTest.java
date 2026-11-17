package org.vcell.restclient.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.BioModelChildSummary;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A BioModel's child summary, as the server's JSON names its arrays (its getters' names: geometryNames,
 * geometryDimensions, simulationContextAnnotations), keeps each application's geometry: whether it's spatial.
 */
@Tag("Fast")
public class DtoModelTransformsTest {

	@Test
	public void readsTheGeometriesUnderTheNamesTheServerSends() throws Exception {
		// as GET /api/v1/vcInfoContainer sends a summary (geoNames, geoDims and scAnnots absent)
		String json = """
				{"simulationContextNames":["compartmental","spatial"],"appTypes":["Deterministic","Stochastic"],
				 "geometryNames":["","cell2D"],"geometryDimensions":[0,2],"simulationContextAnnotations":["","about it"],
				 "simKeys":[]}""";
		org.vcell.restclient.model.BioModelChildSummary dto = new ObjectMapper()
				.readValue(json, org.vcell.restclient.model.BioModelChildSummary.class);

		BioModelChildSummary summary = DtoModelTransforms.dtoToBioModelChildSummary(dto);

		assertArrayEquals(new String[] { "compartmental", "spatial" }, summary.getSimulationContextNames());
		assertArrayEquals(new int[] { 0, 2 }, summary.getGeometryDimensions());
		assertArrayEquals(new String[] { "", "cell2D" }, summary.getGeometryNames());
		assertEquals("about it", summary.getSimulationContextAnnotations()[1]);
	}

	@Test
	public void stillReadsTheSchemasOtherNames() {
		org.vcell.restclient.model.BioModelChildSummary dto = new org.vcell.restclient.model.BioModelChildSummary()
				.simulationContextNames(List.of("spatial")).geoNames(List.of("cell3D")).geoDims(List.of(3));

		BioModelChildSummary summary = DtoModelTransforms.dtoToBioModelChildSummary(dto);

		assertArrayEquals(new int[] { 3 }, summary.getGeometryDimensions());
		assertArrayEquals(new String[] { "cell3D" }, summary.getGeometryNames());
	}
}
