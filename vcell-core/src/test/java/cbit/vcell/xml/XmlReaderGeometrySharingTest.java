package cbit.vcell.xml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryTest;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.model.ModelTest;
import cbit.vcell.parser.Expression;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A BioModel stores a full copy of its geometry -- image included -- inside EVERY
 * {@code <SimulationContext>} (see {@code Xmlproducer.getXML(SimulationContext)}), so a model with
 * N spatial applications on one geometry hands {@code XmlReader.getGeometry} N byte-identical
 * elements. Parsing each separately cost a 62 MP model ~1.4 GB of peak heap per copy and killed
 * two prod api pods (#2021).
 *
 * These tests pin the sharing, the escape hatch, and -- the one that matters most -- that
 * geometries which are NOT identical are still parsed separately.
 */
@Tag("Fast")
public class XmlReaderGeometrySharingTest {

    @AfterEach
    public void clearOverride() {
        System.clearProperty(XmlReader.PROPERTY_SHARE_IDENTICAL_GEOMETRIES);
    }

    /**
     * Two applications over one geometry object, which is what makes Xmlproducer write two
     * identical {@code <Geometry>} elements -- the shape of the model in #2021.
     */
    private static BioModel twoApplicationsOnOneGeometry() throws Exception {
        BioModel bioModel = new BioModel(null);
        bioModel.setName("twoAppsOneGeometry");
        bioModel.setModel(ModelTest.getExample_Wagner_simple(false));
        Model model = bioModel.getModel();

        Geometry geometry = GeometryTest.getImageExample2D();
        geometry.setName("shared_image_geometry");
        geometry.precomputeAll(new GeometryThumbnailImageFactoryAWT(), true, false);

        SimulationContext appOne = newSpatialApplication(model, geometry, "application_one");
        SimulationContext appTwo = newSpatialApplication(model, geometry, "application_two");
        bioModel.setSimulationContexts(new SimulationContext[]{appOne, appTwo});
        return bioModel;
    }

    private static SimulationContext newSpatialApplication(Model model, Geometry geometry, String name)
            throws Exception {
        SimulationContext simContext = new SimulationContext(model, geometry, null, null,
                SimulationContext.Application.NETWORK_DETERMINISTIC);
        simContext.setName(name);

        SubVolume cytosol = geometry.getGeometrySpec().getSubVolume("cytosol");
        SubVolume ec = geometry.getGeometrySpec().getSubVolume("ec");
        SurfaceClass pm = geometry.getGeometrySurfaceDescription().getSurfaceClass(cytosol, ec);

        GeometryContext geoContext = simContext.getGeometryContext();
        Structure structure_ec = model.getStructure("extracellular");
        Structure structure_cyt = model.getStructure("cytosol");
        Structure structure_pm = model.getStructure("plasmaMembrane");

        geoContext.assignStructure(structure_ec, ec);
        geoContext.getStructureMapping(structure_ec).getUnitSizeParameter().setExpression(new Expression(1.0));
        geoContext.assignStructure(structure_cyt, cytosol);
        geoContext.getStructureMapping(structure_cyt).getUnitSizeParameter().setExpression(new Expression(0.5));
        geoContext.assignStructure(structure_pm, pm);
        geoContext.getStructureMapping(structure_pm).getUnitSizeParameter().setExpression(new Expression(1.0));
        return simContext;
    }

    private static BioModel roundTrip(BioModel bioModel) throws Exception {
        String vcml = XmlHelper.bioModelToXML(bioModel);
        return XmlHelper.XMLToBioModel(new XMLSource(vcml));
    }

    @Test
    public void identicalGeometryElementsAreParsedOnce() throws Exception {
        BioModel parsed = roundTrip(twoApplicationsOnOneGeometry());

        assertEquals(2, parsed.getNumSimulationContexts());
        Geometry first = parsed.getSimulationContext(0).getGeometry();
        Geometry second = parsed.getSimulationContext(1).getGeometry();

        assertSame(first, second,
                "two applications over one geometry must share the parsed Geometry, not each get a copy");
    }

    @Test
    public void sharingDoesNotLoseGeometryContent() throws Exception {
        BioModel parsed = roundTrip(twoApplicationsOnOneGeometry());
        Geometry geometry = parsed.getSimulationContext(1).getGeometry();

        // The SECOND application is the one served from the cache, so check it rather than the
        // first: a cache that returned something half-built would show up here and nowhere else.
        assertEquals("shared_image_geometry", geometry.getName());
        assertEquals(2, geometry.getDimension());
        assertNotNull(geometry.getGeometrySpec().getImage(), "the image must survive sharing");
        assertNotNull(geometry.getGeometrySpec().getSubVolume("cytosol"));
        assertNotNull(geometry.getGeometrySpec().getSubVolume("ec"));
        assertNotNull(geometry.getGeometrySurfaceDescription().getGeometricRegions(),
                "surfaces must be present on the shared geometry");

        // Each application still maps its own structures through its own GeometryContext.
        assertNotSame(parsed.getSimulationContext(0).getGeometryContext(),
                parsed.getSimulationContext(1).getGeometryContext());
    }

    /**
     * The negative control. If this ever fails while
     * {@link #identicalGeometryElementsAreParsedOnce} passes, the cache key has stopped
     * discriminating and unrelated geometries are being conflated -- far worse than the
     * memory problem being solved.
     */
    @Test
    public void differentGeometriesAreNotShared() throws Exception {
        BioModel bioModel = new BioModel(null);
        bioModel.setName("twoAppsTwoGeometries");
        bioModel.setModel(ModelTest.getExample_Wagner_simple(false));
        Model model = bioModel.getModel();

        Geometry geometryOne = GeometryTest.getImageExample2D();
        geometryOne.setName("geometry_one");
        geometryOne.precomputeAll(new GeometryThumbnailImageFactoryAWT(), true, false);

        Geometry geometryTwo = GeometryTest.getImageExample2D();
        geometryTwo.setName("geometry_two");   // differs -> different element -> different digest
        geometryTwo.precomputeAll(new GeometryThumbnailImageFactoryAWT(), true, false);

        bioModel.setSimulationContexts(new SimulationContext[]{
                newSpatialApplication(model, geometryOne, "application_one"),
                newSpatialApplication(model, geometryTwo, "application_two")});

        BioModel parsed = roundTrip(bioModel);
        Geometry first = parsed.getSimulationContext(0).getGeometry();
        Geometry second = parsed.getSimulationContext(1).getGeometry();

        assertNotSame(first, second, "geometries that differ must NOT be conflated");
        assertEquals("geometry_one", first.getName());
        assertEquals("geometry_two", second.getName());
    }

    @Test
    public void sharingCanBeSwitchedOff() throws Exception {
        System.setProperty(XmlReader.PROPERTY_SHARE_IDENTICAL_GEOMETRIES, "false");
        BioModel parsed = roundTrip(twoApplicationsOnOneGeometry());

        assertNotSame(parsed.getSimulationContext(0).getGeometry(),
                parsed.getSimulationContext(1).getGeometry(),
                "the escape hatch must restore the pre-#2021 one-Geometry-per-SimulationContext behaviour");
    }
}
