package cbit.vcell.xml;

import cbit.image.VCImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryTest;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelTest;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A BioModel stores a full copy of its geometry -- image included -- inside EVERY
 * {@code <SimulationContext>} (see {@code Xmlproducer.getXML(SimulationContext)}), so a model with
 * N spatial applications on one geometry decodes the same image N times and retains N copies of the
 * pixels. For the model in #2021 that is 62 MB of pixels eleven times over, against a 1000 MB heap.
 *
 * The fix shares the decoded {@link VCImage} and NOT the {@link Geometry}. That distinction is the
 * point of this class: a VCImage is immutable payload, while a Geometry is mutable, and five
 * applications sharing one Geometry would see each other's subvolume renames and geometry edits.
 * Measurement says sharing images alone captures the entire win, so there is nothing to trade.
 */
@Tag("Fast")
public class XmlReaderGeometrySharingTest {

    @AfterEach
    public void clearOverrides() {
        System.clearProperty(XmlReader.PROPERTY_SHARE_IDENTICAL_IMAGES);
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

        bioModel.setSimulationContexts(new SimulationContext[]{
                newSpatialApplication(model, geometry, "application_one"),
                newSpatialApplication(model, geometry, "application_two")});
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
        return XmlHelper.XMLToBioModel(new XMLSource(XmlHelper.bioModelToXML(bioModel)));
    }

    // ---- the default: share the image, never the geometry ---------------------------------

    @Test
    public void identicalImagesAreDecodedOnce() throws Exception {
        BioModel parsed = roundTrip(twoApplicationsOnOneGeometry());
        assertEquals(2, parsed.getNumSimulationContexts());

        VCImage first = parsed.getSimulationContext(0).getGeometry().getGeometrySpec().getImage();
        VCImage second = parsed.getSimulationContext(1).getGeometry().getGeometrySpec().getImage();

        assertSame(first, second,
                "two applications over one image must share the decoded VCImage, not each retain a copy");
    }

    /**
     * The safety guarantee, and the reason this fix shares images rather than geometries: editing
     * one application's geometry must not change another's. If Geometry objects were shared, a
     * subvolume rename in one application would silently appear in all of them.
     */
    @Test
    public void eachApplicationKeepsItsOwnGeometry() throws Exception {
        BioModel parsed = roundTrip(twoApplicationsOnOneGeometry());
        Geometry first = parsed.getSimulationContext(0).getGeometry();
        Geometry second = parsed.getSimulationContext(1).getGeometry();

        assertNotSame(first, second, "applications must NOT share a mutable Geometry by default");
        assertNotSame(first.getGeometrySpec(), second.getGeometrySpec());
        assertNotSame(first.getGeometrySpec().getSubVolume("cytosol"),
                second.getGeometrySpec().getSubVolume("cytosol"),
                "subvolumes must be private to each application, or renames would leak between them");

        // Demonstrate it rather than assert it structurally: rename in one, check the other.
        first.getGeometrySpec().getSubVolume("cytosol").setName("renamed_in_app_one");
        assertNotNull(second.getGeometrySpec().getSubVolume("cytosol"),
                "renaming a subvolume in one application must not rename it in another");
        assertNull(second.getGeometrySpec().getSubVolume("renamed_in_app_one"));
    }

    @Test
    public void sharingAnImageDoesNotLoseItsContent() throws Exception {
        BioModel parsed = roundTrip(twoApplicationsOnOneGeometry());
        // The SECOND application is the one served from the cache, so check that one: a cache
        // returning something half-built would show up here and nowhere else.
        Geometry geometry = parsed.getSimulationContext(1).getGeometry();

        assertEquals("shared_image_geometry", geometry.getName());
        assertEquals(2, geometry.getDimension());
        VCImage image = geometry.getGeometrySpec().getImage();
        assertNotNull(image);
        assertEquals(100, image.getNumX());
        assertEquals(100, image.getNumY());
        assertEquals(2, image.getNumPixelClasses());
        assertNotNull(geometry.getGeometrySpec().getSubVolume("cytosol"));
        assertNotNull(geometry.getGeometrySpec().getSubVolume("ec"));
    }

    /**
     * The negative control. If this fails while {@link #identicalImagesAreDecodedOnce} passes, the
     * cache key has stopped discriminating and unrelated images are being conflated -- far worse
     * than the memory problem being solved.
     */
    @Test
    public void differentImagesAreNotShared() throws Exception {
        BioModel bioModel = new BioModel(null);
        bioModel.setName("twoAppsTwoGeometries");
        bioModel.setModel(ModelTest.getExample_Wagner_simple(false));
        Model model = bioModel.getModel();

        Geometry geometryOne = GeometryTest.getImageExample2D();
        geometryOne.setName("geometry_one");
        geometryOne.getGeometrySpec().getImage().setName("image_one");
        geometryOne.precomputeAll(new GeometryThumbnailImageFactoryAWT(), true, false);

        Geometry geometryTwo = GeometryTest.getImageExample2D();
        geometryTwo.setName("geometry_two");
        geometryTwo.getGeometrySpec().getImage().setName("image_two");
        geometryTwo.precomputeAll(new GeometryThumbnailImageFactoryAWT(), true, false);

        bioModel.setSimulationContexts(new SimulationContext[]{
                newSpatialApplication(model, geometryOne, "application_one"),
                newSpatialApplication(model, geometryTwo, "application_two")});

        BioModel parsed = roundTrip(bioModel);
        assertNotSame(parsed.getSimulationContext(0).getGeometry().getGeometrySpec().getImage(),
                parsed.getSimulationContext(1).getGeometry().getGeometrySpec().getImage(),
                "images that differ must NOT be conflated");
        assertEquals("geometry_one", parsed.getSimulationContext(0).getGeometry().getName());
        assertEquals("geometry_two", parsed.getSimulationContext(1).getGeometry().getName());
    }

    @Test
    public void imageSharingCanBeSwitchedOff() throws Exception {
        System.setProperty(XmlReader.PROPERTY_SHARE_IDENTICAL_IMAGES, "false");
        BioModel parsed = roundTrip(twoApplicationsOnOneGeometry());

        assertNotSame(parsed.getSimulationContext(0).getGeometry().getGeometrySpec().getImage(),
                parsed.getSimulationContext(1).getGeometry().getGeometrySpec().getImage(),
                "the escape hatch must restore the old one-VCImage-per-application behaviour");
    }

    // ---- geometry sharing: opt-in only ----------------------------------------------------

    /**
     * Geometry sharing exists for read-only consumers -- a server that parses a document to
     * serialise it or to generate math, and never edits it. It must stay OFF unless asked for,
     * because a shared Geometry is a shared mutable object.
     */
    @Test
    public void geometrySharingIsOffUnlessAskedFor() throws Exception {
        assertNotSame(roundTrip(twoApplicationsOnOneGeometry()).getSimulationContext(0).getGeometry(),
                roundTrip(twoApplicationsOnOneGeometry()).getSimulationContext(1).getGeometry());

        System.setProperty(XmlReader.PROPERTY_SHARE_IDENTICAL_GEOMETRIES, "true");
        BioModel parsed = roundTrip(twoApplicationsOnOneGeometry());
        assertSame(parsed.getSimulationContext(0).getGeometry(),
                parsed.getSimulationContext(1).getGeometry(),
                "enabling the property must actually share the Geometry");
    }
}
